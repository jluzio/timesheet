package org.example.timesheet.processors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.timesheet.ProcessingException;
import org.example.timesheet.config.ProcessConfig;
import org.example.timesheet.entries.Movement;
import org.example.timesheet.entries.MovementsReader;
import org.example.timesheet.reports.CsvReportWriter;
import org.example.timesheet.reports.ExcelReportWriter;
import org.example.timesheet.util.DateConverter;

import com.google.common.base.Predicate;

@Named
public class TimesheetProcessor {
	private Logger log = LogManager.getLogger(getClass());
	@Inject
	private MovementsReader movementsReader;
	@Inject
	private MovementProcessor movementProcessor;
	@Inject
	private MonthProcessor monthProcessor;
	@Inject
	private CsvReportWriter csvReportWriter;
	@Inject
	private ExcelReportWriter excelReportWriter;
	@Inject
	private DateConverter dateConverter;

	public void process(ProcessConfig config) throws ProcessingException {
		try {
			List<Movement> movements = readMovements(config);
			log.debug(movements);

			List<DayInfo> dayInfos = movementProcessor.process(movements);
			for (DayInfo dayInfo : dayInfos) {
				log.debug(dayInfo);
			}
			LocalDate month = config.getMonth() != null ? dateConverter.toLocalDateTime(config.getMonth()).toLocalDate() : null;
			dayInfos = monthProcessor.process(dayInfos, month, config.isFillAllMonthDays());

			Charset outputCharset = Charset.forName(config.getOutputEncoding());
			if (config.getCsvOutput() != null) {
				try (Writer writer = new OutputStreamWriter(new FileOutputStream(config.getCsvOutput()),
						outputCharset)) {
					csvReportWriter.write(dayInfos, writer);
				}
			}
			if (config.getExcelOutput() != null) {
				try (OutputStream outputStream = new FileOutputStream(config.getExcelOutput())) {
					excelReportWriter.write(dayInfos, outputStream);
				}
			}
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	private List<Movement> readMovements(ProcessConfig config)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		List<Movement> movements = new ArrayList<>();
		for (File input : config.getInputs()) {
			Reader inputReader = new InputStreamReader(new FileInputStream(input), config.getInputConfig().getEncoding());
			List<Movement> currentMovements = movementsReader.read(inputReader, config.getInputConfig());
			for (Movement currentMovement : currentMovements) {
				Movement existingMovement = movements.stream().filter(equalsPredicate(currentMovement)).findFirst()
						.orElse(null);
				if (existingMovement != null) {
					log.debug("Merging movements {} and {} ", existingMovement, currentMovement);
					existingMovement.setType(currentMovement.getType());
					existingMovement.setRemarks(currentMovement.getRemarks());
				} else {
					movements.add(currentMovement);
				}
			}
			inputReader.close();
		}
		return movements;
	}

	private Predicate<Movement> equalsPredicate(Movement movement) {
		return m -> Objects.equals(movement.getDatetime(), m.getDatetime())
				&& Objects.equals(movement.getTypeCode(), m.getTypeCode());
	}

}
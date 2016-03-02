package org.example.timesheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.timesheet.input.Movement;
import org.example.timesheet.input.MovementsReader;
import org.example.timesheet.output.TimesheetCSVWriter;
import org.example.timesheet.output.TimesheetExcelWriter;
import org.example.timesheet.processing.DayInfo;
import org.example.timesheet.processing.MonthProcessor;
import org.example.timesheet.processing.MovementProcessor;

/**
 * Created by jluzio on 24/02/2015.
 */
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
	private TimesheetCSVWriter timesheetCSVWriter;
	@Inject
	private TimesheetExcelWriter timesheetExcelWriter;

	public void process(ProcessConfig config) throws ProcessingException {
		try {
			List<Movement> movements = new ArrayList<>();
			for (File input : config.getInputs()) {
				Reader inputReader = new InputStreamReader(new FileInputStream(input), config.getInputEncoding());
				List<Movement> currentMovements = movementsReader.read(inputReader);
				
				movements.addAll(currentMovements);
				inputReader.close();
			}
			log.debug(movements);

			List<DayInfo> dayInfos = movementProcessor.process(movements);
			for (DayInfo dayInfo : dayInfos) {
				log.debug(dayInfo);
			}
			dayInfos = monthProcessor.process(dayInfos, config.getMonth(), config.isFillAllMonthDays());
			
			Charset outputCharset = Charset.forName(config.getOutputEncoding());
			if (config.getCsvOutput() != null) {
				try (Writer writer = new OutputStreamWriter(new FileOutputStream(config.getCsvOutput()), outputCharset)) {
					timesheetCSVWriter.write(dayInfos, writer);
				}
			}
			if (config.getExcelOutput() != null) {
				try (OutputStream outputStream = new FileOutputStream(config.getExcelOutput())) {
					timesheetExcelWriter.write(dayInfos, outputStream);
				}
			}
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

}

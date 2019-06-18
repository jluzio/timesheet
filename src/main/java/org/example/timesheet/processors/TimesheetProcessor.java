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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.timesheet.ProcessingException;
import org.example.timesheet.config.ProcessConfig;
import org.example.timesheet.config.RunnerConfig.ReportType;
import org.example.timesheet.entries.Entry;
import org.example.timesheet.entries.EntryReader;
import org.example.timesheet.reports.CsvReportWriter;
import org.example.timesheet.reports.ExcelReportWriter;

import com.google.common.base.Predicate;

@Named
public class TimesheetProcessor {
	private Logger log = LogManager.getLogger(getClass());
	@Inject
	private EntryReader entryReader;
	@Inject
	private MonthDataProcessor monthDataProcessor;
	@Inject
	private CsvReportWriter csvReportWriter;
	@Inject
	private ExcelReportWriter excelReportWriter;

	public void process(ProcessConfig config) throws ProcessingException {
		try {
			List<Entry> entries = readEntries(config);
			log.debug(entries);

			List<DayWorkData> dayWorkDatas = monthDataProcessor.process(entries, config);
			for (DayWorkData dayWorkData : dayWorkDatas) {
				log.debug(dayWorkData);
			}

			Charset outputCharset = Charset.forName(config.getReportEncoding());
			File csvReportFile = config.getReportFiles().get(ReportType.CSV);
			File excelReportFile = config.getReportFiles().get(ReportType.EXCEL);
			if (csvReportFile != null) {
				try (Writer writer = new OutputStreamWriter(new FileOutputStream(csvReportFile),
						outputCharset)) {
					csvReportWriter.write(dayWorkDatas, writer);
				}
			}
			if (excelReportFile != null) {
				try (OutputStream outputStream = new FileOutputStream(excelReportFile)) {
					excelReportWriter.write(dayWorkDatas, outputStream);
				}
			}
		} catch (IOException e) {
			throw new ProcessingException(e);
		}
	}

	private List<Entry> readEntries(ProcessConfig config)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		List<Entry> entries = new ArrayList<>();
		for (File input : config.getEntriesFiles()) {
			Reader inputReader = new InputStreamReader(new FileInputStream(input), config.getEntriesConfig().getEncoding());
			List<Entry> currentEntries = entryReader.read(inputReader, config.getEntriesConfig());
			for (Entry currentEntry : currentEntries) {
				Entry existingEntry = entries.stream().filter(equalsPredicate(currentEntry)).findFirst()
						.orElse(null);
				if (existingEntry != null) {
					log.debug("Merging entries {} and {} ", existingEntry, currentEntry);
					existingEntry.setType(currentEntry.getType());
					existingEntry.setRemarks(currentEntry.getRemarks());
				} else {
					entries.add(currentEntry);
				}
			}
			inputReader.close();
		}
		return entries;
	}

	private Predicate<Entry> equalsPredicate(Entry entry) {
		return m -> Objects.equals(entry.getDatetime(), m.getDatetime())
				&& Objects.equals(entry.getTypeCode(), m.getTypeCode());
	}

}

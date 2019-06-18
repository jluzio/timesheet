package org.example.timesheet.processors;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;

import org.example.timesheet.AbstractTest;
import org.example.timesheet.config.EntriesConfig;
import org.example.timesheet.config.Holidays;
import org.example.timesheet.config.ProcessConfig;
import org.example.timesheet.config.RunnerConfig.ReportType;
import org.example.timesheet.config.Vacations;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TimesheetProcessorTest extends AbstractTest {
	@Inject
	private TimesheetProcessor processor;
	@Inject
	private ObjectMapper objectMapper;
	
	@SuppressWarnings("unused")
	@Test
	public void test() throws Exception {
		LocalDate monthDate = LocalDate.of(2017, 6, 1);
		String filename = monthDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File inputFile = new File(classLoader.getResource("201706.xls").getPath());
		File inputFile_custom = new File(classLoader.getResource("201706_custom.xls").getPath());
		List<File> entriesFiles = Arrays.asList(inputFile, inputFile_custom);
		File file = new File(classLoader.getResource("configData-test").getPath());
		String configDataPath = file.getAbsolutePath();
		
		File processDirectory = new File("/home/timesheet");
		File outputDirectory = new File("target/output");
		outputDirectory.mkdir();
		
		File entriesConfigFile = new File(classLoader.getResource("entriesConfig-default.xml").getPath());
		EntriesConfig entriesConfig = (EntriesConfig) JAXBContext.newInstance(EntriesConfig.class)
				.createUnmarshaller()
				.unmarshal(entriesConfigFile);
		
		File configDataDir = new File(classLoader.getResource("configData-test").getPath());
		Vacations vacations = objectMapper.readValue(new File(configDataDir, "vacations.json"), Vacations.class);
		Holidays holidays = objectMapper.readValue(new File(configDataDir, "holidays.json"), Holidays.class);
		
		String outputFormat = "timesheet_%s.%s";
		String inputFormat = "%s.%s";

		ProcessConfig processConfig = new ProcessConfig();
		processConfig.setEntriesFiles(entriesFiles);
		processConfig.setEntriesConfig(entriesConfig);
		processConfig.setVacations(vacations);
		processConfig.setHolidays(holidays);
		processConfig.getReportFiles().put(ReportType.CSV, new File(outputDirectory, String.format(outputFormat, filename, "csv")));
		processConfig.getReportFiles().put(ReportType.EXCEL, new File(outputDirectory, String.format(outputFormat, filename, "xls")));
		processConfig.setMonth(monthDate);
		
		processor.process(processConfig);
	}
	
}

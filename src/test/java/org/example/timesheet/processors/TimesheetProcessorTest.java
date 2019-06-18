package org.example.timesheet.processors;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;

import org.example.timesheet.AbstractTest;
import org.example.timesheet.config.InputConfig;
import org.example.timesheet.config.ProcessConfig;
import org.example.timesheet.processors.TimesheetProcessor;
import org.junit.Test;

public class TimesheetProcessorTest extends AbstractTest {
	@Inject
	private TimesheetProcessor processor;
	
	@SuppressWarnings("unused")
	@Test
	public void test() throws Exception {
		LocalDate monthDate = LocalDate.of(2017, 6, 1);
		String filename = monthDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File inputFile = new File(classLoader.getResource("201706.xls").getPath());
		File inputFile_custom = new File(classLoader.getResource("201706_custom.xls").getPath());
		List<File> inputFiles = Arrays.asList(inputFile, inputFile_custom);
		
		File processDirectory = new File("/home/timesheet");
		File outputDirectory = new File("target/output");
		outputDirectory.mkdir();
		
		File inputConfigFile = new File(classLoader.getResource("inputConfig-default.xml").getPath());
		InputConfig inputConfig = (InputConfig) JAXBContext.newInstance(InputConfig.class)
				.createUnmarshaller()
				.unmarshal(inputConfigFile);
		
		String outputFormat = "timesheet_%s.%s";
		String inputFormat = "%s.%s";

		ProcessConfig processConfig = new ProcessConfig();
		processConfig.setInputs(inputFiles);
		processConfig.setInputConfig(inputConfig);
		processConfig.setCsvOutput(new File(outputDirectory, String.format(outputFormat, filename, "csv")));
		processConfig.setExcelOutput(new File(outputDirectory, String.format(outputFormat, filename, "xls")));
		processConfig.setMonth(Date.from(monthDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		
		processor.process(processConfig);
	}
	
}

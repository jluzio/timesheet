package org.example.timesheet;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;

public class TimesheetRunnerTest extends AbstractTest {
	@Inject
	private TimesheetProcessor processor;
	
	@SuppressWarnings("unused")
	@Test
	public void test() {
		LocalDate monthDate = LocalDate.of(2017, 6, 1);
		String filename = monthDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File inputFile = new File(classLoader.getResource("201706.xls").getPath());
		File inputFile_custom = new File(classLoader.getResource("201706_custom.xls").getPath());
		List<File> inputFiles = Arrays.asList(inputFile, inputFile_custom);
		
		File processDirectory = new File("/home/timesheet");
		File outputDirectory = new File("target/output");
		outputDirectory.mkdir();
		
		String outputFormat = "timesheet_%s.%s";
		String inputFormat = "%s.%s";

		ProcessConfig config = new ProcessConfig();
		config.setInputs(inputFiles);
		config.setInputEncoding("UTF-16LE");
		config.setCsvOutput(new File(outputDirectory, String.format(outputFormat, filename, "csv")));
		config.setExcelOutput(new File(outputDirectory, String.format(outputFormat, filename, "xls")));
		config.setMonth(Date.from(monthDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		
		processor.process(config);
	}
	
}

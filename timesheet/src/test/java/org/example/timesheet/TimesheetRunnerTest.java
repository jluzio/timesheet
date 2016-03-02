package org.example.timesheet;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import org.junit.Test;

public class TimesheetRunnerTest extends AbstractTest {
	@Inject
	private TimesheetProcessor processor;
	
	@SuppressWarnings("unused")
	@Test
	public void test() {
		LocalDate monthDate = LocalDate.of(2015, 2, 1);
		String filename = monthDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File inputFile = new File(classLoader.getResource("201502.xls").getPath());
		
		File processDirectory = new File("/home/timesheet");
		File outputDirectory = new File("target/output");
		outputDirectory.mkdir();
		
		String outputFormat = "timesheet_%s.%s";
		String inputFormat = "%s.%s";

		ProcessConfig config = new ProcessConfig();
		config.setInputs(Arrays.asList(inputFile));
		config.setInputEncoding("UTF-16LE");
		config.setCsvOutput(new File(outputDirectory, String.format(outputFormat, filename, "csv")));
		config.setExcelOutput(new File(outputDirectory, String.format(outputFormat, filename, "xls")));
		config.setMonth(Date.from(monthDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		
		processor.process(config);
	}
	
}

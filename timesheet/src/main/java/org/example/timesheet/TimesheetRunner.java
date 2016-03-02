package org.example.timesheet;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

@Named
public class TimesheetRunner {
	@Inject 
	private TimesheetProcessor processor;
	
	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);
		ctx.getBean(TimesheetRunner.class).run();
	}
	
	public void run() {
		LocalDate monthDate = LocalDate.of(2016, 2, 1);
		String filename = monthDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
		
		File processDirectory = new File("/home/timesheet");
		String outputFormat = "timesheet_%s.%s";
		String inputFormat = "%s.%s";

		File inputFile = new File(processDirectory, String.format(inputFormat, filename, "xls"));
		File customInputFile = new File(processDirectory, String.format(inputFormat, filename + "_custom", "xls"));
		List<File> inputFiles = customInputFile.exists() 
				? Arrays.asList(inputFile, customInputFile)
				: Arrays.asList(inputFile);

		ProcessConfig config = new ProcessConfig();
		config.setInputs(inputFiles);
		config.setInputEncoding("UTF-16LE");
		config.setCsvOutput(new File(processDirectory, String.format(outputFormat, filename, "csv")));
		config.setExcelOutput(new File(processDirectory, String.format(outputFormat, filename, "xls")));
		config.setMonth(Date.from(monthDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
		
		processor.process(config);
	}

}

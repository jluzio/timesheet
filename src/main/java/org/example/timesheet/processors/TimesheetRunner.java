package org.example.timesheet.processors;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.example.timesheet.Application;
import org.example.timesheet.ProcessingException;
import org.example.timesheet.config.InputConfig;
import org.example.timesheet.config.ProcessConfig;
import org.example.timesheet.config.RunnerConfig;
import org.example.timesheet.util.DateConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

@Named
public class TimesheetRunner {
	@Inject 
	private TimesheetProcessor processor;
	@Inject 
	private DateConverter dateConverter;
	
	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(Application.class, args);

		TimesheetRunner runner = ctx.getBean(TimesheetRunner.class);
		RunnerConfig runnerConfig = runner.getRunnerConfig("/home/timesheet");
		runner.run(runnerConfig);
	}
	
	public void run(RunnerConfig runnerConfig) {
		try {
			LocalDate monthDate = null;
			if (runnerConfig.getTargetDate() != null) {
				monthDate = dateConverter.toLocalDateTime(runnerConfig.getTargetDate())
						.toLocalDate()
						.withDayOfMonth(1);
			} else {
				monthDate = LocalDate.now().withDayOfMonth(1);
			}
			String filename = monthDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
			
			String outputFormat = "timesheet_%s.%s";
			String inputFormat = "%s.%s";

			File inputFile = new File(runnerConfig.getInputPath(), String.format(inputFormat, filename, "xls"));
			File customInputFile = new File(runnerConfig.getInputPath(), String.format(inputFormat, filename + "_custom", "xls"));
			List<File> inputFiles = customInputFile.exists() 
					? Arrays.asList(inputFile, customInputFile)
					: Arrays.asList(inputFile);

			ProcessConfig processConfig = new ProcessConfig();
			processConfig.setInputs(inputFiles);
			processConfig.setInputConfig(runnerConfig.getInputConfig());
//			processConfig.setCsvOutput(new File(appConfig.getOutputPath(), String.format(outputFormat, filename, "csv")));
			processConfig.setExcelOutput(new File(runnerConfig.getOutputPath(), String.format(outputFormat, filename, "xls")));
			processConfig.setOutputEncoding(runnerConfig.getOutputEncoding());
			processConfig.setFillAllMonthDays(runnerConfig.isFillAllMonthDays());
			processConfig.setMonth(Date.from(monthDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
			
			processor.process(processConfig);
		} catch (ProcessingException e) {
			throw e;
		} catch (Exception e) {
			throw new ProcessingException(e);
		}
	}

	private RunnerConfig getRunnerConfig(String appHomePath) throws JAXBException {
		Unmarshaller cfgUnmarshaller = JAXBContext.newInstance(RunnerConfig.class).createUnmarshaller();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File defaultRunnerConfigFile = new File(classLoader.getResource("runnerConfig-default.xml").getPath());
		File defauçtInputConfigFile = new File(classLoader.getResource("inputConfig-default.xml").getPath());

		File configFile = new File(appHomePath, "timesheet.xml");
		if (!configFile.exists()) {
			configFile = defaultRunnerConfigFile;
		}
		RunnerConfig runnerConfig = (RunnerConfig) cfgUnmarshaller.unmarshal(configFile);
		if (runnerConfig.getInputConfig() == null) {
			InputConfig inputConfig = (InputConfig) cfgUnmarshaller.unmarshal(defauçtInputConfigFile);
			runnerConfig.setInputConfig(inputConfig);
		}
		return runnerConfig;
	}

}

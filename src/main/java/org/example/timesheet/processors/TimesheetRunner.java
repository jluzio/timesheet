package org.example.timesheet.processors;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.example.timesheet.Application;
import org.example.timesheet.ProcessingException;
import org.example.timesheet.config.EntriesConfig;
import org.example.timesheet.config.Holidays;
import org.example.timesheet.config.ProcessConfig;
import org.example.timesheet.config.RunnerConfig;
import org.example.timesheet.config.RunnerConfig.ReportType;
import org.example.timesheet.config.Vacations;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@Named
public class TimesheetRunner {
	@Inject 
	private TimesheetProcessor processor;
	@Inject 
	private ObjectMapper objectMapper;
	
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
				monthDate = runnerConfig.getTargetDate().withDayOfMonth(1);
			} else {
				monthDate = LocalDate.now().withDayOfMonth(1);
			}
			String filename = monthDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
			
			String outputFormat = "timesheet_%s.%s";
			String inputFormat = "%s.%s";

			File inputFile = new File(runnerConfig.getEntriesPath(), String.format(inputFormat, filename, "xls"));
			File customInputFile = new File(runnerConfig.getEntriesPath(), String.format(inputFormat, filename + "_custom", "xls"));
			List<File> entriesFiles = customInputFile.exists() 
					? Arrays.asList(inputFile, customInputFile)
					: Arrays.asList(inputFile);

			File configDataDir = new File(runnerConfig.getConfigDataPath());
			File vacationsFile = new File(configDataDir, "vacations.json");
			File holidaysFile = new File(configDataDir, "holidays.json");
			Vacations vacations = vacationsFile.exists() ? objectMapper.readValue(vacationsFile, Vacations.class) : new Vacations();
			Holidays holidays = holidaysFile.exists() ? objectMapper.readValue(holidaysFile, Holidays.class) : new Holidays();
			
			ProcessConfig processConfig = new ProcessConfig();
			processConfig.setEntriesFiles(entriesFiles);
			processConfig.setEntriesConfig(runnerConfig.getEntriesConfig());
			processConfig.setVacations(vacations);
			processConfig.setHolidays(holidays);
			processConfig.setReportEncoding(runnerConfig.getReportEncoding());
			processConfig.setFillAllMonthDays(runnerConfig.isFillAllMonthDays());
			processConfig.setMonth(monthDate);
			for (ReportType reportType : runnerConfig.getReportTypes()) {
				String reportExt = getReportExt(reportType);
				File reportFile = new File(runnerConfig.getReportsPath(), String.format(outputFormat, filename, reportExt));
				processConfig.getReportFiles().put(reportType, reportFile);
			}
			
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
		File defauçtInputConfigFile = new File(classLoader.getResource("entriesConfig-default.xml").getPath());

		File configFile = new File(appHomePath, "timesheet.xml");
		if (!configFile.exists()) {
			configFile = defaultRunnerConfigFile;
		}
		RunnerConfig runnerConfig = (RunnerConfig) cfgUnmarshaller.unmarshal(configFile);
		if (runnerConfig.getEntriesConfig() == null) {
			EntriesConfig entriesConfig = (EntriesConfig) cfgUnmarshaller.unmarshal(defauçtInputConfigFile);
			runnerConfig.setEntriesConfig(entriesConfig);
		}
		return runnerConfig;
	}
	
	private String getReportExt(ReportType reportType) {
		switch (reportType) {
		case CSV:
			return "csv";
		case EXCEL:
			return "xls";
		default:
			throw new IllegalArgumentException("Unrecognized reportType: " + reportType);
		}
	}

}

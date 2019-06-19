package org.example.timesheet.processors;

import java.io.File;
import java.time.LocalDate;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;

import org.example.timesheet.AbstractTest;
import org.example.timesheet.config.EntriesConfig;
import org.example.timesheet.config.EntriesConfigReader;
import org.example.timesheet.config.RunnerConfig;
import org.junit.Test;

import com.google.common.collect.Lists;

public class TimesheetRunnerTest extends AbstractTest {
	@Inject
	private TimesheetRunner runner;
	@Inject
	private EntriesConfigReader entriesConfigReader;
	
	@Test
	public void testCurrentFormat() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File inputFile = new File(classLoader.getResource("201706.xls").getPath());
		String entriesPath = inputFile.getParentFile().getAbsolutePath();
		String outputPath = entriesPath;
		LocalDate monthDate = LocalDate.of(2017, 06, 1);
		String configDataPath = new File(classLoader.getResource("configData-test").getPath()).getAbsolutePath();
		
		File entriesConfigFile = new File(classLoader.getResource("entriesConfig-default.xml").getPath());
		EntriesConfig entriesConfig = entriesConfigReader.read(entriesConfigFile);
		
		RunnerConfig appConfig = new RunnerConfig();
		appConfig.setTargetDate(monthDate);
		appConfig.setEntriesPath(entriesPath);
		appConfig.setEntriesConfig(entriesConfig);
		appConfig.setConfigDataPath(configDataPath);
		appConfig.setReportsPath(outputPath);
		appConfig.setReportEncoding("UTF-8");
		appConfig.setFillAllMonthDays(true);
		appConfig.setReportTypes(Lists.newArrayList(RunnerConfig.ReportType.values()));
		
		runner.run(appConfig);
	}
	
	@Test
	public void testFormat201706() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File inputFile = new File(classLoader.getResource("format_201706/201706.xls").getPath());
		String entriesPath = inputFile.getParentFile().getAbsolutePath();
		String outputPath = entriesPath;
		LocalDate monthDate = LocalDate.of(2017, 6, 1);
		String configDataPath = new File(classLoader.getResource("configData-test").getPath()).getAbsolutePath();

		File inputConfigFile = new File(classLoader.getResource("entriesConfig-oldFormat.xml").getPath());
		EntriesConfig entriesConfig = (EntriesConfig) JAXBContext.newInstance(EntriesConfig.class)
				.createUnmarshaller()
				.unmarshal(inputConfigFile);
		
		RunnerConfig appConfig = new RunnerConfig();
		appConfig.setTargetDate(monthDate);
		appConfig.setEntriesPath(entriesPath);
		appConfig.setEntriesConfig(entriesConfig);
		appConfig.setConfigDataPath(configDataPath);
		appConfig.setReportsPath(outputPath);
		appConfig.setReportEncoding("UTF-8");
		appConfig.setFillAllMonthDays(true);
		appConfig.setReportTypes(Lists.newArrayList(RunnerConfig.ReportType.values()));
		
		runner.run(appConfig);
	}

}

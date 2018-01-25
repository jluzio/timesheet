package org.example.timesheet;

import java.io.File;
import java.time.LocalDate;
import java.util.Date;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;

import org.example.timesheet.util.DateConverter;
import org.junit.Test;

import com.google.common.collect.Lists;

public class TimesheetRunnerTest extends AbstractTest {
	@Inject
	private TimesheetRunner runner;
	@Inject
	private DateConverter dateConverter;
	
	@Test
	public void testCurrentFormat() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File inputFile = new File(classLoader.getResource("201706.xls").getPath());
		String inputPath = inputFile.getParentFile().getAbsolutePath();
		String outputPath = inputPath;
		LocalDate monthLocalDate = LocalDate.of(2017, 06, 1);
		Date monthDate = dateConverter.fromLocalDateTime(monthLocalDate.atStartOfDay());
		
		File inputConfigFile = new File(classLoader.getResource("inputConfig-default.xml").getPath());
		InputConfig inputConfig = (InputConfig) JAXBContext.newInstance(InputConfig.class)
				.createUnmarshaller()
				.unmarshal(inputConfigFile);
		
		RunnerConfig appConfig = new RunnerConfig();
		appConfig.setTargetDate(monthDate);
		appConfig.setInputPath(inputPath);
		appConfig.setInputConfig(inputConfig);
		appConfig.setOutputPath(outputPath);
		appConfig.setOutputEncoding("UTF-8");
		appConfig.setFillAllMonthDays(true);
		appConfig.setOutputTargets(Lists.newArrayList(RunnerConfig.OutputTarget.values()));
		
		runner.run(appConfig);
	}
	
	@Test
	public void testFormat201706() throws Exception {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File inputFile = new File(classLoader.getResource("format_201706/201706.xls").getPath());
		String inputPath = inputFile.getParentFile().getAbsolutePath();
		String outputPath = inputPath;
		LocalDate monthLocalDate = LocalDate.of(2017, 6, 1);
		Date monthDate = dateConverter.fromLocalDateTime(monthLocalDate.atStartOfDay());
		
		File inputConfigFile = new File(classLoader.getResource("inputConfig-oldFormat.xml").getPath());
		InputConfig inputConfig = (InputConfig) JAXBContext.newInstance(InputConfig.class)
				.createUnmarshaller()
				.unmarshal(inputConfigFile);
		
		RunnerConfig appConfig = new RunnerConfig();
		appConfig.setTargetDate(monthDate);
		appConfig.setInputPath(inputPath);
		appConfig.setInputConfig(inputConfig);
		appConfig.setOutputPath(outputPath);
		appConfig.setOutputEncoding("UTF-8");
		appConfig.setFillAllMonthDays(true);
		appConfig.setOutputTargets(Lists.newArrayList(RunnerConfig.OutputTarget.values()));
		
		runner.run(appConfig);
	}

}

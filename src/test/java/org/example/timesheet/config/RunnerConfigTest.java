package org.example.timesheet.config;

import java.io.File;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.example.timesheet.AbstractTest;
import org.example.timesheet.config.InputConfig;
import org.example.timesheet.config.RunnerConfig;
import org.junit.Test;

import com.google.common.collect.Lists;

public class RunnerConfigTest extends AbstractTest {
	@Test
	public void testMarshal() throws Exception {
		JAXBContext jaxbCtx = JAXBContext.newInstance(RunnerConfig.class);
		Marshaller marshaller = jaxbCtx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		InputConfig inputConfig = new InputConfig();
		inputConfig.setDateFormat("dd/MM/yyyy");
		inputConfig.setDateTimeFormat("dd/MM/yyyy HH:mm:ss");
		inputConfig.setEncoding("UTF-8");
		inputConfig.setServiceExitText("Service Exit");
		
		RunnerConfig runnerConfig = new RunnerConfig();
		runnerConfig.setFillAllMonthDays(true);
		runnerConfig.setInputPath("/home/timesheet");
		runnerConfig.setInputConfig(inputConfig);
		runnerConfig.setOutputEncoding("UTF-8");
		runnerConfig.setOutputPath("/home/timesheet");
		runnerConfig.setOutputTargets(Lists.newArrayList(RunnerConfig.OutputTarget.values()));
		runnerConfig.setTargetDate(new Date());
		
		marshaller.marshal(inputConfig, System.out);
		marshaller.marshal(runnerConfig, System.out);
	}

	@Test
	public void testUnmarshal() throws Exception {
		JAXBContext jaxbCtx = JAXBContext.newInstance(RunnerConfig.class);
		Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File inputFile = new File(classLoader.getResource("AppConfigTest.xml").getPath());
		RunnerConfig appConfig = (RunnerConfig) unmarshaller.unmarshal(inputFile);
		System.out.println(appConfig);
	}

}

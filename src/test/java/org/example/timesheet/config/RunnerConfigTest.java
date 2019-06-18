package org.example.timesheet.config;

import java.io.File;
import java.time.LocalDate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.example.timesheet.AbstractTest;
import org.junit.Test;

import com.google.common.collect.Lists;

public class RunnerConfigTest extends AbstractTest {
	
	@Test
	public void testMarshal() throws Exception {
		JAXBContext jaxbCtx = JAXBContext.newInstance(RunnerConfig.class);
		Marshaller marshaller = jaxbCtx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
		EntriesConfig entriesConfig = new EntriesConfig();
		entriesConfig.setDateFormat("dd/MM/yyyy");
		entriesConfig.setDateTimeFormat("dd/MM/yyyy HH:mm:ss");
		entriesConfig.setEncoding("UTF-8");
		entriesConfig.setServiceExitText("Service Exit");
		
		RunnerConfig runnerConfig = new RunnerConfig();
		runnerConfig.setFillAllMonthDays(true);
		runnerConfig.setEntriesPath("/home/timesheet");
		runnerConfig.setEntriesConfig(entriesConfig);
		runnerConfig.setReportEncoding("UTF-8");
		runnerConfig.setReportsPath("/home/timesheet");
		runnerConfig.setReportTypes(Lists.newArrayList(RunnerConfig.ReportType.values()));
		runnerConfig.setTargetDate(LocalDate.now());
		
		marshaller.marshal(entriesConfig, System.out);
		marshaller.marshal(runnerConfig, System.out);
	}

	@Test
	public void testUnmarshal() throws Exception {
		JAXBContext jaxbCtx = JAXBContext.newInstance(RunnerConfig.class);
		Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		File inputFile = new File(classLoader.getResource("RunnerConfigTest.xml").getPath());
		RunnerConfig appConfig = (RunnerConfig) unmarshaller.unmarshal(inputFile);
		System.out.println(appConfig);
	}

}

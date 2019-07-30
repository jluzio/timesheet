package org.example.timesheet.config;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.assertj.core.util.Lists;
import org.example.timesheet.AbstractTest;
import org.example.timesheet.config.RunnerConfig.ReportType;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class GenerateSampleConfigTest extends AbstractTest {
	@Inject
	private ObjectMapper objectMapper;
	
	@Test
	public void generateRunnerConfig() throws JAXBException {
		RunnerConfig runnerConfig = new RunnerConfig();
		runnerConfig.setConfigDataPath("/home/timesheet/configData");
		runnerConfig.setEntriesPath("/home/timesheet/entries");
		runnerConfig.setFillAllMonthDays(true);
		runnerConfig.setReportEncoding("UTF-8");
		runnerConfig.setReportsPath("/home/timesheet/reports");
		runnerConfig.setReportTypes(Lists.newArrayList(ReportType.EXCEL, ReportType.CSV));
		runnerConfig.setTargetDate(LocalDate.now());
		
		File output = new File("target/runnerConfig-test.xml");
		Marshaller marshaller = JAXBContext.newInstance(RunnerConfig.class).createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(runnerConfig, output);
	}
	
	@Test
	public void generateDaysOff() throws JsonGenerationException, JsonMappingException, IOException {
		ListMultimap<DayOffType, DayOff> daysOff = ArrayListMultimap.create();
		daysOff.put(DayOffType.HOLIDAY, new DayOff(LocalDate.of(2017, 6, 13)));
		daysOff.put(DayOffType.HOLIDAY, new DayOff(LocalDate.of(2017, 6, 15)));
		
		File output = new File("target/daysOff-test.json");
		objectMapper.writeValue(output, daysOff.asMap());
	}
	
	@Test
	public void generateAbsences() throws JsonGenerationException, JsonMappingException, IOException {
		ListMultimap<AbsenceType, Absence> absences = ArrayListMultimap.create();
		absences.put(AbsenceType.VACATION, new Absence(LocalDate.of(2017, 6, 14)));
		absences.put(AbsenceType.VACATION, new Absence(LocalDate.of(2017, 6, 19), LocalDate.of(2017, 6, 20)));
		absences.put(AbsenceType.VACATION, new Absence(LocalDate.of(2017, 6, 22), LocalDate.of(2017, 6, 26)));
		absences.put(AbsenceType.OTHER, new Absence(LocalDate.of(2017, 6, 16)));
		
		File output = new File("target/absences-test.json");
		objectMapper.writeValue(output, absences.asMap());
	}

}

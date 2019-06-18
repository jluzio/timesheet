package org.example.timesheet.config;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import javax.inject.Inject;

import org.example.timesheet.AbstractTest;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenerateSampleConfigTest extends AbstractTest {
	@Inject
	private ObjectMapper objectMapper;
	
	@Test
	public void generateHolidays() throws JsonGenerationException, JsonMappingException, IOException {
		Holidays holidays = new Holidays();
		holidays.getDates().add(LocalDate.now().plusDays(-1));
		holidays.getDates().add(LocalDate.now().plusDays(+1));
		
		File output = new File("target/holidays-test.json");
		objectMapper.writeValue(output, holidays);
	}

	
	@Test
	public void generateVacations() throws JsonGenerationException, JsonMappingException, IOException {
		Vacations vacations = new Vacations();
		vacations.getVacations().add(new Vacation(LocalDate.now()));
		vacations.getVacations().add(new Vacation(LocalDate.now().plusDays(7), LocalDate.now().plusDays(14)));
		
		File output = new File("target/vacations-test.json");
		objectMapper.writeValue(output, vacations);
	}

}

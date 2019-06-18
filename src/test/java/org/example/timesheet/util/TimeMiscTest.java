package org.example.timesheet.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

public class TimeMiscTest {
	
	@Test
	@org.junit.Ignore
	public void test() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime temporal1 = now.minusDays(1);
		LocalDateTime temporal2 = now.plusDays(1);
		
		System.out.println(ChronoUnit.MILLIS.between(temporal1, temporal2));
	}

}

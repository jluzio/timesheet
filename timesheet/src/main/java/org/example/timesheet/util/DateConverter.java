package org.example.timesheet.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.inject.Named;

@Named
public class DateConverter {
	
	public LocalDateTime toLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(toInstant(date), ZoneId.systemDefault());
	}

	public Instant toInstant(Date date) {
		return Instant.ofEpochMilli(date.getTime());
	}

	public Date fromLocalDateTime(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public Date fromInstant(Instant instant) {
		return Date.from(instant);
	}
	
}

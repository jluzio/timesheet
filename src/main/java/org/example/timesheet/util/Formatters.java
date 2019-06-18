package org.example.timesheet.util;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public class Formatters {
	public static DateTimeFormatter TIME_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
	public static DateTimeFormatter DATE_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static DateTimeFormatter DATETIME_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	public static String format(Temporal temporal, DateTimeFormatter formatter) {
		return temporal==null ? null : formatter.format(temporal);
	}

}

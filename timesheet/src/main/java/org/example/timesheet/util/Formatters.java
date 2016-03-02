package org.example.timesheet.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Formatters {
	public static DateFormat TIME_OUTPUT_FORMAT = new SimpleDateFormat("HH:mm");
	public static DateFormat DATE_OUTPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static DateFormat DATETIME_OUTPUT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public static String format(Date date, DateFormat format) {
		return date==null ? null : format.format(date);
	}

}

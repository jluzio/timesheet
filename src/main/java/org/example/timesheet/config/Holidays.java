package org.example.timesheet.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Holidays {
	private List<LocalDate> dates = new ArrayList<>();

	public List<LocalDate> getDates() {
		return dates;
	}

	public void setDates(List<LocalDate> dates) {
		this.dates = dates;
	}

	@Override
	public String toString() {
		return "Holidays [dates=" + dates + "]";
	}
	
}

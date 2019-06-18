package org.example.timesheet.util;

import java.time.LocalDate;

import javax.inject.Named;

import org.example.timesheet.config.Holidays;
import org.example.timesheet.config.Vacations;

@Named
public class ConfigDataUtil {
	
	public boolean isHoliday(LocalDate date, Holidays holidays) {
		return holidays.getDates().contains(date);
	}

	public boolean isVacation(LocalDate date, Vacations vacations) {
		return vacations.getVacations().stream().anyMatch(v -> {
			if (v.getDate().equals(date)) {
				return true;
			}
			if (v.getEndDate() != null) {
				return date.isAfter(v.getDate()) && (date.isEqual(v.getEndDate()) || date.isBefore(v.getEndDate()));
			}
			return false;
		});
	}

}

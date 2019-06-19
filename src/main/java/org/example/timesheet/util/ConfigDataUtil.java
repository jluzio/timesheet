package org.example.timesheet.util;

import java.time.LocalDate;
import java.util.Optional;

import javax.inject.Named;

import org.example.timesheet.config.Absense;
import org.example.timesheet.config.AbsenseType;
import org.example.timesheet.config.ConfigData;
import org.example.timesheet.config.DayOff;
import org.example.timesheet.config.DayOffType;

@Named
public class ConfigDataUtil {
	
	public Optional<DayOff> getDayOff(LocalDate date, ConfigData configData, DayOffType type) {
		return configData.getDaysOff().stream()
			.filter(v -> type == null || type.equals(v.getType()))
			.filter(v -> {
				if (v.getDate().equals(date)) {
					return true;
				}
				return false;
			})
			.findFirst();
	}

	public Optional<Absense> getAbsense(LocalDate date, ConfigData configData, AbsenseType type) {
		return configData.getAbsenses().stream()
			.filter(v -> type == null || type.equals(v.getType()))
			.filter(v -> {
				if (v.getDate().equals(date)) {
					return true;
				}
				if (v.getEndDate() != null) {
					return date.isAfter(v.getDate()) && (date.isEqual(v.getEndDate()) || date.isBefore(v.getEndDate()));
				}
				return false;
			})
			.findFirst();
	}
	
}

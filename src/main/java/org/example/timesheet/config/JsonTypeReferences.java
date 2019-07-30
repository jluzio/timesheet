package org.example.timesheet.config;

import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

public interface JsonTypeReferences {

	TypeReference<LinkedHashMap<AbsenceType, List<Absence>>> ABSENCES = new TypeReference<LinkedHashMap<AbsenceType, List<Absence>>>() {};
	TypeReference<LinkedHashMap<DayOffType, List<DayOff>>> DAYS_OFF = new TypeReference<LinkedHashMap<DayOffType, List<DayOff>>>() {};

}

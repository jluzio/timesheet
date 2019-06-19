package org.example.timesheet.config;

import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

public interface JsonTypeReferences {

	TypeReference<LinkedHashMap<AbsenseType, List<Absense>>> ABSENSES = new TypeReference<LinkedHashMap<AbsenseType, List<Absense>>>() {};
	TypeReference<LinkedHashMap<DayOffType, List<DayOff>>> DAYS_OFF = new TypeReference<LinkedHashMap<DayOffType, List<DayOff>>>() {};

}

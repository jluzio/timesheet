package org.example.timesheet.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Named
public class ConfigDataReader {
	@Inject
	private ObjectMapper objectMapper;
	
	public ConfigData read(File configDataDir) throws JsonParseException, JsonMappingException, IOException {
		Map<AbsenceType, List<Absence>> absencesMap = null;
		File absencesFile = new File(configDataDir, "absences.json");
		if (absencesFile.exists()) {
			absencesMap = objectMapper.readValue(absencesFile, JsonTypeReferences.ABSENCES);
		}

		File daysOffFile = new File(configDataDir, "daysOff.json");
		Map<DayOffType, List<DayOff>> daysOffMap = null;
		if (daysOffFile.exists()) {
			daysOffMap = objectMapper.readValue(daysOffFile, JsonTypeReferences.DAYS_OFF);
		}
		
		ConfigData configData = new ConfigData();
		if (absencesMap != null) {
			absencesMap.forEach((type, list) -> {
				list.forEach(v -> {
					v.setType(type);
					configData.getAbsences().add(v);
				});
			});
		}
		if (daysOffMap != null) {
			daysOffMap.forEach((type, list) -> {
				list.forEach(v -> {
					v.setType(type);
					configData.getDaysOff().add(v);
				});
			});
		}
		return configData;
	}

}

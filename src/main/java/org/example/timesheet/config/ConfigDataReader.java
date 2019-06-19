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
		Map<AbsenseType, List<Absense>> absensesMap = null;
		File absensesFile = new File(configDataDir, "absenses.json");
		if (absensesFile.exists()) {
			absensesMap = objectMapper.readValue(absensesFile, JsonTypeReferences.ABSENSES);
		}

		File daysOffFile = new File(configDataDir, "daysOff.json");
		Map<DayOffType, List<DayOff>> daysOffMap = null;
		if (daysOffFile.exists()) {
			daysOffMap = objectMapper.readValue(daysOffFile, JsonTypeReferences.DAYS_OFF);
		}
		
		ConfigData configData = new ConfigData();
		if (absensesMap != null) {
			absensesMap.forEach((type, list) -> {
				list.forEach(v -> {
					v.setType(type);
					configData.getAbsenses().add(v);
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

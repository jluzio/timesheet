package org.example.timesheet.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ConfigData implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Absence> absences = new ArrayList<>();
	private List<DayOff> daysOff = new ArrayList<>();

}

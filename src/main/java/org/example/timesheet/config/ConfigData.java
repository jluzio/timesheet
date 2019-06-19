package org.example.timesheet.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfigData implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Absense> absenses = new ArrayList<>();
	private List<DayOff> daysOff = new ArrayList<>();

	public ConfigData() {
		super();
	}

	public List<Absense> getAbsenses() {
		return absenses;
	}

	public void setAbsenses(List<Absense> absenses) {
		this.absenses = absenses;
	}

	public List<DayOff> getDaysOff() {
		return daysOff;
	}

	public void setDaysOff(List<DayOff> daysOff) {
		this.daysOff = daysOff;
	}

	@Override
	public String toString() {
		return "ConfigData [absenses=" + absenses + ", daysOff=" + daysOff + "]";
	}
	
}

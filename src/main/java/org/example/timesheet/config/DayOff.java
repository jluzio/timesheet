package org.example.timesheet.config;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

@Data
public class DayOff implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private LocalDate date;
	private DayOffType type;

	public DayOff() {
		super();
	}

	public DayOff(LocalDate date) {
		super();
		this.date = date;
	}

	public DayOff(String name, LocalDate date, DayOffType type) {
		super();
		this.name = name;
		this.date = date;
		this.type = type;
	}

}

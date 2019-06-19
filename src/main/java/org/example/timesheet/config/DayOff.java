package org.example.timesheet.config;

import java.io.Serializable;
import java.time.LocalDate;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public DayOffType getType() {
		return type;
	}

	public void setType(DayOffType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "DayOff [name=" + name + ", date=" + date + ", type=" + type + "]";
	}

}

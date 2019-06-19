package org.example.timesheet.config;

import java.io.Serializable;
import java.time.LocalDate;

public class Absense implements Serializable {
	private static final long serialVersionUID = 1L;
	private LocalDate date;
	private LocalDate endDate;
	private AbsenseType type;
	
	public Absense() {
		super();
	}

	public Absense(LocalDate date) {
		super();
		this.date = date;
	}
	
	public Absense(LocalDate date, LocalDate endDate) {
		super();
		this.date = date;
		this.endDate = endDate;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate startDate) {
		this.date = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public AbsenseType getType() {
		return type;
	}

	public void setType(AbsenseType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Absense [date=" + date + ", endDate=" + endDate + ", type=" + type + "]";
	}

}

package org.example.timesheet.config;

import java.time.LocalDate;

public class Vacation {
	private LocalDate date;
	private LocalDate endDate;
	
	public Vacation() {
		super();
	}

	public Vacation(LocalDate date) {
		super();
		this.date = date;
	}
	
	public Vacation(LocalDate date, LocalDate endDate) {
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

	@Override
	public String toString() {
		return "Vacation [date=" + date + ", endDate=" + endDate + "]";
	}

}

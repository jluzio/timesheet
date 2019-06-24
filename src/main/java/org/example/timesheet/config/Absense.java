package org.example.timesheet.config;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

@Data
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
	
}

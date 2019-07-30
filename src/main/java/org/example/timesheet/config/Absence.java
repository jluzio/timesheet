package org.example.timesheet.config;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

@Data
public class Absence implements Serializable {
	private static final long serialVersionUID = 1L;
	private LocalDate date;
	private LocalDate endDate;
	private AbsenceType type;
	
	public Absence() {
		super();
	}

	public Absence(LocalDate date) {
		super();
		this.date = date;
	}
	
	public Absence(LocalDate date, LocalDate endDate) {
		super();
		this.date = date;
		this.endDate = endDate;
	}
	
}

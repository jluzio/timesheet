package org.example.timesheet.config;

import java.util.List;

public class Vacations {
	private List<Vacation> vacations;

	public List<Vacation> getVacations() {
		return vacations;
	}

	public void setVacations(List<Vacation> vacations) {
		this.vacations = vacations;
	}

	@Override
	public String toString() {
		return "Vacations [vacations=" + vacations + "]";
	}
	
}

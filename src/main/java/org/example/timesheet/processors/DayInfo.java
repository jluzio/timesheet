package org.example.timesheet.processors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.example.timesheet.entries.Movement;
import org.example.timesheet.util.Formatters;

import com.google.common.base.Joiner;

public class DayInfo {
	private List<Movement> movements = new ArrayList<>();
	private LocalDateTime startDate;
	private LocalDateTime exitDate;
	private long workInMinutes;
	private long breakInMinutes;
	private String remarks;
	private boolean dayOff;
	
	public String getMovementsInfo() {
		Joiner joiner = Joiner.on('|');
		Stream<String> texts = movements.stream().map( (mov) -> mov.getInfo() );
		return joiner.join(texts.iterator());
	}

	public List<Movement> getMovements() {
		return movements;
	}

	public void setMovements(List<Movement> movements) {
		this.movements = movements;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getExitDate() {
		return exitDate;
	}

	public void setExitDate(LocalDateTime exitDate) {
		this.exitDate = exitDate;
	}

	public long getWorkInMinutes() {
		return workInMinutes;
	}

	public void setWorkInMinutes(long workInMinutes) {
		this.workInMinutes = workInMinutes;
	}

	public long getBreakInMinutes() {
		return breakInMinutes;
	}

	public void setBreakInMinutes(long breakInMinutes) {
		this.breakInMinutes = breakInMinutes;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isDayOff() {
		return dayOff;
	}

	public void setDayOff(boolean dayOff) {
		this.dayOff = dayOff;
	}

	@Override
	public String toString() {
		return "DayInfo ["
				+ "startDate=" + Formatters.format(startDate, Formatters.DATETIME_OUTPUT_FORMAT) 
				+ ", exitDate=" + Formatters.format(exitDate, Formatters.DATETIME_OUTPUT_FORMAT) 
				+ ", workInMinutes=" + workInMinutes
				+ ", breakInMinutes=" + breakInMinutes 
				+ ", remarks=" + remarks 
				+ ", dayOff=" + dayOff 
				+ ", movements=" + movements 
				+ "]";
	}

}

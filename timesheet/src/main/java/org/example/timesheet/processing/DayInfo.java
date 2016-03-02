package org.example.timesheet.processing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.example.timesheet.input.Movement;
import org.example.timesheet.util.Formatters;

import com.google.common.base.Joiner;

public class DayInfo {
	private List<Movement> movements = new ArrayList<>();
	private Date startDate;
	private Date exitDate;
	private long workInMinutes;
	private long breakInMinutes;
	private String remarks;
	
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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getExitDate() {
		return exitDate;
	}

	public void setExitDate(Date exitDate) {
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

	@Override
	public String toString() {
		return "DayInfo [startDate=" + Formatters.format(startDate, Formatters.DATETIME_OUTPUT_FORMAT) + ", exitDate=" + Formatters.format(exitDate, Formatters.DATETIME_OUTPUT_FORMAT) + ", workInMinutes=" + workInMinutes
				+ ", breakInMinutes=" + breakInMinutes + ", remarks=" + remarks + ", movements=" + movements + "]";
	}


}

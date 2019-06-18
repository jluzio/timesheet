package org.example.timesheet.processors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.example.timesheet.entries.Entry;
import org.example.timesheet.util.Formatters;

import com.google.common.base.Joiner;

public class DayInfo {
	private List<Entry> entries = new ArrayList<>();
	private LocalDateTime startDatetime;
	private LocalDateTime exitDatetime;
	private long workInMinutes;
	private long breakInMinutes;
	private String remarks;
	private boolean dayOff;
	
	public String getEntriesInfo() {
		Joiner joiner = Joiner.on('|');
		Stream<String> texts = entries.stream().map( (mov) -> mov.getInfo() );
		return joiner.join(texts.iterator());
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	public LocalDateTime getStartDatetime() {
		return startDatetime;
	}

	public void setStartDatetime(LocalDateTime startDate) {
		this.startDatetime = startDate;
	}

	public LocalDateTime getExitDatetime() {
		return exitDatetime;
	}

	public void setExitDatetime(LocalDateTime exitDate) {
		this.exitDatetime = exitDate;
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
				+ "startDatetime=" + Formatters.format(startDatetime, Formatters.DATETIME_OUTPUT_FORMAT) 
				+ ", exitDatetime=" + Formatters.format(exitDatetime, Formatters.DATETIME_OUTPUT_FORMAT) 
				+ ", workInMinutes=" + workInMinutes
				+ ", breakInMinutes=" + breakInMinutes 
				+ ", remarks=" + remarks 
				+ ", dayOff=" + dayOff 
				+ ", entries=" + entries 
				+ "]";
	}

}

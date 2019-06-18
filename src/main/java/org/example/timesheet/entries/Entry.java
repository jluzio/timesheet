package org.example.timesheet.entries;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.timesheet.util.Formatters;

import com.google.common.base.MoreObjects;

public class Entry {
	private LocalDate date;
	private LocalDateTime datetime;
	private EntryType type;
	private EntryTypeCode typeCode;
	private String remarks;

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalDateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}

	public EntryType getType() {
		return type;
	}

	public void setType(EntryType type) {
		this.type = type;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public EntryTypeCode getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(EntryTypeCode typeCode) {
		this.typeCode = typeCode;
	}

	public String getInfo() {
		return MoreObjects.toStringHelper(type.name())
					.add("t", Formatters.format(datetime, Formatters.TIME_OUTPUT_FORMAT))
					.add("rem", remarks)
					.toString();
	}
	
	@Override
	public String toString() {
		return "Entry ["
				+ "date=" + Formatters.format(date, Formatters.DATE_OUTPUT_FORMAT) 
				+ ", datetime=" + Formatters.format(datetime, Formatters.DATETIME_OUTPUT_FORMAT) 
				+ ", type=" + type 
				+ ", typeCode=" + typeCode 
				+ ", remarks=" + remarks 
				+ "]";
	}

}

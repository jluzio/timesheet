package org.example.timesheet.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.timesheet.util.Formatters;

import com.google.common.base.MoreObjects;

/**
 * Created by jluzio on 24/02/2015.
 */
public class Movement {
	private LocalDate date;
	private LocalDateTime datetime;
	private MovementType type;
	private MovementTypeCode typeCode;
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

	public MovementType getType() {
		return type;
	}

	public void setType(MovementType type) {
		this.type = type;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public MovementTypeCode getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(MovementTypeCode typeCode) {
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
		return "Movement ["
				+ "date=" + Formatters.format(date, Formatters.DATE_OUTPUT_FORMAT) 
				+ ", datetime=" + Formatters.format(datetime, Formatters.DATETIME_OUTPUT_FORMAT) 
				+ ", type=" + type 
				+ ", typeCode=" + typeCode 
				+ ", remarks=" + remarks 
				+ "]";
	}

}

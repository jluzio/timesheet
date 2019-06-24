package org.example.timesheet.entries;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.timesheet.util.Formatters;

import com.google.common.base.MoreObjects;

import lombok.Data;

@Data
public class Entry {
	private LocalDate date;
	private LocalDateTime datetime;
	private EntryType type;
	private EntryTypeCode typeCode;
	private String remarks;
	
	public String getInfo() {
		return MoreObjects.toStringHelper(type.name())
					.add("t", Formatters.format(datetime, Formatters.TIME_OUTPUT_FORMAT))
					.add("rem", remarks)
					.toString();
	}

}

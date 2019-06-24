package org.example.timesheet.processors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.example.timesheet.entries.Entry;

import com.google.common.base.Joiner;

import lombok.Data;

@Data
public class DayWorkData {
	private List<Entry> entries = new ArrayList<>();
	private LocalDateTime startDatetime;
	private LocalDateTime exitDatetime;
	private long workInMinutes;
	private long breakInMinutes;
	private String remarks;
	private boolean dayOff;
	private boolean absense;
	
	public String getEntriesInfo() {
		Joiner joiner = Joiner.on('|');
		Stream<String> texts = entries.stream().map( (mov) -> mov.getInfo() );
		return joiner.join(texts.iterator());
	}

}

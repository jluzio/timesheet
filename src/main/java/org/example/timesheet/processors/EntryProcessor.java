package org.example.timesheet.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.timesheet.entries.Entry;
import org.example.timesheet.entries.EntryType;

import com.google.common.base.Joiner;

@Named
public class EntryProcessor {
	private Logger log = LogManager.getLogger(getClass());
	
	public List<DayInfo> process(List<Entry> entries) {
		entries.sort( (m1,m2) -> m1.getDatetime().compareTo(m2.getDatetime()) );
		Joiner remarksJoiner = Joiner.on(';').skipNulls();
		Joiner movInfoJoiner = Joiner.on('|').skipNulls();

		LocalDate lastDay = null;
		DayInfo dayInfo = null;
		List<DayInfo> dayInfos = new ArrayList<>();
		for (int i = 0; i < entries.size(); i++) {
			Entry entry = entries.get(i);
			
			// day transition
			if (lastDay == null || !entry.getDate().equals(lastDay)) {
				if (dayInfo != null) {
					dayInfos.add(dayInfo);
				}
				dayInfo = new DayInfo();
			}
			lastDay = entry.getDate();
			
			log.debug("Processing {}", entry);
			
			Entry lastEntry = dayInfo.getEntries().isEmpty() ? null : dayInfo.getEntries().get(dayInfo.getEntries().size()-1);
			
			dayInfo.getEntries().add(entry);
			
			if (entry.getType() == EntryType.ENTER) {
				if (dayInfo.getStartDatetime() == null) {
					dayInfo.setStartDatetime(entry.getDatetime());
				}
				else if (lastEntry != null) {
					if (lastEntry.getType() == EntryType.SERVICE_EXIT) {
						// ignore entry
						long currentBreakInMinutes = ChronoUnit.MINUTES.between(lastEntry.getDatetime(), entry.getDatetime());
						String remarksText = remarksJoiner.join(
								dayInfo.getRemarks(),
								String.format("%s(%s)", lastEntry.getType(), movInfoJoiner.join(currentBreakInMinutes, lastEntry.getRemarks()))
							);
						dayInfo.setRemarks(remarksText);
					}
					else if (lastEntry.getType() == EntryType.EXIT) {
						long currentBreakInMinutes = ChronoUnit.MINUTES.between(lastEntry.getDatetime(), entry.getDatetime());
						long breakInMinutes = dayInfo.getBreakInMinutes() + currentBreakInMinutes;
						dayInfo.setBreakInMinutes(breakInMinutes);
					}
				}
			}
			else if (entry.getType() == EntryType.EXIT) {
				dayInfo.setExitDatetime(entry.getDatetime());
				long workInMinutes = ChronoUnit.MINUTES.between(dayInfo.getStartDatetime(), dayInfo.getExitDatetime()) - dayInfo.getBreakInMinutes();
				dayInfo.setWorkInMinutes(workInMinutes);
			}
			else if (entry.getType() == EntryType.HOLLIDAY || entry.getType() == EntryType.VACATION) {
				LocalDateTime startOfDay = entry.getDate().atStartOfDay();

				dayInfo.setDayOff(true);
				dayInfo.setWorkInMinutes(0);
				dayInfo.setBreakInMinutes(0);
				dayInfo.setRemarks(entry.getType().name());
				dayInfo.setStartDatetime(startOfDay);
				dayInfo.setExitDatetime(startOfDay);
			}
			
			if (i == entries.size() - 1) {
				dayInfos.add(dayInfo);
			}
		}
		
		return dayInfos;
	}

}

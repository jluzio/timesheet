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
public class EntryDataProcessor {
	private Logger log = LogManager.getLogger(getClass());
	
	public List<DayWorkData> process(List<Entry> entries) {
		entries.sort( (m1,m2) -> m1.getDatetime().compareTo(m2.getDatetime()) );
		Joiner remarksJoiner = Joiner.on(';').skipNulls();
		Joiner movInfoJoiner = Joiner.on('|').skipNulls();

		LocalDate lastDay = null;
		DayWorkData dayWorkData = null;
		List<DayWorkData> dayWorkDatas = new ArrayList<>();
		for (int i = 0; i < entries.size(); i++) {
			Entry entry = entries.get(i);
			
			// day transition
			if (lastDay == null || !entry.getDate().equals(lastDay)) {
				if (dayWorkData != null) {
					dayWorkDatas.add(dayWorkData);
				}
				dayWorkData = new DayWorkData();
			}
			lastDay = entry.getDate();
			
			log.debug("Processing {}", entry);
			
			Entry lastEntry = dayWorkData.getEntries().isEmpty() ? null : dayWorkData.getEntries().get(dayWorkData.getEntries().size()-1);
			
			dayWorkData.getEntries().add(entry);
			
			if (entry.getType() == EntryType.ENTER) {
				if (dayWorkData.getStartDatetime() == null) {
					dayWorkData.setStartDatetime(entry.getDatetime());
				}
				else if (lastEntry != null) {
					if (lastEntry.getType() == EntryType.SERVICE_EXIT) {
						// ignore entry
						long currentBreakInMinutes = ChronoUnit.MINUTES.between(lastEntry.getDatetime(), entry.getDatetime());
						String remarksText = remarksJoiner.join(
								dayWorkData.getRemarks(),
								String.format("%s(%s)", lastEntry.getType(), movInfoJoiner.join(currentBreakInMinutes, lastEntry.getRemarks()))
							);
						dayWorkData.setRemarks(remarksText);
					}
					else if (lastEntry.getType() == EntryType.EXIT) {
						long currentBreakInMinutes = ChronoUnit.MINUTES.between(lastEntry.getDatetime(), entry.getDatetime());
						long breakInMinutes = dayWorkData.getBreakInMinutes() + currentBreakInMinutes;
						dayWorkData.setBreakInMinutes(breakInMinutes);
					}
				}
			}
			else if (entry.getType() == EntryType.EXIT) {
				dayWorkData.setExitDatetime(entry.getDatetime());
				long workInMinutes = ChronoUnit.MINUTES.between(dayWorkData.getStartDatetime(), dayWorkData.getExitDatetime()) - dayWorkData.getBreakInMinutes();
				dayWorkData.setWorkInMinutes(workInMinutes);
			}
			else if (entry.getType() == EntryType.HOLLIDAY || entry.getType() == EntryType.VACATION) {
				LocalDateTime startOfDay = entry.getDate().atStartOfDay();

				dayWorkData.setDayOff(true);
				dayWorkData.setWorkInMinutes(0);
				dayWorkData.setBreakInMinutes(0);
				dayWorkData.setRemarks(entry.getType().name());
				dayWorkData.setStartDatetime(startOfDay);
				dayWorkData.setExitDatetime(startOfDay);
			}
			
			if (i == entries.size() - 1) {
				dayWorkDatas.add(dayWorkData);
			}
		}
		
		return dayWorkDatas;
	}

}

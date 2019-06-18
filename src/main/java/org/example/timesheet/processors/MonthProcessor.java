package org.example.timesheet.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Named;

@Named
public class MonthProcessor {
	
	public List<DayInfo> process(List<DayInfo> dayInfos, LocalDate month, boolean fillAllDays) {
		LocalDate dateInMonth = month != null ? month : dayInfos.get(0).getStartDatetime().toLocalDate();
		LocalDate firstDayOfMonth = dateInMonth.withDayOfMonth(1);
		LocalDate lastDayOfMonth = dateInMonth.plusMonths(1).withDayOfMonth(1).plusDays(-1);

		List<DayInfo> monthDayInfos = new ArrayList<>();
		for (LocalDate date = firstDayOfMonth; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
			final LocalDate findDate = date;
			Optional<DayInfo> dayInfoOptional = dayInfos.stream().filter(
					(dayInfo) -> dayInfo.getStartDatetime().toLocalDate().equals(findDate)
				).findFirst();
			
			if (dayInfoOptional.isPresent()) {
				monthDayInfos.add(dayInfoOptional.get());
			} else if (fillAllDays) {
				LocalDateTime datetimeValue = date.atStartOfDay();
				
				DayInfo dayInfo = new DayInfo();
				dayInfo.setStartDatetime(datetimeValue);
				dayInfo.setExitDatetime(datetimeValue);
				monthDayInfos.add(dayInfo);
			}
		}
		
		return monthDayInfos;
	}

}

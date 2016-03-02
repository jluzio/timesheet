package org.example.timesheet.processing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.example.timesheet.util.DateConverter;

@Named
public class MonthProcessor {
	@Inject
	private DateConverter timeUtil;
	
	public List<DayInfo> process(List<DayInfo> dayInfos, Date month, boolean fillAllDays) {
		Date dateInMonth = month != null ? month : dayInfos.get(0).getStartDate();
		LocalDate localDateInMonth = timeUtil.toLocalDateTime(dateInMonth).toLocalDate();
		LocalDate firstDayOfMonth = localDateInMonth.withDayOfMonth(1);
		LocalDate lastDayOfMonth = localDateInMonth.plusMonths(1).withDayOfMonth(1).plusDays(-1);

		List<DayInfo> monthDayInfos = new ArrayList<>();
		for (LocalDate date = firstDayOfMonth; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
			final LocalDate findDate = date;
			Optional<DayInfo> dayInfoOptional = dayInfos.stream().filter(
					(dayInfo) -> timeUtil.toLocalDateTime(dayInfo.getStartDate()).toLocalDate().equals(findDate)
				).findFirst();
			
			if (dayInfoOptional.isPresent()) {
				monthDayInfos.add(dayInfoOptional.get());
			} else if (fillAllDays) {
				Date dateValue = timeUtil.fromLocalDateTime(date.atStartOfDay());
				
				DayInfo dayInfo = new DayInfo();
				dayInfo.setStartDate(dateValue);
				dayInfo.setExitDate(dateValue);
				monthDayInfos.add(dayInfo);
			}
		}
		
		return monthDayInfos;
	}

}

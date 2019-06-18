package org.example.timesheet.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.example.timesheet.config.Holidays;
import org.example.timesheet.config.ProcessConfig;
import org.example.timesheet.config.Vacations;
import org.example.timesheet.entries.Entry;
import org.example.timesheet.util.ConfigDataUtil;

@Named
public class MonthDataProcessor {
	@Inject
	private EntryDataProcessor entryDataProcessor;
	@Inject
	private ConfigDataUtil configDataUtil;
	
	public List<DayWorkData> process(List<Entry> entries, ProcessConfig config) {
		List<DayWorkData> dayWorkDatas = entryDataProcessor.process(entries, config);
		LocalDate month = config.getMonth() != null ? config.getMonth() : null;
		return process(dayWorkDatas, month, config);
	}
	
	public List<DayWorkData> process(List<DayWorkData> dayWorkDatas, LocalDate month, ProcessConfig config) {
		LocalDate dateInMonth = month != null ? month : dayWorkDatas.get(0).getStartDatetime().toLocalDate();
		LocalDate firstDayOfMonth = dateInMonth.withDayOfMonth(1);
		LocalDate lastDayOfMonth = dateInMonth.plusMonths(1).withDayOfMonth(1).plusDays(-1);

		List<DayWorkData> monthDayInfos = new ArrayList<>();
		for (LocalDate date = firstDayOfMonth; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
			final LocalDate findDate = date;
			Optional<DayWorkData> dayInfoOptional = dayWorkDatas.stream().filter(
					(dayInfo) -> dayInfo.getStartDatetime().toLocalDate().equals(findDate)
				).findFirst();
			
			if (dayInfoOptional.isPresent()) {
				monthDayInfos.add(dayInfoOptional.get());
			} else if (config.isFillAllMonthDays()) {
				// TODO: set vacation or holiday ?
				LocalDateTime datetimeValue = date.atStartOfDay();
				boolean holiday = isHoliday(findDate, config.getHolidays());
				boolean vacations = isVacation(findDate, config.getVacations());
				
				DayWorkData dayWorkData = new DayWorkData();
				dayWorkData.setStartDatetime(datetimeValue);
				dayWorkData.setExitDatetime(datetimeValue);
				dayWorkData.setDayOff(holiday || vacations);
				dayWorkData.setHoliday(holiday);
				monthDayInfos.add(dayWorkData);
			}
		}
		
		return monthDayInfos;
	}
	
	private boolean isHoliday(LocalDate date, Holidays holidays) {
		return configDataUtil.isHoliday(date, holidays);
	}

	private boolean isVacation(LocalDate date, Vacations vacations) {
		return configDataUtil.isVacation(date, vacations);
	}

}

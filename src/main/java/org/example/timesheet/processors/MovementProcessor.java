package org.example.timesheet.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.timesheet.entries.Movement;
import org.example.timesheet.entries.MovementType;

import com.google.common.base.Joiner;

@Named
public class MovementProcessor {
	private Logger log = LogManager.getLogger(getClass());
	
	public List<DayInfo> process(List<Movement> movements) {
		movements.sort( (m1,m2) -> m1.getDatetime().compareTo(m2.getDatetime()) );
		Joiner remarksJoiner = Joiner.on(';').skipNulls();
		Joiner movInfoJoiner = Joiner.on('|').skipNulls();

		LocalDate lastDay = null;
		DayInfo dayInfo = null;
		List<DayInfo> dayInfos = new ArrayList<>();
		for (int i = 0; i < movements.size(); i++) {
			Movement movement = movements.get(i);
			
			// day transition
			if (lastDay == null || !movement.getDate().equals(lastDay)) {
				if (dayInfo != null) {
					dayInfos.add(dayInfo);
				}
				dayInfo = new DayInfo();
			}
			lastDay = movement.getDate();
			
			log.debug("Processing {}", movement);
			
			Movement lastMovement = dayInfo.getMovements().isEmpty() ? null : dayInfo.getMovements().get(dayInfo.getMovements().size()-1);
			
			dayInfo.getMovements().add(movement);
			
			if (movement.getType() == MovementType.ENTER) {
				if (dayInfo.getStartDate() == null) {
					dayInfo.setStartDate(movement.getDatetime());
				}
				else if (lastMovement != null) {
					if (lastMovement.getType() == MovementType.SERVICE_EXIT) {
						// ignore movement
						long currentBreakInMinutes = ChronoUnit.MINUTES.between(lastMovement.getDatetime(), movement.getDatetime());
						String remarksText = remarksJoiner.join(
								dayInfo.getRemarks(),
								String.format("%s(%s)", lastMovement.getType(), movInfoJoiner.join(currentBreakInMinutes, lastMovement.getRemarks()))
							);
						dayInfo.setRemarks(remarksText);
					}
					else if (lastMovement.getType() == MovementType.EXIT) {
						long currentBreakInMinutes = ChronoUnit.MINUTES.between(lastMovement.getDatetime(), movement.getDatetime());
						long breakInMinutes = dayInfo.getBreakInMinutes() + currentBreakInMinutes;
						dayInfo.setBreakInMinutes(breakInMinutes);
					}
				}
			}
			else if (movement.getType() == MovementType.EXIT) {
				dayInfo.setExitDate(movement.getDatetime());
				long workInMinutes = ChronoUnit.MINUTES.between(dayInfo.getStartDate(), dayInfo.getExitDate()) - dayInfo.getBreakInMinutes();
				dayInfo.setWorkInMinutes(workInMinutes);
			}
			else if (movement.getType() == MovementType.HOLLIDAY || movement.getType() == MovementType.VACATION) {
				LocalDateTime startOfDay = movement.getDate().atStartOfDay();

				dayInfo.setDayOff(true);
				dayInfo.setWorkInMinutes(0);
				dayInfo.setBreakInMinutes(0);
				dayInfo.setRemarks(movement.getType().name());
				dayInfo.setStartDate(startOfDay);
				dayInfo.setExitDate(startOfDay);
			}
			
			if (i == movements.size() - 1) {
				dayInfos.add(dayInfo);
			}
		}
		
		return dayInfos;
	}

}

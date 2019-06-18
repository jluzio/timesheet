package org.example.timesheet.reports;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import org.example.timesheet.processors.DayInfo;
import org.example.timesheet.util.Formatters;

import com.google.common.base.MoreObjects;

@Named
public class CsvReportWriter {
	
	public void write(List<DayInfo> dayInfos, Writer writer) throws IOException {
		String lineFormat = "%s\t%s\t%s\t%s\t%s\t%s\t%s%n";
		writer.write(
				String.format(lineFormat, 
						"Date", 
						"Work", 
						"Work (h)", 
						"Break", 
						"Remarks", 
						"Enter", 
						"Exit"
				)
		);
		for (DayInfo dayInfo : dayInfos) {
			String date = Formatters.format(dayInfo.getStartDatetime(), Formatters.TIME_OUTPUT_FORMAT);
			String startDate = Formatters.format(dayInfo.getExitDatetime(), Formatters.TIME_OUTPUT_FORMAT);
			String exitDate = Formatters.format(dayInfo.getExitDatetime(), Formatters.TIME_OUTPUT_FORMAT);
			writer.write(
					String.format(lineFormat, 
							date, 
							dayInfo.getWorkInMinutes(), 
							1f * dayInfo.getWorkInMinutes() / TimeUnit.HOURS.toMinutes(1), 
							dayInfo.getBreakInMinutes(), 
							MoreObjects.firstNonNull(dayInfo.getRemarks(), ""),
							startDate, 
							exitDate 
					)
			);
		}
	}

}

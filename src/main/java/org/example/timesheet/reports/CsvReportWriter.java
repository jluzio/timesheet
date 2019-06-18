package org.example.timesheet.reports;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import org.example.timesheet.processors.DayWorkData;
import org.example.timesheet.util.Formatters;

import com.google.common.base.MoreObjects;

@Named
public class CsvReportWriter {
	
	public void write(List<DayWorkData> dayWorkDatas, Writer writer) throws IOException {
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
		for (DayWorkData dayWorkData : dayWorkDatas) {
			String date = Formatters.format(dayWorkData.getStartDatetime(), Formatters.TIME_OUTPUT_FORMAT);
			String startDate = Formatters.format(dayWorkData.getExitDatetime(), Formatters.TIME_OUTPUT_FORMAT);
			String exitDate = Formatters.format(dayWorkData.getExitDatetime(), Formatters.TIME_OUTPUT_FORMAT);
			writer.write(
					String.format(lineFormat, 
							date, 
							dayWorkData.getWorkInMinutes(), 
							1f * dayWorkData.getWorkInMinutes() / TimeUnit.HOURS.toMinutes(1), 
							dayWorkData.getBreakInMinutes(), 
							MoreObjects.firstNonNull(dayWorkData.getRemarks(), ""),
							startDate, 
							exitDate 
					)
			);
		}
	}

}

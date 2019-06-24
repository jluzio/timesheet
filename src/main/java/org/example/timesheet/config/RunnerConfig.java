package org.example.timesheet.config;

import java.time.LocalDate;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RunnerConfig {
	public static enum ReportType {
		EXCEL, CSV
	}
	
	private LocalDate targetDate;
	private String entriesPath;
	private EntriesConfig entriesConfig;
	private String configDataPath;
	private String reportsPath;
	private String reportEncoding = "UTF-8";
	@XmlElement(name = "reportType")
	private List<ReportType> reportTypes;
	private boolean fillAllMonthDays = true;
	
}

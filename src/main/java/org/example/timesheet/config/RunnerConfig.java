package org.example.timesheet.config;

import java.time.LocalDate;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
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
	private List<ReportType> reportTypes;
	private boolean fillAllMonthDays = true;

	public RunnerConfig() {
		super();
	}

	public LocalDate getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(LocalDate targetDate) {
		this.targetDate = targetDate;
	}

	public String getEntriesPath() {
		return entriesPath;
	}

	public void setEntriesPath(String inputPath) {
		this.entriesPath = inputPath;
	}

	public EntriesConfig getEntriesConfig() {
		return entriesConfig;
	}

	public void setEntriesConfig(EntriesConfig entriesConfig) {
		this.entriesConfig = entriesConfig;
	}

	public String getReportsPath() {
		return reportsPath;
	}

	public void setReportsPath(String outputPath) {
		this.reportsPath = outputPath;
	}

	public String getReportEncoding() {
		return reportEncoding;
	}

	public void setReportEncoding(String outputEncoding) {
		this.reportEncoding = outputEncoding;
	}

	@XmlElement(name = "reportType")
	public List<ReportType> getReportTypes() {
		return reportTypes;
	}

	public void setReportTypes(List<ReportType> reportTypes) {
		this.reportTypes = reportTypes;
	}

	public boolean isFillAllMonthDays() {
		return fillAllMonthDays;
	}

	public void setFillAllMonthDays(boolean fillAllMonthDays) {
		this.fillAllMonthDays = fillAllMonthDays;
	}

	public String getConfigDataPath() {
		return configDataPath;
	}

	public void setConfigDataPath(String configDataPath) {
		this.configDataPath = configDataPath;
	}

}

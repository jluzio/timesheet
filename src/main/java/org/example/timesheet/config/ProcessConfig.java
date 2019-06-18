package org.example.timesheet.config;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.timesheet.config.RunnerConfig.ReportType;

public class ProcessConfig {
	private LocalDate month;
	private List<File> entriesFiles;
	private EntriesConfig entriesConfig;
	private Holidays holidays;
	private Vacations vacations;
	private Map<ReportType, File> reportFiles = new HashMap<>();
	private String reportEncoding = "UTF-8";
	private boolean fillAllMonthDays = true;

	public List<File> getEntriesFiles() {
		return entriesFiles;
	}

	public void setEntriesFiles(List<File> inputs) {
		this.entriesFiles = inputs;
	}

	public String getReportEncoding() {
		return reportEncoding;
	}

	public void setReportEncoding(String outputEncoding) {
		this.reportEncoding = outputEncoding;
	}

	public boolean isFillAllMonthDays() {
		return fillAllMonthDays;
	}

	public void setFillAllMonthDays(boolean fillAllMonthDays) {
		this.fillAllMonthDays = fillAllMonthDays;
	}

	public LocalDate getMonth() {
		return month;
	}

	public void setMonth(LocalDate month) {
		this.month = month;
	}

	public EntriesConfig getEntriesConfig() {
		return entriesConfig;
	}

	public void setEntriesConfig(EntriesConfig entriesConfig) {
		this.entriesConfig = entriesConfig;
	}

	public Holidays getHolidays() {
		return holidays;
	}

	public void setHolidays(Holidays holidays) {
		this.holidays = holidays;
	}

	public Vacations getVacations() {
		return vacations;
	}

	public void setVacations(Vacations vacations) {
		this.vacations = vacations;
	}

	public Map<ReportType, File> getReportFiles() {
		return reportFiles;
	}

	public void setReportFiles(Map<ReportType, File> reportFiles) {
		this.reportFiles = reportFiles;
	}

}
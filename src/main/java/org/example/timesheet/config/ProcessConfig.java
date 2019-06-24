package org.example.timesheet.config;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.timesheet.config.RunnerConfig.ReportType;

import lombok.Data;

@Data
public class ProcessConfig {
	private LocalDate month;
	private List<File> entriesFiles;
	private EntriesConfig entriesConfig;
	private ConfigData configData;
	private Map<ReportType, File> reportFiles = new HashMap<>();
	private String reportEncoding = "UTF-8";
	private boolean fillAllMonthDays = true;

}
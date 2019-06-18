package org.example.timesheet.config;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ProcessConfig {
	private Date month;
	private List<File> inputs;
	private InputConfig inputConfig;
	private File csvOutput;
	private File excelOutput;
	private String outputEncoding = "UTF-8";
	private boolean fillAllMonthDays = true;

	public List<File> getInputs() {
		return inputs;
	}

	public void setInputs(List<File> inputs) {
		this.inputs = inputs;
	}

	public File getCsvOutput() {
		return csvOutput;
	}

	public void setCsvOutput(File csvOutput) {
		this.csvOutput = csvOutput;
	}

	public File getExcelOutput() {
		return excelOutput;
	}

	public void setExcelOutput(File excelOutput) {
		this.excelOutput = excelOutput;
	}

	public String getOutputEncoding() {
		return outputEncoding;
	}

	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	public boolean isFillAllMonthDays() {
		return fillAllMonthDays;
	}

	public void setFillAllMonthDays(boolean fillAllMonthDays) {
		this.fillAllMonthDays = fillAllMonthDays;
	}

	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}

	public InputConfig getInputConfig() {
		return inputConfig;
	}

	public void setInputConfig(InputConfig inputConfig) {
		this.inputConfig = inputConfig;
	}

}
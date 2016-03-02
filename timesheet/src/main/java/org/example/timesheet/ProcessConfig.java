package org.example.timesheet;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ProcessConfig {
	private List<File> inputs;
	private String inputEncoding;
	private File csvOutput;
	private File excelOutput;
	private String outputEncoding = "UTF-8";
	private boolean fillAllMonthDays = true;
	private Date month;

	public List<File> getInputs() {
		return inputs;
	}

	public void setInputs(List<File> inputs) {
		this.inputs = inputs;
	}

	public String getInputEncoding() {
		return inputEncoding;
	}

	public void setInputEncoding(String inputEncoding) {
		this.inputEncoding = inputEncoding;
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

}
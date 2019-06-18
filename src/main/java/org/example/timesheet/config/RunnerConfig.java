package org.example.timesheet.config;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RunnerConfig {
	public static enum OutputTarget {
		EXCEL, CSV
	}

	private Date targetDate;
	private String inputPath;
	private InputConfig inputConfig;
	private String outputPath;
	private String outputEncoding = "UTF-8";
	private List<OutputTarget> outputTargets;
	private boolean fillAllMonthDays = true;

	public RunnerConfig() {
		super();
	}

	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public InputConfig getInputConfig() {
		return inputConfig;
	}

	public void setInputConfig(InputConfig inputConfig) {
		this.inputConfig = inputConfig;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getOutputEncoding() {
		return outputEncoding;
	}

	public void setOutputEncoding(String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	public List<OutputTarget> getOutputTargets() {
		return outputTargets;
	}

	public void setOutputTargets(List<OutputTarget> outputTargets) {
		this.outputTargets = outputTargets;
	}

	public boolean isFillAllMonthDays() {
		return fillAllMonthDays;
	}

	public void setFillAllMonthDays(boolean fillAllMonthDays) {
		this.fillAllMonthDays = fillAllMonthDays;
	}

	@Override
	public String toString() {
		return "RunnerConfig [targetDate=" + targetDate + ", inputPath=" + inputPath + ", inputConfig=" + inputConfig
				+ ", outputPath=" + outputPath + ", outputEncoding=" + outputEncoding + ", outputTargets="
				+ outputTargets + ", fillAllMonthDays=" + fillAllMonthDays + "]";
	}
	
}

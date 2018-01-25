package org.example.timesheet;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InputConfig {
	private String encoding;
	private String serviceExitText;
	private String dateFormat;
	private String dateTimeFormat;

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getServiceExitText() {
		return serviceExitText;
	}

	public void setServiceExitText(String serviceExitText) {
		this.serviceExitText = serviceExitText;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	@Override
	public String toString() {
		return "InputConfig [encoding=" + encoding + ", serviceExitText=" + serviceExitText + ", dateFormat="
				+ dateFormat + ", dateTimeFormat=" + dateTimeFormat + "]";
	}

}

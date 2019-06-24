package org.example.timesheet.config;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement
public class EntriesConfig {
	private String encoding;
	private String serviceExitText;
	private String dateFormat;
	private String dateTimeFormat;

}

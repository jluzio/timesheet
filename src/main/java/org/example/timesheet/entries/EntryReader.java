package org.example.timesheet.entries;

import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Named;

import org.example.timesheet.ProcessingException;
import org.example.timesheet.config.InputConfig;

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;

@Named
public class EntryReader {
	
	@Inject
	public EntryReader() {
		super();
	}

	public List<Entry> read(Reader reader, InputConfig inputConfig) throws ProcessingException {
		int lineIndex = -1;
		String line = null;
		try {
			List<Entry> entries = new ArrayList<>();

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(inputConfig.getDateFormat());
			DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern(inputConfig.getDateTimeFormat());
			Splitter splitter = Splitter.on(CharMatcher.javaIsoControl());

			try (Scanner scanner = new Scanner(reader)) {
				while (scanner.hasNextLine()) {
					line = scanner.nextLine();
					lineIndex++;
	
					// header
					if (lineIndex == 0 || line.startsWith("#")) {
						continue;
					}
	
					List<String> tokens = splitter.splitToList(line);
					String dateString = tokens.get(0);
					String timeString = tokens.get(1);
					String entryTypeString = tokens.get(2);
					String descString = tokens.size() > 3 ? tokens.get(3) : null;
	
					Entry fileEntry = new Entry();
					fileEntry.setDate(LocalDate.parse(dateString, dateFormatter));
					fileEntry.setDatetime(LocalDateTime.parse(dateString + " " + timeString, datetimeFormatter));
	
					switch (entryTypeString) {
					case "E":
						fileEntry.setType(EntryType.ENTER);
						fileEntry.setTypeCode(EntryTypeCode.ENTER);
						break;
					case "S":
						fileEntry.setTypeCode(EntryTypeCode.EXIT);
						if (Objects.equal(descString, inputConfig.getServiceExitText())) {
							fileEntry.setType(EntryType.SERVICE_EXIT);
						} else if (descString!=null && descString.length() > 0) {
							fileEntry.setType(EntryType.SERVICE_EXIT);
							fileEntry.setRemarks(descString);
						} else {
							fileEntry.setType(EntryType.EXIT);
						}
						break;
					case "C":
						fileEntry.setTypeCode(EntryTypeCode.CUSTOM);
						fileEntry.setType(EntryType.valueOf(descString));
						break;
	
					default:
						break;
					}
	
					entries.add(fileEntry);
				}
			}
			return entries;
		} catch (DateTimeParseException e) {
			throw new ProcessingException(String.format("Error in line[%s]: %s", lineIndex+1, line), e);
		}
	}

}

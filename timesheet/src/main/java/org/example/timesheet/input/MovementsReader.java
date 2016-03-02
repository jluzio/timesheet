package org.example.timesheet.input;

import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import javax.inject.Named;

import org.example.timesheet.ProcessingException;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

/**
 * Created by jluzio on 24/02/2015.
 */
@Named
public class MovementsReader {

	private static final String SERVICE_EXIT_TEXT = "Saída em Serviço";

	public List<Movement> read(Reader reader) throws ProcessingException {
		int lineIndex = -1;
		String line = null;
		try {
			List<Movement> entries = new ArrayList<>();

			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			DateFormat datetimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			Splitter splitter = Splitter.on(CharMatcher.JAVA_ISO_CONTROL);

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
	
					Movement fileEntry = new Movement();
					fileEntry.setDate(dateFormat.parse(dateString));
					fileEntry.setDatetime(datetimeFormat.parse(dateString + " " + timeString));
	
					switch (entryTypeString) {
					case "E":
						fileEntry.setType(MovementType.ENTER);
						break;
					case "S":
						if (Objects.equals(SERVICE_EXIT_TEXT, descString)) {
							fileEntry.setType(MovementType.SERVICE_EXIT);
						} else if (descString!=null && descString.length() > 0) {
							fileEntry.setType(MovementType.SERVICE_EXIT);
							fileEntry.setRemarks(descString);
						} else {
							fileEntry.setType(MovementType.EXIT);
						}
						break;
	
					default:
						break;
					}
	
					entries.add(fileEntry);
				}
			}
			return entries;
		} catch (ParseException e) {
			throw new ProcessingException(String.format("Error in line[%s]: %s", lineIndex+1, line), e);
		}
	}

}

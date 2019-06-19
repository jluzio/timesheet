package org.example.timesheet.config;

import java.io.File;

import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

@Named
public class EntriesConfigReader {
	
	public EntriesConfig read(File file) throws JAXBException {
		Unmarshaller unmarshaller = JAXBContext.newInstance(EntriesConfig.class).createUnmarshaller();
		return ((EntriesConfig) unmarshaller.unmarshal(file));
	}

}

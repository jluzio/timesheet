package org.example.timesheet.config;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.example.timesheet.AbstractTest;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Jsr310DateTimeTest extends AbstractTest {
	@Inject
	private ObjectMapper objectMapper;

	@Test
	public void testXml() throws Exception {
		JAXBContext jaxbCtx = JAXBContext.newInstance(Data.class);
		Marshaller marshaller = jaxbCtx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		Data data = new Data();
		data.setLocalDate(LocalDate.now());
		data.setLocalDateTime(LocalDateTime.now());
		data.setLocalTime(LocalTime.now());
		data.setZonedDateTime(ZonedDateTime.now());

		marshaller.marshal(data, System.out);

		StringWriter output = new StringWriter();
		marshaller.marshal(data, output);

		Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
		Data returnData = (Data) unmarshaller.unmarshal(new StringReader(output.toString()));
		System.out.println(returnData);
	}

	@Test
	public void testJson() throws Exception {
		Data data = new Data();
		data.setLocalDate(LocalDate.now());
		data.setLocalDateTime(LocalDateTime.now());
		data.setLocalTime(LocalTime.now());
		data.setZonedDateTime(ZonedDateTime.now());
		
		objectMapper.writeValue(System.out, data);

//		marshaller.marshal(data, System.out);
//
//		StringWriter output = new StringWriter();
//		marshaller.marshal(data, output);
//
//		Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
//		Data returnData = (Data) unmarshaller.unmarshal(new StringReader(output.toString()));
//		System.out.println(returnData);
	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Data {
		@XmlJavaTypeAdapter(io.github.threetenjaxb.core.LocalDateXmlAdapter.class)
		private LocalDate localDate;
		@XmlJavaTypeAdapter(io.github.threetenjaxb.core.LocalDateTimeXmlAdapter.class)
		private LocalDateTime localDateTime;
		@XmlJavaTypeAdapter(io.github.threetenjaxb.core.ZonedDateTimeXmlAdapter.class)
		private ZonedDateTime zonedDateTime;
		@XmlJavaTypeAdapter(io.github.threetenjaxb.core.LocalTimeXmlAdapter.class)
		private LocalTime localTime;

		public LocalDate getLocalDate() {
			return localDate;
		}

		public void setLocalDate(LocalDate localDate) {
			this.localDate = localDate;
		}

		public LocalDateTime getLocalDateTime() {
			return localDateTime;
		}

		public void setLocalDateTime(LocalDateTime localDateTime) {
			this.localDateTime = localDateTime;
		}

		public ZonedDateTime getZonedDateTime() {
			return zonedDateTime;
		}

		public void setZonedDateTime(ZonedDateTime zonedDateTime) {
			this.zonedDateTime = zonedDateTime;
		}

		public LocalTime getLocalTime() {
			return localTime;
		}

		public void setLocalTime(LocalTime localTime) {
			this.localTime = localTime;
		}

	}

}

package org.example.timesheet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
	
	@Bean("properties")
	public Properties getConfigProperties() throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Properties properties = new Properties();
		try (InputStream inputStream = classLoader.getResourceAsStream("timesheet.properties")) {
			properties.load(inputStream);
			return properties;
		}
	}
	
}

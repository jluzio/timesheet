package org.example.timesheet.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public enum CDIContext {
	INSTANCE;

	public static enum Mode {
		ANNOTATION, CLASSPATH_XML;
	}
	
	public static ApplicationContext appCtx() {
		return INSTANCE.get();
	}

	private final ApplicationContext context = newContext();

	@SuppressWarnings("resource")
	public ApplicationContext newContext() {
		Mode mode = Mode.CLASSPATH_XML;

		ApplicationContext context = null;
		if (mode == Mode.ANNOTATION) {
			AnnotationConfigApplicationContext annotationContext = new AnnotationConfigApplicationContext();
			annotationContext.scan("org.example.timesheet");
			annotationContext.refresh();

			context = annotationContext;
		} else if (mode == Mode.CLASSPATH_XML) {
			context = new ClassPathXmlApplicationContext(new String[] { "Spring-AutoScan.xml" });
//			context = new ClassPathXmlApplicationContext(new String[] { "META-INF/beans.xml" });
		}

		return context;
	}

	public ApplicationContext get() {
		return context;
	}

}

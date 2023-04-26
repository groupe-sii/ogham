package fr.sii.ogham.runtime.standalone;

import fr.sii.ogham.core.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineRunner {
	private static final Logger LOG = LoggerFactory.getLogger(StandaloneApp.class);
	
	public static void main(String[] args) {
		if (args.length == 0) {
			throw new IllegalArgumentException("missing action to run");
		}
		String action = args[0];
		int idx = action.lastIndexOf(".");
		String runnerClassName = action.substring(0, idx);
		String runnerMethod = action.substring(idx+1);
		try {
			run(runnerClassName, runnerMethod);
			System.exit(0);
		} catch(Exception e) {
			LOG.error("Failed to run '{}'", action, e);
			System.exit(1);
		}
	}
	
	private static void run(String runnerClassName, String runnerMethod) throws Exception {
		Class<?> klass = Class.forName(runnerClassName);
		Object runner = klass.getConstructor(MessagingService.class).newInstance(new StandaloneApp().init());
		klass.getMethod(runnerMethod).invoke(runner);
	}
}

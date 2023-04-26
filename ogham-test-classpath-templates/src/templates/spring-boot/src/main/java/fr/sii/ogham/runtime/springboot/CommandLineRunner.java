package fr.sii.ogham.runtime.springboot;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import fr.sii.ogham.core.service.MessagingService;

@RequiredArgsConstructor
@Slf4j
@Component
public class CommandLineRunner implements ApplicationRunner {
	private final MessagingService service;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args.getNonOptionArgs().isEmpty()) {
			return;
		}
		String action = args.getNonOptionArgs().get(0);
		if (action.isEmpty()) {
			throw new IllegalArgumentException("action to run is empty");
		}
		int idx = action.lastIndexOf(".");
		String runnerClassName = action.substring(0, idx);
		String runnerMethod = action.substring(idx+1);
		try {
			run(runnerClassName, runnerMethod);
			System.exit(0);
		} catch(Exception e) {
			log.error("Failed to run '{}'", action, e);
			System.exit(1);
		}
	}
	
	private void run(String runnerClassName, String runnerMethod) throws Exception {
		Class<?> klass = Class.forName(runnerClassName);
		Object runner = klass.getConstructor(MessagingService.class).newInstance(service);
		klass.getMethod(runnerMethod).invoke(runner);
	}

}

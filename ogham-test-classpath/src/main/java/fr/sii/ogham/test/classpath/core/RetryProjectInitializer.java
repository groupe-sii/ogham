package fr.sii.ogham.test.classpath.core;

import java.nio.file.Path;

import fr.sii.ogham.test.classpath.core.exception.ProjectInitializationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class RetryProjectInitializer implements ProjectInitializer {
	private final ProjectInitializer delegate;
	private final RetryStrategySupplier retryStrategySupplier;
	
	@Override
	public Project initialize(Path parentFolder, String identifier, ProjectVariables variables) throws ProjectInitializationException {
		RetryStrategy retryStrategy = retryStrategySupplier.get();
		do {
			try {
				return delegate.initialize(parentFolder, identifier, variables);
			} catch(ProjectInitializationException e) {
				log.error("Failed to initialize project. Retrying in {}ms...", retryStrategy.nextRetry() - System.currentTimeMillis(), e);
				try {
					retryStrategy.shouldRetry(e);
				} catch (InterruptedException e1) {
					// skip it
				}
			}
		} while(true);
	}

}

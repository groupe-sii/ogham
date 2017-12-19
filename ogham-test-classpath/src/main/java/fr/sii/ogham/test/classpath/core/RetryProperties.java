package fr.sii.ogham.test.classpath.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties("retry")
public class RetryProperties {
	int maxAttempts = 5;
	long delay = 5000;
}

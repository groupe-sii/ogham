package mock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.sii.ogham.core.exception.MessagingException;

@SpringBootApplication
public class MockApplication {
	public static void main(String[] args) throws MessagingException {
		SpringApplication.run(MockApplication.class, args);
	}

}

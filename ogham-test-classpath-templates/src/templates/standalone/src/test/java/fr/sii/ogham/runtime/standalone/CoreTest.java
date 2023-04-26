package fr.sii.ogham.runtime.standalone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CoreTest {
	StandaloneApp app;
	
	@BeforeEach
	public void setup() {
		app = new StandaloneApp();
	}

	@Test
	public void applicationLoads() {
		app.init();
	}
}

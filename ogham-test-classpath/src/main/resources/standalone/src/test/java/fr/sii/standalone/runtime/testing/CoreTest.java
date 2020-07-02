package fr.sii.standalone.runtime.testing;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class CoreTest {
	StandaloneApp app;
	
	@Before
	public void setup() {
		app = new StandaloneApp();
	}

	@Test
	public void applicationLoads() {
		app.init();
	}
}

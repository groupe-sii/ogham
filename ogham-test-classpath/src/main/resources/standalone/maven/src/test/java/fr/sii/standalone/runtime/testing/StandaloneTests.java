package fr.sii.standalone.runtime.testing;

import org.junit.Before;
import org.junit.Test;

public class StandaloneTests {
	StandaloneApp app;
	
	@Before
	public void setup() {
		app = new StandaloneApp();
	}
	
	@Test
	public void applicationLoads() {
		app.load();
	}
}

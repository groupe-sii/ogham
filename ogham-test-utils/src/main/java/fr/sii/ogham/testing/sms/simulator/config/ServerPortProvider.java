package fr.sii.ogham.testing.sms.simulator.config;

public interface ServerPortProvider {
	int getPort();
	
	void reset();
}

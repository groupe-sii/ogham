package fr.sii.ogham.core.retry;

public interface Retry {
	boolean terminated();
	
	long nextDate();
}

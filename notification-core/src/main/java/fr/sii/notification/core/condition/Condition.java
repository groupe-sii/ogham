package fr.sii.notification.core.condition;

public interface Condition<T> {
	public boolean accept(T obj);
}

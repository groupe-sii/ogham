package fr.sii.notification.core.builder;

import fr.sii.notification.core.exception.BuildException;

public interface Builder<T> {

	public T build() throws BuildException;

}
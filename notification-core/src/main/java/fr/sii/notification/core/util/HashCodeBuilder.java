package fr.sii.notification.core.util;


public class HashCodeBuilder {
	private org.apache.commons.lang3.builder.HashCodeBuilder delegate;

	public HashCodeBuilder(int initialOddNumber, int multiplierOddNumber) {
		super();
		delegate = new org.apache.commons.lang3.builder.HashCodeBuilder(initialOddNumber, multiplierOddNumber);
	}

	public HashCodeBuilder() {
		super();
		delegate = new org.apache.commons.lang3.builder.HashCodeBuilder();
	}

	public HashCodeBuilder append(Object objectValue) {
		delegate.append(objectValue);
		return this;
	}


	public HashCodeBuilder appendSuper(int superHashCode) {
		delegate.appendSuper(superHashCode);
		return this;
	}

	public int hashCode() {
		return delegate.hashCode();
	}
}

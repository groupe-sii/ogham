package fr.sii.ogham.testing.assertion.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

/**
 * Provides a fixed string as description.
 * 
 * @author Aurélien Baudet
 *
 */
public class FixedDescription implements Description {
	private final String description;

	public FixedDescription(String description) {
		super();
		this.description = description;
	}

	@Override
	public Description appendText(String text) {
		return this;
	}

	@Override
	public Description appendDescriptionOf(SelfDescribing value) {
		return this;
	}

	@Override
	public Description appendValue(Object value) {
		return this;
	}

	@SafeVarargs
	@Override
	public final <T> Description appendValueList(String start, String separator, String end, T... values) {
		return this;
	}

	@Override
	public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
		return this;
	}

	@Override
	public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
		return this;
	}

	@Override
	public String toString() {
		return description;
	}

}

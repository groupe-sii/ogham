package mock.builder;

import fr.sii.ogham.core.builder.Builder;

public class MockBuilder<T> implements Builder<T> {
	private T value;

	@Override
	public T build() {
		return value;
	}
	
	public void someValue(T value) {
		this.value = value;
	}
}

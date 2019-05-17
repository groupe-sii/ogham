package fr.sii.ogham.helper.email;

import com.google.common.base.Predicate;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AnyPredicate<T> implements Predicate<T> {
	@Override
	public boolean apply(T input) {
		return true;
	}

	@Override
	public boolean test(@Nullable T input) {
		return false;
	}
}

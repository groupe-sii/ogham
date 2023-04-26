package fr.sii.ogham.testing.assertion.filter;


import java.util.function.Predicate;

public class AnyPredicate<T> implements Predicate<T> {
	@Override
	public boolean test(T input) {
		return true;
	}
}

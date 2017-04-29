package fr.sii.ogham.core.convert;

public interface SupportingConverter extends Converter {
	boolean supports(Class<?> sourceType, Class<?> targetType);
}

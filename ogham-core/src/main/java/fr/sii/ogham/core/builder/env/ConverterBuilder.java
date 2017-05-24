package fr.sii.ogham.core.builder.env;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.convert.Converter;
import fr.sii.ogham.core.convert.ConverterRegistry;
import fr.sii.ogham.core.convert.DefaultConverter;
import fr.sii.ogham.core.convert.SupportingConverter;

/**
 * Builder that configures how conversions are applied.
 * 
 * <p>
 * The user can use the default converter and he can register a custom converter
 * ({@link #register(SupportingConverter)}).
 * </p>
 * 
 * <p>
 * The user can override default converter to use its own implementation
 * ({@link #override(Converter)}).
 * </p>
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            the type of the parent builder (when calling {@link #and()}
 *            method)
 */
public interface ConverterBuilder<P> extends Parent<P>, Builder<Converter> {
	/**
	 * Provide your own converter implementation.
	 * 
	 * <p>
	 * If you custom implementation also implements {@link ConverterRegistry}
	 * interface, you can still use {@link #register(SupportingConverter)} to
	 * register specific converters. If it doesn't implement
	 * {@link ConverterRegistry}, calling {@link #register(SupportingConverter)}
	 * has no effect.
	 * </p>
	 * 
	 * @param converter
	 *            the custom converter to use
	 * @return this instance of fluent use
	 */
	ConverterBuilder<P> override(Converter converter);

	/**
	 * Register a custom converter. By default, {@link DefaultConverter}
	 * implementation is used. This implementation also implements
	 * {@link ConverterRegistry}. So any registered converter is registered in
	 * the {@link ConverterRegistry}.
	 * 
	 * <p>
	 * The {@link DefaultConverter} implementation delegates all conversions to
	 * registered {@link SupportingConverter}s.
	 * </p>
	 * 
	 * <p>
	 * If you don't want to use {@link DefaultConverter} but use your own
	 * implementation, use {@link #override(Converter)} method.
	 * </p>
	 * 
	 * @param converter
	 *            the converter to register into the {@link ConverterRegistry}
	 * @return this instance for fluent chaining
	 */
	ConverterBuilder<P> register(SupportingConverter converter);
}

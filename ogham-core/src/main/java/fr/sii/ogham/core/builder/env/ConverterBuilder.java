package fr.sii.ogham.core.builder.env;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.Parent;
import fr.sii.ogham.core.builder.configuration.MayOverride;
import fr.sii.ogham.core.builder.configurer.Configurer;
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
	 * If your custom implementation also implements {@link ConverterRegistry}
	 * interface, you can still use {@link #register(SupportingConverter)} to
	 * register specific converters. If it doesn't implement
	 * {@link ConverterRegistry}, calling {@link #register(SupportingConverter)}
	 * has no effect.
	 * 
	 * <p>
	 * If {@code null} value is set, it disables custom converter and default
	 * converter is used.
	 * 
	 * @param converter
	 *            the custom converter to use
	 * @return this instance of fluent use
	 */
	ConverterBuilder<P> override(Converter converter);

	/**
	 * Provide the default converter implementation.
	 * 
	 * <p>
	 * The default converter is used if {@link #override(Converter)} is never
	 * used or has been set to null.
	 * 
	 * <p>
	 * If converter implementation also implements {@link ConverterRegistry}
	 * interface, you can still use {@link #register(SupportingConverter)} to
	 * register specific converters. If it doesn't implement
	 * {@link ConverterRegistry}, calling {@link #register(SupportingConverter)}
	 * has no effect.
	 * 
	 * <p>
	 * Automatic configuration is based on priority order. Higher priority is
	 * applied first. It means that the lowest priority is applied last and
	 * overrides any default value set by a {@link Configurer} with higher
	 * priority using {@link #override(Converter)}.
	 * 
	 * <p>
	 * This method gives more control on how {@link Configurer}s should provide
	 * a default converter. Each {@link Configurer} can decide if its default
	 * converter should override or not a previously default converter set by a
	 * {@link Configurer} with higher priority. It can also be used to control
	 * default converter override or not with {@code null} value.
	 * 
	 * <p>
	 * If every {@link Configurer} uses
	 * {@link MayOverride#overrideIfNotSet(Object)}:
	 * 
	 * <pre>
	 * .defaultConverter(MayOverride.overrideIfNotSet(new DefaultConverter()))
	 * </pre>
	 * 
	 * Then the default converter comes from the first applied
	 * {@link Configurer} (the one with highest priority) that sets a non-null
	 * default converter.
	 * 
	 * @param converter
	 *            the custom converter to use
	 * @return this instance of fluent use
	 */
	ConverterBuilder<P> defaultConverter(MayOverride<Converter> converter);

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

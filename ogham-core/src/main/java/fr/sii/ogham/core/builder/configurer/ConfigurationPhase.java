package fr.sii.ogham.core.builder.configurer;

import fr.sii.ogham.core.builder.MessagingBuilder;
import fr.sii.ogham.core.builder.env.PropertiesBuilder;
import fr.sii.ogham.core.service.MessagingService;

/**
 * Enumeration for Ogham configuration phases for auto-configuration lifecycle:
 * 
 * <ol>
 * <li>{@link MessagingBuilder} instantiation (using
 * {@link MessagingBuilder#standard()} or
 * {@link MessagingBuilder#minimal()}).</li>
 * <li>Search {@link MessagingConfigurer} classes in the classpath and register
 * found classes ordered by higher priority.</li>
 * <li><strong>Trigger {@link ConfigurationPhase#AFTER_INIT} phase</strong>:
 * Instantiate and configure previously registered {@link Configurer}s (only
 * configurers registered for {@code AFTER_INIT} phase).</li>
 * <li>Developer can configure Ogham using {@link MessagingBuilder}
 * instance.</li>
 * <li>Developer has finished configuring Ogham so he calls
 * {@link MessagingBuilder#build()}.</li>
 * <li><strong>Trigger {@link ConfigurationPhase#BEFORE_BUILD} phase</strong>:
 * Instantiate and configure previously registered {@link Configurer}s (only
 * configurers registered for {@code BEFORE_BUILD} phase.</li>
 * <li>Instantiate {@link MessagingService} according to
 * {@link MessagingBuilder} configuration.</li>
 * <li>Developer gets an instance of {@link MessagingService} completely
 * configured.</li>
 * </ol>
 * 
 * @see ConfigurerFor
 * @see MessagingBuilder#register(MessagingConfigurer, int, ConfigurationPhase)
 * @author Aurélien Baudet
 *
 */
public enum ConfigurationPhase {
	/**
	 * Early configuration phase.
	 * 
	 * <p>
	 * Property values provided by developer are not available because this
	 * phase is executed <strong>immediately</strong> when calling
	 * {@link MessagingBuilder#standard()} or
	 * {@link MessagingBuilder#minimal()}.
	 * {@link PropertiesBuilder#set(String, String)} used to set property values
	 * from code is called <strong>after</strong>.
	 * </p>
	 * 
	 * <p>
	 * <strong>WARNING</strong>: Configuration set at this phase may not be
	 * overridable by developer.
	 * </p>
	 */
	AFTER_INIT,

	/**
	 * Configuration applied just before building {@link MessagingService}.
	 * 
	 * <p>
	 * At this time, all property values can be safely used.
	 * </p>
	 * 
	 * <p>
	 * Any configuration set at this phase has lower priority than any
	 * configuration provided by developer or configuration initialized during
	 * {@link ConfigurationPhase#AFTER_INIT} phase.
	 * </p>
	 */
	BEFORE_BUILD;
}

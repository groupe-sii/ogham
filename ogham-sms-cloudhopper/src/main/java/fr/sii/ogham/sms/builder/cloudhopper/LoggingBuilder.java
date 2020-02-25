package fr.sii.ogham.sms.builder.cloudhopper;

import com.cloudhopper.smpp.pdu.Pdu;
import com.cloudhopper.smpp.type.LoggingOptions;

import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.fluent.AbstractParent;

/**
 * * Configure logs:
 * <ul>
 * <li>Enable/disable log of {@link Pdu}s</li>
 * <li>Enable/disable log of bytes</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class LoggingBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<LoggingOptions> {
	private LoggingOptions options;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method.
	 * 
	 * @param parent
	 *            the parent builder
	 */
	public LoggingBuilder(CloudhopperBuilder parent) {
		super(parent);
	}

	/**
	 * Provide a {@link LoggingOptions} instance to:
	 * <ul>
	 * <li>Enable/disable log of {@link Pdu}s</li>
	 * <li>Enable/disable log of bytes</li>
	 * </ul>
	 * 
	 * If this method is called several times, only the last instance is used.
	 * 
	 * @param options
	 *            the logging options
	 * @return this instance for fluent chaining
	 */
	public LoggingBuilder options(LoggingOptions options) {
		this.options = options;
		return this;
	}

	@Override
	public LoggingOptions build() {
		return options;
	}

}

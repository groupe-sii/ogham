package fr.sii.ogham.sms.builder.ovh;

import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.exception.builder.BuildException;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.sms.sender.impl.ovh.OvhOptions;
import fr.sii.ogham.sms.sender.impl.ovh.SmsCoding;

/**
 * Configures OVH SMS options:
 * <ul>
 * <li>Enable/disable the "STOP" indication at the end of the message (useful to
 * disable for non-commercial SMS)</li>
 * <li>Define the SMS encoding (see {@link SmsCoding}): 1 for 7bit encoding, 2
 * for 8bit encoding (UTF-8). If you use UTF-8, your SMS will have a maximum
 * size of 70 characters instead of 160</li>
 * <li>Define a tag to mark sent messages (a 20 maximum character string)</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class OvhOptionsBuilder extends AbstractParent<OvhSmsBuilder> implements Builder<OvhOptions> {
	private EnvironmentBuilder<?> environmentBuilder;
	private List<String> noStops;
	private Boolean noStop;
	private List<String> tags;
	private List<String> smsCodings;
	private SmsCoding smsCoding;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 */
	public OvhOptionsBuilder(OvhSmsBuilder parent, EnvironmentBuilder<?> environmentBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		noStops = new ArrayList<>();
		tags = new ArrayList<>();
		smsCodings = new ArrayList<>();
	}

	/**
	 * Enable/disable "STOP" indication at the end of the message (useful to
	 * disable for non-commercial SMS).
	 * 
	 * <p>
	 * You can specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .soStop("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param noStop
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public OvhOptionsBuilder noStop(String... noStop) {
		for (String n : noStop) {
			if (n != null) {
				noStops.add(n);
			}
		}
		return this;
	}

	/**
	 * Enable/disable "STOP" indication at the end of the message (useful to
	 * disable for non-commercial SMS).
	 * 
	 * @param noStop
	 *            enable or disable (no effect if {@code null})
	 * @return this instance for fluent chaining
	 */
	public OvhOptionsBuilder noStop(Boolean noStop) {
		if (noStop != null) {
			this.noStop = noStop;
		}
		return this;
	}

	/**
	 * Set a tag to mark sent messages (20 maximum character string).
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .tag("my tag");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .tag("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param tag
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public OvhOptionsBuilder tag(String... tag) {
		for (String t : tag) {
			if (t != null) {
				tags.add(t);
			}
		}
		return this;
	}

	/**
	 * Set the message encoding:
	 * <ul>
	 * <li>1 for 7bit encoding</li>
	 * <li>2 for 8bit encoding (UTF-8)</li>
	 * </ul>
	 * If you use UTF-8, your SMS will have a maximum size of 70 characters
	 * instead of 160
	 * 
	 * You can specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .smsCoding("${custom.property.high-priority}", "${custom.property.low-priority}");
	 * </pre>
	 * 
	 * The properties are not immediately evaluated. The evaluation will be done
	 * when the {@link #build()} method is called.
	 * 
	 * If you provide several property keys, evaluation will be done on the
	 * first key and if the property exists (see {@link EnvironmentBuilder}),
	 * its value is used. If the first property doesn't exist in properties,
	 * then it tries with the second one and so on.
	 * 
	 * @param smsCoding
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public OvhOptionsBuilder smsCoding(String... smsCoding) {
		for (String s : smsCoding) {
			if (s != null) {
				smsCodings.add(s);
			}
		}
		return this;
	}

	/**
	 * Set the message encoding:
	 * <ul>
	 * <li>{@link SmsCoding#NORMAL}: 7bit encoding</li>
	 * <li>{@link SmsCoding#UTF_8}: 8bit encoding (UTF-8)</li>
	 * </ul>
	 * If you use UTF-8, your SMS will have a maximum size of 70 characters
	 * instead of 160
	 * 
	 * @param smsCoding
	 *            the SMS encoding
	 * @return this instance for fluent chaining
	 */
	public OvhOptionsBuilder smsCoding(SmsCoding smsCoding) {
		if (smsCoding != null) {
			this.smsCoding = smsCoding;
		}
		return this;
	}

	@Override
	public OvhOptions build() throws BuildException {
		PropertyResolver propertyResolver = environmentBuilder.build();
		boolean noStop = buildNoStop(propertyResolver);
		String tag = buildTag(propertyResolver);
		SmsCoding smsCoding = buildSmsCoding(propertyResolver);
		return new OvhOptions(noStop, tag, smsCoding);
	}

	private boolean buildNoStop(PropertyResolver propertyResolver) {
		if (noStop != null) {
			return noStop;
		}
		return BuilderUtils.evaluate(noStops, propertyResolver, Boolean.class);
	}

	private String buildTag(PropertyResolver propertyResolver) {
		return BuilderUtils.evaluate(tags, propertyResolver, String.class);
	}

	private SmsCoding buildSmsCoding(PropertyResolver propertyResolver) {
		if (smsCoding != null) {
			return smsCoding;
		}
		String name = BuilderUtils.evaluate(smsCodings, propertyResolver, String.class);
		return name == null ? null : SmsCoding.valueOf(name);
	}
}

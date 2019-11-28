package fr.sii.ogham.sms.builder.cloudhopper;

import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM7;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_GSM8;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_ISO_8859_1;
import static com.cloudhopper.commons.charset.CharsetUtil.NAME_UCS_2;
import static fr.sii.ogham.sms.SmsConstants.SmppSplitConstants.SEGMENT_SIZE_GSM_7BIT_SMS_PACKING_MODE;
import static fr.sii.ogham.sms.SmsConstants.SmppSplitConstants.SEGMENT_SIZE_GSM_8BIT;
import static fr.sii.ogham.sms.SmsConstants.SmppSplitConstants.SEGMENT_SIZE_UCS2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.sii.ogham.core.builder.AbstractParent;
import fr.sii.ogham.core.builder.Builder;
import fr.sii.ogham.core.builder.env.EnvironmentBuilder;
import fr.sii.ogham.core.env.PropertyResolver;
import fr.sii.ogham.core.util.BuilderUtils;
import fr.sii.ogham.core.util.PriorizedList;
import fr.sii.ogham.sms.SmsConstants.SmppSplitConstants.SegmentSizes;
import fr.sii.ogham.sms.encoder.Encoder;
import fr.sii.ogham.sms.encoder.SupportingEncoder;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.CloudhopperCharsetSupportingEncoder;
import fr.sii.ogham.sms.sender.impl.cloudhopper.encoder.NamedCharset;
import fr.sii.ogham.sms.sender.impl.cloudhopper.splitter.SupportedEncoderConditionalSplitter;
import fr.sii.ogham.sms.splitter.FirstSupportingMessageSplitter;
import fr.sii.ogham.sms.splitter.GsmMessageSplitter;
import fr.sii.ogham.sms.splitter.MessageSplitter;
import fr.sii.ogham.sms.splitter.RandomReferenceNumberGenerator;
import fr.sii.ogham.sms.splitter.ReferenceNumberGenerator;
import fr.sii.ogham.sms.splitter.SupportingSplitter;

/**
 * Configures how Cloudhopper will split messages.
 * 
 * <p>
 * The splitter will check if the whole message can fit in a single segment. If
 * not the splitter will split the whole message in several segments with a
 * header to indicate splitting information such as number of segments,
 * reference number and current segment number.
 * 
 * <p>
 * {@link Encoder} configured using {@link CloudhopperBuilder#encoder()} is used
 * to encode each segment.
 * 
 * <p>
 * If automatic guessing of best standard encoder is enabled for {@link Encoder}
 * (using {@code encoder().autoGuess(true)}), and message splitting is enabled,
 * then standard message splitting is configured such as:
 * <ul>
 * <li>If GSM 7-bit encoder is enabled, {@link GsmMessageSplitter} is used to
 * split messages that support this encoding. If whole message can fit in a
 * single segment of 160 characters. Longer message is split into segments of
 * either 153 characters or 152 characters (depending on reference number
 * generation, see {@link ReferenceNumberGenerator})</li>
 * <li>If GSM 8-bit encoder is enabled, {@link GsmMessageSplitter} is used to
 * split messages that support this encoding. If whole message can fit in a
 * single segment of 140 characters. Longer message is split into segments of
 * either 134 characters or 133 characters (depending on reference number
 * generation, see {@link ReferenceNumberGenerator})</li>
 * <li>If UCS-2 encoder is enabled, {@link GsmMessageSplitter} is used to split
 * messages that support this encoding. If whole message can fit in a single
 * segment of 70 characters. Longer message is split into segments of either 67
 * characters or 66 characters (depending on reference number generation, see
 * {@link ReferenceNumberGenerator})</li>
 * </ul>
 * 
 * Each registered splitter uses the same priority as associated
 * {@link Encoder}.
 * 
 * If you don't want standard message splitting based on supported
 * {@link Encoder}s, you can either disable message splitting or provide a
 * custom splitter with higher priority.
 * 
 * <p>
 * This builder allows to configure:
 * <ul>
 * <li>Enable/disable message splitting</li>
 * <li>Provide a custom split strategy</li>
 * <li>Choose strategy for reference number generation</li>
 * </ul>
 * 
 * <pre>
 * {@code
 * .splitter()
 *   .enable("${ogham.sms.cloudhopper.split.enable}", "${ogham.sms.split.enable}", "true")
 *   .customSplitter(new MyCustomSplitter(), 100000)
 *   .referenceNumber()
 *     .random()
 *     .random(new Random())
 *     .generator(new MyCustomReferenceNumberGenerator())
 * }
 * </pre>
 * 
 * @author Aurélien Baudet
 *
 */
public class MessageSplitterBuilder extends AbstractParent<CloudhopperBuilder> implements Builder<MessageSplitter> {
	private final EnvironmentBuilder<?> environmentBuilder;
	private final ReadableEncoderBuilder encoderBuilder;
	private final List<String> enableProps;
	private final PriorizedList<MessageSplitter> customSplitters;
	private MessageSplitter customSplitter;
	private ReferenceNumberGeneratorBuilder referenceNumberBuilder;

	/**
	 * Initializes the builder with a parent builder. The parent builder is used
	 * when calling {@link #and()} method. The {@link EnvironmentBuilder} is
	 * used to evaluate properties when {@link #build()} method is called.
	 * 
	 * @param parent
	 *            the parent builder
	 * @param environmentBuilder
	 *            the configuration for property resolution and evaluation
	 * @param encoderBuilder
	 *            the encoder builder that is used to configure standard message
	 *            splitting based on encoding charset
	 */
	public MessageSplitterBuilder(CloudhopperBuilder parent, EnvironmentBuilder<?> environmentBuilder, ReadableEncoderBuilder encoderBuilder) {
		super(parent);
		this.environmentBuilder = environmentBuilder;
		this.encoderBuilder = encoderBuilder;
		enableProps = new ArrayList<>();
		customSplitters = new PriorizedList<>();
	}

	/**
	 * Enable/disable message splitting.
	 * 
	 * <p>
	 * If several properties are already set using {@link #enable(String...)},
	 * then the enabled value is appended.
	 * 
	 * For example,
	 * 
	 * <pre>
	 * {@code 
	 * .enable("${custom.property.high-priority}")
	 * .enable(true)
	 * }
	 * </pre>
	 * 
	 * If "custom.property.high-priority" property doesn't exist, then
	 * {@code .enable(true)} is used. If "custom.property.high-priority"
	 * property exists, then the value of "custom.property.high-priority" is
	 * used.
	 * 
	 * <pre>
	 * {@code 
	 * .enable(true)
	 * .enable("${custom.property.high-priority}")
	 * }
	 * </pre>
	 * 
	 * The value of {@code .enable(true)} is always used.
	 * 
	 * @param enable
	 *            enable (true) or disable (false) message splitting
	 * @return this instance for fluent chaining
	 */
	public MessageSplitterBuilder enable(boolean enable) {
		enableProps.add(String.valueOf(enable));
		return this;
	}

	/**
	 * Enable/disable message splitting.
	 * 
	 * You can specify a direct value. For example:
	 * 
	 * <pre>
	 * .enable("true");
	 * </pre>
	 * 
	 * <p>
	 * You can also specify one or several property keys. For example:
	 * 
	 * <pre>
	 * .enable("${custom.property.high-priority}", "${custom.property.low-priority}");
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
	 * @param enable
	 *            one value, or one or several property keys
	 * @return this instance for fluent chaining
	 */
	public MessageSplitterBuilder enable(String... enable) {
		Collections.addAll(enableProps, enable);
		return this;
	}

	/**
	 * Configures how Cloudhopper should generate a reference number.
	 * 
	 * <p>
	 * Reference number is used to identify segments that belong to the same
	 * message. Every segment of the split message must have the same reference
	 * number.
	 * 
	 * <p>
	 * This builder allows to configure:
	 * <ul>
	 * <li>Enable random generation strategy</li>
	 * <li>Customize random generation by providing a custom {@link Random}</li>
	 * <li>Provide a custom generator</li>
	 * </ul>
	 * 
	 * <pre>
	 * {@code
	 *   .referenceNumber()
	 *     .random()
	 *     .random(new Random())
	 *     .generator(new MyCustomReferenceNumberGenerator())
	 * }
	 * </pre>
	 * 
	 * 
	 * @return the builder to configure reference number generation
	 * @see ReferenceNumberGenerator
	 */
	public ReferenceNumberGeneratorBuilder referenceNumber() {
		if (referenceNumberBuilder == null) {
			referenceNumberBuilder = new ReferenceNumberGeneratorBuilder(this);
		}
		return referenceNumberBuilder;
	}

	/**
	 * Register a custom splitter strategy.
	 * 
	 * <p>
	 * Using this method totally disable all other features. Only the provided
	 * splitter is used.
	 * 
	 * <p>
	 * If this method is called several times, only the last one is used.
	 * 
	 * <p>
	 * If {@code null} value is provided, then custom splitting is disabled.
	 * 
	 * @param splitter
	 *            the splitter to use
	 * @return this instance for fluent chaining
	 */
	public MessageSplitterBuilder customSplitter(MessageSplitter splitter) {
		this.customSplitter = splitter;
		return this;
	}

	/**
	 * Register a custom splitter strategy in the chain of splitters.
	 * 
	 * <p>
	 * It is possible to register several custom splitters.
	 * 
	 * <p>
	 * The priority is used to indicate in which order the custom splitter must
	 * be applied in the chain.
	 * 
	 * <p>
	 * If the custom splitter implements {@link SupportingSplitter}, then the
	 * splitter can indicate if it is able to handle to message to split. If the
	 * splitter can't handle the message, then the next splitter is tried.
	 * 
	 * <p>
	 * If the custom splitter doesn't implement {@link SupportingSplitter}, then
	 * the splitter is considered as able to handle the message. Splitting is
	 * used with this splitter. So if such splitter is registered with a higher
	 * priority than others, no other splitter will be tried.
	 * 
	 * @param splitter
	 *            the splitter to register
	 * @param priority
	 *            the associated priority (greater value means higher priority)
	 * @return this instance for fluent chaining
	 */
	public MessageSplitterBuilder customSplitter(MessageSplitter splitter, int priority) {
		customSplitters.register(splitter, priority);
		return this;
	}

	@Override
	public MessageSplitter build() {
		if (customSplitter != null) {
			return customSplitter;
		}
		PropertyResolver propertyResolver = environmentBuilder.build();
		if (!splittingEnabled(propertyResolver)) {
			return null;
		}
		if (encoderBuilder.autoGuessEnabled(propertyResolver)) {
			return buildAutoGuessSplitter(propertyResolver);
		}
		if (!customSplitters.isEmpty()) {
			return new FirstSupportingMessageSplitter(customSplitters.getOrdered());
		}
		return null;
	}

	private boolean splittingEnabled(PropertyResolver propertyResolver) {
		Boolean enableValue = BuilderUtils.evaluate(enableProps, propertyResolver, Boolean.class);
		return enableValue != null && enableValue;
	}

	private MessageSplitter buildAutoGuessSplitter(PropertyResolver propertyResolver) {
		PriorizedList<MessageSplitter> registry = new PriorizedList<>();
		registerStandardSplitter(propertyResolver, encoderBuilder.getGsm7Priorities(), NAME_GSM7, SEGMENT_SIZE_GSM_7BIT_SMS_PACKING_MODE, registry);
		registerStandardSplitter(propertyResolver, encoderBuilder.getGsm8Priorities(), NAME_GSM8, SEGMENT_SIZE_GSM_8BIT, registry);
		registerStandardSplitter(propertyResolver, encoderBuilder.getLatin1Priorities(), NAME_ISO_8859_1, SEGMENT_SIZE_GSM_8BIT, registry);
		registerStandardSplitter(propertyResolver, encoderBuilder.getUcs2Priorities(), NAME_UCS_2, SEGMENT_SIZE_UCS2, registry);
		registry.register(customSplitters);
		return new FirstSupportingMessageSplitter(registry.getOrdered());
	}

	private void registerStandardSplitter(PropertyResolver propertyResolver, List<String> priorities, String supportedCharsetName, SegmentSizes maxSizes, PriorizedList<MessageSplitter> registry) {
		Integer priority = BuilderUtils.evaluate(priorities, propertyResolver, Integer.class);
		if (priority == null || priority <= 0) {
			return;
		}
		registry.register(buildStandardSplitter(supportedCharsetName, maxSizes), priority);
	}

	private MessageSplitter buildStandardSplitter(String supportingCharset, SegmentSizes maxSizes) {
		SupportingEncoder encoder = new CloudhopperCharsetSupportingEncoder(NamedCharset.from(supportingCharset));
		return new SupportedEncoderConditionalSplitter(encoder, new GsmMessageSplitter(encoder, maxSizes, new RandomReferenceNumberGenerator()));
	}

}

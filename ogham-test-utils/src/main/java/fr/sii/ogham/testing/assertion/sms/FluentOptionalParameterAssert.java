package fr.sii.ogham.testing.assertion.sms;

import static fr.sii.ogham.testing.assertion.util.AssertionHelper.assertThat;
import static fr.sii.ogham.testing.assertion.util.AssertionHelper.usingContext;
import static org.apache.commons.lang3.ArrayUtils.toObject;

import java.util.List;

import org.hamcrest.Matcher;

import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.sms.simulator.bean.OptionalParameter;
import fr.sii.ogham.testing.sms.simulator.bean.SubmitSm;
import fr.sii.ogham.testing.util.HasParent;

/**
 * Make assertions on optional parameters of a {@link SubmitSm}.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            The parent type
 */
public class FluentOptionalParameterAssert<P> extends HasParent<P> {
	private final List<OptionalParameterWithContext> actual;
	private final AssertionRegistry registry;

	/**
	 * Initializes with the parent instance and optional parameters for each
	 * {@link SubmitSm} message.
	 * 
	 * @param parent
	 *            the parent instance
	 * @param parameters
	 *            the optional parameters (with some contextual information)
	 * @param registry
	 *            used to regsiter assertions
	 */
	public FluentOptionalParameterAssert(P parent, List<OptionalParameterWithContext> parameters, AssertionRegistry registry) {
		super(parent);
		this.actual = parameters;
		this.registry = registry;
	}

	/**
	 * Make assertions on the whole optional parameter.
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *   .rawRequest()
	 *     .optionalParameter(Tag.MESSAGE_PAYLOAD)
	 *    	 .parameter(notNullValue())
	 * </pre>
	 * 
	 * Will check if the 'message_payload' optional parameter of the first
	 * message is not null.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *   .rawRequest()
	 *     .optionalParameter(Tag.MESSAGE_PAYLOAD)
	 *    	 .parameter(notNullValue())
	 * </pre>
	 * 
	 * Will check if the 'message_payload' optional parameter of every message
	 * is not null.
	 * 
	 * @param matcher
	 *            the assertion to apply on the optional parameter
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public FluentOptionalParameterAssert<P> parameter(Matcher<? super OptionalParameter> matcher) {
		String message = "optional parameter '${tagName}'${found} of ${name} of message ${messageIndex}";
		for (OptionalParameterWithContext parameterWithContext : actual) {
			OptionalParameter parameter = parameterWithContext.getParameter();
			registry.register(() -> assertThat(parameter, usingContext(message, parameterWithContext, matcher)));
		}
		return this;
	}

	/**
	 * Make assertions on the optional parameter value.
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *   .rawRequest()
	 *     .optionalParameter(Tag.MESSAGE_PAYLOAD)
	 *    	 .value(arrayWithSize(134))
	 * </pre>
	 * 
	 * Will check if the value of the 'message_payload' optional parameter of
	 * the first message is an array of 134 bytes.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *   .rawRequest()
	 *     .optionalParameter(Tag.MESSAGE_PAYLOAD)
	 *    	 .value(arrayWithSize(134))
	 * </pre>
	 * 
	 * Will check if the 'message_payload' optional parameter of every message
	 * is an array of 134 bytes.
	 * 
	 * @param matcher
	 *            the assertion to apply on the optional parameter value
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public FluentOptionalParameterAssert<P> value(Matcher<? super Byte[]> matcher) {
		String message = "optional parameter '${tagName}'${found} value of ${name} of message ${messageIndex}";
		for (OptionalParameterWithContext parameterWithContext : actual) {
			OptionalParameter parameter = parameterWithContext.getParameter();
			registry.register(() -> assertThat(toObject(parameter.getValue()), usingContext(message, parameterWithContext, matcher)));
		}
		return this;
	}

	/**
	 * Make assertions on the optional parameter value.
	 * 
	 * <pre>
	 * .receivedMessages().message(0)
	 *   .rawRequest()
	 *     .optionalParameter(Tag.MESSAGE_PAYLOAD)
	 *    	 .value(is(134))
	 * </pre>
	 * 
	 * Will check if the length of the 'message_payload' optional parameter of
	 * the first message is 134.
	 * 
	 * <pre>
	 * .receivedMessages().every()
	 *   .rawRequest()
	 *     .optionalParameter(Tag.MESSAGE_PAYLOAD)
	 *    	 .value(is(134))
	 * </pre>
	 * 
	 * Will check if the length of the 'message_payload' optional parameter of
	 * every message is an array of 134 bytes.
	 * 
	 * @param matcher
	 *            the assertion to apply on the optional parameter value
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public FluentOptionalParameterAssert<P> length(Matcher<? super Integer> matcher) {
		String message = "optional parameter '${tagName}'${found} length of ${name} of message ${messageIndex}";
		for (OptionalParameterWithContext parameterWithContext : actual) {
			OptionalParameter parameter = parameterWithContext.getParameter();
			registry.register(() -> assertThat(parameter.getLength(), usingContext(message, parameterWithContext, matcher)));
		}
		return this;
	}

}

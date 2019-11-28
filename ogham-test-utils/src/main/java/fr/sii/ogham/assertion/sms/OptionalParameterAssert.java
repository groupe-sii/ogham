package fr.sii.ogham.assertion.sms;

import static fr.sii.ogham.assertion.AssertionHelper.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.usingContext;
import static org.apache.commons.lang3.ArrayUtils.toObject;

import java.util.List;

import org.hamcrest.Matcher;

import fr.sii.ogham.assertion.HasParent;
import fr.sii.ogham.helper.sms.bean.OptionalParameter;
import fr.sii.ogham.helper.sms.bean.SubmitSm;

/**
 * Make assertions on optional parameters of a {@link SubmitSm}.
 * 
 * @author Aurélien Baudet
 *
 * @param <P>
 *            The parent type
 */
public class OptionalParameterAssert<P> extends HasParent<P> {
	private List<OptionalParameterWithContext> actual;

	/**
	 * Initializes with the parent instance and optional parameters for each
	 * {@link SubmitSm} message.
	 * 
	 * @param parent
	 *            the parent instance
	 * @param parameters
	 *            the optional parameters (with some contextual information)
	 */
	public OptionalParameterAssert(P parent, List<OptionalParameterWithContext> parameters) {
		super(parent);
		this.actual = parameters;
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
	public OptionalParameterAssert<P> parameter(Matcher<? super OptionalParameter> matcher) {
		String message = "optional parameter ${tagName} of ${name} of message ${messageIndex}";
		for (OptionalParameterWithContext parameterWithContext : actual) {
			OptionalParameter parameter = parameterWithContext.getParameter();
			assertThat(parameter, usingContext(message, parameterWithContext, matcher));
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
	public OptionalParameterAssert<P> value(Matcher<? super Byte[]> matcher) {
		String message = "optional parameter ${tagName} value of ${name} of message ${messageIndex}";
		for (OptionalParameterWithContext parameterWithContext : actual) {
			OptionalParameter parameter = parameterWithContext.getParameter();
			assertThat(toObject(parameter.getValue()), usingContext(message, parameterWithContext, matcher));
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
	public OptionalParameterAssert<P> length(Matcher<? super Integer> matcher) {
		String message = "optional parameter ${tagName} length of ${name} of message ${messageIndex}";
		for (OptionalParameterWithContext parameterWithContext : actual) {
			OptionalParameter parameter = parameterWithContext.getParameter();
			assertThat(parameter.getLength(), usingContext(message, parameterWithContext, matcher));
		}
		return this;
	}

}

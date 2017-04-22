package fr.sii.ogham.assertion.email;

import static fr.sii.ogham.assertion.AssertionHelper.assertThat;
import static fr.sii.ogham.assertion.OghamAssertions.usingContext;
import static fr.sii.ogham.helper.email.EmailUtils.getContent;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;

import com.google.common.base.Charsets;

import fr.sii.ogham.assertion.HasParent;

public class PartAssert<P> extends HasParent<P> {
	/**
	 * The list of messages that will be used for assertions
	 */
	private final List<PartWithContext> actual;

	public PartAssert(PartWithContext actual, P parent) {
		this(Arrays.asList(actual), parent);
	}

	public PartAssert(List<PartWithContext> actual, P parent) {
		super(parent);
		this.actual = actual;
	}

	/**
	 * Make assertions on the string content of a part (body, alternative or
	 * attachment) of the message(s). UTF-8 charset is used to decode body
	 * content.
	 * 
	 * <pre>
	 * .receivedMessages().message(0).body()
	 *    .contentAsString(is("foobar"))
	 * </pre>
	 * 
	 * Will check if the content of the body of the first message is exactly
	 * "foobar".
	 * 
	 * <pre>
	 * .receivedMessages().forEach().body()
	 *    .contentAsString(is("foobar"))
	 * </pre>
	 * 
	 * Will check if the content of the body of every message is exactly
	 * "foobar".
	 * 
	 * @param matcher
	 *            the assertion to apply on string content
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<P> contentAsString(Matcher<String> matcher) {
		return contentAsString(matcher, Charsets.UTF_8);
	}

	/**
	 * Make assertions on the string content of a part (body, alternative or
	 * attachment) of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0).body()
	 *    .contentAsString(is("foobar"), Charset.forName("UTF-8"))
	 * </pre>
	 * 
	 * Will check if the content of the body of the first message is exactly
	 * "foobar".
	 * 
	 * <pre>
	 * .receivedMessages().forEach().body()
	 *    .contentAsString(is("foobar"), Charset.forName("UTF-8"))
	 * </pre>
	 * 
	 * Will check if the content of the body of every message is exactly
	 * "foobar".
	 * 
	 * @param matcher
	 *            the assertion to apply on string content
	 * @param charset
	 *            the charset used to decode the content
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<P> contentAsString(Matcher<String> matcher, Charset charset) {
		try {
			String message = charset.name() + " content of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				assertThat(part == null ? null : IOUtils.toString(getContent(part), charset.name()), usingContext(message, partWithContext, matcher));
			}
			return this;
		} catch (IOException | MessagingException e) {
			throw new AssertionError("Failed to get string content for part", e);
		}
	}

	/**
	 * Make assertions on the raw content of a part (body, alternative or
	 * attachment) of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0).body()
	 *    .content(is(resource("path/to/expected/file"))
	 * </pre>
	 * 
	 * Will check if the content of the body of the first message is exactly the
	 * same as the file resource available in the classpath.
	 * 
	 * <pre>
	 * .receivedMessages().forEach().body()
	 *    .content(is(resource("path/to/expected/file"))
	 * </pre>
	 * 
	 * Will check if the content of the body of every message is exactly the
	 * same as the file resource available in the classpath.
	 * 
	 * @param matcher
	 *            the assertion to apply on raw content
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<P> content(Matcher<byte[]> matcher) {
		try {
			String message = "raw content of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				assertThat(part == null ? null : getContent(part), usingContext(message, partWithContext, matcher));
			}
			return this;
		} catch (IOException | MessagingException e) {
			throw new AssertionError("Failed to get content for part", e);
		}
	}

	/**
	 * Make assertions on the content-type of a part (body, alternative or
	 * attachment) of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0).body()
	 *    .contentType(is("text/html"))
	 * </pre>
	 * 
	 * Will check if the content-type of the body of the first message is
	 * exactly "text/html".
	 * 
	 * <pre>
	 * .receivedMessages().forEach().body()
	 *    .contentType(is("text/html"))
	 * </pre>
	 * 
	 * Will check if the content-type of the body of every message is exactly
	 * "text/html".
	 * 
	 * @param matcher
	 *            the assertion to apply on content-type
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<P> contentType(Matcher<String> matcher) {
		try {
			String message = "content-type of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				assertThat(part == null ? null : part.getContentType(), usingContext(message, partWithContext, matcher));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get string content type for part", e);
		}
	}

	/**
	 * Make assertions on the description of a part (body, alternative or
	 * attachment) of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0).body()
	 *    .description(is("foo bar"))
	 * </pre>
	 * 
	 * Will check if the description of the body of the first message is exactly
	 * "foo bar".
	 * 
	 * <pre>
	 * .receivedMessages().forEach().body()
	 *    .description(is("foo bar"))
	 * </pre>
	 * 
	 * Will check if the description of the body of every message is exactly
	 * "foo bar".
	 * 
	 * @param matcher
	 *            the assertion to apply on description
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<P> description(Matcher<String> matcher) {
		try {
			String message = "description of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				assertThat(part == null ? null : part.getDescription(), usingContext(message, partWithContext, matcher));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get description of part", e);
		}
	}

	/**
	 * Make assertions on the disposition of a part (body, alternative or
	 * attachment) of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0).body()
	 *    .disposition(is(INLINE_DISPOSITION))
	 * </pre>
	 * 
	 * Will check if the disposition of the body of the first message is exactly
	 * "inline".
	 * 
	 * <pre>
	 * .receivedMessages().forEach().body()
	 *    .disposition(is(INLINE_DISPOSITION))
	 * </pre>
	 * 
	 * Will check if the disposition of the body of every message is exactly
	 * "inline".
	 * 
	 * @param matcher
	 *            the assertion to apply on disposition
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<P> disposition(Matcher<String> matcher) {
		try {
			String message = "disposition of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				assertThat(part == null ? null : part.getDisposition(), usingContext(message, partWithContext, matcher));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get disposition of part", e);
		}
	}

	/**
	 * Make assertions on the filename of a part (body, alternative or
	 * attachment) of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0).attachment(0)
	 *    .filename(is("foo.pdf"))
	 * </pre>
	 * 
	 * Will check if the filename of the first attachment of the first message
	 * is exactly "foo.pdf".
	 * 
	 * <pre>
	 * .receivedMessages().forEach().attachment(0)
	 *    .filename(is("foo.pdf"))
	 * </pre>
	 * 
	 * Will check if the filename of the first attachment of every message is
	 * exactly "foo.pdf".
	 * 
	 * @param matcher
	 *            the assertion to apply on filename
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public PartAssert<P> filename(Matcher<String> matcher) {
		try {
			String message = "filename of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				assertThat(part == null ? null : part.getFileName(), usingContext(message, partWithContext, matcher));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get filename of part", e);
		}
	}

	@SuppressWarnings("unchecked")
	public PartAssert<P> headers(Matcher<Iterable<? extends Header>> matcher) {
		try {
			String message = "headers of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				assertThat(part == null ? null : Collections.<Header> list(part.getAllHeaders()), usingContext(message, partWithContext, matcher));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get filename of part", e);
		}
	}
}

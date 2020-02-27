package fr.sii.ogham.testing.assertion.email;

import static fr.sii.ogham.testing.assertion.util.AssertionHelper.assertThat;
import static fr.sii.ogham.testing.assertion.util.AssertionHelper.usingContext;
import static fr.sii.ogham.testing.assertion.util.EmailUtils.getContent;
import static java.util.Arrays.asList;
import static java.util.Collections.list;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Part;

import org.hamcrest.Matcher;

import fr.sii.ogham.testing.assertion.util.AssertionRegistry;
import fr.sii.ogham.testing.util.HasParent;

public class FluentPartAssert<P> extends HasParent<P> {
	/**
	 * The list of messages that will be used for assertions
	 */
	private final List<PartWithContext> actual;
	/**
	 * Registry to register assertions
	 */
	private final AssertionRegistry registry;

	public FluentPartAssert(PartWithContext actual, P parent, AssertionRegistry registry) {
		this(Arrays.asList(actual), parent, registry);
	}

	public FluentPartAssert(List<PartWithContext> actual, P parent, AssertionRegistry registry) {
		super(parent);
		this.actual = actual;
		this.registry = registry;
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
	 * .receivedMessages().every().body()
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
	public FluentPartAssert<P> contentAsString(Matcher<? super String> matcher) {
		return contentAsString(matcher, StandardCharsets.UTF_8);
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
	 * .receivedMessages().every().body()
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
	public FluentPartAssert<P> contentAsString(Matcher<? super String> matcher, Charset charset) {
		try {
			String message = charset.name() + " content of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				registry.register(() -> assertThat(part == null ? null : getContent(part, charset), usingContext(message, partWithContext, matcher)));
			}
			return this;
		} catch (Exception e) {
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
	 * .receivedMessages().every().body()
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
	public FluentPartAssert<P> content(Matcher<byte[]> matcher) {
		try {
			String message = "raw content of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				registry.register(() -> assertThat(part == null ? null : getContent(part), usingContext(message, partWithContext, matcher)));
			}
			return this;
		} catch (Exception e) {
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
	 * .receivedMessages().every().body()
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
	public FluentPartAssert<P> contentType(Matcher<? super String> matcher) {
		try {
			String message = "content-type of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				registry.register(() -> assertThat(part == null ? null : part.getContentType(), usingContext(message, partWithContext, matcher)));
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
	 * .receivedMessages().every().body()
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
	public FluentPartAssert<P> description(Matcher<? super String> matcher) {
		try {
			String message = "description of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				registry.register(() -> assertThat(part == null ? null : part.getDescription(), usingContext(message, partWithContext, matcher)));
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
	 * .receivedMessages().every().body()
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
	public FluentPartAssert<P> disposition(Matcher<? super String> matcher) {
		try {
			String message = "disposition of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				registry.register(() -> assertThat(part == null ? null : part.getDisposition(), usingContext(message, partWithContext, matcher)));
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
	 * .receivedMessages().every().attachment(0)
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
	public FluentPartAssert<P> filename(Matcher<? super String> matcher) {
		try {
			String message = "filename of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				registry.register(() -> assertThat(part == null ? null : part.getFileName(), usingContext(message, partWithContext, matcher)));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get filename of part", e);
		}
	}

	/**
	 * Make assertions on the headers of a part (body, alternative or
	 * attachment) of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0).attachment(0)
	 *    .headers(hasItem(new Header("name", "value")))
	 * </pre>
	 * 
	 * Will check if the headers of the first attachment of the first message
	 * contains a header with name "name" and value "value".
	 * 
	 * <pre>
	 * .receivedMessages().every().attachment(0)
	 *    .header(hasItem(new Header("name", "value")))
	 * </pre>
	 * 
	 * Will check if the headers of the first attachment of every message is
	 * contains a header with name "name" and value "value".
	 * 
	 * @param matcher
	 *            the assertion to apply on headers
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public FluentPartAssert<P> headers(Matcher<? super Iterable<Header>> matcher) {
		try {
			String message = "headers of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				registry.register(() -> assertThat(part == null ? null : list(part.getAllHeaders()), usingContext(message, partWithContext, matcher)));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get headers of part", e);
		}
	}

	/**
	 * Make assertions on a single header of a part (body, alternative or
	 * attachment) of the message(s).
	 * 
	 * <pre>
	 * .receivedMessages().message(0).attachment(0)
	 *    .header("Content-ID", contains("foo"))
	 * </pre>
	 * 
	 * Will check if the "Content-ID" header of the first attachment of the
	 * first message values "foo".
	 * 
	 * <pre>
	 * .receivedMessages().every().attachment(0)
	 *    .header("Content-ID", contains("foo"))
	 * </pre>
	 * 
	 * Will check if the "Content-ID" header of the first attachment of every
	 * message values "foo".
	 * 
	 * @param headerName
	 *            the name of the header to check
	 * @param matcher
	 *            the assertion to apply on the header header
	 * @return the fluent API for chaining assertions on received message(s)
	 */
	public FluentPartAssert<P> header(String headerName, Matcher<? super Iterable<String>> matcher) {
		try {
			String message = "header " + headerName + " of ${partName} of message ${messageIndex}";
			for (PartWithContext partWithContext : actual) {
				Part part = partWithContext.getPart();
				registry.register(() -> assertThat(getHeaderValues(part, headerName), usingContext(message, partWithContext, matcher)));
			}
			return this;
		} catch (MessagingException e) {
			throw new AssertionError("Failed to get header" + headerName + " of part", e);
		}
	}
	
	@SuppressWarnings("squid:S1168")
	private static List<String> getHeaderValues(Part part, String headerName) throws MessagingException {
		if (part != null) {
			String[] vals = part.getHeader(headerName);
			if (vals != null) {
				return asList(vals);
			}
		}
		return null;
	}
}

package fr.sii.ogham.testing.helper.email;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Class used in tests for ensuring that the content of an email is respected.
 * It contains the expected content and the expected Mime Type.
 * 
 * @author Aurélien Baudet
 *
 */
public class ExpectedContent {
	/**
	 * The expected body content as string
	 */
	private String body;

	/**
	 * The expected Mime Type (regular expression)
	 */
	private Pattern mimetype;

	/**
	 * Initialize with the expected body and the expected Mime Type (regular
	 * expression).
	 * 
	 * @param body
	 *            the expected body
	 * @param mimetype
	 *            the expected Mime Type pattern
	 */
	public ExpectedContent(String body, String mimetype) {
		this(body, Pattern.compile(mimetype));
	}

	/**
	 * Initialize with the expected body and the expected Mime Type pattern.
	 * 
	 * @param body
	 *            the expected body
	 * @param mimetype
	 *            the expected Mime Type pattern
	 */
	public ExpectedContent(String body, Pattern mimetype) {
		super();
		this.body = body;
		this.mimetype = mimetype;
	}

	/**
	 * Initialize with the expected body and the expected Mime Type pattern.
	 * 
	 * @param body
	 *            the expected body to read from the stream
	 * @param mimetype
	 *            the expected Mime Type pattern
	 * @throws IOException
	 *             when the expected content stream is not readable
	 */
	public ExpectedContent(InputStream body, String mimetype) throws IOException {
		this(IOUtils.toString(body, Charset.defaultCharset()), mimetype);
	}

	public String getBody() {
		return body;
	}

	public Pattern getMimetype() {
		return mimetype;
	}

}

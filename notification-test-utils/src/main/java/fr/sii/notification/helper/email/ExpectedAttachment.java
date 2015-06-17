package fr.sii.notification.helper.email;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Class used in tests for ensuring that the attachment is respected. It
 * provides the following information:
 * <ul>
 * <li>The expected name of the attachment</li>
 * <li>The expected Mime type of the attachment</li>
 * <li>The expected content of the attachment</li>
 * <li>The expected description of the attachment</li>
 * <li>The expected disposition of the attachment</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 */
public class ExpectedAttachment {
	/**
	 * The name of the attachment
	 */
	private String name;

	/**
	 * The mimetype pattern for the attachment
	 */
	private Pattern mimetype;

	/**
	 * The description of the attachment
	 */
	private String description;

	/**
	 * The disposition of the attachment
	 */
	private String disposition;

	/**
	 * The content of the attachment
	 */
	private byte[] content;

	public ExpectedAttachment(String name, Pattern mimetype, byte[] content, String description, String disposition) {
		super();
		this.name = name;
		this.mimetype = mimetype;
		this.description = description;
		this.disposition = disposition;
		this.content = content;
	}

	public ExpectedAttachment(String name, Pattern mimetype, byte[] content, String description) {
		this(name, mimetype, content, description, "attachment");
	}

	public ExpectedAttachment(String name, Pattern mimetype, byte[] content) {
		this(name, mimetype, content, null);
	}

	public ExpectedAttachment(String name, Pattern mimetype, InputStream content, String description, String disposition) throws IOException {
		this(name, mimetype, IOUtils.toByteArray(content), description, disposition);
	}

	public ExpectedAttachment(String name, Pattern mimetype, InputStream content, String description) throws IOException {
		this(name, mimetype, content, description, "attachment");
	}

	public ExpectedAttachment(String name, Pattern mimetype, InputStream content) throws IOException {
		this(name, mimetype, content, null);
	}

	public ExpectedAttachment(String expectedContentPath, Pattern mimetype, String description, String disposition) throws IOException {
		this(new File(expectedContentPath).getName(), mimetype, ExpectedAttachment.class.getResourceAsStream(expectedContentPath), description, disposition);
	}

	public ExpectedAttachment(String expectedContentPath, Pattern mimetype, String description) throws IOException {
		this(expectedContentPath, mimetype, description, "attachment");
	}

	public ExpectedAttachment(String expectedContentPath, Pattern mimetype) throws IOException {
		this(expectedContentPath, mimetype, (String) null);
	}

	public ExpectedAttachment(String name, String mimetype, byte[] content, String description, String disposition) {
		this(name, Pattern.compile(mimetype), content, description, disposition);
	}

	public ExpectedAttachment(String name, String mimetype, byte[] content, String description) {
		this(name, mimetype, content, description, "attachment");
	}

	public ExpectedAttachment(String name, String mimetype, byte[] content) {
		this(name, mimetype, content, null);
	}

	public ExpectedAttachment(String name, String mimetype, InputStream content, String description, String disposition) throws IOException {
		this(name, mimetype, IOUtils.toByteArray(content), description, disposition);
	}

	public ExpectedAttachment(String name, String mimetype, InputStream content, String description) throws IOException {
		this(name, mimetype, content, description, "attachment");
	}

	public ExpectedAttachment(String name, String mimetype, InputStream content) throws IOException {
		this(name, mimetype, content, null);
	}

	public ExpectedAttachment(String expectedContentPath, String mimetype, String description, String disposition) throws IOException {
		this(new File(expectedContentPath).getName(), mimetype, ExpectedAttachment.class.getResourceAsStream(expectedContentPath), description, disposition);
	}

	public ExpectedAttachment(String expectedContentPath, String mimetype, String description) throws IOException {
		this(expectedContentPath, mimetype, description, "attachment");
	}

	public ExpectedAttachment(String expectedContentPath, String mimetype) throws IOException {
		this(expectedContentPath, mimetype, (String) null);
	}

	public String getName() {
		return name;
	}

	public Pattern getMimetype() {
		return mimetype;
	}

	public String getDescription() {
		return description;
	}

	public String getDisposition() {
		return disposition;
	}

	public byte[] getContent() {
		return content;
	}

}

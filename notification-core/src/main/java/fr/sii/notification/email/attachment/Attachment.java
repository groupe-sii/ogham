package fr.sii.notification.email.attachment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a content to attach to the email. Typically a file to join to the
 * email. An attachment is represented with the following information:
 * <ul>
 * <li>A content (the file to join to the email). See {@link Source} for more
 * information.</li>
 * <li>A name (the name to display for the file). See {@link Source} for more
 * information.</li>
 * <li>An optional description</li>
 * <li>An optional disposition ({@link ContentDisposition#ATTACHMENT} by
 * default)</li>
 * </ul>
 * 
 * @author Aurélien Baudet
 *
 */
public class Attachment {
	/**
	 * The source used to access the content of the attachment
	 */
	private Source source;

	/**
	 * The description for the attachment
	 */
	private String description;

	/**
	 * How to attach the attachment to the email
	 */
	private String disposition;

	/**
	 * Initialize the attachment with the provided source (content and name),
	 * the description of the attachment and the disposition (how to include the
	 * attachment into the mail).
	 * 
	 * @param source
	 *            the source of the attachment
	 * @param description
	 *            the description of the attachment (may be null)
	 * @param disposition
	 *            the disposition of the attachment
	 */
	public Attachment(Source source, String description, String disposition) {
		super();
		this.source = source;
		this.description = description;
		this.disposition = disposition;
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided source (content and name),
	 * the description of the attachment.
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param source
	 *            the source of the attachment
	 * @param description
	 *            the description of the attachment (may be null)
	 */
	public Attachment(Source source, String description) {
		this(source, description, ContentDisposition.ATTACHMENT);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided source (content and name).
	 * </p>
	 * <p>
	 * The description is not used (set to null)
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param source
	 *            the source of the attachment
	 */
	public Attachment(Source source) {
		this(source, null);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided path, description of the
	 * attachment and disposition (how to include the attachment into the mail).
	 * </p>
	 * <p>
	 * The path may contain a lookup prefix. The lookup prefix is case sensitive
	 * and must end with a ':'. It must not contain another ':' character.
	 * </p>
	 * <p>
	 * For example, a path could be "classpath:/email/hello.pdf". The lookup
	 * prefix is "classpath:".
	 * </p>
	 * 
	 * @param path
	 *            the path to the attachment
	 * @param description
	 *            the description of the attachment (may be null)
	 * @param disposition
	 *            the disposition of the attachment
	 */
	public Attachment(String path, String description, String disposition) {
		this(new LookupSource(path), description, disposition);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided path and description of the
	 * attachment.
	 * </p>
	 * <p>
	 * The path may contain a lookup prefix. The lookup prefix is case sensitive
	 * and must end with a ':'. It must not contain another ':' character.
	 * </p>
	 * <p>
	 * For example, a path could be "classpath:/email/hello.pdf". The lookup
	 * prefix is "classpath:".
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param path
	 *            the path to the attachment
	 * @param description
	 *            the description of the attachment (may be null)
	 */
	public Attachment(String path, String description) {
		this(new LookupSource(path), description);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided path.
	 * </p>
	 * <p>
	 * The path may contain a lookup prefix. The lookup prefix is case sensitive
	 * and must end with a ':'. It must not contain another ':' character.
	 * </p>
	 * <p>
	 * For example, a path could be "classpath:/email/hello.pdf". The lookup
	 * prefix is "classpath:".
	 * </p>
	 * <p>
	 * The description is not used (set to null)
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param path
	 *            the path to the attachment
	 */
	public Attachment(String path) {
		this(new LookupSource(path));
	}

	/**
	 * Initialize the attachment with the provided content, name, description of
	 * the attachment and disposition (how to include the attachment into the
	 * mail).
	 * 
	 * @param name
	 *            the name of the attachment
	 * @param stream
	 *            the content of the attachment accessible trough the stream
	 * @param description
	 *            the description of the attachment (may be null)
	 * @param disposition
	 *            the disposition of the attachment
	 * @throws IOException
	 *             when the content of the stream can't be read
	 */
	public Attachment(String name, InputStream stream, String description, String disposition) throws IOException {
		this(new ByteSource(name, stream), description, disposition);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided content, name and description
	 * of the attachment.
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param name
	 *            the name of the attachment
	 * @param stream
	 *            the content of the attachment accessible trough the stream
	 * @param description
	 *            the description of the attachment (may be null)
	 * @throws IOException
	 *             when the content of the stream can't be read
	 */
	public Attachment(String name, InputStream stream, String description) throws IOException {
		this(new ByteSource(name, stream), description);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided content and name.
	 * </p>
	 * <p>
	 * The description is not used (set to null)
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param name
	 *            the name of the attachment
	 * @param stream
	 *            the content of the attachment accessible trough the stream
	 * @throws IOException
	 *             when the content of the stream can't be read
	 */
	public Attachment(String name, InputStream stream) throws IOException {
		this(new ByteSource(name, stream));
	}

	/**
	 * Initialize the attachment with the provided content, name, description of
	 * the attachment and disposition (how to include the attachment into the
	 * mail).
	 * 
	 * @param name
	 *            the name of the attachment
	 * @param content
	 *            the content of the attachment
	 * @param description
	 *            the description of the attachment (may be null)
	 * @param disposition
	 *            the disposition of the attachment
	 */
	public Attachment(String name, byte[] content, String description, String disposition) {
		this(new ByteSource(name, content), description, disposition);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided content, name and description
	 * of the attachment.
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param name
	 *            the name of the attachment
	 * @param content
	 *            the content of the attachment
	 * @param description
	 *            the description of the attachment (may be null)
	 */
	public Attachment(String name, byte[] content, String description) {
		this(new ByteSource(name, content), description);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided content and name.
	 * </p>
	 * <p>
	 * The description is not used (set to null)
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param name
	 *            the name of the attachment
	 * @param content
	 *            the content of the attachment
	 */
	public Attachment(String name, byte[] content) {
		this(new ByteSource(name, content));
	}

	/**
	 * Initialize the attachment with the provided file, description of the
	 * attachment and disposition (how to include the attachment into the mail).
	 * <p>
	 * The name of the attachment is the name of the file.
	 * </p>
	 * 
	 * @param file
	 *            the file to attach
	 * @param description
	 *            the description of the attachment (may be null)
	 * @param disposition
	 *            the disposition of the attachment
	 */
	public Attachment(File file, String description, String disposition) {
		this(new FileSource(file), description, disposition);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided file and description of the
	 * attachment.
	 * </p>
	 * <p>
	 * The name of the attachment is the name of the file.
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param file
	 *            the file to attach
	 * @param description
	 *            the description of the attachment (may be null)
	 */
	public Attachment(File file, String description) {
		this(new FileSource(file), description);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided file.
	 * </p>
	 * <p>
	 * The name of the attachment is the name of the file.
	 * </p>
	 * <p>
	 * The description is not used (set to null)
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param file
	 *            the file to attach
	 */
	public Attachment(File file) {
		this(new FileSource(file));
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("<").append(source.getName());
		if (description != null) {
			builder.append(" )").append(description).append(")");
		}
		builder.append("[").append(disposition).append("]>");
		return builder.toString();
	}
}

package fr.sii.ogham.email.attachment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import fr.sii.ogham.core.resource.ByteResource;
import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.resource.LookupResource;
import fr.sii.ogham.core.resource.NamedResource;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Represents a content to attach to the email. Typically a file to join to the
 * email. An attachment is represented with the following information:
 * <ul>
 * <li>A content (the file to join to the email). See {@link NamedResource} for
 * more information.</li>
 * <li>A name (the name to display for the file). See {@link NamedResource} for
 * more information.</li>
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
	 * The resource used to access the content of the attachment
	 */
	private NamedResource resource;

	/**
	 * The description for the attachment
	 */
	private String description;

	/**
	 * How to attach the attachment to the email
	 */
	private String disposition;

	/**
	 * The ID of the content
	 */
	private String contentId;

	/**
	 * Initialize the attachment with the provided resource (content and name),
	 * the description of the attachment and the disposition (how to include the
	 * attachment into the mail).
	 * 
	 * @param resource
	 *            the resource of the attachment (with name)
	 * @param description
	 *            the description of the attachment (may be null)
	 * @param disposition
	 *            the disposition of the attachment
	 * @param contentId
	 *            the unique id of the content (may not null)
	 */
	public Attachment(NamedResource resource, String description, String disposition, String contentId) {
		super();
		this.resource = resource;
		this.description = description;
		this.disposition = disposition;
		this.contentId = contentId;
	}

	/**
	 * Initialize the attachment with the provided resource (content and name),
	 * the description of the attachment and the disposition (how to include the
	 * attachment into the mail).
	 * 
	 * @param resource
	 *            the resource of the attachment (with name)
	 * @param description
	 *            the description of the attachment (may be null)
	 * @param disposition
	 *            the disposition of the attachment
	 */
	public Attachment(NamedResource resource, String description, String disposition) {
		this(resource, description, disposition, null);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided resource (content and name),
	 * the description of the attachment.
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param resource
	 *            the resource of the attachment
	 * @param description
	 *            the description of the attachment (may be null)
	 */
	public Attachment(NamedResource resource, String description) {
		this(resource, description, ContentDisposition.ATTACHMENT);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided resource (content and name).
	 * </p>
	 * <p>
	 * The description is not used (set to null)
	 * </p>
	 * <p>
	 * The disposition is set to {@link ContentDisposition#ATTACHMENT}
	 * </p>
	 * 
	 * @param resource
	 *            the resource of the attachment
	 */
	public Attachment(NamedResource resource) {
		this(resource, null);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided path, description of the
	 * attachment and disposition (how to include the attachment into the mail).
	 * </p>
	 * <p>
	 * The path may contain a lookup. The lookup is case sensitive and must end
	 * with a ':'. It must not contain another ':' character.
	 * </p>
	 * <p>
	 * For example, a path could be "classpath:/email/hello.pdf". The lookup is
	 * "classpath:".
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
		this(new UnresolvedPath(path), description, disposition);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided path and description of the
	 * attachment.
	 * </p>
	 * <p>
	 * The path may contain a lookup. The lookup is case sensitive and must end
	 * with a ':'. It must not contain another ':' character.
	 * </p>
	 * <p>
	 * For example, a path could be "classpath:/email/hello.pdf". The lookup is
	 * "classpath:".
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
		this(new UnresolvedPath(path), description);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided path.
	 * </p>
	 * <p>
	 * The path may contain a lookup. The lookup is case sensitive and must end
	 * with a ':'. It must not contain another ':' character.
	 * </p>
	 * <p>
	 * For example, a path could be "classpath:/email/hello.pdf". The lookup is
	 * "classpath:".
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
		this(new UnresolvedPath(path));
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided path, description of the
	 * attachment and disposition (how to include the attachment into the mail).
	 * </p>
	 * <p>
	 * The path may contain a lookup. The lookup is case sensitive and must end
	 * with a ':'. It must not contain another ':' character.
	 * </p>
	 * <p>
	 * For example, a path could be "classpath:/email/hello.pdf". The lookup is
	 * "classpath:".
	 * </p>
	 * 
	 * @param path
	 *            the path to the attachment
	 * @param description
	 *            the description of the attachment (may be null)
	 * @param disposition
	 *            the disposition of the attachment
	 */
	public Attachment(ResourcePath path, String description, String disposition) {
		this(new LookupResource(path), description, disposition);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided path and description of the
	 * attachment.
	 * </p>
	 * <p>
	 * The path may contain a lookup. The lookup is case sensitive and must end
	 * with a ':'. It must not contain another ':' character.
	 * </p>
	 * <p>
	 * For example, a path could be "classpath:/email/hello.pdf". The lookup is
	 * "classpath:".
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
	public Attachment(ResourcePath path, String description) {
		this(new LookupResource(path), description);
	}

	/**
	 * <p>
	 * Initialize the attachment with the provided path.
	 * </p>
	 * <p>
	 * The path may contain a lookup. The lookup is case sensitive and must end
	 * with a ':'. It must not contain another ':' character.
	 * </p>
	 * <p>
	 * For example, a path could be "classpath:/email/hello.pdf". The lookup is
	 * "classpath:".
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
	public Attachment(ResourcePath path) {
		this(new LookupResource(path));
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
		this(new ByteResource(name, stream), description, disposition);
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
		this(new ByteResource(name, stream), description);
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
		this(new ByteResource(name, stream));
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
		this(new ByteResource(name, content), description, disposition);
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
		this(new ByteResource(name, content), description);
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
		this(new ByteResource(name, content));
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
		this(new FileResource(file), description, disposition);
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
		this(new FileResource(file), description);
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
		this(new FileResource(file));
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

	public NamedResource getResource() {
		return resource;
	}

	public void setResource(NamedResource resource) {
		this.resource = resource;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("<").append(resource.getName());
		if (description != null) {
			builder.append(" )").append(description).append(")");
		}
		if (contentId != null) {
			builder.append("{").append(contentId).append("}>");
		}
		builder.append("[").append(disposition).append("]>");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(resource, description, disposition, contentId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("resource", "description", "disposition", "contentId").isEqual();
	}
}

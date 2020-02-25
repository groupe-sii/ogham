package fr.sii.ogham.email.message.fluent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.resource.FileResource;
import fr.sii.ogham.core.resource.LookupResource;
import fr.sii.ogham.core.resource.OverrideNameWrapper;
import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.attachment.ContentDisposition;
import fr.sii.ogham.email.message.Email;

/**
 * Fluent API to attach a file to the email.
 * 
 * @author Aurélien Baudet
 * @since 3.0.0
 */
public class AttachBuilder {
	private final Email parent;
	private final List<Attachment> attachments;

	/**
	 * Initializes with the parent to go back to.
	 * 
	 * @param parent
	 *            the parent instance
	 */
	public AttachBuilder(Email parent) {
		super();
		this.parent = parent;
		this.attachments = new ArrayList<>();
	}

	/**
	 * Attach a file to the email. The name of the attachment uses the name of
	 * the file.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param file
	 *            the file to attach
	 * @return the email instance for fluent chaining
	 */
	public Email file(File file) {
		return file(file, null);
	}

	/**
	 * Attach a file to the email. The name of the attachment uses the name of
	 * the file.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param file
	 *            the file to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email file(File file, String contentType) {
		attach(new Attachment(file), contentType);
		return parent;
	}

	/**
	 * Attach a file to the email. The name of the attachment is explicitly
	 * specified.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param customName
	 *            the name to use for the attachment
	 * @param file
	 *            the file to attach
	 * @return the email instance for fluent chaining
	 */
	public Email file(String customName, File file) {
		return file(customName, file, null);
	}

	/**
	 * Attach a file to the email. The name of the attachment is explicitly
	 * specified.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param customName
	 *            the name to use for the attachment
	 * @param file
	 *            the file to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email file(String customName, File file, String contentType) {
		attach(new Attachment(new FileResource(file, customName)), contentType);
		return parent;
	}

	/**
	 * Attach a file to the email. The name of the attachment uses the name of
	 * the file.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param path
	 *            the path to the file to attach
	 * @return the email instance for fluent chaining
	 */
	public Email file(Path path) {
		return file(path, null);
	}

	/**
	 * Attach a file to the email. The name of the attachment uses the name of
	 * the file.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param path
	 *            the path to the file to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email file(Path path, String contentType) {
		return file(path.toFile(), contentType);
	}

	/**
	 * Attach a file to the email. The name of the attachment is explicitly
	 * specified.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param customName
	 *            the name to use for the attachment
	 * @param path
	 *            the path to the file to attach
	 * @return the email instance for fluent chaining
	 */
	public Email file(String customName, Path path) {
		return file(customName, path, null);
	}

	/**
	 * Attach a file to the email. The name of the attachment is explicitly
	 * specified.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param customName
	 *            the name to use for the attachment
	 * @param path
	 *            the path to the file to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email file(String customName, Path path, String contentType) {
		return file(customName, path.toFile(), contentType);
	}

	/**
	 * Attach a resource loaded from a path to the email. The name of the
	 * attachment uses the name of the resource (extracted from the path).
	 * 
	 * <p>
	 * The path may contain the lookup prefix (a prefix that indicates where to
	 * find the resource). For example:
	 * <ul>
	 * <li><code>"classpath:/path/to/file.pdf"</code> references a resource that
	 * is located at "path/to/file.pdf" in the classpath (from the root)</li>
	 * <li><code>"file:/path/to/file.pdf"</code> references a resource that is
	 * located at "/path/to/file.pdf" in the file system</li>
	 * </ul>
	 * By default, if the path doesn't contain any lookup prefix then the
	 * resource is loaded from the root of the classpath.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param path
	 *            the path to the resource to attach
	 * @return the email instance for fluent chaining
	 */
	public Email resource(String path) {
		attach(new Attachment(path), null);
		return parent;
	}

	/**
	 * Attach a resource loaded from a path to the email. The name of the
	 * attachment is explicitly set.
	 * 
	 * <p>
	 * The path may contain the lookup prefix (a prefix that indicates where to
	 * find the resource). For example:
	 * <ul>
	 * <li><code>"classpath:/path/to/file.pdf"</code> references a resource that
	 * is located at "path/to/file.pdf" in the classpath (from the root)</li>
	 * <li><code>"file:/path/to/file.pdf"</code> references a resource that is
	 * located at "/path/to/file.pdf" in the file system</li>
	 * </ul>
	 * By default, if the path doesn't contain any lookup prefix then the
	 * resource is loaded from the root of the classpath.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param customName
	 *            the name to use for the attachment
	 * @param path
	 *            the path to the resource to attach
	 * @return the email instance for fluent chaining
	 */
	public Email resource(String customName, String path) {
		return resource(customName, path, null);
	}

	/**
	 * Attach a resource loaded from a path to the email. The name of the
	 * attachment is explicitly set.
	 * 
	 * <p>
	 * The path may contain the lookup prefix (a prefix that indicates where to
	 * find the resource). For example:
	 * <ul>
	 * <li><code>"classpath:/path/to/file.pdf"</code> references a resource that
	 * is located at "path/to/file.pdf" in the classpath (from the root)</li>
	 * <li><code>"file:/path/to/file.pdf"</code> references a resource that is
	 * located at "/path/to/file.pdf" in the file system</li>
	 * </ul>
	 * By default, if the path doesn't contain any lookup prefix then the
	 * resource is loaded from the root of the classpath.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param customName
	 *            the name to use for the attachment
	 * @param path
	 *            the path to the resource to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email resource(String customName, String path, String contentType) {
		return resource(customName, new UnresolvedPath(path), contentType);
	}

	/**
	 * Attach a resource loaded from a path to the email. The name of the
	 * attachment uses the name of the resource (extracted from the path).
	 * 
	 * <p>
	 * The path may contain the lookup prefix (a prefix that indicates where to
	 * find the resource). For example:
	 * <ul>
	 * <li><code>"classpath:/path/to/file.pdf"</code> references a resource that
	 * is located at "path/to/file.pdf" in the classpath (from the root)</li>
	 * <li><code>"file:/path/to/file.pdf"</code> references a resource that is
	 * located at "/path/to/file.pdf" in the file system</li>
	 * </ul>
	 * By default, if the path doesn't contain any lookup prefix then the
	 * resource is loaded from the root of the classpath.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param path
	 *            the path to the resource to attach
	 * @return the email instance for fluent chaining
	 */
	public Email resource(ResourcePath path) {
		return resource(path, null);
	}

	/**
	 * Attach a resource loaded from a path to the email. The name of the
	 * attachment uses the name of the resource (extracted from the path).
	 * 
	 * <p>
	 * The path may contain the lookup prefix (a prefix that indicates where to
	 * find the resource). For example:
	 * <ul>
	 * <li><code>"classpath:/path/to/file.pdf"</code> references a resource that
	 * is located at "path/to/file.pdf" in the classpath (from the root)</li>
	 * <li><code>"file:/path/to/file.pdf"</code> references a resource that is
	 * located at "/path/to/file.pdf" in the file system</li>
	 * </ul>
	 * By default, if the path doesn't contain any lookup prefix then the
	 * resource is loaded from the root of the classpath.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param path
	 *            the path to the resource to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email resource(ResourcePath path, String contentType) {
		attach(new Attachment(path), contentType);
		return parent;
	}

	/**
	 * Attach a resource loaded from a path to the email. The name of the
	 * attachment is explicitly set.
	 * 
	 * <p>
	 * The path may contain the lookup prefix (a prefix that indicates where to
	 * find the resource). For example:
	 * <ul>
	 * <li><code>"classpath:/path/to/file.pdf"</code> references a resource that
	 * is located at "path/to/file.pdf" in the classpath (from the root)</li>
	 * <li><code>"file:/path/to/file.pdf"</code> references a resource that is
	 * located at "/path/to/file.pdf" in the file system</li>
	 * </ul>
	 * By default, if the path doesn't contain any lookup prefix then the
	 * resource is loaded from the root of the classpath.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param customName
	 *            the name to use for the attachment
	 * @param path
	 *            the path to the resource to attach
	 * @return the email instance for fluent chaining
	 */
	public Email resource(String customName, ResourcePath path) {
		return resource(customName, path, null);
	}

	/**
	 * Attach a resource loaded from a path to the email. The name of the
	 * attachment is explicitly set.
	 * 
	 * <p>
	 * The path may contain the lookup prefix (a prefix that indicates where to
	 * find the resource). For example:
	 * <ul>
	 * <li><code>"classpath:/path/to/file.pdf"</code> references a resource that
	 * is located at "path/to/file.pdf" in the classpath (from the root)</li>
	 * <li><code>"file:/path/to/file.pdf"</code> references a resource that is
	 * located at "/path/to/file.pdf" in the file system</li>
	 * </ul>
	 * By default, if the path doesn't contain any lookup prefix then the
	 * resource is loaded from the root of the classpath.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param customName
	 *            the name to use for the attachment
	 * @param path
	 *            the path to the resource to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email resource(String customName, ResourcePath path, String contentType) {
		attach(new Attachment(new OverrideNameWrapper(new LookupResource(path), customName)), contentType);
		return parent;
	}

	/**
	 * Attach a resource to the email directly using the bytes. The name of the
	 * attachment must be explicitly set.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param attachmentName
	 *            the name of the attachment
	 * @param bytes
	 *            the content of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email bytes(String attachmentName, byte[] bytes) {
		return bytes(attachmentName, bytes, null);
	}

	/**
	 * Attach a resource to the email directly using the bytes. The name of the
	 * attachment must be explicitly set.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param attachmentName
	 *            the name of the attachment
	 * @param bytes
	 *            the content of the attachment
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email bytes(String attachmentName, byte[] bytes, String contentType) {
		attach(new Attachment(attachmentName, bytes), contentType);
		return parent;
	}

	/**
	 * Attach a resource to the email directly using the bytes read from the
	 * {@link InputStream}. The {@link InputStream} is immediately read and
	 * converted to a byte array. The name of the attachment must be explicitly
	 * set.
	 * 
	 * <p>
	 * <strong>IMPORTANT:</strong> You need to manually close the stream.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param attachmentName
	 *            the name of the attachment
	 * @param stream
	 *            the content of the attachment
	 * @return the email instance for fluent chaining
	 * @throws IOException
	 *             when the {@link InputStream} can't be read
	 */
	public Email stream(String attachmentName, InputStream stream) throws IOException {
		return stream(attachmentName, stream, null);
	}

	/**
	 * Attach a resource to the email directly using the bytes read from the
	 * {@link InputStream}. The {@link InputStream} is immediately read and
	 * converted to a byte array. The name of the attachment must be explicitly
	 * set.
	 * 
	 * <p>
	 * <strong>IMPORTANT:</strong> You need to manually close the stream.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#ATTACHMENT}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param attachmentName
	 *            the name of the attachment
	 * @param stream
	 *            the content of the attachment
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 * @throws IOException
	 *             when the {@link InputStream} can't be read
	 */
	public Email stream(String attachmentName, InputStream stream, String contentType) throws IOException {
		attach(new Attachment(attachmentName, stream), contentType);
		return parent;
	}

	/**
	 * Provides the list of attachments.
	 * 
	 * @return the list of attachments
	 */
	public List<Attachment> build() {
		return attachments;
	}

	private void attach(Attachment attachment, String contentType) {
		attachment.setContentType(contentType);
		attachments.add(attachment);
	}
}

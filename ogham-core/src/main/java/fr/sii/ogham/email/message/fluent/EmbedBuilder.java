package fr.sii.ogham.email.message.fluent;

import static fr.sii.ogham.email.attachment.ContentDisposition.INLINE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import fr.sii.ogham.core.resource.path.ResourcePath;
import fr.sii.ogham.core.resource.path.UnresolvedPath;
import fr.sii.ogham.email.attachment.Attachment;
import fr.sii.ogham.email.attachment.ContentDisposition;
import fr.sii.ogham.email.message.Email;

/**
 * Fluent API to embed a file to the email (mainly used for images). The file
 * must be referenced in the email body using a
 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID (or
 * CID)</a>.
 * 
 * @author Aurélien Baudet
 * @since 3.0.0
 */
public class EmbedBuilder {
	private final Email parent;
	private final List<Attachment> attachments;

	/**
	 * Initializes with the parent to go back to.
	 * 
	 * @param parent
	 *            the parent instance
	 */
	public EmbedBuilder(Email parent) {
		super();
		this.parent = parent;
		this.attachments = new ArrayList<>();
	}

	/**
	 * Embed a file to the email. The file must be referenced in the email body
	 * using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param file
	 *            the file to attach
	 * @return the email instance for fluent chaining
	 */
	public Email file(String contentId, File file) {
		return file(contentId, file, null);
	}

	/**
	 * Embed a file to the email. The file must be referenced in the email body
	 * using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param file
	 *            the file to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email file(String contentId, File file, String contentType) {
		embed(new Attachment(file), contentId, contentType);
		return parent;
	}

	/**
	 * Embed a file to the email. The file must be referenced in the email body
	 * using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param path
	 *            the path to the file to attach
	 * @return the email instance for fluent chaining
	 */
	public Email file(String contentId, Path path) {
		return file(contentId, path, null);
	}

	/**
	 * Embed a file to the email. The file must be referenced in the email body
	 * using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param path
	 *            the path to the file to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email file(String contentId, Path path, String contentType) {
		return file(contentId, path.toFile(), contentType);
	}

	/**
	 * Embed a resource loaded from a path to the email. The resource must be
	 * referenced in the email body using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
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
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param path
	 *            the path to the resource to attach
	 * @return the email instance for fluent chaining
	 */
	public Email resource(String contentId, String path) {
		return resource(contentId, path, null);
	}

	/**
	 * Embed a resource loaded from a path to the email. The resource must be
	 * referenced in the email body using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
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
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param path
	 *            the path to the resource to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email resource(String contentId, String path, String contentType) {
		return resource(contentId, new UnresolvedPath(path), contentType);
	}

	/**
	 * Embed a resource loaded from a path to the email. The resource must be
	 * referenced in the email body using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
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
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param path
	 *            the path to the resource to attach
	 * @return the email instance for fluent chaining
	 */
	public Email resource(String contentId, ResourcePath path) {
		return resource(contentId, path, null);
	}

	/**
	 * Embed a resource loaded from a path to the email. The resource must be
	 * referenced in the email body using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
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
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param path
	 *            the path to the resource to attach
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email resource(String contentId, ResourcePath path, String contentType) {
		embed(new Attachment(path), contentId, contentType);
		return parent;
	}

	/**
	 * Embed a resource to the email directly using the bytes. The embedded
	 * resource must be referenced in the email body using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param bytes
	 *            the content of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email bytes(String contentId, byte[] bytes) {
		return bytes(contentId, bytes, null);
	}

	/**
	 * Embed a resource to the email directly using the bytes. The embedded
	 * resource must be referenced in the email body using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param bytes
	 *            the content of the attachment
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 */
	public Email bytes(String contentId, byte[] bytes, String contentType) {
		embed(new Attachment(contentId, bytes), contentId, contentType);
		return parent;
	}

	/**
	 * Attach a resource to the email directly using the bytes read from the
	 * {@link InputStream}. The {@link InputStream} is immediately read and
	 * converted to a byte array.  The embedded
	 * resource must be referenced in the email body using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
	 * 
	 * <p>
	 * <strong>IMPORTANT:</strong> You need to manually close the stream.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is automatically determined.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param stream
	 *            the content of the attachment
	 * @return the email instance for fluent chaining
	 * @throws IOException
	 *             when the {@link InputStream} can't be read
	 */
	public Email stream(String contentId, InputStream stream) throws IOException {
		return stream(contentId, stream, null);
	}

	/**
	 * Attach a resource to the email directly using the bytes read from the
	 * {@link InputStream}. The {@link InputStream} is immediately read and
	 * converted to a byte array.  The embedded
	 * resource must be referenced in the email body using a
	 * <a href="https://tools.ietf.org/html/rfc4021#section-2.2.2">Content-ID
	 * (or CID)</a>.
	 * 
	 * <p>
	 * <strong>IMPORTANT:</strong> You need to manually close the stream.
	 * 
	 * <p>
	 * The disposition of the attachment is set to
	 * {@link ContentDisposition#INLINE}.
	 * 
	 * <p>
	 * The Content-Type of the attachment is explicitly set.
	 * 
	 * @param contentId
	 *            the Content-ID used to reference the embedded attachment in
	 *            the email body
	 * @param stream
	 *            the content of the attachment
	 * @param contentType
	 *            the Content-Type of the attachment
	 * @return the email instance for fluent chaining
	 * @throws IOException
	 *             when the {@link InputStream} can't be read
	 */
	public Email stream(String contentId, InputStream stream, String contentType) throws IOException {
		embed(new Attachment(contentId, stream), contentId, contentType);
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

	private void embed(Attachment attachment, String contentId, String contentType) {
		attachment.setContentId(contentId);
		attachment.setContentType(contentType);
		attachment.setDisposition(INLINE);
		attachments.add(attachment);
	}
}

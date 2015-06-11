package fr.sii.notification.email.attachment.resolver;

/**
 * Extension of the basic attachment source resolution interface for adding
 * conditional use of the implementations. This extension adds a method that
 * indicates to the system that uses it if the implementation is able to handle
 * the provided attachment path.
 * 
 * @author Aurélien Baudet
 *
 */
public interface ConditionalResolver extends SourceResolver {
	/**
	 * Indicates if the attachment path can be handled by this attachment
	 * resolver or not.
	 * 
	 * @param path
	 *            the path to the attachment
	 * @return true if the path can be handled by this attachment source
	 *         resolver, false otherwise
	 */
	public boolean supports(String path);
}

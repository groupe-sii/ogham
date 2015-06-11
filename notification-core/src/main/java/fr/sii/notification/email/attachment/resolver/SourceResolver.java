package fr.sii.notification.email.attachment.resolver;

import fr.sii.notification.email.attachment.Source;
import fr.sii.notification.email.exception.attachment.resolver.SourceResolutionException;

/**
 * <p>
 * Interface for all attachment source resolvers. Source resolvers are in charge
 * of finding the attachment from its path. An attachment can be stored at many
 * places. For example, an attachment can be stored either on the file system,
 * into the classpath, on a distant URL or into a database...
 * </p>
 * <p>
 * Each implementation is able to handle one resolution mechanism. Any new
 * implementation can be defined for future storage source.
 * </p>
 * 
 * @author Aurélien Baudet
 *
 */
public interface SourceResolver {
	/**
	 * Find the attachment using the provided path.
	 * 
	 * @param path
	 *            the path to the attachment
	 * @return the found attachment
	 * @throws SourceResolutionException
	 *             when attachment couldn't be found
	 */
	public Source resolve(String path) throws SourceResolutionException;
}

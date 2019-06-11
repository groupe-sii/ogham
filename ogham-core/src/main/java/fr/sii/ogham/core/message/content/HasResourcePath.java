package fr.sii.ogham.core.message.content;

import fr.sii.ogham.core.resource.path.ResourcePath;

/**
 * Marker interface to indicate that the content comes form a file loaded from a
 * {@link ResourcePath}.
 * 
 * @author Aurélien Baudet
 */
public interface HasResourcePath {
	/**
	 * The path to the resource that has been loaded to retrieve its content
	 * 
	 * @return the path to the loaded resource
	 */
	ResourcePath getPath();
}

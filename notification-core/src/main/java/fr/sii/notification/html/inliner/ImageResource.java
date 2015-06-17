package fr.sii.notification.html.inliner;

import java.util.Arrays;

import fr.sii.notification.html.translator.InlineImageTranslator;

/**
 * A simple object that represents an image to be usable by the
 * {@link InlineImageTranslator}. The aim is to decouple the translator
 * implementation from the general system.
 * 
 * @author Aurélien Baudet
 *
 */
public class ImageResource {
	private String name;

	private String path;

	private byte[] content;

	private String mimetype;

	/**
	 * Initialize the resource with the name of the image, the path of the image
	 * found in the HTML, the content of the image as array of bytes and the
	 * mimetype of the image. The bytes for the content of the image are copied
	 * into a new array to prevent security leaks.
	 * 
	 * @param name
	 *            the name of the image
	 * @param path
	 *            the path to the image
	 * @param content
	 *            the content of the image
	 * @param mimetype
	 *            the mimetype of the image
	 */
	public ImageResource(String name, String path, byte[] content, String mimetype) {
		super();
		this.name = name;
		this.path = path;
		this.content = Arrays.copyOf(content, content.length);
		this.mimetype = mimetype;
	}

	public String getPath() {
		return path;
	}

	public byte[] getContent() {
		return content;
	}

	public String getMimetype() {
		return mimetype;
	}

	public String getName() {
		return name;
	}
}

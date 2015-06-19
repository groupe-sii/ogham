package fr.sii.ogham.core.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.sii.ogham.core.util.EqualsBuilder;
import fr.sii.ogham.core.util.HashCodeBuilder;

/**
 * Resource that provide access to a file.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileResource implements NamedResource {
	/**
	 * The file to attach
	 */
	private File file;

	/**
	 * The name for the attachment
	 */
	private String name;

	/**
	 * Initialize the resource with the provided file and name. The file is the
	 * content of the resource. Use the provided name for the resource name
	 * instead of the name of the file.
	 * 
	 * @param file
	 *            the content of the resource
	 * @param name
	 *            the name for the resource
	 */
	public FileResource(File file, String name) {
		super();
		this.file = file;
		this.name = name;
	}

	/**
	 * Initialize the resource with the provided file. The file is the content
	 * of the resource. Use the name of the file for the name of the resource.
	 * 
	 * @param file
	 *            the content of the resource
	 */
	public FileResource(File file) {
		this(file, file.getName());
	}

	/**
	 * Initialize the resource with the provided file. The file is the content
	 * of the resource. Use the name of the file for the name of the resource.
	 * 
	 * @param fileName
	 *            the content of the resource
	 */
	public FileResource(String fileName) {
		this(new File(fileName));
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	public File getFile() {
		return file;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(name).append(file).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder(this, obj).appendFields("name", "file").isEqual();
	}
}

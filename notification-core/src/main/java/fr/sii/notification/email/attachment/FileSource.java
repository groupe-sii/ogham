package fr.sii.notification.email.attachment;

import java.io.File;

import fr.sii.notification.core.util.EqualsBuilder;
import fr.sii.notification.core.util.HashCodeBuilder;

/**
 * Source that provide access to a file.
 * 
 * @author Aurélien Baudet
 *
 */
public class FileSource implements Source {
	/**
	 * The file to attach
	 */
	private File file;

	/**
	 * The name for the attachment
	 */
	private String name;

	/**
	 * Initialize the source with the provided file and name. The file is the
	 * content of the attachment. The name is displayed in the email to
	 * reference the file.
	 * 
	 * @param file
	 *            the content of the attachment
	 * @param name
	 *            the name for the attachment
	 */
	public FileSource(File file, String name) {
		super();
		this.file = file;
		this.name = name;
	}

	/**
	 * Initialize the source with the provided file. The file is the content of
	 * the attachment. The name is extracted from the file and is used in the
	 * email to reference the file.
	 * 
	 * @param file
	 *            the content of the attachment
	 */
	public FileSource(File file) {
		this(file, file.getName());
	}

	/**
	 * Initialize the source with the provided file. The file is the content of
	 * the attachment. The name is extracted from the file and is used in the
	 * email to reference the file.
	 * 
	 * @param fileName
	 *            the content of the attachment
	 */
	public FileSource(String fileName) {
		this(new File(fileName));
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

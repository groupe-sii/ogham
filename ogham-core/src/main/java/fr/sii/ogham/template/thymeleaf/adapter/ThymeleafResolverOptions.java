package fr.sii.ogham.template.thymeleaf.adapter;

/**
 * 
 * @author Cyril Dejonghe
 *
 */
public class ThymeleafResolverOptions {
	/**
	 * The parent path to add to the resource name (or path)
	 */
	private String parentPath;

	/**
	 * The suffix to add to the resource name (or path)
	 */
	private String extension;

	public ThymeleafResolverOptions(String parentPath, String extension) {
		super();
		this.parentPath = parentPath;
		this.extension = extension;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

}
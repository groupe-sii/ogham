package fr.sii.ogham.template.thymeleaf.adapter;

/**
 * 
 * @author Cyril Dejonghe
 *
 */
public abstract class AbstractSimpleThymeleafResolverAdapter implements ThymeleafResolverAdapter {
	private String parentPath;
	private String extension;

	public String getParentPath() {
		return parentPath;
	}

	@Override
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getExtension() {
		return extension;
	}

	@Override
	public void setExtension(String extension) {
		this.extension = extension;
	}

}

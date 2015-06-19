package fr.sii.ogham.html.inliner;

public class ExternalCss {
	private String path;
	
	private String content;

	public ExternalCss(String path, String content) {
		super();
		this.path = path;
		this.content = content;
	}

	public String getPath() {
		return path;
	}

	public String getContent() {
		return content;
	}
}

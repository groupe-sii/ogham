package fr.sii.notification.html.inliner;

public class ImageResource {
	private String name;
	
	private String path;
	
	private byte[] content;
	
	private String mimetype;

	public ImageResource(String name, String path, byte[] content, String mimetype) {
		super();
		this.name = name;
		this.path = path;
		this.content = content;
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

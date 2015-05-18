package fr.sii.notification.helper;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class ExpectedContent {
	private String body;
	
	private String mimetype;

	public ExpectedContent(String body, String mimetype) {
		super();
		this.body = body;
		this.mimetype = mimetype;
	}

	public ExpectedContent(InputStream body, String mimetype) throws IOException {
		this(IOUtils.toString(body), mimetype);
	}

	public String getBody() {
		return body;
	}

	public String getMimetype() {
		return mimetype;
	}
	
}

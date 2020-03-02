package fr.sii.ogham.spring.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("ogham.mimetype")
public class OghamMimetypeProperties {
	/**
	 * Default mimetype if detection couldn't determine the mimetype
	 */
	private String defaultMimetype = "application/octet-stream";
	@NestedConfigurationProperty
	private TikaProperties tika = new TikaProperties();

	public String getDefaultMimetype() {
		return defaultMimetype;
	}

	public void setDefaultMimetype(String defaultMimetype) {
		this.defaultMimetype = defaultMimetype;
	}

	public TikaProperties getTika() {
		return tika;
	}

	public void setTika(TikaProperties tika) {
		this.tika = tika;
	}

	public static class TikaProperties {
		/**
		 * If Tika detection returns an {@code application/octet-stream}, it may
		 * means that detection was not enough accurate. In order to try other
		 * registered implementations that are able to detect mimetypes, you can
		 * set this to true.
		 * 
		 */
		private boolean failIfOctetStream = true;

		public boolean isFailIfOctetStream() {
			return failIfOctetStream;
		}

		public void setFailIfOctetStream(boolean failIfOctetStream) {
			this.failIfOctetStream = failIfOctetStream;
		}
	}
}

package fr.sii.ogham.email.sendgrid.v4.sender.impl.sendgrid.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;

import com.sendgrid.Client;
import com.sendgrid.SendGrid;

/**
 * Override default {@link SendGrid} implementation in order to be able to
 * change protocol and port.
 * 
 * @author Aurélien Baudet
 */
public class CustomizableUrlClient extends Client {

	private final String protocol;
	private final int port;

	public CustomizableUrlClient(String protocol, int port) {
		super();
		this.protocol = protocol;
		this.port = port;
	}

	public CustomizableUrlClient(Boolean test, String protocol, int port) {
		super(test);
		this.protocol = protocol;
		this.port = port;
	}

	public CustomizableUrlClient(CloseableHttpClient httpClient, String protocol, int port) {
		super(httpClient);
		this.protocol = protocol;
		this.port = port;
	}

	@Override
	public URI buildUri(String baseUri, String endpoint, Map<String, String> queryParams) throws URISyntaxException {
		URI base = super.buildUri(baseUri, endpoint, queryParams);
		URIBuilder builder = new URIBuilder(base);
		builder.setScheme(protocol);
		builder.setPort(port);
		return builder.build();
	}

}

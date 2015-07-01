package fr.sii.ogham.core.util.http;


/**
 * Simple abstraction of the HTTP response. It just contains the useful
 * information (response body as string and http status).
 * 
 * @author Aurélien Baudet
 *
 */
public class Response {
	/**
	 * The HTTP status
	 */
	private final HttpStatus status;

	/**
	 * The response body directly as String
	 */
	private final String body;

	public Response(int status, String body) {
		this(HttpStatus.valueOf(status), body);
	}

	public Response(HttpStatus status, String body) {
		super();
		this.status = status;
		this.body = body;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getBody() {
		return body;
	}
}
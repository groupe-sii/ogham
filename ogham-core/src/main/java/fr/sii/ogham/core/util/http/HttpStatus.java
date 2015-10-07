package fr.sii.ogham.core.util.http;

/**
 * Enum that contains all HTTP status.
 * 
 * @author Aurélien Baudet
 *
 */
public enum HttpStatus {
	// --- 1xx Informational ---

	/** <tt>100 Continue</tt> (HTTP/1.1 - RFC 2616) */
	CONTINUE(100, "Continue"),
	/** <tt>101 Switching Protocols</tt> (HTTP/1.1 - RFC 2616) */
	SWITCHING_PROTOCOLS(101, "Switching Protocols"),
	/** <tt>102 Processing</tt> (WebDAV - RFC 2518) */
	PROCESSING(102, "Processing"),

	// --- 2xx Success ---

	/** <tt>200 OK</tt> (HTTP/1.0 - RFC 1945) */
	OK(200, "OK"),
	/** <tt>201 Created</tt> (HTTP/1.0 - RFC 1945) */
	CREATED(201, "Created"),
	/** <tt>202 Accepted</tt> (HTTP/1.0 - RFC 1945) */
	ACCEPTED(202, "Accepted"),
	/** <tt>203 Non Authoritative Information</tt> (HTTP/1.1 - RFC 2616) */
	NON_AUTHORITATIVE_INFORMATION(203, "Non Authoritative Information"),
	/** <tt>204 No Content</tt> (HTTP/1.0 - RFC 1945) */
	NO_CONTENT(204, "No Content"),
	/** <tt>205 Reset Content</tt> (HTTP/1.1 - RFC 2616) */
	RESET_CONTENT(205, "Reset Content"),
	/** <tt>206 Partial Content</tt> (HTTP/1.1 - RFC 2616) */
	PARTIAL_CONTENT(206, "Partial Content"),
	/**
	 * <tt>207 Multi-Status</tt> (WebDAV - RFC 2518) or <tt>207 Partial Update
	 * OK</tt> (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
	 */
	MULTI_STATUS(207, "Multi-Status"),
	/**
	 * <tt>207 Multi-Status</tt> (WebDAV - RFC 2518) or <tt>207 Partial Update
	 * OK</tt> (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
	 */
	PARTIAL_UPDATE_OK(207, "Partial Update OK"),

	// --- 3xx Redirection ---

	/** <tt>300 Mutliple Choices</tt> (HTTP/1.1 - RFC 2616) */
	MULTIPLE_CHOICES(300, "Mutliple Choices"),
	/** <tt>301 Moved Permanently</tt> (HTTP/1.0 - RFC 1945) */
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	/**
	 * <tt>302 Moved Temporarily</tt> (Sometimes <tt>Found</tt>) (HTTP/1.0 - RFC
	 * 1945)
	 */
	MOVED_TEMPORARILY(302, "Moved Temporarily"),
	/** <tt>303 See Other</tt> (HTTP/1.1 - RFC 2616) */
	SEE_OTHER(303, "See Other"),
	/** <tt>304 Not Modified</tt> (HTTP/1.0 - RFC 1945) */
	NOT_MODIFIED(304, "Not Modified"),
	/** <tt>305 Use Proxy</tt> (HTTP/1.1 - RFC 2616) */
	USE_PROXY(305, "Use Proxy"),
	/** <tt>307 Temporary Redirect</tt> (HTTP/1.1 - RFC 2616) */
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),

	// --- 4xx Client Error ---

	/** <tt>400 Bad Request</tt> (HTTP/1.1 - RFC 2616) */
	BAD_REQUEST(400, "Bad Request"),
	/** <tt>401 Unauthorized</tt> (HTTP/1.0 - RFC 1945) */
	UNAUTHORIZED(401, "Unauthorized"),
	/** <tt>402 Payment Required</tt> (HTTP/1.1 - RFC 2616) */
	PAYMENT_REQUIRED(402, "Payment Required"),
	/** <tt>403 Forbidden</tt> (HTTP/1.0 - RFC 1945) */
	FORBIDDEN(403, "Forbidden"),
	/** <tt>404 Not Found</tt> (HTTP/1.0 - RFC 1945) */
	NOT_FOUND(404, "Not Found"),
	/** <tt>405 Method Not Allowed</tt> (HTTP/1.1 - RFC 2616) */
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	/** <tt>406 Not Acceptable</tt> (HTTP/1.1 - RFC 2616) */
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	/** <tt>407 Proxy Authentication Required</tt> (HTTP/1.1 - RFC 2616) */
	PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
	/** <tt>408 Request Timeout</tt> (HTTP/1.1 - RFC 2616) */
	REQUEST_TIMEOUT(408, "Request Timeout"),
	/** <tt>409 Conflict</tt> (HTTP/1.1 - RFC 2616) */
	CONFLICT(409, "Conflict"),
	/** <tt>410 Gone</tt> (HTTP/1.1 - RFC 2616) */
	GONE(410, "Gone"),
	/** <tt>411 Length Required</tt> (HTTP/1.1 - RFC 2616) */
	LENGTH_REQUIRED(411, "Length Required"),
	/** <tt>412 Precondition Failed</tt> (HTTP/1.1 - RFC 2616) */
	PRECONDITION_FAILED(412, "Precondition Failed"),
	/** <tt>413 Request Entity Too Large</tt> (HTTP/1.1 - RFC 2616) */
	REQUEST_TOO_LONG(413, "Request Entity Too Large"),
	/** <tt>414 Request-URI Too Long</tt> (HTTP/1.1 - RFC 2616) */
	REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
	/** <tt>415 Unsupported Media Type</tt> (HTTP/1.1 - RFC 2616) */
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	/** <tt>416 Requested Range Not Satisfiable</tt> (HTTP/1.1 - RFC 2616) */
	REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
	/** <tt>417 Expectation Failed</tt> (HTTP/1.1 - RFC 2616) */
	EXPECTATION_FAILED(417, "Expectation Failed"),

	/**
	 * Static constant for a 418 error. <tt>418 Unprocessable Entity</tt>
	 * (WebDAV drafts?) or <tt>418 Reauthentication Required</tt> (HTTP/1.1
	 * drafts?)
	 */
	// not used
	// UNPROCESSABLE_ENTITY(418, "Unprocessable Entity");

	/**
	 * Static constant for a 419 error.
	 * <tt>419 Insufficient Space on Resource</tt> (WebDAV -
	 * draft-ietf-webdav-protocol-05?) or
	 * <tt>419 Proxy Reauthentication Required</tt> (HTTP/1.1 drafts?)
	 */
	INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space on Resource"),
	/**
	 * Static constant for a 419 error.
	 * <tt>419 Insufficient Space on Resource</tt> (WebDAV -
	 * draft-ietf-webdav-protocol-05?) or
	 * <tt>419 Proxy Reauthentication Required</tt> (HTTP/1.1 drafts?)
	 */
	PROXY_REAUTHENTICATION_REQUIRED(419, "Proxy Reauthentication Required"),
	/**
	 * Static constant for a 420 error. <tt>420 Method Failure</tt> (WebDAV -
	 * draft-ietf-webdav-protocol-05?)
	 */
	METHOD_FAILURE(420, "Method Failure"),
	/** <tt>422 Unprocessable Entity</tt> (WebDAV - RFC 2518) */
	UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
	/** <tt>423 Locked</tt> (WebDAV - RFC 2518) */
	LOCKED(423, "Locked"),
	/** <tt>424 Failed Dependency</tt> (WebDAV - RFC 2518) */
	FAILED_DEPENDENCY(424, "Failed Dependency"),

	// --- 5xx Server Error ---

	/** <tt>500 Server Error</tt> (HTTP/1.0 - RFC 1945) */
	INTERNAL_SERVER_ERROR(500, "Server Error"),
	/** <tt>501 Not Implemented</tt> (HTTP/1.0 - RFC 1945) */
	NOT_IMPLEMENTED(501, "Not Implemented"),
	/** <tt>502 Bad Gateway</tt> (HTTP/1.0 - RFC 1945) */
	BAD_GATEWAY(502, "Bad Gateway"),
	/** <tt>503 Service Unavailable</tt> (HTTP/1.0 - RFC 1945) */
	SERVICE_UNAVAILABLE(503, "Service Unavailable"),
	/** <tt>504 Gateway Timeout</tt> (HTTP/1.1 - RFC 2616) */
	GATEWAY_TIMEOUT(504, "Gateway Timeout"),
	/** <tt>505 HTTP Version Not Supported</tt> (HTTP/1.1 - RFC 2616) */
	HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),

	/** <tt>507 Insufficient Storage</tt> (WebDAV - RFC 2518) */
	INSUFFICIENT_STORAGE(507, "Insufficient Storage");

	private static final int SERVER_ERROR_STATUS_START = 500;
	private static final int CLIENT_ERROR_STATUS_START = 400;
	private static final int REDIRECT_STATUS_START = 300;
	private static final int SUCCESS_STATUS_START = 200;
	private static final int INFORMAL_STATUS_START = 100;

	private int code;

	private String reason;

	private HttpStatus(int code, String reason) {
		this.code = code;
		this.reason = reason;
	}

	public int getCode() {
		return code;
	}

	public String getReason() {
		return reason;
	}

	/**
	 * Is the HTTP status stand for informal (between 100 inclusive and 200
	 * exclusive)
	 * 
	 * @return true if informal
	 */
	public boolean isInformal() {
		return code >= INFORMAL_STATUS_START && code < SUCCESS_STATUS_START;
	}

	/**
	 * Is the HTTP status stand for success (between 200 inclusive and 300
	 * exclusive)
	 * 
	 * @return true if success
	 */
	public boolean isSuccess() {
		return code >= SUCCESS_STATUS_START && code < REDIRECT_STATUS_START;
	}

	/**
	 * Is the HTTP status stand for redirect (between 300 inclusive and 400
	 * exclusive)
	 * 
	 * @return true if redirect
	 */
	public boolean isRedirect() {
		return code >= REDIRECT_STATUS_START && code < CLIENT_ERROR_STATUS_START;
	}

	/**
	 * Is the HTTP status stand for client error (between 400 inclusive and 500
	 * exclusive)
	 * 
	 * @return true if client error
	 */
	public boolean isClientError() {
		return code >= CLIENT_ERROR_STATUS_START && code < SERVER_ERROR_STATUS_START;
	}

	/**
	 * Is the HTTP status stand for server error (between 500 inclusive and 600
	 * exclusive)
	 * 
	 * @return true if server error
	 */
	public boolean isServerError() {
		return code >= SERVER_ERROR_STATUS_START;
	}

	public static HttpStatus valueOf(int code) {
		HttpStatus[] values = HttpStatus.values();
		for (HttpStatus value : values) {
			if (value.getCode() == code) {
				return value;
			}
		}
		throw new IllegalArgumentException("Unknown HTTP status code (" + code + ")");
	}
}

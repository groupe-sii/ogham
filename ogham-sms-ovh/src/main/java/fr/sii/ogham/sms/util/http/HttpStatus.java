package fr.sii.ogham.sms.util.http;

/**
 * Enum that contains all HTTP status.
 * 
 * @author Aurélien Baudet
 *
 */
public enum HttpStatus {
	// --- 1xx Informational ---

	/** <strong>100 Continue</strong> (HTTP/1.1 - RFC 2616) */
	CONTINUE(100, "Continue"),
	/** <strong>101 Switching Protocols</strong> (HTTP/1.1 - RFC 2616) */
	SWITCHING_PROTOCOLS(101, "Switching Protocols"),
	/** <strong>102 Processing</strong> (WebDAV - RFC 2518) */
	PROCESSING(102, "Processing"),

	// --- 2xx Success ---

	/** <strong>200 OK</strong> (HTTP/1.0 - RFC 1945) */
	OK(200, "OK"),
	/** <strong>201 Created</strong> (HTTP/1.0 - RFC 1945) */
	CREATED(201, "Created"),
	/** <strong>202 Accepted</strong> (HTTP/1.0 - RFC 1945) */
	ACCEPTED(202, "Accepted"),
	/** <strong>203 Non Authoritative Information</strong> (HTTP/1.1 - RFC 2616) */
	NON_AUTHORITATIVE_INFORMATION(203, "Non Authoritative Information"),
	/** <strong>204 No Content</strong> (HTTP/1.0 - RFC 1945) */
	NO_CONTENT(204, "No Content"),
	/** <strong>205 Reset Content</strong> (HTTP/1.1 - RFC 2616) */
	RESET_CONTENT(205, "Reset Content"),
	/** <strong>206 Partial Content</strong> (HTTP/1.1 - RFC 2616) */
	PARTIAL_CONTENT(206, "Partial Content"),
	/**
	 * <strong>207 Multi-Status</strong> (WebDAV - RFC 2518) or <strong>207 Partial Update
	 * OK</strong> (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
	 */
	MULTI_STATUS(207, "Multi-Status"),
	/**
	 * <strong>207 Multi-Status</strong> (WebDAV - RFC 2518) or <strong>207 Partial Update
	 * OK</strong> (HTTP/1.1 - draft-ietf-http-v11-spec-rev-01?)
	 */
	PARTIAL_UPDATE_OK(207, "Partial Update OK"),

	// --- 3xx Redirection ---

	/** <strong>300 Mutliple Choices</strong> (HTTP/1.1 - RFC 2616) */
	MULTIPLE_CHOICES(300, "Mutliple Choices"),
	/** <strong>301 Moved Permanently</strong> (HTTP/1.0 - RFC 1945) */
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	/**
	 * <strong>302 Moved Temporarily</strong> (Sometimes <strong>Found</strong>) (HTTP/1.0 - RFC
	 * 1945)
	 */
	MOVED_TEMPORARILY(302, "Moved Temporarily"),
	/** <strong>303 See Other</strong> (HTTP/1.1 - RFC 2616) */
	SEE_OTHER(303, "See Other"),
	/** <strong>304 Not Modified</strong> (HTTP/1.0 - RFC 1945) */
	NOT_MODIFIED(304, "Not Modified"),
	/** <strong>305 Use Proxy</strong> (HTTP/1.1 - RFC 2616) */
	USE_PROXY(305, "Use Proxy"),
	/** <strong>307 Temporary Redirect</strong> (HTTP/1.1 - RFC 2616) */
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),

	// --- 4xx Client Error ---

	/** <strong>400 Bad Request</strong> (HTTP/1.1 - RFC 2616) */
	BAD_REQUEST(400, "Bad Request"),
	/** <strong>401 Unauthorized</strong> (HTTP/1.0 - RFC 1945) */
	UNAUTHORIZED(401, "Unauthorized"),
	/** <strong>402 Payment Required</strong> (HTTP/1.1 - RFC 2616) */
	PAYMENT_REQUIRED(402, "Payment Required"),
	/** <strong>403 Forbidden</strong> (HTTP/1.0 - RFC 1945) */
	FORBIDDEN(403, "Forbidden"),
	/** <strong>404 Not Found</strong> (HTTP/1.0 - RFC 1945) */
	NOT_FOUND(404, "Not Found"),
	/** <strong>405 Method Not Allowed</strong> (HTTP/1.1 - RFC 2616) */
	METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
	/** <strong>406 Not Acceptable</strong> (HTTP/1.1 - RFC 2616) */
	NOT_ACCEPTABLE(406, "Not Acceptable"),
	/** <strong>407 Proxy Authentication Required</strong> (HTTP/1.1 - RFC 2616) */
	PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
	/** <strong>408 Request Timeout</strong> (HTTP/1.1 - RFC 2616) */
	REQUEST_TIMEOUT(408, "Request Timeout"),
	/** <strong>409 Conflict</strong> (HTTP/1.1 - RFC 2616) */
	CONFLICT(409, "Conflict"),
	/** <strong>410 Gone</strong> (HTTP/1.1 - RFC 2616) */
	GONE(410, "Gone"),
	/** <strong>411 Length Required</strong> (HTTP/1.1 - RFC 2616) */
	LENGTH_REQUIRED(411, "Length Required"),
	/** <strong>412 Precondition Failed</strong> (HTTP/1.1 - RFC 2616) */
	PRECONDITION_FAILED(412, "Precondition Failed"),
	/** <strong>413 Request Entity Too Large</strong> (HTTP/1.1 - RFC 2616) */
	REQUEST_TOO_LONG(413, "Request Entity Too Large"),
	/** <strong>414 Request-URI Too Long</strong> (HTTP/1.1 - RFC 2616) */
	REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
	/** <strong>415 Unsupported Media Type</strong> (HTTP/1.1 - RFC 2616) */
	UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
	/** <strong>416 Requested Range Not Satisfiable</strong> (HTTP/1.1 - RFC 2616) */
	REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
	/** <strong>417 Expectation Failed</strong> (HTTP/1.1 - RFC 2616) */
	EXPECTATION_FAILED(417, "Expectation Failed"),

	/**
	 * Static constant for a 418 error. <strong>418 Unprocessable Entity</strong>
	 * (WebDAV drafts?) or <strong>418 Reauthentication Required</strong> (HTTP/1.1
	 * drafts?)
	 */
	// not used
	// UNPROCESSABLE_ENTITY(418, "Unprocessable Entity");

	/**
	 * Static constant for a 419 error.
	 * <strong>419 Insufficient Space on Resource</strong> (WebDAV -
	 * draft-ietf-webdav-protocol-05?) or
	 * <strong>419 Proxy Reauthentication Required</strong> (HTTP/1.1 drafts?)
	 */
	INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space on Resource"),
	/**
	 * Static constant for a 419 error.
	 * <strong>419 Insufficient Space on Resource</strong> (WebDAV -
	 * draft-ietf-webdav-protocol-05?) or
	 * <strong>419 Proxy Reauthentication Required</strong> (HTTP/1.1 drafts?)
	 */
	PROXY_REAUTHENTICATION_REQUIRED(419, "Proxy Reauthentication Required"),
	/**
	 * Static constant for a 420 error. <strong>420 Method Failure</strong> (WebDAV -
	 * draft-ietf-webdav-protocol-05?)
	 */
	METHOD_FAILURE(420, "Method Failure"),
	/** <strong>422 Unprocessable Entity</strong> (WebDAV - RFC 2518) */
	UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
	/** <strong>423 Locked</strong> (WebDAV - RFC 2518) */
	LOCKED(423, "Locked"),
	/** <strong>424 Failed Dependency</strong> (WebDAV - RFC 2518) */
	FAILED_DEPENDENCY(424, "Failed Dependency"),

	// --- 5xx Server Error ---

	/** <strong>500 Server Error</strong> (HTTP/1.0 - RFC 1945) */
	INTERNAL_SERVER_ERROR(500, "Server Error"),
	/** <strong>501 Not Implemented</strong> (HTTP/1.0 - RFC 1945) */
	NOT_IMPLEMENTED(501, "Not Implemented"),
	/** <strong>502 Bad Gateway</strong> (HTTP/1.0 - RFC 1945) */
	BAD_GATEWAY(502, "Bad Gateway"),
	/** <strong>503 Service Unavailable</strong> (HTTP/1.0 - RFC 1945) */
	SERVICE_UNAVAILABLE(503, "Service Unavailable"),
	/** <strong>504 Gateway Timeout</strong> (HTTP/1.1 - RFC 2616) */
	GATEWAY_TIMEOUT(504, "Gateway Timeout"),
	/** <strong>505 HTTP Version Not Supported</strong> (HTTP/1.1 - RFC 2616) */
	HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),

	/** <strong>507 Insufficient Storage</strong> (WebDAV - RFC 2518) */
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

package mock.web.dto;

public class ErrorResponse {
	private String type;
	
	private String message;
	
	private String cause;

	public ErrorResponse(String type, String message, String cause) {
		super();
		this.type = type;
		this.message = message;
		this.cause = cause;
	}
	
	public ErrorResponse(Throwable e) {
		super();
		this.type = e.getClass().getSimpleName();
		this.message = e.getMessage();
		this.cause = (e.getCause()==null ? null : e.getCause().getMessage());
	}

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public String getCause() {
		return cause;
	}
}

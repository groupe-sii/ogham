package mock.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.sii.ogham.core.exception.MessagingException;
import mock.web.dto.ErrorResponse;

@ControllerAdvice
public class ExceptionTranslator {
	private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslator.class);
	
	@ExceptionHandler(MessagingException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	public ErrorResponse processMessagingException(MessagingException e) {
		LOG.error("Failed to send message", e);
		return new ErrorResponse(e);
	}
}

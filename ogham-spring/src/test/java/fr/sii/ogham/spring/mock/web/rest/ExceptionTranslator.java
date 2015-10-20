package fr.sii.ogham.spring.mock.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.sii.ogham.core.exception.MessagingException;
import fr.sii.ogham.spring.mock.web.dto.ErrorResponse;

@ControllerAdvice
public class ExceptionTranslator {
	@ExceptionHandler(MessagingException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse processMessagingException(MessagingException e) {
		return new ErrorResponse(e);
	}
}

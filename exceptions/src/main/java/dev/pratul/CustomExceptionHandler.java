package dev.pratul;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	// exception on entity validation
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		// Get all errors
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage())
				.collect(Collectors.toList());
		ErrorMessage errorMessage = new ErrorMessage(ZonedDateTime.now(), status.value(), errors);
		return new ResponseEntity<>(errorMessage, headers, status);

	}

	// bad URL exceptions
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorMessage> constraintVoilationException(ConstraintViolationException response) {
		String[] message = { response.getMessage() };
		ErrorMessage errorMessage = new ErrorMessage(ZonedDateTime.now(), HttpStatus.BAD_REQUEST.value(),
				Arrays.asList(message));
		return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}

	// when the element requested does not exists
	@ExceptionHandler(value = { NoSuchElementException.class })
	public ResponseEntity<ErrorMessage> nosuchElementException(NoSuchElementException exception,
			HttpServletRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(ZonedDateTime.now(), HttpStatus.NOT_FOUND.value(),
				Arrays.asList(exception.getMessage()));
		return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { NullPointerException.class })
	public ResponseEntity<ErrorMessage> nullpointerException(NullPointerException exception,
			HttpServletRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(ZonedDateTime.now(), HttpStatus.NOT_FOUND.value(),
				Arrays.asList(exception.getMessage()));
		return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = { UserServiceException.class })
	public ResponseEntity<ErrorMessage> handleSpecificException(Exception exception, WebRequest request) {
		ErrorMessage errorMessage = new ErrorMessage(ZonedDateTime.now(), HttpStatus.NOT_FOUND.value(),
				Arrays.asList(exception.getMessage()));
		return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
	}
}

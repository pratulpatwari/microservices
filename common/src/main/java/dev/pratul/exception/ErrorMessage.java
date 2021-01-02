package dev.pratul.exception;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {

	private ZonedDateTime timestamp;
	private int status;
	private List<String> errors;
}

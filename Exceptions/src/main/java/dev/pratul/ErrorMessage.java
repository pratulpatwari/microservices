package dev.pratul;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ErrorMessage {

	private ZonedDateTime timestamp;
	private int status;
	private List<String> errors;
}

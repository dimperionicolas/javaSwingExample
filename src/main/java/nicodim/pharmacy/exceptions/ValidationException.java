package main.java.nicodim.pharmacy.exceptions;

//Excepciones específicas que heredan de BusinessException
public class ValidationException extends BusinessException {
	public ValidationException(String message) {
		super(message, ErrorCode.VALIDATION_ERROR);
	}
}

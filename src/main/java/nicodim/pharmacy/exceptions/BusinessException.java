package main.java.nicodim.pharmacy.exceptions;

//Excepción base para errores de negocio
public class BusinessException extends Exception {
	private static final long serialVersionUID = 1L;
	private final ErrorCode errorCode;

	public BusinessException(String message) {
		super(message);
		this.errorCode = ErrorCode.GENERAL_ERROR;
	}

	public BusinessException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}

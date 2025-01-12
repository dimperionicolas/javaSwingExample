package main.java.nicodim.pharmacy.exceptions;

//Enumeración de códigos de error
public enum ErrorCode {
	GENERAL_ERROR(1000), VALIDATION_ERROR(1001), DATABASE_ERROR(1002), DUPLICATE_ENTITY(1003), ENTITY_NOT_FOUND(1004),
	INSUFFICIENT_PERMISSIONS(1005);

	private final int code;

	ErrorCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}

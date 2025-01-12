package main.java.nicodim.pharmacy.exceptions;

public class EntityNotFoundException extends BusinessException {
	public EntityNotFoundException(String entityName) {
		super("No se encontró el registro de " + entityName, ErrorCode.ENTITY_NOT_FOUND);
	}
}

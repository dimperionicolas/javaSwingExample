package main.java.nicodim.pharmacy.utils;

public enum UserRole {
	ADMIN("Administrador", 1), EMPLOYEE("Empleado", 2), CUSTOMER("Cliente", 3);

	private final String displayName;
	private final int level;

	UserRole(String displayName, int level) {
		this.displayName = displayName;
		this.level = level;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getLevel() {
		return level;
	}
}
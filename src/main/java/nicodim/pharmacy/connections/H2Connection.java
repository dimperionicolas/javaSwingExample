package main.java.nicodim.pharmacy.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2Connection {

	private static final String JDBC_URL = "jdbc:h2:./H2_pharmacy_database/pharmacy_database;DB_CLOSE_DELAY=-1";
	private static final String JDBC_USER = "sa";
	private static final String JDBC_PASSWORD = "";

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
	}
}

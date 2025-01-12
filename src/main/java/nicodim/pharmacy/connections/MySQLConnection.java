package main.java.nicodim.pharmacy.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import main.java.nicodim.pharmacy.utils.ResourceLoader;

public class MySQLConnection {
	// Este es el método que se encarga de conectar a la base de datos

	private static final Map<String, String> envVariables;
	private static final String DATABASE_NAME;
	private static final String USER;
	private static final String PASSWORD;
	private static final String URL;
	private static final String HOST;
	private static final String PORT;

	static {
		envVariables = ResourceLoader.loadEnvVariables();

		DATABASE_NAME = envVariables.get("MYSQL_DATABASE");
		HOST = envVariables.get("MYSQL_HOST");
		PORT = envVariables.get("MYSQL_PORT");

		USER = envVariables.get("MYSQL_USER");
		PASSWORD = envVariables.get("MYSQL_ROOT_PASSWORD");

		URL = String.format("jdbc:mysql://%s:%s/%s", HOST, PORT, DATABASE_NAME);
	}

	Connection conn = null;

	public Connection getConnection() {
		try {
			// Carga la clase del controlador de MySQL en la JVM
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			System.err.print("Ha ocurrido un ClassNotFoundException " + e.getMessage());
		} catch (SQLException e) {
			System.err.print("Ha ocurrido un SQLException " + e.getMessage());
		}
		return conn;
	}

}

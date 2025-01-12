package main.java.nicodim.pharmacy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import main.java.nicodim.pharmacy.connections.DatabaseConnection;
import main.java.nicodim.pharmacy.connections.H2Connection;
import main.java.nicodim.pharmacy.connections.MySQLConnection;
import main.java.nicodim.pharmacy.views.LoginView;

public class Main {
	public static void main(String[] args) {

		LoginView login = new LoginView();
		login.setVisible(true);

		// checkMySQLConnection();
		// checkH2Connection();
		// checkDBHikariConnection();
		// DatabaseConnection.close();
	}

	private static void checkMySQLConnection() {

		MySQLConnection connectionMySQL = new MySQLConnection();
		// Prueba de conexion basica.
		Connection connection = null;
		try {
			// Obtener la conexión
			connection = connectionMySQL.getConnection();

			// Verificar si la conexión no es nula
			if (connection != null) {
				System.out.println("Conexión exitosa!");
				// Ejecutar una consulta simple (por ejemplo, listar las tablas de la base de
				// datos)
				String query = "SHOW TABLES";
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query);

				System.out.println("Tablas en la base de datos:");
				while (resultSet.next()) {
					// Imprime el nombre de cada tabla
					System.out.println(resultSet.getString(1));
				}
			} else {
				System.out.println("La conexión ha fallado.");
			}
		} catch (Exception e) {
			System.err.println("Error al probar la conexión: " + e.getMessage());
		} finally {
			// Cerrar la conexión
			try {
				if (connection != null) {
					connection.close();
					System.out.println("Conexión cerrada.");
				}
			} catch (Exception e) {
				System.err.println("Error al cerrar la conexión: " + e.getMessage());
			}
		}
	}

	private static void checkDBHikariConnection() {
		try (Connection connection = DatabaseConnection.getConnection()) {
			if (connection != null) {
				System.out.println("Conexión exitosa con HikariCP.");
			}
		} catch (SQLException e) {
			DatabaseConnection.close();
			System.out.println("Error al conectar a la base de datos: " + e.getMessage());
		}

	}

	private static void checkH2Connection() {
		try (Connection connection = H2Connection.getConnection()) {
			if (connection != null) {
				System.out.println("Conexión exitosa a H2.");
			}
		} catch (SQLException e) {
			System.out.println("Error al conectar a la base de datos: " + e.getMessage());
		}

	}

}

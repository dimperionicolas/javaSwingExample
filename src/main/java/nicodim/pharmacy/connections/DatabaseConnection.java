package main.java.nicodim.pharmacy.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import main.java.nicodim.pharmacy.utils.ResourceLoader;

public class DatabaseConnection {
	private static HikariDataSource dataSource;

	private static final Map<String, String> envVariables;
	private static final String DATABASE_NAME;
	private static final String USER;
	private static final String PASSWORD;
	private static final String URL;
	private static final String HOST;
	private static final String PORT;

	Connection conn = null;

	static {
		try {
			envVariables = ResourceLoader.loadEnvVariables();

			DATABASE_NAME = envVariables.get("MYSQL_DATABASE");
			HOST = envVariables.get("MYSQL_HOST");
			PORT = envVariables.get("MYSQL_PORT");

			USER = envVariables.get("MYSQL_USER");
			PASSWORD = envVariables.get("MYSQL_PASSWORD");
			// PASSWORD = envVariables.get("MYSQL_ROOT_PASSWORD"); // Sin setear

			String dbDriver = "com.mysql.cj.jdbc.Driver";

			boolean useMySQL = true; // Cambiar a 'false' para usar H2
			String baseUrl = "";
			if (useMySQL) {
				baseUrl = String.format("jdbc:mysql://%s:%s", HOST, PORT);
				// URL completa con la base de datos
				URL = baseUrl + "/" + DATABASE_NAME;
			} else {
				URL = String.format("jdbc:h2:./H2_pharmacy_database/%s;DB_CLOSE_DELAY=-1", DATABASE_NAME);
				dbDriver = "org.h2.Driver"; // Para H2
			}

			// Primero intentamos crear la base de datos si no existe
			createDatabaseIfNotExists(baseUrl);

			// Configuración del Pool de Conexiones

			// Configuración del Pool de Conexiones
			HikariConfig config = new HikariConfig();
			config.setJdbcUrl(URL);
			config.setUsername(USER);
			config.setPassword(PASSWORD);
			config.setDriverClassName(dbDriver);
			config.setMaximumPoolSize(5); // Tamaño del pool
			config.setMinimumIdle(2); // Conexiones inactivas mínimas
			config.setIdleTimeout(30000); // Tiempo máximo de inactividad de las conexiones
			config.setConnectionTimeout(30000); // Tiempo máximo para obtener una conexión del pool

			try {
				dataSource = new HikariDataSource(config);
				initializeDatabase();
			} catch (Exception e) {
				System.err.println("Error initializing connection pool: " + e.getMessage());
				throw new RuntimeException("Failed to initialize connection pool", e);
			}
		} catch (Exception e) {
			System.err.println("Critical error during database initialization: " + e.getMessage());
			close();
			throw new RuntimeException("Database initialization failed", e);
		}
	}

	public static Connection getConnection() throws SQLException {
		if (dataSource == null) {
			throw new SQLException("Database connection pool has not been initialized");
		}
		return dataSource.getConnection();
	}

	public static void close() {
		if (dataSource != null) {
			dataSource.close();
		}
	}

	private static void createDatabaseIfNotExists(String baseUrl) {
		try (Connection conn = DriverManager.getConnection(baseUrl, USER, PASSWORD);
				Statement stmt = conn.createStatement()) {

			String createDbSQL = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
			stmt.executeUpdate(createDbSQL);
			System.out.println("Database created or verified successfully");

		} catch (SQLException e) {
			System.err.println("Error creating database: " + e.getMessage());
			throw new RuntimeException("Failed to create database", e);
		}
	}

	private static void initializeDatabase() {
		try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {

			// Obtenemos el catálogo actual
			String catalog = conn.getCatalog();
			System.out.println("Current database catalog: " + catalog);

			// Verificamos las tablas existentes de manera más específica
			var rs = conn.getMetaData().getTables(catalog, // catalog - nombre de la base de datos
					null, // schema pattern
					null, // table name pattern - null para obtener todas
					new String[] { "TABLE" } // tipos de tabla - solo queremos tablas normales
			);

			// Creamos una lista de las tablas encontradas para debugging
			boolean tablesExist = false;
			System.out.println("Checking existing tables:");
			while (rs.next()) {
				tablesExist = true;
				String tableName = rs.getString("TABLE_NAME");
				System.out.println("Found table: " + tableName);
			}

			if (!tablesExist) {
				System.out.println("No tables found. Initializing schema...");
				// Leer y ejecutar schema.sql
				try {
					String schemaSql = ResourceLoader.loadSQLFile("Schema.sql");

					// Ejecutamos cada statement SQL por separado
					String[] statements = schemaSql.split(";");
					for (String statement : statements) {
						if (!statement.trim().isEmpty()) {
							System.out.println("Executing SQL statement: " + statement.trim());
							stmt.execute(statement.trim());
						}
					}
					System.out.println("Database schema initialized successfully");
				} catch (Exception e) {
					System.err.println("Error loading or executing schema: " + e.getMessage());
					close();
					throw e;
				}
			} else {
				System.out.println("Existing tables found. Skipping schema initialization.");
			}
		} catch (Exception e) {
			close();
			System.err.println("Error initializing database schema: " + e.getMessage());
			e.printStackTrace(); // Añadimos stack trace completo para debugging
			throw new RuntimeException("Error initializing database schema", e);
		}
	}
}

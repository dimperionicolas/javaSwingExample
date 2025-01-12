package main.java.nicodim.pharmacy.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.ImageIcon;

public class ResourceLoader {

	private static final String IMAGE_PATH = "resources/images/";
	private static final String ENV_FILE_PATH = "src/.env";
	private static final String DATABASE_PATH = "resources/sql/";

	public static ImageIcon loadImage(String fileName) {
		String fullPath = IMAGE_PATH + fileName;
		return new ImageIcon(ResourceLoader.class.getClassLoader().getResource(fullPath));
	}

	public static InputStream loadResources(String string) {
		// No tiene funcionalidad por ahora.
		try (InputStream input = ResourceLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
			if (input != null) {
				return input;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Deprecated
	/**
	 * Carga variables de entorno desde un archivo.env en una Map. No funciona para
	 * cargar variables de entorno desde un path con formato string. Al comprimir en
	 * un jar, las rutas no son correctas y se debe usar el getResourceAsStream()
	 * para cargarlos. #loadEnvVariables()
	 * 
	 * @return Map de valores de entorno.
	 */
	public static Map<String, String> loadEnvVariablesStringPath() {
		Map<String, String> envVariables = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(ENV_FILE_PATH))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty() || line.startsWith("#")) {
					continue; // Salta comentarios o líneas vacías
				}
				String[] parts = line.split("=", 2);
				if (parts.length == 2) {
					envVariables.put(parts[0].trim(), parts[1].trim());
				}
				// System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return envVariables;
	}

	public static Map<String, String> loadEnvVariables() {
		Map<String, String> envVariables = new HashMap<>();
		try (InputStream inputStream = ResourceLoader.class.getResourceAsStream("/.env")) {
			if (inputStream == null) {
				throw new IllegalStateException("No se pudo encontrar el archivo .env");
			}

			// Leer contenido del archivo .env
			Scanner scanner = new Scanner(inputStream);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.trim().isEmpty() || line.startsWith("#")) {
					continue; // Salta comentarios o líneas vacías
				}
				// System.out.println(line);
				String[] parts = line.split("=", 2);
				if (parts.length == 2) {
					envVariables.put(parts[0].trim(), parts[1].trim());
				}

			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return envVariables;
	}

	public static String laoadSQLFile(String fileName) {
		String fullPath = DATABASE_PATH + fileName;

		try (InputStream inputStream = ResourceLoader.class.getClassLoader().getResource(fullPath).openStream()) {
			if (inputStream == null) {
				throw new IllegalStateException("No se pudo encontrar el archivo SQL: " + fileName);
			}
			return new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error cargando archivo SQL: " + fileName, e);
		}
	}

	public static String loadSQLFile(String fileName) {
		String fullPath = DATABASE_PATH + fileName;

		try (InputStream inputStream = ResourceLoader.class.getClassLoader().getResource(fullPath).openStream()) {
			if (inputStream == null) {
				throw new IllegalStateException("No se pudo encontrar el archivo SQL: " + fileName);
			}
			return new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error cargando archivo SQL: " + fileName, e);
		}
	}

}

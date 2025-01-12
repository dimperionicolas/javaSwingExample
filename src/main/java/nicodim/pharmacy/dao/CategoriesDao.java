package main.java.nicodim.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import main.java.nicodim.pharmacy.connections.DatabaseConnection;
import main.java.nicodim.pharmacy.models.Categories;

public class CategoriesDao {

	public boolean registerCategoryQuery(Categories category) {
		String query = "INSERT INTO categories (name, created, updated)" + "VALUES(?,?,?)";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setString(1, category.getName());
			pst.setTimestamp(2, timestamp);
			pst.setTimestamp(3, timestamp);
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al registtrar la categoria: " + e.toString());
			return false;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
	}

	public List<Categories> listCategoriesQuery(String value) {
		List<Categories> list_categories = new ArrayList<>();
		String query = "SELECT * FROM categories";
		String query_search_categories = "SELECT * FROM categories WHERE name LIKE '%" + value + "%'";
		if (!value.equalsIgnoreCase("")) {
			query = query_search_categories;
		}
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query)) {
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					Categories category = new Categories();
					category.setId(rs.getInt("id"));
					category.setName(rs.getString("name"));
					list_categories.add(category);
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener las categorias: " + e.toString());
			return List.of();
		}
		return list_categories;
	}

	public boolean updateCategoryQuery(Categories category) {
		String query = "UPDATE categories SET name = ?, updated = ? WHERE id = ?";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query)) {
			pst.setString(1, category.getName());
			pst.setTimestamp(2, timestamp);
			pst.setInt(3, category.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al modificar los datos de la categoria: " + e.toString());
			return false;
		}
	}

	public boolean deleteCategoryQuery(int id) {
		String query = "DELETE FROM categories WHERE id = " + id;
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query)) {
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,
					"No puedes eliminar una categoria que tenga relacion con otra tabla: " + e.toString());
			return false;
		}
	}

	public boolean exists(int id) {
		return false;
	}
}

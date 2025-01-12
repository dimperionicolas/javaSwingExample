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
import main.java.nicodim.pharmacy.models.Products;

public class ProductsDao {

	public boolean registerProductQuery(Products product) {
		String query = "INSERT INTO products (code, name, description, unit_price, created, updated, category_id)"
				+ "VALUES(?,?,?,?,?,?,?)";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {

			pst.setInt(1, product.getCode());
			pst.setString(2, product.getName());
			pst.setString(3, product.getDescription());
			pst.setDouble(4, product.getUnit_price());
			pst.setTimestamp(5, timestamp);
			pst.setTimestamp(6, timestamp);
			pst.setInt(7, product.getCategory_id());
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al registtrar el producto: " + e.toString());
			return false;
		}
	}

	public List<Products> listProductsQuery(String value) {
		List<Products> list_products = new ArrayList<>();
		String query = "SELECT pro.*, ca.name AS category_name FROM products pro, categories ca "
				+ "WHERE pro.category_id = ca.id";
		String query_search_products = "SELECT pro.*, ca.name AS category_name FROM products pro INNER JOIN categories ca "
				+ " ON pro.category_id = ca.id WHERE pro.name LIKE '%" + value + "%'";
		if (!value.equalsIgnoreCase("")) {
			query = query_search_products;
		}
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			try (ResultSet rs = pst.executeQuery()) {

				while (rs.next()) {
					Products products = new Products();
					products.setId(rs.getInt("id"));
					products.setCode(rs.getInt("code"));
					products.setName(rs.getString("name"));
					products.setDescription(rs.getString("description"));
					products.setUnit_price(rs.getDouble("unit_price"));
					products.setProduct_quantity(rs.getInt("product_quantity"));
					products.setCategory_name(rs.getString("category_name"));
					list_products.add(products);
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener los productos: " + e.toString());
			return List.of();
		}
		return list_products;
	}

	public boolean updateProductQuery(Products product) {
		String query = "UPDATE products SET code = ?, name = ?, description = ?, unit_price = ?, updated = ?, category_id = ? WHERE id = ?";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setInt(1, product.getCode());
			pst.setString(2, product.getName());
			pst.setString(3, product.getDescription());
			pst.setDouble(4, product.getUnit_price());
			pst.setTimestamp(5, timestamp);
			pst.setInt(6, product.getCategory_id());
			pst.setInt(7, product.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al modificar el producto: " + e.toString());
			return false;
		}
	}

	public boolean deleteProductQuery(int id) {
		String query = "DELETE FROM products WHERE id = " + id;
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,
					"No puedes eliminar un producto que tenga relacion con otra tabla: " + e.toString());
			return false;
		}
	}

	// Buscar producto por id
	public Products searchProduct(int id) {
		String query = "SELECT pro.*, ca.name AS category_name " + "FROM products pro INNER JOIN categories ca "
				+ "ON pro.category_id = ca.id " + "WHERE pro.id = ? ";
		Products product = new Products();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setInt(1, id);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					product.setId(rs.getInt("id"));
					product.setCode(rs.getInt("code"));
					product.setName(rs.getString("name"));
					product.setDescription(rs.getString("description"));
					product.setUnit_price(rs.getDouble("unit_price"));
					// product.setProduct_quantity(rs.getInt("product_quantity"));
					product.setCategory_id(rs.getInt("category_id"));
					product.setCategory_name(rs.getString("category_name"));
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener los productos: " + e.toString());
		}
		return product;
	}

	// Buscar producto por codigo
	public Products searchCode(int code) {
		String query = "SELECT pro.id, pro.name, pro.product_quantity FROM products pro WHERE code = ? ";
		Products product = new Products();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {

			pst.setInt(1, code);
			try (ResultSet rs = pst.executeQuery()) {

				if (rs.next()) {
					product.setId(rs.getInt("id"));
					product.setName(rs.getString("name"));
					product.setProduct_quantity(rs.getInt("product_quantity"));

				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener los productos: " + e.toString());
		}
		return product;
	}

	// Buscar producto por codigo
	public Products searchId(int id) {
		String query = "SELECT pro.product_quantity FROM products pro WHERE pro.id = ? ";
		Products product = new Products();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {

			pst.setInt(1, id);
			try (ResultSet rs = pst.executeQuery()) {
				if (rs.next()) {
					product.setProduct_quantity(rs.getInt("product_quantity"));
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener cantidad de producto: " + e.toString());
		}
		return product;

	}

	public boolean updateStockQuery(int ammount, int product_id) {
		String query = "UPDATE products SET  product_quantity = ? WHERE id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {

			pst.setInt(1, ammount);
			pst.setInt(2, product_id);
			pst.execute();
			// TODO un update de cantidad debe actualizar el last update?
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al actualizar cantidad del producto: " + e.toString());
			return false;
		}

	}

	public boolean exists(int id) {
		return false;
	}

}

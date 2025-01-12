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
import main.java.nicodim.pharmacy.models.Suppliers;

public class SuppliersDao {

	public boolean registerSupplierQuery(Suppliers supplier) {
		String query = "INSERT INTO suppliers (name, description, address, telephone, email, city, created, updated)"
				+ "VALUES(?,?,?,?,?,?,?,?)";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setString(1, supplier.getName());
			pst.setString(2, supplier.getDescription());
			pst.setString(3, supplier.getAddress());
			pst.setString(4, supplier.getTelephone());
			pst.setString(5, supplier.getEmail());
			pst.setString(6, supplier.getCity());
			pst.setTimestamp(7, timestamp);
			pst.setTimestamp(8, timestamp);
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al registtrar el proveedor: " + e.toString());
			return false;
		}
	}

	public List<Suppliers> listSuppliersQuery(String value) {
		List<Suppliers> list_suppliers = new ArrayList<>();
		String query = "SELECT * FROM suppliers";
		String query_search_supplierr = "SELECT * FROM suppliers WHERE name LIKE '%" + value + "%'";
		if (!value.equalsIgnoreCase("")) {
			query = query_search_supplierr;
		}
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			try (ResultSet rs = pst.executeQuery();) {
				while (rs.next()) {
					Suppliers supplier = new Suppliers();
					supplier.setId(rs.getInt("id"));
					supplier.setName(rs.getString("name"));
					supplier.setDescription(rs.getString("description"));
					supplier.setAddress(rs.getString("address"));
					supplier.setTelephone(rs.getString("telephone"));
					supplier.setEmail(rs.getString("email"));
					supplier.setCity(rs.getString("city"));
					list_suppliers.add(supplier);
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener proveedores: " + e.toString());
			return List.of();
		}
		return list_suppliers;
	}

	public boolean updateSupplierQuery(Suppliers supplier) {
		String query = "UPDATE suppliers SET name = ?, description = ?, address = ?, telephone = ?, email = ?, city = ?, updated = ? WHERE id = ?";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setString(1, supplier.getName());
			pst.setString(2, supplier.getDescription());
			pst.setString(3, supplier.getAddress());
			pst.setString(4, supplier.getTelephone());
			pst.setString(5, supplier.getEmail());
			pst.setString(6, supplier.getCity());
			pst.setTimestamp(7, timestamp);
			pst.setInt(8, supplier.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al modificar los datos del proveedor: " + e.toString());
			return false;
		}
	}

	public boolean deleteSupplierQuery(int id) {
		String query = "DELETE FROM suppliers WHERE id = " + id;
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,
					"No puedes eliminar un proveedor que tenga relacion con otra tabla: " + e.toString());
			return false;
		}
	}
}

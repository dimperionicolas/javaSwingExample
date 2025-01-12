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
import main.java.nicodim.pharmacy.models.Purchases;

public class PurchasesDao {

	public boolean registerPurchaseQuery(int supplier_id, int employee_id, double total) {
		String query = "INSERT INTO purchases (supplier_id, employee_id, total, created)" + "VALUES(?,?,?,?)";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setInt(1, supplier_id);
			pst.setInt(2, employee_id);
			pst.setDouble(3, total);
			pst.setTimestamp(4, timestamp);
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al registtrar compra: " + e.toString());
			return false;
		}
	}

	public boolean registerPurchaseDetailQuery(int purchase_id, double purchase_price, int purchase_amount,
			double purchase_subtotal, int product_id) {
		String query = "INSERT INTO purchases_details " + "(purchase_id,  purchase_price, purchase_amount, "
				+ "purchase_subtotal, product_id) VALUES(?,?,?,?,?)";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setInt(1, purchase_id);
			pst.setDouble(2, purchase_price);
			pst.setInt(3, purchase_amount);
			pst.setDouble(4, purchase_subtotal);
			pst.setInt(5, product_id);
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al registrar los detalles de la compra: " + e.toString());
			return false;
		}
	}

	// Obtener el id de la compra
	public int purchaseId() {
		int id = 0;
		String query = "SELECT MAX(id) AS id FROM purchases";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			try (ResultSet rs = pst.executeQuery();) {
				if (rs.next()) {
					id = rs.getInt("id");
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener max id: " + e.toString());
		}
		return id;
	}

	// Listar todas las compras realizadas
	public List<Purchases> listAllPurchasesQuery() {
		List<Purchases> list_purchases = new ArrayList<>();
		String query = "SELECT pu.*, su.name AS suppliers_name FROM purchases pu, suppliers su "
				+ "WHERE pu.supplier_id = su.id ORDER BY pu.id ASC";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			try (ResultSet rs = pst.executeQuery();) {
				while (rs.next()) {
					Purchases purchase = new Purchases();
					purchase.setId(rs.getInt("id"));
					purchase.setSupplier_name_product(rs.getString("suppliers_name"));
					purchase.setTotal(rs.getDouble("total"));
					purchase.setCreated(rs.getString("created"));
					list_purchases.add(purchase);
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener lista de compras: " + e.toString());
		}
		return list_purchases;
	}

	// Listar compras para emitir factura en Print()
	public List<Purchases> listPurchaseDetailQuery(int id) {
		List<Purchases> list_purchases = new ArrayList<>();
		String query = "" + "SELECT " + "pu.created, "
				+ "pude.purchase_price, pude.purchase_amount, pude.purchase_subtotal, " + "su.name AS suppliers_name, "
				+ "pro.name AS product_name, " + "em.full_name " + "FROM purchases pu "
				+ "INNER JOIN purchases_details pude ON pu.id = pude.purchase_id "
				+ "INNER JOIN products pro ON pude.product_id = pro.id "
				+ "INNER JOIN suppliers su ON pu.supplier_id = su.id "
				+ "INNER JOIN employees em ON pu.employee_id = em.id " + "WHERE pu.id = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {

			pst.setInt(1, id);
			try (ResultSet rs = pst.executeQuery();) {
				while (rs.next()) {
					Purchases purchase = new Purchases();
					purchase.setProduct_name(rs.getString("product_name"));
					purchase.setPurchase_amount(rs.getInt("purchase_amount"));
					purchase.setPurchase_price(rs.getDouble("purchase_price"));
					purchase.setPurchase_subtotal(rs.getDouble("purchase_subtotal"));
					purchase.setSupplier_name_product(rs.getString("suppliers_name"));
					purchase.setCreated(rs.getString("created"));
					purchase.setPurchaser(rs.getString("full_name"));
					list_purchases.add(purchase);
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener detalle de compras: " + e.toString());
		}
		return list_purchases;
	}

}

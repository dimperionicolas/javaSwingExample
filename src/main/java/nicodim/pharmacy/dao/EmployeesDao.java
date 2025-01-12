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
import main.java.nicodim.pharmacy.models.Employees;

public class EmployeesDao {

	public Employees loginQuery(String user, String password) {
		String query = "SELECT * FROM employees WHERE username = ? AND password = ?";
		Employees employee = new Employees();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {

			pst.setString(1, user);
			pst.setString(2, password);
			try (ResultSet rs = pst.executeQuery()) {

				if (rs.next()) {
					// TODO esto es optimo?
					employee.setId(rs.getInt("id"));
					employee.setFull_name(rs.getString("full_name"));
					employee.setUsername(rs.getString("username"));
					employee.setAddress(rs.getString("address"));
					employee.setTelephone(rs.getString("telephone"));
					employee.setEmail(rs.getString("email"));
					employee.setRol(rs.getString("rol"));
				}
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener empleado: " + e.toString());
		}
		return employee;
	}

	public boolean registerEmployeeQuery(Employees employee) {
		String query = "INSERT INTO employees (id, full_name, username, address, telephone, email, password, rol, created, updated)"
				+ "VALUES(?,?,?,?,?,?,?,?,?,?)";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setInt(1, employee.getId());
			pst.setString(2, employee.getFull_name());
			pst.setString(3, employee.getUsername());
			pst.setString(4, employee.getAddress());
			pst.setString(5, employee.getTelephone());
			pst.setString(6, employee.getEmail());
			pst.setString(7, employee.getPassword());
			pst.setString(8, employee.getRol());
			pst.setTimestamp(9, timestamp);
			pst.setTimestamp(10, timestamp);
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al registtrar los empleados: " + e.toString());
			return false;
		}
	}

	public List<Employees> listEmployeesQuery(String value) {
		List<Employees> list_employees = new ArrayList<>();
		String query = "SELECT * FROM employees ORDER BY rol ASC";
		String query_search_employees = "SELECT * FROM employees WHERE id LIKE '%" + value + "%'";
		if (!value.equalsIgnoreCase("")) {
			query = query_search_employees;
		}

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			try (ResultSet rs = pst.executeQuery()) {

				while (rs.next()) {
					Employees employee = new Employees();
					employee.setId(rs.getInt("id"));
					employee.setFull_name(rs.getString("full_name"));
					employee.setUsername(rs.getString("username"));
					employee.setAddress(rs.getString("address"));
					employee.setTelephone(rs.getString("telephone"));
					employee.setEmail(rs.getString("email"));
					employee.setRol(rs.getString("rol"));
					list_employees.add(employee);
				}
			}
		} catch (

		SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al obtener empleados: " + e.toString());
			return List.of();
		}
		return list_employees;
	}

	public boolean updateEmployeeQuery(Employees employee) {
		String query = "UPDATE employees SET full_name = ?, username = ?, address = ?, telephone = ?, email = ?,  rol = ?,  updated = ? WHERE id = ?";
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setString(1, employee.getFull_name());
			pst.setString(2, employee.getUsername());
			pst.setString(3, employee.getAddress());
			pst.setString(4, employee.getTelephone());
			pst.setString(5, employee.getEmail());
			pst.setString(6, employee.getRol());
			pst.setTimestamp(7, timestamp);
			pst.setInt(8, employee.getId());
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al modificar los datos del empleado: " + e.toString());
			return false;
		}
	}

	public boolean deleteEmployeeQuery(int id) {
		String query = "DELETE FROM employees WHERE id = " + id;
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.execute();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null,
					"No puedes eliminar un empleado que tenga relacion con otra tabla: " + e.toString());
			return false;
		}
	}

	public boolean updateEmployeePasswordQuery(Employees employee) {
		String username = employee.getUsername();
		String query = "UPDATE employees SET password = ? WHERE username = '" + username + "'";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement pst = conn.prepareStatement(query);) {
			pst.setString(1, employee.getPassword());
			pst.executeUpdate();
			return true;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error al modificar los datos del empleado: " + e.toString());
			return false;
		}
	}

	public boolean exists(int id) {
		return false;
	}
}

package main.java.nicodim.pharmacy.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import main.java.nicodim.pharmacy.dao.EmployeesDao;
import main.java.nicodim.pharmacy.models.Employees;
import main.java.nicodim.pharmacy.views.DynamicSystemView;
import main.java.nicodim.pharmacy.views.LoginView;

public class LoginController implements ActionListener {

	public static Employees employee;
	private EmployeesDao employees_dao;
	private LoginView login_view;
	// TODO podria tener la vista tambien, crear el servicio, y agregar la vista del
	// sistema. ademas, controlar el evento de logOut aca

	public LoginController(Employees employee, EmployeesDao employees_dao, LoginView login_view) {
		this.employees_dao = employees_dao;
		this.login_view = login_view;
		this.login_view.btn_enter.addActionListener(this);
	}

	public static boolean getPermission() {
		if (employee.getRol().equals("Administrador".toUpperCase())) {
			return true;
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Obtener los datos de la vista
		String user = login_view.txt_username.getText().trim();
		String pass = String.valueOf(login_view.txt_password.getPassword());

		if (e.getSource() == login_view.btn_enter) {
			// Validar que los campos no esten vacios
			if (!user.equals("") || !pass.equals("")) {
				// Pasar los parámetros al método login
				employee = employees_dao.loginQuery(user, pass);
				// Verificar la existencia del usuario
				if (employee.getUsername() != null) {
					// TODO implementar interfaz y agregar la vista dynamic
					if (employee.getRol().equals("Administrador".toUpperCase())) {
						DynamicSystemView admin = new DynamicSystemView();
						admin.setVisible(true);
					} else {
						DynamicSystemView aux = new DynamicSystemView();
						aux.setVisible(true);
					}
					this.login_view.dispose();
				} else {
					JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrecto");
				}
			} else {
				JOptionPane.showMessageDialog(null, "Los campso estan vacios");
			}
		}
	}

}

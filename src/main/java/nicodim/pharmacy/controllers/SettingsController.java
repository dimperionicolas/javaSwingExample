package main.java.nicodim.pharmacy.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import main.java.nicodim.pharmacy.controllers.base.BaseController;
import main.java.nicodim.pharmacy.models.Employees;
import main.java.nicodim.pharmacy.views.base.AbstractSystemView;

public class SettingsController extends BaseController {

	public SettingsController(AbstractSystemView view) {
		super(view);
		profile();
	}

	@Override
	protected void initializeListeners() {
		views.jlabel_products.addMouseListener(this);
		views.jlabel_purchases.addMouseListener(this);
		views.jlabel_customers.addMouseListener(this);
		views.jlabel_employees.addMouseListener(this);
		views.jlabel_suppliers.addMouseListener(this);
		views.jlabel_sales.addMouseListener(this);
		views.jlabel_categories.addMouseListener(this);
		views.jlabel_reports.addMouseListener(this);
		views.jlabel_settings.addMouseListener(this);

	}

	// Asignar el perfil del usuario
	public void profile() {
		Employees employee = LoginController.employee;
		this.views.txt_profile_id.setText("" + employee.getId());
		this.views.txt_profile_fullname.setText(employee.getFull_name());
		this.views.txt_profile_address.setText(employee.getAddress());
		this.views.txt_profile_telephone.setText(employee.getTelephone());
		this.views.txt_profile_email.setText(employee.getEmail());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		Object componente = e.getSource();
		if (componente instanceof JLabel) {
			((JLabel) componente).getParent().setBackground(new Color(152, 202, 63));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Object componente = e.getSource();
		if (componente instanceof JLabel) {
			((JLabel) componente).getParent().setBackground(new Color(18, 45, 61));

		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == views.jlabel_settings) {
			views.panel_tab_menu_options.setSelectedIndex(8);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		throw new UnsupportedOperationException("Sin funcionalidad.");
	}

	@Override
	protected void cleanFields() {
		throw new UnsupportedOperationException("Sin funcionalidad.");
	}

}

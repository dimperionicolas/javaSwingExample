package main.java.nicodim.pharmacy.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import main.java.nicodim.pharmacy.controllers.base.BaseController;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.exceptions.ValidationException;
import main.java.nicodim.pharmacy.models.Employees;
import main.java.nicodim.pharmacy.services.EmployeeService;
import main.java.nicodim.pharmacy.views.base.AbstractSystemView;

public class EmployeesController extends BaseController {
	private EmployeeService employeeService;

	public EmployeesController(AbstractSystemView views) {
		super(views);
		this.employeeService = EmployeeService.getInstance();
		listAllEmployees();
	}

	@Override
	protected void initializeListeners() {
		this.views.btn_employee_register.addActionListener(this);
		this.views.btn_employee_update.addActionListener(this);
		this.views.btn_employee_delete.addActionListener(this);
		this.views.btn_employee_cancel.addActionListener(this);
		this.views.btn_profile_modify_data.addActionListener(this);
		this.views.jlabel_employees.addMouseListener(this);
		this.views.employees_table.addMouseListener(this);
		this.views.txt_employee_search.addKeyListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == views.btn_employee_register) {
				handleRegisterEmployee();
			} else if (e.getSource() == views.btn_employee_update) {
				habdleUpdateEmployee();
			} else if (e.getSource() == views.btn_employee_delete) {
				handleDeleteEmployee();
			} else if (e.getSource() == views.btn_employee_cancel) {
				handleCancel();
			} else if (e.getSource() == views.btn_profile_modify_data) {
				handleProfileModifyData();
			}
		} catch (BusinessException ex) {
			switch (ex.getErrorCode()) {
			case DUPLICATE_ENTITY:
				showError("Cliente duplicado", ex.getMessage());
				break;
			case DATABASE_ERROR:
				showError("Error de base de datos", ex.getMessage());
				logError(ex);
				break;
			default:
				showError("Error", ex.getMessage());
			}
		}
	}

	private void handleProfileModifyData() throws BusinessException {
		String password = String.valueOf(views.txt_profile_password_modify.getPassword());
		String confirm_password = String.valueOf(views.txt_profile_modify_confirm.getPassword());
		employeeService.validateAndChangeSelfPassword(password, confirm_password);
		showSuccess("Contraseña modificada con exito.");
	}

	private void handleCancel() {
		cleanFields();
		views.btn_employee_register.setEnabled(true);
		views.txt_employee_password.setEnabled(true);
		views.txt_employee_id.setEnabled(true);
	}

	private void handleDeleteEmployee() throws BusinessException {
		int row = views.employees_table.getSelectedRow();
		int id_user = LoginController.employee.getId();
		int id = Integer.parseInt(views.employees_table.getValueAt(row, 0).toString());
		if (row == -1) {
			throw new BusinessException("Debes seleccionar un empleado para eliminar");
		} else if (id == id_user) {
			throw new BusinessException("Debes seleccionar un empleado distinto al logueado");
		} else {
			if (confirmAction("¿En realidad quieres eliminar a este cliente?")) {
				employeeService.deleteEmployee(id);
				refreshView();
				views.btn_employee_register.setEnabled(true);
				views.txt_employee_password.setEnabled(true);
				showSuccess("Empleado eliminado con éxito");
			}
		}

	}

	private void habdleUpdateEmployee() throws BusinessException {
		if (!validateSelectedEmployeeById() || !validateEmployeeFields()) {
			return;
		}
		Employees employeeFromField = buildEmployeeFromField();
		employeeService.updateEmployee(employeeFromField);
		refreshView();
		views.btn_employee_register.setEnabled(true);
		showSuccess("Datos del empleado modificados con éxito");
	}

	private boolean validateSelectedEmployeeById() {
		if (views.txt_employee_id.getText().equals("")) {
			showValidationError("Seleccione un empleado de la tabla para actualizar");
			return false;
		}
		return true;
	}

	private boolean validateEmployeeFields() {
		return validateRequiredFields(views.txt_employee_id.getText(), views.txt_employee_fullname.getText(),
				views.cmb_employee_rol.getSelectedItem().toString());
	}

	private boolean validateAllEmployeeFields() {
		return validateRequiredFields(views.txt_employee_username.getText(), views.txt_employee_address.getText(),
				views.txt_employee_telephone.getText(), views.txt_employee_email.getText(),
				String.valueOf(views.txt_employee_password.getPassword()));
	}

	private void handleRegisterEmployee() throws BusinessException {
		if (!validateEmployeeFields() || !validateAllEmployeeFields()) {
			return;
		}
		Employees employeeFromField = buildEmployeeFromField();
		employeeService.registerEmployee(employeeFromField);
		refreshView();
		showSuccess("Empleado registrado con exito.");
	}

	private void refreshView() {
		cleanTable();
		cleanFields();
		listAllEmployees();
	}

	private Employees buildEmployeeFromField() throws ValidationException {
		Employees employee = new Employees();
		employee.setId(employeeService.checkAndGetId(views.txt_employee_id.getText().trim()));
		employee.setFull_name(views.txt_employee_fullname.getText().trim());
		employee.setUsername(views.txt_employee_username.getText().trim());
		employee.setAddress(views.txt_employee_address.getText().trim());
		employee.setTelephone(views.txt_employee_telephone.getText().trim());
		employee.setEmail(views.txt_employee_email.getText().trim());
		employee.setPassword(String.valueOf(views.txt_employee_password.getPassword()));
		employee.setRol(views.cmb_employee_rol.getSelectedItem().toString());
		return employee;
	}

	@SuppressWarnings("unchecked")
	private List<Employees> getListForTable() throws BusinessException {
		List<?> listAll = listAllElements(employeeService, views.txt_employee_search.getText());
		if (listAll.isEmpty() || listAll.get(0) instanceof Employees) {
			return (List<Employees>) listAll;
		}
		throw new BusinessException("Error al obtener los elementos");
	}

	public void listAllEmployees() {
		if (LoginController.getPermission()) {
			try {
				List<Employees> list = getListForTable();
				model = (DefaultTableModel) views.employees_table.getModel();
				Object[] row = new Object[7];
				for (int i = 0; i < list.size(); i++) {
					row[0] = list.get(i).getId();
					row[1] = list.get(i).getFull_name();
					row[2] = list.get(i).getUsername();
					row[3] = list.get(i).getAddress();
					row[4] = list.get(i).getTelephone();
					row[5] = list.get(i).getEmail();
					row[6] = list.get(i).getRol();
					model.addRow(row);
					views.employees_table.setModel(model);
				}
			} catch (BusinessException ex) {
				showError("Error", ex.getMessage());
			}
		}
	}

	private boolean validateEmployeeTableRowCells(int row) {
		return validateRequiredRowCells(views.employees_table.getValueAt(row, 0).toString(),
				views.employees_table.getValueAt(row, 1).toString(),
				views.employees_table.getValueAt(row, 2).toString(),
				views.employees_table.getValueAt(row, 3).toString(),
				views.employees_table.getValueAt(row, 4).toString(),
				views.employees_table.getValueAt(row, 5).toString(),
				views.employees_table.getValueAt(row, 6).toString());

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Evento al hacer click sobre un elemento de la tabla
		if (e.getSource() == views.employees_table) {
			int row = views.employees_table.rowAtPoint(e.getPoint());
			if (!validateEmployeeTableRowCells(row)) {
				return;
			}

			views.txt_employee_id.setText(views.employees_table.getValueAt(row, 0).toString());
			views.txt_employee_fullname.setText(views.employees_table.getValueAt(row, 1).toString());
			views.txt_employee_username.setText(views.employees_table.getValueAt(row, 2).toString());
			views.txt_employee_address.setText(views.employees_table.getValueAt(row, 3).toString());
			views.txt_employee_telephone.setText(views.employees_table.getValueAt(row, 4).toString());
			views.txt_employee_email.setText(views.employees_table.getValueAt(row, 5).toString());
			views.cmb_employee_rol.setSelectedItem(views.employees_table.getValueAt(row, 6).toString());
			//
//			String address = !(views.employees_table.getValueAt(row, 3) == null)
//					? views.employees_table.getValueAt(row, 3).toString()
//					: "";
//			String telephone = !(views.employees_table.getValueAt(row, 4) == null)
//					? views.employees_table.getValueAt(row, 4).toString()
//					: "";
//			String email = !(views.employees_table.getValueAt(row, 5) == null)
//					? views.employees_table.getValueAt(row, 5).toString()
//					: "";
//
			// Deshabilitar
			views.txt_employee_id.setEditable(false);
			views.txt_employee_password.setEnabled(false);
			views.btn_employee_register.setEnabled(false);

		} else if (e.getSource() == views.jlabel_employees) {
			if (LoginController.getPermission()) {
				views.panel_tab_menu_options.setSelectedIndex(4);
				refreshView();
			} else {
				views.panel_tab_menu_options.setEnabledAt(4, false);
				views.jlabel_employees.setEnabled(false);
				showError("Error", "No tienes privilegios de administrador para acceder a esta vista");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == views.txt_employee_search) {
			refreshView();
		}
	}

	// Limpiar campos
	public void cleanFields() {
		views.txt_employee_id.setText("");
		views.txt_employee_id.setEditable(true);
		views.txt_employee_fullname.setText("");
		views.txt_employee_username.setText("");
		views.txt_employee_address.setText("");
		views.txt_employee_telephone.setText("");
		views.txt_employee_email.setText("");
		views.txt_employee_password.setText("");
		views.cmb_employee_rol.setSelectedIndex(0);
	}

	public void cleanTable() {
		for (int i = 0; i < model.getRowCount(); i++) {
			model.removeRow(i);
			i = i - 1;
		}
	}

}

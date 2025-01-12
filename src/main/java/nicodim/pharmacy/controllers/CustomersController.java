package main.java.nicodim.pharmacy.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import main.java.nicodim.pharmacy.controllers.base.BaseController;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.exceptions.ValidationException;
import main.java.nicodim.pharmacy.models.Customers;
import main.java.nicodim.pharmacy.services.CustomerService;
import main.java.nicodim.pharmacy.views.base.AbstractSystemView;

public class CustomersController extends BaseController {

	private final CustomerService customerService;

	public CustomersController(AbstractSystemView views) {
		super(views);
		customerService = CustomerService.getInstance();
		listAllCustomers();
	}

	@Override
	protected void initializeListeners() {
		views.btn_customer_register.addActionListener(this);
		views.btn_customer_update.addActionListener(this);
		views.btn_customer_delete.addActionListener(this);
		views.btn_customer_cancel.addActionListener(this);
		views.txt_customer_search.addKeyListener(this);
		views.jlabel_customers.addMouseListener(this);
		views.customers_table.addMouseListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == views.btn_customer_register) {
				handleRegisterCustomer();
			} else if (e.getSource() == views.btn_customer_update) {
				handleUpdateCustomer();
			} else if (e.getSource() == views.btn_customer_delete) {
				handleDeleteCustomer();
			} else if (e.getSource() == views.btn_customer_cancel) {
				handleCancel();
			}
		} catch (BusinessException ex) {
			handleBusinessException(ex);
		}
	}

	private void handleBusinessException(BusinessException ex) {
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

	private void handleCancel() {
		views.btn_customer_register.setEnabled(true);
		refreshView();
	}

	private void handleRegisterCustomer() throws BusinessException {
		if (validateCustomerFields()) {
			Customers customerToRegister = buildCustomerFromFields();
			customerService.registerCustomer(customerToRegister);
			refreshView();
			showSuccess("Cliente registrado con éxito");
		}
	}

	private void handleUpdateCustomer() throws BusinessException {
		if (validateSelectedCustomerById() && validateCustomerFields()) {
			Customers customerToUpdate = buildCustomerFromFields();
			customerToUpdate.setId(Integer.parseInt(views.txt_customer_id.getText().trim()));
			customerService.updateCustomer(customerToUpdate);
			refreshView();
			views.btn_customer_register.setEnabled(true);
			showSuccess("Datos del cliente modificados con éxito");
		}
	}

	private void handleDeleteCustomer() throws BusinessException {
		int row = views.customers_table.getSelectedRow();
		if (row == -1) {
			throw new ValidationException("Debes seleccionar un cliente para eliminar");
		}
		int id = Integer.parseInt(views.customers_table.getValueAt(row, 0).toString());
		if (confirmAction("¿En realidad quieres eliminar a este cliente?")) {
			customerService.deleteCustomer(id);
			refreshView();
			showSuccess("Cliente eliminado con éxito");
		}
	}

	private boolean validateSelectedCustomerById() throws ValidationException {
		if (views.txt_customer_id.getText().equals("")) {
			throw new ValidationException("Seleccione un cliente de la tabla para actualizar");
		}
		return true;
	}

	private Customers buildCustomerFromFields() {
		Customers customer = new Customers();
		customer.setFull_name(views.txt_customer_fullname.getText().trim());
		customer.setAddress(views.txt_customer_address.getText().trim());
		customer.setTelephone(views.txt_customer_telephone.getText().trim());
		customer.setEmail(views.txt_customer_email.getText().trim());
		return customer;
	}

	private boolean validateCustomerFields() {
		return validateRequiredFields(views.txt_customer_fullname.getText(), views.txt_customer_address.getText(),
				views.txt_customer_telephone.getText(), views.txt_customer_email.getText());
	}

	private boolean validateCustomerTableRowCells(int row) {
		return validateRequiredRowCells(views.customers_table.getValueAt(row, 0).toString(),
				views.customers_table.getValueAt(row, 1).toString(),
				views.customers_table.getValueAt(row, 2).toString(),
				views.customers_table.getValueAt(row, 3).toString(),
				views.customers_table.getValueAt(row, 4).toString());

	}

	private void refreshView() {
		cleanTable();
		cleanFields();
		listAllCustomers();
	}

	private void listAllCustomers() {
		try {
			List<Customers> list = getListForTable();
			populateCustomersTable(list);
		} catch (BusinessException ex) {
			showError("Error", ex.getMessage());
		}
	}

	private void populateCustomersTable(List<Customers> customers) {
		model = (DefaultTableModel) views.customers_table.getModel();
		model.setRowCount(0);
		for (Customers customer : customers) {
			model.addRow(new Object[] { customer.getId(), customer.getFull_name(), customer.getAddress(),
					customer.getTelephone(), customer.getEmail() });
		}
		views.customers_table.setModel(model);
	}

	@SuppressWarnings("unchecked")
	private List<Customers> getListForTable() throws BusinessException {
		List<?> listAll = listAllElements(customerService, views.txt_customer_search.getText());
		if (listAll.isEmpty() || listAll.get(0) instanceof Customers) {
			return (List<Customers>) listAll;
		}
		throw new BusinessException("Error al obtener los elementos");
	}

	@Override
	protected void cleanFields() {
		views.txt_customer_id.setText("");
		views.txt_customer_id.setEditable(true);
		views.txt_customer_fullname.setText("");
		views.txt_customer_address.setText("");
		views.txt_customer_telephone.setText("");
		views.txt_customer_email.setText("");
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == views.customers_table) {
			handleCustomerTableClick(e);
		} else if (e.getSource() == views.jlabel_customers) {
			views.panel_tab_menu_options.setSelectedIndex(3);
			refreshView();
		}
	}

	private void handleCustomerTableClick(MouseEvent e) {
		int row = views.customers_table.rowAtPoint(e.getPoint());
		if (validateCustomerTableRowCells(row)) {
			populateFieldsFromSelectedRow(row);
			// Deshabilitar botones
			views.btn_customer_register.setEnabled(false);
			views.txt_customer_id.setEditable(false);
		}
	}

	private void populateFieldsFromSelectedRow(int row) {
		views.txt_customer_id.setText(views.customers_table.getValueAt(row, 0).toString());
		views.txt_customer_fullname.setText(views.customers_table.getValueAt(row, 1).toString());
		views.txt_customer_address.setText(views.customers_table.getValueAt(row, 2).toString());
		views.txt_customer_telephone.setText(views.customers_table.getValueAt(row, 3).toString());
		views.txt_customer_email.setText(views.customers_table.getValueAt(row, 4).toString());
	}

	public void keyReleased(KeyEvent e) {
		if (e.getSource() == views.txt_customer_search) {
			cleanTable();
			listAllCustomers();
		}
	}

}

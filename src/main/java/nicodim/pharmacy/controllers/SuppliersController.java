package main.java.nicodim.pharmacy.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import main.java.nicodim.pharmacy.controllers.base.BaseController;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.exceptions.ValidationException;
import main.java.nicodim.pharmacy.models.Suppliers;
import main.java.nicodim.pharmacy.services.SupplierService;
import main.java.nicodim.pharmacy.utils.DynamicCombobox;
import main.java.nicodim.pharmacy.views.base.AbstractSystemView;

public class SuppliersController extends BaseController {

//	private Suppliers supplier;
	private final SupplierService supplierService;

	public SuppliersController(AbstractSystemView views) {
		super(views);
		supplierService = SupplierService.getInstance();
		getSupplierName();
		listAllSuppliers();

	}

	@Override
	protected void initializeListeners() {
		views.btn_supplier_register.addActionListener(this);
		views.btn_supplier_update.addActionListener(this);
		views.btn_supplier_delete.addActionListener(this);
		views.btn_supplier_cancel.addActionListener(this);
		views.suppliers_table.addMouseListener(this);
		views.txt_suppliers_search.addKeyListener(this);
		views.jlabel_suppliers.addMouseListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == views.btn_supplier_register) {
				handleRegisterSupplier();
			} else if (e.getSource() == views.btn_supplier_update) {
				handleUpdateSupplier();
			} else if (e.getSource() == views.btn_supplier_delete) {
				handleDeleteSupplier();
			} else if (e.getSource() == views.btn_supplier_cancel) {
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
		views.btn_supplier_register.setEnabled(true);
		cleanFields();
	}

	private boolean validateSuppliersrFields() {
		return validateRequiredFields(views.txt_supplier_name.getText(), views.txt_supplier_description.getText(),
				views.txt_supplier_address.getText(), views.txt_supplier_telephone.getText(),
				views.txt_supplier_email.getText(), views.cmb_supplier_city.getSelectedItem().toString());
	}

	private void handleDeleteSupplier() throws BusinessException {
		int row = views.suppliers_table.getSelectedRow();
		if (row == -1) {
			throw new ValidationException("Debes seleccionar un proveedor para eliminar");
		}
		int id = Integer.parseInt(views.suppliers_table.getValueAt(row, 0).toString());
		if (confirmAction("¿En realidad quieres eliminar a este proveedor?")) {
			supplierService.deleteSupplier(id);
			refreshView();
			showSuccess("Proveedor eliminado con éxito");
			views.btn_supplier_register.setEnabled(true);
		}
	}

	private void refreshView() {
		cleanTable();
		cleanFields();
		listAllSuppliers();
	}

	private Suppliers buildSupplierFromFields() {
		Suppliers supplier = new Suppliers();
		supplier.setName(views.txt_supplier_name.getText().trim());
		supplier.setDescription(views.txt_supplier_description.getText().trim());
		supplier.setAddress(views.txt_supplier_address.getText().trim());
		supplier.setTelephone(views.txt_supplier_telephone.getText().trim());
		supplier.setEmail(views.txt_supplier_email.getText().trim());
		supplier.setCity(views.cmb_supplier_city.getSelectedItem().toString());
		return supplier;
	}

	private void handleUpdateSupplier() throws BusinessException {
		if (validateSelectedSupplierById() && validateSuppliersrFields()) {
			Suppliers supplierToUpdate = buildSupplierFromFields();
			supplierToUpdate.setId(Integer.parseInt(views.txt_supplier_id.getText().trim()));
			supplierService.updateSupplier(supplierToUpdate);
			refreshView();
			views.btn_supplier_register.setEnabled(true);
			showSuccess("Datos del cliente modificados con éxito");
		}
	}

	private boolean validateSelectedSupplierById() throws ValidationException {
		if (views.txt_supplier_id.getText().equals("")) {
			throw new ValidationException("Seleccione un cliente de la tabla para actualizar");
		}
		return true;
	}

	private void handleRegisterSupplier() throws BusinessException {
		if (validateSuppliersrFields()) {
			Suppliers supplierToRegister = buildSupplierFromFields();
			supplierService.registerSupplier(supplierToRegister);
			refreshView();
			showSuccess("Proveedor registrado con éxito");
		}
	}

	@SuppressWarnings("unchecked")
	private List<Suppliers> getListForTable() throws BusinessException {
		List<?> listAll = listAllElements(supplierService, views.txt_suppliers_search.getText());
		if (listAll.isEmpty() || listAll.get(0) instanceof Suppliers) {
			return (List<Suppliers>) listAll;
		}
		throw new BusinessException("Error al obtener los elementos");
	}

	// Listar proveedores
	public void listAllSuppliers() {
		if (LoginController.getPermission()) {
			try {
				List<Suppliers> list = getListForTable();
				populateSuppliersTable(list);
			} catch (BusinessException ex) {
				showError("Error", ex.getMessage());
			}
		}
	}

	private void populateSuppliersTable(List<Suppliers> list) {
		model = (DefaultTableModel) views.suppliers_table.getModel();
		Object[] row = new Object[7];
		for (int i = 0; i < list.size(); i++) {
			row[0] = list.get(i).getId();
			row[1] = list.get(i).getName();
			row[2] = list.get(i).getDescription();
			row[3] = list.get(i).getAddress();
			row[4] = list.get(i).getTelephone();
			row[5] = list.get(i).getEmail();
			row[6] = list.get(i).getCity();
			model.addRow(row);
		}
		views.suppliers_table.setModel(model);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == views.suppliers_table) {
			handleSupplierTableClick(e);
		} else if (e.getSource() == views.jlabel_suppliers) {

			if (LoginController.getPermission()) {
				views.panel_tab_menu_options.setSelectedIndex(5);
				refreshView();
			} else {
				views.panel_tab_menu_options.setEnabledAt(6, false);
				views.jlabel_suppliers.setEnabled(false);
				JOptionPane.showMessageDialog(null, "No tienes privilegios de administrador para acceder a esta vista");
			}

		}
	}

	private void handleSupplierTableClick(MouseEvent e) {
		int row = views.customers_table.rowAtPoint(e.getPoint());
		if (validateSupplierTableRowCells(row)) {
			populateFieldsFromSelectedRow(row);
			views.btn_supplier_register.setEnabled(false);
			views.txt_supplier_id.setEditable(false);
		}
	}

	private boolean validateSupplierTableRowCells(int row) {
		return validateRequiredRowCells(views.suppliers_table.getValueAt(row, 0).toString(),
				views.suppliers_table.getValueAt(row, 1).toString(),
				views.suppliers_table.getValueAt(row, 2).toString(),
				views.suppliers_table.getValueAt(row, 3).toString(),
				views.suppliers_table.getValueAt(row, 4).toString(),
				views.suppliers_table.getValueAt(row, 5).toString(),
				views.suppliers_table.getValueAt(row, 6).toString());
	}

	private void populateFieldsFromSelectedRow(int row) {
		views.txt_supplier_id.setText(views.suppliers_table.getValueAt(row, 0).toString());
		views.txt_supplier_name.setText(views.suppliers_table.getValueAt(row, 1).toString());
		views.txt_supplier_description.setText(views.suppliers_table.getValueAt(row, 2).toString());
		views.txt_supplier_address.setText(views.suppliers_table.getValueAt(row, 3).toString());
		views.txt_supplier_telephone.setText(views.suppliers_table.getValueAt(row, 4).toString());
		views.txt_supplier_email.setText(views.suppliers_table.getValueAt(row, 5).toString());
		views.cmb_supplier_city.setSelectedItem(views.suppliers_table.getValueAt(row, 6).toString());

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == views.txt_suppliers_search) {
			cleanTable();
			listAllSuppliers();
		}
	}

	public void cleanTable() {
		for (int i = 0; i < model.getRowCount(); i++) {
			model.removeRow(i);
			i = i - 1;
		}
	}

	public void cleanFields() {
		views.txt_supplier_id.setText("");
		views.txt_supplier_id.setEditable(true);
		views.txt_supplier_name.setText("");
		views.txt_supplier_description.setText("");
		views.txt_supplier_address.setText("");
		views.txt_supplier_telephone.setText("");
		views.txt_supplier_email.setText("");
		views.cmb_supplier_city.setSelectedIndex(0);
	}

	// Método para mostrar el nombre del proveedor
	public void getSupplierName() {
		List<Suppliers> list;
		try {
			list = getListForTable();
			for (int i = 0; i < list.size(); i++) {
				int id = list.get(i).getId();
				String name = list.get(i).getName();
				views.cmb_purchase_supplier.addItem(new DynamicCombobox(id, name));
			}
		} catch (BusinessException ex) {
			handleBusinessException(ex);
		}

	}

}

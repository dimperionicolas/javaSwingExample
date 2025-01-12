package main.java.nicodim.pharmacy.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import main.java.nicodim.pharmacy.controllers.base.BaseController;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.models.Products;
import main.java.nicodim.pharmacy.services.ProductService;
import main.java.nicodim.pharmacy.utils.DynamicCombobox;
import main.java.nicodim.pharmacy.views.base.AbstractSystemView;

public class ProductsController extends BaseController implements ChangeListener {

	private ProductService productService;

	public ProductsController(AbstractSystemView views) {
		super(views);
		this.productService = ProductService.getInstance();
		listAllProducts();
		disablesButtonsForNonAdmin();
	}

	@Override
	protected void initializeListeners() {
		views.btn_product_register.addActionListener(this);
		views.btn_product_update.addActionListener(this);
		views.btn_product_delete.addActionListener(this);
		views.btn_product_cancel.addActionListener(this);
		views.product_table.addMouseListener(this);
		views.panel_tab_menu_options.addChangeListener(this);
		views.txt_product_search.addKeyListener(this);
		views.jlabel_products.addMouseListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == views.btn_product_register) {
				handleRegisterProduct();
			} else if (e.getSource() == views.btn_product_update) {
				handleUpdateProduct();
			} else if (e.getSource() == views.btn_product_delete) {
				handleDeleteProduct();
			} else if (e.getSource() == views.btn_product_cancel) {
				handleCancel();
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

	private void handleCancel() {
		views.btn_product_register.setEnabled(true);
		refreshView();
	}

	private void handleRegisterProduct() throws BusinessException {
		if (!validateProductsFields()) {
			return;
		}
		Products productToRegister = buildProductFromFields();
		productService.registerProduct(productToRegister);
		refreshView();
		showSuccess("Producto registrado con éxito");
	}

	private Products buildProductFromFields() {
		Products product = new Products();
		product.setCode(Integer.parseInt(views.txt_product_code.getText()));
		product.setName(views.txt_product_name.getText().trim());
		product.setDescription(views.txt_product_description.getText().trim());
		product.setUnit_price(Double.parseDouble(views.txt_product_unit_price.getText()));
		DynamicCombobox category_id = (DynamicCombobox) views.cmb_product_category.getSelectedItem();
		product.setCategory_id(category_id.getId());
		return product;
	}

	private boolean validateProductsFields() {
		return validateRequiredFields(views.txt_product_code.getText(), views.txt_product_name.getText(),
				views.txt_product_description.getText(), views.txt_product_unit_price.getText(),
				views.cmb_product_category.getSelectedItem().toString());
	}

	private void handleUpdateProduct() throws BusinessException {
		if (!validateSelectedProductById() || !validateProductsFields()) {
			return;
		}
		Products productToUpdate = buildProductFromFields();
		productToUpdate.setId(Integer.parseInt(views.txt_product_id.getText()));
		productService.updateProduct(productToUpdate);
		refreshView();
		views.btn_product_register.setEnabled(true);
		showSuccess("Datos del producto modificados con éxito");
	}

	private boolean validateSelectedProductById() {
		if (views.txt_product_id.getText().equals("")) {
			showValidationError("Seleccione un producto de la tabla para actualizar");
			return false;
		}
		return true;
	}

	private void handleDeleteProduct() throws BusinessException {
		int row = views.product_table.getSelectedRow();
		if (row == -1) {
			throw new BusinessException("Debes seleccionar un producto para eliminar");
		}
		int id = Integer.parseInt(views.product_table.getValueAt(row, 0).toString());
		if (confirmAction("¿En realidad quieres eliminar este producto?")) {
			productService.deleteProduct(id);
			views.btn_product_register.setEnabled(true);
			refreshView();
			showSuccess("Producto eliminado con éxito");
		}
	}

	private void refreshView() {
		cleanTable();
		cleanFields();
		listAllProducts();
	}

	// Listar productos
	public void listAllProducts() {
		try {
			List<Products> list = getListForTable();
			model = (DefaultTableModel) views.product_table.getModel();
			Object[] row = new Object[7];
			for (int i = 0; i < list.size(); i++) {
				row[0] = list.get(i).getId();
				row[1] = list.get(i).getCode();
				row[2] = list.get(i).getName();
				row[3] = list.get(i).getDescription();
				row[4] = list.get(i).getUnit_price();
				row[5] = list.get(i).getProduct_quantity();
				row[6] = list.get(i).getCategory_name();
				model.addRow(row);
			}
			views.product_table.setModel(model);
		} catch (BusinessException ex) {
			showError("Error", ex.getMessage());
		}
	}

	private void disablesButtonsForNonAdmin() {
		if (!LoginController.getPermission()) {
			views.btn_product_register.setEnabled(false);
			views.btn_product_update.setEnabled(false);
			views.btn_product_delete.setEnabled(false);
			views.btn_product_cancel.setEnabled(false);
			views.txt_product_code.setEnabled(false);
			views.txt_product_description.setEnabled(false);
			views.txt_product_id.setEditable(false);
			views.txt_product_name.setEditable(false);
			views.txt_product_unit_price.setEditable(false);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == views.product_table) {
			int row = views.product_table.rowAtPoint(e.getPoint());
			if (!validateProductTableRowCells(row)) { // TODO revisar que es lo que debo validar
				return;
			}

			views.txt_product_id.setText(views.product_table.getValueAt(row, 0).toString());
			Products product;
			try {
				product = productService.searchProduct(Integer.parseInt(views.txt_product_id.getText()));
				views.txt_product_code.setText("" + product.getCode());
				views.txt_product_name.setText(product.getName());
				views.txt_product_description.setText(product.getDescription());
				views.txt_product_unit_price.setText("" + product.getUnit_price());
				views.cmb_product_category
						.setSelectedItem(new DynamicCombobox(product.getCategory_id(), product.getCategory_name()));
				// Deshabilitar botones
				views.btn_product_register.setEnabled(false);
				views.txt_product_id.setEditable(false);
			} catch (BusinessException ex) {
				showError("Error de base de datos", ex.getMessage());
				logError(ex);

			}
		} else if (e.getSource() == views.jlabel_products) {
			views.panel_tab_menu_options.setSelectedIndex(0);
			refreshView();
		}
	}

	private boolean validateProductTableRowCells(int row) {
		Object valueAt = views.product_table.getValueAt(row, 0);
		if (valueAt == null) {
			return false;
		}
		return true; // TODO si al final se hace una consulta con el id, realmente importa esto?
//		return validateRequiredRowCells(views.product_table.getValueAt(row, 0).toString(),
//				views.product_table.getValueAt(row, 1).toString(),
//				views.product_table.getValueAt(row, 2).toString(),
//				views.product_table.getValueAt(row, 3).toString(),
//				views.product_table.getValueAt(row, 4).toString());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == views.txt_product_search) {
			cleanTable();
			listAllProducts();
		}
	}

	public void cleanTable() {
		for (int i = 0; i < model.getRowCount(); i++) {
			model.removeRow(i);
			i = i - 1;
		}
	}

	public void cleanFields() {
		views.txt_product_id.setText("");
		views.txt_product_code.setText("");
		views.txt_product_name.setText("");
		views.txt_product_description.setText("");
		views.txt_product_unit_price.setText("");
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == views.panel_tab_menu_options && views.panel_tab_menu_options.getSelectedIndex() == 0) {
			refreshView();
		}
	}

	@SuppressWarnings("unchecked")
	private List<Products> getListForTable() throws BusinessException {
		List<?> listAll = listAllElements(productService, views.txt_product_search.getText());
		if (listAll.isEmpty() || listAll.get(0) instanceof Products) {
			return (List<Products>) listAll;
		}
		throw new BusinessException("Error al obtener los elementos");
	}

}

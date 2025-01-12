package main.java.nicodim.pharmacy.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import main.java.nicodim.pharmacy.controllers.base.BaseController;
import main.java.nicodim.pharmacy.exceptions.BusinessException;
import main.java.nicodim.pharmacy.models.Categories;
import main.java.nicodim.pharmacy.services.CategoryService;
import main.java.nicodim.pharmacy.utils.DynamicCombobox;
import main.java.nicodim.pharmacy.views.base.AbstractSystemView;

public class CategoriesController extends BaseController {
	private final CategoryService categoryService;

	public CategoriesController(AbstractSystemView views) {
		super(views);
		this.categoryService = CategoryService.getInstance();
		listAllCategories();
		getCategoryName();
	}

	@Override
	protected void initializeListeners() {
		views.btn_category_register.addActionListener(this);
		views.btn_category_update.addActionListener(this);
		views.btn_category_delete.addActionListener(this);
		views.btn_category_cancel.addActionListener(this);
		views.categories_table.addMouseListener(this);
		views.txt_category_search.addKeyListener(this);
		views.jlabel_categories.addMouseListener(this);
		AutoCompleteDecorator.decorate(views.cmb_product_category);
		// TODO el dao correspondiente no estará seteado aun en este puntos
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == views.btn_category_register) {
				handleRegisterCategory();
			} else if (e.getSource() == views.btn_category_update) {
				handleUpdateCategory();
			} else if (e.getSource() == views.btn_category_delete) {
				handleDeleteCategory();
			} else if (e.getSource() == views.btn_category_cancel) {
				handleCancel();
			}
		} catch (BusinessException ex) {
			switch (ex.getErrorCode()) {
			case DUPLICATE_ENTITY:
				showError("Categoría duplicada", ex.getMessage());
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
		refreshView();
		views.btn_category_register.setEnabled(true);
	}

	private void handleDeleteCategory() throws BusinessException {
		int row = views.categories_table.getSelectedRow();
		if (row == -1) {
			throw new BusinessException("Debes seleccionar una categoría para eliminar");
		}
		int id = Integer.parseInt(views.categories_table.getValueAt(row, 0).toString());
		if (confirmAction("¿En realidad quieres eliminar esta categoría?")) {
			categoryService.deleteCategory(id);
			refreshView();
			views.btn_category_register.setEnabled(true);
			showSuccess("Categpría eliminada con éxito");
		}
	}

	private void handleUpdateCategory() throws BusinessException {
		if (!validateSelectedCategoryById() || !validateCategoryFields()) {
			return;
		}
		Categories categoryToUpdate = buildCategoryFromFields();
		categoryToUpdate.setId(Integer.parseInt(views.txt_category_id.getText().trim()));
		categoryService.updateCategory(categoryToUpdate);
		refreshView();
		views.btn_customer_register.setEnabled(true);
		showSuccess("Datos de la categoría modificadas con éxito");
	}

	private boolean validateSelectedCategoryById() {
		if (views.txt_category_id.getText().equals("")) {
			showValidationError("Seleccione una categoría de la tabla para actualizar");
			return false;
		}
		return true;
	}

	private void handleRegisterCategory() throws BusinessException {
		if (!validateCategoryFields()) {
			return;
		}
		Categories categoryToRegister = buildCategoryFromFields();
		categoryService.registerCategory(categoryToRegister);
		refreshView();
		showSuccess("Categoría registrado con éxito");
	}

	private Categories buildCategoryFromFields() {
		Categories category = new Categories();
		category.setName(views.txt_category_name.getText().trim());
		return category;
	}

	private boolean validateCategoryFields() {
		return validateRequiredFields(views.txt_category_name.getText());
	}

	// Listar categorías
	public void listAllCategories() {
		if (!LoginController.getPermission()) {
			return; // No se muestras las categorias si no es admin
		}
		try {
			List<Categories> list = getListForTable();
			model = (DefaultTableModel) views.categories_table.getModel();
			Object[] row = new Object[2];
			for (int i = 0; i < list.size(); i++) {
				row[0] = list.get(i).getId();
				row[1] = list.get(i).getName();
				model.addRow(row);
			}
			views.categories_table.setModel(model);
		} catch (BusinessException ex) {
			showError("Error", ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private List<Categories> getListForTable() throws BusinessException {
		List<?> listAll = listAllElements(categoryService, views.txt_category_search.getText());
		if (listAll.isEmpty() || listAll.get(0) instanceof Categories) {
			return (List<Categories>) listAll;
		}
		throw new BusinessException("Error al obtener los elementos");
	}

	// Método para mostrar el nombre de las categorías en la pestaña producto
	public void getCategoryName() {
		try {
			List<Categories> list = getListForTable();
			for (int i = 0; i < list.size(); i++) {
				int id = list.get(i).getId();
				String name = list.get(i).getName();
				views.cmb_product_category.addItem(new DynamicCombobox(id, name));
			}
		} catch (BusinessException ex) {
			showError("Error", ex.getMessage());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == views.categories_table) {
			int row = views.categories_table.rowAtPoint(e.getPoint());
			if (!validateCategoriesTableRowCells(row)) {
				return;
			}
			views.txt_category_id.setText(views.categories_table.getValueAt(row, 0).toString());
			views.txt_category_name.setText(views.categories_table.getValueAt(row, 1).toString());
			// Deshabilitar botones
			views.btn_category_register.setEnabled(false);
			views.txt_category_id.setEditable(false);
		} else if (e.getSource() == views.jlabel_categories) {
			if (LoginController.getPermission()) {
				views.panel_tab_menu_options.setSelectedIndex(6);
				refreshView();
			} else {
				views.panel_tab_menu_options.setEnabledAt(5, false);
				views.jlabel_categories.setEnabled(false);
				showValidationError("No tienes privilegios de administrador para acceder a esta vista");
			}
		}
	}

	private boolean validateCategoriesTableRowCells(int row) {
		return validateRequiredRowCells(views.categories_table.getValueAt(row, 0).toString(),
				views.categories_table.getValueAt(row, 1).toString());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == views.txt_category_search) {
			cleanTable();
			listAllCategories();
		}
	}

	public void cleanTable() {
		for (int i = 0; i < model.getRowCount(); i++) {
			model.removeRow(i);
			i = i - 1;
		}
	}

	public void cleanFields() {
		views.txt_category_id.setText("");
		views.txt_category_name.setText("");
	}

	private void refreshView() {
		cleanTable();
		listAllCategories();
		cleanFields();
	}
}

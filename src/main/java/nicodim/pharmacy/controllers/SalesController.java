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
import main.java.nicodim.pharmacy.models.Products;
import main.java.nicodim.pharmacy.models.Sales;
import main.java.nicodim.pharmacy.services.CustomerService;
import main.java.nicodim.pharmacy.services.ProductService;
import main.java.nicodim.pharmacy.services.SaleService;
import main.java.nicodim.pharmacy.views.base.AbstractSystemView;

public class SalesController extends BaseController {

	private SaleService saleService;
	private int item = 0;

	DefaultTableModel temp;

	public SalesController(AbstractSystemView views) {
		super(views);
		this.saleService = SaleService.getInstance();
		listAllSalesOnReportTable();
	}

	@Override
	protected void initializeListeners() {
		views.btn_sale_new.addActionListener(this);
		views.btn_sale_confirm.addActionListener(this);
		views.btn_sale_remove.addActionListener(this);
		views.btn_sale_add_product.addActionListener(this);

		views.jlabel_purchases.addMouseListener(this);
		views.jlabel_reports.addMouseListener(this);
		views.jlabel_sales.addMouseListener(this);
		views.txt_sale_product_code.addKeyListener(this);
		views.txt_sale_customer_id.addKeyListener(this);
		views.txt_sale_quantity.addKeyListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == views.btn_sale_confirm) {
				confirmSale();
			} else if (e.getSource() == views.btn_sale_new) {
				startNewSale();
			} else if (e.getSource() == views.btn_sale_remove) {
				removeProductFromSale();
			} else if (e.getSource() == views.btn_sale_add_product) {
				addProductToSale();
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

	private void removeProductFromSale() {
		model = (DefaultTableModel) views.sales_table.getModel();
		model.removeRow(views.sales_table.getSelectedRow());
		calculateSales();
		views.txt_sale_product_code.requestFocus();
	}

	@SuppressWarnings("unchecked")
	private List<Sales> getListForTable() throws BusinessException {
		List<?> listAll = listAllElements(saleService, "");
		if (listAll.isEmpty() || listAll.get(0) instanceof Sales) {
			return (List<Sales>) listAll;
		}
		throw new BusinessException("Error al obtener los elementos");
	}

	private void verifyNoDuplicateProduct() throws ValidationException {
		for (int i = 0; i < views.sales_table.getRowCount(); i++) {
			if (views.sales_table.getValueAt(i, 1).equals(views.txt_sale_product_name.getText())) {
				throw new ValidationException("El producto ya esta registrado en la tabla de ventas");
			}
		}
	}

	private void addProductToSale() throws ValidationException {
		int amount = Integer.parseInt(views.txt_sale_quantity.getText());
		String product_name = views.txt_sale_product_name.getText();
		double price = Double.parseDouble(views.txt_sale_price.getText());
		int sale_id = Integer.parseInt(views.txt_sale_product_id.getText());
		double subtotal = amount * price;
		int stock = Integer.parseInt(views.txt_sale_stock.getText());
		String full_name = views.txt_sale_customer_name.getText();
		if (stock <= amount) {
			throw new ValidationException("Stock no disponible");
		}
		verifyNoDuplicateProduct();
		addTemporalProductsTotable(amount, product_name, price, sale_id, subtotal, full_name);
		calculateSales();
		cleanFieldsSales();// aca
		views.txt_sale_product_code.requestFocus();
	}

	private void addTemporalProductsTotable(int amount, String product_name, double price, int sale_id, double subtotal,
			String full_name) {
		item = item + 1; // TODO para que?
		temp = (DefaultTableModel) views.sales_table.getModel();
		Object[] obj = new Object[6];
		obj[0] = sale_id;
		obj[1] = product_name;
		obj[2] = amount;
		obj[3] = price;
		obj[4] = subtotal;
		obj[5] = full_name;
		temp.addRow(obj);
		views.sales_table.setModel(temp);
	}

	private void startNewSale() {
		refreshView();
		item = 0;
	}

	private void refreshView() {
		cleanFields();
		cleanTableTemp();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == views.jlabel_sales) {
			views.panel_tab_menu_options.setSelectedIndex(2);
		} else if (e.getSource() == views.jlabel_reports) {
			if (LoginController.getPermission()) {
				views.panel_tab_menu_options.setSelectedIndex(7);
				listAllSalesOnReportTable();
			} else {
				views.panel_tab_menu_options.setEnabledAt(7, false);
				views.jlabel_reports.setEnabled(false);
				showError("Error", "No tiene privilegios de administrador para acceder a esta vista");
			}
		}
	}

	private void confirmSale() throws BusinessException {
		Sales sale = new Sales();
		int customer_id = Integer.parseInt(views.txt_sale_customer_id.getText());
		double total = Double.parseDouble(views.txt_sale_total_to_pay.getText());
		sale.setCustomer_id(customer_id);
		sale.setTotal_to_pay(total);
		saleService.registerSale(sale);
		int lastSaleId = saleService.getLastSaleId();
		for (int i = 0; i < views.sales_table.getRowCount(); i++) {
			int product_id = Integer.parseInt(views.sales_table.getValueAt(i, 0).toString());
			int sale_quantity = Integer.parseInt(views.sales_table.getValueAt(i, 2).toString());
			double sale_price = Double.parseDouble(views.sales_table.getValueAt(i, 3).toString());
			double sale_subtotal = sale_quantity * sale_price;
			saleService.registerSaleDetailQuery(product_id, lastSaleId, sale_quantity, sale_price, sale_subtotal);
			saleService.adjustStockAfterSale(product_id, sale_quantity);
		}
		showSuccess("Venta generada");
		refreshView();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == views.txt_sale_product_code) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if ("".equals(views.txt_sale_product_code.getText())) {
					showError("Error", "Ingrese el codigo del producto a vender");
				}
				try {
					int code = Integer.parseInt(views.txt_sale_product_code.getText());
					Products product;
					product = ProductService.getInstance().searchCode(code);
					if (product.getName() != null) {
						views.txt_sale_product_name.setText(product.getName());
						views.txt_sale_product_id.setText("" + product.getId());
						views.txt_sale_stock.setText("" + product.getProduct_quantity());
						views.txt_sale_price.setText("" + product.getUnit_price());
						views.txt_sale_quantity.requestFocus();
					}
				} catch (ValidationException e1) {
					cleanFieldsSales();
					views.txt_sale_product_code.requestFocus();
					e1.printStackTrace();
				}
			}
		} else if (e.getSource() == views.txt_sale_customer_id) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!"".equals(views.txt_sale_customer_id.getText())) {
					int customer_id = Integer.parseInt(views.txt_sale_customer_id.getText());
					try {
						Customers customer = (Customers) CustomerService.getInstance().listAllElements("" + customer_id)
								.get(0);
						if (customer.getFull_name() != null) {
							views.txt_sale_customer_name.setText("" + customer.getFull_name());
						} else {
							views.txt_sale_customer_id.setText("");
							showError("Error", "El cliente no existe");
						}
					} catch (BusinessException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource() == views.txt_sale_quantity) {
			int quantity;
			double price = Double.parseDouble(views.txt_sale_price.getText());
			if (views.txt_sale_quantity.getText().equals("")) {
				quantity = 1;
				views.txt_sale_price.setText("" + price);
			} else {
				quantity = Integer.parseInt(views.txt_sale_quantity.getText());
				price = Double.parseDouble(views.txt_sale_price.getText());
				views.txt_sale_subtotal.setText("" + quantity * price);
			}
		}
	}

	public void cleanTable() {
		for (int i = 0; i < model.getRowCount(); i++) {
			model.removeRow(i);
			i = i - 1;
		}
	}

	public void cleanTableTemp() {
		if (temp == null)
			return;
		for (int i = 0; i < temp.getRowCount(); i++) {
			temp.removeRow(i);
			i = i - 1;
		}
	}

	// Limpiar algunos campos
	public void cleanFieldsSales() {
		views.txt_sale_product_code.setText("");
		views.txt_sale_product_name.setText("");
		views.txt_sale_quantity.setText("");
		views.txt_sale_product_id.setText("");
		views.txt_sale_price.setText("");
		views.txt_sale_subtotal.setText("");
		views.txt_sale_stock.setText("");
	}

	private void calculateSales() {
		double total = 0.00;
		int numRow = views.sales_table.getRowCount();
		for (int i = 0; i < numRow; i++) {
			total = total + Double.parseDouble(String.valueOf(views.sales_table.getValueAt(i, 4)));
		}
		views.txt_sale_total_to_pay.setText("" + total);
	}

	public void listAllSalesOnReportTable() {
		if (LoginController.getPermission()) {
			List<Sales> list;
			try {
				list = getListForTable();
				model = (DefaultTableModel) views.sales_report_table.getModel();
				Object[] row = new Object[5];
				for (int i = 0; i < list.size(); i++) {
					row[0] = list.get(i).getId();
					row[1] = list.get(i).getCustomer_name();
					row[2] = list.get(i).getEmployee_name();
					row[3] = list.get(i).getTotal_to_pay();
					row[4] = list.get(i).getSale_date();
					model.addRow(row);
				}
				views.sales_report_table.setModel(model);
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Este metodo limpia todo
	 */
	@Override
	protected void cleanFields() {
		views.txt_sale_product_code.setText("");
		views.txt_sale_product_name.setText("");
		views.txt_sale_quantity.setText("");
		views.txt_sale_product_id.setText("");
		views.txt_sale_price.setText("");
		views.txt_sale_subtotal.setText("");
		views.txt_sale_customer_id.setText("");
		views.txt_sale_customer_name.setText("");
		views.txt_sale_total_to_pay.setText("");
		views.txt_sale_stock.setText("");
	}
}

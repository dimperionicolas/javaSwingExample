package main.java.nicodim.pharmacy.views.base;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public abstract class AbstractSystemView extends JFrame {

	public JFrame frame;
	public JPanel jpanel_products;
	public JLabel jlabel_products;
	public JPanel jpanel_purchases;
	public JLabel jlabel_purchases;
	public JPanel jpanel_sales;
	public JLabel jlabel_sales;
	public JPanel jpanel_customers;
	public JLabel jlabel_customers;
	public JPanel jpanel_employees;
	public JLabel jlabel_employees;
	public JPanel jpanel_suppliers;
	public JLabel jlabel_suppliers;
	public JPanel jpanel_categories;
	public JLabel jlabel_categories;
	public JPanel jpanel_reports;
	public JLabel jlabel_reports;
	public JPanel jpanel_settings;
	public JLabel jlabel_settings;
	//
	public JButton btn_photo;
	public JButton btn_logout;

	public JTabbedPane panel_tab_menu_options;

	// Product
	public JTextField txt_product_code;
	public JTextField txt_product_name;
	public JTextField txt_product_unit_price;
	public JTextField txt_product_description;
	public JTextField txt_product_id;
	public JButton btn_product_register;
	public JButton btn_product_update;
	public JButton btn_product_delete;
	public JButton btn_product_cancel;
	public JTextField txt_product_search;
	public JTable product_table;
	public JComboBox cmb_product_category;

	// Purchase
	public JTextField txt_purchase_unit_code;
	public JTextField txt_purchase_product_name;
	public JTextField txt_purchase_amount;
	public JTextField txt_purchase_unit_price;
//	public JTextField txt_purchase_supplier;
	public JTextField txt_purchase_subtotal;
	public JTextField txt_purchase_total;
	public JTextField txt_purchase_product_id;
	public JButton btn_purchase_add_to_buy;
	public JButton btn_purchase_confirm;
	public JButton btn_purchase_remove;
	public JButton btn_purchase_new;
	public JComboBox cmb_purchase_supplier;

	public JTable purchase_table;

	// Customer
	public JTextField txt_customer_id;
	public JTextField txt_customer_address;
	public JTextField txt_customer_telephone;
	public JTextField txt_customer_email;
	public JTextField txt_customer_fullname;
	public JButton btn_customer_cancel;
	public JButton btn_customer_delete;
	public JButton btn_customer_update;
	public JButton btn_customer_register;
	public JTextField txt_customer_search;
	public JTable customers_table;

	// Employee
	public JTextField txt_employee_id;
	public JTextField txt_employee_username;
	public JTextField txt_employee_address;
	public JTextField txt_employee_telephone;
	public JTextField txt_employee_fullname;
	public JTextField txt_employee_email;
	public JPasswordField txt_employee_password;
	public JButton btn_employee_cancel;
	public JButton btn_employee_delete;
	public JButton btn_employee_update;
	public JButton btn_employee_register;
	public JTextField txt_employee_search;
	public JTable employees_table;
	public JComboBox<String> cmb_employee_rol;

	// Suppliers
	public JTextField txt_supplier_name;
	public JTextField txt_supplier_address;
	public JTextField txt_supplier_telephone;
	public JTextField txt_supplier_description;
	public JTextField txt_supplier_email;
	public JTextField txt_supplier_id;
	public JButton btn_supplier_register;
	public JButton btn_supplier_update;
	public JButton btn_supplier_delete;
	public JButton btn_supplier_cancel;

	public JTextField txt_suppliers_search;
	public JComboBox<String> cmb_supplier_city;
	public JTable suppliers_table;

	// Category
	public JTextField txt_category_id;
	public JTextField txt_category_name;
	public JButton btn_category_register;
	public JButton btn_category_update;
	public JButton btn_category_delete;
	public JTextField txt_category_search;
	public JButton btn_category_cancel;// TODO dinamic
	public JTable categories_table;

	// Reports
	public JTable purchase_report_table;
	public JTable sales_report_table;

	// Settings
	public JTextField txt_profile_id;
	public JTextField txt_profile_address;
	public JTextField txt_profile_telephone;
	public JTextField txt_profile_email;
	public JTextField txt_profile_fullname;
	public JPasswordField txt_profile_password_modify;
	public JPasswordField txt_profile_modify_confirm;
	public JButton btn_profile_modify_data;

	// Sales
	public JTextField txt_sale_product_code;
	public JTextField txt_sale_product_name;
	public JTextField txt_sale_quantity;
	public JTextField txt_sale_customer_id;
	public JTextField txt_sale_price;
	public JTextField txt_sale_product_id;
	public JTextField txt_sale_subtotal;
	public JTextField txt_sale_customer_name;
	public JTextField txt_sale_stock;
	public JTextField txt_sale_total_to_pay;
	public JButton btn_sale_add_product;
	public JButton btn_sale_new;
	public JButton btn_sale_remove;
	public JButton btn_sale_confirm;
	public JTable sales_table;
	public JLabel lbl_title_name_employee;
	public JLabel lbl_title_rol_employee;

}

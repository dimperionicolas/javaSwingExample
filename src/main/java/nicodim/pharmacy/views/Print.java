/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.nicodim.pharmacy.views;

import java.awt.Font;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import main.java.nicodim.pharmacy.dao.PurchasesDao;
import main.java.nicodim.pharmacy.models.Purchases;

public class Print {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
	}

	private JPanel form_print;
	private JTable purchase_details_table;
	private JTextField txt_invoice;
	private JTextField txt_total;

	public JFrame frame;

	Purchases purchase = new Purchases();
	PurchasesDao purchaseDao = new PurchasesDao();
	DefaultTableModel model = new DefaultTableModel();

	/**
	 * Creates new form Print
	 */
	public Print(int id) {
		initialize();
		frame.setSize(620, 630);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.repaint();

		frame.setTitle("Factura de compra");

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		txt_invoice.setText("" + id);

		listAllPurchasesDetails(id);
		calculatePurchase();
	}

	public void listAllPurchasesDetails(int id) {
		List<Purchases> list = purchaseDao.listPurchaseDetailQuery(id);
		model = (DefaultTableModel) purchase_details_table.getModel();
		Object[] row = new Object[7];
		model.getDataVector().clear(); // TODO no se deberia haber cargado con null, ver otras tablas
		for (int i = 0; i < list.size(); i++) {
			row[0] = list.get(i).getProduct_name();
			row[1] = list.get(i).getPurchase_amount();
			row[2] = list.get(i).getPurchase_price();
			row[3] = list.get(i).getPurchase_subtotal();
			row[4] = list.get(i).getSupplier_name_product();
			row[5] = list.get(i).getPurchaser();
			row[6] = list.get(i).getCreated();
			model.addRow(row);
		}
		purchase_details_table.setModel(model);
	}

	// Calcular el total
	public void calculatePurchase() {
		double total = 0.00;
		int numRow = purchase_details_table.getRowCount();
		for (int i = 0; i < numRow; i++) {
			total = total + Double.parseDouble(String.valueOf(purchase_details_table.getValueAt(i, 3)));
		}
		txt_total.setText("" + total);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 620, 630);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		form_print = new JPanel();
		form_print.setBounds(0, 0, 620, 520);
		form_print.setBackground(new java.awt.Color(152, 202, 63));
		form_print.setLayout(null);

		JPanel jPanel1 = new JPanel();
		JPanel jPanel2 = new JPanel();
		JLabel jLabel1 = new JLabel();
		JLabel jLabel2 = new JLabel();
		JLabel jLabel3 = new JLabel();
		JLabel jLabel4 = new JLabel();
		JScrollPane jScrollPane1 = new JScrollPane();

		txt_invoice = new JTextField();
		txt_total = new JTextField();
		purchase_details_table = new JTable();

		JButton btn_print_purchase = new JButton();
		btn_print_purchase.setBounds(0, 0, 0, 0);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel1.setIcon(new ImageIcon(getClass().getResource("/images/farmacia.png"))); // NOI18N
		jLabel1.setBounds(0, 0, 100, 70);

		jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
		jLabel2.setForeground(new java.awt.Color(255, 255, 255));
		jLabel2.setText("VIDA NATURAL");
		jLabel2.setBounds(210, 20, -1, -1);

		jPanel2.setLayout(null);
		jPanel2.add(jLabel1);
		jPanel2.setBounds(0, 0, 100, 70);
		form_print.add(jPanel2);

		txt_invoice.setEditable(false);
		txt_invoice.setBounds(500, 20, 110, -1);

		jPanel1.setBackground(new java.awt.Color(18, 45, 61));
		jPanel1.setLayout(null);
		jPanel1.add(jLabel2);
		jPanel1.setBounds(0, 0, 620, 70);
		jPanel1.add(txt_invoice);

		form_print.add(jPanel1);

		jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
		jLabel3.setForeground(new java.awt.Color(255, 255, 255));
		jLabel3.setText("DETALLES DE LA COMPRA:");
		jLabel3.setBounds(10, 130, -1, -1);
		form_print.add(jLabel3);

		purchase_details_table = new JTable();
		purchase_details_table.setModel(new DefaultTableModel(new Object[][] { { null, null, null, null, null }, },
				new String[] { "Producto", "Cantidad", "Precio", "Subtotal", "Proveedor", "Comprador por", "Fecha" }) {
			boolean[] columnEditables = new boolean[] { false, false, false, false, false, true, true };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		purchase_details_table.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		purchase_details_table.setBounds(29, 352, 920, 136);
		jScrollPane1.setViewportView(purchase_details_table);
		if (purchase_details_table.getColumnModel().getColumnCount() > 0) {
			purchase_details_table.getColumnModel().getColumn(0).setMinWidth(100);
			purchase_details_table.getColumnModel().getColumn(5).setMinWidth(110);
			purchase_details_table.getColumnModel().getColumn(6).setMinWidth(80);
		}

		jScrollPane1.setBounds(0, 170, 620, 250);
		form_print.add(jScrollPane1);

		jLabel4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
		jLabel4.setForeground(new java.awt.Color(255, 255, 255));
		jLabel4.setText("Total:");
		jLabel4.setBounds(410, 450, -1, -1);
		form_print.add(jLabel4);

		txt_total.setBounds(470, 450, 140, 30);
		form_print.add(txt_total);

		frame.getContentPane().add(form_print);

		btn_print_purchase.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
		btn_print_purchase.setText("IMPRIMIR");
		btn_print_purchase.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btn_print_purchaseActionPerformed(evt);
			}
		});
//		form_print.add(btn_print_purchase);

		frame.getContentPane().add(btn_print_purchase);
	}

	private void btn_print_purchaseActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_print_purchaseActionPerformed
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		printerJob.setJobName("Impresión de Compra");

		// Configurar el componente a imprimir
		printerJob.setPrintable((graphics, pageFormat, pageIndex) -> {
			if (pageIndex > 0) {
				return Printable.NO_SUCH_PAGE;
			}

			// Renderiza el formulario en la página de impresión
			form_print.printAll(graphics);
			return Printable.PAGE_EXISTS;
		});

		// Mostrar el cuadro de diálogo de impresión
		if (printerJob.printDialog()) {
			try {
				printerJob.print();
			} catch (PrinterException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error al imprimir: " + e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);

			}
		} else {
			JOptionPane.showMessageDialog(null, "Impresión cancelada", "Información", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void dispose() {
		frame.dispose();
	}

	public void setVisible(boolean b) {
		frame.setVisible(true);
	}
}

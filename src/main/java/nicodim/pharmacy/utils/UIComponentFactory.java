package main.java.nicodim.pharmacy.utils;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class UIComponentFactory {
	// Constants
	private static final Color TAB_PANEL_BACKGROUND = new Color(69, 193, 241);
	private static final Color MENU_PANEL_BACKGROUND = new Color(18, 45, 61);
	private static final Color REGULAR_COLOR = Color.WHITE;

	private static final Font TITLE_FONT = new Font("Times New Roman", Font.BOLD, 25);
	private static final Font NORMAL_FONT = new Font("Times New Roman", Font.PLAIN, 18);
	private static final Font BUTTON_FONT = new Font("Times New Roman", Font.BOLD, 18);

	// Component Position enum for standardizing layouts
	public enum ComponentPosition {
		ONE(new Bounds(160, 27, 180, 20), new Bounds(10, 20, 140, 35)),
		TWO(new Bounds(160, 77, 180, 20), new Bounds(10, 70, 140, 35)),
		THREE(new Bounds(160, 127, 180, 20), new Bounds(10, 120, 140, 35)),
		FOUR(new Bounds(160, 177, 180, 20), new Bounds(10, 170, 140, 35)),
		FIVE(new Bounds(160, 227, 180, 20), new Bounds(10, 220, 140, 35)),
		SIX(new Bounds(530, 27, 180, 20), new Bounds(380, 20, 140, 35)),
		SEVEN(new Bounds(530, 77, 180, 20), new Bounds(380, 70, 140, 35)),
		EIGHT(new Bounds(530, 127, 180, 20), new Bounds(380, 120, 140, 35)),
		NINE(new Bounds(530, 177, 180, 20), new Bounds(380, 170, 140, 35)),
		TEN(new Bounds(530, 227, 180, 20), new Bounds(380, 220, 140, 35));

		private final Bounds textFieldBounds;
		private final Bounds labelBounds;

		ComponentPosition(Bounds textFieldBounds, Bounds labelBounds) {
			this.textFieldBounds = textFieldBounds;
			this.labelBounds = labelBounds;
		}
	}

	// Button positions enum
	public enum ButtonPosition {
		FIRST(730, 35, 150, 20), SECOND(730, 100, 150, 20), THIRD(730, 165, 150, 20), FOURTH(730, 230, 150, 20);

		private final Bounds bounds;

		ButtonPosition(int x, int y, int width, int height) {
			this.bounds = new Bounds(x, y, width, height);
		}
	}

	// Helper class for storing bounds
	private static class Bounds {
		final int x, y, width, height;

		Bounds(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}

	// Factory methods for creating components

	/**
	 * Crea etiqueta con posicion, tamaño, alineacion, fuente y color customizada.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param text
	 * @param font
	 * @param alignment
	 * @param foregroundColor
	 * @return
	 */
	public static JLabel createCustomLabel(int x, int y, int width, int height, String text, Font font, int alignment,
			Color foregroundColor) {
		JLabel label = new JLabel(text);
		label.setHorizontalAlignment(alignment);
		label.setFont(font);
		if (foregroundColor != null) {
			label.setForeground(foregroundColor);
		}
		label.setBounds(x, y, width, height);
		return label;
	}

	public static JLabel createLabel(ComponentPosition position, String text) {
		Bounds bounds = position.labelBounds;
		return createCustomLabel(bounds.x, bounds.y, bounds.width, bounds.height, text, NORMAL_FONT,
				SwingConstants.RIGHT, null);
	}

	public static JLabel createTitleLabel(String text, int x, int y, int width, int height) {
		return createCustomLabel(x, y, width, height, text, TITLE_FONT, SwingConstants.CENTER, Color.WHITE);
	}

	public static JTextField createTextField(ComponentPosition position) {
		JTextField textField = new JTextField();
		textField.setFont(NORMAL_FONT);
		textField.setColumns(10);
		Bounds bounds = position.textFieldBounds;
		textField.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		return textField;
	}

	public static JButton createButton(ButtonPosition position, String text) {
		JButton button = new JButton(text);
		button.setFont(BUTTON_FONT);
		Bounds bounds = position.bounds;
		button.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		return button;
	}

	public static JButton createCustomButton(int x, int y, int width, int height, String text) {
		JButton button = new JButton(text);
		button.setFont(BUTTON_FONT);
		button.setBounds(x, y, width, height);
		return button;
	}

	public static JPanel createCustomPanel(int x, int y, int width, int height, Color backgroundColor) {
		JPanel panel = new JPanel();
		panel.setBackground(backgroundColor);
		panel.setBounds(x, y, width, height);
		panel.setLayout(null);
		return panel;
	}

	public static JPanel createGeneralPanelTab(JFrame frame, int x, int y, int width, int height, Color bgColor) {
		if (width <= 0 || height <= 0) {
			x = 200;
			y = 0;
			width = 990;
			height = 100;
			bgColor = UIComponentFactory.getMenuPanelBackground();
		}
		JPanel jPanel = new JPanel();
		jPanel.setBackground(bgColor);
		jPanel.setBounds(x, y, width, height);
		frame.getContentPane().add(jPanel);
		jPanel.setLayout(null);
		return jPanel;
	}

	public static JPanel createTitledPanel(String title, int x, int y, int width, int height) {
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(null, title, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(x, y, width, height);
		return panel;
	}

	public static JComboBox<String> createComboBox(List<String> items, int x, int y, int width, int height) {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(items.toArray(new String[0]));
		JComboBox<String> comboBox = new JComboBox<>(model);
		comboBox.setFont(BUTTON_FONT);
		comboBox.setBounds(x, y, width, height);
		return comboBox;
	}

	public static JPasswordField createPasswordField(ComponentPosition position) {
		JPasswordField passwordField = new JPasswordField();
		passwordField.setFont(NORMAL_FONT);
		Bounds bounds = position.textFieldBounds;
		passwordField.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		return passwordField;
	}

	// Getter methods for colors and fonts
	public static Color getTabPanelBackground() {
		return TAB_PANEL_BACKGROUND;
	}

	public static Color getMenuPanelBackground() {
		return MENU_PANEL_BACKGROUND;
	}

	public static Color getRegularColor() {
		return REGULAR_COLOR;
	}

	public static Font getTitleFont() {
		return TITLE_FONT;
	}

	public static Font getNormalFont() {
		return NORMAL_FONT;
	}

	public static Font getButtonFont() {
		return BUTTON_FONT;
	}

	public static JTextField createFixedSearchButton(JPanel tab_products) {
		tab_products.add(createCustomLabel(45, 297, 139, 33, "BUSCAR", NORMAL_FONT, SwingConstants.RIGHT, null));
		JTextField jTextField = new JTextField();
		jTextField.setFont(NORMAL_FONT);
		jTextField.setColumns(10);
		jTextField.setBounds(224, 304, 166, 20);
		tab_products.add(jTextField);
		return jTextField;
	}

	//
	public static JTable createTable(String[] columnNames, boolean[] columnEditables, JPanel container) {

		DefaultTableModel tableModel = new DefaultTableModel(null, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return columnEditables != null && columnEditables.length > column && columnEditables[column];
			}
		};

		// Crear la tabla
		JTable table = new JTable(tableModel);

		// Configuración de la tabla
		table.getTableHeader().setVisible(true);
		table.getTableHeader().setFont(NORMAL_FONT);
		table.setRowHeight(25);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getTableHeader().setReorderingAllowed(false); // Evitar reordenamiento de columnas

		// Envolver la tabla en un JScrollPane
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(35, 335, 920, 165);

		scrollPane.setViewportView(table);
		scrollPane.setColumnHeaderView(table.getTableHeader());

		container.add(scrollPane);
		return table;
	}

	public static JFrame createFrame(int i, int j, int k, int l, int exitOnClose) {
		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 1208, 680);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return frame;
	}

	public static JPanel createNewInternalPanel(String string) {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(null);
		jPanel.setBorder(new TitledBorder(null, string, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		jPanel.setBounds(35, 10, 920, 280);
		return jPanel;
	}

	public static JPanel createAndAddPanelTabContainer(JTabbedPane panel_tab_menu_options, String string) {
		JPanel jPanel = new JPanel();
		jPanel.setBackground(UIComponentFactory.getTabPanelBackground());
		jPanel.setLayout(null);
		panel_tab_menu_options.addTab(string, null, jPanel, null);
		return jPanel;
	}

}

package main.java.nicodim.pharmacy.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import main.java.nicodim.pharmacy.connections.DatabaseConnection;
import main.java.nicodim.pharmacy.controllers.LoginController;
import main.java.nicodim.pharmacy.dao.EmployeesDao;
import main.java.nicodim.pharmacy.models.Employees;
import main.java.nicodim.pharmacy.utils.ResourceLoader;

public class LoginView {

	public JFrame frame;
	public JTextField txt_username;
	public JPasswordField txt_password;
	public JButton btn_enter;

	Employees employee = new Employees();
	EmployeesDao employee_dao = new EmployeesDao();

	/**
	 * Create the application.
	 */
	public LoginView() {
		initialize();
		@SuppressWarnings("unused")
		LoginController employee_login = new LoginController(employee, employee_dao, this);
		frame.setSize(930, 415);
		frame.setResizable(false);
		frame.setTitle("Ingresar al sistema");
		frame.setLocationRelativeTo(null);
		frame.repaint();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Acciones antes de cerrar la ventana, por ejemplo, cerrar el sistema
				DatabaseConnection.close();
				System.exit(0); // Finaliza el sistema
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(255, 255, 255));
		frame.setResizable(false);
		frame.setBounds(100, 100, 930, 415);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(0, 128, 192));
		panel.setBounds(440, 0, 490, 420);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblNewLabel = new JLabel("INICIAR SESIÓN");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 26));
		lblNewLabel.setBounds(10, 11, 459, 119);
		panel.add(lblNewLabel);

		JLabel lblUsuario = new JLabel("Usuario: ");
		lblUsuario.setHorizontalAlignment(SwingConstants.LEFT);
		lblUsuario.setForeground(Color.WHITE);
		lblUsuario.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblUsuario.setBounds(59, 141, 115, 56);
		panel.add(lblUsuario);

		JLabel lblContrasea = new JLabel("Contraseña: ");
		lblContrasea.setHorizontalAlignment(SwingConstants.LEFT);
		lblContrasea.setForeground(Color.WHITE);
		lblContrasea.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblContrasea.setBounds(59, 208, 115, 56);
		panel.add(lblContrasea);

		txt_username = new JTextField();
		txt_username.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		txt_username.setBounds(248, 161, 168, 20);
		panel.add(txt_username);
		txt_username.setColumns(10);

		btn_enter = new JButton("INGRESAR");
		btn_enter.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btn_enter.setBounds(248, 302, 168, 39);
		panel.add(btn_enter);

		txt_password = new JPasswordField();
		txt_password.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		txt_password.setBounds(248, 226, 168, 20);
		panel.add(txt_password);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 0, 440, 412);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setIcon(ResourceLoader.loadImage("wallpaper.jpg"));
		lblNewLabel_1.setBounds(-80, -24, 590, 425);
		panel_1.add(lblNewLabel_1);
	}

	public void setVisible(boolean b) {
		frame.setVisible(true);
	}

	public void dispose() {
		frame.dispose();
	}

}

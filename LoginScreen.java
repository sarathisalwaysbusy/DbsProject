
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginScreen {

	JFrame frame;
	private JPasswordField pwdPassword;
	private JTextField txtUsername;
	private JButton btnLogIn;


	/**
	 * Create the application.
	 */
	public LoginScreen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setMaximizedBounds(frame.getBounds());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblFailedLogin = new JLabel("Either the username or the password is incorrect");
		lblFailedLogin.setBounds(79, 46, 294, 22);
		frame.getContentPane().add(lblFailedLogin);
		lblFailedLogin.setVisible(false);

		JLabel lblUsername = new JLabel("username");
		lblUsername.setBounds(79, 85, 85, 29);
		frame.getContentPane().add(lblUsername);

		JLabel lblPassword = new JLabel("password");
		lblPassword.setBounds(79, 127, 85, 31);
		frame.getContentPane().add(lblPassword);

		pwdPassword = new JPasswordField();
		pwdPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == 10) {
					String userName = txtUsername.getText();
					String pass = new String(pwdPassword.getPassword());
					if (Utils.isAuthCorrect(userName, pass)) {
						System.out.println("Successful login");
						frame.dispose();
						Utils.createNewSession(userName);
					} else {
						lblFailedLogin.setVisible(true);
					}
				}
			}
		});
		pwdPassword.setText("");
		pwdPassword.setBounds(164, 131, 116, 22);
		frame.getContentPane().add(pwdPassword);

		txtUsername = new JTextField();
		txtUsername.setText("");
		txtUsername.setBounds(164, 88, 116, 22);
		frame.getContentPane().add(txtUsername);
		txtUsername.setColumns(10);


		btnLogIn = new JButton("Log In");
		btnLogIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				String userName = txtUsername.getText();
				String pass = new String(pwdPassword.getPassword());
				if (Utils.isAuthCorrect(userName, pass)) {
					System.out.println("Successful login");
					frame.dispose();
					Utils.createNewSession(userName);
				} else {
					lblFailedLogin.setVisible(true);
				}	
			}
		});
		btnLogIn.setBounds(183, 186, 97, 25);
		frame.getContentPane().add(btnLogIn);
	}
}

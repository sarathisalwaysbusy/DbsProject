import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;



public final class Utils {

	static Database dataBase;


	public static boolean isAuthCorrect(String userName, String pass) {
		boolean isAuthCorrect = false;
		try {
			isAuthCorrect = dataBase.stmt.executeQuery("Select password from db_users where username='"
					+ userName + "' and password='" + pass + "'").next();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return isAuthCorrect;
	}

	public static boolean isAdmin(String userName) {
		boolean isAdmin = false;
		try {
			ResultSet resultSet = dataBase.stmt.executeQuery("Select admin from db_users where username='"+userName+"'");
			resultSet.next();
			isAdmin = resultSet.getBoolean("admin");
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return isAdmin;
	}



	public static TableModel buildTableModel(ResultSet resultSet) throws SQLException {
		ResultSetMetaData metaData = resultSet.getMetaData();

		Vector<String> columnNames = new Vector<String>();
		int columnCount = metaData.getColumnCount();
		for (int column = 1; column <= columnCount; column++) {
			columnNames.add(metaData.getColumnName(column));
		}

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		while (resultSet.next()) {
			Vector<Object> vector = new Vector<Object>();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				vector.add(resultSet.getObject(columnIndex));
			}
			data.add(vector);
		}
		return new DefaultTableModel(data, columnNames);
	}

	public static void displayLoginScreen() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginScreen window = new LoginScreen();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void createNewSession(String userName) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Session window = new Session(userName, Utils.isAdmin(userName));
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void addNewUser() throws SQLException {
		String userName = JOptionPane.showInputDialog("Enter The Username");
		if (!userName.isEmpty()) {
			while (dataBase.stmt.executeQuery("Select username from db_users where username='"
					+ userName + "'" ).next()) {
				popError("Entered username is already taken. Please choose another one: ");	
				userName = JOptionPane.showInputDialog("Enter the Username");
				if (userName == null) {
					return;
				}
			}
			String pass = JOptionPane.showInputDialog("Enter Password");
			if (!pass.isEmpty()) {
				String isAdmin = JOptionPane.showInputDialog("Is the user an admin (y/n)");
				if (!isAdmin.isEmpty()) {
					dataBase.stmt.executeUpdate("Insert into db_users values('"+ userName + "','" + pass + "',"+ isAdmin.matches("y") +")");
					popInfo("New user created successfully");
				}
			}
		}
	}
	
	public static void removeUser() throws SQLException {
		String userName = JOptionPane.showInputDialog("Enter The Username");
		if (!userName.isEmpty()) {
			while (!dataBase.stmt.executeQuery("Select username from db_users where username='"
					+ userName + "'" ).next()) {
				popError("No such user Exists. Please re-enter.");	
				userName = JOptionPane.showInputDialog("Enter the Username");
				if (userName == null) {
					return;
				}
			}
			dataBase.stmt.executeUpdate("DELETE FROM db_users WHERE username='" + userName + "'");
			popInfo("Deleted Successfully");
		}
	}

	static void popError(String errorMsg) {
		JOptionPane.showMessageDialog(null, errorMsg, "ERROR",
				JOptionPane.ERROR_MESSAGE);
	}
	
	static void popInfo(String infoMsg) {
		JOptionPane.showMessageDialog(null, infoMsg, "INFO",
				JOptionPane.INFORMATION_MESSAGE);
	}

	static void copyToClipboard(String str) {
		StringSelection stringSelection = new StringSelection(str);
		Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		systemClipboard.setContents(stringSelection, null);
	}

	public static BufferedImage getScreenshot(
			Component component) {

		BufferedImage image = new BufferedImage(
				component.getWidth(),
				component.getHeight(),
				BufferedImage.TYPE_INT_RGB
				);
		component.paint( image.getGraphics() );
		return image;
	}	
}

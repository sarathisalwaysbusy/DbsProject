
import javax.swing.JFrame;
import java.awt.BorderLayout;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JMenu;
import javax.swing.JTabbedPane;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JEditorPane;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import static javax.swing.JOptionPane.YES_OPTION;

public class Session {

	final String userName;
	final boolean isAdminLogin;
	File screenshotSaveDir;
	JFrame frame;
	private JTabbedPane tabbedPane;
	private JButton btnPlus;
	private JButton btnX;
	private JButton btnLogOut;
	private JMenu mnAdminOptions;

	/**
	 * Create the application.
	 */
	public Session(String userName, boolean isAdminLogin) {
		this.userName = userName;
		this.isAdminLogin = isAdminLogin;
		screenshotSaveDir = new File(System.getProperty("user.dir") + File.separator + "screenshots");
		if (!screenshotSaveDir.exists()) {
			screenshotSaveDir.mkdirs();
		}
		initialize();
		addTab(tabbedPane);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JButton btnNewWindow = new JButton("New Window");
		btnNewWindow.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				Utils.createNewSession(userName);
			}
		});

		mnFile.add(btnNewWindow);

		JButton btnNewTab = new JButton("New Tab");
		btnNewTab.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				addTab(tabbedPane);
			}
		});
		mnFile.add(btnNewTab);

		JButton btnExit = new JButton("Exit");
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.dispose();
			}
		});

		JButton btnCloseTab = new JButton("Close Tab");
		btnCloseTab.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tabbedPane.remove(tabbedPane.getSelectedIndex());
			}
		});
		
		JButton btnSaveScreenshot = new JButton("Save Screenshot");
		btnSaveScreenshot.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BufferedImage screenshot = Utils.getScreenshot(
						tabbedPane.getComponentAt(tabbedPane.getSelectedIndex()));
				try {
					String fileName = JOptionPane.showInputDialog("Save as: ");
					if (fileName == null) {
						fileName = Integer.toString((new Random().nextInt(99999999)));
					}
					ImageIO.write(screenshot, "png", 
							new File(screenshotSaveDir, fileName + ".png"));
					Utils.popInfo("Screenshot saved successfully");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					Utils.popError(e1.getMessage());
				}
			}
		});
		mnFile.add(btnSaveScreenshot);

		mnFile.add(btnCloseTab);
		if (isAdminLogin) {
			mnAdminOptions = new JMenu("Admin Options");
			mnFile.add(mnAdminOptions);
		}
		mnFile.add(btnExit);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JButton btnCopyQuery = new JButton("Copy Query");
		btnCopyQuery.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Tab currentTab = (Tab) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
				Utils.copyToClipboard(currentTab.textField.getText());
			}
		});
		mnEdit.add(btnCopyQuery);
		
		JButton btnScreenshotDirectory = new JButton("Screenshot Directory");
		btnScreenshotDirectory.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Utils.popInfo("Current Screenshot Directory:" +
						screenshotSaveDir.getAbsolutePath());
				String newLoc = JOptionPane.showInputDialog("Enter the new path: ");
				String oldLoc = screenshotSaveDir.getAbsolutePath();
				if (newLoc != null) {
					screenshotSaveDir = new File(newLoc);
					if (!screenshotSaveDir.exists()) {
						if(JOptionPane.showConfirmDialog(null, 
								"Not all the directories in the specified path exist. "+
										"Do you want to create them ?",
										"Create New Directory",
										JOptionPane.INFORMATION_MESSAGE) == YES_OPTION) {
							screenshotSaveDir.mkdirs();
						} else {
							screenshotSaveDir = new File(oldLoc);
						}
					}
				}
			}
		});
		mnEdit.add(btnScreenshotDirectory);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		JButton btnHistory = new JButton("History");
		
		mnView.add(btnHistory);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JButton btnMysql = new JButton("MySQL");
		btnMysql.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JEditorPane mySqlHelpPane = new JEditorPane();
				mySqlHelpPane.setEditable(false);
				try {
					mySqlHelpPane.setPage(new URL("https://dev.mysql.com/doc/refman/5.7/en/help.html"));
				} catch (IOException e1) {
					e1.printStackTrace();
					mySqlHelpPane.setContentType("text/html");
					mySqlHelpPane.setText("<html>Could not load</html>");
				}
				JScrollPane scrollPane = new JScrollPane(mySqlHelpPane);
				JPanel mySqlHelpPanel = new JPanel();
				mySqlHelpPanel.add(scrollPane);
				tabbedPane.addTab("Help", mySqlHelpPanel);
			}
		});
		mnHelp.add(btnMysql);
	}

	public ResultSet resolveQuery(String query) {
		try {
			return Utils.dataBase.stmt.executeQuery(query);
		} catch (SQLException e) {
			if (e.getMessage().matches(".*data manipulation.*")) {
				if (isAdminLogin) {
					try {
						Utils.dataBase.stmt.executeUpdate(query);
						Utils.popInfo("Query Successfully Executed");
					} catch (SQLException e1) {
						e1.printStackTrace();
						Utils.popError(e1.getMessage());
					}	
				} else {
					Utils.popError("You are not authorised to execute ddl commands");
				}
			} else {
				e.printStackTrace();
				Utils.popError(e.getMessage());
			}
		}
		return null;
	}

	public void addTab(JTabbedPane tabbedPane) {
		Tab tab = new Tab();
		tabbedPane.addTab("New tab", null, tab, null);

		tab.btnQuery.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				ResultSet resultSet = resolveQuery(tab.textField.getText());
				try {
					if (resultSet != null) {
						tab.table.setModel(Utils.buildTableModel(resultSet));
					}
				} catch (SQLException e) {
					e.printStackTrace();
					Utils.popError(e.getMessage());
				}
			}
		});

		tab.textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					ResultSet resultSet = resolveQuery(tab.textField.getText());
					try {
						if (resultSet != null)
							tab.table.setModel(Utils.buildTableModel(resultSet));
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		btnPlus = new JButton("+");
		GridBagConstraints gbc_btnPlus = new GridBagConstraints();
		gbc_btnPlus.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnPlus.insets = new Insets(0, 0, 5, 0);
		gbc_btnPlus.gridx = 14;
		gbc_btnPlus.gridy = 2;
		tab.add(btnPlus, gbc_btnPlus);

		btnX = new JButton("x");
		GridBagConstraints gbc_btnX = new GridBagConstraints();
		gbc_btnX.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnX.insets = new Insets(0, 0, 5, 0);
		gbc_btnX.gridx = 14;
		gbc_btnX.gridy = 3;
		tab.add(btnX, gbc_btnX);


		btnLogOut = new JButton("Log Out");
		GridBagConstraints gbc_btnLogOut = new GridBagConstraints();
		gbc_btnLogOut.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnLogOut.insets = new Insets(0, 0, 5, 0);
		gbc_btnLogOut.gridx = 14;
		gbc_btnLogOut.gridy = 6;
		tab.add(btnLogOut, gbc_btnLogOut);

		btnLogOut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.dispose();
				Utils.displayLoginScreen();
			}
		});

		btnX.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tabbedPane.remove(tabbedPane.getSelectedIndex());
			}
		});

		btnPlus.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				addTab(tabbedPane);
			}
		});

		if (isAdminLogin) {
			addAdminOptions(tab);
		}

	}

	void addAdminOptions(Tab tab) {
		JButton btnNewUser = new JButton("New User");
		mnAdminOptions.add(btnNewUser);
		btnNewUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Utils.addNewUser();
				} catch (SQLException e1) {
					e1.printStackTrace();
					Utils.popError(e1.getMessage());
				}
			}
		});
		JButton btnRemoveUser = new JButton("Remove User");
		mnAdminOptions.add(btnRemoveUser);
		btnRemoveUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Utils.removeUser();
				} catch (SQLException e1) {
					e1.printStackTrace();
					Utils.popError(e1.getMessage());
				}
			}
		});
	}
	
}

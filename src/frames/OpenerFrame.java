package frames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import concurrent.FileSearchTask;
import imp.NFileChooser;
import opener.NFileFilter;
import util.NFileFilterContainer;

public class OpenerFrame extends JFrame {

	public static final String VERSION = "V 1.4";
	public static final String NAME = "Random File Opener";
	public static final String DATE = "22.08.2014";
	public static final String FILEEXTENSION = "rfo";

	private static final long serialVersionUID = 1L;
	private static OpenerFrame frame;
	private JTextField pathTF;
	private JButton goBT, applyBT, openBT, searchBT;
	private JButton btnGetCurrentDirectory;
	private JLabel foundLB, lastLB;

	private int activeTasks;

	private boolean searching = false;
	private boolean found = false;
	private JCheckBox subFoldersCB;
	private ArrayList<File> files;
	private JLabel searchLB;
	private JPanel panel_2;
	private JLabel lblFileExtension;
	private JLabel lblFileName;
	private JTextField fileNameTF;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenu mnEdit;
	private JSeparator separator;
	private JTextField fileExtensionTF;
	private JComboBox<NFileFilter> extensionCB;
	private JMenu fileFiltersMenu;
	private JMenu mnView;
	private JMenuItem mntmReadFileextensionInput;
	private JMenuItem mntmNewMenuItem;
	private NFileFilterContainer container;
	private String customInput;
	private File dataDirectory;
	private File containerFile;
	private File lastOpenedFile;
	private NFileChooser<NFileFilterContainer> fileChooser;
	private JMenuItem mntmDeleteSavedData;
	private JMenuItem mntmViewSavedData;
	private JButton openAgainBT;
	private JButton getContextBT;
	private JCheckBox folderModeCB;

	private ExecutorService exectuorService;
	private JPanel panel_3;
	private Component rigidArea;
	private JPanel panel_4;
	private Component rigidArea_1;

	public static void init() {
		getReference();
	}

	public static OpenerFrame getReference() {
		if (frame == null)
			frame = new OpenerFrame();
		return frame;
	}

	private OpenerFrame() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				onClose();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				onClose();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(frame, NAME + " " + VERSION + "\nBy Nils Förster, " + DATE,
						"About " + NAME, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnFile.add(mntmAbout);

		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);

		mntmViewSavedData = new JMenuItem("View saved data");
		mntmViewSavedData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Desktop d = Desktop.getDesktop();
				if (Desktop.isDesktopSupported()) {
					try {
						d.browse(dataDirectory.toURI());
					} catch (Exception e) {
						JOptionPane.showMessageDialog(frame, "Could not open the file.", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		mnFile.add(mntmViewSavedData);

		mntmDeleteSavedData = new JMenuItem("Delete saved data");
		mntmDeleteSavedData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int i = JOptionPane.showConfirmDialog(frame,
						"This will delete all the files created by this program and terminates it.\nAre you sure you want to continue?",
						"Confirm", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION) {
					containerFile.delete();
					dataDirectory.delete();
					if (dataDirectory.exists()) {
						JOptionPane.showMessageDialog(frame,
								"Could not delete all files. Try deleting them manually:\n\n" + dataDirectory.getPath(),
								"Error!", JOptionPane.ERROR_MESSAGE);
					} else {
						System.exit(0);
					}
				}
			}
		});
		mnFile.add(mntmDeleteSavedData);

		separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);

		mnView = new JMenu("View");
		menuBar.add(mnView);

		mntmReadFileextensionInput = new JMenuItem("Read File-Extension Input");
		mntmReadFileextensionInput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] s = readFilterExtensions();
				displayReadText("Reading allowed File-Extensions:", s);
			}
		});
		mnView.add(mntmReadFileextensionInput);

		mntmNewMenuItem = new JMenuItem("Read File-Name Input");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String[] s = readFilterName();
				displayReadText("Reading allowed File-Names:", s);
			}
		});
		mnView.add(mntmNewMenuItem);

		mnEdit = new JMenu("Edit File-Extensions");
		menuBar.add(mnEdit);

		fileFiltersMenu = new JMenu("Edit File-Extension filters");
		mnEdit.add(fileFiltersMenu);

		JMenuItem mntmNewFileextensionFilter = new JMenuItem("New File-Extension filter");
		mntmNewFileextensionFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new NFileFilterEditFrame(frame);
			}
		});
		mnEdit.add(mntmNewFileextensionFilter);

		FileNameExtensionFilter f = new FileNameExtensionFilter("File-Filter", FILEEXTENSION);
		fileChooser = new NFileChooser<>(f);
		initContainer();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 604, 0 };
		gridBagLayout.rowHeights = new int[] { 50, 58, 61, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "File Filter", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel panel = new JPanel();
		panel.setBorder(
				new TitledBorder(null, "Directory selection", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 31, 106, 273, 85, 31, 0, 0 };
		gbl_panel.rowHeights = new int[] { 23, 23, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);
		JLabel lblPath = new JLabel("Path:");
		GridBagConstraints gbc_lblPath = new GridBagConstraints();
		gbc_lblPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblPath.gridx = 0;
		gbc_lblPath.gridy = 0;
		panel.add(lblPath, gbc_lblPath);

		btnGetCurrentDirectory = new JButton("Get current Directory");
		btnGetCurrentDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = "";
				/**
				 * try { s=getClass().getProtectionDomain().getCodeSource()
				 * .getLocation().getPath(); } catch (Exception e2) {
				 * e2.printStackTrace();
				 * JOptionPane.showMessageDialog(OpenerFrame.getReference(),
				 * "Der aktuelle Pfad konnte nicht gefunden werden!", "Error",
				 * JOptionPane.ERROR_MESSAGE); }
				 */
				File f = new File(System.getProperty("java.class.path"));
				File dir = f.getAbsoluteFile().getParentFile();
				s = dir.toString();
				if (!s.equals("")) {
					setText(s);
				} else {
					System.out.println("Error!");
					JOptionPane.showMessageDialog(frame, "Could not detect the current location.", "Error!",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		pathTF = new JTextField();
		pathTF.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				changeFolder();
			}
		});
		pathTF.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		pathTF.setEditable(false);
		pathTF.setColumns(10);
		GridBagConstraints gbc_pathTF = new GridBagConstraints();
		gbc_pathTF.fill = GridBagConstraints.HORIZONTAL;
		gbc_pathTF.insets = new Insets(0, 0, 5, 5);
		gbc_pathTF.gridwidth = 4;
		gbc_pathTF.gridx = 1;
		gbc_pathTF.gridy = 0;
		panel.add(pathTF, gbc_pathTF);

		searchBT = new JButton("Choose");
		searchBT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeFolder();
			}
		});
		GridBagConstraints gbc_searchBT = new GridBagConstraints();
		gbc_searchBT.fill = GridBagConstraints.HORIZONTAL;
		gbc_searchBT.insets = new Insets(0, 0, 5, 0);
		gbc_searchBT.gridx = 5;
		gbc_searchBT.gridy = 0;
		panel.add(searchBT, gbc_searchBT);
		GridBagConstraints gbc_btnGetCurrentDirectory = new GridBagConstraints();
		gbc_btnGetCurrentDirectory.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnGetCurrentDirectory.insets = new Insets(0, 0, 0, 5);
		gbc_btnGetCurrentDirectory.gridwidth = 2;
		gbc_btnGetCurrentDirectory.gridx = 0;
		gbc_btnGetCurrentDirectory.gridy = 1;
		panel.add(btnGetCurrentDirectory, gbc_btnGetCurrentDirectory);

		searchLB = new JLabel("Searching...");
		searchLB.setVisible(false);
		searchLB.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_searchLB = new GridBagConstraints();
		gbc_searchLB.insets = new Insets(0, 0, 0, 5);
		gbc_searchLB.gridx = 2;
		gbc_searchLB.gridy = 1;
		panel.add(searchLB, gbc_searchLB);

		subFoldersCB = new JCheckBox("Include Sub-Folders");
		subFoldersCB.setHorizontalAlignment(SwingConstants.RIGHT);
		subFoldersCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateUI();
			}
		});
		GridBagConstraints gbc_subFoldersCB = new GridBagConstraints();
		gbc_subFoldersCB.insets = new Insets(0, 0, 0, 5);
		gbc_subFoldersCB.anchor = GridBagConstraints.EAST;
		gbc_subFoldersCB.gridwidth = 2;
		gbc_subFoldersCB.gridx = 3;
		gbc_subFoldersCB.gridy = 1;
		panel.add(subFoldersCB, gbc_subFoldersCB);

		folderModeCB = new JCheckBox("Folders only");
		folderModeCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateUI();
			}
		});
		GridBagConstraints gbc_folderModeCB = new GridBagConstraints();
		gbc_folderModeCB.gridx = 5;
		gbc_folderModeCB.gridy = 1;
		panel.add(folderModeCB, gbc_folderModeCB);

		// extensionCB.setSelectedIndex(0);
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_2.anchor = GridBagConstraints.NORTH;
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		getContentPane().add(panel_2, gbc_panel_2);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Search & Open Files",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		getContentPane().add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 99, 241, 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 23, 0, 23, 0 };
		gbl_panel_1.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);
		
		panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 5, 5);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 0;
		panel_1.add(panel_4, gbc_panel_4);
				panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.X_AXIS));
		
				applyBT = new JButton("Scan for Files");
				panel_4.add(applyBT);
				applyBT.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						updateUI();
						apply();
						updateUI();
					}
				});
				
						applyBT.setEnabled(false);
								
								rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
								panel_4.add(rigidArea_1);
						
								openBT = new JButton("Open Path");
								panel_4.add(openBT);
								openBT.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent arg0) {
										open(new File(pathTF.getText()), false);
									}
								});
								openBT.setEnabled(false);

		foundLB = new JLabel("");
		foundLB.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_foundLB = new GridBagConstraints();
		gbc_foundLB.fill = GridBagConstraints.HORIZONTAL;
		gbc_foundLB.insets = new Insets(0, 0, 5, 5);
		gbc_foundLB.gridx = 1;
		gbc_foundLB.gridy = 0;
		panel_1.add(foundLB, gbc_foundLB);
				
						goBT = new JButton("Get next File");
						GridBagConstraints gbc_goBT = new GridBagConstraints();
						gbc_goBT.insets = new Insets(0, 0, 5, 0);
						gbc_goBT.gridx = 2;
						gbc_goBT.gridy = 0;
						panel_1.add(goBT, gbc_goBT);
						goBT.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								open(files.get(new Random().nextInt(getFileCount())), true);
							}
						});
						goBT.setEnabled(false);
		
				lastLB = new JLabel("Last opened: -");
				GridBagConstraints gbc_lastLB = new GridBagConstraints();
				gbc_lastLB.anchor = GridBagConstraints.SOUTH;
				gbc_lastLB.fill = GridBagConstraints.HORIZONTAL;
				gbc_lastLB.insets = new Insets(0, 0, 5, 0);
				gbc_lastLB.gridwidth = 3;
				gbc_lastLB.gridx = 0;
				gbc_lastLB.gridy = 1;
				panel_1.add(lastLB, gbc_lastLB);
				
				panel_3 = new JPanel();
				GridBagConstraints gbc_panel_3 = new GridBagConstraints();
				gbc_panel_3.anchor = GridBagConstraints.SOUTH;
				gbc_panel_3.gridwidth = 3;
				gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
				gbc_panel_3.gridx = 0;
				gbc_panel_3.gridy = 2;
				panel_1.add(panel_3, gbc_panel_3);
				panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));
				
						getContextBT = new JButton("Get Context");
						panel_3.add(getContextBT);
						getContextBT.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								File f = lastOpenedFile;
								if (f.isFile()) {
									f = f.getParentFile();
								}
								open(f, false);
							}
						});
				getContextBT.setEnabled(false);
						
						rigidArea = Box.createRigidArea(new Dimension(20, 20));
						panel_3.add(rigidArea);
				
						openAgainBT = new JButton("Open again");
						panel_3.add(openAgainBT);
						openAgainBT.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								open(lastOpenedFile, false);
							}
						});
		openAgainBT.setEnabled(false);

		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 73, 319, 144, 0 };
		gbl_panel_2.rowHeights = new int[] { 20, 20, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		fileExtensionTF = new JTextField();
		fileExtensionTF.setColumns(10);
		fileExtensionTF.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				textUpdate();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				textUpdate();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				textUpdate();
			}
		});

		lblFileExtension = new JLabel("File extension: ");
		GridBagConstraints gbc_lblFileExtension = new GridBagConstraints();
		gbc_lblFileExtension.anchor = GridBagConstraints.WEST;
		gbc_lblFileExtension.insets = new Insets(0, 0, 5, 5);
		gbc_lblFileExtension.gridx = 0;
		gbc_lblFileExtension.gridy = 0;
		panel_2.add(lblFileExtension, gbc_lblFileExtension);
		GridBagConstraints gbc_fileExtensionTF = new GridBagConstraints();
		gbc_fileExtensionTF.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileExtensionTF.insets = new Insets(0, 0, 5, 5);
		gbc_fileExtensionTF.gridx = 1;
		gbc_fileExtensionTF.gridy = 0;
		panel_2.add(fileExtensionTF, gbc_fileExtensionTF);

		extensionCB = new JComboBox<NFileFilter>();
		extensionCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isVisible() && container != null)
					updateCombobox();
				if (isComboboxOnCustom()) {
					fileExtensionTF.setText(customInput);
				}
			}
		});
		GridBagConstraints gbc_extensionCB = new GridBagConstraints();
		gbc_extensionCB.fill = GridBagConstraints.HORIZONTAL;
		gbc_extensionCB.insets = new Insets(0, 0, 5, 0);
		gbc_extensionCB.gridx = 2;
		gbc_extensionCB.gridy = 0;
		panel_2.add(extensionCB, gbc_extensionCB);

		lblFileName = new JLabel("File name:");
		GridBagConstraints gbc_lblFileName = new GridBagConstraints();
		gbc_lblFileName.anchor = GridBagConstraints.WEST;
		gbc_lblFileName.insets = new Insets(0, 0, 0, 5);
		gbc_lblFileName.gridx = 0;
		gbc_lblFileName.gridy = 1;
		panel_2.add(lblFileName, gbc_lblFileName);

		fileNameTF = new JTextField();
		fileNameTF.setColumns(10);
		GridBagConstraints gbc_fileNameTF = new GridBagConstraints();
		gbc_fileNameTF.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileNameTF.gridwidth = 2;
		gbc_fileNameTF.gridx = 1;
		gbc_fileNameTF.gridy = 1;
		panel_2.add(fileNameTF, gbc_fileNameTF);

		updateUI();
		updateCombobox();
		extensionCB.setSelectedIndex(0);

		requestFocus();
		setVisible(true);
		// setSize(640, 428);
		pack();
		setPreferredSize(getSize());
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setTitle(NAME + " " + VERSION);

		files = new ArrayList<>();
		// searchLB.setVisible(false);

	}

	private void initContainer() {
		container = null;
		String appdata = System.getenv("APPDATA");
		dataDirectory = new File(appdata + "/" + NAME);
		containerFile = new File(dataDirectory + "/data." + FILEEXTENSION);
		System.out.println("Createing files & loading data...");

		if (!dataDirectory.exists() | !containerFile.exists()) {
			dataDirectory.mkdir();
			try {
				containerFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(containerFile.getPath());
		try {
			container = fileChooser.loadData(containerFile);
			System.out.println("Loading successfull!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (container == null) {
			container = NFileFilterContainer.getDefaultContainer();
		}
		updateCombobox();
	}

	private void onClose() {
		System.out.println("Starting to save...");
		if (containerFile.exists()) {
			try {
				fileChooser.saveToFile(container, containerFile);
				System.out.println("Save successfull!");
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame,
						"Error while saveing the data. The program will be terminated and the data will be lost.",
						"Error!", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} else {
			System.out.println("File doesn't exist. Nothing is saved.");
		}
	}

	private void setText(String s) {
		if (vertifyPath(s))
			pathTF.setText(s);
		updateUI();
	}

	private boolean vertifyPath(String s) {
		File f = new File(s);
		return f.exists() && f.isDirectory();
	}

	private void changeFolder() {
		int i = 0;
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(pathTF.getText()));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		i = chooser.showOpenDialog(frame);
		if (i == JFileChooser.APPROVE_OPTION) {
			pathTF.setText(chooser.getSelectedFile().getPath());
		}
		updateUI();
	}

	private void apply() {
		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("Detected cores available: " + cores);
		if (exectuorService != null) {
			exectuorService.shutdownNow();
		}
		activeTasks = 0;
		found = false;
		exectuorService = Executors.newFixedThreadPool(cores);
		files.clear();
		updateUI();

		File origin = new File(pathTF.getText());
		if (!origin.exists() || !origin.isDirectory()) {
			return;
		}

		searchLB.setVisible(true);
		discoverFolder(origin);

		// new Thread() {
		// public void run() {
		// files.clear();
		// searching = true;
		// updateUI();
		//
		// searchForFiles(pathTF.getText());

		// filecount = files.size();
		// Opener.print(filecount);
		// searching = false;
		// found = true;
		// updateUI();
		// }
		// }.start();
	}

	private synchronized void discoverFolder(File f) {
		increaseTaskCount();
		exectuorService.submit(new FileSearchTask(folderModeCB.isSelected(), f, 
				readFilterName(), readFilterExtensions()) {

			@Override
			public void onFolderFound(File folder) {
				if (subFoldersCB.isSelected()) {
					discoverFolder(folder);
				}
				if (folderModeCB.isSelected()) {
					File[] fol = new File[1];
					fol[0] = folder;
					discoverFiles(fol);
				}
			}

			@Override
			public void onFilesFound(File[] files) {
				discoverFiles(files);
			}

			@Override
			public void onTaskFinished() {
				decreaseTaskCount();
				System.out.println("Tasks: " + activeTasks);
				if (!isSearching()) {
					onSearchFinished();
				}
			}
		});
		System.out.println("Discovered a folder! " + f.getAbsolutePath());
	}

	private synchronized void onSearchFinished() {
		System.out.println("Search for data finished.");
		searchLB.setVisible(false);
		found = true;
		updateUI();
	}

	private synchronized void discoverFiles(File[] f) {
		Collections.addAll(files, f);
		String s = "Files found: ";
		if (folderModeCB.isSelected()) {
			s = "Folders found: ";
		}

		foundLB.setText(s + files.size());

		System.out.println(f.length + " files added to the list. New list count: " + files.size() + " finished? "
				+ exectuorService.isTerminated());
	}

	private synchronized void increaseTaskCount() {
		activeTasks++;
	}

	private synchronized void decreaseTaskCount() {
		activeTasks--;
	}

	public synchronized boolean isSearching() {
		return activeTasks > 0;
	}

	public synchronized int getFileCount() {
		if (files == null) {
			return 0;
		}
		return files.size();
	}

	private String[] readFilterName() {
		String s = fileNameTF.getText();
		String[] r = s.toLowerCase().split(",");
		return r;
	}

	private String[] readFilterExtensions() {
		String str = fileExtensionTF.getText();
		String[] t = str.toLowerCase().split(",");
		String[] r = new String[t.length];
		for (int i = 0; i < t.length; i++) {
			r[i] = NFileFilter.convertToExtension(t[i]);
		}
		return r;
	}

	private void displayReadText(String displayText, String[] read) {
		String text = displayText + "\n\n";
		for (int i = 0; i < read.length; i++) {
			text = text + (i + 1) + ") " + read[i] + "\n";
		}
		if (read.length == 1 && (read[0] == null) | read[0].equals("")) {
			text = "No text found.";
		}
		JOptionPane.showMessageDialog(frame, text, "Interpreted Input", JOptionPane.INFORMATION_MESSAGE);
	}

	public boolean acceptFilter(File f) {
		if (acceptFilterName(f)) {
			if (acceptFilterExtension(f)) {
				return true;
			}
		}
		return false;
	}

	public boolean acceptFilterExtension(File f) {
		String[] extensions = readFilterExtensions();
		// System.out.println("FileExtension check - "+extensions.length);
		if (extensions.length == 0) {
			return true;
		}
		for (String s : extensions) {
			if (f.getName().toLowerCase().endsWith(s.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public boolean acceptFilterName(File f) {
		String[] names = readFilterName();
		// System.out.println("FileName check - "+names.length);
		if (names.length == 0) {
			return true;
		}
		for (String s : names) {
			if (f.getName().toLowerCase().contains(s.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	public void updateCombobox() {
		if (extensionCB == null)
			return;

		int c = extensionCB.getSelectedIndex();
		extensionCB.removeAllItems();
		fileFiltersMenu.removeAll();
		container.sortieren();
		for (int i = 0; i < container.getListe().size(); i++) {
			NFileFilter filter = container.getListe().get(i);
			extensionCB.addItem(filter);
			if (!filter.isDefaultCustom() && !filter.isDefaultNone()) {
				JMenuItem item = new JMenuItem(filter.getName());
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						JMenuItem c = (JMenuItem) arg0.getSource();
						new NFileFilterEditFrame(container.get(c.getText()), frame);
					}
				});
				fileFiltersMenu.add(item);
			}
		}
		extensionCB.setSelectedIndex(c);

		NFileFilter filter = (NFileFilter) extensionCB.getSelectedItem();
		if (filter != null) {
			fileExtensionTF.setText(filter.getExtensionString());
		}
		updateUI();
	}

	private void textUpdate() {
		if (isComboboxOnCustom()) {
			System.out.println("Custom text update");
			customInput = fileExtensionTF.getText();
		}
	}

	public void updateUI() {
		int filecount = getFileCount();
		goBT.setEnabled(!searching && found && filecount > 0);
		btnGetCurrentDirectory.setEnabled(!searching);
		applyBT.setEnabled(!searching && vertifyPath(pathTF.getText()));
		openBT.setEnabled(vertifyPath(pathTF.getText()));
		// foundLB.setText("Files found: " + filecount);
		subFoldersCB.setEnabled(!searching);
		openAgainBT.setEnabled(lastOpenedFile != null);
		getContextBT.setEnabled(lastOpenedFile != null);
		// folderModeCB.setEnabled(subFoldersCB.isSelected());
		// folderModeCB.setSelected(folderModeCB.isSelected() &&
		// folderModeCB.isEnabled());

		fileExtensionTF.setEnabled(isComboboxOnCustom() && !searching && !folderModeCB.isSelected());
		fileNameTF.setEnabled(!searching && !folderModeCB.isSelected());
		extensionCB.setEnabled(!searching && !folderModeCB.isSelected());
	}

	private void open(File f, boolean addToHistory) {
		String s;
		System.out.println(
				"Opening: " + f.getAbsolutePath() + ", addToHistory? " + addToHistory + " is Dir? " + f.isDirectory());

		if (addToHistory) {
			lastLB.setForeground(Color.black);
			lastOpenedFile = null;
		}

		try {
			if (addToHistory) {
				lastOpenedFile = f;
			}

			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(f);
			} else {
				JOptionPane.showMessageDialog(frame,
						"Your system does not support the method used in this programm to browse files. The File was not opened.",
						"Error!", JOptionPane.ERROR_MESSAGE);
			}
			s = "Last opened: " + f.getPath();
		} catch (Exception e) {
			e.printStackTrace();
			s = "Failed to open: " + f.getPath();

			if (addToHistory) {
				lastLB.setForeground(Color.red);
			}
		}

		if (addToHistory) {
			lastLB.setText(s);
		}
		System.out.println("My last opened is now: " + lastOpenedFile.getAbsolutePath());
		updateUI();
	}

	public boolean isComboboxOnCustom() {
		NFileFilter f = (NFileFilter) extensionCB.getSelectedItem();
		if (f == null) {
			return false;
		}
		return f.isDefaultCustom();
	}

	public boolean isComboboxOnNone() {
		return extensionCB.getSelectedIndex() == 0;
	}

	public NFileFilterContainer getFilterContainer() {
		return container;
	}
}

package frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import opener.NFileFilter;
import opener.NFileFilterTable;
import util.NFileFilterContainer;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NFileFilterEditFrame extends JDialog {

	private static final long serialVersionUID = 7190692305085740342L;
	private boolean createMode;
	private NFileFilter myFilter, editBackup;
	private OpenerFrame parent;
	private JTextField nameTF;
	private NFileFilterTable table;
	private JButton applyBT;
	private JButton resetBT;
	private JButton deleteBT;

	/**
	 * @wbp.parser.constructor
	 */
	public NFileFilterEditFrame(OpenerFrame parent) {
		myFilter = new NFileFilter();
		this.parent = parent;
		createMode = true;
		init();
	}

	public NFileFilterEditFrame(NFileFilter filter, OpenerFrame parent) {
		this.myFilter = filter;
		editBackup = new NFileFilter(filter);
		parent.getFilterContainer().removeFilter(filter);
		this.parent = parent;
		createMode = false;
		init();
	}

	private void init() {
		setSize(315, 345);
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Filter Name", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JScrollPane scrollPane = new JScrollPane();

		applyBT = new JButton("Create Filter");
		applyBT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				apply();
			}
		});

		resetBT = new JButton("Reset to default");
		resetBT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				reset();
			}
		});

		deleteBT = new JButton("Delete");
		deleteBT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				delete();
			}
		});
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout
				.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createSequentialGroup().addContainerGap()
								.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
										.addComponent(scrollPane, Alignment.LEADING)
										.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 356,
												Short.MAX_VALUE)
										.addGroup(Alignment.LEADING,
												groupLayout.createSequentialGroup().addComponent(applyBT)
														.addPreferredGap(ComponentPlacement.UNRELATED)
														.addComponent(resetBT)
														.addPreferredGap(ComponentPlacement.RELATED, 79,
																Short.MAX_VALUE)
														.addComponent(deleteBT)))
								.addContainerGap()));
		groupLayout
				.setVerticalGroup(
						groupLayout
								.createParallelGroup(
										Alignment.LEADING)
								.addGroup(
										groupLayout.createSequentialGroup().addContainerGap()
												.addComponent(panel, GroupLayout.PREFERRED_SIZE, 68,
														GroupLayout.PREFERRED_SIZE)
												.addGap(18)
												.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 168,
														Short.MAX_VALUE)
												.addGap(18)
												.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
														.addComponent(applyBT).addComponent(deleteBT)
														.addComponent(resetBT))
												.addContainerGap()));

		table = new NFileFilterTable(this);
		scrollPane.setViewportView(table);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.out.println("Closing...");
				if (!createMode) {
					parent.getFilterContainer().addFilter(editBackup);
					parent.updateCombobox();
				}
			}
		});

		nameTF = new JTextField();
		nameTF.setColumns(10);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup().addContainerGap()
								.addComponent(nameTF, GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
								.addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel
						.createSequentialGroup().addContainerGap().addComponent(nameTF, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(14, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		getContentPane().setLayout(groupLayout);
		String title = "Create new File-Name Extension Filter";
		if (!createMode) {
			title = "Edit " + myFilter;
		}
		setTitle(title);
		setResizable(false);
		setAlwaysOnTop(true);

		update();
		writeTable();

		setModal(true);
		setVisible(true);
	}

	private void reset() {
		myFilter.reset();
		writeTable();
	}

	private void apply() {
		String backupName = myFilter.getName();
		myFilter.setName(nameTF.getText());
		NFileFilterContainer container = parent.getFilterContainer();
		if (checkFilter()) {
			if (createMode) {
				myFilter.prepareExtensions();
				container.addFilter(myFilter);
				container.sortieren();
				parent.updateCombobox();
				dispose();
			} else {
				myFilter.prepareExtensions();
				container.addFilter(myFilter);
				container.sortieren();
				parent.updateCombobox();
				dispose();
			}
		} else {
			myFilter.setName(backupName);
		}
	}

	private boolean checkFilter() {
		boolean create = true;
		String errorMsg = "";
		NFileFilterContainer container = parent.getFilterContainer();

		if (container.contains(myFilter.getName())) {
			create = false;
			errorMsg = "The Filter-Name is not unique.";
		}
		if (myFilter.size() == 0) {
			create = false;
			errorMsg = "There are no file-extensions.";
		}
		if (stripString(myFilter.getName()).equals("")) {
			create = false;
			errorMsg = "Type in a name.";
		}
		if (!create)
			JOptionPane.showMessageDialog(this, errorMsg, "Error!", JOptionPane.ERROR_MESSAGE);
		return create;
	}

	public void update() {
		System.out.println("upd");
		nameTF.setEnabled(myFilter.isNameEditable());
		if (createMode) {
			resetBT.setVisible(false);
			// deleteBT.setVisible(false);
			deleteBT.setEnabled(false);
		} else {
			applyBT.setText("Save changes");
			nameTF.setText(myFilter.getName());
			resetBT.setEnabled(myFilter.isResetable());
			deleteBT.setEnabled(myFilter.isDeleteable());
		}
	}

	private void delete() {
		int c = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to delete the filter '" + myFilter.getName() + "'?", "Confirm",
				JOptionPane.YES_NO_OPTION);
		if (c == JOptionPane.YES_OPTION) {
			myFilter = null;
			parent.updateCombobox();
			dispose();
		}
	}

	private void writeTable() {
		table.setModel(new DefaultTableModel(myFilter.size() + 1, 1));
		table.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("Allowed File-Extensions");
		table.getTableHeader().setReorderingAllowed(false);
		for (int i = 0; i < myFilter.size(); i++) {
			table.setValueAt(myFilter.getExtension(i), i, 0);
		}
		update();
	}

	public void readTable() {
		myFilter.clear();
		for (int i = 0; i < table.getRowCount(); i++) {
			String s = (String) table.getValueAt(i, 0);
			if (s != null) {
				s = stripString(s);
				if (!(s == null) && !s.equals(""))
					myFilter.add(s);
			}
		}
		writeTable();
	}

	public static String stripString(String s) {
		return s.replaceAll("\\s", "");
	}
}

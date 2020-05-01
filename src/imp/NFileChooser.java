package imp;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * NFileChooser allows easy Saving / Loading of data. Create a new NFileChooser
 * object for each Class that holds your data.<br>
 * The generic Class required must implement the Interface
 * {@link java.io.Serializable Serializable}.<br>
 * <br>
 * The NFileChooser's main methods are
 * {@link NFileChooser#saveToFile(Serializable, Component)
 * saveToFile(Serializable, Component)} and
 * {@link NFileChooser#loadData(Component) loadData(Component)} witch allow easy
 * and comfortable saving data to a File / loading data from a file chosen by
 * the user. These methods require a {@link java.awt.Component Component} to
 * display a JFileChooser upon.
 * 
 * @author Nils F.
 * @version 1.0
 * @see {@link javax.swing.JFileChooser JFileChooser}
 * @see {@link java.io.Serializable Serializable}
 * @param <T>
 *            - Every NFileChooser object is liked to it's Class that holds
 *            data. This class must implement Serializable
 */

public class NFileChooser<T extends Serializable> implements Serializable {

	// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	// RenderingHints.VALUE_ANTIALIAS_ON);
	// [19:20:27] Max Hoffmann:
	// g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	// RenderingHints.VALUE_INTERPOLATION_BICUBIC)

	private static final long serialVersionUID = 8513739666876256992L;
	private JFileChooser chooser;
	private FileNameExtensionFilter filter;
	private String overwriteDialogTitle = "";
	private String overwriteDialogMessage = "";

	/**
	 * When creating a new NFileChooser don't forget to declare it's Class that
	 * holds data to save / load.<br>
	 * You'll need a {@link javax.swing.filechooser.FileNameExtensionFilter
	 * FileNameExtensionFilter} for each NFileChooser. If you don't have one
	 * follow the FileNameExtensionFilter's JavaDoc at it's constructor. It
	 * comes with an example.<br>
	 * <br>
	 * <b>Note:</b> The <i>FileNameExtensionsFilter</i> is later refered to as a
	 * <i>FileFilter</i>!
	 * 
	 * @param filter
	 *            this NFileChooser's filter
	 */
	public NFileChooser(FileNameExtensionFilter filter) {
		this.filter = filter;
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		overwriteDialogMessage = "The selected file already exists. Overwrite it?";
		overwriteDialogTitle = "Overwrite file?";
	}
	

	/**
	 * 
	 * @param dir
	 * @return
	 */
	public static File[] getAllFilesInDirectory(File dir) {
		if(!dir.isDirectory()){
			throw new IllegalArgumentException("The given file is no directory");
		}

		File[] f = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				File file = new File(filename);
				return !file.isDirectory();
			}
		});

		return f;
	}
	
	/**
	 * 
	 * @param dir
	 * @return
	 */
	public File[] getFilesInDirectoryByFilter(File dir){
		File[] f = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				File file = new File(filename);
				return filter.accept(file);
			}
		});
		return f;
	}

	/**
	 * The NFileChooser's main feature! An easy way to save your data to a file
	 * of the user's chose.<br>
	 * <br>
	 * <b>Note:</b> This function calls
	 * {@link NFileChooser#saveToFile(Serializable, String, Component)
	 * saveToFile(Serializable, String, Component)} with all it's parameters and
	 * the default extension will be the first one in the FileFilter. Click the
	 * link to read the full Javadoc.
	 * 
	 * @since 1.0
	 * @param data
	 *            the data to save in a file
	 * @param parent
	 *            the parent component to display the dialog upon
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public File saveToFile(T data, Component parent) throws IOException {
		String[] extensions = getFileExtensions();
		return saveToFile(data, extensions[0], parent);
	}

	/**
	 * The NFileChooser's main feature. An easy way to save your data to a file
	 * of the user's choice. If the file doesn't exist it will be created. If
	 * the file already exists the user is asked if he / she wants to overwrite
	 * it. The chosen file must be accepted by the FileNameExtensionFilter. If
	 * it is not, the parameter <i>extension</i> is appended to the filename.
	 * 
	 * @param data
	 *            - the data to be saved
	 * @param extension
	 *            - the default file-extension if the file isn't accepted by the
	 *            FileFilter
	 * @param parent
	 *            - component to display the dialog upon
	 * @return the file containing the data - will be <i>null</i> if the user
	 *         refuses to select a file
	 * @throws IOException
	 *             thrown when the file can't be created / written
	 * @throws IllegalArgumentException
	 *             thrown when the default extension isn't accepted by the
	 *             FileFilter
	 * @see {@link NFileChooser#saveToFile(Serializable, Component)
	 *      saveToFile(Serializable, Component)} call this function if you don't
	 *      need a default extension
	 */
	public File saveToFile(T data, String extension, Component parent)
			throws IOException {
		if (!isAceptedExtension(extension)) {
			throw new IllegalArgumentException("'" + extension
					+ "' is not an accepted file-extension!");
		}
		File f = getSaveFile(parent);
		if (f != null && !filter.accept(f)) {
			String s = f.getPath() + "." + extension.toLowerCase();
			f = new File(s);
		}
		if (saveToFile(data, f)) {
			return f;
		}
		return null;
	}

	/**
	 * Opens a JFileChooser so the user can browse his files and select a File
	 * to save data. If the file already exists the user is asked if he / she
	 * wants to overwrite the file. If the file does not exist it is <b>not</b>
	 * created!
	 * 
	 * @param parent
	 *            component to display the FileChooser upon
	 * @return a file selected by the user - if the user cancels the selection
	 *         <i>null</i> is returned
	 * @throws IllegalStateException
	 *             when the FileChooser dialog returns an unexpected value. It's
	 *             very likely this will never happen.
	 * @see {@link java.io.File#createNewFile() createNewFile()}
	 */
	public File getSaveFile(Component parent) {
		boolean running = true;
		File f = null;
		while (running) {
			f = null;
			int c = chooser.showSaveDialog(parent);
			switch (c) {
			case JFileChooser.APPROVE_OPTION:
				f = chooser.getSelectedFile();
				if (f.exists()) {
					int i = JOptionPane.showConfirmDialog(parent,
							overwriteDialogMessage, overwriteDialogTitle,
							JOptionPane.YES_NO_OPTION);
					if (i != JOptionPane.OK_OPTION) {
						break;
					}
				}
				running = false;
				break;
			case JFileChooser.CANCEL_OPTION:
				running = false;
				break;
			case JFileChooser.ERROR_OPTION:
				running = false;
				break;
			default:
				throw new IllegalStateException(
						"Unexpected answer from JFileChooser!");
			}
		}
		return f;
	}

	/**
	 * Takes an Object and writes it in a file. If the file doesn't exist it is
	 * created. If the file already existed it is overwritten. The file must be
	 * in a accepted file-format, that was given to the NFileChooser in the
	 * Constructor.
	 * 
	 * @return true if the data was saved in the file, false otherwise
	 * @param data
	 *            - the object to save
	 * @param outputFile
	 *            - the file to save the object in
	 * @throws IOException
	 *             thrown when the file can't be created / written
	 * @throws IllegalArgumentException
	 *             thrown when the file is not accepted by the FileFilter
	 */
	public boolean saveToFile(T data, File outputFile) throws IOException {
		if (outputFile == null) {
			return false;
		}
		if (!accept(outputFile)) {
			throw new IllegalArgumentException(
					"File not accepted by Filefilter");
		}
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		FileOutputStream fo = new FileOutputStream(outputFile);
		ObjectOutputStream oo = new ObjectOutputStream(fo);
		oo.writeObject(data);
		oo.close();
		fo.close();
		return true;
	}

	/**
	 * One of the NFileChooser's main features. Easy data loading / reading from
	 * a file the user chooses via a {@link javax.swing.JFileChooser
	 * JFileChooser}. The JFileChooser's settings are specified by the
	 * NFileChooser's constructor.<br>
	 * <br>
	 * <b>Note:</b> If the chosen file does not exist, or the JFileChooser's
	 * dialog is closed without selecting a file <i>null</i> is returned.
	 * 
	 * @param parent
	 *            - The Component to display the Dialog upon
	 * @return the data stored in a selected file
	 * @throws ClassNotFoundException
	 *             thrown when the read data cannot be assigned to a Class
	 * @throws FileNotFoundException
	 *             thrown then the File does not exist
	 * @throws IOException
	 *             thrown when the file could not be read
	 * @throws ClassCastException
	 *             thrown when the data could not be cast into the Class
	 * @throws IllegalArgumentException
	 *             thrown when the file is not accepted by the FileFilter.
	 */
	public T loadData(Component parent) throws ClassNotFoundException,
			IOException, ClassCastException {
		File f = getLoadFile(parent);
		if (f == null || !f.exists()) {
			return null;
		}
		return loadData(f);
	}

	/**
	 * Loads the data stored in a {@link java.io.File File}.
	 * 
	 * @param inputFile
	 *            - the File to load data from
	 * @return the data stored in the file
	 * @throws ClassNotFoundException
	 *             thrown when the read data cannot be assigned to a Class
	 * @throws FileNotFoundException
	 *             thrown then the File does not exist
	 * @throws IOException
	 *             thrown when the file could not be read
	 * @throws ClassCastException
	 *             thrown when the data could not be cast into the Class
	 * @throws IllegalArgumentException
	 *             thrown when the file is not accepted by the FileFilter.
	 */
	public T loadData(File inputFile) throws ClassNotFoundException,
			IOException, ClassCastException {
		if (!inputFile.exists()) {
			throw new FileNotFoundException("File does not exist!");
		}
		if (!filter.accept(inputFile)) {
			throw new IllegalArgumentException(
					"File not accepted by the FileFilter");
		}
		FileInputStream fi = new FileInputStream(inputFile);
		ObjectInputStream oi = new ObjectInputStream(fi);
		// DeckHolder holder = new DeckHolder();
		@SuppressWarnings("unchecked")
		T data = (T) oi.readObject();
		oi.close();
		fi.close();
		return data;
	}

	/**
	 * Opens a JFileChooser to get a File. The NFileChooser's FileFilter is
	 * applied.<br>
	 * <br>
	 * <b>Note:</b> If the user refuses to select a file <i>null</i> is
	 * returned.
	 * 
	 * @param parent
	 *            - The Component to display the JFileChooser upon
	 * @return the selected file
	 * @throws IllegalStateException
	 *             thrown if the JFileChooser received an unexpected user-input.
	 *             This will almost never happen.
	 */
	public File getLoadFile(Component parent) {
		File f = null;
		int i = chooser.showOpenDialog(parent);
		switch (i) {
		case JFileChooser.APPROVE_OPTION:
			f = chooser.getSelectedFile();
			if (!f.exists()) {
				return null;
			}
		case JFileChooser.CANCEL_OPTION:
			break;
		case JFileChooser.ERROR_OPTION:
			break;
		default:
			throw new IllegalStateException(
					"Unexpected answer from JFileChooser!");
		}
		// }
		return f;
	}

	/**
	 * Get the filter that was used in the constructor.
	 * 
	 * @return This NFileChooser's filter
	 */
	public FileNameExtensionFilter getFilter() {
		return filter;
	}

	/**
	 * Get all the File-Extensions that are acepted by the NFileChooser. <br>
	 * <br>
	 * <b>Example:</b> C/users/Nils/myFile.<i>EXTENSION</i>
	 * 
	 * @see {@link NFileChooser#getFilter() getFilter()}
	 * @return an array of Strings that contains all accepted extensions
	 */
	public String[] getFileExtensions() {
		return filter.getExtensions();
	}

	/**
	 * Get the text that describes the File-Extensions for this NFileChooser.
	 * For example: "JPG and GIF image files".
	 * 
	 * @see {@link NFileChooser#getFileExtensions() getFileExtensions()}
	 * @see {@link NFileChooser#getFilter() getFilter()}
	 */
	public String getDescription() {
		return filter.getDescription();
	}

	/**
	 * Tests the specified file, returning true if the file is accepted, false
	 * otherwise. True is returned if the extension matches one of the file name
	 * extensions of this FileFilter, or the file is a directory.
	 * 
	 * @param f
	 *            - the file to test
	 * @return true if the file is to be accepted, false otherwise
	 */
	public boolean accept(File f) {
		return filter.accept(f);
	}

	/**
	 * Changes the directory to be set to the parent of the current directory.
	 */
	void changeToParentDirectory() {
		chooser.changeToParentDirectory();
	}

	/**
	 * Checks if a String is in the accepted extensions array. This operation is
	 * not case-sensitive.
	 * 
	 * @param s
	 *            - the String check if it is an accepted extension
	 * @return true if it's an extension, false otherwise
	 * @see {@link NFileChooser#getFileExtensions() getFileExtensions()}
	 */
	public boolean isAceptedExtension(String s) {
		for (String ex : getFileExtensions()) {
			if (ex.toLowerCase().equals(s.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the File's description.
	 * 
	 * @param f
	 *            - the file
	 * @return the String containing the description
	 */
	public String getDescription(File f) {
		return chooser.getDescription(f);
	}

	/**
	 * Gets the file-type for a File.
	 * 
	 * @param f
	 *            - the File
	 * @return a String containing the file-type
	 */
	String getTypeDescription(File f) {
		return chooser.getTypeDescription(f);
	}

	/**
	 * Returns true if the file (directory) can be visited. Returns false if the
	 * directory cannot be traversed.
	 * 
	 * @param f
	 *            - the File
	 * @return true if the file/directory can be traversed, otherwise false
	 */
	public boolean isTraversable(File f) {
		return chooser.isTraversable(f);
	}

	/**
	 * Tells the UI to rescan its files list from the current directory.
	 */
	public void rescanCurrentDirectory() {
		chooser.rescanCurrentDirectory();
	}

	/**
	 * Sets the selected file. If the file's parent directory is not the current
	 * directory, changes the current directory to be the file's parent
	 * directory.
	 * 
	 * @param file
	 *            - the selected File
	 */
	public void setSelectedFile(File file) {
		chooser.setSelectedFile(file);
	}

	/**
	 * Sets the current directory. Passing in null sets the file chooser to
	 * point to the user's default directory. This default depends on the
	 * operating system. It is typically the "My Documents" folder on Windows,
	 * and the user's home directory on Unix. If the file passed in as
	 * currentDirectory is not a directory, the parent of the file will be used
	 * as the currentDirectory. If the parent is not traversable, then it will
	 * walk up the parent tree until it finds a traversable directory, or hits
	 * the root of the file system.
	 * 
	 * @param dir
	 *            - the directory to point to
	 */
	public void setCurrentDirectory(File dir) {
		chooser.setCurrentDirectory(dir);
	}

	/**
	 * Gets the current directory that is displayed when save / load is called.
	 */
	public File getCurrentDirectory() {
		return chooser.getCurrentDirectory();
	}

	/**
	 * When the user wants to save a file with the
	 * {@link NFileChooser#saveToFile(Serializable, File)
	 * saveToFile(Serializable, File)}- (or any other saveToFile-) method and
	 * the file alreay exists he / she is asked to overwrite the file. This
	 * method changes the dialog's text.
	 * 
	 * @param title
	 *            - the dialog's new title
	 * @param message
	 *            - the dialog's new message
	 * 
	 * @see {@link NFileChooser#getOverwriteDialogTitle()
	 *      getOverwriteDialogTitle()}
	 * @see {@link NFileChooser#getOverwriteDialogMessage()
	 *      getOverwriteDialogMessage()}
	 */
	public void setOverwriteDialogText(String title, String message) {
		overwriteDialogTitle = title;
		overwriteDialogMessage = message;
	}

	/**
	 * When the user wants to save a file with the
	 * {@link NFileChooser#saveToFile(Serializable, File)
	 * saveToFile(Serializable, File)}- (or any other saveToFile-) method and
	 * the file alreay exists he / she is asked to overwrite the file. This
	 * method returns the dialog's title.
	 * 
	 * @return the dialog's title as a String
	 * @see {@link NFileChooser#setOverwriteDialogText(String, String)
	 *      setOverwriteDialogText(String, String)}
	 * @see {@link NFileChooser#getOverwriteDialogMessage()
	 *      getOverwriteDialogMessage()}
	 */
	public String getOverwriteDialogTitle() {
		return overwriteDialogTitle;
	}

	/**
	 * When the user wants to save a file with the
	 * {@link NFileChooser#saveToFile(Serializable, File)
	 * saveToFile(Serializable, File)}- (or any other saveToFile-) method and
	 * the file alreay exists he / she is asked to overwrite the file. This
	 * method returns the dialog's message.
	 * 
	 * @return the dialog's message as a String
	 * @see {@link NFileChooser#setOverwriteDialogText(String, String)
	 *      setOverwriteDialogText(String, String)}
	 * @see {@link NFileChooser#getOverwriteDialogTitle()
	 *      getOverwriteDialogTitle()}
	 */
	public String getOverwriteDialogMessage() {
		return overwriteDialogMessage;
	}

}

package concurrent;

import java.io.File;
import java.util.ArrayList;

public abstract class FileSearchTask implements Runnable {

	private boolean foldersOnly;
	private File folderToSearch;
	private String[] filterNames, filterExtensions;
	private ArrayList<File> filesFound;
	public FileSearchTask(boolean foldersOnly, File folderToSearch, String[] filterNames,
			String[] filterExtensions) {
		super();
		this.foldersOnly = foldersOnly;
		this.folderToSearch = folderToSearch;
		this.filterNames = filterNames;
		this.filterExtensions = filterExtensions;
		filesFound = new ArrayList<>();
	}

	@Override
	public void run() {
		if (!folderToSearch.exists() || !folderToSearch.isDirectory()) {
			onTaskFinished();
			return;
		}

		for (File f : folderToSearch.listFiles()) {
			if (f.isDirectory()) {
				onFolderFound(f);
				continue;
			}

			if (foldersOnly) {
				continue;
			}

			if (acceptFilter(f)) {
				filesFound.add(f);
			}
		}

		File[] files = new File[filesFound.size()];
		for (int i = 0; i < filesFound.size(); i++) {
			files[i] = filesFound.get(i);
		}

		onFilesFound(files);
		onTaskFinished();
	}

	private boolean acceptFilter(File f) {
		String s = f.getName().toLowerCase();

		boolean acceptUserFilters = false;
		if (filterNames.length >= 1)
			for (String filter : filterNames) {
				acceptUserFilters |= s.contains(filter);
			}

		if (!acceptUserFilters) {
			return false;
		}
		if (acceptUserFilters && filterExtensions.length == 0) {
			return true;
		}

		for (String filter : filterExtensions) {
			if (f.getName().toLowerCase().endsWith(filter.toLowerCase())) {
				return true;
			}
		}

		return false;
	}

	public abstract void onTaskFinished();

	public abstract void onFolderFound(File folder);

	public abstract void onFilesFound(File[] files);

}

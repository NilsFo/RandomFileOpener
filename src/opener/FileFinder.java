package opener;

import java.io.File;
import java.io.FilenameFilter;

public class FileFinder {

	static int c;

	public static File[] findFiles(String dirName) throws Exception {
		File dir = new File(dirName);
		c=0;
		File[] f = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				// return true;
				c++;
				Opener.print(c + " Checkin' File: "+filename);
				return true;
			}
		});
		if (f == null)
			throw new Exception();
		return f;
	}

}

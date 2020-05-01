package opener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import frames.NFileFilterEditFrame;

public class NFileFilter implements Serializable, Comparable<NFileFilter> {

	private static final long serialVersionUID = 7890267238863104626L;

	private boolean defaultCustom;
	private boolean defaultNone;
	private boolean deleteable;
	private String name;
	private ArrayList<String> extensions;
	private ArrayList<String> defaultExtensions;

	public NFileFilter() {
		deleteable = true;
		defaultCustom=false;
		defaultNone=false;
		name = "New File-Filter";
		extensions = new ArrayList<>();
		defaultExtensions = new ArrayList<>();
	}
	
	public NFileFilter(NFileFilter f){
		deleteable=f.isDeleteable();
		name=f.getName();
		defaultCustom=f.defaultCustom;
		defaultNone=f.defaultNone;
		extensions=new ArrayList<>(f.extensions);
		defaultExtensions=new ArrayList<>(f.defaultExtensions);
	}

	public static NFileFilter createDefaultMusicFilter() {
		NFileFilter f = new NFileFilter();
		f.name = "Audio";
		f.deleteable = false;
		f.addToDefaultListe(".mp3");
		f.addToDefaultListe(".ogg");
		f.addToDefaultListe(".oga");
		f.addToDefaultListe(".wav");
		f.addToDefaultListe(".wma");
		f.sortieren();
		return f;
	}

	public static NFileFilter createDefaultImagesFilter() {
		NFileFilter f = new NFileFilter();
		f.name = "Images";
		f.addToDefaultListe(".bmp");
		f.addToDefaultListe(".gif");
		f.addToDefaultListe(".jpg");
		f.addToDefaultListe(".jpeg");
		f.addToDefaultListe(".png");
		f.addToDefaultListe(".tif ");
		f.addToDefaultListe(".tga");
		f.addToDefaultListe(".bpx");
		f.sortieren();
		f.deleteable = false;
		return f;
	}

	public static NFileFilter createDefaultMoviesFilter() {
		NFileFilter f = new NFileFilter();
		f.name = "Movies";
		f.addToDefaultListe(".webm");
		f.addToDefaultListe(".mkv");
		f.addToDefaultListe(".flv");
		f.addToDefaultListe(".avi");
		f.addToDefaultListe(".mov");
		f.addToDefaultListe(".wmv");
		f.addToDefaultListe(".mp4");
		f.addToDefaultListe(".m4p");
		f.addToDefaultListe(".mpg");
		f.addToDefaultListe(".mpeg");
		f.addToDefaultListe(".m4v");
		f.addToDefaultListe(".ogg");
		f.sortieren();
		f.deleteable = false;
		return f;
	}

	public static NFileFilter createDefaultNoneFilter() {
		NFileFilter f = new NFileFilter();
		f.name = "<None>";
		f.deleteable = false;
		f.defaultNone = true;
		return f;
	}

	public static NFileFilter createDefaultCustomFilter() {
		NFileFilter f = new NFileFilter();
		f.name = "<Custom>";
		f.deleteable = false;
		f.defaultCustom = true;
		return f;
	}

	public String getExtensionString() {
		String result = "";
		for (String s : extensions) {
			result = result + s + ", ";
		}
		if (extensions.size() >= 1)
			result = result.substring(0, result.length() - 2);
		return result;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(NFileFilter f) {
		if (isDefaultCustom()) {
			return Integer.MAX_VALUE;
		}
		if (isDefaultNone()) {
			return Integer.MIN_VALUE;
		 }
		return getName().compareTo(f.getName());
	}

	public void sortieren() {
		Collections.sort(extensions);
		Collections.sort(defaultExtensions);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDefaultCustom() {
		return defaultCustom;
	}

	public boolean isDefaultNone() {
		return defaultNone;
	}

	public String getExtension(int i) {
		return extensions.get(i);
	}

	private void addToDefaultListe(String s) {
		defaultExtensions.add(s);
		add(s);
	}

	public void add(String s) {
		extensions.add(s);
	}

	public void prepareExtensions() {
		System.out.println(extensions);
		ArrayList<String> l = new ArrayList<>();
		for (String s : extensions) {
			if (!s.startsWith(".")) {
				s = "." + s;
			}
			l.add(s);
		}
		extensions = new ArrayList<>(l);
		System.out.println(extensions);
	}

	public boolean isNameEditable() {
		if (isDefaultCustom() | isDefaultNone()) {
			return false;
		}
		if (defaultExtensions.size() != 0) {
			return false;
		}
		return true;
	}

	public boolean isResetable() {
		return defaultExtensions.size() != 0;
	}

	public void reset() {
		extensions = new ArrayList<>(defaultExtensions);
	}

	public void clear() {
		extensions.clear();
	}

	public int size() {
		return extensions.size();
	}

	public boolean isDeleteable() {
		return deleteable;
	}
	
	public static String convertToExtension(String s){
		String r = new String(s.toLowerCase());
		r=NFileFilterEditFrame.stripString(r);
		if(!r.startsWith(".") && !r.equals("")){
			r="."+r;
		}
		return r;
	}
}

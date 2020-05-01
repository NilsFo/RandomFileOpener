package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import opener.NFileFilter;

public class NFileFilterContainer implements Serializable {

	private static final long serialVersionUID = 746197131835436812L;

	private ArrayList<NFileFilter> liste;
	
	private NFileFilterContainer(){
		liste = new ArrayList<>();
	}
	
	public static NFileFilterContainer getDefaultContainer(){
		NFileFilterContainer container = new NFileFilterContainer();
		container.addFilter(NFileFilter.createDefaultCustomFilter());
		container.addFilter(NFileFilter.createDefaultImagesFilter());
		container.addFilter(NFileFilter.createDefaultMoviesFilter());
		container.addFilter(NFileFilter.createDefaultMusicFilter());
		container.addFilter(NFileFilter.createDefaultNoneFilter());
		container.sortieren();
		return container;
	}
	
	public ArrayList<NFileFilter> getListe(){
		return liste;
	}
	
	public void addFilter(NFileFilter f){
		liste.add(f);
	}
	
	public void removeFilter(NFileFilter f){
		liste.remove(f);
	}
	
	public void sortieren(){
		Collections.sort(liste);
		for(NFileFilter f:liste){
			f.sortieren();
		}
	}
	
	public NFileFilter get(String s){
		System.out.println("TEST: "+liste.size());
		if(s==null){
			return null;
		}
		for(NFileFilter f: liste){
			System.out.println("Name: "+f.getName()+" - "+s);
			if(f.getName().toLowerCase().equals(s.toLowerCase())){
				return f;
			}
		}
		return null;
	}
	
	public boolean contains(String contains){
		for(NFileFilter f: liste){
			if(f.getName().replaceAll("\\s","").toLowerCase().equals(contains.replaceAll("\\s","").toLowerCase())){
				return true;
			}
		}
		return false;
	}
}

package opener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JTable;

import frames.NFileFilterEditFrame;

public class NFileFilterTable extends JTable {

	private static final long serialVersionUID = -7211152738328054360L;
	private NFileFilterEditFrame parent;	
	
	public NFileFilterTable(NFileFilterEditFrame parent){
		this.parent=parent;
		addPropertyChangeListener(new PropertyChangeListener() {

		    @Override
		    public void propertyChange(PropertyChangeEvent evt) {
		        if ("tableCellEditor".equals(evt.getPropertyName())) {
		            if (isEditing())
		                processEditingStarted();
		            else
		                processEditingStopped();
		        }
		    }
		});
		
	}
	

	protected void processEditingStopped() {
	    //System.out.println("save " + editingRow + ":" + editingColumn);
	    parent.readTable();
	}

	protected void processEditingStarted() {
	    //System.out.println("edit " + editingRow + ":" + editingColumn);
	    //if (editingRow > -1 && editColumn > -1)
	    //    oldValue = (String) model.getValueAt(editRow, editColumn);
	}

}

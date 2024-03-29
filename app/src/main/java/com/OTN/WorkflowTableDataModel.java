package com.OTN;

import javax.swing.*;
import javax.swing.table.*;
import java.time.*;

import java.util.*;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowTableDataModel extends AbstractTableModel {
	// table 
	private String[] columnNames = { "Name" , "Time created", "Borders"};
	// data
	private LinkedList<AreaElement> workflowelements ;

	WorkflowTableDataModel() {
		Logger logger = LoggerFactory.getLogger(WorkflowTableDataModel.class);
    	logger.info("WorkflowTableDataModel created");
		workflowelements = new LinkedList<AreaElement>();
	}

	// table 
	@Override
	 public int getColumnCount() {
        return columnNames.length;
    }
    @Override
    public int getRowCount() {
        return workflowelements.size();
    }
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) { 
	    	case  0 : 
	    		return workflowelements.get(row).getName();
	 
	    	
	    	case 1:
	    		return workflowelements.get(row).getCreationTime();


	    	case 2:
	    		return workflowelements.get(row).isComplete();

	    }
    return null;
    }

    public boolean isCellEditable(int row, int col) { 
    	switch (col) {
	    	case  0 : // name
	    		return true;
	 
	    	
	    	case 1: // creation time
	    		return false;


	    	case 2: // Borders
	    		return false;
	    }
	    return false ; 
	}

    @Override
	public Class getColumnClass(int c) {

        return getValueAt(0, c).getClass();
    }

  	public void setValueAt(Object value, int row, int col) {
        switch (col) { 
	    	case  0 : // name
	    		 workflowelements.get(row).setName( value.toString() );
	    		 break;
	 
	    	
	    	case 1:
	    		return;  // creation time


	    	case 2:
	    		return; // Borders
 
	    }
        fireTableCellUpdated(row, col);
    }

    // hybrid
    public void deletRow ( int row){
    	workflowelements.remove( row );
    	fireTableDataChanged();
    }

    // data

	public void addAreaElement (File osmfile ) {
	AreaElement areaElement = new AreaElement(osmfile);
	areaElement.setCreationTime ( LocalDateTime.now().toString() );
	workflowelements.add(areaElement);
	fireTableDataChanged();
	}

	public AreaElement addAreaElement (AreaElement areaFather ) {
	AreaElement areaElement = new AreaElement( areaFather.getName() );
	areaElement.setCreationTime ( LocalDateTime.now().toString() );
	workflowelements.add(areaElement);
	return areaElement;
	}

	public AreaElement getLastAreaElement(){
		return workflowelements.getLast();
	}
}
		 

package com.OTN;

import javax.swing.*;
import javax.swing.table.*;
import java.time.*;
import java.time.format.DateTimeFormatter;  

import java.util.*;
import java.io.*;

public class WorkflowTableDataModel extends AbstractTableModel {
	// table 
	private String[] columnNames = { "Name" , "Time created", "Valid"};
	// data
	private List<AreaElement> workflowelements = new LinkedList<AreaElement>();

	WorkflowTableDataModel() {}

	// table 

	 public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return workflowelements.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        switch (col) {
	    	case  0 : 
	    	return workflowelements.get(row).getName();

	    	
	    	case 1:
	    	return workflowelements.get(row).getCreationTime();


	    	case 2:
	    	return workflowelements.get(row).isValid();
	    }
        return null;
    
    }

	public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    // data

	public void addAreaElement (File osmfile ) {
	AreaElement AreaElement = new AreaElement(osmfile);
	AreaElement.setCreationTime ( LocalDateTime.now().toString());
	workflowelements.add(AreaElement);
	}

	public List<AreaElement> getAreaElement(){
		return workflowelements;
	}
}
		 

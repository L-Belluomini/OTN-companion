package com.OTN;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.Container;

public class OTNGuiProfileEditor {
	private ProfilesTableDataModel tableData;
	private int row;
	OTNGuiProfileEditor(ProfilesTableDataModel etableData ){
		this.tableData = etableData;
		
	}

	public editProfile ( int erow ) {
		
		this.row = erow;
	}

}
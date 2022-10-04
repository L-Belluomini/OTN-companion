package com.OTN;

import javax.swing.*;
import java.awt.*;

import java.util.List;
import java.util.LinkedList;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JDialog;

public class OTNUserErrorGeneration {
	
	LinkedList<String> errors;
	String title;

	public OTNUserErrorGeneration(String etitle){
		this.title = etitle;
		this.errors = new LinkedList();	
	}

	public void addError( String error ){

		this.errors.add(error);
	}

	public boolean showDialog() {
		
		if(errors.size()==0){
			return false;
		}

		JFrame frame = new JFrame();

		JOptionPane.showOptionDialog(frame, getPanel(), title, JOptionPane.OK_CANCEL_OPTION,  JOptionPane.ERROR_MESSAGE, null, null, null);

		return true;
	}

	private JPanel getPanel() {

	JPanel panel = new JPanel(new GridBagLayout());

	GridBagConstraints c = new GridBagConstraints();

	c.anchor = GridBagConstraints.FIRST_LINE_START;
  	c.gridx = 0;
	c.gridy = 0;

	JLabel errorText = new JLabel();

	for(String text:errors){
		errorText = new JLabel (text);
		panel.add(errorText);
		c.gridy ++;
		panel.add(errorText, c);
		}

	return panel;
	}


}
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

	public boolean showErrorDialog() {
		
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
	c.insets = new Insets(0,0,5,0);

	JLabel explanationText = new JLabel("<html>User imput error(s) detected.<br>Please resolve the following issues and try again.</html>");

	panel.add(explanationText, c);

	c = new GridBagConstraints();
	c.anchor = GridBagConstraints.FIRST_LINE_START;
  	c.gridx = 0;
	c.gridy = 1;

	JLabel errorText = new JLabel();

	for(String text:errors){
		errorText = new JLabel ("- " + text);
		panel.add(errorText);
		c.gridy ++;
		panel.add(errorText, c);
		}

	return panel;
	}


}
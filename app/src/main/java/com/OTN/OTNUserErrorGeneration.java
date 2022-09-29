package com.OTN;

import javax.swing.*;
import java.awt.*;

import java.util.List;
import java.util.LinkedList;

import javax.swing.JFrame;
//import javax.swing.JOptionPane;
import javax.swing.JDialog;

public class OTNUserErrorGeneration {
	
	LinkedList<String> errors;

	public OTNUserErrorGeneration(){
	
		this.errors = new LinkedList();

	}

	public void addError( String error ){

		this.errors.add(error);
	}

	public boolean showDialog() {
		
		if(errors.size()==0){
			return false;
		}

		JDialog errorDialog = new JDialog();

		errorDialog.setLayout( new GridBagLayout() );
		errorDialog.setTitle("User error generation");
		errorDialog.setSize(new Dimension(250,120));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
	  	c.gridx = 0;
		c.gridy = 0;

		// add title, explanation, and formattation"


		JLabel errorText = new JLabel();

		for(String idk:errors){
				errorText = new JLabel (idk);
				c.gridy ++;
				errorDialog.add(errorText, c);
		}

				
		errorDialog.setVisible(true);

		return true;
	}


}
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

		/*JDialog errorDialog = new JDialog();

		errorDialog.setLayout( new GridBagLayout() );
		errorDialog.setTitle(title);
		errorDialog.setSize(new Dimension(250,120));

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
	  	c.gridx = 0;
		c.gridy = 0;*/

		// add title, explanation, and formattation"


		JLabel errorText = new JLabel();

		for(String text:errors){
				errorText = new JLabel (text);
				//c.gridy ++;
				//errorDialog.add(errorText, c);
		}

		JFrame frame = new JFrame();

		JOptionPane.showMessageDialog(frame, errorText, title, JOptionPane.ERROR_MESSAGE);

				
		//errorDialog.setVisible(true);

		return true;
	}


}
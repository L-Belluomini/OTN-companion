package com.OTN;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class OTNGuiFilter {

	private OSMEditor editor ;
	private JTextField topTF;
	private JTextField bottomTF;
	private JTextField leftTF;
	private JTextField rightTF;
	private File _tempFile;
	private File _openOSM;


		OTNGuiFilter( File openOSM , File tempFile ) {
			System.out.println("filter gui initialized");
			editor = new OSMEditor();
			editor.loadFile ( openOSM );
			System.out.println("editor initialized");
			this._tempFile = tempFile;
			this._openOSM = openOSM;

			JFrame filterFrame= new JFrame("filter");    
	        //JPanel filterPanel=new JPanel();  
	        filterFrame.setLayout( new GridBagLayout() ); 
			filterFrame.setVisible(true);//making the frame visible

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
	    	c.gridy = 0;
	    	c.fill = GridBagConstraints.BOTH;

			JTabbedPane filterPane = new JTabbedPane();
			filterFrame.add(filterPane , c);


	    	filterFrame.pack();
	    	
	    	//////////////// SET PANES ////////////////////////////////////////
	    	
	    	JPanel bbFilterPannel  =new JPanel( new GridBagLayout() );  
	    	filterPane.add( "Boundiing Box",bbFilterPannel );
	    	bbFilterPannel.setSize(300,300);

	    	JLabel topLabel = new JLabel("top boundires");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 2;
	    	bbFilterPannel.add ( topLabel , c);

	    	this.topTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 2;
	    	c.fill = GridBagConstraints.BOTH;
	    	bbFilterPannel.add ( topTF , c);

	    	JLabel bottomLabel = new JLabel("bottom boundires");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 2;
	    	bbFilterPannel.add ( bottomLabel , c);

	    	this.bottomTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 4;
	    	c.fill = GridBagConstraints.BOTH;
	    	bbFilterPannel.add ( bottomTF , c);

	    	JLabel leftLabel = new JLabel("left boundires");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 2;
	    	bbFilterPannel.add ( leftLabel , c);

	    	this.leftTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 0;
	    	c.gridy = 3;
	    	c.fill = GridBagConstraints.BOTH;
	    	bbFilterPannel.add ( leftTF , c);

	    	JLabel rightLabel = new JLabel("right boundires");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 2;
	    	bbFilterPannel.add ( rightLabel , c);

	    	this.rightTF = new JTextField(15);    
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 3;
	    	c.fill = GridBagConstraints.BOTH;
	    	bbFilterPannel.add ( rightTF , c);

	    	c = new GridBagConstraints();
	    	c.fill = GridBagConstraints.VERTICAL;
	    	c.anchor = GridBagConstraints.LAST_LINE_END;
	  	  	c.gridx = 0;
	    	c.gridy = 1;

	    	JButton filterButton = new JButton("select");
	    	filterFrame.add(filterButton, c);
	    	filterButton.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent e){
					Float top = Float.parseFloat ( topTF.getText() );
					Float bottom = Float.parseFloat ( bottomTF.getText() );
					Float left = Float.parseFloat ( leftTF.getText() );
					Float right =Float.parseFloat ( rightTF.getText() );

					if ( top != null && bottom != null && left != null && right != null ){
						editor.loadFile( _openOSM );
						editor.setFilter(left , right , top , bottom );
						
						editor.setOutput( _tempFile );
						editor.runFilter();
					} else {
						_tempFile = _openOSM;
					}
	        	}  
	    	});
	}
}
package com.OTN;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

//import OTNcompanion;

public class OTNGui {

	private JFrame frame;
	private JTabbedPane tabs;
	private OTNCompanion otnc;
	final JFileChooser fc = new JFileChooser();
	private File openOSM = null;
	
	public static void main(String[] args) {
	new OTNGui();
    }
	
	OTNGui () {
		this.otnc = new OTNCompanion();
		this.frame = new JFrame();
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); 
		frame.setLayout( new GridBagLayout() ); 
		frame.setVisible(true);//making the frame visible

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
    	c.gridy = 0;
    	c.fill = GridBagConstraints.BOTH;

		this.tabs = new JTabbedPane();
    	frame.add(tabs , c);
    	fillOSMPane();
    	fillProfilesPane();
    	fillGraphPane();
    	otnc.createAddProfiles(true);


    	c = new GridBagConstraints();
    	JButton button = new JButton("generate");
    	button.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				otnc.createGraph(false);
        	}  
    	});  
    	c.fill = GridBagConstraints.VERTICAL;
    	c.anchor = GridBagConstraints.LAST_LINE_END;
  	  	c.gridx = 0;
    	c.gridy = 1;
    	frame.add(button, c);
    	frame.pack();
	}	

	private void fillOSMPane() {
		JPanel paneOSM =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Area",paneOSM );

    	GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
    	c.gridy = 0;
    	c.fill = GridBagConstraints.BOTH;

    	JButton loadButton = new JButton("load OSM file");
    	loadButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	            int returnVal = fc.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	File file = fc.getSelectedFile();
	            	if ( file.exists() ){
	            		openOSM = file;
	            	}

	            }
        	}  
    	});  
    	paneOSM.add ( loadButton , c);

    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
    	c.gridy = 1;
    	c.fill = GridBagConstraints.BOTH;

    	JButton selectButton = new JButton("selct area");
    	selectButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				otnc.setFileDir( openOSM.getPath());
        	}  
    	});  

    	paneOSM.add ( selectButton , c);
    	

	}

	private void fillProfilesPane() {
		JPanel paneProfiles =new JPanel(  new GridBagLayout() );  
    	this.tabs.add( "Profiles",paneProfiles );

    	ProfilesTableDataModel tableData = new ProfilesTableDataModel() ;

      	JTable table = new JTable(tableData);
      	table.setFillsViewportHeight(true);
      	JScrollPane scrollpane = new JScrollPane(table);

      	/*GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
    	c.gridy = 0;
    	c.fill = GridBagConstraints.BOTH;
      	paneProfiles.add(scrollpane , c );
      	*/
      	paneProfiles.add(scrollpane);
	}

	private void fillGraphPane() {
		JPanel graphPane =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Graph",graphPane );
    	
    	GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
    	c.gridy = 0;
    	c.fill = GridBagConstraints.BOTH;

    	JButton loadButton = new JButton("select storage Dir");
    	loadButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	            int returnVal = fc.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	File file = fc.getSelectedFile();
	            	if ( file.exists() ){
	            		otnc.setStorageDir( file.getPath());
	            	}

	            }
        	}  
    	});  
    	loadButton.setActionCommand("load");
    	graphPane.add ( loadButton , c);
    }

}
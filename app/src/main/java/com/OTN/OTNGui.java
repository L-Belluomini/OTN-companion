package com.OTN;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.Container;
import java.awt.Dimension;

//import OTNcompanion;

public class OTNGui {

	private JFrame frame;
	private JTabbedPane tabs;
	private OTNCompanion otnc;
	final JFileChooser fc = new JFileChooser();
	private File openOSM = null;
	private File tempFile;
	
	public static void main(String[] args) {
	new OTNGui();
    }
	
	OTNGui () {
		this.otnc = new OTNCompanion();
		this.frame = new JFrame("OTN-Companion");
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); 
		frame.setVisible(true);//making the frame visible
		frame.setSize(new Dimension(500,300));

		Container content = frame.getContentPane();

		content.setLayout( new GridBagLayout() );

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 1;
    	c.weighty = 1; 
    	c.fill = GridBagConstraints.BOTH;

		this.tabs = new JTabbedPane();
    	content.add(tabs , c);
    	fillOSMPane();
    	fillProfilesPane();
    	fillGraphPane();
    	// for testing purposes
    	otnc.createAddProfiles(true);

		c = new GridBagConstraints();
    	c.fill = GridBagConstraints.VERTICAL;
    	c.anchor = GridBagConstraints.LAST_LINE_END;
  	  	c.gridx = 0;
    	c.gridy = 1;

    	JPanel buttonPanel = new JPanel();

    	JButton button = new JButton("generate");
    	button.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				otnc.createGraph(false);
				otnc.storeProfiles();
        	}  
    	});

    	buttonPanel.add(button);

    	content.add(buttonPanel, c);
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
		c.gridx = 1;
    	c.gridy = 0;

    	JButton polyButton = new JButton("load poly file");
    	polyButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	            int returnVal = fc.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	File file = fc.getSelectedFile();
	            	if ( file.exists() ){
	            		// to do
	            	}

	            }
        	}  
    	});  

    	paneOSM.add ( polyButton , c);

    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 1;
    	c.gridy = 2;

    	JButton filterButton = new JButton("add BB Filter");
    	filterButton.addActionListener( new ActionListener() {  
			public void actionPerformed(ActionEvent e){ 
				if ( openOSM == null ) {
					return;
				}
				try {
					tempFile = File.createTempFile("otnc", ".osm");
				} catch (IOException ex) {
					System.out.println(ex.toString());
				}
				tempFile.deleteOnExit();
				OTNGuiFilter filter = new OTNGuiFilter( openOSM,tempFile );
	        }  
    	}); 

    	paneOSM.add ( filterButton , c);


    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
    	c.gridy = 1;
    	c.fill = GridBagConstraints.BOTH;
    	c.ipady= 3;

    	paneOSM.add ( new JSeparator (SwingConstants.HORIZONTAL) , c );

    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 1;
    	c.gridy = 3;
    	c.fill = GridBagConstraints.BOTH;

    	JButton selectButton = new JButton("selct raw area");
    	selectButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				if (openOSM == null ){
					System.out.println("no osm file set");
					return;
				}
				otnc.setFileDir( openOSM.getPath());
        	}  
    	});  

    	paneOSM.add ( selectButton , c);


    	JButton analyzebutton = new JButton("analyze");
    	analyzebutton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				if (openOSM == null ){
					System.out.println("no osm file set");
					return;
				}
				OSMAnalyzer analizer = new OSMAnalyzer ( openOSM );
				analizer.execute();
        	}  
    	});  
    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
    	c.gridx = 1;
    	c.gridy = 4;
    	paneOSM.add (analyzebutton, c);

	}

	

	private void fillProfilesPane() {
		JPanel paneProfiles =new JPanel(  new GridBagLayout() );  
    	this.tabs.add( "Profiles",paneProfiles );

    	ProfilesTableDataModel tableData = new ProfilesTableDataModel() ;

    	GridBagConstraints c = new GridBagConstraints();

    	c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.fill = GridBagConstraints.BOTH; 

      	JTable table = new JTable(tableData);
      	table.setFillsViewportHeight(true);
      	JScrollPane scrollpane = new JScrollPane(table);

      	paneProfiles.add(scrollpane, c);
	}

	private void fillGraphPane() {
		JPanel graphPane =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Graph",graphPane );
    	
    	GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
    	c.gridy = 0;
    	c.fill = GridBagConstraints.BOTH;

    	JButton setStorageDirButton = new JButton("select storage Dir");
    	setStorageDirButton.addActionListener(new ActionListener(){  
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
    	setStorageDirButton.setActionCommand("load");
    	graphPane.add ( setStorageDirButton , c);
    }

    public void setFilterOutput ( File tempFile ){
    	if ( tempFile == null ){
    		return;
    	}
    	this.tempFile = tempFile;

    }

}
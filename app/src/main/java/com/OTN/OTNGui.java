package com.OTN;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JFrame;


public class OTNGui {

	private JFrame frame;
	private JTabbedPane tabs;
	private OTNCompanion otnc;
	final JFileChooser fc = new JFileChooser();
	final private LinkedList<AreaElement> areaElements;
	private AreaElement selectedElement;
	private File vnsKmlFile;
	private JRadioButton otnRadioButton;
	private JRadioButton vnsRadioButton;
	private JTextField areaNameTF;

	public static void main(String[] args) {
	new OTNGui();
    }
	
	OTNGui () {
		
		this.otnc = new OTNCompanion();
		this.areaElements = new LinkedList();

		this.frame = new JFrame("OTN-Companion");
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); 
		frame.setVisible(true);
		frame.setSize(new Dimension(500,300));
		frame.setLayout(null);
		frame.setIconImage(new ImageIcon(getClass().getResource("/otnLogo.png")).getImage());

		Container content = frame.getContentPane();

		content.setLayout( new GridBagLayout());

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

    	//otnc.createAddProfiles(true); // for testing purposes//////////////////////////////////////////

		c = new GridBagConstraints();
    	c.fill = GridBagConstraints.VERTICAL;
    	c.anchor = GridBagConstraints.LAST_LINE_END;
  	  	c.gridx = 0;
    	c.gridy = 1;

    	JPanel buttonPanel = new JPanel();

    	JButton button = new JButton("generate");
    	button.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				if ( otnRadioButton.isSelected() ){
					otnc.createGraph();
				} else if ( vnsRadioButton.isSelected() && vnsKmlFile.exists() ) {
				otnc.createVNSGraph(vnsKmlFile);
				}

        	}  
    	});

    	buttonPanel.add(button);

    	content.add(buttonPanel, c);
    	frame.pack();
	}	

	private void fillOSMPane() {
		JPanel paneOSM =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Area",paneOSM );
    	
		areaNameTF = new JTextField(15);
		GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 1;
    	c.gridy = 1;
    	c.insets = new Insets(0,0,0,5);
    	c.fill = GridBagConstraints.BOTH;
    	paneOSM.add ( areaNameTF , c);


    	c = new GridBagConstraints();
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
	            		areaElements.add ( new AreaElement ( file  ) );
	            		areaElements.getLast().setName( areaNameTF.getText() );
	            		selectedElement = areaElements.getLast();
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
	            		selectedElement.setpolyBoundary(file);
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
    	filterButton.setBackground( Color.CYAN );
    	filterButton.addActionListener( new ActionListener() {  
			public void actionPerformed(ActionEvent e){ 
				if ( ! selectedElement.isValid() ) {
					return;
				}
				AreaElement inpunt = areaElements.getLast();
				AreaElement output = new AreaElement();
				areaElements.add (output);

				OTNGuiFilter filter = new OTNGuiFilter( inpunt , output );
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

    	/*
    	JButton selectButton = new JButton("selct raw area");
    	selectButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				if (areaElements.getLast() == null ){
					System.out.println("no osm file set");
					return;
				}
				otnc.setOsmArea( areaElements.getFirst() );
        	}  
    	});  

    	paneOSM.add ( selectButton , c); 
    	*/


    	JButton analyzebutton = new JButton("analyze");
    	analyzebutton.setBackground( Color.CYAN );
    	analyzebutton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				if (areaElements.getLast() == null ){
					System.out.println("no osm file set");
					return;
				}
				OSMAnalyzer analizer = new OSMAnalyzer ( areaElements.getLast().getOsmFile() );
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
    	tableData.addProfile("car", "car", "fastest", false, false, true);
		tableData.addProfile("foot", "foot", "fastest", false, false, true);

		/////////////////////////// TABLE & SCROLLPANE /////////////////////////////

		JTable table = new JTable(tableData);
      	table.setFillsViewportHeight(true);

      	JScrollPane scrollpane = new JScrollPane(table);

    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.fill = GridBagConstraints.BOTH;

      	paneProfiles.add(scrollpane, c);

      	/////////////////////////// BUTTONS ////////////////////////////////

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.LAST_LINE_END;
	  	c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5,5,5,5); //profilesButtonsPanel costraints

      	JPanel profilesButtonsPanel = new JPanel(new GridBagLayout());

      		JButton deleteProfileButton = new JButton("delete Profile");
			GridBagConstraints cb = new GridBagConstraints();
			cb.fill = GridBagConstraints.VERTICAL;
			cb.anchor = GridBagConstraints.LAST_LINE_END;
		  	cb.gridx = 0;
			cb.gridy = 0;

			profilesButtonsPanel.add(deleteProfileButton, cb);


      		JButton newProfileButton = new JButton("new Profile");
      		cb = new GridBagConstraints();
			cb.fill = GridBagConstraints.VERTICAL;
			cb.anchor = GridBagConstraints.LAST_LINE_END;
		  	cb.gridx = 1;
			cb.gridy = 0;
			cb.insets = new Insets(0,5,0,0);

			profilesButtonsPanel.add(newProfileButton, cb);


      	paneProfiles.add(profilesButtonsPanel,c);
	}

	private void fillGraphPane() {
		JPanel graphPane =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Graph",graphPane );

    	GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
    	c.gridy = 0;
    	c.fill = GridBagConstraints.BOTH;

    	ButtonGroup graphCompatGroup = new ButtonGroup();
    	this.otnRadioButton = new JRadioButton("OTN", true );
    	this.vnsRadioButton = new JRadioButton("VNS");
    	graphCompatGroup.add( otnRadioButton );
    	graphCompatGroup.add( vnsRadioButton );

    	graphPane.add ( otnRadioButton , c);
    	c.gridx = 1;
    	graphPane.add ( vnsRadioButton , c);

    	final JButton vnskmlButton = new JButton("load kml (VNS only)");
    	vnskmlButton.setEnabled(false);
    	vnskmlButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	            int returnVal = fc.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	File file = fc.getSelectedFile();
	            	if ( file.exists() ){
	            		vnsKmlFile = file;
	            	}
	            }
        	}  
    	});

    	otnRadioButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	if ( otnRadioButton.isSelected() ){
	        		vnskmlButton.setEnabled(false);
	        	}
	            
	        }
    	});

    	vnsRadioButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	if ( vnsRadioButton.isSelected() ){
	        		vnskmlButton.setEnabled(true);
	        	}
	            
	        }
    	});

    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
    	c.gridy = 1;
    	c.fill = GridBagConstraints.BOTH;
    	graphPane.add ( vnskmlButton , c);

    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 0;
    	c.gridy = 2;
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

}
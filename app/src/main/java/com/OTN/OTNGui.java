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
import java.time.*;


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
		frame.setIconImage(new ImageIcon(getClass().getResource("/otnLogo.png")).getImage());

		Container content = frame.getContentPane();

		content.setLayout( new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 1;
    	c.weighty = 1; 
    	c.fill = GridBagConstraints.BOTH; //tabs costraints

		this.tabs = new JTabbedPane();
    	content.add(tabs , c);
    	fillOSMPane();
    	fillAreaPane();
    	fillProfilesPane();
    	fillGraphPane();

    	//otnc.createAddProfiles(true); // for testing purposes


    	/////////////////////////////// GENERATE BUTTON ///////////////////////////////////

		c = new GridBagConstraints();
    	c.fill = GridBagConstraints.VERTICAL;
    	c.anchor = GridBagConstraints.LAST_LINE_END;
  	  	c.gridx = 0;
    	c.gridy = 1;
    	c.insets = new Insets(0,0,0,5);//generatePanel costraints

    	JPanel generatePanel = new JPanel();

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

    	generatePanel.add(button);

    	content.add(generatePanel, c);

    	frame.pack();
	}	

	/////////////////////////////// OSMPANE ///////////////////////////////////////////////////

	private void fillOSMPane() {
		JPanel paneOSM =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "OSM file",paneOSM );
    	
    	/*
		areaNameTF = new JTextField(15);
		GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 1;
    	c.gridy = 1;
    	c.insets = new Insets(0,0,0,5);
    	c.fill = GridBagConstraints.BOTH;
    	paneOSM.add ( areaNameTF , c);
    	*/

    	GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
    	c.gridy = 0;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.insets = new Insets(5,5,5,5);

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
    	c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
    	c.gridy = 1;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.insets = new Insets(5,5,5,5);

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
		c.gridx = 0;
    	c.gridy = 2;
    	c.insets = new Insets (3,0,3,0);
    	c.fill = GridBagConstraints.HORIZONTAL;

    	paneOSM.add ( new JSeparator (SwingConstants.HORIZONTAL) , c );

    	/*
    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 1;
    	c.gridy = 3;
    	c.fill = GridBagConstraints.BOTH;

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

    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
    	c.gridy = 3;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.insets = new Insets(5,5,5,5);

    	JButton clearButton = new JButton("restore to original file");
      			/*clearButton.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent e){
				}*/

		paneOSM.add(clearButton, c);

		c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
    	c.gridy = 4;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.insets = new Insets(5,5,5,5);

    	JButton discardButton = new JButton("discard file");
      			/*discardButton.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent e){
				}*/

		paneOSM.add(discardButton, c);

	}

	//////////////////////////////// WORKFLOW AREA ////////////////////////////////////////////////

	private void fillAreaPane() {
		JPanel paneArea =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Area",paneArea );

    	JLabel workflowtitle = new JLabel("area workflow");

    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 1;
    	c.anchor = GridBagConstraints.CENTER;
    	c.insets = new Insets (5,5,5,5);

    	paneArea.add(workflowtitle, c);

		/////////////////////////// TABLE & SCROLLPANE /////////////////////////////

		WorkflowTableDataModel workflowTableData = new WorkflowTableDataModel() ;

		JTable wftable = new JTable(workflowTableData);
      	wftable.setFillsViewportHeight(true);

      	JScrollPane wfscrollpane = new JScrollPane(wftable);

    	c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 1;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.fill = GridBagConstraints.BOTH;

      	paneArea.add(wfscrollpane, c);

      	/////////////////////////// BUTTONS ////////////////////////////////

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
	  	c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(5,5,5,5); //areaButtonsPanel costraints

      	JPanel areaButtonsPanel = new JPanel(new GridBagLayout());

      		JButton deleteWFelementbutton = new JButton("delete");
      			/*deleteProfileButton.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent e){
				}*/

			GridBagConstraints cab = new GridBagConstraints();
			cab.fill = GridBagConstraints.VERTICAL;
			cab.anchor = GridBagConstraints.FIRST_LINE_START;
		  	cab.gridx = 0;
			cab.gridy = 0;
			cab.weightx = 1;

			areaButtonsPanel.add(deleteWFelementbutton, cab);

			cab = new GridBagConstraints();
			cab.anchor = GridBagConstraints.LAST_LINE_END;
			cab.gridx = 1;
	    	cab.gridy = 0;
	    	cab.weightx = 1;

			JPanel rightareaButtons = new JPanel(new GridBagLayout());

				JButton savePolyButton = new JButton("save poly file");
				savePolyButton.setEnabled(false);

				GridBagConstraints crab = new GridBagConstraints();
				crab.gridx = 0;
				crab.gridy = 0;

				rightareaButtons.add(savePolyButton, crab);

				JButton analyzebutton = new JButton("analyze");
		    	analyzebutton.setBackground( Color.CYAN );
		    	analyzebutton.setEnabled(false);

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

		    	crab = new GridBagConstraints();
		    	crab.gridx = 1;
		    	crab.gridy = 0;
		    	crab.insets = new Insets(0,5,0,0);

		    	rightareaButtons.add(analyzebutton, crab);

		    	crab = new GridBagConstraints();
		    	crab.gridx = 2;
				crab.gridy = 0;
				crab.insets = new Insets(0,5,0,1);

		    	JButton filterButton = new JButton("filter area");
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

				rightareaButtons.add(filterButton, crab);

	    	areaButtonsPanel.add (rightareaButtons , cab);

      	paneArea.add(areaButtonsPanel,c);
	}

	///////////////////////////////////// PROFILES PANE ///////////////////////////////////

	private void fillProfilesPane() {
		JPanel paneProfiles =new JPanel(  new GridBagLayout() );  
    	this.tabs.add( "Profiles",paneProfiles );

    	ProfilesTableDataModel profilesTableData = new ProfilesTableDataModel() ;
    	profilesTableData.addProfile("car", "car", "fastest", false, false, true);
		profilesTableData.addProfile("foot", "foot", "fastest", false, false, true);

		/////////////////////////// TABLE & SCROLLPANE /////////////////////////////

		JTable profilesTable = new JTable(profilesTableData);
      	profilesTable.setFillsViewportHeight(true);

      	JScrollPane profilesScrollpane = new JScrollPane(profilesTable);

    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.fill = GridBagConstraints.BOTH;

      	paneProfiles.add(profilesScrollpane, c);

      	/////////////////////////// BUTTONS ////////////////////////////////

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.LAST_LINE_END;
	  	c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(5,5,5,5); //profilesButtonsPanel costraints

      	JPanel profilesButtonsPanel = new JPanel(new GridBagLayout());

      		JButton deleteProfileButton = new JButton("delete profile");
      			
      			/*deleteProfileButton.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent e){
				}*/

			GridBagConstraints cb = new GridBagConstraints();
			cb.fill = GridBagConstraints.VERTICAL;
			cb.anchor = GridBagConstraints.LAST_LINE_END;
		  	cb.gridx = 0;
			cb.gridy = 0;

			profilesButtonsPanel.add(deleteProfileButton, cb);


      		JButton newProfileButton = new JButton("new profile");
      			/*newProfileButton.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent e){
				}*/

      		cb = new GridBagConstraints();
			cb.fill = GridBagConstraints.VERTICAL;
			cb.anchor = GridBagConstraints.LAST_LINE_END;
		  	cb.gridx = 1;
			cb.gridy = 0;
			cb.insets = new Insets(0,5,0,3);

			profilesButtonsPanel.add(newProfileButton, cb);


      	paneProfiles.add(profilesButtonsPanel,c);
	}

	//////////////////////////////////// GRAPH PANE ////////////////////////////////////

	private void fillGraphPane() {
		JPanel graphPane =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Graph",graphPane );

    	ButtonGroup graphCompatGroup = new ButtonGroup();
    	this.otnRadioButton = new JRadioButton("OTN", true );
    	this.vnsRadioButton = new JRadioButton("VNS");
    	graphCompatGroup.add( otnRadioButton );
    	graphCompatGroup.add( vnsRadioButton );

    	GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
    	c.gridy = 0;
    	c.fill = GridBagConstraints.HORIZONTAL;

    	graphPane.add ( otnRadioButton , c);
    	
    	c.gridx = 1;

    	graphPane.add ( vnsRadioButton , c);

    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.insets = new Insets(5,5,5,5);

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

      	graphPane.add ( vnskmlButton , c);

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
    	c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
    	c.gridy = 2;
    	c.gridwidth = 2;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.insets = new Insets(5,5,5,5);

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
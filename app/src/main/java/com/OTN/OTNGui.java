package com.OTN;

import com.formdev.flatlaf.FlatDarkLaf;
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
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JCheckBox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class OTNGui {

	private JFrame frame;
	final private JTabbedPane tabs;
	private OTNCompanion otnc;
	final JFileChooser fc = new JFileChooser();
	final private LinkedList<AreaElement> areaElements;
	private AreaElement selectedElement;
	private File vnsKmlFile;
	private JRadioButton otnRadioButton;
	private JRadioButton vnsRadioButton;
	final private WorkflowTableDataModel workflowTableData;
	final private JTable wftable; 
	final private ProfilesTableDataModel profilesTableData;
	final private JTable profilesTable; 

	public static void main(String[] args) {
	try {
    	UIManager.setLookAndFeel( new FlatDarkLaf() );
		} catch( Exception ex ) {
    	System.err.println( "Failed to initialize LaF" );
		}
	new OTNGui();
    }

	
	
	OTNGui () {
		
		Logger logger = LoggerFactory.getLogger(OTNGui.class);
    	logger.info("otngui created");
		
		this.otnc = new OTNCompanion();
		this.areaElements = new LinkedList();
		this.workflowTableData = new WorkflowTableDataModel();
		this.wftable = new JTable(this.workflowTableData);
		profilesTableData = new ProfilesTableDataModel();
		profilesTable = new JTable(profilesTableData);

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
    	c.gridwidth = 2;
    	c.fill = GridBagConstraints.BOTH; //tabs costraints

    	//@Leo please disable all tabs until an OSM file is added with the loadButton

		this.tabs = new JTabbedPane();
    	content.add(tabs , c);
    	//fillOSMPane();
    	fillAreaPane();
    	fillProfilesPane();
    	fillGraphPane();

    	//otnc.createAddProfiles(true); // for testing purposes


    	/////////////////////////////// GENERATE BUTTON ///////////////////////////////////

		c = new GridBagConstraints();
       	c.anchor = GridBagConstraints.FIRST_LINE_START;
  	  	c.gridx = 0;
    	c.gridy = 1;
    	c.insets = new Insets(5,5,5,5);

    	JPanel leftmainbuttons = new JPanel (new GridBagLayout());

    	GridBagConstraints lmbc = new GridBagConstraints();
    	lmbc.anchor = GridBagConstraints.FIRST_LINE_START;
		lmbc.gridx = 0;
    	lmbc.gridy = 0;

    	JButton loadButton = new JButton("load OSM file");
    	loadButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	            int returnVal = fc.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	File file = fc.getSelectedFile();
	            	if ( file.exists() ){
	            		workflowTableData.addAreaElement( file );
	            		tabs.setEnabledAt( tabs.indexOfTab("Profiles") , true );
	            		tabs.setEnabledAt( tabs.indexOfTab("Graph") , true );

	            		//@leo add default areaElement name when created
	            	}
	            }
        	}  
    	});  
    	leftmainbuttons.add ( loadButton , lmbc);

		lmbc = new GridBagConstraints();
		lmbc.anchor = GridBagConstraints.FIRST_LINE_START;
		lmbc.gridx = 1;
    	lmbc.gridy = 0;
    	lmbc.insets = new Insets(0,5,0,0);

    	JButton polyButton = new JButton("load poly file");
    	polyButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	            int returnVal = fc.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	File file = fc.getSelectedFile();
	            	if ( file.exists() ){
	            		workflowTableData.getLastAreaElement().setpolyBoundary(file);
	            	}
	            }
        	}  
    	});
    	leftmainbuttons.add(polyButton, lmbc);

    	content.add(leftmainbuttons, c);

		c = new GridBagConstraints();
    	c.fill = GridBagConstraints.VERTICAL;
    	c.anchor = GridBagConstraints.LAST_LINE_END;
  	  	c.gridx = 1;
    	c.gridy = 1;
    	c.insets = new Insets(5,5,5,5);

    	JButton button = new JButton("generate");
	   	button.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				// set area elemnt for graph
				otnc.setOsmArea( workflowTableData.getLastAreaElement() );
				// set profikes for graphs
				otnc.setProfiles( profilesTableData );
				if ( otnRadioButton.isSelected() ){
					otnc.createGraph();
				} else if ( vnsRadioButton.isSelected() && vnsKmlFile.exists() ) {
				otnc.createVNSGraph(vnsKmlFile);
				}
        	}  
    	});
    	content.add(button, c);

    	frame.pack();
	}	

	/////////////////////////////// OSMPANE ///////////////////////////////////////////////////

	/*private void fillOSMPane() {
		JPanel paneOSM =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "OSM file",paneOSM );
    	
    
		areaNameTF = new JTextField(15);
		GridBagConstraints c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridx = 1;
    	c.gridy = 1;
    	c.insets = new Insets(0,0,0,5);
    	c.fill = GridBagConstraints.BOTH;
    	paneOSM.add ( areaNameTF , c);
    

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
	            		workflowTableData.addAreaElement( file );
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
	            		workflowTableData.getLastAreaElement().setpolyBoundary(file);
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
    	

    	c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
    	c.gridy = 3;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.insets = new Insets(5,5,5,5);

    	JButton clearButton = new JButton("restore to original file");
      			clearButton.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent e){
				}

		//paneOSM.add(clearButton, c);

		c = new GridBagConstraints();
    	c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
    	c.gridy = 4;
    	c.fill = GridBagConstraints.HORIZONTAL;
    	c.insets = new Insets(5,5,5,5);

    	JButton discardButton = new JButton("discard file");
      			discardButton.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent e){
				}

		//paneOSM.add(discardButton, c);

	}

	*/

	//////////////////////////////// WORKFLOW AREA ////////////////////////////////////////////////

	private void fillAreaPane() {
		JPanel paneArea =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Area",paneArea );

    	JLabel workflowtitle = new JLabel("area workflow");
    	wftable.setFillsViewportHeight(true);
		wftable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 1;
    	c.anchor = GridBagConstraints.CENTER;
    	c.insets = new Insets (5,5,5,5);

    	paneArea.add(workflowtitle, c);

		/////////////////////////// TABLE & SCROLLPANE /////////////////////////////

      	JScrollPane wfscrollpane = new JScrollPane(wftable);
      	//@leo when an areaElement is created (be it from OSM file or a filter) the table doesn't update

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

      		JButton deleteWFelementbutton = new JButton("delete area element");
      		deleteWFelementbutton.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent e){

					if ( wftable.getSelectedRows().length == 0 ) {
						
						JOptionPane.showMessageDialog(frame,"No area elements selected","Error",JOptionPane.ERROR_MESSAGE);

						return;
					}

					String areaName = (String) workflowTableData.getValueAt( wftable.getSelectedRows()[0] , 0 );

					int paneReturnVal = JOptionPane.showConfirmDialog(frame, 
                	 areaName+" will be permanently deleted in the workflow.\n"
                	+"Are you sure you want to proceed?", "Delete " + areaName,JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
					
					if ( paneReturnVal == 0 ){
						workflowTableData.deletRow( wftable.getSelectedRows()[0] );
					}
					
				}
			});

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
				//@leo to do

				GridBagConstraints crab = new GridBagConstraints();
				crab.gridx = 0;
				crab.gridy = 0;

				rightareaButtons.add(savePolyButton, crab);

				JButton analyzebutton = new JButton("analyze");
		    	analyzebutton.setBackground( Color.CYAN );
		    	analyzebutton.setEnabled(false);

		    	analyzebutton.addActionListener(new ActionListener(){  
					public void actionPerformed(ActionEvent e){ 
						/*
						if (areaElements.getLast() == null ){
							System.out.println("no osm file set");
							return;
						}
						OSMAnalyzer analizer = new OSMAnalyzer ( areaElements.getLast().getOsmFile() );
						analizer.execute();
						*/
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
				crab.insets = new Insets(0,5,0,0);

		    	JButton filterButton = new JButton("filter area");
		    	filterButton.setBackground( Color.CYAN );
		    	filterButton.addActionListener( new ActionListener() {  
					public void actionPerformed(ActionEvent e){ 
						if ( ! workflowTableData.getLastAreaElement().isValid() ) {
							//@gabri display dialog for error
							return;
						}
						AreaElement input = workflowTableData.getLastAreaElement();
						AreaElement output = workflowTableData.addAreaElement( input );
						OTNGuiFilter filter = new OTNGuiFilter( input , output );
						//@leo even if no filter is applied a new area element is added to the table
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
    	this.tabs.setEnabledAt( this.tabs.indexOfTab("Profiles") , false );

    	
    	profilesTableData.addProfile("car", "car", "fastest", false, false, true);
		profilesTableData.addProfile("foot", "foot", "fastest", false, false, true);

		/////////////////////////// TABLE & SCROLLPANE /////////////////////////////

		
      	profilesTable.setFillsViewportHeight(true);
      	profilesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

      	JScrollPane profilesScrollpane = new JScrollPane(profilesTable);

    	GridBagConstraints c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 0;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.gridwidth = 2;
    	c.fill = GridBagConstraints.BOTH;

      	paneProfiles.add(profilesScrollpane, c);

      	/////////////////////////// BUTTONS ////////////////////////////////

		   		JButton deleteProfileButton = new JButton("delete profile");
      			
      			deleteProfileButton.addActionListener(new ActionListener(){  
					public void actionPerformed(ActionEvent e){
						if ( profilesTable.getSelectedRows().length == 0 ) {
							JOptionPane.showMessageDialog(frame,"No profiles selected","Error",JOptionPane.ERROR_MESSAGE);
							return;
						}

						String profileName = (String) profilesTableData.getValueAt( profilesTable.getSelectedRows()[0] ,0 );;

						int paneRetValue = JOptionPane.showConfirmDialog(null, 
	                	profileName+" will be permanently deleted from profiles.\n"
	                	+"Are you sure you want to proceed?", "Delete " + profileName,JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
						if ( paneRetValue == 0 ){
							profilesTableData.deletRow( profilesTable.getSelectedRows()[0] );
						}
						
					}
				});

			c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
		  	c.gridx = 0;
			c.gridy = 1;
			c.insets = new Insets(5,5,5,5);

			paneProfiles.add(deleteProfileButton, c);


      		JButton newProfileButton = new JButton("new profile");
      			newProfileButton.addActionListener(new ActionListener(){  
				public void actionPerformed(ActionEvent e){

				JDialog newProfileDialog = new JDialog(frame, "Create new profile", true);
				newProfileDialog.setLayout(new GridBagLayout());
				newProfileDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

				GridBagConstraints c = new GridBagConstraints();
				c.anchor = GridBagConstraints.FIRST_LINE_END;
	  			c.gridx = 0;
				c.gridy = 0;
				c.weightx = 1;
				c.insets = new Insets(10,5,5,5);			
				JLabel profileNamelabel = new JLabel("Name:");
				newProfileDialog.add(profileNamelabel, c);

				c = new GridBagConstraints();
	  			c.gridx = 1;
				c.gridy = 0;
				c.weightx = 1;
				c.insets = new Insets(5,5,5,10);
				JTextField profileNameTF = new JTextField(15);
				newProfileDialog.add(profileNameTF,c);

				c = new GridBagConstraints();
				c.anchor = GridBagConstraints.FIRST_LINE_END;
	  			c.gridx = 0;
				c.gridy = 1;
				c.weightx = 1;
				c.insets = new Insets(5,10,5,5);			
				JLabel profileVehiclelabel = new JLabel("Vehicle:");
				newProfileDialog.add(profileVehiclelabel, c);

				c = new GridBagConstraints();
	  			c.gridx = 1;
				c.gridy = 1;
				c.weightx = 1;
				c.insets = new Insets(5,5,5,10);
				SpinnerListModel vehicleListModel = new SpinnerListModel(new String[] { 
					"foot", "hike", "wheelchair","bike","racingbike","bike2",
					"mtb","car","car4wd","motorcycle" });
    			JSpinner vehicleSpinner = new JSpinner(vehicleListModel);
    			Component vehicleSpinnerEditor = vehicleSpinner.getEditor();
				JFormattedTextField ftf = ((JSpinner.DefaultEditor) vehicleSpinnerEditor).getTextField();
				ftf.setColumns(13);
    			newProfileDialog.add(vehicleSpinner,c);

    			c.anchor = GridBagConstraints.FIRST_LINE_END;
	  			c.gridx = 0;
				c.gridy = 2;
				c.weightx = 1;
				c.insets = new Insets(5,10,5,5);		
				JLabel profileWeightinglabel = new JLabel("Weighting:");
				newProfileDialog.add(profileWeightinglabel, c);

				c = new GridBagConstraints();
	  			c.gridx = 1;
				c.gridy = 2;
				c.weightx = 1;
				c.insets = new Insets(5,5,5,10);		
				JTextField profileWeightingTF = new JTextField(15);
				newProfileDialog.add(profileWeightingTF,c);

				c = new GridBagConstraints();
				c.anchor = GridBagConstraints.FIRST_LINE_START;
	  			c.gridx = 0;
				c.gridy = 3;
				c.gridwidth = 2;
				c.insets = new Insets(5,10,5,10);	
				c.fill = GridBagConstraints.HORIZONTAL;	
				JPanel profileBooleanPanel = new JPanel(new GridBagLayout());

					GridBagConstraints pbpc = new GridBagConstraints();

					pbpc.anchor = GridBagConstraints.CENTER;
		  			pbpc.gridx = 0;
					pbpc.gridy = 0;
					pbpc.weightx = 1;			
					
					JLabel turnCostLabel = new JLabel("Turn cost:");
					profileBooleanPanel.add(turnCostLabel, pbpc);
					
		  			pbpc.gridx = 1;
					JCheckBox turnCostCheckbox = new JCheckBox();
					profileBooleanPanel.add(turnCostCheckbox,pbpc);

		  			pbpc.gridx = 2;
					JLabel chLabel = new JLabel("ch:");
					profileBooleanPanel.add(chLabel, pbpc);

		  			pbpc.gridx = 3;
					JCheckBox chCheckbox = new JCheckBox();
					profileBooleanPanel.add(chCheckbox, pbpc);

					pbpc.gridx = 4;
					JLabel lmLabel = new JLabel("lm:");
					profileBooleanPanel.add(lmLabel, pbpc);

		  			pbpc.gridx = 5;
					JCheckBox lmCheckbox = new JCheckBox();
					profileBooleanPanel.add(lmCheckbox, pbpc);

				newProfileDialog.add(profileBooleanPanel,c);

				c = new GridBagConstraints();
				c.gridx = 0;
    			c.gridy = 4;
    			c.fill = GridBagConstraints.BOTH;
    			c.gridwidth = 2;
    			c.insets = new Insets(5,5,5,5);
    			newProfileDialog.add ( new JSeparator (SwingConstants.HORIZONTAL) , c );

				final String name = profileNameTF.getText();
				final String vehicle = (String) vehicleSpinner.getValue();
				final String weighting = profileWeightingTF.getText();
				final boolean tc = turnCostCheckbox.isSelected();
				final boolean ch = chCheckbox.isSelected();
				final boolean lm = lmCheckbox.isSelected();

				JButton createProfileButton = new JButton("create profile");
				c.anchor = GridBagConstraints.CENTER;
	  			c.gridx = 0;
				c.gridy = 5;
				c.gridwidth = 2;
				c.insets = new Insets(5,5,5,10);
				newProfileDialog.add(createProfileButton, c);
				createProfileButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
					//@Leo not sure what's wrong with this button, does nothing, lots of exceptions pls fix
					profilesTableData.addProfile ( name , vehicle ,  weighting ,  tc , ch , lm );
	            	}

				});

				newProfileDialog.pack();
				newProfileDialog.setVisible(true);
				
				}
			});

      		c = new GridBagConstraints();
			c.anchor = GridBagConstraints.LAST_LINE_END;
		  	c.gridx = 1;
			c.gridy = 1;
			c.insets = new Insets(5,5,5,5);

			paneProfiles.add(newProfileButton, c);
	}

	//////////////////////////////////// GRAPH PANE ////////////////////////////////////

	private void fillGraphPane() {
		JPanel graphPane =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Graph",graphPane );
    	this.tabs.setEnabledAt( this.tabs.indexOfTab("Graph") , false );

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

    	graphPane.add ( setStorageDirButton , c);
    }

}
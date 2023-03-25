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
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JCheckBox;

import java.net.URL;
import javax.imageio.ImageReader;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import com.graphhopper.util.Constants;



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
		logger.info(Constants.VERSION);
		this.otnc = new OTNCompanion();
		this.areaElements = new LinkedList();
		this.workflowTableData = new WorkflowTableDataModel();
		this.wftable = new JTable(this.workflowTableData);
		profilesTableData = new ProfilesTableDataModel();
		profilesTable = new JTable(profilesTableData);

		this.frame = new JFrame("OTN-Companion");
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); 
		frame.setVisible(true);
		frame.setIconImage(new ImageIcon(getClass().getResource("/otn_logo_shield_minimal_32x32.png")).getImage());

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
    	c.fill = GridBagConstraints.VERTICAL;
    	c.anchor = GridBagConstraints.CENTER;
  	  	c.gridx = 0;
    	c.gridy = 1;
    	c.weightx = 1;
    	c.insets = new Insets(5,5,5,5);

    	JButton button = new JButton("generate");
	   	button.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 

				OTNUserErrorGeneration userError = new OTNUserErrorGeneration("Graph generation error(s)");


				if(workflowTableData.getRowCount()==0){
					userError.addError("There is no area element");
				}
				
				// other test (TO DO valid area)

				if(profilesTableData.getRowCount()==0){
					userError.addError("There are no profiles");
				}

				if(! otnc.isStorageDirSet()){
					userError.addError("Storage Dir not set");
				}

				if (userError.showDialog()) {
					return;
				}

				otnc.setOsmArea( workflowTableData.getLastAreaElement() );

				otnc.setProfiles( profilesTableData );
				OTNcSwingWorker otncWorker = new OTNcSwingWorker ( otnc );

				long start = System.currentTimeMillis();
				
				System.out.println("started time");

				final JDialog dialogwait = new JDialog();


				otncWorker.addPropertyChangeListener(new PropertyChangeListener() { // listener to kill dialog after worker finishes
			        @Override
			        public void propertyChange(PropertyChangeEvent evt) {
			           if (evt.getPropertyName().equals("state")) {
			               if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
			                  dialogwait.dispose();
			               }
			            }
			        }
			    });


				if ( otnRadioButton.isSelected() ){
					//otnc.createGraph();
					otncWorker.execute();// @gabri ecco lo swing worker, per il momneto disabilitiamo tutta la parte relativa a vns
				}/*
				 else if ( vnsRadioButton.isSelected() && vnsKmlFile.exists() ) {
					//otnc.createVNSGraph(vnsKmlFile);
				}
				*/

				///////////////////// WAIT DIALOG/////////////

				dialogwait.setLayout( new GridBagLayout() );

				GridBagConstraints c = new GridBagConstraints();
				c.anchor = GridBagConstraints.CENTER;
	  			c.gridx = 0;
				c.gridy = 0;

				dialogwait.setTitle("Graph generation");
				dialogwait.setModal(true);
				dialogwait.setSize(new Dimension(250,120));
				dialogwait.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				JLabel text = new JLabel ("This may take a while...");
				dialogwait.add(text, c);
				dialogwait.setVisible(true); // tis is blocking, listener will kill it

				// tis continue when worker is finised
				long finish = System.currentTimeMillis();
				
				System.out.println("Graph generated");
  
				long timeElapsed = finish - start;

				long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeElapsed);

				dialogwait.dispose();

				frame = new JFrame();
				JOptionPane.showMessageDialog(frame,"Graph succesfully generated in " + timeSeconds +" s.","Filter generation",
				JOptionPane.PLAIN_MESSAGE);
				frame.dispose();
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

    	/////////////////////////////////// LOAD BUTTONS /////////////////////////////

    	GridBagConstraints c = new GridBagConstraints();
       	c.anchor = GridBagConstraints.CENTER;
       	c.fill = GridBagConstraints.BOTH;
       	c.weightx = 1;
  	  	c.gridx = 0;
    	c.gridy = 0;
    	c.insets = new Insets(5,5,5,5);

    	JPanel loadButtons = new JPanel (new GridBagLayout());

    	GridBagConstraints lbc = new GridBagConstraints();
		lbc.gridx = 0;
    	lbc.gridy = 0;
    	//lbc.weightx = 1;

    	final JButton loadButton = new JButton("load OSM file");
    	loadButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	            int returnVal = fc.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	File file = fc.getSelectedFile();

	            	OTNUserErrorGeneration userError = new OTNUserErrorGeneration("File error(s)");

	            	String filename = file.getName();
					String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

					if (! file.exists() ){
					userError.addError("File does not exist");
					} else {
						if (! (extension.equals("pbf") || extension.equals("osm") || extension.equals("bz2") ||  extension.equals("bzip2") ) ) {
    					userError.addError("Invalid file extension");
    					}
    				}

    				if ( userError.showDialog() ) {
					return;
					}
	            	
	            	workflowTableData.addAreaElement( file );
	            	tabs.setEnabledAt( tabs.indexOfTab("Profiles") , true );
	            	tabs.setEnabledAt( tabs.indexOfTab("Graph") , true );
	            	workflowTableData.getLastAreaElement().setName( file.getName().substring(0, file.getName().lastIndexOf(".")) );
	            	//@leo name cleansing
	            	loadButton.setEnabled(false);
	            	
	            }
        	}  
    	});  
    	loadButtons.add ( loadButton , lbc);

		lbc = new GridBagConstraints();
		lbc.gridx = 1;
    	lbc.gridy = 0;
    	lbc.insets = new Insets (0,5,0,0);
    	//lbc.weightx = 1;

    	JButton polyButton = new JButton("load poly file");
    	polyButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){ 
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	            int returnVal = fc.showOpenDialog(frame);
	            if (returnVal == JFileChooser.APPROVE_OPTION) {
	            	File file = fc.getSelectedFile();
	            	if ( file.exists() ){
	            		workflowTableData.getLastAreaElement().setpolyBoundary(file);
	            		workflowTableData.fireTableDataChanged();
	            	}
	            }
        	}  
    	});

    	loadButtons.add(polyButton, lbc);

    	paneArea.add(loadButtons, c);

		c = new GridBagConstraints();
		c.gridx = 0;
    	c.gridy = 1;
    	c.fill = GridBagConstraints.BOTH;
    	paneArea.add ( new JSeparator (SwingConstants.HORIZONTAL) , c );

    	////////////////////////////// WORKFLOW TITLE ///////////////////////////

    	JLabel workflowtitle = new JLabel("area workflow");
    	wftable.setFillsViewportHeight(true);
		wftable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

    	c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 2;
    	c.weightx = 1;
    	c.anchor = GridBagConstraints.CENTER;
    	c.insets = new Insets (5,5,5,5);

    	paneArea.add(workflowtitle, c);

		/////////////////////////// TABLE & SCROLLPANE /////////////////////////////

      	JScrollPane wfscrollpane = new JScrollPane(wftable);

    	c = new GridBagConstraints();
    	c.gridx = 0;
    	c.gridy = 3;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.fill = GridBagConstraints.BOTH;

    	wfscrollpane.setOpaque(false);
    	wfscrollpane.getViewport().setBackground ( 
    		new Color(wfscrollpane.getBackground().getRed(),
    		wfscrollpane.getBackground().getGreen(),
    		wfscrollpane.getBackground().getBlue(), 125)
    	);
    	wftable.setOpaque(false);
    	wftable.setBackground(
    		new Color(wftable.getBackground().getRed(),
    		wftable.getBackground().getGreen(),
    		wftable.getBackground().getBlue(), 125)
    	);

    	wfscrollpane.setPreferredSize(new Dimension(500,250));

    	//wfscrollpane.getViewport().setExtentSize(new Dimension(500,300));

    	System.out.println(wfscrollpane.getPreferredSize());

      	paneArea.add(wfscrollpane, c);

		ImageIcon backgroundimage = new ImageIcon(getClass().getResource("/florence_street_map.png"));
		
		Image img = backgroundimage.getImage().getScaledInstance(wfscrollpane.getPreferredSize().getWidth(), -1,  java.awt.Image.SCALE_DEFAULT);

		//backgroundimage = new ImageIcon(img);

		/*BufferedImage img = null;
			try {
			    img = ImageIO.read(new File("res/florence_street_map.png"));
			} catch (IOException e) {
			    e.printStackTrace();
			}


		Image dimg = img.getScaledInstance(300, -1,Image.SCALE_SMOOTH);

		ImageIcon backgroundimage = new ImageIcon(dimg);*/

      	JLabel background = new JLabel();

      	c = new GridBagConstraints();
    	c.gridx = 0; 
    	c.gridy = 3;
    	c.weightx = 1;
    	c.weighty = 1;
    	c.fill = GridBagConstraints.BOTH;

    	//backgroundimage.setSize(wfscrollpane.getMinimumSize());

    	//background.setIcon(backgroundimage);

    	paneArea.add(background, c);

      	/////////////////////////// BUTTONS ////////////////////////////////

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
	  	c.gridx = 0;
		c.gridy = 4;
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
					if ( wftable.getRowCount() == 0 ) {
						loadButton.setEnabled(true);
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
		    	analyzebutton.setBackground( Color.decode("#2a5285") );
		    	//analyzebutton.setEnabled(false);

		    	analyzebutton.addActionListener(new ActionListener(){  
					public void actionPerformed(ActionEvent e){ 
						
						if ( workflowTableData.getLastAreaElement() == null ){
							System.out.println("no osm file set");
							return;
						}
						OSMAnalyzer analizer = new OSMAnalyzer ( workflowTableData.getLastAreaElement().getOsmFile() );
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
				crab.insets = new Insets(0,5,0,0);

		    	JButton filterButton = new JButton("filter area");
		    	filterButton.setBackground( Color.decode("#2a5285") );
		    	filterButton.addActionListener( new ActionListener() {  
					public void actionPerformed(ActionEvent e){

						OTNUserErrorGeneration filterAreaError = new OTNUserErrorGeneration("Area Error(s)");

						if( workflowTableData.getRowCount()==0){
							filterAreaError.addError("There is no area element to filter");
						}else{ 
							if ( ! workflowTableData.getLastAreaElement().isValid() ) {
								filterAreaError.addError("The selected area element is not valid");
							}
						}

						if (filterAreaError.showDialog()) {
							return;
						}

						AreaElement input = workflowTableData.getLastAreaElement();
						AreaElement output = workflowTableData.addAreaElement( input );
						OTNGuiFilter filter = new OTNGuiFilter( input , output );

						filter.addWindowListener ( new WindowAdapter() {
					        @Override
					        public void windowClosing(WindowEvent e) {
					        	//System.out.println("filter windows listener firing"+ Long.toString( workflowTableData.getLastAreaElement().getOsmFile().length() ) );
					        	if (  workflowTableData.getLastAreaElement().getOsmFile().length() < 1 ) {
					        		
					        		workflowTableData.deletRow( workflowTableData.getRowCount() -1);
					        	}
					        } });

						workflowTableData.fireTableDataChanged();
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

				final JDialog newProfileDialog = new JDialog(frame, "Create new profile", true);
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
				final JTextField profileNameTF = new JTextField(15);
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
				
				final JComboBox  vehicleBox = new JComboBox ( new String[] { 
					"foot", "hike", "wheelchair","bike","racingbike","bike2",
					"mtb","car","car4wd","motorcycle" });
    			newProfileDialog.add(vehicleBox,c);


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
				final JComboBox profileWeightingBox = new JComboBox ( new String[] { 
					"fastest", "shortest", "short_fastest","curvature","custom" });
				newProfileDialog.add(profileWeightingBox,c);

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
					final JCheckBox turnCostCheckbox = new JCheckBox();
					profileBooleanPanel.add(turnCostCheckbox,pbpc);

		  			pbpc.gridx = 2;
					JLabel chLabel = new JLabel("ch:");
					profileBooleanPanel.add(chLabel, pbpc);

		  			pbpc.gridx = 3;
					final JCheckBox chCheckbox = new JCheckBox();
					profileBooleanPanel.add(chCheckbox, pbpc);

					pbpc.gridx = 4;
					JLabel lmLabel = new JLabel("lm:");
					profileBooleanPanel.add(lmLabel, pbpc);

		  			pbpc.gridx = 5;
					final JCheckBox lmCheckbox = new JCheckBox();
					profileBooleanPanel.add(lmCheckbox, pbpc);

				newProfileDialog.add(profileBooleanPanel,c);

				c = new GridBagConstraints();
				c.gridx = 0;
    			c.gridy = 4;
    			c.fill = GridBagConstraints.BOTH;
    			c.gridwidth = 2;
    			c.insets = new Insets(5,5,5,5);
    			newProfileDialog.add ( new JSeparator (SwingConstants.HORIZONTAL) , c );
    			/*
				final String name = profileNameTF.getText();
				final String vehicle = (String) vehicleSpinner.getValue();
				final String weighting = profileWeightingTF.getText();
				final boolean tc = turnCostCheckbox.isSelected();
				final boolean ch = chCheckbox.isSelected();
				final boolean lm = lmCheckbox.isSelected();
				*/

				JButton createProfileButton = new JButton("create profile");
				c.anchor = GridBagConstraints.CENTER;
	  			c.gridx = 0;
				c.gridy = 5;
				c.gridwidth = 2;
				c.insets = new Insets(5,5,5,10);
				newProfileDialog.add(createProfileButton, c);
				createProfileButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						profilesTableData.addProfile ( profileNameTF.getText() , (String) vehicleBox.getSelectedItem() ,  (String) profileWeightingBox.getSelectedItem() ,  turnCostCheckbox.isSelected() , chCheckbox.isSelected() , lmCheckbox.isSelected() );
	            		newProfileDialog.dispose();
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
    	vnsRadioButton.setEnabled(false);

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

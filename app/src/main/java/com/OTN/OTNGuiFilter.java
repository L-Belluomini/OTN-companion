package com.OTN;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.Container;

public class OTNGuiFilter {

	private OSMEditor editor ;
	private JTextField topTF;
	private JTextField bottomTF;
	private JTextField leftTF;
	private JTextField rightTF;
	private JTextField centerlatTF;
	private JTextField centerlongTF;
	private JTextField radiusTF;
	private File _tempFile;
	private File _openOSM;
	private JTabbedPane tabs;
	JFrame frame;

		OTNGuiFilter( File openOSM , File tempFile ) {
			System.out.println("filter gui initialized");
			editor = new OSMEditor();
			editor.loadFile ( openOSM );
			System.out.println("editor initialized");
			this._tempFile = tempFile;
			this._openOSM = openOSM;

			JFrame filterFrame= new JFrame("Filter");    
	        filterFrame.setLayout( new GridBagLayout() ); 
			filterFrame.setVisible(true);

			Container content = filterFrame.getContentPane();

			content.setLayout( new GridBagLayout() );

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
	    	c.gridy = 0;
	    	c.weightx = 1;
	    	c.weighty = 1; 
	    	c.fill = GridBagConstraints.BOTH;

			this.tabs = new JTabbedPane();
	    	content.add(tabs , c);
    		fillBBPane();
    		fillRadiusPane();

    		//////////////////// SELECT PANEL ////////////////////

    		c = new GridBagConstraints();
    		c.fill = GridBagConstraints.VERTICAL;
    		c.anchor = GridBagConstraints.LAST_LINE_END;
  	  		c.gridx = 0;
    		c.gridy = 1;

    		JPanel buttonPanel = new JPanel();

    		////////////////// SELECT BUTTON ////////////////////

	    	JButton filterButton = new JButton("select");
	    	filterButton.addActionListener( new ActionListener(){  
				public void actionPerformed(ActionEvent e){
					Float top = Float.parseFloat ( topTF.getText() );
					Float bottom = Float.parseFloat ( bottomTF.getText() );
					Float left = Float.parseFloat ( leftTF.getText() );
					Float right =Float.parseFloat ( rightTF.getText() );

					if ( top != null && bottom != null && left != null && right != null ){
						System.out.println("filter data valid");
						editor.setFilter(left , right , top , bottom );

						System.out.println("set coords");
						
						editor.setOutput( _tempFile );
						System.out.println("started time");

						long start = System.currentTimeMillis();

						final JDialog bbdialogwait = new JDialog();
			
						editor.addPropertyChangeListener(new PropertyChangeListener() {

					        @Override
					        public void propertyChange(PropertyChangeEvent evt) {
					           if (evt.getPropertyName().equals("state")) {
					               if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
					                  bbdialogwait.dispose();
					               }
					            }
					         }
					      });

						editor.execute();

						///////// WAIT DIALOG /////////

	    				bbdialogwait.setLayout( new GridBagLayout() );

						GridBagConstraints c = new GridBagConstraints();
	    				c.anchor = GridBagConstraints.CENTER;
	  	  				c.gridx = 0;
	    				c.gridy = 0;

						bbdialogwait.setTitle("Bounding box generation");
						bbdialogwait.setModal(true);
						bbdialogwait.setSize(new Dimension(250,120));
						bbdialogwait.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
						JLabel text = new JLabel ("This may take a while...");
						bbdialogwait.add(text, c);
						bbdialogwait.setVisible(true);

						long finish = System.currentTimeMillis();
						
						System.out.println("filter applied");

        				long timeElapsed = finish - start;

        				bbdialogwait.dispose();

        				frame = new JFrame();

        				JOptionPane.showMessageDialog(frame,"Bounding box succesfully applied in " + timeElapsed +" ms.","Bounding box generation",
    					JOptionPane.PLAIN_MESSAGE);


					} else {
						_tempFile = _openOSM;
					}
	        	}  
	    	});

	    	buttonPanel.add(filterButton);

	    	content.add(buttonPanel, c);

	    	filterFrame.pack();
    	
    	}		

	    //////////////////// BB FILTER PANNEL ////////////////////
	    	
		private void fillBBPane() {
		JPanel bbFilterPanel =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Bounding box",bbFilterPanel );

	    	JLabel topleftcorner = new JLabel("Top left corner:");
	    	GridBagConstraints c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
	    	c.gridx = 0;
	    	c.gridy = 0;
	    	c.gridwidth = 4;
	    	c.insets = new Insets(5,5,5,5);
	    	bbFilterPanel.add ( topleftcorner , c);

	    	JLabel topLabel = new JLabel("Lat:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 0;
	    	c.gridy = 1;
	    	c.insets = new Insets(0,5,0,5);
	    	bbFilterPanel.add ( topLabel , c);

	    	this.topTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 1;
	    	c.insets = new Insets(0,0,0,5);
	    	c.fill = GridBagConstraints.BOTH;
	    	bbFilterPanel.add ( topTF , c);

	    	JLabel leftLabel = new JLabel("Long:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 2;
	    	c.gridy = 1;
	    	c.insets = new Insets(0,0,0,5);
	    	bbFilterPanel.add ( leftLabel , c);

	    	this.leftTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 3;
	    	c.gridy = 1;
	    	c.fill = GridBagConstraints.BOTH;
	    	c.insets = new Insets(0,0,0,5);
	    	bbFilterPanel.add ( leftTF , c);

	    	c = new GridBagConstraints();
			c.gridx = 0;
	    	c.gridy = 2;
	    	c.fill = GridBagConstraints.BOTH;
	    	c.gridwidth = 4;
	    	c.insets = new Insets(5,5,5,5);

	    	bbFilterPanel.add ( new JSeparator (SwingConstants.HORIZONTAL) , c );

	    	JLabel bottomrightcorner = new JLabel("Bottom right corner:");
	    	c= new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
	    	c.gridx = 0;
	    	c.gridy = 3;
	    	c.gridwidth = 4;
	    	c.insets = new Insets(0,5,5,5);
	    	bbFilterPanel.add ( bottomrightcorner , c);

	    	JLabel bottomLabel = new JLabel("Lat:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 0;
	    	c.gridy = 4;
	    	c.insets = new Insets(0,5,0,5);
	    	bbFilterPanel.add ( bottomLabel , c);

	    	this.bottomTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 4;
	    	c.fill = GridBagConstraints.BOTH;
	    	c.insets = new Insets(0,0,0,5);
	    	bbFilterPanel.add ( bottomTF , c);

	    	JLabel rightLabel = new JLabel("Long:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 2;
	    	c.gridy = 4;
	    	c.insets = new Insets(0,0,0,5);
	       	bbFilterPanel.add ( rightLabel , c);

	    	this. rightTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 3;
	    	c.gridy = 4;
	    	c.fill = GridBagConstraints.BOTH;
	    	c.insets = new Insets(0,0,0,5);
	    	bbFilterPanel.add ( rightTF , c);

	    }

	   	//////////////////// RADIUS FILTER PANNEL ////////////////////

		private void fillRadiusPane() {
		JPanel radiusFilterPanel =new JPanel( new GridBagLayout() );  
    	this.tabs.add( "Bounding circle",radiusFilterPanel );		

	    	JLabel centerlat = new JLabel("Circle center lat:");
	    	GridBagConstraints c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_END;
	    	c.weightx = 0.5;
			c.gridx = 0;
	    	c.gridy = 0;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPanel.add ( centerlat  , c);

	    	this.centerlatTF = new JTextField(15);    
	    	c = new GridBagConstraints();
	    	c.weightx = 0.5;
			c.gridx = 1;
	    	c.gridy = 0;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPanel.add ( centerlatTF , c);

	    	JLabel centerlong = new JLabel("Circle center long:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_END;
	    	c.weightx = 0.5;
			c.gridx = 0;
	    	c.gridy = 1;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPanel.add ( centerlong  , c);

	    	this.centerlongTF = new JTextField(15);    
	    	c = new GridBagConstraints();
	    	c.weightx = 0.5;
			c.gridx = 1;
	    	c.gridy = 1;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPanel.add ( centerlongTF , c);

	    	c = new GridBagConstraints();
			c.gridx = 0;
	    	c.gridy = 2;
	    	c.fill = GridBagConstraints.BOTH;
	    	c.gridwidth = 3;
	    	c.ipady= 3;

	    	radiusFilterPanel.add ( new JSeparator (SwingConstants.HORIZONTAL) , c );

	    	JLabel radiustxt = new JLabel ("Circle radius:");
	    	c = new	GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_END;
	    	c.weightx = 0.5;
	    	c.gridx = 0;
	    	c.gridy = 3;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPanel.add (radiustxt, c);

	    	this.radiusTF = new JTextField(15);    
	    	c = new GridBagConstraints();
	    	c.weightx = 0.5;
			c.gridx = 1;
	    	c.gridy = 3;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPanel.add ( radiusTF , c);

	    }

	}
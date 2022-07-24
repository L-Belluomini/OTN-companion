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
	JFrame frame;


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
			filterFrame.setPreferredSize(new Dimension(500,300));

			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
	    	c.gridy = 0;
	    	c.fill = GridBagConstraints.BOTH;

			JTabbedPane filterPane = new JTabbedPane();
			filterFrame.add(filterPane , c);
	    	
	    	//////////////// SET PANES ////////////////////////////////////////
	    	
	    	JPanel bbFilterPannel  =new JPanel( new GridBagLayout() );
	    	filterPane.add( "Bounding box",bbFilterPannel );

	    	JLabel topleftcorner = new JLabel("Top left corner:");
	    	c= new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
	    	c.gridx = 0;
	    	c.gridy = 0;
	    	c.gridwidth = 4;
	    	c.insets = new Insets(5,5,5,5);
	    	bbFilterPannel.add ( topleftcorner , c);

	    	JLabel topLabel = new JLabel("Lat:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 0;
	    	c.gridy = 1;
	    	c.insets = new Insets(0,5,0,5);
	    	bbFilterPannel.add ( topLabel , c);

	    	this.topTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 1;
	    	c.insets = new Insets(0,0,0,5);
	    	c.fill = GridBagConstraints.BOTH;
	    	bbFilterPannel.add ( topTF , c);

	    	JLabel leftLabel = new JLabel("Long:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 2;
	    	c.gridy = 1;
	    	c.insets = new Insets(0,0,0,5);
	    	bbFilterPannel.add ( leftLabel , c);

	    	this.leftTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 3;
	    	c.gridy = 1;
	    	c.fill = GridBagConstraints.BOTH;
	    	c.insets = new Insets(0,0,0,5);
	    	bbFilterPannel.add ( leftTF , c);

	    	c = new GridBagConstraints();
			c.gridx = 0;
	    	c.gridy = 2;
	    	c.fill = GridBagConstraints.BOTH;
	    	c.gridwidth = 4;
	    	c.insets = new Insets(5,5,5,5);

	    	bbFilterPannel.add ( new JSeparator (SwingConstants.HORIZONTAL) , c );

	    	JLabel bottomrightcorner = new JLabel("Bottom right corner:");
	    	c= new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
	    	c.gridx = 0;
	    	c.gridy = 3;
	    	c.gridwidth = 4;
	    	c.insets = new Insets(0,5,5,5);
	    	bbFilterPannel.add ( bottomrightcorner , c);

	    	JLabel bottomLabel = new JLabel("Lat:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 0;
	    	c.gridy = 4;
	    	c.insets = new Insets(0,5,0,5);
	    	bbFilterPannel.add ( bottomLabel , c);

	    	this.bottomTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 4;
	    	c.fill = GridBagConstraints.BOTH;
	    	c.insets = new Insets(0,0,0,5);
	    	bbFilterPannel.add ( bottomTF , c);

	    	JLabel rightLabel = new JLabel("Long:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 2;
	    	c.gridy = 4;
	    	c.insets = new Insets(0,0,0,5);
	       	bbFilterPannel.add ( rightLabel , c);

	    	this. rightTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 3;
	    	c.gridy = 4;
	    	c.fill = GridBagConstraints.BOTH;
	    	c.insets = new Insets(0,0,0,5);
	    	bbFilterPannel.add ( rightTF , c);


	    	/*JLabel bottomLabel = new JLabel("bottom boundires");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 4;
	    	bbFilterPannel.add ( bottomLabel , c);

	    	this.bottomTF = new JTextField(15);
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 4;
	    	c.fill = GridBagConstraints.BOTH;
	    	bbFilterPannel.add ( bottomTF , c);

	    	JLabel rightLabel = new JLabel("right boundires");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 3;
	    	bbFilterPannel.add ( rightLabel , c);

	    	this.rightTF = new JTextField(15);    
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 1;
	    	c.gridy = 3;
	    	c.fill = GridBagConstraints.BOTH;
	    	bbFilterPannel.add ( rightTF , c);*/


	    	JPanel radiusFilterPannel  = new JPanel( new GridBagLayout() );
	    	filterPane.add( "Bounding circle", radiusFilterPannel );
	    	radiusFilterPannel.setSize(300,300);
		

	    	JLabel centerlat = new JLabel("Circle center lat:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_END;
	    	c.weightx = 0.5;
			c.gridx = 0;
	    	c.gridy = 0;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPannel.add ( centerlat  , c);

	    	this.centerlatTF = new JTextField(15);    
	    	c = new GridBagConstraints();
	    	c.weightx = 0.5;
			c.gridx = 1;
	    	c.gridy = 0;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPannel.add ( centerlatTF , c);

	    	JLabel centerlong = new JLabel("Circle center long:");
	    	c = new GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_END;
	    	c.weightx = 0.5;
			c.gridx = 0;
	    	c.gridy = 1;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPannel.add ( centerlong  , c);

	    	this.centerlongTF = new JTextField(15);    
	    	c = new GridBagConstraints();
	    	c.weightx = 0.5;
			c.gridx = 1;
	    	c.gridy = 1;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPannel.add ( centerlongTF , c);

	    	c = new GridBagConstraints();
			c.gridx = 0;
	    	c.gridy = 2;
	    	c.fill = GridBagConstraints.BOTH;
	    	c.gridwidth = 3;
	    	c.ipady= 3;

	    	radiusFilterPannel.add ( new JSeparator (SwingConstants.HORIZONTAL) , c );

	    	JLabel radiustxt = new JLabel ("Circle radius:");
	    	c = new	GridBagConstraints();
	    	c.anchor = GridBagConstraints.FIRST_LINE_END;
	    	c.weightx = 0.5;
	    	c.gridx = 0;
	    	c.gridy = 3;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPannel.add (radiustxt, c);

	    	this.radiusTF = new JTextField(15);    
	    	c = new GridBagConstraints();
	    	c.weightx = 0.5;
			c.gridx = 1;
	    	c.gridy = 3;
	    	c.insets = new Insets(5,5,5,5);
	    	radiusFilterPannel.add ( radiusTF , c);



	    	c = new GridBagConstraints();
	    	c.fill = GridBagConstraints.VERTICAL;
	    	c.anchor = GridBagConstraints.LAST_LINE_END;
	  	  	c.gridx = 0;
	    	c.gridy = 1; 

	    	JButton filterButton = new JButton("select");
	    	filterFrame.add(filterButton, c);
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

						bbdialogwait.setTitle("Bounding box generation");
						bbdialogwait.setModal(true);
						bbdialogwait.setSize(300,300);
						bbdialogwait.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
						JLabel text = new JLabel ("This may take a while...");
						bbdialogwait.add(text);
						bbdialogwait.pack();
						bbdialogwait.setVisible(true);

						long finish = System.currentTimeMillis();
						
						System.out.println("filter applied");

        				long timeElapsed = finish - start;

        				bbdialogwait.dispose();

        				frame = new JFrame();

        				JOptionPane.showMessageDialog(frame,"Bounding box succesfully applied","Bounding box generation",
    					JOptionPane.PLAIN_MESSAGE);


					} else {
						_tempFile = _openOSM;
					}
	        	}  
	    	});
	}
}
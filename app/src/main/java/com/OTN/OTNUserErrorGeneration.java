package com.OTN;

public class OTNUserErrorGeneration {
	
	LinkedList<String> errors;

	public OTNGuiUserErrorGeneration(){
	
		this.errors = new LinkedList();

	}

	public void addError( String error ){

		this.errors.add();
	}

	public void showdialog() {
		
		if(errors.size()==0){
			return;
		}

				JDialog errorDialog = new JDialog();

				errorDialog.setLayout( new GridBagLayout() );

				GridBagConstraints c = new GridBagConstraints();
				c.anchor = GridBagConstraints.CENTER;
	  			c.gridx = 0;
				c.gridy = 0;

				errorDialog.setTitle("User error generation");
				errorDialog.setSize(new Dimension(250,120));

				// add "title explanation and formattation"
				JLabel text = new JLabel();


				for(String idk:errors){
					text = new JLabel (idk);
					errorDialog.add(text, c);
				}

				
				
				errorDialog.setVisible(true);


		frame = new JFrame();

		JOptionPane.showMessageDialog(frame,this.str,JOptionPane.WARNING_MESSAGE);
	}


}
package com.OTN;

public class OTNGuiUserErrorGeneration {
	JFrame frame;

	public OTNGuiUserErrorGeneration(){}

	String str;

	public void set(String estring) { 
		this.str = this.str + estring;
	}

	public void showdialog() {

		frame = new JFrame;

		JOptionPane.showMessageDialog(frame,
		this.str
    	JOptionPane.WARNING_MESSAGE);
	}

	public bool isErrorPresent ( ) {
		if ( this.str.lenght() > 0 ){
			return true;
		} else {
			return false;
		}
	}


}
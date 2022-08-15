package com.OTN;

import javax.swing.*;
import javax.swing.table.*;

import com.graphhopper.config.CHProfile;
import com.graphhopper.config.LMProfile;
import com.graphhopper.config.Profile;

import java.util.*;

public class ProfilesTableDataModel extends AbstractTableModel {
	// table 
	private String[] columnNames = { "Name" , "Vehicle" , "Weighting" , "Turn cost" , "CH" , "LM"  };
	// data
	private List<Profile> profiles = new LinkedList<Profile>();
    private List<CHProfile> chProfiles = new LinkedList<CHProfile>();
    private List<LMProfile> lmProfiles = new LinkedList<LMProfile>();
	
	ProfilesTableDataModel() {}

	// table 

	 public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return profiles.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        switch (col) {
	    	case  0 : 
	    	return profiles.get(row).getName();

	    	
	    	case 1:
	    	return profiles.get(row).getVehicle();


	    	case 2:
	    	return profiles.get(row).getWeighting();


	    	case 3:
	    	return profiles.get(row).isTurnCosts();


	    	case 4:
	    	//return profiles.get(row).getVehicle();
	    	return true; // to do


	    	case 5:
	    	return false; // to do

        }
    return null;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    public boolean isCellEditable(int row, int col)
        { switch (col) { 
	    	case  0 : // name
	    		return true;
	 
	    	
	    	case 1: // creation time
	    		return false;


	    	case 2: // is valid
	    		return false;

	    }
	    return false ; 
	}

	public void setValueAt(Object value, int row, int col) {
        switch (col) { 
	    	case  0 : // name
	    	//.get(row).setName( value.toString() );
	    		break;
	 
	    	
	    	case 1:
	    		return;  // creation time


	    	case 2:
	    		return; // is valid
 
	    }
        fireTableCellUpdated(row, col);
    }

    public void deletRow ( int row){
    	profiles.remove( row ); // to do add remove ch profile
    }


    // data

	public void addProfile (String name , String vehicle , String weighting , boolean tc , boolean ch , boolean lm ) {
		Profile profile = new Profile( name ).setVehicle( vehicle ).setWeighting( weighting ).setTurnCosts( tc );
		this.profiles.add( profile);

		if ( ch &&  ( profile != null ) ) {
			 CHProfile chPrpfile =  new CHProfile( name );
			 this.chProfiles.add(chPrpfile);
		}
		if ( lm &&  ( profile != null )  ) {
			 LMProfile lmProfile =  new LMProfile( name );
			 this.lmProfiles.add( lmProfile );
		}
	}

	public List<Profile> getProfiles(){
		return profiles;
	}
	public List<CHProfile>  getCHprofiles(){
		return chProfiles;
	}
	public List<LMProfile>  getlmProfiles(){
		return lmProfiles;
	}


}
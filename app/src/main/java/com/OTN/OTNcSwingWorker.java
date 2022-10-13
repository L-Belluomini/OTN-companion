package com.OTN;

import java.io.*;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OTNcSwingWorker extends SwingWorker <Void, Void>{ 
	
	public OTNCompanion otncsw;
	
	public OTNcSwingWorker(){
		otncsw = new OTNCompanion();
	}

	public OTNcSwingWorker( OTNCompanion eotnc ){
		otncsw = eotnc;
	}


	@Override
	public Void doInBackground(){
	otncsw.createGraph();
		
		return null;
	}

	@Override
    public void done() {
    	return;
    }

}

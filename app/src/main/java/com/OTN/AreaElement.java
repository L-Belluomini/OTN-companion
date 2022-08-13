package com.OTN;

import java.io.*; 

public class AreaElement  {
	private File osmfile;
	private File polyFileBoundary;
	private boolean complete = false;
	private String areaName;
	private String creationTime;

	AreaElement (File osmfile ) {
		if ( osmfile.exists() ){
			this.osmfile = osmfile;
		}	
	}
	AreaElement () {
		File tempFile;
		try {
			tempFile = File.createTempFile("otnc", ".osm");
		} catch ( IOException ex ) {
			System.out.println(ex.toString());
			return;
		}
		tempFile.deleteOnExit();
		this.osmfile = tempFile;
	}

	public File getOsmFile(){
		return this.osmfile;
	}
	public File getPolyFile(){
		return this.polyFileBoundary;

	}

	public void setpolyBoundary( File polyFileBoundary ){
		if ( polyFileBoundary.exists() && polyFileBoundary.getName().endsWith(".poly") ){
			this.polyFileBoundary = polyFileBoundary;
			this.complete = true;
			// to do add real check ?
		}
	}

	public void setName(String name){
		this.areaName = name;
	}

	public String getName() {
		return areaName;
	}

	public void setCreationTime (String time){
		this.creationTime = time;
	} 
	
	public String getCreationTime () {
		return this.creationTime;
	}

	public boolean isValid() {
		if ( ! this.complete ){
			return this.osmfile.exists();
		} else {
			return ( this.osmfile.exists() && this.polyFileBoundary.exists() );
		}
	}
	public boolean isComplete() {
		return complete;
	}
}
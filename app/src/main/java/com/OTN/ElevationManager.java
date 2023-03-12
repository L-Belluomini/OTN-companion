package com.OTN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.System.Logger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;

import com.graphhopper.reader.dem.ElevationProvider;

public class ElevationManager implements ElevationProvider {

	private File DTEDdir;
	
	public ElevationManager(File dtDir) {
		/*if (FilenameUtils.getExtension(dtDir.getPath()).equals("dt0")){
			
		} else {
			//TODO gestisci l'errore di file
		}*/
		
		DTEDdir = dtDir;
	}
	
	@Override
	public boolean canInterpolate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getEle(double latitude, double longitude) {
		
		DTEDAnalyzer dtXAnalyzer = null; 
		
		int parallel = (int)latitude;
		int meridian = (int)longitude;
		
		String path = DTEDdir.toString() + 
			File.separator + (meridian >= 0 ? "e" : "w") + String.format( "%03d", Math.abs(meridian)) +
			File.separator + (parallel >= 0 ? "n" : "s") + String.format( "%02d", Math.abs(parallel));
			
			if (new File(path + ".dt2").exists()) {
				dtXAnalyzer = new DTEDAnalyzer(new File(DTEDdir + ".dt2"));
			} else if (new File(path + ".dt1").exists()) {
				dtXAnalyzer = new DTEDAnalyzer(new File(DTEDdir + ".dt1"));
			} else if (new File(path + ".dt0").exists()) {
				dtXAnalyzer = new DTEDAnalyzer(new File(DTEDdir + ".dt0"));
			} else {
				System.out.println("File not found");
			}
		
		
		double height = dtXAnalyzer._getHeight(latitude, longitude);
		
		if (Double.isNaN(height)) {
			System.out.println("No height for point " + latitude + " - " + longitude);
			return 0; // TODO da rivedere
		}
		return height;
	}


	@Override
	public void release() {
		// TODO Auto-generated method stub
		//dtXAnalyzer.close();
	}
}

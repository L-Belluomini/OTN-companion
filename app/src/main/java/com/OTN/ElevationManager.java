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
//import org.apache.commons.logging.Log;

import com.graphhopper.reader.dem.ElevationProvider;

//mport org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElevationManager implements ElevationProvider {

	private File DTEDdir;
	private org.slf4j.Logger logger;
	
	public ElevationManager(File dtFile) {
		/*if (FilenameUtils.getExtension(dtFile.getPath()).equals("dt0")){
			dt0Analyzer = new DTEDAnalyzer(dtFile);
		} else {
			//TODO gestisci l'errore di file
		}*/
		logger = LoggerFactory.getLogger(OTNCompanion.class);
		DTEDdir = dtFile;

	}
	
	@Override
	public boolean canInterpolate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public double getEle(double latitude, double longitude) {
		
		DTEDAnalyzer dtXAnalyzer = null; 
		
		int parallel = (int)latitude;
		int meridian = (int)longitude;
		
		String path = DTEDdir.toString() + 
			File.separator + (meridian >= 0 ? "e" : "w") + String.format ( "%03d", Math.abs ( meridian ) ) +
			File.separator + (parallel >= 0 ? "n" : "s") + String.format ( "%02d", Math.abs ( parallel ) );
			
		logger.info("Searching for file with path " + path);
		
		if (new File(path + ".dt2").exists()) {
			dtXAnalyzer = new DTEDAnalyzer(new File(path + ".dt2"));
		} else if (new File(path + ".dt1").exists()) {
			dtXAnalyzer = new DTEDAnalyzer(new File(path + ".dt1"));
		} else if (new File(path + ".dt0").exists()) {
			dtXAnalyzer = new DTEDAnalyzer(new File(path + ".dt0"));
		} else {
			logger.error("File not found, returning height 0.0");
			return 0.0;
		}
		logger.info("Found file, proceeding to capture height");
			//System.out.println("File found, getting  height");
		double height = dtXAnalyzer._getHeight(latitude, longitude);
		
		if (Double.isNaN(height)) {
			logger.error("No height for point " + latitude + " - " + longitude + " Returning height 0.0");
			return 0.0; // TODO da rivedere
		}
		logger.info("Heigt Found: " + height);
		return height;
	}


	@Override
	public void release() {
		// TODO Auto-generated method stub
	}
}

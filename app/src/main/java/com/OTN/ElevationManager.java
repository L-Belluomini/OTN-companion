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

	public  DTEDAnalyzer dt0Analyzer = null; 
	
	public ElevationManager(File dtFile) {
		if (FilenameUtils.getExtension(dtFile.getPath()).equals("dt0")){
			dt0Analyzer = new DTEDAnalyzer(dtFile);
		} else {
			//TODO gestisci l'errore di file
		}
	}
	
	@Override
	public boolean canInterpolate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getEle(double latitude, double longitude) {
		double height = dt0Analyzer._getHeight(latitude, longitude);
		
		if (Double.isNaN(height)) {
			System.out.println("No height for point " + latitude + " - " + longitude);
			return 0; // TODO da rivedere
		}
		return height;
	}


	@Override
	public void release() {
		// TODO Auto-generated method stub
		dt0Analyzer.close();
	}
}

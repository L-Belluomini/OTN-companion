package com.OTN;

import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.*;
import org.openstreetmap.osmosis.core.filter.common.*;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.*;
import org.openstreetmap.osmosis.areafilter.v0_6.*;
import java.io.*;
import javax.swing.SwingWorker;

public class OSMEditor extends SwingWorker <Void, Void>{

	private RunnableSource osmReader;
	private AreaFilter filter;
	private Sink osmXmlwriter;
	private AreaElement areaElement;

	private final double[][] radiusCosSin = {
		{ // sin
			0
			,0.156434465040231
			,0.309016994374947
			,0.453990499739547
			,0.587785252292473
			,0.707106781186547
			,0.809016994374947
			,0.891006524188368
			,0.951056516295153
			,0.987688340595138
			,1
			,0.987688340595138
			,0.951056516295154
			,0.891006524188368
			,0.809016994374947
			,0.707106781186548
			,0.587785252292473
			,0.453990499739547
			,0.309016994374947
			,0.156434465040231
			,1.22464679914735E-16
			,-0.156434465040231
			,-0.309016994374947
			,-0.453990499739547
			,-0.587785252292473
			,-0.707106781186547
			,-0.809016994374947
			,-0.891006524188368
			,-0.951056516295153
			,-0.987688340595138
			,-1
			,-0.987688340595138
			,-0.951056516295154
			,-0.891006524188368
			,-0.809016994374947
			,-0.707106781186548
			,-0.587785252292473
			,-0.453990499739547
			,-0.309016994374948
			,-0.156434465040231
			,-2.44929359829471E-16
		} ,
		{ // cos
			1
			,0.987688340595138
			,0.951056516295153
			,0.891006524188368
			,0.809016994374947
			,0.707106781186548
			,0.587785252292473
			,0.453990499739547
			,0.309016994374947
			,0.156434465040231
			,6.12323399573677E-17
			,-0.156434465040231
			,-0.309016994374947
			,-0.453990499739547
			,-0.587785252292473
			,-0.707106781186547
			,-0.809016994374947
			,-0.891006524188368
			,-0.951056516295153
			,-0.987688340595138
			,-1
			,-0.987688340595138
			,-0.951056516295154
			,-0.891006524188368
			,-0.809016994374947
			,-0.707106781186548
			,-0.587785252292473
			,-0.453990499739547
			,-0.309016994374948
			,-0.156434465040231
			,-1.83697019872103E-16
			,0.156434465040231
			,0.309016994374947
			,0.453990499739547
			,0.587785252292473
			,0.707106781186547
			,0.809016994374947
			,0.891006524188368
			,0.951056516295153
			,0.987688340595138
			,1
		}
	};
	
	private final double earthRadiusM = 6371000;

	OSMEditor(AreaElement eareaElement){
		areaElement = eareaElement;

	}

	void loadFile(File file) {
		boolean pbf = false;
		CompressionMethod compression = CompressionMethod.None;

		if (file.getName().endsWith(".pbf")) {
		    pbf = true;
		} else if (file.getName().endsWith(".gz")) {
		    compression = CompressionMethod.GZip;
		} else if (file.getName().endsWith(".bz2")) {
		    compression = CompressionMethod.BZip2;
		}
		try {
			if (pbf) {
			    osmReader = new crosby.binary.osmosis.OsmosisReader(
			            new FileInputStream(file));
			} else {
			    osmReader = new XmlReader(file, false, compression);
			}
		} catch (IOException e) {
			System.out.println(e.toString());

		}

		//TO DO test for file extension

	}
	void setOutput (File file) {
		if (filter == null ) {
			System.out.println("filter type and cords not set");
			return;
		}
		osmXmlwriter = new XmlWriter( file , CompressionMethod.None );
		filter.setSink(osmXmlwriter);
	}

	void setFilter( double left,
	        double right,
	        double top,
	        double bottom ) {

		filter = new  BoundingBoxFilter(IdTrackerType.Dynamic , left , right , top , bottom , true , true , false , true  );
		osmReader.setSink(filter);

		String tmpStgring;
		double tmpLat;
		double tmpLong;
		File tempFile = null;
		System.out.println("started creating poly file  border");

		try {
			tempFile = File.createTempFile("BoundingBoxArea", ".poly");
			tempFile.deleteOnExit();
			FileWriter writer = new FileWriter( tempFile );
			BufferedWriter buffer = new BufferedWriter(writer);

			tmpStgring = areaElement.getName();
			buffer.write(tmpStgring);
			buffer.newLine();
			tmpStgring = "ExactBoundingBox";
			buffer.write(tmpStgring);
			buffer.newLine();

			tmpStgring = Double.toString(left) + " " + Double.toString(top) + System.lineSeparator(); 
			buffer.write(tmpStgring);  
			buffer.newLine();

			tmpStgring = Double.toString(right) + " " + Double.toString(bottom) + System.lineSeparator(); 
			buffer.write(tmpStgring);  
			buffer.newLine();

			tmpStgring = Double.toString(left) + " " + Double.toString(bottom) + System.lineSeparator(); 
			buffer.write(tmpStgring);  
			buffer.newLine();

			tmpStgring = "END";
			buffer.write(tmpStgring);
			buffer.newLine();
			tmpStgring = "END";
			buffer.write(tmpStgring);
			buffer.flush();


		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
		areaElement.setpolyBoundary( tempFile );


	}

	void setFilter( double centerLat,
        double centerLong,
        double rMteters) {
		
		String tmpStgring;
		double tmpLat;
		double tmpLong;
		File tempFile = null;
		System.out.println("started creating poly file for filter");

		try {
			tempFile = File.createTempFile("radiusArea", ".poly");
			tempFile.deleteOnExit();
			FileWriter writer = new FileWriter( tempFile );
			BufferedWriter buffer = new BufferedWriter(writer);

			tmpStgring = areaElement.getName();
			buffer.write(tmpStgring);
			buffer.newLine();
			tmpStgring = "RadiusArea";
			buffer.write(tmpStgring);
			buffer.newLine();

			for ( int  i = 0; i < radiusCosSin.length; i++ ) {
				tmpLong = centerLong + ( ( ( rMteters * radiusCosSin[1][i]) / 2 * 3.14 * earthRadiusM ) * 360 );
				tmpLat = centerLat + ( ( ( rMteters * radiusCosSin[0][i]) / 2 * 3.14 * ( earthRadiusM * Math.cos ( tmpLong ) ) ) * 360 );
				tmpStgring = Double.toString(tmpLong) + " " + Double.toString(tmpLat) + System.lineSeparator(); 
				buffer.write(tmpStgring);
				buffer.newLine();  
			}
			System.out.println("finished loop for poly file");
			tmpStgring = "END";
			buffer.write(tmpStgring);
			buffer.newLine();
			tmpStgring = "END";
			buffer.write(tmpStgring);
			buffer.flush();
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}

		filter = new  PolygonFilter( IdTrackerType.Dynamic, tempFile, true ,true , false , true );
		osmReader.setSink(filter);

		areaElement.setpolyBoundary( tempFile );

	}

	void setFilter(File polyFile ) {
		filter = new  PolygonFilter( IdTrackerType.Dynamic, polyFile, true ,true , false , true );
		osmReader.setSink(filter);
		areaElement.setpolyBoundary( polyFile );
	}


	

	@Override
	public Void doInBackground(){
		if ( osmReader == null ){
			 System.out.println("reader not set");
			 return null;
		} 
		if ( filter == null ){
			 System.out.println("filter not set");
			 return null;
		}
		if ( osmXmlwriter == null ){
			 System.out.println("osmXmlwriter not set");
			 return null;
		}
		System.out.println("running actual filter");
		osmReader.run();
		
		return null;
	}

	@Override
    public void done() {
    	return;
    }
}
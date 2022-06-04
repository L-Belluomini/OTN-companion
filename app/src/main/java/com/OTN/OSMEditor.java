package com.OTN;

import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.*;
import org.openstreetmap.osmosis.core.filter.common.*;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.*;
import org.openstreetmap.osmosis.areafilter.v0_6.*;
import java.io.*;

public class OSMEditor {

	private RunnableSource osmReader;
	private AreaFilter filter;
	private Sink osmXmlwriter;

	OSMEditor(){
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

	}

	void setFilter( double left,
	        double right,
	        double top,
	        double bottom ) {

		filter = new  BoundingBoxFilter(IdTrackerType.Dynamic , left , right , top , bottom , true , true , false , true  );
		osmReader.setSink(filter);
	}

	void setOutput (File file) {
		osmXmlwriter = new XmlWriter( file , CompressionMethod.None );
		filter.setSink(osmXmlwriter);
	}

	

	void runFilter (){
		if ( osmReader == null ){
			 System.out.println("reader not set");
			 return;
		} 
		if ( filter == null ){
			 System.out.println("filter not set");
			 return;
		}
		if ( osmXmlwriter == null ){
			 System.out.println("osmXmlwriter not set");
			 return;
		}

		osmReader.run();


	}
}
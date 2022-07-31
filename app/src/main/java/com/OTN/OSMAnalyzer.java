package com.OTN;

import java.io.*;
import java.util.*;

import javax.swing.SwingWorker;

import org.openstreetmap.osmosis.core.domain.v0_6.*;
import org.openstreetmap.osmosis.core.task.v0_6.*;
import org.openstreetmap.osmosis.core.filter.common.*;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.*;
import org.openstreetmap.osmosis.tagfilter.v0_6.TagFilter;
import org.openstreetmap.osmosis.tagfilter.v0_6.*;

import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;


public class OSMAnalyzer extends SwingWorker <Void, Void> {
	private RunnableSource osmReader;
	private String filePath;
	private Sink osmXmlwriter;
	private File tempFile;

public OSMAnalyzer (File file) {
	filePath = file.getPath();
	System.out.println(filePath);
	loadFile(file);
}


	private void loadFile( File file) {
		System.out.println("started analizing config");
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
			return;

		}
		


	}
	@Override
	public Void doInBackground() {
		Map<String,Set<String>>map = new HashMap <String,Set<String>> ( );
		Set<String> keys = new HashSet<String>( );
		Set<String> boundaryValues = new HashSet<String>( );
		keys.add("boundary");
		boundaryValues.add("administrative");
		map.put ("boundary", boundaryValues );

		TagFilter filter = new TagFilter ( "accept-relation" , keys , map  ); 

		osmReader.setSink(filter);
		try {
			tempFile = File.createTempFile("osmAnalisys", ".osm");
			tempFile.deleteOnExit();
		} catch (IOException ex) {
			System.out.println(ex.toString());
			return null;
		}
		osmXmlwriter = new XmlWriter( tempFile , CompressionMethod.None );
		filter.setSink(osmXmlwriter);
		System.out.println("analizer filter running");
		osmReader.run();
		System.out.println("analizer filter finished");
		InputStream filterdStream;
		try {
		filterdStream = new FileInputStream( tempFile.getPath());
		} catch (IOException ex) {
			System.out.println(ex.toString());
			return null;
		}
		geoPoliticalBoundires result = new geoPoliticalBoundires(filePath);
		OsmIterator iterator = new OsmXmlIterator(filterdStream, false);
		for (EntityContainer container : iterator) {
			if (container.getType() == de.topobyte.osm4j.core.model.iface.EntityType.Relation){
				for ( int keyIndex =0; keyIndex < container.getEntity().getNumberOfTags(); keyIndex ++ ){
					if ( container.getEntity().getTag(keyIndex).getKey() == "admin_level") {
						switch( container.getEntity().getTag(keyIndex).getValue() ) {
							case ("2"):
								break;
							case ("3"):
								break;
								
						}
					}
				}
				
				System.out.println(  container.getEntity().toString() );
			}
			
		}
		return null;

	}

	@Override
    public void done() {
    	return ;
    }

	

}
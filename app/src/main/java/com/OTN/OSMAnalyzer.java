package com.OTN;

import java.io.*;
import java.util.*;

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

public class OSMAnalyzer {
	private RunnableSource osmReader;
	private Sink osmXmlwriter;
	private File tempFile;

public OSMAnalyzer () {
}
public OSMAnalyzer (File file) {
	loadFile(file);
}


	void loadFile( File file) {
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
		Map<String,Set<String>>map = new HashMap <String,Set<String>> ( );

		Set<String> set = new HashSet<String>( );
		set.add("boundary");

		TagFilter filter = new TagFilter ( "accept-relation" , set , map  );

		osmReader.setSink(filter);
		try {
			tempFile = File.createTempFile("osmAnalisys", ".osm");
			tempFile.deleteOnExit();
		} catch (IOException ex) {
			System.out.println(ex.toString());
			return;
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
			return;
		}
		OsmIterator iterator = new OsmXmlIterator(filterdStream, false);
		for (EntityContainer container : iterator) {
			System.out.println( Long.toString( container.getEntity().getId() ) );
		}


	}
	

}
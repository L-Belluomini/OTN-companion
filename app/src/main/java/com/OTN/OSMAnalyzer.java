package com.OTN;

import java.io.*;
import java.nio.*;
import java.nio.file.Paths;
import java.util.*;

import javax.swing.SwingWorker;

import org.apache.xerces.dom.EntityImpl;
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
import de.topobyte.osm4j.diskstorage.*;
import de.topobyte.osm4j.diskstorage.nodedb.*;
import de.topobyte.osm4j.diskstorage.waydb.*;
import de.topobyte.osm4j.diskstorage.vardb.*;
import de.topobyte.osm4j.utils.OsmFile;

public class OSMAnalyzer extends SwingWorker <Void, Void> {
	private RunnableSource osmReader;
	private String filePath;
	private Sink osmXmlwriter;
	private File tempFile;
	private File nodeDataFile;
	private File nodeIndexFile;
	private File wayDataFile;
	private File wayIndexFile;
	private EntityProviderImpl provider;
	private NodeDB nodeProvider;
	//private 

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
	
	private void filter() {
		Map<String,Set<String>>map = new HashMap <String,Set<String>> ( );
		Set<String> keys = new HashSet<String>( );
		Set<String> boundaryValues = new HashSet<String>( );
		keys.add("boundary");
		keys.add("admin_level");
		boundaryValues.add("administrative");
		map.put ("boundary", boundaryValues );

		TagFilter filter = new TagFilter ( "accept-relation" , keys , map  ); 

		osmReader.setSink(filter);
		try {
			tempFile = File.createTempFile("osmAnalisys", ".osm");
			tempFile.deleteOnExit();
		} catch (IOException ex) {
			System.out.println(ex.toString());
			return ;
		}
		osmXmlwriter = new XmlWriter( tempFile , CompressionMethod.None );
		filter.setSink(osmXmlwriter);
		System.out.println("analizer filter running");
		osmReader.run();
		System.out.println("analizer filter finished");

	}

	private void scan() {
		
		System.out.println("started scan");

		InputStream filterdStream;
		try {
		filterdStream = new FileInputStream( tempFile.getPath());
		} catch (IOException ex) {
			System.out.println(ex.toString());
			return ;
		}
		geoPoliticalBoundires result = new geoPoliticalBoundires(filePath);
		OsmIterator iterator = new OsmXmlIterator(filterdStream, false);
		System.out.println("started scan iterator");
		for (EntityContainer container : iterator) {
			if (container.getType() == de.topobyte.osm4j.core.model.iface.EntityType.Relation) {

				System.out.println(  container.getEntity().toString() );
			} 
			
		}
		return;
	}
	
	@Override
	public Void doInBackground() {

		filter ();
		scan();
		setupDB();
		System.out.println("analyze backgorund done");
		
		return null;
	}

	@Override
    public void done() {
    	return ;
    }
	
	private void setupDB() { 
		System.out.println("started db");
		try {
			nodeDataFile = File.createTempFile("osmData", DbExtensions.EXTENSION_DATA );
			nodeIndexFile = File.createTempFile("osmData", DbExtensions.EXTENSION_INDEX );
			wayDataFile = File.createTempFile("osmData", DbExtensions.EXTENSION_DATA);
			wayIndexFile = File.createTempFile("osmData", DbExtensions.EXTENSION_INDEX );

			
			nodeDataFile.deleteOnExit();
			nodeIndexFile.deleteOnExit();
			wayDataFile.deleteOnExit();
			wayIndexFile.deleteOnExit();
		} catch (IOException ex) {
			System.out.println(ex.toString());
			return;
		}

		NodeDB nodeDB;
		VarDB<WayRecordWithTags> varDB;

		
		try {

			EntityDbSetup.createNodeDb( Paths.get(filePath) , nodeIndexFile.toPath() , nodeDataFile.toPath() );
			EntityDbSetup.createWayDb( Paths.get(filePath), wayIndexFile.toPath(), wayDataFile.toPath(), true);
			System.out.println("db set up");

			varDB = new VarDB<>(wayDataFile.toPath(), wayIndexFile.toPath() , new WayRecordWithTags(0));
			nodeDB = new NodeDB( nodeDataFile.toPath() , nodeIndexFile.toPath() );
			
			

		} catch (IOException ex) {
			System.out.println(ex.toString());
			return;
		}


		

		provider = new EntityProviderImpl(nodeDB,varDB);
		System.out.println("db loaded");
	}
}
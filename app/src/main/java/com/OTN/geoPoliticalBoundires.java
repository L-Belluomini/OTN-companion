package com.OTN;

import java.io.*;
import java.util.*;

import de.topobyte.osm4j.core.model.iface.*;

import org.locationtech.jts.geom.*;

public class geoPoliticalBoundires {
	private List<OsmRelation> boundariesL3;
	private List<OsmRelation> boundariesL2;
	private List<OsmRelation> boundariesL4;
	private List<OsmRelation> boundariesL5;
	private List<OsmRelation> boundariesL6;
	private List<OsmRelation> boundariesL7;
	private List<OsmRelation> boundariesL8;
	private List<OsmRelation> boundariesL9;
	private File osmFile;
	private String filePath;

	public geoPoliticalBoundires (String path ){
	this.filePath = path; 
	}

	public geoPoliticalBoundires ( File osmfile ){
		this.osmFile = osmfile;
	}

	public void AddL3Boundary (OsmRelation l3Bboundary){
		boundariesL3.add(l3Bboundary);
	}
/*
	static public Geometry GetPolyBoundarie (OsmRelation boundarieOSM ) {
		return new Geometry();
	}
*/

}
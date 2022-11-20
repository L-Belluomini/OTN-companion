package com.OTN;

import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperConfig;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.LMProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.storage.*;

import com.google.gson.Gson;
import com.google.gson.stream.*;


import java.util.*;
import java.io.*;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OTNCompanion 
{
    private List<Profile> profiles = new LinkedList<Profile>();
    private List<CHProfile> chProfiles = new LinkedList<CHProfile>();
    private List<LMProfile> lmProfiles = new LinkedList<LMProfile>();
    private GraphHopperConfig ghConfig = null;
    private String storageDir ="";
    private AreaElement area;
    private Logger logger;


    public OTNCompanion() {
        logger = LoggerFactory.getLogger(OTNCompanion.class);
        logger.info("OTNCompanion created");
    }
    public void setOsmArea ( AreaElement earea ){
        this.area = earea;
        logger.info("area setted {}",earea);

    }

    public void setStorageDir ( String storageDir ) {
        this.storageDir = storageDir;
        System.out.println("set stroge Dir:" + storageDir);
    }

    public boolean isStorageDirSet (){
        if(storageDir.length()==0){
            return false;
        }

        if( ! new File(storageDir).isDirectory() ){
            return false;
        }

        return true;
    }

    public void createAddProfiles(boolean defaultProfile ){

        System.out.println("creating profiles");
        
        if (defaultProfile){
            System.out.println("default profiles ( ch car + alt foot)");
  
            this.profiles.add(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));
            this.profiles.add(new Profile("foot").setVehicle("foot").setWeighting("shortest").setTurnCosts(false));
            this.chProfiles.add( new CHProfile("car"));
            this.lmProfiles.add( new LMProfile("foot"));

        } else {

            System.out.println(" profile ch car");
            this.profiles.add(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));
            this.chProfiles.add( new CHProfile("car"));

        }

        this.ConsolidateProfiles();

    }

    private void ConsolidateProfiles(){
        this.ghConfig = new GraphHopperConfig();
        
        if (  this.profiles.size() != 0 ){
            ghConfig.setProfiles(profiles);
        }
        if (  this.chProfiles.size() != 0 ){
            ghConfig.setCHProfiles(chProfiles);
        }
        if ( this.lmProfiles.size() != 0 ){
            ghConfig.setLMProfiles(lmProfiles);
        }

    }

    public void setProfiles ( ProfilesTableDataModel  tableprofiles ){
        if (tableprofiles.getProfiles().size() ==0 ) {
            return;
        }
        profiles.clear();
        profiles.addAll( tableprofiles.getProfiles() );
        chProfiles.clear();
        chProfiles.addAll( tableprofiles.getCHprofiles( ) );
        lmProfiles.clear();
        lmProfiles.addAll(  tableprofiles.getlmProfiles( ) );
        
        this.ConsolidateProfiles();
    }

    private void storeProfiles(){
        if ( this.storageDir == "" ) {
            System.out.println("storage dir not set");
            return;
        }
        if ( this.area.getOsmFile().getPath() == "" ) { 
            System.out.println("osm file not set");
            return;
        }

        System.out.println("storign profiles to json file");

        Gson gson = new Gson();
        String jsonConfing = gson.toJson(this.ghConfig);
        File myObj=null;

        try {
        myObj = new File(storageDir +  File.separator + "config.json");
        if (myObj.createNewFile()) {
        System.out.println("File created: " + myObj.getName());
        } else {
            System.out.println("File already exists.");
        }
        FileWriter myWriter = new FileWriter(myObj);
        myWriter.write(jsonConfing);
        myWriter.close();
        System.out.println("Successfully wrote to the file.");

        } catch (IOException e) {
            System.out.println("An error occurred, creating or writing " + myObj.getName());
        }
        
    }


    public void createGraph( ) {
        if ( this.area.getOsmFile().getPath() == "" ) { 
            System.out.println("osm file not set");
            return;
        }
        if ( this.storageDir == "" ) {
            System.out.println("storage dir not set");
            return;
        }
        if ( this.profiles.size() == 0 ) { 
            System.out.println("no profile configured");
            return;
        }


        File dir = new File (storageDir +  File.separator + area.getName().replaceAll("\\s+","") );
        dir.mkdir();
        this.storageDir = dir.getPath();

        System.out.println("creating graph in " + storageDir );
        System.out.println("based on file " + area.getOsmFile().getPath() );
        

        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile( this.area.getOsmFile().getPath() ); // move to ghConfig ?
        GraphHopperConfig tmp = this.ghConfig;

        /*if ( diskStorage ) {
            DAType type = DAType.MMAP;
            tmp.putObject ("graph.dataaccess.default_type" , type.toString() );
            System.out.println(" set memory to disk");
        }*/

        hopper.init(tmp);
        hopper.setGraphHopperLocation(this.storageDir); // move to ghConfig ?
        System.out.println("creating graph, this may take a while....");
        long start = System.currentTimeMillis();
        hopper.importAndClose();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("graph finished, took:"+ Long.toString(timeElapsed/1000)+ " s");
        
        FileInputStream fis = null;
        FileOutputStream fos = null;
        storeProfiles();

        // copy poly
        if (  ( this.area.getPolyFile() != null ) ) {
            if (this.area.getPolyFile().exists() ) {
                fis = null;
                fos = null;
                try {
                    fis = new FileInputStream( this.area.getPolyFile() );
                    fos = new FileOutputStream(storageDir +  File.separator + "border" +".poly");
                    int c;

                    while ((c = fis.read()) != -1) {
                        fos.write(c);
                    }
                    System.out.println( "copied poly file successfully" );
                    if (fis != null) {
                        fis.close();
                    }
                    if (fos != null) {
                        fos.close();
                    }

                } catch (IOException ex) {
                    System.out.println(ex.toString());
                }
            }

        }
        this.storageDir= "";
            
    }

    public boolean isAnyProfileSet(){
        return ! profiles.isEmpty( );  
    }


    public void createVNSGraph(File kmlFile ){
        profiles.clear();
        chProfiles.clear();
        lmProfiles.clear();
        
        this.profiles.add(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));
        this.chProfiles.add( new CHProfile("car"));

        createGraph( );

        // copy kml
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(kmlFile);
            fos = new FileOutputStream(storageDir +  File.separator + "area.kml");
            int c;

            while ((c = fis.read()) != -1) {
                fos.write(c);
            }
            System.out.println( "copied kml file successfully" );
            
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }

        }catch (IOException ex) {
            System.out.println(ex.toString());
        }

         // create timestamp & .timestamp
        try{
           
        File areatimestamtp = new File (storageDir +  File.pathSeparator + "area.timestamp" );
        FileWriter timewriter = new FileWriter( areatimestamtp );
        BufferedWriter timebuffer = new BufferedWriter(timewriter);
        timebuffer.write( Instant.now().toString()  );
        timebuffer.flush();

        File timestamp =new File (storageDir +  File.pathSeparator + "timestamp" );
        timewriter = new FileWriter( timestamp );
        timebuffer = new BufferedWriter(timewriter);
        timebuffer.write( Instant.now().toString()  );
        timebuffer.flush();
        System.out.println( "created timestamps file successfully" );

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        
    }

    public void setForVNSGraph(){
        profiles.clear();
        chProfiles.clear();
        lmProfiles.clear();
        
        this.profiles.add(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));
        this.chProfiles.add( new CHProfile("car"));
    }
    
    public void completeVNSGraph(File kmlFile ){
    
        File dir = new File ( this.storageDir);
        
        FileInputStream fis = null;
        FileOutputStream fos = null;

        // test if generate has run
        /*if ( dir.isDirectory() ) {
            
        try {
            fis = new FileInputStream(kmlFile);
            fos = new FileOutputStream(storageDir +  File.separator + "area.kml");
            int c;

            while ((c = fis.read()) != -1) {
                fos.write(c);
            }
            System.out.println( "copied kml file successfully" );
            
            if (fis != null) {
                fis.close();
            }
            if (fos != null) {
                fos.close();
            }

        }catch (IOException ex) {
            System.out.println(ex.toString());
        }

         // create timestamp & .timestamp
        try{
           
        File areatimestamtp = new File (storageDir +  File.pathSeparator + "area.timestamp" );
        FileWriter timewriter = new FileWriter( areatimestamtp );
        BufferedWriter timebuffer = new BufferedWriter(timewriter);
        timebuffer.write( Instant.now().toString()  );
        timebuffer.flush();

        File timestamp =new File (storageDir +  File.pathSeparator + "timestamp" );
        timewriter = new FileWriter( timestamp );
        timebuffer = new BufferedWriter(timewriter);
        timebuffer.write( Instant.now().toString()  );
        timebuffer.flush();
        System.out.println( "created timestamps file successfully" );

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

        }*/
    }
}


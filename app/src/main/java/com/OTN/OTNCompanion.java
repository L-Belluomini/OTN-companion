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



public class OTNCompanion 
{
    private List<Profile> profiles = new LinkedList<Profile>();
    private List<CHProfile> chProfiles = new LinkedList<CHProfile>();
    private List<LMProfile> lmProfiles = new LinkedList<LMProfile>();
    private GraphHopperConfig ghConfig = null;
    private String fileDir="";
    private String storageDir ="";


    public OTNCompanion() {
    }

    public void setFileDir(String fileDir){
        this.fileDir = fileDir;
        System.out.println("set OSM file:" + fileDir);
    }

    public void setStorageDir ( String storageDir ) {
        this.storageDir = storageDir;
        System.out.println("set stroge Dir:" + storageDir);
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
        
        System.out.println("profile creation ended");
    }

    public void storeProfiles(){
        System.out.println("storign profiles to json file");

        Gson gson = new Gson();
        String jsonConfing = gson.toJson(this.ghConfig);
        File myObj=null;

        try {
        myObj = new File(storageDir + "/config.json");
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

    public void createGraph( boolean diskStorage) {
        if ( this.fileDir == "" ) { 
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

        System.out.println("creating graph in " + storageDir );
        System.out.println("based on file " + fileDir);
        

        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(this.fileDir); // move to ghConfig ?
        GraphHopperConfig tmp = this.ghConfig;

        if ( diskStorage ) {
            DAType type = DAType.MMAP;
            tmp.putObject ("graph.dataaccess.default_type" , type.toString() );
            System.out.println(" set memory to disk");
        }
        hopper.init(tmp);
        hopper.setGraphHopperLocation(this.storageDir); // move to ghConfig ?
        System.out.println("creating graph, this may take a while....");
        long start = System.currentTimeMillis();
        hopper.importAndClose();
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("graph finished, took:"+ Long.toString(timeElapsed/1000)+ " s");
    }

    /*public void loadJson(){

        GraphHopperConfig jConfig=null;
       try {
            Gson ason = new Gson();
            FileReader fReader =  new FileReader(storageDir + "/config.json");
            JsonReader reader = new JsonReader(fReader);
            jConfig = ason.fromJson (reader , GraphHopperConfig.class );
            if (jConfig == null){
                System.out.println( "reading j config failed");
                return;
            }
        } catch (IOException e) {
            System.out.println ( e.toString());
        }

    }*/
}


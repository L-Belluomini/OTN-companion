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
        System.out.println( "Hello World! \nI am OTN-companion, here to help" );
    }

    public void setFileDir(String fileDir){
        this.fileDir = fileDir;
    }

    public void setStorageDir ( String storageDir ) {
        this.storageDir = storageDir;
    }

    public void createAddProfiles(boolean defaultProfile){

        System.out.println("creating profiles");
        
        if (defaultProfile){
            System.out.println("default profiles ( ch car + foot)");
  
            this.profiles.add(new Profile("car").setVehicle("car").setWeighting("fastest").setTurnCosts(false));
            this.profiles.add(new Profile("foot").setVehicle("foot").setWeighting("shortest").setTurnCosts(false));
            this.chProfiles.add( new CHProfile("car"));

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

        storageDir = "target/routing-graph-cache"; // tmp

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

    public void createGraph(){
        System.out.println("creating graph in" + storageDir );
        System.out.println("based on file " + fileDir);

        storageDir = "target/routing-graph-cache"; // tmp

        this.fileDir = "file.pbf";
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(fileDir); // move to ghConfig ?
        hopper.init(this.ghConfig);
        hopper.setGraphHopperLocation(storageDir); // move to ghConfig ?
        System.out.println("creating graph, this may take a while....");
        hopper.importAndClose();
        System.out.println("graph finished");
    }

    public void loadJson(){

        GraphHopperConfig jConfig=null;
         try {
            Gson ason = new Gson();
            JsonReader reader = new JsonReader(new FileReader(storageDir + "/config.json"));
            jConfig = ason.fromJson (reader , GraphHopperConfig.class );
        } catch (IOException e) {
          System.out.println("An error occurred, reading " );
        }

    }
}


package com.OTN;

import com.OTN.OTNCompanion;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import picocli.CommandLine.HelpCommand;

import com.google.gson.Gson;
import com.google.gson.stream.*;

import com.graphhopper.GraphHopperConfig;
import com.graphhopper.GraphHopper;


import java.util.*;
import java.io.*;

@Command ( 
	name = "otn-companion" , 
	subcommands = {  CommandLine.HelpCommand.class} , 
	description = "companion for Open Tak navigation, made mostly to generate graph for offline navigation on bigger computer than phones"
)
public class cliInterface{
	@Spec CommandSpec spec;

	public static void main(String[] args) {
		int exitCode = new CommandLine ( new cliInterface ( ) ).execute(args);
		System.exit(exitCode);
  	}
  	@Command (
  		name = "generate",
  		description = "generate graph from OSM file",
  		mixinStandardHelpOptions = true,
  		sortOptions = false
  	)
  	int generateGraph ( 
  		@Parameters( arity = "1" , paramLabel ="storageDir" , description = "directory to contain the graph " )
  		String storageDir ,
  		@Option ( names ={ "-i" , "--inputfile"} , required = true , paramLabel ="OsmFile" , description = " OSM file" )
  		String fileDir ,
  		@Option ( names ={ "-d" , "--default"} , description = "load default profiles( ch car + foot)" , defaultValue = "false" )
  		boolean defaultProfile ,
  		@Option ( names = {"--car"} , description = "load just car profile (ch)" , defaultValue = "false")
  		boolean altProfile ,
        @Option ( names = {"--slow_mode"}, description = "use disk insted of ram for generating graph", defaultValue = "false")
        boolean diskStorage
  		) {

  		OTNCompanion companion  = new OTNCompanion();
  		companion.setStorageDir ( storageDir );
  		companion.setFileDir ( fileDir );
  		if (!defaultProfile  & !altProfile ) { // no option set fals back to default
  			System.out.println(" no option set, falling back to default");
  			defaultProfile = true;
  		} else if ( altProfile & defaultProfile ) { // if both set WORONG
  			System.out.println("DO NOT set deafult and other options, falling back to default");
  		}
  		companion.createAddProfiles( defaultProfile  );
  		companion.createGraph( diskStorage );
  		companion.storeProfiles();
  		return 0;
  	  	}
  	
  	@Command (
  		name = "check",
  		description = "check grapf constintency againt the profiles in the json",
  		mixinStandardHelpOptions = true
  	)
  	int checkGraph (
  		@Parameters( paramLabel ="storageDir" , description = "directory contaning the graph AND the json" )
		String storageDir
		) {
  		
  		GraphHopper hopper = new GraphHopper(); 
  		hopper.setGraphHopperLocation(storageDir);
  		Gson ason;
  		FileReader fReader;
  		JsonReader reader;
  		GraphHopperConfig jConfig;
  		System.out.println( "reading......");
  		try {
            ason = new Gson();
            fReader =  new FileReader(storageDir + "/config.json");
            reader = new JsonReader(fReader);
            jConfig = ason.fromJson (reader , GraphHopperConfig.class );
            if (jConfig == null){
                System.out.println( "reading j config failed");
                return 1;
            }
        } catch (Exception e) {
        	System.out.println( "reading j config failed with ERRROR:");
            System.out.println ( e.toString());
            return 1;
        }
        System.out.println( "reading j config CORRECT");
        	hopper.init(jConfig);
        	try {
        		hopper.load(storageDir);	
        	} catch (Exception e) {
        		System.out.println ( e.toString());
            	return 1;
        	}
            System.out.println( "loaded graph CORRECT");

        return 0;
		}

}
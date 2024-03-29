/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package midterm;


import logger.LoggerSingleton;
import gephi.GephiController;

import java.io.*;
import java.util.Arrays;

/**
 *
 * @author leonardo
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 3){
            System.out.println("Usage and exit:" );
            System.out.println("java midterm.Main [number of bit of identifier]"
                    + "[number of nodes][bucket dimension]" );
            return;
        }
        
        int m = Integer.parseInt(args[0]); //number of bit of identifier
        int n = Integer.parseInt(args[1]); //number of nodes
        int k = Integer.parseInt(args[2]); //bucket dimension
        int alpha = 3; // alpha is set to 3 at compile time, this can be easily changed
        
        System.out.printf("m=%d n=%d k=%d \n",m,n,k);
        
        if (n > Math.pow(2, m)) {
            System.out.println("TOO MANY NODES !");
            return;
        }
        
        String parameters = "m" + m + "n" + n + "k" + k;
        // preparing the statistics directory and getting the right dir for this run
        String filecsvdir = getWorkingDir(parameters);
        
        String filecsv = LoggerSingleton.setFilePath(filecsvdir + "graph");
        
        LoggerSingleton.setDEBUG(true);
        
        Orchestrator o =  new Orchestrator(n,m,k, alpha);
        // initialization phase
        o.init();     
        // route table construction
        o.routeTableConstruction();
        
        LoggerSingleton.getIstance().close();     
      
        // somethis like ./statistics/m8n100k10/run2/graph.csv should be passed
        GephiController gc = new GephiController(filecsv,m,k);
        gc.processGraph();
    }
    
    /**
     * the parameters are used as name to differentiate the runs;
     * each parameters will have their own directory and in each of them there
     * will be a directory for each run with the same parameters
     */
    private static String getWorkingDir(String parameters){
        String maindir = "./statistics/";
        // checking if the dir containing statistics exists
        File mainDirectory = new File(maindir);
        if (!mainDirectory.isDirectory()) {            
            mainDirectory.mkdirs();
            }
        // checking if the dir relative to this parameters exists
        File parametersDir = new File(maindir + parameters);
        if (!parametersDir.isDirectory()) {
            parametersDir.mkdirs();
        }
        
        File currentRun = new File(maindir + parameters + "/run1/");
        File[] filesList = parametersDir.listFiles();
        if (filesList.length == 0){
            // if the directory is empty just make the directory run1
            currentRun.mkdirs();
        }else{
           // if it's not empty check the last run number and create the
           // successive directory
          Arrays.sort(filesList);
          File lastRun = filesList[filesList.length -1];         
          String lastRunFilename = lastRun.getName();
          int lastRunNumber = Character.getNumericValue(lastRunFilename.charAt(lastRunFilename.length() -1));
          int newRunNumber = lastRunNumber +1;
          
          currentRun = new File(maindir + parameters + "/run" + newRunNumber + "/");
          currentRun.mkdirs();
        }
        return currentRun.getPath() + "/";
    }
}

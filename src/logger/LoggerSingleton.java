/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logger;
import node.Node.INode;
import java.io.*;

import java.util.*;
/**
 *
 * @author leonardo
 */
public class LoggerSingleton {
    private static LoggerSingleton __istance = null;
    private static boolean DEBUG = false;
    private static String filepath = "new.csv";

    
    private FileWriter csvWriter;
    private LoggerSingleton(){
        try{
            this.csvWriter = new FileWriter(filepath ); 
            this.write("Source,Target ");  

        } catch (IOException e ){
            System.out.println(e);
        }
    }   
    
    public void close(){ 
         try {            
            csvWriter.flush(); 
            csvWriter.close();
            
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public static LoggerSingleton getIstance(){
        if (__istance == null)
                __istance =  new LoggerSingleton();
        
        return __istance;
    }
    
    public void log(INode n1, INode n2) {
        if(DEBUG)
            System.out.println("Node " + n1.getIdentifier().toLong() + " linked to " 
                    +  n2.getIdentifier().toLong());
        
        this.write(n1.getIdentifier().toLong() + ","+ n2.getIdentifier().toLong() );   
        
    }
    
    public static String setFilePath(String fp){
        filepath = fp + ".csv";
        return filepath;
    }
    
    public static void setDEBUG(boolean v){
        DEBUG = v;
    }
    
    private void write(String s) {

        try{
            csvWriter.append(s + "\n");  
            csvWriter.flush(); 
            
        } catch (IOException e ){
            System.out.println(e);
        }
    }
}

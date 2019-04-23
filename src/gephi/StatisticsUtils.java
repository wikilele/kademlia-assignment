/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gephi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author leonardo
 */
public class StatisticsUtils {
    private final int m;
    private final int k;
    private final String statisticsDir ;
    private final String csvFileDir;
    
    public StatisticsUtils(int m, int k, String csvfiledir){
        this.m = m;
        this.k = k;
        this.csvFileDir = csvfiledir;
        this.statisticsDir = csvfiledir + "../../";
        
    }
    
    /**
     * This functions saves the statistics in two different csv files.\
     * The first one is 'local', it is svaed in the csvFileDir and contains the statistics
     * of the current run.\
     * The second file is relative to all the possible runs with different parameters.
     */
    public void saveStatistics(int nodeCount, int edgeCount, double degree, double ccoef,double diameter, double pathlength){
        String headercsv = "m, n, k, edeges, avg-degree, clustering_coeff, diameter, avg-pathlength\n";
        String line = m + "," + nodeCount + "," + k + "," + edgeCount + "," + 
                degree + "," + ccoef + "," + diameter + "," + pathlength + "\n";
      
        createLocalFile(headercsv, line, csvFileDir + "statistics.csv" );
        createAllstatsFile(headercsv, line,statisticsDir + "allstats.csv" );
    }
    
    private void createLocalFile(String header, String line, String path){
          try{
            FileWriter csvWriter = new FileWriter( path); 
            csvWriter.append(header ); 
            csvWriter.append(line ); 
            csvWriter.flush(); 
            csvWriter.close();
            
        } catch (IOException e ){
            System.out.println(e);
        }
    }
    
    private void createAllstatsFile(String header, String line, String path){
        try{
            File allstatscsv = new File(path);

            boolean justcreated = false;
            if (!allstatscsv.exists()) {
                allstatscsv.createNewFile();
                justcreated = true;
            } 
       
            FileWriter csvWriter = new FileWriter(path, true); 
           if (justcreated) 
               csvWriter.append(header); 
            csvWriter.append(line ); 
            csvWriter.flush(); 
            csvWriter.close();
            
        } catch (IOException e ){
            System.out.println(e);
        }
    }
    
    /**
     * The report is given as an html string which displays some images saved in tmp.\
     * This images are removed when we close this script, so the below function is used
     * to take the image and save it in the right place before it gets removed.
     */
    public void getANDsaveReport(String html, String filename){
            Document doc = Jsoup.parse(html);
            // the first img tag is what we need
            Element image = doc.select("img").first();
            // deleting the initial string 'file:'
            String url = image.absUrl("src").substring(5); 
            
            // copying the image in the right directory
            File source = new File(url);
            File dest = new File( csvFileDir + filename);
            try {
                Files.copy(source.toPath(), dest.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
}

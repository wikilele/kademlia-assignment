/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gephi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.gephi.io.exporter.api.ExportController;

import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.exporter.spi.GraphExporter;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
import org.gephi.layout.plugin.fruchterman.*;

import org.gephi.layout.plugin.AutoLayout;
import java.util.concurrent.TimeUnit;

import org.gephi.statistics.plugin.Degree;
import org.gephi.statistics.plugin.ClusteringCoefficient;
import org.gephi.statistics.plugin.GraphDistance;

import org.gephi.preview.api.*;
import org.gephi.preview.types.EdgeColor;
import java.awt.Color;

import org.jsoup.nodes.*;
import org.jsoup.Jsoup;
import java.nio.file.Files;

/**
 *
 * @author leonardo
 */
public class GephiController {
    private String storeDir ;
    private String csvFileName ;
    private String maindir ;
    private final int m;
    private final int k;
    
    public GephiController(String dirname, String csvfilename, String mdir, int m, int k){
        storeDir = dirname;
        csvFileName = csvfilename;
        maindir = mdir;
        this.m = m;
        this.k = k;
    }
    
    public void manageFilecsv(){      
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);

        //Import file
        Container container;
        try {
            
            File file_node = new File(csvFileName );
            container = importController.importFile(file_node);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
            container.getLoader().setAllowAutoNode(true);  //create missing nodes
            container.getLoader().setAutoScale(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        // Import Ended
        
        //Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);
        //Get a graph model- it exists because we have a workspace 
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
       
        //computing the statistics
        computeStatistics(graphModel);
         
        // Executing FruchtermanReingold  layout algorithm
        FruchtermanReingold frlayout = new FruchtermanReingold(new FruchtermanReingoldBuilder() );
       
        AutoLayout autoLayout = new AutoLayout(1, TimeUnit.SECONDS);
        autoLayout.setGraphModel(graphModel);
        autoLayout.addLayout(frlayout, 1f);
      
        autoLayout.execute();
        
        
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GRAY));

    
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 60);  
        
        // Export to file png or pdf
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
            ec.exportFile(new File(storeDir + "graph.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

    }
    
    private void computeStatistics(GraphModel graphModel){
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        // Get Nodes and Edges count
        int nodeCount = directedGraph.getNodeCount();
        int edgeCount = directedGraph.getEdgeCount();
        System.out.println("Nodes: " + nodeCount + " Edges : " + edgeCount);
    
        // Average Degree
        Degree deg = new Degree();
        deg.execute(graphModel);
        double degree = deg.getAverageDegree();
        System.out.println("Average degree " + degree);
        
        getANDsaveReport(deg.getDirectedReport(), "degree_distribution.png");
        
        // Clustering Coefficient
        ClusteringCoefficient clustcoeff = new ClusteringCoefficient();
        clustcoeff.execute(graphModel);
        double ccoef = clustcoeff.getAverageClusteringCoefficient();
        System.out.println("Avg clustering coeff " + ccoef);
       
        // Diameter and Avg path length
        GraphDistance gdistance = new GraphDistance();
        gdistance.execute(graphModel);
        double diameter = gdistance.getDiameter();
        double pathlength = gdistance.getPathLength();

        getANDsaveReport(gdistance.getReport(), "betweennescentrality_distribution.png");
        System.out.println("Diameter " + diameter);
        System.out.println("Path lenght " + pathlength);
        
        saveStatistics(nodeCount,edgeCount,degree, ccoef, diameter, pathlength);
    }
    
    private void saveStatistics(int nodeCount, int edgeCount, double degree,double ccoef,double diameter, double pathlength){
        String headercsv = "m, n, k, edeges, avg-degree, clustering_coeff, diameter, avg-pathlength\n";
        String line = m + "," + nodeCount + "," + k + "," + edgeCount + "," + 
                degree + "," + ccoef + "," + diameter + "," + pathlength + "\n";
        try{
            FileWriter csvWriter = new FileWriter(storeDir + "statistics.csv" ); 
            csvWriter.append(headercsv ); 
            csvWriter.append(line ); 
            csvWriter.flush(); 
            csvWriter.close();
            
        } catch (IOException e ){
            System.out.println(e);
        }
        
        
        try{
            File allstatscsv = new File(maindir + "allstats.csv");

            boolean justcreated = false;
            if (!allstatscsv.exists()) {
                allstatscsv.createNewFile();
                justcreated = true;
            } 
       
            FileWriter csvWriter = new FileWriter(maindir + "allstats.csv", true); 
           if (justcreated) 
               csvWriter.append(headercsv ); 
            csvWriter.append(line ); 
            csvWriter.flush(); 
            csvWriter.close();
            
        } catch (IOException e ){
            System.out.println(e);
        }
    }
    
    private void getANDsaveReport(String html, String filename){
            Document doc = Jsoup.parse(html);
            // the first img tag is what we need
            Element image = doc.select("img").first();
            // deleting the initial string 'file:'
            String url = image.absUrl("src").substring(5); 
            
            File source = new File(url);
            File dest = new File( storeDir + filename);
            try {
                copyFile(source,dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
    
    private static void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }

}

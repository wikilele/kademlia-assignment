/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gephi;

import java.io.File;

import java.io.IOException;

import org.gephi.io.exporter.api.ExportController;

import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;

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



/**
 *
 * @author leonardo
 */
public class GephiController {
    private final String csvFileDir ;
    private final String csvFilePath ;
   
    StatisticsUtils statUtils;
    
    /**
     * @param csvfilepath should be int he form: ./statistics/m8n100k10/run2/graph.csv
     */
    public GephiController( String csvfilepath, int m, int k){       
        csvFilePath = csvfilepath;
        csvFileDir = csvfilepath.substring(0, csvfilepath.length() -9);
       
        statUtils = new StatisticsUtils(m,k, csvFileDir);
    }
    
    public void processGraph(){      
        //Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //Get controllers and models
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);

        //Import file
        Container container;
        try {
            
            File file_node = new File(csvFilePath );
            container = importController.importFile(file_node);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
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
        int nodesNum = computeStatistics(graphModel);
        
        // generating the image for too many nodes is only time consuming
        // the image is good only for small numbers of nodes
        if (nodesNum <= 800){
            // Executing FruchtermanReingold  layout algorithm
            FruchtermanReingold frlayout = new FruchtermanReingold(new FruchtermanReingoldBuilder() );

            AutoLayout autoLayout = new AutoLayout(1, TimeUnit.SECONDS);
            autoLayout.setGraphModel(graphModel);
            autoLayout.addLayout(frlayout, 1f);

            autoLayout.execute();

            // just setting the edge color to have a better graph image
            PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
            PreviewModel previewModel = previewController.getModel();
            previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.GRAY));

            previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
            previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 60);  

            // Export to file png 
            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
            try {
                ec.exportFile(new File(csvFileDir + "graph.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }
    }
    /**
     * Get the statistics from the graphModels and saves them.
     * @return the number of nodes
     */
    private int computeStatistics(GraphModel graphModel){
        DirectedGraph directedGraph = graphModel.getDirectedGraph();
        // Get Nodes and Edges count
        int nodeCount = directedGraph.getNodeCount();
        int edgeCount = directedGraph.getEdgeCount();
        System.out.println("Nodes: " + nodeCount + " Edges : " + edgeCount);
    
        // Average Degree
        Degree deg = new Degree();
        deg.execute(directedGraph);
        double degree = deg.getAverageDegree();
        System.out.println("Average degree " + degree);
        
        // saving the degree distribution chart image
         statUtils.getANDsaveReport(deg.getDirectedReport(), "degree_distribution.png");
        
        // Clustering Coefficient
        ClusteringCoefficient clustcoeff = new ClusteringCoefficient();
        clustcoeff.execute(directedGraph);
        double ccoef = clustcoeff.getAverageClusteringCoefficient();
        System.out.println("Avg clustering coeff " + ccoef);
       
        // Diameter and Avg path length
        GraphDistance gdistance = new GraphDistance();
        gdistance.execute(directedGraph);
        double diameter = gdistance.getDiameter();
        double pathlength = gdistance.getPathLength();
        
        // saving the betweenness centrality  chart image
         statUtils.getANDsaveReport(gdistance.getReport(), "betweennescentrality_distribution.png");
        
        System.out.println("Diameter " + diameter);
        System.out.println("Path lenght " + pathlength);
        
         statUtils.saveStatistics(nodeCount,edgeCount,degree, ccoef, diameter, pathlength);
         
        return nodeCount;
    }

}

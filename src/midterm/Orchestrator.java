/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package midterm;
import java.util.ArrayList;
import java.util.List;

import kademlia.*;
import node.Identifier.BinStringIdentifier;
import node.Node.*;
import node.Identifier.IIdentifier;

/**
 *
 * @author leonardo
 */
public class Orchestrator {
    private List<Long> idList; //list of identifier already picked
    private final int n; //number of nodes
    private final int m; //length of the identifiers
    private final long maxIdsNum; //max number of identifiers
    private KademliaNodeList kademlia; // the data struct of kademlia

    
    public Orchestrator(int n, int m, int k, int a){
        this.n = n;
        this.m = m;
        maxIdsNum = (long)Math.pow(2, m);
        System.out.println("Max number of Id: " + maxIdsNum);
        
        idList = new ArrayList<Long>();  
               
        kademlia = new KademliaNodeList(m,k,a);        
    }

    /** 
    * A first node, whose identifier is selected at random,
    * is inserted in the data structure, with an empty routing table.\
    * This phase is executed only once.
    */
    public void init(){
        long randomId = pickRandomId();
        kademlia.addNode(randomId);
    }
    
    /**
     * it picks up the identifier of a node p chosen at random,
     * in the range of allowed identifiers, and simulates the join of p to the
     * Kademlia network.This is repeated n − 1 times.
     */    
    public void routeTableConstruction(){
        for (int i = 0; i < (n-1); i++){
            
            long randomId = pickRandomId();
            INode bootstrap = kademlia.getBootstrapNode();                             
            
            // the node is inserted after the choice of the bootstrap node,
            // to avoid choosing it.
            INode addedNode = kademlia.addNode(randomId);
            
            //the new node inserts the bootstrap in it's table
            addedNode.ADD_CONTACT(bootstrap);
                                   
            // generate m identifier which the new node will search to populate
            // i'ts routing table
            for(int j= 0; j < m ; j ++){
                IIdentifier tolookId = generateIdentifier(j);
                
                List<INode> closestNodes = addedNode.LOOKUP(tolookId);
                closestNodes.forEach(n -> addedNode.ADD_CONTACT(n));
            }
            
        }
       
    }
    
    /**
     * @return a random long not in the list of already choosen long.
     */
    private long pickRandomId(){
        long retval;

        do{
            double rand = Math.random() * (double)maxIdsNum;
            retval = (long) rand;           
        }while (idList.contains(retval));
        
        idList.add(retval); 
            
        return retval;
    }
    
    /**
     * The coordinator generates a sequence of identifiers ID, uniformly dis-
     * tributed, at random, in the identifiers range paired with the different
     * buckets of the routing tables of p.
     */
    private IIdentifier generateIdentifier(int j){
        // getting the range extremes
        double low = Math.pow(2, j);
        double high = Math.min(Math.pow(2, j +1), Math.pow(2, this.m));
        double diff = high - low;
        // computing a random from 0 to high - low
        double rnd = Math.random() * diff;
        // returning the id to be searched 
        return new BinStringIdentifier((long)(low + rnd), this.m);
    }
    
}

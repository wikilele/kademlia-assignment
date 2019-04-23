/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.Node;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import node.Identifier.IIdentifier;

/**
 *
 * @author leonardo
 * 
 * Since the LOOKUP procedure is quite big and complicated, it has been moved to
 * a separated class in order to have a cleaner code.
 */
public class NodeLookUpUtil {
    private int alpha; // the number of Node contacted at each iteration
    private INode holderNode;
    private int k;
    
    public NodeLookUpUtil(int a, INode n, int k){
        this.alpha = a;
        this.holderNode = n;
        this.k = k;
    }    
    
    public List<INode> lookUp(IIdentifier tolookID){
        // used to keep track of the nodes traversed by the FIND_NODE
        List<INode> traversed = new LinkedList(); 
        // this variable is used just to pack and unpack the FIND_NODE response
        FindNodeResponse response;
        // Closest node in absolute returned by the FIND_NODEs
        List<NodeTuple> kAbsoluteClosestNodes = new LinkedList<>(); 
        
        // taking alpha contacts from the non-empty k-bucket closest to the key
        // if a node has just entered the kademlia network,
        // it will have only the bootstrap node in its routing table
        response = holderNode.FIND_NODE(tolookID, traversed);
        
        traversed = response.getINodesTraversed();
        
        // adding the node fonded in the closest node, sorting and taking the first alpha
        kAbsoluteClosestNodes = addAll(kAbsoluteClosestNodes,response.getINodesFound(),tolookID);   
        kAbsoluteClosestNodes = this.sortANDlimitNodeTuples(kAbsoluteClosestNodes, alpha);
        
        // we assume the caller node has at leat one node in its routing table
        if (kAbsoluteClosestNodes.size() <= 0 ) return new LinkedList<INode>();
        
        // the absolutely closest node to the target
        INode closestNode = kAbsoluteClosestNodes.get(0).getNode();
        INode oldclosestNode; // this variable is used to exit the loop below
        
        do{
            // select from k-closest, alpha closest contacts which have not been queried yet
            List<NodeTuple> notQueriedAlphaNodes = new LinkedList<>();
            int i = 0;
            while(notQueriedAlphaNodes.size() < alpha && i < kAbsoluteClosestNodes.size() ){
                // the first three not queried node will be added to the list
                if(!kAbsoluteClosestNodes.get(i).isQueried())
                        notQueriedAlphaNodes.add(kAbsoluteClosestNodes.get(i));
                                                
            }
            
            // send FIND_NODE to the aplha choosen contacts
            for(NodeTuple nToQuery : notQueriedAlphaNodes){
                
                response = nToQuery.getNode().FIND_NODE(tolookID, traversed);
                nToQuery.setQueried(true);
                
                traversed = response.getINodesTraversed();               
                
                // add to k-closest the new received nodes, sorting and taking the first k
                kAbsoluteClosestNodes = addAll(kAbsoluteClosestNodes, response.getINodesFound(),tolookID);                
                kAbsoluteClosestNodes = this.sortANDlimitNodeTuples(kAbsoluteClosestNodes, k);
                
            }
            // update closestNode
            oldclosestNode = closestNode;
            closestNode = kAbsoluteClosestNodes.get(0).getNode();
         
            // we exit if closestNode remains the same for 2 consecutives iterations
        }while(!closestNode.equalTo(oldclosestNode));
        
        
        // send FIND_NODE to the remaining not queired nodes
        List<INode> returnedNodes = new LinkedList<>();
        for (NodeTuple t : kAbsoluteClosestNodes ){
            if(!t.isQueried()){
                response = t.getNode().FIND_NODE(tolookID, traversed);
                t.setQueried(true);
                
                returnedNodes.addAll(response.getINodesFound());
                traversed = response.getINodesTraversed();
            }
        }
        
        // adding to closest node, sorting and taking the first k
        kAbsoluteClosestNodes = addAll(kAbsoluteClosestNodes,returnedNodes,tolookID);
        kAbsoluteClosestNodes = this.sortANDlimitNodeTuples(kAbsoluteClosestNodes, k);
        
        // traversed will contain all the nodes traversed by the routing table
        // the holder node will populate its rt with this nodes
        traversed.forEach(n -> holderNode.ADD_CONTACT(n));
        
        // return the closest nodes
        List<INode> retList = new LinkedList<>(); 
        kAbsoluteClosestNodes.forEach(t -> retList.add(t.getNode()) );

        return retList;
    }
          
    
    private class NodeTuple {
        private final INode node;
        // the distance between node and the ID input of the LOOKUP procedure
        private final long xorDistance;
        private boolean queried;
        
        public NodeTuple(INode n, long d, boolean b){
            this.node = n;
            this.xorDistance = d;
            this.queried = b;
        }
        
        public INode getNode(){ return this.node;}
        public long getXorDistance(){ return this.xorDistance;}
        public boolean isQueried(){ return this.queried;}
        public void setQueried(boolean val){ this.queried = val;}
        
        @Override
        public boolean equals(Object o){ 
            NodeTuple n = (NodeTuple) o;
            return this.node.equalTo(n.getNode());
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 47 * hash + Objects.hashCode(this.node.getIdentifier());
            return hash;
        }
        
    }
    
    private List<NodeTuple> sortANDlimitNodeTuples (List<NodeTuple> input, int limit){
        int threshold = Math.min(input.size(),limit);
        List<NodeTuple> retList = input.stream()
                                   .sorted(Comparator.comparing(NodeTuple::getXorDistance))
                                   // it will use the redefined equals in the Tuple class
                                   .distinct()
                                   .limit(threshold)
                                   .collect(Collectors.toList());
        return retList;
    }
    
    private List<NodeTuple> addAll(List<NodeTuple> output, List<INode> input, IIdentifier id){
        for(INode n : input){
            output.add(new NodeTuple(n, id.xorDistance(n.getIdentifier()), false));
        }
        return output;
    }
}
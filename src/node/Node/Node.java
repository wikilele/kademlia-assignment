/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.Node;
import node.Identifier.*;
import node.RoutingTable.*;
import java.util.*;


/**
 *
 * @author leonardo
 */
public class Node extends INode{ 
    private NodeLookUpUtil lookUpUtil;
    
    public Node(IIdentifier id, IRoutingTable routtable, int alpha){
        super(id,routtable, alpha);
        // class implementing the LOOKUP function
        lookUpUtil = new NodeLookUpUtil(this.alpha, this, this.rt.getBucketSize());
    }
    
    @Override
    public void ADD_CONTACT(INode ni){
        if(ni.equalTo(this)) return;
       
        // The ADD_CONTACT logic id in the Routing Table class because it knows
        // the buckets. 
        rt.addContact(ni, ID.xorDistance(ni.getIdentifier()));
    }
   
    @Override
    public boolean PING(INode n){
        return true;      
    }    

    @Override
    public FindNodeResponse FIND_NODE(IIdentifier tofindID, List<INode> traversed){
        List<INode> nodesFound;
        if(tofindID.compareTo(this.getIdentifier()) == 0 )
            nodesFound =  new LinkedList<>();        
        else
            //The FIND_NODE logic is in the Routing Table class since it has the infos about the buckets.
            nodesFound = rt.findNode(ID.xorDistance(tofindID));
        
        // the nodes traversed by the previous FIND_NODEs are added to this.routingtable
        traversed.forEach(n -> this.ADD_CONTACT(n));
        traversed.add(this);
        
        return new FindNodeResponse(nodesFound, traversed);
    }
   
    
    @Override
    public List<INode> LOOKUP(IIdentifier tolookID){
        return lookUpUtil.lookUp(tolookID);
    }
}

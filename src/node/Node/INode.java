/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.Node;

import java.util.List;
import node.Identifier.IIdentifier;
import node.RoutingTable.IRoutingTable;


/**
 *
 * @author leonardo
 */
abstract public class INode { 
    protected IIdentifier ID; 
    protected IRoutingTable rt;
    protected final int alpha;
   
    /**
    * @param id identifier of the node
    * @param routtable the routing table of the node
    * @param a alpha used in the LOOKUP procedure
    */
    public INode(IIdentifier id, IRoutingTable routtable, int a){
        ID = id;
        rt = routtable;
        rt.initHolderNode(this);
        
        alpha = a;
    }
   
    public IIdentifier getIdentifier(){
       return ID;
    }
    
    public boolean equalTo(INode n){
        return ID.compareTo(n.getIdentifier()) == 0;
    }
       
    /**
    * @param tofindID MUST be different from this.ID
    * @param traversed a list of node traversed by the previous call of the FIND_NODE
    */
    abstract public FindNodeResponse FIND_NODE(IIdentifier tofindID, List<INode> traversed); 
    
    /**
     * This procedure mimics the LOOKUP one used in kademlia
     */
    abstract public List<INode> LOOKUP(IIdentifier tolookID);
   
    /**
    * @param ni  MUST be different from this node
    */
    abstract public void ADD_CONTACT(INode ni);
   
    // ping another node to verify if it is alive
    abstract public boolean PING(INode n);
     
    public void print(){
       System.out.println("Node " + ID.toLong());
       rt.print();
    }
    

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kademlia;
import java.util.*;
import node.Node.*;
import node.Identifier.*;
import node.RoutingTable.*;

/**
 *
 * @author leonardo
 */
public class KademliaNodeList {
    private final int m; //number of bit of identifier
    private final int k; //bucket dimension
    private final int alpha;
    
    /**
     * Since the assignment says: "initialize a data structure of n elements",
     * I decided to use a List; the List will reflect the concatenation of
     * leaves in the Kademlia's tree.
     */
    private List<INode> nodeList;
    
    public KademliaNodeList(int m, int k, int a){
        this.m = m;
        this.k = k;
        this.alpha = a;
        
        nodeList = new LinkedList<>();      
    }
    
    /**
     * @param index next element to be compared with node
     * The list is in descending order.
     */
    private void __addNodeRecursive(INode n, int index){
        if (index >= nodeList.size())
            // end of the list append the element there
            nodeList.add(n);
        else {
            INode nextElem = nodeList.get(index);
                      
            if (n.getIdentifier().compareTo(nextElem.getIdentifier()) > 0)
                nodeList.add(index, n);
            
            else __addNodeRecursive(n, index +1);
        }
    }
     
    public INode addNode(long randomId){
        IIdentifier id = new BinStringIdentifier(randomId, m);
        IRoutingTable rt = new RoutingTable(m,k);
        INode addn = new Node(id, rt, alpha);
        __addNodeRecursive(addn,0);
        return addn;
    }
    /**
     * @return a node taken at random in the Node List. This node is supposed to
     * be used as bootstrap node for the join of a new node.
     */
    public INode getBootstrapNode(){
        Random rand = new Random();
        int pos = rand.nextInt(nodeList.size());
        return nodeList.get(pos);
    }

        
    public void print(){
        nodeList.forEach((node) -> node.print());
    }
    
}

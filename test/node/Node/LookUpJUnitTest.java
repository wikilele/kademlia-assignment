/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.Node;

import java.math.BigInteger;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

import kademlia.KademliaNodeList;
import node.Identifier.IIdentifier;
import node.Identifier.BigIntIdentifier;
import java.util.Random;
import java.util.LinkedList;

/**
 *
 * @author leonardo
 */
public class LookUpJUnitTest {
    private final int m = 4;
    private final int k = 2;
    private final int alpha = 3;
    private final int n = 15;
    private KademliaNodeList kademlia;
    
    public LookUpJUnitTest() {
        kademlia = new KademliaNodeList(m,k, alpha);
        kademlia.addNode(BigInteger.ZERO);
    }
        
    /**
     * Simple lookup test with only two nodes
     */
    @Test
    public void testLookUp1() {
            
        INode bootstrap = kademlia.getBootstrapNode();          
        
        INode addedNode = kademlia.addNode(BigInteger.valueOf(1));

        addedNode.ADD_CONTACT(bootstrap);
        // the id value doesn't matter in this case                           
        IIdentifier tolookId = new BigIntIdentifier(BigInteger.valueOf(10),m);
                
        List<INode> closestNodes = addedNode.LOOKUP(tolookId);
        
        for(INode n : closestNodes){
            assertTrue(n.equalTo(addedNode) || n.equalTo(bootstrap));
        }    
    }
    
    /**
     * Testing that the identifiers ruturned are in a correct range
     */
    @Test
    public void testLookUp2() {
        List<INode> allNodes = new LinkedList<>();
        allNodes.add(kademlia.getBootstrapNode());
        for (int i = 1; i < n; i++){   
                       
            INode addedNode = kademlia.addNode(BigInteger.valueOf(i));
            for(INode n :allNodes){
                addedNode.ADD_CONTACT(n);
            }
            allNodes.add(addedNode);
     
        }
        IIdentifier tolookId = new BigIntIdentifier(BigInteger.valueOf(2),m);
        INode node;
        do{
            node = kademlia.getBootstrapNode();
        } while(node.getIdentifier().toLong() == 0);
        List<INode> returnedNodes = node.LOOKUP(tolookId);
        
        
        returnedNodes.forEach(n -> assertTrue(n.getIdentifier().toLong() <= 3));
    }
    
}

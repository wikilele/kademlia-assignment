/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.RoutingTable;

import java.math.BigInteger;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

import node.Node.*;
import node.Identifier.*;

/**
 *
 * @author leonardo
 */
public class BucketJUnitTest {
    Bucket b;
    INode nodeToRemove = null;
    final int m = 4;
    final int k = 10;
    
    
    public BucketJUnitTest() {
        b = new Bucket(0);
        
        for(long i=0; i<10; i++){
            INode tmp = new Node(new BinStringIdentifier(BigInteger.valueOf(i),m),
                                    new RoutingTable(4,20),3);
            b.promoteToTail(tmp);
            
            if (i==5) nodeToRemove = tmp;
        }
        
    }

    @Test
    public void testRemove() {
        int oldsize = b.getActualSize();
        b.remove(nodeToRemove);
        
        assertEquals(oldsize - 1, b.getActualSize());
        
        List<INode> nodeList = b.getINodeList(k);
        for(INode ni: nodeList){
            if(ni.getIdentifier() == nodeToRemove.getIdentifier())
                fail("This node must not be there!");
        }        
                
    }
}

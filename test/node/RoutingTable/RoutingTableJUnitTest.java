/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.RoutingTable;


import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import node.Node.*;
import node.Identifier.*;

import java.math.BigInteger;
/**
 *
 * @author leonardo
 */
public class RoutingTableJUnitTest  {
    private RoutingTable rt;
    private Node holderNode; //the node holding the table
    
    private IRoutingTable mockrt; //just used to initialize other nodes
    
    private final int m = 4;
    private final int k = 10;
    
    public RoutingTableJUnitTest() {
       
        rt = new RoutingTable(m,k);
        holderNode = new Node(new BigIntIdentifier(BigInteger.valueOf(0),m),rt,3);       
        
        mockrt = new RoutingTable(m,k);
    }
    
    /**
     * Testing if all elements are added and returned
     */
    @Test 
    public void testAddContactAndFindNode1() {        
        for(long i = 1; i <= k; i++){
            Node toAdd = new Node(new BigIntIdentifier(BigInteger.valueOf(i),m),mockrt,3);
            long xor = holderNode.getIdentifier().toLong() ^i;
   
            rt.addContact(toAdd, BigInteger.valueOf(xor));
        }
        rt.print();
        // in that case the xor distance is irrelevant because it must return k elem
        List<INode> lni = rt.findNode(BigInteger.valueOf(1));
        
        assertEquals(k,lni.size());
    
    }
    
    /**
     * Testing if exactly k elemetns are returned even if there are k+1 
     * elements in the whole table
     */     
    @Test 
    public void testAddContactAndFindNode2() {
        System.out.println("test 2");
        rt.print();
        for(long i = 1; i <= k+1; i++){
            Node toAdd = new Node(new BigIntIdentifier(BigInteger.valueOf(i),m),mockrt,3);
            BigInteger xor = holderNode.getIdentifier().xorDistance(toAdd.getIdentifier());
   
            rt.addContact(toAdd, xor);
        }
        rt.print();
        // in that case the xor distance is irrelevant because it must return k elem
        List<INode> lni = rt.findNode(BigInteger.valueOf(1));
              
        assertEquals(k,lni.size());
    }
    
    /**
     * Testing if the bucket size is respected and the Node alive is reinserted
     * at the end of the bucket. 
     * The result depends on the return value of Node.PING, this test anyway
     * is aware of that.
     */
    @Test
    public void testAddContactAndFindNode3() {
        //A particular setup is needed for that test
        rt = new RoutingTable(m,3);
        holderNode = new Node(new BigIntIdentifier(BigInteger.valueOf(0),m),rt,3);      
        
        for(long i = 1; i <= 7; i++){
            Node toAdd = new Node(new BigIntIdentifier(BigInteger.valueOf(i),m),mockrt,3);
            long xor = holderNode.getIdentifier().toLong() ^i;
   
            rt.addContact(toAdd, BigInteger.valueOf(xor));
        }
        rt.print();
        // want to address bucket with index 2 (so distance from 4 to 7)
        List<INode> lni = rt.findNode(BigInteger.valueOf(4));
        long lastElemId = lni.get(lni.size()-1).getIdentifier().toLong();
        
        assertTrue(lastElemId == 4 || lastElemId == 7 );   
    
    }
    
    
    /**
     * Testing the reinsertion of an already existing node.
     */
    @Test
    public void testAddContactAndFindNode4() {
        
        for(long i = 1; i <= k; i++){
            INode toAdd = new Node(new BigIntIdentifier(BigInteger.valueOf(i),m),mockrt,3);
            long xor = holderNode.getIdentifier().toLong() ^i;
   
            rt.addContact(toAdd, BigInteger.valueOf(xor));
        }
        //reinserting an exsiting node
        INode toAdd = new Node(new BigIntIdentifier(BigInteger.valueOf(2),m),mockrt,3);
        long xor = holderNode.getIdentifier().toLong() ^2;
   
        rt.addContact(toAdd, BigInteger.valueOf(xor));

        rt.print();
        /**
         * The Node with id 2 now should be at the tail of bucket 1 (distance from 2 to 3)
         * Since bucket 1  has only two elements the Node will be in the
         * second position of the returned list.
         */
        List<INode> lni = rt.findNode(BigInteger.valueOf(2));
        
        long secondElemId = lni.get(1).getIdentifier().toLong();
        
        assertTrue(secondElemId == 2);   
    }
    
    /**
     *  Testing the retriving of elements from different buckets but not from
     *  all ones.
     */    
    @Test
    public void testAddContactAndFindNode5() {
        //A particular setup is needed for that test
        rt = new RoutingTable(m,3);
        holderNode = new Node(new BigIntIdentifier(BigInteger.valueOf(0),m),rt,3);
        
        for(long i = 1; i <= 6; i++){
            Node toAdd = new Node(new BigIntIdentifier(BigInteger.valueOf(i),m),mockrt,3);
            long xor = holderNode.getIdentifier().toLong() ^i;
   
            rt.addContact(toAdd, BigInteger.valueOf(xor));
        }
        rt.print();
        // want to address bucket with index 1 (so distance from 2 to 4)
        List<INode> lni = rt.findNode(BigInteger.valueOf(2));
        
        for(INode ni : lni){
            long id = ni.getIdentifier().toLong();
            
            assertTrue(id == 2 || id == 3 || id == 1);
        }
    }
    
    /**
     *  Testing the FIND_NODE with an id not belonging to a created node
     */    
    @Test
    public void testAddContactAndFindNode6() {
       
        for(long i = 1; i <= 10; i++){
            Node toAdd = new Node(new BigIntIdentifier(BigInteger.valueOf(i),m),mockrt,3);
            long xor = holderNode.getIdentifier().toLong() ^i;
   
            rt.addContact(toAdd, BigInteger.valueOf(xor));
        }
        rt.print();
        // want to address bucket with index 1 (so distance from 2 to 4)
        List<INode> lni = rt.findNode(BigInteger.valueOf(11)).subList(0, 3);
        
        for(INode ni : lni){
            long id = ni.getIdentifier().toLong();
            
            assertTrue(id == 8 || id == 9 || id == 10);
        }
      
    }

}

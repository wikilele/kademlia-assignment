/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.RoutingTable;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import node.Node.*;

import logger.LoggerSingleton;
import java.math.BigInteger;;
/**
 *
 * @author leonardo
 */
public class RoutingTable implements IRoutingTable{
    private final int bucketSize;
    private final int bucketNumber ;
    private List<Bucket> bucketList;
    // a reference to the node holding this table
    private INode holderNode = null;
    
    /**
     * @param m  number of buckets
     * @param k size of buckets
     */
    public RoutingTable( int m, int k){
        bucketNumber = m;
        bucketSize = k;
        bucketList = new ArrayList<Bucket>();
        for(int i=0; i< m; i++){
            // the position of the bucket in the list gives us also the range 
            // of elements it manages            
            bucketList.add(new Bucket(i));
        }
    }
    
    public void initHolderNode(INode n){
        if (holderNode == null)
                holderNode = n;
        else return;
    }
    
    public int getBucketSize(){return this.bucketSize;}
    
    /**
     * @param xordistance MUST be grater than zero
     */
    private int findBucketIndex(BigInteger xordistance) throws IndexOutOfBoundsException {
        if (xordistance.compareTo(BigInteger.ZERO)== 0)
            throw new IndexOutOfBoundsException("xor distance cannot be 0!");
        
        
        int i = xordistance.bitLength() -1;
        return i;
    }
    

    public List<INode> findNode(BigInteger xordistance){       
        List<INode> retList = new LinkedList<INode>();
        
        int index = -1;
        try{
            index = findBucketIndex(xordistance);
        } catch (IndexOutOfBoundsException e){
            index = 0;
        }
        int tmpindex = index;
        
        // if the tmpindex wraps around means that we have visited all the buckets
        // so we must return the list even if its size is not bucketSize
        boolean returnedAll = false;
        int scanningIndex = -1;
        while (retList.size() < bucketSize && !returnedAll ){
            Bucket b = bucketList.get(tmpindex);
            
            retList.addAll(b.getINodeList(bucketSize - retList.size()));       

            // We first check in index -1, then index + 1, then index -2,
            // index +2, -3, +3 and so on.
            // 
            // We use a trick to compute the module and not the reminder.
            // If we just use the % operator we will get negative numbers.
            tmpindex = (((index + scanningIndex) % bucketNumber )
                                            + bucketNumber ) % bucketNumber ;
            
            // if index is -1 it becomes just 1
            // if index is 1 it becoms -2 and so on and so forth.
            scanningIndex = (scanningIndex < 0) ? -scanningIndex: -(scanningIndex +1);

            returnedAll = (tmpindex == index);
        }

        return retList;
    }
    

    public void addContact(INode contact, BigInteger xordistance){      
        int index = -1;
        try{
            index = findBucketIndex(xordistance);
        }catch (IndexOutOfBoundsException e){
            return;
        }
        Bucket b = bucketList.get(index);
        
        if (null != b.remove(contact)){
            //Yes, the contact does exists
            b.promoteToTail(contact);
        } else {
            //no, the contact does not exist
            if (b.getActualSize() < bucketSize){
                // the bucket is not full
                b.promoteToTail(contact);
                LoggerSingleton.getIstance().log(holderNode, contact);
            } else{
                // the bucket is full
                INode leastSeenNode = b.getHead();
                if (holderNode.PING(leastSeenNode)){
                    // The node is alive
                    // promote it to tail
                    b.remove(leastSeenNode);
                    b.promoteToTail(leastSeenNode);                   
                } else {
                    // the node is not alive
                    //evict the node
                    b.remove(leastSeenNode);
                    //promote the contact to tail
                    b.promoteToTail(contact);
                    LoggerSingleton.getIstance().log(holderNode, contact);
                }
            }
        }
    }
    
    public void print(){
        for (Bucket b : bucketList){
            b.print();
        }
        System.out.println();
    }

}

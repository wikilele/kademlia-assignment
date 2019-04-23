/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.RoutingTable;

import java.util.List;
import node.Node.*;
import java.math.BigInteger;

/**
 *
 * @author leonardo
 */
public interface IRoutingTable {
    
    /**
     * @param holderNode the node holding this table
     * This function should be called only once
     */
    public void initHolderNode(INode holderNode);
    
    /**
     * @return the size of its buckets (must be the k parameter) 
     */
    public int getBucketSize();
    
    /**
     * @param xordistance MUST be greater than 0
     * @return should return a List of k INode closest to the target
     */
    public List<INode> findNode(BigInteger xordistance);
    
    /**
     * @param contact MUST not be the node holding this table
     * @param xordistance  MUST be greater than 0
     */
    public void addContact(INode contact, BigInteger xordistance);
    
    public void print();
}

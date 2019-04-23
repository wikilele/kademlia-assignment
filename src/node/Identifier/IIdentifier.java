/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.Identifier;

/**
 *
 * @author leonardo
 */
public interface IIdentifier extends Comparable<IIdentifier> {
    
    public int length();
    public long xorDistance(IIdentifier id);
         
    public String toString();
    public long toLong();
    
}

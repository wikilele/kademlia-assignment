/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.Identifier;
import java.math.BigInteger;
/**
 *
 * @author leonardo
 */
public interface IIdentifier extends Comparable<IIdentifier> {
    
    public int length();
    public BigInteger xorDistance(IIdentifier id);
    
    public BigInteger toBigInteger();
    
    public String toString();  
    // should be used only for output purposes
    public long toLong();
    
}

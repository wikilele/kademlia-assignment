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
public class BigIntIdentifier implements IIdentifier {
    private BigInteger ID ;
    private int m;
    
    /**
    * @assumption alll the identifier must be initialized passing the same vale of m
    */
    public BigIntIdentifier(BigInteger id, int m){
        // padding the string with zeroes until it reaches length m
        /*ID = String.format("%1$" + m + "s", Long.toBinaryString(id))
                    .replace(" ","0");  */
        this.m = m;
        ID = id;
                    
    }
    
    public int length(){
        return m;
    }
    
    // Computing the xor distance between this and another id
    public BigInteger xorDistance(IIdentifier id){
        return ID.xor(id.toBigInteger());
    }
    
    /**
     * The comparing is based on the numerical value of the identifier.\
     * This function does not consider the xor distance.
     */
    public int compareTo(IIdentifier id){
        return ID.compareTo(id.toBigInteger());
    }
    
    public String toString(){
        return ID.toString();
    }
    public BigInteger toBigInteger(){
        return ID;
    }
    
    public long toLong(){
        return ID.longValue();
    }
}

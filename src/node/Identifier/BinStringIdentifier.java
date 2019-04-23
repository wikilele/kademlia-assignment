/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.Identifier;
import java.util.Hashtable;
/**
 *
 * @author leonardo
 */
public class BinStringIdentifier implements IIdentifier {
    private String ID ;
    
    /**
    * @assumption alll the identifier must be initialized passing the same vale of m
    */
    public BinStringIdentifier(long id, int m){
        // padding the string with zeroes until it reaches length m
        ID = String.format("%1$" + m + "s", Long.toBinaryString(id))
                    .replace(" ","0");                   
    }
    
    public int length(){
        return ID.length();
    }
    
    // Computing the xor distance between this and another id
    public long xorDistance(IIdentifier id){
        return this.toLong() ^ id.toLong();
    }
    
    /**
     * The comparing is based on the numerical value of the identifier.\
     * This function does not consider the xor distance.
     */
    public int compareTo(IIdentifier id){
        return (int) (this.toLong() - id.toLong());
    }
    
    public String toString(){
        return ID;
    }
    public long toLong(){
        return Long.parseLong(ID, 2);
    }
}

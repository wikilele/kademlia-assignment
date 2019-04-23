/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.RoutingTable;
import node.Node.INode;
import java.util.List;
import java.util.LinkedList;
/**
 *
 * @author leonardo
 */
public class Bucket {
    /**
     * the bucket will manage elements which
     * xor(this.id, tofind.id) belongs to [2^i,2^(i+1))
     */
    private int index;
    private List<INode> nodeList;
    
    public Bucket(int i){
        this.index = i;
        nodeList = new LinkedList<INode>();
    }
    
    public int getActualSize(){
        return nodeList.size();
    }
    
    //append
    public void promoteToTail(INode ni){
        nodeList.add(ni);
    }
    
    public INode remove(INode ni){
        for(INode listElem: nodeList){
            if (listElem.equalTo(ni)){
                nodeList.remove(listElem);
                return listElem;
            }
        }
        return null;
    }
    
    // least recently seen node
    public INode getHead(){
        return nodeList.get(0);
    }
    
    public List<INode> getINodeList(int quantity){
        if (quantity >= this.getActualSize())
            return nodeList;
        else 
            // gettting the last elements
            // fromIndex is included, toIndex is not
            return nodeList.subList(nodeList.size() - quantity, nodeList.size() );
    }
    
    public void print(){
        // just to avoid lots of printing
        if (nodeList.isEmpty()) return;
        
        System.out.printf("%d :: [%d,%d) :: ",
                index, (int)Math.pow(2, index), (int)Math.pow(2, index +1));
        for(INode ni: nodeList){
            System.out.print("|" + ni.getIdentifier().toLong() + "| ");
        }
        System.out.println();
    }
}

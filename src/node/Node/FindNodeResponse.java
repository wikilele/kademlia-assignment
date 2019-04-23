/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package node.Node;

import java.util.List;

/**
 *
 * @author leonardo
 * 
 * This class is used to return 2 elements in the FIND_NODE procedure.
 */
public class FindNodeResponse{
    // the list of elements founded closest to the FIND_NODE input
    private List<INode> nodesFound;
    // the list of elements traversed by the FIND_NODE procedure
    private List<INode> nodesTraversed;
        
    public FindNodeResponse(List<INode> nf, List<INode> nt){
        nodesFound = nf;
        nodesTraversed = nt;
    }
        
    public List<INode> getINodesFound(){return this.nodesFound;}
    public List<INode> getINodesTraversed(){return this.nodesTraversed;}
        
}
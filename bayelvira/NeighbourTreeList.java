import NeighbourTree;
import RelationList;
import NodeJoinTree;
import Graph;
import Bnet;
import java.util.Vector;
import java.io.*;

/**
 * This class implements the basic structure that a node uses to 
 * access to his neighbours.
 * 
 * @since 21/7/99
 */

public class NeighbourTreeList {

    /**
     * Contains the list of neighbours of this node
     */
    
    Vector neighbourList;
 
 /**
  * Constructor 1.
  */
   
   NeighbourTreeList() {
      neighbourList = new Vector();      
   }        
   
   
   /**
    * This method is used to set the value of the JointTreeNodes variable.
    * 
    * @param V The list of the nodes of the Joint Tree
    */
   
   public void setNeighbourList (Vector V) {
      neighbourList = V;
   }
  
   
   /**
    * This method is used for accessing to the list of nodes.
    * 
    * @return The list of NodeJointTree.
    */
   
   public Vector getNeighbourList () {
      return neighbourList;
   } 
   
   
   /**
    * Returns the Relation in the position of parameter p.
    * 
    * @param p The position of the relation to obtain
    * @return The Relation at position p.
    */
   
   public NeighbourTree elementAt(int p){
      return ( (NeighbourTree) neighbourList.elementAt(p));
   }
        
        
   /**
    * Inserts a NodeJointTree at the end of the list of nodes.
    * 
    * @param N The node to insert
    */

   public void insertNeighbour (NeighbourTree N) {
      neighbourList.addElement(N);
   }
   
   
   /**
    * Removes a NodeJoinTree of the list of nodes
    * 
    * @param N Node to remove.
    */

   public void removeNeighbour (NeighbourTree N) {
      neighbourList.removeElement(N);
   }
   
   
  /**
   * This method returns the position of the element passed
   * 
   * @param R A relation
   * @param N
   * @return The position of R in the list or -1 is R is not in it.
   */
  
   public int indexOf(NeighbourTree N) {   
      return neighbourList.indexOf(N);
   }


/**
 * Returns the position of a node in the list of neighbours.
 * @param n a NodeJoinTree.
 * @return the position of node n in the list of neighbours.
 */

public int indexOf(NodeJoinTree n) {

  int i, s;
  NeighbourTree nt;
  
  s = neighbourList.size();
  
  for (i=0 ; i<s ; i++) {
    nt = elementAt(i);
    if (nt.getNeighbour() == n)
      return i;
  }
  return (-1);
}


/**
 * Returns the position of a node in the list of neighbours.
 * @param n the label of the node  to search.
 * @return the position of node n in the list of neighbours.
 */

public int indexOf(int n) {

  int i, s;
  NeighbourTree nt;
  
  s = neighbourList.size();
  
  for (i=0 ; i<s ; i++) {
    nt = elementAt(i);
    if (nt.getNeighbour().getLabel() == n)
      return i;
  }
  return (-1);
}


   /**
    * This method calculates the number of neighbours of the node
    * 
    * @return The number of relations of the list.
    */	 
  
   public int size() {
      return neighbourList.size();
   }

   /**
    * Removes the element in the position p.
    * 
    * @param p The position of the element to remove
    */
      

   public void removeElementAt (int p) {
      neighbourList.removeElementAt(p);
   }

   /**
    * Removes a node from the list of neighbours
    * 
    * @param NodetoRemove The node to remove
    */
   
   public void removeNeighbour (NodeJoinTree NodetoRemove) {
      int i = 0;
      boolean found = false;
      NeighbourTree NeighbourNode;
      
      while ((i<this.size()) && (!found)) {
         NeighbourNode = (NeighbourTree) this.neighbourList.elementAt(i);
         if (NeighbourNode.neighbour.nodeRelation.isTheSame(NodetoRemove.nodeRelation)) {
            this.removeNeighbour(NeighbourNode);
            found = true;
         }
         i++;
      }
   }
   

/**
 * Gets the message towards a given node.
 * @param n the node towards which the message is searched.
 * @return the message.
 */

public Relation getMessage(NodeJoinTree n) {
 
  int pos;
  
  pos = indexOf(n.getLabel());
  
  return elementAt(pos).getMessage();
}
  
  
} // end of class
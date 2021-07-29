import NodeJoinTree;
import Relation;

/** Class NeighbourTree. Implements the structure that will contain the 
    the information about one neighbour of a node in the Joint Tree. For working
    with this objects it's recommended to use a Vector 
    
    Last modified: 8/6/99*/

public class NeighbourTree {
   
   /** Contains the Node of the Joint Tree that is neighbouring */
   NodeJoinTree neighbour;  
   
   /** Message */
   Relation message;
    
   /** Constructor */
    
    NeighbourTree() {
        neighbour = new NodeJoinTree();
        message = new Relation();
    }
   
   
   /**
    * This function is used to set the neighbour node.
    * 
    * @param N The node to set.
    */
   
   public void setNeighbour (NodeJoinTree N) {
      neighbour = N;
   }
   
   
   /**
    * This function is used to set the message.
    * 
    * @param R The message (it is a relation).
    */
   
   public void setMessage (Relation R) {
      message = R;
   }
   
   
   /**
    * This function is used to get the node that is neighbouring.
    * 
    * @return The neighbour.
    */
   
   public NodeJoinTree getNeighbour () {
      return neighbour;
   }
   
   
   /**
    * This function is used to get the message.
    * 
    * @return The message.
    */
   
   public Relation getMessage () {
      return message;
   }
   
   
} // end of class
    
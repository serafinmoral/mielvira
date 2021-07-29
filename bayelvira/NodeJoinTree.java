import Relation;
import java.util.Vector;
import NeighbourTreeList;

/**
 * This class is used to contain every node of a Joint Tree
 * @since 23/6/99
 */

public class NodeJoinTree {
   
   /**
    * Contains the clique of the node.
    */
   Relation nodeRelation;
   
   /**
    * A list with the neighbours of the node.
    */
   NeighbourTreeList neighbourList;
   
   /**
    * A label
    */
   
   int label;
    
    
   /**
    * Constructor. Creates an empty NodeJoinTree
    */
    
   NodeJoinTree (){
     nodeRelation = new Relation();
     neighbourList = new NeighbourTreeList();
   }
    
    
    /**
     * Constructor 2. Introduces the relation R in the nodeRelation.
     * 
     * @param R
     */
    
    NodeJoinTree (Relation R) {

      neighbourList = new NeighbourTreeList();
      nodeRelation = R;
    }
    
    
    /**
     * Introduces the Relation R as the nodeRelation.
     * 
     * @param R Relation that will set in the NodeJoinTree.
     */
    
    public void setNodeRelation (Relation R) {
        nodeRelation = R;
    }
    
    
    /**
     * This method set the Neighbour List.
     * 
     * @param List Contains the list of the neighbours.
     */
    
    public void setNeighbourList (NeighbourTreeList List) {
        neighbourList = List;
    }
    
    
    /**
     * Set the label.
     * @param l the label.
     */
    
    public void setLabel(int l) {
      label = l;
    }
    
    
    /**
     * Get the label.
     * @return the label.
     */
    
    public int getLabel() {
      return label;
    }
    
    
    /**
     * This function is used to get clique of the node.
     * 
     * @return The Relation of the node.
     */
    
    public Relation getNodeRelation () {
        return nodeRelation;
    }

    
    /**
     * Gets the list of variables in the node.
     * @return a NodeList with the variables in the node.
     */
    
    public NodeList getVariables() {
     
      return nodeRelation.getVariables();
    }
    
    
    /**
     * This function is used to get list of neighbours.
     * 
     * @return The neighbours of the node.
     */
    
     public NeighbourTreeList getNeighbourList () {
       return neighbourList;
     }
   
   
   /**
    * Removes all the neighbours of a node except the one who contains the 
    * relation R as message.
    * 
    * @param R is the message that determines the neighbour that does not
    * removes.
    */
    
   public void removeOtherParents (Relation R) {      
      int i;
      NeighbourTree element;
      Relation tmp;
      
      for (i=0; i<this.neighbourList.size(); i++) {
         element = new NeighbourTree();
         element = (NeighbourTree) this.neighbourList.elementAt(i);
         tmp = (Relation) element.getMessage();
         if (!tmp.isTheSame(R))
            this.neighbourList.removeElementAt(i);
      }
      
   }
 
   
   /**
    * Determines whether a node is a leaf or not. A node is
    * considered to be a leaf if it has less than two neighbours.
    * @return true if the node is a leaf, false otherwise.
    */
   
   public boolean isLeaf() {
    
     if (neighbourList.size()<2)
       return true;
     return false;
   }
   
   
   /**
    * Inserts a neighbour.
    * @param neighbour a NodeJoinTree that will be a neighbour
    * of this.
    */
   
   public void insertNeighbour(NodeJoinTree neighbour) {
    
     NeighbourTree nt;
     Relation msg;
     
     nt = new NeighbourTree();
     nt.setNeighbour(neighbour);
     
     // Now compute the message
     msg = getNodeRelation().intersection(neighbour.getNodeRelation());
     nt.setMessage(msg);
     
     neighbourList.insertNeighbour(nt);
   }
   
   
   /**
    * Removes a neighbour.
    * @param neighbour the neighbour to remove (a NodeJoinTree)
    */
   
   public void removeNeighbour(NodeJoinTree neighbour) {
    
     neighbourList.removeNeighbour(neighbour);
   }


   /**
    * Absorb from node. That is, this ask a sum-message to node and the 
    * message is combined with the potential stored in this. Messages
    * are modified.
    *
    * @param node the nodeJoinTree from which this absorbs
    * @param divide "yes" if division is performed during the absortion 
    */

    public void absorbFromNode(NodeJoinTree node, String divide) {
      Relation R,Rnode,message;
      Potential pot,newSep; 
      NeighbourTreeList ntl;
      NeighbourTree nt;
      int i;
     
      ntl = node.getNeighbourList();
      i = ntl.indexOf(this);
      if (i==-1) {
        System.out.println("NodeJoinTree.absorbFromNode:error: no neighbours");
        System.exit(0);
      }
      Rnode = node.getNodeRelation();
      pot = Rnode.getValues();
      nt = ntl.elementAt(i);
      message = nt.getMessage();
      if (divide.equals("yes")) {
        nt.getMessage().setOtherValues(
                         nt.getMessage().getValues());
      }
      newSep = 
         pot.marginalizePotential(nt.getMessage().getVariables().toVector());

      nt.getMessage().setValues(newSep);
      
      R = this.getNodeRelation();
      if (divide.equals("yes")){
        pot = (R.getValues()).combine(
                 newSep.divide(nt.getMessage().getOtherValues()));  
      }
      else pot = (R.getValues()).combine(newSep);       
      R.setValues(pot);
    }
    
public void print() {  
  NeighbourTree nt;
  int j;
  
  System.out.println("Node "+getLabel()+" has variables :");
  getNodeRelation().print();
  System.out.println("Node "+getLabel()+" has neighbours :");
  for (j=0 ; j<getNeighbourList().size() ; j++) {
    nt = getNeighbourList().elementAt(j);
    System.out.println("Label : "+nt.getNeighbour().getLabel());
    
    if(nt.getMessage()!=null){
      if(nt.getMessage().getValues()!=null) {
	System.out.println("OUTGOING MESSAGE");
	((PotentialMTree)nt.getMessage().getValues()).print();
      }
      if(nt.getMessage().getOtherValues()!=null) {
	System.out.println("INCOMING MESSAGE");
	((PotentialMTree)nt.getMessage().getOtherValues()).print();
      }
    }
  }
  System.out.println(" ");
}



} // end of class

import java.util.Vector;
import FiniteStates;
import Relation;
import NodePairTable;


/**
 * Class name : PairTable.
 * Class description : Implements a list where each element
 *                     is a pair (FiniteStates,Vector),
 *                     where Vector containd relations where X
 *                     appears.
 *
 * Last modified : 28/04/99.
 */

public class PairTable {
  
  Vector info;
  
  /**
   * Creates an empty table.
   */
  
  PairTable() {
    
    info = new Vector();
  }
  
  
  /**
   * Inserts an element at the end of the list.
   * @param node the element to insert.
   */
  
  public void addElement(NodePairTable node) {
   
    info.addElement(node);
  }
  
  
  /**
   * Inserts an element at the end of the list.
   * The element is a FiniteStates variable with no relations.
   * It is assumed that var is not in the list.
   * @param var a FiniteStates variable.
   */
  
  public void addElement(FiniteStates var) {
  
    NodePairTable node;
    
    node = new NodePairTable(var);
    info.addElement(node);
  }
  
  /**
   * @return the size of this pairtable
   */ 

  public int size(){
    return info.size();
  }

  /**
   * @return the element in position i
   *
   * @param i the index
   */

  public NodePairTable elementAt(int i){
    return (NodePairTable)info.elementAt(i);
  }
  
  /**
   * Inserts a relation in the position corresponding to
   * variable var. var is assumed to be in the list.
   * @param var the key variable.
   * @param rel the relation to insert.
   */
  
  public void addRelation(FiniteStates var, Relation rel) {

    NodePairTable node;
    boolean done = false;
    int i = 0;
    
    while ((!done) && (i<info.size())) {
      node = (NodePairTable)(info.elementAt(i));
      if (node.variable == var) {
	done = true;
	node.relations.addElement(rel);
      }
      i++;
    }
  }
  
  /**
   * Inserts a relation in all the positions corresponding
   * to variables in the domain of the valuation.
   * @param rel a Relation.
   */
  
    public void addRelation(Relation rel) {

    NodePairTable node;
    int i = 0;
    
    for (i=0 ; i<info.size() ; i++) {
      node = (NodePairTable)(info.elementAt(i));
      
      if (rel.getVariables().getId(node.variable)>-1)
	node.relations.addElement(rel);
    }
  }
  
  
  /**
   * Removes a relation from all the positions where it
   * appears.
   * @param rel the relation to remove.
   */
  
  public void removeRelation(Relation rel) {
   
    int i, s;
    NodePairTable node;
    
    s = info.size();
    
    for (i=s-1 ; i>=0 ; i--) {
      node = (NodePairTable)(info.elementAt(i));
      node.relations.removeElement(rel);
    }
  }
  
  
  /**
   * Remove element with key var.
   * @param var a FiniteStates variable.
   */
  
  public void removeVariable(FiniteStates var) {
    
    int i, s;
    NodePairTable node;
    
    s = info.size();
    
    for (i=s-1 ; i>=0 ; i--) {
      node = (NodePairTable)(info.elementAt(i));
      
      if (node.variable == var) {
	info.removeElementAt(i);
	break;
      }
    }
  }
  
  
  /**
   * Return the next node to remove according to
   * the criterium of minimum size.
   * @return the variable to remove.
   */
  
  public FiniteStates nextToRemove() {
   
    NodePairTable node;
    int i;
    double s, min = 90.0E20;
    FiniteStates var;
    
    var = ((NodePairTable)info.elementAt(0)).variable;

    for (i=0 ; i<info.size() ; i++) {   
       
      node = (NodePairTable)info.elementAt(i);
      
      // If the node is in just one relation, remove it.
      if (node.relations.size()==1)
	return node.variable;
      
      s = node.totalSize();
      
      if (s<min) {
	min = s;
	var = node.variable;	
      }
    }
    
    return var;
  }
  


  /**
   * Return the next node to remove according to
   * the criterium of minimum size, and taking into account that
   * the deletion sequece obtained is a constrained deletion sequence,
   * where the nodes of set have to be the nodes sited at the end of 
   * the sequence
   *
   * @return the variable to remove.
   * @param set the list of nodes that must be removed only after
   * the rest of nodes have been removed
   */
  
  public FiniteStates nextToRemove(NodeList set) {
   
    NodePairTable node;
    int i;
    double s, min = 90.0E20;
    FiniteStates var;
    
    var = ((NodePairTable)info.elementAt(0)).variable;

    for (i=0 ; i<info.size() ; i++) {   
       
      node = (NodePairTable)info.elementAt(i);
      
      s = node.totalSize();
      
      if (set.getId(node.getVariable())==-1){ // in this way the value s for a node not in
        s = (double) -1/s;      // set is always slower that for a node in
      }				// set. The minus sign is to mantain the
				// order among the nodes not in set

      if (s<min) {
	min = s;
	var = node.getVariable();	
      }
    }
    
    return var;
  }
  
  
  
  /**
   * Return the next node to remove according to
   * the criterium of minimum size. The node must be
   * different of the argument.
   * @param notRemovable a FiniteStates.
   * @return the variable to remove.
   */
  
  public FiniteStates nextToRemove(FiniteStates notRemovable) {
   
    NodePairTable node;
    int i;
    double s, min = 90.0E20;
    FiniteStates var;
    
    var = ((NodePairTable)info.elementAt(0)).variable;
    
    if (var==notRemovable)
      var = new FiniteStates();

    for (i=0 ; i<info.size() ; i++) {   
       
      node = (NodePairTable)info.elementAt(i);
      
      if ((node.relations.size()==1)&&(node.variable!=notRemovable))
	return node.variable;
      
      s = node.totalSize();
      
      if ((s<min) && (node.variable!=notRemovable)) {
	min = s;
	var = node.variable;	
      }
    }
    
    return var;
  }
  
}
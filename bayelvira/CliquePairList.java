import java.io.*;
import java.util.Vector;

/**
 * Class CliquePairList - 
 * Implements a list of CliquePair in which the elements are ranked
 * in ascendent order attending to one of the following parameters:
 * size, restricted size or ratio.
 *
 * @since 17/09/99
 */

public class CliquePairList {

/**
 * The list
 */

private Vector list;

/**
 * Constructor: creates a list from a join tree, a list of variables and 
 * the criterion used to rank the elements.
 * 
 * IMPORTANT: the nodes of the jointree must be labeled.
 *
 * @param jt the jointree
 * @param set the nodelist
 * @param criterion 
 */

public CliquePairList(JoinTree jt, NodeList set, String criterion ) {

  int i,j,s;
  NodeJoinTree nodeHead,nodeTail;
  NeighbourTreeList ntl;
  NeighbourTree nt;
  CliquePair cp;
  NodeList nl,nl2;

  list = new Vector();
  s = jt.size();
  for (i=0; i<s; i++) {
    nodeHead = jt.elementAt(i);
    ntl = nodeHead.getNeighbourList();
    for(j=0;j<ntl.size();j++){
      nt = ntl.elementAt(j);
      nodeTail = nt.getNeighbour();
      // the following comparison is to avoid the introduction of the
      // same link two times
      if (nodeHead.getLabel() < nodeTail.getLabel()){
        // Now we have to test if the separator contains variables not in set
        nl = (nodeHead.getVariables()).intersection(nodeTail.getVariables());
        nl2 = nl.intersection(set);
        if (nl.size() != nl2.size()){ // some variables in nl are not in set
          cp = new CliquePair(nodeHead,nodeTail,set);
          addElement(cp,criterion);
        }
        else // one of the nodes is a subset of the other 
          if ( nl.equals(nodeHead.getVariables()) ||
                      nl.equals(nodeTail.getVariables()) ){ 
            cp = new CliquePair(nodeHead,nodeTail,set);
            addElement(cp,criterion);
          }
      }          
    }
  }
  
}                                             

/**
 * @returns the element in position i
 *
 * @param i the index
 */

public CliquePair elementAt(int i) {
  return (CliquePair)list.elementAt(i);
}   

/**
 * @return the element in the first position and remove it from the list
 */
 
public CliquePair getFirstAndRemove(){
  CliquePair cp;
  
  cp = (CliquePair) list.elementAt(0);
  list.removeElementAt(0);   
  return cp;
}

/**
 * @returns the size of list
 */

public int size( ) {
  return list.size();
}   

/**
 * Add a new element to the list, mantaining the rank
 *
 * @param e the element to add
 * @param criterion the criterion used for the ranking
 */

public void addElement(CliquePair e, String criterion) {

  int min,max,middle;
  int s;
  double eValue,middleValue;


  eValue = e.getValue(criterion);
  s = list.size();
  if (s == 0){
    middle = 0;
  }
  else if ( ((CliquePair)list.elementAt(s-1)).getValue(criterion) < eValue){
      middle = s;
  }
  else{
    for(min = 0, max = s-1, middle = (int) (min+max)/2; 
        min < max; 
        middle = (int) (min+max)/2){
      middleValue = ((CliquePair)list.elementAt(middle)).getValue(criterion);
      if (middleValue > eValue){
        max = middle;
      }
      else if (middleValue < eValue) {
        min = middle + 1;
      }
      else break; // middle is the possition 
    }
  }
  // Insert the element in middle
  list.insertElementAt(e,middle);

}

/**
 * Removes all the elements (links) in the list which contain the clique passed
 * as argument.
 *
 * @param clique 
 */

public void removeAllElements(NodeJoinTree clique){

  int i,s;
  CliquePair cp;

  s = list.size();
  for(i=0; i<s; ){
    cp = elementAt(i);
    if ( (clique == cp.getHead()) || (clique == cp.getTail()) ){
      list.removeElementAt(i);
    }
    else i++;
  }

}


/**
 * @return a list containing all the elements (links) in this list which 
 * contain the clique passed as argument. The elements will be removed also.
 *
 * @param clique 
 */

public Vector getListAndRemoveElements(NodeJoinTree clique){

  int i;
  CliquePair cp;
  Vector v = new Vector();

  for(i=0; i<list.size(); ){
    cp = elementAt(i);
    if ( (clique == cp.getHead()) || (clique == cp.getTail()) ){
      v.addElement(cp);
      list.removeElementAt(i);
    }
    else i++;
  }
  
  return v;

}
         
/**
 * print the object
 */

public void print(){
 
  int i;

  System.out.println("Printing CliquePairList");
  for(i=0;i<this.size();i++){
    System.out.println("Element at " + i);
    this.elementAt(i).print();
  }

}

} // end of class
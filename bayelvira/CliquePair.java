import java.io.*;
import java.util.Vector;

/**
 * Class CliquePair - 
 * This class implements an object which contains two nodeJoinTrees that
 * are neighbours in a join tree. The class will be used for the implementation
 * of some heuristics used in the process of the inner restriction of a
 * join tree respect a list of varibles. Therefore, some extra information
 * is stores for each link:
 *  - size: the size of the variable list formed with the union of both cliques
 *  - restrictedSize: the size of the clique formed from this link, that is,
 *    removing from the whole set the variables not in set, except those necessarys 
 *    in order to mantain a join tree (the join property holds after to 
 *    replace this link by the new clique)
 *  - ratio: the quotient restrictedSize/size.
 *
 * @since 16/09/99
 */

public class CliquePair {

/**
 * The two NodeJoinTree sited in the extrems of the link. Although the link in
 * a join tree is undirected, we use the directed terminologie head and 
 * tail for the cliques in the link.
 */

private NodeJoinTree head;
private NodeJoinTree tail;

/**
 * The induced variable list by the union of head and tail.
 */

private NodeList completeList;

/**
 * The restricted variable list.
 */

private NodeList restrictedList;

/**
 * The numerical data described in the beginning of this class.
 */

private double size;
private double restrictedSize;
private double ratio;


/**
 * Constructor. Builds a cliquePair from two cliques and the set of variables
 * to which the jointree has to be restricted.
 * 
 * @param cliqueA
 * @param cliqueB
 * @param set the list of variables
 */

public CliquePair(NodeJoinTree cliqueA, NodeJoinTree cliqueB, NodeList set){
  NodeList varsSep;
  NeighbourTreeList ntl;
  NeighbourTree nt;
  NodeJoinTree node,node2,other;
  Vector headTail;
  Relation R;
  int i,j;
  
  head = cliqueA;
  tail = cliqueB;
  
  completeList = new NodeList(); 
  completeList.join(head.getVariables()); 
  completeList.join(tail.getVariables()); 
  size = (int) FiniteStates.getSize(completeList);
  
  // now a list with the variables in all the links (separators) with head
  // or tail in an extrem (except for the link head-tail) is build, because
  // this variables has to be mantained in the new clique in order to ensure
  // the join property.
  
  varsSep = new NodeList();
  headTail = new Vector();   // as the process is the same for the two nodes,
  headTail.addElement(head); // we introduce both in a vector and use an
  headTail.addElement(tail); // interative procedure
  
  for(i=0;i<2;i++){
    node = (NodeJoinTree)headTail.elementAt(i);
    other = (NodeJoinTree)headTail.elementAt((i+1)%2);
    ntl = node.getNeighbourList();
    for(j=0;j<ntl.size();j++){
      nt = ntl.elementAt(j);
      node2 = nt.getNeighbour();
      if (node2 != other){
        R = nt.getMessage();
        varsSep.join(R.getVariables());
      }
    }
  }
  
  restrictedList = completeList.intersection(set);
  restrictedList.join(varsSep);
  
  restrictedSize = (int) FiniteStates.getSize(restrictedList);
  ratio = restrictedSize/size;     
}

// Access methods

/**
 * @return the nodeJoinTree in head
 */
 
 public NodeJoinTree getHead(){
   return head;
 }
 
 /**
 * @return the nodeJoinTree in tail
 */
 
 public NodeJoinTree getTail(){
   return tail;
 }
 

 /**
  * @return completeList
  */

 public NodeList getCompleteList(){
   return completeList;
 }

 /**
  * @return restrictedList
  */

 public NodeList getRestrictedList(){
   return restrictedList;
 }



 /**
 * @return size
 */
 
 public double getSize(){
   return size;
 }

 /**
 * @return restrictedSize
 */
 
 public double getRestrictedSize(){
   return restrictedSize;
 }
 
 /**
 * @return ratio
 */
 
 public double getRatio(){
   return ratio;
 }
 

 /** 
  * @return the value specified by criterion
  *
  * @param criterion
  */ 
 
  public double getValue(String criterion){

    if (criterion.equals("size")){
      return size;
    }
    else if (criterion.equals("restrictedSize")){
      return restrictedSize;
    }
    else if (criterion.equals("ratio")) {
      return (restrictedSize + ratio);
    }
    else {
      System.out.println("CliquePair:getValue:ERROR --- unknown criterion");
      System.exit(0); 
    }       

    return -1; // the method never will arrive until this point
  }

/**
 * Print a cliquePair
 */

public void print(){
  int i;
  NodeList nl;

  System.out.println("\tHead: " + this.getHead().getLabel());
  System.out.println("\tTail: " + this.getTail().getLabel());
  System.out.print("\tCompleteList: ");
  nl = this.getCompleteList();
  for(i=0; i<nl.size(); i++)
    System.out.print(nl.elementAt(i).getName() + " ");
  System.out.println();
  System.out.print("\tRestrictedList: ");
  nl = this.getRestrictedList();
  for(i=0; i<nl.size(); i++)
    System.out.print(nl.elementAt(i).getName() + " ");
  System.out.println();
  System.out.println("\tSize:           " + this.getSize());
  System.out.println("\tRestrictedSize: " + this.getRestrictedSize());
  System.out.println("\tRatio:          " + this.getRatio());


}

} // end of class

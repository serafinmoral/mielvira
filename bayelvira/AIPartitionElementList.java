import java.io.*;
import java.util.Vector;

/**
 * Class AIPartitionElementList - 
 * (short name for AbductiveInferencePartitionElementList)
 * Implements a list of AIPartitionElements sorted from greater to lower
 * probability. 
 *
 * @since 26/08/99
 */

public class AIPartitionElementList {

/**
 * The list
 */

private Vector list;

/**
 * Constructor: creates an empty list 
 */

AIPartitionElementList( ) {
  list = new Vector();
}                                             

/**
 * @returns the element in position i
 *
 * @param i the index
 */

public AIPartitionElement elementAt(int i) {
  return (AIPartitionElement)list.elementAt(i);
}


/**
 * Add a new element to the list, mantaining the rank
 *
 * @param e the element to add
 */

public void addElement(AIPartitionElement e) {

  int min,max,middle;
  int s;
  double prob,pmiddle;


  prob = e.getProb();
  s = list.size();
  if (s == 0){
    middle = 0;
  } else if ( ( ((AIPartitionElement)list.elementAt(s-1)).getProb() > prob)){
    middle = s;
  }
  else{
    for(min = 0, max = s-1, middle = (int) (min+max)/2; 
        min < max; 
        middle = (int) (min+max)/2){
      pmiddle = ((AIPartitionElement)list.elementAt(middle)).getProb();
      if (pmiddle < prob){
        max = middle;
      }
      else if (pmiddle > prob) {
        min = middle + 1;
      }
      else break; // middle is the possition 
    }
  }
  // Insert the element in middle
  list.insertElementAt(e,middle);

}


/**
 * Removes the element in position i
 *
 * @param i the position of the element to be removed
 */

public void removeElementAt(int i) {
  list.removeElementAt(i);
}


/**
 * Removes all the elements in the list except the k first
 *
 * @param k the number of elements to mantain in the list
 */

public void truncate(int k){

  int i,s;

  s = list.size();
  if (s > k)
    for(i=s-1; i>=k; i--) list.removeElementAt(i);  
}


/**
 * Print the list
 */ 

public void print ( ){

  int i,s;

  System.out.println("The elements in the partition are: ");

  s = list.size();
  for(i=0; i<s; i++){
    System.out.println("Element " + i);
    ((AIPartitionElement)list.elementAt(i)).print();
    System.out.println();
  }

}

} // end of class
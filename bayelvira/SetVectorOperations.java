/**
 * this class contains some useful set operations implemented for the 
 * class Vector
 *
 * @since 20/08/99
 */

import java.util.Vector;

public class SetVectorOperations{

  
  /**
   * @return the union of A and B
   * @param A a vector
   * @param B the other vector
   */
  

  public Vector union(Vector A, Vector B){

    Vector aux = new Vector();
    int i;

    aux = (Vector) A.clone();
    for(i=0;i<B.size();i++)
      if (!(aux.contains(B.elementAt(i))))  
         aux.addElement(B.elementAt(i));

    return aux;

  }
  
  /**
   * @return the intersection of A and B
   * @param A a vector
   * @param B the other vector
   */
  
  public Vector intersection(Vector A, Vector B){
  
    Vector aux = new Vector();
    int i,j;

    for(i=0;i<A.size();i++)
      if (B.contains(A.elementAt(i))) aux.addElement(A.elementAt(i)); 
   
    return aux;
  }
  
  /**
   * @return the elements of A that are not in B
   * @param A a vector
   * @param B the other vector
   */

  public Vector notIn(Vector A, Vector B){
    Vector aux = new Vector();
    int i,j;
    
    for(i=0;i<A.size();i++)
      if (!B.contains(A.elementAt(i))) aux.addElement(A.elementAt(i));
      
    return aux;
  }

} // end of class

/* FiniteStates.java */

import java.util.Vector;
import Node;
import Network;
import java.io.*;


/**
 * Implements the class of nodes corresponding to
 * discrete random variables with a finite number of
 * possible values.
 * All the objects of this class has the kindOfNode's variable
 * set to Chance and the typeOfVariable's variable set to 
 * Finite States
 *
 * Last modified : 14/09/99
 */
  
public class FiniteStates extends Node implements Cloneable {

   private Vector states;
   private int numStates;
   private int transparency;
   
   public static int NOT_TRANSPARENT = 0;
   public static int TRANSPARENT = 1;
   

   /**
    * Creates a new empty FiniteStates object. 
    */
   public FiniteStates() {
      setTypeOfVariable(FINITE_STATES);
      setKindOfNode(CHANCE);
      setTransparency(NOT_TRANSPARENT);
   }


   /**
    * Creates a FiniteStates object with the name and
    * the states given as parameters.
    *
    * @param nam the name of the new node.
    * @param stat the states of the new node.
    */

   public FiniteStates(String nam, Vector stat) {

      this ();
      numStates=stat.size();

      setName(nam);
      setStates (stat);  
   }

   /**
    * Creates a FiniteStates object with the parameters given
    *
    * @param n the name of the new node.
    * @param x the x coordinate.
    * @param y the y coordinate.
    * @param a Contains the states of the node created
    */

   public FiniteStates(String n, int x, int y, Vector defaultStates){
      
      this (n, defaultStates);
      setPosX (x);
      setPosY (y);
      setTypeOfVariable(FINITE_STATES);
      setKindOfNode(CHANCE);
      setStates(defaultStates);
   }



   /**
    * Creates a new FiniteStates object with the name given. To set 
    * the default states uses an array of strings.
    *
    * @param nam the name of the new node.
    * @param stat the states of the new node as an array of String.
    */

   public FiniteStates (String nam, String[] stat) {

      this ();
      
      numStates=stat.length;
      setName(nam);

      states = new Vector();
      for (int i=0; i<numStates; i++)
         states.addElement(stat[i]);
   }


   /**
    * Creates a new FiniteStates object with the number of states
    * given as parameter
    * @param n the number of states of the new node.
    * the states will be referred by numbers from 0 to n-1.
    */

   public FiniteStates(int n) {

      this ();
      
      Integer in;
      int i;
   
      states=new Vector();
      numStates=n;

      for (i=0 ; i<numStates ; i++) {
         in=new Integer(i);
         states.addElement(in.toString());
      }
   }


/* ****************** Access methods **************** */

   /**
    * @return the number of states of this node.
    */

   public int getNumStates() {
      return(numStates);
   }


   /**
    * @return the vector of states of this node.
    */

   public Vector getStates() {
      return(states);
   }


   /**
   * @param i a number of state.
   * @return the name of state number i.
   */

   public String getState(int i) {
      return((String) states.elementAt(i));
   }


/************************ Modifiers *********************/

   /**
   * Sets the number of states to n.
   * @param n the number of states.
   */

   public void setNumStates(int n) {
      numStates=n;
   }


   /**
   * Sets the states and the number of states of this node.
   * @param stat the vector of states.
   */

   public void setStates(Vector stat) {
      numStates = stat.size();
      states = stat;
   }
   
   public void setTransparency(int trans_type) {
      transparency = trans_type;
   }


   
   /**
   * Sets the states and the number of states of this node.
   * @param s an array of strings containing the states.
   */

   public void setStates(String stat[]) {

      int i;
        
      numStates=stat.length;

      states = new Vector();
      for (i=0; i<numStates; i++)
         states.addElement(stat[i]);
   }


   /**
    * For knowing the number of a state. This number is the
    * position of the string in the states vector.
    * 
    * @param stat the name of the state.
    * @return The position of the state in the vector or -1 in
    *         the string is not found.
    */

   public int getId(String stat) {
     
      int i;
        
      i=-1;
      for(i=0; i<states.size(); i++)
         if( stat.equals((String) states.elementAt(i)))
            return(i);
        
      return(i);	 
   }  
     

   /**
    * @return the states of the node as an array of strings.
    */

   public String[] getStringStates() {
     
      String nodeStates[];
      int i;

      nodeStates = new String[numStates];
      for (i=0 ; i<states.size() ; i++)
         nodeStates[i]=(String) states.elementAt(i);

      return nodeStates;
   }


   /**
    * Saves the information about a FiniteStates object using the
    * text output stream p.
    * @param p a PrintWriter (the file).
    */

   public void save(PrintWriter p) {
     
      String nodeStates[];
      int i;
        
      p.print("finite-states "+getName() + " {\n");
        
      super.save(p);
        
      p.print("num-states = " + numStates + ";\n");
      p.print("states = (");
      nodeStates = getStringStates();
        
      for(i=0 ; i<numStates-1 ; i++)
         p.print(nodeStates[i] + " ");
       
      p.print(nodeStates[numStates-1] + ");\n");
        
      p.print("}\n\n");
   }  


   /**
    * Creates a soft copy of this node, i.e., a reference.
    * Thus, modifying the new node, the old one is also modified.
    * @return a copy of this node.
    */

   public FiniteStates copy() {

      FiniteStates newNode;

      newNode = new FiniteStates();
      newNode.setTitle (getTitle());
      newNode.setName (getName());
      newNode.setComment (getComment());
      newNode.numStates = numStates;
      newNode.states = states;
      newNode.setTypeOfVariable (getTypeOfVariable());
      newNode.setKindOfNode (getKindOfNode());
      return newNode;
   }

   /**
   * Creates a hard copy (clone)  of this node.
   * @return a clone of this node.
   */


   public Object clone(){

      FiniteStates o = null;
       
      o = (FiniteStates)super.clone();
      o.states = (Vector)states.clone();
      for(int i=0 ;i < o.states.size() ; i++){
	   String aux = new String((String)states.elementAt(i));
	   o.states.setElementAt(aux,i);
      }
      return o;

   }


   /**
   * @param v a vector of FinteStates nodes.
   * @return the size of a hypothetical probability table
   * corresponding to the variables in v; i.e. the product of
   * the number of cases of all the variables.
   */

   public static double getSize(Vector v) {

      int i;
      double s = 1.0;

      for (i=0; i<v.size(); i++)
         s*=((FiniteStates)v.elementAt(i)).numStates;

      return s;
   }


   /**
   * @param list a NodeList of FinteStates nodes.
   * @return the size of a hypothetical probability table
   * corresponding to the variables in list; i.e. the product of
   * the number of cases of all the variables.
   */

   public static double getSize(NodeList list) {

      int i;
      double s = 1.0;

      for (i=0; i<list.size(); i++)
         s*=((FiniteStates)list.elementAt(i)).numStates;

      return s;
   }


   /**
   * @param v a vector of FiniteStates nodes.
   * @return the position of this node in vector v, -1 if the
   * node is not in the vector.
   */

   public int indexOf(Vector V) {

      int i;
      FiniteStates Aux;

      for (i=0; i<V.size(); i++)
         {
            Aux=(FiniteStates)V.elementAt(i);
            if (compareTo(Aux)==0)
	      return (i);
         }

      return (-1);
      }

}  // end of FiniteStates class

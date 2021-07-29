/**
 * Class Relation. Implements relations among variables. 
 *
 * @since 14/9/99
*/

import java.util.Vector;
import java.util.Enumeration;
import FiniteStates;
import Potential;
import NodeList;
import java.io.*;

public class Relation implements Cloneable {

   /**
    * A string that contains any comment about the relation
    */

   private String comment;

   /**
    * A string with the name of the relation
    */
   private String name;

   /**
    * Contains the potential of the relation
    */
   private Potential values;
   
   /**
    * Another potential, used for propagating in Join Trees.
    */
   
   private Potential otherValues;

   /**
    * Indicates if the relation is active or not
    */
   private boolean active;

   /**
    * Contains the kind of the relation
    */
   private String kind;

   /**
    * Contains the list of variables of the relation
    */
   private NodeList variables;


   /**
    * Creates a new empty relation. By default the relation
    * created cotains conditional probability
    */

   public Relation() {
      comment = new String();
      name = new String();
      active = true;
      kind = new String("conditional-prob");
      variables = new NodeList();
		
   }  


   /**
    * Creates a new relation of type conditional-prob with a
    * PotentialTable.
    * 
    * @param V a vector with the variables in the relation
    */

   public Relation(Vector V) { 

      double x;

      comment = new String();
      name = new String();
      active = true;
      kind = new String("conditional-prob");  
      variables = new NodeList();
      variables.nodes = (Vector) V.clone();
      x = 1/((double)(((FiniteStates)V.elementAt(0)).getNumStates()));
      values = new PotentialTable(V);
      ((PotentialTable)values).setValue(x);
   }



/* *************** Access Methods ************* */

   /**
    * This method is used to get the List of nodes contained variables
    * 
    * @return The list of the nodes of the relation
    */

   public NodeList getVariables() {
      return(variables);
   }  

   /**
    * Get the potential of the relation
    * 
    * @return The potential of the relation
    */  
  
   public Potential getValues() {  
      return(values);
   }  

   
   /**
    * Get the other potential of the relation
    * 
    * @return The other potential of the relation
    */  
  
   public Potential getOtherValues() {  
      return(otherValues);
   }
   
   
   /**
    * Gets the value of the variable active of the relation
    * 
    * @return The value of the active variable
    */   
   
   public boolean getActive() {
      return (active);
   }


   /**
    * Gets the kind of the relation
    * 
    * @return A string that contains the kind of the relation
    */
   
   public String getKind() {
      return (kind);
   }


   /**
    * Gets the name of the relation
    * 
    * @return A string that contaions the name of the relation
    */
   
   public String getName() {
      return (name);
   }



/* *************** Modifiers ************** */

   /**
    * Set the variables contained in the parameter Nodes according to
    * the order of this variables in the parameter Names
    * 
    * @param Names The list of names of the variables that are going to be set
    * @param Nodes The list of nodes that contained the variables that are going to be set
    */   

   public void setVariablesFromNames (Vector Names, NodeList Nodes) {
  
      int i;
      Node N;
 
      variables = new NodeList();
 
      for (i=0 ; i<Names.size() ; i++) {
         N = Nodes.getNode((String) Names.elementAt(i));
         variables.insertNode(N); 
      }    
   }


   /**
    * Sets the variables equal to the argument NodeList.
    * 
    * @param NL a NodeList
    */

   public void setVariables(NodeList NL) { 
      variables = NL;
   }

   /**
    * Sets the variables equal to a clone of the vector of nodes. Note that
    * the nodes are not cloned.
    * 
    * @param nodes a Vector of nodes (Node)
    */

   public void setVariables(Vector nodes) { 
      Node node;
      int i;

      variables = new NodeList();
      for(i=0;i<nodes.size();i++) {
        node=(Node)nodes.elementAt(i);
        variables.insertNode(node);
      }
   }

   /**
    * Set the values equal to the argument Potential
    * 
    * @param P A Potential
    */

   public void setValues(Potential P) {  
      values = P; 
   }

   
   /**
    * Set the other values equal to the argument Potential
    * 
    * @param p A Potential
    */

   public void setOtherValues(Potential p) {  
      otherValues = p;
   }
   
   
   /**
    * Set the value of the variable comment of the object Relation.
    * 
    * @param s A string that contains the comment
    */

   public void setComment(String s) { 
      comment = new String(s);
   }
    /**
    * Set the value of the name  of the object Relation.
    * 
    * @param s A string that contains the name of this relation
    */

    public void setName(String s){
        name = new String(s);
    }

   /**
    * This method is used to set the kind of the relation
    * 
    * @param S Contains the kind of this relation
    */

   public void setKind(String S) {
  
      if (S.equals("potential"))
         kind = new String("potential");
      else
         if (S.equals("conditional-prob"))
            kind = new String("conditional-prob");
   } 

   /** 
    * Set the field active to a new value.
    * @param act is the new value.
    */

   public void setActive(boolean act) {
      active=act;
   }


   /**
    * Saves the relation in the Printwriter p
    * 
    * @param p Where the relation is saved
    */

   public void save(PrintWriter p) {

      int i, j;

      p.print("relation " );
      i =  getVariables().size();

      for(j=0 ; j<i ; j++)
         p.print( ((Node)getVariables().elementAt(j)).getName() + " ");

      p.print("{ \n");

      if (!comment.equals(""))
         p.print("comment = \""+ comment+"\";\n");

      if (!name.equals(""))
         p.print("name-of-relation = "+ name+";\n"); 
 
      if (!kind.equals("conditional-prob"))
         p.print("kind = "+ kind+";\n");
 
      if (!active) 
         p.print("active=false;\n");
 
      if(!(values==null)) {
         values.save(p);
      }

      p.print("}\n\n");
      
   }

   
   /**
 p    * Prints the relation to the standard output.
    */

   public void print() {

      int i, j;

      System.out.print("relation " );
      i =  getVariables().size();

      for(j=0 ; j<i ; j++)
         System.out.print( ((Node)getVariables().elementAt(j)).getName() + " ");

      System.out.print("{ \n");

      if (!comment.equals(""))
         System.out.print("comment = \""+ comment+"\";\n");
 
      if (!name.equals(""))
         System.out.print("name-of-relation = "+ name+";\n"); 
  
      if (!kind.equals("conditional-prob"))
         System.out.print("kind = "+ kind+";\n");
 
      if (!active) 
         System.out.print("active=false;\n");
 
      if(!(values==null)) {
         if(values.getClass().getName().equals("PotentialTable"))
            ((PotentialTable) values).print();
	 else 
	   if(values.getClass().getName().equals("PotentialTree"))
	     ((PotentialTree) values).print();
	   else 
	     if(values.getClass().getName().equals("PotentialMTree"))
	       ((PotentialMTree) values).print();
             else if(values.getClass().getName().equals("PotentialConvexSet"))
               ((PotentialTable) values).print();
      }

      System.out.print("}\n\n");
      
   }
   
   
   /**
    * Copies a Relation.
    * 
    * @return a copy of this relation
    */

   public Relation copy() {
 
      Relation R;
  
      R = new Relation();
      R.comment = comment;
      R.name = name;
      R.values = values;
      R.variables = (NodeList) variables.copy();
      R.active = active;
      R.kind = kind;
  
      return R;
   }


   /**
    * This method tell us if the Node N is contained in the Relation
    * who receive this message
    *     
    * @param nodeToFind Node to find in the relation
    * @return True if the Node is contained in the relation, False in
    * other case
    */

   public boolean isInRelation (Node nodeToFind) {
      boolean isIn = false;
      int i = 0;  
      String S = new String();
    
      while ((i<this.variables.size()) && (!isIn)) {
         S= (this.variables.elementAt(i)).getName();
         if (S.equals(nodeToFind.getName()))
            isIn = true;
         else
            i++;
      }
    
      return isIn;        
   }
   
   
   /**
    * This function tell us if the Relation R is contained in the relation that
    * receives the message
    * 
    * @param R is the relation that we like to check
    * @return True if R is contained in the object and False in other case
    */
   
   public boolean isContained (Relation R) {
      boolean value = true;
      int i=0;
      
      while ((value) && (i<R.variables.size())) 
         if (!isInRelation(R.variables.elementAt(i))) 
            value = false;
         else
            i++;
      
      return value;
   }
   
   
   /**
    * This method tell if the Relation that receive the message has the same
    * variables that the relation R
    * 
    * @param R Relation to compare with the one that receive the message
    * @return True if the variables of the relations are the same
    */
   
   public boolean isTheSame (Relation R) {
      int i = 0;      
      
      if ((R.variables.size() == this.variables.size()) && (this.isContained(R)))
         return true;
      else
         return false;
         
   }

   /**
    * This method obtains the union of this relation
    * and the one given as argument. The result of the union is
    * stored in this relation.
    * @param r the relation to join with this.
    */
   	
   public void union(Relation r) {
     
      int j, q;
        
      for (j=0; j<r.variables.size(); j++) {
         q = variables.getId(r.variables.elementAt(j));
          
         if (q==-1)
            variables.insertNode(r.variables.elementAt(j));
      }      
            
   }



   /** Calculates the intersection between this relation and
   * the argument one.
   * @param r the relation to intersect with this.
   * @return the intersection.
   */
    
   public Relation intersection(Relation r) {

      Relation intersection = new Relation();
      int i=0, j, tamanol1, tamanol2;
      boolean salir;
          
      tamanol1 = variables.size();
      tamanol2 = r.variables.size();
          
      // The first while controls the nodes of the first relation

      while (i<tamanol1) {
         salir=false;
         j=0;
              
         /* Check if the actual node of the first list is in the second
            with the next while */
                 
         while ((!salir) && (j<tamanol2)){
            if (variables.elementAt(i).getName().equals(r.variables.elementAt(j).getName())) {
	      intersection.variables.insertNode(variables.elementAt(i));
	      salir=true;
            }
            j++;
         }
         i++;    
      }
      return intersection;
   }


   /**
   * Clone a Relation.
   * 
   * @return a clone of this relation
   */



   public Object clone(){

      Relation o = null;
      try{
         o = (Relation)super.clone();
         if(values != null)
     	      o.values = (Potential)values.clone();
         if(otherValues != null)
     	      o.otherValues = (Potential)otherValues.clone();
         if(variables != null)	
     	      o.variables = (NodeList)variables.clone();
      }catch (CloneNotSupportedException e){
         System.out.println("Can't clone Relation");
      }
      return o;

   }


 /**
 * restrict the relation to the variables of the relation belonging to set
 * NOTE: otherValues is set to NULL
 * @param set a nodelist of variables
 */

public void restrictToVariables(NodeList set) {

  NodeList nl;
 
  nl = variables.intersection(set);
  if (nl.size() == 0) {
    variables = new NodeList();
    values = new PotentialTable();
    otherValues = new PotentialTable();    
  }
  else{
    if (values != null) 
      values = ((PotentialTable)values).marginalizePotential(nl.toVector());
    otherValues = null;
    variables = nl;
  }
}
  	
}  // end of Relation class

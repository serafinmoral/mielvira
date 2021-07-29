/* RelationList.java */

import java.util.Vector;
import java.util.Enumeration;
import Relation;


/**
 * Class RelationList. A list of Relations.
 * @see Relation
 * Last modified : 20/04/99
 */

public class RelationList {

  /**
   * Contains the list of relations
   */ 
  private Vector relations;
  
  
  /**
   * Creates an empty RelationList.
   */
  
  public RelationList() {    
    relations = new Vector();
  }
  
    
  public Vector getRelations() {
     return relations;
  }
  
  
  public void setRelations (Vector V){
     relations = V;
  }
  
  /**
   * Inserts a Relation at the end of the list.
   * @param rel the relation to insert.
   */
  
  public void insertRelation(Relation rel) {
    
    relations.addElement(rel);
  }

  
  /**
   * Inserts a Relation at the position pos
   * @param rel the relation to insert.
   * @param pos the position where the relation have to be inserted
   */
  
  public void setElementAt(Relation rel, int pos) {
    
    relations.setElementAt(rel,pos);
  }

  
  /**
   * Removes a relation from the list.
   * @param rel the relation to be removed.
   */
  
  public void removeRelation(Relation rel) {
   
    relations.removeElement(rel);
  }
  
  
  /**
   * Removes a relation in a given position.
   * @param p the position of the relation to remove.
   */
  
  public void removeRelationAt(int p) {
   
    relations.removeElementAt(p);
  }
  
  
  /**
   * @param rel a relation.
   * @return the position of rel in the list.
   * -1 if rel is not in it.
   */
  
  public int indexOf(Relation rel) {
   
    return relations.indexOf(rel);
  }
  
  
  /**
   * @return the number of relations in the list.
   */	 
  
  public int size() {

    return relations.size();
  }
  
  /**
   * @param p a position in the list.
   * @return the Relation at position p.
   */
  
  public Relation elementAt(int p) {
   
    return ((Relation)relations.elementAt(p));
  }

  
  /**
   * @return an enumeration of the relations in the list.
   */
  
  public Enumeration elements() {
    
    return ((Enumeration)relations.elements());
  }
  
  
  /**
   * @returns the size of the potential resulting from the
              combination of all the relations in the list,
	      considering fully expanded potentials and
	      FiniteStates variables.
  */
  
  public double totalSize() {
   
    NodeList list;
    int i;

    list = (NodeList) elementAt(0).getVariables().copy();
    for (i=1 ; i<size() ; i++)
      list.merge(elementAt(i).getVariables());
    
    return (FiniteStates.getSize(list));
  }

  
  /**
   * @param var a FiniteStates variable.
   * @returns the size of the potential resulting from the
              combination of all the relations in the list
	      that contain the argument variable,
	      considering fully expanded potentials and
	      FiniteStates variables.
  */
  
  public double totalSize(FiniteStates var) {
   
    RelationList list;
    
    list = getRelationsOf(var);
    
    return list.totalSize();
  }

  
  /**
   * @param var a FiniteStates variable.
   * @returns a new RelationList containing only the relations
   * in this list that contain the argument variable.
   */
  
  public RelationList getRelationsOf(FiniteStates var) {
   
    int i, p;
    Relation rel;
    RelationList list;
    
    list = new RelationList();
    
    for (i=0 ; i<size() ; i++) {
      rel = elementAt(i);
      p = rel.getVariables().getId(var);
      if (p!=-1)
	list.insertRelation(rel);
    }
    
    return list;
  }
  
  
  /**
   * Obtain the relations that contain a given variable and
   * remove them from the list.
   * @param var a FiniteStates variable.
   * @returns a new RelationList containing only the relations
   * in this list that contain the argument variable.
   */
  
  public RelationList getRelationsOfAndRemove(FiniteStates var) {
   
    int i, p;
    Relation rel;
    RelationList list;
    
    list = new RelationList();
    
    for (i=size()-1 ; i>=0 ; i--) {
      rel = elementAt(i);
      p = rel.getVariables().getId(var);
      if (p!=-1) {
	list.insertRelation(rel);
	removeRelationAt(i);
      }
    }
    
    return list;
  }
  
  
  
  
  /**
   * @param var a FiniteStates variable to search for.
   * @return the number of relations where the variable is.
   * -1 if the variable is not in any relation.
  */
  
  public int isInOnlyOne(FiniteStates var) {
    
    int i, p = -2, nr = 0;
    Relation rel;
    
    for (i=0 ; i<size() ; i++) {
      rel = elementAt(i);
      p = rel.getVariables().getId(var);
      if (p!=-1)
	nr++;
    }
   
    if (nr>1)
      return (-1);
    
    return nr;
  }
  
  
  /**
   * @param var a FiniteStates variables.
   * @return true if the variable is in at least one of the
   * relations.
   */
  
  public boolean isIn(FiniteStates var) {
    
    int i, p = -2;
    Relation rel;
    
    for (i=0 ; i<size() ; i++) {
      rel = elementAt(i);
      p = rel.getVariables().getId(var);
      if (p!=-1)
	return true;
    }
    
    return false;
  }
  
  
  
  /**
   * @param list a list of candidate nodes to be removed.
   * @return the position in the argument list of the
   * node producing the minimum size when being removed.
   */
  
  public int nextToRemove(NodeList list) {
   
    int i, pos;
    double s, min = 90.0E20;
    FiniteStates var;
    
    pos = list.size()-1;
    
    for (i=0 ; i<list.size() ; i++) {   
       
      var = (FiniteStates)list.elementAt(i);
      
      if (isInOnlyOne(var)==1)
	return i;
      
      s = totalSize(var);
      
      if (s<min) {
	min = s;
	pos = i;	
      }
    }
    
    return pos;
  }
  
  
  /**
   * Copies a RelationList.
   * @return a copy of this RelationList.
   */
  
  public RelationList copy() {
  
    RelationList list;
    Relation rel;
    int i;
    
    list = new RelationList();
    
    for (i=0 ; i<relations.size() ; i++) {
      rel = (Relation)relations.elementAt(i);
      list.insertRelation(rel.copy());
    }
    
    return list;
  }

  
  /**
   * Used to know if the relation R is contained in a
   * RelationList object
   */
  
   public boolean contains (Relation R) {
      int i = 0;
      boolean find = false;
      
      while ((i<this.size()) && (!find)) {
         if (R.isTheSame(this.elementAt(i)))
            find = true;
         else
            i++;
      }
      
      return find;
      
   }

   public void print() {
      int i;

      for(i=0;i<size();i++) {
         elementAt(i).print();
      }
   }	
   
   /** Restrict every relation to the set of evidences. Each observed
     * variable will be remove from the list of variables of the potential
     * and from the list of variables of the relation. The new potential will
     * contain only the values that are compatible with the evidence.
     * If one observed variable is the first in the list of variables of the
     * relation, then the new relation will be inactive (active = false). Othercase
     * active will be equal than before.
     * @param ev is the given evidence
     */
   public void restrictToObservations(Evidence ev) {
      Relation r;
      int i, s;
      NodeList nodesEv,newNodeList;
      
      
      if(ev.size()>0){
	s = size();
	nodesEv=new NodeList(ev.getVariables());
	for (i=0 ; i<s ; i++) {
	  r = elementAt(i);
	  if(r.getValues()!=null) {
	    r.setValues (r.getValues().restrictVariable((Configuration)ev));
	  //r.getVariables().nodes = r.getValues().getVariables();
	  }
	  if(ev.isObserved(r.getVariables().elementAt(0)))
	    r.setActive(false);
	  newNodeList=r.getVariables().difference(nodesEv);
	  r.setVariables(newNodeList);
	}
      }
   }


    /** 
     * Find a relation in this RelationList
     * @param nameOfRelation the name of relation that we are looking for
     * @return the Relation (if this Relation does not exists in this 
     *         RelationList, returns null)
     */

    public Relation getRelation(String nameOfRelation){
	int i,s;
	boolean enc=false;
	Relation r=null;

	s = size();
	for (i=0;(i<s)&&(!enc);i++){
	    r = elementAt(i);
	    if (r.getName().equals(nameOfRelation))
		enc=true;
	}
        if (!enc) return null;
	else return r;
    }
	    

    /** Actualize the set of PotentialFunctions in the relationList
      * by assigning each argument in the potential given by a string
      * to the related Potential.
      */
    public void repairPotFunctions(){
	int i,s,j;
	Relation r,raux;
	PotentialFunction f,faux=new PotentialFunction();

	s = size();
	for (i=0;i<s;i++){
	  r = elementAt(i);
	  if (r.getValues().getClass()==faux.getClass()){
	    f = (PotentialFunction) r.getValues();
	    for (j=0;j<f.strArg.size();j++){
		if (f.arguments.elementAt(j)==null){
		   raux = getRelation((String) f.strArg.elementAt(j));
	           f.setArgumentAt(raux.getValues(),j);
		}
	    }
	  }
	}
    }      
	    
	
}



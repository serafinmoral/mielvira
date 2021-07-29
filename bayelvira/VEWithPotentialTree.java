/* _________________________________________________________________________

			      VEWithPotentialTree

			     Elvira Project

   File: VEWithPotentialTree.java
   Path: /home/gte/acu/Desarrollo/bayelvira/VEWithPotentialTree.java
   Description: 
   Created: Mon Oct  4 18:40:55 CEST 1999
   Author: Andres Cano,,244258,,
   Modified: Mon Oct  4 19:28:48 CEST 1999
   Last maintained by: Andres Cano,,244258,,

   RCS $Revision: 1.2 $ $State: Exp $
   

   _________________________________________________________________________

   Note: 

   ________________________________________________________________________ */


import java.io.*;

/**
 * Class VEWithPotentialTree.
 * Implements the variable elimination method of propagation using
 * PotentialTrees. If the initial potentials are not PotentialTrees
 * then they are converted to PotentialTrees.
 */

public class VEWithPotentialTree extends VariableElimination {
  
  static final double limitForPrunning = 0.0001;

  
  /**
  * Program for performing experiments.
  * The arguments are as follows.
  * 1. Input file: the network.
  * 2. Output file.
  * 3. Evidence file.
  * If the evidence file is omitted, then no evidences are
  * considered
  */
public static void main(String args[]) throws ParseException, IOException {
   Bnet b;
   Evidence e;
   FileInputStream networkFile, evidenceFile;
   VEWithPotentialTree ve;
   int i;
   
   if (args.length<2)
     System.out.println("Too few arguments. Arguments are: ElviraFile OutputFile EvidenceFile");
   else {
     networkFile = new FileInputStream(args[0]);
     b = new Bnet(networkFile);
     
     if (args.length==3) {
       evidenceFile= new FileInputStream(args[2]);
       e = new Evidence(evidenceFile,b.getNodeList());
     }
     else
       e = new Evidence();
     
     ve = new VEWithPotentialTree(b,e);
     ve.obtainInterest();
     ve.propagate(args[1]);
   }
}
							       
VEWithPotentialTree(Bnet b, Evidence e){
  super(b,e);
  System.out.println("Llamado constructor VEWithPotentialTree");
}

/* Transform one of the original relations in a PotentialTree.
   @ param r is the relation to be transformed
*/

public Relation transformInitialRelation(Relation r) {    
  Relation rNew;
  System.out.println("Llamado metodo transformInitialRelation de VEWithPotentialTree");
  rNew = new Relation();
  rNew.setVariables(r.getVariables().copy());
  if (r.getValues().getClass().getName().equals("PotentialTable"))
    rNew.setValues(((PotentialTable)r.getValues()).toTree());
  else
    rNew.setValues(r.getValues());
  return rNew;
}

/* Transform  the PotentialTree obtained as a result of adding
 * over one variable (FiniteStates)
 * @param pot is the PotentialTree
 * @param rlist is the current RelationList
*/ 

public Potential transformAfterAdding(Potential potential){
  PotentialTree pot;
  int k,pos;
  FiniteStates Y;
  
  pot=(PotentialTree)potential;
  System.out.println("Llamado metodo transformAfterAdding de VEWithPotentialTree");  
  pot.limitBound(limitForPrunning);
  pot = pot.sortAndBound(10000);
  for (k=pot.getVariables().size()-1 ; k>=0 ; k--) {
    Y = (FiniteStates)pot.getVariables().elementAt(k);
    if (!pot.getTree().isIn(Y)) {
      if (CurrentRelations.isIn(Y)) {
	pos = pot.getVariables().indexOf(Y);
	pot.getVariables().removeElementAt(pos);
      }
    }
  }
  return pot;
}
  
/* Transform the PotentialTree obtained as a result of eliminating
 * one variable (FiniteStates). 
 * @param rlist is the current RelationList
*/ 

public Potential transformAfterEliminating(Potential potential){
  PotentialTree pot;
  
  System.out.println("Llamado metodo transformAfterEliminating de VEWithPotentialTree");
  pot=(PotentialTree)potential;    
  pot.limitBound(limitForPrunning);
  return pot;
}

}


import java.util.Vector;
import java.util.Hashtable;
import java.util.Random;
import java.util.Date;
import java.io.*;
import NodeList;
import Relation;
import RelationList;
import PairTable;
import DSeparation;


/** 
 * VariableElimination implements a generic variable elimination method of propagation.
 * The initial potentials can be of any kind, but
 * they must define the methods:
 * <ul>
 * <li> <code>Potential combine(Pontential combine)</code>
 * <li> <code>Potential addVariable(FiniteStates var)</code>
 * </ul>
 * This class can be extended for special requirements. The methods 
 * that can be overloaded are:
 * <ul>
 * <li> <code><a href="#transformInitialRelation(Relation)">Relation transformInitialRelation(Relation r)</a></code>
 * <li> <code><a href="#transformAfterAdding(Potential)">Potential transformAfterAdding(Potential pot)</a></code>
 * <li> <code><a href="#transformAfterEliminating(Potential)">Potential transformAfterEliminating(Potential pot)</a></code>
 * <li> <code><a href="#combine(Potential, Potential)">Potential combine(Potential pot1,Potential pot2)</a></code>
 * <li> <code><a href="#addVariable(Potential, FiniteStates)">Potential addVariable(Potential pot,FiniteStates var)</a></code>
 * </ul>
 * @author Antonio Salmerón
 * @author Andrés Cano
 * @see VEWithPotentialTree
 */

public class VariableElimination extends Propagation {

/**
  * Max number real numbers to represent each Potential
  */
int limitSize;
//Vector posteriorDistributions;
//RelationList initialRelations;
RelationList CurrentRelations;

VariableElimination(Bnet b, Evidence e) {

  observations = e;
  network = b;
}

/**
  * Program for performing experiments.
  * The arguments are as follows.
  * <ol>
  * <li> Input file: the network.
  * <li> Output file.
  * <li> Evidence file.
  * </ol>
  * If the evidence file is omitted, then no evidences are
  * considered
*/
public static void main(String args[]) throws ParseException, IOException {
   Bnet b;
   Evidence e;
   FileInputStream networkFile, evidenceFile;
   VariableElimination ve;
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
     
     ve = new VariableElimination(b,e);
     ve.obtainInterest();     
     ve.propagate(args[1]);
   }
}
	
/**
 * Transforms one of the original relations in a Potential. In this
 * case no transformation is carried out. This method can be 
 * overloaded for special requirements.
 * @ param r the relation to be transformed
 */

public Relation transformInitialRelation(Relation r){
  return r;
}

/** 
  * Combines two Potentials. This method can be overloaded in subclases
  * for special requirements. 
  * @param pot1 the first potential
  * @param pot2 the second potential
  */

public Potential combine(Potential pot1,Potential pot2) {
  Potential potaux;
  
  potaux=pot1.combine(pot2);
  return potaux;      
}

/**
 * Removes the argument variable summing over all its values. This
 * method can be overloaded in subclases for special requirements
 * @param pot a potential
 * @param var a FiniteStates variable.
 * @return a new Potential with the result of the deletion.
 */

public Potential addVariable(Potential pot,FiniteStates var) {
  return pot.addVariable(var);  
}

/**
 * Transforms the Potential obtained as a result of adding
 * over one variable (FiniteStates). This
 * method can be overloaded in subclases for special requirements
 * @param rlist the current RelationList
 */ 

public Potential transformAfterAdding(Potential pot){
  return pot;
}

/**
 * Transforms the Potential obtained as a result of eliminating
 * one variable (FiniteStates). This
 * method can be overloaded in subclases for special requirements
 * @param pot the PotentialTree
 * @param rlist the current RelationList
*/ 

public Potential transformAfterEliminating(Potential pot){
  return pot;
}

/**
 * Sets the maximum size of a potential.
 * @param n the size.
 */

public void setLimitSize(int n) {
  
  limitSize = n;
}

/**
 * Returns all the initial relations present
 * in the network.
 */

public RelationList getInitialRelations() {
  
  Relation r, rNew;
  RelationList ir;
  int i;
 
  ir = new RelationList();
  
  for (i=0 ; i<network.getRelationList().size() ; i++) {
    r = (Relation)((Relation)(network.getRelationList().elementAt(i))).copy();
    rNew=transformInitialRelation(r);
    ir.insertRelation(rNew);
  }
  
  return ir;
}

/**
 * Returns the initial relations present
 * in the network that can affect the posterior distribution of node
 * 
 */

public RelationList getInitialRelations(Node node) {
  Relation r, rNew;
  RelationList ir;
  int i;
  DSeparation dsep;
  Vector varAffecting;

  ir = new RelationList();
  dsep=new DSeparation(network,observations);
  varAffecting=dsep.allAffecting(node);
  for (i=0;i<varAffecting.size();i++) {
    //r=network.getRelation((Node)varAffecting.elementAt(i));
    r=(Relation)network.getRelation((Node)varAffecting.elementAt(i)).copy();
    rNew=transformInitialRelation(r);
    ir.insertRelation(rNew);
  }
  return ir;
}

/**
 * Computes the posterior distributions.
 * There will be a posterior distribution for each
 * variable of interest.
 * Posterior distributions are stored in PosteriorDistribution.
 * Note that observed variables are not included in the deletion
 * sequence.
 */

public void getPosteriorDistributions() {

  FiniteStates x;
  int i, s;

  //posteriorDistributions = new Vector();
  
  s=interest.size();
  for(i=0;i<s;i++) {
    x=(FiniteStates)interest.elementAt(i);
    //if (observations.indexOf(x) == -1) {
    if (!observations.isObserved(x)) {
      getPosteriorDistributionOf(x);
    }    
  }  
}

/**
 * Gets the posterior distribution of variable V and
 * stores it in results.
 * @param V a FiniteStates.
 */

public void getPosteriorDistributionOf(FiniteStates V) {
 
  NodeList NotRemoved;
  FiniteStates X, Y;
  Relation R;
  RelationList RLtemp;
  //PotentialTree Pot;
  Potential Pot;
  PairTable P;
  int i, j, k, p, pos, s;

  NotRemoved = new NodeList();
  P = new PairTable();
  
  s = network.getNodeList().size();
  for (i=0 ; i<s ; i++) {
    X = (FiniteStates)network.getNodeList().elementAt(i);
    //if ((observations.indexOf(X) == -1) && (X!=V)) {
    if ((!observations.isObserved(X)) && (X!=V)) {
      NotRemoved.insertNode(X);
      P.addElement(X);
    }
  }

  
  //CurrentRelations = getInitialRelations();
  CurrentRelations = getInitialRelations(V);

     
  /* Now restrict the valuations to the obervations */

  if (observations.size() > 0)
    CurrentRelations.restrictToObservations(observations);
    //restrictToObservations(CurrentRelations);
  
  for (i=0 ; i<CurrentRelations.size() ; i++)
    P.addRelation(CurrentRelations.elementAt(i));

  
  for (i=NotRemoved.size() ; i>0 ; i--) {
    X = P.nextToRemove();
    NotRemoved.removeNode(X);
    P.removeVariable(X);
    RLtemp = CurrentRelations.getRelationsOfAndRemove(X);
    
    if(RLtemp.size()>0) {
      R = RLtemp.elementAt(0);
      P.removeRelation(R);
      Pot=R.getValues();
    
    for (j=1 ; j<RLtemp.size() ; j++) {
      R = RLtemp.elementAt(j);
      P.removeRelation(R);
      //Pot = Pot.combine((PotentialTree)R.getValues());
      Pot=combine(Pot,R.getValues());
    }
    
    Pot=addVariable(Pot,X);
    Pot=transformAfterAdding(Pot);
    
    //System.out.println("He borrado "+X.Name);
    R = new Relation();
    R.setKind("potential");
    R.getVariables().nodes = (Vector)Pot.getVariables().clone();
    R.setValues(Pot);
    CurrentRelations.insertRelation(R);
    P.addRelation(R);
    }
  }
  
  /* After this, CurrentRelations must only contain relations
     for variable V */
  
  RLtemp = CurrentRelations.getRelationsOf(V);
    
  R = RLtemp.elementAt(0);
  P.removeRelation(R);
  //Pot = (PotentialTree)R.getValues();
  Pot = R.getValues();

  for (j=1 ; j<RLtemp.size() ; j++) {
    R = RLtemp.elementAt(j);
    P.removeRelation(R);
    //Pot = Pot.combine((PotentialTree)R.getValues());
    Pot=combine(Pot,R.getValues());
  }
  //Pot.limitBound(limitForPrunning);
  Pot=transformAfterEliminating(Pot);
 
  results.addElement(Pot);
}

/**
 * Restricts a list of relations to the observations.
 * @param RL the relation list to restrict.
 */

public void restrictToObservations(RelationList RL) {

  Relation R;
  int i, s;
  
  s = RL.size();
  
  for (i=0 ; i<s ; i++) {
    R = RL.elementAt(i);
    //R.setValues(((PotentialTree)R.getValues()).restrictVariable(observations));
    R.setValues((R.getValues()).restrictVariable(observations));
    R.getVariables().nodes = R.getValues().getVariables();
  }

}

/**
 * Carries out a propagation.
 *
 * @param OutputFile the file where the exact results will be
 *                   stored.
 */

public void propagate(String OutputFile) throws ParseException, IOException {

  Date D;
  double time;
  
  D = new Date();
  time = (double)D.getTime();
  getPosteriorDistributions();
  D = new Date();
  time = ((double)D.getTime() - time) / 1000;
  System.out.println("Time (secs): "+time);
  
  normalizeResults();
  saveResults(OutputFile);
}

/**
 * Saves the result of a propagation to a file.
 * @param Name a String containing the file name.
 */

public void saveResults(String S) throws IOException {

  FileWriter F;
  PrintWriter P;
  Potential Pot;
  PotentialTable Pot2;
  int i;

  F=new FileWriter(S);
  
  P=new PrintWriter(F);
  
  for (i=0 ; i<results.size() ; i++) {
    Pot = (Potential)results.elementAt(i);
    Pot.saveResult(P);
  }
  
  F.close();
}

}

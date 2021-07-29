import java.util.Vector;
import java.util.Hashtable;
import java.io.*;
import ProbabilityTree;
import MultipleTree;
import Potential;
import Configuration;
import NodeQueueM;
import PriorityQueueM;

/**
 * Implementation of class PotentialMTree. A potential represented
 * by a probability tree of class MultipleTree.
 *
 * @since 25/06/99
*/

public class PotentialMTree extends Potential {

MultipleTree values;
long size;
/** <code>true</code> if <code>values</code> represents an exact potential.
  * All constructor set <code>isExact</code> to <code>false</code>.
  */
private boolean isExact;


/**
 * Creates a new PotentialMTree with no variables and
 * an empty tree.
*/
    
public PotentialMTree() {

  variables = new Vector();
  values = new MultipleTree();
  size=0;
  isExact=false;
}


/**
 * Creates a PotentialMTree from a PotentialTree.
 * @param pot a PotentialTree.
 */

public PotentialMTree(PotentialTree pot) {
 
  variables = (Vector)pot.getVariables().clone();
  size = pot.getSize();
  
  values = new MultipleTree(pot.getTree());
  isExact = false;
}


/**
 * Creates a new PotentialMTree with an empty tree.
 * @param vars variables that the potential will contain.
*/
	 
public PotentialMTree(Vector vars) {

  variables = (Vector)vars.clone();
  values = new MultipleTree();
  size=0;
  isExact=false;
}


/**
 * Creates a new PotentialMTree with an empty tree.
 * @param vars variables that the potential will contain,
 * given as a NodeList.
*/
	 
public PotentialMTree(NodeList vars) {

  variables = (Vector)vars.nodes.clone();
  values = new MultipleTree();
  size=0;
  isExact=false;
}

/**
 * Set the PotentialMTree as exact or not
 * @param Exact true if the PotentialMTree will be exact
 */

public void setExact(boolean Exact){
  isExact=Exact;
}


/**
 * Tells if the PotentialMTree is exact or not
 * @return true if the PotentialMTree is exact
 */

public boolean getExact(){
  //return false;
  return isExact;
}

/**
 * Assigns a tree to the potential.
 * @param tree the MultipleTree to be assigned.
*/

public void setTree(MultipleTree tree) {

  values = tree;
  size = tree.getSize();
}


/**
 * @return the MultipleTree associated with the potential.
*/

public MultipleTree getTree(){
  
  return values;
}



/**
 * @return the number of values (size) of the potential.
*/

public long getSize() {

  return size;
}


/**
 * Gets the value for a onfiguration.
 * @param conf a Configuration.
 * @return the value corresponding to configuration conf.
*/

public double getValue(Configuration conf) {

  return values.getProb(conf);
}


/**
 * Gets the value for a configuration. In this case, the
 * configuration is represented by means of an array of int.
 * At each position, the value for certain variable is stored.
 * To know the position in the array corresponding to a given
 * variable, we use a hash table. In that hash table, the
 * position of every variable in the array is stored.
 *
 * @param positions a Hash table.
 * @param conf an array of int.
 * @return the value corresponding to configuration conf.
 */

public double getValue(Hashtable positions, int[] conf) {
 
  return values.getProb(positions,conf);
}


/**
 * Sets a value for a configuration.
 * @param x a double value.
 * @param conf a Configuration.
*/

public void setValue(Configuration conf, double x) {

  Configuration aux;
  MultipleTree tree;
  FiniteStates var;
  int i, p, val, s;
  boolean update;
  

  update=true;

  aux = conf.copy();
  s = conf.variables.size();

  tree = values;

  for (i=0 ; i<s ; i++) {

    if (tree.getLabel() != 1) {
      var = aux.getVariable(0);
      val = aux.getValue(0);
      aux.remove(0);
      if (tree.getLabel()==2) // if the node is a probability,
	update=false; // do not update the number of leaves.
      tree.assignVar(var);
    }
    else {
      p = aux.indexOf(tree.getVar());
      var = aux.getVariable(p);
      val = aux.getValue(p);
      aux.remove(p);
    }

    tree = (MultipleTree)tree.getChild(val);
  }

  tree.assignProb(x);
  if (update)
    size++;
}


/**
 * @return the sum of all the values in the potential.
*/

public double totalPotential() {
 
  long s;
  
  s = (long)FiniteStates.getSize(variables);
  
  return values.conditionalSum(s);
}


/**
 * @param conf a Configuration.
 * @return the sum of all the values in the potential
   matching with configuration C. The result is the same
   as restricting the potential to C and then using
   totalPotential()
 * @see totalPotencial()
*/

public double totalPotential(Configuration conf) {

  Configuration auxConf;
  FiniteStates temp;
  int i, nv;
  double sum;

  nv = 1;
  for (i=0 ; i<variables.size() ; i++) {
    temp = (FiniteStates)variables.elementAt(i);
    nv = nv * temp.getNumStates();
  }

  // Evaluate the tree for all the possible configurations
  // and sum the values.
  
  auxConf = new Configuration(variables,conf);
  sum = 0.0;
  for (i=0 ; i<nv ; i++) {
    sum += getValue(auxConf);
    auxConf.nextConfiguration(conf);
  }

  return sum;
}


/**
 * @return the entropy of the potential.
*/

public double entropyPotential() {

  Configuration auxConf;
  FiniteStates temp;
  int i, nv;
  double sum, x;

  nv = 1;
  for (i=0 ; i<variables.size() ; i++) {
    temp=(FiniteStates)variables.elementAt(i);
    nv = nv * temp.getNumStates();
  }


  // Evaluate the tree for all the configurations and
  // compute the entropy.
  
  auxConf = new Configuration(variables);
  sum = 0.0;
  
  for (i=0 ; i<nv ; i++) {
    x = getValue(auxConf);
    if (x>0.0)
      sum += x * Math.log(x);
    auxConf.nextConfiguration();
  }

  return ((-1.0)*sum);
}




/**
 * @param conf a Configuration.
 * @return the entropy of the values of the potential
   matching with configuration conf. The result is the
   same as restricting first to conf and then using
   entropyPotential().
 * @see entropyPotential()
*/

public double entropyPotential(Configuration conf) {

  Configuration auxConf;
  FiniteStates temp;
  int i, nv;
  double sum, x;

  nv = 1;
  for (i=0; i<variables.size(); i++) {
    temp=(FiniteStates)variables.elementAt(i);
    nv = nv * temp.getNumStates();
  }

  // Evaluate the tree for all the configurations and
  // compute the entropy.

  auxConf=new Configuration(variables,conf);
  sum=0.0;
  
  for (i=0 ; i<nv ; i++) {
    x = getValue(auxConf);
    if (x>0.0)
      sum += x * Math.log(x);
    auxConf.nextConfiguration(conf);
  }

  return ((-1.0)*sum);
}


/**
 * Restricts the potential to a given configuration.
 * @param conf restricting configuration.
 * @return Returns a new PotentialMTree where variables
 * in conf have been instantiated to their values in conf.
*/

//public PotentialMTree restrictVariable(Configuration conf) {
public Potential restrictVariable(Configuration conf) {
  
  Vector aux;
  FiniteStates temp;
  PotentialMTree pot;
  MultipleTree tree;
  int i, p, s, v;

  s = variables.size();
  aux = new Vector(s); // New list of variables.
  tree = getTree(); // tree will be the new tree
  
  for (i=0 ; i<s ; i++) {
    temp = (FiniteStates)variables.elementAt(i);
    p = conf.indexOf(temp);
    
    if (p==-1) // If it is not in conf, add to the new list.
      aux.addElement(temp);
    else {     // Otherwise, restrict the tree to it.
      v = conf.getValue(p);
      tree = tree.restrict(temp,v);
    }
  }

  pot = new PotentialMTree(aux);
  pot.setTree(tree);
  
  return pot;
}


/**
 * Combines this protential with the argument.
 * @param p a PotentialMTree.
 * @returns a new PotentialMTree consisting of the combination
 * of p and this PotentialMTree.
*/

//public PotentialMTree combine(PotentialMTree p) {
public Potential combine(Potential pMTree) {
 
  Vector v, v1, v2;
  FiniteStates aux;
  int i, nv;
  PotentialMTree pot,p;
  double x;
  MultipleTree tree, tree1, tree2;

  p=(PotentialMTree)pMTree;
  
  v1 = variables; // Variables of this potential.
  v2 = p.variables; // Variables of the argument.
  v = new Vector(); // Variables of the new potential.

  for (i=0 ; i<v1.size() ; i++) {
    aux=(FiniteStates)v1.elementAt(i);
    v.addElement(aux);
  }

  for (i=0 ; i<v2.size() ; i++) {
    aux=(FiniteStates)v2.elementAt(i);
    if (aux.indexOf(v1)==-1)
      v.addElement(aux);
  }

  // The new Potential.
  pot=new PotentialMTree(v);
  
  tree1=getTree(); // Tree of this potential.
  tree2=p.getTree(); // Tree of the argument.
  
  tree = MultipleTree.combine(tree1,tree2); // The new tree.
  
  pot.setTree(tree);
  
  return pot;
}

public PotentialMTree combine(PotentialMTree p) {
  return (PotentialMTree)combine((Potential)p);
}


/**
 * Removes a list of variables by adding over all their states.
 * @param vars vector of FiniteStates.
 * @return A new PotentialMTree with the result of the operation.
*/

public PotentialMTree addVariable(Vector vars) {

  Vector aux;
  FiniteStates var1, var2;
  int i, j;
  boolean found;
  PotentialMTree pot;
  MultipleTree tree;

  aux=new Vector(); // New list of variables.
  for (i=0 ; i<variables.size() ; i++) {
    var1 = (FiniteStates)variables.elementAt(i);
    found = false;
    
    for (j=0 ; j<vars.size() ; j++) {
      var2 = (FiniteStates)vars.elementAt(j);
      if (var1==var2) {
	found = true;
	break;
      }
    }
    
    if (!found)
      aux.addElement(var1);
    }

  pot = new PotentialMTree(aux); // The new tree.

  tree = values;
  
  for (i=0 ; i<vars.size() ; i++) {
    var1 = (FiniteStates)vars.elementAt(i);
    tree = tree.multiAddVariable(var1);
  }

  pot.setTree(tree);
  
  return pot;
}


/**
 * Removes the argument variable summing over all its values.
 * @param var a FiniteStates variable.
 * @return a new PotentialMTree with the result of the deletion.
 */

//public PotentialMTree addVariable(FiniteStates var) {
public Potential addVariable(FiniteStates var) {

  Vector v;
  PotentialMTree pot;
  
  v = new Vector();
  v.addElement(var);
  
  pot = addVariable(v);
  
  return pot;
}


/**
 * Marginalizes a PotentialMTree to a list of variables.
 * It is equivalent to remove the other variables.
 * @param vars a vector of FiniteStates variables.
 * @return a new PotentialMTree with the marginal.
 * @see addVariable(Vector vars)
*/

public Potential marginalizePotential(Vector vars) {

  Vector v;
  int i, j;
  boolean found;
  FiniteStates var1, var2;
  PotentialMTree pot;

  v = new Vector(); // List of variables to remove
                    // (those not in vars).
  for (i=0 ; i<variables.size() ; i++) {
    var1 = (FiniteStates)variables.elementAt(i);
    found = false;
    for (j=0 ; j<vars.size(); j++) {
      var2 = (FiniteStates)vars.elementAt(j);
      if (var1 == var2) {
	found = true;
	break;
      }
    }
      
    if (!found)
      v.addElement(var1);
  }

  pot = addVariable(v);

  return pot;
}


/**
 * Updates the actual size of the potential.
 */

public void updateSize() {

  values.updateSize();
  size = values.getSize();
}


/**
 * @return a copy of this PotentialMTree.
 */

public PotentialMTree copy() {
 
  PotentialMTree pot;
  
  pot = new PotentialMTree(variables);
  pot.size = size;
  pot.values = values.copy();
  
  return pot;
}


/**
 * Normalizes this potential to sum up to one.
 */

public void normalize() {

  long totalSize;
  
  totalSize = (long)FiniteStates.getSize(variables);
  values.normalize(totalSize);
}


/**
 * Computes a new PotentialMTree from this conditional on another
 * potential.
 * @param condPot the conditioning potential.
 * @return a new PotentialMTree where the tree will be of class
 * MultipleTree, with the first value as in this and the second value
 * equal to the conditional on the argument Potential.
 */

public PotentialMTree conditional(PotentialMTree condPot) {
 
  PotentialMTree pot;
  MultipleTree condTree;
  long totalSize;
  Configuration conf;

  conf = new Configuration();
  totalSize = (long)FiniteStates.getSize(condPot.getVariables());
  condTree = condPot.getTree();
  
  pot = new PotentialMTree(variables);
  pot.size = size;
  pot.values=((MultipleTree)values).conditional(condTree,totalSize,conf);
  
  return pot; 
}


/**
 * Sorts the variables in the tree and limits the number of leaves.
 * Sets <code>isExact</code> to false if the method carry out an
 * approximation in the <code>PotentialMTree</code>.
 * This version is for trees of class MultipleTree.
 * @param maxLeaves maximum number of leaves in the new tree.
 * @param method the method of prunning: 0 for conditional
 * prunning or 1 for max-min prunning.
 * @return a new PotentialMTree sorted and bounded.
 */

public PotentialMTree conditionalSortAndBound(int maxLeaves,
					      int method) {
 
  PotentialMTree pot;
  MultipleTree treeNew, treeSource, treeSource2, treeResult;
  NodeQueueM nodeQ;
  PriorityQueueM queue;
  FiniteStates var;
  int j, nv;
  long newSize, maxSize;
  boolean isAnApproxTree=false;
  double normalization;
   
  // Size of the entire tree (expanded)
  maxSize = (long)FiniteStates.getSize(variables);


   
  nodeQ = new NodeQueueM(1E20); // Infinity node.
  
  // Priority queue where the tree nodes will be stored
  // sorted according to their information value.
  queue = new PriorityQueueM(nodeQ);
  
  // The new potential (with the same variables as this).
  pot = new PotentialMTree(variables);
  
  treeNew = new MultipleTree();
  treeSource = (MultipleTree)values;
 
//  if (method==1) {normalization = treeSource.conditionalProdSum(maxSize);}
 if (method==1) {normalization = treeSource.sum(maxSize) * treeSource.conditionalAverage();}
  else if (method==2) {normalization = treeSource.sum(maxSize)/treeSource.conditionalAverage();}
       else {normalization = treeSource.sum(maxSize);}
  // normalization = treeSource.sum(maxSize) * treeSource.conditionalAverage();
  pot.setTree(treeNew);
 
  
  
  if (values.getLabel()!=2) { // If the tree node is not a probab.
                              // put it in the queue.
    nodeQ = new NodeQueueM(treeNew,treeSource,maxSize,method,normalization);
    queue.insert(nodeQ);
    
    // While the size is not exceeded, add new nodes to the tree.
    while (!queue.isEmpty() &&
	   ((pot.size + queue.size()) < maxLeaves)) {
      nodeQ = queue.deleteMax();
      treeResult = (MultipleTree)nodeQ.res;
      treeSource = (MultipleTree)nodeQ.source;
      var = nodeQ.var;
      treeResult.assignVar(var);
      nv = var.getNumStates();
      newSize = maxSize / nv;
      
       normalization = normalization + nodeQ.updateNormalization;
      
      // For each child of the selected node:
      for (j=0 ; j<nv ; j++) {
	treeSource2 = treeSource.restrict(var,j);
	treeNew = treeResult.getChild(j);
	if (treeSource2.getLabel()!=2) { // If the tree node is not
                                     // a prob. put it in the queue.
	  nodeQ = new NodeQueueM(treeNew,treeSource2,newSize,method,normalization);
	  queue.insert(nodeQ);
	}
	else {
	  treeNew.assignProb(treeSource2.getProb());
	  treeNew.assignSecondValue(treeSource2.getSecondValue());
	  treeNew.assignMax(treeSource2.getMax());
	  treeNew.assignMin(treeSource2.getMin());
	  pot.size++;
	}
      }
    }

    // Substitute the remaining nodes by the average value.
    if(!queue.isEmpty()) {
      isAnApproxTree=true;
      while (!queue.isEmpty()) {
	nodeQ = queue.deleteMax();
	treeResult = (MultipleTree)nodeQ.res;
	treeSource = (MultipleTree)nodeQ.source;
	treeResult.assignProb(treeSource.average());
	treeResult.assignSecondValue(treeSource.conditionalAverage());
	treeResult.assignMin(treeSource.minimum());
	treeResult.assignMax(treeSource.maximum());
	pot.size++;
      }
    }
  }
  else {
    treeNew.assignProb(values.getProb());
    treeNew.assignSecondValue(values.getSecondValue());
    treeNew.assignMax(values.getMax());
    treeNew.assignMin(values.getMin());
    pot.size++;
  }
  if(!this.getExact())
    pot.setExact(false);
  else if(isAnApproxTree)
    pot.setExact(false);
  else
    pot.setExact(true);

  return pot;
}


/**
 * Bounds the tree associated with the potential by removing
 * nodes which information value is lower than a given threshold.
 * The tree is modified.
 * This method is for trees of class MultipleTree only.
 * @param limit the information limit.
 * @see MultipleTree.conditionalPrune()
 */

public void conditionalLimitBound(double limit,int method) {
 
  long maxSize;
  long [] numberDeleted;
  boolean bounded = false;
  double [] globalSum;
  
  numberDeleted = new long[1]; // Number of deleted nodes.
  globalSum = new double[1];
  
  maxSize = (long)FiniteStates.getSize(variables);
  
  
  if (method==1)
    {globalSum[0] = values.conditionalProdSum(maxSize);}
  else if (method==2)
    {globalSum[0] = values.sum(maxSize)/values.conditionalAverage();}
       else {globalSum[0] = values.sum(maxSize);}
  // If the tree is not just a probability value:
  if (values.getLabel()!=2) {
    if (method==1) {bounded = values.conditionalPrune(limit,maxSize,globalSum,
				      numberDeleted);}
    if (method==2) {bounded = values.conditionalPruneSimple(limit,maxSize,globalSum,
				      numberDeleted);}
  }
  
  if (bounded)
    size -= numberDeleted[0];
}


/**
 * Saves a tree to a file.
 * @param p the PrintWriter where the tree will be written.
*/   

public void save(PrintWriter p) {

  int i;
  Node n;
  
  for (i=0 ; i<variables.size() ; i++) {
    n = (Node)variables.elementAt(i);
    p.println("node "+n.getName());
  }
  p.print("values= tree ( \n");
  
  values.save(p,10);
  
  p.print("\n);\n\n");
}


/**
 * Prints a Potential to the standard output.
 */

public void print() {
  
  int i;
  Node n;
  
  for (i=0 ; i<variables.size() ; i++) {
    n = (Node)variables.elementAt(i);
    System.out.println("node "+n.getName());
  }
  System.out.println("Exact Potential: " + getExact());
  System.out.print("values= tree ( \n");
  
  values.print(10);
    
  System.out.print("\n);\n\n");
}

public void saveResult(PrintWriter P) {
  PotentialTable Pot2;
  Pot2=new PotentialTable(this);
  Pot2.saveResult(P);
}

} // End of class


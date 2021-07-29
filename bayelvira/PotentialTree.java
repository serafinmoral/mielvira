import java.util.Vector;
import java.util.Hashtable;
import java.io.*;
import ProbabilityTree;
import Potential;
import Configuration;
import NodeQueue;
import PriorityQueue;

/**
 * Implementation of class PotentialTree. A potential represented
 * by a probability tree.
 *
 * @since 11/11/99
*/

public class PotentialTree extends Potential {

ProbabilityTree values;
long size;


/**
 * Creates a new PotentialTree with no variables and a single value.
*/
    
PotentialTree() {

  variables = new Vector();
  values = new ProbabilityTree(0);
  size=0;
}


/**
 * Creates a new PotentialTree.
 * @param vars variables that the potential will contain.
*/
	 
PotentialTree(Vector vars) {

  variables = (Vector)vars.clone();
  values = new ProbabilityTree(0);
  size=0;
}


/**
 * Creates a new PotentialTree.
 * @param vars variables that the potential will contain,
 * given as a NodeList.
*/
	 
PotentialTree(NodeList vars) {

  variables = (Vector)vars.nodes.clone();
  values = new ProbabilityTree(0);
  size=0;
}

/**
 * Constructs a PotentialTree from a nodelist and a relation
 * defined over a subset of variables of the nodelist passed as parameter.
 * If the potential passed as parameter is not defined over a subset
 * of the variables in this, the method build a unitary potential
 *
 * @param vars the node list of variables over for the new potential
 * @param rel the relation defined over a subset of vars
 */

PotentialTree(NodeList vars, Relation rel) {
  
  int i, nv, pos; 
  Configuration conf,subConf;
  PotentialTree pot;
  
  variables = (Vector)vars.toVector().clone();
  
  // determining if pot.variables is a subset of vars
  
  if ( (rel.getVariables().kindOfInclusion(vars)).equals("subset") ){
    pot = (PotentialTree)rel.getValues();  
    values = pot.getTree().copy();
    size = pot.getSize();
  }
  else{ // unitary potential
    values = new ProbabilityTree(1.0); 
    size = 1;
  }
}


/**
 * Assigns a tree to the potential.
 * @param tree the Tree to be assigned.
*/

public void setTree(ProbabilityTree tree) {

  values = tree;
  size = tree.getSize();
}


/**
 * @return the Tree associated with the potential.
*/

public ProbabilityTree getTree(){
  
  return values;
}


/**
 * @return the number of values (size) of the potential.
*/

public long getSize() {

  return size;
}


public long getNumberOfValues(){
  return values.getSize();
}


/**
 * Saves a tree to a file.
 * @param p the <code>PrintWriter</code> where the tree will be written.
*/   

public void save(PrintWriter p) {

  p.println("node "+values.getVar().getName());
  p.print("values= tree ( \n");
  
  values.save(p,10);
  
  p.print("\n);\n\n");
}


/**
 * Prints a <code>PotentialTree</code> to the standard output.
 */

public void print() {
  
  System.out.println("node "+values.getVar().getName());
  System.out.print("values= tree ( \n");
  
  values.print(10);
  
  System.out.print("\n);\n\n");
}


/**
 * Gets the value for a onfiguration.
 * @param conf a <code>Configuration</code>.
 * @return the value corresponding to <code>Configuration conf</code>.
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
 * @param x a <code>double</code> value.
 * @param conf a <code>Configuration</code>.
*/

public void setValue(Configuration conf, double x) {

  Configuration aux;
  ProbabilityTree tree;
  FiniteStates var;
  int i, p, val, s;
  boolean update;
  

  update = true;

  aux = conf.copy();
  s = conf.variables.size();

  tree = values;

  for (i=0 ; i<s ; i++) {

    if (!tree.isVariable()) {
      var = aux.getVariable(0);
      val = aux.getValue(0);
      aux.remove(0);
      
      if (tree.isProbab()) // if the node is a probability,
	update=false;      // do not update the number of leaves.
      tree.assignVar(var);
    }
    else {
      p = aux.indexOf(tree.getVar());
      var = aux.getVariable(p);
      val = aux.getValue(p);
      aux.remove(p);
    }

    tree = tree.getChild(val);
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
  
  return values.sum(s);
}


/**
 * @param conf a <code>Configuration</code>.
 * @return the sum of all the values in the potential
   matching with <code>Configuration conf</code>. The result is the same
   as restricting the potential to <code>conf</code> and then using
   <code>totalPotential()</code>.
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
    temp = (FiniteStates)variables.elementAt(i);
    nv = nv * temp.getNumStates();
  }


  // Evaluate the tree for all the configurations and
  // compute the entropy.
  
  auxConf = new Configuration(variables);
  sum = 0.0;
  
  for (i=0 ; i<nv ; i++) {
    x = getValue(auxConf);
    if (x > 0.0)
      sum += x * Math.log(x);
    auxConf.nextConfiguration();
  }

  return ((-1.0) * sum);
}




/**
 * @param conf a <code>Configuration</code>.
 * @return the entropy of the values of the potential
   matching with <code>Configuration conf</code>. The result is the
   same as restricting first to conf and then using
   <code>entropyPotential()</code>.
 * @see entropyPotential()
*/

public double entropyPotential(Configuration conf) {

  Configuration auxConf;
  FiniteStates temp;
  int i, nv;
  double sum, x;

  nv = 1;
  for (i=0 ; i<variables.size() ; i++) {
    temp = (FiniteStates)variables.elementAt(i);
    nv = nv * temp.getNumStates();
  }

  // Evaluate the tree for all the configurations and
  // compute the entropy.

  auxConf = new Configuration(variables,conf);
  sum = 0.0;
  
  for (i=0 ; i<nv ; i++) {
    x = getValue(auxConf);
    if (x > 0.0)
      sum += x * Math.log(x);
    auxConf.nextConfiguration(conf);
  }

  return ((-1.0) * sum);
}


/**
 * Restricts the potential to a given configuration.
 * @param conf the restricting <code>Configuration</code>.
 * @return Returns a new <code>PotentialTree</code> where variables
 * in conf have been instantiated to their values in <code>conf</code>.
*/

public Potential restrictVariable(Configuration conf) {

  Vector aux;
  FiniteStates temp;
  PotentialTree pot;
  ProbabilityTree tree;
  int i, p, s, v;

  s = variables.size();
  aux = new Vector(s); // New list of variables.
  tree = getTree(); // tree will be the new tree
  
  for (i=0 ; i<s ; i++) {
    temp = (FiniteStates)variables.elementAt(i);
    p = conf.indexOf(temp);
    
    if (p == -1) // If it is not in conf, add to the new list.
      aux.addElement(temp);
    else {     // Otherwise, restrict the tree to it.
      v = conf.getValue(p);
      tree = tree.restrict(temp,v);
    }
  }

  pot = new PotentialTree(aux);
  pot.setTree(tree);
  
  return pot;
}


/**
 * This method divides two PotentialTrees.
 * For the exception 0/0, the method compute the result as 0.
 * The exception ?/0: the method abort with a message in the standar output.
 * @param p the <code>PotentialTree</code> to divide with this.
 * @return a new <code>PotentialTree</code> with the result of the combination.        
*/

public Potential divide(Potential p) { 

  Vector v, v1, v2;
  FiniteStates aux;
  int i, nv;
  PotentialTree pot;
  double x;
  ProbabilityTree tree, tree1, tree2;
  
  v1 = variables; // Variables of this potential.
  v2 = p.variables; // Variables of the argument.
  v = new Vector(); // Variables of the new potential.

  for (i=0 ; i<v1.size() ; i++) {
    aux = (FiniteStates)v1.elementAt(i);
    v.addElement(aux);
  }

  for (i=0 ; i<v2.size() ; i++) {
    aux = (FiniteStates)v2.elementAt(i);
    if (aux.indexOf(v1) == -1)
      v.addElement(aux);
  }

  // The new Potential.
  pot = new PotentialTree(v);
  
  tree1 = getTree(); // Tree of this potential.
  tree2 = ((PotentialTree)p).getTree(); // Tree of the argument.
  
  tree = ProbabilityTree.divide(tree1,tree2); // The new tree.
  
  pot.setTree(tree);

  return pot;
}


/**
 * Combines this potential with the argument. The argument <code>p</code>
 * can be a <code>PotentialTable</code> or a <code>PotentialTree</code>.
 * @param p a <code>Potential</code>.
 * @returns a new <code>PotentialTree</code> consisting of the combination
 * of <code>p</code> and this <code>Potential</code>.
*/

public Potential combine(Potential p) { 

  Vector v, v1, v2;
  FiniteStates aux;
  int i, nv;
  PotentialTree pot;
  double x;
  ProbabilityTree tree, tree1, tree2;
  
  if (p.getClass().getName().equals("PotentialTree")) {
    v1 = variables; // Variables of this potential.
    v2 = p.variables; // Variables of the argument.
    v = new Vector(); // Variables of the new potential.

    for (i=0 ; i<v1.size() ; i++) {
      aux = (FiniteStates)v1.elementAt(i);
      v.addElement(aux);
    }

    for (i=0 ; i<v2.size() ; i++) {
      aux = (FiniteStates)v2.elementAt(i);
      if (aux.indexOf(v1) == -1)
	v.addElement(aux);
    }

    // The new Potential.
    pot = new PotentialTree(v);
  
    tree1 = getTree(); // Tree of this potential.
    tree2 = ((PotentialTree)p).getTree(); // Tree of the argument.
  
    tree = ProbabilityTree.combine(tree1,tree2); // The new tree.
  
    pot.setTree(tree);
  }
  else if (p.getClass().getName().equals("PotentialTable") ||
	   p.getClass().getName().equals("PotentialConvexSet")) {
    return combine((PotentialTable)p);
  }
  else{
    System.out.println("Error in Potential PotentialTree.combine(Potential p): argument p was not a PotentialTree nor a PotentialTable nor a PotentialConvexSet");
    System.exit(1);
    pot = this;
  }
  
  return pot;
}



/**
 * Combines this <code>Potential</code> with the <code>PotentialTable</code>
 * of the argument.
 * @param p a <code>PotentialTable</code>.
 * @returns a new <code>PotentialTree</code> consisting of the combination
 * of <code>p</code> (PotentialTable) and this PotentialTree.
*/

public PotentialTree combine(PotentialTable p) {

  PotentialTree pt;

  pt = p.toTree();
  return (PotentialTree)combine((Potential)pt);
}

/**
 * Combines this potential with the PotentialTree of the argument.
 * @param p a PotentialTree.
 * @returns a new PotentialTree consisting of the combination
 * of p (PotentialTree) and this PotentialTree.
*/

public PotentialTree combine(PotentialTree p) {

  return (PotentialTree)combine((Potential)p);
}


/**
 * Combines two Potentials. The argument p MUST be a subset of
 * the potential which receives the message, and must be a PotentialTree
 *
 * IMPORTANT: this method modify the object which receives the message.
 *
 * @param p the PotentialTree to combine with this.     
*/

public void combineWithSubset(Potential p) { 

  ProbabilityTree tree;
  
  tree = ProbabilityTree.combine(this.getTree(),
				 ((PotentialTree)p).getTree());
  this.setTree(tree);
}


/**
 * Removes a list of variables by adding over all their states.
 * @param vars <code>Vector</code> of <code>FiniteStates</code>.
 * @return A new <code>PotentialTree</code> with the result of the operation.
*/

public PotentialTree addVariable(Vector vars) {

  Vector aux;
  FiniteStates var1, var2;
  int i, j;
  boolean found;
  PotentialTree pot;
  ProbabilityTree tree;

  
  aux = new Vector(); // New list of variables.
  for (i=0 ; i<variables.size() ; i++) {
    var1 = (FiniteStates)variables.elementAt(i);
    found = false;
    
    for (j=0 ; j<vars.size() ; j++) {
      var2 = (FiniteStates)vars.elementAt(j);
      if (var1 == var2) {
	found = true;
	break;
      }
    }
    
    if (!found)
      aux.addElement(var1);
    }

  pot = new PotentialTree(aux); // The new tree.

  tree = values;
  
  for (i=0 ; i<vars.size() ; i++) {
    var1 = (FiniteStates)vars.elementAt(i);
    tree = tree.addVariable(var1);
  }

  pot.setTree(tree);
  
  return pot;
}


/**
 * Removes the argument variable summing over all its values.
 * @param var a FiniteStates variable.
 * @return a new PotentialTree with the result of the deletion.
 */

public Potential addVariable(FiniteStates var) {

  Vector v;
  PotentialTree pot;
  
  v = new Vector();
  v.addElement(var);
  
  pot = addVariable(v);
  
  return pot;
}


/**
 * Marginalizes a PotentialTree to a list of variables.
 * It is equivalent to remove the other variables.
 * @param vars a vector of FiniteStates variables.
 * @return a new PotentialTree with the marginal.
 * @see addVariable(Vector vars)
*/

public Potential marginalizePotential(Vector vars) {

  Vector v;
  int i, j;
  boolean found;
  FiniteStates var1, var2;
  PotentialTree pot;

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
 * Removes a list of variables by applying marginalization by maximum.
 * @param vars vector of FiniteStates.
 * @return A new PotentialTree with the result of the operation.
*/

public Potential maxMarginalizePotential(Vector vars) {

  Vector aux;
  FiniteStates var1, var2;
  int i, j;
  boolean found;
  PotentialTree pot;
  ProbabilityTree tree;

  
  aux = new Vector(); // New list of variables.
  for (i=0 ; i<variables.size() ; i++) {
    var1 = (FiniteStates)variables.elementAt(i);
    found = false;
    
    for (j=0 ; j<vars.size() ; j++) {
      var2 = (FiniteStates)vars.elementAt(j);
      if (var1 == var2) {
	found = true;
	break;
      }
    }
    
    if (!found)
      aux.addElement(var1);
    }

  pot = new PotentialTree(aux); // The new tree.

  tree = values;
  
  for (i=0 ; i<vars.size() ; i++) {
    var1 = (FiniteStates)vars.elementAt(i);
    tree = tree.maximizeOverVariable(var1);
  }

  pot.setTree(tree);
  
  return pot;
}



/**
 * @return the configuration of maximum probability included in the
 * potential, that is consistent with the subConfiguration passed as
 * parameter (this subconfiguration can be empty)
 *
 * NOTE: if there are more than one configuration with maximum 
 * probability, the first one is returned
 *
 * @param subconf the subconfiguration to ensure consistency
 */

public Configuration getMaxConfiguration(Configuration subconf) {

  Explanation best;
  Configuration bestFound;
  Vector confValues;
  int i;
  Configuration conf;

  // first we creates a configuration with all values set to -1, 
  // -1 indicates that that variable can take every possible state

  confValues = new Vector();
  for(i=0;i<variables.size();i++)
    confValues.addElement(new Integer(-1)); 
  bestFound = new Configuration(variables,confValues);

  // an explanation that will contain the best found is initialized with
  // probability equal to -1.0

  best = new Explanation(new Configuration(variables),-1.0);

  best = values.getMaxConfiguration(best,bestFound,subconf);

  // if some value in best.conf is -1, then we have found a set of
  // configurations of maximal probability. In this case we return the
  // first one by changing -1 by 0.

  conf = best.getConf();
  for(i=0;i<conf.size();i++)
    if (conf.getValue(i) == -1) conf.putValue(conf.getVariable(i),0);

  return conf;
} 


/**
 * @return the configuration of maximum probability included in the
 * potential, that is consistent with the subConfiguration passed as
 * parameter (this subconfiguration can be empty), and differents to 
 * all the configurations passed in the vector
 *
 * NOTE: if there are more than one configuration with maximum 
 * probability, the first one is returned
 *
 * @param subconf the subconfiguration to ensure consistency
 * @param list the list of configurations to be differents
 */

public Configuration getMaxConfiguration(Configuration subconf,
                                         Vector list) {

  Explanation best;
  Configuration bestFound;
  Vector confValues;
  int i;
  Configuration conf;

  // first we creates a configuration with all values set to -1, 
  // -1 indicates that that variable can take every possible state

  confValues = new Vector();
  for(i=0;i<variables.size();i++)
    confValues.addElement(new Integer(-1)); 
  bestFound = new Configuration(variables,confValues);

  // an explanation that will contain the best found is initialized with
  // probability equal to -1.0

  best = new Explanation(new Configuration(variables),-1.0);

  best = values.getMaxConfiguration(best,bestFound,subconf,list);

  // if some value in best.conf is -1, then we have found a set of
  // configurations of maximal probability. In this case we return the
  // first one by changing -1 by 0.

  conf = best.getConf();
  for(i=0;i<conf.size();i++)
    if (conf.getValue(i) == -1) conf.putValue(conf.getVariable(i),0);
  
  return best.getConf();

} 


/**
 * Sorts the variables in the tree and limits the number of leaves.
 * @param maxLeaves maximum number of leaves in the new tree.
 * @return a new PotentialTree sorted and bounded.
 */

public PotentialTree sortAndBound(int maxLeaves) {
 
  PotentialTree pot;
  ProbabilityTree treeNew, treeSource, treeSource2, treeResult;
  NodeQueue nodeQ;
  PriorityQueue queue;
  FiniteStates var;
  int j, nv;
  long newSize, maxSize;
  
  // Size of the entire tree (expanded)
  maxSize = (long)FiniteStates.getSize(variables);
  
  nodeQ = new NodeQueue(1E20); // Infinity node.
  
  // Priority queue where the tree nodes will be stored
  // sorted according to their information value.
  queue = new PriorityQueue(nodeQ);
  
  // The new potential (with the same variables as this).
  pot = new PotentialTree(variables);
  
  treeNew = new ProbabilityTree();
  treeSource = values;
  pot.setTree(treeNew);
   
  if (!values.isProbab()) { // If the tree node is not a probab.
                            // put it in the queue.
    nodeQ = new NodeQueue(treeNew,treeSource,maxSize);
    queue.insert(nodeQ);
    
    // While the size is not exceeded, add new nodes to the tree.
    while (!queue.isEmpty() &&
	   ((pot.size + queue.size()) < maxLeaves)) {
      nodeQ = queue.deleteMax();
      treeResult = nodeQ.res;
      treeSource = nodeQ.source;
      var = nodeQ.var;
      treeResult.assignVar(var);
      nv = var.getNumStates();
      newSize = maxSize / nv;
      
      // For each child of the selected node:
      for (j=0 ; j<nv ; j++) {
	treeSource2 = treeSource.restrict(var,j);
	treeNew = treeResult.getChild(j);
	if (!treeSource2.isProbab()) { // If the tree node is not
                                       // a prob. put it in the queue.
	  nodeQ = new NodeQueue(treeNew,treeSource2,newSize);
	  queue.insert(nodeQ);
	}
	else {
	  treeNew.assignProb(treeSource2.getProb());
	  pot.size++;
	}
      }
    }

    // Substitute the remaining nodes by the average value.
    while (!queue.isEmpty()) {
      nodeQ = queue.deleteMax();
      treeResult = nodeQ.res;
      treeSource = nodeQ.source;
      treeResult.assignProb(treeSource.average());
      pot.size++;
    }
  }
  else {
    treeNew.assignProb(values.getProb());
    pot.size++;
  }

  return pot;
}


/**
 * Sorts the variables in the tree, according to an information
 * criterion. The same as the former method, but without
 * the restriction of a maximum number of nodes.
 * @return a new PotentialTree with the variables sorted.
 */

public PotentialTree sort() {
 
  PotentialTree pot;
  ProbabilityTree treeNew, treeSource, treeSource2, treeResult;
  NodeQueue nodeQ;
  PriorityQueue queue;
  FiniteStates var;
  int j, nv;
  long newSize, maxSize;
  
  
  maxSize = (long)FiniteStates.getSize(variables);
  nodeQ = new NodeQueue(1E20);
  queue = new PriorityQueue(nodeQ);
  
  pot = new PotentialTree(variables);
  treeNew = new ProbabilityTree();
  treeSource = values;
  pot.setTree(treeNew);

   
  if (!values.isProbab()) { // If the tree node is not a probab.
                            // put it in the queue.
    nodeQ = new NodeQueue(treeNew,treeSource,maxSize);
    queue.insert(nodeQ);
    
    while (!queue.isEmpty()) {
      nodeQ = queue.deleteMax();
      treeResult = nodeQ.res;
      treeSource = nodeQ.source;
      var = nodeQ.var;
      treeResult.assignVar(var);
      nv = var.getNumStates();
      newSize = maxSize / nv;
      
      for (j=0 ; j<nv ; j++) {
	treeSource2 = treeSource.restrict(var,j);
	treeNew = treeResult.getChild(j);
	if (treeSource2.getLabel()!=2) {
	  nodeQ = new NodeQueue(treeNew,treeSource2,newSize);
	  queue.insert(nodeQ);
	}
	else {
	  treeNew.assignProb(treeSource2.getProb());
	  pot.size++;
	}
      }
    }
  }
  else {
    treeNew.assignProb(values.getProb());
    pot.size++;
  }

  return pot;
}


/**
 * Bounds the tree associated with the potential by removing
 * nodes which information value is lower than a given threshold.
 * THE TREE IS MODIFIED.
 * @param limit the information limit.
 * @see ProbabilityTree.prune()
 */

public void limitBound(double limit) {
 
  long maxSize;
  long [] numberDeleted;
  boolean bounded = false;
  double globalSum;
  
  numberDeleted = new long[1]; // Number of deleted nodes.
  
  maxSize = (long)FiniteStates.getSize(variables);
  
  globalSum = values.sum(maxSize);
  
  // If the tree is not just a probability value:
  if (!values.isProbab())
    bounded = values.prune(limit,maxSize,globalSum,numberDeleted);
  
  if (bounded)
    size -= numberDeleted[0];
}


/**
 * Updates the actual size of the potential.
 */

public void updateSize() {

  values.updateSize();
  size = values.getSize();
}


/**
 * @return a copy of this <code>PotentialTree</code>.
 */

public PotentialTree copy() {
 
  PotentialTree pot;
  
  pot = new PotentialTree(variables);
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
 * Saves this potential to a <code>PrintWriter</code>.
 */

public void saveResult(PrintWriter p) {
  
  PotentialTable pot2;
  
  pot2 = new PotentialTable(this);
  pot2.saveResult(p);
}


/**
 * This method incorporates the evidence passed as argument to the
 * potential, that is, put to 0 all the values whose configurations
 * are not consistent with the evidence.
 *
 * The method works as follows: for each observed variable a
 * probability tree is built with 1.0 as value for the observed
 * state and 0.0 for the rest. Then the tree is combined with
 * this new tree, and the result is a new tree with the evidence
 * entered.         
 * @param ev a <code>Configuration</code> representing the evidence
 */

public void instantiateEvidence(Configuration evid) {   
  
  ProbabilityTree tree, twig;
  Configuration conf;
  PotentialTree pot, pot2;
  FiniteStates variable;
  int i, j, v;

  conf = new Configuration(evid,new NodeList(variables));
  
  if (conf.size() != 0){
    pot = this.copy();

    for (i=0 ; i<conf.size() ; i++) {
      variable = conf.getVariable(i);
      v = conf.getValue(i);

      // building a tree for variable
      tree = new ProbabilityTree(variable);
      for(j=0 ; j<tree.child.size() ; j++){
        twig = (ProbabilityTree) tree.child.elementAt(j);
        twig.label = 2;
        if (j == v)
	  twig.value = 1.0;
        tree.leaves++;
      }                            
      // building the potential for the variable
      pot2 = new PotentialTree();
      pot2.variables.addElement(variable);
      pot2.setTree(tree);
      // combination
      pot = pot.combine(pot2);
    }
    this.setTree(pot.getTree());
  }
}


} // end of class
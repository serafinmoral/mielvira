import java.util.Vector;
import java.util.Hashtable;
import java.lang.Math;
import FiniteStates;
import Configuration;
import NodeList;
import java.io.*;

/**
 * Implementation of class ProbabilityTree. A probability Tree
 * is a compact representation of a probability distribution.
 * Each internal node represents a variable and each leave
 * node represents a probability value. Each variable node
 * has as many children as possible values it has. Tha value stored
 * in a leave corresponds to the probability of the
 * configuration that leads from the root node to that leave.
 *
 * @since 30/11/99
*/


public class ProbabilityTree {


FiniteStates var;
double value;
int label; // 0: empty node. 1: full node. 2: probability node
Vector child;
long leaves;
static final int EMPTY_NODE=0;
static final int FULL_NODE=1;
static final int PROBAB_NODE=2;


/**
 * Constructor. Creates an empty tree node.
*/

public ProbabilityTree() {

  label = EMPTY_NODE;
  value = 0.0;
  leaves = 0;
  child = new Vector();
}


/**
 * Creates a tree with the argument as root node.
 * @param variable a FiniteStates variable.
*/

public ProbabilityTree(FiniteStates variable) {
  
  int i, j;
  ProbabilityTree tree;
  
  label = FULL_NODE;
  value = 0.0;
  leaves = 0;
  var = variable;
  child = new Vector();
  
  j = variable.getNumStates();
  for (i=0 ; i<j ; i++) {
    tree = new ProbabilityTree();
    child.addElement(tree);
  }
}


/**
 * Creates a probability node with value p
 * @param p a double value.
*/

public ProbabilityTree(double p) {

  label = PROBAB_NODE;
  value = p;
  child = new Vector();
  leaves = 1;
}


/**
 * @return <code>true</code> if the node is a probability and
 * <code>false</code> otherwise.
 */

public boolean isProbab() {
  
  if (label == PROBAB_NODE)
    return true;
  
  return false;
}


/**
 * @return <code>true</code> if the node is a variable and
 * <code>false</code> otherwise.
 */

public boolean isVariable() {
  
  if (label == FULL_NODE)
    return true;
  
  return false;
}


/**
 * @return <code>true</code> if the node is empty and
 * <code>false</code> otherwise.
 */

public boolean isEmpty() {
  
  if (label == EMPTY_NODE)
    return true;
  
  return false;
}


/**
 * @return the label of the node.
*/

public int getLabel() {

  return label;
}


/**
 * @return the probability value attached to the node.
*/

public double getProb() {

  return value;
}


/**
 * @param conf a Configuration.
 * @return the probability value of the tree following
 * the path indicated by configuration C.
*/

public double getProb(Configuration conf) {

  int p, val;
  ProbabilityTree tree;

  if (label == FULL_NODE) { // If the node is a variable
    
    val = conf.getValue(var);
    tree = (ProbabilityTree)child.elementAt(val);
    return(tree.getProb(conf));
  }
  else {
    if (label == PROBAB_NODE) // If the node is a prob. 
      return value;
    else 
      return(-1.0);
  }
}



/**
 * @param positions a hash table with the positions of the
 * variables in conf.
 * @param conf an array of int with a configuration.
 * @return the value of the tree for the configuration.
 */

public double getProb(Hashtable positions, int[] conf) {
 
  int p, val;
  ProbabilityTree tree;

  if (label == FULL_NODE) { // If the node is a variable,
                            // call the same method with the
                            // corresponding child.
    p = ((Integer)positions.get(var)).intValue();
    val = conf[p];

    tree = (ProbabilityTree)child.elementAt(val);
    return (tree.getProb(positions,conf));
  }
  else {
    if (label == PROBAB_NODE) 
      return value;
    else 
      return(-1.0);
  }
}


/**
 * Assigns a value p to the node.
 * Also, sets the label to 2.
 * @param p a double value.
*/

public void assignProb(double p) {

  label = PROBAB_NODE;
  value = p;
  leaves = 1;
}


/**
 * Assigns a variable to an empty node.
 * Initializes as many children as values of the
 * variable, to empty trees.
 * @param variable a FiniteStates variable.
*/

public void assignVar(FiniteStates variable) {
 
  ProbabilityTree tree;
  int i,j;
    
  var = variable;
  label = FULL_NODE;
  child = new Vector();
  j= variable.getNumStates();

  for (i=0 ; i<j ; i++) {
    tree = new ProbabilityTree();
    child.addElement(tree);
  }
}


/**
 * @return the FiniteStates variable stored in the tree node.
*/

public FiniteStates getVar() {

  return var;
}


/**
 * @return the vector of children of the tree.
*/

public Vector getChild() {
  
  return child; 
}


/**
 * @param i an int value. Number of child to be returned.
 * (first value of i=0).
 * @return the i-th child of the tree.
*/

public ProbabilityTree getChild(int i) {
  
 return ((ProbabilityTree)(child.elementAt(i))); 
}
   

/**
 * Inserts a Tree as child of the current node.
 * @param tree Tree to be added as child.
*/

public void insertChild(ProbabilityTree tree) {
 
  child.addElement(tree);
}
 

/**
 * @return a copy of the tree.
*/

public ProbabilityTree copy() {

  ProbabilityTree tree, tree2;
  int i, nv;
  
  tree = new ProbabilityTree();
  tree.var = var;
  tree.label = FULL_NODE;
  tree.leaves = leaves;

  if (label != PROBAB_NODE) { // If it is not a probability,

    nv = child.size();
      
    for (i=0 ; i<nv ; i++) {
      tree2 = ((ProbabilityTree)child.elementAt(i)).copy();
      tree.child.addElement(tree2);
    }
  }
  else
    tree.assignProb(value);
  
  return tree;
}


/**
 * Restricts a tree to a variable.
 * @param variable a FiniteStates variable to which the tree
 * will be restricted.
 * @param v the value of variable to instantiate (first value = 0).
 * @return a new tree consisting of the restriction of the
 * current tree to the value number v of variable variable.
*/

public ProbabilityTree restrict(FiniteStates variable, int v) {
 
  ProbabilityTree tree, tree2;
  int i, nv;

  if (label == PROBAB_NODE) {
    tree = new ProbabilityTree();
    tree.assignProb(value);
    return tree;
  }
  
  if (var == variable)
    tree = getChild(v).copy();
  else { 
    tree = new ProbabilityTree();
    tree.var = var;
    tree.label = FULL_NODE;
      
    nv = child.size();
    for (i=0 ; i<nv ; i++) {
      tree2 = ((ProbabilityTree)
	       child.elementAt(i)).restrict(variable,v);
      tree.child.addElement(tree2);
      tree.leaves += tree2.leaves;
    }
  }
  
    
  return tree;
}


/**
 * Restricts a tree to a Configuration of variables.
 * @param conf the configuration to which the tree
 * will be restricted.
 * @return a new <code>ProbabilityTree</code> consisting of the restriction
 * of the current tree to the values of <code>Configuration conf</code>.
*/

public ProbabilityTree restrict(Configuration conf) {
 
  ProbabilityTree tree, tree2;
  int i, nv, index;

  if (label == PROBAB_NODE) {
    tree = new ProbabilityTree();
    tree.assignProb(value);
    return tree;
  }
  
  index = conf.indexOf(var);
  if (index > -1) // if var is in conf
    tree = getChild(conf.getValue(index)).restrict(conf);
  else { 
    tree = new ProbabilityTree();
    tree.var = var;
    tree.label = FULL_NODE;
      
    nv = child.size();
    for (i=0 ; i<nv ; i++) {
      tree2 = ((ProbabilityTree) child.elementAt(i)).restrict(conf);
      tree.child.addElement(tree2);
      tree.leaves += tree2.leaves;
    }
  }
    
  return tree;
}


/**
 * Combines two trees. This operation is analogous to the pointwise
 * product of two probability tables.
 * To be used as a static function.
 * @param tree1 a <code>ProbabilityTree</code>.
 * @param tree2 a <code>ProbabilityTree</code> to be multiplied with tree1.
 * @return a new <code>ProbabilityTree</code> resulting from combining tree1
 * and tree2.
*/

public static ProbabilityTree combine(ProbabilityTree tree1,
				      ProbabilityTree tree2) {

  ProbabilityTree tree, tree3, tree4;
  int i, nv;

  if (tree1.getLabel() == PROBAB_NODE) { // Probability node.
    if (tree2.getLabel() == PROBAB_NODE) {
      tree = new ProbabilityTree(tree1.getProb() * tree2.getProb());
    }
    else {
      tree = new ProbabilityTree();
      tree.var = tree2.getVar();
      tree.label = FULL_NODE;
      tree.leaves = 1;
      
      nv=tree2.child.size();
      for (i=0 ; i<nv ; i++) {
	tree3 = ProbabilityTree.combine(tree1,tree2.getChild(i));
	tree.insertChild(tree3);
	tree.leaves += tree3.leaves;
      }
    }
  }
  else {
    tree = new ProbabilityTree();
    tree.var = tree1.getVar();
    tree.label = FULL_NODE;

    nv = tree1.child.size();
    for (i=0 ; i<nv ; i++) {
      tree3 = tree2.restrict(tree1.getVar(),i);
      tree4 = ProbabilityTree.combine(tree1.getChild(i),tree3);
      tree.insertChild(tree4);
      tree.leaves += tree4.leaves;
    }
  }

  return tree;
}



/**
 * Divide two trees.
 * To be used as a static function.
 *
 * For the exception 0/0, the method compute the result as 0.
 * The exception ?/0: the method abort with a message in the standar output.  
 *
 * @param tree1 a Tree.
 * @param tree2 a Tree.
 * @return a new Tree resulting from combining tree1 and tree2.
*/

public static ProbabilityTree divide(ProbabilityTree tree1,
				      ProbabilityTree tree2) {

  ProbabilityTree tree, tree3, tree4;
  int i, nv;
  double x,y,x2;

  if (tree1.getLabel() == PROBAB_NODE) { // Probability node.
    if (tree2.getLabel() == PROBAB_NODE) {
      x = tree1.getProb();
      y = tree2.getProb();
      x2=x; 
      
      if (y == 0.0){
        if (x == 0.0)
	  x=0;
        else{
          System.out.println("Divide by zero " + x2 +"/" + y);
          x = 0;
          //System.exit(0);
        }
      }
      else
	x/=y;

      if (Double.isInfinite(x))
	System.out.println(x2 + "/" + y + " Ha dado infinito");

      tree = new ProbabilityTree(x);
      tree.leaves = 1;
    }
    else {
      tree = new ProbabilityTree();
      tree.var = tree2.getVar();
      tree.label = FULL_NODE;
      tree.leaves = 1;
      
      nv=tree2.child.size();
      for (i=0 ; i<nv ; i++) {
	tree3 = ProbabilityTree.divide(tree1,tree2.getChild(i));
	tree.insertChild(tree3);
	tree.leaves += tree3.leaves;
      }
    }
  }
  else {
    tree = new ProbabilityTree();
    tree.var = tree1.getVar();
    tree.label = FULL_NODE;

    nv = tree1.child.size();
    for (i=0 ; i<nv ; i++) {
      tree3 = tree2.restrict(tree1.getVar(),i);
      tree4 = ProbabilityTree.divide(tree1.getChild(i),tree3);
      tree.insertChild(tree4);
      tree.leaves += tree4.leaves;
    }
  }

  return tree;
}



/**
 * Adds the argument tree to this.
 * @param tree a Tree.
 * @return a new Tree with the addition of tree and the current Tree.
*/

public ProbabilityTree add(ProbabilityTree tree) {
 
  ProbabilityTree tree1, tree2, treeH;
  int i, nv;
  
  if (label == PROBAB_NODE) {
    if (tree.getLabel() == PROBAB_NODE) /* If both are probabilities */
      tree1 = new ProbabilityTree(value + tree.getProb());
    else {
      tree1 = new ProbabilityTree();
      tree1.var = tree.getVar();
      tree1.label = FULL_NODE;

      nv = tree.getChild().size();
      tree2 = new ProbabilityTree(value);
      for (i=0 ; i<nv ; i++) {
	treeH = tree.getChild(i).restrict(tree.getVar(),i);
	treeH = treeH.add(tree2);
	tree1.insertChild(treeH);
	tree1.leaves += treeH.leaves;
      }
    }      
  }
  else {
    tree1 = new ProbabilityTree();
    tree1.var = var;
    tree1.label = FULL_NODE;
    
    nv = child.size();
    
    for (i=0 ; i<nv ; i++) {
      treeH = getChild(i).restrict(var,i);
      treeH = treeH.add(tree.restrict(var,i));
      tree1.insertChild(treeH);
      treeH.leaves += tree1.leaves;
    }
  }
    
  return tree1;
}


/**
 * Removes variable <code>variable</code> by summing over all its values.
 * @param variable a FiniteStates variable.
 * @return a new Tree with the result of the operation.
*/

public ProbabilityTree addVariable(FiniteStates variable) {

  ProbabilityTree tree, treeH;
  int i, nv;
  
  
  if (label == PROBAB_NODE)
    tree = new ProbabilityTree(value * variable.getNumStates());
  else {
    if (var == variable)
      tree = addChildren();
    else {
      tree = new ProbabilityTree();
      tree.var = var;
      tree.label = FULL_NODE;
      
      nv = child.size();
      for (i=0 ; i<nv ; i++) {
	treeH = getChild(i).restrict(var,i).addVariable(variable);
	tree.insertChild(treeH);
	tree.leaves += treeH.leaves;
      }
    }
  }
  
  return tree;
}


/**
 * @return a new tree equal to the addition of all the children
 *         of the current Tree.
 */

public ProbabilityTree addChildren() {
 
  ProbabilityTree tree;
  int i, nv;
  
  tree = getChild(0);
  
  nv = child.size();
  for (i=1 ; i<nv ; i++)
    tree = tree.add(getChild(i));

  return tree;
}



/**
 * return the maximum of two doubles
 */
public double maximum(double a, double b){
  if (a>=b) return a;
  else return b;
}

/**
 * Integrate the argument tree to this by applying maximization.
 * @param tree a Tree.
 * @return a new Tree with the addition of tree and the current Tree.
*/

public ProbabilityTree max(ProbabilityTree tree) {
 
  ProbabilityTree tree1, tree2, treeH;
  int i, nv;
  
  if (label==2) {
    if (tree.getLabel()==2) /* If both are probabilities */
      tree1 = new ProbabilityTree(maximum(value,tree.getProb()));
    else {
      tree1 = new ProbabilityTree();
      tree1.var = tree.getVar();
      tree1.leaves = 0;
      tree1.label = 1;

      nv = tree.getChild().size();
      tree2 = new ProbabilityTree(value);
      for (i=0 ; i<nv ; i++) {
	treeH = tree.getChild(i).restrict(tree.getVar(),i);
	treeH = treeH.max(tree2);
	tree1.insertChild(treeH);
	tree1.leaves += treeH.leaves;
      }
    }
      
  }
  else {
    tree1 = new ProbabilityTree();
    tree1.var = var;
    tree1.leaves = 0;
    tree1.label = 1;
    
    nv = child.size();
    
    for (i=0 ; i<nv ; i++) {
      treeH = getChild(i).restrict(var,i);
      treeH = treeH.max(tree.restrict(var,i));
      tree1.insertChild(treeH);
      treeH.leaves += tree1.leaves;
    }
  }
    
  return tree1;
}


/**
 * @return a new tree equal to the maximization of all the children
 *         of the current Tree.
 */

public ProbabilityTree maxChildren() {
 
  ProbabilityTree tree;
  int i, nv;
  
  tree = getChild(0);
  
  nv = child.size();
  for (i=1 ; i<nv ; i++)
    tree = tree.max(getChild(i));

  return tree;
}


/**
 * Removes variable variable by maximizing over it
 * @param variable a FiniteStates variable.
 * @return a new Tree with the result of the operation.
*/

public ProbabilityTree maximizeOverVariable(FiniteStates variable) {

  ProbabilityTree tree, treeH;
  int i, nv;
  
  
  if (label==2)
    tree = new ProbabilityTree(value); // el valor a devolver es el mismo
  else { 
    if (var==variable)
      tree = maxChildren();
    else {
      tree = new ProbabilityTree();
      tree.var = var;
      tree.label = 1;
      tree.leaves = 0;
      
      nv = child.size();
      for (i=0 ; i<nv ; i++) {
	treeH = getChild(i).restrict(var,i).maximizeOverVariable(variable);
	tree.insertChild(treeH);
	tree.leaves += treeH.leaves;
      }
    }
  }
  
  return tree;
}


/**
 * @return the configuration of maximum probability included in the
 * tree, that is consistent with the subConfiguration passed as
 * parameter (this subconfiguration can be empty)
 *
 * NOTE: if there are more than one configuration with maximum 
 * probability, the first one is returned
 *
 * @param bestExpl the best explanation found until this moment
 *  (configuration + probability)
 * @param conf the configuration built following the path from
 *        this node until the root.
 * @param subconf the subconfiguration to ensure consistency
 */

public Explanation getMaxConfiguration(Explanation bestExpl,
                                         Configuration conf,
                                         Configuration subconf) {

  int pos,numChildren,i;
  ProbabilityTree tree;
  Explanation exp;

  if (isProbab()){
    if (value > bestExpl.getProb()){
      bestExpl.setProb(value);
      bestExpl.setConf(conf);
    }
  }
  else if (isVariable()){
    pos = subconf.indexOf(var);
    if (pos==-1){ // all the children are explored
      numChildren = child.size();
      for(i=0;i<numChildren;i++){
        tree = (ProbabilityTree) child.elementAt(i);
        conf.putValue(var,i);
        bestExpl = tree.getMaxConfiguration(bestExpl,conf,subconf);        
      }            
    }
    else{ // only the child with the value in subconf is explored
      tree = (ProbabilityTree) child.elementAt(subconf.getValue(pos));
      conf.putValue(var,subconf.getValue(pos));
      bestExpl = tree.getMaxConfiguration(bestExpl,conf,subconf);
    } 
    // restoring the value -1 for var in conf
    conf.putValue(var,-1);
  }
  else{ // empty node
    System.out.println("Error detected at ProbabilityTree.getMaxConfiguration");
    System.out.println("An empty node was found");
    System.exit(0);
  }


  return bestExpl;
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
 * @param bestExpl the best explanation found until this moment
 *  (configuration + probability)
 * @param conf the configuration built following the path from
 *        this node until the root.
 * @param subconf the subconfiguration to ensure consistency
 * @param list the list of configurations to be differents     
 */

public Explanation getMaxConfiguration(Explanation bestExpl,
                                         Configuration conf,
                                         Configuration subconf,
					 Vector list) {

  int pos,numChildren,i;
  ProbabilityTree tree;
  Explanation exp;
  Configuration firstConf; 

  if (isProbab()){
    if (value > bestExpl.getProb()){
      firstConf = conf.getFirstNotInList(list);
      if (firstConf.size()>0){
        bestExpl.setProb(value);
        bestExpl.setConf(firstConf);
      }
    }
  }
  else if (isVariable()){
    pos = subconf.indexOf(var);
    if (pos==-1){ // all the children are explored
      numChildren = child.size();
      for(i=0;i<numChildren;i++){
        tree = (ProbabilityTree) child.elementAt(i);
        conf.putValue(var,i);
        bestExpl = tree.getMaxConfiguration(bestExpl,conf,subconf,list);        
      }            
    }
    else{ // only the child with the value in subconf is explored
      tree = (ProbabilityTree) child.elementAt(subconf.getValue(pos));
      conf.putValue(var,subconf.getValue(pos));
      bestExpl = tree.getMaxConfiguration(bestExpl,conf,subconf,list);
    } 
    // restoring the value -1 for var in conf
    conf.putValue(var,-1);
  }
  else{ // empty node
    System.out.println("Error detected at ProbabilityTree.getMaxConfiguration");
    System.out.println("An empty node was found");
    System.exit(0);
  }


  return bestExpl;
}


/**
 * Gets the list of variables in the tree.
 * @return the list of nodes.
 */

public NodeList getVarList() {
 
  NodeList list;
  ProbabilityTree tree;
  int i;
  
  list = new NodeList();
  
  if (label != FULL_NODE)
    return list;
  
  list.insertNode(var);
  
  for (i=0 ; i<var.getNumStates() ; i++) {
    tree = getChild(i);
    list.merge(tree.getVarList());
  }
  
  return list;
}


/**
 * Computes the information of a variable within a tree.
 * @param variable a <code>FiniteStates</code> variable.
 * @param potentialSize maximum size of the potential containing
 * the tree.
 * @return the value of information of variable.
 */

public double information(FiniteStates variable,
			  long potentialSize) {
 
  ProbabilityTree tree;
  int i, nv;
  long newSize;
  double entropy = 0.0, s = 0.0, totalS = 0.0, info = 0.0;
  
  nv = variable.getNumStates();
  newSize = potentialSize / nv;
  
  for (i=0 ; i<nv ; i++) {
    tree = restrict(variable,i);
    s = tree.sum(newSize);
    entropy += -s * Math.log(s);
    totalS += s;
  }
  
  if (totalS == 0.0)
    info = 0.0;
  else
    info = Math.log(nv) - Math.log(totalS) - (entropy / totalS);

 return (totalS * info);
}


/**
 * Computes the addition of all the values in the tree.
 * @param treeSize size of the fully expanded tree.
 * @return the addition computed.
 */

public double sum(long treeSize) {
 
  double s = 0.0;
  int i, nv;
  long newSize;
  
  if (label == PROBAB_NODE)
    s = (double)treeSize * value;
  else {
    nv = var.getNumStates();
    newSize = treeSize / nv;
    for (i=0 ; i<nv ; i++)
      s += getChild(i).sum(newSize);
  }
  
  return s;
}


/**
 * Computes the mean of the values in the tree.
 * @return the mean.
 */

public double average() {

  double av = 0.0;
  int i, nv;
  
  if (label == PROBAB_NODE)
    av = value;
  else {
    nv = var.getNumStates();
    for (i=0 ; i<nv ; i++)
      av += getChild(i).average();
    av /= (double)nv;
  }
  
  return av;
}


/**
 * Bounds the tree by substituting nodes whose children are
 * leaves by the average of them. This is done for nodes
 * with an information value lower than a given threshold.
 * @param limit the infromation threshold for pruning.
 * @param oldSize size of this tree if it were complete.
 * @param globalSum the addition of the original potential.
 * @param numberDeleted an array with a single value storing
 * the number of deleted leaves.
 */

public boolean prune(double limit, long oldSize, double globalSum,
		     long numberDeleted[]) {

  long newSize;
  int i, numberChildren;
  ProbabilityTree ch;
  double pr, sum = 0.0, entropy = 0.0, info;
  boolean bounded = true, childBounded; 
  
  numberChildren = var.getNumStates();
  
  newSize = oldSize / numberChildren;
  
  for (i=0 ; i<numberChildren ; i++) {
    ch = getChild(i);
    
    if (ch.label == PROBAB_NODE) {
       pr = ch.value;
       sum += pr;
       entropy += (-pr * Math.log(pr));
    }
    else {
      childBounded = ch.prune(limit,newSize,globalSum,numberDeleted);

      if (!childBounded)
	bounded = false;

      if (bounded) {
	ch = getChild(i);
	pr = ch.value;
	sum += pr;
	entropy += (-pr * Math.log(pr));
      }
    }
  }
  
  if (bounded) {
    if (sum <= 0.0)
      info = 0.0;
    else
      info = ((newSize * sum) / globalSum) *
	(Math.log(numberChildren) - Math.log(sum) - entropy / sum);
   
    if (info <= limit) {
      pr = average();
      numberDeleted[0] += numberChildren;
      assignProb(pr);
      child = new Vector();
    }
    else
      bounded = false;
  }

  return bounded;
}


/**
 * @return the number of leaves beneath this tree node.
 */

public long getSize() {
 
  return leaves;
}


/**
 * Updates the number of leaves in this tree and in each of
 * its subtrees.
 */

public void updateSize() {
 
  ProbabilityTree tree;
  int i, nv;
  
  if (label == PROBAB_NODE)
    leaves = 1;
  else {
    leaves = 0;
    nv = var.getNumStates();
    
    for (i=0 ; i<nv ; i++) {
      tree = getChild(i);
      tree.updateSize();
      leaves += tree.getSize();
    }
  }
}


/**
 * @param variable a FiniteStates variable.
 * @return <code>true</code> if variable is in some node in the tree,
 *         and <code>false</code> otherwise.
 */

public boolean isIn(FiniteStates variable) {
 
  boolean found = false;
  int i;
  
  if (label != FULL_NODE)
    found = false;
  else {
    if (var == variable)
      found = true;
    else {
      for (i=0 ; i<child.size() ; i++) {
	if (getChild(i).isIn(variable)) {
	  found = true;
	  break;
	}
      }
    }
  }
  
  return found;
}


/**
 * Saves the tree to a file.
 * @param p the PrintWriter where the tree will be written.
 * @param j a tab factor (number of blank spaces befor a child
 * is written).
 */

public void save(PrintWriter p,int j) {
  
  int i,l,k;
  
  if (label == PROBAB_NODE)
    p.print(value+";\n");
  else {
    p.print("case "+var.getName()+" {\n");
    
    for (i=0 ; i< child.size() ; i++) {
      for (l=1 ; l<=j ; l++)
	p.print(" ");
      
      p.print(var.getState(i) + " = ");
      getChild(i).save(p,j+10);
    }
    
    for (i=1 ; i<=j ; i++)
      p.print(" ");
    
    p.print("          } \n");
  }        
}


/**
 * Prints a tree to the standard output.
 * @param j a tab factor (number of blank spaces befor a child
 * is written).
 */

public void print(int j) {
  
  int i,l,k;


  
  if (label == PROBAB_NODE)
    System.out.print(value+";\n");
  else {
    System.out.print("case "+var.getName()+" {\n");
    
    for(i=0 ; i< child.size() ; i++) {
      for (l=1 ; l<=j ; l++)
	System.out.print(" ");
      
      System.out.print( var.getState(i) + " = ");
      getChild(i).print(j+10);
    }
    
    for (i=1 ; i<=j ; i++)
      System.out.print(" ");
    
    System.out.print("          } \n");
  }        
}



/**
 * Normalizes this tree to sum up to 1.
 * @param totalSize size of the fully expanded tree.
 */

public void normalize(long totalSize) {
 
  double total;
  int i, nv;
  
  total = sum(totalSize);
  
  if (label == PROBAB_NODE)
    value /= total;
  else {
    nv = var.getNumStates();
    for (i=0 ; i<nv ; i++)
      getChild(i).normalizeAux(total);
  }
}


/**
 * Auxiliar to the previous one.
 */

public void normalizeAux(double total) {
 
  int i, nv;
  
  if (label == PROBAB_NODE)
    value /= total;
  else {
    nv = var.getNumStates();
    for (i=0 ; i<nv ; i++)
      getChild(i).normalizeAux(total);
  }  
}

} // End of class
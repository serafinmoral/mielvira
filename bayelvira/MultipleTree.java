import java.util.Vector;
import java.util.Hashtable;
import Configuration;
import ProbabilityTree;
import java.io.*;


/**
 * Class MultipleTree. Implements a probability tree with
 * two possible values at each leaf.
 *
 * @since  25/06/99
 */

public class MultipleTree {
 
FiniteStates var;
int label; // 0: empty node. 1: full node. 2: probability node
Vector child;
long leaves;
double value;
double secondValue;
double max;
double min;
static final int EMPTY_NODE=0;
static final int FULL_NODE=1;
static final int PROBAB_NODE=2;
  

/**
 * Constructor. Creates an empty tree node.
*/

public MultipleTree() {

  label = EMPTY_NODE;
  value = 0.0;
  max = 0.0;
  min = 0.0;
  secondValue = 0.0;
  leaves = 0;
  child = new Vector();
}


/**
 * Constructor. Creates a MultipleTree from a ProbabilityTree.
 * @param pt a ProbabilityTree.
 */

public MultipleTree(ProbabilityTree pt) {
 
  int i;
  MultipleTree temp;
  
  child = new Vector();
  label = pt.getLabel();
  if (label==PROBAB_NODE) {
    value = pt.getProb();
    max = value;
    min = value;
    leaves = 1;
  }
  else {
    var = pt.getVar();
    for (i=0 ; i<pt.getChild().size() ; i++) {
      temp = new MultipleTree(pt.getChild(i));
      child.addElement(temp);
    }
  }
}


/**
 * Creates a probability node with value p
 * @param p a double value.
*/

public MultipleTree(double p) {

  label = PROBAB_NODE;
  value = p;
  max = p;
  min = p;
  child = new Vector();
  leaves = 1;
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
 
  MultipleTree tree;
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
  MultipleTree tree;

  if (label == FULL_NODE) { // If the node is a variable
    
    val = conf.getValue(var);
    tree = (MultipleTree)child.elementAt(val);
    return(tree.getProb(conf));
  }
  else {
    if (label==PROBAB_NODE) // If the node is a prob. 
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
  MultipleTree tree;

  if (label == FULL_NODE) { // If the node is a variable,
                    // call the same method with the
                    // corresponding child.
    p = ((Integer)positions.get(var)).intValue();
    val = conf[p];

    tree = (MultipleTree)child.elementAt(val);
    return (tree.getProb(positions,conf));
  }
  else {
    if (label==PROBAB_NODE) 
      return value;
    else 
      return(-1.0);
  }
}


/**
 * Assigns a value p to the node.
 * Also, sets the label to PROBAB_NODE.
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
 
  MultipleTree tree;
  int i,j;
    
  var = variable;
  label = FULL_NODE;
  child = new Vector();
  j= variable.getNumStates();

  for (i=0 ; i<j ; i++) {
    tree = new MultipleTree();
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
 * Assigns the second value.
 * @param x a double value.
 */

public void assignSecondValue(double x) {
 
  secondValue = x;
}


/**
 * Assign the max value.
 * @param x the value that will be the max.
 */

public void assignMax(double x) {
  
  max = x;
}


/**
 * Assign the min value.
 * @param x the value that will be the min.
 */

public void assignMin(double x) {
  
  min = x;
}


/**
 * Gets the second value.
 * @return the secon value.
 */

public double getSecondValue() {
  
  return secondValue;
}


/**
 * Gets the max value.
 * @return the max value.
 */

public double getMax() {
  
  return max;
}


/**
 * Gets the min value.
 * @return the min value.
 */

public double getMin() {
  
  return min;
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

public MultipleTree getChild(int i) {
  
  return ((MultipleTree)(child.elementAt(i))); 
}


/**
 * Inserts a Tree as child of the current node.
 * @param tree Tree to be added as child.
 */

public void insertChild(MultipleTree tree) {
 
  child.addElement(tree);
}
 


/**
 * Creates a MultipleTree constantly equal to 1.
 * @return a unit MultipleTree.
 */

public static MultipleTree unitTree() {
 
  MultipleTree t;
  
  t = new MultipleTree();
  t.assignProb(1.0);
  t.assignSecondValue(0.0);
  t.assignMax(1.0);
  t.assignMin(1.0);
  
  return t;
}


/**
 * Computes the minimum of the min values in the subtree.
 * @return the minimum of the min values in the subtree.
 */

public double minimum() {
  
  double m = 1e20, m2;
  int i, nv;
  
  if (label==PROBAB_NODE)
    m = min;
  else {
    nv = var.getNumStates();
    for (i=0 ; i<nv ; i++) {
      m2 = ((MultipleTree)child.elementAt(i)).minimum();
      if (m2<m)
	m = m2;
    }
  }
  
  return m;
}


/**
 * Computes the maximum of the max values in the subtree.
 * @return the maximum of the max values in the subtree.
 */

public double maximum() {
  
  double m = 0.0, m2;
  int i, nv;
  
  if (label==PROBAB_NODE)
    m = max;
  else {
    nv = var.getNumStates();
    for (i=0 ; i<nv ; i++) {
      m2 = ((MultipleTree)child.elementAt(i)).maximum();
      if (m2>m)
	m = m2;
    }
  }
  
  return m;
}


/**
 * Assume you have a ProbabilityTree. Then you assign
 * it to a variable of type MultipleTree. This function is
 * used to complete the fields that are in MultipleTree
 * but not in ProbabilityTree. More precisely, max and min
 * are set to the same value as 'value' in the original tree,
 * and the second value is set to zero. Non-leaf nodes remain
 * unchanged.
 */

public void convert() {
 
  int i, nv;

  if (label!=PROBAB_NODE) { // If this node is not a probability,

    nv = getChild().size();
      
    for (i=0 ; i<nv ; i++)
      ((MultipleTree)getChild().elementAt(i)).convert();
  }
  else {
    assignSecondValue(0.0);
    assignMax(value);
    assignMin(value);
  }
}


/**
 * Constructs a MultipleTree from this MultipleTree conditional
 * on another MultipleTree. The first value at each leaf will
 * be equal to the one in the first tree, and the second to the
 * conditional value.
 * @param condTree the conditioning tree.
 * @return the new MultipleTree.
 * @param currentConf the Configuration leading to this
 * tree node.
 */

public MultipleTree conditional(MultipleTree condTree,
				long totalSize,
				Configuration currentConf) {

  int i, nv;
  MultipleTree newTree, auxTree, otherTree;
  long newSize;
  
  newTree = new MultipleTree();

  newTree.var = var;
  newTree.label = FULL_NODE;
  newTree.value = 0.0;
  newTree.secondValue = 0.0;
  newTree.leaves = leaves;
  newTree.assignMax(max);
  newTree.assignMin(min);

  if (label!=PROBAB_NODE) { // If it is not a probability,

    nv = child.size();
      
    for (i=0 ; i<nv ; i++) {
      currentConf.insert(var,i);
      auxTree = ((MultipleTree)child.elementAt(i)).conditional(condTree,
						   totalSize,
						   currentConf);
      newTree.child.addElement(auxTree);
      currentConf.remove(currentConf.size()-1);
    }
  }
  else {
    newSize = totalSize /
      (long)FiniteStates.getSize(currentConf.variables);
    otherTree = condTree.restrict(currentConf);
    newTree.assignProb(value);
    newTree.assignMax(max);
    newTree.assignMin(min);
    newTree.assignSecondValue(otherTree.average());
  }
  
  return newTree;
}
 
 
/**
 * Computes the information of a variable within a tree.
 * @param variable a FiniteStates variable.
 * @param potentialSize maximum size of the potential containing
 * the tree.
 * @return the value of information of variable.
 */

public double conditionalInformation(FiniteStates variable,
				     long potentialSize,double normalization, double[] updateFactor ) {
 
  MultipleTree tree;
  int i, nv;
  long newSize;
  double entropy = 0.0, s = 0.0, s2 = 0.0,  totalA = 0.0, totalS = 0.0, total3=0.0, info = 0.0;
  
  nv = variable.getNumStates();
  newSize = potentialSize / nv;
  
  for (i=0 ; i<nv ; i++) {
    tree = restrict(variable,i);
    s = tree.average();
    s2 = tree.conditionalAverage();
    if (s > 0.0) {entropy += s*s2 * Math.log(s);}
    totalS += s2;
    totalA += s;
    total3 += s*s2;
  }
  
  updateFactor[0] = newSize*(total3-(totalS*totalA)/nv);
  
  normalization = normalization + updateFactor[0];
  
  
  if (totalA == 0.0)
    info = 0.0;
  else
    if (normalization == 0.0) {info= 1E10;}
    else {
      info =(1.0/normalization)* (newSize*entropy - newSize*total3 *Math.log(totalA/nv) + 
          normalization*Math.log( (normalization - updateFactor[0])/normalization));
      if (info > 1E10) {info=1E10; System.out.println("Informacion muy grande");}
    }

 return (info);
}
 
/**
 * Computes the information of a variable within a tree.
 * It is a simplified and approximate version of conditionalInformation
 * @param variable a FiniteStates variable.
 * @param potentialSize maximum size of the potential containing
 * the tree.
 * @return the value of information of variable.
 */

public double conditionalInformationSimple(FiniteStates variable,
				     long potentialSize,double normalization, double[] updateFactor ) {
 
  MultipleTree tree;
  int i, nv;
  long newSize;
  double entropy = 0.0, s = 0.0, s2 = 0.0,  totalA = 0.0, totalS = 0.0,  info = 0.0;
  
  nv = variable.getNumStates();
  newSize = potentialSize / nv;
  
  for (i=0 ; i<nv ; i++) {
    tree = restrict(variable,i);
    s = tree.average();
    s2 = tree.conditionalAverage();
    if (s > 0.0) {entropy += s * Math.log(s);}
    totalS += s2;
    totalA += s;
    
  }
  
  updateFactor[0] = 0.0;
    
  
  
  if (totalA == 0.0)
    info = 0.0;
  else
    if (normalization == 0.0) {info= 1E10;}
    else {
      info =(1.0/normalization)* (newSize*entropy - newSize*totalA *Math.log(totalA/nv))*(totalS/nv);
      if (info > 1E10) {info=1E10; System.out.println("Informacion muy grande");}
    }

 return (info);
}



/**
 * Computes the information of a variable within a tree,
 * according to the max-min criterion.
 * @param variable a FiniteStates variable.
 * @param potentialSize maximum size of the potential containing
 * the tree.
 * @return the value of information of variable.  
 */

public double maxMinInformation(FiniteStates variable,
				     long potentialSize) {
 
  MultipleTree tree;
  int i, nv;
  long newSize;
  double mini = 1e20, info = 0.0;
  double[] s;
  double[] m;
  
  
  nv = variable.getNumStates();
  s = new double[nv];
  m = new double[nv];
  newSize = potentialSize / nv;
  
  for (i=0 ; i<nv ; i++) {
    tree = restrict(variable,i);
    s[i] = tree.conditionalSum(newSize);
    m[i] = tree.minimum();
    if (m[i]<mini)
      mini = m[i];
  }
  
  for (i=0 ; i<nv ; i++) {
    info += ( (m[i]-mini) * s[i] );
  }
  
 return info;
}



/**
 * Restricts a tree to a variable.
 * @param variable a FiniteStates variable to which the tree
 * will be restricted.
 * @param v the value of variable to instantiate (first value = 0).
 * @return a new tree consisting of the restriction of the
 * current tree to the value number v of variable variable.
*/

public MultipleTree restrict(FiniteStates variable, int v) {
 
  MultipleTree tree, tree2;
  int i, nv;
  
  if (label==PROBAB_NODE) {
    tree = new MultipleTree();
    tree.assignProb(value);
    tree.assignSecondValue(secondValue);
    tree.assignMax(max);
    tree.assignMin(min);
    // No hace falta pues se hace en assignProb tree.leaves = 1;
    return tree;
  }
  
  if (var==variable)
    tree = getChild(v).copy();
  else { 
    tree = new MultipleTree();
    tree.var = var;
    tree.label = FULL_NODE;
    // Ya se hace en MultipleTree tree.value = 0.0;
    //tree.secondValue = 0.0;
    //tree.leaves = 0;
      
    nv = child.size();
    for (i=0 ; i<nv ; i++) {
      tree2 = ((MultipleTree)
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
 * @return a new <code>MultipleTree</code> consisting of the restriction of the
 * current tree to the values of <code>Configuration conf</code>.
*/

public MultipleTree restrict(Configuration conf) { 
  MultipleTree tree, tree2;
  int i, nv,index;
  
  if (label==PROBAB_NODE) {
    tree = new MultipleTree();
    tree.assignProb(value);
    tree.assignSecondValue(secondValue);
    tree.assignMax(max);
    tree.assignMin(min);
    // No hace falta pues se hace en assignProb tree.leaves = 1;
    return tree;
  }
  
  index=conf.indexOf(var);
  if (index>-1) // if var is in conf
    tree = getChild(conf.getValue(index)).restrict(conf);
  else { 
    tree = new MultipleTree();
    tree.var = var;
    tree.label = FULL_NODE;
    // Se hace en MultipleTree() tree.value = 0.0;
    // Igual tree.secondValue = 0.0;
    // igual tree.leaves = 0;
      
    nv = child.size();
    for (i=0 ; i<nv ; i++) {
      tree2 = ((MultipleTree)
	       child.elementAt(i)).restrict(conf);
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
 * @return a new <code>MultipleTree</code> consisting of the restriction of the
 * current tree to the values of <code>Configuration conf</code>.
*/

/*public MultipleTree restrict(Configuration conf) {
 
  MultipleTree tree;
  FiniteStates variable;
  int i, v;

  tree = new MultipleTree();
  
  if (conf.size() == 0)
    tree = copy();
  else {
    variable = conf.getVariable(0);
    v = conf.getValue(0);
    tree = restrict(variable,v);
  
    for (i=1 ; i<conf.size() ; i++) {
      variable = conf.getVariable(i);
      v = conf.getValue(i);
      tree = tree.restrict(variable,v);
    }
  }
  
  return tree;
}
*/

/**
 * Computes the addition of all the values in the tree,
 * considering the second values.
 * @param treeSize size of the fully expanded tree.
 * @return the addition computed.
 */

public double conditionalSum(long treeSize) {
 
  double s = 0.0;
  int i, nv;
  long newSize;
  
  if (label==PROBAB_NODE)
    s = (double)treeSize * secondValue;
  else {
    nv = var.getNumStates();
    newSize = treeSize / nv;
    for (i=0 ; i<nv ; i++)
      s += ((MultipleTree)child.elementAt(i)).conditionalSum(newSize);
  }
  
  return s;
}

/**
 * Computes the addition of all the products of
 * first and second values in the tree
 * @param treeSize size of the fully expanded tree.
 * @return the addition computed.
 */

public double conditionalProdSum(long treeSize) {
 
  double s = 0.0;
  int i, nv;
  long newSize;
  
  if (label==PROBAB_NODE)
    s = (double)treeSize * value * secondValue;
  else {
    nv = var.getNumStates();
    newSize = treeSize / nv;
    for (i=0 ; i<nv ; i++)
      s += ((MultipleTree)child.elementAt(i)).conditionalProdSum(newSize);
  }
  
  return s;
}

/**
 * Computes the mean of the second values in the tree.
 * @return the mean.
 */

public double conditionalAverage() {

  double av = 0.0;
  int i, nv;
  
  if (label == PROBAB_NODE)
    av = secondValue;
  else {
    nv = var.getNumStates();
    for (i=0 ; i<nv ; i++)
      av += ((MultipleTree)child.elementAt(i)).conditionalAverage();
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

public boolean conditionalPrune(double limit, long oldSize,
				double globalSum[],
				long numberDeleted[]) {

  long newSize;
  int i, numberChildren;
  MultipleTree ch;
  double pr, pr2, maxvalue, minvalue, sum2=0.0, sum = 0.0, sum3=0.0, entropy = 0.0, info;
  double normalization;
  Double aux;
  boolean bounded = true, childBounded; 
  
  
  
  numberChildren = var.getNumStates();
  
  newSize = oldSize / numberChildren;
  
  
  
  
  for (i=0 ; i<numberChildren ; i++) {
    ch = (MultipleTree)child.elementAt(i);
    
    if (ch.getLabel()==PROBAB_NODE) {
       pr = ch.getProb();
       pr2 = ch.getSecondValue();
       sum2 += pr2;
       sum3 += pr*pr2;
       sum += pr;
       if (pr > 0.0) {entropy += (pr*pr2 * Math.log(pr));}
    }
    else {
      childBounded = ch.conditionalPrune(limit,newSize,
					 globalSum,numberDeleted);

      if (!childBounded)
	bounded = false;

      if (bounded) {
	ch = (MultipleTree)child.elementAt(i);
	pr = ch.getProb();
	pr2 = ch.getSecondValue();
	sum2 += pr2;
	sum += pr;
	sum3 +=pr*pr2;
	if (pr > 0.0) {entropy += (pr*pr2 * Math.log(pr));}
      }
    }
  }
  
  if (bounded) {
     normalization= globalSum[0];
    if (sum<=0.0)
      info = 0.0;
    else {
      if (normalization > 0.0)
	{info = ( 1.0 / normalization) * ( newSize*entropy - newSize*sum3*Math.log(sum/numberChildren)+
		normalization * Math.log( (normalization - sum3*newSize + sum*sum2*newSize/numberChildren)/normalization));
	}
      else { if(sum2==0.0) {info=0.0;} else {info=1E10;}}
	}
      
     //System.out.println("Limit "+limit+" Entropy "+info);
    if (info<=limit) {
      pr = average();
      pr2 = conditionalAverage(); 
      maxvalue = maximum();
      minvalue = minimum();
      numberDeleted[0] += numberChildren-1;
      assignProb(pr);
      assignSecondValue(pr2);
      assignMax(maxvalue);
      assignMin(minvalue);
      child = new Vector();
      
       globalSum[0] = normalization - sum3*newSize + newSize*sum*sum2/numberChildren;
     
    }
    else
      bounded = false;
  }

  return bounded;
}

/**
 * Bounds the tree by substituting nodes whose children are
 * leaves by the average of them. This is done for nodes
 * with an information value lower than a given threshold.
 * The difference with conditionalPrune is he measure of
 * conditional information, which is simpler.
 * @param limit the infromation threshold for pruning.
 * @param oldSize size of this tree if it were complete.
 * @param globalSum the addition of the original potential.
 * @param numberDeleted an array with a single value storing
 * the number of deleted leaves.
 */

public boolean conditionalPruneSimple(double limit, long oldSize,
				double globalSum[],
				long numberDeleted[]) {

  long newSize;
  int i, numberChildren;
  MultipleTree ch;
  double pr, pr2, maxvalue, minvalue,  sum = 0.0, sum2=0.0, entropy = 0.0, info;
  double normalization;
  Double aux;
  boolean bounded = true, childBounded; 
  
  
  
  numberChildren = var.getNumStates();
  
  newSize = oldSize / numberChildren;
  
  
  
  
  for (i=0 ; i<numberChildren ; i++) {
    ch = (MultipleTree)child.elementAt(i);
    
    if (ch.getLabel()==PROBAB_NODE) {
       pr = ch.getProb();
       pr2 = ch.getSecondValue();
       sum2 += pr2;
       sum += pr;
       if (pr > 0.0) {entropy += (pr * Math.log(pr));}
    }
    else {
      childBounded = ch.conditionalPruneSimple(limit,newSize,
					 globalSum,numberDeleted);

      if (!childBounded)
	bounded = false;

      if (bounded) {
	ch = (MultipleTree)child.elementAt(i);
	pr = ch.getProb();
	pr2 = ch.getSecondValue();
	sum2 += pr2;
	sum += pr;
	if (pr > 0.0) {entropy += (pr * Math.log(pr));}
      }
    }
  }
  
  if (bounded) {
     normalization= globalSum[0];
    if (sum<=0.0)
      info = 0.0;
    else {
      if (normalization > 0.0)
	{info = ( 1.0 / normalization) * ( newSize*entropy - newSize*sum*Math.log(sum/numberChildren))*(sum2/numberChildren);
	}
      else { if(sum2==0.0) {info=0.0;} else {info=1E10;}}
	}
      
     //System.out.println("Limit "+limit+" Entropy "+info);
    if (info<=limit) {
      pr = average();
      pr2 = conditionalAverage(); 
      maxvalue = maximum();
      minvalue = minimum();
      numberDeleted[0] += numberChildren-1;
      assignProb(pr);
      assignSecondValue(pr2);
      assignMax(maxvalue);
      assignMin(minvalue);
      child = new Vector();
      
 
     
    }
    else
      bounded = false;
  }

  return bounded;
}


/**
 * Combines two trees.
 * To be used as a static function.
 * @param tree1 a MultipleTree.
 * @param tree2 a MultipleTree.
 * @return a new MultipleTree resulting from combining tree1 and tree2.
*/

public static MultipleTree combine(MultipleTree tree1, MultipleTree tree2) {

  MultipleTree tree, tree3, tree4;
  int i, nv;
  double pr;


  if (tree1.getLabel()==PROBAB_NODE) { // Probability node.
    if (tree2.getLabel()==PROBAB_NODE) {
      pr = tree1.getProb() * tree2.getProb();
      tree = new MultipleTree();
      tree.assignProb(pr);
      tree.assignMax(tree1.getMax() * tree2.getMax());
      tree.assignMin(tree1.getMin() * tree2.getMin());
      tree.leaves = 1;
    }
    else {
      tree = new MultipleTree();
      tree.var = tree2.getVar();
      tree.label = FULL_NODE;
      tree.leaves = 1;
      
      nv=tree2.getChild().size();
      for (i=0 ; i<nv ; i++) {
	tree3 = MultipleTree.combine(tree1,tree2.getChild(i));
	tree.insertChild(tree3);
	tree.leaves += tree3.leaves;
      }
    }
  }
  else {
    tree = new MultipleTree();
    tree.var = tree1.getVar();
    tree.label = FULL_NODE;
    tree.leaves = 0;

    nv = tree1.getChild().size();
    for (i=0 ; i<nv ; i++) {
      tree3 = tree2.restrict(tree1.getVar(),i);
      tree4 = MultipleTree.combine(tree1.getChild(i),tree3);
      tree.insertChild(tree4);
      tree.leaves += tree4.leaves;
    }
  }

  return tree;
}


/**
 * Removes variable variable by summing over all its values.
 * @param variable a FiniteStates variable.
 * @return a new MultipleTree with the result of the operation.
*/

public MultipleTree multiAddVariable(FiniteStates variable) {

  MultipleTree tree, treeH;
  int i, nv, ns;
  
  
  if (label==PROBAB_NODE) {
    ns = variable.getNumStates();
    tree = new MultipleTree();
    tree.assignProb(value * ns);
    tree.assignMax(max * ns);
    tree.assignMin(min * ns);
  }
  else {
    if (var==variable)
      tree = multiAddChildren();
    else {
      tree = new MultipleTree();
      tree.var = var;
      tree.label = FULL_NODE;
      tree.leaves = 0;
      
      nv = getChild().size();
      for (i=0 ; i<nv ; i++) {
	treeH = ((MultipleTree)child.elementAt(i)).restrict(var,i).multiAddVariable(variable);
	tree.insertChild(treeH);
	tree.leaves += treeH.leaves;
      }
    }
  }
  
  return tree;
}


/**
 * @return a new MultipleTree equal to the addition of all the
 * children of the current MultipleTree.
*/

public MultipleTree multiAddChildren() {
 
  MultipleTree tree;
  int i, nv;
  
  tree = (MultipleTree)child.elementAt(0);
  
  nv = child.size();
  for (i=1 ; i<nv ; i++)
    tree = tree.multiAdd((MultipleTree)child.elementAt(i));

  return tree;
}


/**
 * Adds the argument tree to this.
 * @param tree a MultipleTree.
 * @return a new MultipleTree with the addition of tree and
 * the current MultipleTree.
*/

public MultipleTree multiAdd(MultipleTree tree) {
 
  MultipleTree tree1, tree2, treeH;
  int i, nv;
  
  if (label==PROBAB_NODE) {
    if (tree.getLabel()==PROBAB_NODE) { /* If both are probabilities */
      tree1 = new MultipleTree();
      tree1.assignProb(value + tree.getProb());
      tree1.assignMax(max + tree.getMax());
      tree1.assignMin(min + tree.getMin());
    }
    else {
      tree1 = new MultipleTree();
      tree1.var = tree.getVar();
      tree1.leaves = 0;
      tree1.label = FULL_NODE;

      nv = tree.getChild().size();
      tree2 = new MultipleTree();
      tree2.assignProb(value);
      tree2.assignMax(max);
      tree2.assignMin(min);
      for (i=0 ; i<nv ; i++) {
	treeH = ((MultipleTree)tree.child.elementAt(i)).restrict(tree.getVar(),i);
	treeH = treeH.multiAdd(tree2);
	tree1.insertChild(treeH);
	tree1.leaves += treeH.leaves;
      }
    }
      
  }
  else {
    tree1 = new MultipleTree();
    tree1.var = var;
    tree1.leaves = 0;
    tree1.label = FULL_NODE;
    
    nv = getChild().size();
    
    for (i=0 ; i<nv ; i++) {
      treeH = ((MultipleTree)child.elementAt(i)).restrict(var,i);
      treeH = treeH.multiAdd(tree.restrict(var,i));
      tree1.insertChild(treeH);
      treeH.leaves += tree1.leaves;
    }
  }
    
  return tree1;
}


/**
 * Gets the list of variables in the tree.
 * @return the list of nodes.
 */

public NodeList getVarList() {
 
  NodeList list;
  MultipleTree tree;
  int i;
  
  list = new NodeList();
  
  if (label!=FULL_NODE)
    return list;
  
  list.insertNode(var);
  
  for (i=0 ; i<var.getNumStates() ; i++) {
    tree = getChild(i);
    list.merge(tree.getVarList());
  }
  
  return list;
}


/**
 * @return a copy of the tree.
 */

public MultipleTree copy() {

  MultipleTree tree, tree2;
  int i, nv;
  
  tree = new MultipleTree();
  tree.var = var;
  tree.label = FULL_NODE;
  tree.value = 0.0;
  tree.leaves = leaves;

  if (label!=PROBAB_NODE) { // If it is not a probability,

    nv = child.size();
      
    for (i=0 ; i<nv ; i++) {
      tree2 = getChild(i).copy();
      tree.child.addElement(tree2);
    }
  }
  else {
    tree.assignProb(value);
    tree.assignSecondValue(secondValue);
    tree.assignMax(max);
    tree.assignMin(min);
  }
  return tree;
}


/**
 * Saves the tree to a file.
 * @param p the PrintWriter where the tree will be written.
 * @param j a tab factor (number of blank spaces befor a child
 * is written).
 */

public void save(PrintWriter p,int j) {
  
  int i, l, k;
  
  if (label==PROBAB_NODE)
    p.print(value+"; ["+min+" , "+max+"]\n");
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
  
  if (label==PROBAB_NODE)
    System.out.print(value+"; ["+min+" , "+max+"]\n");
  else {
    System.out.print("case "+var.getName()+" {\n");
    
    for(i=0; i< child.size(); i++) {
      for (l=1;l<=j;l++)
	System.out.print(" ");
      
      System.out.print( var.getState(i) + " = ");
      getChild(i).print(j+10);
    }
    
    for (i=1;i<=j;i++)
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
  
  if (label == PROBAB_NODE) {
    value /= total;
    max /= total;
    min /= total;
  }
  else {
    nv = var.getNumStates();
    for (i=0 ; i<nv ; i++)
      ((MultipleTree)child.elementAt(i)).normalizeAux(total);
  }
}


/**
 * Auxiliar to the previous one.
 */

public void normalizeAux(double total) {
 
  int i, nv;
  
  
  if (label == PROBAB_NODE) {
    value /= total;
    max /= total;
    min /= total;
  }
  else {
    nv = var.getNumStates();
    for (i=0 ; i<nv ; i++)
      ((MultipleTree)child.elementAt(i)).normalizeAux(total);
  }  
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
  
  if (label==PROBAB_NODE)
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
 * Computes the information of a variable within a tree.
 * @param variable a FiniteStates variable.
 * @param potentialSize maximum size of the potential containing
 * the tree.
 * @return the value of information of variable.
 */

public double information(FiniteStates variable,
			  long potentialSize) {
 
  MultipleTree tree;
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

} // End of class

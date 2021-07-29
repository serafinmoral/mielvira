import java.io.*;
import java.util.Vector;
import java.util.Date;
import java.util.Hashtable;
import JoinTree;
import Triangulation;
import Relation;
import RelationList;
import Propagation;
import MultipleTree;

/**
 * Class BinaryTreeProp.
 * Implements an approximate propagation method over a
 * binary join tree. Potentials are represented by means of
 * MultipleTrees, allowing to compute, during the propagation,
 * an interval where the true probability lies.
 *
 * @since 22/11/99
 */


public class BinaryTreeProp extends Propagation {
  
/**
 * The binary join tree.
 */
  
JoinTree binTree;

/**
 * A vector containing the maximum size of a potential
 * at each stage. The number of stages will be equal to
 * the size of this array.
 */

private int[] maximumSize;

/**
  * A vector of boolean values. Each value <code>sortAndBound[i]</code>
  * tell us if the algorithm carry out a <code>limitSortAndBound</code>
  * at step <code>i</code>
  */
private boolean[] sortAndBound;

/**
 * The number of propagation stages.
 */

private int stages;

/**
 * The current stage.
 */

private int currentStage;

/**
 * The information limit for prunning.
 */

private double limitForPrunning;

/**
 * Method for computing the information value used for prunning.
 * 0 for conditional prunning or 1 for max-min prunning.
 */

private int method;


/**
 * Program for performing experiments.
 * The arguments are as follows.
 * <ol>
 * <li> Input file: the network.
 * <li> Output file results:
 * <li> Output error file, where the error and computing time
 *    of each experiment will be stored.
 * <li> File with exact results.
 * <li> The method for approximating (0|1).
 * <li> A double; limit for prunning.
 * <li> An integer. The number of stages. After it, an integer
 *    for each stage indicating the maximum potential size
 *    at that stage.
 * <li> File with instantiations.
 * </ol>
 * The last argument can be omitted. In that case, it will
 * be considered that no observations are present.
 */

public static void main(String args[]) throws ParseException, IOException {

  Bnet b;
  Evidence e;
  FileInputStream networkFile, evidenceFile;
  BinaryTreeProp propagation;
  int i, m, nstages;
  int[] ls;
  double lp;
  boolean[] sortAndBound;
  
  double[] errors;
  double g, mse, timePropagating;
  Date date;
  FileWriter f;
  PrintWriter p;
  
  if (args.length<7) {
    System.out.print("Too few arguments. Arguments are: ElviraFile");
    System.out.print(" OutputFile OutputErrorFile InputExactResultsFile");
    System.out.print(" MethodForPropagating(0|1) LimitForPrunning NumberStages");
    System.out.println(" MaxSizeInStage1 MaxSizeInStage2 ... ");
    System.out.println(" SortAndBoundInStage1(true|false) SortAndBoundInStage2 ... [EvidenceFile]");
}
  else {
    nstages = (Integer.valueOf(args[6])).intValue();
    if (args.length<(2*nstages)+7){
      System.out.print("Too few arguments. Arguments are: ElviraFile");
      System.out.print(" OutputFile OutputErrorFile InputExactResultsFile");
      System.out.print(" MethodForPropagating(0|1) LimitForPrunning NumberStages");
      System.out.println(" MaxSizeInStage1 MaxSizeInStage2 ... ");
      System.out.println(" SortAndBoundInStage1(true|false) SortAndBoundInStage2 ... [EvidenceFile]");
    }
    else {
      networkFile = new FileInputStream(args[0]);
      b = new Bnet(networkFile);
      
      if (args.length==2*nstages+8) {
	evidenceFile= new FileInputStream(args[2*nstages+7]);
	e = new Evidence(evidenceFile,b.getNodeList());
      }
      else
	e = new Evidence();
      
//args[i+7+nstages]
      ls = new int[nstages];
      sortAndBound=new boolean[nstages];
      for (i=0 ; i<nstages ; i++) {
	ls[i] = (Integer.valueOf(args[i+7])).intValue();
	sortAndBound[i] = (Boolean.valueOf(args[i+7+nstages])).booleanValue();
/*	System.out.println((Boolean.valueOf(args[i+7+nstages]).toString()));
	System.out.println((Boolean.valueOf(args[i+7+nstages])).booleanValue());
	System.out.println("sortAndBound["+i+"]:"+sortAndBound[i]);*/
      }
			  
	
      lp = (Double.valueOf(args[5])).doubleValue();
      m = (Integer.valueOf(args[4])).intValue();

      propagation = new BinaryTreeProp(b,e,lp,ls,sortAndBound,m);

      date = new Date();
      timePropagating = (double)date.getTime();
      propagation.propagate(args[1]/*,args[3], args[2]*/);
      
      date = new Date();
      timePropagating = ((double)date.getTime()-timePropagating) / 1000;
      
      System.out.println("Reading exact results");
      propagation.readExactResults(args[3]);
      System.out.println("Exact results read");
      
      System.out.println("Computing errors");
      errors = new double[2];
      propagation.computeError(errors);
      
      g = errors[0];
      mse = errors[1];
      
      f = new FileWriter(args[2]);
      p = new PrintWriter(f);
      
      p.println("Time propagating (secs) : "+timePropagating);
      p.println("G : "+g);
      p.println("MSE : "+mse);
      f.close();
      
      System.out.println("Done"); 
    }
  }
}


/**
 * Creates a new propagation.
 * @param b a belief network.
 * @param e an evidence.
 * @param lp the limit for prunning.
 * @param ls the maximum sizes for potentials.
 */

BinaryTreeProp(Bnet b, Evidence e, double lp, int[] ls, boolean[] sortAndBound,
	       int m) {
  
  Triangulation triang;
  RelationList rel1, rel2, ir,irTree;
  NodeList numeration = new NodeList();
  int i;
  PotentialTree pt;
  PotentialMTree pmt;
  Relation newRel,rel;
  
  observations = e;
  network = b;
  positions = new Hashtable();
  
/*  binTree = new JoinTree(b); //Como estaba antes
  ir = getInitialRelations();     
  binTree.Leaves(ir); */
  
  
  binTree = new JoinTree(b,e);
  irTree = getInitialRelations();
  irTree.restrictToObservations(observations);
  for(i=0;i<irTree.size();i++) {
    ((PotentialTree)irTree.elementAt(i).getValues()).limitBound(lp);
  }

  ir=new RelationList();
  for(i=0;i<irTree.size();i++) {
    rel = irTree.elementAt(i);
    newRel = new Relation();
    newRel.setVariables(rel.getVariables().copy());
    pt=(PotentialTree)rel.getValues();
    pmt = new PotentialMTree(pt);
    newRel.setValues(pmt);
    ir.insertRelation(newRel);
  }


  //System.out.println("RELACIONES A INSERTAR EN LOS CLIQUES");
  //ir.print();
     
/*  System.out.println("Antes de Leaves");
  binTree.setLabels();
  binTree.display();*/
  
  binTree.Leaves(ir);

  /*System.out.println("Antes de binTree");
  binTree.setLabels();
  binTree.display();  */
  
  binTree.binTree();
  
  /*System.out.println("Despues de binTree");
  binTree.setLabels();
  binTree.display();*/

  setMaximumSizes(ls);
  setSortAndBound(sortAndBound);
  setLimitForPrunning(lp);
  setMethod(m);
  binTree.setLabels();
}


/**
 * Sets the limit for prunning.
 * @param lp the information limit for prunning.
 */

public void setLimitForPrunning(double lp) {
  
 limitForPrunning = lp;
}


/**
 * Sets the maximum sizes of multiple trees at each stage of the algorithm 
 * of propagation.
 * @param ls an array with the maximum sizes.
 */

private void setMaximumSizes(int[] ls) {
 
  int i;
  
  stages = ls.length;
  maximumSize = new int[stages];
  
  for (i=0 ; i<stages ; i++)
    maximumSize[i] = ls[i];
}

/**
 * Sets <code>sortAndBound</code>  at each stage of the algorithm of propagation.
 * @param sAB an array the values to set in <code>sortAndBound</code>.
 */
private void setSortAndBound(boolean[] sAB) {
 
  int i;
  
  stages = sAB.length;
  sortAndBound = new boolean[stages];
  
  for (i=0 ; i<stages ; i++)
    sortAndBound[i] = sAB[i];  
}

/**
 * Sets the method of propagation.
 * 0 for conditional prunning or 1 for max-min prunning.
 * @param m the method.
 */

public void setMethod(int m) {
  
  method = m;
}


/**
 * Initializes all the messages to 1, except those corresponding
 * to leaf nodes, which will contain the potential in the
 * node.
 */
  
public void initMessages() {
 
  RelationList ir;
  Relation r, r2, message, otherMessage;
  int i, j;
  PotentialMTree pot, pot2;
  Vector leaves;
  NodeJoinTree node, otherNode;
  NeighbourTree neighbour, nt;
  NeighbourTreeList ntl;
  NodeList nl1, nl2;
  

  for (i=0 ; i<binTree.getJoinTreeNodes().size() ; i++) {
    // Set the potentials in the cliques to 1.
    node = binTree.elementAt(i);    
    r = node.getNodeRelation();
    if ((r.getValues() == null) || (!r.getValues().getClass().getName().equals("PotentialMTree"))) {
      pot = new PotentialMTree(r.getVariables());
      pot.setTree(MultipleTree.unitTree());
      r.setValues(pot);
    }
    
    // Now set the messages
    ntl = node.getNeighbourList();
    for (j=0 ; j<ntl.size() ; j++) {
      nt = ntl.elementAt(j);
      r = nt.getMessage();
      pot = new PotentialMTree(r.getVariables());
      pot.setTree(MultipleTree.unitTree());
      r.setValues(pot);
      pot = new PotentialMTree(r.getVariables());
      pot.setTree(MultipleTree.unitTree());
      r.setOtherValues(pot);
    }
  }
  
  
  leaves = binTree.getLeaves();
  //System.out.println("Number of leaves = "+leaves.size());
  
  // Compute the potentials for cliques in leaves.
/*  
  ir = getInitialRelations();
  
  if (ir.size()>leaves.size())
    System.out.println("Warning: more initial relations than leaves");
  
  for (i=0 ; i<ir.size() ; i++) {
    r = ir.elementAt(i);
    nl1 = r.getVariables();
    
    for (j=0 ; j<leaves.size() ; j++) {
      node = (NodeJoinTree)leaves.elementAt(i);
      r2 = node.getNodeRelation();
      nl2 = r2.getVariables();
      if (nl1.equals(nl2)) {
	pot2 = ((PotentialMTree)r.getValues()).copy();
	r2.setValues(pot2);
      }
    }
  }
*/
  
  // Now, compute the messages corresponding to leaves.

  for (i=0 ; i<leaves.size() ; i++) {
    node = (NodeJoinTree)leaves.elementAt(i);
    pot = (PotentialMTree)node.getNodeRelation().getValues();
    neighbour = node.getNeighbourList().elementAt(0);
    message = neighbour.getMessage();
    pot2 = (PotentialMTree)(pot.marginalizePotential(
                            message.getVariables().toVector()));

    // The outgoing message is stored in values.
    // The incoming message is stored in otherValues.

    message.setValues(pot2);
    
    // Now update the messages in the opposite direction
    otherNode = neighbour.getNeighbour();
    otherMessage = otherNode.getNeighbourList().getMessage(node);
    otherMessage.setOtherValues(pot2);
  }
}

/**
 * Carries out a propagation.
 *
 * @param exactFile the name of the file with the exact results.
 * @param resultFile the name of the file where the errors will
 *        be stored.
 */

public void propagate(String resultFile
		      /*,String exactFile, String errorFile
		*/) throws ParseException, IOException {

  NodeJoinTree root;
  //other;
 // NeighbourTreeList list;
  NodeList variables;
  //Vector aCopy;
 // Date date;
 // double[] errors;
 // double g, mse, timePropagating;
  //FileWriter f;
  //PrintWriter p;
  //Hashtable positions;
  int i;

  // Necessary for identifying the nodes during the
  // message passing
  binTree.setLabels();
  
  // Positions of the variables in the vector of results
 // positions = new Hashtable();
  
  // Read the exact results.
  //System.out.println("Reading exact results");
  //readExactResults(exactFile);
  //System.out.println("Exact results read");

  // Initialize messages
  System.out.println("Initializing messages");
  initMessages();

  // Insert observations right here
  
  System.out.println("Starting propagation");
  
  // Perform the propagation
  
  //date = new Date();
 // timePropagating = (double)date.getTime();
  
  root = binTree.elementAt(0);
  
  //list = root.getNeighbourList();
//  System.out.println("NAVIGATEUP");
//binTree.display();  
  
  currentStage=0;
  if(stages>0) {
//System.out.println("ETAPA ACTUAL: "+currentStage);
    navigateUp(root);
    stages--;
    currentStage++;
  }

//binTree.display();  
//  System.out.println("NAVIGATEDOWNUP");  
  while(stages>2) {
//System.out.println("ETAPA ACTUAL: "+currentStage);    
      navigateDownUp(root);
      stages-=2;
      currentStage+=2;
  }

 // System.out.println("NAVIGATEDOWNFINAL");
//binTree.display();
//System.out.println("ETAPA ACTUAL: "+currentStage);
  if(stages==1){
    navigateDown(root);
    currentStage++;
  }
  else { // stages == 2
    navigateDownUpForcingDown(root);
    currentStage+=2;     
  }
    
  // End of propagation
  System.out.println("Propagation done");
  
  // Obtain the marginals for each single variable
  System.out.println("Computing marginals");
  
  // CAMBIADO por acu aCopy = computeMarginals(positions);
  computeMarginals(); 
  System.out.println("Done");
  
  saveResults(resultFile);
}
	       
/**
 * Send messages from root (sender) to leaves, and then from leaves
 * to root.
 * The method do not navigate throw a branch if the message in the opposite
 * direction (<code>getOtherValues()</code>) is exact.
 * @param sender the NodeJoinTree that sends the request.
 * @param recipient the NodeJoinTree that receives the request.
 */

private void navigateDownUp(NodeJoinTree sender) {
  NeighbourTreeList list;
  NodeJoinTree other;
  int i;
  
  list = sender.getNeighbourList();
  for (i=0 ; i<list.size() ; i++) {
    other = list.elementAt(i).getNeighbour();
    // if opposite message is not exact
    if(!((PotentialMTree)(list.elementAt(i).getMessage().getOtherValues())).getExact()) {
      // if previous messages was not exact
      if(!((PotentialMTree)(list.elementAt(i).getMessage().getValues())).getExact())
	sendMessage(sender,other,false);
      navigateDownUp(sender,other);
    }
  }
}	       
	       
/**
 * Sends messages from root (sender) to leaves, and then from leaves
 * to root, through the branch <code>recipient</code>.
 * This method do not navigate throw a branch if the message in the opposite
 * direction (<code>getOtherValues()</code>) is exact.
 * @param sender the NodeJoinTree that sends the request.
 * @param recipient the NodeJoinTree that receives the request.
 */

private void navigateDownUp(NodeJoinTree sender, NodeJoinTree recipient) {
  NeighbourTreeList list;
  NodeJoinTree other;
  int i;
    
  // Nodes to which the message will be sent downwards.
  list = recipient.getNeighbourList();

  for (i=0 ; i<list.size() ; i++) {
    other = list.elementAt(i).getNeighbour(); 
    if (other.getLabel() != sender.getLabel()) {
      // if opposite message is not exact
      if(!((PotentialMTree)(list.elementAt(i).getMessage().getOtherValues())).getExact()) {  
	// if previous message was not exact
	if(!((PotentialMTree)(list.elementAt(i).getMessage().getValues())).getExact())
	  sendMessage(recipient,other,false);	
	navigateDownUp(recipient,other);
      }
    }
  }
  // if opposite message is not exact
  if(!((PotentialMTree)(list.getMessage(sender).getOtherValues())).getExact())  
    sendMessage(recipient,sender,true);
}

/**
 * Send messages from root (sender) to leaves, and then from leaves
 * to root.
 * When it navigates  down,  if the message in the opposite
 * direction (<code>getOtherValues()</code>) in a branch is exact, then it will 
 * continue with navigateDown by that branch (and not doing the ascendip step).
 * @param sender the NodeJoinTree that sends the request.
 * @param recipient the NodeJoinTree that receives the request.
 */

private void navigateDownUpForcingDown(NodeJoinTree sender) {
  NeighbourTreeList list;
  NodeJoinTree other;
  int i;
  
  list = sender.getNeighbourList();
  for (i=0 ; i<list.size() ; i++) {
    other = list.elementAt(i).getNeighbour();

    // if opposite message is not exact
    if(!((PotentialMTree)(list.elementAt(i).getMessage().getOtherValues())).getExact()){
      // if previous message was not exact
      if(!((PotentialMTree)(list.elementAt(i).getMessage().getValues())).getExact())
	sendMessage(sender,other,false);
      navigateDownUpForcingDown(sender,other);
    }
    else { 
      // if previous message was not exact
      if(!((PotentialMTree)(list.elementAt(i).getMessage().getValues())).getExact())
	sendMessage(sender,other,false);
      navigateDown(sender,other);
    }
  }
}	       
	       
/**
 * Sends messages from root (sender) to leaves, and then from leaves
 * to root, through the branch <code>recipient</code>.
 * This method do not navigate throw a branch if the message in the opposite
 * direction (<code>getOtherValues()</code>) is exact.
 * @param sender the NodeJoinTree that sends the request.
 * @param recipient the NodeJoinTree that receives the request.
 */

private void navigateDownUpForcingDown(NodeJoinTree sender, NodeJoinTree recipient) {
  NeighbourTreeList list;
  NodeJoinTree other;
  int i;
    
  // Nodes to which the message will be sent downwards.
  list = recipient.getNeighbourList();

  for (i=0 ; i<list.size() ; i++) {
    other = list.elementAt(i).getNeighbour(); 
    if (other.getLabel() != sender.getLabel()) {
      // if opposite message is not exact
      if(!((PotentialMTree)(list.elementAt(i).getMessage().getOtherValues())).getExact()) {      
	if(!((PotentialMTree)(list.elementAt(i).getMessage().getValues())).getExact())
	  sendMessage(recipient,other,false);	
	navigateDownUpForcingDown(recipient,other);
      }
      else {
	if(!((PotentialMTree)(list.elementAt(i).getMessage().getValues())).getExact())
	  sendMessage(recipient,other,false);	
	navigateDown(recipient,other);
      }	
    }
  }
  sendMessage(recipient,sender,true);
}


/**
 * Send messages from leaves to root (sender).
 * @param sender the NodeJoinTree that sends the request.
 * @param recipient the NodeJoinTree that receives the request.
 */

private void navigateUp(NodeJoinTree sender) {
  NeighbourTreeList list;
  NodeJoinTree other;
  int i;
  
  list = sender.getNeighbourList();
  for (i=0 ; i<list.size() ; i++) {
    other = list.elementAt(i).getNeighbour();
    //sendMessage(sender,other,false); Estaba mal
    navigateUp(sender,other);
  }
}	       

/**
 * Send messages from leaves to root (sender) through the branch recipient
 * @param sender the NodeJoinTree that sends the request (root node).
 * @param recipient the NodeJoinTree that receives the request.
 */

private void navigateUp(NodeJoinTree sender, NodeJoinTree recipient) {

  NeighbourTreeList list;
  NodeJoinTree other;
  int i;

  // Nodes to which the message will be sent downwards.
  list = recipient.getNeighbourList();

  for (i=0 ; i<list.size() ; i++) {
    other = list.elementAt(i).getNeighbour();

    if (other.getLabel() != sender.getLabel()) {
      
//      sendMessage(recipient,other);
      
      navigateUp(recipient,other);
    }
  }
  
  sendMessage(recipient,sender,false);
}

/**
 * Send messages from root (sender) to leaves.
 * @param sender the NodeJoinTree that sends the request.
 * @param recipient the NodeJoinTree that receives the request.
 */

private void navigateDown(NodeJoinTree sender) {
  NeighbourTreeList list;
  NodeJoinTree other;
  int i;
  
  list = sender.getNeighbourList();
  for (i=0 ; i<list.size() ; i++) {
    other = list.elementAt(i).getNeighbour();
    sendMessage(sender,other,false);
    navigateDown(sender,other);
  }
}

/**
 * Send messages from root (sender) to leaves through the brach recipient.
 * @param sender the NodeJoinTree that sends the request (root node).
 * @param recipient the NodeJoinTree that receives the request.
 */

private void navigateDown(NodeJoinTree sender, NodeJoinTree recipient) {

  NeighbourTreeList list;
  NodeJoinTree other;
  int i;

  // Nodes to which the message will be sent downwards.
  list = recipient.getNeighbourList();

  for (i=0 ; i<list.size() ; i++) {
    other = list.elementAt(i).getNeighbour();

    if (other.getLabel() != sender.getLabel()) {
      
      sendMessage(recipient,other,false);
      
      navigateDown(recipient,other);
    }
  }
  
//  sendMessage(recipient,sender);
}


/**
 * Computes the error of an estimation. The exact results are
 * stored in the instance variable exactResult, while the
 * obtained results are given as an argument.
 * @param obtained a vector with the obtained results.
 * @param errors an array where the errors will be stored. In
 * the first position, the g-error. In the second one, the mean
 * square error.
 * @param positions a Hashtable with the positions of the
 * results of each variable.
 */

/*public void computeError(Vector obtained, double[] errors,
			 Hashtable positions) {
 
  int counter, i, j, nv, pos;
  double c, cAcum, g, gAcum, v1, v2;
  Relation rel;
  FiniteStates var;
  PotentialTable exactPot, approxPot;
  
  gAcum = 0.0;
  cAcum = 0.0;
  counter = 0;
  
  for (i=0 ; i<exactResults.size() ; i++) {
    rel = exactResults.elementAt(i);
    exactPot = (PotentialTable)rel.getValues();
    var = (FiniteStates)exactPot.getVariables().elementAt(0);
    
    if (observations.isObserved(var))
      continue;
    
    pos = ((Integer)positions.get(var)).intValue();
    
    approxPot = (PotentialTable)obtained.elementAt(pos);
    
    g = 0.0;
    c = 0.0;
    nv = var.getNumStates();
    
    for (j=0 ; j<nv ; j++) {
      v1 = approxPot.values[j];
      v2 = exactPot.values[j];
      c += Math.pow(v2-v1,2);
      
      if ((v2==0.0)||(v2==1.0))
	continue;
      
      g += Math.pow(v1-v2,2) / (v2 * (1-v2));
    }
    
    g = Math.sqrt(g/(double)nv);
    c /= (double)nv;
    gAcum += (g * g);
    cAcum += c;
    counter++;
  }
  
  gAcum = Math.sqrt(gAcum); // g-error.
  cAcum /= (double)counter; // mean squared error

  errors[0] = gAcum;
  errors[1] = cAcum;
}
*/

/**
 * @return the initial relations present in the network.
 */

public RelationList getInitialRelations() {
  
  Relation rel, newRel;
  RelationList list;
  PotentialTree pt;
  PotentialMTree pmt;
  int i;
 
  list = new RelationList();
  
  for (i=0 ; i<network.getRelationList().size() ; i++) {
    rel = (Relation)network.getRelationList().elementAt(i);
    newRel = new Relation();
    newRel.setVariables(rel.getVariables().copy());
    
    if (rel.getValues().getClass().getName().
	equals("PotentialTable"))
      pt = ((PotentialTable)rel.getValues()).toTree();
    else
      pt = (PotentialTree)rel.getValues();
    
  /*  pmt = new PotentialMTree(pt);
    newRel.setValues(pmt);*/
    newRel.setValues(pt);
    
    list.insertRelation(newRel);
  }
  
  return list;
}


/**
 * Computes the marginals after a propagation and put them into the
 * instance variable <code>results</code>. Sets <code>positions</code>
 * for each variable.
 */

public void computeMarginals(/*Hashtable positions*/) {
  
  int i, j, k, nv, pos;
  Vector /*aCopy,*/ leaves, marginal;
  NodeList variables;
  Relation r1, r2;
  PotentialMTree pot;
  PotentialTable table;
  NodeJoinTree temp;
  NodeList l;
  FiniteStates v;
  NeighbourTree nt;
  int posResult;
  
  
  //results = new Vector();
  //aCopy = new Vector();
  leaves = binTree.getLeaves();
  variables = network.getNodeList();
  nv = variables.size();
  temp = new NodeJoinTree();
  
  posResult=0;
  for (i=0 ; i<nv ; i++) {
    v = (FiniteStates)variables.elementAt(i);
    
    j = 0;
    pos = -1;
    while ((pos<0) && (j<leaves.size())) {
      temp = (NodeJoinTree)leaves.elementAt(j);
      l = temp.getNodeRelation().getVariables();
      pos = l.getId(v);
      j++;
    }
    
    if (pos>=0) {
      r1 = temp.getNodeRelation();
      pot = (PotentialMTree)r1.getValues();
      
      for (k=0 ; k<temp.getNeighbourList().size() ; k++) {
	nt = temp.getNeighbourList().elementAt(k);
	r2 = nt.getMessage();
	
	pot = pot.combine((PotentialMTree)r2.getOtherValues());
      }
      marginal = new Vector();
      marginal.addElement(v);
      pot = (PotentialMTree)(pot.marginalizePotential(marginal));
      pot.normalize();
      results.addElement(pot);
      
      //System.out.println("RESULTADO PROPAGACION");
      //pot.print();
      
      // Positions is a hash table storing the position of the
      // potential corresponding to v in the results vector.
      //positions.put(v,new Integer(i));
      //positions = new Hashtable();
      positions.put(v,new Integer(posResult));
      posResult++;
      
      // This one is used for computing the error
     /* CAMBIADO por acu table = new PotentialTable(pot);
      table.normalize();
      aCopy.addElement(table);*/
    }
  }
  //return aCopy;
}


/**
 * Sends a message from a node to another one.
 * Marks the messages as not exact when this method carry out an approximation
 * or one of the input messages are not exact. 
 * The message is computed by combining all the messages inwards
 * the sender but that one comming from the recipient. Then, the
 * result is sorted and bounded conditional to the message
 * going from the recipient to the sender.
 * It is required that the nodes in the tree be labeled.
 * Use the method setLabels if necessary.
 *
 * @param sender the node that send the message.
 * @param recipient the node that receives the message.
 * @param takeNumberNextStage is true if we must take the maximum size of the
 * message with maximumSize[currentStage+1] and is false if we must take
 * the maximun size of the message with maximumSize[currentStage]
 */

public void sendMessage(NodeJoinTree sender, NodeJoinTree recipient,
			boolean takeNumberNextStage) {
  NeighbourTreeList list, auxList;
  NeighbourTree nt;
  PotentialMTree aux, pot, incoming;
  Relation rel, outwards, inwards;
  int i, label;
  Vector separator;
  boolean isExact=true;
  
//  System.out.print("Mensaje de "+sender.getLabel()+" a "+recipient.getLabel());
  
  incoming = new PotentialMTree();
  outwards = new Relation();
  inwards = new Relation();
  separator = new Vector();
  
  aux = (PotentialMTree)sender.getNodeRelation().getValues();
  
  pot = new PotentialMTree();
  pot.setTree(MultipleTree.unitTree());
  
  list = sender.getNeighbourList();
  
  for (i=0 ; i<list.size() ; i++) {
    nt = list.elementAt(i);
    label = nt.getNeighbour().getLabel();
    rel = nt.getMessage();
    
    // Combine the messages coming from the other neighbours
    
    if (label != recipient.getLabel()) { 
      pot = pot.combine((PotentialMTree)rel.getOtherValues());
      if(!((PotentialMTree)rel.getOtherValues()).getExact()) {
	isExact=false;
      }
    }
    else {
      incoming = (PotentialMTree)rel.getOtherValues();
      outwards = rel;
      
      separator =  rel.getVariables().getNodes();
      auxList = recipient.getNeighbourList();
      inwards = auxList.getMessage(sender);
      
    }
  }
  
  // Now combine with the potential in the node.
  
  pot = pot.combine(aux);
  
  pot = (PotentialMTree)(pot.marginalizePotential(separator)); 
  
  pot = pot.conditional(incoming);
  
  pot.conditionalLimitBound(limitForPrunning,method);
  
  pot.setExact(isExact);
  
  if(takeNumberNextStage){
    if(sortAndBound[currentStage+1])
      pot = pot.conditionalSortAndBound(maximumSize[currentStage+1],method);
  }
  else {
    if(sortAndBound[currentStage])
      pot = pot.conditionalSortAndBound(maximumSize[currentStage],method);
  }
  
  // Now update the messages in the join tree.

  pot.conditionalLimitBound(limitForPrunning,method);
  
//System.out.println(" : "+pot.getExact());  
  outwards.setValues(pot);
  inwards.setOtherValues(pot);
}


} // End of class

import java.util.Vector;
import java.util.Hashtable;
import java.util.Random;
import java.util.Date;
import Relation;
import RelationList;
import PairTable;
import ImportanceSampling;
import NodeList;
import PotentialTree;
import java.io.*;


/**
 * Class ImportanceSamplingTree.
 * Implements the importance sampling method of propagation
 * based on approximate node deletion. Using Probability Trees.
 *
 * @since 1/12/99
 */

public class ImportanceSamplingTree extends ImportanceSampling {

double limitForPrunning; // Limit of information for prunning.


/**
 * Program for performing experiments.
 * The arguments are as follows.
 * <ol>
 * <li> A double; limit for prunning.
 * <li> An integer. Maximum size of a potential.
 * <li> An integer. Number of simulation steps.
 * <li> An integer. Number of experiments.
 * <li> Input file: the network.
 * <li> Output error file, where the error and computing time
 *      of each experiment will be stored.
 * <li> File with exact results.
 * <li> File with instantiations.
 * </ol>
 * The last argument can be omitted. In that case, it will
 * be considered that no observations are present.
 */

public static void main(String args[]) throws ParseException, IOException {

  Bnet b;
  Evidence e;
  FileInputStream networkFile, evidenceFile;
  ImportanceSamplingTree propagation;
  int i, ss, nruns, ls;
  double lp;
  
  if (args.length<7)
    System.out.println("Too few arguments");
  else {
    networkFile = new FileInputStream(args[4]);
    b = new Bnet(networkFile);
  
    if (args.length==8) {
      evidenceFile= new FileInputStream(args[7]);
      e = new Evidence(evidenceFile,b.getNodeList());
    }
    else
      e = new Evidence();
  
    lp = (Double.valueOf(args[0])).doubleValue();

    ls = (Integer.valueOf(args[1])).intValue();

    ss = (Integer.valueOf(args[2])).intValue();
    
    nruns = (Integer.valueOf(args[3])).intValue();
    
    propagation = new ImportanceSamplingTree(b,e,lp,ls,ss,nruns);
    
    System.out.println("Reading exact results");
    propagation.readExactResults(args[6]);
    System.out.println("Done");

    propagation.propagate(args[5]);
  }
}


/**
 * Creates an empty object. Necessary for subclass definition.
 */

ImportanceSamplingTree() {
  
}

/**
 * Creates a new propagation with for a given evidence and
 * a given network.
 * @param b a belief netowrk.
 * @param e an evidence.
 */

ImportanceSamplingTree(Bnet b, Evidence e) {

  observations = e;
  network = b;
  positions = new Hashtable(20);
}


/**
 * Creates a new propagation.
 * @param b a belief netowrk.
 * @param e an evidence.
 * @param lp the limit for prunning.
 * @param ls the maximum size for potentials.
 * @param ss the sample size.
 * @param nruns the number of runs.
 */

ImportanceSamplingTree(Bnet b, Evidence e, double lp, int ls,
		       int ss, int nruns) {

  observations = e;
  network = b;
  setLimitSize(ls);
  setLimitForPrunning(lp);
  setSampleSize(ss);
  setNumberOfRuns(nruns);
  positions = new Hashtable(20);
}


/**
 * Sets the limit for prunning.
 * @param epsilon the deviation respect to a uniform distribution.
 */

public void setLimitForPrunning(double epsilon) {
 
  limitForPrunning = 1+((0.5-epsilon) * Math.log(0.5-epsilon) /
			Math.log(2) +
			(0.5+epsilon) * Math.log(0.5+epsilon)/
			Math.log(2));
}


/**
 * @return the initial relations present
 * in the network.
 */

public RelationList getInitialRelations() {
  
  Relation rel, newRel;
  RelationList list;
  int i;
 
  list = new RelationList();
  
  for (i=0 ; i<network.getRelationList().size() ; i++) {
    rel = (Relation)network.getRelationList().elementAt(i);
    newRel = new Relation();
    newRel.setVariables(rel.getVariables().copy());
    
    if (rel.getValues().getClass().getName().
	equals("PotentialTable"))
      newRel.setValues(((PotentialTable)rel.getValues()).toTree());
    else
      newRel.setValues(rel.getValues());
    
    list.insertRelation(newRel);
  }
  
  return list;
}


/**
 * Compute the sampling distributions.
 * There will be a sampling distribution for each
 * variable of interest.
 * The deletion sequence will be stored in the list
 * DeletionSequence. For each variable in that list,
 * its sampling distribution will be at the same position in
 * list SamplingDistributions.
 *
 * Note that observed variables are not included in the deletion
 * sequence, since they need not be simulated.
 */

public void getSamplingDistributions() {

  NodeList notRemoved;
  FiniteStates variableX, variableY;
  Relation rel;
  RelationList currentRelations, tempList;
  PotentialTree pot;
  PairTable pairTable;
  int i, j, k, p, pos, s;


  notRemoved = new NodeList();
  pairTable = new PairTable();
  
  deletionSequence = new NodeList();
  samplingDistributions = new Vector();
  
  s = network.getNodeList().size();
  
  for (i=0 ; i<s ; i++) {
    variableX = (FiniteStates)network.getNodeList().elementAt(i);
    
    if (!observations.isObserved(variableX)) {
      notRemoved.insertNode(variableX);
      pairTable.addElement(variableX);
    }
  }

  currentRelations = getInitialRelations();
  
    
  /* Now restrict the valuations to the obervations */

  if (observations.size() > 0)
    restrictToObservations(currentRelations);
  
  for (i=0 ; i<currentRelations.size() ; i++)
    pairTable.addRelation(currentRelations.elementAt(i));

    
  for (i=notRemoved.size() ; i>0 ; i--) {
    variableX = pairTable.nextToRemove();
    positions.put(variableX,new Integer(i-1));

    notRemoved.removeNode(variableX);
    pairTable.removeVariable(variableX);
    deletionSequence.insertNode(variableX);
    tempList = currentRelations.getRelationsOfAndRemove(variableX);    
    
    rel = tempList.elementAt(0);
    pairTable.removeRelation(rel);
    pot = (PotentialTree)rel.getValues();

    for (j=1 ; j<tempList.size() ; j++) {
      rel = tempList.elementAt(j);
      pairTable.removeRelation(rel);
      pot = pot.combine((PotentialTree)rel.getValues());
    }
    
    samplingDistributions.addElement(pot);
    
    if (i>1) {
      pot = (PotentialTree)pot.addVariable(variableX);
      pot.limitBound(limitForPrunning);
    }
    
    pot = pot.sortAndBound(limitSize);

    for (k=pot.getVariables().size()-1 ; k>=0 ; k--) {
      variableY = (FiniteStates)pot.getVariables().elementAt(k);
      
      if (!pot.getTree().isIn(variableY)) {
	if (currentRelations.isIn(variableY)) {
	  pos = pot.getVariables().indexOf(variableY);
	  pot.getVariables().removeElementAt(pos);
	}
      }
    }
    
    rel = new Relation();
    rel.setKind("potential");
    rel.getVariables().nodes = (Vector)pot.getVariables().clone();
    rel.setValues(pot);
    currentRelations.insertRelation(rel);
    pairTable.addRelation(rel);
  }
}


/**
 * Simulates a configuration.
 * @param generator a random number generator.
 * @return true if the simulation was ok.
 */

public boolean simulateConfiguration(Random generator) {
 
  FiniteStates variableX;
  PotentialTree pot;
  int i, s, v;
  boolean ok = true;
  
  s = samplingDistributions.size()-1;
  
  for (i=s ; i>=0 ; i--) {
    variableX = (FiniteStates)deletionSequence.elementAt(i);
    pot = (PotentialTree)samplingDistributions.elementAt(i);
    
    v = simulateValue(variableX,s-i,pot,generator);
    
    if (v==-1) { // Zero
      ok = false;
      break;
    }
    currentConf[s-i] = v;
  }

  return ok;
}


/**
 * Simulates a value for a variable.
 * @param variableX a FiniteStates variable to be generated.
 * @param pos the position of variableX in the current conf.
 * @param pot the sampling distribution of variableX.
 * @param generator a random number generator.
 * @return the value simulated. -1 if the valuation is 0.
 */

public int simulateValue(FiniteStates variableX, int pos,
			 PotentialTree pot, Random generator) {
 
  int i, nv, v = -1;
  double checksum = 0.0, r, cum=0.0;
  double [] values;

  
  nv = variableX.getNumStates();
  values = new double[nv];
  
  for (i=0 ; i<nv ; i++) {
    currentConf[pos] = i;
    values[i] = pot.getValue(positions,currentConf);
    checksum += values[i];
  }
  
  if (checksum == 0.0) {
    System.out.println("Zero valuation");
    return -1;
  }

  r = generator.nextDouble();

  for (i=0 ; i<nv ; i++) {
    cum += (values[i] / checksum);
    if (r <= cum) {
      v = i;
      break;
    }
  }

  currentWeight /= (values[v] / checksum);
  return v;
}


/**
 * Simulates a sample of size sampleSize and updates the weights
 * in simulationInformation.
 */

public void simulate() {
 
  int i;
  double w;
  boolean ok;
  Random generator = new Random();

  
  currentConf = new int[network.getNodeList().size()];
  
  for (i=0 ; i<sampleSize ; i++) {
    currentWeight = 1.0;
    
    ok = simulateConfiguration(generator);
    
    if (ok) {
      w = evaluate();
      currentWeight *= w;     
      updateSimulationInformation();
    }
  }
}


/**
 * Computes the probability of obtaining a configuration
 * according to the original conditional distributions in
 * the network.
 * @param conf a configuration to evaluate.
 * @return the probability of conf.
 */

public double evaluate(Configuration conf) {
 
  int i, s;
  double value = 1.0;
  Relation rel;
  PotentialTree pot;
  
  s = initialRelations.size();
  
  for (i=0 ; i<s ; i++) {
    rel = (Relation)initialRelations.elementAt(i);
    pot = (PotentialTree)rel.getValues();
    value *= pot.getValue(conf);
  }
  
  return value;
}



/**
 * Computes the probability of obtaining configuration currentConf
 * according to the original conditional distributions in
 * the network.
 * @return the probability of currentConf.
 */

public double evaluate() {
 
  int i, s;
  double value = 1.0;
  Relation rel;
  PotentialTree pot;
  
  s = initialRelations.size();
  
  for (i=0 ; i<s ; i++) {
    rel = (Relation)initialRelations.elementAt(i);
    pot = (PotentialTree)rel.getValues();
    
    // If size == 0 it means that the potential corresponds to
    // a single variable and that variable is observed.
    // So, do not evaluate it.
    
    if (pot.getVariables().size() > 0)
      value *= pot.getValue(positions,currentConf);
  }
  
  return value;
}


/**
 * Restricts a list of relations to the observations.
 * @param list the list of relations to restrict.
 */

public void restrictToObservations(RelationList list) {

  Relation rel;
  int i, s;
  
  s = list.size();
  
  for (i=0 ; i<s ; i++) {
    rel = list.elementAt(i);
    rel.setValues(((PotentialTree)rel.getValues())
		  .restrictVariable(observations));
    rel.getVariables().nodes = rel.getValues().getVariables();
  }
}


/**
 * Carries out a propagation. At the end of the propagation,
 * SimulationInformation will contain the result.
 * NOTE: The exact results must be ready before calling to this
 * method.
 *
 * @param resultFile the name of the file where the errors will
 *        be stored.
 */

public void propagate(String resultFile) throws IOException {

								      
  double[] errors;
  double g = 0.0, mse = 0.0;
  int i;
  FileWriter f;
  PrintWriter p;
  Date date;
  double timeSamplingDist, timeSimulating = 0.0, parTime;

  
  errors = new double[2];
      
  initialRelations = getInitialRelations();
  
  if (observations.size()>0)
    restrictToObservations(initialRelations);
  
  date = new Date();
  timeSamplingDist = (double)date.getTime();
  System.out.println("Computing sampling distributions");
  getSamplingDistributions();
  System.out.println("Sampling distributions computed");
  date = new Date();
  timeSamplingDist=((double)date.getTime()-timeSamplingDist)/1000;
  
  initSimulationInformation();
  
  System.out.println("Simulating");
  

  for (i=0 ; i<numberOfRuns ; i++) {
    date = new Date();
    parTime = (double)date.getTime();

    simulate();
    normalizeResults();
    
    date = new Date();
    parTime = ((double)date.getTime() - parTime) / 1000;
    
    timeSimulating += parTime;
    computeError(errors);
    g += errors[0];
    mse += errors[1];
    
    if (i < (numberOfRuns - 1))
      clearSimulationInformation();
  }

  timeSimulating /= numberOfRuns;
  
  g /= (double)numberOfRuns;
  mse /= (double)numberOfRuns;
  f = new FileWriter(resultFile);
  
  p = new PrintWriter(f);
  
  p.println("Time computing sampling distributions (secs): "+
	    timeSamplingDist);
  p.println("Time simulating (avg) : "+timeSimulating);
  p.println("G : "+g);
  p.println("MSE : "+mse);
  p.println("Variance : "+varianceOfWeights());
  f.close();

  System.out.println("Done");
}

}

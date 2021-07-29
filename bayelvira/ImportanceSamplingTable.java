import NodeList;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Random;
import java.util.Date;
import Relation;
import RelationList;
import PairTable;
import java.io.*;

/**
 * Class ImportanceSamplingTable.
 * Implements the importance sampling method of propagation
 * based on approximate node deletion using probability tables.
 *
 * @since 1/12/99
 */


public class ImportanceSamplingTable extends ImportanceSampling {

/**
 * Program for performing experiments.
 * The arguments are as follows.
 * <ol>
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
  ImportanceSamplingTable propagation;
  int i, ss, nruns, ls;
  
  
  if (args.length<6)
    System.out.println("Too few arguments");
  else {
    networkFile = new FileInputStream(args[3]);
    b = new Bnet(networkFile);

    if (args.length==7) {
      evidenceFile= new FileInputStream(args[6]);
      e = new Evidence(evidenceFile,b.getNodeList());
    }
    else
      e = new Evidence();

    ls = (Integer.valueOf(args[0])).intValue();

    ss = (Integer.valueOf(args[1])).intValue();
    
    nruns = (Integer.valueOf(args[2])).intValue();
 
    propagation = new ImportanceSamplingTable(b,e,ls,ss,nruns);

    System.out.println("Reading exact results");
    propagation.readExactResults(args[5]);
    System.out.println("Done");
    
    propagation.propagate(args[4]);
  }
}


/**
 * Creates an empty object. Necessary for subclass definitions.
 */

ImportanceSamplingTable() {
  
}

/**
 * Creates a new propagation with for a given evidence and
 * a given network.
 * @param b a belief netowrk.
 * @param e an evidence.
 */

ImportanceSamplingTable(Bnet b, Evidence e) {

  observations = e;
  network = b;
  positions = new Hashtable(20);
}


/**
 * Creates a new propagation.
 * @param b a belief netowrk.
 * @param e an evidence.
 * @param ls the maximum size for potentials.
 * @param ss the sample size.
 * @param nruns the number of runs.
 */

ImportanceSamplingTable(Bnet b, Evidence e, int ls, int ss, int nruns) {

  observations = e;
  network = b;
  setLimitSize(ls);
  setSampleSize(ss);
  setNumberOfRuns(nruns);
  positions = new Hashtable(20);
}


/**
 * @return the initial relations present
 * in the network.
 */

public RelationList getInitialRelations() {
  
  Relation rel, newRel;
  RelationList list;
  PotentialTable pot;
  int i;
 
  list = new RelationList();
  
  for (i=0 ; i<network.getRelationList().size() ; i++) {
    rel = (Relation)network.getRelationList().elementAt(i);
    newRel = new Relation();
    newRel.setVariables(rel.getVariables().copy());
    
    if (rel.getValues().getClass().getName().equals("PotentialTree")) {
      pot = new PotentialTable((PotentialTree)rel.getValues());
      newRel.setValues(pot);
    }
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
  Relation rel, rel1, rel2;
  RelationList currentRelations, tempList, list;
  PotentialTable pot;
  PairTable pairTable;
  double inc, min = 1e20, minInc, size = 0.0,
				  size1, size2, totalSize;
  int i, j, k, l, p, p1 = 0, p2 = 0, pos, s;
  boolean modified;


  notRemoved = new NodeList();
  pairTable = new PairTable();
  
  deletionSequence = new NodeList();
  samplingDistributions = new Vector();
  
  for (i=0 ; i<network.getNodeList().size() ; i++) {
    variableX = (FiniteStates)network.getNodeList().elementAt(i);
    if (!observations.isObserved(variableX)) {
      notRemoved.insertNode(variableX);
      pairTable.addElement(variableX);
    }
  }

  currentRelations = getInitialRelations(); 
    
  // Now restrict the valuations to the obervations.

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
    
    for (j=0 ; j<tempList.size() ; j++) {
      pairTable.removeRelation(tempList.elementAt(j));
    }
    
    samplingDistributions.addElement(tempList.copy());
    
    // Now combine while the threshold is not surpassed.
    
    modified = true;
    
    while (modified) {
      modified = false;
      s = tempList.size();
      minInc = 1e20;
      
      for (j=0 ; j<s-1 ; j++) {
	list = new RelationList();
	rel = tempList.elementAt(j);
	list.insertRelation(rel);
	size1 = ((PotentialTable)rel.getValues()).values.length;
	
	for (l=j+1 ; l<s ; l++) {
	  rel = tempList.elementAt(l);
	  size2 = ((PotentialTable)rel.getValues()).values.length;
	  if (size1 > size2)
	    size = size1;
	  else
	    size = size2;
	  
	  list.insertRelation(rel);
	  totalSize = list.totalSize();
	  inc = totalSize - size;
	  
	  if (inc<minInc) {
	    p1 = j;
	    p2 = l;
	    modified = true;
	    minInc = inc;
	    min = totalSize;
	  }
	  list.removeRelationAt(1);
	}
      }
      
      if (modified && (min<=(double)limitSize)) {
	rel1 = tempList.elementAt(p1);
	rel2 = tempList.elementAt(p2);

	tempList.removeRelationAt(p2);
	tempList.removeRelationAt(p1);
	pot = (PotentialTable)rel1.getValues();

	pot = pot.combine((PotentialTable)rel2.getValues());

	rel = new Relation();
	rel.setKind("potential");
	rel.getVariables().nodes=(Vector)pot.getVariables().clone();
	rel.setValues(pot);
	tempList.insertRelation(rel);
      }
      else {
	modified = false;
      }
    }
    
    
    if (i>1) {
      for (j=0 ; j<tempList.size() ; j++) {
	rel = tempList.elementAt(j);
	if (rel.getVariables().size()>1) {
	  pot = (PotentialTable)rel.getValues();
	  pot = (PotentialTable)pot.addVariable(variableX);
	  rel.setKind("potential");
	  rel.getVariables().nodes=(Vector)pot.getVariables().clone();
	  rel.setValues(pot);
	  currentRelations.insertRelation(rel);
	  pairTable.addRelation(rel);
	}
      }
    }
    
  }
}


/**
 * Simulates a configuration.
 * @param generator a random number generator.
 * @return true if the simulation was ok.
 */

public boolean simulateConfiguration(Random generator) {
 
  FiniteStates variableX;
  RelationList list;
  int i, s, v;
  boolean ok = true;

  
  s = samplingDistributions.size()-1;
 
  for (i=s ; i>=0 ; i--) {
    variableX = (FiniteStates)deletionSequence.elementAt(i);
    list = (RelationList)samplingDistributions.elementAt(i);
    v = simulateValue(variableX,s-i,list,generator);
    
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
 * @param list the list of sampling distributions of variableX.
 * @param generator a random number generator.
 * @return the value simulated. -1 if the valuation is 0.
 */

public int simulateValue(FiniteStates variableX, int pos,
			 RelationList list, Random generator) {
 
  int i, nv, v = -1;
  double checksum = 0.0, r, cum=0.0;
  double [] values;

  
  nv = variableX.getNumStates();
  values = new double[nv];
  
  for (i=0 ; i<nv ; i++) {
    currentConf[pos] = i;
    values[i] = evaluate(list);
    checksum+=values[i];
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
 * in SimulationInformation.
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
 * Evaluates a RelationList for a Configuration.
 * @param list the RelationList.
 * @param conf a configuration to evaluate.
 * @return the probability of conf.
 */

public double evaluate(RelationList list, Configuration conf) {
 
  int i, s;
  double value = 1.0;
  Relation rel;
  PotentialTable pot;
  
  s = list.size();
  for (i=0 ; i<s ; i++) {
    rel = list.elementAt(i);
    pot = (PotentialTable)rel.getValues();
    value *= pot.getValue(conf);
  }
  
  return value;
}


/**
 * Evaluates a RelationList for currentConf.
 * @param list the RelationList.
 * @return the probability of currentConf.
 */

public double evaluate(RelationList list) {
 
  int i, s;
  double value = 1.0;
  Relation rel;
  PotentialTable pot;
  
  s = list.size();
  for (i=0 ; i<s ; i++) {
    rel = list.elementAt(i);
    pot = (PotentialTable)rel.getValues();
    
    // If size == 0 it means that the potential corresponds to
    // a single variable and that variable is observed.
    // So, do not evaluate it.
    
    if (pot.getVariables().size() > 0)
      value *= pot.getValue(positions,currentConf);
  }
  
  return value;
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
  PotentialTable pot;
  
  s = initialRelations.size();
  for (i=0 ; i<s ; i++) {
    rel = (Relation)initialRelations.elementAt(i);
    pot = (PotentialTable)rel.getValues();
    
    // If size == 0 it means that the potential corresponds to
    // a single variable and that variable is observed.
    // So, do not evaluate it.
    
    if (pot.getVariables().size() > 0)
      value *= pot.getValue(conf);
  }
  
  return value;
}


/**
 * Computes the probability of obtaining currentConf
 * according to the original conditional distributions in
 * the network.
 * @return the probability of currentConf.
 */

public double evaluate() {
 
  int i, s;
  double value = 1.0;
  Relation rel;
  PotentialTable pot;
  
  s = initialRelations.size();
  for (i=0 ; i<s ; i++) {
    rel = (Relation)initialRelations.elementAt(i);
    pot = (PotentialTable)rel.getValues();
    
    // If size == 0 it means that the potential corresponds to
    // a single variable and that variable is observed.
    // So, do not evaluate it.
    
    if (pot.getVariables().size() > 0)
      value *= pot.getValue(positions,currentConf);
  }
  
  return value;
}


/**
 * Restrict a list of relations to the observations.
 * @param list the relation list to restrict.
 */

public void restrictToObservations(RelationList list) {

  Relation rel;
  int i, s;
  
  s = list.size();
  
  for (i=0 ; i<s ; i++) {
    rel = list.elementAt(i);
    rel.setValues (((PotentialTable)rel.getValues()).
      restrictVariable(observations));
    rel.getVariables().nodes = rel.getValues().getVariables();
  }
}


/**
 * Carries out a propagation. At the end of the propagation,
 * <code>simulationInformation</code> will contain the result.
 * NOTE: The exact results must be ready before calling to this method.
 * @param resultFile the name of the file where the errors will be stored.
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
  
  f=new FileWriter(resultFile);
  
  p=new PrintWriter(f);
  
  p.println("Time computing sampling distributions (secs): "+
	    timeSamplingDist);
  p.println("Time simulating (avg) : "+timeSimulating);
  p.println("G : "+g);
  p.println("MSE : "+mse);
  p.println("Variance : "+varianceOfWeights());
  f.close();

  System.out.println("Done");
}

} // End of class
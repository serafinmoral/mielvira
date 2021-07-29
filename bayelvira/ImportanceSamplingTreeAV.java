import java.util.Vector;
import java.util.Hashtable;
import java.util.Random;
import java.util.Date;
import Relation;
import RelationList;
import PairTable;
import ImportanceSampling;
import ImportanceSamplingTree;
import NodeList;
import PotentialTree;
import java.io.*;


/**
 * Class ImportanceSamplingTreeAV.
 * Implements the importance sampling method of propagation
 * based on approximate node deletion, using Probability Trees.
 * The added feature in this version is the use of antithetic
 * variates during the simulation. It means that each time a value
 * of a variable is simulated with a random number U, another value
 * is simulated using (1-U). The benefits are reduction
 * of variance and computing time. 
 *
 * @since 1/12/99
 */

public class ImportanceSamplingTreeAV extends ImportanceSamplingTree {

/**
 * Current weight of the antithetic configuration.
 */

double currentAntitheticWeight;

/**
 * The antithetic configuration.
 */

int[] currentAntitheticConf;


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
  ImportanceSamplingTreeAV propagation;
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
    
    propagation = new ImportanceSamplingTreeAV(b,e,lp,ls,ss,nruns);

    System.out.println("Reading exact results");
    propagation.readExactResults(args[6]);
    System.out.println("Done");

    propagation.propagate(args[5]);
  }
}


/**
 * Creates an empty object. Necessary for subclass definition.
 */

ImportanceSamplingTreeAV() {
  
}

/**
 * Creates a new propagation for a given evidence and
 * a given network.
 * @param b a belief netowrk.
 * @param e an evidence.
 */

ImportanceSamplingTreeAV(Bnet b, Evidence e) {

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

ImportanceSamplingTreeAV(Bnet b, Evidence e, double lp, int ls,
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
 * Simulates two antithetic configurations.
 * @param generator a random number generator.
 * @return true if the simulation was ok.
 */

public boolean simulateAntitheticConfigurations(Random generator) {
 
  FiniteStates variableX;
  PotentialTree pot;
  int i, s;
  int[] generatedValues;
  boolean ok = true;
  
  generatedValues = new int[2];
  
  s = samplingDistributions.size()-1;
  
  for (i=s ; i>=0 ; i--) {
    // The variable to simulate.
    variableX = (FiniteStates)deletionSequence.elementAt(i);
    // The sampling distribution for this variable.
    pot = (PotentialTree)samplingDistributions.elementAt(i);
    
    simulateAntitheticValues(variableX,s-i,pot,generator,generatedValues);
    
    if ((generatedValues[0]==-1) || (generatedValues[1]==-1)) {
      ok = false;
      break;
    }
    currentConf[s-i] = generatedValues[0];
    currentAntitheticConf[s-i] = generatedValues[1];
  }

  return ok;
}


/**
 * Simulates two antithetic values for a variable.
 * @param variableX a FiniteStates variable to be generated.
 * @param pos the position of variableX in vectors
 *        currentConf and currentAntitheticConf.
 * @param pot the sampling distribution of variableX.
 * @param generator a random number generator.
 * @param generatedValues an array of int with two positions, where
 *        the generated values will be stored. In position 0, the
 *        value for currentConf, and in positon 1 the value
 *        for currentAntitheticConf.
 */

public void simulateAntitheticValues(FiniteStates variableX, int pos,
				     PotentialTree pot,
				     Random generator,
				     int[] generatedValues) {
 
  int i, nv, v = -1;
  double checkSum = 0.0, checkSum2 = 0.0, r, r2, cum=0.0;
  double [] values, values2;

  
  nv = variableX.getNumStates();
  values = new double[nv];
  values2 = new double[nv];
  
  for (i=0 ; i<nv ; i++) {
    currentConf[pos] = i;
    currentAntitheticConf[pos] = i;
    values[i] = pot.getValue(positions,currentConf);
    values2[i] = pot.getValue(positions,currentAntitheticConf);
    checkSum += values[i];
    checkSum2 += values2[i];
  }
  
  if ((checkSum == 0.0) || (checkSum2==0.0)) {
    System.out.println("Zero valuation");
    generatedValues[0] = -1;
    generatedValues[1] = -1;
    return;
  }

  r = generator.nextDouble();
  r2 = 1 - r;
  
  for (i=0 ; i<nv ; i++) {
    cum += (values[i] / checkSum);
    if (r <= cum) {
      v = i;
      break;
    }
  }
  
  generatedValues[0] = v;
  
  v = -1;
  cum = 0.0;
  for (i=0 ; i<nv ; i++) {
    cum += (values2[i] / checkSum2);
    if (r2 <= cum) {
      v = i;
      break;
    }
  }
  
  generatedValues[1] = v;
  
  currentWeight /= (values[generatedValues[0]] / checkSum);
  currentAntitheticWeight/=(values2[generatedValues[1]]/checkSum2);
}


/**
 * Computes the probability of obtaining the configuration
 * stored in <code>currentAntitheticConf</code>
 * according to the original conditional distributions in
 * the network.
 * @return the probability of <code>currentAntitheticConf</code>.
 */

public double evaluateAntithetic() {
 
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
      value *= pot.getValue(positions,currentAntitheticConf);
  }
  
  return value;
}


/**
 * Simulates a sample of size <code>sampleSize</code> and
 * updates the weights in <code>simulationInformation</code>.
 * This method redefines the same one defined in the superclass.
 */

public void simulate() {
 
  int i, iterations;
  double w;
  boolean ok;
  Random generator = new Random();

  iterations = (int)(sampleSize / 2);
  currentConf = new int[network.getNodeList().size()];
  currentAntitheticConf = new int[network.getNodeList().size()];
  
  for (i=0 ; i<iterations ; i++) {
    currentWeight = 1.0;
    currentAntitheticWeight = 1.0;
    
    ok = simulateAntitheticConfigurations(generator);
    
    if (ok) {
      w = evaluate();
      currentWeight *= w; 
      w = evaluateAntithetic();
      currentAntitheticWeight *= w;
      updateSimulationInformation();
    }
  }
}


/**
 * Updates the simulation information according to the
 * current weights and the simulated configurations.
 * This method redefine the one implemented in class
 * <code>ImportanceSampling</code>.
 */

public void updateSimulationInformation() {
  
  int i, s, v;
  PotentialTable pot;
  
  s = results.size();
  
  for (i=0 ; i<s ; i++) {
    pot = (PotentialTable)results.elementAt(i);
    v = currentConf[i];
    pot.incValue(v,currentWeight);
    
    v = currentAntitheticConf[i];
    pot.incValue(v,currentAntitheticWeight);
  }
  
  // The following is used to compute the variance of the weights.
  sumW += (currentWeight + currentAntitheticWeight);
  sumW2 += (currentWeight * currentWeight);
  sumW2 += (currentAntitheticWeight * currentAntitheticWeight);
}


/**
 * Carries out a propagation. At the end of the propagation,
 * the instance variable <code>results</code>
 * will contain the result, as a list of <code>PotentialTable</code>.
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

} // End of class
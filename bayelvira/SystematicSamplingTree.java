import NodeList;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Random;
import java.util.Date;
import Relation;
import RelationList;
import PairTable;
import ImportanceSamplingTable;
import java.io.*;

/**
 * Class SystematicSamplingTree.
 * Implements the recursive systematic sampling method of propagation
 * based on approximate node deletion using probability trees.
 *
 * @since 1/12/99
 */


public class SystematicSamplingTree extends ImportanceSamplingTree {

/**
 * Program for performing experiments.
 * The arguments are as follows.
 * <ol>
 * <li> A double; limit for prunning.
 * <li> An integer. Maximum size of a potential.
 * <li> An integer. Sample size.
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
  SystematicSamplingTree propagation;
  int ss, ls;
  double lp;
  
  
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

    lp = (Double.valueOf(args[0])).doubleValue();
    
    ls = (Integer.valueOf(args[1])).intValue();

    ss = (Integer.valueOf(args[2])).intValue();
    
    propagation = new SystematicSamplingTree(b,e,lp,ls,ss);

    propagation.propagate(args[5],args[4]);
  }
}


/**
 * Creates a new propagation.
 * @param b a belief network.
 * @param e an evidence.
 * @param lp the limit for prunning.
 * @param ls the maximum size for potentials.
 * @param ss the sample size.
 * @param nruns the number of runs.
 */

SystematicSamplingTree(Bnet b, Evidence e, double lp,
		       int ls, int ss) {

  observations = e;
  network = b;
  setLimitForPrunning(lp);
  setLimitSize(ls);
  setSampleSize(ss);
  positions = new Hashtable(20);
}


/**
 * Carries out a propagation. At the end of the propagation,
 * SimulationInformation will contain the result.
 *
 * @param exactFile the name of the file containing the
 *        exact results.
 * @param resultFile the name of the file where the errors will
 *        be stored.
 */

public void propagate(String exactFile, String resultFile) throws ParseException, IOException {

								      
  double[] errors;
  double g = 0.0, mse = 0.0, low, high, increment, first, weight;
  int iterations, positionToSimulate;
  FileWriter f;
  PrintWriter p;
  Date date;
  double timeSamplingDist, timeSimulating;

  
  errors = new double[2];
  
  System.out.println("Reading exact results");
  readExactResults(exactFile);
  System.out.println("Done");
    
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
  
  date = new Date();
  timeSimulating = (double)date.getTime();
  

  // Position of the first variable to simulate
  positionToSimulate = samplingDistributions.size()-1;
  // Number of iterations
  iterations = sampleSize;
  // Increment in the "random" numbers
  increment = 1.0/(double)iterations;
  // first "random" number
  first = 0.5/(double)iterations;
  // Lower limit
  low = 0.0;
  // Upper limit
  high = 1.0;
  // Weight of the configuration simulated
  weight = 1.0;
  
  currentConf = new int[network.getNodeList().size()];
  simulate(positionToSimulate,iterations,low,high,first,
	   increment,weight);
	
  normalizeResults();
  
  date = new Date();
  timeSimulating = ((double)date.getTime()-timeSimulating) / 1000;
  
  computeError(errors);
  g = errors[0];
  mse = errors[1];

  f=new FileWriter(resultFile);
  
  p=new PrintWriter(f);
  
  p.println("Time computing sampling distributions (secs): "+
	    timeSamplingDist);
  p.println("Time simulating (avg) : "+timeSimulating);
  p.println("G : "+g);
  p.println("MSE : "+mse);
  f.close();

  System.out.println("Done");
}


/**
 * Recursive method that performs the simulation.
 *
 * @param positionToSimulate position of the variable to simulate
 *        in the deletionSequence.
 * @param iterations  number of configurations to generate.
 * @param low lower limit of the simulation interval.
 * @param high upper limit of the simulation interval.
 * @param first first number to use to generate the configuration.
 * @param increment increment from number to number.
 * @param weight current weight of the part of the configuration
 *        already simulated.
 */

public void simulate(int positionToSimulate, int iterations,
		     double low, double high, double first,
		     double increment, double weight) {

  int newPosition, pos, newIterations, val;
  FiniteStates variable;
  PotentialTable pot;
  PotentialTree auxPot;
  double aux, newLow, newHigh, newFirst, newIncrement, scaleRatio;

  
  if (positionToSimulate == -1) {  // We have an entire config.
    currentWeight = 0.0;
   
    if (weight != 0.0)
      currentWeight = (double)iterations / weight;

    currentWeight *= evaluate();

    updateSimulationInformation();
  } // end if
  else {
    newPosition = positionToSimulate-1;
 
    // Variable to be simulated.
    variable = (FiniteStates)deletionSequence.elementAt(positionToSimulate);
 
    // Check whether the variable is instantiated.
    if (observations.isObserved(variable)) {
      newLow = 0.0;
      newHigh = 1.0;
      scaleRatio = high - low;
      newFirst = (first - low) / scaleRatio;
      newIncrement = increment / scaleRatio;
      simulate(newPosition,iterations,newLow,newHigh,newFirst,
	       newIncrement,weight);
    }
    else {
      auxPot = (PotentialTree)samplingDistributions.elementAt(positionToSimulate);
 
      // The position of this variable in currentConf
      pos = samplingDistributions.size()-positionToSimulate-1;
 
      // Gets the valuation for the variable to simulate 
      pot = getValuation(auxPot,variable.getNumStates(),pos);


      // Amplitude of the former interval
      scaleRatio = high - low;

      if (scaleRatio <= 0.0) {
	System.out.println("Interval of amplitude 0.\n");
      }
      else {
	// New first number
	newFirst = (first - low) / scaleRatio;

	// New increment
	newIncrement = increment / scaleRatio;
 
	// New lower limit
	newLow = 0.0;
  
	// New upper limit
	newHigh = 0.0;
 
	val = 0;
	
	while (newFirst < 1.0) {
	  newLow = newHigh;

	  aux = pot.getValue(val);
	  newHigh += aux;

	  while (newHigh <= newFirst) {
	    val++;
	    newLow = newHigh;
	    aux = pot.getValue(val);
	    newHigh += aux;
	
	  } // end of 2nd while
   
	  currentConf[pos] = val;
   
	  newIterations = (int)((newHigh-newFirst)/newIncrement)+1;
	  
	  aux *= weight;
   
	  simulate(newPosition,newIterations,newLow,newHigh,newFirst,
		   newIncrement,aux);
   
	  newFirst += (newIterations * newIncrement);
	  val++;
      
	} // end of 1st while
	
      } // end of 3rd else
      
    } // end of 2nd else
    
  } // end of 1st else
}


/**
 * Computes the restriction of the sampling distribution
 * corresponding to a given variable to the configuration
 * already simulated, and stores the result in a single
 * PotentialTable that can be used to simulate that variable.
 * @param auxPot the sampling distribution associated with
 *        the variable.
 * @param nv the number of possible values of the variable.
 * @param pos the position of the variable in currentConf.
 */

public PotentialTable getValuation(PotentialTree auxPot, int nv,
				  int pos) {

  int i;
  PotentialTable pot;
  
  pot = new PotentialTable(nv);
  
  for (i=0 ; i<nv ; i++) {
    currentConf[pos] = i;
    pot.setValue(i,auxPot.getValue(positions,currentConf));
  }
  
  pot.normalize();
  
  return pot;
}

} // End of class
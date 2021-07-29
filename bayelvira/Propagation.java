import java.util.Vector;
import java.util.Dictionary;
import java.util.Hashtable;
import FiniteStates;
import java.io.*;
import NodeList;
import Relation;
import DSeparation;
import Evidence;
import Bnet;


/**
 * Class Propagation. Implements a general setting for
 * performing propagations over a network.
 *
 * @since 22/11/99
 */

public class Propagation    {

Evidence observations; // The observations
Bnet network;          // The network
String propComment;    // A comment about the propagation
String problem;        // The name of the problem
String method;         // The name of the method
NodeList interest;     // Variables of interest
Vector results;        // Results of the propagation
RelationList exactResults; // Exact results of the propagation. Usually read from a file.
PropagationStatistics statistics; // some interesting data about the propagation
/**
 * Stores the position of the network variables in vector 
 * <code>results</code>. This instance variable must be
 * initialized in the constructor of every subclass that uses it.
 */
Hashtable positions;




/**
 * Constructs a new propagation. Problem: marginal, method:
 * variable elimination.
 */

Propagation() {
  
  observations = new Evidence();
  interest = new NodeList();
  results = new Vector();
  setProblem("marginal");
  setMethod("deletion");  
  statistics = new PropagationStatistics();
  positions = new Hashtable(20);
}


/**
 * Constructs a new propagation with observations e.
 * @param e the evidence.
 * Problem: marginal, method:
 * variable elimination.
 */

Propagation(Evidence e) {
 
  observations = e;
  interest = new NodeList();
  results = new Vector();
  setProblem("marginal");
  setMethod("deletion");
  setPropComment("");
  statistics = new PropagationStatistics();
  positions = new Hashtable(20);
}


/**
 * Sets the problem.
 * @param problemName the problem.
 */

public void setProblem(String problemName) {
  
  if (problemName.equals("marginal"))
    problem = new String("marginal");
}


/**
 * Sets the method.
 * @param methodName the method.
 */

public void setMethod(String methodName) {
  
  if (methodName.equals("deletion"))
    method = new String("deletion");
  
  if (methodName.equals("importanceapproximate"))
    method = new String("importanceapproximate");
}


/**
 * Sets the comment.
 * @param c the comment.
 */

public void setPropComment(String c) { 
  
  propComment = new String(c);
}

/**
 * Sets the statistics.
 * @param s the statistics.
 */

public void setStatistics(PropagationStatistics s) {

  statistics = s;
}                       

/**
 * Get the comment.
 */

public String getPropComment( ) { 
  
  return propComment;
}


/**
 * Get the statistics.
 */

public PropagationStatistics getStatistics( ) {

  return statistics;
}             

public void delete() {
  
  DSeparation dsep;
  Vector Relevant;
  Vector Rels;
  Dictionary AllGroups;
  Vector Group;
  Vector Sequence;
  
  dsep = new DSeparation(network,observations);
  Relevant = dsep.allAffecting((Node) interest.elementAt(0));
}  


/**
 * Reads the exact results from the argument file and put them into
 * the field <code>exactResults</code>
 * @param fileName the file with the exact results
 */

public void readExactResults(String fileName) throws ParseException, IOException {
  
    FileInputStream f;
    ResultParse parser;
    
    f = new FileInputStream(fileName);
    parser = new ResultParse(f);
    
    parser.initialize(network.getNodeList());
    parser.CompilationUnit();
    
    exactResults = parser.Results;
}

/**
 * Saves the result of a propagation to a file.
 * @param Name a String containing the file name.
 */

public void saveResults(String S) throws IOException {
  FileWriter F;
  PrintWriter P;
  Potential Pot;
  int i;

  F=new FileWriter(S);
  
  P=new PrintWriter(F);
  
  for (i=0 ; i<results.size() ; i++) {
    Pot = (Potential)results.elementAt(i);
    Pot.saveResult(P);
  }
  
  F.close();
}

/**
 * Normalizes the results of a propation.
 */

public void normalizeResults() {
  Potential pot;
  int i;

  for (i=0 ; i<results.size() ; i++) {
    pot = (Potential)results.elementAt(i);
    pot.normalize();
  }
}

/** 
 * If there is not interest variables specified, set all the unobserved 
 * variables as interest
 */

public void obtainInterest( ) {

  int i,s;
  FiniteStates fs;

  if (interest.size() == 0) {
    s = network.getNodeList().size();
    interest = new NodeList();
    for (i=0; i<s; i++) {
      fs = (FiniteStates)network.getNodeList().elementAt(i);
      //if (observations.indexOf(fs) == -1) {
      if (!observations.isObserved(fs)) {
        interest.insertNode(fs);
      }
    }
  }

}

/**
 * Computes the error of an estimation. The exact results are
 * suposed to be stored in the instance variable <code>exactResult</code>,
 * while the obtained results are suposed to be stored in the instance
 * variable <code>results</code>.
 * @param errors an array where the errors will be stored. In
 * the first position, the g-error. In the second one, the mean
 * square error
 */

public void computeError(double[] errors) {
 
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
    
    //approxPot = (PotentialTable)results.elementAt(pos);
    approxPot = PotentialTable.convertToPotentialTable((Potential)(results.elementAt(pos)));

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

}  // end of class

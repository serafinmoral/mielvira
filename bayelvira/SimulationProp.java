import java.io.*;
import Relation;
import Propagation;

/**
 * Class SimulationProp. General class for simulation
 * algorithms.
 *
 * @since 29/09/99
 */

public class SimulationProp extends Propagation {

int sampleSize;
RelationList initialRelations; // The initial relations.


/**
 * Sets the sample size.
 * @param n the new sample size.
 */

public void setSampleSize(int n) {
  
  sampleSize = n;
}


/**
 * Gets the sample size.
 * @return the sample size.
 */

public int getSampleSize() {
  
  return sampleSize;
}


/**
 * Normalizes the results of a simulation.
 */

public void normalizeResults() {

  PotentialTable pot;
  int i;

  for (i=0 ; i<results.size() ; i++) {
    pot = (PotentialTable)results.elementAt(i);
    pot.normalize();
  }
}


/**
 * Saves the result of a propagation to a file.
 * @param fileName a String containing the file name.
 */

public void saveResults(String fileName) throws IOException {

  FileWriter f;
  PrintWriter p;
  PotentialTable pot;
  int i;

  f = new FileWriter(fileName);
  
  p = new PrintWriter(f);
  
  for (i=0 ; i<results.size() ; i++) {
    pot = (PotentialTable)results.elementAt(i);
    pot.saveResult(p);
  }
  
  f.close();
}


}
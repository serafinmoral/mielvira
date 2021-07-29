import java.io.*;

/**
 * Class JoinTreeStatistics - 
 * Contains some interested information about the joinTree
 *
 * @since 07/10/99
 */

public class JoinTreeStatistics {

private int numCliques;
private int minVarsInClique;  // min number of variables in a clique
private int maxVarsInClique;  // max  "  
private double meanVarsInClique; // mean "

private double minCliqueSize; 
private double maxCliqueSize;
private double meanCliqueSize;
private double JTSize;  // size of the jointree

/**
 * Constructor
 */ 

public JoinTreeStatistics() {
  numCliques = 0;
  minVarsInClique = 0;
  maxVarsInClique = 0;
  meanVarsInClique = 0.0;
  minCliqueSize = 0.0;
  maxCliqueSize = 0.0;
  meanCliqueSize = 0.0;
  JTSize = 0.0;
}


/**
 * Access methods
 */


public void setJTSize(double s){
  JTSize = s;
}

public void setMinCliqueSize(double s){
  minCliqueSize = s;
}

public void setMeanCliqueSize(double s){
  meanCliqueSize = s;
}

public void setMaxCliqueSize(double s){
  maxCliqueSize = s;
}

public void setNumCliques(int n){
  numCliques = n;
}

public void setMinVarsInClique(int v){
  minVarsInClique = v;
}

public void setMaxVarsInClique(int v){
  maxVarsInClique = v;
}

public void setMeanVarsInClique(double v){
  meanVarsInClique = v;
}

public double getJTSize( ){
  return JTSize;
}

public double getMinCliqueSize( ){
  return minCliqueSize;
}

public double getMeanCliqueSize( ){
  return meanCliqueSize;
}

public double getMaxCliqueSize( ){
  return maxCliqueSize;
}

public int getNumCliques( ){
  return numCliques;
}

public int getMinVarsInClique( ){
  return minVarsInClique;
}

public int getMaxVarsInClique( ){
  return maxVarsInClique;
}

public double getMeanVarsInClique( ){
  return meanVarsInClique;
}


/**
 * Print the object
 */


public void print( ){

  System.out.println("Number of cliques  : " + numCliques);
  System.out.println("Minimum number of variables in a clique: " +
							minVarsInClique);
  System.out.println("Maximum number of variables in a clique: " +
							maxVarsInClique);
  System.out.println("Mean number of variables in a clique   : " +
							meanVarsInClique);
  System.out.println();
  System.out.println("Minimum size clique: " + minCliqueSize);
  System.out.println("Maximum size clique: " + maxCliqueSize);
  System.out.println("Mean size clique   : " + meanCliqueSize);
  System.out.println("Total size         : " + JTSize);   
}

} // end of class


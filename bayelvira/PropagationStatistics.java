import java.io.*;

/**
 * Class PropagationStatistics - 
 * Contains some interested information in order to evaluate 
 * the propagation carried out.
 *
 * @since 07/10/99
 */

public class PropagationStatistics {

private double time;
private JoinTreeStatistics JTStat; // some data about the joinTree

private double JTExtraSize;   // these two values are necesary in exact partial 
private double JTInitialSize; // abductive inference, because the join tree
                              // is modified 


/**
 * Constructor
 */ 

public PropagationStatistics() {
  time = 0.0;
  JTStat = new JoinTreeStatistics();
  JTExtraSize = 0.0;
  JTInitialSize = 0.0; 
}


/**
 * Access methods
 */

public void setTime(double t){
  time = t;
}

public void setJTStat(JoinTreeStatistics s){
  JTStat = s;
}

public void setJTInitialSize(double s){
  JTInitialSize = s;
}

public void setJTExtraSize(double s){
  JTExtraSize = s;
}

public double getTime( ){
  return time;
}

public JoinTreeStatistics getJTStat(){
  return JTStat;
}


public double getJTInitialSize( ){
  return JTInitialSize;
}

public double getJTExtraSize( ){
  return JTExtraSize;
}


/**
 * Print the object
 * @param partial indicates if the propagation is a exact partial abductive
 *                inference
 */

public void print( ){
  this.print(false);
}

public void print(boolean partial ){
  System.out.println("Printing statistics about the propagation");
  System.out.println("\tTime: " + time);
  System.out.println();

  if (partial){
    System.out.println("\tSize of the initial jt: " + JTInitialSize);
    System.out.println("\tSize of the whole jt: " +
					(JTInitialSize+JTExtraSize));
    System.out.println("\tData about the jt used in abductive inference");
  }

  JTStat.print();
}

} // end of class


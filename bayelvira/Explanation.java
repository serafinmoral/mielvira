import java.io.*;

/**
 * Class Explanation
 * An element of ConfProb is a pair (configuration,double). 
 * 
 * @since 25/08/99
 */

public class Explanation {

/** 
 * The configuration of variables and values
 */

private Configuration conf;

/**
 * Probability asociated to the configuration. 
 */

private double prob;

/**
 * Creates an empty explanation with probability equal to 0.0
 */

Explanation() {

  conf = new Configuration();
  prob = 0.0;
}

/**
 * Creates an explanation for a given configuration and a given probability
 */

Explanation(Configuration c,double d) {

  conf = c;
  prob = d;
}


/**
 * Get the configuration in the explanation
 */

Configuration getConf() {
  return conf;
}


/**
 * Get the probability of the explanation
 */

double getProb() {
  return prob;
}

/**
 * Method to modify the configuration in the explanation
 */

void setConf(Configuration c) {
  conf = c;
}

/**
 * Method to modify the probability of the explanation
 */

void setProb(double p) {
  prob = p;
}


/**
 * Prints this explanation to the standard output.
 */

public void print(){
  
  System.out.print("(");
  conf.print();
  System.out.print(" , "+ prob + ")"); 
} 


/**
 * Saves this explanation in a file.
 * @param p the PrintWriter where the configuration
 * will be written.
 */

public void save(PrintWriter p) throws IOException {

  int i,s;
  String n;
  
  s = conf.size();
  for(i=0; i<s; i++){
    p.print( ((Node)conf.getVariables().elementAt(i)).getName() + " = ");
    n = new String((String)
                   ((FiniteStates)conf.getVariables().elementAt(i)).
                   getState(((Integer)
                            conf.getValues().elementAt(i)).intValue()));     

    p.print(n + "\n");
  }

  p.print("} with probability " + prob + "\n");

}       

} // end of class
import java.io.*;
import java.util.Vector;

/**
 * Class AIPartitionElement - 
 * (short name for AbductiveInferencePartitionElement)
 * Contains the information necessary to indentify an element
 * of the partition build by Nilsson's algorithm
 *
 * @since 26/08/99
 */

public class AIPartitionElement {

/**
 * The NodeJoinTree for which the PartitionElement was built
 */

private NodeJoinTree clique;

/**
 * The index of the explanation for which the PartitionElement was built
 */

private int expIndex;

/**
 * The configuration whose values must be included in all the elements
 * of this PartitionElement
 */

private Configuration sameValues;

/**
 * A vector with the configurations impossible to be consider
 */

private Vector distincts;

/**
 * The maximum subconfiguration found
 */

private Configuration maxSubConf;

/**
 * The probability P(maxSubConf,observations)
 */

private double prob;


/**
 * Constructor
 *
 * @param n the clique 
 * @param e the explanation index 
 * @param i the configuration to initialize sameValues
 */

AIPartitionElement(NodeJoinTree n, int e, Configuration i) {

  clique = n;
  expIndex = e;
  sameValues = i;
  distincts = new Vector();
  maxSubConf = new Configuration(n.getVariables());
  prob = 0.0;

}                                             

/**
 * @return the clique
 */

public NodeJoinTree getClique() {
  return clique;
}

/**
 * @return the explanation index
 */

public int getExpIndex() {
  return expIndex;
}

/** 
 * @return the configuration sameValues
 */

public Configuration getSameValues() {
  return sameValues;
}

/**
 * @return the vector of configurations not allowed
 */

public Vector getDistincts() {
  return distincts;
}

/**
 * @return the max configuration
 */ 

public Configuration getMaxSubConf() {
  return maxSubConf;
}

/**
 * @return the probability
 */

public double getProb() {
  return prob;
}


/**
 * Set the clique
 */

public void setClique(NodeJoinTree n) {
  clique = n;
}

/**
 * Set the expIndex
 */

public void setExpIndex(int i) {
  expIndex = i;
}

/** 
 * Set the configuration sameValues
 */

public void setSameValues(Configuration c) {
  sameValues=c;
}

/**
 * set the vector of configurations not allowed
 */

public void setDistincts(Vector v) {
  int i;
  
  distincts = new Vector();
  for(i=0;i<v.size();i++)
    distincts.addElement(v.elementAt(i));
}

/**
 * set the max configuration
 */ 

public void setMaxSubConf(Configuration c) {
  maxSubConf=c;
}


/**
 * set the probability
 */

public void setProb(double p) {
  prob=p;
}

/**
 * adds a new distinct configuration
 *
 * @param c the configuration to be added
 */

public void addDistinct(Configuration c) {

  distincts.addElement(c);
}


/**
 * Print this partition element
 */
 
public void print ( ) {

  int i,j;
  Configuration conf;

  System.out.println("");
  System.out.println("Clique: " + clique.getLabel());
  System.out.println("Explanation: " + expIndex);
  System.out.print("Same subconfiguration: ");
  for (i=0; i<sameValues.size(); i++) {
    System.out.print(((Node)sameValues.getVariables().elementAt(i)).getName()
                         + " ");
  }
  sameValues.print();
  System.out.println();

  System.out.println("Differents subconfigurations: ");
  for(i=0;i<distincts.size();i++){
    conf = (Configuration)distincts.elementAt(i);
    for (j=0; j<conf.size(); j++) {
      System.out.print(((Node)conf.getVariables().elementAt(j)).getName()
                         + " ");
    }
    conf.print();
    System.out.println();
  }

  System.out.print("Max subconfiguration found: ");
  for (i=0; i<maxSubConf.size(); i++) {
    System.out.print(((Node)maxSubConf.getVariables().elementAt(i)).getName()
                         + " ");
  }
  maxSubConf.print();
  System.out.println();

  System.out.println("with probability " + prob);
    
}

} // end of class       
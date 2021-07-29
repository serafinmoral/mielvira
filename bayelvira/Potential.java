import java.util.Vector;
import java.util.Hashtable;
import java.io.*;
import Configuration;
import FiniteStates;


/**
 * Abstract class Potential. Specifies some methods and instance
 * variables that are common to all kinds of potentials.
 * The basic structure of a potential is a list of variables
 * for which it is defined, and a piece of information about
 * those variables. Usually, that information will be given
 * as a probability table or as a probability tree.
 *
 * Last modified : 17/04/99
 */

public abstract class Potential implements Cloneable{

Vector variables;
  
/**
 * @return the vector of variables of the potential.
 */

Vector getVariables() {

  return variables;
}


/**
 * Clone a Potential
 * @return a Potential clone .
 */


public Object clone(){

    Potential o = null;
    Node aux;
    try{

	o = (Potential)super.clone();
	o.variables = (Vector)variables.clone();
	for(int i=0 ;i < o.variables.size() ; i++){
	    aux = (Node)o.variables.elementAt(i);
	    aux = (Node)aux.clone();
	    o.variables.setElementAt(aux,i);
	}
    }catch (CloneNotSupportedException e){
	System.out.println("Can't clone Potential");
    }

    return o;

}

/** Combine current potential with Potential pot
  * @param pot
  * @return The combination of the two potential 
  */

abstract public Potential combine(Potential pot);


/**
 * Removes the argument variable summing over all its values.
 * @param var a FiniteStates variable.
 * @return a new Potential with the result of the deletion.
 */

abstract public Potential addVariable(FiniteStates var);

/**
 * Save the potential into the file represented by the PrintWriter P
 * @param P is the PrintWriter
 */

abstract public void saveResult(PrintWriter P);

/**
 * Normalizes the values of this potential.
 */

abstract public void normalize();


/**
 * Gets the value of a potential for a given configuration of
 * variables.
 * @param conf a configuration of variables.
 * @return the value of the potetial for conf.
 */

abstract double getValue(Configuration conf);

/**
 * Gets the value for a configuration. In this case, the
 * configuration is represented by means of an array of int.
 * At each position, the value for certain variable is stored.
 * To know the position in the array corresponding to a given
 * variable, we use a hash table. In that hash table, the
 * position of every variable in the array is stored.
 *
 * @param positions a Hash table.
 * @param conf an array of int.
 * @return the value corresponding to configuration conf.
 */

abstract public double getValue(Hashtable positions, int[] conf);



/**
 * Sets the value of a potential for a given configuration.
 * @param conf a configuration of variables.
 * @param val the value for conf.
 */

abstract void setValue(Configuration conf, double val); 


/**
 * Gets the size of the potential.
 * @return the size of array in table mode;
 * the number of nodes in tree mode.
 */

abstract long getSize();


/**
 * @return the sum of all the values of a potential.
 */

abstract double totalPotential(); 


/**
 * @param conf a configuration of variables.
 * @return the sum of the values of a potential
 * restricted to configuration conf.
 */

abstract double totalPotential(Configuration conf);

/**
 * Computes the entropy of a potential.
 * @return the sum of the values x Log x stored in the potential.
 */

abstract double entropyPotential();


/**
 * Computes the entropy of a potential restricted to
 * a given configuration.
 * @param conf the configuration.
 * @return the sum of the values x Log x fixing configuration conf.
 */

abstract double entropyPotential(Configuration conf);

abstract Potential restrictVariable(Configuration conf);
/*{
  System.out.println("Llamado el restrictVariable de Potential");
  return this;
}
*/

/**
 * Marginalizes over a set of variables. It is equivalent
 * to addVariable over the other variables.
 * @param vars a vector of variables.
 * @return a Potential with the marginalization over vars.    
 */


abstract public Potential marginalizePotential(Vector vars);

/**
 * Saves a Potential to a file
 * @param p the PrintWriter a vector of variables where the potential will be written.    
 */

abstract public void save(PrintWriter p);


void print(){
}



/**
 * This method combine the potential which receive the message with
 * the one passed as parameter. 
 *
 * The method is not defined as abstract to avoid the need of definining
 * it in all the subclass of potential. Those subclass that really need
 * this method would overwrite it. (at the moment the method is overwriten 
 * only in PotentialTable).
 *
 * @param a potential that have to be a subset of the potential which
 * receives the message
 */

public void combineWithSubset(Potential p) {   

  System.out.println("At the moment the method (Potential)combineWithSubset is not implemented for "
				+ this.getClass().getName());
  System.exit(0);
}


/**
 * This method incorporates the evidence passed as argument to the
 * potential, that is, put to 0 all the values whose configurations
 * are not consistent with the evidence
 * 
 * @param ev a configuration representing the evidence    
 *
 * The method is not defined as abstract to avoid the need of definining
 * it in all the subclass of potential. Those subclass that really need
 * this method would overwrite it. (at the moment the method is overwriten 
 * only in PotentialTable).
 */


public void instantiateEvidence(Configuration evid) {  

  System.out.println("At the moment the method (Potential)instantiateEvidence is not implemented for "
				+ this.getClass().getName());
  System.exit(0);
}


/**
 * This method divides two Potentials.
 * For the exception 0/0, the method compute the result as 0.
 * The exception ?/0: the method abort with a message in the standar output.
 * @param p the PotentialTable to divide with this.
 * @return a new PotentialTable with the result of the combination.  
 * 
 * The method is not defined as abstract to avoid the need of definining
 * it in all the subclass of potential. Those subclass that really need
 * this method would overwrite it. (at the moment the method is overwriten 
 * only in PotentialTable).
 */


public Potential divide(Potential p) {  

  System.out.println("At the moment the method (Potential)divide is not implemented for "
				+ this.getClass().getName());
  System.exit(0);
  return this; // the method never will arrive until this point
}



/**
 * Marginalizes over a set of variables using MAXIMUM as marginalization
 * operator. 
 *
 * @param vars a vector of variables.
 * @return a Potential with the marginalization over vars.    
 *
 * The method is not defined as abstract to avoid the need of definining
 * it in all the subclass of potential. Those subclass that really need
 * this method would overwrite it. (at the moment the method is overwriten 
 * only in PotentialTable).
 */


public Potential maxMarginalizePotential(Vector vars){

  System.out.println("At the moment the method (Potential)maxMarginalizePotential is not implemented for "
				+ this.getClass().getName());
  System.exit(0);
  return this; // the method never will arrive until this point
}

/**
 * @return the configuration of maximum probability included in the
 * potential, that is consistent with the subConfiguration passed as
 * parameter (this subconfiguration can be empty)
 *
 * NOTE: if there are more than one configuration with maximum
 * probability, the first one is returned
 *
 * @param subconf the subconfiguration to ensure consistency     
 *
 * The method is not defined as abstract to avoid the need of definining
 * it in all the subclass of potential. Those subclass that really need
 * this method would overwrite it. (at the moment the method is overwriten 
 * only in PotentialTable).
 */


public Configuration getMaxConfiguration(Configuration subconf){

  System.out.println("At the moment the method (Potential)getMaxConfiguration is not implemented for "
				+ this.getClass().getName());
  System.exit(0);
  return subconf; // the method never will arrive until this point
}


/**
 * @return the configuration of maximum probability included in the
 * potential, that is consistent with the subConfiguration passed as
 * parameter (this subconfiguration can be empty), and differents to
 * all the configurations passed in the vector
 *
 * NOTE: if there are more than one configuration with maximum
 * probability, the first one is returned
 *
 * @param subconf the subconfiguration to ensure consistency
 * @param list the list of configurations to be differents  
 *
 * The method is not defined as abstract to avoid the need of definining
 * it in all the subclass of potential. Those subclass that really need
 * this method would overwrite it. (at the moment the method is overwriten 
 * only in PotentialTable).
 */


public Configuration getMaxConfiguration(Configuration subconf,
                                         Vector list) {

  System.out.println("At the moment the method (Potential)getMaxConfiguration is not implemented for "
				+ this.getClass().getName());
  System.exit(0);
  return subconf; // the method never will arrive until this point
}


} // end of class


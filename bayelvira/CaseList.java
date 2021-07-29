/**
 * This class implements a case list 
 */

import java.util.*;
import Potential;
import Configuration;

public abstract class CaseList extends Potential{

    /**
     * variable indicating the number of cases
     */

private int numberOfCases;  
  

/**
 * Gets the number of instances from the case list for a given
 * configuration of variables.
 * @param conf a configuration of variables.
 * @return the value of the potetial for conf.
 */

abstract double getValue(Configuration conf);


/**
 * VERY IMPORTANT!!!!! This method IS NOT APPLICABLE in caseList
 * @param conf a configuration of variables.
 * @param val the value for conf.
 */

abstract void setValue(Configuration conf, double val); 


/**
 * Gets the size of the case list.
 * @return the size of array in memory mode;
 *
 */

public long getSize(){

 return (numberOfCases*variables.size());


}



/**
 * @return the sum of all the values of a potential. In this case is the same
 * of number of cases in case list.
 */

public double totalPotential(){

  return ((double)numberOfCases);
}


/**
 * @param conf a configuration of variables.
 * @return the sum of the instances of a case list
 * restricted to configuration conf.
 */

abstract double totalPotential(Configuration conf);

/**
 * Computes the entropy of a case list.
 * @return the sum of the values x Log x stored in the case list.
 */

abstract double entropyPotential();


/**
 * Computes the entropy of a case list restricted to
 * a given configuration.
 * @param conf the configuration.
 * @return the sum of the values x Log x fixing configuration conf.
 */

abstract double entropyPotential(Configuration conf);

/**** acces methods ***********/

public int getNumberOfCases(){
   return numberOfCases;
}

public void setNumberOfCases(int casesNumber){
   numberOfCases = casesNumber;
}

/******************************/


/**
 * this method store a case configuration at position pos on case list
 * @param int pos
 * @param Configuration conf
 * @return boolen . true if the operation is succesful.
 */


abstract  boolean put(Configuration conf);


/**
 * this method gets a case configuration from the position pos on case list
 * @param int pos.
 * @return Configuration. This configuration is null if the operation is not succeful
 */


abstract Configuration get(int pos);


/**
 * Marginalizes over a set of variables. It is equivalent
 * to addVariable over the other variables.
 * @param vars a vector of variables.
 * @return a Potential with the marginalization over vars.    
 */


public Potential marginalizePotential(Vector vars){

    System.out.println("marginalizePotential is not implemented in CaseList");
    return null;	
}

}






















/* Prueba del fichero PotFunciones.java */

import java.util.Vector;


/** Abstract class Function: Specifies those methods wich are common to all
  * kind of functions, particularly when these functions are considered as
  * PotentialFunction.
  * The basic structure includes the name of the function and a integer
  * representing its code number.
  * @see PotentialFunction
  * Last modifided:30/11/99
  * @author Juan F. Huete.
  * @version 1.0
  */


public abstract class Function {
    int  tp;
    String nombre;




/**  Restrict the variables in inputPot to the  values given in the Configuration
  * conf.
  * @return a Potential
  */
  abstract Potential restrictFunctionToVariable(
                           PotentialFunction inputPot, Configuration conf);


/** Marginalizes over a set of variables
  * @param vars,  a vector of variables
  * @return a Potential with  the marginalization over vars
  */

   abstract Potential marginalizeFunctionPotential(Vector vars);

/** Removes the argument variable suming over all its values
  * @param potVar the set of variables in the original Potential
  * @param var - a FiniteStates variable
  * @return a new Potential with the result of the deletion
  */

  abstract Potential  functionAddVariable(Vector potVar, Vector vars);

/** Evaluate the function using the configuration conf. In order to evaluate
  * this function we will use the set of arguments (arg).
  * @return the value of this function.
  */

  abstract double PotValue(double arg[],Configuration conf);


/** @return type  of this function
  */

    public int getTipe( )
    {
	return tp;
    }

/** @return the name of this function
  */
    public String getName()
    {
	return nombre;
    }

   


}


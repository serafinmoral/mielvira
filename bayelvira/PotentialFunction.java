import java.util.Vector;
import java.util.Hashtable;
import java.io.*;
import Potential;
import Configuration;
import Relation;
import RelationList;

/**
 * Implementation of class PotentialFunction. A potential represented
 * by a function.
 * @author J. Huete
 * Last modified : 17/11/99
*/

public class PotentialFunction extends Potential {

    Function function; // Function stored in the potential.
    Vector arguments;    // Arguments of potActive function (the arguments needed
    					 // by the funciton stored in the potential
    Vector strArg; // The same set of arguments represented by String 
    long size;    // not used
    String name;  // The name of this potential
    


/**
 * Creates a new PotentialFunction with no variables and no function
*/
    
PotentialFunction() {

  variables = new Vector();
  arguments = new Vector();
  strArg = new Vector();
  size=0;
  name = new String("default");
}


/**
 * Creates a new PotentialFunction.
 * @param vars variables that the potential will contain.
*/
	 
PotentialFunction(Vector vars) {

  variables = (Vector)vars.clone();
  arguments = new Vector();
  strArg = new Vector();
  name = new String("default");
  size=0;
}


/**
 * Creates a new PotentialFunction
 * @param vars variables that the potential will contain,
 * given as a NodeList.
*/
	 
PotentialFunction(NodeList vars) {

  variables = (Vector)vars.nodes.clone();
  arguments = new Vector();
  strArg = new Vector();
  name = new String("default");
  size=0;
}



/**
  * Program for performing experiments.
  * The arguments are as follows.
  * 1. Input file: the network.
  * 2. Output file.
  * 3. Evidence file.
  * If the evidence file is omitted, then no evidences are
  * considered
*/
public static void main(String args[]) throws ParseException, IOException {
   Bnet b;
   FileInputStream networkFile;
   FileWriter fileout;
   int i,j,nv;
   Vector V;
   RelationList L = new RelationList();
   Configuration observation, conf;
   Relation r;
   Potential auxpot;
   FunctionSumNormIdf f=null;

   if (args.length<2)
     System.out.println("Too few arguments. Arguments are: ElviraFile outputfile");
   else {
     networkFile = new FileInputStream(args[0]);
     b = new Bnet(networkFile);
     fileout = new FileWriter(args[1]);
     b.saveBnet(fileout);
     fileout.close();
     System.out.println(" Red "+b.getName());

    
     V =  b.getRelationList();

     L.setRelations(V);
     L.repairPotFunctions();

   //      for (i=0; i<L.size();i++){
//    	   r = (Relation) L.elementAt(i);
//    	   if (r.getValues().getClass().getName().equals("PotentialFunction"))
//    	      r.setValues(((PotentialFunction)r.getValues()).potentialFunctionToTable());
//    	      r.getValues().print();
//    	      }
	
	
       for (i=0; i<L.size();i++){
	   r = (Relation) L.elementAt(i);
	   if (r.getValues().getClass().getName().equals("PotentialFunction")) {
	      System.out.println("Antes de Cambio");
	   	  f = ((FunctionSumNormIdf)( (PotentialFunction)r.getValues()).getFunction());
	      r.setValues(f.sumToAddNormIdf( (PotentialFunction)r.getValues()));
	      r.getValues().print();
	      r.setValues(((PotentialFunction)r.getValues()).potentialFunctionToTable());
	      r.getValues().print();
	      }
       }

   //    for (i=0; i<L.size();i++){
//  	   r = (Relation) L.elementAt(i);
//  	   r.getValues().print();
//         nv = (int)FiniteStates.getSize(r.getVariables());
//  	   conf = new Configuration(r.getVariables());
//  	   System.out.println("Relacion :"+ 
//  			    ((Relation) L.elementAt(i)).getName());
//  	   for (j=0;j<nv;j++){
//  	      conf.print();
//  	      System.out.println(" Eval = "+
//  		       ((Relation) L.elementAt(i)).getValues().getValue(conf));
//  	      conf.nextConfiguration();
//  	   }
//      } // fin del for i=0;


    //    for (i=0; i<L.size();i++){
//         r = (Relation) L.elementAt(i);
//         System.out.println(" \n Antes de Entrar \n");
//         r.getValues().print();
//         observation = new Configuration(r.getVariables());
//         if (observation.size() >1) 
//  	   { 
//  	       if (observation.size()>2) observation.remove(2);
//                 observation.remove(0);
	       
//  	       System.out.print("Evidencias "+
//  				observation.getVariable(0).getName());
//  	       observation.print();
	
//  	       auxpot = r.getValues().restrictVariable(observation);
//  	       r = new Relation();
//  	        r.setValues(auxpot);
//  		r.setVariables(auxpot.getVariables());
//         System.out.println(" \n Tras restringir Variables \n");
//         r.getValues().print();
//             nv = (int)FiniteStates.getSize(r.getVariables());
//  	   conf = new Configuration(r.getVariables());
//  	   System.out.println("Relacion :"+ r.getName());
//  	   for (j=0;j<nv;j++){
//  	      conf.print();
//  	      System.out.println(" Eval = "+
//  		       r.getValues().getValue(conf));
//  	      conf.nextConfiguration();

//  	   }
//  	}
//        }

    }
}
	


/**
 * Assigns a given function
 * @param funct the Function to be assigned.
*/

public void setFunction(Function funct) {

  function = funct;
  size = 0;
}

/**
 * Assigns a given function
 * @param s the name of the function to be assigned. Is the mane of a Function class
 * that must exists
 */

public void setFunction(String s)
{
    if (s.equals("NormIdf"))
    	function = new FunctionNormIdf();
    else if (s.equals("AddNormIdf"))
	    function = new FunctionAddNormIdf();
	else if (s.equals("SumNormIdf"))
		function = new FunctionSumNormIdf();
    //  else function = new Function(s);
}


/**
 * @return the function associated with the potential.
*/

public Function getFunction(){
  
  return function;
}

/**
 * @return the argument associated with the potential.
*/

public Vector getArguments(){

 return arguments;
}
/**
 * @return the number of values (size) of the potential.
*/

public long getSize() {

  return size;
}


/**
 * Saves a function to a file.
 * @param p the PrintWriter where the function will be written.
*/   

public void save(PrintWriter p) {

    int i;

           
  p.print("values= function  \n");
  p.print("          "+ function.getName()+"(");
  for (i=0;i<strArg.size();i++)
{
      p.print(strArg.elementAt(i));
      if (i+1!=strArg.size()) p.print(",");
  }
  p.print(");");
  p.print("\n\n");
}


/**
 * Prints a PotentialFunction to the standard output.
 */

public void print() {
    int i;

 System.out.print("Nodes ");

  for (i=0 ; i<variables.size() ; i++)
    System.out.print(((FiniteStates)variables.elementAt(i)).getName()+" ");

  System.out.print("\n");            
  System.out.print("values= function \n");
  System.out.print("          "+ function.getName()+"(");
  for (i=0;i<strArg.size();i++){
      System.out.print(strArg.elementAt(i));
      if (i+1!=strArg.size()) System.out.print(",");
  }
  System.out.print(");");
  System.out.print("\n\n");

}


/**
 * Gets the value for a Configuration.
 * If one of the arguments is a Potential, we first evaluate this Potential with
 * the given Configuration conf, and the result is used as an argument of the function.
 * @param conf a Configuration.
 * @return the value corresponding to configuration conf.
*/

public double getValue(Configuration conf) {

   Object obj=null;
   Double d = new Double(0.0);
   Potential p;
   Class clobj;
   Configuration auxConf;
   double argAux[] = new double[arguments.size()];
   int i;
	
    FiniteStates tmp;
	
    System.out.println("En Potential Function");
	for (i=0;i<conf.getVariables().size(); i++)
          {   tmp = (FiniteStates)conf.getVariable(i);
              System.out.print("  "+tmp.getName() );
          }
	conf.print();
	
 
   for (i=0;i<arguments.size(); i++){

       if (arguments.elementAt(i).getClass()==d.getClass()){
       System.out.println("Arg doble");
	   d = (Double) (arguments.elementAt(i));
	   argAux[i]= d.doubleValue();
        } else
       {System.out.println("Arg. Funcion");
	   argAux[i]= ((Potential)(arguments.elementAt(i))).getValue(conf);
	   }
   }
   auxConf = new Configuration(variables,conf);
    	
   return function.PotValue(argAux,auxConf);

}


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


public double getValue(Hashtable positions, int[] conf) {

  Configuration auxConf;
  FiniteStates var;
  int i, p, s;
  
  auxConf = new Configuration();
  
  s = variables.size();
 //ESZTA VARIABLE la ! no existe, esta anulada por ser insertada como evidencia
  //  p = ((Integer)positions.get((FiniteStates)variables.elementAt(1))).intValue();


  System.out.println("Variables ="+s);		
  for (i=0;i<s;i++)
       System.out.println(((FiniteStates)variables.elementAt(i)).getName()+"=" +conf[i]);	

  for (i=0 ; i<s ; i++) {
    var = (FiniteStates)variables.elementAt(i);
    System.out.println("  "+var.getName());

    p = ((Integer)positions.get(var)).intValue();
    System.out.println("pos "+p);
    auxConf.insert(var,conf[p]);
  }

  auxConf.print();

  System.out.println("Estamos en getValue con hashtable Ahora a evaluar");

  return getValue(auxConf);

  
}
  

/** Transforms a PotentialFunction into a PotentialTable
*/

public PotentialTable potentialFunctionToTable(){

	PotentialTable potTable = new PotentialTable(variables);
	Configuration conf = new Configuration(variables);
	int nv,j;
	
	nv = (int)FiniteStates.getSize(variables);	
	for (j=0;j<nv;j++){
	      potTable.setValue(conf,getValue(conf));
	      conf.nextConfiguration();
	   }
	return potTable;
}



/** restrictVariable restrict the PotentialFunction to a given configuration
  * @param conf the configuration
  *  @return the restricted values
  */

 public Potential restrictVariable(Configuration conf) {	

	   return function.restrictFunctionToVariable(this, conf);
    }


/** setArgumentAt set a Potential p as the argument (in position i) of this function
*/

   public void setArgumentAt( Potential p, int i)
    {
	arguments.setElementAt(p,i);
    }

/** setArgumentAt set a Double p as the argument (in position i) of this function
*/
    public void setArgumentAt(Double d,int i)
    {
	arguments.setElementAt(d,i);
    }

/** getStrArgument Is needed in order to save, print,... the PotentialFunction
  * @return the String representing the argument (in position i) in this function
  */

    public  String getStrArgument(int i)
    {
	return (String) strArg.elementAt(i);
    }
 
/** addArguments includes a new double value as an argument of this function.
  * @param d the value
  */


    public void addArguments( double d)
    {
       Double D;
       D=new Double(d);
       arguments.addElement(D);
       strArg.addElement(D.toString());
    }
    

/** addArguments includes a new argument s of this function.
  * s must be a name of a known relation in the relation list. We can use
  * the function repairPotFunction in RelationList to include the PotentialFunction
  * stored in the relation s as the argument of this function.
  * @param  s  a string that must represent the name of  a known relation
  * @see RelationList#repairPotFunction
  */

    public void addArguments(String s){
	strArg.addElement(s);
	arguments.addElement(null);
    }	

/** addArguments includes a new Potential p as an argument of this fucntion
*/

    public void addArguments(Potential p){
    
    arguments.addElement(p);
    strArg.addElement("Unknow");
    }

/** Marginalizes over a set of variables
  * @param vars,  a vector of variables
  * @return a Potential with  the marginalization over vars
  */
    public Potential marginalizePotential(Vector vars){

     return function.marginalizeFunctionPotential(vars);

    }

/** Removes the argument variable suming over all its values
  * @param var - a FiniteStates variable
  * @return a new Potential with the result of the deletion
  */

public Potential addVariable(Vector vars) {

  return function.functionAddVariable(variables,vars);


}

/** Removes the argument variable suming over all its values
  * @param temp - a FiniteStates variable
  * @return a new Potential with the result of the deletion
  */

 public Potential addVariable(FiniteStates temp) {

        Vector v;

	v = new Vector();
        v.addElement(temp);
        return (addVariable(v));
	
    }



/********************************************************************
 *********** NOT IMPLEMENTED FUCNTION *******************************
 ********************************************************************/


/**
 * @return the sum of all the values in the potential.
*/

public double totalPotential() {
 
    System.out.println("totalPotential in PotentialFunction");
    return 1.0;
}


/**
 * @param conf a Configuration.
 * @return the sum of all the values in the potential
   matching with configuration C. The result is the same
   as restricting the potential to C and then using
   totalPotential()
 * @see totalPotencial()
*/

public double totalPotential(Configuration conf) {

  Configuration auxConf;
  FiniteStates temp;
  int i, nv;
  double sum;

  nv = 1;
  for (i=0 ; i<variables.size() ; i++) {
    temp = (FiniteStates)variables.elementAt(i);
    nv = nv * temp.getNumStates();
  }

  // Evaluate the tree for all the possible configurations
  // and sum the values.
  
  auxConf = new Configuration(variables,conf);
  sum = 0.0;
  for (i=0 ; i<nv ; i++) {
    sum += getValue(auxConf);
    auxConf.nextConfiguration(conf);
  }

  return sum;
}

    /* 
     * Otras Funciones
     *
     */


    public Potential combine(Potential p) { 
    System.out.println("Combine de PotentialFunction");
    print();
    p.print();
    return p;

    }


    public void saveResult(PrintWriter P)
    {System.out.println(" saveResult in PotentialFunction");
    }

    public void normalize() 
    {System.out.println(" normalize in PotentialFunction");
    }

    public double entropyPotential() {
    System.out.println(" entropyPotential in PotentialFunction");
    return 1.0;
    }


    public double entropyPotential(Configuration conf) {
    System.out.println(" entropyPotential in PotentialFunction");
    return 1.0;
    }

    public void setValue(Configuration conf, double x) {
	System.out.println(" setValue in PotentialFunction");
    }


}















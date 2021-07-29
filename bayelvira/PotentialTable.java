/* PotentialTable.java */

import java.util.Vector;
import java.util.Hashtable;
import FiniteStates;
import Potential;
import PotentialTree;
import java.io.*;
import java.util.Random;

/**
 * Implements object Potential with an array of double.
 *
 * @since 11/11/99
 */


public class PotentialTable extends Potential {

double[] values;
  

/**
 * Constructs a new potential with an empty list of variables
 * and a single value.
 */

PotentialTable() {

  variables = new Vector();
  values = new double[1];
}


/**
 * Constructs a new potential with an empty list of variables
 * and a given number of values in the array.
 * @param numberOfValues the numbero fo values in the array.
 */

PotentialTable(int numberOfValues) {

  variables = new Vector();
  values = new double[numberOfValues];
}   


/**
 * Constructs a new PotentialTable for a single variable
 * and sets the values to 0.0 .
 * @param var a FiniteStates variable.
 */

PotentialTable(FiniteStates var) {
 
  int i, nv;
  
  nv = var.getNumStates();
  variables = new Vector();
  variables.addElement(var);
  values = new double[nv];
  
  for (i=0 ; i<nv ; i++)
    values[i] = 0.0;
}


/**
 * Constructs a new PotentialTable for a list of
 * variables and creates an array to store the values.
 * @param vars a Vector of variables.
 */

PotentialTable(Vector vars) {

  int nv;
  
  // Compute the size of the array.
  nv = (int)FiniteStates.getSize(vars);

  variables = (Vector)vars.clone();
  values = new double[nv]; 
}


/**
 * Constructs a new PotentialTable for a list of
 * variables and creates an array to store the values.
 * @param vars a NodeList.
 */

PotentialTable(NodeList vars) {

  int nv;
  
  // Compute the size of the array.
  nv = (int)FiniteStates.getSize(vars);

  variables = (Vector)vars.nodes.clone();
  values = new double[nv]; 
}


/**
 * Constructs a PotentialTable from a PotentialTree.
 * @param tree a PotentialTree.
 */

PotentialTable(PotentialTree tree) {
  
  int i, nv; 
  Configuration conf;
  
  variables = (Vector) tree.getVariables().clone();
  nv = (int)FiniteStates.getSize(variables); // Size of the array.
  conf = new Configuration(variables);
  values = new double[nv];
  
  // Evaluate the tree for each possible configuration.
  for (i=0 ; i<nv ; i++) {
    values[i] = tree.getTree().getProb(conf);
    conf.nextConfiguration();
  } 
}

/**
 * Constructs a PotentialTable from a PotentialMTree.
 * @param tree a PotentialMTree.
 */

PotentialTable(PotentialMTree tree) {

  int i, nv;
  Configuration conf;

  variables = (Vector) tree.getVariables().clone();
  nv = (int)FiniteStates.getSize(variables); // Size of the array.
  conf = new Configuration(variables);
  values = new double[nv];

  // Evaluate the tree for each possible configuration.
  for (i=0 ; i<nv ; i++) {
    values[i] = tree.getTree().getProb(conf);
    conf.nextConfiguration();
  }
} 

/**
 * Constructs a PotentialTable from a nodelist and a relation
 * defined over a subset of variables of the nodelist passed as parameter.
 * If the potential passed as parameter is not defined over a subset
 * of the variables in this, the method build a unitary potential
 *
 * @param vars the node list of variables over for the new potential
 * @param rel the relation defined over a subset of vars
 */

PotentialTable(NodeList vars, Relation rel) {
  
  int i, nv, pos; 
  Configuration conf,subConf;
  
  variables = (Vector)vars.toVector().clone();
  nv = (int)FiniteStates.getSize(variables); // Size of the array.
  values = new double[nv];
  
  // determining if pot.variables is a subset of vars
  
  if ( (rel.getVariables().kindOfInclusion(vars)).equals("subset") ){  
    conf = new Configuration(variables);
    
    for (i=0 ; i<nv ; i++)
      values[i] = 0.0;
      
    for (i=0 ; i<nv ; i++) {
      subConf = new Configuration(conf,rel.getVariables());
      pos = subConf.getIndexInTable();
      values[i] += ((PotentialTable)rel.getValues()).getValue(pos);
      conf.nextConfiguration();
    }
  }
  else{ // unitary potential
    for (i=0 ; i<nv ; i++)
      values[i] = 1.0;
  }
}


/**
 * Constructs a new Conditional P(X|Z) PotentialTable for a list of
 * variables and creates an array to store the values generated randomly.
 * @param generator. One instance of the class Random.
 * @param vars a NodeList.
 */

PotentialTable(Random generator, NodeList nodes, int degreeOfExtreme){

int nv,i;
double sum,r;
Configuration conf = new Configuration(nodes);
PotentialTable potmarg,pot;


nv=(int)FiniteStates.getSize(nodes);

variables = nodes.copy().toVector();
values = new double[nv];
 
 sum = 0.0;
 for(i=0 ; i < nv  ; i++){
     r = generator.nextDouble();
     values[conf.getIndexInTable()] = Math.pow(r, (float) degreeOfExtreme);
     conf.nextConfiguration();
 }

 normalize();
 if(variables.size()>1){
     potmarg = (PotentialTable)this.addVariable((FiniteStates)
						variables.elementAt(0));
     pot = (PotentialTable)this.divide(potmarg);
     values = pot.values;
 }
}




/**
 * @return the vector of variables of the potential.
 */

public Vector getVariables(){
  
  return(variables);  
}  


/**
 * @param conf a configuration.
 * @return the value of the potential for configuration conf.
 */

public double getValue(Configuration conf) {

  int pos;
  Configuration aux;

  // Take a configuration from conf just for variables
  // in the potential.
  aux = new Configuration(variables,conf);
  pos = aux.getIndexInTable();
  
  return values[pos];
}


/**
 * @return the value at position index in the array.
 */

public double getValue(int index) {
  
  return values[index];
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
  
  for (i=0 ; i<s ; i++) {
    var = (FiniteStates)variables.elementAt(i);
    p = ((Integer)positions.get(var)).intValue();
    auxConf.insert(var,conf[p]);
  }
  
  p = auxConf.getIndexInTable();
  
  return values[p];
}


/**
 * Sets the value in a position in the array of values.
 * @param index the position in the array.
 * @param value the value.
 */

public void setValue(int index, double value) {
  
  values[index] = value;
}


/**
 * Sets the value for a configuration of variables.
 * @param conf a configuration of variables.
 * @param value a real value.
 */

public void setValue(Configuration conf, double value) {

  int index;

  index = conf.getIndexInTable();
  values[index] = value;
}


/**
 * Sets all the values in a potential to a given value.
 * @param value a real value.
 */

public void setValue(double value) {
  
  int index;
    
  for (index=0 ; index<values.length ; index++)
    values[index] = value;
} 


/**
 * Increments the i-th value of the potential.
 * @param i the value to increment.
 * @param increment the increment.
 */

public void incValue(int i, double increment) {
 
  values[i] += increment;
}


/**
 * @return the size of the potential (number of values).
 */

public long getSize() {

  return values.length;
}


/**
 * Saves a potential in a file. This one must be used when
 * saving a network.
 * @param p the PrintWriter where the potential will be written.
 */

public void save(PrintWriter p) {

  int i, total; 
  Configuration conf;
  
  p.print("values= table ( \n");
  
  total = (int)FiniteStates.getSize(variables);
  
  conf = new Configuration(variables);
  
  for (i=0 ; i<total ; i++) {
    p.print("                ");
    conf.save(p);
    p.print(" = "+values[i]+",\n");
    conf.nextConfiguration();
  }
  p.print("                );\n");
}


/**
 * Saves a potential in a file. This one must be used to
 * save the results of a propagation, not to save a network.
 * @param p the PrintWriter where the potential will be written.
 */

public void saveResult(PrintWriter p) {

  int i, total; 
  Configuration conf;
  
  for(i=0;i<variables.size();i++){
    p.println("node "+((FiniteStates)variables.elementAt(i)).getName());
  }
  
  save(p);
}


/**
 * Prints this potential to the standard output.
 */

public void print() {

  int i, total; 
  Configuration conf;

  System.out.print("Nodes ");
  
  for (i=0 ; i<variables.size() ; i++)
    System.out.print(((FiniteStates)variables.elementAt(i)).getName()+" ");
  
  System.out.print("\n");
  System.out.print("values= table ( \n");
  
  total = (int)FiniteStates.getSize(variables);
  
  conf = new Configuration(variables);
  
  for (i=0 ; i<total ; i++) {
    System.out.print("                ");
    conf.print();
    System.out.print(" = "+values[i]+",\n");
    conf.nextConfiguration();
  }
  System.out.print("                );\n");
}


/**
 * Restricts the potential to a configuration.
 * @param conf a Configuration.
 * @return A new PotentialTable result of the restriction.
*/

//public PotentialTable restrictVariable(Configuration conf) {
public Potential restrictVariable(Configuration conf) {

  Configuration auxConf;
  Vector aux;
  FiniteStates temp;
  PotentialTable pot;
  int i;

  // Creates a configuration preserving the values in conf.
  auxConf = new Configuration(variables,conf);

  // Computes the list of variables of the new Potential.
  aux = new Vector();
  for (i=0 ; i<variables.size() ; i++) {
    temp = (FiniteStates)variables.elementAt(i);
    if (conf.indexOf(temp)==-1)
      aux.addElement(temp);
  }

  pot = new PotentialTable(aux);

  for (i=0 ; i<pot.values.length ; i++) {
    pot.values[i] = getValue(auxConf);
    auxConf.nextConfiguration(conf);
  }

  return pot;
}


/**
 * Sums over all the values of variables in a list.
 * @param vars a vector containing variables.
 * @return a new PotentialTable without the variables in vars.
 */

public PotentialTable addVariable(Vector vars) {

  Vector aux;
  FiniteStates temp;
  int i, pos;
  Configuration auxConf1, auxConf2;
  PotentialTable pot;

  aux=new Vector();
  
  // Creates the list of variables of the new potential.
  for (i=0 ; i<variables.size() ; i++) {
    temp = (FiniteStates)variables.elementAt(i);
    if (vars.indexOf(temp)==-1)
      aux.addElement(temp);
  }

  // Creates the new potential and sets the values to 0.0
  pot=new PotentialTable(aux);

  for (i=0 ; i<pot.values.length ; i++) 
    pot.values[i]=0.0;

  // Now for each configuration of the old potential, take
  // its value and see with which configuration of the new
  // one it corresponds. Then increment the value of the
  // new potential for that configuration.

  auxConf1=new Configuration(variables);
  
  for (i=0 ; i<values.length ; i++) {
    auxConf2 = new Configuration(auxConf1,vars);
    pos = auxConf2.getIndexInTable();
    pot.values[pos] += values[i];
    auxConf1.nextConfiguration();
  }

  return pot;
}


/**
 * Removes the argument variable summing over all its values.
 * @param var a FiniteStates variable.
 * @return a new PotentialTable with the result of the deletion.
 */

//public PotentialTable addVariable(FiniteStates var) {

public Potential addVariable(FiniteStates var) {

  Vector v;
    
  v = new Vector();
  v.addElement(var);
  return (addVariable(v));
}


/**
 * Marginalizes over a set of variables. It is equivalent
 * to addVariable over the other variables.
 * @param vars a vector of variables.
 * @return a PotentialTable with the marginalization over vars.
 */

public Potential marginalizePotential(Vector vars) {

  Vector vars2;
  int i;
  FiniteStates temp;
  PotentialTable pot;

  vars2 = new Vector();
  for (i=0 ; i<variables.size() ; i++) {
      temp = (FiniteStates)variables.elementAt(i);
      if (vars.indexOf(temp)==-1)
	vars2.addElement(temp);
  }

  pot = addVariable(vars2);

  return pot;
}


/**
 * Marginalizes over a set of variables using maximun as 
 * marginalization operator. 
 *
 * @param vars a vector of variables.
 * @return a Potential with the max-marginalization over vars.
 */

public Potential maxMarginalizePotential(Vector vars) {

  int i,pos;
  PotentialTable pot;
  Vector vars2;
  Configuration conf,subConf;
  SetVectorOperations svo = new SetVectorOperations();  

  // creates the new potential and sets the values to 0.0
  pot = new PotentialTable(vars);

  for(i=0; i<pot.values.length; i++) 
    pot.values[i]=0.0;

  // Store in vars2 the variables presented in "variables" but 
  // not included in vars

  vars2 = svo.notIn(variables,vars);

  // Now for each configuration of the old potential, take
  // its value and see with which subconfiguration of the new
  // one it corresponds. If the new value is greater than
  // the value stored until this moment for the subconfiguration 
  // then set the value as new value for the potential.

  conf=new Configuration(variables);

  for (i=0 ; i<values.length ; i++) {
    subConf = new Configuration(conf,vars2);
    pos = subConf.getIndexInTable();
    if (values[i] > pot.values[pos])
      pot.values[pos] = values[i];
    conf.nextConfiguration();
  }
    

  return pot;
}



/**
 * @return the sum of all the values in the potential.
 */

public double totalPotential() {

  int i;
  double sum = 0.0;

  for (i=0 ; i<values.length ; i++)
    sum += values[i];

  return sum;
}


/**
 * Computes the sum of all the values of the potential
 * restricted to a configuration.
 * @param conf a configuration of variables.
 * @return the sum.
 */

public double totalPotential(Configuration conf) {

  Configuration auxConf;
  Vector aux;
  FiniteStates temp;
  int i, nv;
  double sum;

  aux = new Vector();
  
  for (i=0 ; i<variables.size() ; i++) {
    temp = (FiniteStates)variables.elementAt(i);
    if (conf.indexOf(temp)==-1)
      aux.addElement(temp);
  }

  // Number of values of the restricted potential.
  nv = (int)FiniteStates.getSize(aux);

  // Configuration preserving the values in conf.
  auxConf = new Configuration(variables,conf);
  
  sum = 0.0;
  for (i=0 ; i<nv ; i++) {
    sum+=getValue(auxConf);
    auxConf.nextConfiguration(conf);
  }

  return sum;
}


/**
 * @return the entropy of the potential.
 */

public double entropyPotential() {

  int i;
  double x, sum = 0.0;

  for (i=0 ; i<values.length ; i++) {
    x = values[i];
    if (x>0.0)
      sum += x*Math.log(x);
  }
  return ((-1.0)*sum);
}


/**
 * Computes the entropy of this potential restricted to
 * a configuration of variables.
 * @param conf a configuration of variables.
 * @return the entropy.
 */

public double entropyPotential(Configuration conf) {

  Configuration auxConf;
  Vector aux;
  FiniteStates temp;
  int i, nv;
  double sum, x;

  aux = new Vector();
  for (i=0 ; i<variables.size() ; i++) {
    temp = (FiniteStates)variables.elementAt(i);
    if (conf.indexOf(temp)==-1)
      aux.addElement(temp);
  }

  // Size of the restricted potential.
  nv = (int)FiniteStates.getSize(aux);
  
  // Configuration preserving the values in conf.
  auxConf = new Configuration(variables,conf);
  
  sum = 0.0;
  for (i=0 ; i<nv ; i++) {
    x = getValue(auxConf);
    if (x>0.0)
      sum += x*Math.log(x);
    auxConf.nextConfiguration(conf);
  }

  return (-1.0*(sum));
}

/**
 * Computes the cross entropy of this potential.
 * We assume that the last n-2 variables of 
 * the potential are the condicional subset.
 * @param conf a configuration of variables.
 * @return the entropy.
 */

public double crossEntropyPotential() {

  Configuration auxConf,confyz,confxz,confz;
  PotentialTable pyz,pz,pxz;
  Vector aux,varsyz,varsxz,varsz;
  FiniteStates temp;
  int i, nv;
  double sum, valxyz,valyz,valxz,valz,valxZ,valyZ,valxyZ;

  // Size of the restricted potential.
  nv = (int)FiniteStates.getSize(variables);

 
  // Configuration preserving the values in variables.
  auxConf = new Configuration(variables);

  // Compute subsets of variables
  varsyz = (Vector)variables.clone();
  varsyz.removeElementAt(0); // remove variable x
  varsxz = (Vector)variables.clone();
  varsxz.removeElementAt(1); // remove variable y
  varsz = (Vector)varsxz.clone();
  varsz.removeElementAt(0); // remove variables x and y

  // Compute the marginals Potentials Tables

  //pyz = addVariable((FiniteStates)variables.elementAt(0));
  //pxz = addVariable((FiniteStates)variables.elementAt(1));
  pyz = (PotentialTable)addVariable((FiniteStates)variables.elementAt(0));
  pxz = (PotentialTable)addVariable((FiniteStates)variables.elementAt(1));


  if(varsz.size() > 0){
      //pz = addVariable((FiniteStates)variables.elementAt(0));
      //pz = (PotentialTable)pz.addVariable((FiniteStates)variables.elementAt(1));
      pz = (PotentialTable)addVariable((FiniteStates)variables.elementAt(0));
      pz = (PotentialTable)pz.addVariable((FiniteStates)variables.elementAt(1));
  }else{
      pz = new PotentialTable();
  }

  sum = 0.0;
  for(i=0 ; i < nv ; i++){
      valxyz = getValue(auxConf);
      if (valxyz > 0.0){
	  confyz = new Configuration(varsyz,auxConf);
	  confxz = new Configuration(varsxz,auxConf);
	  if(varsz.size() > 0){
	      confz = new Configuration(varsz,auxConf);
	  }else{
	      confz = new Configuration();
	  }
	  valyz = pyz.getValue(confyz);
	  valxz = pxz.getValue(confxz);
	  if(varsz.size() > 0){
	      valz = pz.getValue(confz);
	  }else{
	      valz=1.0;
	  }
	  valxyZ = valxyz / valz;
	  valyZ = valyz / valz;
	  valxZ = valxz / valz;
	  sum += (valxyz * Math.log(valxyZ/(valxZ*valyZ)));
      }
      auxConf.nextConfiguration();
  }
  
  return(sum);

}

/**
 * Combines two Potentials. The argument p must be a subset of
 * the potential which receives the message, and must be a PotentialTable
 *
 * IMPORTANT: this method modify the object which receives the message. 
 *
 * @param p the PotentialTable to combine with this.
 */

public void combineWithSubset(Potential p) {

  Vector v1, v2;
  Configuration conf, conf2;
  int i;
  double x;


  v1 = variables;
  v2 = p.variables;

  // Now explore all the configurations in the new potential,
  // evaluate the two operands according to this configuration,
  // and multiply the two values.
  
  conf = new Configuration(v1);

  for (i=0 ; i<this.values.length ; i++) {
    conf2 = new Configuration(v2,conf);

    x = getValue(conf);
    x *= p.getValue(conf2);
    setValue(conf,x);
    conf.nextConfiguration();
  }

}


/**
 * Combines two Potentials. The argument p can be a PotentialTable or
 * a PotentialTree.
 * @param p the Potential to combine with this.
 * @return a new PotentialTable with the result of the combination.
 */

//public PotentialTable combine(PotentialTable p) {
public Potential combine(Potential p) {

  Vector v, v1, v2;
  Configuration conf, conf1, conf2;
  FiniteStates aux;
  int i;
  PotentialTable pot;
  double x;

  //System.out.println("combine de PotentialTable");

  if(p.getClass().getName().equals("PotentialTable") ||
     p.getClass().getName().equals("PotentialConvexSet") ||
     p.getClass().getName().equals("PotentialTree")){
 // System.out.println("Potencial 1:");
 //print();
 // System.out.println("Potencial 2:");  
 // ((PotentialTable)p).print();
  v1 = variables;
  v2 = p.variables;
  v = new Vector(); // Variables of the new potential.

  for (i=0 ; i<v1.size() ; i++) {
    aux = (FiniteStates)v1.elementAt(i);
    v.addElement(aux);
  }

  for (i=0 ; i<v2.size() ; i++) {
    aux = (FiniteStates)v2.elementAt(i);
    if (aux.indexOf(v1)==-1)
      v.addElement(aux);
  }

  // Creates the new potential.
  pot = new PotentialTable(v);

 
  

  // Now explore all the configurations in the new potential,
  // evaluate the two operands according to this configuration,
  // and multiply the two values.
  
  conf = new Configuration(v);

  for (i=0 ; i<pot.values.length ; i++) {
    conf1 = new Configuration(v1,conf);
    conf2 = new Configuration(v2,conf);

    x = getValue(conf1);
    x *= p.getValue(conf2);
    pot.setValue(conf,x);
    conf.nextConfiguration();
  }
  //System.out.println("Resultado:");
  //pot.print();
  } 
  else{
     System.out.println("Error in Potential PotentialTable.combine(Potential p): argument p was not a PotentialTable nor a PotentialTree nor a PotentialConvexSet");
     System.exit(1);
     pot=this;
  }
  return pot;
}

/**
 * Combines this potential with the PotentialTable of the argument.
 * @param p a PotentialTable.
 * @returns a new PotentialTable consisting of the combination
 * of p (PotentialTable) and this PotentialTable.
*/

public PotentialTable combine(PotentialTable p) {
  //System.out.println("combine(PotentialTable) de PotentialTable"); 
  return (PotentialTable)combine((Potential)p);
}

/**
 * Combines this potential with the PotentialTree of the argument.
 * @param p a PotentialTree.
 * @returns a new PotentialTable consisting of the combination
 * of p (PotentialTree) and this PotentialTable.
*/

public PotentialTable combine(PotentialTree p) {

  return (PotentialTable)combine((Potential)p);
}

/**
 * This method divides two PotentialTables.
 * For the exception 0/0, the method compute the result as 0.
 * The exception ?/0: the method abort with a message in the standar output.
 * @param p the PotentialTable to divide with this.
 * @return a new PotentialTable with the result of the combination.
 */

public PotentialTable divide(PotentialTable p) {
  return (PotentialTable) divide((Potential)p);
}

/**
 * This method divides two PotentialTables.
 * For the exception 0/0, the method compute the result as 0.
 * The exception ?/0: the method abort with a message in the standar output.
 * @param p the PotentialTable to divide with this.
 * @return a new PotentialTable with the result of the combination.
 */

public Potential divide(Potential p) {


  Vector v, v1, v2;
  Configuration conf, conf1, conf2;
  FiniteStates aux;
  int i;
  PotentialTable pot;
  double x,y;

  v1 = variables;
  v2 = p.variables;
  v = new Vector(); // Variables of the new potential.

  for (i=0 ; i<v1.size() ; i++) {
    aux = (FiniteStates)v1.elementAt(i);
    v.addElement(aux);
  }

  for (i=0 ; i<v2.size() ; i++) {
    aux = (FiniteStates)v2.elementAt(i);
    if (aux.indexOf(v1)==-1)
      v.addElement(aux);
  }

  // Creates the new potential.
  pot = new PotentialTable(v);

  // Now explore all the configurations in the new potential,
  // evaluate the two operands according to this configuration,
  // and divide the two values.
  
  conf = new Configuration(v);

  for (i=0 ; i<pot.values.length ; i++) {
    conf1 = new Configuration(v1,conf);
    conf2 = new Configuration(v2,conf);

    x = getValue(conf1);
    y = p.getValue(conf2);

    if((x!=0.0) && (y!=0.0)){
	x /= y;
    }else{
	if ((x==0.0) && (y==0.0)){
	    x=0;
	}else{
	    try{
		x /= y;
	    }catch (Exception e){
		System.out.println("Divide by zero");
                System.exit(0);
                x = 0;
	    }
	}
    }
    pot.setValue(conf,x);
    conf.nextConfiguration();
  }

  return pot;
}




/**
 * Adds the argument potential to this Potential.
 * The process is the same as in combine, but instead
 * of multiplying, now we sum.
 * @param p the PotentialTable to add to this.
 * @return a PotentialTable with the result of the addition.
 */

public PotentialTable Add(PotentialTable p) {

  Vector v, v1, v2;
  Configuration conf, conf1, conf2;
  FiniteStates aux;
  int i;
  PotentialTable pot;
  double x;

  v1 = variables;
  v2 = p.variables;
  v = new Vector(); // Variables of the new potential.

  for (i=0 ; i<v1.size() ; i++) {
    aux = (FiniteStates)v1.elementAt(i);
    v.addElement(aux);
  }

  for (i=0 ; i<v2.size() ; i++) {
    aux = (FiniteStates)v2.elementAt(i);
    if (aux.indexOf(v1)==-1)
      v.addElement(aux);
  }
  
  // Creates the new potential.
  pot = new PotentialTable(v);
  
  // Now explore all the configurations in the new potential,
  // evaluate the two operands according to this configuration,
  // and sum the two values.
  
  conf = new Configuration(v);

  for (i=0 ; i<pot.values.length ; i++) {
    conf1 = new Configuration(v1,conf);
    conf2 = new Configuration(v2,conf);

    x = getValue(conf1);
    x += p.getValue(conf2);
    pot.setValue(conf,x);
  }

  return pot;
}
    

/**
 * @return a copy of this PotentialTable.
 */

public PotentialTable copy() {
 
  PotentialTable pot;
  int i, n;
  
  pot = new PotentialTable(variables);
  
  n = (int)FiniteStates.getSize(variables);
  
  for (i=0 ; i<n ; i++)
    pot.values[i] = values[i];
  
  return pot;
}


/**
 * Converts from table to probability tree.
 * @return a new PotentialTree with the same information as this
 * table.
 */

public PotentialTree toTree() {

  int i, total; 
  Configuration conf;
  PotentialTree pot;
  Vector vars;
  
  
  pot = new PotentialTree(getVariables());
  vars = (Vector) getVariables().clone();
  conf = new Configuration();
  
  setTreeFromTable(pot.values,conf,vars);
  
  return pot;
}


/**
 * Recursive procedure that constructs a probability tree
 * from a probability table.
 * @param tree the tree we are constructing. This tree is modified.
 * @param conf the configuration that leads to the subtree
 * we are operating in this recursion step. This configuration is
 * modified.
 * @param vars variables not already explored. This vector is
 * modified.
 */
 
public void setTreeFromTable(ProbabilityTree tree,
			     Configuration conf, Vector vars) {
  
  FiniteStates var;
  int i;
  Vector aux;
  
  if (vars.size() == 0)
    tree.assignProb(getValue(conf));
  else {
    var = (FiniteStates) vars.elementAt(0);
    vars.removeElementAt(0);
    tree.assignVar(var);
    
    for(i=0 ; i<var.getNumStates() ; i++) {
      aux = (Vector)vars.clone();
      conf.insert(var,i);
      setTreeFromTable(tree.getChild(i),conf,aux);
      conf.remove(conf.variables.size()-1);
    }
  }
}



/**
 * Normalizes the values of this potential.
 */

public void normalize() {
 
  int i;
  double s;
  
  s = totalPotential();
  
  for (i=0 ; i<values.length ; i++)
    values[i] /= s;
}


/**
 * This method incorporates the evidence passed as argument to the
 * potential, that is, put to 0 all the values whose configurations 
 * are not consistent with the evidence
 * @param ev a configuration representing the evidence
 */

public void instantiateEvidence(Configuration evid) {

  int i, total; 
  Configuration conf,scEvid,scPot; // the subconfigurations are used
                                   // to test the consistence
  Vector varsEvid; //for the variables of the evidence
  Vector evidNotInPot,potNotInEvid; // variables not in ...  
  SetVectorOperations svo = new SetVectorOperations();  
    
  total = (int)FiniteStates.getSize(variables);
  varsEvid = evid.getVariables();
  evidNotInPot = svo.notIn(varsEvid,variables);
  potNotInEvid = svo.notIn(variables,varsEvid);
  scEvid = new Configuration(evid,evidNotInPot);
  
  conf = new Configuration(variables);
  
  for (i=0 ; i<total ; i++) {
    scPot = new Configuration(conf,potNotInEvid);
    if (!scPot.equals(scEvid)) setValue(conf,0.0); 
    conf.nextConfiguration();
  }
  
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
 */

public Configuration getMaxConfiguration(Configuration subconf) {

  Configuration conf,aux,sc;
  double prob=-1; // we suppose that the probability always is positive
  int total,i;
  Vector vars;
  SetVectorOperations svo = new SetVectorOperations();

  total = (int)FiniteStates.getSize(variables);

  aux = new Configuration(variables);
  conf = new Configuration(variables);

  if (subconf.size()==0) { // there is no consistency to check
    for (i=0 ; i<total ; i++) {
      if (getValue(i) > prob) {
        prob = getValue(i);
        conf.setValues((Vector)aux.getValues().clone());
      }
      aux.nextConfiguration();
    }
  }
  else{ // there is consistency to check
    vars = svo.notIn(variables,subconf.getVariables());
    for (i=0 ; i<total ; i++) {
      sc = new Configuration(aux,vars);
      if (sc.equals(subconf)) {
        if (getValue(i) > prob) {
          prob = getValue(i);
          conf.setValues((Vector)aux.getValues().clone());
        }
      }
      aux.nextConfiguration();
    }

  }


  return conf;

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
 */

public Configuration getMaxConfiguration(Configuration subconf,
                                         Vector list) {

  Configuration conf,aux,sc;
  double prob=-1; // we suppose that the probability always is positive
  int total,i;
  Vector vars;
  SetVectorOperations svo = new SetVectorOperations();

  total = (int)FiniteStates.getSize(variables);

  aux = new Configuration(variables);
  conf = new Configuration( );

  if (subconf.size()==0) { // there is no consistency to check
    for (i=0 ; i<total ; i++) {
      if ((getValue(i) > prob) && (!aux.contained(list)) )  {
        prob = getValue(i);
        if (conf.size()==0) conf = new Configuration(variables);
        conf.setValues((Vector)aux.getValues().clone());
      }
      aux.nextConfiguration();
    }
  }
  else{ // there is consistency to check
    vars = svo.notIn(variables,subconf.getVariables());
    for (i=0 ; i<total ; i++) {
      sc = new Configuration(aux,vars);
      if (sc.equals(subconf)) {
        if ((getValue(i) > prob) && (!aux.contained(list)) ) {
          prob = getValue(i);
          if (conf.size()==0) conf = new Configuration(variables);
          conf.setValues((Vector)aux.getValues().clone());
        }
      }
      aux.nextConfiguration();
    }

  }


  return conf;

} 

/**
  * Converts a Potential to a PotentialTable.
  * @param pot the Potential
  * @returns a new PotentialTable associated to the 
  * <code>Potential pot</code>
  */
static PotentialTable convertToPotentialTable(Potential pot) {
  PotentialTable newPot;
  if(pot.getClass().getName().equals("PotentialTree")) {
    newPot = new PotentialTable((PotentialTree)pot);
  }
  else if(pot.getClass().getName().equals("PotentialMTree")) {
    newPot = new PotentialTable((PotentialMTree)pot);
  }
  else if(pot.getClass().getName().equals("PotentialTable")) {
    newPot = ((PotentialTable)pot).copy();	
  }
  else
    newPot=null;
  
  return newPot;
}

} // end of class



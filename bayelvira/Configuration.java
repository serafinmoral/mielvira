import java.util.Vector;
import FiniteStates;
import java.io.*;

/**
 * Implements a configuration, consisting of a list of
 * pairs (variable,value), where variable is a FiniteStates node
 * and value is an Integer.
 *
 * Last modified : 28/08/99
 */

public class Configuration {

Vector variables;
Vector values;



/**
 * Creates an empty Configuration.
 */

Configuration() {
  
  variables = new Vector();
  values = new Vector();
}
  

/**
 * Creates a configuration for a list of variables with the
 * values equal to zero.
 * @param vars a vector of variables.
 */

Configuration(Vector vars) {

  Integer val;
  int i;

  variables = (Vector)vars.clone();
  values = new Vector();

  for (i=0 ; i<vars.size() ; i++) {
    val = new Integer(0);
    values.addElement(val);
  }
}


/**
 * Creates a configuration for a list of variables with the
 * values equal to zero.
 * @param vars a NodeList containing the variables.
 */

Configuration(NodeList vars) {

  Integer val;
  int i;

  variables = (Vector)vars.nodes.clone();
  values = new Vector();

  for (i=0 ; i<vars.size() ; i++) {
    val = new Integer(0);
    values.addElement(val);
  }
}


/* Crea una configuracion dados un vector de variables
   y otro de posiciones */

/**
 * Creates a new configuration from a list of variables
 * and a list of values of those variables.
 * @param vars a vector of variables (FiniteStates). It is directly
 * taken as the list of variables of the configuration. Thus, do
 * not modify it outside.
 * @param vals a vector of values (Integer). It is directly
 * taken as the list of values of the configuration. Thus, do
 * not modify it outside.
 */

Configuration(Vector vars, Vector vals) {
  
  variables = vars;
  values = vals;
}  


/**
 * Creates a configuration for the variables given with all
 * the values set to zero, except for those variables which
 * are also in the argument configuration, whose value
 * will be equal to the one they have in the argument
 * configuration.
 * @param vars a vector of FiniteStates.
 * @param conf a Configuration.
 */

Configuration(Vector vars, Configuration conf) {

  Integer val;
  FiniteStates aux;
  int i, x, pos;

  variables = (Vector)vars.clone();
  values = new Vector();

  for (i=0 ; i<vars.size() ; i++) {
    aux = (FiniteStates)vars.elementAt(i);
    pos = conf.indexOf(aux);
    
    // If the variable is in conf, keep its old value.
    if (pos>=0)
      x = ((Integer)conf.values.elementAt(pos)).intValue();
    else
      x = 0;

    val = new Integer(x);
    values.addElement(val);
  }
}


/**
 * Creates a new configuration equal to the one passed as
 * argument, but dropping those variables contained in the
 * argument vector.
 * @param conf a Configuration.
 * @param vars a Vector of FiniteStates.
 */

Configuration(Configuration conf, Vector vars) {

  int i;
  Integer val;
  FiniteStates temp;

  variables = new Vector();
  values = new Vector();

  for (i=0 ; i<conf.variables.size() ; i++) {
    temp = (FiniteStates)conf.variables.elementAt(i);
    
    // If the variable is not in vars, take it.
    if (vars.indexOf(temp)==-1) {
      variables.addElement(temp);
      val = new Integer(((Integer)conf.values.elementAt(i)).intValue());
      values.addElement(val);
    }
  }
}


/**
 * Creates a new configuration equal to the one passed as
 * argument, but only with the variables pased as argument, and
 * mantaining the order in that nodeList
 *
 * @param conf a Configuration.
 * @param nl a nodeList of FiniteStates.
 */


Configuration(Configuration conf, NodeList nl) {

  int i,pos;
  Integer val;
  FiniteStates temp;

  variables = new Vector();
  values = new Vector();

  for (i=0; i<nl.size(); i++){
    temp = (FiniteStates)nl.elementAt(i);
    // if the variable is in conf take it
    pos = conf.getVariables().indexOf(temp);
    if (pos != -1){
      variables.addElement(temp);
      val = new Integer(((Integer)conf.values.elementAt(pos)).intValue());
      values.addElement(val);
    }
  }

}


/**
 * Set the values for the configuration
 *
 * @param v the vector containing the values
 */

public void setValues(Vector v) {
  values = v;
}

/**
 * Saves this configuration in a file.
 * @param p the PrintWriter where the configuration
 * will be written.
 */

public void save(PrintWriter p) {
 
 int i, j, k;
 String name; 
 
 p.print("[");
 
 j = values.size();
 
 for(i=0 ; i<j ; i++) {
   name = new String((String) 
		     ((FiniteStates)variables.elementAt(i)).
		     getState(((Integer)
			       values.elementAt(i)).intValue()));
   try {
     k = Integer.parseInt(name);
     p.print("\""+name+"\"");
   }
   catch (NumberFormatException e) {
     p.print(name);
   }	      
   if (i==j-1) {
     p.print("]");
   }
   else {
     p.print(",");
   }
 }		      
}



/**
 * Prints this configuration to the standard output.
 */

public void print(){
 
 int i,j,k;
 String name; 
 
 System.out.print("[");
 
 j = values.size();
 
 for (i=0 ; i<j ; i++) {
   name = new String((String) 
		     ((FiniteStates)variables.elementAt(i)).
		     getState(((Integer)
			       values.elementAt(i)).intValue()));
   try {
     k = Integer.parseInt(name);
     System.out.print("\""+name+"\"");
   }
   catch (NumberFormatException e) {
     System.out.print(name);
   }	      
   if(i==j-1) {
     System.out.print("]");
   }
   else {
     System.out.print(",");
   }
 }		      
}


/**
 * Creates a copy of this configuration. Both vectors
 * variables and values will be shared.
 * @return a Configuration with the copy.
 */

public Configuration copy() {

  Configuration aux;

  aux = new Configuration(variables,values);
  
  return aux;
}



/**
 * Modifies this Configuration and makes it be equal to
 * the next. For example, if a configuration for two binary
 * variables has the values (0,0), the next would be (0,1),
 * the next (1,0) and so on.
 */

public void nextConfiguration() {

  int i;
  int carry = 1;
  FiniteStates aux;
  Integer valI;
  Integer valJ;

  for (i=variables.size()-1 ; i>=0 ; i--) {
    
    if (carry==0) // It is done.
      break;

    aux=(FiniteStates)variables.elementAt(i);
    valJ=(Integer)values.elementAt(i);

    // If the variable is in its last value,
    // set it to 0 and carry 1.
    if ((aux.getNumStates()-1)==valJ.intValue()) {
      valI=new Integer("0");
      values.setElementAt(valI,i);
      carry=1;
    }
    // Otherwise, increment its value and carry 0
    else {
      valI=new Integer(valJ.intValue()+1);
      values.setElementAt(valI,i);
      carry=0;
    }
  }
}


/**
 * Computes the next configuration from this, and the result is
 * stored in this. In this case the variables contained in
 * the argument configuration remain unchanged.
 * @param conf a Configuration with the variables that must
 * remain unchanged.
 */

public void nextConfiguration(Configuration conf) {

  int i, pos, carry=1;
  FiniteStates aux;
  Integer valI;
  Integer valJ;

  for (i=variables.size()-1 ; i>=0 ; i--) {
    
    if (carry==0) // It is done.
      break;

    aux = (FiniteStates)variables.elementAt(i);
    valJ = (Integer)values.elementAt(i);

    pos = conf.indexOf(aux);

    // If the variable is in conf, continue
    if (pos>=0)
      continue;
    
    // If the variable is in its last value,
    // set it to 0 and carry 1.
    if ((aux.getNumStates()-1)==valJ.intValue()) {
      valI = new Integer("0");
      values.setElementAt(valI,i);
      carry = 1;
    }
    // Otherwise, increment its value and carry 0
    else {
      valI=new Integer(valJ.intValue()+1);
      values.setElementAt(valI,i);
      carry=0;
    }
  }
}


/**
 * Computes ths index in the array of values of a potential
 * corresponding to this configuration. For example, if a
 * configuration for two variables with i and j cases respectively
 * is l,m then the position is l*j+m.
 * @return the index in the array of values of a Potential
 * corresponding to this configuration.
 */

public int getIndexInTable() {

  int i, nv, pos, numVal;
  FiniteStates aux;
  Integer temp;

  nv = variables.size(); // Number of variables.

  aux = (FiniteStates)variables.elementAt(nv-1); // The last one.
  temp = (Integer)values.elementAt(nv-1); // its value.


  numVal = aux.getNumStates();
  pos = temp.intValue();
  
  // Computes position
  for (i=nv-2 ; i>=0 ; i--) {
    aux = (FiniteStates)variables.elementAt(i);
    temp = (Integer)values.elementAt(i);

    pos+=(temp.intValue())*numVal;
    numVal*=aux.getNumStates();
  }

  return pos;
}


/**
 * Gets the position of a variable in a configuration.
 * @param var a variable (Node).
 * @returns the position of var in the list of variables.
 * @returns -1 if var is not contained in the list.
 */

public int indexOf(Node var) {

  int i;
  Node aux;

  for (i=0 ; i<variables.size() ; i++) {
    aux=(Node)variables.elementAt(i);
    if (aux==var)
      return (i);
  }
  
  return (-1);
}


/**
 * Inserts a pair (variable,value) at the end of the configuration.
 * @param var a FiniteStates variable.
 * @param val an integer value.
 */

public void insert(FiniteStates var, int val) {

  Integer i;

  variables.addElement(var);
  i = new Integer(val);
  values.addElement(i);
}

/**
 * Put a pair (variable,value) at the configuration. If the variable is at configuration,
 * set the value in the position of the variable,
 * else insert (variable,value) at the end of the configuration.
 * @param var a FiniteStates variable.
 * @param val an integer value.
 */

public void putValue(FiniteStates var, int val){

    Integer i;
    int pos;
    
    pos=indexOf(var);

    if (pos == -1){
	insert(var,val);
    }
    else{
	i = new Integer(val);
	values.setElementAt(i,pos);
    }
}

/**
 * Removes a pair (variable,value).
 * @param position the position of the pair to remove.
 * If position is outside the range of the list, an error
 * is produced.
 */

public void remove(int position) {

  variables.removeElementAt(position);
  values.removeElementAt(position);
}



/**
 * Returns the value of the variable stored in a position.
 * @param position the position.
 * @return the value of the variable at position 'position'.
 */

public int getValue(int position) {

  return ((Integer)values.elementAt(position)).intValue();
}


/**
 * Returns the value of a given variable.
 * @param var the variable (FiniteStates).
 * @return the value of variable var.
 */

public int getValue(FiniteStates var) {

  return getValue(indexOf(var));
}


/**
 * Returns the variable stored in a position.
 * @param position the position.
 * @return the variable at that position.
 */

public FiniteStates getVariable(int position) {

  return (FiniteStates)variables.elementAt(position);
}


/**
 * @return the number of variables in the configuration.
 */

public int size() {
 
  return variables.size();
}

/**
 * @return the variables in the configuration.
 */

public Vector getVariables() {
 
  return variables;
}

/**
 * @return the values in the configuration.
 */

public Vector getValues() {
 
  return values;
}



/**
 * @return true if the configuration passed as argument is
 * equal to the configuration which receives the message
 * and false in other case
 */

public boolean equals(Configuration conf) {
  int i,j; 
  FiniteStates fs;
 
  if (size()!=conf.size()) return false;
  else{
    for(i=0;i<size();i++){
      fs = getVariable(i);
      j = conf.indexOf(fs);
      if (j==-1) return false;
      else{
        if (getValue(i) != conf.getValue(j)) return false;
      }
    }
  }
  
  return true;
}


/**
 * @return true if exists in the vector passed as argument, a
 * configuration equals to the configuration which receives the message
 * 
 * @param v the vector
 */

public boolean contained(Vector v) {

  int i;
  Configuration conf;
  
  for (i=0;i<v.size();i++){
    conf = (Configuration) v.elementAt(i);
    if (this.equals(conf)) return true;
  }

  return false;
}

/**
 * @return the first configuration from the set of configurations
 * represented by the object which receives the message (-1 stands for every
 * value) that is not included in list.
 * If there isn't any valid configuration, an empty configuration is returned. 
 */

public Configuration getFirstNotInList(Vector list){
  int i,j;
  long s;
  Vector v; // the vector with all the variables with -1 as value 
  Configuration subconf,conf;
  Vector vals;
  boolean found=false;  

  v = new Vector();
  for(i=0;i<this.size();i++)
    if (this.getValue(i)==-1) v.addElement(this.getVariable(i));
 
  subconf = new Configuration(v);
  s = (long) FiniteStates.getSize(v); 
  for(i=0;i<s;i++){
    // creating a clone of this
    vals = new Vector();
    for(j=0;j<values.size();j++)
      vals.addElement(new Integer(((Integer)values.elementAt(i)).intValue() ));
    conf = new Configuration(this.getVariables(),vals);

    // setting in conf the values of subconf 
    for(j=0;j<subconf.size();j++){
      conf.putValue(subconf.getVariable(j),subconf.getValue(j));
    }
    // searching if subconf is contained in list      
    if (subconf.contained(list)) {found=true;break;}
    else subconf.nextConfiguration();
  }

  if (found == true) return subconf;
  else return new Configuration();

}

} //end of class



























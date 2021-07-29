/**
 * This class implements a case list as a vector of integer arrays. This arrays
 * are the states index of the variables.
 */

import java.util.Vector;
import java.io.*;

class CaseListMem extends CaseList{
	
    private Vector cases;

    /**
     * this constructor method is the default constructor of the class
     */

    public CaseListMem(){

	cases = null;

    }

    /**
     * this constructor method stores the case list variables and the vector of
     * cases.
     */

    public CaseListMem(NodeList variables){
	int nvars;
	    
        this.variables = variables.toVector();
	setNumberOfCases(0);
	cases = new Vector();
	
	    
    }

    /**
     * this method get the vector of cases.
     * @return Vector. the set of cases.
     */

    public Vector getCases(){
     return cases;
    }

    /**
     * this method compute the absolute frequency for a configuration given as 
     * parameter.
     * @param Configuration conf.
     * @return double.
     */

    public double getValue(Configuration conf){
     int i,cont;
     Configuration confAux,confAux2;
     Vector vars;
     cont = 0;
     for(i=0 ;i< getNumberOfCases(); i++){
        confAux = get(i);
        vars = conf.getVariables();
        confAux2 = new Configuration(vars,confAux);
        if(confAux2.equals(conf)) cont++;
     }

     return (double)cont;

    }

    public void setValue(Configuration conf,double val){
      System.out.println("CaseList: Not implemented");
    }

    /**
     * @see this#getValue()
     */

    public double totalPotential(Configuration conf){

        return (getValue(conf));
    }

    /**
     * this method put a configuration at the end of case list
     * @param Configuration.
     * @return boolean. true if the operation is succesful.
     */

    public boolean put(Configuration conf){

	int i,posInConf,val;
	FiniteStates var;
	boolean exito=true;
	int[] cas;
	
	cas = new int[variables.size()];
	for(i=0;i<variables.size();i++) cas[i]=-1;

	
	if(cases != null){
       	    for(i=0 ;i<variables.size();i++){
		var = (FiniteStates) variables.elementAt(i);
		posInConf = conf.indexOf(var);
		if(posInConf != -1){
		    val = conf.getValue(posInConf);
		    cas[i]=val; 
		}
	    }
	    try{
		cases.addElement(cas);
	    }catch (OutOfMemoryError e){
		System.out.println(e);
		exito = false;
	    }
	    if (exito) setNumberOfCases(cases.size());
	   
        }

	return exito;
    }

    /**
     * this method gets a case configuration from the position pos on case list
     * @param int pos.
     * @return Configuration. This configuration is null if the operation is 
     * not succeful.
     */

    public Configuration get(int pos){
	int i;
	Configuration conf;
	int cas[];

        if (pos < getNumberOfCases()){
           if(cases!=null){
               conf = new Configuration(variables);
	       cas = (int[])cases.elementAt(pos);
               for(i=0;i<variables.size();i++)
		   conf.putValue((FiniteStates)variables.elementAt(i),cas[i]);
           }
           else conf=null;
           return conf;
        }
        else{
          System.out.println("Position "+pos+" > number of cases");
          conf = null;
          return conf;
        }

    }


    /**
     * this method print a case list
     */

    public void print(){
	
	for(int i=0 ; i< getNumberOfCases() ; i++){
	    Configuration conf = get(i);
	    conf.print();
	    System.out.print("\n");
	}
    }



    /** this methods is not implemented
     */

    public double entropyPotential(){
     return (-1.0);
    }

    public double entropyPotential(Configuration conf){
        return (-1.0);
    }

    /** Combine current potential with Potential pot
     * @param pot
     * @return The combination of the two potential 
     */

    public Potential combine(Potential pot){
	
	return (Potential)(new CaseListMem());
    }
    
    /**
     * Removes the argument variable summing over all its values.
     * @param var a FiniteStates variable.
     * @return a new Potential with the result of the deletion.
     */
    
    public Potential addVariable(FiniteStates var){

	return (Potential)(new CaseListMem());

    }    
    
    /**
     * Save the potential into the file represented by the PrintWriter P
     * @param P is the PrintWriter
     */

    public void saveResult(PrintWriter P){

	System.out.println("CaseListMem: Not implemented");
    }
    
    /**
     * Normalizes the values of this potential.
     */
    
    public void normalize(){

	System.out.println("CaseListMem: Not implemented");
    }
    
    Potential restrictVariable(Configuration conf){

	return (Potential)(new CaseListMem());

    }


    
    
}
















/**
 * CaseListOutMem.java
 *
 *
 * Created: Mon Oct  4 18:43:05 1999
 *
 * @author Proyecto Elvira
 * @version
 */

/**
 * This class implements a case list as a random acces file of integer. 
 * This integer are the states index of the variables.
 */


import java.io.*;
import java.util.Vector;

public class CaseListOutMem extends CaseList {
    
    private RandomAccessFile cases;
    
    /**
     * this constructor method is the default constructor of the class
     */
    
    public CaseListOutMem(){

	cases=null;
    }

    /**
     * this constructor method stores the case list variables and the file of
     * cases.
     */

    public CaseListOutMem(NodeList variables, String FileName){
	int nvars,cas,pos,index;
	    
        this.variables = variables.toVector();
	nvars = variables.size();
        setNumberOfCases(0);
	try{
	    cases = new RandomAccessFile(FileName,"rw");
	}catch (IOException e){
	    cases=null;
	    System.out.println(e);
	}
    }

    /**
     * this method get the file of cases.
     * @return RandomAccessFile. the set of cases.
     */

    public RandomAccessFile getCases(){
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

    public double entropyPotential(){
     return (-1.0);
    }

    public double entropyPotential(Configuration conf){
        return (-1.0);
    }
       
    /**
     * this method put a configuration at the end of case list
     * @param Configuration.
     * @return boolean. true if the operation is succesful.
     */

    public boolean put(Configuration conf){
	int i,posInConf,val,index;
	FiniteStates var;
	boolean exito=true;
        int pos;

	pos = getNumberOfCases();
	
	for(i=0 ;i<variables.size();i++){
	    index = getIndex(pos,i);
	    try{
		cases.seek(index);
		cases.writeInt(-1);
	    }catch (IOException e){
		exito = false;
		System.out.println(e);
		return exito;
	    }
	}
	
	for(i=0 ;i<variables.size();i++){
	    var = (FiniteStates) variables.elementAt(i);
	    posInConf = conf.indexOf(var);
	    if(posInConf != -1){
		val = conf.getValue(posInConf);
		index = getIndex(pos,i);
		try{
		    cases.seek(index);
		    cases.writeInt(val);
		    exito = true;
		}catch (IOException e){
		    exito = false;
		    System.out.println(e);
                    return exito;
		}
	    }
	}
	
	if(exito){
	    pos++;
	    setNumberOfCases(pos);
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
     int i,val,index;
     if(pos < getNumberOfCases()){
	Configuration conf = new Configuration(variables);
	for(i=0;i<variables.size();i++){
	    index = getIndex(pos,i);
	    try{
		cases.seek(index);
		val = cases.readInt();
		conf.putValue((FiniteStates)variables.elementAt(i),val);
	    }catch (IOException e){
		conf = null;
		System.out.println(e);
	    }
	}
        return conf;
     }else{
        System.out.println("Position "+pos+" > number of cases");
        Configuration conf = null;
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

    /**
     * this method is private to the class, and get the index into the file
     */

    private int getIndex(int row, int column){
	int pos;
	pos = (row*variables.size()*4)+(column*4);
	return pos;
    }


    /** this methods are not implemented 
     */


    /** Combine current potential with Potential pot
     * @param pot
     * @return The combination of the two potential 
     */
    
    public Potential combine(Potential pot){
	
	return (Potential)(new CaseListOutMem());
    }
    
    /**
     * Removes the argument variable summing over all its values.
     * @param var a FiniteStates variable.
     * @return a new Potential with the result of the deletion.
     */
    
    public Potential addVariable(FiniteStates var){

	return (Potential)(new CaseListOutMem());

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

	return (Potential)(new CaseListOutMem());

    }    

  
    
} // CaseListOutMem








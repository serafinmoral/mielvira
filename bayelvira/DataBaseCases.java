
/**
 * DataBaseCases.java
 *
 *
 * Created: Thu May  6 19:57:19 1999
 *
 * @author Proyecto Elvira
 * @version   1.0
 */

import NodeList;
import java.io.*;
import java.util.*;
import FiniteStates;
import PotentialTable;
import Potential;
import Configuration;
import Bnet;


public class DataBaseCases extends Bnet implements ConditionalIndependence {
    
   
    int numberOfCases;                // Number of cases to be considered.
    
   

public static void main(String[] args) throws ParseException,FileNotFoundException,IOException {

    NodeList variableList,vars,vars2;
    PotentialTable potencial,poten,p2,potential,totalpotential;
    Vector relationlist;
    Relation relation;
    int i;
    

    //FileInputStream f = new FileInputStream("ejemplo.elv");
    //Bnet net = new Bnet(f);
    
    //FileWriter f3 = new FileWriter("ejemplo.dbc");
    
    FileInputStream f2 = new FileInputStream("ejemplo.dbc");
    //DataBaseCases dataBase = new DataBaseCases(net,f3,600,false);
    //f3.close();
    DataBaseCases db = new DataBaseCases(f2);
    vars=db.getVariables();
    vars2 = vars.copy();
    
    potencial = db.getPotentialTable(vars2);
    potencial.normalize();
    potencial.print();
    
   
    


    }



    /**
     * Constructor for a data base from a data base file .dbc
     * @param FileInputStream f. The file of the data base.
     */ 
    
    
public DataBaseCases(FileInputStream f) throws IOException,ParseException
,FileNotFoundException {
    
    DataBaseParse parser = new DataBaseParse(f);
    parser.initialize();
    parser.CompilationUnit();
    translate(parser);
    f.close();
    
}

    /**
     * This constructor carries out a logic samplic of a bnet and stores this
     * in a file .dbc.
     * @param Bnet. the bnet to be sampling
     * @param PrintWriter. the file for storing the data base.
     * @param int n . The number of cases in the data base.
     * @param boolean. true if we want store the data base in main memory.
     */


public DataBaseCases(Bnet network, FileWriter f, int n, boolean memory) throws IOException{

    Configuration conf,confaux;
    int i; boolean ok;
    RelationList samplingRelationList;
    Random generator = new Random();
    String fileName = network.getName()+"db.bin";
    CaseList cases;
    Relation relation;
    Vector relations;
    NodeList variables;
    PrintWriter fv = new PrintWriter(f);

    fv.print("// Data Base. Elvira Format\n\n");
    fv.print("data-base "+network.getName()+" {\n");
    //  fv.print("title = "+network.getTitle()+";\n");
//      fv.print("comment = "+network.getComment()+";\n");
    fv.print("number-of-cases = "+ n +";\n\n\n");
    variables = network.getNodeList();
    setNodeList(variables);
    setLinkList(null);
    variables.save(fv);
    numberOfCases = n;
    fv.print("relation  {\n\n");
    fv.print("memory = "+memory+";\n\n");
    if(memory)
	cases = (CaseList) new CaseListMem(variables);
    else cases = (CaseList) new CaseListOutMem(variables,fileName);
    
    relation = new Relation();
    relation.setVariables(variables);
    relation.setValues(cases);
    relations = new Vector();
    relations.addElement(relation);
    setRelationList(relations);

    fv.print("cases = (\n");

    samplingRelationList=getOrderInSimulation(network);
    for (i=0 ; i< n ; i++){
	conf= simulateConfiguration(samplingRelationList,generator);
	confaux = reorder(conf);
	confaux.save(fv);fv.print("\n");
	cases.put(confaux);
    }
    fv.print(");\n\n");
    fv.print("}\n}");
    fv.close();
   
}

    /**
     * This method translate the parse variables to instance variables.
     */

 public void translate(DataBaseParse parser) {   
   
      setNodeList(parser.Nodes);
      setComment(parser.Comment);
      setTitle(parser.Title);
      setAuthor(parser.Author);
      setName(parser.Name);
      setRelationList(parser.Relations);
      setNumberOfCases(parser.casesNumber);
      
             
   }

    /**
     * This method sort a configuration having into account the variables order
     * @param Configuration. The configuration to be sorted.
     * @return Configuration. The configuration sorted with the variables order
     */

private Configuration reorder(Configuration conf){
	
	Configuration confaux = new Configuration();
	int i,value;
	FiniteStates node;
	NodeList variables = getNodeList();

	for(i=0;i<variables.size();i++){
	    node=(FiniteStates)variables.elementAt(i);
	    value=conf.getValue(node);
	    confaux.insert(node,value);
	}
	return confaux;

    }

    /** 
     * This method compute the initial order among the relations to be
     * simulated.
     * @param Bnet net. The net to be simulated.
     * @return RelationList. The relations sorted to be similated.
     */

private RelationList getOrderInSimulation(Bnet net){
    
    NodeList variables = getNodeList();
    Configuration confaux1 = new Configuration(variables);
    Configuration conf = new Configuration();
    Vector relationList;
    RelationList relationListOrd = new RelationList();
    NodeList varOfRelation;
    boolean inOrder=true;
    int i,j,pos;
    Relation relation;
    FiniteStates node;
	
    relationList=(Vector)(net.getRelationList()).clone();
    while(confaux1.size() != 0){
	for(i=0 ; i< relationList.size(); i++){
	    inOrder=true;
	    relation=(Relation)relationList.elementAt(i);
	    varOfRelation = relation.getVariables();
	    for(j=1;j<varOfRelation.size();j++){
		node=(FiniteStates)varOfRelation.elementAt(j);
		if(conf.indexOf(node)==-1) {
		    inOrder=false;
		    break;
		}
	    }
	    if(inOrder){
		node=(FiniteStates)varOfRelation.elementAt(0);
		conf.insert(node,-1);
		confaux1.remove(confaux1.indexOf(node));
		relationListOrd.insertRelation(relation);
		pos=relationList.indexOf(relation);
		relationList.removeElementAt(pos);
		break;
	    }
	}
    }
    
    return relationListOrd;
    
}

    
/**
 * Simulates a configuration.
 * @param generator a random number generator.
 * @return true if the simulation was ok.
 */

public Configuration simulateConfiguration(RelationList samplingDistributions, 
					   Random generator) {
 
    FiniteStates variableX;
    Relation relation;
    NodeList variableList;
    int i, s, v;
    Configuration currentConf = new Configuration();
    
    s = samplingDistributions.size();
    
    for (i=0 ; i<s ; i++) {
	relation = (Relation)samplingDistributions.elementAt(i);
	variableList=relation.getVariables();
	variableX = (FiniteStates)variableList.elementAt(0);
	v = simulateValue(variableX,relation,currentConf,generator);
	currentConf.insert(variableX,v);
    }
    
    return currentConf;
}

    
    
    /**
     * Simulates a value for a variable.
     * @param variableX a FiniteStates variable to be generated.
     * @param pos the position of variableX in the current conf.
     * @param list the list of sampling distributions of variableX.
     * @param generator a random number generator.
     * @return the value simulated. -1 if the valuation is 0.
     */
    
    public int simulateValue(FiniteStates variableX,
			     Relation relation, Configuration currentConf, 
			     Random generator) {
	
	int i, j, nv, v = -1;
	double checksum = 0.0, r, cum=0.0, value;
	Configuration conf= new Configuration();
	NodeList variablesInRelation;
	PotentialTable potential;
	int numberOfVariables,state,pos;
	FiniteStates node;
	
	conf.insert(variableX,0);
	nv = variableX.getNumStates();
	variablesInRelation=relation.getVariables();
	numberOfVariables=variablesInRelation.size();
	
	for(j=1; j<numberOfVariables ; j++){
	    node=(FiniteStates)variablesInRelation.elementAt(j);
	    state=currentConf.getValue(node);
	    conf.insert(node,state);
	}
	
	potential=(PotentialTable)relation.getValues();
	
	for(i=0 ; i<nv ;i++){
	    conf.putValue(variableX,i);
	    pos=conf.getIndexInTable();
	    value=potential.getValue(pos);
	    checksum+=value;
	}
	
	if (checksum == 0.0) {
	    System.out.println("Zero valuation");
	    return -1;
	}
	
	r = generator.nextDouble();
	
	for (i=0 ; i<nv ; i++) {
	    conf.putValue(variableX,i);
	    pos=conf.getIndexInTable();
	    value=potential.getValue(pos);
	    cum += (value / checksum);
	    if (r <= cum) {
		v = i;
		break;
	    }
	}
	
	return v;
    }
    
    
    /******** access methods ********************/

public int getNumberOfCases(){
    return numberOfCases;
}
    

public NodeList getVariables(){
    return getNodeList();
}


    /** 
     * This method set the number of cases to be considered
     */

public void setNumberOfCases(int numberOfCases){
    CaseList cases = (CaseList)((Relation)getRelationList().elementAt(0))
	.getValues();
    if(numberOfCases <= cases.getNumberOfCases())
	this.numberOfCases = numberOfCases;
} 


/** 
 * This method return a potential absolute frequencies table 
 * for a subset of variables in the DB.
 * @param NodeList vars. Subset of variables
 * @return PotentialTable. 
 */ 

public PotentialTable  getPotentialTable (NodeList vars){

    Configuration conf,confAux;
    boolean missingValues;
    PotentialTable pot = new PotentialTable(vars);
    FiniteStates node;
    int i,nState,indexTable;
    double increment = (double)(1.0);
    CaseList cases;
    
    cases= (CaseList)((Relation)getRelationList().elementAt(0)).getValues();
    for(int cas=0; cas < getNumberOfCases(); cas++){
	conf = cases.get(cas);
	confAux = new Configuration(vars.toVector(),conf);
	missingValues = false;
	for(i=0; i< confAux.size();i++){
	    nState = confAux.getValue(i);
	    if(nState == -1){
		missingValues = true;
		break;
	    }
	}
	if(!missingValues){
	    indexTable = confAux.getIndexInTable();
	    pot.incValue(indexTable,increment);
	}
    }
	
    return(pot);
    
}

    /**
     * this method compute the absolute frequency for a configuration given as 
     * parameter.
     * @param Configuration conf.
     * @return double.
     */

public double getTotalPotential(Configuration conf){

    int ncases;
    CaseList cases;
    double val;

    cases =(CaseList)((Relation)getRelationList().elementAt(0)).getValues();
    ncases = cases.getNumberOfCases();
    cases.setNumberOfCases(getNumberOfCases());
    val = cases.totalPotential(conf);
    cases.setNumberOfCases(ncases);

    return val;

}

/**
 * This method carry out a conditional independence test I(x,y|z) over
 * data in DB. To make this tests a chi-square tests is achieved. 
 * @param Node x.
 * @param Node y.
 * @param NodeList z. the conditionating set.
 * @param int degreeOfAccuracy. [0..4] zAlpha[]={1.28,1.64,1.96,2.33,2.58}.
 * @return boolean.
 */
 
public boolean independents (Node x, Node y, NodeList z, int degreeOfAccuracy){
    NodeList varsxyz;
    PotentialTable pxyz;
    int i,nStatesx,nStatesy,nStatesz,degreesOfFreedom;
    double dxyz,chiSquare,test;
    double zAlpha[]={1.28,1.64,1.96,2.33,2.58};

    varsxyz = new NodeList();
    varsxyz.insertNode(x);
    varsxyz.insertNode(y);
    for(i=0 ; i<z.size(); i++)
	varsxyz.insertNode((FiniteStates)z.elementAt(i));

    pxyz = getPotentialTable(varsxyz);
    pxyz.normalize();
    dxyz = pxyz.crossEntropyPotential();
    //System.out.println(dxyz);
    chiSquare = ((double)2.0*(double)getNumberOfCases())*dxyz;
	
    if (z.size() !=0){
	degreesOfFreedom = ((int)FiniteStates.getSize(z.nodes))*
		((((FiniteStates)x).getNumStates()-1)*
		 (((FiniteStates)y).getNumStates()-1));
    }else {
	degreesOfFreedom =((((FiniteStates)x).getNumStates()-1)*
			   (((FiniteStates)y).getNumStates()-1));
    }
    test = (Math.pow((zAlpha[degreeOfAccuracy] + 
		      Math.sqrt((2*degreesOfFreedom)-1.0)),2))/2.0;

    //System.out.println(degreesOfFreedom);
    //System.out.println(test);
    //System.out.println(chiSquare);

    if(chiSquare > test) return(false);
    else return (true);

}

/**
 * @see this#independents (Node x, Node y, NodeList z, int degreeOfAccuracy)
 * by default the degree of accuracy is 4.
 */
public boolean independents (Node x, Node y, NodeList z){

    return (independents(x,y,z,4));

}



} // DataBaseCases
















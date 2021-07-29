
/**
 * K2Metrics.java
 *
 *
 * Created: Mon Nov  8 11:09:40 1999
 *
 * @author P. Elvira
 * @version  1.0
 */

public class K2Metrics extends Metrics {
  
    LogFactorial f; // For storing and computing the log factorial n.
  
    
    public K2Metrics() {
    	f = new LogFactorial();
	setData(null);
    }
    
    public K2Metrics(DataBaseCases data){
    	f = new LogFactorial();
	setData(data);
    }


    public double score (Bnet b){
	
	NodeList vars,parentsX,varsXPa;
	int i,j;
	double prod = 1.0;
	double valscore;
	FiniteStates nodeX,nodeY;
	
	vars = b.getNodeList();
	
	for(i=0; i< vars.size(); i++){
	    nodeX = (FiniteStates) vars.elementAt(i);
	    parentsX = b.parents(nodeX);
	    varsXPa = new NodeList();
	    varsXPa.insertNode(nodeX);
	    varsXPa.join(parentsX);
	    valscore = score(varsXPa);
	    prod*=valscore;
	}
	
	return prod;

    }

    public double score (NodeList vars){

	Configuration conf,confPa;
	PotentialTable totalPot,parentsPot;
	double sum = 0.0;
	NodeList ParentsXi,varsaux;
	FiniteStates Xi;
	int nStatesXi,k,pos,Nij,Nijk;
	double j,nconfPa,logFactXi,partialSum;
	
	Xi = (FiniteStates)vars.elementAt(0);
	totalPot = getData().getPotentialTable(vars);
	Nij=0;
	parentsPot = null;
	if(vars.size()>1){
	    parentsPot = (PotentialTable)totalPot.addVariable(Xi);
	    ParentsXi = vars.copy();
	    ParentsXi.removeNode(Xi);
	    confPa = new Configuration(ParentsXi);
	    nconfPa = FiniteStates.getSize(ParentsXi);
	}
	else{
	    nconfPa = 1.0;
	    varsaux = new NodeList();
	    varsaux.insertNode(Xi);
	    confPa = new Configuration(varsaux);
	    Nij = (int)totalPot.totalPotential();
	}
	    
	nStatesXi = Xi.getNumStates();
	
	logFactXi = f.logFactorial(nStatesXi-1);
	
	for(j=0.0 ; j< nconfPa ; j++ ){
	    for(k=0 ; k< nStatesXi ; k++){
		conf = new Configuration(vars.toVector(),confPa);
		conf.putValue(Xi,k);
		pos = conf.getIndexInTable();
		Nijk =(int)totalPot.getValue(pos);
		sum+=f.logFactorial(Nijk);
	    }
	    if(vars.size()>1){
		pos = confPa.getIndexInTable();
		Nij=(int)parentsPot.getValue(pos);
	    }
	    partialSum = logFactXi - f.logFactorial(Nij+nStatesXi -1);
	    sum+=partialSum;
	    confPa.nextConfiguration();
	}
	
	return Math.exp(sum);
	
	
    }
    
} // K2Metrics



/* K2Metrics.java */

package elvira.learning;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Date;
import java.io.*;
import elvira.database.DataBaseCases;
import elvira.Bnet;
import elvira.NodeList;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.potential.*;
import elvira.parser.*;

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
    Hashtable[] cache;


 public static void main(String args[]) throws ParseException, IOException { 
    
     Bnet baprend;
     FileWriter f2;
     double time;
     NodeList nodesSorted;
     Metrics met;
     boolean var=false;
      if(args.length < 4){
	  System.out.println("too few arguments: Usage: file.dbc n.cases BIC,K2 file.elv ");
	  System.exit(0);
      }
      FileInputStream f = new FileInputStream(args[0]);
      DataBaseCases cases = new DataBaseCases(f);
      cases.setNumberOfCases(Integer.valueOf(args[1]).intValue());
      if(args[2].equals("BIC")) met = (Metrics) new BICMetrics(cases);
      else met = (Metrics) new K2Metrics(cases);
            
      FileInputStream fnet = null;
      fnet = new FileInputStream(args[3]);
      Bnet net = new Bnet(fnet);
      System.out.println("La medida es: "+met.score(net));

   }  




    
    public K2Metrics() {
    	f = new LogFactorial();
	setData(null);
    }
    
    public K2Metrics(DataBaseCases data){
    	f = new LogFactorial();
	setData(data);
	cache = new Hashtable[data.getNodeList().size()];
	for(int i=0 ; i< data.getNodeList().size(); i++)
	    cache[i] = new Hashtable();
    }


      public NodeList getNodeList(){
        
        return(getData().getNodeList());
    }
    
    public double score (Bnet b){
	
	NodeList vars,parentsX,varsXPa;
	int i,j;
	double logSum = 0.0;
	double valscore;
	FiniteStates nodeX,nodeY;
	
	vars = b.getNodeList();
	
	for(i=0; i< vars.size(); i++){
	    nodeX = (FiniteStates) vars.elementAt(i);
	    parentsX = b.parents(nodeX);
	    varsXPa = new NodeList();
	    varsXPa.insertNode(nodeX);
	    varsXPa.join(parentsX);
	    varsXPa = getData().getNodeList().
		      intersectionNames(varsXPa).sortNames(varsXPa);
	    valscore = score(varsXPa);
	    logSum+=valscore;
	}
	
	return logSum;

    }

    public double score (Bnet b, Hashtable scores){

	NodeList vars,parentsX,varsXPa;
	int i,j;
	double logSum = 0.0;
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
	    scores.put(nodeX,(new Double(valscore)));
	    logSum+=valscore;
	}
	
	return logSum;
    }


    public double score (Hashtable scores){
       
       FiniteStates nodex;
       double logSum=0.0;
       Enumeration nodes = scores.keys();
       
       while(nodes.hasMoreElements()){
	   nodex = (FiniteStates)nodes.nextElement();
	   logSum+= ((Double)scores.get(nodex)).doubleValue();
       }
       return logSum;
    }
    

    public double score (NodeList vars , Hashtable scores){

	double val;
	FiniteStates node;

	val = score(vars);
	node = (FiniteStates)vars.elementAt(0);
	scores.put(node,(new Double(val)));
	return score(scores);
    }

    public double score (NodeList vars){

	Configuration conf,confPa;
	PotentialTable totalPot,parentsPot;
	double sum = 0.0;
	NodeList ParentsXi,ParentsXid,varsaux;
	FiniteStates Xi;
	int nStatesXi,k,pos,Nij,Nijk,posXi;
	double j,nconfPa,logFactXi,partialSum;
	double time,timeInitial;
	Date D;
	D = new Date();
	timeInitial = (double)D.getTime();
	
	conf = confPa = null;
	Xi = (FiniteStates)vars.elementAt(0);
	ParentsXi = vars.copy();
	ParentsXi.removeNode(Xi);
	ParentsXid = vars.copy();
	ParentsXid.removeNode(Xi);
	posXi = getData().getNodeList().getId(Xi);
	ParentsXid.sort(getData().getNodeList());
	Double valor = (Double) cache[posXi].get(ParentsXid.toString2());
	
	if (valor == null){
	    totalPot = getData().getPotentialTable(vars);
	    //totalPot.print();
	    Nij=0; 
	    parentsPot = null;
	    if(vars.size()>1){
		parentsPot = (PotentialTable)totalPot.addVariable(Xi);
		//System.out.print("Tabla de los Padres :\n");
		//parentsPot.print();
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
	    //System.out.println("Numero de estados: "+nStatesXi);
	    logFactXi = f.logFactorial(nStatesXi-1);
            //System.out.println("Su log factorial es: "+logFactXi);
	
	    for(j=0.0 ; j< nconfPa ; j++ ){
		for(k=0 ; k< nStatesXi ; k++){
		    conf = new Configuration(vars.toVector(),confPa);
		    conf.putValue(Xi,k);
                    //System.out.print("ConfTotal: ");conf.print();
		    //pos = conf.getIndexInTable();
		    //System.out.println("Su posicion en table: "+pos);
		    Nijk =(int)totalPot.getValue(conf);
		    sum+=f.logFactorial(Nijk);
	            //System.out.println("logFact de: "+Nijk+" es: "+f.logFactorial(Nijk));
	            //try{System.in.read();}catch(IOException e){};
		}
		if(vars.size()>1){
		    //pos = confPa.getIndexInTable();
		    Nij=(int)parentsPot.getValue(confPa);
		}
		partialSum = logFactXi - f.logFactorial(Nij+nStatesXi -1);
		sum+=partialSum;
		confPa.nextConfiguration();
	    }
	    D = new Date();
            time = (double)D.getTime();
            totalTime+= (time - timeInitial)/1000;
	    timeStEval+=(time - timeInitial)/1000;
	    totalSt++;
            tStEval++;
            avStNVar+=(ParentsXi.size()+1);
	    valor = new Double(sum);
	    cache[posXi].put(ParentsXid.toString2(),valor);
	    return (valor.doubleValue());
	    
	}else{
             D = new Date();
	     time = (double)D.getTime();
             totalTime+= (time - timeInitial)/1000;
             totalSt++;
	     return (valor.doubleValue());
	}
    }
    public double score (FiniteStates X, FiniteStates Y, Configuration conf){
        
        NodeList vars;
        
        vars = new NodeList();
        vars.insertNode(X);
         vars.insertNode(Y);
         return(score(vars,conf));
        
        
    }

 public double score (NodeList vars, Configuration conf){

	Configuration confaux,confPa;
	PotentialTable parentsPot;
        PotentialTable totalPot;
	double sum = 0.0;
	NodeList ParentsXi,ParentsXid,varsaux;
	FiniteStates Xi;
	int nStatesXi,k,pos,Nij,Nijk,posXi;
	double j,nconfPa,logFactXi,partialSum;
	double time,timeInitial;
	Date D;
	D = new Date();
	timeInitial = (double)D.getTime();
	
	confaux = confPa = null;
	Xi = (FiniteStates)vars.elementAt(0);
	ParentsXi = vars.copy();
	ParentsXi.removeNode(Xi);
	ParentsXid = vars.copy();
	ParentsXid.removeNode(Xi);
	posXi = getData().getNodeList().getId(Xi);
	
	
	    totalPot = getData().getPotentialTable(vars,conf);
	    //totalPot.print();
	    Nij=0; 
	    parentsPot = null;
	    if(vars.size()>1){
		parentsPot = (PotentialTable) totalPot.addVariable(Xi);
		//System.out.print("Tabla de los Padres :\n");
		//parentsPot.print();
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
	    //System.out.println("Numero de estados: "+nStatesXi);
	    logFactXi = f.logFactorial(nStatesXi-1);
            //System.out.println("Su log factorial es: "+logFactXi);
	
	    for(j=0.0 ; j< nconfPa ; j++ ){
		for(k=0 ; k< nStatesXi ; k++){
		    confaux = new Configuration(vars.toVector(),confPa);
		    confaux.putValue(Xi,k);
                    //System.out.print("ConfTotal: ");conf.print();
		    //pos = conf.getIndexInTable();
		    //System.out.println("Su posicion en table: "+pos);
		    Nijk =(int)totalPot.getValue(confaux);
		    sum+=f.logFactorial(Nijk);
	            //System.out.println("logFact de: "+Nijk+" es: "+f.logFactorial(Nijk));
	            //try{System.in.read();}catch(IOException e){};
		}
		if(vars.size()>1){
		    //pos = confPa.getIndexInTable();
		    Nij=(int)parentsPot.getValue(confPa);
		}
		partialSum = logFactXi -  f.logFactorial(Nij+nStatesXi -1);
		sum+=partialSum;
		confPa.nextConfiguration();
	    }
	    D = new Date();
            time = (double)D.getTime();
            totalTime+= (time - timeInitial)/1000;
	    timeStEval+=(time - timeInitial)/1000;
	    totalSt++;
            tStEval++;
            avStNVar+=(ParentsXi.size()+1);
	   
	 
	    return (sum);
	    
	
    }
    
 public double score(double[] frec,int nc,double ess){
     double sk; 
     int i;
       double N;
        double x,a,total;
         N=0;
	   total=0.0;
     
        for (i=0;i<nc;i++){
             
                a = f.logFactorial((int) frec[i]);
               total +=a;
               N  += frec[i];
            }
           
        
	   total += f.logFactorial(nc-1) - f.logFactorial((int) N+nc-1);
            
	  return(total);
   }
   
   
   public double score(double[][] frec,int nc,int nx){
     double sk,sp; 
     int i,j;
       double N;
        double x,a,total;
       
	   total=0.0;
    
        for (j=0; j<nx;j++){
          N=0.0;
        for (i=0;i<nc;i++){
             
                a = f.logFactorial((int) frec[i][j]);
               total +=a;
               N  += frec[i][j];
            }
        
	   total += f.logFactorial(nc-1) - f.logFactorial((int) N+nc-1);
        } 
           
	  return(total);
   }
      
 public double scorecon(double[][] frec,int nc,int nx,double[] leng, double lengt){
     double sk,sp,tk; 
     int i,j;
       double N;
        double a,total;
   
     total=0.0;
         sk = nc;
        sp= nc;
          
        for (j=0; j<nx;j++){
        
          
          N=0;
        
        for (i=0;i<nc;i++){
             //   tk=sk*leng[i]/lengt;
            tk=1.0;
            //    System.out.println("tk " + tk);
                a = f.gammaln(frec[i][j]+tk );
               total +=a;
               total -= f.gammaln(tk );
               N  += frec[i][j];
            }
            
        
	   total += (f.gammaln(sp) - f.gammaln(N+sp));
        } 
        
        
           
	  return(total);
   }
 
 
    public double score (FiniteStates X, Configuration conf){

	Configuration confPa;
	PotentialTable totalPot;
	double sum = 0.0;
	NodeList ParentsX,varsaux;
	int nStatesX,k,pos,Nij,Nijk,posX;
	double j,nconfPa,logFactXi,partialSum;
	double time,timeInitial,NPijk,NPij,a;
	Date D;
	D = new Date();
	timeInitial = (double)D.getTime();
	
	
        ParentsX = new  NodeList(conf.getVariables()); 
	
	totalPot =  getData().getPotentialTable(X,conf);
          
	
	{
	  
	    //totalPot.print();
	    
	    
	    nStatesX = X.getNumStates();
	    NPijk = 1;
	    NPij = nStatesX;
	    Nij=0;
		for(k=0 ; k< nStatesX ; k++){
                    //System.out.print("ConfTotal: ");conf.print();
		    //pos = conf.getIndexInTable();
		    //System.out.println("Su posicion en table: "+pos);
		    Nijk =(int)totalPot.getValue(k);
                    sum+=f.logFactorial(Nijk);
                    Nij+= Nijk;
	            //try{System.in.read();}catch(IOException e){};
		}
		
		    //pos = confPa.getIndexInTable();
		  
		
		partialSum = f.logFactorial(nStatesX-1) - f.logFactorial(Nij+nStatesX -1) ;
		sum+=partialSum;
		
	    
	    D = new Date();
            time = (double)D.getTime();
            totalTime+= (time - timeInitial)/1000;
	    timeStEval+=(time - timeInitial)/1000;
	    totalSt++;
            tStEval++;
	    return (sum);
	    
	}
    }
    
   
     public double scoref (FiniteStates Cl, FiniteStates X, Configuration condition){

	
        double totalfreq,a,N;
	double[][] freq;
        double [] frec, frex;
        int nc,nx;
        double size;
       double sum,sumx, result;
        int i,j;
        double incc,inct,incx,aux;
        double maxprobc,maxcond,auxmax;
        double[] probc;
        NodeList varconf;
        double tme;
        
        
	    freq = getData().getFreq(Cl,X,condition);
	    //totalPot.print();
	     
            nc = Cl.getNumStates();
            nx = X.getNumStates();
            
            frec = new double[nc];
            
            frex = new double[nx];
            probc  = new double[nc];
	    size = 0.0;
            for (i=0; i<nc;i++){
             frec[i] = 0.0;   
                 for (j=0; j<nx;j++){
               frec[i]+= freq[i][j];
            }
             size += frec[i];
            }  
               for (j=0; j<nx;j++){
             frex[j] = 0.0;   
                for (i=0; i<nc;i++){
               frex[j]+= freq[i][j];
            }
            
            }  
            sum = 0.0;
            varconf = new NodeList(condition.getVariables());
            aux = varconf.getSize();
            
            inct = 1.0;
            incc = 1.0;
            incx = 1.0;
            tme = nc;
 
              maxprobc = -1.0;
                for (i=0; i<nc;i++){
                  if (frec[i]> maxprobc) {maxprobc = frec[i];}
                  probc[i] = (frec[i] + incc)/(size+tme);
                }
              
              maxprobc = (maxprobc + incc)/(size+tme);
              
              maxcond = 0.0;
              double maxerror = 0.0;
              int indexmax;
              
               for (j=0; j<nx;j++){
                    auxmax = -1.0;
                    indexmax=0;
                    for (i=0; i<nc;i++){
                        if (freq[i][j]>auxmax) {auxmax = freq[i][j];indexmax=i;}
                        
                    }
                    
                    maxerror += ((frec[indexmax] + incc)/(size+tme)) * ((frex[j]+incx)/(size+nx));
                  
                    maxcond += auxmax;
                  
               }
              
              maxcond = (maxcond+1.0)/(size+tme);
     //         double lfnc   = f.logFactorial(nc-1);
      
              sum = 0.0;
              sumx = 0.0;
           
          
              
              for (j=0; j<nx;j++){
        N=0;
           for (i=0;i<nc;i++){
             
                a = f.logFactorial((int) freq[i][j]);  
               sum +=a;
               N  += freq[i][j];
            }
        
	   sum += f.logFactorial(nc-1) - f.logFactorial((int) N+nc-1);
        } 
              
            N=0;
	
     
        for (i=0;i<nc;i++){
             
                a = f.logFactorial((int) frec[i]);
               sumx +=a;
               N  += frec[i]; 
            }
           
        
	     sumx += f.logFactorial(nc-1) - f.logFactorial((int) N+nc-1);
            
              
               
              
        //      sumx += lfnc -  f.logFactorial((int) size + nc -1);
              
	    //System.out.println("Numero de estados: "+nStatesXi);
	  
            //System.out.println("Su log factorial es: "+logFactXi);
	
              
          //    System.out.println(X.getName() + ": Max COnd " + maxcond + "Pre Prob " + maxprobc + "More error " + maxerror);
result = Math.exp(sum) *  maxcond + Math.exp(sumx)* maxerror -
    (Math.exp(sum)+ Math.exp(sumx) ) *( maxprobc);
	
return(result);
	   
	    
	}
    
     
     
     
     
    } // K2Metrics

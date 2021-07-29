/* BDeMetrics.java */

package elvira.learning;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Date;
import java.io.*;
import elvira.database.DataBaseCases;
import elvira.Bnet;
import elvira.Node;
import elvira.NodeList;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.ConditionalIndependence;
import elvira.potential.*;
import elvira.parser.*;

/**
 * BDeMetrics.java
 *
 *
 * Created: Mon Nov  8 11:09:40 1999
 *
 * @author P. Elvira
 * @version  1.0
 */

public class BDeMetrics extends Metrics implements ConditionalIndependence{
  
    LogFactorial f; // For storing and computing the log factorial n.
    Hashtable[] cache;
    double tme = 1.0;

 public static void main(String args[]) throws ParseException, IOException { 
   
     Bnet baprend;
     FileWriter f2;
     double time;
     NodeList nodesSorted;
     Metrics met;
     boolean var=false;
      if(args.length < 4){
	  System.out.println("too few arguments: Usage: file.dbc n.cases BIC,K2,BDe file.elv ");
	  System.exit(0);
      }
      FileInputStream f = new FileInputStream(args[0]);
      DataBaseCases cases = new DataBaseCases(f);
      cases.setNumberOfCases(Integer.valueOf(args[1]).intValue());
      if(args[2].equals("BIC")) met = (Metrics) new BICMetrics(cases);
      else if(args[2].equals("K2")) met = (Metrics) new K2Metrics(cases);
      else met =  met = (Metrics) new BDeMetrics(cases);
            
      FileInputStream fnet = null;
      fnet = new FileInputStream(args[3]);
      Bnet net = new Bnet(fnet);
      System.out.println("La medida es: "+met.score(net));

   }  




    
    public BDeMetrics() {
    	f = new LogFactorial();
	setData(null);
    }
    
    public BDeMetrics(DataBaseCases data){
    	f = new LogFactorial();
	setData(data);
	cache = new Hashtable[data.getNodeList().size()];
	for(int i=0 ; i< data.getNodeList().size(); i++)
	    cache[i] = new Hashtable();
    }

    public BDeMetrics(DataBaseCases data,int tme){
    	f = new LogFactorial();
	setData(data);
	cache = new Hashtable[data.getNodeList().size()];
	for(int i=0 ; i< data.getNodeList().size(); i++)
	    cache[i] = new Hashtable();
	this.tme = tme;
    }
    
      public BDeMetrics(DataBaseCases data,double tme){
    	f = new LogFactorial();
	setData(data);
	cache = new Hashtable[data.getNodeList().size()];
	for(int i=0 ; i< data.getNodeList().size(); i++)
	    cache[i] = new Hashtable();
	this.tme = tme;
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
    
    public double getDep (Node x, Node y, NodeList z) {
  return(scoreDep(x,y,z));
    
    
}   
    
    public double scoreDep (Node x, Node y, NodeList z) {
    
    NodeList aux;
    double x1,x2;
    
    aux = new NodeList();
    aux.insertNode(x);
    aux.join(z);
    x1 = score(aux);
    aux.insertNode(y);
    x2 = score(aux);
    return(x2-x1);
    
    
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
	double time,timeInitial,NPijk,NPij,a;
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
		parentsPot = (PotentialTable) totalPot.addVariable(Xi);
		//System.out.print("Tabla de los Padres :\n");
		//parentsPot.print();
		confPa = new Configuration(ParentsXi);
		nconfPa = FiniteStates.getSize(ParentsXi);
       //         System.out.println("Parent config " + nconfPa);
                
	    }
		
	    else{
		nconfPa = 1.0;
		varsaux = new NodeList();
		varsaux.insertNode(Xi);
		confPa = new Configuration(varsaux);
		Nij = (int)totalPot.totalPotential();
	    }
	    
            
          
        
	    nStatesXi = Xi.getNumStates();
	    NPijk = tme/(nStatesXi*nconfPa);
	    NPij = tme/nconfPa;
	    for(j=0.0 ; j< nconfPa ; j++ ){
		for(k=0 ; k< nStatesXi ; k++){
		    conf = new Configuration(vars.toVector(),confPa);
		    conf.putValue(Xi,k);
                    //System.out.print("ConfTotal: ");conf.print();
		    //pos = conf.getIndexInTable();
		    //System.out.println("Su posicion en table: "+pos);
		    Nijk =(int)totalPot.getValue(conf);
		    a = f.gammaln((((double)Nijk)+NPijk));
		    sum+=(a-f.gammaln(NPijk));
	            //try{System.in.read();}catch(IOException e){};
		}
		if(vars.size()>1){
		    //pos = confPa.getIndexInTable();
		    Nij=(int)parentsPot.getValue(confPa);
		}
		partialSum = f.gammaln(NPij) - f.gammaln((((double)Nij)+NPij));
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
	    Nij=0; 
	    if(ParentsX.size()>0){
		//System.out.print("Tabla de los Padres :\n");
		//parentsPot.print();
		nconfPa = FiniteStates.getSize(ParentsX);
	    }
	    else{
		nconfPa = 1.0;
	    }
	    
	    nStatesX = X.getNumStates();
	    NPijk = tme/(nStatesX*nconfPa);
	    NPij = tme/nconfPa;
	    Nij=0;
		for(k=0 ; k< nStatesX ; k++){
                    //System.out.print("ConfTotal: ");conf.print();
		    //pos = conf.getIndexInTable();
		    //System.out.println("Su posicion en table: "+pos);
		    Nijk =(int)totalPot.getValue(k);
		    a = f.gammaln((((double)Nijk)+NPijk));
		    sum+=(a-f.gammaln(NPijk));
                    Nij+= Nijk;
	            //try{System.in.read();}catch(IOException e){};
		}
		
		    //pos = confPa.getIndexInTable();
		  
		
		partialSum = f.gammaln(NPij) - f.gammaln((((double)Nij)+NPij));
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
        double size,a;
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
	
	
        NodeList auxList = new  NodeList(conf.getVariables()); 
        size = auxList.getSize();
	
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
	
	double    NPijk = tme/(nStatesXi*nconfPa*size);
	double     NPij = tme/(nconfPa*size);
	    for(j=0.0 ; j< nconfPa ; j++ ){
		for(k=0 ; k< nStatesXi ; k++){
		    confaux = new Configuration(vars.toVector(),confPa);
		    confaux.putValue(Xi,k);
                    //System.out.print("ConfTotal: ");conf.print();
		    //pos = conf.getIndexInTable();
		    //System.out.println("Su posicion en table: "+pos);
		    Nijk =(int)totalPot.getValue(confaux);
		    a = f.gammaln((((double)Nijk)+NPijk));
		    sum+=(a-f.gammaln(NPijk));
	            //try{System.in.read();}catch(IOException e){};
		}
		if(vars.size()>1){
		    //pos = confPa.getIndexInTable();
		    Nij=(int)parentsPot.getValue(confPa);
		}
		partialSum = f.gammaln(NPij) - f.gammaln((((double)Nij)+NPij));
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
        sk = ess/nc;
        for (i=0;i<nc;i++){
             
                a = f.gammaln(frec[i]+sk);
               total +=a;
               N  += frec[i];
            }
            total -= nc*f.gammaln(sk);
        
	   total += f.gammaln(ess) - f.gammaln(N+ess);
            
	  return(total);
   }
   
   
   public double score(double[][] frec,int nc,int nx, double ess){
     double sk,sp; 
     int i,j;
       double N;
        double x,a,total;
       
	   total=0.0;
         sk = ess/(nc*nx);
        sp= ess/nx;
          
        for (j=0; j<nx;j++){
          N=0.0;
        for (i=0;i<nc;i++){
             
                a = f.gammaln(frec[i][j]+sk);
               total +=a;
               N  += frec[i][j];
            }
            total -= nc*f.gammaln(sk);
        
	   total += (f.gammaln(sp) - f.gammaln(N+sp));
        } 
        
        
           
	  return(total);
   }
   
   
   
   public double score0(double[][] frec,int nc,int nx, double ess){
     double sk,sp; 
     int i,j,not,not2;
       double N;
        double x,a,total;
        boolean change;
          
	   total=0.0;
       
         
         not2=0;
         for (j=0; j<nx;j++){ 
             change = false;
              for(i=0; i<nc; i++) {
             if(frec[i][j] >0) { change=true;}
          }
               if (change) {not2++;}
         }
          
         
           
        sp= ess/not2;
        
        
        for (j=0; j<nx;j++){
          N=0.0;  not = 0;
          for(i=0; i<nc; i++) {
             
             if(frec[i][j] >0) { not++;}
          }
         
            if(not>0) 
            {
                sk = sp/not;
          
        for (i=0;i<nc;i++){
             
                a = f.gammaln(frec[i][j]+sk);
               total +=a;
               N  += frec[i][j];
            }
            total -= nc*f.gammaln(sk);
        
	   total += (f.gammaln(sp) - f.gammaln(N+sp));
            }
         
        }
        
        
           
	  return(total);
   }
   
   
   public double scorecon(double[][] frec,int nc,int nx,double ess, double[] leng, double lengt){
     double sk,sp,tk; 
     int i,j;
       double N;
        double a,total;
   
     total=0.0;
        
        sp= ess/nx;
        sk = sp;
          
        for (j=0; j<nx;j++){
        
          
          N=0;
        
        for (i=0;i<nc;i++){
                tk=sk*leng[i]/lengt;
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
 
   
   public double score0(double[][] frec,int nc,int nx){
    
           
	  return(score0(frec,nc,nx,tme));
   }
   
   
      
    
    public double score (PotentialTable t, double ess){

	int i;
        double N;
        long nconf;
        double x,a,total;
	double sk;
      
          
	
	{  N=0;
	   total=0.0;
	    nconf= t.getSize();
            sk = ess/nconf;
            
            for (i=0;i<nconf;i++){
                x = t.getValue(i);
                a = f.gammaln(x+sk);
               total +=a;
               N  += x;
            }
            total -= nconf*f.gammaln(sk);
        
	   total += f.gammaln(ess) - f.gammaln(N+ess);
            
	  return(total);
	    
	}
    }
    
   
    
    
 
   
    public double scoreDep2 (Node x, Node y, NodeList z) {
    
    NodeList aux;
    double x1,x2;
    
    aux = new NodeList();
    aux.insertNode(x);
    aux.insertNode(y);
    aux.join(z);
    x2 = score(aux);
    return(x2);
    
    
}
  
public boolean independents (Node x, Node y, NodeList z) {
    
  double aux;
  
  aux = scoreDep(x,y,z);
  if (aux<=0) {return(true);}
  else {return(false);}
    
 
  
}


   
public boolean independents (Node x, Node y, NodeList z, int degree) {
    
  return(independents(x,y,z));
    
 
  
}


     public double scoref (FiniteStates Cl, FiniteStates X, Configuration condition){

	
        double totalfreq;
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
            
            
 //           System.out.println("Size :" + size);
            sum = 0.0;
            varconf = new NodeList(condition.getVariables());
            aux = varconf.getSize();
            aux=1.0;
            
            inct = tme/((double) nc*nx*aux);
            incc = tme/((double) nc*aux);
            incx = tme/((double) nx*aux);
 
              maxprobc = -1.0;
                for (i=0; i<nc;i++){
                  if (frec[i]> maxprobc) {maxprobc = frec[i];}
                  probc[i] = (frec[i] + incc)/(size+tme/aux);
                }
              
              maxprobc = (maxprobc + incc)/(size+tme/aux);
              
              maxcond = 0.0;
              double maxerror = 0.0;
              int indexmax;
              
               for (j=0; j<nx;j++){
                    auxmax = -1.0;
                    indexmax=0;
                    for (i=0; i<nc;i++){
                        if (freq[i][j]>auxmax) {auxmax = freq[i][j];indexmax=i;}
                        
                    }
                    
                    maxerror += ((frec[indexmax] + incc)/(size+tme/aux)) * ((frex[j]+incx)/(size+tme/aux));
                    auxmax += inct;
                    maxcond += auxmax;
                  
               }
              
              maxcond = maxcond/(size+tme/aux);
     //         double lfnc   = f.logFactorial(nc-1);
           double   gltotal = f.gammaln(inct);
             double   glc = f.gammaln(incc);
              sum = 0.0;
              sumx = 0.0;
              
               for (j=0; j<nx;j++){
                
                    for (i=0; i<nc;i++){
                              sum +=  f.gammaln(freq[i][j]+inct);
		              sum-= gltotal;
                   //        sum+=f.logFactorial((int) freq[i][j]);  
                    }
                    sum-=  f.gammaln(frex[j]+incx)  -  f.gammaln(incx );
               }
              
              
                    for (i=0; i<nc;i++){
                     //      sumx+=f.logFactorial((int) frec[i]);  
                    sumx+= f.gammaln(frec[i]+incc)  -  glc;
                    }
               sumx-=  f.gammaln(size + tme/aux)  -  f.gammaln(tme/aux);
              
        //      sumx += lfnc -  f.logFactorial((int) size + nc -1);
              
	    //System.out.println("Numero de estados: "+nStatesXi);
	  
            //System.out.println("Su log factorial es: "+logFactXi);
	
              
          //    System.out.println(X.getName() + ": Max COnd " + maxcond + "Pre Prob " + maxprobc + "More error " + maxerror);

       //        double a = 3*Math.exp(sum) + Math.exp(sumx);
              result = Math.exp(sum) *  maxcond + Math.exp(sumx)* maxerror -
    (Math.exp(sum)+ Math.exp(sumx) ) *( maxprobc);
              
              
        
	
  //         result = 0.5 *  maxcond + 0.5 * maxerror -
  // (1.0 ) *( maxprobc);

//             return(sum-sumx);
               
              return(result);
 //
 //
	   
	    
	}
    
 
     public double scoref (FiniteStates Cl, FiniteStates X1, FiniteStates X2){

	
        double totalfreq;
	double[][][] freqc12,probit;
        double [][] frec1, frec2,fre12;
         double [][] probi12;
        double [] frec,fre1,fre2;
        int nc,nx1,nx2;
        double size;
       double sum,sumx, result,maxaux;
        int i,j,k,maxi;
        double incc,inct,inc1,inc2,inc12;
        double goodtded,goodtied;
        double goodtdei,goodtiei;
        double[] probc;
        NodeList varconf;
        
        
        
	    freqc12 = getData().getFreq(Cl,X1,X2);
	    //totalPot.print();
	     
            nc = Cl.getNumStates();
            nx1 = X1.getNumStates();
            nx2 = X2.getNumStates();
            
            frec = new double[nc];
             frec1 = new double[nc][nx1];
              frec2 = new double[nc][nx2];
              fre12 =  new double[nx1][nx2];
              probi12 =  new double[nx1][nx2];
               probit =  new double[nc][nx1][nx2];
          
	    size = 0.0;
            for (i=0; i<nc;i++){
                   for (j=0; j<nx1;j++){
             frec1[i][j] = 0.0;   
                 for (k=0; k<nx2;k++){
               frec1[i][j] += freqc12[i][j][k];
            }
             size += frec1[i][j];
            }
            }
              
          
       
	    
            for (i=0; i<nc;i++){
                for (k=0; k<nx2;k++){
                  
                 frec2[i][k] = 0.0;   
                 for (j=0; j<nx1;j++){ 
                     frec2[i][k] += freqc12[i][j][k];
            }
            
            }
            }
                      
            for (i=0; i<nc;i++){
                  frec[i] = 0.0;  
                   for (j=0; j<nx1;j++){
                      frec[i] += frec1[i][j];
                   }
            }
            
            
                  for (j=0; j<nx1;j++){
                    for (k=0; k<nx2;k++){
                      fre12[j][k] = 0.0;   
                       for (i=0; i<nc;i++){
                           fre12[j][k] += freqc12[i][j][k];
                       }
            
                    }
                  }
          
              
            inct = tme/((double) nc*nx1*nx2);
            incc = tme/((double) nc);
            inc1 = tme/((double) nx1);  inc2 = tme/((double) nx2);
            inc12 = tme/((double) nx1*nx2);
       double        incc1 = tme/((double) nc*nx1);   double        incc2 = tme/((double) nc*nx2);
            
              for (j=0; j<nx1;j++){
                    for (k=0; k<nx2;k++){
                      probi12[j][k] = 0.0;   
                       for (i=0; i<nc;i++){
                           probi12[j][k] += ((frec1[i][j]+incc1)/(size+tme))*
                                             ((frec2[i][k]+incc2)/(frec[i]+incc))  ;
                          
                           probit[i][j][k] = ((frec1[i][j]+incc1)/(size+tme))*
                                             ((frec2[i][k]+incc2)/(frec[i]+incc))  ;
                         
                       }
            
                    }
                  }
          
            
            
            goodtied = 0.0;
            goodtded = 0.0;
           
            for(j=0; j<nx1; j++){
                for(k=0; k<nx2; k++){
                    maxaux = 0.0;
                     maxi=0;
            
                    for (i=0; i<nc; i++){
                        if (freqc12[i][j][k]>maxaux){maxaux=freqc12[i][j][k]; maxi=i;}
                       
                    }
                    goodtded += maxaux+inct;
                    goodtied +=  ((frec1[maxi][j]+incc1)/(size+tme))*
                                             ((frec2[maxi][k]+incc2)/(frec[maxi]+incc))  ;
                    
                }
                
            }
            
            
            goodtded =  goodtded /(size+tme);
          
           
              
            goodtiei = 0.0;
            goodtdei = 0.0;
           
            for(j=0; j<nx1; j++){
                for(k=0; k<nx2; k++){
                    maxaux = 0.0;
                     maxi=0;
            
                    for (i=0; i<nc; i++){
                        if (probit[i][j][k]>maxaux){maxaux=probit[i][j][k]; maxi=i;}
                       
                    }
                    
                    goodtiei += maxaux;
                    goodtdei += freqc12[maxi][j][k] + inct;
                    
                }
                
            }
             
            
             goodtdei =  goodtdei /(size+tme);
            
            
            
               
           double   gltotal = f.gammaln(inct);
             double   glc = f.gammaln(incc);
              double   glc1 = f.gammaln(incc1);
                double   glc2 = f.gammaln(incc2);
              sum = 0.0;
    double          sumx1 = 0.0;
    double           sumx2 = 0.0;
    double           sumx12 = 0.0;          
                  
             
                   for (i=0; i<nc;i++){
                       for (j=0; j<nx1;j++){
                              sumx1 +=  f.gammaln(frec1[i][j]+incc1);
		              sumx1-= glc1;
                   //        sum+=f.logFactorial((int) freq[i][j]);  
                    }
                    sumx1-=  f.gammaln(frec[i]+incc)  -  glc;
               }
    
          for (i=0; i<nc;i++){
                       for (k=0; k<nx2;k++){
                              sumx2 +=  f.gammaln(frec2[i][k]+incc2);
		              sumx2-= glc2;
                   //        sum+=f.logFactorial((int) freq[i][j]);  
                    }
                    sumx2-=  f.gammaln(frec[i]+incc)  -  glc;
               }
    
              
      for (i=0; i<nc;i++){
                       for (j=0; j<nx1;j++){
                             for (k=0; k<nx2;k++){
                              sumx12 +=  f.gammaln(freqc12[i][j][k]+inct);
		              sumx12-= gltotal;
                   //        sum+=f.logFactorial((int) freq[i][j]);  
                    }
                  
               }
      
        sumx12-=  f.gammaln(frec[i]+incc)  -  glc; 
      }
 
 //  System.out.println(X1.getName() + " " + X2.getName() );
 //    System.out.println("Dep " + sumx12 + "Indep 1" + sumx1  + "Indep 2" + sumx2 );
// System.out.println("tded: " +  goodtded + "tdei: " +  goodtdei + "tiei: " +  goodtiei + "tied: " +  goodtied  );    
             
result = Math.exp(sumx12) * goodtded   + Math.exp(sumx1+sumx2)* goodtied -
 Math.exp(sumx12) * goodtdei   - Math.exp(sumx1+sumx2)* goodtiei ;


result = +sumx12-sumx1-sumx2;
return(result);
	
	    
	}
    
         
   
public boolean independents (Node x, Node y, NodeList z, double degree) {
    
  return(independents(x,y,z));
    
 
  
}


} // BDeMetrics



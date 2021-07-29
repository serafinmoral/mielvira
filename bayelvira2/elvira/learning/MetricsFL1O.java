/*
 * MetricsFL1O.java
 *
 * Created on 26 de junio de 2007, 13:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package elvira.learning;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.*;
import java.io.*;
import elvira.database.DataBaseCases;
import elvira.Bnet;
import elvira.NodeList;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.potential.*;
import elvira.parser.*;

import elvira.learning.preprocessing.*;
import elvira.tools.statistics.analysis.Stat;
/**
 *
 * @author smc
 */
public class MetricsFL1O extends L1OMetrics{
    
    /** Creates a new instance of MetricsFL1O */
    public MetricsFL1O() {
    }
 
    public MetricsFL1O(DataBaseCases data){
	setData(data);
	cache = new Hashtable[data.getNodeList().size()];
	for(int i=0 ; i< data.getNodeList().size(); i++)
	    cache[i] = new Hashtable();
    }   
    
 public double score (NodeList vars, Configuration condition){

	Configuration conf,confPa;
	PotentialTable totalPot,parentsPot;
	double sum = 0.0;
        double[] freq;
	NodeList ParentsXi,ParentsXid,varsaux;
	FiniteStates Xi;
	int nStatesXi,k,pos,Nij,Nijk,posXi;
	double j,nconfPa,logFactXi,partialSum;
	double time,timeInitial;
        int maxfreq1,maxfreq2;
	Date D;
	D = new Date();
	timeInitial = (double)D.getTime();
	
	conf = confPa = null;
       
	Xi = (FiniteStates)vars.elementAt(0);
	ParentsXi = vars.copy();
       sum = 0.0;
	ParentsXi.removeNode(Xi);
	ParentsXid = vars.copy();
	ParentsXid.removeNode(Xi);
	posXi = getData().getNodeList().getId(Xi);
	
	{
	    totalPot = getData().getPotentialTable(vars,condition);
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
             freq = new double[nStatesXi];
	    //System.out.println("Numero de estados: "+nStatesXi);
	  
            //System.out.println("Su log factorial es: "+logFactXi);
	
	    for(j=0.0 ; j< nconfPa ; j++ ){
                 maxfreq1 = 0;maxfreq2 = 0;
		for(k=0 ; k< nStatesXi ; k++){
		    conf = new Configuration(vars.toVector(),confPa);
		    conf.putValue(Xi,k);
                    //System.out.print("ConfTotal: ");conf.print();
		    //pos = conf.getIndexInTable();
		    //System.out.println("Su posicion en table: "+pos);
		    Nijk =(int)totalPot.getValue(conf);
                    freq[k] = Nijk;
		    if (Nijk>maxfreq1) {
                        maxfreq2 = maxfreq1; maxfreq1 = Nijk;}
                    else {if (Nijk>maxfreq2) { maxfreq2 =Nijk;}}
	       
	            //try{System.in.read();}catch(IOException e){};
		}
                 
                 for(k=0 ; k< nStatesXi ; k++){
		  
                   if ( freq[k]-1 > maxfreq2) {sum += freq[k];}
                   if ( freq[k]-1 == maxfreq2) {sum += freq[k]/2.0;}
		 
	       
	            //try{System.in.read();}catch(IOException e){};
		}
                 
	
                
		
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
    }
    public double score (FiniteStates X, Configuration conf){

double[] freq;
	PotentialTable totalPot;
	double sum = 0.0;
        double maxfreq1,maxfreq2;
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
	    
	  
	    nStatesX = X.getNumStates();   freq = new double[nStatesX];
	 
	  maxfreq1 = 0;maxfreq2 = 0;
		for(k=0 ; k< nStatesX ; k++){
		   
                    //System.out.print("ConfTotal: ");conf.print();
		    //pos = conf.getIndexInTable();
		    //System.out.println("Su posicion en table: "+pos);
		    
                    freq[k] = totalPot.getValue(k);
                 
		    if (freq[k]>maxfreq1) {
                        maxfreq2 = maxfreq1; maxfreq1 = freq[k];}
                    else {if (freq[k]>maxfreq2) { maxfreq2 =freq[k];}}
	       
	            //try{System.in.read();}catch(IOException e){};
		}
                 
                 for(k=0 ; k< nStatesX ; k++){
		  
                   if ( freq[k]-1 > maxfreq2) {sum += freq[k];}
                   if ( freq[k]-1 == maxfreq2) {sum += freq[k]/2.0;}
		 
	       
	            //try{System.in.read();}catch(IOException e){};
		}
                 
	
                
		
	    
	    D = new Date();
            time = (double)D.getTime();
            totalTime+= (time - timeInitial)/1000;
	    timeStEval+=(time - timeInitial)/1000;
	    totalSt++;
            tStEval++;
	    return (sum);
	    
	}
    }
     
    
    
}

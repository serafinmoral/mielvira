/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.learning;

import java.util.*;
import java.io.*;
import elvira.*;
import elvira.database.DataBaseCases;
import elvira.parser.ParseException;

/**
 *
 * @author smc
 */
public class Experiment {
    // A List of Nodes sorted.
    DataBaseCases input;   // The cases for the input algorithm.
    BDeMetrics metric;      // The decomposable metric for scoring.
    PrintWriter output;       // Index for the input nodes, nodei<begining are considered as root nodes.
    
    
 public static void main(String args[]) throws ParseException, IOException { 
    
   
     PrintWriter f2;
     double ess;
    
     BDeMetrics met;
    
      FileInputStream f = new FileInputStream(args[0]);
       f2 = new PrintWriter(args[2]);
      DataBaseCases cases = new DataBaseCases(f);
       ess= Double.valueOf(args[1]).doubleValue();
       met = new BDeMetrics(cases,ess);
       Experiment exper = new Experiment(cases,met,f2);
       
       exper.parents();
       
   }     
 
 public Experiment(DataBaseCases cases, BDeMetrics met, PrintWriter f2) {
     
     input=cases;
     metric = met;
     output = f2;
     
     
 }
 
 

 
 public void parents() {
     FiniteStates target;
     int size;
     NodeList vars, total,parents;
     double fitness, fitness2;
     FiniteStates var;
     double val,max,max2,val2;
     boolean  first;
     int maxparents = 9;
    
     int n,i,imax,imax2;
 
     
     size = input.getNodeList().size();
     total = input.getNodeList();
      target = (FiniteStates) total.elementAt(size-1);
	    
       n=0;
	    vars = new NodeList();
	    vars.insertNode(target);
            parents = new NodeList();
	    fitness = metric.score(vars);
        //    fitness2 = computeScore(target,vars);
            output.println(n + " , " + fitness );
            
            
	    //System.out.println(fitness);
	   
	    while(vars.size()<maxparents){
                 first = true;
                    imax = 0;
                    max = 0.0; max2 = 0.0;
                for(i=0; i<size-1; i++){
                   
	        var = (FiniteStates) total.elementAt(i);
	        if(vars.getId(var) == -1){
                       System.out.println("Candidato padre " + var.getName());
                      
	              vars.insertNode(var);
		      val = metric.score(vars);
                   //   val2 = computeScore(target,parents);
                      
                       System.out.println("Score " + val);
                      vars.removeNode(vars.size()-1);
                  //    parents.removeNode(var);
                      if (first) {
                          max = val;
                          imax = i;
                          first = false;
                    //      max2 = val2;
                          
                      }
                      else {
		        if(val > max){
		             max = val;
		             imax = i;
                        //     max2 = val;
		           }
	               }
                }
                }
		vars.insertNode(total.elementAt(imax));
                parents.insertNode(total.elementAt(imax));
                System.out.println("Entrando " + imax) ;
                fitness = max;
                //fitness2 = max2;
                n++;
                  output.println(n + " , " + fitness);
		
	    }
	    //System.out.println(PaXi.toString2());
	    //try{
	    //	System.in.read();
	    //}catch (IOException e){};
            output.close();
	}
     
      
  

   
   
 

    
}

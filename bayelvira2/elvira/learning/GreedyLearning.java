/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.learning;
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import java.util.Date;

import elvira.*;
import elvira.potential.*;
import elvira.database.DataBaseCases;
import elvira.parser.ParseException;
/**
 *
 * @author smc
 */
public class GreedyLearning extends Learning  {
   public BDeMetrics metscores;
       ConditionalIndependence input; 
       double delay;   
   
    public static void main(String args[]) throws ParseException, IOException { 

      BDeMetrics metric;
      Bnet net, baprend;
      FileWriter f2;
      boolean[][] paths;
      int method,i,j;
      net = null;
      if(args.length < 4){
	  System.out.println("too few arguments: Usage: file.dbc numberCases file.elv method  [file.elv (true net to be compared)]");
	  System.exit(0);
      }


      FileInputStream f = new FileInputStream(args[0]);
      DataBaseCases cases = new DataBaseCases(f);
      cases.setNumberOfCases(Integer.valueOf(args[1]).intValue());
      method = (Integer.valueOf(args[3]).intValue());
     
      if(args.length > 4){ 
          FileInputStream fnet = new FileInputStream(args[4]);
          net = new Bnet(fnet); 
      } 
      GreedyLearning outputNet2 = new GreedyLearning(cases);
     
      outputNet2.learning();
      System.out.println("Fin de añadir. Paso a buscar");
       outputNet2.search();
     System.out.println("Fin de aprender. Paso a estimar parametros");
//      outputNet2.refine4(outputNet2.metscores);
      DELearning outputNet3 = new DELearning(cases,outputNet2.getOutput());
      outputNet3.learning(2.0) ;
      f2 = new FileWriter(args[2]);
       System.out.println("Fin de aprender parametros ");
      baprend = (Bnet)outputNet3.getOutput();
      baprend.saveBnet(f2);
      f2.close();
      
      
 
       System.out.println("tiempo consumido"+outputNet2.delay);
      if(args.length > 4){
	  FileInputStream fnet = new FileInputStream(args[4]);
	  net = new Bnet(fnet);
          
          
        
          double d2 = net.KL(baprend);
          System.out.println("Divergencia KL de la red aprendida - real " + d2);
	  LinkList addel[] = new LinkList[3];
	  addel = outputNet2.compareOutput(net);
	  System.out.print("\nNumero de arcos a�adidos: "+addel[0].size());
	  System.out.print(addel[0].toString());
	  System.out.print("\nNumero de arcos borrados: "+addel[1].size());
	  System.out.print(addel[1].toString());
	  System.out.println("\nNumero de arcos mal orientados: "+addel[2].size());
	  System.out.print(addel[2].toString());
	
      }  

      
  
  }      
    
  

public GreedyLearning(){
	
    
    setOutput(null);
}
  
    
   
   public GreedyLearning(DataBaseCases cases) {

    Bnet b;
    Graph dag;
    NodeList nodes;
    
    int i;

  //  System.out.println("Paso por Crear base de datos");
        
    this.input = cases;
    nodes = cases.getVariables();
    //links = new LinkList();
    b = new Bnet();
    b.setKindOfGraph(2);
    dag = (Graph) b;
    for(i=0 ; i < (nodes.size()) ;i++)
       try {
	       dag.addNode(nodes.elementAt(i));
	    }
	    catch (InvalidEditException iee) {};

   
    
   
      metscores = new BDeMetrics(cases,2.0);
    setOutput(b);
   
    
} 


   
     public GreedyLearning(DataBaseCases cases, Bnet b) {
              
    this.input = cases;
            
            metscores = new BDeMetrics(cases,2.0);
            setOutput(b);
   
     }
   
  
 
public void learning() {
    
 double[][] depmatrix;  
 boolean[][] possible;
 Graph dag;
 double depdegree,maxdep,fit,newfit;
 int i,j,nnodes,maxi,maxj;

 
 FiniteStates maxX,maxY;


 Date D = new Date();
 delay = (double) D.getTime();

 FiniteStates nodeX, nodeY;
 NodeList parents, cond;
  
 
nnodes =  getOutput().getNodeList().size();

 
 
   
     dag = (Graph)getOutput();
     depmatrix = new double[nnodes][nnodes];
     possible = new boolean[nnodes][nnodes];
  maxX=new FiniteStates();
                   maxY=new FiniteStates();
                   maxi= 0;
                   maxj=0; 
maxdep=-1.0;

 
 
 for(i=0 ; i< nnodes;i++){
  possible[i][i] = false;
 nodeX=(FiniteStates)(dag.getNodeList()).elementAt(i);
   cond = new NodeList(); 
      cond.insertNode(nodeX);
      fit = metscores.scoret(cond);
      possible[i][i] = false;
  for (j=i+1 ; j< nnodes ;j++){
      nodeY=(FiniteStates)(dag.getNodeList()).elementAt(j);
      cond.insertNode(nodeY);
         newfit = metscores.scoret(cond);
         
    depdegree = newfit -fit;
          cond.removeNode(1);
      
    
  //    depdegree      =input.getDep((Node) nodeX, (Node) nodeY,subSet);
//         System.out.println("New - Old " + ndepdegree + " " + depdegree);
 //       System.out.print("\n I( "+nodeX.getName()+" , "+nodeY.getName()+" |   ) : "+depdegree+"\n");
       if (depdegree > maxdep) 
                 { maxdep = depdegree;
                   maxX=nodeX;
                   maxY=nodeY;
                   maxi= i;
                   maxj=j; }
      
       
        depmatrix[j][i] = depdegree;
        depmatrix[i][j] = depdegree;
        possible[i][j] = true;
        possible[j][i] = true;



        
      
  }
 }
 
 
 
 while(maxdep>0.0){
     
       try {
	       dag.createLink(maxX, maxY, true);
                
	    }
	    catch (InvalidEditException iee) {};
            

Node   oldmaxX = maxX;
   
Node   oldmaxY = maxY;            
       
   possible[maxi][maxj] = false;
   possible[maxj][maxi] = false;

   int oldmaxj = maxj;
   
     
     maxdep=-1.0;
     for (i=0;i<nnodes;i++){
         for (j=0;j<nnodes;j++){
             if (possible[i][j]){
              nodeX=(FiniteStates)(dag.getNodeList()).elementAt(i);
              nodeY=(FiniteStates)(dag.getNodeList()).elementAt(j);
                if ((dag.isThereDirectedPath(oldmaxY,nodeX)) && (dag.isThereDirectedPath(nodeY,oldmaxX))){
                    possible[i][j] = false;
                    
                }
             
                else{ 

            if (j==oldmaxj){
                cond = new NodeList();
                cond.insertNode(nodeY);
                parents = dag.parents(nodeY); 
                cond.join(parents);
                fit = metscores.scoret(cond);
                cond.insertNode(nodeX);
                newfit = metscores.scoret(cond);
                depmatrix[i][j] = newfit -fit;


            }

             if (depmatrix[i][j]>maxdep) {
                 maxdep = depmatrix[i][j];
                 maxX=(FiniteStates)(dag.getNodeList()).elementAt(i);
                   maxY=(FiniteStates)(dag.getNodeList()).elementAt(j);
                   maxi= i;
                   maxj=j;
             }
                }
                }
         }
         
     }
     
 }  
     
 
 
 

 
 D = new Date();
 delay = (((double)D.getTime()) - delay) / 1000;
 
    
    
}

 public void search(){

    Graph dag;
    boolean add;
    NodeList nodes,vars;
    double fitness,newfitness1,newfitness2,aux;
    Node NodeX,NodeY;
    int i,j;
    boolean improv;
    
    
    dag= (Graph) getOutput();
    nodes= dag.getNodeList();
    improv = true;

    while(improv) {
    improv= false;
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      for(j=i+1; j< nodes.size(); j++){
          
            NodeY = (FiniteStates) nodes.elementAt(j);
            if (dag.getLink(NodeX,NodeY) != null ) {
                vars = new NodeList();
                vars.insertNode(NodeY);
                vars.join(dag.parents(NodeY));
                fitness = metscores.scoret(vars);
                vars.removeNode(NodeX);
                newfitness1 = metscores.scoret(vars);
                newfitness2 = newfitness1;
                vars = new NodeList();
                vars.insertNode(NodeX);
                vars.join(dag.parents(NodeX));
                aux =  metscores.scoret(vars);
                fitness += aux;
                newfitness1 += aux;
               
                    vars.insertNode(NodeY);
                    newfitness2 += metscores.scoret(vars);
                
                if ((newfitness1>fitness)||(newfitness2>fitness)){
                  
                      try {
                         dag.removeLink(NodeX,NodeY);
                           }
                       catch (InvalidEditException iee) {};
                    if (newfitness2>newfitness1) {
                         
                       if (!dag.isThereDirectedPath(NodeX, NodeY)){ 
                            improv = true;
                        try {
                         dag.createLink(NodeY,NodeX,true);
                           }
                       catch (InvalidEditException iee) {};
                       }
                       else {if (fitness>=newfitness1){
                           try {
                         dag.createLink(NodeX,NodeY,true);
                           }
                       catch (InvalidEditException iee) {}; 
                       }
                       else {improv= true;}}
                       
                       
                    }

                }

            }
            else {
                if  (dag.getLink(NodeY,NodeX) != null ) {
                    vars = new NodeList();
                vars.insertNode(NodeX);
                vars.join(dag.parents(NodeX));
                fitness = metscores.scoret(vars);
                vars.removeNode(NodeY);
                newfitness1 = metscores.scoret(vars);
                newfitness2 = newfitness1;
                vars = new NodeList();
                vars.insertNode(NodeY);
                vars.join(dag.parents(NodeY));
                aux =  metscores.scoret(vars);
                fitness += aux;
                newfitness1 += aux;
               
                    vars.insertNode(NodeX);
                    newfitness2 += metscores.scoret(vars);
                
                if ((newfitness1>fitness)||(newfitness2>fitness)){
                  
                     
                     try {
                         dag.removeLink(NodeY,NodeX);
                           }
                       catch (InvalidEditException iee) {};
                    
                       if (newfitness2>newfitness1) {
                              if (!dag.isThereDirectedPath(NodeY, NodeX)){ 
                                   improv = true;
                         try {
                         dag.createLink(NodeX,NodeY,true);
                           }
                       catch (InvalidEditException iee) {};
                              }
                    }
                         else {if (fitness>=newfitness1){
                           try {
                         dag.createLink(NodeY,NodeX,true);
                           }
                       catch (InvalidEditException iee) {}; 
                       }
                       else {improv= true;}}
                       

                }

                }
                else {
                     vars = new NodeList();
                    vars.insertNode(NodeX);
                    vars.join(dag.parents(NodeX));
                   fitness = metscores.scoret(vars);

                newfitness1 = fitness;
                 if (dag.isThereDirectedPath(NodeX, NodeY)){
                    newfitness2 = fitness - 1.0;
                }
                 else {
                vars.insertNode(NodeY);
                newfitness2 = metscores.scoret(vars);
                 }
                vars = new NodeList();
                vars.insertNode(NodeY);
                vars.join(dag.parents(NodeY));
                aux =  metscores.scoret(vars);
                fitness += aux;
                newfitness2 += aux;
                if (dag.isThereDirectedPath(NodeY, NodeX)){
                    newfitness1 = fitness - 1.0;
                }
                else{
                    vars.insertNode(NodeX);
                    newfitness1 += metscores.scoret(vars);
                }



                if ((newfitness1>fitness)||(newfitness2>fitness)){
                   improv = true;

                    if (newfitness2>newfitness1) {
                         try {
                         dag.createLink(NodeY,NodeX,true);
                           }
                       catch (InvalidEditException iee) {};
                    }
                    else {
                         try {
                         dag.createLink(NodeX,NodeY,true);
                           }
                       catch (InvalidEditException iee) {};
                    }

                }

                }
            }

          }
      }

    }
 }
   
   
}

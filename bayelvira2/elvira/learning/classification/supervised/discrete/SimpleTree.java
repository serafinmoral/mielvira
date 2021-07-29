/*
 * SimpleTree.java
 *
 * Created on 12 de septiembre de 2007, 12:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package elvira.learning.classification.supervised.discrete;




/**
 *
 * @author smc
 */


import java.util.Vector;
import elvira.database.DataBaseCases;
import elvira.Bnet;
import elvira.CaseListMem;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.Graph;
import elvira.Node;
import elvira.NodeList;
import elvira.Relation;
import elvira.learning.classification.ConfusionMatrix;
import elvira.potential.*;
import elvira.learning.K2Metrics;
import elvira.learning.L1OMetrics; 
import elvira.learning.BDeMetrics; 
import java.io.*;




public class SimpleTree extends DiscreteClassifier {
    
   
    NodeList attributes;
 
    
    double prior;
    BDeMetrics metric;
    ProbabilityTree tree;
    boolean compfre = false;
    
    
    
    
    /** Creates a new instance of SimpleTree */
    public SimpleTree() {
         super();
        
    }
      
    
    
    public SimpleTree(DataBaseCases data, int classn) {

          this.cases         = data;
  	this.nVariables  = this.cases.getVariables().size();
   	this.nCases      = this.cases.getNumberOfCases();
    this.laplace       = true;
    this.evaluations   = 0;
    this.logLikelihood = 0;
   
     this.prior = 2.0;
       this.attributes = new NodeList();
      
   NodeList    nodelist    = this.cases.getVariables();

    Node        node;
   	Vector      vector      = this.cases.getRelationList();
       
   	Relation    relation    = (Relation)vector.elementAt(0);
        System.out.println(nCases);
   	CaseListMem caselistmem = (CaseListMem)relation.getValues();

 
      for (int j=0; j< nodelist.size(); j++) {
        node = (Node)(caselistmem.getVariables()).elementAt(j);
        if (node.getTypeOfVariable() == Node.CONTINUOUS) {
          System.err.println("ERROR: There is continuous values. First, use a Discretization method.");
          System.exit(0);
        }
          if (j != classn) {
        attributes.insertNode(node);     
      }
      
      }
      

    classVar = (FiniteStates)this.cases.getNodeList().elementAt(classn);
    
    this.classNumber         = classVar.getNumStates();
    
   //  metric = new BDeMetrics(data,classVar.getNumStates());
          metric = new BDeMetrics(data);
       
    this.confusionMatrix     = new ConfusionMatrix(this.classNumber);   

       
    }     
    
    
    
    public SimpleTree(DataBaseCases data) {

          this.cases         = data;
  	this.nVariables  = this.cases.getVariables().size();
   	this.nCases      = this.cases.getNumberOfCases();
    this.laplace       = true;
    this.evaluations   = 0;
    this.logLikelihood = 0;
      this.compfre = false;
     this.prior = 2.0;
       this.attributes = new NodeList();
      
   NodeList    nodelist    = this.cases.getVariables();

    Node        node;
   	Vector      vector      = this.cases.getRelationList();
       
   	Relation    relation    = (Relation)vector.elementAt(0);
        System.out.println(nCases);
   	CaseListMem caselistmem = (CaseListMem)relation.getValues();

 
      for (int j=0; j< nodelist.size()-1; j++) {
        node = (Node)(nodelist).elementAt(j);
        if (node.getTypeOfVariable() == Node.CONTINUOUS) {
          System.err.println("ERROR: There is continuous values. First, use a Discretization method.");
          System.exit(0);
        }
          
        attributes.insertNode(node);     
      
      
      }
      

    classVar = (FiniteStates) nodelist.elementAt(nodelist.size()-1);
    
    this.classNumber         = classVar.getNumStates();
    
   //  metric = new BDeMetrics(data,classVar.getNumStates());
          metric = new BDeMetrics(data);
       
    this.confusionMatrix     = new ConfusionMatrix(this.classNumber);   

       
    }     
    
      public SimpleTree(DataBaseCases data, FiniteStates C, NodeList att) {
               this.cases         = data;
  	this.nVariables  = this.cases.getVariables().size();
   	this.nCases      = this.cases.getNumberOfCases();
    this.laplace       = true;
    this.evaluations   = 0;
    this.logLikelihood = 0;
    this.classVar = C;
    this.attributes = att;
    this.compfre = true;
       metric = new BDeMetrics(data);
      }
      
     public SimpleTree(DataBaseCases data, FiniteStates C, NodeList att, boolean fre) {
               this.cases         = data;
  	this.nVariables  = this.cases.getVariables().size();
   	this.nCases      = this.cases.getNumberOfCases();
    this.laplace       = true;
    this.evaluations   = 0;
    this.logLikelihood = 0;
    this.classVar = C;
    this.attributes = att;
    this.compfre = fre;
       metric = new BDeMetrics(data);
      }
        
    
  public void structuralLearning() throws elvira.InvalidEditException{
      int sizel,i,nc;
     
      ProbabilityTree localtree,tree2;
     
      double nleaves,csize;
      Configuration c;
      double wtotal[],weight,prob;
      
     
    
      
     nc = classVar.getNumStates();
    
     c = new Configuration();
     
      weight = 1.0;
      
      wtotal = new double[nc];
      
      for (i=0;i<nc;i++) {wtotal[i] = 0.0;}
      
     csize = 1.0;
    
     
//     tree = learnr(attributes,c,wtotal,weight,csize);
     
//    tree = learnr(attributes,c,wtotal);
     tree = learnr(attributes,c);
        weight=1.0;
  
   
   
   
   
   // tree.print(1);

 //       c = new Configuration();
  // pruneMixt(tree,c);    
       
   //     tree.print(1);  
 //        estimateMixt(tree,wtotal,weight,c );  
       
      }
      
    
    
 
 
  
  
  private ProbabilityTree learnr(NodeList lnodes, Configuration c,double awtotal[] ) {
  
   ProbabilityTree tree;
   tree = new ProbabilityTree();
   double freq[][], frec[];
   int i,j,nvar,max,nc,nx,ngr;
   double depdegree,sx,sc,tme,ele,ftotal;
   double sentaw[];
   PotentialTable f2;
   FiniteStates maxVar,X;
   
   nvar = lnodes.size();
   
     nc = classVar.getNumStates();
    
   frec = cases.getFreq(classVar,c);
   ngr = 0; 
   
  
   
   ftotal = 0.0;
   for(i=0;i<nc;i++){
        if(frec[i] >0){ 
        ngr++;
        ftotal += frec[i];
        
        }
   }
   
     tme = nc;
    ele = 1.0;
   
  //    System.out.println("Prior = " + prior);
  
   tree = new ProbabilityTree();
  
   
   if ((ngr<2)||(nvar < 1)){
       
    
     
     
     
     tree.assignVar(classVar);
  //     System.out.println("No Elijo Todos uniformes");
   if (ngr==0) {
       ftotal = 0.0;
         for (i=0; i<nc;i++){ ftotal += awtotal[i];}
         
         for (i=0; i<nc;i++){
         
        tree.getChild(i).assignProb((awtotal[i]+ele)/(ftotal+tme));   
       
     }    
         
   }
   else{ 
     for (i=0; i<nc;i++){
         
        tree.getChild(i).assignProb((frec[i]+ele)/(ftotal+tme));   
       
     }
   }
      
   return(tree); 
       
   }
   
   
   
    max = -1;
   depdegree = 0.0;
   
   
   for(i=0; i<nvar;i++){
        X =  (FiniteStates) lnodes.elementAt(i);
    
       sx =  metric.score(classVar,X,c);
    //     System.out.println(sx);
   //     System.out.println("Variable " + ((FiniteStates) lnodes.elementAt(i)).getName()+ "Score " + sx);
       if (sx> depdegree) 
      {
          max = i;
          depdegree = sx;
      }
       
   }
     
 if(max==-1){
     tree.assignVar(classVar);
  //     System.out.println("No Elijo ");
     for (i=0; i<nc;i++){
          
       tree.getChild(i).assignProb((frec[i]+ele)/(ftotal+tme));   
       
     }
 }
 else{
       maxVar = (FiniteStates) lnodes.elementAt(max);
  //       System.out.println("Elijo " + maxVar.getName());
       nx = maxVar.getNumStates();
       tree.assignVar(maxVar);
         lnodes.removeNode(max);
        sentaw = new double[nc];
             for(j=0;j<nc;j++) {sentaw[j] = frec[j];}
         
          for (i=0; i<nx;i++){
             
              
           
            
             c.insert(maxVar,i);   
             tree.replaceChild(learnr(lnodes,c,sentaw),i);
             c.remove(maxVar);
            
          }
         lnodes.insertNode(maxVar);
 }
     
   
   
   
      
   return(tree);
      
  }
        
 

  
  
  public ProbabilityTree learnr(NodeList lnodes, Configuration c ) {
  
   ProbabilityTree tree;
   tree = new ProbabilityTree();
   double freq[][], frec[], frex[];
   long inleaves;
   int i,j,nvar,max,nc,nx,ngr;
   double depdegree,sx,sc,tme,ele,ftotal,weight;
   double awtotal[];
   PotentialTable f2;
   FiniteStates maxVar,X;
   
   nvar = lnodes.size();
   
     nc = classVar.getNumStates();
    
   frec = cases.getFreq(classVar,c);
   ngr = 0; 
   
  tme = nc;
  ele = 1;
   
   ftotal = 0.0;
   for(i=0;i<nc;i++){
        if(frec[i] >0){ 
        ngr++;
        ftotal += frec[i];
        
        }
   }
   
   

   tree = new ProbabilityTree();
     inleaves = (long) ftotal;
   //     tree.setLeaves (inleaves);
   
   if ((ngr<2)||(nvar < 1)){
       
    
     
     
     
     tree.assignVar(classVar);
 //      System.out.println("No Elijo Todos uniformes");
  
    //      if (nvar<1) {   System.out.println("***********************" +
     //             " No quedan variables   ");}
  
     for (i=0; i<nc;i++){
         if (compfre)
         {tree.getChild(i).assignProb((frec[i]+ele));  }
       
         else { tree.getChild(i).assignProb((frec[i]+ele)/(ftotal+tme));   }  
     }
 
      
   return(tree); 
       
   }
   
   
   
    max = -1;
   depdegree = -10.000;
   
   double scoreX = metric.score(classVar,c);
   depdegree = scoreX;
   
   for(i=0; i<nvar;i++){
        X =  (FiniteStates) lnodes.elementAt(i);
          
       sx = metric.score(classVar,X,c);
     //   sx = entropyGain(classVar,X,c);
    //     System.out.println(sx);
   //     System.out.println("Variable " + ((FiniteStates) lnodes.elementAt(i)).getName()+ "Score " + sx);
       if (sx> depdegree) 
      {
          max = i;
          depdegree = sx;
      }
       
   }
     
 if(depdegree <= scoreX){
     tree.assignVar(classVar);
   //    System.out.println("No Elijo ");
     for (i=0; i<nc;i++){
      if (compfre)
         {tree.getChild(i).assignProb((frec[i]+ele));  }
       
         else { tree.getChild(i).assignProb((frec[i]+ele)/(ftotal+tme));   }  
       
     }
 }
 else{
       maxVar = (FiniteStates) lnodes.elementAt(max);
  //       System.out.println("Elijo " + maxVar.getName());
       nx = maxVar.getNumStates();
       tree.assignVar(maxVar);
         lnodes.removeNode(max);
     
         
          for (i=0; i<nx;i++){
             
              
           
            
             c.insert(maxVar,i);   
             tree.replaceChild(learnr(lnodes,c),i);
             c.remove(maxVar);
            
          }
         lnodes.insertNode(maxVar);
 }
     

   
      
   return(tree);
      
  }
        
  
public void prune (ProbabilityTree localtree, Configuration c ) {
    int nc,i,nx;
    FiniteStates X,Y;
    double sx, frec[],ftotal,ele,prior;
    boolean all;
      
      X  = localtree.getVar();
       if(X == classVar) {
          return;
       }
      
      nx = X.getNumStates();
      
      for(i=0;i<nx;i++){
              
             c.insert(X,i);   
             prune(localtree.getChild(i),  c);
             c.remove(X);
            
      }
      
      
        all = true;
       for(i=0;i<nx;i++){
      
         Y = localtree.getChild(i).getVar();
         if (Y!=classVar) {all = false;}
         
       }
         if(all) { 
              sx =  metric.scoref(classVar,X,c);
              System.out.println("Variable " + X.getName() + "score " + sx);
        //      sx =  metric.score(classVar,X,c)-metric.score(classVar,c) ;
              if (sx>0) {return;}
               System.out.println("Pruning " + X.getName());
              localtree.assignVar(classVar);
              
               nc = classVar.getNumStates();
               prior = nc;
               ele = 1.0;
                 frec = cases.getFreq(classVar,c);
                 ftotal = 0.0;
                 for (i=0; i<nc;i++){ftotal += frec[i];}
                 
                 
               for (i=0; i<nc;i++){
         
                     localtree.getChild(i).assignProb((frec[i]+ele)/(ftotal+prior));  } 
       
     }
 
    
         
      
      return;
}


public void pruneMixt (ProbabilityTree localtree, Configuration c ) {
    int nc,i,j,nx;
    FiniteStates X,Y;
    boolean all;
    double sx, frec[],ftotal,ele,prior,w1,w2,prob;
      
      X  = localtree.getVar();
       if(X == classVar) {
          return;
       }
      
      nx = X.getNumStates();
      
      System.out.println(X.getName());
      
      for(i=0;i<nx;i++){
              
             c.insert(X,i);   
             pruneMixt(localtree.getChild(i),  c);
             c.remove(X);
            
      }
        all = true;
       for(i=0;i<nx;i++){
      
         Y = localtree.getChild(i).getVar();
         if (Y!=classVar) {all = false;}
         
       }
         
            w1 =  metric.score(classVar,X,c);
              w2 = metric.score(classVar,c);
              sx =  w1-w2;
              w1 = Math.exp(w1);
              w2 =  Math.exp(w2);
                 nc = classVar.getNumStates();
               prior = nc;
               ele = 1.0;
                 frec = cases.getFreq(classVar,c);
                 ftotal = 0.0;
                 for (i=0; i<nc;i++){ftotal += frec[i];}
              
         if(all) {
           
              
            
                 
              if (sx>=0) {
               
               for(j=0;j<nx;j++){
                 for (i=0;i<nc;i++) {
                       prob = ((w2)* (frec[i]+ele)/(ftotal+prior) + (w1)*localtree.getChild(j).getChild(i).getProb())/((w1+w2)); 
                       //  prob = (w2* (frec[i]+ele)/(ftotal+prior) + w1*localtree.getChild(j).getChild(i).getProb())/(w1+w2); 
                      //      System.out.println(" Pesos " + w1 + "  " + w2);
                         // System.out.println("antes " + localtree.getChild(j).getChild(i).getProb());
                        localtree.getChild(j).getChild(i).assignProb(prob);  
                  //   System.out.println("despues " + localtree.getChild(j).getChild(i).getProb());
                 } 
                   
               }
                  
                  
                  return;}
             System.out.println("Pruning " + X.getName());
              localtree.assignVar(classVar);
              
               nc = classVar.getNumStates();
               prior = nc;
               ele = 1.0;
                 frec = cases.getFreq(classVar,c);
                 ftotal = 0.0;
                 for (i=0; i<nc;i++){ftotal += frec[i];}
                 
                 
               for (i=0; i<nc;i++){
         
                     localtree.getChild(i).assignProb((frec[i]+ele)/(ftotal+prior));  } 
       
     }
        
         else {
            
              for(j=0;j<nx;j++){
      
         Y = localtree.getChild(j).getVar();
         if (Y==classVar) {
           for (i=0;i<nc;i++) {
                         
                       
                         prob = (w2* (frec[i]+ele)/(ftotal+prior) + w1*localtree.getChild(j).getChild(i).getProb())/(w1+w2); 
                  //        System.out.println(" Pesos " + w1 + "  " + w2);
                    //    System.out.println("antes " + localtree.getChild(j).getChild(i).getProb());
                        localtree.getChild(j).getChild(i).assignProb(prob);  
                    //   System.out.println("despues " + localtree.getChild(j).getChild(i).getProb());
                 } 
         
         
         }
         
       }
          
            
            
            
         }
 
    
         
      
      return;
}
   
public void estimateMixt (ProbabilityTree tree, double awtotal[], double aweight, Configuration c ) {
    double freq[][], frec[], ftotal,tme,ele, sentaw[],sx,sc;
    int i,j,nc,nx;
    long inleaves;
    FiniteStates X;
    
     frec = cases.getFreq(classVar,c);
       
        ftotal = 0.0;
        nc = classVar.getNumStates();
        
   for(i=0;i<nc;i++){
       
        ftotal += frec[i];
        
       
   }
       inleaves = (long) ftotal;
     //   tree.setLeaves(inleaves);
        tme = nc;
        ele = 1.0;
   
     
   for(i=0;i<nc;i++){
     
       awtotal[i] = (((frec[i]+ele)/(ftotal+tme))*aweight + awtotal[i]);
  //       System.out.println("Weight " + aweight);
  //     System.out.println("i = " + i + "freq " + frec[i] + "fretotal " +   awtotal[i])   ;
   }
        
       
      X = tree.getVar();
  //    System.out.println(X.getName());
      if(X == classVar) {
          ftotal = 0.0;
           for(i=0;i<nc;i++){ftotal += awtotal[i];}   
             for (i=0; i<nc;i++){
         
        tree.getChild(i).assignProb((awtotal[i])/(ftotal));   
       
     }   
       }
       else{
           nx = X.getNumStates();
            freq = cases.getFreq(classVar,X,c); 
  
            
            
             sx =  metric.score(freq,nc,nx,tme);
             sc =  metric.score(frec,nc,tme);
      
          
             
             
             if (sx-sc<50.0){
           // aweight *= 2.0;
             aweight *= 2*Math.exp(sx-sc);
             
             for(j=0; j<nx; j++){
                 sentaw = new double[nc];
                 for(i=0;i<nc;i++) {sentaw[i] = awtotal[i];}
                  c.insert(X,j);   
                  estimateMixt(tree.getChild(j), sentaw,aweight, c);
             c.remove(X);
                 
                 
             }
            
             }
             else{
                 
                  aweight = 1.0;
             
             for(j=0; j<nx; j++){
                 sentaw = new double[nc];
                 for(i=0;i<nc;i++) {sentaw[i] = 0.0;}
                  c.insert(X,j);   
                  estimateMixt(tree.getChild(j), sentaw,aweight, c);
             c.remove(X);
                 
                 
             }
               
                 
             }
       }
     
  
}
public double entropyGain(FiniteStates classVar, FiniteStates X, Configuration c){
    double result, freq[][], frec[],total, partial, count;
    int i,j,nc,nx;
    
    result = 0.0;
      nc = classVar.getNumStates();
        nx = X.getNumStates();
     freq = cases.getFreq(classVar,X,c);
      frec = new double[nc];
    
      for (i=0;i<nc;i++) {
        frec[i] = 0.0;
            for(j=0;j<nx;j++){
            frec[i] += freq[i][j];
            }
      
        
    }
      
   //       for (i=0;i<nc;i++) {
   //     frec[i]++; }
      
  //  for(i=0;i<nc;i++) {
        
    //    for(j=0;j<nx;j++){
      //      freq[i][j]++;
   //     }
  //  }
    
   
    total = 0.0;
    
   
     total = 0.0;
     for (i=0;i<nc;i++) {
      if(frec[i]>0) {  result -= frec[i] * Math.log(frec[i]) ;
        total += frec[i];}
         }
      result /= total;
      result += Math.log(total);
      
        for(j=0;j<nx;j++){
            partial = 0.0;
            count = 0.0;
            for (i=0;i<nc;i++) {
               if(freq[i][j]>0) { partial -= freq[i][j] * Math.log(freq[i][j]) ;
              count +=  freq[i][j] ;}
         }
      partial /= total;
    if(count>0.0) {   partial += Math.log(count)*count/total;}
      result -= partial;    
          
        }
      
    
    
    return(result);
    
    
    
}
    
 public double entropyGainImpr(FiniteStates classVar, FiniteStates X, Configuration c){
    double result, freq[][], frec[],total, partial, count;
    int i,j,nc,nx;
    
    result = 0.0;
      nc = classVar.getNumStates();
        nx = X.getNumStates();
     freq = cases.getFreq(classVar,X,c);
      frec = new double[nc];
    
      for (i=0;i<nc;i++) {
        frec[i] = 0.0;
            for(j=0;j<nx;j++){
            frec[i] += freq[i][j];
            }
      
        
    }
      
   //       for (i=0;i<nc;i++) {
   //     frec[i]++; }
      
  //  for(i=0;i<nc;i++) {
        
    //    for(j=0;j<nx;j++){
      //      freq[i][j]++;
   //     }
  //  }
    
   
    total = 0.0;
    
   
     total = 0.0;
     for (i=0;i<nc;i++) {
     {  result -= (frec[i]+1.0) * Math.log(frec[i]+1.0) ;
        total += frec[i];}
         }
      result /= (total+nc);
      result += Math.log(total+nc);
      
        for(j=0;j<nx;j++){
            partial = 0.0;
            count = 0.0;
            for (i=0;i<nc;i++) {
              { partial -= (freq[i][j]+1.0) * Math.log(freq[i][j]+1.0) ;
              count +=  freq[i][j] ;}
         }
      partial /= (total+nc);
     {   partial += Math.log(count+nc)*(count+1.0)/(total+nc);}
      result -= partial;    
          
        }
      
    
    
    return(result);
    
    
    
}
    
  
  
  private ProbabilityTree learnr(NodeList lnodes, Configuration c, double awtotal[], double aweight, double consize ) {
  
   ProbabilityTree tree;
   tree = new ProbabilityTree();
   double freq[][], frec[];
   int i,j,nvar,max,nc,nx,ngr;
   double depdegree,sx,sc,tme,ele,ftotal;
   double sentaw[];
   PotentialTable f2;
   FiniteStates maxVar,X;
   
   nvar = lnodes.size();
   
     nc = classVar.getNumStates();
    
//   tme = prior/consize;
  
     
     tme = prior;
   ele = tme/nc;
   
   frec = cases.getFreq(classVar,c);
   ngr = 0; 
   
  //   System.out.println("Peso : " + aweight);   
   
   
   ftotal = 0.0;
   for(i=0;i<nc;i++){
        if(frec[i] >0){ 
        ngr++;
        ftotal += frec[i];
        
        }
   }
   
   
     
   for(i=0;i<nc;i++){
     
       awtotal[i] = (((frec[i]+ele)/(ftotal+tme))*aweight + awtotal[i]);
       
       
   }
   
 
   tree = new ProbabilityTree();
  
   
   if ((ngr<2)||(nvar < 1)){
       
    
     ftotal = 0.0;
     
     
     for(i=0;i<nc;i++){ftotal += awtotal[i];}
     
     tree.assignVar(classVar);
  //     System.out.println("No Elijo ");
     
     for (i=0; i<nc;i++){
         
        tree.getChild(i).assignProb((awtotal[i]+ele)/(ftotal+prior));   
       
     }
      
      
   return(tree); 
       
   }
   
  
   sc =  metric.score(frec,nc,tme); 
   
    X =  (FiniteStates) lnodes.elementAt(0);
    nx = X.getNumStates();
       freq = cases.getFreq(classVar,X,c); 
  
       sx =  metric.score(freq,nc,nx,tme);
   
    max = 0;
   depdegree = sx;
   
   
   for(i=1; i<nvar;i++){
        X =  (FiniteStates) lnodes.elementAt(i);
       freq = cases.getFreq(classVar,X,c); 
         nx = X.getNumStates();
       sx =  metric.score(freq,nc,nx,tme);
    //     System.out.println(sx);
   //   System.out.println("Variable " + ((FiniteStates) lnodes.elementAt(i)).getName()+ "Score " + x);
       if (sx> depdegree) 
      {
          max = i;
          depdegree = sx;
      }
       
   }
     
 
       maxVar = (FiniteStates) lnodes.elementAt(max);
    //     System.out.println("Elijo " + maxVar.getName());
       nx = maxVar.getNumStates();
       tree.assignVar(maxVar);
         lnodes.removeNode(max);
        
         
          for (i=0; i<nx;i++){
              sentaw = new double[nc];
              
              
              if ((depdegree-sc)  <= 4.0){
              for(j=0;j<nc;j++) {sentaw[j] = awtotal[j];}
             c.insert(maxVar,i);   
             tree.replaceChild(learnr(lnodes,c,sentaw,5*aweight*Math.exp(depdegree-sc),consize*nx),i);
             c.remove(maxVar);
              }
              else {
              for(j=0;j<nc;j++) {sentaw[j] = awtotal[j]/(aweight*10.0E5);}
             c.insert(maxVar,i);   
             tree.replaceChild(learnr(lnodes,c,sentaw,1.0,consize*nx),i);
             c.remove(maxVar);
              }
          }
         lnodes.insertNode(maxVar);
    
     
   
   
   
      
   return(tree);
      
  }
   
    
     public double[] computePosterior(Configuration instance) {
         ProbabilityTree t1,t2;
         
         int i,nc,nv,j,k,l;
         double[] result;
         NodeList nl;
         FiniteStates nob;
         double sum;
         
         nv = classVar.getNumStates();
         result = new double[nv];
         
     
           t2 = tree.restrict(instance);
           nl = t2.getVarList();
           k = nl.size();
          
           for(j=0; j<k; j++){
               nob = (FiniteStates) nl.elementAt(j);
               
               if (!(nob == classVar) ){
                  t2 = t2.addVariable(nob);  
               }    
           }
           for(l=0; l<nv; l++){
               result[l] = t2.getChild(l).getProb();
              
           } 
           
             
         
         
         
  
         
         
         return result;
    }  

     
     public int assignClass(Configuration conf) {
         double[] result;
         int i,max,nv;
         double x;
         
         result = computePosterior(conf);
         
         max=-1;
         x = -1.0;
          nv = classVar.getNumStates();
         for (i=0;i<nv;i++){
              if (result[i] > x) {
                  x = result[i];
                  max = i;
              }
         }
          
     //          System.out.println("Assign " +  max + "probability " + x);
        return max; 
         
     
         
     }       
   public double test(DataBaseCases test) {
    DataBaseCases newtest = test;
    Configuration c;
    int trueclass;
/*    if(!test.getVariables().equals(this.cases.getVariables()))
      newtest = this.projection(this.cases, test.getVariables());
*/

   
;

    //Check: the variables must have the same number of states
  	NodeList    nodelistTrain    = this.cases.getVariables();
   	Vector      vectorTrain      = this.cases.getRelationList();
   	Relation    relationTrain    = (Relation)vectorTrain.elementAt(0);
   	CaseListMem caselistmemTrain = (CaseListMem)relationTrain.getValues();

   	NodeList    nodelistTest    = newtest.getVariables();
   	Vector      vectorTest      = newtest.getRelationList();
   	Relation    relationTest    = (Relation)vectorTest.elementAt(0);
   	CaseListMem caselistmemTest = (CaseListMem)relationTest.getValues();

    int nStatesTrain, nStatesTest;
    FiniteStates varStatesTrain = new FiniteStates();
    FiniteStates varStatesTest = new FiniteStates();
   

    int nTest = newtest.getNumberOfCases();

    Vector      vector      = newtest.getRelationList();
  	Relation    relation    = (Relation)vector.elementAt(0);
    CaseListMem caselistmem = (CaseListMem)relation.getValues();
    caselistmem.setVariables (nodelistTrain.toVector());

    double accuracy = (double)0;
caselistmem.initializeIterator();
    while(caselistmem.hasNext()) {
      
    		c = caselistmem.getNext();
             
               trueclass = c.getValue(classVar);
               
     //          System.out.println("true Class " + trueclass );
                c.remove(classVar);
      int assignedClass = this.assignClass(c);
      if (assignedClass == trueclass)
                accuracy = accuracy + 1;
                this.confusionMatrix.actualize(trueclass);
      this.confusionMatrix.actualize(trueclass, assignedClass);
    }
    accuracy = (accuracy / (double) nTest) * (double) 100;

  	return(accuracy);
  }
     
         
 /**
   * Main to use the class from the command line
   */
  public static void main(String[] args) throws FileNotFoundException, IOException, elvira.InvalidEditException, elvira.parser.ParseException, Exception{
    int nvar;
      
    if(args.length != 4) {
      System.out.println("Usage: file-train.dbc classnumber file-test.dbc file-out.elv");
      System.exit(0);
    }
  nvar =(Integer.valueOf(args[1]).intValue());
    FileInputStream fi = new FileInputStream(args[0]);
    DataBaseCases   db = new DataBaseCases(fi);
    fi.close();

    SimpleTree clasificador = new SimpleTree(db, nvar);
    clasificador.structuralLearning();

    System.out.println("Classifier learned");

    FileInputStream ft = new FileInputStream(args[2]);
    DataBaseCases   dt = new DataBaseCases(ft);
    ft.close();

    double accuracy = clasificador.test(dt);

    System.out.println("Classifier tested. Accuracy: " + accuracy);

    clasificador.getConfusionMatrix().print();

  }       
}

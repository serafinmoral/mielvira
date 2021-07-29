/*
 * SimpleTreeNaive.java
 *
 * Created on 1 de octubre de 2007, 12:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package elvira.learning.classification.supervised.discrete;


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


/**
 *
 * @author smc
 */
public class SimpleTreeNaive extends SimpleTree {
    
    
    Vector naive;
    
    
    /** Creates a new instance of SimpleTreeNaive */
    public SimpleTreeNaive() {
    }
      public SimpleTreeNaive(DataBaseCases data, int classn) {

          this.cases         = data;
  	this.nVariables  = this.cases.getVariables().size();
   	this.nCases      = this.cases.getNumberOfCases();
    this.laplace       = false;
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
          metric = new BDeMetrics(data,2);
          
          naive = new Vector();
       
    this.confusionMatrix     = new ConfusionMatrix(this.classNumber);   

       
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
  
    learnNaive();
   
   
   
    tree.print(1);

   //     c = new Configuration();
//      prune(tree,c);    
       
   //     tree.print(1);  
         estimateMixt(tree,wtotal,weight,c );  
  //    tree.print(1); 
      }
      
    
       
  private void learnNaive(){
      int i,nv,nc,j,k,nx;
      FiniteStates X;
      double freq[][],ftotal;
      Configuration c;
      
      nv = attributes.size();
      nc = classVar.getNumStates();
      
      c = new Configuration();
      for (i=0;i<nv;i++){
              X =  (FiniteStates) attributes.elementAt(i);
              freq =  cases.getFreq(classVar,X, c);
              nx = X.getNumStates();
              
              for (j=0;j<nc;j++){ 
                  ftotal =0.0;
                  for(k=0;k<nx;k++){
                      ftotal += freq[j][k];
                  }
                   for(k=0;k<nx;k++){
                      freq[j][k] = (freq[j][k]+1.0)/(ftotal + nx);
                      System.out.println("Variable " + i + "j " + j + "k " + k + "freq " + freq[j][k]);
                  }
              }
              naive.add(freq);
          
      }
      
      
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
  ele = 1.0;
   
   ftotal = 0.0;
   for(i=0;i<nc;i++){
        if(frec[i] >0){ 
        ngr++;
        ftotal += frec[i];
        
        }
   }
   
   

   tree = new ProbabilityTree();
     inleaves = (long) ftotal;
        tree.setLeaves (inleaves);
   
   if ((ngr<2)||(nvar < 1)){
       
    
     
     
     
     tree.assignVar(classVar);
     System.out.println("No Elijo Todos uniformes");
  
        if (nvar<1) {   System.out.println("***********************" +
                " No quedan variables   ");}
  
     for (i=0; i<nc;i++){
         
        tree.getChild(i).assignProb((frec[i]+ele)/(ftotal+prior));   
       
     }
 
      
   return(tree); 
       
   }
   
   
   
    max = -1;
   depdegree = 0.0;
   
   
   for(i=0; i<nvar;i++){
        X =  (FiniteStates) lnodes.elementAt(i);
          
       
        sx = entropyGain(classVar,X,c);
       System.out.println(sx);
      System.out.println("Variable " + ((FiniteStates) lnodes.elementAt(i)).getName()+ "Score " + sx);
       if (sx> depdegree) 
      {
          max = i;
          depdegree = sx;
      }
       
   }
     
 if(max==-1){
     tree.assignVar(classVar);
     System.out.println("No Elijo ");
     for (i=0; i<nc;i++){
          
       tree.getChild(i).assignProb((frec[i]+ele)/(ftotal+prior));   
       
     }
 }
 else{
       maxVar = (FiniteStates) lnodes.elementAt(max);
       System.out.println("Elijo " + maxVar.getName());
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
  
     public double[] computePosterior(Configuration instance) {
         ProbabilityTree t1,t2;
         
         int i,nc,nv,j,k,l,nnu;
         double result[],freq[][],ftotal;
         NodeList nl,nonused;
         FiniteStates nob,X;
         double sum;
         
         nv = classVar.getNumStates();
         result = new double[nv];
         
           nonused = attributes.copy();
           
           tree.reduceList(nonused,instance);
           
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
           
              for(l=0; l<nv; l++){
               System.out.println("Antes " + result[l]);
              
           } 
           
             
         nnu = nonused.size();
         
        System.out.println("Non used variables " + nnu);
         
         for (i=0;i<nnu;i++){
             X = (FiniteStates) nonused.elementAt(i);
             System.out.println("non used variable " + X.getName());
             j =attributes.getId(X);
             freq = (double[][]) naive.elementAt(j);
                l = instance.getValue(X);
             if (l!=-1){   for (k=0;k<nv;k++){
                 result[k] *= freq[k][l];
             }
             }
             
         }

        
         
      ftotal = 0.0;
      
      for (i=0;i<nv;i++){
          ftotal += result[i];
      }
          for (i=0;i<nv;i++){
         result[i] /= ftotal;
      }
          for(l=0; l<nv; l++){
               System.out.println("Después " + result[l]);
              
           }    
         
         return result;
    }  
          
   
   
     
     
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

    SimpleTree clasificador = new SimpleTreeNaive(db, nvar);
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

/*
 * MultipleTree.java
 *
 * Created on 5 de octubre de 2007, 10:25
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
import elvira.learning.classification.supervised.discrete.SimpleTree;
import elvira.potential.*;
import elvira.learning.K2Metrics;
import elvira.learning.L1OMetrics; 
import elvira.learning.BDeMetrics; 
import java.io.*;
/**
 *
 * @author smc
 */
public class MultipleTree  extends DiscreteClassifier{
       
  
    Vector listtrees;
    NodeList attributes;
 
 
   
    K2Metrics metric;
    
    /** Creates a new instance of MultipleTree */
    public MultipleTree() {
    }
     public MultipleTree(DataBaseCases data, int classn) {

          this.cases         = data;
  	this.nVariables  = this.cases.getVariables().size();
   	this.nCases      = this.cases.getNumberOfCases();
    this.laplace       = true;
    this.evaluations   = 0;
    this.logLikelihood = 0;
  
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
          metric = new K2Metrics(data);
       
    this.confusionMatrix     = new ConfusionMatrix(this.classNumber);   
    this.listtrees = new Vector();
       
    }  
     
    /**
   * This method learns the structure and parameters 
   */
  public void structuralLearning() throws elvira.InvalidEditException{
      int sizel,i,j,nc;
      NodeList localnodes;
      ProbabilityTree localtree;
      PotentialTable freque;
      double nleaves,depx,depy;
      SimpleTree classtree;
      Configuration c;
      FiniteStates X,Y;
      
      
      NodeList aux;
      aux = new NodeList();
      nc = classVar.getNumStates();
      
       
      classtree = new SimpleTree(cases, classVar,attributes,false);
      c = new Configuration();
      
       localtree = classtree.learnr(aux,c);
       aux.insertNode(classVar);
       localtree.print(0);
      
         listtrees.add(localtree);
           c = new Configuration(); 
         for(i=0;i<attributes.size();i++){
              X = (FiniteStates) attributes.elementAt(i);
              
              depx = metric.score(classVar,X,c);
              for(j=i+1;j<attributes.size();j++){
                  Y = (FiniteStates) attributes.elementAt(j);
                  depy = metric.score(classVar,Y,c);
                  if (depy>depx) {
                      attributes.setElementAt(Y,i);
                      attributes.setElementAt(X,j);
                      depx = depy;
                      X= Y;
                  }
              }
         }
      
               
         
     for (i=0;i<attributes.size();i++){
              X = (FiniteStates) attributes.elementAt(i);
           
              classtree = new SimpleTree(cases, X,aux ,false);
               c = new Configuration();
              localtree = classtree.learnr(aux,c);  
              double    weight = 1.0;
       nc = X.getNumStates();
   double[]   wtotal = new double[nc];
      
      for (j=0;j<nc;j++) {wtotal[j] = 0.0;}
      //  localtree.print(0);
       //     classtree.pruneMixt(localtree,c );
                 classtree.estimateMixt(localtree,wtotal,weight,c );  
           //    localtree.print(0);
      
         listtrees.add(localtree);
              aux.insertNode(X);
     }
            
         
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
         
        for(i=0;i<nv;i++){result[i] = 1.0;}
         
         
         
         nc = listtrees.size();
         
         for(i=0; i<nc; i++) {
           t1 = (ProbabilityTree) listtrees.elementAt(i);  
           t2 = t1.restrict(instance);
           nl = t2.getVarList();
           k = nl.size();
          
           for(j=0; j<k; j++){
               nob = (FiniteStates) nl.elementAt(j);
               
               if (!(nob == classVar) ){
                  t2 = t2.addVariable(nob);  
               }    
           }
           
           if (t2.getVar() == classVar){
           for(l=0; l<nv; l++){
               result[l] *= t2.getChild(l).getProb();
              
           } }
           
             
         }
         
         
       sum=0.0;
       
           for(l=0; l<nv; l++){
               sum += result[l];
           } 
         
           for(l=0; l<nv; l++){
               result[l] /= sum;
             
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
        return max; 
         
         
     } 
  
   /**
   * This method tests the learned classifier given a DataBaseCases.
   * It returns the accuracy of the classifier.
   * It requires the the class variable with assigned values in the test database.
   * @param. DataBaseCases test. The test database of the classifier.
   * @returns double. The accuracy of the classifier on the <code> test <code\> dataset
   */
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
//                c.print();
               trueclass = c.getValue(classVar);
//                System.out.println("true Class " + trueclass );
                c.remove(classVar);
      int assignedClass = this.assignClass(c);
      if (assignedClass == trueclass)
    		accuracy = accuracy + 1;
      this.confusionMatrix.actualize(trueclass, assignedClass);
    }
    accuracy = (accuracy / (double) nTest) * (double) 100;

  	return(accuracy);
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

    MultipleTree clasificador = new MultipleTree(db, nvar);
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
  
 
      
     
     
     
     
 

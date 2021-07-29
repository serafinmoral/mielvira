/*
 * HierarchyClassifier.java
 *
 * Created on 13 de diciembre de 2006, 16:35
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package elvira.learning.classification.supervised.discrete;

/**
 *
 * @author smc
 */



import elvira.Configuration;
import elvira.Bnet;
import elvira.database.DataBaseCases;
import java.util.Vector;
import elvira.Hierarchy;
import elvira.Relation;
import elvira.CaseListMem;
import elvira.FiniteStates;
import java.io.*;
import elvira.learning.classification.ConfusionMatrix;

public class HierarchyClassifier extends DiscreteClassifier{
    
public static final int NAIVE = 1;
public static final int SELECTIVENAIVE = 2;
public static final int CLASSTREE = 3;
    
    
protected HierarchyStructure tree;    
protected int method;
    
    
    
    /** Creates a new instance of HierarchyClassifier */
    public HierarchyClassifier(DataBaseCases data, Hierarchy h, int m) throws elvira.InvalidEditException{
        super(data,true);
        method = m;
        tree = new HierarchyStructure(h);
        classifier = new Bnet();
     
    
}
    public HierarchyClassifier(DataBaseCases data, HierarchyStructure b, int m) throws elvira.InvalidEditException{
        super(data,true);
        method = m;
        tree = b;
         classifier = new Bnet();
     
    }
    
  public HierarchyClassifier(DataBaseCases data, int classn, int m) throws elvira.InvalidEditException{
        super(data,true);
        method = m;
         classVar = (FiniteStates)this.cases.getNodeList().elementAt(classn);
    
       this.classNumber         = classVar.getNumStates();
       this.confusionMatrix     = new ConfusionMatrix(this.classNumber);
        classifier = new Bnet();
    }
       
    
 
 
    /** 
   * Method of Classifier interface. This method is used to classify a instance,
	 * @param instance case to classify
	 * @return a double arrray with a probability associated to each class value
   */
  public double[] classify (Configuration instance) {
    double valprob[];
    int i,n;
    
    n = classVar.getNumStates();
    valprob = new double[n];
    
    for(i=0;i<n;i++ ) {valprob[i] = 1.0;}
    
    tree.classify (instance, 1.0, valprob);
    double total = 0.0;
    for(i=0;i<n;i++) {total += valprob[i];}
    for(i=0;i<n;i++) {valprob[i] = valprob[i]/total;}
    return(valprob);
    
  } 
    
  
   
     public int assignClass(Configuration c){
         int i,n,index;
         double maxp;
         double[] result;
         
         
         maxp = 0.0;
         index=0;
         n = classVar.getNumStates();
         result = classify(c);
         for (i=0; i<n;i++){
             if(result[i]>maxp){
                 index= i;
                 maxp = result[i];
             }
             
         }
         
         
         return(index);
     }
  
  
    
  /**
   * This method learns the classifier structure and parameters. It is better to do both things at the same time
   * by efficiency reasons.
   */
    
    public  void structuralLearning() throws elvira.InvalidEditException, Exception
  {  DataBaseCases localcases;
     HierarchyClassifier childClass;
     int i,nc;
     
     System.out.println("Entrada structural learning ");
     tree.print();
     tree.auxvar.print();
     System.out.println("Numvero casos " + tree.auxvar.getNumStates());
     nc = tree.reference.getNumStates();
   //  for (i=0; i<nc; i++){
  //       System.out.println(" i = " + tree.getMembers()[i]);
   //  }
   
     
          localcases = cases.transform(tree.reference, tree); 
          DiscreteClassifier localcla;
        
          localcla = new Naive_Bayes();
         
            switch (method){
        case NAIVE: {localcla =  new Naive_Bayes(localcases, true);
                    break;
        }
        
        case SELECTIVENAIVE: {localcla =  new WrapperSelectiveNaiveBayes(localcases, true);
                    break;
        }
          case CLASSTREE: {localcla =  new SimpleTree(localcases);
                    break;
        }
    }
            
            localcla.setClassVar((FiniteStates) tree.auxvar);
            localcla.structuralLearning();
             if  (localcla.getClass() == Naive_Bayes.class){
            localcla.parametricLearning();
             }
           
            if  (localcla.getClass() == WrapperSelectiveNaiveBayes.class){
            localcla.parametricLearning();
             }
            tree.net = localcla;
            classifier = localcla.classifier;
            
          if (!tree.isLeaf())  {
                nc= tree.getNumChildren() ;
                for (i=0;i<nc;i++){
                    childClass = new HierarchyClassifier (cases ,(HierarchyStructure) tree.getChildat(i), method);
                    childClass.structuralLearning();                 
                    
                }
                
          }
    
  }
          public  void learnhierarchy(int m) throws elvira.InvalidEditException, Exception
  { 
        int i;
        boolean[] active = new boolean[this.classNumber];   
        for(i=0; i<this.classNumber; i++) {active[i]=true;}
        this.tree= new HierarchyStructure(this.classVar);
        
        if (m==1) {
        this.learnhierarchy(  this.classNumber, active, tree  );}
        else  {this.learnhierarchy2(  this.classNumber, active, tree  );}
      
        
          
              
          }
          
       
      public  void learnhierarchy(int nactive, boolean[] active, HierarchyStructure ltree) throws elvira.InvalidEditException, Exception
  { 
      
          
          
     int i,j;
     double[][] distancematrix;
     double[] totalvector;
     boolean[] membersr,membersl;  
   
     System.out.println("Entro en learn hierarchy with " + nactive + "members ");
     
     
     ltree.auxvar = new FiniteStates(nactive);
       ltree.members = active;
     
      ltree.type = tree.LEAF;
      
      
      
    if (nactive <= 2){
        
       
      
        return;
    }
         
    
        
        DiscreteClassifier localcla;
        DataBaseCases localcases  = cases.transform(classVar, ltree); 
        DataBaseCases train,test;
        
        
        distancematrix = new double[nactive][nactive];
        totalvector = new double[nactive];
        
        train = new    DataBaseCases();
        
          test = new    DataBaseCases();
        
        FiniteStates localclass;
        
        localclass = new FiniteStates(nactive);
        localclass.setName("Claseauxililar");
          
          
        localcases.divideIntoTrainAndTest(train, test, 0.5);
        
        localcla = new Naive_Bayes();
        
            switch (method){
        case NAIVE: {localcla =  new Naive_Bayes(train, true);
        localcla.setClassVar(localclass);
          localcla.structuralLearning();
            localcla.parametricLearning();
                    break;
        }
        
        case SELECTIVENAIVE: {localcla =  new WrapperSelectiveNaiveBayes(train, true);
        localcla.setClassVar(localclass);
        System.out.println(localcla.getClassVar().getName());
        localcla.structuralLearning();
            localcla.parametricLearning();
                    break;
        }
          case CLASSTREE: {localcla =  new SimpleTree(train);
          
          localcla.structuralLearning();
                    break;
        }
    }
         
            
            
            localcla.test(test);
            
            for(i=0; i<nactive; i++){ 
                totalvector[i] = localcla.confusionMatrix.getVector()[i];  
                for (j=0; j<nactive; j++){
                  distancematrix[i][j] = localcla.confusionMatrix.getMatrix()[i][j];  
                  
                }
                }
            
            
            switch (method){
        case NAIVE: {localcla =  new Naive_Bayes(test, true);
         localcla.setClassVar(localclass);
        localcla.structuralLearning();
            localcla.parametricLearning();
                    break;
        }
        
        case SELECTIVENAIVE: {localcla =  new WrapperSelectiveNaiveBayes(test, true);
        localcla.setClassVar(localclass);
        localcla.structuralLearning();
            localcla.parametricLearning();
                    break;
        }
          case CLASSTREE: {localcla =  new SimpleTree(test);
           localcla.setClassVar(localclass);
            localcla.structuralLearning();
                    break;
        }
    }
         
            
            
            localcla.test(train);
            
            for(i=0; i<nactive; i++){ 
                  totalvector[i] += localcla.confusionMatrix.getVector()[i]; 
                  System.out.println("Total vector " + totalvector[i]);
                for (j=0; j<nactive; j++){
                    
                  distancematrix[i][j]  +=     localcla.confusionMatrix.getMatrix()[i][j];   
                  System.out.println("Distance Matrix i = " + i + "j = " + j + "Distancia = " + distancematrix[i][j]);
                }
                    distancematrix[i][i]=0;
                }
            
           boolean[][] groups = new boolean[nactive][nactive];
           
           
           
           
            for(i=0; i<nactive; i++) 
                for (j=0; j<nactive; j++){
                    if (i==j) groups[i][j] = true;
                    else groups[i][j] = false;
           
                }
           
           int imax, jmax;
           double distancemax;
           
           int remaining;
           remaining = nactive;
           
           while (remaining>2){
                  imax=0;jmax=0;
                  distancemax= -1;
                for(i=0; i<remaining; i++) 
                 for (j=i+1; j<remaining; j++){
                   if ((distancematrix[i][j]+distancematrix[j][i])/(totalvector[i]+totalvector[j]) > distancemax   ){
                       imax = i; jmax=j; distancemax = (distancematrix[i][j]+distancematrix[j][i])/(totalvector[i]+totalvector[j]);
                   }
                     
                 }
              
               
               for(j=0; j<nactive; j++) {
                   groups[imax][j] = groups[imax][j]|| groups[jmax][j];
                    groups[jmax][j] =  groups[remaining-1][j];    
                   
               }
                  
             System.out.println("Uno grupos " + imax + " y " + jmax);
                  
               totalvector[imax] = totalvector[imax] + totalvector[jmax];
               
               totalvector[jmax] = totalvector[remaining-1];
                 
                 
                 for (j=0; j<nactive; j++){
                      if (j != imax)  {
                    distancematrix[imax][j] = distancematrix[imax][j] + distancematrix[jmax][j];
                    distancematrix[j][imax] =   distancematrix[j][imax] + distancematrix[j][jmax];
                      }
                     
                 }
               
               for (j=0; j<nactive; j++){
               
                {   distancematrix[jmax][j] = distancematrix[remaining-1][j] ;
                    distancematrix[j][jmax] =   distancematrix[j][remaining-1] ;
                }
                     
                 }
               
               
                    remaining--; 
               
           }
           
           
           
        membersl = new boolean[classVar.getNumStates()];
                
        membersr = new boolean[classVar.getNumStates()];
           
    j = 0;
   int nr = 0;
   int nl = 0;
    
    
    for (i=0;i<classVar.getNumStates(); i++){
        if (active[i]) {
            if (groups[0][j]) {membersl[i] = true;membersr[i] = false;nl++;}
            else {membersr[i] = true;membersl[i] = false; nr++; } 
            j++;
        }
        else {membersl[i] = false; membersr[i] = false;}
        
    }
         
        ltree.type = ltree.INNER;     
   
   ltree.addChild(membersl);
           
    
      ltree.addChild(membersr);
           
      ltree.auxvar = new FiniteStates(2);
      
     
      
      System.out.println("Izquierda " + nl + "Derecha " + nr);
    
       learnhierarchy(nl, membersl, (HierarchyStructure) ltree.getChildat(0)); 
      
           learnhierarchy(nr, membersr, (HierarchyStructure) ltree.getChildat(1));    
      }
      
       public  void learnhierarchy2(int nactive, boolean[] active, HierarchyStructure ltree) throws elvira.InvalidEditException, Exception
  { 
      
          
          
     int i,j;
    
     boolean[] membersr,membersl;  
   
     System.out.println("Entro en learn hierarchy 2 with " + nactive + "members ");
     
     
     ltree.auxvar = new FiniteStates(nactive);
       ltree.members = active;
     
      ltree.type = tree.LEAF;
      
      
      
    if (nactive <= 2){
        
       
      
        return;
    }
         
    
        
  
           
        membersl = new boolean[classVar.getNumStates()];
                
        membersr = new boolean[classVar.getNumStates()];
           
    j = 0;
   int nr = nactive/2;
   int nl = nactive - nr;
    
    int current = 0;
    for (i=0;i<classVar.getNumStates(); i++){
        if (active[i]) {
            if (current<nl) {
             membersl[i] = true;membersr[i] = false;current++;}
            else {
                membersr[i] = true;membersl[i] = false; current++; } 
          
        }
        else {membersl[i] = false; membersr[i] = false;}
        
    }
         
        ltree.type = ltree.INNER;     
   
   ltree.addChild(membersl);
           
    
      ltree.addChild(membersr);
           
      ltree.auxvar = new FiniteStates(2);
      
     
      
      System.out.println("Izquierda " + nl + "Derecha " + nr);
    
       learnhierarchy2(nl, membersl, (HierarchyStructure) ltree.getChildat(0)); 
      
           learnhierarchy2(nr, membersr, (HierarchyStructure) ltree.getChildat(1));    
      }
      
       
   public double test(DataBaseCases test) {
    DataBaseCases newtest = test;
/*    if(!test.getVariables().equals(this.cases.getVariables()))
      newtest = this.projection(this.cases, test.getVariables());
*/
  System.out.println("Entro en test");
    //Check: the classifier must be trained


    //Check: the number of variables must be the same
   

    //Check: the variables must have the same number of states
  

    int nTest = newtest.getNumberOfCases();
 int assignedClass;
         int realClass;
    Vector      vector      = newtest.getRelationList();
  	Relation    relation    = (Relation)vector.elementAt(0);
    CaseListMem caselistmem = (CaseListMem)relation.getValues();
  Configuration caseToTest;
  	
    double accuracy = (double) 0;

      
        
        caselistmem.initializeIterator();
        
        
        
  
         
    
     while (caselistmem.hasNext()){
       caseToTest = caselistmem.getNext();
       System.out.println("Nuevo caso");
       assignedClass = this.assignClass(caseToTest);
       realClass = (int) caseToTest.getValue(classVar);
      if (assignedClass == realClass){
    		accuracy = accuracy + 1;
                 
      }
       {
      this.confusionMatrix.actualize(realClass, assignedClass);
       this.confusionMatrix.actualize(realClass);
    }
    }
    accuracy = (accuracy / (double) nTest) * (double) 100;

  	return(accuracy);
  }
   
    
             
 /**
   * Main to use the class from the command line
   */
  public static void main(String[] args) throws FileNotFoundException, IOException, elvira.InvalidEditException, elvira.parser.ParseException, Exception{
    int nvar;
      
    if(args.length != 5) {
      System.out.println("Usage: file-train.dbc classnumber file-test.dbc file-out.elv method");
      System.exit(0);
    }
  nvar =(Integer.valueOf(args[1]).intValue());
    FileInputStream fi = new FileInputStream(args[0]);
    DataBaseCases   db = new DataBaseCases(fi);
    fi.close();

    HierarchyClassifier clasificador = new HierarchyClassifier(db, nvar, (Integer.valueOf(args[4]).intValue()));
    
    clasificador.learnhierarchy(1);
    clasificador.tree.print();
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
      
      
      

            
        
                
         
    
  



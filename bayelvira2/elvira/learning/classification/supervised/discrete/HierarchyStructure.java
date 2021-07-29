/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.learning.classification.supervised.discrete;

/**
 *
 * @author smc
 */


import java.io.*;
import java.util.Vector;
import elvira.*;
import elvira.learning.classificationtree.*;

public class HierarchyStructure extends Hierarchy {
    
public FiniteStates auxvar;
public DiscreteClassifier net;
    
    
    
    /** Creates a new instance of HierarchyBnet */
    public HierarchyStructure() {
    }
   
     
    /** Creates a new instance of NodeHierarchy */
    public HierarchyStructure(FiniteStates x) {
     
        
      super(x);
      
      auxvar=null;
      net=null;
        
        
    }
     
    
       
    /** Creates a new instance of NodeHierarchy */
    public HierarchyStructure(Hierarchy h) {
     
        HierarchyStructure hc;
        int i,n;
        
        reference = h.reference;
        type = h.type;
        nchild = h.nchild;
        name="Valuesof"+h.name;
         children = new Vector();
         
         if (nchild>0) {auxvar=new FiniteStates(nchild);
         auxvar.setName(name);}
         else {auxvar=null;}
         
         
         
         for (i=0; i<nchild; i++){
          hc = new HierarchyStructure( h.getChildat(i));
          children.addElement(hc);
         
         }
         
        members = new boolean[reference.getNumStates()];
        
        n=0;
        
        for(i=0;i<reference.getNumStates(); i++){
            
            members[i] = h.members[i];
            if (members[i]) {n++;}
        }  
     
      if (type == LEAF){
            
       auxvar=new FiniteStates(n);
       auxvar.setName(name);  
            
      }
     
     
        
        
    }
     
        
/***********************
 *
 * Add a child to a node with the given defnition
 *
 **********************/    
    
 public void addChild(boolean definition[]){
     
      HierarchyStructure y;
      
      
     
        type = INNER;
         nchild ++;
        
         y = new HierarchyStructure(reference);
         y.members = definition;
         children.addElement (y);
        
        
     
     
 }    
    
 
 public FiniteStates getAuxVar(){
     
     return (auxvar);
 }
    
    
 
    /** 
   * Recursive auxiliar evaluation procedure. This method is used to classify a instance,
	 * @param instance case to classify
	 * @return a double arrray with a probability associated to each class value
   */
  public void  classify (Configuration instance, double x, double[] valprob) {
  
    int i,n,current,nc;

    DiscreteClassifier localdec;
    
    FiniteStates nodevar; 
    double[] result;
    
   
  
    localdec = this.net;
    nodevar = getAuxVar();
    n = nodevar.getNumStates();
   
    
    if (localdec.getClass() == SimpleTree.class){
    result = ((SimpleTree) localdec).computePosterior(instance);
    } 
    else{ if  (localdec.getClass() == Naive_Bayes.class){
    result = ((Naive_Bayes) localdec).classify(instance);
    } 
    else {
    result =  localdec.classify(instance);
    }
    }
       double total = 0.0;
    for(i=0;i<n;i++) {total += result[i];}
    for(i=0;i<n;i++) {result[i] = result[i]/total;
                  System.out.println("Caso " + i + "Valor Probabilidad " + result[i]);
    }
    
    if (this.isLeaf()) {
        n = reference.getNumStates();
        current=0;
        for(i=0; i<n; i++) {
            if (this.getMembers()[i]) {
//                System.out.println("hoja Caso " + i + "Probabilidad " + x*result[current]);
                valprob[i] = x*result[current];
                current++;
            }
        }
        
    }
    else { 
        nc= getNumChildren() ;
                for (i=0;i<nc;i++){
  //                  System.out.println("Bajando " + "hijo " + i + "Probabilidad " + x*result[i]);
        ((HierarchyStructure) getChildat(i)).classify (instance, x*result[i],valprob);
    }
    }
    
    
  }   

 
  
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.learning.classification.supervised.discrete;

import elvira.Bnet;
import elvira.FiniteStates;
import elvira.NodeList;
import elvira.Relation;
import elvira.database.DataBaseCases;
import elvira.learning.SimpleLearning;

/**
 *
 * @author smc
 */
public class SimpleBayesianClassifier  extends DiscreteClassifierDiscriminativeLearning {
    
       
    public SimpleBayesianClassifier() {
    super();
  } 
    
    
    public SimpleBayesianClassifier(DataBaseCases data) throws elvira.InvalidEditException{
    super(data, true);
  }
    
    
  
  public void structuralLearning() throws elvira.InvalidEditException{
    this.evaluations = 1; //There is not search
    SimpleLearning ls; 
    
    
       int i;
    NodeList newnodes;
    Relation rel;
    
    newnodes = cases.getVariables().copy();
    for(i=0;i<newnodes.size(); i++) {
        newnodes.setElementAt(newnodes.elementAt(i).copy(), i);
    }
    
    classVar = (FiniteStates) newnodes.lastElement();
    
    ls = new SimpleLearning(cases);
    
    ls.learning();
    
    
    
    classifier = new Bnet(newnodes);
    
    
    
    
    
    
  }
    
}

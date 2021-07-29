/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.learning.classification.supervised.discrete;

import elvira.Bnet;
import elvira.CaseListMem;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.Link;
import elvira.potential.Potential;
import elvira.LinkList;
import elvira.Node;
import elvira.NodeList;
import elvira.Relation;
import elvira.database.DataBaseCases;
import elvira.learning.classification.AuxiliarPotentialTable;
import elvira.potential.PotentialTable;

import java.io.*;
import java.util.Vector;
import java.util.Iterator;
/**
 *
 * @author smc
 */
public class Parentsgraph  extends DiscreteClassifierDiscriminativeLearning {
    
    int nparents;
    
    public Parentsgraph() {
    super();
  } 
    
    
    public Parentsgraph(DataBaseCases data) throws elvira.InvalidEditException{
    super(data, true);
  }
    
    
    public Parentsgraph(DataBaseCases data, int limit) throws elvira.InvalidEditException{
    super(data, true);
    nparents = limit;
  }
    
    
  public void structuralLearning() throws elvira.InvalidEditException{
    this.evaluations = 1; //There is not search
    
    int i;
    NodeList newnodes;
    Relation rel;
    
    newnodes = cases.getVariables().copy();
    for(i=0;i<newnodes.size(); i++) {
        newnodes.setElementAt(newnodes.elementAt(i).copy(), i);
    }
    
    classVar = (FiniteStates) newnodes.lastElement();
    
    classifier = new Bnet(newnodes);
    
    if (newnodes.size()-1< nparents ) {nparents = newnodes.size()-1;}
    
    for (i=0;i<nparents; i++) {
        classifier.addLink(newnodes.elementAt(i), classVar);
    }   
        
        
        for (i=0; i<newnodes.size()-1; i++) {
            rel = new Relation(newnodes.elementAt(i));
            classifier.addRelation(rel);
        }
        
    }
    
    
    
    
    
   
     
    
    
}

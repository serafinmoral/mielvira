/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.learning.classification.supervised.discrete;

import elvira.database.DataBaseCases;

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
import elvira.potential.PotentialTable;
import elvira.learning.BDeMetrics;
import elvira.learning.BICMetrics;
import elvira.learning.AkaikeMetrics;
import elvira.learning.K2Metrics;

import elvira.learning.Metrics;

import elvira.learning.DELearning;

import java.io.*;
import java.util.Vector;
import java.util.Iterator;

/**
 *
 * @author smc
 */
public class SBND  extends DiscreteClassifierDiscriminativeLearning {
    
    Metrics met;
    
      /**
   * Basic Constructor
   */
  public SBND() {
    super();
  }

  /**
   * Constructor
   * @param DataBaseCases. cases. The input to learn a classifier
   * @param boolean correction. To apply the laplace correction
   */
  public SBND(DataBaseCases data, boolean lap) throws elvira.InvalidEditException{
    super(data, lap);
    
    met = new BDeMetrics (cases,4.0);
    classVar = (FiniteStates) cases.getNodeList().lastElement();
  }
  
  
   /**
   * Constructor
   * @param DataBaseCases. cases. The input to learn a classifier
   * @param boolean correction. To apply the laplace correction
   */
  public SBND(DataBaseCases data, boolean lap, int m) throws elvira.InvalidEditException{
    super(data, lap);
    
     met = new BDeMetrics ();
    switch (m) {
        case 1:{  met = new BDeMetrics (cases,4.0);
                 break;
        }
        case 2:{met = new BICMetrics (cases);
                 break;
        }
         case 3:{met = new AkaikeMetrics (cases);
                 break;
        }
          case 4:{met = new K2Metrics (cases);
                 break;
        }
  }
   
    
    
    classVar = (FiniteStates) cases.getNodeList().lastElement();
  }
    
  
  public SBND(DataBaseCases data, boolean lap, int m, int s) throws elvira.InvalidEditException{
    super(data, lap);
    
     met = new BDeMetrics ();
    switch (m) {
        case 1:{  met = new BDeMetrics (cases,s);
                 break;
        }
        case 2:{met = new BICMetrics (cases);
                 break;
        }
         case 3:{met = new AkaikeMetrics (cases);
                 break;
        }
          case 4:{met = new K2Metrics (cases);
                 break;
        }
  }
   
    
    
    classVar = (FiniteStates) cases.getNodeList().lastElement();
  }
    
  
   public void structuralLearning() throws elvira.InvalidEditException{
       NodeList candidates,nodes,par;
      
       
       
       boolean[][] graph;
       boolean[] active;
       int i, imax;
       double max,z1,z2;
       
      candidates = new NodeList();
      
      candidates.insertNode(classVar);
          
       
      nodes = cases.getVariables();
      par = new NodeList();
      classifier = new Bnet(nodes);  
       
      
       active = new boolean[nVariables];
       
       for(i=0; i<nVariables-1; i++ ) {
           active[i] = true;
       }
       
     
       do {
             max = -1.0;
             imax = 0;
             for(i=0; i<nVariables-1; i++ ) {
              
           if (active[i]) {
              z1 = computeParents(nodes.elementAt(i),candidates);
              candidates.removeNode(classVar);
               z2 = computeParents(nodes.elementAt(i),candidates);
            candidates.insertNode(classVar);
            if ((z1-z2) >  max ) {
                max = z1-z2;
                imax = i;
            }
           }
           
             }
          if (max>0.0) {
              par = new NodeList();
              z1 = computeParents(nodes.elementAt(imax),candidates,par); 
              active[imax] = false;
              candidates.insertNode(nodes.elementAt(imax));
              System.out.println("He seleccionado variable " + nodes.elementAt(imax).getName());
              
                  { for(i=0;i<nVariables; i++ ) {
                  if(  par.getId(nodes.elementAt(i)) != -1     ) {
                     classifier.createLink(nodes.elementAt(i), nodes.elementAt(imax));
                       System.out.println("Con padre " + nodes.elementAt(i).getName());
                  }
              }
          }   
          }
             
       } while (max>0.0) ;
             
       
       
       
       
       
   }  
   
   
   
   
   public void parametricLearning() { 
       DELearning param;
       
       param = new DELearning(cases,classifier);
       param.learninglaplace();
   }
   
   
   public double computeParents(Node x, NodeList candidates, NodeList par) {
       
       boolean[] isp;
       int i;
       double max, thismax,z;
       boolean add;
       int varmax;
       int nnodes;
       Node v;
       
       nnodes = candidates.size();
       isp = new boolean[nnodes];
       
        par.insertNode(x);

      
        for (i = 0; i < nnodes ; i++) {
            isp[i] = false;
        }
        
        
        max = met.scoret(par);

    //    System.out.println("primera llamada a score sin padres");
        boolean changes = true;

    //    System.out.println("Modificando el conjunto de padres");
        while (changes) {
            thismax = max - 1.0;
            changes = false;
            add = true;
            varmax = 0;
            for (i = 0; i < nnodes; i++) {
                v = candidates.elementAt(i);
                if (isp[i]) {
                    par.removeNode(v);
                    z = met.scoret(par);
                    par.insertNode(v);
                    if (z > thismax) {
                        thismax = z;
                        varmax = i;
                        add = false;
                    }
                } else {
                    par.insertNode(v);
                         //  System.out.println("adding variable " + v.getName());
                         //  System.out.println("Numero de variables " + par.size());
                         z = met.scoret(par);

                        // System.out.println("Score " + z);
                    par.removeNode(v);

                    if (z > thismax) {
                        thismax = z;
                        varmax = i;
                        add = true;
                    }

                }
            }
            if (thismax > max) {
                max = thismax;
                changes = true;
      //              System.out.println("New score " + max);
                if (add) {
        //                    System.out.println("Adding variable " + candidates.elementAt(varmax).getName());
                    par.insertNode(candidates.elementAt(varmax));
                    isp[varmax] = true;
                } else {
                    par.removeNode(candidates.elementAt(varmax));
        //                 System.out.println("Removing variable " + par.elementAt(varmax).getName());
                    isp[varmax] = false;
                }
            }

        }

       par.removeNode(x);
       
       return max;
   }
  
  
   public double computeParents(Node x, NodeList candidates)  {
       
      NodeList par = new NodeList();
       
      return ( computeParents(x,candidates,par)) ;
      
    
}

}
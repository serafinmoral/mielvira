/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.learning;

import elvira.Bnet;
import elvira.FiniteStates;
import elvira.Graph;
import elvira.InvalidEditException;
import elvira.Node;
import elvira.NodeList;
import elvira.database.DataBaseCases;
import elvira.parser.ParseException;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author smc
 */
public class SimpleLearning extends Learning {
    
     
    DataBaseCases input;   // The cases for the input algorithm.
    int numberMaxOfParents;// The number of maximal parents for each node.
    Metrics metric;      // The 
    NodeList nodes;
    Bnet result;
  
     
public SimpleLearning(DataBaseCases cases) {
    
    
    input = cases;
    nodes = cases.getVariables();
    //links = new LinkList();
    result = new Bnet();
    metric = new BDeMetrics(cases,2.0);
    
    
    
    
    
}


    public void  learning() {
NodeList candidates,cmn;
Vector parents;
NodeList res;

Node n;

int i, imin;
double min,z;





candidates = nodes.copy();
 
imin = 0;




while(candidates.size()>1) {
    
    
    parents = new Vector();

imin = 0;

 cmn = candidates.copy();
     cmn.removeNode(candidates.elementAt(0));
     res = new NodeList();

     computeParents(((FiniteStates)candidates.elementAt(0)),cmn,res );
min = res.getSize();
parents.insertElementAt(res,0);
     

for(i=1;i<candidates.size(); i++) {
    res = new NodeList();

    cmn = candidates.copy();
     cmn.removeNode(candidates.elementAt(i));
     computeParents(((FiniteStates)candidates.elementAt(i)),cmn,res );

     z = res.size();
     parents.insertElementAt(res, i);
     if (z < min) {
         min=z;
         imin = i;
     }
     }
     n = candidates.elementAt(imin);
     res = (NodeList) parents.elementAt(imin);
     
     candidates.removeNode(n);
     
     
     for(i=0;i<res.size(); i++) {
         try {
         result.addLink(res.elementAt(i), n);
           } catch (InvalidEditException iee) {};
     }
}
             
}


    
    
    
 


        
        



    public void computeParents (FiniteStates x, NodeList cand, NodeList res ) {
        boolean changes,insert;
        FiniteStates z;
        int i,imax;
        double max,v;
        NodeList aux;
        
        
        aux = new NodeList();
        aux.insertNode(x);
        
        changes = true;
        
        max = metric.scoret(aux);
        imax = 0;
        
        while(changes) {
            changes = false;
            insert = true;
            for(i=0;i<cand.size();i++) {
                z = (FiniteStates) cand.elementAt(i);
                if (aux.getId(z) == -1){
                    aux.insertNode(z);
                    v= metric.score(aux);
                    if (v>max) {
                        changes=true;
                        imax=i;
                        insert=true;
                        max = v;
                    }
                    aux.removeNode(z);
                }
                else {
                     aux.removeNode(z); 
                      v= metric.score(aux);
                    if (v>max) {
                        changes=true;
                        imax=i;
                        insert=false;
                        max = v;
                    }
                    aux.insertNode(z);
                    
                }
            }
             if(changes) {
                 if(insert) {
                      aux.insertNode(cand.elementAt(imax));
                      res.insertNode(cand.elementAt(imax));
                 }
                 else {aux.removeNode(cand.elementAt(imax));
                 res.removeNode(cand.elementAt(imax));
                 }
             }
                
            
            
            
            
        }
        
        aux.removeNode(x);
        
        
        
        
    }
    
    public static void main(String args[]) throws ParseException, IOException { 

      BDeMetrics metric;
      Bnet net, baprend;
      FileWriter f2;
      
      net = null;
     


      FileInputStream f = new FileInputStream(args[0]);
      DataBaseCases cases = new DataBaseCases(f);
      metric = new BDeMetrics(cases);
      SimpleLearning learn = new SimpleLearning(cases);
      learn.learning();
      
   }  
    

}
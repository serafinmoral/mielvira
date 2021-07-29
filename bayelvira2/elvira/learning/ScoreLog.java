/*
 * This class serves to score a Bnet with a database of cases used for test
* using the log likelihood of the probability of all the observations in the database
* Computations are carried out by variable elimination, as having a large number of observations
* this procedure is always fast.
 */
package elvira.learning;
import elvira.*;
import elvira.database.*;
import elvira.learning.*;
import elvira.learning.classification.supervised.discrete.DiscreteClassifier;
import elvira.inference.clustering.HuginPropagation;
import elvira.potential.*;
import elvira.inference.elimination.VariableElimination;
import java.io.IOException;

/**
 *
 * @author smc
 */
public class ScoreLog {
   
    protected DataBaseCases cases;
    protected Bnet net;
    protected FiniteStates varclass;
    protected Boolean onlyclass = false;
    
    public ScoreLog (DataBaseCases d, Bnet b) {
        cases = d;
        net = b;
    }
    
     public ScoreLog (String name, Bnet b)  throws elvira.parser.ParseException, IOException{
         DataBaseCases d;
         
         d = new DataBaseCases(name);
        cases = d;
        net = b;
    }
     
       public ScoreLog (DataBaseCases d, DiscreteClassifier  c)  throws elvira.parser.ParseException, IOException{
         
        cases = d;
        net = c.getClassifier();
        onlyclass = true;
        varclass = c.getClassVar();
    }
     
     public double computeScoreLog(){
         double score;
     Configuration conf;
     Evidence evi;
     VariableElimination prog;
     
     
     score = 0.0;
     cases.initializeIterator();
      while(cases.hasNext()) {
                 conf = cases.getNext();
                 evi = new Evidence(conf);
                 prog = new  VariableElimination(net,evi,true); 
                 if (!onlyclass) {
                 score += Math.log(prog.computeProbabilityEvidence());
                 }
                 else {
                      score += Math.log(prog.computeProbabilityVar(varclass));
                     
                 }
                 
      }
         
      
      
     return score;
    
    
}
     
     public double avercomputeScoreLog(){
       
     
    
         
     return computeScoreLog()/((double) cases.getNumberOfCases());
    
    
}
     
}

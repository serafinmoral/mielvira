import java.util.Vector;
import java.io.*;
import java.util.Date;

/**
 * Class AbductiveInference
 * Implements a general setting for performing abductive inference
 *
 * @since 25/08/99
 */

public class AbductiveInference extends Propagation {

/**
 * The number of explanations to be calculated
 */

protected int nExplanations;		


/**
 * True if an explanation set is defined. False in other case.
 */

protected boolean partial;		

/**
 * Variables in the explanation set
 */ 

protected NodeList explanationSet;	

/**
 * A vector containing the K best explanations found during the propagation.
 * Each explanation will be represented by an instance of ConfProb
 */
protected Vector kBest;


/**
 * @return the number of explanations to calclulated
 */

public int getNExplanations( ){
  return nExplanations;
}

/**
 * @return the kind of abductive inference to be performed
 */

public boolean getPartial( ) {
  return partial;
}

/**
 * @return the explanation set
 */

public NodeList getExplanationSet(){
  return explanationSet;
}

/**
 * @return the k best explanations found until this moment
 */

public Vector getKBest(){
  return kBest;
}


/**
 * set the number of explanations to obtain 
 */

public void setNExplanations(int i) {
  nExplanations = i;
}


/**
 * set the explanation set
 *
 * @param nl the node list to be fixed as the explanation set
 */

public void setExplanationSet(NodeList nl) {
  explanationSet = nl;
}

/** 
 * set partial
 */

public void setPartial(boolean b) {
  partial = b;
}

/**
 * Obtain the K MPEs using exhaustive computation
 */

public void exhaustive(){

  PotentialTable pot,pot2;
  int i,pos;
  double prob;
  Relation R2;
  RelationList rl;
  Configuration conf,aux;
  Explanation exp;
  double pEvidence;
  HuginPropagation hp;

  Date D;
  double time;


  System.out.println("Computing best explanation (exhaustive)...");
  D = new Date();
  time = (double)D.getTime();    

  // first, we calculate the probability of the evidence
  // using a hugin propagation

  hp = new HuginPropagation(network,observations);
  if (observations.size()>0){
    pEvidence = hp.obtainEvidenceProbability("yes");
  }
  else pEvidence = 1.0;
  System.out.println("Probabilidad de la evidencia: " + pEvidence);          


  // if there is not explanation set, put it as the non observed variables
  if (explanationSet.size()==0){
    obtainInterest();
    explanationSet = interest;
  }
  
  rl = network.getInitialTables();
  pot = (PotentialTable)((Relation)rl.elementAt(0)).getValues();
  if (observations.size()>0) pot.instantiateEvidence(observations); 
  for(i=1; i<rl.size(); i++) {
    R2 = (Relation) rl.elementAt(i);
    pot2 = (PotentialTable)R2.getValues();
    if (observations.size()>0) pot2.instantiateEvidence(observations);
    pot = pot.combine(pot2);
  }
  pot = (PotentialTable) pot.marginalizePotential(explanationSet.toVector()); 
  

  // now select the kBest

  kBest = new Vector();
  conf = new Configuration(pot.getVariables());
  for(i=0; i<pot.values.length; i++){
    prob = pot.getValue(i);
    pos = posToInsert(prob);
    if (pos != -1){
      aux = new Configuration(explanationSet.toVector(),conf);
      exp = new Explanation(aux,prob);
      kBest.insertElementAt(exp,pos);
    }
    if (kBest.size() > nExplanations) 
      kBest.removeElementAt(nExplanations);
    conf.nextConfiguration();
  }

  // now we divide by pEvidence

  if (observations.size()!=0){
    for(i=0; i < nExplanations; i++){
      exp = (Explanation) kBest.elementAt(i);
      exp.setProb(exp.getProb()/pEvidence);
    }
  }

  // showing messages

  D = new Date();
  time = ((double)D.getTime() - time) / 1000;
  System.out.println("Best explanation computed");
  System.out.println("Time (secs): " + time);     

}



/**
 * @return the position of kBest in which the value must be inserted.
 * @return -1 if the value mustn't be inserted.
 *
 * @param val the value to insert 
 */

public int posToInsert(double val) {

  int min,max,middle;
  int s;
  double prob,pmiddle;


  s = kBest.size();
  if (s == 0) {
    middle = 0;
  } else if ( ( ((Explanation)kBest.elementAt(s-1)).getProb() > val)) {
      if (s < nExplanations) middle = s;
      else middle = -1;
  }
  else{
    for(min = 0, max = s-1, middle = (int) (min+max)/2; 
        min < max; 
        middle = (int) (min+max)/2){
      pmiddle = ((Explanation)kBest.elementAt(middle)).getProb();
      if (pmiddle < val){
        max = middle;
      }
      else if (pmiddle > val) {
        min = middle + 1;
      }
      else break; // middle is the possition 
    }
  }

  return middle;

}



/**
 * Saves the result of a propagation to a file.
 * @param Name a String containing the file name.
 */

public void saveResults(String S) throws IOException {

  FileWriter F;
  PrintWriter P;
  Explanation exp;
  int i;

  F=new FileWriter(S);
  
  P=new PrintWriter(F);
  
  for (i=1 ; i<=kBest.size() ; i++) {
    exp = (Explanation)kBest.elementAt(i-1);
    P.print("\nExplanation " + i + " { \n");
    exp.save(P);
  }
  
  F.close();
}



}  // end of class
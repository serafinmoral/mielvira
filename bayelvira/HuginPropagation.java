import java.util.Vector;
import java.io.*;
import java.util.Date;

/**
 * Class HuginPropagation. Implements the Hugin probabilistic propagation, 
 * and also some auxiliar procedures that can be useful for othe classes
 *
 * @since 27/09/99
 */

public class HuginPropagation extends Propagation {

/**
 * The jointree in which the propagation is carried out 
 */

JoinTree jt;

/**
 * An string indicating the type of potential to be used in propagation
 */

private String typeOfPotential;

/**
 * Program for performing experiments.
 * The arguments are as follows.
 * 1. Input file: the network.
 * 2. Output file.
 * 3. typeOfPotential. (tables or trees)
 * 4. Evidence file.
 * If the evidence file is omitted, then no evidences are
 * considered
 */

public static void main(String args[]) throws ParseException, IOException {

  Bnet b;
  Evidence e;
  FileInputStream networkFile, evidenceFile;
  HuginPropagation hp;
  int i;
  
  if (args.length<3)
    System.out.println("Too few arguments");
  else {
    networkFile = new FileInputStream(args[0]);
    b = new Bnet(networkFile);

    if ( !( (args[2].equals("tables")) || (args[2].equals("trees")) ) ){
      System.out.println("TypeOfPotential have to be in {tables,trees}");
      System.exit(0);
    } 

    if (args.length==4) {
      evidenceFile= new FileInputStream(args[3]);
      e = new Evidence(evidenceFile,b.getNodeList());
    }
    else
      e = new Evidence();
  
    hp = new HuginPropagation(b,e,args[2]);

    hp.propagate(hp.getJoinTree().elementAt(0),"no");
    hp.saveResults(args[1]);
  }
}




/**
 * Constructor.
 * @param b the Bnet used for the compilation of the joinTree
 * assume tables for potentials
 */

HuginPropagation(Bnet b){

  observations = new Evidence();
  interest = new NodeList();
  results = new Vector();
  setProblem("marginal");
  setMethod("Hugin");
  network = b;
  typeOfPotential = new String("tables");
  jt = new JoinTree();
  jt.treeOfCliques(network);        

}

/**
 * Constructor.
 * @param b the Bnet used for the compilation of the joinTree
 * @param e the evidence
 * assume tables for potentials
 */

HuginPropagation(Bnet b,Evidence e){

  observations = e;
  interest = new NodeList();
  results = new Vector();
  setProblem("marginal");
  setMethod("Hugin");
  network = b;
  typeOfPotential = new String("tables");
  jt = new JoinTree();
  jt.treeOfCliques(network);        
}

/**
 * Constructor.
 * @param b the Bnet used for the compilation of the joinTree
 * @param e the evidence
 * @param pot indicates the type of potential to be used
 */

HuginPropagation(Bnet b,Evidence e,String pot){

  observations = e;
  interest = new NodeList();
  results = new Vector();
  setProblem("marginal");
  setMethod("Hugin");
  network = b;
  typeOfPotential = new String(pot);
  jt = new JoinTree();
  jt.treeOfCliques(network);        
}



/**
 * Constructor. Do not build the join tree
 * @param b the Bnet used for the compilation of the joinTree
 * @param e the evidence
 * assume tables for potentials
 */

HuginPropagation(Evidence e, Bnet b){

  observations = e;
  interest = new NodeList();
  results = new Vector();
  setProblem("marginal");
  setMethod("Hugin");
  network = b;
  typeOfPotential = new String("tables");
  jt = new JoinTree();

}

/**
 * Constructor. Do not build the join tree
 * @param b the Bnet used for the compilation of the joinTree
 * @param e the evidence
 * @param pot the type of potential to be used
 */

HuginPropagation(Evidence e, Bnet b, String pot){

  observations = e;
  interest = new NodeList();
  results = new Vector();
  setProblem("marginal");
  setMethod("Hugin");
  network = b;
  typeOfPotential = new String(pot);
  jt = new JoinTree();

}




/**
 * @return the join tree used in the propagation
 */

public JoinTree getJoinTree(){
  return jt;
}

/**
 * set the jointree to be used in propagation
 * @param tree the join tree
 */

public void setJoinTree(JoinTree tree){
  jt = tree;
}

/**
 * @return the type of potentias used in the propagation
 */

public String getTypeOfPotential(){
  return typeOfPotential;
}

/**
 * set the type of Potential to be used in propagation
 * @param pot the string indicating the type of potential to be used
 */

public void setTypeOfPotential(String pot){
  typeOfPotential = new String(pot);
}


/**
 * Restrict the tables of a join Tree to the observations.
 */

public void instantiateEvidence( ) {

  Relation R;
  NodeJoinTree node;
  int i, s;
  Potential pot; // era potentialTable
  SetVectorOperations svo = new SetVectorOperations();
  Vector commonVars;
  
  s = jt.getJoinTreeNodes().size();
  
  for (i=0 ; i<s ; i++) {
    node = jt.elementAt(i);
    R = node.getNodeRelation();
    commonVars = svo.intersection(R.getVariables().getNodes(),
                                      observations.getVariables());
    if (commonVars.size()!=0){
      pot = R.getValues(); // era (PotentialTable)
      pot.instantiateEvidence(observations);
      pot = transformPotentialAfterAddition(pot);
    }
  }

}


/**
 * Init messages for a Hugin-like propagation. So, only a message between
 * two cliques is necessary, and the message X-Y and Y-X are the same.
 * IMPORTANT: the cliques must be labeled
 *
 */

public void initHuginMessages( ) {

  Relation R;
  NodeJoinTree node,neighbour;
  int i,j,k,s,pos;
  int nodeLabel,neighbourLabel;  
  PotentialTable potTable; 
  PotentialTree potTree;
  NeighbourTreeList ntl,ntl2;
  NeighbourTree nt,nt2;
  String usedPotential;
  

  // identifying the type of potential used in the jointree
  usedPotential = new
       String((jt.elementAt(0).getNodeRelation().getValues()).getClass().getName());

  // beginning the process

  s = jt.getJoinTreeNodes().size();
  
  for (i=0 ; i<s ; i++) {
    node = jt.elementAt(i);
    nodeLabel = node.getLabel();
    ntl = node.getNeighbourList();
    
    for(j=0;j<ntl.size();j++){
      nt = ntl.elementAt(j);
      neighbour = nt.getNeighbour();
      neighbourLabel = neighbour.getLabel();
      R = nt.getMessage();
  
      if (nodeLabel < neighbourLabel){ //init unitary potential
        if ( usedPotential.equals("PotentialTable") ){
	  potTable = new PotentialTable(R.getVariables());
          potTable.setValue(1.0);
          R.setValues(potTable);
        }
        else if ( usedPotential.equals("PotentialTree") ){
          potTree = new PotentialTree(R.getVariables());
          potTree.setTree(new ProbabilityTree(1.0));
          potTree.updateSize();
          R.setValues(potTree);
        }

      }
      else{ // locate the inverse message and assign
        pos = jt.indexOf(neighbour);
        ntl2 = jt.elementAt(pos).getNeighbourList();
        for(k=0;k<ntl2.size();k++){
          nt2 = ntl2.elementAt(k);
          if (nt.getNeighbour().getLabel() == nodeLabel){
            nt2.setMessage(R);
            break;
          }
        }
      }
      
    } // end for j

  } // end for i

}



/**
 * Performs an upward propagation across the join tree, taking as root the 
 * node passed as argument. If divide="no", no divisions are performed. We
 * can take advantage of this parameter when we are sure that the
 * propagation is performed just after the initialisation of the potentials,
 * because in this case we will divide by 1 and so the division has no
 * sense. 
 * 
 * In messages values stores the more recent separator and otherValues the
 * old separator
 *
 * NOTE: all the tables (potentials and messages) are modified
 *
 * @param root the node used as root for the propagation
 * @param divide if "no" the division in the separators are not performed
 */

public void upward(NodeJoinTree root,String divide) {

  NeighbourTreeList ntl;
  NodeJoinTree node;
  int i;
  Relation R,R2;
  Potential pot,newSep,oldSep,temp;  // era PotentialTable

  ntl = root.getNeighbourList();
  
  // first we ask the messages

  for(i=0; i<ntl.size(); i++){
    node = ntl.elementAt(i).getNeighbour();
    askMessage(root,node,divide);  
  }
  
  // now, the messages are combined with the potential stored in root
  
  R = root.getNodeRelation();
  pot = R.getValues();  // era (PotentialTable)
  for(i=0; i<ntl.size(); i++){
    node = ntl.elementAt(i).getNeighbour();
    R2 = ntl.elementAt(i).getMessage();
    newSep = R2.getValues(); // era (PotentialTable)
    if (divide.equals("yes")){
      oldSep = R2.getOtherValues(); // era (PotentialTable)
      //pot = pot.combine(newSep.divide(oldSep));
      temp = newSep.divide(oldSep);
      temp = transformPotentialAfterCombination(temp);
      pot.combineWithSubset(temp);
      pot = transformPotentialAfterCombination(pot);
    }
    else{ //pot = pot.combine(newSep);
           pot.combineWithSubset(newSep);
           pot = transformPotentialAfterCombination(pot);
    }
    R.setValues(pot);
  }  


}


/**
 * ask: Request a message to a node which recursively does the same until 
 * a leaf is reached, point at which the information is propagated backward
 * 
 * @param sender the node which ask for the message
 * @param recipient the node asked for the message
 * @param divide if "no" no division is performed in the separator
 */

public void askMessage(NodeJoinTree sender, NodeJoinTree recipient,
			String divide) {

  NeighbourTreeList ntl;
  NeighbourTree nt;
  NodeJoinTree node;
  int i,father;
  Relation R,R2;
  Potential pot,newSep,oldSep,message,temp; // era PotentialTable


  ntl = recipient.getNeighbourList();
  if (ntl.size() != 1) { // recipient is not a leaf, ask more messages

    for(i=0; i<ntl.size(); i++){
      node = ntl.elementAt(i).getNeighbour();
      if (node.getLabel() != sender.getLabel())
         askMessage(recipient,node,divide);  
    }
    
  }

  // combination in recipient
  
  R = recipient.getNodeRelation();
  pot = R.getValues(); // era (PotentialTable)
  for(i=0; i<ntl.size(); i++){
    node = ntl.elementAt(i).getNeighbour();
    if (node.getLabel() != sender.getLabel()) {
      R2 = ntl.elementAt(i).getMessage();
      newSep = R2.getValues(); // era (PotentialTable)
      if (divide.equals("yes")){
        oldSep = R2.getOtherValues(); // era PotentialTable
        //pot = pot.combine(newSep.divide(oldSep));
        temp = newSep.divide(oldSep);
        temp = transformPotentialAfterCombination(temp); 
        pot.combineWithSubset(temp);
      }
      else //pot = pot.combine(newSep);
             pot.combineWithSubset(newSep);
      pot = transformPotentialAfterCombination(pot);
      R.setValues(pot);
    }
  }  
  
  // sending (storing) message to sender (father)
  // pot contains the potential stored in recipient 
  
  father = ntl.indexOf(sender);
  nt = ntl.elementAt(father);
  newSep = pot.marginalizePotential(nt.getMessage().getVariables().toVector());
  newSep = transformPotentialAfterAddition(newSep);
  if (divide.equals("no")){
    nt.getMessage().setValues(newSep);
  }
  else{   
    nt.getMessage().setOtherValues(nt.getMessage().getValues()); // era (PotentialTable)nt.get.....
    nt.getMessage().setValues(newSep);
  }
    
}


/**
 * Performs a downward propagation across the join tree, taking as root the 
 * node passed as argument. 
 *
 * NOTE: all the tables (potentials and messages) are modified
 *
 * @param root the node used as root for the propagation
 */

public void downward(NodeJoinTree root) {

  NeighbourTreeList ntl;
  NeighbourTree nt;
  NodeJoinTree node;
  int i;
  Relation R,R2;
  Potential pot,newSep; // era PotentialTable

  ntl = root.getNeighbourList();
  
  // caculate the message, store it in the separator and send 

  R = root.getNodeRelation();
  pot = R.getValues(); // era PotentialTable
  for(i=0; i<ntl.size(); i++){
    nt = ntl.elementAt(i);
    node = nt.getNeighbour();
    R2 = nt.getMessage();
    R2.setOtherValues(R2.getValues()); // era (PotentialTable)R2.get...
    newSep = pot.marginalizePotential(nt.getMessage().getVariables().toVector());
    newSep = transformPotentialAfterAddition(newSep); 
    nt.getMessage().setValues(newSep);
    sendMessage(root,node);  
  }
  
}

/**
 * sendMessage: send a message to a node which recursively does the same until 
 * a leaf is reached.
 * 
 * @param sender the node which ask for the message
 * @param recipient the node asked for the message
 */

public void sendMessage(NodeJoinTree sender, NodeJoinTree recipient) {

  NeighbourTreeList ntl;
  NeighbourTree nt;
  NodeJoinTree node;
  int i,father;
  Relation R,R2;
  Potential pot,newSep,oldSep,temp; // era PotentialTable


  // combination of the potential with the message sended by sender

  R = recipient.getNodeRelation();
  pot = R.getValues(); // era (PotentialTable)
  
  ntl = recipient.getNeighbourList();
  father = ntl.indexOf(sender);
  nt = ntl.elementAt(father);
  newSep = nt.getMessage().getValues(); // era PotentialTable
  oldSep = nt.getMessage().getOtherValues(); // era Potential Table

  //pot = pot.combine(newSep.divide(oldSep));
  temp = newSep.divide(oldSep);
  temp = transformPotentialAfterCombination(temp);
  pot.combineWithSubset(temp);
  pot = transformPotentialAfterCombination(pot);

  R.setValues(pot);
  
  // now send message to the children

  if (ntl.size() != 1) { // the node is not a leaf 
    for(i=0; i<ntl.size(); i++){
      node = ntl.elementAt(i).getNeighbour();
      if (node.getLabel() != sender.getLabel()){
        nt = ntl.elementAt(i);
        R2 = nt.getMessage();
        nt.getMessage().setOtherValues(nt.getMessage().getValues());
        newSep = pot.marginalizePotential(nt.getMessage().getVariables().toVector());
        newSep = transformPotentialAfterAddition(newSep); 
        nt.getMessage().setValues(newSep);
        sendMessage(recipient,node);
      }  
    }
  }
  
}



/**
 * Procedure to obtain the probability of the observed evidence.
 * @param initialize indicates if initializations over the jointree have
 *        to be carried out
 */

public double obtainEvidenceProbability(String initialize) {

  if (initialize.equals("yes")){
    if (typeOfPotential.equals("tables")) jt.initTables(network);
    else if (typeOfPotential.equals("trees")) jt.initTrees(network);
    else {
      System.out.println(typeOfPotential + " is not avalilabe in HuginPropagation");
      System.exit(0);
    }
    transformRelationsInJoinTree();
    jt.setLabels();
    instantiateEvidence( );
    initHuginMessages( );
  }
  upward(jt.elementAt(0),"yes");

  return jt.elementAt(0).getNodeRelation().getValues().totalPotential();
}

/**
 * @return the small relation containing the node passed as argument
 *
 * @param fs the node for which we want to find the small potential containing it 
 */

Relation locateSmallestTable(FiniteStates fs) {

  int i,j,s,s2;
  Relation R,nR;
  NodeJoinTree njt;
  NodeList vars;
  NeighbourTreeList ntl;
  NeighbourTree nt;

  // first we initialize the relation as big as possible

  R = new Relation();
  R.setVariables(network.getNodeList());

  // now we locate the smallest table

  s = jt.size();
  for(i=0; i<s; i++) {
    njt = jt.elementAt(i);
    vars = njt.getVariables();
    if (vars.getId(fs.getName())!=-1){ // fs is included in this clique
      nR = njt.getNodeRelation(); 
      if ((FiniteStates.getSize(nR.getVariables().toVector())) <=
            (FiniteStates.getSize(R.getVariables().toVector()))){
        R = nR;
      }
      // now we visit the separators
      ntl = njt.getNeighbourList();
      s2 = ntl.size();
      for(j=0; j<s2; j++){
        nt = ntl.elementAt(j);
        // the following comparison is to ensure that a separator is visited
        // only one time
        if (njt.getLabel() < nt.getNeighbour().getLabel()) {
          nR = nt.getMessage();
          if (nR.getVariables().getId(fs.getName()) != -1){
	    if (((int)FiniteStates.getSize(nR.getVariables().toVector())) <=
                ((int)FiniteStates.getSize(R.getVariables().toVector()))){
              R = nR;
            }
          }    
        }
      }
    }

    // if R contains the table for fs, then finish at this point
    if (((int)FiniteStates.getSize(R.getVariables().toVector())) ==
        fs.getNumStates())
      break;
  }  
  return R;
}


/**
 * Get the posterior distributions and store them in results
 */

public void getPosteriorDistributions( ){

  int i,s;
  FiniteStates fs;
  Relation R;
  Potential pot; // era PotentialTable
  NodeList nl;

  results = new Vector();
  s = interest.size();
  for (i=0; i<s; i++) {
    fs = (FiniteStates)interest.elementAt(i);
    R = locateSmallestTable(fs);
    pot = R.getValues(); // era (PotentialTable)
    if (R.getVariables().size() != 1){
      nl = new NodeList();
      nl.insertNode(fs);
      pot = pot.marginalizePotential(nl.toVector()); 
    }
    pot.normalize();
    results.addElement(pot);
  }



}

/**
 * propagate: performs a Hugin propagation
 *
 * @param root the node used as root for the upward and downward phases
 * @param divide "yes" if division are carried out in the upward phase and
 *        "no" in other case
 */

public void propagate(NodeJoinTree root,String divide){

  Date D;
  double time;


  jt.sortVariables(network.getNodeList());
  if (typeOfPotential.equals("tables")) jt.initTables(network);
  else if (typeOfPotential.equals("trees")) jt.initTrees(network);
  else {
    System.out.println(typeOfPotential + " is not avalilabe in HuginPropagation");
    System.exit(0);
  }
  transformRelationsInJoinTree();
  jt.setLabels();
  obtainInterest();


  System.out.println("Computing posterior distributions...");
  D = new Date();
  time = (double)D.getTime();
  

  if (observations.size() > 0) instantiateEvidence( );  
  
  initHuginMessages( );  

  upward(root,divide);
  downward(root);

  // calculation of the posterior distribution
  getPosteriorDistributions();
  
  // showing messages

  D = new Date();
  time = ((double)D.getTime() - time) / 1000;
  System.out.println("Posterior distributions computed.");
  System.out.println("Time (secs): " + time);

}


/**
 * Saves the result of a propagation to a file.
 * @param Name a String containing the file name.
 */

public void saveResults(String S) throws IOException {

  FileWriter F;
  PrintWriter P;
  Potential Pot; // era PotentialTable
  int i;

  F=new FileWriter(S);
  
  P=new PrintWriter(F);
  
  for (i=0 ; i<results.size() ; i++) {
    Pot = (Potential) results.elementAt(i); // era PotentialTable
    Pot.saveResult(P);
  }
  
  F.close();
}


/**
 * Transforms the initial relations of the joinTree if they
 * are potentialTrees. The only thing to do is the pruning of 
 * nodes which children are equals, so we use a smallest value
 * for limit.
 * This method can be overcharged for special requirements.
 * @ param r the relation to be transformed
 */

public void transformRelationsInJoinTree( ){
  int i,s;
  Relation r;

  if (typeOfPotential.equals("trees")){
    s = jt.size();
    for(i=0;i<s;i++){
      r = ((NodeJoinTree)jt.elementAt(i)).getNodeRelation();
      r = transformRelation(r);
    }
  }
}                      


/**
 * Transforms a relation if it is a PotentialTree.
 * The only thing to do is the pruning of 
 * nodes which children are equals, so we use a smallest value
 * for limit.
 * This method can be overcharged for special requirements.
 * @ param r the relation to be transformed
 */

public Relation transformRelation(Relation r){
  PotentialTree pot;

  if (typeOfPotential.equals("trees")){
    pot = (PotentialTree)r.getValues();
    pot.limitBound(0.0000000001);
    r.setValues(pot);
  }  
  return r;
}                      


/**
 * Transforms a potentialTree.
 * The only thing to do is to prune the 
 * nodes which children are equals, so we use a smallest value
 * for limit.
 * This method can be overcharged for special requirements.
 * IMPORTANT: the potential passed as parameter is modified
 * @ param pot the relation to be transformed
 */

public Potential transformPotentialAfterAddition(Potential pot){

  if (typeOfPotential.equals("trees")){
    ((PotentialTree)pot).limitBound(0.0000000001);
  }  
  return pot;
}                      


/**
 * Transforms a potentialTree.
 * This method can be overcharged for special requirements.
 * IMPORTANT: the potential passed as parameter is modified
 * @ param pot the relation to be transformed
 */

public Potential transformPotentialAfterCombination(Potential pot){

  if (typeOfPotential.equals("trees")){
    ((PotentialTree)pot).limitBound(0.0000000001);
  }  
  return pot;
}                      



} // end of class
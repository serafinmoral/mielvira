/* Naive_Bayes.java */

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
 * Naive_Bayes.java
 * 
 * This is a public class to learns a naive-Bayes classification model.
 * A naive-Bayes classifier assumes independence between the predictives
 * variables given the class.
 *
 * @author Rosa Blanco rosa@si.ehu.es UPV.
 * @version 0.2
 * @since 26/03/2003
 */

public class Naive_Bayes extends DiscreteClassifierDiscriminativeLearning {

  /**
   * Basic Constructor
   */
  public Naive_Bayes() {
    super();
  }

  /**
   * Constructor
   * @param DataBaseCases. cases. The input to learn a classifier
   * @param boolean correction. To apply the laplace correction
   */
  public Naive_Bayes(DataBaseCases data, boolean lap) throws elvira.InvalidEditException{
    super(data, lap);
  }

/*  public Naive_Bayes(DataBaseCases data, boolean lap, int classIndex) throws elvira.InvalidEditException{
    super(data, lap, classIndex);
  }
*/

  /**
   * This abstract method learns the classifier structure.
   * The naive-Bayes model has only arcs from the class variable
   * to the predictives variables, it assumes that the predicitives
   * variables are independents given the class 
   */
  public void structuralLearning() throws elvira.InvalidEditException{
    this.evaluations = 1; //There is not search
    
  	Vector      vector      = this.cases.getRelationList();
  	Relation    relation    = (Relation)vector.elementAt(0);
  	CaseListMem caselistmem = (CaseListMem)relation.getValues();

    Vector vectorNodes = new Vector();
    for(int i= 0; i< this.nVariables; i++)
      vectorNodes.add(this.cases.getVariables().elementAt(i).copy());

    NodeList nodeList  = new NodeList(vectorNodes);
    Node classVariable = nodeList.lastElement();
    String nameClass = classVariable.getName().concat(" ClassNode");
    classVariable.setTitle(nameClass);
    classVariable.setComment("ClassNode");
    
    Vector childrenLinks = new Vector();
    for(int i= 0; i< this.nVariables-1; i++) {
        childrenLinks.add(new Link(classVariable, nodeList.elementAt(i)));
    }
    LinkList childrenList = new LinkList();
    childrenList.setLinks(childrenLinks);
    nodeList.elementAt(this.nVariables-1).setChildren(childrenList);

    for(int i= 0; i< this.nVariables-1; i++) {
      Vector parentsLinks = new Vector();
      parentsLinks.add(new Link((FiniteStates)nodeList.elementAt(this.nVariables-1),(FiniteStates)nodeList.elementAt(i)));
      LinkList parentsList = new LinkList();
      parentsList.setLinks(parentsLinks);
      ((FiniteStates)nodeList.elementAt(i)).setParents(parentsList);
    }

    this.classifier = new Bnet();
    for(int i= 0; i< this.nVariables; i++) {
      this.classifier.addNode((FiniteStates)nodeList.elementAt(i));
      this.classifier.addRelation((FiniteStates)nodeList.elementAt(i));
    }
    this.classifier.setLinkList(childrenList);

    Vector defaultStates = new Vector();
    defaultStates.addElement(this.classifier.ABSENT);
    defaultStates.addElement(this.classifier.PRESENT);
    this.classifier.setFSDefaultStates(defaultStates);
    this.classifier.setName("classifier naive-Bayes");
    
   }
  
  
     public double[] computePosterior(Configuration instance) {
     int i,j,nv,size;
     double[] result;
     Relation rel;
     Potential pot;
         
         nv = classVar.getNumStates();
         result = new double[nv];
         
//         instance.remove(classVar);
         
         for(i=0;i<nv;i++) {result[i] = 1.0;}
         
         Vector rellist = classifier.getRelationList();
      
         size = rellist.size();
         for (i=0; i<size;i++) {
             rel =  (Relation) rellist.elementAt(i);
             
             pot =  rel.getValues();
             pot.restrictVariable(instance);
            
             for (j=0;j<nv;j++) {
               result[j] *= pot.getValue(j);  
             }
         
         }
         
         
         
         return(result);
     }
     

  /**
   * This method makes the factorization of the classifier
   * When naive-Bayes the factorization is made by means of 
   * the relative frecuencies given the class 
    
  public void parametricLearning(){ 
    Vector auxPotentialTables = new Vector(); 

    //Create a AuxiliarPotentialTable vector (one element for each node) in
    //order to caculate the potentials of the variables
    NodeList nodeList       = this.classifier.getNodeList(); 
    Vector vector           = this.cases.getRelationList(); 
    Relation relation       = (Relation)vector.elementAt(0); 
    CaseListMem caselistmem = (CaseListMem)relation.getValues(); 

    for(int i= 0; i< this.nVariables; i++) { 
      AuxiliarPotentialTable aux = new AuxiliarPotentialTable((FiniteStates)this.classifier.getNodeList().elementAt(i)); 
      //The table are initialized with random values of probability
      aux.initialize(0); 
      auxPotentialTables.add(aux); 
    } 

    for(int l= 0; l< this.nCases; l++) { 
      //The class haven't parents
      for(int i= 0; i< this.nVariables-1; i++) { 
        ((AuxiliarPotentialTable)auxPotentialTables.elementAt(i)).addCase((int)caselistmem.getValue(l,i), (int)caselistmem.getValue(l, this.nVariables-1), 1); 
      } 
    
      ((AuxiliarPotentialTable)auxPotentialTables.elementAt(this.nVariables-1)).addCase((int)caselistmem.getValue(l, this.nVariables-1), 0, 1); 
    } 
    
    //Save the learned potential table into the classifier
    Iterator relationListIterator       = this.classifier.getRelationList().iterator(); 
    Iterator auxPotentialTablesIterator = auxPotentialTables.iterator(); 

    for(int i= 0; relationListIterator.hasNext(); i++) { 
      Relation relationC                       = (Relation)relationListIterator.next(); 
      AuxiliarPotentialTable auxPotentialTable = (AuxiliarPotentialTable)auxPotentialTablesIterator.next(); 
      PotentialTable potentialTable            = (PotentialTable)relationC.getValues(); 

      if (this.laplace)  
        auxPotentialTable.applyLaplaceCorrection(); 
      potentialTable.setValues(auxPotentialTable.getPotentialTableCases()); 
    } 

    }
  */
  
  /**
   * Main to use the class from the command line
   */
  public static void main(String[] args) throws FileNotFoundException, IOException, elvira.InvalidEditException, elvira.parser.ParseException, Exception{
    //Comprobar argumentos
    if(args.length != 3) {
      System.out.println("Usage: file-train.dbc file-test.dbc file-out.elv");
      System.exit(0);
    }

    FileInputStream fi = new FileInputStream(args[0]);
    DataBaseCases   db = new DataBaseCases(fi);
    fi.close();

    Naive_Bayes clasificador = new Naive_Bayes(db, true);
    clasificador.train();

    System.out.println("Classifier learned");

    FileInputStream ft = new FileInputStream(args[1]);
    DataBaseCases   dt = new DataBaseCases(ft);
    ft.close();

    double accuracy = clasificador.test(dt);

    System.out.println("Classifier tested. Accuracy: " + accuracy);

    clasificador.getConfusionMatrix().print();

    FileWriter fo = new FileWriter(args[2]);
    clasificador.getClassifier().saveBnet(fo);
    fo.close();
  }
  
}//End of class


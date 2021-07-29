/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.potential.RPT.learningRPTs;

import elvira.Node;
import elvira.potential.RPT.RecursivePotentialTree;
import elvira.database.DataBaseCases;
import elvira.NodeList;
import elvira.potential.RPT.ListTreeNode;
import elvira.CaseListMem;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.potential.RPT.SplitTreeNode;

import java.util.ArrayList;
import java.util.Vector;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author cora
 */
public class LearningModel {

    private ArrayList<CPT_rpt> model;
    private NodeList variables;
    private DataBaseCases train;
  //  private DataBaseCases test;

    public LearningModel(DataBaseCases tr) {
        train = tr;
     //   test = ts;
        variables = tr.getVariables();
        model = new ArrayList<CPT_rpt>();
    }

    public void learn() throws IOException, FileNotFoundException {

        //Random seed = new Random();
        //Initialize the tree
        int size = variables.size();
        for (int i = 0; i < size; i++) {
            Node node = variables.getNodes().get(i);
            NodeList candidates = variables.duplicate();
            candidates.removeNode(node);
            this.addNewCPT(node, candidates);
        }

        LearningModel aux = this.duplicate();
        double LLH = aux.evaluateModel(train);
        //System.out.println("Initial LLH: "+LLH);
        double nextLLH = LLH;
        //Modify each CPT
        
    boolean salir = false;
    int k=0;
    while(!salir){
        salir = true;

//            System.out.println();
//            System.out.println("   ++++++    ++++++     ++++");
//            System.out.println("Iteration "+k);
//            System.out.println("   ++++++    ++++++     ++++");
//            System.out.println();

        for (int i = 0; i < size; i++) {
            boolean finished = false;
            
//            System.out.println();
//            System.out.println("   ++++++    ++++++     ++++");
//            System.out.println("CPT "+this.model.get(i).getHeadNode().getName());
//            System.out.println("   ++++++    ++++++     ++++");
//            System.out.println();

            NodeList auxCand = this.model.get(i).getCandidates();
            
            while(this.model.get(i).hasCandidates() && !finished){
                Node cand = this.model.get(i).getRandomNode();
//                System.out.println("cand chosen: "+cand.getName());

                if(this.isAllowed(this.model.get(i).getHeadNode(), cand)){
                    //this.getRPT().generateDotFile("nextTree.dot");

                    Random choice = new Random();
                    int option = choice.nextInt(3);

                    boolean partialConf = false;
                    boolean allLeaves = false;

                    if((option==0 || option==1) && (this.model.get(i).getNumberOfVarsInTree() > 2)){
                        System.out.println("Configuration");
                        this.addParentConf(i,cand);
//                        this.getRPT().generateDotFile("Conf.dot");
                        partialConf = true;
                    }else{
                        System.out.println("All leaves");
                        this.addParent(i, cand);
//                        System.out.println("Number of variables in CPT: "+this.model.get(i).getNumberOfVarsInTree());
//                        this.model.get(i).getRPT().print(3);
                        allLeaves = true;
                    }
                    
                    nextLLH = this.evaluateModel(train);
                   // System.out.println("Candidate LLH: " + nextLLH + " cand" +j + " named " +cand.getName());
                   System.out.println("Diferencia: " + (LLH - nextLLH));
                   // if( (LLH - nextLLH) > 29){
                    if( (nextLLH < LLH)){
                       // System.out.println("recover previous model, LLH "+nextLLH);
                        System.out.println("recover previous model");
//                        for(int w=0; w<model.size();w++){
                        aux.model.get(i).setCandidates(this.model.get(i).getCandidates());
//                        }
                       // aux.printCandidateLists();
                        this.model = aux.duplicateModel();
                        this.variables = aux.variables.copy();

                    }else{

                       //try to add the opposite link just in case it is better
                       boolean inverse = false;

                        if(this.isAllowed(cand, this.model.get(i).getHeadNode())){

                            LearningModel aux2 = aux.duplicate();
                            int index = aux2.model.indexOf(cand);

                            if(partialConf){
                                System.out.println("Configuration");
                                aux2.addParentConf(index,this.model.get(i).getHeadNode());
                            }else{
                                if(allLeaves){
                                    System.out.println("All leaves");
                                    aux2.addParent(index, this.model.get(i).getHeadNode());
                                }
                            }

                            //Ahora evaluar el modelo
                             double nextLLH2 = aux2.evaluateModel(train);

                              System.out.println("Inverso "+nextLLH2+ " y anterior: "+nextLLH);

                            if( (nextLLH2 > nextLLH)){
                                
                                System.out.println("CHANGE MODEL! por el inverso ");

                                aux.model = aux2.duplicateModel();
                                aux.variables = aux2.variables.copy();
                                LLH = nextLLH2;

                                //generar archivo .dot
//                                String name = "newModel";
//                                name = name+k+i;
//                                aux.getRPT().generateDotFile(name+".dot");
                                inverse = true;
                            }
                        }

                        if(!inverse){
                            System.out.println("CHANGE MODEL! ");
                            finished = true;
                            salir = false;
                            auxCand.removeNode(cand);
                           // auxCand.printNames();
                            //this.model.get(i).setCandidates(aux.model.get(i).getCandidates());
                            aux.model = this.duplicateModel();
                            aux.variables = this.variables.copy();
                            LLH = nextLLH;

                            //generar archivo .dot
//                            String name = "newModel";
//                            name = name+k+i;
//                            this.getRPT().generateDotFile(name+".dot");
                        }

                    }
                }
            }//end while
            this.model.get(i).setCandidates(auxCand);
        }//end for i
    k++;
    }//end bucle
  //      }

        //After adding parents, try to factorize
    aux = this.duplicate();
      boolean variation = true;
      while(variation){
        variation = false;
          for (int i = 0; i < size; i++) {
            
            
           // int son = ((SplitTreeNode)this.model.get(i).getRPT()).getNumberOfStates();
            //System.out.println("Try factorize cand: "+i+" of "+son+" states");
            //for(int j=0; j<son; j++){
                System.out.println("CPT to factorize "+i);
                this.model.get(i).factorize2();

                nextLLH = this.evaluateModel(train);

                double diferencia = (LLH - nextLLH);
                System.out.println("Diferencia: " + diferencia);

                if((nextLLH <= LLH) || (diferencia == Double.NaN)){
                    System.out.println("recover previous model, LLH "+nextLLH);
                    this.model = aux.duplicateModel();
                    this.variables = aux.variables.copy();
                }else{
                    System.out.println("CHANGE MODEL! , LLH "+nextLLH);
                    aux.model = this.duplicateModel();
                    aux.variables = this.variables.copy();
                    LLH = nextLLH;
                    variation = true;
                    String name = "newModelFact";
                    name = name+i;
                    this.getRPT().generateDotFile(name+".dot");
                }
        //  }

            }//end for i
        }

      this.testCPTs();

    }

   public void learnGreedy() throws IOException, FileNotFoundException {

        //Random seed = new Random();
        //Initialize the tree
        int size = variables.size();
        for (int i = 0; i < size; i++) {
            Node node = variables.getNodes().get(i);
            NodeList candidates = variables.duplicate();
            candidates.removeNode(node);
            this.addNewCPT(node, candidates);
        }

        this.testCPTs();

        LearningModel aux = this.duplicate();
        double LLH = aux.evaluateModel(train);
        //System.out.println("Initial LLH: "+LLH);
        double nextLLH = LLH;
        //Modify each CPT

    boolean salir = false;
    int k=0;
    while(!salir){
        salir = true;

//            System.out.println();
//            System.out.println("   ++++++    ++++++     ++++");
//            System.out.println("Iteration "+k);
//            System.out.println("   ++++++    ++++++     ++++");
//            System.out.println();

        for (int i = (size-1); i >=0; i--) {
           // boolean finished = false;

//            System.out.println();
//            System.out.println("   ++++++    ++++++     ++++");
//            System.out.println("CPT "+this.model.get(i).getHeadNode().getName());
//            System.out.println("   ++++++    ++++++     ++++");
//            System.out.println();

            NodeList auxCand = this.model.get(i).getCandidates();

            while(this.model.get(i).hasCandidates()){
                Node cand = this.model.get(i).getRandomNode();
//                System.out.println("cand chosen: "+cand.getName());

                if(this.isAllowed(this.model.get(i).getHeadNode(), cand)){
                    //this.getRPT().generateDotFile("nextTree.dot");

                    Random choice = new Random();
                    int option = choice.nextInt(3);
                    //option = 2;

                    boolean partialConf = false;
                    boolean allLeaves = false;

                    if((option==0 || option==1) && (this.model.get(i).getNumberOfVarsInTree() > 2)){
//                        System.out.println("By Configuration");
                        this.addParentConf(i,cand);
//                        this.getRPT().generateDotFile("Conf.dot");
                        partialConf = true;
                    }else{
                        //System.out.println("All leaves");
                    this.addParent(i, cand);
                    allLeaves = true;

//                    System.out.println("Number of variables in CPT: "+this.model.get(i).getNumberOfVarsInTree());
                    //this.model.get(i).getRPT().print(3);
                    }

                    nextLLH = this.evaluateModel(train);
                   // System.out.println("Candidate LLH: " + nextLLH + " cand" +j + " named " +cand.getName());
                   //System.out.println("Diferencia: " + (LLH - nextLLH));
                    if((nextLLH < LLH) || (new Double(nextLLH)).isNaN()){
                       // System.out.println("recover previous model, LLH "+nextLLH);
                        //System.out.println("recover previous model");
//                        for(int w=0; w<model.size();w++){
                        aux.model.get(i).setCandidates(this.model.get(i).getCandidates());
//                        }
                       // aux.printCandidateLists();
                        this.model = aux.duplicateModel();
                        this.variables = aux.variables.copy();

                    }else{
//                        System.out.println("CHANGE MODEL! ");
//                        //finished = true;
//                        salir = false;
//                        auxCand.removeNode(cand);
//                       // auxCand.printNames();
//                       // this.model.get(i).setCandidates(aux.model.get(i).getCandidates());
//                        aux.model = this.duplicateModel();
//                        aux.variables = this.variables.copy();
//                        LLH = nextLLH;
//                        String name = "newModel";
//                        name = name+k+i;
//                        this.getRPT().generateDotFile(name+".dot");


                      //try to add the opposite link just in case it is better
                       boolean inverse = false;
                        LearningModel aux2 = aux.duplicate();
                        if(aux2.isAllowed(cand, this.model.get(i).getHeadNode())){


                            //System.out.println("ANALIZAMOS EL INVERSO");
                            
                            int index = aux2.getIndexOf(cand);
                            //System.out.println("Index... "+index);

                            if(partialConf){
                                //System.out.println("Configuration");
                                aux2.addParentConf(index,this.model.get(i).getHeadNode());
                            }else{
                                if(allLeaves){
                                    //System.out.println("All leaves");
                                    aux2.addParent(index, this.model.get(i).getHeadNode());
                                }
                            }

                            //Ahora evaluar el modelo
                             double nextLLH2 = aux2.evaluateModel(train);
                            //System.out.println("Inverso "+nextLLH2+ " y anterior: "+nextLLH);
                            // System.out.println("LLH del inverso "+nextLLH2);
                            if( (nextLLH2 > nextLLH)){
                                // System.out.println("recover previous model, LLH "+nextLLH);
                                //System.out.println("CHANGE MODEL! por el inverso ");

                                aux.model = aux2.duplicateModel();
                                aux.variables = aux2.variables.copy();
                                LLH = nextLLH2;

                                //generar archivo .dot
//                                String name = "newModel";
//                                name = name+k+i;
//                                aux.getRPT().generateDotFile(name+".dot");
                                inverse = true;
                            }
                        }

                        if(!inverse){
                           // System.out.println("CHANGE MODEL! ");
                            salir = false;
                            auxCand.removeNode(cand);
                           // auxCand.printNames();
                            //this.model.get(i).setCandidates(aux.model.get(i).getCandidates());
                            aux.model = this.duplicateModel();
                            aux.variables = this.variables.copy();
                            LLH = nextLLH;

                            //generar archivo .dot
//                            String name = "newModel";
//                            name = name+k+i;
//                            this.getRPT().generateDotFile(name+".dot");
                        }

// ****
// ****

                    }
                }
            }//end while
            this.model.get(i).setCandidates(auxCand);
        }//end for i
    k++;
    }//end bucle
  //      }

//        //After adding parents, try to factorize
    aux = this.duplicate();
      boolean variation = true;
      while(variation){
        variation = false;
          for (int i = 0; i < size; i++) {

          if(!this.model.get(i).isFactorized()){
           // int son = ((SplitTreeNode)this.model.get(i).getRPT()).getNumberOfStates();
            //System.out.println("Try factorize cand: "+i+" of "+son+" states");
            //for(int j=0; j<son; j++){
                //System.out.println("CPT to factorize "+i);
                this.model.get(i).factorize3();
                nextLLH = this.evaluateModel(train);
                double diferencia = (LLH - nextLLH);
                //System.out.println("Diferencia: " + diferencia);
              //  System.out.println("datos: "+nextLLH+" <=?"+LLH+" or "+ nextLLH + " ==? Double.NaN"+((nextLLH <= LLH) || (nextLLH == Double.NaN)) );
                    if((nextLLH <= LLH) || (new Double(nextLLH)).isNaN()){
                       // System.out.println("recover previous model, LLH "+nextLLH);
                        //System.out.println("recover previous model");
//                        for(int w=0; w<model.size();w++){
                        //aux.model.get(i).setCandidates(this.model.get(i).getCandidates());
//                        }
                       // aux.printCandidateLists();
                        this.model = aux.duplicateModel();
                        this.variables = aux.variables.copy();

                    }else{
                        //System.out.println("CHANGE MODEL! ");
                        aux.model = this.duplicateModel();
                        aux.variables = this.variables.copy();
                        LLH = nextLLH;
                        variation = true;
//                    String name = "newModelFact";
//                    name = name+i;
//                    this.getRPT().generateDotFile(name+".dot");
                }

          }

            }//end for i
        }

      //this.testCPTs();

    }


    private boolean isAllowed(Node headVar, Node parent){
        boolean result =  this.retrieveDescendants(headVar).contains(parent);

        if(result){
            int index = this.variables.getId(headVar);
            this.model.get(index).removeNodeFromCandidates(parent);
            //System.out.println("Not allowed");
        }

        return !result;
    }

    private Vector<Node> retrieveAncestors(Node headNode) {

        //identify CPT for the headNode and get its parents
        int index = this.variables.getId(headNode);
        NodeList parentHead = this.model.get(index).getParentsCopy();

        //now get the ancestors of the parents
        if (parentHead != null) {
            //current set of parents
            Vector<Node> parents = parentHead.getNodes();
            //global set of parents
            Vector<Node> allparents = new Vector<Node>();
            allparents.addAll((Vector<Node>) parents.clone());
            Vector<Node> auxparents = new Vector<Node>();

            boolean newparent = true;

            while (newparent) {
                newparent = false;
            //while (parents.size()>0) {
                int size = parents.size();
               // System.out.println("Number of parents: " + size);
                auxparents = new Vector<Node>();

                for (int i = 0; i < size; i++) {
                    //get index of the parent
                    index = this.variables.getId(parents.get(i));
                    //get parents of the parent
                    NodeList aux = this.model.get(index).getParentsCopy();
                    if (aux != null) {
                        auxparents.addAll(aux.getNodes());
                        newparent=true;
                    }
                }

                if (auxparents.size()>0) {
                    allparents.addAll((Vector<Node>) auxparents.clone());
                    //System.out.println("auxparents has vars");
                    parents = new Vector<Node>(auxparents);
                } else {
                    parents = new Vector<Node>();
                }

            }//end while

//            System.out.println("lista de padres encontrados:");
//            for (int i = 0; i < allparents.size(); i++) {
//                System.out.println(allparents.get(i).getName());
//            }

//            System.out.println("+++++++++++++++++++++++++++");
            return allparents;
        } else {
//            System.out.println("No parents for the variable");
            return null;
        }
    }

    private Vector<Node> retrieveDescendants(Node headNode) {

        //Buscar en todas las listas de padres a ver donde está headNode
        int size = this.model.size();
        Vector<Node> descendants = new Vector<Node>();
        for(int i=0; i< size; i++){
            if(this.model.get(i).containsParent(headNode)){
                descendants.add(this.model.get(i).getHeadNode());
            }
        }


        boolean newparents = true;
        int size2 = descendants.size();
        Vector<Node> aux2 = new Vector<Node>(descendants);

        while(newparents){
            newparents = false;
            Vector<Node> aux = new Vector<Node>();
            for(int j=0; j< size2; j++){
                for(int i=0; i< size; i++){
                    if(this.model.get(i).containsParent(aux2.get(j))){
                        aux.add(this.model.get(i).getHeadNode());
//                        System.out.println("descendant found!");
                        newparents = true;
                    }
                }
            }
            aux2.clear();
            aux2.addAll(aux);
            size2 = aux.size();
            descendants.addAll(aux);
        //System.out.println("looking for descendants...");
        }

//        System.out.println("END descendants");

//                    System.out.println("lista de descendientes encontrados:");
//            for (int i = 0; i < descendants.size(); i++) {
//                System.out.println(descendants.get(i).getName());
//            }
//
//            System.out.println("+++++++++++++++++++++++++++");
        return descendants;
    }

//    private void updateCandidates(Node headNode, Node parent) {
//
//        int position = variables.getId(parent);
//        this.model.get(position).removeNodeFromCandidates(headNode);
//        //descendants of headNode cannot be parents of parent
//
//        //retrieve descendants of headNode
//        Vector<Node> descendants = this.retrieveDescendants(headNode);
//
//        //delete them from parent's list of candidates
//        for(int i=0; i< descendants.size(); i++){
//            this.model.get(position).removeNodeFromCandidates(descendants.get(i));
//        }
//
//        //retrieve ancestors
//        Vector<Node> ancestors = this.retrieveAncestors(parent);
//
//        if(ancestors != null){
////                    System.out.println("Ancestors retrieved:");
////            for (int i = 0; i < ancestors.size(); i++) {
////                System.out.println(ancestors.get(i).getName());
////            }
//
////            System.out.println("+++++++++++++++++++++++++++");
//
//        //remove parent from candidate lists of all ancestors
//        int size = ancestors.size();
//        for (int i = 0; i < size; i++) {
//            position = variables.getId(ancestors.get(i));
//            this.model.get(position).removeNodeFromCandidates(headNode);
//        }
//        }
//    }

    /**
     * with penalty term
     * 
     * @param dataset
     * @return
     */
    public double evaluateModel(DataBaseCases dataset) {

        CaseListMem clm = dataset.getCaseListMem();
        Vector<Configuration> clmvect = new Vector<Configuration>();
        int sizeConf = clm.getNumberOfCases();
        for (int i = 0; i < sizeConf; i++) {
            clmvect.add(clm.get(i));
        }
        double likelihoodTree = this.getRPT().evaluateSetOfConfigurations(clmvect);
        //newTree.print(0);
//        System.out.println("Current RPT Score: " + likelihoodTree);

        return likelihoodTree;
    }

    /**
     * without penalty term
     *
     * @param dataset
     * @return
     */
    public double evaluateModelLLH(DataBaseCases dataset) {

        CaseListMem clm = dataset.getCaseListMem();
        Vector<Configuration> clmvect = new Vector<Configuration>();
        int sizeConf = clm.getNumberOfCases();
        for (int i = 0; i < sizeConf; i++) {
            clmvect.add(clm.get(i));
        }
        double likelihoodTree = this.getRPT().evaluateSetOfConfigurationsLLH(clmvect);
        //newTree.print(0);
        //System.out.println("Current RPT Score: " + likelihoodTree);

        return likelihoodTree;
    }

    private void addNewCPT(Node node, NodeList candidates) {
        model.add(new CPT_rpt(node, candidates, train));
    }

    private void addParent(int index, Node cand) {

        this.model.get(index).addParent(cand);
    }

    private void addParentConf(int index, Node cand) {

        this.model.get(index).addParentConf(cand);
    }

    public RecursivePotentialTree getRPT() {
        ListTreeNode root = new ListTreeNode();

        int size = this.model.size();
        for (int i = 0; i < size; i++) {
            root.addChild(((CPT_rpt) this.model.get(i)).getRPT());
        }

        RecursivePotentialTree result = new RecursivePotentialTree();
        result.setValues(root);
        return result;
    }

    public void printCandidateLists() {
        int size = this.model.size();
        for (int i = 0; i < size; i++) {
            this.model.get(i).printCandidateList();
        }
    }

    protected LearningModel duplicate(){
        LearningModel aux = new LearningModel(train);

        aux.variables = this.variables.duplicate();
        aux.model = this.duplicateModel();

        return aux;

    }

    private ArrayList<CPT_rpt> duplicateModel(){
        ArrayList<CPT_rpt> aux = new ArrayList<CPT_rpt>();
        int size = this.model.size();

        for(int i=0; i<size; i++){
            aux.add(this.model.get(i).duplicate());
        }

        return aux;
    }

    public void testCPTs(){
        int size = this.model.size();
        for(int i=0; i<size;i++){
            Vector<Node> vars = new Vector<Node>();
            vars.addAll(this.model.get(i).getRPT().getVariables());
            vars.remove(this.model.get(i).getHeadNode());
            Configuration parents = new Configuration(vars);
            int numConfParents = parents.possibleValues();

            int statesHead = ((FiniteStates)this.model.get(i).getHeadNode()).getNumStates();
            for(int j=0; j<numConfParents; j++){
                double suma = 0;
                for(int k=0; k< statesHead; k++){
                    parents.putValue(((FiniteStates)this.model.get(i).getHeadNode()), k);
                    suma+=this.model.get(i).getRPT().getValue(parents);
                }

//                parents.pPrint();
//                System.out.println();
                if(suma<0.9 || suma > 1)System.out.println("Suma de configuracion de los padres: "+suma);
                parents.remove(((FiniteStates)this.model.get(i).getHeadNode()));
                parents.nextConfiguration();
            }


        }

//        System.out.println("Y el total del potencial: "+this.getRPT().totalPotential());
    }


    private int getIndexOf(Node head){
        int size = this.model.size();

        for(int i=0; i<size; i++){
            if(this.model.get(i).getHeadNode().getName() == head.getName()){
                return i;
            }
        }

        return -1;
    }
}

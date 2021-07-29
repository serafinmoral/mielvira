/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;

import elvira.Configuration;
import elvira.Node;
import elvira.potential.Potential;
import elvira.potential.PotentialTable;
import elvira.potential.ProbabilityTree;
import elvira.potential.PotentialTree;
import elvira.FiniteStates;
import elvira.NodeList;
import elvira.Bnet;
import elvira.Relation;
import java.util.Vector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.io.Serializable;

/**
 *
 * @author mgomez
 */
public class RecursivePotentialTree extends Potential implements Serializable{
    static final long serialVersionUID = 3955066704255756705L;
    
    /**
     * The values of the potential will be a TreeNode
     */
    private TreeNode values;

    /**
     * Default constructor
     */
    public RecursivePotentialTree() {
        super();
    }

    /**
     * Copies this potential.
     * @return a copy of this <code>RecursivePotentialTree</code>.
     */
    public Potential copy() {

        RecursivePotentialTree pot;

        pot = new RecursivePotentialTree();
        pot.setVariables(this.getVariables());
        pot.values = values.copy();

        return pot;
    }

    public void setRandomRPT(Vector<Node> variables, int niveles, double probSplit, double probPotential){

        Vector<Node> vars = (Vector<Node>) variables.clone();
        TreeNode root = this.createRandomRPT(vars, niveles, probSplit, probPotential, true);

        HashSet<Node> finalVars = root.getVariables();
        Vector<Node> intersection = (Vector<Node>) variables.clone();

        if(intersection!=null){
             if(finalVars!=null){
                intersection.removeAll(finalVars);
             }

            if(intersection.size()>0){
//                System.out.println("añadimos potential de "+intersection.size());

//                for(int i=0; i<intersection.size();i++){
//                    System.out.println("int: "+intersection.get(i).getName());
//                }
//
//                System.out.println("------------");
                
                ListTreeNode newroot = new ListTreeNode();
                newroot.addChild(root);

                Configuration conf = new Configuration(intersection);
                int possibleValues = conf.possibleValues();
                Potential pot = new PotentialTree(intersection);
                Random generator = new Random();

                for(int i=0; i<possibleValues; i++){
                    double value = generator.nextDouble();
                    pot.setValue(conf, value);
                    conf.nextConfiguration();
                }
                TreeNode newPot = new PotentialTreeNode(pot);
                newroot.addChild(newPot);
                this.setValues(newroot);
            }
            else{
                this.setValues(root);
            }
        }else{
            this.setValues(root);
        }

        
    }

    /**
     * modifies this tree to get it normalized as a conditioned probability distribution
     * 
     * @param variables contains the variables of this tree. The first variable in the
     * vector is the variable of the node, and the rest are its parents.
     */
    public void normalizeToConditional(Vector<Node> variables){
        Vector<Node> vars = (Vector<Node>) variables.clone();
        vars.remove(0);
        Configuration conf = new Configuration(vars);
//        System.out.println("crear split chain de "+vars.size()+" elementos.");
        TreeNode chain = elvira.potential.RPT.Util.createSplitChain(vars);
//        System.out.println("creado split chain");

        for(int i=0; i<conf.possibleValues(); i++){
            Configuration confaux = conf.duplicate();
            elvira.potential.RPT.Util.updateSplitChainLeaf(chain, confaux, new ValueTreeNode( 1/this.getValue(conf)));
            conf.nextConfiguration();
        }

//        System.out.println("añadidos valores a split chain");
        TreeNode newRoot = new ListTreeNode();
        ((ListTreeNode)newRoot).addChild(values);
        ((ListTreeNode)newRoot).addChild(chain);
        this.setValues(newRoot);
    }


    /**
     * modifies this tree to get it normalized as a conditioned probability distribution
     * 
     * @param variables contains the variables of this tree. The first variable in the
     * vector is the variable of the node, and the rest are its parents.
     */
    public void normalizeToConditional2(Vector<Node> variables){
        Node var = variables.get(0);
        Vector<Node> nodeVar = new Vector<Node>();
        nodeVar.add(var);
        Vector<Node> nodeVar2 = (Vector<Node>) variables.clone();
        nodeVar2.remove(0);

        PotentialTable table = this.RPTtoPotentialTable();
        Configuration confNodo = new Configuration(nodeVar);
        Configuration confPadres = new Configuration(nodeVar2);
        Configuration conjunta = new Configuration(variables);
        

        for(int i=0; i<confNodo.possibleValues(); i++){
            double sum = 0;
            for(int j=0; j<confPadres.possibleValues();j++){
                conjunta.setValues(confNodo, confPadres);
                sum += table.getValue(conjunta);
                confPadres.nextConfiguration();
            }

            confPadres = new Configuration(nodeVar2);
            conjunta = new Configuration(variables);

            for(int j=0; j<confPadres.possibleValues();j++){
                conjunta.setValues(confNodo, confPadres);
                table.setValue(conjunta,(table.getValue(conjunta)/sum));
                confPadres.nextConfiguration();
            }
            confNodo.nextConfiguration();
        }

        conjunta = new Configuration(variables);
        for(int k=0; k<conjunta.possibleValues(); k++){
            double valor = table.getValue(conjunta);
            if(this.getValue(conjunta)!= valor){
                //cambiar
                //this.setValue(conjunta,valor);
            }
            conjunta.nextConfiguration();
        }
    }    
    

    public TreeNode getRoot(){
        return this.values;
    }

    public void changeTablesIntoTrees(){
        this.values. changeTablesIntoTrees();
    }

    /**
     * Method for setting the root of the recursive potential tree
     * @param values
     */
    public void setValues(TreeNode values) {
        this.values = values;
        this.values.setParent(null);
    }

    /**
     * Method for setting a value for a given configuration
     * @param conf
     * @param value
     */
    public void setValue(Configuration conf, double value) {
        //????
    }

    /**
     * Method for checking if values is equals to a certain
     * treeNode passed as argument
     * @param treeNode
     */
    public boolean checkValuesEquals(TreeNode treeNode){
        return (values==treeNode);
    }

    /**
     * Method for returning the variable related to the root node.
     * In this case a Vector with all the variables involved in the potential
     * will be returned
     */
    public Vector getVariables() {
        // IF variables is null, it is needed to gather the information
        // about the variables
        if (super.getVariables() == null){   
            // It is needed to colect the set of variables
            // from the childs
//System.out.println("Collecting vars for root node");
            HashSet<Node> vars=values.getVariables();
            // Now add the content of vars to variables
            Vector<Node> collectedVars=new Vector<Node>();
            if(vars!=null){
                collectedVars.addAll(vars);
            }
            setVariables(collectedVars);
        }

        // If this has been computed before, return the vector
        return super.getVariables();
    }

    /**
     * Method for returning the variable related to the root node.
     * In this case a Vector with all the variables involved in the potential
     * will be returned
     */
    public void refreshVariables() {

        HashSet<Node> vars=values.getVariables();
        Vector<Node> collectedVars=new Vector<Node>();
        if(vars!=null){
                collectedVars.addAll(vars);
            }
        if(collectedVars!=null){
            super.setVariables(collectedVars);
        }
    }

    /**
     * Method for printing the content of the tree
     */
    public void print(int spaces){
        // Print the list of vars and call print method
        // on the root
        System.out.println("---------- Recursive Potential Tree ------------");
        System.out.println("Valores null: "+(values == null));
        values.print(spaces);
        System.out.println("------------------------------------------------");
    }

    /**
     * Method for printing the content of the tree
     */
    public void print(){
        // Print the list of vars and call print method
        // on the root
        System.out.println("---------- Recursive Potential Tree ------------");
         System.out.println("Variables:");
         this.refreshVariables();
        for(int i=0; i<this.getVariables().size();i++){
            System.out.println(((Node)this.getVariables().get(i)).getName());
        }

        System.out.println("Valores null: "+(values == null));
        values.print(0);
        System.out.println("------------------------------------------------");
    }

    /**
     * Combines (multiplies) this potential with the one given in the argument.
     *
     * @param pot
     * @return
     */
    @Override
    public Potential combine(Potential pot) {
        RecursivePotentialTree result=new RecursivePotentialTree();
        ListTreeNode root=new ListTreeNode();
        root.addChild(values.copy());
        if(pot.getClass()==RecursivePotentialTree.class){
            root.addChild(((RecursivePotentialTree)pot).values.copy());
        }
        else{
            PotentialTreeNode potNode = new PotentialTreeNode(pot.copy());
            root.addChild(potNode);
        }
        result.setValues(root);
        result.refreshVariables();
        return result;
    }

    /**
     * deletes the variable given as an argument by summing it out
     * @param var
     * @return
     */
    @Override
    public Potential addVariable(Node var) {

//        System.out.println(" add variable   "+var.getName());
//        System.out.println(" de: ");
//        this.print(0);
//        System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXX  ");


        RecursivePotentialTree result;
        //Hay que comprobar si la raiz de this es una lista, y si lo es
        //entonces se divide. Si la raiz es un split o un potencial, no
        //hace falta hacer nada previamente
        Class argument = values.getClass();
        if(argument == ListTreeNode.class){
            //System.out.println("Root is a list");
            ListTreeNode root = new ListTreeNode();
            ListTreeNode with = new ListTreeNode();
            ListTreeNode without = new ListTreeNode();
            root.addChild(with);
            root.addChild(without);
            ((ListTreeNode)this.values).divideTree(var,root);

            TreeNode marginalizedNode = with.addVariable(var);

//            System.out.println("Nodo marginalizado");
//            marginalizedNode.print(0);
//            System.out.println("END Nodo marginalizado");

            ListTreeNode finalRoot = new ListTreeNode();
            
            finalRoot.addChild(marginalizedNode);

            result=new RecursivePotentialTree();
            switch(without.getNumberOfChildren()){
                case 1:
                    finalRoot.addChild(without.getChild(0));
                    result.setValues(finalRoot);
                    break;
                case 0:
                    result.setValues(marginalizedNode);
                    break;
                default:
                    finalRoot.addChild(without);
                    result.setValues(finalRoot);
                    break;
            }      
            
        }
        else{
        result=new RecursivePotentialTree();
        result.setValues(values.addVariable(var));
        }

        if((result.values.getClass() == ListTreeNode.class)&&(result.getVariables().size()==1)){
            result.values = ((ListTreeNode)result.values).combineSons();
        }

        if(result.getVariables().size()<=0){
             System.out.println(" VARIABLE ELIMINADA!   *****    add variable   "+var.getName());
        }

//        for(int i=0; i< result.getVariables().size();i++)
//            System.out.println(((FiniteStates)result.getVariables().get(i)).getName());

        result.refreshVariables();
        return result;
    }

    /**
     * restricts this potential to the configuration given as an argument
     * 
     * @param conf
     * @return
     */
    @Override
    public Potential restrictVariable(Configuration conf) {
        RecursivePotentialTree result=new RecursivePotentialTree();
        result.setValues(values.restrictVariable(conf));
        result.refreshVariables();
        return result;
    }
    
    /**
     * DO NOT USE. For testing purpose
     *
     * computes the recursive tree to get the value it represents. This must be used only over 
     * a recursive tree with no variables inside it, for example the result of 
     * a restriction over a configuration of all the variables in the potential
     *
     * Only works if th Potential Nodes in the tree are Potential Tables
     * 
     * @return the value represented in the tree
     */
    private double toValue(){
        return this.values.toValue();
    }

    /**
     * do not use. For testing purpose
     *
     * @return
     */
    public PotentialTable getPotentialTable(){

        Vector<Node> variables = this.getVariables();
        Configuration conf = new Configuration(variables);
        PotentialTable pot = new PotentialTable(variables);
        for(int i=0; i< conf.possibleValues();i++){
            Potential restricted = this.restrictVariable(conf);
//            System.out.println("Restringido...");
//            ((RecursivePotentialTree)restricted).print(0);
//            System.out.println("...");
            double value = ((RecursivePotentialTree)restricted).toValue();
            pot.setValue(conf,value);
            conf.nextConfiguration();
        }
        return pot;
    }

    public long getSize(){
        long size;
        size = this.values.getTreeSize();
        return size;
    }

    public long getNumberOfNodes(){
        long size;
        size = this.values.getNumberOfNodes();
        return size;
    }

    /**
     * returns the value of the potential for a given configuration. If the configuration
     * does not contains all the variables of the potential, it returns the sum of the potential
     * for all the possible configurations of the variables not in conf
     *
     * @param conf
     * @return
     */
    public double getValue(Configuration conf){

        Vector<Node> variables = this.getVariables();
        NodeList totalVars = new NodeList(variables);
        NodeList parents = new NodeList(conf.getVariables());
        NodeList difference = totalVars.difference(parents);

        if(difference.getSize()>0){
            double value = 0;
            Configuration confRemainingVars = new Configuration(difference);
            for(int j=0; j<confRemainingVars.possibleValues();j++){
                value += this.values.getValue(new Configuration(conf, confRemainingVars, totalVars));
                confRemainingVars.nextConfiguration();
            }
            return value;
        }
        else{
            return this.values.getValue(conf);
        }
    }

    public double totalPotential(){
        return this.values.getTotalPotential();
    }

    public void normalize(){

        double totalPot = this.totalPotential();
        ValueTreeNode norm = new ValueTreeNode(1/totalPot);
        if(this.values.getClass()==ListTreeNode.class){
            ((ListTreeNode)this.values).addChild(norm);
        }
        else{
            ListTreeNode newroot = new ListTreeNode();
            newroot.addChild(this.values);
            newroot.addChild(norm);
            this.setValues(newroot);
        }
    }

    public PotentialTree RPTtoPotentialTree(){

        ProbabilityTree aux = new ProbabilityTree();
        aux = this.values.RPTtoPotentialTree();
        PotentialTree result = new PotentialTree(aux);
        return result;
    }

    public PotentialTable RPTtoPotentialTable(){
        this.refreshVariables();
        HashSet<Node> vars = this.values.getVariables();
        if(vars == null) System.out.println("VARIABLES A NULL");

//        System.out.println("*** Variables en el potencial: "+vars.size());
        Vector<Node> variables = new Vector<Node>();
        variables.addAll(vars);
//        System.out.println("--- VAriables en el potencial: "+variables.size());
        PotentialTable result = new PotentialTable(variables);
        Configuration conf = new Configuration(variables);
        int possibleVals = conf.possibleValues();
        for(int i=0; i<possibleVals; i++){
            result.setValue(conf, this.getValue(conf));
            conf.nextConfiguration();
        }
        return result;
    }    
    
    public int poisson(){
        Random generator = new Random();
        Vector<Double> poisson = new Vector<Double>();
        poisson.add(0.3639);
        poisson.add(0.1839);
        poisson.add(0.0613);
//        poisson.add(0.0153);
//        poisson.add(0.0031);
//        poisson.add(0.0005);
        double semilla = generator.nextDouble();

        if(semilla >= poisson.get(0)){
            return 2;
        }
        if(semilla >= poisson.get(1) && semilla < poisson.get(0)){
            return 3;
        }
        if(semilla >= poisson.get(2) && semilla < poisson.get(1)){
            return 4;
        }
//        if(semilla >= poisson.get(3) && semilla < poisson.get(2)){
//            return 5;
//        }
//        if(semilla >= poisson.get(4) && semilla < poisson.get(3)){
//            return 6;
//        }
//        if(semilla >= poisson.get(5) && semilla < poisson.get(4)){
//            return 7;
//        }
//        if(semilla >= 0 && semilla < poisson.get(5)){
//            return 8;
//        }
        return 5;
    }

    /**
     * Simulates a sample of size n. The tree must be normalised.
     *
     * @return a vector of configurations.
     */

//    public Vector<Configuration> simulateSample(int n) {
//
//        Vector<Configuration> sample = new Vector<Configuration>();
//        int i, j, s;
//        NodeList v;
//        Configuration conf;
//        double acum = 0.0, r;
//
//        v = new NodeList(this.getVariables());
//
//        s = (int)v.getSize();
//
//        for (i=0 ; i<n ; i++) {
//            conf = new Configuration(v);
//            acum = 0.0;
//            r = Math.random();
//            for (j=0 ; j<s ; j++) {
//                acum += this.getValue(conf);
//                if (acum >= r)
//                    break;
//                else conf.nextConfiguration();
//            }
//            sample.addElement(conf);
//        }
//
//
//        return sample;
//    }

    public Bnet generateBnet(NodeList structure, double probProp, double probPot ){

        Vector<Relation> relations = new Vector<Relation>();
        Vector<Node> variables = new Vector<Node>();

        for(int i=0; i<structure.size(); i++){

            variables.add(structure.elementAt(i));
            NodeList parents = structure.elementAt(i).getParentNodes();
            variables.addAll(parents.getNodes());


//            for(int l=0; l<variables.size();l++)
//                    System.out.println(variables.get(l).getName());
//
//            System.out.println(" ****************************** ");

            RecursivePotentialTree relation = new RecursivePotentialTree();
            Vector<Node> vars = (Vector<Node>) variables.clone();
//            System.out.println("Variables al comienzo: "+vars.size());
//            for(int l=0; l<vars.size();l++)
//                    System.out.println(vars.get(l).getName());
            relation.setRandomRPT(vars, vars.size(), probProp, probPot);
//            relation.refreshVariables();
            relation.setVariables(variables);
//            System.out.println("Variables al final: "+relation.getVariables().size());
//            for(int l=0; l<relation.getVariables().size();l++)
//                    System.out.println(((Node)relation.getVariables().get(l)).getName());



//            System.out.println("random rpt creado de "+relation.getVariables().size()+" que debe ser igual a "+variables.size());

            if(parents.size()>0){
//                System.out.println("Normalizamos a condicional");
                relation.normalizeToConditional(variables);
            }else{
//                System.out.println("Normalizamos tradicional");
                relation.normalize();
            }
            relations.add(new Relation(relation));
//            for(int l=0; l<variables.size();l++){
//                    System.out.println(relations.get(i).getVariables().getNodes().get(l).getName());
//            }
            variables.clear();
//            System.out.println(" ******************************   *************************************  ");
//            System.out.println("Variables en la relacion definitiva: "+relations.get(i).getVariables().size());

        }
//        System.out.println("CREADAS TODAS LAS RELACIONES");

        //(NodeList structure y Vector<Relation> relations)
//        System.out.println("NUMERO DE RELACIONES: "+relations.size());

        Bnet net = new Bnet(structure);
        net.setRelationList(relations);
        return net;

    }


/**
 * GENERAR RPT ALEATORIO
 */
   private TreeNode createRandomRPT(Vector<Node> variables, int depth, double probSplit, double probPotential, boolean isFirst){
        Random generator = new Random();
        TreeNode root;
        if(variables.size()>=1){
            if(depth > 1){ //crear nodo interno
                depth--;
                if(variables.size()>=5){
                    double semilla = generator.nextDouble();
                    if(semilla <= probSplit || !isFirst){
                        //Generar split
                        int index = generator.nextInt(variables.size());
                        root = new SplitTreeNode(variables.get(index));
                        int states = ((FiniteStates) ((SplitTreeNode)root).getVariable()).getNumStates();
        //                Vector<Node> aux = (Vector<Node>) variables.clone();
        //                aux.remove(variables.get(index));
                        for(int i=0; i< states;i++){
                             Vector<Node> aux = (Vector<Node>) variables.clone();
                             aux.remove(variables.get(index));
                            ((SplitTreeNode)root).addSon(this.createRandomRPT(aux, depth, probSplit, probPotential, true));
                        }
                    }else{
                        //Generar lista
                        root = new ListTreeNode();
                        int children = this.poisson();
                        for(int i=0; i<children; i++){
                            ((ListTreeNode)root).addChild(this.createRandomRPT(variables, depth, probSplit, probPotential, false));
                        }
                    }
                }else{
                    root = this.randomPotential(variables);
                }
            }else{
                double semilla = generator.nextDouble();
                if(semilla <= probPotential){
                    root = this.randomPotential(variables);
                    //return root;
                    //
                }else{
                    //Generar nodo valor
                    root = this.randomValue();
                }
            }
        }//si no quedan variables..
        else{ //devolvemos un nodo valor
            root = this.randomValue();
        }
        return root;
    }



   private TreeNode createRandomRPT2(Vector<Node> variables, double probSplit, double probPotential, boolean isFirst){
       TreeNode root = null;
       Random generator = new Random();
        double seed;
        int size = variables.size();
        if(size<=2){
            if(size >0){
                root = this.randomPotential(variables);
            }else{
                root = this.randomValue();
            }
        }else{
            if(isFirst){
                root = this.generateComplexNode(variables, probSplit, probPotential);
            }else{
                if(size >=5){
                    root = this.generateComplexNode(variables, probSplit, probPotential);
                }
                else{
                    seed = generator.nextDouble();
                    if(seed <= 0.2){
                        root = this.generateComplexNode(variables, probSplit, probPotential);
                    }else{
                        root = this.generateSimpleNode(variables, probPotential);
                    }
                }
            }
       }
        return root;
    }

   private TreeNode generateComplexNode(Vector<Node> variables, double probSplit, double proPotential){

       SplitTreeNode newSplitNode;
       ListTreeNode newListNode;
       TreeNode node;
       Random generator = new Random();
       double seed;
       int index, states, children;

       seed = generator.nextDouble();
       if(seed <= probSplit){
           index = generator.nextInt(variables.size());
           newSplitNode = new SplitTreeNode(variables.get(index));
           states = ((FiniteStates)newSplitNode.getVariable()).getNumStates();

           variables.remove(variables.get(index));

           for(int i=0; i<states; i++){
//               newSplitNode.addSon(this.createRandomRPT(variables, variables.size(), probSplit, proPotential));
           }
           node = newSplitNode;
       }
       else{

           newListNode = new ListTreeNode();
           children = this.poisson();
           for(int i=0; i<children; i++){
//               newListNode.addChild(this.createRandomRPT(variables, variables.size(), probSplit, proPotential));
           }
           node= newListNode;
       }
       return node;
   }

   private TreeNode generateSimpleNode(Vector<Node> variables, double probPotential){
       TreeNode root;
       Random generator = new Random();
       double seed;
       seed = generator.nextDouble();

       if(seed <= probPotential){
           root = this.randomPotential(variables);
       }else{
           root = this.randomValue();
       }
       return root;
   }


   private TreeNode randomPotential(Vector<Node> variables){
       Random generator = new Random();
       int vars = generator.nextInt(variables.size());
        if(vars == 0){
            vars = 1;
        }
        Vector<Node> varsForPot = new Vector<Node>();

        for(int i=0; i<vars; i++){
            int index = generator.nextInt(variables.size());
            varsForPot.add(variables.get(index));
            variables.remove(index);
        }

        Configuration conf = new Configuration(varsForPot);
        int possibleValues = conf.possibleValues();
        Potential pot = new PotentialTree(varsForPot);

        for(int i=0; i<possibleValues; i++){
            double value = generator.nextDouble();
            pot.setValue(conf, value);
            conf.nextConfiguration();
        }

        TreeNode root = new PotentialTreeNode(pot);

       return root;
   }


   private TreeNode randomValue(){
        Random generator = new Random();
        double value = generator.nextDouble();
        TreeNode root = new ValueTreeNode(value);
        return root;
   }

      /**
     * Evaluates a set of configurations according to this probability tree,
     * by computing the log-likelihood.
     *
     * @param v a set of configurations given as a vector.
     * @return the computed log-likelihood, i.e., the sum of the evaluations
     * of all the configurations.
     */

    public double evaluateSetOfConfigurations(Vector<Configuration> v) {

        double likelihood = 0;
        int i;
        Configuration conf;

        for (i=0 ; i<v.size() ; i++) {
            conf = v.elementAt(i);
            likelihood += Math.log(this.getValue(conf));
        }

        return likelihood;
    }

}


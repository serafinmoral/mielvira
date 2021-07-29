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
import java.io.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

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

    private Vector<Double> sizes;
    private Vector<Double> numnodos;

    /**
     * Default constructor
     */
    public RecursivePotentialTree() {
        super();
        sizes = new Vector<Double>();
        numnodos = new Vector<Double>();
    }

    /**
     * Copies this potential.
     * @return a copy of this <code>RecursivePotentialTree</code>.
     */
    public Potential copy() {

        RecursivePotentialTree pot;

        pot = new RecursivePotentialTree();
        pot.setSizes(this.sizes);
        pot.setNumnodos(this.numnodos);
        pot.setVariables(this.getVariables());
        pot.values = values.copy();

        return pot;
    }

    public void setSizes(Vector<Double> sizes2){
        sizes=sizes2;
    }

    public void setNumnodos(Vector<Double> sizes2){
        numnodos=sizes2;
    }

    public void addSizes(double sizes2){
        sizes.add(new Double(sizes2));
    }

    public void addNumnodos(double sizes2){
        numnodos.add(new Double(sizes2));
    }

    public void setRandomRPT(Vector<Node> variables, int niveles, double probSplit, double probPotential){

        Vector<Node> vars = (Vector<Node>) variables.clone();
        //TreeNode root = this.createRandomRPT(vars, niveles, probSplit, probPotential, true);
        TreeNode root = this.createRandomRPT2(vars, probSplit, probPotential, true);

        HashSet<Node> finalVars = root.getVariables();
        Vector<Node> intersection = (Vector<Node>) variables.clone();

        if(intersection!=null){
             if(finalVars!=null){
                intersection.removeAll(finalVars);
             }

            if(intersection.size()>0){
//                System.out.println("añadimos potential de "+intersection.penalty());

//                for(int i=0; i<intersection.penalty();i++){
//                    System.out.println("int: "+intersection.get(i).getName());
//                }
//
//                System.out.println("------------");
                
                ListTreeNode newroot = new ListTreeNode();
                newroot.addChild(root);

                Configuration conf = new Configuration(intersection);
                long possibleValues = conf.possibleValues();
                Potential pot = new PotentialTree(intersection);
                Random generator = new Random();

                for(int i=0; i<possibleValues; i++){
                    double value = generator.nextDouble();
                    pot.setValue(conf, value);
                    conf.nextConfiguration();
                }
                TreeNode newPot = new PotentialTreeNode(pot);
                newroot.addChild(newPot);
                //System.out.println("Añadido extra");
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

        if(this.getRoot().getClass() != PotentialTreeNode.class){
            Vector<Node> vars = (Vector<Node>) variables.clone();
            vars.remove(0);
            Configuration conf = new Configuration(vars);
    //        System.out.println("crear split chain de "+vars.penalty()+" elementos.");
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
            //((ListTreeNode)newRoot).addChild(chain);
            Potential aux = new PotentialTree(((SplitTreeNode)chain).RPTtoPotentialTree2());
            ((ListTreeNode)newRoot).addChild(new PotentialTreeNode(aux));
            this.setValues(newRoot);
        }else{
            //Normalizar un potencial que no es un RPT
            Vector<Node> vars = (Vector<Node>) variables.clone();
            Vector<Node> varsH = new Vector<Node>();
            Node head = vars.get(0);
            varsH.add(head);
            vars.remove(0);

            Configuration confP = new Configuration(vars);
            Configuration confH = new Configuration(varsH);



            for(int i=0; i<confP.possibleValues(); i++){
                double sum = 0.0;
                for(int j=0; j< confH.possibleValues(); j++){
                    Configuration confaux = confP.duplicate();
                    confaux.putValue(((FiniteStates)head), j);
                    sum = this.getValue(confaux);
                }
                for(int j=0; j< confH.possibleValues(); j++){
                    Configuration confaux = confP.duplicate();
                    confaux.putValue(((FiniteStates)head), j);
                    this.setValue(confaux, (this.getValue(confaux)/sum));
                }

                confP.nextConfiguration();

            }

        }

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
        this.values.changeTablesIntoTrees();
    }

    /**
     * Method for setting the root of the recursive potential tree
     * @param values
     */
    public void setValues(TreeNode values) {
        this.values = values;
        this.values.setParent(null);
       // this.refreshVariables();
    }

    /**
     * Method for setting a value for a given configuration
     * @param conf
     * @param value
     */
    public void setValue(Configuration conf, double value) {
        //????
        if(this.values.getClass() == SplitTreeNode.class)
            ((SplitTreeNode)this.values).setValue(conf, value);
        else
            System.err.println("Error, not implemented!");
    }

    /**
     * Method for setting a split node of the given variable on all the leaves of the tree
     * @param conf
     * @param value
     */
    public void setLeaves(Node var) {
        SplitTreeNode node = new SplitTreeNode(var);
        for(int i=0; i<((FiniteStates)var).getNumStates(); i++){
            TreeNode value2 = new ValueTreeNode(0.0);
            node.addSon(value2);
        }
        this.values.replaceLeaves(node);
        //????
    }

    /**
     * Method for setting a split node of the given variable on the leaves of the tree
     * consistent with a given configuration
     * 
     * @param conf
     * @param value
     */
    public void setLeavesConf(Node var, Configuration conf) {
        SplitTreeNode node = new SplitTreeNode(var);
        for(int i=0; i<((FiniteStates)var).getNumStates(); i++){
            node.addSon(new ValueTreeNode(0.0));
        }

        this.values.replaceLeavesConf(node, conf);
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

//        Vector<Double> newsizes = (Vector<Double>)this.sizes.clone();
//        newsizes.add(new Double(root.getTreeSize()));
//        result.setSizes(newsizes);

//        Vector<Double> newnumnodos = (Vector<Double>)this.numnodos.clone();
//        newnumnodos.add(new Double(root.getNumberOfNodes()));
//        result.setNumnodos(newnumnodos);

        result.setValues(root);
        result.refreshVariables();
        return result;
    }

    /**
     * deletes the variable given as an argument by summing it out
     * @param var
     * @return
     */
//    @Override
//    public Potential addVariable(Node var) {
//
////        System.out.println(" add variable   "+var.getName());
////        System.out.println(" de: ");
////        this.print(0);
////        System.out.println(" XXXXXXXXXXXXXXXXXXXXXXXXXXX  ");
//
//
//        RecursivePotentialTree result;
//        //Hay que comprobar si la raiz de this es una lista, y si lo es
//        //entonces se divide. Si la raiz es un split o un potencial, no
//        //hace falta hacer nada previamente
//        Class argument = values.getClass();
//        if(argument == ListTreeNode.class){
//            //System.out.println("Root is a list");
//            ListTreeNode root = new ListTreeNode();
//            ListTreeNode with = new ListTreeNode();
//            ListTreeNode without = new ListTreeNode();
//            root.addChild(with);
//            root.addChild(without);
//            ((ListTreeNode)this.values).divideTree(var,root);
//
//            TreeNode marginalizedNode = with.addVariable(var);
//
////            System.out.println("Nodo marginalizado");
////            marginalizedNode.print(0);
////            System.out.println("END Nodo marginalizado");
//
//            ListTreeNode finalRoot = new ListTreeNode();
//
//            finalRoot.addChild(marginalizedNode);
//
//            result=new RecursivePotentialTree();
//            switch(without.getNumberOfChildren()){
//                case 1:
//                    finalRoot.addChild(without.getChild(0));
//                    result.setValues(finalRoot);
//                    break;
//                case 0:
//                    result.setValues(marginalizedNode);
//                    break;
//                default:
//                    finalRoot.addChild(without);
//                    result.setValues(finalRoot);
//                    break;
//            }
//
//        }
//        else{
//        result=new RecursivePotentialTree();
//        result.setValues(values.addVariable(var));
//        }
//
//        if((result.values.getClass() == ListTreeNode.class)&&(result.getVariables().penalty()==1)){
//            result.values = ((ListTreeNode)result.values).combineSons();
//        }
//
//        if(result.getVariables().penalty()<=0){
//             System.out.println(" VARIABLE ELIMINADA!   *****    add variable   "+var.getName());
//        }
//
////        for(int i=0; i< result.getVariables().penalty();i++)
////            System.out.println(((FiniteStates)result.getVariables().get(i)).getName());
//
//        result.refreshVariables();
//        return result;
//    }

    @Override
    public Potential addVariable(Node var) {
        TreeNode newroot = null;
        RecursivePotentialTree result = new RecursivePotentialTree();
        Class argument = values.getClass();
        if(argument == ValueTreeNode.class){
            newroot = new ValueTreeNode(((ValueTreeNode)values).getValue() * ((FiniteStates)var).getNumStates());
        }
        if(argument == PotentialTreeNode.class){
            Potential pot = null;
            if(((PotentialTreeNode)values).getVariables().contains(var)){
                pot = ((PotentialTreeNode)values).getPotential().addVariable(var);
            }else{
                pot = ((PotentialTreeNode)values).getPotential().mul(((FiniteStates)var).getNumStates());
            }
            newroot = new PotentialTreeNode(pot);
        }
        if(argument == SplitTreeNode.class){
            if(var.equals(((SplitTreeNode)values).getVariable())){
               // System.out.println("STARTING PAIRWISE SUM");
                newroot = ((SplitTreeNode)values).pairwiseSum();
                //System.out.print("Max size found: "+newroot.maxSize+"  ");
//                sizes.add(new Double(newroot.maxSize));
//                sizes.add(new Double(newroot.getTreeSize()));
//                numnodos.add(new Double(newroot.maxNumnodos));
//                numnodos.add(new Double(newroot.getNumberOfNodes()));
            }else{
                newroot = new SplitTreeNode(((SplitTreeNode)values).getVariable());
              //  System.out.println("Created SN in addvariable ...");
                int size = ((SplitTreeNode)values).getNumberOfStates();
                for(int j=0; j<size; j++){
                    Potential aux = new RecursivePotentialTree();
                    ((RecursivePotentialTree)aux).setValues(((SplitTreeNode)values).getSon(j).copy());
                    aux = aux.addVariable(var);
//                    System.out.println("... *************************** ...");
//                    aux.print();
//                    System.out.println("... *************************** ...");
                    ((SplitTreeNode)newroot).addSon(((RecursivePotentialTree)aux).getRoot());
                   // System.out.println("... son added ...");
//                    sizes.add(new Double(aux.getSize()));
//                    numnodos.add(new Double(((RecursivePotentialTree)aux).getRoot().maxNumnodos));
//                    numnodos.add(new Double(((RecursivePotentialTree)aux).getNumberOfNodes()));
                }
            }
        }
        if(argument == ListTreeNode.class){
            //System.out.println("Root is a list");
            ListTreeNode root = new ListTreeNode();
            ListTreeNode with = new ListTreeNode();
            ListTreeNode without = new ListTreeNode();
            root.addChild(with);
            root.addChild(without);
            ((ListTreeNode)this.values).divideTree(var,root);
//            System.out.println("   --------> WITH part, variable "+var.getName());
//            with.print(0);
//            System.out.println("   ********************    ");
            TreeNode multiplied = with.pairwiseMultiplication();
//            sizes.add(new Double(multiplied.getTreeSize()));
//            numnodos.add(new Double(multiplied.maxNumnodos));
//            numnodos.add(new Double(multiplied.getNumberOfNodes()));

//            System.out.println("   --------> MULTIPLIED part    ");
//            multiplied.print(0);
//            System.out.println("   ********************    ");

            Potential aux = new RecursivePotentialTree();
            ((RecursivePotentialTree)aux).setValues(multiplied);
            aux = aux.addVariable(var);

            newroot = new ListTreeNode();

            ((ListTreeNode)newroot).addChild(((RecursivePotentialTree)aux).getRoot());

            int size = without.getNumberOfChildren();
            for(int i=0; i<size;i++){
                ((ListTreeNode)newroot).addChild(without.getChild(i));
            }

        }

//        Vector<Double> newsizes = (Vector<Double>)this.sizes.clone();
//        newsizes.add(new Double(newroot.getTreeSize()));
//        result.setSizes(newsizes);
//
//        Vector<Double> newnumnodos = (Vector<Double>)this.numnodos.clone();
//        newnumnodos.add(new Double(newroot.getNumberOfNodes()));
//        result.setNumnodos(newnumnodos);

        result.setValues(newroot);
        return result;
    }


    public double returnSizes(){
        if(!sizes.isEmpty()){
        double max = sizes.get(0);

        for(int i=1; i<sizes.size();i++){
            if(sizes.get(i)> max){
                max = sizes.get(i);
            }
        }
        //System.out.println("max: "+max);
        return max;
        }
        return 0;
    }

        public double returnNumnodos(){
        if(!numnodos.isEmpty()){
        double max = numnodos.get(0);

        for(int i=1; i<numnodos.size();i++){
            if(numnodos.get(i)> max){
                max = numnodos.get(i);
            }
        }
        //System.out.println("max: "+max);
        return max;
        }
        return 0;
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

//        Vector<Double> newsizes = (Vector<Double>)this.sizes.clone();
//        newsizes.add(new Double(result.getRoot().getTreeSize()));
//        result.setSizes(newsizes);
//
//        Vector<Double> newnumnodos = (Vector<Double>)this.numnodos.clone();
//        newnumnodos.add(new Double(result.getRoot().getNumberOfNodes()));
//        result.setNumnodos(newnumnodos);

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

    public long getNumberOfFreeParameters(){
        long size;
        size = this.values.getNumberOfFreeParameters();
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
        int j;
        this.refreshVariables();
        HashSet<Node> vars = this.values.getVariables();
        if(vars == null) System.out.println("VARIABLES A NULL");

  //      System.out.println("*** Variables en el potencial: "+vars.penalty());
        Vector<Node> variables = new Vector<Node>();
        variables.addAll(vars);
 //       System.out.println("--- VAriables en el potencial: "+variables.penalty());
        PotentialTree result = new PotentialTree(variables);
        System.out.println("Variables " + variables);

        for(j=0; j<variables.size(); j++) {
            System.out.println( ((FiniteStates) variables.elementAt(j)));
            System.out.println( ((FiniteStates) variables.elementAt(j)).getNumStates());
        }

        Configuration conf = new Configuration(variables);
        long possibleVals = conf.possibleValues();
        System.out.println("Number Possible Values " + possibleVals);
        for(int i=0; i<possibleVals; i++){
           
            result.setValue(conf, this.getValue(conf));
            conf.nextConfiguration();
        }
        return result;        
        
        
//        ProbabilityTree aux = new ProbabilityTree();
//        aux = this.values.RPTtoPotentialTree();
//        PotentialTree result = new PotentialTree(aux);
//        return result;
    }

    public PotentialTable RPTtoPotentialTable(){
        this.refreshVariables();
        HashSet<Node> vars = this.values.getVariables();
        if(vars == null) System.out.println("VARIABLES A NULL");

//        System.out.println("*** Variables en el potencial: "+vars.penalty());
        Vector<Node> variables = new Vector<Node>();
        variables.addAll(vars);
//        System.out.println("--- VAriables en el potencial: "+variables.penalty());
        PotentialTable result = new PotentialTable(variables);
        Configuration conf = new Configuration(variables);
        long possibleVals = conf.possibleValues();
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
     * Simulates a sample of penalty n. The tree must be normalised.
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


//            for(int l=0; l<variables.penalty();l++)
//                    System.out.println(variables.get(l).getName());
//
//            System.out.println(" ****************************** ");

            RecursivePotentialTree relation = new RecursivePotentialTree();
            Vector<Node> vars = (Vector<Node>) variables.clone();
//            System.out.println("Variables al comienzo: "+vars.penalty());
//            for(int l=0; l<vars.penalty();l++)
//                    System.out.println(vars.get(l).getName());
            relation.setRandomRPT(vars, vars.size(), probProp, probPot);
//            relation.refreshVariables();
            relation.setVariables(variables);
//            System.out.println("Variables al final: "+relation.getVariables().penalty());
//            for(int l=0; l<relation.getVariables().penalty();l++)
//                    System.out.println(((Node)relation.getVariables().get(l)).getName());



//            System.out.println("random rpt creado de "+relation.getVariables().penalty()+" que debe ser igual a "+variables.penalty());

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
//            System.out.println("Variables en la relacion definitiva: "+relations.get(i).getVariables().penalty());

        }
//        System.out.println("CREADAS TODAS LAS RELACIONES");

        //(NodeList structure y Vector<Relation> relations)
//        System.out.println("NUMERO DE RELACIONES: "+relations.penalty());

        Bnet net = new Bnet(structure);
        net.setRelationList(relations);
        return net;

    }


/**
 * GENERAR RPT ALEATORIO
 *
 * el booleano isFirst sirve para que no se aniden listas
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
               Vector<Node> variables2 = (Vector<Node>) variables.clone();
               newSplitNode.addSon(this.createRandomRPT2(variables2, probSplit, proPotential,false));
           }
           node = newSplitNode;
       }
       else{

           newListNode = new ListTreeNode();
           children = this.poisson();
           for(int i=0; i<children; i++){
               Vector<Node> variables2 = (Vector<Node>) variables.clone();
               //Create a sublist of variables for the child
               int vars = generator.nextInt(variables.size());
                if(vars == 0){
                    vars = 1;
                }
                Vector<Node> varsForPot = new Vector<Node>();

                for(int j=0; j<vars; j++){
                    index = generator.nextInt(variables2.size());
                    varsForPot.add(variables2.get(index));
                    variables2.remove(index);
                }
                // *********
                newListNode.addChild(this.createRandomRPT2(varsForPot, probSplit, proPotential,false));
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
        long possibleValues = conf.possibleValues();
        Potential pot = new PotentialTree(varsForPot);

        for(int i=0; i<possibleValues; i++){
            double value = generator.nextDouble();
            pot.setValue(conf, value);
            conf.nextConfiguration();
        }
        ((PotentialTree)pot).pruneLowValues(0.001);

        //No se si es necesario normalizar los nodos potencial, ya que al final el RPT completo se normaliza
        //pot.normalize();

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
            double a = this.getValue(conf);
                    if(a!=0)
                likelihood += Math.log(a);
        }

        double penalty = this.getNumberOfFreeParameters() * (Math.log(v.size())/2.0);
       // System.out.println("Penalty: "+penalty);

        return (likelihood-penalty);
       //return likelihood;
    }

        public double evaluateSetOfConfigurationsLLH(Vector<Configuration> v) {

        double likelihood = 0;
        int i;
        Configuration conf;

        for (i=0 ; i<v.size() ; i++) {
            conf = v.elementAt(i);
            likelihood += Math.log(this.getValue(conf));
        }

       // double penalty = this.getNumberOfFreeParameters() * (Math.log(v.size())/2.0);
       // System.out.println("Penalty: "+penalty);

        //return (likelihood-penalty);
       return likelihood;
    }

    public void transformPTintoRPT(PotentialTree pt){

        //pt.getTree().print(3);

        Vector<Node> vars = pt.getVariables();

        TreeNode root = elvira.potential.RPT.Util.createSplitChain(vars);
        //root.print(3);

        Configuration conf = new Configuration(vars);
        long sizeconf = conf.possibleValues();

        for(int i=0; i< sizeconf; i++){
            elvira.potential.RPT.Util.updateSplitChainLeaf(root,conf.duplicate(), new ValueTreeNode(pt.getValue(conf)));
            conf.nextConfiguration();
        }

        this.setValues(root);
    }

    public void generateDotFile(String namefile) throws FileNotFoundException, IOException {
        FileOutputStream myFOS = new FileOutputStream(namefile);
        BufferedOutputStream myBOS = new BufferedOutputStream(myFOS);
        DataOutputStream myDOS = new DataOutputStream(myBOS);

        myDOS.writeBytes("digraph G{\n");
       // myDOS.writeBytes("penalty=\"8,6\"; node[fontsize=24];\n");
        myDOS.writeBytes("size=\"7.5,7.5\"; ratio=auto;\n");

        //label of the root
        myDOS.writeBytes(this.values.getNodeDotCode(0));

        //the tree
        this.values.generateDotCode(myDOS, 0, 0);

        myDOS.writeBytes("\n}");

        myDOS.close();
        myBOS.close();
        myFOS.close();
    }

    public void findAndReplaceSplit(Node lastParent, Configuration randomConf, Node newParent, Potential p){
        Configuration variables = new Configuration();
        this.values.findAndReplaceSplit(lastParent, randomConf, newParent, p, variables);
        this.refreshVariables();
        //this.print(0);
    }
    
        /**
     * Saves the potential to a file. Saves just the tree.
     * @param p the <code>PrintWriter</code> where the potential will be written.
     */
    public void save(PrintWriter p) {
        PotentialTree pt = this.RPTtoPotentialTree();
        pt.save(p);
    }

    public void replaceValues(Potential p){
        Configuration conf = new Configuration(this.getVariables());
        long size = conf.possibleValues();

        for(int i=0; i<size; i++){

        }
    }

        /**
     * Simulates a sample of size n. The tree must be normalised.
     *
     * @return a vector of configurations.
     */
    public Vector<Configuration> simulateSample(int n) {

        Vector<Configuration> sample = new Vector<Configuration>();
        int i, j, s;
        NodeList v;
        Configuration conf;
        double acum = 0.0, r;

        v = new NodeList(this.getVariables());

        s = (int) v.getSize();

        for (i = 0; i < n; i++) {
            conf = new Configuration(v);
            acum = 0.0;
            r = Math.random();
            for (j = 0; j < s; j++) {
                acum += this.getValue(conf);
                if (acum >= r) {
                    break;
                } else {
                    conf.nextConfiguration();
                }
            }
            sample.addElement(conf);
        }


        return sample;
    }
}


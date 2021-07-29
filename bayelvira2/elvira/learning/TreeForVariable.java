/*
 * SimpleTree.java
 *
 * Created on 12 de septiembre de 2007, 12:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package elvira.potential.RPT.learningdb;

/**
 *
 * @author smc
 */
import elvira.learning.classification.supervised.discrete.*;
import elvira.database.DataBaseCases;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.Node;
import elvira.NodeList;
import elvira.potential.RPT.SplitTreeNode;
import elvira.potential.RPT.TreeNode;
import elvira.potential.RPT.ValueTreeNode;
import elvira.tools.CmdLineArguments;
import elvira.tools.CmdLineArguments.CmdLineArgumentsException;
import elvira.tools.CmdLineArguments.argumentType;

import java.util.HashSet;
import java.io.*;

public class TreeForVariable extends DiscreteClassifier {

    /**
     * Scorer data member
     */
    private Scorer scorer;

    /**
     * List of variables to consider as attributes
     */
    private NodeList attributes;
    
    /**
     * List of variables to consider as forbidden parents
     */
    private HashSet<Node> forbidden;
    /**
     * Result of the learning process
     */
    private TreeNode tree;
    /**
     * Resultant tree score
     */
    private double score;

    /**
     * Parameter to set the kind of pruning: soft or severe
     */
    private boolean softPruning;

    /** 
     * Constructor
     */
    public TreeForVariable() {
        super();
    }

    /**
     * Constructor
     * @param scorer to use
     * @param classn 
     */
    public TreeForVariable(Scorer scorer, int classn) {
        Node node;

        // Assign data members
        this.scorer=scorer;
        this.cases = scorer.getDataBase();
        this.nVariables = this.cases.getVariables().size();
        this.nCases = this.cases.getNumberOfCases();
        this.laplace = true;
        this.evaluations = 0;
        this.logLikelihood = 0;
        this.attributes = new NodeList();

        // nodes contains the variables in the database
        NodeList nodes = this.cases.getVariables();

        // Check the variables in order to avoid continuous variables
        for (int j = 0; j < nodes.size(); j++) {
            node = nodes.elementAt(j);
            if (node.getTypeOfVariable() == Node.CONTINUOUS) {
                System.err.println("ERROR: There is continuous values. First, use a Discretization method.");
                System.exit(0);
            }
            if (j != classn) {
                attributes.insertNode(node);
            }
        }

        // Assign classVar
        classVar = (FiniteStates) nodes.elementAt(classn);

        // Assign classNumber data member
        this.classNumber = classVar.getNumStates();

        // Creates space for forbidden
        forbidden = new HashSet<Node>();
    }

    /**
     * Constructor
     * @param scorer to use
     * @param nodeX it will be classVaR
     * @param nodeY forbiden node, it will not be considered
     * as parent
     */
    public TreeForVariable(Scorer scorer, Node nodeX, Node nodeY) {
        Node node;

        // Assign data members
        this.scorer=scorer;
        this.cases = scorer.getDataBase();
        this.nVariables = this.cases.getVariables().size();
        this.nCases = this.cases.getNumberOfCases();
        this.laplace = true;
        this.evaluations = 0;
        this.logLikelihood = 0;
        this.attributes = new NodeList();

        // nodes contains the variables in the database
        NodeList nodes = this.cases.getVariables();

        // Creates space for forbidden
        forbidden = new HashSet<Node>();

        // Store nodeY as forbidden
        forbidden.add(nodeY);

        // Check the variables in order to avoid continuous variables
        for (int j = 0; j < nodes.size(); j++) {
            node = nodes.elementAt(j);
            if (node.getTypeOfVariable() == Node.CONTINUOUS) {
                System.err.println("ERROR: There is continuous values. First, use a Discretization method.");
                System.exit(0);
            }
            if (!node.getName().equals(nodeX.getName()) && !forbidden.contains(node)) {
                attributes.insertNode(node);
            }
        }

        // Assign classVar
        classVar = (FiniteStates) nodes.getNode(nodeX.getName());

        // Assign classNumber data member
        this.classNumber = classVar.getNumStates();
    }

    /**
     * Constructor
     * @param scorer to use
     * @param nodeX it will be classVaR
     * @param restricted list of forbidden variables
     * as parent
     */
    public TreeForVariable(Scorer scorer, Node nodeX, HashSet<Node> restricted) {
        Node node;

        // Assign data members
        this.scorer=scorer;
        this.cases = scorer.getDataBase();
        this.nVariables = this.cases.getVariables().size();
        this.nCases = this.cases.getNumberOfCases();
        this.laplace = true;
        this.evaluations = 0;
        this.logLikelihood = 0;
        this.attributes = new NodeList();

        // nodes contains the variables in the database
        NodeList nodes = this.cases.getVariables();

        // Creates space for forbidden
        forbidden = new HashSet<Node>();

        // Store restricted list as forbidden
        forbidden.addAll(restricted);

        // Check the variables in order to avoid continuous variables
        for (int j = 0; j < nodes.size(); j++) {
            node = nodes.elementAt(j);
            if (node.getTypeOfVariable() == Node.CONTINUOUS) {
                System.err.println("ERROR: There is continuous values. First, use a Discretization method.");
                System.exit(0);
            }
            if (!node.getName().equals(nodeX.getName()) && !forbidden.contains(node)) {
                attributes.insertNode(node);
            }
        }

        // Assign classVar
        classVar = (FiniteStates)nodes.getNode(nodeX.getName());

        // Assign classNumber data member
        this.classNumber = classVar.getNumStates();
    }


    /**
     * Class constructor
     * @param previousTree
     * @param forbiddenNode
     */
    public TreeForVariable(TreeForVariable previousTree,Node forbiddenNode){
        this.scorer=previousTree.scorer;
        this.cases = scorer.getDataBase();
        this.nVariables = this.cases.getVariables().size();
        this.nCases = this.cases.getNumberOfCases();
        this.laplace = true;
        this.evaluations = 0;
        this.logLikelihood = 0;

        // nodes contains the variables in the database
        NodeList nodes = this.cases.getVariables();

        // Creates space for forbidden
        forbidden = new HashSet<Node>();

        // Store restricted list as forbidden
        forbidden=new HashSet<Node>();
        forbidden.addAll(previousTree.forbidden);
        forbidden.add(forbiddenNode);

        // Set classvar data member
        attributes=previousTree.attributes.copy();
        attributes.removeNode(forbiddenNode);

        // Assign classVar
        classVar = previousTree.classVar;

        // Assign classNumber data member
        this.classNumber = previousTree.classNumber;
    }

    /**
     * Class constructor
     * @param previousTree
     * @param forbiddenNode
     */
    public TreeForVariable(TreeForVariable previousTree,HashSet<Node> forbiddenNodes){
        this.scorer=previousTree.scorer;
        this.cases = scorer.getDataBase();
        this.nVariables = this.cases.getVariables().size();
        this.nCases = this.cases.getNumberOfCases();
        this.laplace = true;
        this.evaluations = 0;
        this.logLikelihood = 0;

        // nodes contains the variables in the database
        NodeList nodes = this.cases.getVariables();

        // Creates space for forbidden
        forbidden = new HashSet<Node>();

        // Store restricted list as forbidden
        forbidden=new HashSet<Node>();
        forbidden.addAll(previousTree.forbidden);
        forbidden.addAll(forbiddenNodes);

        // Set classvar data member
        attributes=previousTree.attributes.copy();
        for(Node forbiddenNode: forbiddenNodes){
            attributes.removeNode(forbiddenNode);
        }

        // Assign classVar
        classVar = previousTree.classVar;

        // Assign classNumber data member
        this.classNumber = previousTree.classNumber;
    }

    /**
     * Shows list of forbidden nodes
     */
    public void showsForbidden(){
        System.out.println("............... FORBIDDEN ...................");
        for(Node forbiddenNode : forbidden){
            System.out.print(forbiddenNode.getName()+"  ");
        }
        System.out.println("\n...........................................");
    }

    /**
     * Default implementation
     */
    public void structuralLearning() {
        // Call to learn method
        tree = learn(attributes, new Configuration());

        // Call to prune method
        prune(tree, new Configuration());

        // Compute the score
        computeScore(tree, new Configuration());
    }

    /**
     * Gets the result of the learning
     * @return
     */
    public TreeNode getTree() {
        return tree;
    }

    /**
     * Gets the score
     * @return score
     */
    public double getScore() {
        return score;
    }

    /**
     * Method for learning
     * @param nodes
     * @param referenceConfiguration
     * @return
     */
    private TreeNode learn(NodeList nodes, Configuration referenceConfiguration) {
        TreeNode treeNew;
        double frec[];
        int max, nx, ngr;
        double scoreNewX, ftotal, maxDifference, difference;
        FiniteStates maxVar, parentVariable;

        // Determine the number of variables in nodes
        int nvar = nodes.size();

        // Get the frequencies for classVar, compatible with
        // referenceConfiguration
        frec = scorer.computeFrequencies(classVar, referenceConfiguration);

        // Set ngr to 0, tme to classNumber and ele to 1
        ngr = 0;

        // Consider every value for classVar
        ftotal = 0.0;
        for (int i = 0; i < classNumber; i++) {
            if (frec[i] > 0) {
                ngr++;
                ftotal += frec[i];
            }
        }

        // If there is only one value to store or there are
        // no variables, make a simple tree with the variable
        // and the frequencies as leaf nodes
        if ((ngr < 2) || (nvar < 1)) {
            treeNew = createSplitTreeNode(frec, ftotal);

            // return tree
            return (treeNew);
        }

        // The tree will contain more variables. it is needed
        // to compute scores, adding the most valuable
        max = 0;
        maxDifference = Double.NEGATIVE_INFINITY;

        // Compute the score for classVar, without parents
        double scorePrevX = scorer.performTest(classVar, referenceConfiguration);

        // Consider as parent the variables in nodes
        for (int i = 0; i < nvar; i++) {
            // Consider this a candidate parent
            parentVariable = (FiniteStates) nodes.elementAt(i);

            // Compute the new score
            scoreNewX = scorer.performTest(classVar, parentVariable, referenceConfiguration);
           int configurationSize = referenceConfiguration.size();
            // Compute the diference
            difference = scoreNewX - scorePrevX - Math.log( (attributes.size() - configurationSize))  ;

            // If there is improvement, update max and depdegree
            if (difference > maxDifference) {
                max = i;
                maxDifference = difference;
            }
        }

        // If there is not improvement, finish 
        if (maxDifference <= 0) {
            treeNew = createSplitTreeNode(frec, ftotal);

            // return tree
            return (treeNew);
        }

        // Add the variable with max difference
        // Select the variable to include in the tree
        maxVar = (FiniteStates) nodes.elementAt(max);
        treeNew = new SplitTreeNode(maxVar);

        // Remove max from nodes
        nodes.removeNode(max);

        // Consider every state for maxVar
        nx = maxVar.getNumStates();
        referenceConfiguration.insert(maxVar, 0);
        for (int i = 0; i < nx; i++) {
            // Add the value for maxVar to referenceConfiguration
            referenceConfiguration.putValue(maxVar.getName(), i);

            // Now learn for the rest of variables in this branch of the tree
            ((SplitTreeNode) treeNew).addSon(learn(nodes, referenceConfiguration));
        }

        // Remove maxVar from the configuration
        referenceConfiguration.remove(maxVar);

        // Insert maxVar. Needed?????
        nodes.insertNode(maxVar);

        // return treeNew
        return (treeNew);
    }

    /**
     * Prune method
     * @param treeNode
     * @param referenceConfiguration
     * @return result of the operation
     */
    public boolean prune(TreeNode treeNode, Configuration referenceConfiguration) {
        // Gets the var related to treeNode
        FiniteStates var = (FiniteStates) ((SplitTreeNode) treeNode).getVariable();
        boolean pruned = false;

        // If the var is varClass, then there is nothing to do and return false
        if (var.getName().equals(classVar.getName())) {
            return false;
        }

        // If there are two ore more variables related to treeNode
        // then the operation will be performed on child tree nodes
        HashSet<Node> treeVariables = treeNode.getVariables();
        if (treeVariables.size() > 2) {
            // Add the new var to the configuration
            referenceConfiguration.insert(var, 0);
            
            // Consider every child passing the operation
            for (int i = 0; i < var.getNumStates(); i++) {
                // Set i value for var
                referenceConfiguration.putValue(var.getName(), i);

                // Now call the method for the child
                pruned = prune(((SplitTreeNode) treeNode).getSon(i), referenceConfiguration);
            }

            // Before proceeding, remove var from the configuration
            referenceConfiguration.remove(var);
        }

        // Now may be needed to apply the operation on treeNode
        treeVariables = treeNode.getVariables();

        if (treeVariables.size() == 2) {
            // Check if it is needed to prune
            pruned = pruneValues(treeNode, referenceConfiguration);
        }
        return pruned;
    }

    /**
     * Pruning method
     * @param treeNode
     * @param referenceConfiguration
     * @return
     */
    private boolean pruneValues(TreeNode treeNode, Configuration referenceConfiguration) {
        boolean pruned = false;
        FiniteStates treeVar = (FiniteStates) ((SplitTreeNode) treeNode).getVariable();
        TreeNode newTreeNode;
        SplitTreeNode parentNode;

        // Compute the scores
        double score1 = scorer.performTest(classVar, treeVar, referenceConfiguration);
        double score2 = scorer.performTest(classVar, referenceConfiguration);
        int configurationSize = referenceConfiguration.size();
        double factor1 = Math.log((double) (attributes.size() - configurationSize) / (configurationSize + 1));
        double factor2 = Math.log(attributes.size() - configurationSize);
        double multiplier=1;
        if (softPruning){
            multiplier=0.7;
        }
        double factor3 = Math.log(multiplier * (attributes.size() - configurationSize));
        double difference = score1 - score2 - factor3;

        // If there is improvement, then prune
        if (difference <= 0) {
            pruned = true;

            // Compute the frequencies for classVar
            // Get the frequencies for classVar, compatible with
            double frec[]=scorer.computeFrequencies(classVar, referenceConfiguration);

            // Set ngr to 0, tme to classNumber and ele to 1
            int ngr = 0;
            double tme = classNumber;
            double ele = 1;

            // Consider every value for classVar
            double ftotal = 0.0;
            for (int i = 0; i < classNumber; i++) {
                if (frec[i] > 0) {
                    ngr++;
                    ftotal += frec[i];
                }
            }

            newTreeNode = createSplitTreeNode(frec, ftotal);

            // This new node will substitute to treeNode
            parentNode = (SplitTreeNode) treeNode.getParent();

            // Get the value for the parent variable in order to change
            // the son. This is needed if we are not in the root of the
            // tree
            if (parentNode != null) {
                int parentState = referenceConfiguration.getValue(parentNode.getVariable().getName());
                parentNode.setSon(newTreeNode, parentState);
                newTreeNode.setParent(parentNode);
            }
        }

        // return pruned value
        return pruned;
    }

    /**
     * Creates a new split tree node for classVar
     * @param frec computed frequencies
     * @param ftotal sum of frequencies
     * @return
     */
    private TreeNode createSplitTreeNode(double frec[], double ftotal) {
        TreeNode newTree = new SplitTreeNode(classVar);
        double tme = classNumber;
        int ele = 1;
        
        // Assign the value nodes as childs
        for (int i = 0; i < classNumber; i++) {
            ((SplitTreeNode) newTree).addSon(new ValueTreeNode((frec[i] + ele) / (ftotal + tme)));
        }

        // return tree
        return (newTree);
    }

    /**
     * Computes the score of the tree, summing the scores of the leaf nodes
     * @param treeNode
     * @param referenceConfiguration
     */
    private void computeScore(TreeNode treeNode, Configuration referenceConfiguration) {
        // Initialize score to 0
        score=0;
        
        // If it is a node related to classVar, compute the score
        // In any other case, pass the operation to childs
        HashSet<Node> variables = treeNode.getVariables();

        // Get the variable related to TreeNode
        FiniteStates variable = (FiniteStates) ((SplitTreeNode) treeNode).getVariable();

        if (variables.size() > 1) {
            // Consider every valor for variable
            referenceConfiguration.insert(variable, 0);
            for (int i = 0; i < variable.getNumStates(); i++) {
                referenceConfiguration.putValue(variable.getName(), i);

                // Now call the method again
                computeScore(((SplitTreeNode) treeNode).getSon(i), referenceConfiguration);
            }

            // Remove variable from configuration
            referenceConfiguration.remove(variable);
        } else {
            // Compute the score and add it to score
            score = score + scorer.performTest(variable, referenceConfiguration);
//printScoreInformation(variable,referenceConfiguration,score);
        }
    }

    /**
     * Print score information
     * @param variable
     * @param conf
     * @param score
     */
    private void printScoreInformation(Node variable, Configuration conf, double score){
        System.out.println("Score realizado..............................");
        System.out.println(" Variable: "+variable.getName());
        conf.pPrint();
        System.out.println();
        System.out.println("Valor: "+score);
        System.out.println("---------------------------------------------");
    }

    /**
     * Print score information
     * @param variableA
     * @param variableB
     * @param conf
     * @param score
     */
    private void printScoreInformation(Node variableA, Node variableB, Configuration conf, double score){
        System.out.println("Score realizado..............................");
        System.out.println(" Variable A: "+variableA.getName());
        System.out.println(" Variable B: "+variableB.getName());
        conf.pPrint();
        System.out.println();
        System.out.println("Valor: "+score);
        System.out.println("---------------------------------------------");
    }

    /**
     * Print information about the tree
     */
    public void print(){
        System.out.println("\n\n ............. Tree for "+classVar.getName()+" ..................");
        System.out.print("Variables: ");
        for(Node node: tree.getVariables()){
            System.out.print("  "+node.getName());
        }
        System.out.println();
        System.out.print("Prohibidas: ");
        for(Node node : forbidden){
            System.out.print("  "+node.getName());
        }
        System.out.println();
        System.out.println("SCORE: "+score);
        System.out.println("................................................................");
    }

    /**
     * Main to use the class from the command line
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, elvira.InvalidEditException, elvira.parser.ParseException, Exception {
        CmdLineArguments params = new CmdLineArguments();
        String trainFile = "-train", trainFileString = null;
        String classNumber = "-classNumber";
        int classVarPosition = 0;

        try {
            params.addArgument(trainFile, argumentType.s, "", "Filename of train DB (.dbc format). No default value, must be provided.");
            params.addArgument(classNumber, argumentType.i, "0", "Position of class variable. 0 as default value.");

            // Parse the arguments
            params.parseArguments(args);

            // Extract the information
            trainFileString = params.getString(trainFile);
            classVarPosition = params.getInteger(classNumber);
        } catch (CmdLineArgumentsException ex) {
            params.printHelp();
            System.exit(1);
        }

        System.out.println("Nombre de BBDD: " + trainFileString);
        FileInputStream fi = new FileInputStream(trainFileString);
        DataBaseCases db = new DataBaseCases(fi);
        Scorer scorer=new Scorer(db);
        fi.close();
        TreeForVariable classifier = new TreeForVariable(scorer, classVarPosition);
        // Now learn
        classifier.structuralLearning();
        classifier.tree.print(2);
    }
}

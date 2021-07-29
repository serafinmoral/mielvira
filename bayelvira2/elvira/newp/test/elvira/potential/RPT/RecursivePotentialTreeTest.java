/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;

import elvira.Configuration;
import elvira.Node;
import elvira.FiniteStates;
import elvira.potential.Potential;
import elvira.potential.PotentialTable;
import elvira.Network;
import elvira.Relation;
import elvira.InvalidEditException;
import elvira.parser.ParseException;
import elvira.Bnet;
import elvira.potential.PotentialTree;
import elvira.NodeList;
import elvira.inference.elimination.VEWithPotentialBPTree;
import elvira.Evidence;
import elvira.LinkList;
import elvira.Link;
import elvira.inference.elimination.VEWithPotentialTree;
import elvira.inference.elimination.VEwithRPT;

import java.io.FileInputStream;

import java.io.IOException;
import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;

/**
 *
 * @author Cora
 */
public class RecursivePotentialTreeTest {
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);
        Node X4 = new FiniteStates("X4", 2);
        Node X5 = new FiniteStates("X5", 2);
        Node X6 = new FiniteStates("X6", 2);
    public RecursivePotentialTreeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setValues method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testSetValues() {
        System.out.println("setValues");
        TreeNode values = null;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(values);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkValuesEquals method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testCheckValuesEquals() {
        System.out.println("checkValuesEquals");
        TreeNode treeNode = null;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        boolean expResult = false;
        boolean result = instance.checkValuesEquals(treeNode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVariables method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testGetVariables() {
        System.out.println("getVariables");
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Vector expResult = null;
        Vector result = instance.getVariables();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of print method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testPrint() {
        System.out.println("print");
        int spaces = 0;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.print(spaces);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of combine method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testCombine() {
        System.out.println("combine");

        Vector<Node> vars = new Vector<Node>();
        vars.add(X1);
        vars.add(X2);
        Configuration conf = new Configuration(vars);
        double[] values = {0.4,0.7,0.6,0.3};
        Potential pot = new PotentialTable(vars);
        int size = conf.possibleValues();
        for(int i=0; i<size; i++){
            pot.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
//        RecursivePotentialTree pot = new RecursivePotentialTree();
//        TreeNode root = new ValueTreeNode(0.5);
//        pot.setValues(root);

        SplitTreeNode split = new SplitTreeNode(X3);
        TreeNode value1 = new ValueTreeNode(0.2);
        TreeNode value2 = new ValueTreeNode(0.8);
        split.addSon(value1);
        split.addSon(value2);
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(split);
        
        
        //Potential expResult = null;
        Potential result = instance.combine(pot);
        result.print();
        System.out.println("El potencial resultado suma "+result.totalPotential());
        result.normalize();
        result.print();
        System.out.println("El potencial resultado suma "+result.totalPotential());
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of addVariable method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testAddVariable() {
        System.out.println("addVariable");
        Node var = null;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Potential expResult = null;
        Potential result = instance.addVariable(var);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of restrictVariable method, of class RecursivePotentialTree.
     */
     @Ignore
    @Test
    public void testRestrictVariable() {
        System.out.println("restrictVariable: Tree 1");

        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        PotentialTable potentialx2 = new PotentialTable(variables);
        double values[]={0.2,0.8};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potentialx2.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNode = new PotentialTreeNode(potentialx2);

        //Value Nodes
        ValueTreeNode value1 = new ValueTreeNode(0.6);
        ValueTreeNode value2 = new ValueTreeNode(0.4);
        ValueTreeNode value3 = new ValueTreeNode(0.3);
        ValueTreeNode value4 = new ValueTreeNode(0.7);
        ValueTreeNode value5 = new ValueTreeNode(0.5);
        ValueTreeNode value6 = new ValueTreeNode(0.3);
        ValueTreeNode value7 = new ValueTreeNode(0.7);

        //Split nodes
        SplitTreeNode spx3_1 = new SplitTreeNode(X3);
        spx3_1.addSon(value1);
        spx3_1.addSon(value2);
        SplitTreeNode spx3_2 = new SplitTreeNode(X3);
        spx3_2.addSon(value3);
        spx3_2.addSon(value4);
        SplitTreeNode spx3_3 = new SplitTreeNode(X3);
        spx3_3.addSon(value6);
        spx3_3.addSon(value7);
        SplitTreeNode spx2_1 = new SplitTreeNode(X2);
        spx2_1.addSon(spx3_1);
        spx2_1.addSon(spx3_2);
        SplitTreeNode spx2_2 = new SplitTreeNode(X2);
        spx2_2.addSon(value5);
        spx2_2.addSon(spx3_3);
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(spx2_1);
        spx1.addSon(spx2_2);

        //List node
        ListTreeNode list = new ListTreeNode();
        list.addChild(spx1);
        list.addChild(potNode);

        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(list);
        //instance.print(0);

        //Configuration X1=0, X2=0
        variables = new Vector<Node>();
        variables.add(X1);
        variables.add(X2);
        Configuration toRestrict = new Configuration(variables);

        Potential result = instance.restrictVariable(toRestrict);
        instance.print(0);
        System.out.println(" now the result");
        ((RecursivePotentialTree)result).print(0);
    }
 @Ignore
    @Test
    public void testEjemplo1Amarg() {

        //Creamos el potencial
                //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X1);
        variables.add(X2);
        variables.add(X3);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.6,0.4,0.3,0.7,0.5,0.5,0.3,0.7};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNode = new PotentialTreeNode(potential);

        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(potNode);

        Potential result = instance.addVariable(X1);
        instance.print(0);
        System.out.println(" now the result");
        ((RecursivePotentialTree)result).print(0);

    }
 @Ignore
    @Test
    public void testEjemplo2marg(){

        System.out.println("start example 2");
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        variables.add(X3);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.7,0.3,0.6,0.4};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNode = new PotentialTreeNode(potential);

        //Value Nodes
        ValueTreeNode value1 = new ValueTreeNode(0.3);
        ValueTreeNode value2 = new ValueTreeNode(0.7);

        //Split nodes
        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(value1);
        spx2.addSon(value2);
        //Split nodes
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(spx2);
        spx1.addSon(potNode);

        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(spx1);
        instance.print(0);

        Potential result = instance.addVariable(X3);
        instance.print(0);
        System.out.println(" now the result");
        ((RecursivePotentialTree)result).print(0);

    }
 @Ignore
    @Test
    public void testEjemplo1SumValues(){
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        //Value Nodes
        ValueTreeNode value1 = new ValueTreeNode(0.7);
        ValueTreeNode value2 = new ValueTreeNode(0.3);
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(value1);
        spx1.addSon(value2);
        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(spx1);
        instance.print(0);

        Potential result = instance.addVariable(X1);
        ((RecursivePotentialTree)result).print(0);
    }
 @Ignore
    @Test
    public void testEjemplo2SumValues(){
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.7,0.3};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNode = new PotentialTreeNode(potential);

        //Value Node
        ValueTreeNode value = new ValueTreeNode(0.3);
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(potNode);
        spx1.addSon(value);
        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(spx1);
        instance.print(0);

        Potential result = instance.addVariable(X1);
        ((RecursivePotentialTree)result).print(0);
    }
 @Ignore
    @Test
    public void testEjemplo3SumValues(){
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.3,0.7};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodex2 = new PotentialTreeNode(potential);

        //Potential for x2 and x3
        variables = new Vector<Node>();
        variables.add(X2);
        variables.add(X3);
        PotentialTable potential2 = new PotentialTable(variables);
        double values2[]={0.2,0.5,0.8,0.5};
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential2.setValue(conf, values2[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodex2x3 = new PotentialTreeNode(potential2);

        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(potNodex2);
        spx1.addSon(potNodex2x3);
        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(spx1);
        instance.print(0);

        Potential result = instance.addVariable(X1);
        ((RecursivePotentialTree)result).print(0);
    }
 @Ignore
    @Test
    public void testEjemplo4SumValues(){
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.7,0.3};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNode = new PotentialTreeNode(potential);

        //Value Node
        ValueTreeNode value = new ValueTreeNode(0.3);
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(value);
        spx1.addSon(potNode);
        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(spx1);
        instance.print(0);

        Potential result = instance.addVariable(X1);
        ((RecursivePotentialTree)result).print(0);
    }
 @Ignore
    @Test
    public void testEjemplo5SumValues(){
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X3);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.8,0.2};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodex3 = new PotentialTreeNode(potential);

        //value nodes
        ValueTreeNode value1 = new ValueTreeNode(0.7);
        ValueTreeNode value2 = new ValueTreeNode(0.3);

        //Splits
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx1.addSon(spx2);
        spx1.addSon(value2);
        spx2.addSon(value1);
        spx2.addSon(potNodex3);
        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(spx1);
        instance.print(0);

        Potential result = instance.addVariable(X1);
        ((RecursivePotentialTree)result).print(0);
    }
 @Ignore
        @Test
    public void testEjemplo6SumValues(){
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        variables.add(X3);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.6,0.4,0.5,0.5};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodex3 = new PotentialTreeNode(potential);

        //value nodes
        ValueTreeNode value1 = new ValueTreeNode(0.3);
        ValueTreeNode value2 = new ValueTreeNode(0.7);

        //Splits
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx1.addSon(spx2);
        spx1.addSon(potNodex3);
        spx2.addSon(value1);
        spx2.addSon(value2);
        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(spx1);
        instance.print(0);

        Potential result = instance.addVariable(X1);
        ((RecursivePotentialTree)result).print(0);
    }
 @Ignore
        @Test
    public void testEjemplo7SumValues(){
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X3);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.7,0.3};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodex3 = new PotentialTreeNode(potential);

        //value nodes
        ValueTreeNode value1 = new ValueTreeNode(0.3);
        ValueTreeNode value2 = new ValueTreeNode(0.7);

        //Splits
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx1.addSon(spx2);
        spx1.addSon(potNodex3);
        spx2.addSon(value1);
        spx2.addSon(value2);
        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(spx1);
        instance.print(0);

        Potential result = instance.addVariable(X1);
        ((RecursivePotentialTree)result).print(0);
    }
 @Ignore
        @Test
    public void testEjemplo8SumValues(){
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X3);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.6,0.4};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodex3 = new PotentialTreeNode(potential);

        //value nodes
        ValueTreeNode value1 = new ValueTreeNode(0.3);
        ValueTreeNode value2 = new ValueTreeNode(0.7);
        ValueTreeNode value3 = new ValueTreeNode(0.5);
        //Splits
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        SplitTreeNode spx2 = new SplitTreeNode(X2);
        SplitTreeNode spx22 = new SplitTreeNode(X2);
        spx1.addSon(spx2);
        spx1.addSon(spx22);
        spx2.addSon(value1);
        spx2.addSon(value2);
        spx22.addSon(value3);
        spx22.addSon(potNodex3);
        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(spx1);
        instance.print(0);

        Potential result = instance.addVariable(X1);
        ((RecursivePotentialTree)result).print(0);
    }
 @Ignore
        @Test
    public void testEjemplo9SumValues(){
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X3);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.6,0.4};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodex3 = new PotentialTreeNode(potential);

        //value nodes
        ValueTreeNode value1 = new ValueTreeNode(0.4);
        ValueTreeNode value2 = new ValueTreeNode(0.6);
        ValueTreeNode value3 = new ValueTreeNode(0.7);
        ValueTreeNode value4 = new ValueTreeNode(0.3);
        //Splits
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        SplitTreeNode spx2 = new SplitTreeNode(X2);
        SplitTreeNode spx3 = new SplitTreeNode(X3);

        spx1.addSon(spx2);
        spx1.addSon(spx3);
        spx2.addSon(value1);
        spx2.addSon(value2);
        spx3.addSon(value3);
        spx3.addSon(value4);
        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(spx1);
        instance.print(0);

        Potential result = instance.addVariable(X1);
        ((RecursivePotentialTree)result).print(0);
    }

 @Ignore
    @Test
    public void testEjemplo10DirectMult(){
        //Variables
        Node X = new FiniteStates("X", 2);
        Node Y = new FiniteStates("Y", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.3,0.7};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX = new PotentialTreeNode(potential);

                //Potential for x2 (right side of the tree)
        Vector<Node> variablesY = new Vector<Node>();
        variablesY.add(Y);
        PotentialTable potentialY = new PotentialTable(variablesY);
        double valuesY[]={0.6,0.4};
        Configuration confY=new Configuration(variablesY);
        for(int i=0; i < confY.possibleValues(); i++){
            potentialY.setValue(confY, valuesY[i]);
            confY.nextConfiguration();
        }
        PotentialTreeNode potNodeY = new PotentialTreeNode(potentialY);

        ValueTreeNode val = new ValueTreeNode(0.5);

        TreeNode result1 = potNodeX.directMultiplication(potNodeY);
        potNodeX.print(0);
        potNodeY.print(0);
        result1.print(0);
        TreeNode result2 = potNodeX.directMultiplication(val);
        potNodeX.print(0);
        val.print(0);
        result2.print(0);
        TreeNode result3 = val.directMultiplication(potNodeX);
        potNodeX.print(0);
        val.print(0);
        result3.print(0);
    }
 @Ignore
    @Test
    public void testEjemplo11DirectMult(){

        System.out.println("EJEMPLO 11 ----------------------------------");
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 3);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X1);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.3,0.7};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX1 = new PotentialTreeNode(potential);

        //Potential for x2 (right side of the tree)
        Vector<Node> variablesY = new Vector<Node>();
        variablesY.add(X2);
        PotentialTable potentialY = new PotentialTable(variablesY);
        double valuesY[]={0.2,0.3,0.5};
        Configuration confY=new Configuration(variablesY);
        for(int i=0; i < confY.possibleValues(); i++){
            potentialY.setValue(confY, valuesY[i]);
            confY.nextConfiguration();
        }
        PotentialTreeNode potNodeX2 = new PotentialTreeNode(potentialY);

        ValueTreeNode valNode = new ValueTreeNode(0.3);
        ValueTreeNode value1 = new ValueTreeNode(0.3);
        ValueTreeNode value2 = new ValueTreeNode(0.7);
        ValueTreeNode value3 = new ValueTreeNode(0.2);
        ValueTreeNode value4 = new ValueTreeNode(0.3);
        ValueTreeNode value5 = new ValueTreeNode(0.5);

        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(value1);
        spx1.addSon(value2);
        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(value3);
        spx2.addSon(value4);
        spx2.addSon(value5);

        System.out.println("RESULTADOS ---- ");

        TreeNode result1 = spx1.directMultiplication(spx2);
        result1.print(0);
        System.out.println("RESULTADOS ---- ");
        TreeNode result2 = spx1.directMultiplication(spx1);
        result2.print(0);
        System.out.println("RESULTADOS ---- ");
        TreeNode result3 = spx1.directMultiplication(potNodeX1);
        result3.print(0);
        System.out.println("RESULTADOS ---- ");
        TreeNode result4 = spx1.directMultiplication(potNodeX2);
        result4.print(0);
        System.out.println("RESULTADOS ---- ");
        TreeNode result5 = spx1.directMultiplication(valNode);
        result5.print(0);
        System.out.println("RESULTADOS ---- ");
        TreeNode result6 = potNodeX2.directMultiplication(spx1);
        result6.print(0);
        System.out.println("RESULTADOS ---- ");
        TreeNode result7 = valNode.directMultiplication(spx1);
        result7.print(0);
    }
 @Ignore
    @Test
    public void testEjemplo12additionConListas(){

        System.out.println("COMIENZA EL TEST 12");
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        ValueTreeNode value1 = new ValueTreeNode(0.2);
        ValueTreeNode value2 = new ValueTreeNode(0.8);
        ValueTreeNode value3 = new ValueTreeNode(0.3);
        ValueTreeNode value4 = new ValueTreeNode(0.4);
        ValueTreeNode value5 = new ValueTreeNode(0.6);

        Vector<Node> variables = new Vector<Node>();
        variables.add(X3);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.4,0.6};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX3 = new PotentialTreeNode(potential);

        SplitTreeNode spx3 = new SplitTreeNode(X3);
        spx3.addSon(value4);
        spx3.addSon(value5);

        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(value1);
        spx2.addSon(value2);

        ListTreeNode lista = new ListTreeNode();
        lista.addChild(value3);
        lista.addChild(potNodeX3);
        lista.addChild(spx3);

        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(spx2);
        spx1.addSon(lista);


        TreeNode result = spx1.addVariable(X1);
        result.print(0);

    }
 @Ignore
    @Test
    public void testEjemplo13additionConListas(){
        System.out.println("COMIENZA EL TEST 13");
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        ValueTreeNode value1 = new ValueTreeNode(0.4);
        ValueTreeNode value2 = new ValueTreeNode(0.3);
        ValueTreeNode value3 = new ValueTreeNode(0.4);
        ValueTreeNode value4 = new ValueTreeNode(0.6);

        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.7,0.3};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX2 = new PotentialTreeNode(potential);

        SplitTreeNode spx3 = new SplitTreeNode(X3);
        spx3.addSon(value3);
        spx3.addSon(value4);

        ListTreeNode lista = new ListTreeNode();
        lista.addChild(value2);
        lista.addChild(potNodeX2);
        lista.addChild(spx3);

        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(value1);
        spx1.addSon(lista);


        TreeNode result = spx1.addVariable(X1);
        result.print(0);

    }
 @Ignore
    @Test
    public void testEjemplo14additionConListas(){
        System.out.println("COMIENZA EL TEST 14");
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        ValueTreeNode value1 = new ValueTreeNode(0.4);
        ValueTreeNode value2 = new ValueTreeNode(0.3);
        ValueTreeNode value3 = new ValueTreeNode(0.4);
        ValueTreeNode value4 = new ValueTreeNode(0.6);
        ValueTreeNode value5 = new ValueTreeNode(0.4);

        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.7,0.3};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX2 = new PotentialTreeNode(potential);

        SplitTreeNode spx3 = new SplitTreeNode(X3);
        spx3.addSon(value3);
        spx3.addSon(value4);

        ListTreeNode lista = new ListTreeNode();
        lista.addChild(value2);
        lista.addChild(value5);
        lista.addChild(spx3);

        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(potNodeX2);
        spx1.addSon(lista);


        TreeNode result = spx1.addVariable(X1);
        result.print(0);
    }
 @Ignore
    @Test
    public void testEjemplo15additionConListas(){
        System.out.println("COMIENZA EL TEST 15");
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        ValueTreeNode value1 = new ValueTreeNode(0.3);
        ValueTreeNode value2 = new ValueTreeNode(0.4);
        ValueTreeNode value3 = new ValueTreeNode(0.6);
        ValueTreeNode value4 = new ValueTreeNode(0.4);

        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.7,0.3};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX2 = new PotentialTreeNode(potential);

        SplitTreeNode spx3 = new SplitTreeNode(X3);
        spx3.addSon(value2);
        spx3.addSon(value3);

        ListTreeNode listaI = new ListTreeNode();
        listaI.addChild(value1);
        listaI.addChild(potNodeX2);

        ListTreeNode listaD = new ListTreeNode();
        listaD.addChild(spx3);
        listaD.addChild(value4);
        
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(listaI);
        spx1.addSon(listaD);

        TreeNode result = spx1.addVariable(X1);
        result.print(0);
    }
 @Ignore
    @Test
    public void testEjemplo16directMulConListas(){
        System.out.println("COMIENZA EL TEST 16");
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        ValueTreeNode value1 = new ValueTreeNode(0.5);
        ValueTreeNode value2 = new ValueTreeNode(0.8);
        ValueTreeNode value3 = new ValueTreeNode(0.2);
        ValueTreeNode value4 = new ValueTreeNode(0.1);

        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.6,0.4};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX2 = new PotentialTreeNode(potential);

        Vector<Node> variables2 = new Vector<Node>();
        variables2.add(X3);
        PotentialTable potential2 = new PotentialTable(variables2);
        double values2[]={0.6,0.4};
        Configuration conf2=new Configuration(variables2);
        for(int i=0; i < conf2.possibleValues(); i++){
            potential2.setValue(conf2, values2[i]);
            conf2.nextConfiguration();
        }
        PotentialTreeNode potNodeX3 = new PotentialTreeNode(potential2);

        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(value1);
        spx1.addSon(potNodeX2);

        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(value2);
        spx2.addSon(value3);

        ListTreeNode lista = new ListTreeNode();
        lista.addChild(spx2);
        lista.addChild(potNodeX3);
        lista.addChild(value4);

        TreeNode result = spx1.directMultiplication(lista).addVariable(X1);
        System.out.println("RESULTADO:     ");
        result.print(0);
    }
 @Ignore
    @Test
    public void testEjemplo17directMulConListas(){
        System.out.println("COMIENZA EL TEST 17");
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        ValueTreeNode value1 = new ValueTreeNode(0.5);
        ValueTreeNode value2 = new ValueTreeNode(0.8);
        ValueTreeNode value3 = new ValueTreeNode(0.2);
        ValueTreeNode value4 = new ValueTreeNode(0.1);

        Vector<Node> variables = new Vector<Node>();
        variables.add(X1);
        variables.add(X2);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.8,0.2,0.4,0.6};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX1X2 = new PotentialTreeNode(potential);

        Vector<Node> variables2 = new Vector<Node>();
        variables2.add(X3);
        PotentialTable potential2 = new PotentialTable(variables2);
        double values2[]={0.6,0.4};
        Configuration conf2=new Configuration(variables2);
        for(int i=0; i < conf2.possibleValues(); i++){
            potential2.setValue(conf2, values2[i]);
            conf2.nextConfiguration();
        }
        PotentialTreeNode potNodeX3 = new PotentialTreeNode(potential2);

        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(value2);
        spx2.addSon(value3);

        ListTreeNode lista = new ListTreeNode();
        lista.addChild(spx2);
        lista.addChild(potNodeX3);
        lista.addChild(value4);

        TreeNode result = potNodeX1X2.directMultiplication(lista).addVariable(X1);
        System.out.println("RESULTADO:     ");
        result.print(0);
    }
 @Ignore
    @Test
    public void testEjemplo18directMulConListas(){
        System.out.println("COMIENZA EL TEST 18");
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        ValueTreeNode value1 = new ValueTreeNode(0.5);
        ValueTreeNode value2 = new ValueTreeNode(0.8);
        ValueTreeNode value3 = new ValueTreeNode(0.2);
        ValueTreeNode value4 = new ValueTreeNode(0.1);

        Vector<Node> variables2 = new Vector<Node>();
        variables2.add(X3);
        PotentialTable potential2 = new PotentialTable(variables2);
        double values2[]={0.6,0.4};
        Configuration conf2=new Configuration(variables2);
        for(int i=0; i < conf2.possibleValues(); i++){
            potential2.setValue(conf2, values2[i]);
            conf2.nextConfiguration();
        }
        PotentialTreeNode potNodeX3 = new PotentialTreeNode(potential2);

        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(value2);
        spx2.addSon(value3);

        ListTreeNode lista = new ListTreeNode();
        lista.addChild(spx2);
        lista.addChild(potNodeX3);
        lista.addChild(value4);

        TreeNode result = value1.directMultiplication(lista).addVariable(X3);
        System.out.println("RESULTADO:     ");
        result.print(0);
    }
 @Ignore
    @Test
    public void testEjemplo19directMulConListas(){
        System.out.println("COMIENZA EL TEST 19");
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        ValueTreeNode value1 = new ValueTreeNode(0.5);
        ValueTreeNode value2 = new ValueTreeNode(0.8);
        ValueTreeNode value3 = new ValueTreeNode(0.2);
        ValueTreeNode value4 = new ValueTreeNode(0.1);

        Vector<Node> variables2 = new Vector<Node>();
        variables2.add(X3);
        PotentialTable potential2 = new PotentialTable(variables2);
        double values2[]={0.6,0.4};
        Configuration conf2=new Configuration(variables2);
        for(int i=0; i < conf2.possibleValues(); i++){
            potential2.setValue(conf2, values2[i]);
            conf2.nextConfiguration();
        }
        PotentialTreeNode potNodeX3 = new PotentialTreeNode(potential2);

        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(value2);
        spx2.addSon(value3);

        ListTreeNode lista = new ListTreeNode();
        lista.addChild(spx2);
        lista.addChild(potNodeX3);
        lista.addChild(value4);

        Vector<Node> variables = new Vector<Node>();
        variables.add(X3);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.8,0.2};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX3L1 = new PotentialTreeNode(potential);

        Vector<Node> variables1 = new Vector<Node>();
        variables1.add(X2);
        PotentialTable potential1 = new PotentialTable(variables1);
        double values1[]={0.6,0.4};
        Configuration conf1=new Configuration(variables1);
        for(int i=0; i < conf1.possibleValues(); i++){
            potential1.setValue(conf1, values1[i]);
            conf1.nextConfiguration();
        }
        PotentialTreeNode potNodeX2L1 = new PotentialTreeNode(potential1);

        ValueTreeNode value5 = new ValueTreeNode(0.3);
        ValueTreeNode value6 = new ValueTreeNode(0.7);

        SplitTreeNode spx2L1 = new SplitTreeNode(X2);
        spx2L1.addSon(value5);
        spx2L1.addSon(value6);

        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(spx2L1);
        spx1.addSon(potNodeX3L1);

        ListTreeNode lista1 = new ListTreeNode();
        lista1.addChild(spx1);
        lista1.addChild(potNodeX2L1);
        
        TreeNode result = lista1.directMultiplication(lista).addVariable(X2);
        System.out.println("RESULTADO:     ");
        result.print(0);
    }
 @Ignore
    @Test
    public void testEjemplo20MarginalizacionConListas(){
        System.out.println("COMIENZA EL TEST 20");
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        ValueTreeNode value1 = new ValueTreeNode(0.5);
        ValueTreeNode value2 = new ValueTreeNode(0.5);
        ValueTreeNode value3 = new ValueTreeNode(0.4);
        ValueTreeNode value4 = new ValueTreeNode(0.6);

        Vector<Node> variables = new Vector<Node>();
        variables.add(X3);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.8,0.2};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX3 = new PotentialTreeNode(potential);

        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(value3);
        spx2.addSon(value4);

        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(value2);
        spx1.addSon(spx2);

        ListTreeNode lista = new ListTreeNode();
        lista.addChild(value1);
        lista.addChild(spx1);
        lista.addChild(potNodeX3);
        RecursivePotentialTree original = new RecursivePotentialTree();
        original.setValues(lista);

        RecursivePotentialTree result = (RecursivePotentialTree)original.addVariable(X1);
        System.out.println("RESULTADO:     ");
        result.print(0);
    }

    public RecursivePotentialTree createNetwork1(){

        ValueTreeNode value1 = new ValueTreeNode(0.5);

        //potencial 1
        Vector<Node> variables = new Vector<Node>();
        variables.add(X6);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.3,0.7};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX6 = new PotentialTreeNode(potential);

        //potencial 2
        variables.clear();
        variables.add(X1);
        potential = null;
        potential = new PotentialTable(variables);
        double values2[]={0.1,0.9};
        conf = null;
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values2[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX1 = new PotentialTreeNode(potential);

        //potencial 3
        variables.clear();
        variables.add(X5);
        potential = null;
        potential = new PotentialTable(variables);
        double values3[]={0.7,0.3};
        conf = null;
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values3[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX5 = new PotentialTreeNode(potential);

        //potencial 4
        variables.clear();
        variables.add(X3);
        variables.add(X1);
        potential = null;
        potential = new PotentialTable(variables);
        double values4[]={0.1,0.2,0.9,0.8};
        conf = null;
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values4[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX3X1 = new PotentialTreeNode(potential);

        //potencial 5
        variables.clear();
        variables.add(X2);
        variables.add(X1);
        variables.add(X6);
        potential = null;
        potential = new PotentialTable(variables);
        double values5[]={0.4,0.1,0.3,0.9,0.6,0.7,0.7,0.3};
        conf = null;
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values5[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX2X1X6 = new PotentialTreeNode(potential);
 
        //potencial 6
        variables.clear();
        variables.add(X4);
        variables.add(X5);
        potential = null;
        potential = new PotentialTable(variables);
        double values6[]={0.2,0.4,0.8,0.6};
        conf = null;
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values6[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX4X5 = new PotentialTreeNode(potential);

        //potencial 7
        variables.clear();
        variables.add(X4);
        potential = null;
        potential = new PotentialTable(variables);
        double values7[]={1,0};
        conf = null;
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values7[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX4 = new PotentialTreeNode(potential);

        SplitTreeNode spx3 = new SplitTreeNode(X3);
        spx3.addSon(value1);
        spx3.addSon(potNodeX4X5);

        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(spx3);
        spx2.addSon(potNodeX4);

        ListTreeNode lista = new ListTreeNode();
        lista.addChild(potNodeX6);
        lista.addChild(potNodeX1);
        lista.addChild(potNodeX5);
        lista.addChild(potNodeX3X1);
        lista.addChild(potNodeX2X1X6);
        lista.addChild(spx2);

        RecursivePotentialTree original = new RecursivePotentialTree();
        original.setValues(lista);
        return original;
    }

    @Ignore
    @Test
    public void seeStructure(){
        RecursivePotentialTree tree = this.createNetwork3();
        Potential toFact = tree.getPotentialTable();
        toFact.normalize();
        learningRPT instance = new learningRPT(0.0,1.0);
        TreeNode tree2 = instance.factorize(toFact);
        RecursivePotentialTree treeResult = new RecursivePotentialTree();
        treeResult.setValues(tree2);
        Potential result = treeResult.getPotentialTable();
        result.normalize();
        tree.print(0);
        System.out.println("   +         +         +        +       ");
        tree2.print(0);

        System.out.println("Tree size, original: "+tree.getSize()+" and learned: "+treeResult.getSize());

        Configuration conf = new Configuration(toFact.getVariables());
        for(int i=0; i< toFact.getSize(); i++){
            System.out.println(toFact.getValue(conf)+" vs "+ result.getValue(conf));
            conf.nextConfiguration();
        }
    }

    @Ignore
    @Test
    public void seeStructure2(){
        RecursivePotentialTree tree = this.createNetwork3();
        Potential toFact = tree.getPotentialTable();
        toFact.normalize();
        learningRPT instance = new learningRPT(0.0001,0.0);
        TreeNode tree2 = instance.factorize(toFact);
        RecursivePotentialTree treeResult = new RecursivePotentialTree();
        treeResult.setValues(tree2);
        Potential result = treeResult.getPotentialTable();
        tree.print(0);
        System.out.println("   +         +         +        +       ");
        tree2.print(0);

        Configuration conf = new Configuration(toFact.getVariables());
        for(int i=0; i< toFact.getSize(); i++){
            System.out.println(toFact.getValue(conf)+" vs "+ result.getValue(conf));
            conf.nextConfiguration();
        }
    }

    @Ignore
    @Test
    public void VariableEliminationX1_1(){

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        
        RecursivePotentialTree tree = this.createNetwork1();
        Potential result;

        result = tree.addVariable(X6);
        result = result.addVariable(X5);
        result = result.addVariable(X3);
        result = result.addVariable(X2);
        result = result.addVariable(X4);

        System.out.println("Result : ");
        ((RecursivePotentialTree)result).print(0);
    }

    @Ignore
    @Test
    public void VariableEliminationX1_2(){
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        RecursivePotentialTree tree = this.createNetwork1();
        Potential result;

        result = tree.addVariable(X4);
        result = result.addVariable(X2);
        result = result.addVariable(X3);
        result = result.addVariable(X5);
        result = result.addVariable(X6);

        System.out.println("Result : ");
        ((RecursivePotentialTree)result).print(0);
    }

    @Ignore
    @Test
    public void VariableEliminationX2_1(){
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        RecursivePotentialTree tree = this.createNetwork1();
        Potential result;

        result = tree.addVariable(X4);
        result = result.addVariable(X1);
        result = result.addVariable(X3);
        result = result.addVariable(X5);
        result = result.addVariable(X6);

        System.out.println("Result : ");
        ((RecursivePotentialTree)result).print(0);
    }

    @Ignore
    @Test
    public void VariableEliminationX3(){
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        RecursivePotentialTree tree = this.createNetwork1();
        Potential result;

        result = tree.addVariable(X4);
        result = result.addVariable(X1);
        result = result.addVariable(X2);
        result = result.addVariable(X5);
        result = result.addVariable(X6);

        System.out.println("Result : ");
        ((RecursivePotentialTree)result).print(0);
    }

    @Ignore
    @Test
    public void VariableEliminationX4(){
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        RecursivePotentialTree tree = this.createNetwork1();
        Potential result;

        result = tree.addVariable(X3);
        result = result.addVariable(X1);
        result = result.addVariable(X2);
        result = result.addVariable(X5);
        result = result.addVariable(X6);

        System.out.println("Result : ");
        ((RecursivePotentialTree)result).print(0);
    }

   @Ignore
    @Test
    public void VariableEliminationX5(){
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        RecursivePotentialTree tree = this.createNetwork1();
        Potential result;

        result = tree.addVariable(X3);
        result = result.addVariable(X1);
        result = result.addVariable(X2);
        result = result.addVariable(X4);
        result = result.addVariable(X6);

        System.out.println("Result : ");
        ((RecursivePotentialTree)result).print(0);
    }

   @Ignore
    @Test
    public void VariableEliminationX6(){
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        RecursivePotentialTree tree = this.createNetwork1();
        Potential result;

        result = tree.addVariable(X3);
        result = result.addVariable(X1);
        result = result.addVariable(X2);
        result = result.addVariable(X4);
        result = result.addVariable(X5);

        System.out.println("Result : ");
        ((RecursivePotentialTree)result).print(0);
    }

        public RecursivePotentialTree createNetwork2(){

        ValueTreeNode value1 = new ValueTreeNode(0.5);
        ValueTreeNode value2 = new ValueTreeNode(0.1);
        ValueTreeNode value3 = new ValueTreeNode(0.6);
        ValueTreeNode value4 = new ValueTreeNode(0.3);

        //potencial 1
        Vector<Node> variables = new Vector<Node>();
        variables.add(X1);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.2,0.8};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX1 = new PotentialTreeNode(potential);

        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(potNodeX1);
        spx2.addSon(value2);
       
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(value3);
        spx1.addSon(value4);

        SplitTreeNode spx3 = new SplitTreeNode(X3);
        spx3.addSon(spx1);
        spx3.addSon(value1);

        ListTreeNode lista = new ListTreeNode();
        lista.addChild(spx2);
        lista.addChild(spx3);

        //lista.print(0);

        RecursivePotentialTree original = new RecursivePotentialTree();
        original.setValues(lista);
        return original;
    }

        public RecursivePotentialTree createNetwork3(){

        ValueTreeNode value1 = new ValueTreeNode(0.6);
        ValueTreeNode value2 = new ValueTreeNode(0.4);
        ValueTreeNode value3 = new ValueTreeNode(0.1);
        ValueTreeNode value4 = new ValueTreeNode(0.9);

        //potencial 1
        Vector<Node> variables = new Vector<Node>();
        variables.add(X1);
        PotentialTable potential = new PotentialTable(variables);
        double values[]={0.7,0.3};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX1 = new PotentialTreeNode(potential);

        //potencial 2
        variables = new Vector<Node>();
        variables.add(X2);
        potential = new PotentialTable(variables);
        double values2[]={0.6,0.4};
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values2[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX2 = new PotentialTreeNode(potential);


        SplitTreeNode spx4 = new SplitTreeNode(X4);
        spx4.addSon(value1);
        spx4.addSon(value2);

        SplitTreeNode spx5 = new SplitTreeNode(X5);
        spx5.addSon(value3);
        spx5.addSon(value4);

        SplitTreeNode spx3 = new SplitTreeNode(X3);
        spx3.addSon(spx4);
        spx3.addSon(spx5);


        ListTreeNode lista = new ListTreeNode();
        lista.addChild(potNodeX1);
        lista.addChild(potNodeX2);
        lista.addChild(spx3);

        //lista.print(0);

        RecursivePotentialTree original = new RecursivePotentialTree();
        original.setValues(lista);
        return original;
    }

@Ignore
    @Test
    public void getPotentialTable(){

        System.out.println(" Test para crear una tabla a partir de un RPT ");

        RecursivePotentialTree tree = this.createNetwork2();
        //tree.print(2);
        PotentialTable pot = tree.getPotentialTable();
        pot.print(0);
    }
@Ignore
    @Test
    public void getTreeSizeTest(){
        RecursivePotentialTree tree = this.createNetwork1();
        //tree.print(2);
        long size = tree.getSize();
        tree.print(0);
        System.out.println("Size of the tree: "+ size);
    }

    /**
     * Test of setValues method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testSetValues_TreeNode() {
        System.out.println("setValues");
        TreeNode values = null;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(values);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkValuesEquals method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testCheckValuesEquals_TreeNode() {
        System.out.println("checkValuesEquals");
        TreeNode treeNode = null;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        boolean expResult = false;
        boolean result = instance.checkValuesEquals(treeNode);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVariables method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testGetVariables1() {
        System.out.println("getVariables");
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Vector expResult = null;
        Vector result = instance.getVariables();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of print method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testPrint_int() {
        System.out.println("print");
        int spaces = 0;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.print(spaces);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of print method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testPrint1() {
        System.out.println("print");
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.print();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of combine method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testCombine_Potential() {
        System.out.println("combine");
        Potential pot = null;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Potential expResult = null;
        Potential result = instance.combine(pot);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addVariable method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testAddVariable_Node() {
        System.out.println("addVariable");
        Node var = null;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Potential expResult = null;
        Potential result = instance.addVariable(var);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of restrictVariable method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testRestrictVariable_Configuration() {
        System.out.println("restrictVariable");
        Configuration conf = null;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Potential expResult = null;
        Potential result = instance.restrictVariable(conf);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPotentialTable method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testGetPotentialTable() {
        System.out.println("getPotentialTable");
        RecursivePotentialTree instance = new RecursivePotentialTree();
        PotentialTable expResult = null;
        PotentialTable result = instance.getPotentialTable();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSize method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        RecursivePotentialTree instance = new RecursivePotentialTree();
        long expResult = 0L;
        long result = instance.getSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumberOfNodes method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testGetNumberOfNodes() {
        System.out.println("getNumberOfNodes");
        RecursivePotentialTree instance = new RecursivePotentialTree();
        long expResult = 0L;
        long result = instance.getNumberOfNodes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getValue method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testGetValue() {
        System.out.println("getValue");
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        PotentialTable potentialx2 = new PotentialTable(variables);
        double values[]={0.2,0.8};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potentialx2.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNode = new PotentialTreeNode(potentialx2);

        //Value Nodes
        ValueTreeNode value1 = new ValueTreeNode(0.6);
        ValueTreeNode value2 = new ValueTreeNode(0.4);
        ValueTreeNode value3 = new ValueTreeNode(0.3);
        ValueTreeNode value4 = new ValueTreeNode(0.7);
        ValueTreeNode value5 = new ValueTreeNode(0.5);
        ValueTreeNode value6 = new ValueTreeNode(0.3);
        ValueTreeNode value7 = new ValueTreeNode(0.7);

        //Split nodes
        SplitTreeNode spx3_1 = new SplitTreeNode(X3);
        spx3_1.addSon(value1);
        spx3_1.addSon(value2);
        SplitTreeNode spx3_2 = new SplitTreeNode(X3);
        spx3_2.addSon(value3);
        spx3_2.addSon(value4);
        SplitTreeNode spx3_3 = new SplitTreeNode(X3);
        spx3_3.addSon(value6);
        spx3_3.addSon(value7);
        SplitTreeNode spx2_1 = new SplitTreeNode(X2);
        spx2_1.addSon(spx3_1);
        spx2_1.addSon(spx3_2);
        SplitTreeNode spx2_2 = new SplitTreeNode(X2);
        spx2_2.addSon(value5);
        spx2_2.addSon(spx3_3);
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(spx2_1);
        spx1.addSon(spx2_2);

        //List node
        ListTreeNode list = new ListTreeNode();
        list.addChild(spx1);
        list.addChild(potNode);

        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(list);
        //instance.print(0);

        //Configuration X1=0, X2=0
        variables = new Vector<Node>();
        variables.add(X1);
        variables.add(X2);
        variables.add(X3);
        Configuration tocheck = new Configuration(variables);
        tocheck.nextConfiguration();
        double expResult = 0.0;
        double result = instance.getValue(tocheck);
        System.out.println("is " + expResult +" equal to "+ result+"?");
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of print method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testPrint_0args() {
        System.out.println("print");
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.print();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTotalPotential method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testGetTotalPotential() {
        System.out.println("getTotalPotential");
        //Variables
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);

        //Potential for x2 (right side of the tree)
        Vector<Node> variables = new Vector<Node>();
        variables.add(X2);
        PotentialTable potentialx2 = new PotentialTable(variables);
        double values[]={0.2,0.8};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potentialx2.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNode = new PotentialTreeNode(potentialx2);

        //Value Nodes
        ValueTreeNode value1 = new ValueTreeNode(0.6);
        ValueTreeNode value2 = new ValueTreeNode(0.4);
        ValueTreeNode value3 = new ValueTreeNode(0.3);
        ValueTreeNode value4 = new ValueTreeNode(0.7);
        ValueTreeNode value5 = new ValueTreeNode(0.5);
        ValueTreeNode value6 = new ValueTreeNode(0.3);
        ValueTreeNode value7 = new ValueTreeNode(0.7);

        //Split nodes
        SplitTreeNode spx3_1 = new SplitTreeNode(X3);
        spx3_1.addSon(value1);
        spx3_1.addSon(value2);
        SplitTreeNode spx3_2 = new SplitTreeNode(X3);
        spx3_2.addSon(value3);
        spx3_2.addSon(value4);
        SplitTreeNode spx3_3 = new SplitTreeNode(X3);
        spx3_3.addSon(value6);
        spx3_3.addSon(value7);
        SplitTreeNode spx2_1 = new SplitTreeNode(X2);
        spx2_1.addSon(spx3_1);
        spx2_1.addSon(spx3_2);
        SplitTreeNode spx2_2 = new SplitTreeNode(X2);
        spx2_2.addSon(value5);
        spx2_2.addSon(spx3_3);
        SplitTreeNode spx1 = new SplitTreeNode(X1);
        spx1.addSon(spx2_1);
        spx1.addSon(spx2_2);

        //List node
        ListTreeNode list = new ListTreeNode();
        list.addChild(spx1);
        list.addChild(potNode);

        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(list);
        //instance.print(0);
        //double expResult = 0.0;
        double result = instance.totalPotential();

        System.out.println("Total potential: "+result);
       // assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
       // fail("The test case is a prototype.");
    }

   @Ignore
    @Test
    public void testPerformance() throws ParseException, IOException{

        Network b=Network.read("D:/PROYECTOS/2010/VariableElimination/alarm.elv");
        Vector list = b.getInitialRelations().getRelations();

        ListTreeNode listRoot = new ListTreeNode();
        learningRPT instance = new learningRPT(0.001,0.02);

        Vector<PotentialTree> netTree = new Vector<PotentialTree>();
        long size2 = 0;

        for(int i=0; i<list.size();i++){
            listRoot.addChild(instance.factorize(((Relation)list.get(i)).getValues()));
            netTree.add(new PotentialTree(((Relation)list.get(i)).getValues()));
            size2 += netTree.get(i).getSize();
        }

        long size = listRoot.getTreeSize();

        // System.out.println("Size of the resulting recursive tree: "+size+" nodes.");

        // System.out.println("Size of the resulting probability trees: "+size2+" nodes.");

        System.out.println("alarm Diferencia de tamaño entre recursivos y probability trees: "+ (size - size2));

        b=Network.read("D:/PROYECTOS/2010/VariableElimination/asia.elv");
        list = b.getInitialRelations().getRelations();

        listRoot = new ListTreeNode();
        instance = new learningRPT(0.001,0.02);

        netTree = new Vector<PotentialTree>();
        size2 = 0;

        for(int i=0; i<list.size();i++){
            listRoot.addChild(instance.factorize(((Relation)list.get(i)).getValues()));
            netTree.add(new PotentialTree(((Relation)list.get(i)).getValues()));
            size2 += netTree.get(i).getSize();
        }

        size = listRoot.getTreeSize();

        // System.out.println("Size of the resulting recursive tree: "+size+" nodes.");

        // System.out.println("Size of the resulting probability trees: "+size2+" nodes.");

        System.out.println("asia Diferencia de tamaño entre recursivos y probability trees: "+ (size - size2));

                b=Network.read("D:/PROYECTOS/2010/VariableElimination/alarm.elv");
        list = b.getInitialRelations().getRelations();

        listRoot = new ListTreeNode();
        instance = new learningRPT(0.001,0.02);

        netTree = new Vector<PotentialTree>();
        size2 = 0;

        for(int i=0; i<list.size();i++){
            listRoot.addChild(instance.factorize(((Relation)list.get(i)).getValues()));
            netTree.add(new PotentialTree(((Relation)list.get(i)).getValues()));
            size2 += netTree.get(i).getSize();
        }

        size = listRoot.getTreeSize();

        // System.out.println("Size of the resulting recursive tree: "+size+" nodes.");

        // System.out.println("Size of the resulting probability trees: "+size2+" nodes.");

        System.out.println("Barley Diferencia de tamaño entre recursivos y probability trees: "+ (size - size2));

                b=Network.read("D:/PROYECTOS/2010/VariableElimination/alarm.elv");
        list = b.getInitialRelations().getRelations();

        listRoot = new ListTreeNode();
        instance = new learningRPT(0.001,0.02);

        netTree = new Vector<PotentialTree>();
        size2 = 0;

        for(int i=0; i<list.size();i++){
            listRoot.addChild(instance.factorize(((Relation)list.get(i)).getValues()));
            netTree.add(new PotentialTree(((Relation)list.get(i)).getValues()));
            size2 += netTree.get(i).getSize();
        }

        size = listRoot.getTreeSize();

        // System.out.println("Size of the resulting recursive tree: "+size+" nodes.");

        // System.out.println("Size of the resulting probability trees: "+size2+" nodes.");

        System.out.println("pedigree4 Diferencia de tamaño entre recursivos y probability trees: "+ (size - size2));
        //listRoot.print(0);

                b=Network.read("D:/PROYECTOS/2010/VariableElimination/alarm.elv");
        list = b.getInitialRelations().getRelations();

        listRoot = new ListTreeNode();
        instance = new learningRPT(0.001,0.02);

        netTree = new Vector<PotentialTree>();
        size2 = 0;

        for(int i=0; i<list.size();i++){
            listRoot.addChild(instance.factorize(((Relation)list.get(i)).getValues()));
            netTree.add(new PotentialTree(((Relation)list.get(i)).getValues()));
            size2 += netTree.get(i).getSize();
        }

        size = listRoot.getTreeSize();

        // System.out.println("Size of the resulting recursive tree: "+size+" nodes.");

        // System.out.println("Size of the resulting probability trees: "+size2+" nodes.");

        System.out.println("alarm Diferencia de tamaño entre recursivos y probability trees: "+ (size - size2));
    }

        @Ignore
    @Test
    public void testPerformanceInference() throws ParseException, IOException, InvalidEditException{

        FileInputStream fis = new FileInputStream("D:/PROYECTOS/2010/VariableElimination/alarm.elv");
        Bnet b = new Bnet(fis);

        System.out.println("Potential Tree");

        VEWithPotentialTree veTree = new VEWithPotentialTree(b);
        veTree.obtainInterest();
        veTree.propagate("tree.txt");

        System.out.println("Recursivos");

        VEwithRPT veRecursiveTree = new VEwithRPT(b);
        veRecursiveTree.obtainInterest();
        veRecursiveTree.propagate("recursivetree.txt");


        System.out.println("Binary trees");
        Evidence e = new Evidence();
        VEWithPotentialBPTree veBinaryTree = new VEWithPotentialBPTree(b, e);
        veBinaryTree.obtainInterest();
        veBinaryTree.propagate("recursivetree.txt");

    }

        @Ignore
    @Test
    public void testGenerateRandomRPT(){
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Vector<Node> variables = new Vector<Node>();
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);
        variables.add(X1);
        variables.add(X2);
        variables.add(X3);
        instance.setRandomRPT(variables,variables.size(),0.9,0.5);
        instance.print(0);
    }

       @Ignore
    @Test
    public void testRPTtoPotTree(){
        RecursivePotentialTree aux = this.createNetwork3();
        PotentialTree result = aux.RPTtoPotentialTree();
        result.print();
    }

    @Ignore
    @Test
    public void testGeneratepoisson(){
        RecursivePotentialTree aux = this.createNetwork3();
        for(int i=0; i<100; i++){
            int valor = aux.poisson();
            System.out.println("El valor obtenido es: "+valor);
        }
    }

    @Ignore
    @Test
    public void testGenerateRandomTree(){
      RecursivePotentialTree instance = new RecursivePotentialTree();
      Vector<Node> variables = new Vector<Node>();
      variables.add(X1);
      variables.add(X2);
      variables.add(X3);
      //for(int i=0; i<100; i++){
        instance.setRandomRPT(variables, variables.size(), 0.5, 0.5);
        instance.print(2);
      //}

    }

    @Ignore
    @Test
    public void testNormalizeToConditional(){
        RecursivePotentialTree tree = this.createNetwork2();
        tree.print(0);
        Vector<Node> variables = new Vector<Node>();
        variables.add(X1);
        variables.add(X2);
        variables.add(X3);
        tree.normalizeToConditional(variables);
        tree.print(0);
    }


        /**
     * Test of getValue method, of class RecursivePotentialTree.
     */
    @Ignore
    @Test
    public void testGetValue2() {
        System.out.println("getValue");
        //Variables
        Node A = new FiniteStates("A", 2);
        Node B = new FiniteStates("B", 2);
        Node C = new FiniteStates("C", 2);

        //Value Nodes
        ValueTreeNode value1 = new ValueTreeNode(0.4);
        ValueTreeNode value2 = new ValueTreeNode(0.6);
        ValueTreeNode value3 = new ValueTreeNode(0.1);
        ValueTreeNode value4 = new ValueTreeNode(0.4);
        ValueTreeNode value5 = new ValueTreeNode(0.3);
        ValueTreeNode value6 = new ValueTreeNode(1);
        ValueTreeNode value7 = new ValueTreeNode(2);

        //Split nodes
        SplitTreeNode spxC = new SplitTreeNode(C);
        spxC.addSon(value1);
        spxC.addSon(value2);
        SplitTreeNode spxC_2 = new SplitTreeNode(C);
        spxC_2.addSon(value3);
        spxC_2.addSon(value4);
        SplitTreeNode spxB = new SplitTreeNode(B);
        spxB.addSon(spxC);
        spxB.addSon(spxC_2);
        SplitTreeNode spxB_2 = new SplitTreeNode(B);
        spxB_2.addSon(value6);
        spxB_2.addSon(value7);
        SplitTreeNode spxA = new SplitTreeNode(A);
        spxA.addSon(spxB);
        spxA.addSon(value5);


        //List node
        ListTreeNode list = new ListTreeNode();
        list.addChild(spxA);
        list.addChild(spxB_2);

        //Build the tree and print it
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setValues(list);
        //instance.print(0);

        //Configuration X1=0, X2=0
        Vector<Node> variables = new Vector<Node>();
        variables.add(A);
        variables.add(B);
        Configuration tocheck = new Configuration(variables);
        double result = instance.getValue(tocheck);
        System.out.println("Resultado para "+tocheck.toString()+" de variables: "+variables.get(0).getName()+","+variables.get(1).getName()+" es "+ result);
        tocheck.nextConfiguration();
        result = instance.getValue(tocheck);
        System.out.println("Resultado para "+tocheck.toString()+" de variables: "+variables.get(0).getName()+","+variables.get(1).getName()+" es "+ result);
        tocheck.nextConfiguration();
        result = instance.getValue(tocheck);
        System.out.println("Resultado para "+tocheck.toString()+" de variables: "+variables.get(0).getName()+","+variables.get(1).getName()+" es "+ result);
        tocheck.nextConfiguration();
        result = instance.getValue(tocheck);
        System.out.println("Resultado para "+tocheck.toString()+" de variables: "+variables.get(0).getName()+","+variables.get(1).getName()+" es "+ result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
        variables = new Vector<Node>();
        variables.add(A);
        tocheck = new Configuration(variables);
        result = instance.getValue(tocheck);
        System.out.println("Resultado para "+tocheck.toString()+" de variable: "+variables.get(0).getName()+" es "+ result);
        tocheck.nextConfiguration();
        result = instance.getValue(tocheck);
        System.out.println("Resultado para "+tocheck.toString()+" de variables: "+variables.get(0).getName()+" es "+ result);
    }

    @Ignore
    @Test
    public void testgenerateBnet() throws ParseException, IOException, InvalidEditException{

        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/Diabetes.elv");
        Bnet b = new Bnet(fis);

        System.out.println(b.getNodeList().size());
        NodeList structure = b.getNodeList();
        RecursivePotentialTree instance = new RecursivePotentialTree();
        System.out.println("TRANSFORMANDO RELACIONES A RECURSIVOS...");

        Bnet result = instance.generateBnet(structure, 0.7, 0.8);

        System.out.println("TRANSFORMANDO RELACIONES A POTENTIAL TREE...");

        Vector<Relation> relations = result.getRelationList();
        System.out.println("NUMERO DE RELACIONES: "+relations.size());
        Vector<Relation> newRelations = new Vector<Relation>();
        for(int i=0; i<relations.size(); i++){
            Potential aux = ((RecursivePotentialTree)relations.get(i).getValues()).RPTtoPotentialTree();
            aux.setVariables(relations.get(i).getVariables().getNodes());
            newRelations.add(new Relation(aux));
            System.out.println("variables en relacion: "+newRelations.get(i).getVariables().size()+" y deberian ser: "+relations.get(i).getVariables().size());
        }
        result.setRelationList(newRelations);
        
        System.out.println("ESCRIBIENDO EN FICHERO...");
        FileWriter f;
        f = new FileWriter("artificialDiabetesRPT.elv");
        result.saveBnet(f);
        f.close();
        System.out.println("FIN");
    }

    @Ignore
    @Test
    public void testgenerateBnet2(){

        Node A = new FiniteStates("A", 2);
        Node B = new FiniteStates("B", 2);
        Node C = new FiniteStates("C", 2);
        LinkList p = new LinkList();
        p.insertLink(new Link(A,B));
        p.insertLink(new Link(C,B));
        B.setParents(p);
        LinkList q = new LinkList();
        q.insertLink(new Link(A,C));
        C.setParents(q);
        Vector<Node> variables = new Vector<Node>();
        variables.add(A);
        variables.add(B);
        variables.add(C);


        NodeList structure = new NodeList(variables);
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Bnet result = instance.generateBnet(structure, 0.8, 0.7);
        Vector<Relation> relations = result.getRelationList();
        for(int i=0; i<relations.size(); i++){
            System.out.println("   ***   ");
            relations.get(i).print();
            System.out.println("   ***   ");
        }
    }


   @Ignore
    @Test
    public void testgenerateBnetaPT(){

        Node A = new FiniteStates("A", 2);
        Node B = new FiniteStates("B", 2);
        LinkList p = new LinkList();
        p.insertLink(new Link(A,B));
        B.setParents(p);
        Vector<Node> variables = new Vector<Node>();
        variables.add(A);
        variables.add(B);
        NodeList structure = new NodeList(variables);
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Bnet result = instance.generateBnet(structure, 0.7, 0.8);
        Vector<Relation> relations = result.getRelationList();
        for(int i=0; i<relations.size(); i++){
            System.out.println("   ***   ");
            relations.get(i).print();
            System.out.println("   ***   ");
        }
    }

   @Ignore
   @Test
    public void testcreateRandomRPT(){
        Node X1 = new FiniteStates("A", 2);
        Node X2 = new FiniteStates("B", 2);
        Node X3 = new FiniteStates("C", 2);
        Node X4 = new FiniteStates("D", 2);
        Node X5 = new FiniteStates("E", 2);
        Node X6 = new FiniteStates("F", 2);
        Node X7 = new FiniteStates("G", 2);
        Node X8 = new FiniteStates("H", 2);
        Node X9 = new FiniteStates("I", 2);
        Node X10 = new FiniteStates("J", 2);
        Node X11 = new FiniteStates("K", 2);
        Node X12 = new FiniteStates("L", 2);
        Node X13 = new FiniteStates("M", 2);
        Node X14 = new FiniteStates("N", 2);
        Node X15 = new FiniteStates("O", 2);
        Vector<Node> variables = new Vector<Node>();
        variables.add(X1);
        variables.add(X2);
        variables.add(X3);
        variables.add(X4);
        variables.add(X5);
        variables.add(X6);
        variables.add(X7);
        variables.add(X8);
        variables.add(X9);
        variables.add(X10);
        variables.add(X11);
        variables.add(X12);
        variables.add(X13);
        variables.add(X14);
        variables.add(X15);
        Vector<Node> vars = new Vector<Node>();
        vars.add(X11);
        vars.add(X2);
        vars.add(X3);
        vars.add(X4);
        vars.add(X5);
        vars.add(X14);
        vars.add(X7);
        vars.add(X9);
        vars.add(X15);
        vars.add(X10);
        vars.add(X1);
        vars.add(X12);
        vars.add(X13);
        vars.add(X6);
        vars.add(X8);
        int depth = variables.size();
        double probSplit = 0.9;
        double probPotential = 0.7;
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setRandomRPT(variables,depth,probSplit,probPotential);
        System.out.println("variables: "+instance.getVariables().size());
        Potential pot = instance;
        for(int i=0; i<instance.getVariables().size()-1; i++){
            System.out.println("eliminamos "+vars.get(i).getName());
             pot = pot.addVariable(vars.get(i));
        }
        pot.normalize();
        pot.print();


    }

   @Ignore
    @Test
    public void testPruebaBnet1() throws ParseException, IOException, InvalidEditException{

        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/Barley.elv");
        Bnet b = new Bnet(fis);
        System.out.println("pt\trpt");
        for(double j=0; j<=1; j+=0.1){
            for(int k=0; k<5; k++){

                NodeList structure = b.getNodeList();
                RecursivePotentialTree instance = new RecursivePotentialTree();
//                System.out.println("TRANSFORMANDO RELACIONES A RECURSIVOS...");
                Bnet result = instance.generateBnet(structure, j, 0.8);

//                System.out.println("VE con recursivos...");
                VEwithRPT veRPT = new VEwithRPT(result);
                veRPT.obtainInterest();
                veRPT.statistics.setFileName("statsVErptDIABETES.txt");
                long timerpt1 = System.currentTimeMillis();
                veRPT.propagate("VErptDIABETES.txt");
                long timerpt2 = System.currentTimeMillis();
                veRPT.statistics.printOperationsAndSizes();

//                System.out.println("TRANSFORMANDO RELACIONES A POTENTIAL TREE...");
                Vector<Relation> relations = result.getRelationList();
//                System.out.println("NUMERO DE VAriables: "+result.getNodeList().size());
                Vector<Relation> newRelations = new Vector<Relation>();
                for(int i=0; i<relations.size(); i++){
                    Potential aux = ((RecursivePotentialTree)relations.get(i).getValues()).RPTtoPotentialTree();
                    aux.setVariables(relations.get(i).getVariables().getNodes());
                    newRelations.add(new Relation(aux));
                }
                result.setRelationList(newRelations);

//                System.out.println("VE CON PROBABILITY TREES:");
                VEWithPotentialTree vePT = new VEWithPotentialTree(result);
                vePT.obtainInterest();
                vePT.statistics.setFileName("statsVEptDIABETES.txt");
                long timept1 = System.currentTimeMillis();
                vePT.propagate("VEptDIABETES.txt");
                long timept2 = System.currentTimeMillis();
                vePT.statistics.printOperationsAndSizes();

                System.out.println((timept2-timept1)+"\t"+(timerpt2-timerpt1));
           }
            System.out.println("");
        }
//

        System.out.println("FIN");
    }


     @Ignore
    @Test
    public void testPruebaBnet2() throws ParseException, IOException, InvalidEditException{

        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/alarm.elv");
        Bnet b = new Bnet(fis);
        System.out.println("pt\trpt");

        NodeList structure = b.getNodeList();
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Bnet result = instance.generateBnet(structure, 0.8, 0.8);

       

        for(int k=0; k<37; k++){

             VEwithRPT veRPT = new VEwithRPT(result);

            NodeList nodelist = new NodeList();
            nodelist.insertNode(structure.getNodes().get(k));

            veRPT.setInterest(nodelist);
            veRPT.statistics.setFileName("statsVErpt.txt");
            long timerpt1 = System.currentTimeMillis();
            veRPT.propagate("VErptAUX.txt");
            long timerpt2 = System.currentTimeMillis();


            ((RecursivePotentialTree)veRPT.results.get(0)).refreshVariables();
            System.out.println(((RecursivePotentialTree)veRPT.results.get(0)).getVariables().size());
            if(((RecursivePotentialTree)veRPT.results.get(0)).getVariables().size()>0){
                ((RecursivePotentialTree)veRPT.results.get(0)).RPTtoPotentialTable().print();
            }else{
                ((RecursivePotentialTree)veRPT.results.get(0)).print(0);
            }
        }


        Vector<Relation> relations = result.getRelationList();
        Vector<Relation> newRelations = new Vector<Relation>();
        for(int i=0; i<relations.size(); i++){
        Potential aux = ((RecursivePotentialTree)relations.get(i).getValues()).RPTtoPotentialTree();
        aux.setVariables(relations.get(i).getVariables().getNodes());
        newRelations.add(new Relation(aux));
        }
        result.setRelationList(newRelations);

        System.out.println("VE CON PROBABILITY TREES:");
       

        for(int k=0; k<37; k++){

             VEWithPotentialTree vePT = new VEWithPotentialTree(result);
            NodeList nodelist = new NodeList();
            nodelist.insertNode(structure.getNodes().get(k));

            vePT.setInterest(nodelist);
            vePT.statistics.setFileName("statsVEpt.txt");
            long timept1 = System.currentTimeMillis();
            vePT.propagate("VEptAUX.txt");
            long timept2 = System.currentTimeMillis();

            ((Potential)vePT.results.get(0)).print();

            }
            //

            System.out.println("FIN");
    }


     @Ignore
    @Test
    public void testPruebaBnet3() throws ParseException, IOException, InvalidEditException{

        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/alarm.elv");
        Bnet b = new Bnet(fis);
        System.out.println("pt\trpt");

        NodeList structure = b.getNodeList();
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Bnet result = instance.generateBnet(structure, 0.8, 0.8);

        Vector<Relation> relationsAux = result.getRelationList();
        for(int i=0; i<relationsAux.size(); i++){
            relationsAux.get(i).print();
        }

        System.out.println("EMPEZAMOS *******************************************************************************************");

             VEwithRPT veRPT = new VEwithRPT(result);

             veRPT.obtainInterest();
            veRPT.statistics.setFileName("statsVErpt.txt");

            veRPT.propagate("VErptAUX.txt");

//            veRPT.statistics.printOperationsAndSizes();



            for(int i=0; i<veRPT.results.size(); i++){
            ((RecursivePotentialTree)veRPT.results.get(i)).refreshVariables();
            System.out.println(((RecursivePotentialTree)veRPT.results.get(i)).getVariables().size());
            if(((RecursivePotentialTree)veRPT.results.get(i)).getVariables().size()>0){
                ((RecursivePotentialTree)veRPT.results.get(i)).RPTtoPotentialTable().print();
            }else{
                ((RecursivePotentialTree)veRPT.results.get(i)).print(0);
            }
            }


            System.out.println("FIN");
    }


    @Ignore
    @Test
    public void testPruebaBnet4() throws ParseException, IOException, InvalidEditException{

        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/Barley.elv");
        Bnet b = new Bnet(fis);
        System.out.println("pt\trpt");
        for(int k=0; k<10; k++){

                NodeList structure = b.getNodeList();
                RecursivePotentialTree instance = new RecursivePotentialTree();
//                System.out.println("TRANSFORMANDO RELACIONES A RECURSIVOS...");
                Bnet result = instance.generateBnet(structure, 0.001, 0.8);

//                System.out.println("VE con recursivos...");
                VEwithRPT veRPT = new VEwithRPT(result);
                veRPT.obtainInterest();
                veRPT.statistics.setFileName("statsVErpt.txt");
                long timerpt1 = System.currentTimeMillis();
                veRPT.propagate("VErpt.txt");
                long timerpt2 = System.currentTimeMillis();
                veRPT.statistics.printOperationsAndSizes();

//                System.out.println("TRANSFORMANDO RELACIONES A POTENTIAL TREE...");
                Vector<Relation> relations = result.getRelationList();
//                System.out.println("NUMERO DE VAriables: "+result.getNodeList().size());
                Vector<Relation> newRelations = new Vector<Relation>();
                for(int i=0; i<relations.size(); i++){
                    Potential aux = ((RecursivePotentialTree)relations.get(i).getValues()).RPTtoPotentialTree();
                    aux.setVariables(relations.get(i).getVariables().getNodes());
                    newRelations.add(new Relation(aux));
                }
                result.setRelationList(newRelations);

//                System.out.println("VE CON PROBABILITY TREES:");
                VEWithPotentialTree vePT = new VEWithPotentialTree(result);
                vePT.obtainInterest();
                vePT.statistics.setFileName("statsVEpt.txt");
                long timept1 = System.currentTimeMillis();
                vePT.propagate("VEpt.txt");
                long timept2 = System.currentTimeMillis();

                System.out.println((timept2-timept1)+"\t"+(timerpt2-timerpt1));
           }
            System.out.println("");
//

        System.out.println("FIN");
    }


      @Ignore
    @Test
    public void testPruebaBnet5() throws ParseException, IOException, InvalidEditException{

        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/Barley.elv");
        Bnet b = new Bnet(fis);
        System.out.println("pt\trpt");

                NodeList structure = b.getNodeList();
                RecursivePotentialTree instance = new RecursivePotentialTree();
//                System.out.println("TRANSFORMANDO RELACIONES A RECURSIVOS...");
                Bnet result = instance.generateBnet(structure, 0.001, 0.8);


//                System.out.println("TRANSFORMANDO RELACIONES A POTENTIAL TREE...");
                Vector<Relation> relations = result.getRelationList();
//                System.out.println("NUMERO DE VAriables: "+result.getNodeList().size());
                Vector<Relation> newRelations = new Vector<Relation>();
                for(int i=0; i<relations.size(); i++){
                    ((RecursivePotentialTree)relations.get(i).getValues()).print(0);
                }

        System.out.println("FIN");
    }

    @Ignore
    @Test
    public void testPruebaBnet6() throws ParseException, IOException, InvalidEditException, ClassNotFoundException{

        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/BarleyModificada.elv");
        Bnet b = new Bnet(fis);
        System.out.println("RPTs generados:");

        NodeList structure = b.getNodeList();
        RecursivePotentialTree instance = new RecursivePotentialTree();
        Bnet result = instance.generateBnet(structure, 0.5, 0.8);

//      Para escribir el objeto:
        ObjectOutputStream salida=new ObjectOutputStream(new FileOutputStream("rptBarleyMod.obj"));
        salida.writeObject(result);
        salida.close();

//        Para leer el objeto:
//        ObjectInputStream entrada=new ObjectInputStream(new FileInputStream("rpt.obj"));
//        RecursivePotentialTree obj1=(RecursivePotentialTree)entrada.readObject();


        Vector<Relation> relations = result.getRelationList();
        for(int i=0; i<relations.size(); i++){
            ((RecursivePotentialTree)relations.get(i).getValues()).print(0);
            System.out.println();
            System.out.println("****///*****////******///******");
            System.out.println();
        }

        System.out.println("FIN");
    }

    @Ignore
    @Test
    public void testPruebaBnet7() throws ParseException, IOException, InvalidEditException, ClassNotFoundException{

//        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/asia.elv");
//        Bnet b = new Bnet(fis);
//        System.out.println("RPTs generados:");
//
//        NodeList structure = b.getNodeList();
//        RecursivePotentialTree instance = new RecursivePotentialTree();
//        Bnet result = instance.generateBnet(structure, 0.5, 0.8);
//
////      Para escribir el objeto:
//        ObjectOutputStream salida=new ObjectOutputStream(new FileOutputStream("rpt.txt"));
//        salida.writeObject(result);
//        salida.close();

//        Para leer el objeto:
        ObjectInputStream entrada=new ObjectInputStream(new FileInputStream("rptBarleyMod.obj"));
        Bnet result=(Bnet)entrada.readObject();


//        Vector<Relation> relations = result.getRelationList();
//        for(int i=0; i<relations.size(); i++){
//            ((RecursivePotentialTree)relations.get(i).getValues()).print(0);
//            System.out.println();
//            System.out.println("****///*****////******///******");
//            System.out.println();
//        }

        VEwithRPT veRPT = new VEwithRPT(result);
        veRPT.obtainInterest();
        veRPT.statistics.setFileName("statsVErptTRAZA.txt");
        long timerpt1 = System.currentTimeMillis();
        veRPT.propagate("VErptTRAZA.txt");
        long timerpt2 = System.currentTimeMillis();
        veRPT.statistics.printOperationsAndSizes();

        System.out.println("FIN");
    }

    @Ignore
    @Test
    public void testPruebaBnet8() throws ParseException, IOException, InvalidEditException{

        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/BarleyModificada.elv");
        Bnet b = new Bnet(fis);

//      Para escribir el objeto:
        ObjectOutputStream salida;

        System.out.println("pt\trpt");
        for(double j=0; j<=1; j+=0.1){
            for(int k=0; k<5; k++){

                NodeList structure = b.getNodeList();
                RecursivePotentialTree instance = new RecursivePotentialTree();
//                System.out.println("TRANSFORMANDO RELACIONES A RECURSIVOS...");
                Bnet result = instance.generateBnet(structure, j, 0.8);

//                System.out.println("VE con recursivos...");
                VEwithRPT veRPT = new VEwithRPT(result);
                veRPT.obtainInterest();
                veRPT.statistics.setFileName("statsVErpt.txt");
                long timerpt1 = System.currentTimeMillis();
                veRPT.propagate("VErpt.txt");
                long timerpt2 = System.currentTimeMillis();
                veRPT.statistics.printOperationsAndSizes();

                Integer iteration = k;
                Double splitParam = j;
                String name = "RPT_";
                name = name.concat(iteration.toString());
                name = name.concat("_");
                name = name.concat(splitParam.toString());
                salida=new ObjectOutputStream(new FileOutputStream(name));
                salida.writeObject(result);
                salida.close();

//                System.out.println("TRANSFORMANDO RELACIONES A POTENTIAL TREE...");
                Vector<Relation> relations = result.getRelationList();
//                System.out.println("NUMERO DE VAriables: "+result.getNodeList().size());
                Vector<Relation> newRelations = new Vector<Relation>();
                for(int i=0; i<relations.size(); i++){
                    Potential aux = ((RecursivePotentialTree)relations.get(i).getValues()).RPTtoPotentialTree();
                    aux.setVariables(relations.get(i).getVariables().getNodes());
                    newRelations.add(new Relation(aux));
                }
                result.setRelationList(newRelations);

//                System.out.println("VE CON PROBABILITY TREES:");
                VEWithPotentialTree vePT = new VEWithPotentialTree(result);
                vePT.obtainInterest();
                vePT.statistics.setFileName("statsVEpt.txt");
                long timept1 = System.currentTimeMillis();
                vePT.propagate("VEpt.txt");
                long timept2 = System.currentTimeMillis();

                System.out.println((timept2-timept1)+"\t"+(timerpt2-timerpt1));



           }
            System.out.println();
        }
            System.out.println("");
//

        System.out.println("FIN");
    }

        @Ignore
    @Test
    public void testPruebaBnet9() throws ParseException, IOException, InvalidEditException, ClassNotFoundException{

//        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/asia.elv");
//        Bnet b = new Bnet(fis);
//        System.out.println("RPTs generados:");
//
//        NodeList structure = b.getNodeList();
//        RecursivePotentialTree instance = new RecursivePotentialTree();
//        Bnet result = instance.generateBnet(structure, 0.5, 0.8);
//
////      Para escribir el objeto:
//        ObjectOutputStream salida=new ObjectOutputStream(new FileOutputStream("rpt.txt"));
//        salida.writeObject(result);
//        salida.close();

//        Para leer el objeto:
        ObjectInputStream entrada=new ObjectInputStream(new FileInputStream("RPT_0_0.0"));
        Bnet result=(Bnet)entrada.readObject();


        Vector<Relation> relations = result.getRelationList();
        for(int i=0; i<relations.size(); i++){
            ((RecursivePotentialTree)relations.get(i).getValues()).print(0);
            System.out.println();
            System.out.println("****///*****////******///******");
            System.out.println();
        }


        System.out.println("SIGUIENTE RPT GENERADO **************************************************************");
        System.out.println("SIGUIENTE RPT GENERADO **************************************************************");
        System.out.println("SIGUIENTE RPT GENERADO **************************************************************");

        entrada=new ObjectInputStream(new FileInputStream("RPT_1_0.7"));
        result=(Bnet)entrada.readObject();


        relations = result.getRelationList();
        for(int i=0; i<relations.size(); i++){
            ((RecursivePotentialTree)relations.get(i).getValues()).print(0);
            System.out.println();
            System.out.println("****///*****////******///******");
            System.out.println();
        }


        System.out.println("FIN");
    }

  //  @Ignore
    @Test
    public void testPruebaBnet10() throws ParseException, IOException, InvalidEditException{

        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/Barley.elv");
        Bnet b = new Bnet(fis);

//      Para escribir el objeto:
        ObjectOutputStream salida;

        System.out.println("pt\trpt");
        for(double j=0; j<=1; j+=0.1){
            System.out.println();
            System.out.println("*Prob Split: *"+j);
            System.out.println();
            for(int n=2; n<10; n++){
                System.out.println();
                System.out.println("*Numero de estados: *"+n);
                System.out.println();
                for(int k=0; k<5; k++){

                    NodeList structure = b.getNodeList();

                    Vector<Node> auxList = structure.getNodes();
                    for(int i=0; i<auxList.size();i++){
                        ((FiniteStates)auxList.get(i)).setNumStates(n);
                        auxList.set(i, auxList.get(i));
                    }
                    structure.setNodes(auxList);



                    RecursivePotentialTree instance = new RecursivePotentialTree();
    //                System.out.println("TRANSFORMANDO RELACIONES A RECURSIVOS...");
                    Bnet result = instance.generateBnet(structure, j, 0.8);

    //                System.out.println("VE con recursivos...");
                    VEwithRPT veRPT = new VEwithRPT(result);
                    veRPT.obtainInterest();
                    veRPT.statistics.setFileName("statsVErpt.txt");
                    long timerpt1 = System.currentTimeMillis();
                    veRPT.propagate("VErpt.txt");
                    long timerpt2 = System.currentTimeMillis();
                    veRPT.statistics.printOperationsAndSizes();

                    Integer iteration = k;
                    Integer states = k;
                    Double splitParam = j;
                    String name = "RPT3_";
                    name = name.concat(iteration.toString());
                    name = name.concat("_");
                    name = name.concat(splitParam.toString());
                    name = name.concat("_");
                    name = name.concat(states.toString());
                    salida=new ObjectOutputStream(new FileOutputStream(name));
                    salida.writeObject(result);
                    salida.close();

    //                System.out.println("TRANSFORMANDO RELACIONES A POTENTIAL TREE...");
                    Vector<Relation> relations = result.getRelationList();
    //                System.out.println("NUMERO DE VAriables: "+result.getNodeList().size());
                    Vector<Relation> newRelations = new Vector<Relation>();
                    for(int i=0; i<relations.size(); i++){
                        Potential aux = ((RecursivePotentialTree)relations.get(i).getValues()).RPTtoPotentialTree();
                        aux.setVariables(relations.get(i).getVariables().getNodes());
                        newRelations.add(new Relation(aux));
                    }
                    result.setRelationList(newRelations);

    //                System.out.println("VE CON PROBABILITY TREES:");
                    VEWithPotentialTree vePT = new VEWithPotentialTree(result);
                    vePT.obtainInterest();
                    vePT.statistics.setFileName("statsVEpt.txt");
                    long timept1 = System.currentTimeMillis();
                    vePT.propagate("VEpt.txt");
                    long timept2 = System.currentTimeMillis();

                    System.out.println((timept2-timept1)+"\t"+(timerpt2-timerpt1));



               }
                System.out.println();
            }

        }
            System.out.println("");
//

        System.out.println("FIN");
    }

}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;

import elvira.LinkList;
import elvira.Node;
import elvira.FiniteStates;
import elvira.Configuration;
import elvira.potential.Potential;
import elvira.potential.PotentialTable;
import elvira.potential.PotentialTree;
import elvira.Link;
import elvira.NodeList;
import elvira.Bnet;
import elvira.Relation;
import java.io.FileInputStream;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

/**
 *
 * @author Cora
 */
public class learningRPTTest {

    Potential pot;
    Vector<Float> V = new Vector<Float>();

    public learningRPTTest() {
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

    public Potential setPotential(){
        String[] states={"0","1"};
        Node A = new FiniteStates("A",states);
        Node B = new FiniteStates("B",states);
        Vector<Node> vars = new Vector<Node>();
        vars.add(A);
        vars.add(B);
        Configuration conf = new Configuration(vars);
        pot = new PotentialTable(vars);
        double[] values={0.24,0.06,0.35,0.35};
        for(int i=0; i<conf.possibleValues();i++){
            pot.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        return pot;
    }


    /**
     * Test of getConnectedComponents method, of class learningRPT.
     */
    @Ignore
    @Test
    public void testIsConected1() {
        System.out.println("isConected --> conected graph");

        Node a,b,c,d,e;
        String[] states={"0","1"};
        a = new FiniteStates("a",states);
        b = new FiniteStates("b",states);
        c = new FiniteStates("c",states);
        d = new FiniteStates("d",states);
        e = new FiniteStates("e",states);

        LinkList list = new LinkList();
        list.insertLink(new Link(a,b));
        list.insertLink(new Link(a,c));
        list.insertLink(new Link(c,d));
        list.insertLink(new Link(e,d));

        learningRPT instance = new learningRPT(0.1, 0.5);

        Vector<Set<Node>> result = instance.getConnectedComponents(list,null);

        for(int i=0; i<result.size();i++){
            Iterator it = result.get(i).iterator();
            while(it.hasNext()){
                System.out.println("element: " + ((Node)it.next()).getName());
            }
            System.out.println("next cluster");
        }
    }

    /**
     * Test of getConnectedComponents method, of class learningRPT.
     */
    @Ignore
    @Test
    public void testIsConected2() {
        System.out.println("isConected --> desconected graph");

        Node a,b,c,d,e;
        String[] states={"0","1"};
        a = new FiniteStates("a",states);
        b = new FiniteStates("b",states);
        c = new FiniteStates("c",states);
        d = new FiniteStates("d",states);
        e = new FiniteStates("e",states);

        LinkList list = new LinkList();
        list.insertLink(new Link(a,b));
        list.insertLink(new Link(c,d));
        list.insertLink(new Link(e,d));

        learningRPT instance = new learningRPT(0.1, 0.5);

        Vector<Set<Node>> result = instance.getConnectedComponents(list,null);

        for(int i=0; i<result.size();i++){
            Iterator it = result.get(i).iterator();
            while(it.hasNext()){
                System.out.println("element: " + ((Node)it.next()).getName());
            }
            System.out.println("next cluster");
        }
    }


//    /**
//     * Test of factorizingAlgorithm method, of class learningRPT.
//     */
//    @Ignore
//    @Test
//    public void testFactorizingAlgorithm_Potential() {
//        System.out.println("factorizingAlgorithm");
//
//        String[] states={"0","1"};
//        Node A,B,C,D;
//        A = new FiniteStates("A", states);
//        B = new FiniteStates("B", states);
//        C = new FiniteStates("C", states);
//        D = new FiniteStates("D", states);
//
//        Vector<Node> vars = new Vector<Node>();
//        vars.add(A);
//        vars.add(B);
//        vars.add(C);
//        vars.add(D);
//
//        Potential pot = new PotentialTable(vars);
//
//        double[] values = {0.7,0.3,0.4,0.6,0.1,0.9,0.2,0.8,0.3,0.7,0.4,0.6,0.9,0.1,0.9,0.1};
//        Configuration conf = new Configuration(vars);
//        for(int i=0; i<values.length;i++){
//            pot.setValue(conf,values[i]);
//            conf.nextConfiguration();
//        }
//
//        learningRPT instance = new learningRPT();
//        Vector<Potential> result = instance.factorizingAlgorithm(pot);
//
//        for(int i=0; i<result.size(); i++){
//            System.out.println(" RESULTADO: ");
//            result.get(i).print();
//        }
//        // TODO review the generated test code and remove the default call to fail.
//        //fail("The test case is a prototype.");
//    }

    /**
     * Test of chooseVarByNeightbourhood method, of class learningRPT.
     */
    @Ignore
    @Test
    public void testChooseVarByNeightbourhood() {
        System.out.println("chooseVarByNeightbourhood");

        String[] states={"0","1"};
        Node A,B,C,D;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);

        Link AB, AD, BC, DC;
        AB = new Link(B, A);
        AB.setWidth(0.3);
        AD = new Link(D, A);
        AD.setWidth(0.2);
        BC = new Link(C, B);
        BC.setWidth(0.5);
        DC = new Link(C, D);
        DC.setWidth(0.8);

        LinkList graph = new LinkList();
        graph.insertLink(AB);
        graph.insertLink(AD);
        graph.insertLink(BC);
        graph.insertLink(DC);

        Vector<Node> vars = new Vector<Node>();
        vars.add(A);
        vars.add(B);
        vars.add(C);
        vars.add(D);

        learningRPT instance = new learningRPT(0.1, 0.5);
        Node result = instance.chooseVarByNeightbourhood(graph, vars);

        System.out.println("La variable a eliminar sería "+ result.getName());
    }

    /**
     * Test of buildGraph method, of class learningRPT.
     */
//    @Ignore
//    @Test
//    public void testBuildGraph() {
//        System.out.println("buildGraph");
//        Potential pot = null;
//        double epsilon = 0.0;
//        LinkList expResult = null;
//        LinkList result = learningRPT.buildGraph(pot, epsilon);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of crossEntropy method, of class learningRPT.
     */
    @Ignore
    @Test
    public void testCrossEntropy() {
        System.out.println("crossEntropy");
        Potential pi = null;
        Potential pj = null;
        Potential pij = null;
        double expResult = 0.0;
        double result = learningRPT.crossEntropy(pi, pj, pij);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getConnectedComponents method, of class learningRPT.
     */
    @Ignore
    @Test
    public void testIsConected() {
        System.out.println("isConected");
        LinkList list = null;
        Potential pot = null;
        learningRPT instance = new learningRPT(0.1, 0.5);
        Vector expResult = null;
        Vector result = instance.getConnectedComponents(list, pot.getVariables());
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }


//    /**
//     * Test of factorizingAlgorithm method, of class learningRPT.
//     */
//    @Ignore
//    @Test
//    public void testFactorizingAlgorithm() {
//        System.out.println("factorizingAlgorithm");
//        Potential potential = null;
//        learningRPT instance = new learningRPT();
//        Vector expResult = null;
//        Vector result = instance.factorizingAlgorithm(potential);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of factorize method, of class learningRPT.
     */
    @Ignore
    @Test
    public void testPotentialFactorization() {
        System.out.println("PotentialFactorization");


        String[] states={"0","1"};
        Node A,B,C,D;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);

        Vector<Node> vars = new Vector<Node>();
        vars.add(A);
        vars.add(B);
        vars.add(C);
        vars.add(D);

        Potential potential = new PotentialTable(vars);

        double[] values = {0.7,0.3,0.4,0.6,0.1,0.9,0.2,0.8,0.3,0.7,0.4,0.6,0.9,0.1,0.9,0.1};
        Configuration conf = new Configuration(vars);
        for(int i=0; i<values.length;i++){
            potential.setValue(conf,values[i]);
            conf.nextConfiguration();
        }

  

        // asc begin
        // el epsilon tiene que ser positivo:
        //Double epsilon = -22.18;
        Double epsilon = 0.001;

        learningRPT instance = new learningRPT(epsilon, 0.5);
        // asc end
        TreeNode treeResult = instance.factorize(potential);


        System.out.println();
        System.out.println(" RESULTADO FINAL: ");
        System.out.println();

        treeResult.print(0);

       
        RecursivePotentialTree recTree = new RecursivePotentialTree();
        recTree.setValues(treeResult);

        PotentialTable potab = recTree.getPotentialTable();
        potential.normalize();
        potential.print();
        potab.normalize();
        potab.print();

        Configuration confvars = new Configuration(vars);

        for(int i=0; i<=confvars.possibleValues(); i++){
            System.out.println("Valor "+i);
            System.out.println(potential.getValue(confvars) +" vs "+potab.getValue(confvars));
            confvars.nextConfiguration();
        }

    }

    /**
     * Test of factorize method, of class learningRPT.
     */
    @Ignore
    @Test
    public void testPotentialFactorization2() {
        System.out.println("PotentialFactorization");


        String[] states={"0","1"};
        Node A,B,C,D,E,F;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);
        E = new FiniteStates("E", states);
        F = new FiniteStates("F", states);

        Vector<Node> vars1 = new Vector<Node>();
        vars1.add(A);
        vars1.add(D);
        vars1.add(B);
        Potential potential1 = new PotentialTable(vars1);
        double[] values = {0.6,0.6,0.4,0.4,0.7,0.3,0.7,0.3};
        Configuration conf = new Configuration(vars1);
        for(int i=0; i<values.length;i++){
            potential1.setValue(conf,values[i]);
            conf.nextConfiguration();
        }

        Vector<Node> vars2 = new Vector<Node>();
        vars2.add(B);
        vars2.add(C);
        vars2.add(D);
        Potential potential2 = new PotentialTable(vars2);
        double[] values2 = {0.08,0.12,0.02,0.18,0.32,0.48,0.08,0.72};
        Configuration conf2 = new Configuration(vars2);
        for(int i=0; i<values2.length;i++){
            potential2.setValue(conf2,values2[i]);
            conf2.nextConfiguration();
        }

        Vector<Node> vars3 = new Vector<Node>();
        vars3.add(E);
        vars3.add(F);
        Potential potential3 = new PotentialTable(vars3);
        double[] values3 = {0.6,0.2,0.4,0.8};
        Configuration conf3 = new Configuration(vars3);
        for(int i=0; i<values3.length;i++){
            potential3.setValue(conf3,values3[i]);
            conf3.nextConfiguration();
        }

        Potential potential = potential1.combine(potential2);
        potential = potential.combine(potential3);

        //potential.print();

        

        //Double epsilon = 91.1;
        //Double epsilon = 0.001;
        Double epsilon = -0.0001;
        learningRPT instance = new learningRPT(epsilon, 0.5);
        TreeNode treeResult = instance.factorize(potential);


        System.out.println();
        System.out.println(" RESULTADO FINAL: ");
        System.out.println();

        treeResult.print(0);
        RecursivePotentialTree recTree = new RecursivePotentialTree();
        recTree.setValues(treeResult);

        PotentialTable potab = recTree.getPotentialTable();
        potential.normalize();
        potential.print();
        potab.normalize();
        potab.print();

        Configuration confvars = new Configuration(potential.getVariables());

        for(int i=0; i<=confvars.possibleValues(); i++){
            System.out.println("Valor "+i);
            System.out.println(potential.getValue(confvars) +" vs "+potab.getValue(confvars));
            confvars.nextConfiguration();
        }
    }


    /**
     * Test of buildConditionedGraph method, of class learningRPT.
     */
//    @Ignore
//    @Test
//    public void testBuildConditionedGraph() {
//        System.out.println("buildConditionedGraph");
//        Potential pot = null;
//        double epsilon = 0.0;
//        Vector<Node> varToCond = null;
//        LinkList expResult = null;
//        LinkList result = learningRPT.buildConditionedGraph(pot, epsilon, varToCond);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of conditionedCrossEntropy method, of class learningRPT.
     */
    @Ignore
    @Test
    public void testConditionedCrossEntropy() {
        System.out.println("conditionedCrossEntropy");
        Potential pi = null;
        Potential pj = null;
        Potential pij = null;
        Potential pijc = null;
        double expResult = 0.0;
        double result = learningRPT.conditionedCrossEntropy(pi, pj, pij, pijc);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////

    @Ignore
    @Test
    public void test1(){

        System.out.println(" --- TEST 1 --- ");
        String[] states={"0","1"};
        Node A,B,C;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);

        Vector<Node> vars1 = new Vector<Node>();
        vars1.add(A);
        vars1.add(B);
        Potential potential1 = new PotentialTable(vars1);
        double[] values = {0.8,0.4,0.2,0.6};
        Configuration conf = new Configuration(vars1);
        for(int i=0; i<values.length;i++){
            potential1.setValue(conf,values[i]);
            conf.nextConfiguration();
        }

        Vector<Node> vars2 = new Vector<Node>();
        vars2.add(C);

        Potential potential2 = new PotentialTable(vars2);
        double[] values2 = {0.7,0.3};
        Configuration conf2 = new Configuration(vars2);
        for(int i=0; i<values2.length;i++){
            potential2.setValue(conf2,values2[i]);
            conf2.nextConfiguration();
        }

        Potential potential = potential1.combine(potential2);
        potential.print();

        

        //Double epsilon = -0.0001;
        Double epsilon = 0.001;
        learningRPT instance = new learningRPT(epsilon, 0.5);
        TreeNode treeResult = instance.factorize(potential);


        System.out.println();
        System.out.println(" RESULTADO FINAL: ");
        System.out.println();

        treeResult.print(0);
        RecursivePotentialTree recTree = new RecursivePotentialTree();
        recTree.setValues(treeResult);

        PotentialTable potab = recTree.getPotentialTable();
        potential.normalize();
        //potential.print();
        potab.normalize();
        //potab.print();

        Configuration confvars = new Configuration(potential.getVariables());

        for(int i=0; i<=confvars.possibleValues(); i++){
            System.out.println("Valor "+i);
            System.out.println(potential.getValue(confvars) +" vs "+potab.getValue(confvars));
            confvars.nextConfiguration();
        }
    }

    @Ignore
    @Test
    public void test2(){

        System.out.println(" --- TEST 2 --- ");

        String[] states={"0","1"};
        Node A,B,C,D;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);

        Vector<Node> vars1 = new Vector<Node>();
        vars1.add(A);
        vars1.add(B);
        Potential potential1 = new PotentialTable(vars1);
        double[] values = {0.1,0.6,0.9,0.4};
        Configuration conf = new Configuration(vars1);
        for(int i=0; i<values.length;i++){
            potential1.setValue(conf,values[i]);
            conf.nextConfiguration();
        }

        Vector<Node> vars2 = new Vector<Node>();
        vars2.add(C);
        vars2.add(D);

        Potential potential2 = new PotentialTable(vars2);
        double[] values2 = {0.3,0.4,0.7,0.6};
        Configuration conf2 = new Configuration(vars2);
        for(int i=0; i<values2.length;i++){
            potential2.setValue(conf2,values2[i]);
            conf2.nextConfiguration();
        }

        Potential potential = potential1.combine(potential2);
        potential.print();

        

        //Double epsilon = -0.0001;
        Double epsilon = 0.001;
        learningRPT instance = new learningRPT(epsilon, 0.5);
        TreeNode treeResult = instance.factorize(potential);


        System.out.println();
        System.out.println(" RESULTADO FINAL: ");
        System.out.println();

        treeResult.print(0);
        RecursivePotentialTree recTree = new RecursivePotentialTree();
        recTree.setValues(treeResult);

        PotentialTable potab = recTree.getPotentialTable();
        potential.normalize();
        //potential.print();
        potab.normalize();
        //potab.print();

        Configuration confvars = new Configuration(potential.getVariables());

        for(int i=0; i<=confvars.possibleValues(); i++){
            System.out.println("Valor "+i);
            System.out.println(potential.getValue(confvars) +" vs "+potab.getValue(confvars));
            confvars.nextConfiguration();
        }
    }

    @Ignore
    @Test
    public void test3(){

        System.out.println(" --- TEST 3 --- ");

        String[] states={"0","1"};
        Node A,B,C,D;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);

        Vector<Node> vars1 = new Vector<Node>();
        vars1.add(A);
        vars1.add(B);
        Potential potential1 = new PotentialTable(vars1);
        double[] values = {0.7,0.5,0.3,0.5};
        Configuration conf = new Configuration(vars1);
        for(int i=0; i<values.length;i++){
            potential1.setValue(conf,values[i]);
            conf.nextConfiguration();
        }

        Vector<Node> vars2 = new Vector<Node>();
        vars2.add(B);
        vars2.add(C);

        Potential potential2 = new PotentialTable(vars2);
        double[] values2 = {0.6,0.1,0.4,0.9};
        Configuration conf2 = new Configuration(vars2);
        for(int i=0; i<values2.length;i++){
            potential2.setValue(conf2,values2[i]);
            conf2.nextConfiguration();
        }

        Potential potential = potential1.combine(potential2);
        potential.print();

        

        //Double epsilon = -0.0001;
        Double epsilon = 0.00;
        learningRPT instance = new learningRPT(epsilon, 0.5);
        TreeNode treeResult = instance.factorize(potential);


        System.out.println();
        System.out.println(" RESULTADO FINAL: ");
        System.out.println();

        treeResult.print(0);
        RecursivePotentialTree recTree = new RecursivePotentialTree();
        recTree.setValues(treeResult);

        PotentialTable potab = recTree.getPotentialTable();
        potential.normalize();
        //potential.print();
        potab.normalize();
        //potab.print();

        Configuration confvars = new Configuration(potential.getVariables());

        for(int i=0; i<=confvars.possibleValues(); i++){
            System.out.println("Valor "+i);
            System.out.println(potential.getValue(confvars) +" vs "+potab.getValue(confvars));
            confvars.nextConfiguration();
        }
    }


    @Ignore
    @Test
    public void testPGM1(){

        System.out.println(" --- TEST PGM 1. 6 variables independientes --- ");

        String[] states={"0","1"};
        Node A,B,C,D,E,F;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);
        E = new FiniteStates("E", states);
        F = new FiniteStates("F", states);

        Vector<Node> vars1 = new Vector<Node>();
        vars1.add(A);
        Potential potential1 = new PotentialTable(vars1);
        double[] values = {0.7,0.3};
        Configuration conf = new Configuration(vars1);
        for(int i=0; i<values.length;i++){
            potential1.setValue(conf,values[i]);
            conf.nextConfiguration();
        }

        Vector<Node> vars2 = new Vector<Node>();
        vars2.add(B);

        Potential potential2 = new PotentialTable(vars2);
        double[] values2 = {0.1,0.9};
        Configuration conf2 = new Configuration(vars2);
        for(int i=0; i<values2.length;i++){
            potential2.setValue(conf2,values2[i]);
            conf2.nextConfiguration();
        }


        Vector<Node> vars3 = new Vector<Node>();
        vars3.add(C);

        Potential potential3 = new PotentialTable(vars3);
        double[] values3 = {0.4,0.6};
        Configuration conf3 = new Configuration(vars3);
        for(int i=0; i<values3.length;i++){
            potential3.setValue(conf3,values3[i]);
            conf3.nextConfiguration();
        }


        Vector<Node> vars4 = new Vector<Node>();
        vars4.add(D);

        Potential potential4 = new PotentialTable(vars4);
        double[] values4 = {0.65,0.35};
        Configuration conf4 = new Configuration(vars4);
        for(int i=0; i<values4.length;i++){
            potential4.setValue(conf4,values4[i]);
            conf4.nextConfiguration();
        }

        Vector<Node> vars5 = new Vector<Node>();
        vars5.add(E);

        Potential potential5 = new PotentialTable(vars5);
        double[] values5 = {0.85,0.15};
        Configuration conf5 = new Configuration(vars5);
        for(int i=0; i<values5.length;i++){
            potential5.setValue(conf5,values5[i]);
            conf5.nextConfiguration();
        }


        Vector<Node> vars6 = new Vector<Node>();
        vars6.add(F);

        Potential potential6 = new PotentialTable(vars6);
        double[] values6 = {0.25,0.75};
        Configuration conf6 = new Configuration(vars6);
        for(int i=0; i<values6.length;i++){
            potential6.setValue(conf6,values6[i]);
            conf6.nextConfiguration();
        }


        Potential potential = potential1.combine(potential2);
        potential = potential.combine(potential3);
        potential = potential.combine(potential4);
        potential = potential.combine(potential5);
        potential = potential.combine(potential6);

        potential.print();

        

        //Double epsilon = -0.0001;
        Double epsilon = 0.001;
        learningRPT instance = new learningRPT(epsilon, 0.5);
        TreeNode treeResult = instance.factorize(potential);


        System.out.println();
        System.out.println(" RESULTADO FINAL: ");
        System.out.println();

        treeResult.print(0);
        RecursivePotentialTree recTree = new RecursivePotentialTree();
        recTree.setValues(treeResult);

        PotentialTable potab = recTree.getPotentialTable();
        //potential.normalize();
        //potential.print();
        //potab.normalize();
        //potab.print();

        Configuration confvars = new Configuration(potential.getVariables());

        for(int i=0; i<=confvars.possibleValues(); i++){
            System.out.println("Valor "+i);
            System.out.println(potential.getValue(confvars) +" vs "+potab.getValue(confvars));
            confvars.nextConfiguration();
        }
    }



    @Ignore
    @Test
    public void testPGM2(){

        System.out.println(" --- TEST PGM2. 6 variables, dependientes por pares--- ");

        String[] states={"0","1"};
        Node A,B,C,D,E,F;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);
        E = new FiniteStates("E", states);
        F = new FiniteStates("F", states);

        Vector<Node> vars1 = new Vector<Node>();
        vars1.add(A);
        vars1.add(B);
        Potential potential1 = new PotentialTable(vars1);
        double[] values = {0.1,0.6,0.9,0.4};
        Configuration conf = new Configuration(vars1);
        for(int i=0; i<values.length;i++){
            potential1.setValue(conf,values[i]);
            conf.nextConfiguration();
        }

        Vector<Node> vars2 = new Vector<Node>();
        vars2.add(C);
        vars2.add(D);

        Potential potential2 = new PotentialTable(vars2);
        double[] values2 = {0.3,0.4,0.7,0.6};
        Configuration conf2 = new Configuration(vars2);
        for(int i=0; i<values2.length;i++){
            potential2.setValue(conf2,values2[i]);
            conf2.nextConfiguration();
        }

        Vector<Node> vars3 = new Vector<Node>();
        vars3.add(E);
        vars3.add(F);

        Potential potential3 = new PotentialTable(vars3);
        double[] values3 = {0.75,0.2,0.25,0.8};
        Configuration conf3 = new Configuration(vars3);
        for(int i=0; i<values3.length;i++){
            potential3.setValue(conf3,values3[i]);
            conf3.nextConfiguration();
        }

        Potential potential = potential1.combine(potential2);
        potential = potential.combine(potential3);
        potential.print();

        

        //Double epsilon = -0.0001;
        Double epsilon = 0.001;
        learningRPT instance = new learningRPT(epsilon, 0.5);
        TreeNode treeResult = instance.factorize(potential);


        System.out.println();
        System.out.println(" RESULTADO FINAL: ");
        System.out.println();

        treeResult.print(0);
        RecursivePotentialTree recTree = new RecursivePotentialTree();
        recTree.setValues(treeResult);

        PotentialTable potab = recTree.getPotentialTable();
        potential.normalize();
        //potential.print();
        potab.normalize();
        //potab.print();

        Configuration confvars = new Configuration(potential.getVariables());

        for(int i=0; i<=confvars.possibleValues(); i++){
            System.out.println("Valor "+i);
            System.out.println(potential.getValue(confvars) +" vs "+potab.getValue(confvars));
            confvars.nextConfiguration();
        }
    }


    @Ignore
    @Test
    public void testPGM3() {

        System.out.println(" --- TEST PGM3. 3 variablesárbol de prob. con raiz en A--- ");

        String[] states = {"0", "1"};
        Node A, B, C;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);

        Vector<Node> vars1 = new Vector<Node>();
        vars1.add(A);
        vars1.add(B);
        vars1.add(C);
        Potential potential1 = new PotentialTable(vars1);
        double[] values = {0.3, 0.3, 0.3, 0.3, 0.1, 0.2, 0.75, 0.25};
        Configuration conf = new Configuration(vars1);
        for (int i = 0; i < values.length; i++) {
            potential1.setValue(conf, values[i]);
            conf.nextConfiguration();
        }



        Potential potential = potential1.copy();
        potential.normalize();


        //original.print();

        Vector<Double> X = new Vector<Double>();
        Vector<Double> Y = new Vector<Double>();

        for (double epsilon = 0.0; epsilon <= 0.01; epsilon += 0.001) {
            learningRPT instance = new learningRPT(epsilon, 0.5);
            TreeNode treeResult = instance.factorize(potential);
            RecursivePotentialTree recTree = new RecursivePotentialTree();
            recTree.setValues(treeResult);
            PotentialTable potab = recTree.getPotentialTable();
            double KL = this.KLdivergence(potential, potab);
            System.out.println(epsilon + "\t" + KL);
            X.add(epsilon);
            Y.add(KL);
            recTree.print(0);

            Configuration confvars = new Configuration(potential.getVariables());

            System.out.println(potential.totalPotential() + " y " + potab.totalPotential());

        }
        System.out.println(" --- ");
        for (int k = 0; k < X.size(); k++) {
            System.out.print(X.get(k) + ",");
        }
        System.out.println(" --- ");
        for (int k = 0; k < Y.size(); k++) {
            System.out.print(Y.get(k) + ",");
        }
    }


    @Ignore
    @Test
    public void testPGM4() {

        System.out.println(" --- TEST PGM4. 3 variablesárbol de prob. con raiz en A--- ");

        String[] states = {"0", "1"};
        Node A, B, C;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);

        Vector<Node> vars1 = new Vector<Node>();
        vars1.add(A);
        vars1.add(B);
        vars1.add(C);
        Potential potential1 = new PotentialTable(vars1);
        double[] values = {0.1, 0.1, 0.1, 0.1, 0.9, 0.9, 0.9, 0.9};
        Configuration conf = new Configuration(vars1);
        for (int i = 0; i < values.length; i++) {
            potential1.setValue(conf, values[i]);
            conf.nextConfiguration();
        }



        Potential potential = potential1.copy();
        potential.normalize();


        //original.print();
        

        Vector<Double> X = new Vector<Double>();
        Vector<Double> Y = new Vector<Double>();

        for (double epsilon = 0.0; epsilon <= 0.01; epsilon += 0.001) {
            learningRPT instance = new learningRPT(epsilon, 0.5);
            TreeNode treeResult = instance.factorize(potential);
            RecursivePotentialTree recTree = new RecursivePotentialTree();
            recTree.setValues(treeResult);
            PotentialTable potab = recTree.getPotentialTable();
            double KL = this.KLdivergence(potential, potab);
            System.out.println(epsilon + "\t" + KL);
            X.add(epsilon);
            Y.add(KL);
            recTree.print(0);

            Configuration confvars = new Configuration(potential.getVariables());

            System.out.println(potential.totalPotential() + " y " + potab.totalPotential());

        }
        System.out.println(" --- ");
        for (int k = 0; k < X.size(); k++) {
            System.out.print(X.get(k) + ",");
        }
        System.out.println(" --- ");
        for (int k = 0; k < Y.size(); k++) {
            System.out.print(Y.get(k) + ",");
        }
    }



    public double KLdivergence(Potential p1, Potential p2){

        Configuration conf = new Configuration(p1.getVariables());
        double KL = 0;
        for(int i=0; i<conf.possibleValues(); i++){
            //KL = KL + (p1.getValue(conf)*Math.log(p1.getValue(conf)/p2.getValue(conf)));
            KL = KL + (p1.getValue(conf)*(Math.log(p1.getValue(conf)) - Math.log(p2.getValue(conf))));
            //System.out.println(KL);
            conf.nextConfiguration();
        }

        return KL;
    }

    public double meanSquaredError(Potential p1, Potential p2) {

        Configuration conf = new Configuration(p1.getVariables());
        double MSE = 0;
        for (int i = 0; i < conf.possibleValues(); i++) {
            //KL = KL + (p1.getValue(conf)*Math.log(p1.getValue(conf)/p2.getValue(conf)));
            MSE = MSE + Math.pow(p1.getValue(conf) - p2.getValue(conf),2);
            conf.nextConfiguration();
        }

        MSE = Math.sqrt(MSE/conf.possibleValues());

        return MSE;
    }


    public PotentialTable generateRandomTable(Vector<Node> v, double pruningThreshold) {
        PotentialTable table = null;
        PotentialTree tree;

        NodeList varList = new NodeList(v);

        tree = PotentialTree.randomTree(varList);

        tree.sort();

        tree.limitBound(pruningThreshold);

       // V.add((float)Math.log(tree.getSize()));
         V.add((float)tree.getSize());
      //  tree.print();

        table = new PotentialTable(tree);

        return (table);
    }

    @Ignore
    @Test
    public void hugeTest(){

        String[] states={"0","1","2"};
        Node A,B,C,D,E,F,G,H,I,J;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);
        E = new FiniteStates("E", states);
        F = new FiniteStates("F", states);
        G = new FiniteStates("G", states);
        H = new FiniteStates("H", states);
        I = new FiniteStates("I", states);
        J = new FiniteStates("J", states);

        Vector<Node> vars = new Vector<Node>();
        vars.add(A);
        vars.add(B);
        vars.add(C);
        vars.add(D);
        vars.add(E);
        vars.add(F);
//        vars.add(G);
//        vars.add(H);
//        vars.add(I);
//        vars.add(J);

        NodeList list = new NodeList(vars);
        // Random probability table
        Vector<Float> X = new Vector<Float>();
        Vector<Float> Y = new Vector<Float>();
        Vector<Float> Z = new Vector<Float>();
        Vector<Float> W = new Vector<Float>();

        //original.print();
        

        for(int execution = 0; execution < 30; execution++){
            System.out.println(" EXECUTION "+execution);
            Potential original = new PotentialTable(new Random(),list,2);
            original.normalize();
//            System.out.println("ORIGINAL ************");
//            original.print();
//            System.out.println("************");
            for(double epsilon = 0.0; epsilon <= 0.01; epsilon += 0.001){
            //for(double epsilon = 0.0; epsilon <= 0.05; epsilon += 0.005){
            //for(double epsilon = 0.0; epsilon <= 0.1; epsilon += 0.01){
            //for(double epsilon = 0.0; epsilon <= 0.04; epsilon += 0.002){
                learningRPT instance = new learningRPT(epsilon, 0.5);
                TreeNode treeResult = instance.factorize(original);
                RecursivePotentialTree recTree = new RecursivePotentialTree();
                recTree.setValues(treeResult);
                PotentialTable potab = recTree.getPotentialTable();
                double KL = this.KLdivergence(original, potab);
                System.out.println(epsilon + "\t" + KL);
                X.add((float)epsilon);
                Y.add((float)KL);
                Z.add((float)Math.log(recTree.getSize()));
                W.add((float)this.meanSquaredError(original, potab));
//                System.out.println("ARBOL FINAL ////////////");
//                recTree.print(0);
//                System.out.println("////////////");
//
//                Configuration confvars = new Configuration(original.getVariables());
//
//                for(int i=0; i<=confvars.possibleValues(); i++){
//                    System.out.println("Valor "+i);
//                    System.out.println(original.getValue(confvars) +" vs "+potab.getValue(confvars));
//                    confvars.nextConfiguration();
//                }

            }
            System.out.println(" --- ");
        }

            System.out.println(" --- ");
            for(int k = 0; k< X.size(); k++){
                System.out.print(X.get(k)+",");
            }
            System.out.println(" --- ");
            for(int k = 0; k< Y.size(); k++){
                System.out.print(Y.get(k)+",");
            }
            System.out.println(" --- ");
            for(int k = 0; k< Y.size(); k++){
                System.out.print(Z.get(k)+",");
            }
            System.out.println(" --- ");
            for(int k = 0; k< Y.size(); k++){
                System.out.print(W.get(k)+",");
            }
    }

    @Ignore
    @Test
    public void secondHugeTest(){

        String[] states={"0","1"};
        Node A,B,C,D,E,F;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);
        E = new FiniteStates("E", states);
        F = new FiniteStates("F", states);

        Vector<Node> vars1 = new Vector<Node>();
        vars1.add(A);
        vars1.add(D);
        vars1.add(B);
        Potential potential1 = new PotentialTable(vars1);
        double[] values = {0.6,0.6,0.4,0.4,0.7,0.3,0.7,0.3};
        Configuration conf = new Configuration(vars1);
        for(int i=0; i<values.length;i++){
            potential1.setValue(conf,values[i]);
            conf.nextConfiguration();
        }

        Vector<Node> vars2 = new Vector<Node>();
        vars2.add(B);
        vars2.add(C);
        vars2.add(D);
        Potential potential2 = new PotentialTable(vars2);
        double[] values2 = {0.08,0.12,0.02,0.18,0.32,0.48,0.08,0.72};
        Configuration conf2 = new Configuration(vars2);
        for(int i=0; i<values2.length;i++){
            potential2.setValue(conf2,values2[i]);
            conf2.nextConfiguration();
        }

        Vector<Node> vars3 = new Vector<Node>();
        vars3.add(E);
        vars3.add(F);
        Potential potential3 = new PotentialTable(vars3);
        double[] values3 = {0.6,0.2,0.4,0.8};
        Configuration conf3 = new Configuration(vars3);
        for(int i=0; i<values3.length;i++){
            potential3.setValue(conf3,values3[i]);
            conf3.nextConfiguration();
        }

        Potential potential = potential1.combine(potential2);
        potential = potential.combine(potential3);
        potential.normalize();


        //original.print();
        

        Vector<Double> X = new Vector<Double>();
        Vector<Double> Y = new Vector<Double>();
        Vector<Double> Z = new Vector<Double>();

        for(int execution = 0; execution < 10; execution++){
            System.out.println(" EXECUTION "+execution);
            for(double epsilon = 0.0; epsilon <= 0.1; epsilon += 0.01){
                learningRPT instance = new learningRPT(epsilon, 0.5);
                TreeNode treeResult = instance.factorize(potential);
                RecursivePotentialTree recTree = new RecursivePotentialTree();
                recTree.setValues(treeResult);
                PotentialTable potab = recTree.getPotentialTable();
                double KL = this.KLdivergence(potential, potab);
                System.out.println(epsilon + "\t" + KL);
                X.add(epsilon);
                Y.add(KL);
                Z.add((double)recTree.getSize());
                System.out.println("***************");
                recTree.print(0);

                Configuration confvars = new Configuration(potential.getVariables());

//                for(int i=0; i<=confvars.possibleValues(); i++){
//                    System.out.println("Valor "+i);
//                    System.out.println(potential.getValue(confvars) +" vs "+potab.getValue(confvars) + " razon: " + potential.getValue(confvars)/potab.getValue(confvars));
//                    confvars.nextConfiguration();
//                }
                System.out.println(potential.totalPotential() +" y "+potab.totalPotential());

            }
            System.out.println(" --- ");
            for(int k = 0; k< X.size(); k++){
                System.out.print(X.get(k)+",");
            }
            System.out.println(" --- ");
            for(int k = 0; k< Y.size(); k++){
                System.out.print(Y.get(k)+",");
            }
            System.out.println(" --- ");
            for(int k = 0; k< Y.size(); k++){
                System.out.print(Z.get(k)+",");
            }
        }
}
    @Ignore
    @Test
    public void asiaTest()throws elvira.parser.ParseException ,java.io.IOException{
        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/PROYECTOS/2010/factorizacion potenciales/codigo/asia.elv");
        Bnet asia = new Bnet(fis);
        Vector<Relation> rels = asia.getRelationList();
        Potential combined = rels.get(0).getValues();
        for(int i=1; i<rels.size(); i++){
            combined = combined.combine(rels.get(i).getValues());
        }
        Vector<Double> X = new Vector<Double>();
        Vector<Double> Y = new Vector<Double>();
        

        for(double epsilon = 0.0; epsilon <= 0.1; epsilon += 0.01){
            learningRPT instance = new learningRPT(epsilon, 0.5);
            TreeNode result = instance.factorize(combined);
            result.print(0);
            RecursivePotentialTree recTree = new RecursivePotentialTree();
            recTree.setValues(result);
            PotentialTable potab = recTree.getPotentialTable();
            double KL = this.KLdivergence(combined, potab);
            //System.out.println(epsilon + "\t" + KL);
            X.add(epsilon);
            Y.add(KL);
        }

        System.out.println(" --- ");
        for(int k = 0; k< X.size(); k++){
            System.out.print(X.get(k)+",");
        }
        System.out.println(" --- ");
        for(int k = 0; k< Y.size(); k++){
            System.out.print(Y.get(k)+",");
        }
        //result.print(0);
    }


    @Ignore
    @Test
    public void randomTest() {

        PotentialTable t;
        String[] states={"0","1"};
        Node A,B,C,D,E,F;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);
        E = new FiniteStates("E", states);
        F = new FiniteStates("F", states);
        Vector<Node> vars = new Vector<Node>();
        vars.add(A);
        vars.add(B);
        vars.add(C);
        vars.add(D);
        vars.add(E);
        vars.add(F);


        
        Vector<Float> X = new Vector<Float>();
        Vector<Float> Y = new Vector<Float>();
        Vector<Float> Z = new Vector<Float>();
        Vector<Float> W = new Vector<Float>();
       // Vector<Float> V = new Vector<Float>();

        for(int ejecuciones = 0; ejecuciones <30; ejecuciones++){
            t = generateRandomTable(vars,0.01);
            t.normalize();
            //t.print();
            Potential potential = t.copy();
            for (double epsilon = 0.0; epsilon <= 0.01; epsilon += 0.001) {
                learningRPT instance = new learningRPT(epsilon, 0.01);
                TreeNode treeResult = instance.factorize(potential);
                RecursivePotentialTree recTree = new RecursivePotentialTree();
                recTree.setValues(treeResult);
                PotentialTable potab = recTree.getPotentialTable();
                double KL = this.KLdivergence(potential, potab);
                double MSE = this.meanSquaredError(potential, potab);
                System.out.println(epsilon + "\t" + KL + "\t" + MSE);
                X.add((float)epsilon);
                Y.add((float)KL);
                Z.add((float)MSE);
                W.add((float)Math.log(recTree.getSize()));
                
                //recTree.print(0);

               // System.out.println(potential.totalPotential() + " y " + potab.totalPotential());

            }
        }
        System.out.println(" --- ");
        for (int k = 0; k < X.size(); k++) {
            System.out.print(X.get(k) + ",");
        }
        System.out.println(" --- ");
        for (int k = 0; k < Y.size(); k++) {
            System.out.print(Y.get(k) + ",");
        }
        System.out.println(" --- ");
        for (int k = 0; k < Z.size(); k++) {
            System.out.print(Z.get(k) + ",");
        }
        System.out.println(" --- ");
        for (int k = 0; k < Z.size(); k++) {
            System.out.print(W.get(k) + ",");
        }
        System.out.println(" --- ");
        for (int k = 0; k < V.size(); k++) {
            System.out.print(V.get(k) + ",");
        }
    }

    @Ignore
    @Test
    public void randomTest2() {

        PotentialTable t;
        String[] states={"0","1"};
        Node A,B,C,D,E,F;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);
        D = new FiniteStates("D", states);
        E = new FiniteStates("E", states);
        F = new FiniteStates("F", states);
        Vector<Node> vars = new Vector<Node>();
        vars.add(A);
        vars.add(B);
        vars.add(C);
        vars.add(D);
        vars.add(E);
        vars.add(F);

        Vector<Float> diffs = new Vector<Float>();
        Vector<Float> KLdivs = new Vector<Float>();

        for(int iter =0; iter <30; iter++){
            t = generateRandomTable(vars,0.0001);
            t.normalize();
            //t.print();
            Potential potential = t.copy();

            double epsilon = 0.002;
            learningRPT instance = new learningRPT(epsilon, 0.001);
            TreeNode treeResult = instance.factorize(potential);
            RecursivePotentialTree recTree = new RecursivePotentialTree();
            recTree.setValues(treeResult);
            PotentialTable potab = recTree.getPotentialTable();

            epsilon = 0.0005;
            learningRPT instance2 = new learningRPT(epsilon, 0.001);
            TreeNode treeResult2 = instance2.factorize(potab);
            RecursivePotentialTree recTree2 = new RecursivePotentialTree();
            recTree2.setValues(treeResult2);
            PotentialTable potab2 = recTree2.getPotentialTable();

            treeResult.print(0);
            System.out.println(" ++++++++++++ ");
            treeResult2.print(0);

            diffs.add((float)treeResult.getTreeSize() - treeResult2.getTreeSize());
            KLdivs.add((float)this.KLdivergence(potab, potab2));

//            System.out.println("Size RPT1: "+treeResult.getTreeSize()+" size RPT2: "+treeResult2.getTreeSize());
//            System.out.println("KL divergence: "+this.KLdivergence(potab, potab2));
        }

        System.out.println();
        for(int i=0; i<30; i++){
            System.out.print(diffs.get(i)+",");
        }
        System.out.println();
        for(int i=0; i<30; i++){
            System.out.print(KLdivs.get(i)+",");
        }

    }


    @Ignore
    @Test
    public void ascTest() {

        String[] states = {"0", "1"};
        Node A, B, C;
        A = new FiniteStates("A", states);
        B = new FiniteStates("B", states);
        C = new FiniteStates("C", states);

        Vector<Node> vars1 = new Vector<Node>();
        vars1.add(A);
        vars1.add(B);
        vars1.add(C);
        Potential potential1 = new PotentialTable(vars1);
        double[] values = {0.3, 0.3, 0.7, 0.7, 0.1, 0.2, 0.4, 0.2};
        Configuration conf = new Configuration(vars1);
        for (int i = 0; i < values.length; i++) {
            potential1.setValue(conf, values[i]);
            conf.nextConfiguration();
        }



        Potential potential = potential1.copy();
        //potential.normalize();



        //original.print();
        

        Vector<Double> X = new Vector<Double>();
        Vector<Double> Y = new Vector<Double>();

        for (double epsilon = 0.0; epsilon <= 0.01; epsilon += 0.001) {
            learningRPT instance = new learningRPT(epsilon, 0.5);
            TreeNode treeResult = instance.factorize(potential);
            RecursivePotentialTree recTree = new RecursivePotentialTree();
            recTree.setValues(treeResult);
            PotentialTable potab = recTree.getPotentialTable();
            double KL = this.KLdivergence(potential, potab);
            System.out.println(epsilon + "\t" + KL);
            X.add(epsilon);
            Y.add(KL);
            recTree.print(0);

            Configuration confvars = new Configuration(potential.getVariables());

            System.out.println(potential.totalPotential() + " y " + potab.totalPotential());

        }
        System.out.println(" --- ");
        for (int k = 0; k < X.size(); k++) {
            System.out.print(X.get(k) + ",");
        }
        System.out.println(" --- ");
        for (int k = 0; k < Y.size(); k++) {
            System.out.print(Y.get(k) + ",");
        }
    }

    @Ignore
    @Test
    public void TestArtificialELV()throws elvira.parser.ParseException ,java.io.IOException{
        FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/artificial.elv");
        Bnet artificial = new Bnet(fis);
        learningRPT instance = new learningRPT(0.001, 0.5);

        Vector<Relation> relations = artificial.getRelationList();
        Vector<Relation> newRelations = new Vector<Relation>();

        for(int i=0; i<relations.size();i++){
           RecursivePotentialTree pot = new RecursivePotentialTree();
           pot.setValues(instance.factorize(relations.get(i).getValues()));
           newRelations.add(new Relation(pot));
        }

        artificial.setRelationList(newRelations);

        Vector<Relation> relationsFinal = artificial.getRelationList();
        for(int i=0; i<relationsFinal.size(); i++){
            System.out.println("   ***   ");
            relationsFinal.get(i).print();
            System.out.println("   ***   ");
        }
    }

}


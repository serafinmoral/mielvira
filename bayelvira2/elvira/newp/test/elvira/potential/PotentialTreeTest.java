/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential;

import elvira.Configuration;
import elvira.FiniteStates;
import elvira.Node;
import elvira.NodeList;
import elvira.tools.FactorisationTools;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Vector;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Cora
 */
public class PotentialTreeTest {

    public PotentialTreeTest() {
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
     * Test of main method, of class PotentialTree.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        PotentialTree.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of makePotential method, of class PotentialTree.
     */
    @Test
    public void testMakePotential() {
        System.out.println("makePotential");
        PotentialTree instance = new PotentialTree();
        Potential expResult = null;
        Potential result = instance.makePotential();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTree method, of class PotentialTree.
     */
    @Test
    public void testSetTree() {
        System.out.println("setTree");
        ProbabilityTree tree = null;
        PotentialTree instance = new PotentialTree();
        instance.setTree(tree);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTree method, of class PotentialTree.
     */
    @Test
    public void testGetTree() {
        System.out.println("getTree");
        PotentialTree instance = new PotentialTree();
        ProbabilityTree expResult = null;
        ProbabilityTree result = instance.getTree();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSize method, of class PotentialTree.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        PotentialTree instance = new PotentialTree();
        long expResult = 0L;
        long result = instance.getSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumberOfNodes method, of class PotentialTree.
     */
    @Test
    public void testGetNumberOfNodes() {
        System.out.println("getNumberOfNodes");
        PotentialTree instance = new PotentialTree();
        long expResult = 0L;
        long result = instance.getNumberOfNodes();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumberOfLeaves method, of class PotentialTree.
     */
    @Test
    public void testGetNumberOfLeaves() {
        System.out.println("getNumberOfLeaves");
        PotentialTree instance = new PotentialTree();
        long expResult = 0L;
        long result = instance.getNumberOfLeaves();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getNumberOfValues method, of class PotentialTree.
     */
    @Test
    public void testGetNumberOfValues() {
        System.out.println("getNumberOfValues");
        PotentialTree instance = new PotentialTree();
        long expResult = 0L;
        long result = instance.getNumberOfValues();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of save method, of class PotentialTree.
     */
    @Test
    public void testSave() {
        System.out.println("save");
        PrintWriter p = null;
        PotentialTree instance = new PotentialTree();
        instance.save(p);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of print method, of class PotentialTree.
     */
    @Test
    public void testPrint() {
        System.out.println("print");
        PotentialTree instance = new PotentialTree();
        instance.print();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compareTo method, of class PotentialTree.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        PotentialTree pot = null;
        PotentialTree instance = new PotentialTree();
        int expResult = 0;
        int result = instance.compareTo(pot);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compareAndPrint method, of class PotentialTree.
     */
    @Test
    public void testCompareAndPrint() {
        System.out.println("compareAndPrint");
        PotentialTree pot = null;
        PotentialTree instance = new PotentialTree();
        int expResult = 0;
        int result = instance.compareAndPrint(pot);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getValue method, of class PotentialTree.
     */
    @Test
    public void testGetValue_Configuration() {
        System.out.println("getValue");
        Configuration conf = null;
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.getValue(conf);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getValue method, of class PotentialTree.
     */
    @Test
    public void testGetValue_Hashtable_intArr() {
        System.out.println("getValue");
        Hashtable positions = null;
        int[] conf = null;
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.getValue(positions, conf);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getVectors method, of class PotentialTree.
     */
    @Test
    public void testGetVectors() {
        System.out.println("getVectors");
        Hashtable positions = null;
        int posX = 0;
        int nv = 0;
        int[] conf = null;
        double[] vals = null;
        PotentialTree instance = new PotentialTree();
        instance.getVectors(positions, posX, nv, conf, vals);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getActiveNodes method, of class PotentialTree.
     */
    @Test
    public void testGetActiveNodes() {
        System.out.println("getActiveNodes");
        Hashtable positions = null;
        int posX = 0;
        int[] conf = null;
        Vector activeNodes = null;
        PotentialTree instance = new PotentialTree();
        instance.getActiveNodes(positions, posX, conf, activeNodes);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class PotentialTree.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        Hashtable positions = null;
        int[] conf = null;
        Vector activeNodes = null;
        double newVal = 0.0;
        int mode = 0;
        PotentialTree instance = new PotentialTree();
        instance.update(positions, conf, activeNodes, newVal, mode);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setValue method, of class PotentialTree.
     */
    @Test
    public void testSetValue() {
        System.out.println("setValue");
        Configuration conf = null;
        double x = 0.0;
        PotentialTree instance = new PotentialTree();
        instance.setValue(conf, x);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of totalPotential method, of class PotentialTree.
     */
    @Test
    public void testTotalPotential_0args() {
        System.out.println("totalPotential");
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.totalPotential();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of totalPotential method, of class PotentialTree.
     */
    @Test
    public void testTotalPotential_Configuration() {
        System.out.println("totalPotential");
        Configuration conf = null;
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.totalPotential(conf);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sumConsistent method, of class PotentialTree.
     */
    @Test
    public void testSumConsistent() {
        System.out.println("sumConsistent");
        Configuration conf = null;
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.sumConsistent(conf);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of nonCeroValues method, of class PotentialTree.
     */
    @Test
    public void testNonCeroValues() {
        System.out.println("nonCeroValues");
        Configuration conf = null;
        PotentialTree instance = new PotentialTree();
        int expResult = 0;
        int result = instance.nonCeroValues(conf);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addValue method, of class PotentialTree.
     */
    @Test
    public void testAddValue() {
        System.out.println("addValue");
        Configuration conf = null;
        double toAdd = 0.0;
        PotentialTree instance = new PotentialTree();
        instance.addValue(conf, toAdd);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of entropyPotential method, of class PotentialTree.
     */
    @Test
    public void testEntropyPotential_0args() {
        System.out.println("entropyPotential");
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.entropyPotential();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of entropyPotential method, of class PotentialTree.
     */
    @Test
    public void testEntropyPotential_Configuration() {
        System.out.println("entropyPotential");
        Configuration conf = null;
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.entropyPotential(conf);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of restrictVariable method, of class PotentialTree.
     */
    @Test
    public void testRestrictVariable() {
        System.out.println("restrictVariable");
        Configuration conf = null;
        PotentialTree instance = new PotentialTree();
        Potential expResult = null;
        Potential result = instance.restrictVariable(conf);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of divide method, of class PotentialTree.
     */
    @Test
    public void testDivide() {
        System.out.println("divide");
        Potential p = null;
        PotentialTree instance = new PotentialTree();
        Potential expResult = null;
        Potential result = instance.divide(p);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of combine method, of class PotentialTree.
     */
    @Test
    public void testCombine_Potential() {
        System.out.println("combine");
        Potential p = null;
        PotentialTree instance = new PotentialTree();
        Potential expResult = null;
        Potential result = instance.combine(p);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of combine method, of class PotentialTree.
     */
    @Test
    public void testCombine_3args() {
        System.out.println("combine");
        Potential pot1 = null;
        Potential pot2 = null;
        int field = 0;
        PotentialTree instance = new PotentialTree();
        instance.combine(pot1, pot2, field);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of combine method, of class PotentialTree.
     */
    @Test
    public void testCombine_PotentialTable() {
        System.out.println("combine");
        PotentialTable p = null;
        PotentialTree instance = new PotentialTree();
        PotentialTree expResult = null;
        PotentialTree result = instance.combine(p);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addition method, of class PotentialTree.
     */
    @Test
    public void testAddition() {
        System.out.println("addition");
        Potential p = null;
        PotentialTree instance = new PotentialTree();
        Potential expResult = null;
        Potential result = instance.addition(p);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sum method, of class PotentialTree.
     */
    @Test
    public void testSum() {
        System.out.println("sum");
        PotentialTable p = null;
        PotentialTree instance = new PotentialTree();
        PotentialTree expResult = null;
        PotentialTree result = instance.sum(p);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of combineWithSubset method, of class PotentialTree.
     */
    @Test
    public void testCombineWithSubset() {
        System.out.println("combineWithSubset");
        Potential p = null;
        PotentialTree instance = new PotentialTree();
        instance.combineWithSubset(p);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addVariable method, of class PotentialTree.
     */
    @Test
    public void testAddVariable_Vector() {
        System.out.println("addVariable");
        Vector vars = null;
        PotentialTree instance = new PotentialTree();
        PotentialTree expResult = null;
        PotentialTree result = instance.addVariable(vars);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addVariable method, of class PotentialTree.
     */
    @Test
    public void testAddVariable_Node() {
        System.out.println("addVariable");
        Node var = null;
        PotentialTree instance = new PotentialTree();
        Potential expResult = null;
        Potential result = instance.addVariable(var);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addVariable method, of class PotentialTree.
     */
    @Test
    public void testAddVariable_3args() {
        System.out.println("addVariable");
        Potential pot = null;
        FiniteStates var = null;
        int field = 0;
        PotentialTree instance = new PotentialTree();
        instance.addVariable(pot, var, field);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of marginalizePotential method, of class PotentialTree.
     */
    @Test
    public void testMarginalizePotential() {
        System.out.println("marginalizePotential");
        Vector vars = null;
        PotentialTree instance = new PotentialTree();
        Potential expResult = null;
        Potential result = instance.marginalizePotential(vars);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of maxMarginalizePotential method, of class PotentialTree.
     */
    @Test
    public void testMaxMarginalizePotential() {
        System.out.println("maxMarginalizePotential");
        Vector vars = null;
        PotentialTree instance = new PotentialTree();
        Potential expResult = null;
        Potential result = instance.maxMarginalizePotential(vars);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaxConfiguration method, of class PotentialTree.
     */
    @Test
    public void testGetMaxConfiguration_Configuration() {
        System.out.println("getMaxConfiguration");
        Configuration subconf = null;
        PotentialTree instance = new PotentialTree();
        Configuration expResult = null;
        Configuration result = instance.getMaxConfiguration(subconf);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMaxConfiguration method, of class PotentialTree.
     */
    @Test
    public void testGetMaxConfiguration_Configuration_Vector() {
        System.out.println("getMaxConfiguration");
        Configuration subconf = null;
        Vector list = null;
        PotentialTree instance = new PotentialTree();
        Configuration expResult = null;
        Configuration result = instance.getMaxConfiguration(subconf, list);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sortAndBound method, of class PotentialTree.
     */
    @Test
    public void testSortAndBound_int() {
        System.out.println("sortAndBound");
        int maxLeaves = 0;
        PotentialTree instance = new PotentialTree();
        Potential expResult = null;
        Potential result = instance.sortAndBound(maxLeaves);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sortAndBound method, of class PotentialTree.
     */
    @Test
    public void testSortAndBound_double() {
        System.out.println("sortAndBound");
        double threshold = 0.0;
        PotentialTree instance = new PotentialTree();
        PotentialTree expResult = null;
        PotentialTree result = instance.sortAndBound(threshold);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sort method, of class PotentialTree.
     */
    @Test
    public void testSort() {
        System.out.println("sort");
        PotentialTree instance = new PotentialTree();
        PotentialTree expResult = null;
        PotentialTree result = instance.sort();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sortUtility method, of class PotentialTree.
     */
    @Test
    public void testSortUtility() {
        System.out.println("sortUtility");
        PotentialTree instance = new PotentialTree();
        PotentialTree expResult = null;
        PotentialTree result = instance.sortUtility();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of measureRelevance method, of class PotentialTree.
     */
    @Test
    public void testMeasureRelevance() {
        System.out.println("measureRelevance");
        String varName = "";
        PotentialTree instance = new PotentialTree();
        Vector expResult = null;
        Vector result = instance.measureRelevance(varName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sortUtilityAndPrune method, of class PotentialTree.
     */
    @Test
    public void testSortUtilityAndPrune() {
        System.out.println("sortUtilityAndPrune");
        double minimum = 0.0;
        double maximum = 0.0;
        double limit = 0.0;
        PotentialTree instance = new PotentialTree();
        PotentialTree expResult = null;
        PotentialTree result = instance.sortUtilityAndPrune(minimum, maximum, limit);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of pruneLowValues method, of class PotentialTree.
     */
    @Test
    public void testPruneLowValues() {
        System.out.println("pruneLowValues");
        double limit = 0.0;
        PotentialTree instance = new PotentialTree();
        instance.pruneLowValues(limit);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of limitBound method, of class PotentialTree.
     */
    @Test
    public void testLimitBound_double() {
        System.out.println("limitBound");
        double limit = 0.0;
        PotentialTree instance = new PotentialTree();
        instance.limitBound(limit);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of limitBound method, of class PotentialTree.
     */
    @Test
    public void testLimitBound_double_double() {
        System.out.println("limitBound");
        double limit = 0.0;
        double limitSum = 0.0;
        PotentialTree instance = new PotentialTree();
        instance.limitBound(limit, limitSum);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of limitBound method, of class PotentialTree.
     */
    @Test
    public void testLimitBound_3args() {
        System.out.println("limitBound");
        int kindOfAppr = 0;
        double limit = 0.0;
        double limitSum = 0.0;
        PotentialTree instance = new PotentialTree();
        instance.limitBound(kindOfAppr, limit, limitSum);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of limitBound method, of class PotentialTree.
     */
    @Test
    public void testLimitBound_int() {
        System.out.println("limitBound");
        int limit = 0;
        PotentialTree instance = new PotentialTree();
        instance.limitBound(limit);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of limitBoundUtility method, of class PotentialTree.
     */
    @Test
    public void testLimitBoundUtility() {
        System.out.println("limitBoundUtility");
        double minimum = 0.0;
        double maximum = 0.0;
        double limit = 0.0;
        PotentialTree instance = new PotentialTree();
        instance.limitBoundUtility(minimum, maximum, limit);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSize method, of class PotentialTree.
     */
    @Test
    public void testUpdateSize() {
        System.out.println("updateSize");
        PotentialTree instance = new PotentialTree();
        instance.updateSize();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkSize method, of class PotentialTree.
     */
    @Test
    public void testCheckSize() {
        System.out.println("checkSize");
        PotentialTree instance = new PotentialTree();
        boolean expResult = false;
        boolean result = instance.checkSize();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of copy method, of class PotentialTree.
     */
    @Test
    public void testCopy() {
        System.out.println("copy");
        PotentialTree instance = new PotentialTree();
        Potential expResult = null;
        Potential result = instance.copy();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of normalize method, of class PotentialTree.
     */
    @Test
    public void testNormalize() {
        System.out.println("normalize");
        PotentialTree instance = new PotentialTree();
        instance.normalize();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of instantiateEvidence method, of class PotentialTree.
     */
    @Test
    public void testInstantiateEvidence() {
        System.out.println("instantiateEvidence");
        Configuration evid = null;
        PotentialTree instance = new PotentialTree();
        instance.instantiateEvidence(evid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getClassName method, of class PotentialTree.
     */
    @Test
    public void testGetClassName() {
        System.out.println("getClassName");
        PotentialTree instance = new PotentialTree();
        String expResult = "";
        String result = instance.getClassName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of crossEntropyPotential method, of class PotentialTree.
     */
    @Test
    public void testCrossEntropyPotential() {
        System.out.println("crossEntropyPotential");
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.crossEntropyPotential();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of factoriseOnlyPT method, of class PotentialTree.
     */
    @Test
    public void testFactoriseOnlyPT() {
        System.out.println("factoriseOnlyPT");
        FiniteStates varFac = null;
        FactorisationTools factParam = null;
        PotentialTree instance = new PotentialTree();
        Vector expResult = null;
        Vector result = instance.factoriseOnlyPT(varFac, factParam);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of split method, of class PotentialTree.
     */
    @Test
    public void testSplit() {
        System.out.println("split");
        FiniteStates varSplit = null;
        PotentialTree instance = new PotentialTree();
        Vector expResult = null;
        Vector result = instance.split(varSplit);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of splitOnlyPT method, of class PotentialTree.
     */
    @Test
    public void testSplitOnlyPT() {
        System.out.println("splitOnlyPT");
        FiniteStates varFac = null;
        FactorisationTools factParam = null;
        PotentialTree instance = new PotentialTree();
        Vector expResult = null;
        Vector result = instance.splitOnlyPT(varFac, factParam);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of splitAndFactorisePT method, of class PotentialTree.
     */
    @Test
    public void testSplitAndFactorisePT() {
        System.out.println("splitAndFactorisePT");
        FiniteStates varFac = null;
        FactorisationTools factParam = null;
        PotentialTree instance = new PotentialTree();
        Vector expResult = null;
        Vector result = instance.splitAndFactorisePT(varFac, factParam);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of repair method, of class PotentialTree.
     */
    @Test
    public void testRepair() {
        System.out.println("repair");
        NodeList nodes = null;
        PotentialTree instance = new PotentialTree();
        instance.repair(nodes);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of convertToPotentialTree method, of class PotentialTree.
     */
    @Test
    public void testConvertToPotentialTree() {
        System.out.println("convertToPotentialTree");
        Potential pot = null;
        PotentialTree expResult = null;
        PotentialTree result = PotentialTree.convertToPotentialTree(pot);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of convertUtilityIntoProbability method, of class PotentialTree.
     */
    @Test
    public void testConvertUtilityIntoProbability() {
        System.out.println("convertUtilityIntoProbability");
        PotentialTree instance = new PotentialTree();
        instance.convertUtilityIntoProbability();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of factoriseRT method, of class PotentialTree.
     */
    @Test
    public void testFactoriseRT() {
        System.out.println("factoriseRT");
        FiniteStates x = null;
        PotentialTree instance = new PotentialTree();
        Vector expResult = null;
        Vector result = instance.factoriseRT(x);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of factorisationDegree method, of class PotentialTree.
     */
    @Test
    public void testFactorisationDegree() {
        System.out.println("factorisationDegree");
        FiniteStates x = null;
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.factorisationDegree(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of factorisationDegree2 method, of class PotentialTree.
     */
    @Test
    public void testFactorisationDegree2() {
        System.out.println("factorisationDegree2");
        FiniteStates x = null;
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.factorisationDegree2(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of log method, of class PotentialTree.
     */
    @Test
    public void testLog() {
        System.out.println("log");
        PotentialTree instance = new PotentialTree();
        instance.log();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of randomTree method, of class PotentialTree.
     */
    @Test
    public void testRandomTree() {
        System.out.println("randomTree");
        NodeList v = null;
        PotentialTree expResult = null;
        PotentialTree result = PotentialTree.randomTree(v);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of KLDivergence method, of class PotentialTree.
     */
    @Test
    public void testKLDivergence() {
        System.out.println("KLDivergence");
        PotentialTree t = null;
        PotentialTree t2 = null;
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.KLDivergence(t, t2);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of simulateSample method, of class PotentialTree.
     */
    @Test
    public void testSimulateSample() {
        System.out.println("simulateSample");
        int n = 0;
        PotentialTree instance = new PotentialTree();
        Vector expResult = null;
        Vector result = instance.simulateSample(n);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of evaluateSetOfConfigurations method, of class PotentialTree.
     */
    @Test
    public void testEvaluateSetOfConfigurations() {
        System.out.println("evaluateSetOfConfigurations");
        Vector<Configuration> v = null;
        PotentialTree instance = new PotentialTree();
        double expResult = 0.0;
        double result = instance.evaluateSetOfConfigurations(v);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of bestToFactorise method, of class PotentialTree.
     */
    @Test
    public void testBestToFactorise() {
        System.out.println("bestToFactorise");
        PotentialTree instance = new PotentialTree();
        FiniteStates expResult = null;
        FiniteStates result = instance.bestToFactorise();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mul method, of class PotentialTree.
     */
    @Test
    public void testMul() {
        System.out.println("mul");
        double x = 0.0;
        PotentialTree instance = new PotentialTree();
        PotentialTree expResult = null;
        PotentialTree result = instance.mul(x);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }


   /**
     * Test of createSplitChain method, of class Util.
     */
    @Test
    public void testCreateSplitChain() {
        System.out.println("createSplitChain");
        Node X1 = new FiniteStates("X1", 3);
        Node X2 = new FiniteStates("X2", 3);
        Node X3 = new FiniteStates("X3", 3);
        Node X4 = new FiniteStates("X4", 3);
        Node X5 = new FiniteStates("X5", 3);
        Node X6 = new FiniteStates("X6", 3);
        Node X7 = new FiniteStates("X7", 3);
        Node X8 = new FiniteStates("X8", 3);
        Node X9 = new FiniteStates("X9", 3);
        Node X10 = new FiniteStates("X10", 3);
        Node X11 = new FiniteStates("X11", 3);
        Node X12 = new FiniteStates("X12", 3);
        Node X13 = new FiniteStates("X13", 3);
        Node X14 = new FiniteStates("X14", 3);
        Node X15 = new FiniteStates("X15", 3);
        Node X16 = new FiniteStates("X16", 3);
        Node X17 = new FiniteStates("X17", 3);
        Node X18 = new FiniteStates("X18", 3);
        Node X19 = new FiniteStates("X19", 3);
        Node X20 = new FiniteStates("X20", 3);
        Node X21 = new FiniteStates("X21", 3);
        Node X22 = new FiniteStates("X22", 3);
        Node X23 = new FiniteStates("X23", 3);
        Node X24 = new FiniteStates("X24", 3);
        Node X25 = new FiniteStates("X25", 3);
        Node X26 = new FiniteStates("X26", 3);
        Node X27 = new FiniteStates("X27", 3);
        Node X28 = new FiniteStates("X28", 3);
        Node X29 = new FiniteStates("X29", 3);
        Node X30 = new FiniteStates("X30", 3);
        Node X31 = new FiniteStates("X31", 3);
        Node X32 = new FiniteStates("X32", 3);


        Vector<Node> vars = new Vector<Node>();
        vars.add(X1);
        vars.add(X2);
        vars.add(X3);
        vars.add(X4);
        vars.add(X5);
        vars.add(X6);
        vars.add(X7);
        vars.add(X8);
        vars.add(X9);
        vars.add(X10);
        vars.add(X11);
        vars.add(X12);
        vars.add(X13);
        vars.add(X14);
        vars.add(X15);
//        vars.add(X16);
//        vars.add(X17);
//        vars.add(X18);
//        vars.add(X19);
//        vars.add(X20);
//        vars.add(X21);
//        vars.add(X22);
//        vars.add(X23);
//        vars.add(X24);
//        vars.add(X25);
//        vars.add(X26);
//        vars.add(X27);
//        vars.add(X28);
//        vars.add(X29);
//        vars.add(X30);
//        vars.add(X31);
//        vars.add(X32);
        ProbabilityTree result = new ProbabilityTree();
        //result.assignVar(X1);
        //result.print(0);
    }

}
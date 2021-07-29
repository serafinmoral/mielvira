/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;

import elvira.Configuration;
import elvira.Node;
import elvira.potential.Potential;
import elvira.FiniteStates;
import java.util.Vector;
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
public class UtilTest {

    public UtilTest() {
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
     * Test of getSpacesString method, of class Util.
     */
    @Ignore
    @Test
    public void testGetSpacesString() {
        System.out.println("getSpacesString");
        int spaces = 0;
        String expResult = "";
        String result = Util.getSpacesString(spaces);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkVarInDomain method, of class Util.
     */
    @Ignore
    @Test
    public void testCheckVarInDomain() {
        System.out.println("checkVarInDomain");
        Node node = null;
        Potential potential = null;
        boolean expResult = false;
        boolean result = Util.checkVarInDomain(node, potential);
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
        TreeNode result = Util.createSplitChain(vars);
        //result.print(0);
    }

    /**
     * Test of updateSplitChainLeaf method, of class Util.
     */
    @Ignore
    @Test
    public void testUpdateSplitChainLeaf() {
        System.out.println("createSplitChain to update");
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);
        Node X4 = new FiniteStates("X4", 2);

        Vector<Node> vars = new Vector<Node>();
        vars.add(X1);
        vars.add(X2);
        vars.add(X3);
//        vars.add(X4);
        TreeNode chain = Util.createSplitChain(vars);

        System.out.println("updateSplitChainLeaf");
        Configuration conf = new Configuration(vars);
        TreeNode tree = new ValueTreeNode(0.5);
        for(int i=0; i<conf.possibleValues();i++){
            System.out.println("Configuración:   ");
            conf.print();
            Configuration confy = conf.duplicate();
            Util.updateSplitChainLeaf(chain, confy, tree);
            conf.nextConfiguration();
        }
        // TODO review the generated test code and remove the default call to fail.
        System.out.println("Print result: ");
        chain.print(0);
    }

}
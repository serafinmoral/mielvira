/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.inference.elimination;

import elvira.Bnet;
import elvira.Evidence;
import elvira.Relation;
import elvira.potential.RPT.RecursivePotentialTree;
import elvira.potential.RPT.TreeNode;
import elvira.potential.RPT.learningRPT;
import elvira.potential.CanonicalPotential;
import elvira.potential.PotentialTable;
import elvira.Network;
import elvira.parser.ParseException;
import elvira.Node;
import elvira.NodeList;

import java.io.IOException;
import java.io.FileInputStream;
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
public class VEwithRPTTest {

    public VEwithRPTTest() {
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
     * Test of transformInitialRelation method, of class VEwithRPT.
     */
    @Ignore
    @Test
    public void testTransformInitialRelation() {
        System.out.println("transformInitialRelation");
        Relation r = null;
        VEwithRPT instance = null;
        Relation expResult = null;
        Relation result = instance.transformInitialRelation(r);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

/**
 * Program for performing experiments from the command line.
 * The command line arguments are as follows:
 * <ul>
 * <li> Input file: the network.
 * <li> Output file.
 * <li> Evidence file.
 * </ul>
 * If the evidence file is omitted, then no evidences are
 * considered.
 */

   // @Ignore
    @Test
public void testMain() throws ParseException, IOException {

  Network b;
  Evidence e;
  FileInputStream networkFile, evidenceFile;
  VEwithRPT ve;
  VEWithPotentialTree ve2;
  String base;
  int i;
    //networkFile = new FileInputStream(args[0]);
    //b = new Bnet(networkFile);
    //office
    //b=Network.read("D:/PROYECTOS/2010/VariableElimination/asia.elv");
    //home
    b=Network.read("C:/Users/Cora/Desktop/ElviraCoraProvisional/Diabetes.elv");

    System.out.println(b.getNodeList().size());
//    ve = new VEwithRPT((Bnet)b);
//    ve.obtainInterest();
//System.out.println("DONE! now propagation");
//	 // Propagate
//    ve.generateStatistics=true;
//    ve.statistics.setFileName("stats.txt");
//
//    ve.propagate("info.txt");
//
//        ve2 = new VEWithPotentialTree((Bnet)b);
//    ve2.obtainInterest();
//System.out.println("DONE 2! now propagation");
//	 // Propagate
//    ve2.propagate("info2.txt");
    //ve.statistics.printOperationsAndSizes();
    //ve.statistics.print();
}

    @Ignore
    @Test
public void testAsia() throws ParseException, IOException {

  VEwithRPT ve;
  VEWithPotentialTree ve2;
    //networkFile = new FileInputStream(args[0]);
    //b = new Bnet(networkFile);
    //office
    //b=Network.read("D:/PROYECTOS/2010/VariableElimination/asia.elv");
    //home
    FileInputStream fis = new FileInputStream("C:/Users/Cora/Desktop/ElviraCoraProvisional/artificial.elv");
    Bnet artificial = new Bnet(fis);
    learningRPT instance = new learningRPT(0.001, 0.5);

    Vector<Relation> relations = artificial.getRelationList();
    Vector<Relation> newRelations = new Vector<Relation>();

    for(int i=0; i<relations.size();i++){
       RecursivePotentialTree pot = new RecursivePotentialTree();
       pot.setValues(instance.factorize(relations.get(i).getValues()));
       pot.setVariables(relations.get(i).getVariables().getNodes());
       newRelations.add(new Relation(pot));
    }

    artificial.setRelationList(newRelations);

    Node X4 = artificial.getNode("X4");
    NodeList interest = new NodeList();
    interest.insertNode(X4);

    System.out.println();
    System.out.println();
    System.out.println("----------------------------------------------------");
    System.out.println("----------------IMPRIMIR CURRENT RELATIONS INICIAL--------------");
    for(int w=0; w< artificial.getRelationList().size(); w++){
        ((Relation)artificial.getRelationList().get(w)).getValues().print();
    }
    System.out.println("----------------------------------------------------");
    System.out.println("----------------------------------------------------");
    System.out.println();
    System.out.println();


    ve = new VEwithRPT(artificial);
    ve.setInterest(interest);
    //ve.obtainInterest();

    System.out.println("DONE! now propagation");
	 // Propagate
    ve.generateStatistics=true;
    ve.statistics.setFileName("statsASIA.txt");
    

    ve.propagate("infoASIA.txt");
    ve.statistics.printOperationsAndSizes();

//    ve2 = new VEWithPotentialTree(artificial);
//    ve2.obtainInterest();
//    System.out.println("DONE 2! now propagation");
//	 // Propagate
//    ve2.propagate("info2.txt");
    //ve.statistics.printOperationsAndSizes();
    //ve.statistics.print();
}

}
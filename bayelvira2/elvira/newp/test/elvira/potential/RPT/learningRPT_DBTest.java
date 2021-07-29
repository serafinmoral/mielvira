/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;

import elvira.database.DataBaseCases;
import elvira.potential.PotentialTree;
import elvira.potential.PotentialTable;
import elvira.potential.Potential;
import elvira.Configuration;
import elvira.Bnet;
import elvira.CaseListMem;
import elvira.Relation;
import elvira.learning.PCLearning;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Vector;
import elvira.parser.ParseException;
import java.io.FileWriter;
/**
 *
 * @author cora
 */
public class learningRPT_DBTest {

    public learningRPT_DBTest() {
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
     * Test of learn method, of class learningRPT_DB.
     */
    @Ignore
    @Test
    public void testLearn() throws ParseException, IOException {
        System.out.println("learn");
        FileInputStream f = new FileInputStream("/home/cora/Dropbox/ElviraCoraProvisional/newfile.dbc");
        DataBaseCases data = new DataBaseCases(f);
        //data.setNumberOfCases(624);

        learningRPT_DB instance = new learningRPT_DB();
        TreeNode result = instance.learn(data);

        System.out.println();
        System.out.println("FINISH!");
        System.out.println();

        RecursivePotentialTree newTree = new RecursivePotentialTree();
        newTree.setValues(result);
        newTree.normalize();
        //newTree.print(0);
        
        PotentialTable pot = data.getPotentialTable(data.getVariables());
        pot.normalize();
        //pot.print(2);
        PotentialTree auxtree = new PotentialTree(pot);
        auxtree.normalize();
        //auxtree.print();
        PotentialTable potab = newTree.getPotentialTable();
        double KL = this.KLdivergence(auxtree, potab);
        double mse = this.meanSquaredError(auxtree, potab);
        System.out.println("KL divergence" + KL + " and MSE: "+mse);


        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
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


       @Ignore
    @Test
    public void asiaTest()throws elvira.parser.ParseException ,java.io.IOException{
        FileInputStream fis = new FileInputStream("/home/cora/Dropbox/ElviraCoraProvisional/asia.elv");
        Bnet asia = new Bnet(fis);
        Vector<Relation> rels = asia.getRelationList();
        Potential combined = rels.get(0).getValues();
        for(int i=1; i<rels.size(); i++){
            combined = combined.combine(rels.get(i).getValues());
        }

        System.out.println("relations combined");

        FileWriter ftr = new FileWriter("DBsampledBarley.dbc");
        DataBaseCases data = new DataBaseCases(asia,ftr,1000,true);

        learningRPT_DB instance = new learningRPT_DB();
        TreeNode result = instance.learn(data);

        
        System.out.println("model learned");

        RecursivePotentialTree newTree = new RecursivePotentialTree();
        newTree.setValues(result);
        newTree.normalize();
        PotentialTable potab = newTree.getPotentialTable();

        double KL = this.KLdivergence(combined, potab);
        double mse = this.meanSquaredError(combined, potab);
        System.out.println("second exp. KL divergence" + KL + " and MSE: "+mse);

        System.out.println("And the tree: ");
        newTree.print(2);
    }

    @Test
    public void asiaTestAux() throws ParseException, FileNotFoundException, IOException {

        FileInputStream f = new FileInputStream("asiaDataBase2.dbc");
        DataBaseCases dataBase = new DataBaseCases(f);

        DataBaseCases tr = new DataBaseCases();
        DataBaseCases ts = new DataBaseCases();
        dataBase.divideIntoTrainAndTest(tr, ts, 0.7);

        FileWriter ftr = new FileWriter("trainAsia.dbc");
        FileWriter fts = new FileWriter("testAsia.dbc");

        tr.saveDataBase(ftr);
        ts.saveDataBase(fts);

        learningRPT_DB instance = new learningRPT_DB();
        TreeNode result = instance.learn(tr);
        System.out.println("model learned");

        RecursivePotentialTree newTree = new RecursivePotentialTree();
        newTree.setValues(result);
        newTree.normalize();

        CaseListMem clm = ts.getCaseListMem();
        Vector<Configuration> clmvect = new Vector<Configuration>();
        int sizeConf = clm.getNumberOfCases();
        for(int i=0; i<sizeConf; i++){
            clmvect.add(clm.get(i));
        }

        double likelihoodTree = newTree.evaluateSetOfConfigurations(clmvect);
        newTree.print(0);
        System.out.println("Verosimilitud RPT: "+likelihoodTree);

        //asiaK2.elv
        FileInputStream fis = new FileInputStream("/home/cora/Dropbox/ElviraCoraProvisional/asiaK2.elv");
       // Bnet asia = new Bnet(fis);
        Bnet asianet = new Bnet(fis);

        double likelihoodBnet = ts.logLikelihood(asianet);
        System.out.println("Verosimilitud Bnet K2: "+likelihoodBnet);

//        PCLearning pcalg = new PCLearning(ts);
//        pcalg.learning();
//        Bnet asianet2 = pcalg.getOutput();
//
//        double likelihoodBnetPC = ts.logLikelihood(asianet2);
//        System.out.println("Verosimilitud Bnet PC: "+likelihoodBnetPC);
    }

}

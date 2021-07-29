/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import elvira.Node;
import elvira.NodeList;
import elvira.parser.ParseException;
import elvira.database.DataBaseCases;
import elvira.learning.BDeMetrics;

import java.io.*;
/**
 *
 * @author mgomez
 */
public class TestScore {
    /**
     * Database cases to use
     */
    private DataBaseCases data;

    /**
     * Metric to use
     */
    BDeMetrics metric;

    public TestScore() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException, ParseException{
        FileInputStream f = new FileInputStream("asiaDataBase.dbc");
        data = new DataBaseCases(f);

        // Creates a BDEMetric object
        metric=new BDeMetrics(data,2);
    }

    @After
    public void tearDown() {
    }

    /**
     * Method to check the link between S and L
     */
    @Test
    public void checkLinkSL(){
        NodeList nodex=new NodeList();
        NodeList nodey=new NodeList();
        Node nodeS=data.getNode("S");
        Node nodeL=data.getNode("L");
        Node nodeA=data.getNode("A");
        Node nodeT=data.getNode("T");
        Node nodeB=data.getNode("B");
        Node nodeE=data.getNode("E");
        Node nodeX=data.getNode("X");
        Node nodeD=data.getNode("D");

        // Check the link between S and L
        nodex.insertNode(nodeS);
        nodey.insertNode(nodeL);
        nodey.insertNode(nodeS);

        // Now perform both tests
        double score1=metric.score(nodex);
        double score2=metric.score(nodey);

        System.out.println("ENTRE S y L : Score1: "+score1+"  Score2: "+score2);
        System.out.println(" Mejora: "+(score2 > score1));

        // Now check the link between D and its parents: L and B
        nodex=new NodeList();
        nodey=new NodeList();
        nodex.insertNode(nodeE);
        nodey.insertNode(nodeT);
        nodey.insertNode(nodeE);

        score1=metric.score(nodex);
        System.out.println("Score para E solo: "+score1);

        score2=metric.score(nodey);
        System.out.println("Score para E y T : "+score2);

        nodey=new NodeList();
        nodey.insertNode(nodeL);
        nodey.insertNode(nodeE);
        score2=metric.score(nodey);
        System.out.println("Score para E y L: "+score2);

        // Now add the other parent
        nodey.insertNode(nodeT);
        double score3=metric.score(nodey);
        System.out.println("Score para E, T y L : "+score3);
    }
}
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
import elvira.RelationList;
import elvira.Relation;
import elvira.InvalidEditException;
import elvira.parser.ParseException;
import elvira.Bnet;
import elvira.LinkList;
import elvira.Link;
import elvira.potential.PotentialTree;
import elvira.NodeList;
import elvira.inference.elimination.VEWithPotentialTree;
import elvira.inference.elimination.VEwithRPT;
import elvira.inference.elimination.VEWithPotentialBPTree;
import elvira.Evidence;

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

/**
 *
 * @author Cora
 */
public class TestSimple {
        Node X1 = new FiniteStates("X1", 2);
        Node X2 = new FiniteStates("X2", 2);
        Node X3 = new FiniteStates("X3", 2);
        Node X4 = new FiniteStates("X4", 2);
        Node X5 = new FiniteStates("X5", 2);
        Node X6 = new FiniteStates("X6", 2);
    public TestSimple() {
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


    private Bnet createNetwork() throws Exception {
        Bnet net=new Bnet();

        // Se agregan todas las variables a la red
        net.addNode(X1);
        net.addNode(X2);
        net.addNode(X3);
        net.addNode(X4);
        net.addNode(X5);
        net.addNode(X6);

        Vector<Node> variables = new Vector<Node>();
        variables.add(X6);
        PotentialTree potential = new PotentialTree(variables);
        double values[]={0.3,0.7};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }

        // Se agrega el potencia a la red
        Relation rel=new Relation(variables);
        rel.setValues(potential);
        net.addRelation(rel);

        // Se hace lo mismo para X1
        variables=new Vector<Node>();
        variables.add(X1);
        potential=new PotentialTree(variables);
        values=new double[2];
        values[0]=0.1;
        values[1]=0.9;
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }

        // Se agrega el potencia a la red
        rel=new Relation(variables);
        rel.setValues(potential);
        net.addRelation(rel);

        // Igual para X5
        variables=new Vector<Node>();
        variables.add(X5);
        potential=new PotentialTree(variables);
        values=new double[2];
        values[0]=0.7;
        values[1]=0.3;
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }

        // Se agrega el potencia a la red
        rel=new Relation(variables);
        rel.setValues(potential);
        net.addRelation(rel);

        // Ahora se crea la condicionada de X3 dado X1
        variables=new Vector<Node>();
        variables.add(X3);
        variables.add(X1);
        potential=new PotentialTree(variables);
        values=new double[2];
        double valuesCondicionadaX3X1[]={0.1, 0.2 , 0.9, 0.8};
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, valuesCondicionadaX3X1[i]);
            conf.nextConfiguration();
        }

        // Se agrega el potencia a la red
        rel=new Relation(variables);
        rel.setValues(potential);
        net.addRelation(rel);

        // Igual con la condicionada de X2 dado X1 y X6
        variables=new Vector<Node>();
        variables.add(X2);
        variables.add(X1);
        variables.add(X6);
        potential=new PotentialTree(variables);
        double valuesCondicionadaX2X1X6[]={0.4, 0.3, 0.1, 0.7, 0.6, 0.7, 0.9, 0.3};
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, valuesCondicionadaX2X1X6[i]);
            conf.nextConfiguration();
        }

        // Se agrega el potencia a la red
        rel=new Relation(variables);
        rel.setValues(potential);
        net.addRelation(rel);

        // Igual con la condicionada de X4 dado X2, X3 y X5
        variables=new Vector<Node>();
        variables.add(X4);
        variables.add(X2);
        variables.add(X3);
        variables.add(X5);
        potential=new PotentialTree(variables);
        double valuesCondicionadaX4X2X3X5[]={0.5, 0.5, 0.2, 0.4, 1, 1, 1, 1, 0.5, 0.5, 0.8, 0.6, 0, 0, 0, 0};
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, valuesCondicionadaX4X2X3X5[i]);
            conf.nextConfiguration();
        }

        // Se agrega el potencia a la red
        rel=new Relation(variables);
        rel.setValues(potential);
        net.addRelation(rel);

        // Hay que crear los enlaces
        LinkList links=new LinkList();
        Link link=new Link(X6,X2);
        links.insertLink(link);
        link=new Link(X1,X2);
        links.insertLink(link);
        link=new Link(X1,X3);
        links.insertLink(link);
        link=new Link(X2,X4);
        links.insertLink(link);
        link=new Link(X3,X4);
        links.insertLink(link);
        link=new Link(X5,X4);
        links.insertLink(link);

        // Se asignan los enlaces
        net.setLinkList(links);

        // Se imprime la red completa
        net.save("artificial.elv");

        return net;
    }

    private Bnet createNetworkWithRPT() throws Exception {
        Bnet net=new Bnet();

        // Se agregan todas las variables a la red
        net.addNode(X1);
        net.addNode(X2);
        net.addNode(X3);
        net.addNode(X4);
        net.addNode(X5);
        net.addNode(X6);

        // Se crea el potential para X6
        RecursivePotentialTree rptForX6=createRPTForX6();

        // Se agrega el potencial a la red
        Vector variables=new Vector();
        variables.add(X6);
        Relation rel=new Relation(variables);
        rel.setValues(rptForX6);
System.out.println("Potencial creado para X6: "+rptForX6.getClass().getName());
System.out.println("Tipo de values de relacion: "+rel.getValues().getClass().getName());
        net.addRelation(rel);

        // Se crea el potencial para X1
        RecursivePotentialTree rptForX1=createRPTForX1();

        // Se agrega el potencial a la red
        variables=new Vector();
        variables.add(X1);
        rel=new Relation(variables);
        rel.setValues(rptForX1);
        net.addRelation(rel);

        // Se crea el potencial para X5
        RecursivePotentialTree rptForX5=createRPTForX5();

        // Se agrega el potencial a la red
        variables=new Vector();
        variables.add(X5);
        rel=new Relation(variables);
        rel.setValues(rptForX5);
        net.addRelation(rel);

        // Se crea el potencial para X3
        RecursivePotentialTree rptForX3=createRPTForX3();

        // Se agrega el potencial a la red
        variables=new Vector();
        variables.add(X3);
        variables.add(X1);
        rel=new Relation(variables);
        rel.setValues(rptForX3);
        net.addRelation(rel);

        // Se crea el potencial para X2
        RecursivePotentialTree rptForX2=createRPTForX2();

        // Se agrega el potencial a la red
        variables=new Vector();
        variables.add(X2);
        variables.add(X1);
        variables.add(X6);
        rel=new Relation(variables);
        rel.setValues(rptForX2);
        net.addRelation(rel);

        // Se agrega el potencial para X4
        RecursivePotentialTree rptForX4=createRPTForX4();

        // Se agrega el potencial a la red
        variables=new Vector();
        variables.add(X4);
        variables.add(X2);
        variables.add(X3);
        variables.add(X5);
        rel=new Relation(variables);
        rel.setValues(rptForX4);
        net.addRelation(rel);

        // Hay que crear los enlaces
        LinkList links=new LinkList();
        Link link=new Link(X6,X2);
        links.insertLink(link);
        link=new Link(X1,X2);
        links.insertLink(link);
        link=new Link(X1,X3);
        links.insertLink(link);
        link=new Link(X2,X4);
        links.insertLink(link);
        link=new Link(X3,X4);
        links.insertLink(link);
        link=new Link(X5,X4);
        links.insertLink(link);

        // Se asignan los enlaces
        net.setLinkList(links);

        // Se imprime la red completa
        //net.save("artificialRPT.elv");

        return net;
    }

    private RecursivePotentialTree createRPTForX6(){
        RecursivePotentialTree potTree=new RecursivePotentialTree();

        //Se crea el potencial para X6
        Vector<Node> variables = new Vector<Node>();
        variables.add(X6);
        PotentialTree potential = new PotentialTree(variables);
        double values[]={0.3,0.7};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX6 = new PotentialTreeNode(potential);
        potTree.setValues(potNodeX6);
        return potTree;
    }

    private RecursivePotentialTree createRPTForX1(){
         RecursivePotentialTree potTree=new RecursivePotentialTree();

         // Se crea el RPT para X1
         Vector<Node> variables = new Vector<Node>();
         variables.add(X1);
         PotentialTree potential = null;
         potential = new PotentialTree(variables);
         double values[]={0.1,0.9};
         Configuration conf=new Configuration(variables);
         for(int i=0; i < conf.possibleValues(); i++){
             potential.setValue(conf, values[i]);
             conf.nextConfiguration();
         }
         PotentialTreeNode potNodeX1 = new PotentialTreeNode(potential);
         potTree.setValues(potNodeX1);
         return potTree;
    }

    private RecursivePotentialTree createRPTForX5(){
         RecursivePotentialTree potTree=new RecursivePotentialTree();

         // Se crea el RPT para X5
         Vector<Node> variables = new Vector<Node>();
         variables.add(X5);
         PotentialTree potential = null;
         potential = new PotentialTree(variables);
         double values[]={0.7,0.3};
         Configuration conf=new Configuration(variables);
         for(int i=0; i < conf.possibleValues(); i++){
             potential.setValue(conf, values[i]);
             conf.nextConfiguration();
         }
         PotentialTreeNode potNodeX5 = new PotentialTreeNode(potential);
         potTree.setValues(potNodeX5);
         return potTree;
    }

    private RecursivePotentialTree createRPTForX3(){
        RecursivePotentialTree potTree=new RecursivePotentialTree();

        Vector variables=new Vector();
        variables.add(X3);
        variables.add(X1);
        PotentialTree potential=new PotentialTree(variables);
        double values[]={0.1,0.2,0.9,0.8};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX3 = new PotentialTreeNode(potential);
        potTree.setValues(potNodeX3);
        return potTree;
    }

    private RecursivePotentialTree createRPTForX2(){
        RecursivePotentialTree potTree=new RecursivePotentialTree();

        Vector variables=new Vector();
        variables.add(X2);
        variables.add(X1);
        variables.add(X6);
        PotentialTree potential=new PotentialTree(variables);
        double values[]={0.4,0.3,0.1,0.7,0.6,0.7,0.9,0.3};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX2 = new PotentialTreeNode(potential);
        potTree.setValues(potNodeX2);
        return potTree;
    }

    private RecursivePotentialTree createRPTForX4(){

        Vector variables=new Vector();
        variables.add(X5);
        variables.add(X4);
        PotentialTree potential = new PotentialTree(variables);
        double values6[]={0.2,0.8,0.4,0.6};
        Configuration conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values6[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX4X5 = new PotentialTreeNode(potential);

        //potencial 7
        variables.clear();
        variables.add(X4);
        potential = null;
        potential = new PotentialTree(variables);
        double values7[]={1,0};
        conf = null;
        conf=new Configuration(variables);
        for(int i=0; i < conf.possibleValues(); i++){
            potential.setValue(conf, values7[i]);
            conf.nextConfiguration();
        }
        PotentialTreeNode potNodeX4 = new PotentialTreeNode(potential);

        SplitTreeNode spx3 = new SplitTreeNode(X3);
        ValueTreeNode value1 = new ValueTreeNode(0.5);
        spx3.addSon(value1);
        spx3.addSon(potNodeX4X5);

        SplitTreeNode spx2 = new SplitTreeNode(X2);
        spx2.addSon(spx3);
        spx2.addSon(potNodeX4);

        RecursivePotentialTree original = new RecursivePotentialTree();
        original.setValues(spx2);
        return original;
    }

    //@Ignore
    @Test
    public void inferenceWithTrees() throws Exception{
        String base;
System.out.println("Metodo inferenceWithTrees...........");
        Bnet net=createNetwork();
        VEWithPotentialTree evaluator=new VEWithPotentialTree(net);
        if (evaluator.generateStatistics == true){
            base="artificial";
            base=base.concat("_VEWithPotentialTree_data");
            evaluator.statistics.setFileName(base);
         }
        NodeList interestVar = new NodeList();
        interestVar.insertNode(X4);
        evaluator.setInterest(interestVar);
        evaluator.obtainInterest();
        evaluator.propagate("propagacionArboles.txt");
        evaluator.statistics.printOperationsAndSizes();
System.out.println("Fin de metodo inferenceWithTrees........");
    }

    //@Ignore
    @Test
    public void inferenceWithRPTrees() throws Exception{
        String base;
        Bnet net=createNetworkWithRPT();
        VEwithRPT evaluator=new VEwithRPT(net);
        if (evaluator.generateStatistics == true){
            base="artificialRPT";
            base=base.concat("_VEWithRPT_data");
            evaluator.statistics.setFileName(base);
         }
        NodeList interestVar = new NodeList();
        interestVar.insertNode(X4);
        evaluator.setInterest(interestVar);
        NodeList interestNode=new NodeList();
        interestNode.insertNode(X2);
        //evaluator.setInterest(interestNode);
        evaluator.obtainInterest();
        evaluator.propagate("propagacionRPT.txt");
        evaluator.statistics.printOperationsAndSizes();
    }

    @Ignore
    @Test
    public void testPerformanceInference() throws ParseException, IOException, InvalidEditException{

        FileInputStream fis = new FileInputStream("artificial.elv");
        Bnet b = new Bnet(fis);

        System.out.println("Potential Tree");

        VEWithPotentialTree veTree = new VEWithPotentialTree(b);
        veTree.obtainInterest();
        veTree.propagate("tree.txt");

        System.out.println("Recursivos");

        VEwithRPT veRecursiveTree = new VEwithRPT(b);
        veRecursiveTree.obtainInterest();
        veRecursiveTree.propagate("rpt.txt");


        System.out.println("Binary trees");
        Evidence e = new Evidence();
        VEWithPotentialBPTree veBinaryTree = new VEWithPotentialBPTree(b, e);
        veBinaryTree.obtainInterest();
        veBinaryTree.propagate("binaryTree.txt");

    }


}
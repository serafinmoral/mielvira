
/**
 * K2Learning.java
 *
 *
 * Created: Tue Nov 23 10:08:13 1999
 *
 * @author P. Elvira.
 * @version 1.0
 */

import java.util.*;
import java.io.*;


public class K2Learning extends Learning {
    
    NodeList nodesSorted;  // A List of Nodes sorted.
    DataBaseCases input;   // The cases for the input algorithm.
    int numberMaxOfParents;// The number of maximal parents for each node.
    K2Metrics metric;      // The K2 metric for scoring.
    int begining; // Index for the input nodes, nodei<begining are considered as root nodes.


 public static void main(String args[]) throws ParseException, IOException { 
      
     int addel[] = new int[3];
     NodeList nodes;
     Bnet b,baprend;
     FileWriter f2,f1;
     FileInputStream f = new FileInputStream(args[0]);
     b = new Bnet(f);
     nodes = b.topologicalOrder();
     f1 = new FileWriter(args[1]);
     
     DataBaseCases cases = new DataBaseCases(b,f1,5000,false);
     K2Learning outputNet1 = new K2Learning(cases,nodes,5);
     
     outputNet1.learning();
     
     f2 = new FileWriter(args[2]);
     baprend = (Bnet)outputNet1.getOutput();
     baprend.saveBnet(f2);
     f2.close();
     f1.close();
     addel = outputNet1.compareOutput(b);
     System.out.print("\nNumero de arcos añadidos: "+addel[0]);
     System.out.print("\nNumero de arcos borrados: "+addel[1]);
     System.out.println("\nNumero de arcos mal orientados: "+addel[2]);

   }  

    public K2Learning(){
	setNodesSorted(null);
	setInput(null);
	setMetric(null);
    }

    /**
     * Constructor for the Learning Algorithm K2.
     * @param DataBaseCases. cases. The input of algorithm.
     * @param NodeList nodes. The list of nodes sorted.
     * @param int nMaxOfParents. The max number of parents for each node.
     */

    public K2Learning(DataBaseCases cases,NodeList nodes,int nMaxParents){
	nodesSorted = nodes;
	input = cases;
	numberMaxOfParents = nMaxParents;
	begining = 1;
	metric = new K2Metrics(cases);
    }
    
    /**
     * This methods implements the K2 algorithm.
     */

    public void  learning(){

	int i,j;
	FiniteStates nodeXi,nodeZ;
	NodeList PaXi,vars;
	double fitness,fitnessNew;
	boolean OkToProceed;
	LinkList links;
	Link newLink;
	
	links = new LinkList();
	
	for(i=begining; i< nodesSorted.size();i++){
	    nodeXi = (FiniteStates)nodesSorted.elementAt(i);
	    PaXi = new NodeList();
	    vars = new NodeList();
	    vars.insertNode(nodeXi);
	    fitness = metric.score(vars);
	    OkToProceed = true;
	    while(OkToProceed && (PaXi.size()<=numberMaxOfParents)){
		nodeZ = maxScore(nodeXi,PaXi,i);
		if(nodeZ!=null){
		    vars = new NodeList();
		    vars.insertNode(nodeXi);
		    if(PaXi.size()>0)
			vars.join(PaXi);
		    vars.insertNode(nodeZ);
		    fitnessNew = metric.score(vars);
		    if(fitnessNew > fitness){
			fitness = fitnessNew;
			PaXi.insertNode(nodeZ);
		    }
		    else OkToProceed = false;
		}
		else OkToProceed = false;
	    }
	    for(j=0 ; j<PaXi.size();j++){
		newLink = new Link(PaXi.elementAt(j),nodeXi);
		links.insertLink(newLink);
	    }
	}

	setOutput(new Bnet());
	for(i=0 ; i< nodesSorted.size();i++)
	    getOutput().addNode(nodesSorted.elementAt(i));
	for(i=0 ; i< links.size();i++){
	    newLink = (Link) links.elementAt(i);
	    getOutput().createLink(newLink.getTail(),newLink.getHead());
	}
	    
    }

    /**
     * This methos is private. It is used for searching the parent for the node
     * nodei that maximize the score metric.
     * @param FiniteStates nodei. the node.
     * @param NodeList pa. The actual parents set for the node i.
     * @param int index. The position for the node i.
     * @return FiniteStates. The maximal node.
     */

    private FiniteStates maxScore(FiniteStates nodei,NodeList pa,int index){

	int i;
	FiniteStates node, nodeZ;
	NodeList vars;
	double val;
	double max = 0.0;

	nodeZ=null;

	for(i=0; i<index; i++){
	    node = (FiniteStates)nodesSorted.elementAt(i);
	    if(pa.getId(node) == -1){
		vars = new NodeList();
		vars.insertNode(nodei);
		vars.join(pa);
		vars.insertNode(node);
		val = metric.score(vars);
		if(val > max){
		    max = val;
		    nodeZ = node;
		}
	    }
	}
	return nodeZ;
    }

    public void setNodesSorted(NodeList nl){
	nodesSorted = nl;
    }
    public void setInput(DataBaseCases db){
	input = db;
    }
    public void setMetric(K2Metrics metric){
	this.metric = metric;
    }
} // K2Learning










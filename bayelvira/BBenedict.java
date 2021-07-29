
/**
 * BBenedict.java
 *
 *
 * Created: Tue Nov 23 10:08:13 1999
 *
 * @author P. Elvira
 * @version 1.0
 */

import java.util.*;
import java.io.*;
import DataBaseCases;
import NodeList;
import KLMetrics;

public class BBenedict extends Learning {
    
    NodeList nodesSorted;  // A List of Nodes Sorted.
    DataBaseCases input;   // The data base of cases.
    double umbral1;        //  
    double umbral2;        // 
    KLMetrics metric;      // The metric for scoring bnets.
    int begining; // Index for the input nodes, nodei<begining are considered as root nodes.


 public static void main(String args[]) throws ParseException, IOException { 
      
      NodeList nodes;
      Bnet b,baprend;
      FileWriter f2,f1;
      FileInputStream f = new FileInputStream(args[0]);
      b = new Bnet(f);
      nodes = b.topologicalOrder();
      f1 = new FileWriter(args[1]);

      DataBaseCases cases = new DataBaseCases(b,f1,1000,true);
      BBenedict outputNet1 = new BBenedict(cases,nodes,0.01,0.01);           

      outputNet1.learning();
            
      f2 = new FileWriter(args[2]);
      baprend = (Bnet)outputNet1.getOutput();
      baprend.saveBnet(f2);
      f2.close();
      f1.close();
     

   }  

    public BBenedict(){
	setInput(null);
	setNodesSorted(null);
	setMetric(null);
    }



    /** 
     * Constructor for the Learning Algoritm Basic Benedict.
     * @param DataBaseCases. The cases for the input.
     * @param NodeList nodes. A List of nodes sorted.
     * @param double u1. For stoping the learning process.
     * @param double u2. For stoping the learning process.
     */


    public BBenedict(DataBaseCases cases,NodeList nodes,double u1,double u2){

	Bnet bLearned;

	nodesSorted = nodes;
	input = cases;
	umbral1=u1;
	umbral2=u2;
	begining = 1;
	metric = new KLMetrics(cases);
	bLearned = new Bnet();
	bLearned.setNodeList(nodes);
	setOutput(bLearned);
    }
    
    /**
     * This methos computes the learning process.
     */


    public void  learning(){

	int i,j,h;
	FiniteStates nodeXi,nodeXj;
	NodeList vars,minDsep;
	double fitness,fitnessold,min;
	LinkList links;
	Link newLink,linkToInsert=null;
	Bnet currentBnet;
	
	currentBnet = getOutput();
	fitness = metric.score(currentBnet);
	links = new LinkList();
	for(i=begining ; i< nodesSorted.size();i++){
	    nodeXi = (FiniteStates)nodesSorted.elementAt(i);
	    for(j=0 ; j<i ; j++){
		nodeXj = (FiniteStates)nodesSorted.elementAt(j);
		newLink = new Link(nodeXj,nodeXi);
		links.insertLink(newLink);
	    }
	}
	
	min = fitness;
	fitnessold = (new Double(1/0.)).doubleValue();
	
	while(!stop(fitness,fitnessold)){
	    fitnessold = fitness;
	    for(h=0 ; h < links.size() ; h++){
		newLink = (Link)links.elementAt(h);
		currentBnet.createLink(newLink.getTail(),newLink.getHead());
		fitness = 0.0;
		for(i=0; i< nodesSorted.size();i++){
		    nodeXi = (FiniteStates)nodesSorted.elementAt(i);
		    for(j=0 ; j<i;j++){
			nodeXj = (FiniteStates)nodesSorted.elementAt(j);
			if(currentBnet.parents(nodeXi).getId(nodeXj)==-1){
			    minDsep = currentBnet.minimunDSeparatingSet(
				      (Node)nodeXi,(Node)nodeXj);
			    vars = new NodeList();
			    vars.insertNode(nodeXi);
			    vars.insertNode(nodeXj);
			    vars.join(minDsep);
			    fitness+=metric.score(vars);
			}
		    }
		}
		if(fitness < min){
		    min = fitness;
		    linkToInsert = newLink;
		}
		currentBnet.removeLink(newLink.getTail(),newLink.getHead());
	    }
	    fitness = min;
	    currentBnet.createLink(linkToInsert.getTail(),linkToInsert.getHead());
	    links.removeLink(linkToInsert);
	}
		
	setOutput(currentBnet);
	    
    }

    /**
     * This methos is private. For stoping the learning process.
     * @param double. fitness for the current bnet.
     * @param double. fitness for the previous bnet.
     */

    private boolean stop(double fitness, double fitnessold){

	if(fitness < umbral1) return true;
	if((Math.abs(fitness-fitnessold)) < umbral2) return true;
	return false;
    }


    public void setInput(DataBaseCases data){
	this.input=data;
    }

    public void setNodesSorted(NodeList ndl){
	nodesSorted = ndl;
    }

    public void setMetric(KLMetrics metric){
	this.metric = metric;
    }


} // BBenedict










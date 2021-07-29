
/**
 * PCLearning.java
 *
 *
 * Created: Tue May 11 11:54:08 1999
 *
 * @author Proyecto Elvira
 * @version 1.0
 */

import DataBaseCases;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.io.*;
import java.util.Date;


public class PCLearning extends Learning  {

    ConditionalIndependence input; // Input of the Learning Process.
    //Network output;                // Output of the PC Algorithm.
    Date delay;                    // Delay of the Learning Process.
    int numberOfTest;              // Number of test in the learning process.
    double setSizeCondMean;        // size mean of conditionating set.
    int levelOfConfidence;         // [0..4] level of confidence for the C.I. tests

   public static void main(String args[]) throws ParseException, IOException { 

      int[] addel = new int[3];      
      NodeList nodes,minimun; LinkList links; Node nodex,nodey;
      Bnet b,baprend;
      FileWriter f2,f1;
      FileInputStream f = new FileInputStream(args[0]);
      b = new Bnet(f);

      f1 = new FileWriter(args[1]);

      DataBaseCases cases = new DataBaseCases(b,f1,10000,false);
      //PCLearning outputNet1 = new PCLearning(b);
      PCLearning outputNet2 = new PCLearning(cases);
      //outputNet1.setLevelOfConfidence(3);
      outputNet2.setLevelOfConfidence(3);
      //outputNet1.learning();
      outputNet2.learning();
      
      //f2 = new FileWriter(args[2]);
      //baprend = (Bnet)outputNet1.getOutput();
      //baprend.saveBnet(f2);
      //f2.close();
      f2 = new FileWriter(args[2]);
      baprend = (Bnet)outputNet2.getOutput();
      baprend.saveBnet(f2);
      f2.close();
      f1.close();
      addel = outputNet2.compareOutput(b);
      System.out.print("\nNumero de arcos añadidos: "+addel[0]);
      System.out.print("\nNumero de arcos borrados: "+addel[1]);
      System.out.println("\nNumero de arcos mal orientados: "+addel[2]);
     

   }  

public PCLearning(){

    setInput(null);
    setOutput(null);
}
    /** 
     * Initializes a full unidirected graph with the variables contained into
     * the Data Base of Cases cases. Also initializes the input of algorithm
     * PC as a Data Base of Cases and the PC output as a Bnet with the above
     * graph. 
     * @param DataBaseCases cases. The data bases of discrete cases.
     */    


public PCLearning(DataBaseCases cases) {

    Bnet b;
    Graph dag;
    NodeList nodes;
    LinkList links;
    Link link;
    Node nodet,nodeh;
    boolean directed = false;
    int i,j;

    nodes = cases.getVariables();
    //links = new LinkList();
    b = new Bnet();
    dag = (Graph) b;
    for(i=0 ; i < (nodes.size()) ;i++)
	dag.addNode(nodes.elementAt(i));

    for(i=0 ; i < (nodes.size()-1) ;i++)
	for(j=i+1 ; j<nodes.size() ;j++){
	    nodet=(Node)nodes.elementAt(i);
	    nodeh=(Node)nodes.elementAt(j);
	    dag.createLink(nodet, nodeh, directed);
	}
    
    this.input = cases;
    setOutput(b);
    setLevelOfConfidence(4);
    
} 


    /** 
     * Initializes a full unidirected graph with the variables contained into
     * the List of Nodes nodes. Also initializes the input of algorithm
     * PC as a Data Base of Cases and the PC output as a Bnet with the above
     * graph. It's very important that the variables contained in nodes are
     * a subset of the variables contained in the Data Bases of Cases.
     * @see DataBaseCases - method getVariables();
     * @param DataBaseCases cases. The data bases of discrete cases.
     * @param NodeList nodes. Must be a subset of variables of the cases.
     */    


public PCLearning(DataBaseCases cases, NodeList nodes) {

    Bnet b;
    Graph dag;
    LinkList links;
    Link link;
    Node nodet,nodeh;
    boolean directed = false;
    int i,j;

    //links = new LinkList();
    b = new Bnet();
    dag = (Graph) b;
    for(i=0 ; i < (nodes.size()) ;i++)
	dag.addNode(nodes.elementAt(i));
    
    for(i=0 ; i < (nodes.size()-1) ;i++)
	for(j=i+1 ; j<nodes.size() ;j++){
	    nodet=(Node)nodes.elementAt(i);
	    nodeh=(Node)nodes.elementAt(j);
	    dag.createLink(nodet, nodeh, directed);
	}
    
    this.input = cases;
    setOutput(b);
    setLevelOfConfidence(4);
    
}


    /** 
     * Initializes a full unidirected graph with the variables contained into
     * the List of Nodes of the parameter input. Also initializes the input of
     * algorithm PC as a Graph and the PC output as a Bnet with the above
     * graph. 
     * @param Graph input. The input graph. (d-separation criterion).
     */    

public PCLearning(Graph input) {

    Bnet b;
    Graph dag;
    NodeList nodes;
    LinkList links;
    Link link;
    Node nodet,nodeh;
    boolean directed = false;
    int i,j;
    
    nodes = input.getNodeList();
    b = new Bnet();
    dag = (Graph) b;
    for(i=0 ; i < (nodes.size()) ;i++)
	dag.addNode(nodes.elementAt(i));
    //links = new LinkList();
    
    for(i=0 ; i < (nodes.size()-1) ;i++)
	for(j=i+1 ; j<nodes.size() ;j++){
	    nodet=(Node)nodes.elementAt(i);
	    nodeh=(Node)nodes.elementAt(j);
	    dag.createLink(nodet, nodeh, directed);
	    
	}
   
    this.input=input;
    setOutput(b);
    setLevelOfConfidence(4);
    
}


    /**
     * see  PCLearning(Graph input) and 
     * also PCLearning(DataBaseCases cases, NodeList nodes)
     * @param Graph input
     * @param NodeList nodes.
     */

public PCLearning(Graph input, NodeList nodes) {

    Bnet b;
    Graph dag;
    LinkList links;
    Link link;
    Node nodet,nodeh;
    boolean directed = false;
    int i,j;
    
    //links = new LinkList();
    
    b = new Bnet();
    dag = (Graph) b;
    for(i=0 ; i < (nodes.size()) ;i++)
	dag.addNode(nodes.elementAt(i));

    for(i=0 ; i < (nodes.size()-1) ;i++)
	for(j=i+1 ; j<nodes.size() ;j++){
	    nodet=(Node)nodes.elementAt(i);
	    nodeh=(Node)nodes.elementAt(j);
	    dag.createLink(nodet, nodeh, directed);
	    
	}
    
    this.input = input;
    setOutput(b);
    setLevelOfConfidence(4);
}



    /**
     * This method implements the PC algorithm(Causation, Prediction and Search
     * 1993. Lectures Notes in Statistical 81 SV. Spirtes,Glymour,Sheines).
     * Only the structure of the net is discovered.
     * levelOfConfidence can be valued 0..4, indicating the level of
     * confidence for testing the conditional independences. 0 will be the
     * minor confidence.
     */

public void learning(){

 Graph dag;
 int n,i,j,pos;
 FiniteStates nodeX,nodeY;
 Hashtable sepSet;
 NodeList adyacenciesX,adyacenciesY,adyacenciesXY,
     adyacenciesYX,vars,subSet;
 LinkList linkList;
 Link link;
 Vector subSetsOfnElements,index;
 Enumeration en;
 boolean ok,encontrado=false,directed=false;


 //vars = getOutput().getNodeList();
 index = new Vector();
 for(i=0 ; i< getOutput().getNodeList().size() ;i++){
     sepSet = new Hashtable();
     index.addElement(sepSet);
 }
 //linkList = getOutput().getLinkList();
 dag = (Graph)getOutput();

 for (n=0 ; n <= dag.maxOfAdyacencies() ; n++)
     for (i=0 ; i<((dag.getNodeList()).size()-1);i++){
	 nodeX=(FiniteStates)(dag.getNodeList()).elementAt(i);
	 adyacenciesX=dag.neighbourhood(nodeX);
	 for (j=i+1 ; j< (dag.getNodeList()).size() ;j++){
	     encontrado=false;
	     nodeY=(FiniteStates)(dag.getNodeList()).elementAt(j);
	     System.out.print("\nNodes: "+nodeX.getName()+", "+nodeY.getName()+" Step: "+n);
	     link=getOutput().getLinkList().getLinks(nodeX.getName(),nodeY.getName());
	     if(link==null)
		 link=getOutput().getLinkList().getLinks(nodeY.getName(),nodeX.getName());
	     adyacenciesY=dag.neighbourhood(nodeY);
	     if(adyacenciesX.getId(nodeY) !=-1){ // they are adyacent.
		 adyacenciesXY= adyacenciesX.copy();
		 adyacenciesYX= adyacenciesY.copy();    
		 adyacenciesXY.join(adyacenciesYX);
		 adyacenciesXY.removeNode(nodeX);
		 adyacenciesXY.removeNode(nodeY);
		 if(adyacenciesXY.size() >= n){
		     subSetsOfnElements=adyacenciesXY.subSetsOfSize(n);
		     en = subSetsOfnElements.elements();
		     while((!encontrado)&&(en.hasMoreElements()||n==0)){
			 if(n==0) subSet = new NodeList();
			 else subSet = (NodeList)en.nextElement();
			 ok = input.independents(nodeX,nodeY,subSet,
						 levelOfConfidence);
			 if(ok){
			     dag.removeLink(link);
			     pos = dag.getNodeList().getId(nodeX);
			     sepSet = (Hashtable)index.elementAt(pos);
			     sepSet.put(nodeY,subSet);
			     pos = dag.getNodeList().getId(nodeY);
			     sepSet = (Hashtable)index.elementAt(pos);
			     sepSet.put(nodeX,subSet);
			     encontrado = true;
			     
			 }
			 if(n==0) encontrado=true;
		     }
		 }
	     }
	 }
     }
 
 for(i=0 ; i< index.size(); i++){
     nodeX=(FiniteStates)dag.getNodeList().elementAt(i);
     sepSet = (Hashtable) index.elementAt(i);
     en=sepSet.keys();
     while(en.hasMoreElements()){
	 nodeY = (FiniteStates)en.nextElement();
	 subSet = (NodeList)sepSet.get(nodeY);
	 System.out.println(nodeX.getName()+"  "+nodeY.getName());
	 System.out.println("----------------------------------");
	 subSet.print();
	//   try{
//  	     System.in.read();
//  	 }catch (IOException e){
//  	 }
     }
 }

 headToHeadLink((Graph)dag,index);
 remainingLink((Graph)dag,index);
 
}


    /**
     * this method is used by the learning method. This method carry out the 
     * v-structures (-->x<--).
     * @param Graph dag. An undirected Graph 
     * @param Vector index. Vector that stores the true conditional 
     * independence tests found in the learning process.
     */


private void headToHeadLink(Graph dag, Vector index){

NodeList nodes,nbX,nbY,subDsep;
Hashtable sepSet;
LinkList links;
Link delLink,newLink;
int i,j,z,pos;
Node nodeX,nodeY,nodeZ;
boolean directedXY,directedYZ;

nodes = dag.getNodeList();
links = dag.getLinkList();

 for(i=0;i< nodes.size();i++){
     nodeX = (Node) nodes.elementAt(i);
     nbX = dag.neighbourhood(nodeX);
     for(j=0 ; j< nbX.size() ; j++){
	 nodeY = (Node) nbX.elementAt(j);
	 nbY = dag.neighbourhood(nodeY);
	 nbY.removeNode(nodeX);
	 for(z=0 ; z < nbY.size() ;z++){
	     nodeZ = (Node) nbY.elementAt(z);
	     if( nbX.getId(nodeZ) == -1 ){  // No adyacentes X y Z
		 pos = nodes.getId(nodeX);
		 sepSet = (Hashtable)index.elementAt(pos);
		 subDsep = (NodeList)sepSet.get(nodeZ);
		 if((subDsep!=null)&&(subDsep.getId(nodeY) == -1)){
		     delLink = links.getLinks(nodeX.getName(),nodeY.getName());
		     if(delLink == null){
			 delLink = links.getLinks(nodeY.getName(),nodeX.getName());
		     }
		     directedXY = delLink.getDirected();
		     if(!directedXY){
			 dag.removeLink(delLink);
			 dag.createLink(nodeX,nodeY);
		     }
		     delLink = links.getLinks(nodeY.getName(),nodeZ.getName());
		     if(delLink == null){ 
			 delLink = links.getLinks(nodeZ.getName(),nodeY.getName());
		     }
		     directedYZ = delLink.getDirected();
		     if(!directedYZ){
			 dag.removeLink(delLink);
			 dag.createLink(nodeZ,nodeY);
			
		     }
		 }
		     
	     }
	 }
     }
 }
}
    

    /**
     * This method carry out the direction of the remaining link that can
     * be computed.
     * @param Graph @see above method.
     * @param Vector @see above method.
     */

private void remainingLink(Graph dag,Vector index){

boolean change;
int i,j;
Link linkAB,linkBC,linkCB;
Node nodeA,nodeB,nodeC;
NodeList nbTail,nbHead,nbC;
Vector acc;

 do{
     change = false;
     for(i=0 ;i<dag.getLinkList().size();i++){
	 linkAB = (Link) dag.getLinkList().elementAt(i);
	 nodeA = (Node) linkAB.getTail();
	 nodeB = (Node) linkAB.getHead();
	 if(linkAB.getDirected()){   // A-->B
	     nbHead = dag.neighbourhood(nodeB);
	     nbHead.removeNode(nodeA);
	     nbTail = dag.neighbourhood(nodeA);
	     nbTail.removeNode(nodeB);
	     for(j=0 ; j< nbHead.size() ; j++){
		 nodeC = (Node) nbHead.elementAt(j);
		 nbC = dag.neighbourhood(nodeC);
		 if(nbC.getId(nodeA) == -1){    
		     linkCB = dag.getLinkList().getLinks(nodeC.getName(),
							     nodeB.getName());
		     if(linkCB!= null){
			 if(!linkCB.getDirected()){
			     dag.removeLink(linkCB);
			     dag.createLink(nodeB,nodeC);
			     change = true;
			 }
		     }else{		     
			 linkBC = dag.getLinkList().getLinks(nodeB.getName(),
							    nodeC.getName());
			 if(!linkBC.getDirected()){
			     dag.removeLink(linkBC);
			     dag.createLink(nodeB,nodeC);
			     change = true;
			 }
		     }
		 }
	     }
	 }
	 else{ // A--B
	     acc = new Vector();
	     acc = dag.accessibles(nodeA);
	     if(acc.indexOf(nodeB)!= -1){
		 dag.removeLink(linkAB);
		 dag.createLink(nodeA,nodeB);
		 change = true;
	     }
	 }
     }
     
 }while (change);
 
}

    /**
     * This method construct a consistent extension of a partially oriented
     * graph (usually, the output of the PC algorithm). This extension is 
     * achieved having into account the index nodes of the param nodes.
     * @param NodeList nodes. A set of sorted Nodes.
     */


public void extendOutput(NodeList nodes){

LinkList links,linksOutput;
int i,posTail,posHead;
Link link,newLink;
Graph dag;

dag = (Graph) getOutput();
links = linkUnOriented();
//linksOutput = getOutput().getLinkList();

 for(i=0 ; i< links.size() ; i++){
     link = links.elementAt(i);
     posTail = nodes.getId(link.getTail());
     posHead = nodes.getId(link.getHead());
     if(posTail < posHead)
	 link.setDirected(true);
     else{
	 dag.removeLink(link);
	 dag.createLink(link.getHead(),link.getTail());
     }
 }

}

    /**
     * This method carry out the link unoriented of the PC output as a Link
     * List.
     * @return LinkList. A Link list of unoriented links.
     */


public LinkList linkUnOriented(){

LinkList links, linksUO;
int i;
Link link;

linksUO = new LinkList();
links = getOutput().getLinkList();

for(i=0 ; i< links.size() ; i++){
    link = links.elementAt(i);
    if(!link.getDirected())
	linksUO.insertLink(link);
}

return linksUO;

}

    /** Access methods ***/

    public int getLevelOfConfidence(){
	return levelOfConfidence;
    }

    public void setLevelOfConfidence(int level){
	levelOfConfidence = level;
    }

    public void setInput(ConditionalIndependence input){
	this.input = input;
    }

} // PCLearning

















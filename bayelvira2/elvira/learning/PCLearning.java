
/* PCLearning.java */

package elvira.learning;

import java.io.*;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Date;

import elvira.*;
import elvira.potential.*;
import elvira.database.DataBaseCases;
import elvira.parser.ParseException;

/**
 * PCLearning.java
 *
 *
 * Created: Tue May 11 11:54:08 1999
 *
 * @author Proyecto Elvira
 * @version 1.0
 */

public class PCLearning extends Learning  {

    ConditionalIndependence input; // Input of the Learning Process.
    int indmethod= 0; // 0 test 1 scores 
    int refining = 0;// 0 no 1 yes
    int triangles = 0;// 0 no 1 yes
    double delay;                    // Delay of the Learning Process.
    int numberOfTest;              // Number of test in the learning process.
    double setSizeCondMean;        // size mean of conditionating set.
    double levelOfConfidence;         // level of conf. for the C.I. tests

   public static void main(String args[]) throws ParseException, IOException { 

      BDeMetrics metric;
      Bnet net, baprend;
      FileWriter f2;
      
      net = null;
     


      FileInputStream f = new FileInputStream(args[0]);
      DataBaseCases cases = new DataBaseCases(f);
      cases.setNumberOfCases(Integer.valueOf(args[1]).intValue());
      metric = new BDeMetrics(cases);
      PCLearning outputNet2 = new PCLearning(cases,1);
      outputNet2.PCPAthremove(metric);
      
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

  //  System.out.println("Paso por Crear base de datos");
    nodes = cases.getVariables();
    //links = new LinkList();
    b = new Bnet();
    b.setKindOfGraph(2);
    dag = (Graph) b;
    for(i=0 ; i < (nodes.size()) ;i++)
       try {
           dag.addNode(nodes.elementAt(i).copy());
        }
        catch (InvalidEditException iee) {};

    nodes=dag.getNodeList();
   for(i=0 ; i < (nodes.size()-1) ;i++)
    for(j=i+1 ; j<nodes.size() ;j++){
         nodet=(Node)nodes.elementAt(i);
         nodeh=(Node)nodes.elementAt(j);
         try {
            dag.createLink(nodet, nodeh, directed);
         }
         catch (InvalidEditException iee) {};
     }
    
    this.input = cases;
    setOutput(b);
    setLevelOfConfidence(0.99);
    
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
    b.setKindOfGraph(2);
    dag = (Graph) b;
    for(i=0 ; i < (nodes.size()) ;i++)
       try {
	   dag.addNode(nodes.elementAt(i).copy());
       }
       catch (InvalidEditException iee) {};
    
    nodes=dag.getNodeList();
    for(i=0 ; i < (nodes.size()-1) ;i++)
    for(j=i+1 ; j<nodes.size() ;j++){
        nodet=(Node)nodes.elementAt(i);
        nodeh=(Node)nodes.elementAt(j);
        try {
           dag.createLink(nodet, nodeh, directed);
        }
        catch (InvalidEditException iee) {};
    }
    
    this.input = cases;
    setOutput(b);
    setLevelOfConfidence(0.99);
    
}



    /** 
     * This method allows the initialization of several differents
     * PC learning algorithms.
     * The default is method 0, which simply calls to the PC with classical
     * statistical tests procedures.
     * With method 1, it will consider PC algorithm, but with tests done
     * by comparing Bayesian scores.
     * @param DataBaseCases cases. The data bases of discrete cases.
     * @param NodeList nodes. The nodes on which the learning will be carried out.
     * @param int method The method for the conditional independence tests.
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
    
    nodes = input.getNodeList().duplicate();
    b = new Bnet();
    b.setKindOfGraph(2);
    dag = (Graph) b;
    for(i=0 ; i < (nodes.size()) ;i++)
       try {
           dag.addNode(nodes.elementAt(i).copy());
        }
        catch (InvalidEditException iee) {};
    //links = new LinkList();
    
    nodes=dag.getNodeList();
    for(i=0 ; i < (nodes.size()-1) ;i++)
    for(j=i+1 ; j<nodes.size() ;j++){
        nodet=(Node)nodes.elementAt(i);
        nodeh=(Node)nodes.elementAt(j);
        try {
           dag.createLink(nodet, nodeh, directed);
        }
        catch (InvalidEditException iee) {};
        
    }
   
    this.input=input;
    setOutput(b);
    setLevelOfConfidence(0.99);
    
}

    /**
     * see  PCLearning(Graph input) and 
     * also PCLearning(DataBaseCases cases, NodeList nodes)
     * @param BDeMetrics input Mtrics used for the conditional independence tests
     * @param NodeList nodes.
     */

public PCLearning(BDeMetrics input, NodeList nodes) {

    Bnet b;
    Graph dag;
    LinkList links;
    Link link;
    Node nodet,nodeh;
    boolean directed = false;
    int i,j;
    NodeList nodesInput = input.getNodeList().duplicate();
    
    //links = new LinkList();
 
    nodesInput = nodesInput.intersectionNames(nodes);   
    b = new Bnet();
    b.setKindOfGraph(2);
    dag = (Graph) b;
    
    for(i=0 ; i < (nodesInput.size()) ;i++)
      try {
          dag.addNode(nodesInput.elementAt(i).copy());
       }
       catch (InvalidEditException iee) {};

    nodes=dag.getNodeList();
    for(i=0 ; i < (nodesInput.size()-1) ;i++)
    for(j=i+1 ; j<nodesInput.size() ;j++){
        nodet=(Node)nodesInput.elementAt(i);
        nodeh=(Node)nodesInput.elementAt(j);
        try {
           dag.createLink(nodet, nodeh, directed);
        }
        catch (InvalidEditException iee) {};
        
    }
    
    this.input = input;
    setOutput(b);
    setLevelOfConfidence(0.99);
}



    /**
     * see  PCLearning(Graph input) and 
     * also PCLearning(DataBaseCases cases, NodeList nodes)
     * @param Graph input
     * @param NodeList nodes.
     */

public PCLearning(ConditionalIndependence input, NodeList nodes) {

    Bnet b;
    Graph dag;
    LinkList links;
    Link link;
    Node nodet,nodeh;
    boolean directed = false;
    int i,j;
    NodeList nodesInput = input.getNodeList().duplicate();
    
    //links = new LinkList();
 
    nodesInput = nodesInput.intersectionNames(nodes);   
    b = new Bnet();
    b.setKindOfGraph(2);
    dag = (Graph) b;
    
    for(i=0 ; i < (nodesInput.size()) ;i++)
      try {
          dag.addNode(nodesInput.elementAt(i).copy());
       }
       catch (InvalidEditException iee) {};

    nodes=dag.getNodeList();
    for(i=0 ; i < (nodesInput.size()-1) ;i++)
    for(j=i+1 ; j<nodesInput.size() ;j++){
        nodet=(Node)nodesInput.elementAt(i);
        nodeh=(Node)nodesInput.elementAt(j);
        try {
           dag.createLink(nodet, nodeh, directed);
        }
        catch (InvalidEditException iee) {};
        
    }
    
    this.input = input;
    setOutput(b);
    setLevelOfConfidence(0.99);
}



public PCLearning(DataBaseCases cases, NodeList nodes, int method) {

     this(cases,nodes);
     
     indmethod = method;
     
     ConditionalIndependence input;
    
    switch (method){
        case 0: {}
        case 1: {this.input = new BDeMetrics(cases);}
    }

    
    
}


public PCLearning(DataBaseCases cases, int method) {
    this(cases);
     
     ConditionalIndependence input;
   
     indmethod = method;
     
    switch (method){
        case 0: {System.out.println("M�todo tradicional");break;}
      case 1: {this.input = new BDeMetrics(cases);}
    }

    
    
}


public void setRefining(int i){
    refining = i;
}



public void setTriangles(int i){
    triangles = i;
}



public void order0indep(Vector index) {
  Graph dag;
  int i,j,pos;
   Hashtable sepSet;
   FiniteStates nodeX,nodeY;
  NodeList    subSet;
  boolean ok;
   Link link;
   
 
  dag = (Graph)getOutput();
  
    
 for (i=0 ; i<(dag.getNodeList()).size();i++){
      nodeX=(FiniteStates)(dag.getNodeList()).elementAt(i);
  for (j=i+1 ; j< (dag.getNodeList()).size() ;j++){
      nodeY=(FiniteStates)(dag.getNodeList()).elementAt(j);
   //   System.out.println("Nodes: "+nodeX.getName()+", "
//                +nodeY.getName()+" Step: 0");
       subSet = new NodeList();
        ok=input.independents(nodeX,nodeY,subSet,levelOfConfidence);
        System.out.print("\n I( "+nodeX.getName()+" , "+nodeY.getName()+" | "+subSet.toString2()+") : "+ok+"\n");
        if (ok) {
             link=getOutput().getLinkList().getLinks(nodeX.getName(),
                             nodeY.getName());
         if(link==null)
             link=getOutput().getLinkList().getLinks(nodeY.getName()
                                 ,nodeX.getName());
                   try {
           dag.removeLink(link);
        }
        catch (InvalidEditException iee) {};
                     
                 pos = dag.getNodeList().getId(nodeX);
                 sepSet = (Hashtable)index.elementAt(pos);
                 sepSet.put(nodeY,subSet);
                 pos = dag.getNodeList().getId(nodeY);
                 sepSet = (Hashtable)index.elementAt(pos);
                 sepSet.put(nodeX,subSet);  
        }
      
  }
 }
    
    
    
}



public void order0indep() {
  Graph dag;
  int i,j,pos;
   Hashtable sepSet;
   FiniteStates nodeX,nodeY;
  NodeList    subSet;
  boolean ok;
   Link link;
   
 
  dag = (Graph)getOutput();
  
    
 for (i=0 ; i<(dag.getNodeList()).size();i++){
      nodeX=(FiniteStates)(dag.getNodeList()).elementAt(i);
  for (j=i+1 ; j< (dag.getNodeList()).size() ;j++){
      nodeY=(FiniteStates)(dag.getNodeList()).elementAt(j);
   //   System.out.println("Nodes: "+nodeX.getName()+", "
//                +nodeY.getName()+" Step: 0");
       subSet = new NodeList();
        ok=input.independents(nodeX,nodeY,subSet,levelOfConfidence);
        System.out.print("\n I( "+nodeX.getName()+" , "+nodeY.getName()+" | "+subSet.toString2()+") : "+ok+"\n");
        if (ok) {
             link=getOutput().getLinkList().getLinks(nodeX.getName(),
                             nodeY.getName());
         if(link==null)
             link=getOutput().getLinkList().getLinks(nodeY.getName(),
                                 nodeX.getName());
                   try {
           dag.removeLink(link);
        }
        catch (InvalidEditException iee) {};
                     
               
        }
      
  }
 }
    
    
    
}

public void solvetrian(Vector index) {
  Graph dag;
  int i,j,k,pos;
   Hashtable sepSet;
   FiniteStates nodeX,nodeY,nodeZ,nodeA,nodeB;
  NodeList    subSet,adyacenciesX,adyacenciesY;
  double max= -1;
  int best = -1;
  double sxy, syz, sxz;
  
  boolean ok;
   Link link;
   BDeMetrics metric;
   
   
   
 
   if (indmethod == 0) {return;}
   
  metric = (BDeMetrics) input;
  dag = (Graph)getOutput();

  
  
  System.out.println("Start solving triangles"); 
  
 for (i=0 ; i<(dag.getNodeList()).size();i++){
       nodeX=(FiniteStates)(dag.getNodeList()).elementAt(i);
        
       
  for (j=i+1 ; j< (dag.getNodeList()).size() ;j++){
      nodeY=(FiniteStates)(dag.getNodeList()).elementAt(j);
         adyacenciesX=dag.neighbours(nodeX);
      if (adyacenciesX.getId(nodeY) !=-1) {
           adyacenciesY=dag.neighbours(nodeY);
  
            for (k=j+1 ; k< (dag.getNodeList()).size() ;k++){
                 nodeZ=(FiniteStates)(dag.getNodeList()).elementAt(k);
             if ((adyacenciesX.getId(nodeZ) !=-1)&&(adyacenciesY.getId(nodeZ) !=-1)) {
                  System.out.println("Solving a triangle");
                     subSet = new NodeList();
                      subSet.insertNode(nodeZ);
                     max = -1.0;
                     best = -1;
                      sxy = metric.scoreDep (nodeX, nodeY, subSet );    
           //              System.out.println("Nodes: "+nodeX.getName()+", "
           //       +nodeY.getName()+" Score : " + sxy);
                      subSet = new NodeList();
                      subSet.insertNode(nodeX);
                      syz = metric.scoreDep (nodeY, nodeZ, subSet );   
                                          subSet = new NodeList();
          //                                 System.out.println("Nodes: "+nodeY.getName()+", "
         //         +nodeZ.getName()+" Score : " + syz);
                      subSet.insertNode(nodeY);
                      sxz = metric.scoreDep (nodeX, nodeZ, subSet );  
                  //     System.out.println("Nodes: "+nodeX.getName()+", "
                 // +nodeZ.getName()+" Score : " + sxz); 
                     if (sxy > max) {max = sxy; best = 1;}
                      if (syz > max) {max = syz;best = 2;}
                       if (sxz > max) {max = sxz;best = 3;}
                     if (max > 0) {
             //             System.out.println("removing link " + best);
                              switch (best){  
                         case 1  :  nodeA = nodeX;
                                    nodeB = nodeY;
                                     subSet = new NodeList();
                                     subSet.insertNode(nodeZ);
                                     k= dag.getNodeList().size();
                                    break;
                         case 2 :  nodeA = nodeZ;
                                   nodeB = nodeY;
                                       subSet = new NodeList();
                                     subSet.insertNode(nodeX);
                                    break;
                            case 3 :nodeA = nodeX;
                                    nodeB = nodeZ;          
                                      subSet = new NodeList();
                                     subSet.insertNode(nodeY);
                                     break;
                                    
                                  default : nodeA = null;
                                            nodeB = null;
                              }
                              
               //               System.out.println("Eliminando enlace entre " +  nodeA.getName() + " y nodo " +  nodeB.getName());
                   link=getOutput().getLinkList().getLinks(nodeA.getName(),
                             nodeB.getName());
         if(link==null)
             link=getOutput().getLinkList().getLinks(nodeB.getName()
                                 ,nodeA.getName());
                   try {
           dag.removeLink(link);
        }
        catch (InvalidEditException iee) {};
                     
                
                 pos = dag.getNodeList().getId(nodeA);
              //   System.out.println("posicion " + pos);
                 sepSet = (Hashtable)index.elementAt(pos);
                 sepSet.put(nodeB,subSet);
                 pos = dag.getNodeList().getId(nodeB);
                    
                 sepSet = (Hashtable)index.elementAt(pos);
                 sepSet.put(nodeA,subSet);               
                              
                              
                     }
                      
                      
             
             }      
                 
                 
          
            }
          
      }
      
      
   
      
  }
 }
    
    
    
}

    /** 
     * Initializes a full unidirected graph with the variables contained into
     * the List of Nodes of the parameter input. Also initializes the input of
     * algorithm PC as a Graph and the PC output as a Bnet with the above
     * graph. 
     * @param Graph input. The input graph. (d-separation criterion).
     */    

    /**
     * This method implements the PC algorithm(Causation, Prediction and Search
     * 1993. Lectures Notes in Statistical 81 SV. Spirtes,Glymour,Sheines).
     * Only the structure of the net is discovered.
     * levelOfConfidence indicates the level of
     * confidence for testing the conditional independences. 0.0 will be the
     * minor confidence.
     */

public void learning(){

     Metrics metric; 
    
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
 Date D = new Date();
 delay = (double) D.getTime();

 //vars = getOutput().getNodeList();
 index = new Vector();
 for(i=0 ; i< getOutput().getNodeList().size() ;i++){
     sepSet = new Hashtable();
     index.addElement(sepSet);
 }
 //linkList = getOutput().getLinkList();
 dag = (Graph)getOutput();

 
 order0indep(index); 
 
if (triangles ==1) { solvetrian(index);}
 
 
 for (n=1 ; n <= dag.maxOfAdyacencies() ; n++)
     for (i=0 ; i<(dag.getNodeList()).size();i++){
     for (j=0 ; j< (dag.getNodeList()).size() ;j++){
         if(i!=j){
         encontrado=false;
         nodeX=(FiniteStates)(dag.getNodeList()).elementAt(i);
         adyacenciesX=dag.neighbours(nodeX);
         nodeY=(FiniteStates)(dag.getNodeList()).elementAt(j);
         System.out.println("Nodes: "+nodeX.getName()+", "
                  +nodeY.getName()+" Step: "+n);
         link=getOutput().getLinkList().getLinks(nodeX.getName(),
                             nodeY.getName());
         if(link==null)
             link=getOutput().getLinkList().getLinks(nodeY.getName()
                                 ,nodeX.getName());
         
         if(adyacenciesX.getId(nodeY) !=-1){ // they are adyacent.
             adyacenciesX.removeNode(nodeY);
             if(adyacenciesX.size() >= n){
             subSetsOfnElements=adyacenciesX.subSetsOfSize(n);
             en = subSetsOfnElements.elements();
             while((!encontrado)&&(en.hasMoreElements()||n==0)){
                 if(n==0) subSet = new NodeList();
                 else subSet = (NodeList)en.nextElement();
                 ok=input.independents(nodeX,nodeY,subSet,levelOfConfidence);
           //      System.out.print("\n I( "+nodeX.getName()+" , "+nodeY.getName()+" | "+subSet.toString2()+") : "+ok+"\n");
                 if(ok){
                 
                 try {
                     dag.removeLink(link);
                 } catch (InvalidEditException iee) { };                 
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
     }

 
// for(i=0 ; i< index.size(); i++){
//     nodeX=(FiniteStates)dag.getNodeList().elementAt(i);
//     sepSet = (Hashtable) index.elementAt(i);
//     en=sepSet.keys();
//     while(en.hasMoreElements()){
//   nodeY = (FiniteStates)en.nextElement();
//   subSet = (NodeList)sepSet.get(nodeY);
//   System.out.println(nodeX.getName()+"  "+nodeY.getName());
//   System.out.println("----------------------------------");
//   subSet.print();
    //   try{
//           System.in.read();
//       }catch (IOException e){
//       }
//     }
// }

 headToHeadLink((Graph)dag,index);
 remainingLink((Graph)dag,index);
 extendOutput();
 if (refining==1) {
     if (indmethod == 0) {
         metric = new BDeMetrics((DataBaseCases) input);
     }
     else { metric = (BDeMetrics) input;                        }
     refinesearch(metric); 
 }
 D = new Date();
 delay = (((double)D.getTime()) - delay) / 1000;
 
}


public void learningo(){

    
    
 Graph dag;
 int n,i,j;

 
 NodeList subSet,nodes;
 LinkList links;
 
 Link     delLink;
 
 Enumeration en;
 
 boolean ok,encontrado=false;
 Node NodeX, NodeY;
 NodeList neigh,previous;
 Date D = new Date();
 delay = (double) D.getTime();

 //vars = getOutput().getNodeList();
 
 
 //linkList = getOutput().getLinkList();
 dag = (Graph)getOutput();
 links = dag.getLinkList();
 
 order0indep(); 
 
  nodes = input.getNodeList();
  
  for(i=0; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     
     neigh = dag.neighbours(NodeX);
   
   

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          if (neigh.getId(NodeY)>-1) {
          previous.insertNode(NodeY);
           delLink = links.getLinks(NodeX.getName(),NodeY.getName());
             if(delLink == null){
             delLink = links.getLinks(NodeY.getName(),NodeX.getName());
             }
            try {
               
                dag.removeLink(delLink);
               }
                 catch (InvalidEditException iee) {};
          }

        }
     
  n=1;
  while (n<previous.size()) {
     j=0;
     while ( j<previous.size()){
           NodeY  = (FiniteStates) previous.elementAt(j);
           encontrado = false;
           
           en = previous.subSetsOfSize(n).elements();
           while( (en.hasMoreElements())&& !encontrado){
               subSet = (NodeList)en.nextElement();
               if(subSet.getId(NodeY)<0){
                    ok=input.independents(NodeX,NodeY,subSet,levelOfConfidence);
           //      System.out.print("\n I( "+nodeX.getName()+" , "+nodeY.getName()+" | "+subSet.toString2()+") : "+ok+"\n");
                 if(ok){
                    encontrado = true;
                    previous.removeNode(j);
                   
                   
               }
           }
           
     }
           
     
    if (!encontrado) {j++;}
    
  }
     
   n++;  
 
  } 
  
  for (j=0;j<previous.size();j++){
      
        NodeY  = (FiniteStates) previous.elementAt(j);
        
         try {
         dag.createLink(NodeY,NodeX,true);
        }
        catch (InvalidEditException iee) {};
      
  }
  
  
  }
 
 D = new Date();
 delay = (((double)D.getTime()) - delay) / 1000;
 
}



  /**
     * this method is used by the learning method. This is a global
     * recursive orientation procedure 
     * @param Graph dag. An undirected Graph 
     * @param Vector index. Vector that stores the true conditional 
     * independence tests found in the learning process.
     */

protected void orientation(Graph dag, Vector index){


NodeList totalnodes,candidates,neigh,remaining,nbY,Separator;
Node X,Y,Z,Bestnode;
int i,j,k, nnodes,pos,ofenses,ntotal;
double minofenses,rate;
boolean condition,selected;
Hashtable sepSet;


totalnodes = dag.getNodeList();

nnodes = totalnodes.size(); 
remaining = totalnodes.copy();


candidates = new NodeList();

while (nnodes > 0) {
    
 

for(i=0;i<remaining.size();i++){
    X = remaining.elementAt(i);
    System.out.println("Start testing " + X.getName());
    

    neigh = dag.nonorientedneighbours(X);
    System.out.println("Number neigb " + neigh.size());
    
    condition = true;
    if (candidates.getId(X)==-1){
    for(j=0;j<neigh.size();j++){
                Y = neigh.elementAt(j);
                  System.out.println("Connected with " + Y.getName());
                nbY = dag.nonorientedneighbours(Y);
                pos = dag.getNodeList().getId(Y);
                sepSet = (Hashtable)index.elementAt(pos);
        for (k=j+1;k<neigh.size();k++){
          Z = neigh.elementAt(k);
          System.out.println("Selecting " + Z.getName());
           if( nbY.getId(Z) == -1 )  {
                  Separator = (NodeList)sepSet.get(Z);
                  if (Separator.getId(X) > -1){
                      condition = false;
                      System.out.println("Condition failed with " + Z.getName());
                      break;
                  }
             
                  
           }
          
        }
                 if(!condition) {break;}
    }
    if (condition) {candidates.insertNode(X);
 //                    System.out.println("Adding " + X.getName() + " to candidates ");
    }
    
    }
}
    
    Bestnode = totalnodes.elementAt(0);
    selected = false; minofenses = 2.0;
  for(i=0;i<candidates.size(); i++)  {
      X = candidates.elementAt(i);
      ofenses = 0;ntotal=0;
      neigh = dag.nonorientedneighbours(X);
        pos = dag.getNodeList().getId(X);
        sepSet = (Hashtable)index.elementAt(pos);
      for (j=0;j<neigh.size();j++){
          Y = neigh.elementAt(j);
          nbY = dag.nonorientedneighbours(Y);
          for (k=0;k<nbY.size(); k++){
              Z =  nbY.elementAt(k);
              if ((neigh.getId(Z) ==-1)&& (!Z.equals(X))){
                 ntotal++;
                   Separator = (NodeList) sepSet.get(Z);
                   if(Separator.getId(Y) == -1){
                      ofenses++;
                   }
              }
          }
          
      }
      if (ofenses==0){
           System.out.println("Selected a leaf (1) " + X.getName());
           
               try {
                dag.removeNOrientedLinksNode(X);
               } catch (InvalidEditException iee) { };     
             for (j=0;j<neigh.size();j++){
                 Y = neigh.elementAt(j);
                    try {
                //        System.out.println("Creating oriented Link  " + Y.getName() + " " +  X.getName() );
                      dag.createLink(Y, X,true);
                    
                 } catch (InvalidEditException iee) { };     
             }
               nnodes--;
               remaining.removeNode(X);
                candidates.removeNode(X);
          selected=true;     
         break;
      }
      else { rate = ((double) ofenses)/((double) ntotal);
            if (rate < minofenses) {
                minofenses = rate;
                Bestnode = X;
            }
      }
  }
    

    


  if((!selected) && candidates.size()>0){
      X = Bestnode;
       neigh = dag.nonorientedneighbours(X);
      try {
                dag.removeNOrientedLinksNode(X);
               } catch (InvalidEditException iee) { };
                
             for (j=0;j<neigh.size();j++){
                 Y = neigh.elementAt(j);
                    try {
                      dag.createLink(Y, X,true);
   //                   System.out.println("Creating oriented Link  " + Y.getName() + " " +  X.getName() );
                 } catch (InvalidEditException iee) { };     
             }
               nnodes--;
               remaining.removeNode(X);
               candidates.removeNode(X);
          selected=true;    
      
  }  
    
if(!selected) {
    minofenses = 2.0; ntotal = 0; ofenses = 0; 
    for(i=0;i<remaining.size();i++){
    X = remaining.elementAt(i);
    neigh = dag.nonorientedneighbours(X);
    for(j=0;j<neigh.size();j++){
                Y = neigh.elementAt(j);
                nbY = dag.nonorientedneighbours(Y);
                pos = dag.getNodeList().getId(Y);
                sepSet = (Hashtable)index.elementAt(pos);
        for (k=j+1;k<neigh.size();k++){
          Z = neigh.elementAt(k);
           if( nbY.getId(Z) == -1 )  {
                  ntotal++;
                  Separator = (NodeList)sepSet.get(Z);
                  if (Separator.getId(X) > -1){
                      ofenses++;
                  }
             
                  
           }
          
        }
    }
    
    
 rate =   ((double) ofenses)/((double) ntotal);
            if (rate < minofenses) {
                minofenses = rate;
                Bestnode = X;
            }
    
    
}

 
 X = Bestnode;
 neigh = dag.nonorientedneighbours(X);
      try {
                dag.removeNOrientedLinksNode(X);
               } catch (InvalidEditException iee) { };
                 
             for (j=0;j<neigh.size();j++){
                 Y = neigh.elementAt(j);
                    try {
                      dag.createLink(Y, X,true);
    //                  System.out.println("Creating oriented Link  " + Y.getName() + " " +  X.getName() );
                 } catch (InvalidEditException iee) { };     
             }
               nnodes--;
               remaining.removeNode(X);
   
       
    
    
}


}



}
    
    
    
    /**
     * this method is used by the learning method. This method carry out the 
     * v-structures (-->x<--).
     * @param Graph dag. An undirected Graph 
     * @param Vector index. Vector that stores the true conditional 
     * independence tests found in the learning process.
     */

    
    
    

protected void headToHeadLink(Graph dag, Vector index){

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
     nbX = dag.neighbours(nodeX);
     for(j=0 ; j< nbX.size() ; j++){
     nodeY = (Node) nbX.elementAt(j);
     nbY = dag.neighbours(nodeY);
     nbY.removeNode(nodeX);
     for(z=0 ; z < nbY.size() ;z++){
         nodeZ = (Node) nbY.elementAt(z);
         if( nbX.getId(nodeZ) == -1 ){  // No adyacentes X y Z
         pos = nodes.getId(nodeX);
         sepSet = (Hashtable)index.elementAt(pos);
         subDsep = (NodeList)sepSet.get(nodeZ);
                 { System.out.println("Testing 3 nodes " +subDsep.size() );}
         if((subDsep!=null)&&(subDsep.getId(nodeY) == -1)){
                    System.out.println("Directing ");
             delLink = links.getLinks(nodeX.getName(),nodeY.getName());
             if(delLink == null){
             delLink = links.getLinks(nodeY.getName(),nodeX.getName());
             }
             directedXY = delLink.getDirected();
             if(!directedXY){ 
                         dag.orientLinkDag(delLink,nodeX,nodeY);
                        }
                     
                     
             delLink = links.getLinks(nodeY.getName(),nodeZ.getName());
             if(delLink == null){ 
             delLink = links.getLinks(nodeZ.getName(),nodeY.getName());
             }
             directedYZ = delLink.getDirected();
             if(!directedYZ){
              dag.orientLinkDag(delLink,nodeZ,nodeY);
                     
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

protected void remainingLink(Graph dag,Vector index){

boolean change,change2,oriented,skip;
int i,j,k;
Link linkAB,linkBC,linkCB,linkBW;
Node nodeA,nodeB,nodeC,nodeW;
NodeList nbTail,nbHead,nbC,children;
Vector acc;
LinkList links;

do{ 
    change2=false;
 do{
     change = false;
     for(i=0 ;i<dag.getLinkList().size();i++){
	//dag.setVisitedAll(false);
     linkAB = (Link) dag.getLinkList().elementAt(i);
     nodeA = (Node) linkAB.getTail();
     nodeB = (Node) linkAB.getHead();
        System.out.println("Testing nodes " + nodeA.getName() + " " + nodeB.getName());
     if(linkAB.getDirected()){   // A-->B
       //      System.out.println("Direced arc ");
         nbHead = dag.siblings(nodeB);
      //   nbHead.removeNode(nodeA);
      //   nbTail = dag.neighbours(nodeA);
      //   nbTail.removeNode(nodeB);
         for(j=0 ; j< nbHead.size() ; j++){
         nodeC = (Node) nbHead.elementAt(j);
         //        System.out.println("Vecino de cabeza "+ nodeC.getName());
         nbC = dag.neighbours(nodeC);
         if(nbC.getId(nodeA) == -1){    
             linkCB = dag.getLinkList().getLinks(nodeC.getName(),
                                 nodeB.getName());
                       if(linkCB== null)  
                       { linkCB =dag.getLinkList().getLinks(nodeB.getName(),nodeC.getName());}
                     
             
             if(!linkCB.getDirected()){
                             dag.orientLinkDag(linkCB,nodeB,nodeC);
                              change = true;
             }
             
         }
         }
     }
     else{ // A -- B Non-oriented link
          oriented = false;
          if(dag.isThereDirectedPath(nodeA,nodeB))
          {  System.out.println("Orienting from "+ nodeA.getName() + " to "+ nodeB.getName() + "directed path");
              dag.orientLinkDag(linkAB,nodeA,nodeB);
              change = true;
              oriented = true;
          }
           if((dag.isThereDirectedPath(nodeB,nodeA)) && (!oriented))
          {   System.out.println("Orienting from "+ nodeB.getName() + " to "+ nodeA.getName() + "directed path");
              dag.orientLinkDag(linkAB,nodeB,nodeA);
              change = true;
              oriented = true;
              
          }
          if (!oriented){
          nbHead =  dag.siblings(nodeB);
          nbHead.removeNode(nodeA);
            for(j=0 ; j< nbHead.size() ; j++){
         nodeC = (Node) nbHead.elementAt(j);
                // System.out.println("Vecino de B "+ nodeC.getName());
         nbC = dag.neighbours(nodeC);
         if(nbC.getId(nodeA) == -1){   
                       for(k=0 ; k< nbHead.size() ; k++){
                           if ((k!=j)){
                               skip = false;
                               children = dag.children(nodeA);
                               nodeW = (Node) nbHead.elementAt(k);
                            
                               if(children.getId(nodeW) == -1){skip=true;}
                               
                                 linkBC = dag.getLinkList().getLinks(nodeB.getName(),nodeC.getName());
                                 if(linkBC == null) {linkBC = dag.getLinkList().getLinks(nodeC.getName(),nodeB.getName());}
                                 if (linkBC.getDirected()) {skip=true;}
                                 
                               if (!skip){
                                    children = dag.children(nodeC);
                                    if(children.getId(nodeW) == -1){
                                        linkBW = dag.getLinkList().getLinks(nodeB.getName(),nodeW.getName());
                 
                                if(linkBW == null) {linkBW = dag.getLinkList().getLinks(nodeW.getName(),nodeB.getName());}
                                          if (!linkBW.getDirected()) {dag.orientLinkDag(linkBW,nodeB,nodeW);
                                                                     change = true;
                                                                     System.out.println("Orienting from "+ nodeB.getName() + " to "+ nodeW.getName() + " rule 3");
                                                                       }
                                   
                                 skip=true;  
                               }}
                               if (!skip){
                                    children = dag.children(nodeW);
                                    if(children.getId(nodeC) == -1){
                                          dag.orientLinkDag(linkBC,nodeB,nodeC);
                                          System.out.println("Orienting from "+ nodeB.getName() + " to "+ nodeC.getName() + " rule 4");
                                                                      
                                          change = true;
                                    
                                    }
                                   
                                   
                               }  
                               
                           }
                           
                           
                           
                       }
                     
                   
                     
                 }
            }
          }
         
     }
         
     }
     
     
     
     
     }while (change);
    

 for(i=0 ;i<dag.getLinkList().size();i++){
     linkAB = (Link) dag.getLinkList().elementAt(i);
     nodeA = (Node) linkAB.getTail();
     nodeB = (Node) linkAB.getHead();
     //    System.out.println("Testing nodes " + nodeA.getName() + nodeB.getName());
     if(!linkAB.getDirected()){
              try {
                   dag.removeLink(linkAB);
                     if (!dag.isThereDirectedPath(nodeB,nodeA)){
                        System.out.println("Orienting from "+ nodeA.getName() + " to "+ nodeB.getName() + " arbitrary");
                        dag.createLink(nodeA,nodeB); 
                     }
                     else 
                     { System.out.println("Orienting from "+ nodeB.getName() + " to "+ nodeA.getName() + " arbitrary");
                                           
                         dag.createLink(nodeB,nodeA);} 
              }
                  catch (InvalidEditException iee) {};
                          
         change2=true;
         break;
         }
              
             
         }
 


 }while (change2);     
 
 
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
     else 
       try {
           dag.removeLink(link);
           dag.createLink(link.getHead(),link.getTail());
       }
        catch (InvalidEditException iee) {};
         
 }

}


    /**
     * This method construct a consistent extension of a partially oriented
     * graph (usually, the output of the PC algorithm).
     */


public void extendOutput(){

LinkList links;
Link link;
Graph dag;
NodeList cola;
Node node,hermano;

cola = new NodeList();
dag = (Graph) getOutput();
links = linkUnOriented();

 System.out.println("Second extension");
  while(links.size()>0){
   link = links.elementAt(0);
   node = link.getTail();
   for( ; dag.siblings(node).size()>0 ;){
      hermano = dag.siblings(node).elementAt(0);
      cola.insertNode(hermano);
      link = dag.getLink(node,hermano);
      if(link==null) link=dag.getLink(hermano,node);
      try{
        dag.removeLink(link);
        if (!dag.isThereDirectedPath(hermano,node))
           {dag.createLink(node,hermano);}
        else
           {dag.createLink(hermano,node);}
      }catch(InvalidEditException iee){};   
   }
   while(cola.size()>0){
      node = cola.elementAt(0);
      for(; dag.siblings(node).size()>0;){
         hermano = dag.siblings(node).elementAt(0);
         cola.insertNode(hermano);
         link = dag.getLink(node,hermano);
         if(link == null) link=dag.getLink(hermano,node);
         try{
           dag.removeLink(link);
            if (!dag.isThereDirectedPath(hermano,node))
           {dag.createLink(node,hermano);}
        else
           {dag.createLink(hermano,node);}
         }catch(InvalidEditException iee){};
      } 
      cola.removeNode(node);
   }
   links = linkUnOriented();
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

    public double getLevelOfConfidence(){
    return levelOfConfidence;
    }

    public void setLevelOfConfidence(double  level){
    levelOfConfidence = level;
    }
    
    public void setInput(ConditionalIndependence input){
    this.input = input;
    }
    public ConditionalIndependence getInput(){
    return input;
    }

    
    /*******************************************+
     * This is equivalent to the K2 algorithm with a known order + a search algorithm
     * @param metric
     */
 
public void refinesearch(Metrics metric){
    
    Graph dag;
    boolean add;
    NodeList nodes,parents,vars;
    double fitness,fitnessNew;
    Node NodeX,NodeY;
    int i,j,pos,best;
    boolean improv;
    
    add=true;
    dag= (Graph) getOutput();
     if (!dag.isADag()){ System.out.println("no es dag");  }
    System.out.println(" Graph " + dag.toString());
     System.out.println("antes de topological order ");
    nodes = dag.topologicalOrder(); 
     System.out.println("despues de topological order ");
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
      parents = dag.parents(NodeX); 
      vars = new NodeList();
      vars.insertNode(NodeX);
      vars.join(parents);
      fitness = metric.scoret(vars);
      improv=true;
      best=0;
      while(improv){
       improv=false;
       for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) { 
                //   System.out.println("Nodo no padre");
              vars.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew = metric.scoret(vars);
               //    System.out.println("Score finished");
              if (fitnessNew > fitness){
                //   System.out.println("Score mejorado");
                  fitness = fitnessNew;
                  improv=true;
                  
                   add=true;
                  best=j;
              }
              
                vars.removeNode(NodeY); 
           
          }
          else {
             //    System.out.println("Nodo padre");
            vars.removeNode(NodeY);
             //      System.out.println("Computing score " + vars.size());
             fitnessNew = metric.scoret(vars);
              //     System.out.println("Score finished");
              if (fitnessNew > fitness){
              //         System.out.println("Score mejorado" + NodeY.getName());
                  fitness = fitnessNew;
                  improv=true;
                  add=false;
                   best = j;
              }
              vars.insertNode(NodeY);
              
          }
       }
       if (improv){
          // System.out.println("BEst thing " + best);
           if (add){
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
             //   System.out.println(NodeY.getName());
                try {
         dag.createLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           }
           else {
              NodeY = (FiniteStates) nodes.elementAt(best);
              vars.removeNode(NodeY); 
             // System.out.println(NodeY.getName()+NodeX.getName());
              try {
               //   System.out.println(dag.getLinkList().toString());
         dag.removeLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           }
           
       }
       
      }
      }
      
}
  public void refinesearchprior(Metrics metric){
    
    Graph dag;
    boolean add;
    NodeList nodes,parents,vars;
    double fitness,fitnessNew;
    Node NodeX,NodeY;
    int i,j,pos,best,nparents;
    boolean improv;
    
    add=true;
    dag= (Graph) getOutput();
     if (!dag.isADag()){ System.out.println("no es dag");  }
    System.out.println(" Graph " + dag.toString());
     System.out.println("antes de topological order ");
    nodes = dag.topologicalOrder(); 
     System.out.println("despues de topological order ");
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
      parents = dag.parents(NodeX); 
      vars = new NodeList();
      vars.insertNode(NodeX);
      vars.join(parents);
      fitness = metric.scoret(vars);
      improv=true;
      best=0;
      nparents = parents.size();
      while(improv){
       improv=false;
       for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) { 
              
                //   System.out.println("Nodo no padre");
              vars.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew = metric.scoret(vars)  - Math.log(((double) (i-nparents))/(nparents+1.0));
               //    System.out.println("Score finished");
              if (fitnessNew > fitness){
                //   System.out.println("Score mejorado");
                  fitness = fitnessNew;
                  improv=true;
                  
                   add=true;
                  best=j;
              }
              
                vars.removeNode(NodeY); 
           
          }
          else {
             //    System.out.println("Nodo padre");
            vars.removeNode(NodeY);
             //      System.out.println("Computing score " + vars.size());
             fitnessNew = metric.scoret(vars) + Math.log(((double) (i-nparents+1))/(nparents));
              //     System.out.println("Score finished");
              if (fitnessNew > fitness){
              //         System.out.println("Score mejorado" + NodeY.getName());
                  fitness = fitnessNew;
                  improv=true;
                  add=false;
                   best = j;
              }
              vars.insertNode(NodeY);
              
          }
       }
       if (improv){
          // System.out.println("BEst thing " + best);
           if (add){
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
               nparents++;
             //   System.out.println(NodeY.getName());
                try {
         dag.createLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           }
           else {
              NodeY = (FiniteStates) nodes.elementAt(best);
              vars.removeNode(NodeY);
              nparents--;
             // System.out.println(NodeY.getName()+NodeX.getName());
              try {
               //   System.out.println(dag.getLinkList().toString());
         dag.removeLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           }
           
       }
       
      }
      }
      
}
  
    /*******************************************+
     * This is equivalent to the K2 algorithm with a known order + a search algorithm
     * @param metric
     */
 
public void refinePath(Metrics metric){
    
    Graph dag;
    boolean add;
    NodeList nodes,parents,vars,connect;
    double fitness,fitnessNew,bestimprov;
    Node NodeX,NodeY,NodeZ;
    int i,j,k,pos,pos2,best;
    boolean improv;
      boolean[][] matrix;
      boolean loop;
      
    add=true;
    dag= (Graph) getOutput();
     if (!dag.isADag()){ System.out.println("no es dag");  }
    System.out.println(" Graph " + dag.toString());
     System.out.println("antes de topological order ");
    nodes = dag.topologicalOrder(); 
     System.out.println("despues de topological order ");
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
      parents = dag.parents(NodeX); 
      vars = new NodeList();
      vars.insertNode(NodeX);
      vars.join(parents);
      
      NodeList    previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

        matrix = dag.computeDepMatrix(previous);
       HashSet pastparents = new HashSet();
       loop = false;
    
      improv=true;
      best=0;
      while(improv && !loop){
       improv=false;
       bestimprov = 0.0;
       for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) { 
                //   System.out.println("Nodo no padre");
              connect = new NodeList();
              connect.insertNode(NodeX);
              for (k=0;k<parents.size();k++) {
                 NodeZ = parents.elementAt(k);
                 pos2 = nodes.getId(NodeZ);
                  if (matrix[pos2][j] ) {
                      connect.insertNode(NodeZ);
                  }
              }
              fitness = metric.scoret(connect);
              connect.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew = metric.scoret(connect);
               //    System.out.println("Score finished");
              if (fitnessNew - fitness> bestimprov){
                //   System.out.println("Score mejorado");
                  bestimprov = fitnessNew - fitness;
                  improv=true;
                  
                   add=true;
                  best=j;
              }
              
                
           
          }
          else {
             //    System.out.println("Nodo padre");
           
                connect = new NodeList();
              connect.insertNode(NodeX);
              for (k=0;k<parents.size();k++) {
                 NodeZ = parents.elementAt(k);
                 pos2 = nodes.getId(NodeZ);
                  if (matrix[pos2][j] ) {
                      connect.insertNode(NodeZ);
                  }
              }
              
                fitness = metric.scoret(connect);
              connect.removeNode(NodeY);
              
             
             //      System.out.println("Computing score " + vars.size());
             fitnessNew = metric.scoret(connect);
              //     System.out.println("Score finished");
               if (fitnessNew - fitness> bestimprov){
                //   System.out.println("Score mejorado");
                  bestimprov = fitnessNew - fitness;
                  improv=true;
                  add=false;
                   best = j;
              }
            
              
          }
       }
       if (improv){
          // System.out.println("BEst thing " + best);
           if (add){
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
             //   System.out.println(NodeY.getName());
                try {
         dag.createLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           }
           else {
              NodeY = (FiniteStates) nodes.elementAt(best);
              vars.removeNode(NodeY); 
             // System.out.println(NodeY.getName()+NodeX.getName());
              try {
               //   System.out.println(dag.getLinkList().toString());
         dag.removeLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           }
              
       }
       
       parents =  dag.parents(NodeX); 
         parents.sortByNames();
          
          if (pastparents.contains(parents.toString2())) {loop = true;}
          else {pastparents.add(parents.toString2());}
       
      }
      }
      
}

public void refinePathPrior(Metrics metric){
    
    Graph dag;
    boolean add;
    NodeList nodes,parents,vars,connect;
    double fitness,fitnessNew,bestimprov;
    Node NodeX,NodeY,NodeZ;
    int i,j,k,pos,pos2,best,n;
    boolean improv;
      boolean[][] matrix;
      boolean loop;
      
    add=true;
    dag= (Graph) getOutput();
     if (!dag.isADag()){ System.out.println("no es dag");  }
    System.out.println(" Graph " + dag.toString());
     System.out.println("antes de topological order ");
    nodes = dag.topologicalOrder(); 
     System.out.println("despues de topological order ");
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
      parents = dag.parents(NodeX); 
      vars = new NodeList();
      vars.insertNode(NodeX);
      vars.join(parents);
      
      NodeList    previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

        matrix = dag.computeDepMatrix(previous);
       HashSet pastparents = new HashSet();
       loop = false;
    
      improv=true;
      best=0;
      while(improv && !loop){
       improv=false;
       bestimprov = 0.0;
       for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) { 
                //   System.out.println("Nodo no padre");
              connect = new NodeList(); n= 0;
              connect.insertNode(NodeX);
              for (k=0;k<parents.size();k++) {
                 NodeZ = parents.elementAt(k);
                 pos2 = nodes.getId(NodeZ);
                  if (matrix[pos2][j] ) {
                      connect.insertNode(NodeZ); n++;
                  }
              }
              fitness = metric.scoret(connect);
              connect.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew = metric.scoret(connect)  - Math.log(((double) (i-n))/(n+1.0)) ;
               //    System.out.println("Score finished");
              if (fitnessNew - fitness> bestimprov){
                //   System.out.println("Score mejorado");
                  bestimprov = fitnessNew - fitness;
                  improv=true;
                  
                   add=true;
                  best=j;
              }
              
                
           
          }
          else {
             //    System.out.println("Nodo padre");
           
                connect = new NodeList(); n=0;
              connect.insertNode(NodeX);
              for (k=0;k<parents.size();k++) {
                 NodeZ = parents.elementAt(k);
                 pos2 = nodes.getId(NodeZ);
                  if (matrix[pos2][j] ) {
                      connect.insertNode(NodeZ); n++;
                  }
              }
              
                fitness = metric.scoret(connect);
              connect.removeNode(NodeY);
              
             
             //      System.out.println("Computing score " + vars.size());
             fitnessNew = metric.scoret(connect)  + Math.log(((double) (i-n+1))/(n)); ;
              //     System.out.println("Score finished");
               if (fitnessNew - fitness> bestimprov){
                //   System.out.println("Score mejorado");
                  bestimprov = fitnessNew - fitness;
                  improv=true;
                  add=false;
                   best = j;
              }
             
              
          }
       }
       if (improv){
          // System.out.println("BEst thing " + best);
           if (add){
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
             //   System.out.println(NodeY.getName());
                try {
         dag.createLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           }
           else {
              NodeY = (FiniteStates) nodes.elementAt(best);
              vars.removeNode(NodeY); 
             // System.out.println(NodeY.getName()+NodeX.getName());
              try {
               //   System.out.println(dag.getLinkList().toString());
         dag.removeLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           }
              
       }
       
       parents =  dag.parents(NodeX); 
         parents.sortByNames();
          
          if (pastparents.contains(parents.toString2())) {loop = true;}
          else {pastparents.add(parents.toString2());}
       
      }
      }
      
}


    /*******************************************+
     * This is equivalent to the K2 algorithm with a known order
     * @param metric
     */

public void K2(Metrics metric){

    Graph dag;
    
    NodeList nodes,vars;
    double fitness,fitnessNew;
    Node NodeX,NodeY;
    int i,j,pos,best;
    boolean improv;

    
    dag= (Graph) getOutput();
   
    nodes = dag.topologicalOrder();
   
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
   
      vars = new NodeList();
      vars.insertNode(NodeX);
    
      fitness = metric.scoret(vars);
      improv=true;
      best=0;
      while(improv){
       improv=false;
       for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) {
                //   System.out.println("Nodo no padre");
              vars.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew =  metric.scoret(vars);
               //    System.out.println("Score finished");
              if (fitnessNew > fitness){
                //   System.out.println("Score mejorado");
                  fitness = fitnessNew;
                  improv=true;

        
                  best=j;
              }

                vars.removeNode(NodeY);

          }
         
       }
       if (improv){
          // System.out.println("BEst thing " + best);
          
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
             //   System.out.println(NodeY.getName());
                try {
         dag.createLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           
         

       }

      }
      }

}

public void K2Prior(Metrics metric){

    Graph dag;
    
    NodeList nodes,vars;
    double fitness,fitnessNew;
    Node NodeX,NodeY;
    int i,j,pos,best,nparents;
    boolean improv;

    
    dag= (Graph) getOutput();
   
    nodes = dag.topologicalOrder();
   
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
   
      vars = new NodeList();
      vars.insertNode(NodeX);
    
      fitness = metric.scoret(vars);
      improv=true;
      best=0;
      nparents = 0;
      while(improv){
       improv=false;
       for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) {
                //   System.out.println("Nodo no padre");
              vars.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew =  metric.scoret(vars)- Math.log(((double) (i-nparents))/(nparents+1.0));
               //    System.out.println("Score finished");
              if (fitnessNew > fitness){
                //   System.out.println("Score mejorado");
                  fitness = fitnessNew;
                  improv=true;

        
                  best=j;
              }

                vars.removeNode(NodeY);

          }
         
       }
       if (improv){
          // System.out.println("BEst thing " + best);
          
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
               nparents++;
             //   System.out.println(NodeY.getName());
                try {
         dag.createLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           
         

       }

      }
      }

}



    /*******************************************+
     * This is equivalent to the K2 algorithm with a known order
     * @param metric
     */

public void K2withRemoving(Metrics metric){

    Graph dag;
    
    NodeList nodes,vars;
    double fitness,fitnessNew;
    Node NodeX,NodeY;
    int i,j,pos,best;
    boolean improv;

    
    dag= (Graph) getOutput();
   
    nodes = dag.topologicalOrder();
   
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
   
      vars = new NodeList();
      vars.insertNode(NodeX);
    
      fitness = metric.scoret(vars);
      improv=true;
      best=0;
      while(improv){
       improv=false;
       for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) {
                //   System.out.println("Nodo no padre");
              vars.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew = metric.scoret(vars);
               //    System.out.println("Score finished");
              if (fitnessNew > fitness){
                //   System.out.println("Score mejorado");
                  fitness = fitnessNew;
                  improv=true;

        
                  best=j;
              }

                vars.removeNode(NodeY);

          }
         
       }
       if (improv){
          // System.out.println("BEst thing " + best);
          
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
             //   System.out.println(NodeY.getName());
                try {
         dag.createLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           
         

       }

      }
      
      improv= true;
      while(improv) { 
          improv = false;
    for(j=0; j< i; j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           pos = vars.getId(NodeY);
          if(pos>-1) {
          fitness = metric.scoret(vars);
          vars.removeNode(NodeY);
          fitnessNew = metric.scoret(vars);
          if (fitnessNew < fitness) {
              vars.insertNode(NodeY);
          }
          else{  improv = true;
              try {
               //   System.out.println(dag.getLinkList().toString());
                 dag.removeLink(NodeY,NodeX);
                }
                  catch (InvalidEditException iee) {};      
              
              
              
          }
          
          
          
          }
     }
      
      }
      
      }
    
}

public void K2withRemovingPrior(Metrics metric){

    Graph dag;
    
    NodeList nodes,vars;
    double fitness,fitnessNew;
    Node NodeX,NodeY;
    int i,j,pos,best,nparents;
    boolean improv;

    
    dag= (Graph) getOutput();
   
    nodes = dag.topologicalOrder();
   
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
   
      vars = new NodeList();
      vars.insertNode(NodeX);
    
      fitness = metric.scoret(vars);
      improv=true;
      best=0;
      nparents = 0;
      while(improv){
       improv=false;
       for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) {
                //   System.out.println("Nodo no padre");
              vars.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew = metric.scoret(vars) - Math.log(((double) (i-nparents))/(nparents+1.0));
               //    System.out.println("Score finished");
              if (fitnessNew > fitness){
                //   System.out.println("Score mejorado");
                  fitness = fitnessNew;
                  improv=true;

        
                  best=j;
              }

                vars.removeNode(NodeY);

          }
         
       }
       if (improv){
          // System.out.println("BEst thing " + best);
          
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
               nparents++;
             //   System.out.println(NodeY.getName());
                try {
         dag.createLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           
         

       }

      }
      
      improv= true;
      while(improv) { 
          improv = false;
    for(j=0; j< i; j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           pos = vars.getId(NodeY);
          if(pos>-1) {
          fitness = metric.scoret(vars);
          vars.removeNode(NodeY);
          fitnessNew = metric.scoret(vars)  + Math.log(((double) (i-nparents+1))/(nparents));
          if (fitnessNew < fitness) {
              vars.insertNode(NodeY);
          }
          else{  improv = true;
             nparents--;
              try {
               //   System.out.println(dag.getLinkList().toString());
                 dag.removeLink(NodeY,NodeX);
                }
                  catch (InvalidEditException iee) {};      
              
              
              
          }
          
          
          
          }
     }
      
      }
      
      }

    
    
    
    
    
    
    
}



    /*******************************************+
     * This is equivalent to the K2 algorithm with a known order
     * @param metric
     */

public void K2withPC(Metrics metric){

    Graph dag;
    
    NodeList nodes,vars, subset, test;
    double fitness,fitnessNew;
    Node NodeX,NodeY;
    int i,j,pos,best,n;
    boolean improv, indep;
    Enumeration en;

    
    dag= (Graph) getOutput();
   
    nodes = dag.topologicalOrder();
   
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
   
      vars = new NodeList();
      vars.insertNode(NodeX);
    
      fitness = metric.scoret(vars);
      improv=true;
      best=0;
      while(improv){
       improv=false;
       for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) {
                //   System.out.println("Nodo no padre");
              vars.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew = metric.scoret(vars);
               //    System.out.println("Score finished");
              if (fitnessNew > fitness){
                //   System.out.println("Score mejorado");
                  fitness = fitnessNew;
                  improv=true;

        
                  best=j;
              }

                vars.removeNode(NodeY);

          }
         
       }
       if (improv){
          // System.out.println("BEst thing " + best);
          
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
             //   System.out.println(NodeY.getName());
                try {
         dag.createLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           
         

       }

      }
      
   j = 1;
      while ( j<vars.size()) {
          
          NodeY = (FiniteStates) vars.elementAt(j); 
          subset = new NodeList();
          subset.insertNode(NodeX);
          fitness = metric.scoret(subset);
          subset.insertNode(NodeY);
          fitnessNew = metric.scoret(subset);
          if (fitness >= fitnessNew) {
              vars.removeNode(NodeY);
               try {
               //   System.out.println(dag.getLinkList().toString());
                 dag.removeLink(NodeY,NodeX);
                }
                  catch (InvalidEditException iee) {};      
          }
          else{j++;}
              
      }
   
   
   n= 1;
   
    while (n< vars.size()) {
        
          j = 1;
      while ( j<vars.size()) {
          
          NodeY = (FiniteStates) vars.elementAt(j);
          
          
            indep = false;
           en =vars.subSetsOfSize(n).elements();
                while( (en.hasMoreElements())) {
                     subset = (NodeList)en.nextElement(); 
                     if (subset.getId(NodeY) == -1){
                     test = new NodeList();
                     test.insertNode(NodeX);
                     test.join(subset);
                     fitness = metric.scoret(test);
                     test.insertNode(NodeY);
                     fitnessNew = metric.scoret(test);
                     
                     if (fitness >= fitnessNew) {
                         indep= true;
                         break;
                     }
                     }
          
                } 
           if (indep) {
                  try {
               //   System.out.println(dag.getLinkList().toString());
                 dag.removeLink(NodeY,NodeX);
                }
                  catch (InvalidEditException iee) {};    
                    vars.removeNode(NodeY);
           }
           else {j++;}
      }
        
      n++;  
    }
               
   
   
      
      
      
      
      
      
      }

    
    
    
    
    
    
    
}


public void K2withPCPrior(Metrics metric){

    Graph dag;
    
    NodeList nodes,vars, subset, test;
    double fitness,fitnessNew;
    Node NodeX,NodeY;
    int i,j,pos,best,n,nparents;
    boolean improv, indep;
    Enumeration en;

    
    dag= (Graph) getOutput();
   
    nodes = dag.topologicalOrder();
   
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
   
      vars = new NodeList();
      vars.insertNode(NodeX);
    
      fitness = metric.scoret(vars);
      improv=true;
      best=0;
      nparents = 0;
      while(improv){
       improv=false;
       for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) {
                //   System.out.println("Nodo no padre");
              vars.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew = metric.scoret(vars)- Math.log(((double) (i-nparents))/(nparents+1.0));  ;
               //    System.out.println("Score finished");
              if (fitnessNew > fitness){
                //   System.out.println("Score mejorado");
                  fitness = fitnessNew;
                  improv=true;

        
                  best=j;
              }

                vars.removeNode(NodeY);

          }
         
       }
       if (improv){
          // System.out.println("BEst thing " + best);
          
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
               nparents++;
             //   System.out.println(NodeY.getName());
                try {
         dag.createLink(NodeY,NodeX);
        }
        catch (InvalidEditException iee) {};
           
         

       }

      }
      
   j = 1;
      while ( j<vars.size()) {
          
          NodeY = (FiniteStates) vars.elementAt(j); 
          subset = new NodeList();
          subset.insertNode(NodeX);
          fitness = metric.scoret(subset);
          subset.insertNode(NodeY);
          fitnessNew = metric.scoret(subset) - Math.log((double) (i));        
          if (fitness >= fitnessNew) {
              vars.removeNode(NodeY);
               try {
               //   System.out.println(dag.getLinkList().toString());
                 dag.removeLink(NodeY,NodeX);
                }
                  catch (InvalidEditException iee) {};      
          }
          else{j++;}
              
      }
   
   
   n= 1;
   
    while (n< vars.size()) {
        
          j = 1;
      while ( j<vars.size()) {
          
          NodeY = (FiniteStates) vars.elementAt(j);
          
          
            indep = false;
           en =vars.subSetsOfSize(n).elements();
                while( (en.hasMoreElements())) {
                     subset = (NodeList)en.nextElement(); 
                     if (subset.getId(NodeY) == -1){
                     test = new NodeList();
                     test.insertNode(NodeX);
                     test.join(subset);
                     fitness = metric.scoret(test);
                     test.insertNode(NodeY);
                     fitnessNew = metric.scoret(test)- Math.log(((double) (i-n))/(n+1.0));
                     
                     if (fitness >= fitnessNew) {
                         indep= true;
                         break;
                     }
                     }
          
                } 
           if (indep) {
                  try {
               //   System.out.println(dag.getLinkList().toString());
                 dag.removeLink(NodeY,NodeX);
                }
                  catch (InvalidEditException iee) {};    
                    vars.removeNode(NodeY);
           }
           else {j++;}
      }
        
      n++;  
    }
               
   
   
      
      
      
      
      
      
      }

    
    
    
    
    
    
    
}



/***** 
 * This is a mixed scored and test based method to compute the scores if a correct ordering is given
 * @param metric
 */

public void refinePC(Metrics metric){
    
    Graph dag;
    NodeList nodes,vars,parents,subSet;
    int i,j,ibest,n;
    Node NodeX,NodeY, bestNode;
    double  fitness, newfitness,best;
    double[] scores;
    Enumeration en;   
    
    dag= (Graph) getOutput();
     if (!dag.isADag()){ System.out.println("no es dag");  }
    System.out.println(" Graph " + dag.toString());
     System.out.println("antes de topological order ");
    nodes = dag.topologicalOrder(); 
     System.out.println("despues de topological order ");
     
    scores = new double[nodes.size()];
    
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
      parents = dag.parents(NodeX); 
      for (j=0; j<parents.size(); j++){
              NodeY  = (FiniteStates) parents.elementAt(j);  
                try {
               //   System.out.println(dag.getLinkList().toString());
                 dag.removeLink(NodeY,NodeX);
                }
                  catch (InvalidEditException iee) {};     
      }
       vars = new NodeList();
      vars.insertNode(NodeX);
      fitness = metric.scoret(vars);

      best = 0.0;ibest=i;bestNode = (FiniteStates) nodes.elementAt(0);  
     for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);   
          vars.insertNode(NodeY);
          newfitness = metric.scoret(vars);
          scores[j] = newfitness - fitness;
          vars.removeNode(1);
          if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
     }
      
      while (best>0.0){
          best = -1.0;
          scores[ibest] = -1;  
          
             for (n=0;n<=parents.size();n++) {
                en =parents.subSetsOfSize(n).elements();
                 while( (en.hasMoreElements())) { 
                      subSet = (NodeList)en.nextElement();
                        vars = new NodeList();
                        vars.insertNode(NodeX);
                        vars.insertNode(bestNode);
                        vars.join(subSet);
                        fitness = metric.scoret(vars);
                       
                      for(j=0;j<i;j++){
                          if(scores[j]>0.0){
                                NodeY  = (FiniteStates) nodes.elementAt(j);   
                                vars.insertNode(NodeY);
                                newfitness = metric.scoret(vars);
                                vars.removeNode(vars.size()-1);
                                if (scores[j] > (newfitness-fitness)) {scores[j] = newfitness-fitness;}
                              
                          }
                      }
                  
                  
             }
             }
           try {
           dag.createLink(bestNode,NodeX);
            }
            catch (InvalidEditException iee) {};
                parents = dag.parents(NodeX); 
            for(j=0;j<i;j++){
                  if(scores[j]>best){
                      best = scores[j];
                      NodeY  = (FiniteStates) nodes.elementAt(j);   
                      bestNode = NodeY;ibest=j;
                  }
                  }
      }
    }
}
   
public void refinePCPrior(Metrics metric){
    
    Graph dag;
    NodeList nodes,vars,parents,subSet;
    int i,j,ibest,n;
    Node NodeX,NodeY, bestNode;
    double  fitness, newfitness,best;
    double[] scores;
    Enumeration en;   
    
    dag= (Graph) getOutput();
     if (!dag.isADag()){ System.out.println("no es dag");  }
    System.out.println(" Graph " + dag.toString());
     System.out.println("antes de topological order ");
    nodes = dag.topologicalOrder(); 
     System.out.println("despues de topological order ");
     
    scores = new double[nodes.size()];
    
    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
      parents = dag.parents(NodeX); 
      for (j=0; j<parents.size(); j++){
              NodeY  = (FiniteStates) parents.elementAt(j);  
                try {
               //   System.out.println(dag.getLinkList().toString());
                 dag.removeLink(NodeY,NodeX);
                }
                  catch (InvalidEditException iee) {};     
      }
       vars = new NodeList();
      vars.insertNode(NodeX);
      fitness = metric.scoret(vars);

      best = 0.0;ibest=i;bestNode = (FiniteStates) nodes.elementAt(0);  
     for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);   
          vars.insertNode(NodeY);
          newfitness = metric.scoret(vars)  - Math.log((double) (i)) ;
          scores[j] = newfitness - fitness;
          vars.removeNode(1);
          if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
     }
      
      while (best>0.0){
          best = -1.0;
          scores[ibest] = -1;  
          
             for (n=0;n<=parents.size();n++) {
                en =parents.subSetsOfSize(n).elements();
                 while( (en.hasMoreElements())) { 
                      subSet = (NodeList)en.nextElement();
                        vars = new NodeList();
                        vars.insertNode(NodeX);
                        vars.insertNode(bestNode);
                        vars.join(subSet);
                        fitness = metric.scoret(vars)     ;
                       
                      for(j=0;j<i;j++){
                          if(scores[j]>0.0){
                                NodeY  = (FiniteStates) nodes.elementAt(j);   
                                vars.insertNode(NodeY);
                                newfitness = metric.scoret(vars) - Math.log(((double) (i-n-1))/(n+2.0));
                                vars.removeNode(vars.size()-1);
                                if (scores[j] > (newfitness-fitness)) {scores[j] = newfitness-fitness;}
                              
                          }
                      }
                  
                  
             }
             }
           try {
           dag.createLink(bestNode,NodeX);
            }
            catch (InvalidEditException iee) {};
                parents = dag.parents(NodeX); 
            for(j=0;j<i;j++){
                  if(scores[j]>best){
                      best = scores[j];
                      NodeY  = (FiniteStates) nodes.elementAt(j);   
                      bestNode = NodeY;ibest=j;
                  }
                  }
      }
    }
}
     

public void refinePCSimpleP(Metrics metric){

    Graph dag;
    NodeList nodes,vars,parents,subSet;
    int i,j,ibest,n;
    Node NodeX,NodeY, bestNode;
    double  fitness, newfitness,best;
    double[] scores;
    Enumeration en;

    dag= (Graph) getOutput();
     if (!dag.isADag()){ System.out.println("no es dag");  }
    System.out.println(" Graph " + dag.toString());
     System.out.println("antes de topological order ");
    nodes = dag.topologicalOrder();
     System.out.println("despues de topological order ");

    scores = new double[nodes.size()];

    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
      parents = dag.parents(NodeX);
      for (j=0; j<parents.size(); j++){
              NodeY  = (FiniteStates) parents.elementAt(j);
                try {
               //   System.out.println(dag.getLinkList().toString());
                 dag.removeLink(NodeY,NodeX);
                }
                  catch (InvalidEditException iee) {};
      }
       vars = new NodeList();
      vars.insertNode(NodeX);
      fitness = metric.scoret(vars);

      best = 0.0;ibest=i;bestNode = (FiniteStates) nodes.elementAt(0);
     for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
          vars.insertNode(NodeY);
          newfitness = metric.scoret(vars)  - Math.log((double) (i)) ;
          scores[j] = newfitness - fitness;
          vars.removeNode(1);
          if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
     }

      n=0;

      while (best>0.0){
          best = -1.0;
          scores[ibest] = -1;
            try {
           dag.createLink(bestNode,NodeX);
            }
            catch (InvalidEditException iee) {};
                parents = dag.parents(NodeX);
                vars = new NodeList();
                vars.insertNode(NodeX);
                vars.join(parents);
                n++;
                fitness = metric.scoret(vars)     ;

                      for(j=0;j<i;j++){
                          if(scores[j]>0.0){
                                NodeY  = (FiniteStates) nodes.elementAt(j);
                                vars.insertNode(NodeY);
                                newfitness = metric.scoret(vars) - Math.log(((double) (i-n))/(n+1.0));
                                vars.removeNode(vars.size()-1);
         
                                if (scores[j] > (newfitness-fitness)) {scores[j] = newfitness-fitness;}

                          }
                      }



            for(j=0;j<i;j++){
                  if(scores[j]>best){
                      best = scores[j];
                      NodeY  = (FiniteStates) nodes.elementAt(j);
                      bestNode = NodeY;ibest=j;
                  }
                  }
      }
    }
}


public void refinePCMixedP(Metrics metric){

    Graph dag;
    NodeList nodes,vars,parents,subSet;
    int i,j,ibest,n;
    Node NodeX,NodeY, bestNode;
    double  fitness, newfitness,best;
    double[] scores;
    Enumeration en;

    dag= (Graph) getOutput();
     if (!dag.isADag()){ System.out.println("no es dag");  }
    System.out.println(" Graph " + dag.toString());
     System.out.println("antes de topological order ");
    nodes = dag.topologicalOrder();
     System.out.println("despues de topological order ");

    scores = new double[nodes.size()];

    for(i=0; i< nodes.size(); i++){
      NodeX = (FiniteStates) nodes.elementAt(i);
      System.out.println("Refinando nodo " + NodeX.getName());
      parents = dag.parents(NodeX);
      for (j=0; j<parents.size(); j++){
              NodeY  = (FiniteStates) parents.elementAt(j);
                try {
               //   System.out.println(dag.getLinkList().toString());
                 dag.removeLink(NodeY,NodeX);
                }
                  catch (InvalidEditException iee) {};
      }
       vars = new NodeList();
      vars.insertNode(NodeX);
      fitness = metric.scoret(vars);

      best = 0.0;ibest=i;bestNode = (FiniteStates) nodes.elementAt(0);
     for(j=0;j<i;j++){
          NodeY  = (FiniteStates) nodes.elementAt(j);
          vars.insertNode(NodeY);
          newfitness = metric.scoret(vars)  - Math.log((double) (i)) ;
          scores[j] = newfitness - fitness;
          vars.removeNode(1);
          if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
     }

      n=0;

      while (best>0.0){
          best = -1.0;
          scores[ibest] = -1;
            try {
           dag.createLink(bestNode,NodeX);
            }
            catch (InvalidEditException iee) {};
            n++;
                parents = dag.parents(NodeX);
                vars = new NodeList();
                vars.insertNode(NodeX);
                vars.join(parents);
                fitness = metric.scoret(vars)     ;

                      for(j=0;j<i;j++){
                          if(scores[j]>0.0){
                                NodeY  = (FiniteStates) nodes.elementAt(j);
                                vars.insertNode(NodeY);
                                newfitness = metric.scoret(vars) - Math.log(((double) (i-n))/(n+1.0));
                                vars.removeNode(vars.size()-1);
                                if (scores[j] > (newfitness-fitness)) {scores[j] = newfitness-fitness;}

                          }
                      }



            for(j=0;j<i;j++){
                  if(scores[j]>best){
                      best = scores[j];
                      NodeY  = (FiniteStates) nodes.elementAt(j);
                      bestNode = NodeY;ibest=j;
                  }
                  }
      }





    refinesearchprior(metric);








    }
}



/*
 * A mixed strategy (given an order) between tests and scores taking paths into account
 * 
 */


public void refinePCPath(Metrics metric){

   Graph dag;
   NodeList nodes,vars,parents,subSet,previous,connect;
   int i,j,k,ibest,n,l;
   Node NodeX,NodeY,NodeZ, bestNode;
   double  fitness, newfitness,best;
   double[] scores;
   boolean[][] matrix;
   Enumeration en;

   dag= (Graph) getOutput();
    if (!dag.isADag()){ System.out.println("no es dag");  }
   System.out.println(" Graph " + dag.toString());
    System.out.println("antes de topological order ");
   nodes = dag.topologicalOrder();
    System.out.println("despues de topological order ");

   scores = new double[nodes.size()];

   for(i=0; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     System.out.println("Refinando nodo " + NodeX.getName());
     parents = dag.parents(NodeX);
     for (j=0; j<parents.size(); j++){
             NodeY  = (FiniteStates) parents.elementAt(j);
               try {
              //   System.out.println(dag.getLinkList().toString());
                dag.removeLink(NodeY,NodeX);
               }
                 catch (InvalidEditException iee) {};
     }

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

     matrix = dag.computeDepMatrix(previous);

      vars = new NodeList();
     vars.insertNode(NodeX);
     fitness = metric.scoret(vars);

     best = 0.0;
     ibest=i;
     bestNode = (FiniteStates) nodes.elementAt(0);
    for(j=0;j<i;j++){
         NodeY  = (FiniteStates) nodes.elementAt(j);
         vars.insertNode(NodeY);
         newfitness = metric.scoret(vars);
         scores[j] = newfitness - fitness;
         vars.removeNode(1);
         if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
    }

     while (best>0.0){
         best = -1.0;
         scores[ibest] = 0.0;
         try {
          dag.createLink(bestNode,NodeX);
           }
           catch (InvalidEditException iee) {};
              parents = dag.parents(NodeX);
             for(j=0;j<i;j++){
                   if((scores[j]>0.0) && (matrix[ibest][j]) && (j!=ibest)){
                    NodeY  = (FiniteStates) nodes.elementAt(j);
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if(matrix[l][j] && (ibest != l)){
                         
                         connect.insertNode(NodeZ);}
                    }
              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
                while( (en.hasMoreElements())) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars);
                       if (scores[j] > (newfitness-fitness))
                            {scores[j] = newfitness-fitness;}

                         }
                     }


            }
             }
               for(j=0;j<i;j++){
                  if(scores[j]>best){
                      best = scores[j];
                      NodeY  = (FiniteStates) nodes.elementAt(j);   
                      bestNode = NodeY;ibest=j;
                  }
                  }  
              
             }
     }

}

 


public void refinePCPathPrior(Metrics metric){

   Graph dag;
   NodeList nodes,vars,parents,subSet,previous,connect;
   int i,j,k,ibest,n,l;
   Node NodeX,NodeY,NodeZ, bestNode;
   double  fitness, newfitness,best;
   double[] scores;
   boolean[][] matrix;
   Enumeration en;

   dag= (Graph) getOutput();
    if (!dag.isADag()){ System.out.println("no es dag");  }
   System.out.println(" Graph " + dag.toString());
    System.out.println("antes de topological order ");
   nodes = dag.topologicalOrder();
    System.out.println("despues de topological order ");

   scores = new double[nodes.size()];

   for(i=0; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     System.out.println("Refinando nodo " + NodeX.getName());
     parents = dag.parents(NodeX);
     for (j=0; j<parents.size(); j++){
             NodeY  = (FiniteStates) parents.elementAt(j);
               try {
              //   System.out.println(dag.getLinkList().toString());
                dag.removeLink(NodeY,NodeX);
               }
                 catch (InvalidEditException iee) {};
     }

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

     matrix = dag.computeDepMatrix(previous);

      vars = new NodeList();
     vars.insertNode(NodeX);
     fitness = metric.scoret(vars);

     best = 0.0;
     ibest=i;
     bestNode = (FiniteStates) nodes.elementAt(0);
    for(j=0;j<i;j++){
         NodeY  = (FiniteStates) nodes.elementAt(j);
         vars.insertNode(NodeY);
         newfitness = metric.scoret(vars) - Math.log((double) (i));
         scores[j] = newfitness - fitness;
         vars.removeNode(1);
         if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
    }

     while (best>0.0){
         best = -1.0;
         scores[ibest] = 0.0;
         try {
          dag.createLink(bestNode,NodeX);
           }
           catch (InvalidEditException iee) {};
              parents = dag.parents(NodeX);
             for(j=0;j<i;j++){
                   if((scores[j]>0.0) && (matrix[ibest][j]) && (j!=ibest)){
                    NodeY  = (FiniteStates) nodes.elementAt(j);
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if(matrix[l][j] && (ibest != l)){
                         
                         connect.insertNode(NodeZ);}
                    }
              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
                while( (en.hasMoreElements())) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars) - Math.log(((double) (i-n-1))/(n+2.0));
                       if (scores[j] > (newfitness-fitness))
                            {scores[j] = newfitness-fitness;}

                         }
                     }


            }
             }
               for(j=0;j<i;j++){
                  if(scores[j]>best){
                      best = scores[j];
                      NodeY  = (FiniteStates) nodes.elementAt(j);   
                      bestNode = NodeY;ibest=j;
                  }
                  }  
              
             }
     }

}

/*
 * A mixed strategy (given an order) between tests and scores taking paths into account
 * similar to refine3, but now less tests are carried out: only the tests conditional to the
 * sets with greater size
 * 
 */


public void refinePCPathmax(Metrics metric){

   Graph dag;
   NodeList nodes,vars,parents,subSet,previous,connect;
   int i,j,k,ibest,n,l;
   Node NodeX,NodeY,NodeZ, bestNode;
   double  fitness, newfitness,best;
   double[] scores;
   boolean[][] matrix;
   Enumeration en;

   dag= (Graph) getOutput();
    if (!dag.isADag()){ System.out.println("no es dag");  }
   System.out.println(" Graph " + dag.toString());
    System.out.println("antes de topological order ");
   nodes = dag.topologicalOrder();
    System.out.println("despues de topological order ");

   scores = new double[nodes.size()];

   for(i=0; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     System.out.println("Refinando nodo " + NodeX.getName());
     parents = dag.parents(NodeX);
     for (j=0; j<parents.size(); j++){
             NodeY  = (FiniteStates) parents.elementAt(j);
               try {
              //   System.out.println(dag.getLinkList().toString());
                dag.removeLink(NodeY,NodeX);
               }
                 catch (InvalidEditException iee) {};
     }

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

     matrix = dag.computeDepMatrix(previous);

      vars = new NodeList();
     vars.insertNode(NodeX);
     fitness = metric.scoret(vars);

     best = 0.0;
     ibest=i;
     bestNode = (FiniteStates) nodes.elementAt(0);
    for(j=0;j<i;j++){
         NodeY  = (FiniteStates) nodes.elementAt(j);
         vars.insertNode(NodeY);
         newfitness = metric.scoret(vars);
         scores[j] = newfitness - fitness;
         vars.removeNode(1);
         if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
    }

     while (best>0.0){
         best = -1.0;
         scores[ibest] = 0.0;
         try {
          dag.createLink(bestNode,NodeX);
           }
           catch (InvalidEditException iee) {};
              parents = dag.parents(NodeX);
             for(j=0;j<i;j++){
                   if((scores[j]>0.0) && (matrix[ibest][j]) && (j!=ibest)){
                    NodeY  = (FiniteStates) nodes.elementAt(j);
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if(matrix[l][j] && (ibest != l)){
                         
                         connect.insertNode(NodeZ);}

            
                    }
              
                    
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(connect);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars);
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}

                        
                     


            }
            }
                 for(j=0;j<i;j++){
                  if(scores[j]>best){
                      best = scores[j];
                      NodeY  = (FiniteStates) nodes.elementAt(j);   
                      bestNode = NodeY;ibest=j;
                  }
                  }
              
             }
     }

}


public void refinePCPathmaxPrior(Metrics metric){

   Graph dag;
   NodeList nodes,vars,parents,subSet,previous,connect;
   int i,j,k,ibest,n,l;
   Node NodeX,NodeY,NodeZ, bestNode;
   double  fitness, newfitness,best;
   double[] scores;
   boolean[][] matrix;
   Enumeration en;

   dag= (Graph) getOutput();
    if (!dag.isADag()){ System.out.println("no es dag");  }
   System.out.println(" Graph " + dag.toString());
    System.out.println("antes de topological order ");
   nodes = dag.topologicalOrder();
    System.out.println("despues de topological order ");

   scores = new double[nodes.size()];

   for(i=0; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     System.out.println("Refinando nodo " + NodeX.getName());
     parents = dag.parents(NodeX);
     for (j=0; j<parents.size(); j++){
             NodeY  = (FiniteStates) parents.elementAt(j);
               try {
              //   System.out.println(dag.getLinkList().toString());
                dag.removeLink(NodeY,NodeX);
               }
                 catch (InvalidEditException iee) {};
     }

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

     matrix = dag.computeDepMatrix(previous);

      vars = new NodeList();
     vars.insertNode(NodeX);
     fitness = metric.scoret(vars);

     best = 0.0;
     ibest=i;
     bestNode = (FiniteStates) nodes.elementAt(0);
    for(j=0;j<i;j++){
         NodeY  = (FiniteStates) nodes.elementAt(j);
         vars.insertNode(NodeY);
         newfitness = metric.scoret(vars)- Math.log((double) (i));
         scores[j] = newfitness - fitness;
         vars.removeNode(1);
         if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
    }

     while (best>0.0){
         best = -1.0;
         scores[ibest] = 0.0;
         try {
          dag.createLink(bestNode,NodeX);
           }
           catch (InvalidEditException iee) {};
              parents = dag.parents(NodeX);
             for(j=0;j<i;j++){
                   if((scores[j]>0.0) && (matrix[ibest][j]) && (j!=ibest)){
                    NodeY  = (FiniteStates) nodes.elementAt(j);
                    connect = new NodeList(); n=0;
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if(matrix[l][j] && (ibest != l)){
                         n++;
                         connect.insertNode(NodeZ);}

            
                    }
              
                    
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(connect);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars)  - Math.log(((double) (i-n-1))/(n+2.0));
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}

                        
                     


            }
            }
                 for(j=0;j<i;j++){
                  if(scores[j]>best){
                      best = scores[j];
                      NodeY  = (FiniteStates) nodes.elementAt(j);   
                      bestNode = NodeY;ibest=j;
                  }
                  }
              
             }
     }

}


/* It is a modification of refine3, but now after entering a node, another previously 
 * introduced node can be out.
 * 
 */		


public void PCPAthremove(Metrics metric){

   Graph dag;
   NodeList nodes,vars,parents,subSet,previous,connect;
   int i,j,k,ibest,n,l;
   Node NodeX,NodeY,NodeZ, bestNode;
   double  fitness, newfitness,best;
   double[] scores;
   boolean[][] matrix;
   Enumeration en;

   dag= (Graph) getOutput();
    if (!dag.isADag()){ System.out.println("no es dag");  }
   System.out.println(" Graph " + dag.toString());
    System.out.println("antes de topological order ");
   nodes = dag.topologicalOrder();
    System.out.println("despues de topological order ");

   scores = new double[nodes.size()];

   for(i=1; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     System.out.println("Refinando nodo " + NodeX.getName());
     parents = dag.parents(NodeX);
    //  System.out.println(dag.getLinkList().toString());
     System.out.println("Parentssize " + parents.size());
     for (j=0; j<parents.size(); j++){
        
             NodeY  = (FiniteStates) parents.elementAt(j);
            System.out.println("Removing parent " + NodeY.getName());
           try {

               dag.removeLink(NodeY, NodeX);
           } catch (InvalidEditException iee) {
           }
           ;
       }

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

     matrix = dag.computeDepMatrix(previous);

      vars = new NodeList();
     vars.insertNode(NodeX);
     fitness = metric.scoret(vars);

     best = 0.0;
     ibest=i;
     bestNode = (FiniteStates) previous.elementAt(0);
    for(j=0;j<i;j++){
         NodeY  = (FiniteStates) previous.elementAt(j);
         vars.insertNode(NodeY);
         newfitness = metric.scoret(vars);
         scores[j] = newfitness - fitness;
         vars.removeNode(1);
         if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
    }

     while (best>0.0){
         best = -1.0;
         scores[ibest] = 0.0;
      
              parents = dag.parents(NodeX);
             for(j=0;j<i;j++){
                   NodeY  = (FiniteStates) previous.elementAt(j);
        //              System.out.println("Recomputing scores (1) " + NodeY.getName() );
                   if((scores[j]>0.0) && (matrix[ibest][j]) && (j!=ibest)){
          //                   System.out.println("Recomputing scores (2) " + NodeY.getName() );
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = previous.getId(NodeZ);
                     if(matrix[l][j] && (ibest != l)){
            //             System.out.println("Inserting node to test " + NodeZ.getName() );
                         connect.insertNode(NodeZ);}
                    }

              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
                while( (en.hasMoreElements())) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars);
                      
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}
//                          System.out.println("New fitness node " + NodeY.getName() + " " + scores[j] );
  if (scores[j]<=0) {break;}
                         }
               
               if (scores[j]<=0) {break;}  
                     }


           
            }
                   
            if((parents.getId(NodeY)>0) && (matrix[ibest][j])&& (!NodeY.equals(bestNode))){
                  System.out.println("Testing removing " + NodeY.getName());
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if((matrix[l][j]) && (!NodeZ.equals(NodeY))  && (!NodeZ.equals(bestNode))    ) {
                         
                         connect.insertNode(NodeZ);}
                    }
        boolean removed = false;
              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
               
                while( (en.hasMoreElements()) && (!removed)) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars);
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}
                        if ((newfitness-fitness)<=0.0) {
                            System.out.println("Removing link between " + NodeY.getName() + " and " + NodeX.getName());
                           try{
                               dag.removeLink(NodeY, NodeX);
                               }
                 catch (InvalidEditException iee) {};
                    removed= true;
                        }

                         }
               
                if (removed) {break;}
                     }


            }
            
                   
             }
              
                 System.out.println("Creating link between " + bestNode.getName() + " and " + NodeX.getName());
         try {
          dag.createLink(bestNode,NodeX);
           }
           catch (InvalidEditException iee) {};
           
              
              best=0.0;
        for(j=0;j<i;j++){
           if (scores[j] > best) {best = scores[j]; bestNode =(FiniteStates) nodes.elementAt(j); ibest=j;} 
            
        }       
              
     }
   
 
   
   
   
   }

}


public void PCPAthremovePrior(Metrics metric){

   Graph dag;
   NodeList nodes,vars,parents,subSet,previous,connect;
   int i,j,k,ibest,n,l;
   Node NodeX,NodeY,NodeZ, bestNode;
   double  fitness, newfitness,best;
   double[] scores;
   boolean[][] matrix;
   Enumeration en;

   dag= (Graph) getOutput();
    if (!dag.isADag()){ System.out.println("no es dag");  }
   System.out.println(" Graph " + dag.toString());
    System.out.println("antes de topological order ");
   nodes = dag.topologicalOrder();
    System.out.println("despues de topological order ");

   scores = new double[nodes.size()];

   for(i=1; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     System.out.println("Refinando nodo " + NodeX.getName());
     parents = dag.parents(NodeX);
    //  System.out.println(dag.getLinkList().toString());
     System.out.println("Parentssize " + parents.size());
     for (j=0; j<parents.size(); j++){
        
             NodeY  = (FiniteStates) parents.elementAt(j);
            System.out.println("Removing parent " + NodeY.getName());
           try {

               dag.removeLink(NodeY, NodeX);
           } catch (InvalidEditException iee) {
           }
           ;
       }

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

     matrix = dag.computeDepMatrix(previous);

      vars = new NodeList();
     vars.insertNode(NodeX);
     fitness = metric.scoret(vars);

     best = 0.0;
     ibest=i;
     bestNode = (FiniteStates) previous.elementAt(0);
    for(j=0;j<i;j++){
         NodeY  = (FiniteStates) previous.elementAt(j);
         vars.insertNode(NodeY);
         newfitness = metric.scoret(vars) - Math.log((double) (i));
         scores[j] = newfitness - fitness;
         vars.removeNode(1);
         if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
    }

     while (best>0.0){
         best = -1.0;
         scores[ibest] = 0.0;
      
              parents = dag.parents(NodeX);
             for(j=0;j<i;j++){
                   NodeY  = (FiniteStates) previous.elementAt(j);
        //              System.out.println("Recomputing scores (1) " + NodeY.getName() );
                   if((scores[j]>0.0) && (matrix[ibest][j]) && (j!=ibest)){
          //                   System.out.println("Recomputing scores (2) " + NodeY.getName() );
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = previous.getId(NodeZ);
                     if(matrix[l][j] && (ibest != l)){
            //             System.out.println("Inserting node to test " + NodeZ.getName() );
                         connect.insertNode(NodeZ);}
                    }

              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
                while( (en.hasMoreElements())) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars)  - Math.log(((double) (i-n-1))/(n+2.0))   ;
                      
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}
//                          System.out.println("New fitness node " + NodeY.getName() + " " + scores[j] );
  if (scores[j]<=0) {break;}
                         }
               
               if (scores[j]<=0) {break;}  
                     }


           
            }
                   
            if((parents.getId(NodeY)>0) && (matrix[ibest][j])&& (!NodeY.equals(bestNode))){
                  System.out.println("Testing removing " + NodeY.getName());
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if((matrix[l][j]) && (!NodeZ.equals(NodeY))  && (!NodeZ.equals(bestNode))    ) {
                         
                         connect.insertNode(NodeZ);}
                    }
        boolean removed = false;
              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
               
                while( (en.hasMoreElements()) && (!removed)) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars) - Math.log(((double) (i-n-1))/(n+2.0)) ;
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}
                        if ((newfitness-fitness)<=0.0) {
                            System.out.println("Removing link between " + NodeY.getName() + " and " + NodeX.getName());
                           try{
                               dag.removeLink(NodeY, NodeX);
                               }
                 catch (InvalidEditException iee) {};
                    removed= true;
                        }

                         }
               
                if (removed) {break;}
                     }


            }
            
                   
             }
              
                 System.out.println("Creating link between " + bestNode.getName() + " and " + NodeX.getName());
         try {
          dag.createLink(bestNode,NodeX);
           }
           catch (InvalidEditException iee) {};
           
              
              best=0.0;
        for(j=0;j<i;j++){
           if (scores[j] > best) {best = scores[j]; bestNode =(FiniteStates) nodes.elementAt(j); ibest=j;} 
            
        }       
              
     }
   
 
   
   
   
   }

}


/* Equal to refine 4, but the path condition is not taken into account
 * 
 */


public void PCremove(Metrics metric){

   Graph dag;
   NodeList nodes,vars,parents,subSet,previous,connect;
   int i,j,k,ibest,n,l;
   Node NodeX,NodeY,NodeZ, bestNode;
   double  fitness, newfitness,best;
   double[] scores;
   boolean[][] matrix;
   Enumeration en;
   boolean removed;

   dag= (Graph) getOutput();
    if (!dag.isADag()){ System.out.println("no es dag");  }
   System.out.println(" Graph " + dag.toString());
    System.out.println("antes de topological order ");
   nodes = dag.topologicalOrder();
    System.out.println("despues de topological order ");

   scores = new double[nodes.size()];

   for(i=0; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     System.out.println("Refinando nodo " + NodeX.getName());
     parents = dag.parents(NodeX);
    //  System.out.println(dag.getLinkList().toString());
     System.out.println("Parentssize " + parents.size());
     for (j=0; j<parents.size(); j++){
        
             NodeY  = (FiniteStates) parents.elementAt(j);
              System.out.println("Removing parent " + NodeY.getName());
               try {
               
                dag.removeLink(NodeY,NodeX);
               }
                 catch (InvalidEditException iee) {};
     }

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

    

      vars = new NodeList();
     vars.insertNode(NodeX);
     fitness = metric.scoret(vars);

     best = 0.0;
     ibest=i;
     bestNode = new FiniteStates();
    for(j=0;j<i;j++){
         NodeY  = (FiniteStates) previous.elementAt(j);
         vars.insertNode(NodeY);
         newfitness = metric.scoret(vars);
         scores[j] = newfitness - fitness;
         vars.removeNode(1);
         if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
    }

     while (best>0.0){
         best = -1.0;
         scores[ibest] = 0.0;
        
           
              parents = dag.parents(NodeX);
              
             for(j=0;j<i;j++){
                   NodeY  = (FiniteStates) previous.elementAt(j);
        //              System.out.println("Recomputing scores (1) " + NodeY.getName() );
                   if((scores[j]>0.0) &&  (j!=ibest)){
          //                   System.out.println("Recomputing scores (2) " + NodeY.getName() );
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       
                     
            //             System.out.println("Inserting node to test " + NodeZ.getName() );
                         connect.insertNode(NodeZ);}

              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
                while( (en.hasMoreElements())) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars);
                      
                       if (scores[j] > (newfitness-fitness))
                            {scores[j] = newfitness-fitness;}
                        if (scores[j]<=0) {break;}
//                          System.out.println("New fitness node " + NodeY.getName() + " " + scores[j] );

                         }
                       if (scores[j]<=0) {break;}
                     }


            }
            
                   
            if((parents.getId(NodeY)>0) &&  (!NodeY.equals(bestNode))){
                  System.out.println("Testing removing " + NodeY.getName());
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                      
                     if( (!NodeZ.equals(NodeY))  && (!NodeZ.equals(bestNode))    ) {
                         
                         connect.insertNode(NodeZ);}
                    }
        removed = false;
              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
               
                while( (en.hasMoreElements()) && (!removed)) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars);
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}
                        if ((newfitness-fitness)<=0.0) {
                            System.out.println("Removing link between " + NodeY.getName() + " and " + NodeX.getName());
                           try{
                               dag.removeLink(NodeY, NodeX);
                               }
                 catch (InvalidEditException iee) {};
                    removed= true;
                        }

                         }
              if (removed) {break;}
                     }
      

            }
            
                   
             }
              best=0.0;
              
               System.out.println("Creating link between " + bestNode.getName() + " and " + NodeX.getName());
         try {
          dag.createLink(bestNode,NodeX);
           }
           catch (InvalidEditException iee) {};
           
              
        for(j=0;j<i;j++){
           if (scores[j] > best) {best = scores[j]; bestNode =(FiniteStates) nodes.elementAt(j); ibest=j;} 
            
        }       
              
     }
   
 
   
   
   
   }

}


public void PCremovePrior(Metrics metric){

   Graph dag;
   NodeList nodes,vars,parents,subSet,previous,connect;
   int i,j,k,ibest,n,l;
   Node NodeX,NodeY,NodeZ, bestNode;
   double  fitness, newfitness,best;
   double[] scores;
   boolean[][] matrix;
   Enumeration en;
   boolean removed;

   dag= (Graph) getOutput();
    if (!dag.isADag()){ System.out.println("no es dag");  }
   System.out.println(" Graph " + dag.toString());
    System.out.println("antes de topological order ");
   nodes = dag.topologicalOrder();
    System.out.println("despues de topological order ");

   scores = new double[nodes.size()];

   for(i=0; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     System.out.println("Refinando nodo " + NodeX.getName());
     parents = dag.parents(NodeX);
    //  System.out.println(dag.getLinkList().toString());
     System.out.println("Parentssize " + parents.size());
     for (j=0; j<parents.size(); j++){
        
             NodeY  = (FiniteStates) parents.elementAt(j);
              System.out.println("Removing parent " + NodeY.getName());
               try {
               
                dag.removeLink(NodeY,NodeX);
               }
                 catch (InvalidEditException iee) {};
     }

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

    

      vars = new NodeList();
     vars.insertNode(NodeX);
     fitness = metric.scoret(vars);

     best = 0.0;
     ibest=i;
     bestNode = new FiniteStates();
    for(j=0;j<i;j++){
         NodeY  = (FiniteStates) previous.elementAt(j);
         vars.insertNode(NodeY);
         newfitness = metric.scoret(vars)- Math.log((double) (i));
         scores[j] = newfitness - fitness;
         vars.removeNode(1);
         if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
    }

     while (best>0.0){
         best = -1.0;
         scores[ibest] = 0.0;
        
           
              parents = dag.parents(NodeX);
              
             for(j=0;j<i;j++){
                   NodeY  = (FiniteStates) previous.elementAt(j);
        //              System.out.println("Recomputing scores (1) " + NodeY.getName() );
                   if((scores[j]>0.0) &&  (j!=ibest)){
          //                   System.out.println("Recomputing scores (2) " + NodeY.getName() );
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       
                     
            //             System.out.println("Inserting node to test " + NodeZ.getName() );
                         connect.insertNode(NodeZ);}

              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
                while( (en.hasMoreElements())) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars) - Math.log(((double) (i-n-1))/(n+2.0));
                      
                       if (scores[j] > (newfitness-fitness))
                            {scores[j] = newfitness-fitness;}
                        if (scores[j]<=0) {break;}
//                          System.out.println("New fitness node " + NodeY.getName() + " " + scores[j] );

                         }
                       if (scores[j]<=0) {break;}
                     }


            }
            
                   
            if((parents.getId(NodeY)>0) &&  (!NodeY.equals(bestNode))){
                  System.out.println("Testing removing " + NodeY.getName());
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                      
                     if( (!NodeZ.equals(NodeY))  && (!NodeZ.equals(bestNode))    ) {
                         
                         connect.insertNode(NodeZ);}
                    }
        removed = false;
              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
               
                while( (en.hasMoreElements()) && (!removed)) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars) - Math.log(((double) (i-n-1))/(n+2.0));
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}
                        if ((newfitness-fitness)<=0.0) {
                            System.out.println("Removing link between " + NodeY.getName() + " and " + NodeX.getName());
                           try{
                               dag.removeLink(NodeY, NodeX);
                               }
                 catch (InvalidEditException iee) {};
                    removed= true;
                        }

                         }
              if (removed) {break;}
                     }
      

            }
            
                   
             }
              best=0.0;
              
               System.out.println("Creating link between " + bestNode.getName() + " and " + NodeX.getName());
         try {
          dag.createLink(bestNode,NodeX);
           }
           catch (InvalidEditException iee) {};
           
              
        for(j=0;j<i;j++){
           if (scores[j] > best) {best = scores[j]; bestNode =(FiniteStates) nodes.elementAt(j); ibest=j;} 
            
        }       
              
     }
   
 
   
   
   
   }

}
	
	
	
/* It aims to get an optimality condition making a search after PCPathremove
 * 
 * 
 */
		
public void refineoptimal(Metrics metric){

   Graph dag;
   NodeList nodes,vars,parents,subSet,previous,connect;
   int i,j,k,ibest,n,l;
   Node NodeX,NodeY,NodeZ, bestNode;
   double  fitness, newfitness,best;
   double[] scores;
   boolean[][] matrix;
   Enumeration en;

   dag= (Graph) getOutput();
    if (!dag.isADag()){ System.out.println("no es dag");  }
   System.out.println(" Graph " + dag.toString());
    System.out.println("antes de topological order ");
   nodes = dag.topologicalOrder();
    System.out.println("despues de topological order ");

   scores = new double[nodes.size()];

   for(i=0; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     System.out.println("Refinando nodo " + NodeX.getName());
     parents = dag.parents(NodeX);
    //  System.out.println(dag.getLinkList().toString());
     System.out.println("Parentssize " + parents.size());
     for (j=0; j<parents.size(); j++){
        
             NodeY  = (FiniteStates) parents.elementAt(j);
              System.out.println("Removing parent " + NodeY.getName());
               try {
               
                dag.removeLink(NodeY,NodeX);
               }
                 catch (InvalidEditException iee) {};
     }

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

     matrix = dag.computeDepMatrix(previous);

      vars = new NodeList();
     vars.insertNode(NodeX);
     fitness = metric.scoret(vars);

     best = 0.0;
     ibest=i;
     bestNode = (FiniteStates) nodes.elementAt(0);
    for(j=0;j<i;j++){
         NodeY  = (FiniteStates) nodes.elementAt(j);
         vars.insertNode(NodeY);
         newfitness = metric.scoret(vars);
         scores[j] = newfitness - fitness;
         vars.removeNode(1);
         if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
    }

     while (best>0.0){
         best = -1.0;
         scores[ibest] = 0.0;
         System.out.println("Creating link between " + bestNode.getName() + " and " + NodeX.getName());
         try {
          dag.createLink(bestNode,NodeX);
           }
           catch (InvalidEditException iee) {};
              parents = dag.parents(NodeX);
             for(j=0;j<i;j++){
                   NodeY  = (FiniteStates) nodes.elementAt(j);
        //              System.out.println("Recomputing scores (1) " + NodeY.getName() );
                   if((scores[j]>0.0) && (matrix[ibest][j])){
          //                   System.out.println("Recomputing scores (2) " + NodeY.getName() );
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if(matrix[l][j]){
            //             System.out.println("Inserting node to test " + NodeZ.getName() );
                         connect.insertNode(NodeZ);}

              for (n=1;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
                while( (en.hasMoreElements())) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.score(vars);
                      
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}
//                          System.out.println("New fitness node " + NodeY.getName() + " " + scores[j] );

                         }
                     }


            }
            }
                   
            if((parents.getId(NodeY)>0) && (matrix[ibest][j])&& (!NodeY.equals(bestNode))){
                  System.out.println("Testing removing " + NodeY.getName());
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if((matrix[l][j]) && (!NodeZ.equals(NodeY))  && (!NodeZ.equals(bestNode))    ) {
                         
                         connect.insertNode(NodeZ);}
                    }
        boolean removed = false;
              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
               
                while( (en.hasMoreElements()) && (!removed)) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars);
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}
                        if ((newfitness-fitness)<=0.0) {
                            System.out.println("Removing link between " + NodeY.getName() + " and " + NodeX.getName());
                           try{
                               dag.removeLink(NodeY, NodeX);
                               }
                 catch (InvalidEditException iee) {};
                    removed= true;
                        }

                         }
                     }


            }
            
                   
             }
              best=0.0;
        for(j=0;j<i;j++){
           if (scores[j] > best) {best = scores[j]; bestNode =(FiniteStates) nodes.elementAt(j); ;ibest=j;} 
            
        }       
              
     }
   
 /* OPtimality condition */
     
     boolean optimal = false;
     boolean loop = false;
     boolean add = true;
     double mbest;
     
     HashSet pastparents = new HashSet();
      parents = dag.parents(NodeX);
      parents.sortByNames();
      pastparents.add(parents.toString2());
      
      
      
      
     while ((!optimal) && (!loop)){
           optimal = true;
           best = 0.0;
     //      System.out.println("Starting best " + best);
            for(j=0;j<i;j++){
         NodeY  = (FiniteStates) nodes.elementAt(j);
       
           connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if((matrix[l][j]) && (j!=l)) {
            //             System.out.println("Inserting node to test " + NodeZ.getName() );
                         connect.insertNode(NodeZ);}
                    }

           if (parents.getId(NodeY) >-1) {
                  for (n=0;n<=connect.size();n++) {
                     en =connect.subSetsOfSize(n).elements();
               
                     while (en.hasMoreElements()) {
                       subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars);
                       if (best  < (-newfitness+fitness))
                           {best = -newfitness+fitness; bestNode =NodeY; optimal = false; add = false; ibest = j; 
  //                          System.out.println("Cahange best parent" + best);
                       }
               
           }
                  }
           }
           
           else {
               vars = new NodeList();
               vars.insertNode(NodeX); 
               fitness = metric.scoret(vars);
                  vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars);
                mbest = newfitness - fitness;
               if (mbest > best) {
                    System.out.println("Connect size " + connect.size());
                    for (n=0;n<=connect.size();n++) {
                     en =connect.subSetsOfSize(n).elements();
                     if (mbest <= best) {break;}
                     while ( (en.hasMoreElements()) && (mbest > best)) {
                       subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars);
                       if (mbest  > (newfitness - fitness)) {mbest = newfitness-fitness;
                        
                       }
               
           }
                  }
                   
                   
                   
               }
                
             if (mbest > best) {best =  mbest; optimal = false; add = true; bestNode =NodeY;
                 System.out.println("Cahange best non-parent" + best);
             }   
               
           }
         
         
            }
          if (!optimal) {
              System.out.println("Best " + best);
              if (add) {
                    try {
          dag.createLink(bestNode,NodeX);
          System.out.println("Adding arc (new) " + bestNode.getName() + " to " + NodeX.getName());
           }
           catch (InvalidEditException iee) {};
              }
              else {
                   try {
          dag.removeLink(bestNode,NodeX);
           System.out.println("removing arc " + bestNode.getName() + " to " + NodeX.getName());
           }
           catch (InvalidEditException iee) {};
              }
              
          parents = dag.parents(NodeX);
          parents.sortByNames();
          
          if (pastparents.contains(parents.toString2())) {loop = true;}
          else {pastparents.add(parents.toString2());}
              
              
          }
            
            
            
            
     }
   
   
   
   }

}


	
/* It aims to get an optimality condition making a search after PCPathremove
 * 
 * 
 */
		
public void refineoptimalPrior(Metrics metric){

   Graph dag;
   NodeList nodes,vars,parents,subSet,previous,connect;
   int i,j,k,ibest,n,l;
   Node NodeX,NodeY,NodeZ, bestNode;
   double  fitness, newfitness,best;
   double[] scores;
   boolean[][] matrix;
   Enumeration en;

   dag= (Graph) getOutput();
    if (!dag.isADag()){ System.out.println("no es dag");  }
   System.out.println(" Graph " + dag.toString());
    System.out.println("antes de topological order ");
   nodes = dag.topologicalOrder();
    System.out.println("despues de topological order ");

   scores = new double[nodes.size()];

   for(i=0; i< nodes.size(); i++){
     NodeX = (FiniteStates) nodes.elementAt(i);
     System.out.println("Refinando nodo " + NodeX.getName());
     parents = dag.parents(NodeX);
    //  System.out.println(dag.getLinkList().toString());
     System.out.println("Parentssize " + parents.size());
     for (j=0; j<parents.size(); j++){
        
             NodeY  = (FiniteStates) parents.elementAt(j);
              System.out.println("Removing parent " + NodeY.getName());
               try {
               
                dag.removeLink(NodeY,NodeX);
               }
                 catch (InvalidEditException iee) {};
     }

     previous = new NodeList();
        for (j=0;j<i;j++){

          NodeY  = (FiniteStates) nodes.elementAt(j);
          previous.insertNode(NodeY);

        }

     matrix = dag.computeDepMatrix(previous);

      vars = new NodeList();
     vars.insertNode(NodeX);
     fitness = metric.scoret(vars);

     best = 0.0;
     ibest=i;
     bestNode = (FiniteStates) nodes.elementAt(0);
    for(j=0;j<i;j++){
         NodeY  = (FiniteStates) nodes.elementAt(j);
         vars.insertNode(NodeY);
         newfitness = metric.scoret(vars)- Math.log((double) (i));
         scores[j] = newfitness - fitness;
         vars.removeNode(1);
         if (scores[j] > best) {best = scores[j]; bestNode = NodeY;ibest=j;}
    }

     while (best>0.0){
         best = -1.0;
         scores[ibest] = 0.0;
         System.out.println("Creating link between " + bestNode.getName() + " and " + NodeX.getName());
         try {
          dag.createLink(bestNode,NodeX);
           }
           catch (InvalidEditException iee) {};
              parents = dag.parents(NodeX);
             for(j=0;j<i;j++){
                   NodeY  = (FiniteStates) nodes.elementAt(j);
        //              System.out.println("Recomputing scores (1) " + NodeY.getName() );
                   if((scores[j]>0.0) && (matrix[ibest][j])){
          //                   System.out.println("Recomputing scores (2) " + NodeY.getName() );
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if(matrix[l][j]){
            //             System.out.println("Inserting node to test " + NodeZ.getName() );
                         connect.insertNode(NodeZ);}

              for (n=1;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
                while( (en.hasMoreElements())) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.score(vars)  - Math.log(((double) (i-n-1))/(n+2.0))  ;
                      
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}
//                          System.out.println("New fitness node " + NodeY.getName() + " " + scores[j] );

                         }
                     }


            }
            }
                   
            if((parents.getId(NodeY)>0) && (matrix[ibest][j])&& (!NodeY.equals(bestNode))){
                  System.out.println("Testing removing " + NodeY.getName());
                    connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if((matrix[l][j]) && (!NodeZ.equals(NodeY))  && (!NodeZ.equals(bestNode))    ) {
                         
                         connect.insertNode(NodeZ);}
                    }
        boolean removed = false;
              for (n=0;n<=connect.size();n++) {
               en =connect.subSetsOfSize(n).elements();
               
                while( (en.hasMoreElements()) && (!removed)) {
                     subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.insertNode(bestNode);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars) - Math.log(((double) (i-n-1))/(n+2.0));
                       if (scores[j] > (newfitness-fitness))
{scores[j] = newfitness-fitness;}
                        if ((newfitness-fitness)<=0.0) {
                            System.out.println("Removing link between " + NodeY.getName() + " and " + NodeX.getName());
                           try{
                               dag.removeLink(NodeY, NodeX);
                               }
                 catch (InvalidEditException iee) {};
                    removed= true;
                        }

                         }
                     }


            }
            
                   
             }
              best=0.0;
        for(j=0;j<i;j++){
           if (scores[j] > best) {best = scores[j]; bestNode =(FiniteStates) nodes.elementAt(j); ;ibest=j;} 
            
        }       
              
     }
   
 /* OPtimality condition */
     
     boolean optimal = false;
     boolean loop = false;
     boolean add = true;
     double mbest;
     
     HashSet pastparents = new HashSet();
      parents = dag.parents(NodeX);
      parents.sortByNames();
      pastparents.add(parents.toString2());
      
      
      
      
     while ((!optimal) && (!loop)){
           optimal = true;
           best = 0.0;
     //      System.out.println("Starting best " + best);
            for(j=0;j<i;j++){
         NodeY  = (FiniteStates) nodes.elementAt(j);
       
           connect = new NodeList();
                    for (k=0;k<parents.size();k++){
                       NodeZ = (Node) parents.elementAt(k);
                       l = nodes.getId(NodeZ);
                     if((matrix[l][j]) && (j!=l)) {
            //             System.out.println("Inserting node to test " + NodeZ.getName() );
                         connect.insertNode(NodeZ);}
                    }

           if (parents.getId(NodeY) >-1) {
                  for (n=0;n<=connect.size();n++) {
                     en =connect.subSetsOfSize(n).elements();
               
                     while (en.hasMoreElements()) {
                       subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars) - Math.log(((double) (i-n))/(n+1.0));
                       if (best  < (-newfitness+fitness))
                           {best = -newfitness+fitness; bestNode =NodeY; optimal = false; add = false; ibest = j; 
  //                          System.out.println("Cahange best parent" + best);
                       }
               
           }
                  }
           }
           
           else {
               vars = new NodeList();
               vars.insertNode(NodeX); 
               fitness = metric.scoret(vars);
                  vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars) - Math.log((double) (i))  ;
                mbest = newfitness - fitness;
               if (mbest > best) {
                    System.out.println("Connect size " + connect.size());
                    for (n=0;n<=connect.size();n++) {
                     en =connect.subSetsOfSize(n).elements();
                     if (mbest <= best) {break;}
                     while ( (en.hasMoreElements()) && (mbest > best)) {
                       subSet = (NodeList)en.nextElement();
                       vars = new NodeList();
                       vars.insertNode(NodeX);
                       vars.join(subSet);
                       fitness = metric.scoret(vars);
                       vars.insertNode(NodeY);
                        newfitness = metric.scoret(vars)  - Math.log(((double) (i-n))/(n+1.0))  ;
                       if (mbest  > (newfitness - fitness)) {mbest = newfitness-fitness;
                        
                       }
               
           }
                  }
                   
                   
                   
               }
                
             if (mbest > best) {best =  mbest; optimal = false; add = true; bestNode =NodeY;
                 System.out.println("Cahange best non-parent" + best);
             }   
               
           }
         
         
            }
          if (!optimal) {
              System.out.println("Best " + best);
              if (add) {
                    try {
          dag.createLink(bestNode,NodeX);
          System.out.println("Adding arc (new) " + bestNode.getName() + " to " + NodeX.getName());
           }
           catch (InvalidEditException iee) {};
              }
              else {
                   try {
          dag.removeLink(bestNode,NodeX);
           System.out.println("removing arc " + bestNode.getName() + " to " + NodeX.getName());
           }
           catch (InvalidEditException iee) {};
              }
              
          parents = dag.parents(NodeX);
          parents.sortByNames();
          
          if (pastparents.contains(parents.toString2())) {loop = true;}
          else {pastparents.add(parents.toString2());}
              
              
          }
            
            
            
            
     }
   
   
   
   }

}


 
    /*******************************************+
     * This is equivalent to the K2 algorithm with a known order + a search algorithm
     * @param metric
     */
 
public double computeMarkovFrontier(Node NodeX, NodeList Candidates, NodeList Frontier){
    
   
    NodeList vars; 
    double fitness, fitnessNew; 
    Node NodeY;
    int j;
    
      vars = new NodeList();
      vars.insertNode(NodeX);
      
      fitness = metric.scoret(vars);
      improv=true;
      best=0;
      while(improv){
       improv=false;
       for(j=0;j<Candidates.size(); j++){
          NodeY  = (FiniteStates) Candidates.elementAt(j);
           //     System.out.println("Comprobando nodo " + NodeY.getName());
          pos = vars.getId(NodeY);
          if(pos==-1) { 
                //   System.out.println("Nodo no padre");
              vars.insertNode(NodeY);
              //     System.out.println("Computing score " + vars.size());
              fitnessNew = metric.scoret(vars);
               //    System.out.println("Score finished");
              if (fitnessNew > fitness){
                //   System.out.println("Score mejorado");
                  fitness = fitnessNew;
                  improv=true;
                  
                   add=true;
                  best=j;
              }
              
                vars.removeNode(NodeY); 
           
          }
          else {
             //    System.out.println("Nodo padre");
            vars.removeNode(NodeY);
             //      System.out.println("Computing score " + vars.size());
             fitnessNew = metric.scoret(vars);
              //     System.out.println("Score finished");
              if (fitnessNew > fitness){
              //         System.out.println("Score mejorado" + NodeY.getName());
                  fitness = fitnessNew;
                  improv=true;
                  add=false;
                   best = j;
              }
              vars.insertNode(NodeY);
              
          }
       }
       if (improv){
          // System.out.println("BEst thing " + best);
           if (add){
               NodeY = (FiniteStates) nodes.elementAt(best);
               vars.insertNode(NodeY);
             //   System.out.println(NodeY.getName());
           
       
           }
           else {
              NodeY = (FiniteStates) nodes.elementAt(best);
              vars.removeNode(NodeY); 
             // System.out.println(NodeY.getName()+NodeX.getName());
             
           }
           
       }
       
      }

      Frontier = vars; 
      
return (fitness);

}
      



} // PCLearning

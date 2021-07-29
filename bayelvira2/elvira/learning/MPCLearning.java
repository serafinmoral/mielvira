/*
 * MPCLearning.java
 *
 * Created on 6 de noviembre de 2006, 16:33
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package elvira.learning;


import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

import java.util.Date;

import elvira.*;
import elvira.potential.*;
import elvira.database.DataBaseCases;
import elvira.parser.ParseException;

         
   public class MPCLearning extends PCLearning {
    
    
 
  public static void main(String args[]) throws ParseException, IOException { 

      BDeMetrics metric;
      Bnet net, baprend;
      FileWriter f2;
      int method;
      net = null;
      if(args.length < 4){
	  System.out.println("too few arguments: Usage: file.dbc numberCases file.elv method (for saving the result) [file.elv (true net to be compared)]");
	  System.exit(0);
      }


      FileInputStream f = new FileInputStream(args[0]);
      DataBaseCases cases = new DataBaseCases(f);
      cases.setNumberOfCases(Integer.valueOf(args[1]).intValue());
      method = (Integer.valueOf(args[3]).intValue());
      metric = new BDeMetrics(cases);
      if(args.length > 4){ 
          FileInputStream fnet = new FileInputStream(args[4]);
          net = new Bnet(fnet); 
      } 
      MPCLearning outputNet2 = new MPCLearning(cases,method);
      outputNet2.setLevelOfConfidence(0.99);
      outputNet2.learning();
     System.out.println("Fin de aprender. Paso a estimar parametros");
      DELearning outputNet3 = new DELearning(cases,outputNet2.getOutput());
      outputNet3.learning(2.0) ;
      f2 = new FileWriter(args[2]);
       System.out.println("Fin de aprender parametros ");
      baprend = (Bnet)outputNet3.getOutput();
      baprend.saveBnet(f2);
      f2.close();
 
       System.out.println("tiempo consumido"+outputNet2.delay);
      if(args.length > 3){
	  FileInputStream fnet = new FileInputStream(args[4]);
	  net = new Bnet(fnet);
          double d2 = net.KL(baprend);
          System.out.println("Divergencia KL de la red aprendida - real " + d2);
	  LinkList addel[] = new LinkList[3];
	  addel = outputNet2.compareOutput(net);
	  System.out.print("\nNumero de arcos a�adidos: "+addel[0].size());
	  System.out.print(addel[0].toString());
	  System.out.print("\nNumero de arcos borrados: "+addel[1].size());
	  System.out.print(addel[1].toString());
	  System.out.println("\nNumero de arcos mal orientados: "+addel[2].size());
	  System.out.print(addel[2].toString());
	  System.out.print("\nArcos no orientados: ");
	  System.out.print(outputNet2.linkUnOriented().toString());
      }  

      
  
  }      
    
  

public MPCLearning(){
	
    setInput(null);
    setOutput(null);
}

    /** 
     * Initializes an empty unidirected graph with the variables contained into
     * the Data Base of Cases cases. Also initializes the input of algorithm
     * PC as a Data Base of Cases and the PC output as a Bnet with the above
     * graph. 
     * @param DataBaseCases cases. The data bases of discrete cases.
     */    


public MPCLearning(DataBaseCases cases) {

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
	       dag.addNode(nodes.elementAt(i));
	    }
	    catch (InvalidEditException iee) {};

   
    
    this.input = cases;
    setOutput(b);
    setLevelOfConfidence(0.99);
    
} 


    /** 
     * Initializes an empty unidirected graph with the variables contained into
     * the List of Nodes nodes. Also initializes the input of algorithm
     * PC as a Data Base of Cases and the PC output as a Bnet with the above
     * graph. It's very important that the variables contained in nodes are
     * a subset of the variables contained in the Data Bases of Cases.
     * @see DataBaseCases - method getVariables();
     * @param DataBaseCases cases. The data bases of discrete cases.
     * @param NodeList nodes. Must be a subset of variables of the cases.
     */    


public MPCLearning(DataBaseCases cases, NodeList nodes) {

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
          dag.addNode(nodes.elementAt(i));
       }
       catch (InvalidEditException iee) {};
    
  
    
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



public MPCLearning(Graph input) {

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
	       dag.addNode(nodes.elementAt(i));
	    }
	    catch (InvalidEditException iee) {};
    //links = new LinkList();
    
  
   
    this.input=input;
    setOutput(b);
    setLevelOfConfidence(0.99);
    
}

    /**
     * see  PCLearning(Graph input) and 
     * also PCLearning(DataBaseCases cases, NodeList nodes)
     * @param BDeMetrics input Metrics used for the conditional independence tests
     * @param NodeList nodes.
     */

public MPCLearning(BDeMetrics input, NodeList nodes) {

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
	      dag.addNode(nodesInput.elementAt(i));
	   }
	   catch (InvalidEditException iee) {};

  
    
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

public MPCLearning(ConditionalIndependence input, NodeList nodes) {

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
	      dag.addNode(nodesInput.elementAt(i));
	   }
	   catch (InvalidEditException iee) {};

  
    
    this.input = input;
    setOutput(b);
    setLevelOfConfidence(0.99);
}



public MPCLearning(DataBaseCases cases, NodeList nodes, int method) {

     this(cases,nodes);
     
     ConditionalIndependence input;
    
    switch (method){
        case 0: {}
        case 1: {this.input = new BDeMetrics(cases);}
    }

    
    
}


public MPCLearning(DataBaseCases cases, int method) {
    this(cases);
     
     ConditionalIndependence input;
   
     
    switch (method){
        case 0: {System.out.println("M�todo tradicional");break;}
      case 1: {this.input = new BDeMetrics(cases,1.0);}
    }

    
    
}



  
    
 
public void learning() {
    
 double[][] depmatrix;    
 Graph dag;
 double depdegree,maxdep;
 int i,j,n,pos,nnodes,maxi,maxj,posz,m;
 NodeList[] added,candidates;
  
 Link link;
 FiniteStates maxX,maxY;
 Vector index,subSetsOfnElements;
 Enumeration en;
 boolean encontrado=false,directed=false;
 Date D = new Date();
 delay = (double) D.getTime();
 Hashtable sepSet;
 FiniteStates nodeX, nodeY, nodeZ, nodeR, nodeH;
 NodeList subSet, nodespath, newnodespath;
  NodeList adyacenciesX,
     minCut;
 boolean found,ok;
 
nnodes =  getOutput().getNodeList().size();
added = new NodeList[nnodes];
candidates = new NodeList[nnodes];
 for(i=0 ; i< nnodes;i++){
 added[i] = new NodeList();
 candidates[i] = new NodeList();
 
       
 }
 
 
   
     dag = (Graph)getOutput();
     depmatrix = new double[nnodes][nnodes];
  maxX=new FiniteStates();
                   maxY=new FiniteStates();
                   maxi= 0;
                   maxj=0; 
maxdep=-1.0;

 index = new Vector();
 for(i=0 ; i< nnodes;i++){
       sepSet = new Hashtable();
     
  index.addElement(sepSet);
 }
 
 for(i=0 ; i< nnodes;i++){
 
 nodeX=(FiniteStates)(dag.getNodeList()).elementAt(i);
  for (j=i+1 ; j< nnodes ;j++){
      nodeY=(FiniteStates)(dag.getNodeList()).elementAt(j);
  
       subSet = new NodeList();
       depdegree      =input.getDep((Node) nodeX, (Node) nodeY,subSet);
        System.out.print("\n I( "+nodeX.getName()+" , "+nodeY.getName()+" | "+subSet.toString2()+") : "+depdegree+"\n");
       if (depdegree > maxdep) 
                 { maxdep = depdegree;
                   maxX=nodeX;
                   maxY=nodeY;
                   maxi= i;
                   maxj=j; }
      
        depmatrix[i][j] = depdegree;
        
        if (depdegree <= 0.0) {
                      
				 pos = dag.getNodeList().getId(nodeX);
				 sepSet = (Hashtable)index.elementAt(pos);
				 sepSet.put(nodeY,subSet);
				 pos = dag.getNodeList().getId(nodeY);
				 sepSet = (Hashtable)index.elementAt(pos);
				 sepSet.put(nodeX,subSet);
                                 
        }
        else {
            candidates[i].insertNode(nodeY);
            candidates[j].insertNode(nodeX);
        }
      
  }
 }
 
 
 
 while(maxdep>0.0){
     
      candidates[maxi].removeNode(maxY);
      candidates[maxj].removeNode(maxX);
      System.out.print("Adding arc " + maxX.getName()+" , "+ maxY.getName() + "\n");
    
      for (j=0; j<nnodes; j++){
          
         nodeR =  (FiniteStates)(dag.getNodeList()).elementAt(j);
           System.out.print("Testing Node " + nodeR.getName()+ " ");
         i = 0;
         while (candidates[j].size()>i){
             nodeZ = (FiniteStates) candidates[j].elementAt(i);  
             System.out.print("With Node " + nodeZ.getName()+ "\n");
              encontrado = false;
               nodespath= new NodeList();
                newnodespath= new NodeList();
                
                if (dag.isThereMixedPath(maxY,nodeZ)){ 
               
                
                 for (n=0 ; n < added[j].size();  n++)
               {   nodeH = (FiniteStates) added[j].elementAt(n);
                     if (dag.isThereMixedPath(nodeZ,nodeH)){
                            if ( nodespath.getId(nodeH) ==-1    )      nodespath.insertNode(nodeH);
                      }
                     else {
                       
                        if (dag.isThereMixedPath(maxX,nodeH)){
                        if (     newnodespath.getId(nodeH) ==-1    )    newnodespath.insertNode(nodeH);
                      }
                       
                     }
               }
                 
                if (nodeR == maxX) {
                 if (     newnodespath.getId(maxY) == -1   )    newnodespath.insertNode(maxY); 
                }
                 
                }
                
                     if (dag.isThereMixedPath(maxX,nodeZ)){ 
               
                
                 for (n=0 ; n < added[j].size();  n++)
               {   nodeH = (FiniteStates) added[j].elementAt(n);
                     if (dag.isThereMixedPath(nodeZ,nodeH)){
                      ;
                      }
                     else {
                       
                        if (dag.isThereMixedPath(maxY,nodeH)){
                     if (     newnodespath.getId(nodeH) ==-1   )   newnodespath.insertNode(nodeH);
                      }
                       
                     }
               }
                 
                 
                   if (nodeR == maxY) {
              if (     newnodespath.getId(maxX) ==-1   ) newnodespath.insertNode(maxX); 
                }
                 
                }
                
              if (newnodespath.size()>0){
               
                  System.out.println(" Si Prueba");
                  
                  
                    for (n=1 ; n < newnodespath.size();  n++)
                    
        
          { 
              subSet = new NodeList();
                nodeX = (FiniteStates) newnodespath.elementAt(n);  
                subSet.insertNode(nodeX);
              
                   
              depdegree      =input.getDep((Node) nodeR, (Node) nodeZ, subSet);
               System.out.print("\n I( "+nodeR.getName()+" , "+nodeZ.getName()+" | "+subSet.toString2()+") : "+depdegree+"\n");
              posz = dag.getNodeList().getId(nodeZ);
                 if (j<posz){ 
                   if (depmatrix[j][posz] > depdegree) {
                         depmatrix[j][posz] = depdegree;
                   }
                 }
                 else{ 
                 if (depmatrix[posz][j] > depdegree) {
                     depmatrix[posz][j] = depdegree;
                 }
                 }
                 if (depdegree<=0.0){
                     candidates[j].removeNode(i);
                     candidates[posz].removeNode(nodeR);
                     sepSet = (Hashtable) index.elementAt(j);
		     sepSet.put(nodeZ,subSet);
	             sepSet = (Hashtable)index.elementAt(posz);
		     sepSet.put(nodeR,subSet);
                     encontrado = true;   
                 }
            
            
           if (encontrado) {break;}    
            
      }
         
       
      
            
         
                  
              }  
                
                
          
             
                
              if (!encontrado) {i++;}   
                    
                   
         }
           
          
          
      }
      
      
      
     
      
        try {
	       dag.createLink(maxX, maxY, directed);
                
	    }
	    catch (InvalidEditException iee) {};
      added[maxi].insertNode(maxY);
      added[maxj].insertNode(maxX);
     
     depmatrix[maxi][maxj] = -1.0;
     
     maxdep=-1.0;
     for (i=0;i<nnodes;i++){
         for (j=i+1;j<nnodes;j++){
             if (depmatrix[i][j]>maxdep) {
                 maxdep = depmatrix[i][j];
                 maxX=(FiniteStates)(dag.getNodeList()).elementAt(i);
                   maxY=(FiniteStates)(dag.getNodeList()).elementAt(j);
                   maxi= i;
                   maxj=j;
             }
             
         }
         
     }
     
 }  
     
 
 
 for (n=1 ; n <= dag.maxOfAdyacencies() ; n++)
     for (i=0 ; i<(dag.getNodeList()).size();i++){
             
		 nodeX=(FiniteStates)(dag.getNodeList()).elementAt(i);
		 adyacenciesX=dag.neighbours(nodeX);
                 m = adyacenciesX.size();
                 
	 for (j=i+1 ; j< (dag.getNodeList()).size() ;j++){
	       {
		nodeY=(FiniteStates)(dag.getNodeList()).elementAt(j);
		 encontrado=false;
		 pos = adyacenciesX.getId(nodeY);
                 if (!(pos==-1)){
		 System.out.println("Nodes: "+nodeX.getName()+", "
				  +nodeY.getName()+" Step: "+n);
		 link=getOutput().getLinkList().getLinks(nodeX.getName(),
							 nodeY.getName());
		 if(link==null)
		     link=getOutput().getLinkList().getLinks(nodeY.getName()
							     ,nodeX.getName());
		 
		 // they are adyacent.
                   
                  try {
	       dag.removeLink(link);
	    }
	    catch (InvalidEditException iee) {};
          
                   minCut = dag.minimunDSeparatingSet(  nodeX, nodeY);
                            ok=false;
                         //        System.out.println("Tama�o de corte " + minCut.size());
		     if(minCut.size() >= n){
			 subSetsOfnElements=minCut.subSetsOfSize(n);
			 en = subSetsOfnElements.elements();
                
			 while((!encontrado)&&(en.hasMoreElements()||n==0)){
			     if(n==0) subSet = new NodeList();
			     else subSet = (NodeList)en.nextElement();
			     ok=input.independents(nodeX,nodeY,subSet,levelOfConfidence);
			//     System.out.print("\n I( "+nodeX.getName()+" , "+nodeY.getName()+" | "+subSet.toString2()+") : "+ok+"\n");
			     if(ok){
				 
						     
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
                   if(!ok) {
                        try {
	     dag.createLink(nodeX,nodeY,false);
	    }
	    catch (InvalidEditException iee) {};
                      }
		 
	     }
	 }
         }
     }

 
 
 
 
 headToHeadLink((Graph)dag,index);
 remainingLink((Graph)dag,index);
 extendOutput();
 D = new Date();
 delay = (((double)D.getTime()) - delay) / 1000;
 
    
    
}

  
   }
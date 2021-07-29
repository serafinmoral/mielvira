/* Bnet.java */

import java.util.Vector;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

/**
 * This class implements the structure for store and 
 * manipulate the Bayessian Networks (Bnets)
 *
 * @since 13/9/99 
 */


public class Bnet extends Network {

   public static final String ABSENT = "Absent";
   public static final String PRESENT = "Present";   

public static void main(String args[]) throws ParseException, IOException {
Bnet b;
int nparents, nnodes,ncases;
Random generator;
FileWriter f;



 if (args.length<4) {
    System.out.print("Too few arguments. Arguments are: file,number of nodes,number of cases,number of parents");
 }
 else { f = new FileWriter(args[0]);
	
           nnodes = (Integer.valueOf(args[1])).intValue();
	   ncases = (Integer.valueOf(args[2])).intValue();
	   nparents = (Integer.valueOf(args[3])).intValue();
	   generator = new Random();
	   b = new Bnet(generator,nnodes,nparents,ncases,true,5);
	   b.saveBnet(f);
	   f.close();}
}
   /** 
    * Creates a new empty Bnet object 
    */
    
   public Bnet () {
      super();
   }


   /**
    * Creates a Network parsing it from a file
    * 
    * @param f File that contains the Bnet
    */

   public Bnet (FileInputStream f) throws ParseException ,IOException {   
      
      BayesNetParse parser = new BayesNetParse(f);
      parser.initialize();
 
      parser.CompilationUnit();
      translate(parser);  
   }

   
   /**
    * Get a Bnet from the URL given as parameter    
    */
  
   public Bnet (URL url) throws IOException, ParseException {
      
      InputStream istream=url.openStream();
      BayesNetParse parser = new BayesNetParse(istream);
      parser.initialize();
 
      parser.CompilationUnit();
      translate(parser);      
  }
   
    /**
     * This constructor generates a random Bnet with discrete nodes of finite
     * states. it generates relations as uniform PotentialTables.
     * @see Graph#Graph(Random,int,double,boolean)
     * @param double. nStates. average number of states for each node.
     * @param degreeOfExtreme
     */

  public Bnet (Random generator, int numberOfNodes, double nParents, 
       double nStates,boolean conex,int degreeOfExtreme){

     PotentialTable potentialTable;
     Relation relation;
     Vector relations;
     NodeList pa,nodes;
     FiniteStates node;
     Vector states;
     int i;
     Graph g = new Graph(generator,numberOfNodes,nParents,conex);
     setNodeList(g.getNodeList());
     setLinkList(g.getLinkList());
     relations = new Vector();
     
     for(i=0 ; i< getNodeList().size() ;i++){
	 node = (FiniteStates)getNodeList().elementAt(i);
//	 states = randomStates(generator,nStates);
	states = exactStates(nStates);
	 node.setStates(states);
	 node.setTitle("");
	 node.setComment("");
	 node.setPosX(0);
	 node.setPosY(0);
	 node.setTypeOfVariable("finite-states");
	 node.setKindOfNode("chance");
       }

     for(i=0 ; i< getNodeList().size() ; i++){
	 nodes = new NodeList();
	 node = (FiniteStates)getNodeList().elementAt(i);
	 nodes.insertNode(node);
	 pa = g.parents(node);
	 nodes.join(pa);
	 relation = new Relation();
	 relation.setVariables(nodes);
	 potentialTable = new PotentialTable(generator,nodes,degreeOfExtreme);
	 relation.setValues(potentialTable);
	 relations.addElement(relation);
     }

     setRelationList(relations);

      setName("");
      setTitle("");
      setComment("");
      setAuthor("");
      setWhoChanged("");
      setWhenChanged("");
      setLocked(false);
      setVersion((float)1.0);
      setFSDefaultStates(FiniteStatesDefaultStates);
	 
 } 



   /**
    * Stores the Bnet in the file given as parameter
    * 
    * @param f File where the Bnet is saved
    * @see Network#save
    */

   public void saveBnet(FileWriter f) throws IOException { 
      
      PrintWriter p;
  
      p = new PrintWriter(f);
  
      p.print("// Bayesian Network\n");
      p.print("//   Elvira format \n\n"); 
      p.print("bnet  "+getName()+" { \n\n");
  
      save (p);   
      p.print ("}\n");      
   }

   
   
  /**
   * Check if the addition of a new link yields a cycle
   * 
   * @param tail Tail of the new link 
   * @param head Head of the new link
   * @return True if there is a cycle 
   * False in other case
   */
  
   public boolean hasCycle(Node tail, Node head) {
      
      LinkList ll=new LinkList ();
      Link link=new Link(tail,head);
      Graph g;

      ll.setLinks((Vector) getLinkList().getLinks().clone());
      ll.insertLink(link);
      g = new Graph(getNodeList(),ll,DIRECTED);

      return (!(g.isADag()));
   }   



   private Vector exactStates(double nStates){
     Vector aux;
     int i;    

    aux = new Vector();

    for(i=0;i< nStates; i++)
      { String nameState = new String("s"+i);
        aux.addElement(nameState);
      }
    return aux;
}

    private Vector randomStates(Random generator,double nStates){

	int x,i;
	Vector aux;
	double Xi;
	double suma;
	double tem,TR,den,div;

	suma = 0.0;
	tem = (double) (-1.0/nStates);
	den = generator.nextDouble();
	Xi = tem * Math.log(den);
	suma = suma + Xi;
	for(x=1;suma<1;x++){
	    den = generator.nextDouble();
	    Xi = tem * Math.log(den);
	    suma = suma + Xi;
	}             
	
	if((x-1)<2) x=3;
	aux = new Vector();

	for (i=0;i<x-1;i++){
	    String nameState = new String("s"+i);
	    aux.addElement(nameState);
	}
	return aux;
    }
     
} // end of Bnet.java


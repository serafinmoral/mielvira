/* Graph.java */

import java.util.Vector;
import java.util.Enumeration;
import java.io.*;
import java.util.Hashtable;
import java.util.Random;

/**
 * A graph is a set of nodes and a set of links among them.
 * We allow at most one link for each paif of nodes.
 *
 * since 9/11/99
 */

public class Graph implements Cloneable, ConditionalIndependence{
   
   
   /* Kinds of Graphs */
   public static final int DIRECTED = 0;
   public static final int UNDIRECTED = 1;
   public static final int MIXED = 2;

   private int kindOfGraph;	   
   private NodeList nodeList;		
   private LinkList linkList;		
   
   
   /**
    * Inicialices all the variables to empty. The links of
    * the graph are directed
    */

   public Graph() {

      kindOfGraph = DIRECTED; 		/* by default directed */
      nodeList = new NodeList();
      linkList = new LinkList();
   }


   /** 
    * Build a graph with the given lists of nodes and links. 
    * The kind of the graph is given as parameter too.
    */

   public Graph (NodeList nodes, LinkList links, int kind) {
      kindOfGraph = kind;
      nodeList = nodes;
      linkList = links;
   }
   
   
   /**
    * Create a new empty graph whose kind is given as parameter
    */
   
   public Graph (int kind) {
      kindOfGraph = kind;
      nodeList = new NodeList();
      linkList = new LinkList();
   }

    /**
     * Create a new Random Graph with a given number of nodes.
     * @param generator. The random generator.
     * @param int. number of nodes.
     * @param double. average of parents for each node.
     * @param boolean. conex. true if we want a conex graph.
     */


   public Graph(Random generator, int numberOfNodes, double nParents,
		boolean conex){
	
	Node node,nodep,nodeh;
	Link link;
	NodeList pa,nodesConex,total;
	Vector conexos = new Vector();
	Vector others;
	SetVectorOperations S = new SetVectorOperations();
	int i,j;

	kindOfGraph = DIRECTED;             /* by default directed */
        nodeList = new NodeList();
	linkList = new LinkList();
	for(i=0;i<numberOfNodes;i++){
	    node = new FiniteStates(2);
	    node.setName("x"+i);
	    nodeList.insertNode(node);
	    
	    // ahora se genera los padres aleatorios del node x.
	    
	    pa = randomParents(generator,node,nParents);
	    for(j=0 ; j < pa.size() ; j++){
		nodep = (Node)pa.elementAt(j);
		link = new Link(nodep,node);
		linkList.insertLink(link);
	    }
	}

	// Ahora miramos las componentes conexas del grafo. 
	// Se almacenan en el vector conexos.
	
	if (conex){
	    total = new NodeList();
	    nodesConex = new NodeList();
	    undirectedAccessibles((Node)nodeList.elementAt(0),nodesConex);
	    total.join(nodesConex);
	    while ( nodesConex != null) {
		conexos.addElement(nodesConex);
		others = S.notIn(nodeList.toVector(),total.toVector());
		if(others.size()> 0){
		    node = (Node)others.elementAt(0);
		    nodesConex = new NodeList();
		    undirectedAccessibles(node,nodesConex);
		    total.join(nodesConex);
		} else nodesConex = null;
		
	    }
	    
	    // Ahora unimos las componentes conexas del grafo.
	    
	    for (i=0 ; i< conexos.size()-1 ; i++){
		nodep = (Node)((NodeList)conexos.elementAt(i)).elementAt(0);
		nodeh = (Node)((NodeList)conexos.elementAt(i+1)).elementAt(0);
		link = new Link(nodep,nodeh);
		linkList.insertLink(link);
	    }
	}
    }
	




/* ********************* Accesing methods ******************** */

   /**
    * Used to access to KindOfGraph value
    */
   
   public int getKindOfGraph() {
      return kindOfGraph;
   }


   /**
    * Gets the list of Nodes of the graph
    * 
    * @return A list with the nodes of the graph
    */

   public NodeList getNodeList() {
      return nodeList;
   }


   /**
    * Gets the list of links of the Graph
    * 
    * @return A LinkList with all the links of the Graph
    */
   
   public LinkList getLinkList() {
      return linkList;
   }



/* ********************* Modifiers ******************** */
   
   
   public void setNodeList (NodeList nodes) {
      nodeList = nodes;
   }
   
   public void setLinkList (LinkList links) {
      linkList = links;
   }
   


/* ************* Add / Remove nodes and arcs ************ */

   /**
    * Insert a node in the graph
    * @param n the node to insert.
    */

   public void addNode(Node n) {
      nodeList.insertNode(n);
   }


   /**
    * Dettach a node and its adjacent links from the graph 
    * without destroying the node.
    * 
    * @param n the node to remove.
    */

   public void removeNode(Node node) {
      nodeList.removeNode(node);
      
      // now Remove the links of the node
      for (int i=0; i<linkList.size(); i++) {
         Link link = linkList.elementAt(i);
         if ((link.getHead() == node) || (link.getTail()== node))
            removeLink(link);
      }
   }


   /**
    * Dettach a node and its adjacent links from the graph
    * without destroying the node.
    *
    * @param position the position of the node to be removed.
    * @see#removeNode(Node)
    */

   public void removeNode(int position) {

      nodeList.removeNode(nodeList.elementAt(position));
   }


   /** 
    * Add a new link, between head and tail, in the link 
    * list. The kind of the link is set by the directed parameter
    *
    * @param head first node of the link 
    * @param tail second node of the link
    * @param directed kind of node
    */

   public void createLink (Node head, Node tail, boolean directed) {
      linkList.insertLink (new Link (head, tail, directed));
   }   
   
   
   /** 
    * Add a new link, between head and tail, in the link 
    * list. By default the link is directed
    */
   
   public void createLink (Node head, Node tail) {
      if (kindOfGraph == UNDIRECTED) 
         createLink (head, tail, false);
      else
         createLink (head, tail, true);
   }

   
   /**
    * Dettach a link the graph without destroying it.
    * 
    * @param l Link to remove
    */
   
   public void removeLink (Link l) {      
      linkList.removeLink (l);
   }

   
   /**
    * Dettach a link the graph without destroying it. 
    * 
    * @param p Position of the link to remove
    */

   public void removeLink (int p) {
      linkList.removeLink (p);
   }
   

/* ****************** Other methods *********************** */

   /** 
    * Clone a graph. It copies the nodeList and the linkList but
    * does not copy every node and every link.
    */
   
   /* Hay que modificar esta forma de clonar. El error que da cuando
      se  intenta clonar una lista de nodos se remonta al clone de la
      clase FiniteStates */
      
   public Object clone() {
      return  (new Graph ((NodeList) nodeList.copy(), 
                          (LinkList) linkList.copy(), kindOfGraph));
   }
   

 
   /**
    * This method is used to obtain the link that contains the node N
    * 
    * @param N The node to find in one of the links
    * @return An integer with the position of the link
    * or -1 if this link isn't in the list
    */

   public int getNodePosition (Node n) {

      int position;
      Link link;

      for(position=0; position < linkList.size(); position++){
         link = (Link) linkList.elementAt(position);
         if (n==link.getTail() || n==link.getHead()) 
             return position;
      }

      return (-1);
   }


   /**
    * Obtain the link between node1 and node2
    * @return A link between the nodes given as paramters 
    *         or null in other case
    */
   
   public Link getLink (Node node1, Node node2) {
      int position;
      Link link;
      
      for (position=0; position<linkList.size(); position++) {
         link = (Link) linkList.elementAt(position);
         
         if (node1==link.getTail() && node2==link.getHead()) 
            return ((Link) linkList.elementAt(position));
            
         /* If the link from nameNode1 to nameNode2 does not exits
            and the link is not directed is necessary check if 
            the link from nameNode2 to NameNode1 exists */
            
         if (!link.getDirected()) 
            if (node1==link.getHead() && node2==link.getTail()) 
               return ((Link) linkList.elementAt(position));
      }
      return null;
   }
                       

   /**
    * Return the list of parents of a node 
    * 
    * @param node Is the node whose parents are returned
    * @return A NodeList with the parents of the node
    */

   public NodeList parents(Node node) {

      NodeList parents = new NodeList();
      Link link;
      Enumeration e = linkList.elements();
  
      while (e.hasMoreElements()){
         link = (Link) e.nextElement();
         if (node==link.getHead()) 
            parents.insertNode (link.getTail());
      }
 
   return parents;
  
   }


   /**
    * Return the list of the children of the given node
    *
    * @param node Node whose children are returned
    * @return A Vector with the children
    */

   public NodeList children(Node node) {

      NodeList children = new NodeList();
      Link link;
      Enumeration e=linkList.elements();

      while (e.hasMoreElements()){
         link = (Link) e.nextElement();
         if (node == link.getTail())
            children.insertNode (link.getHead());
      }

      return children;
   }


   /**
    * Calculates the neighbours nodes of the node given
    * (including parents and children)
    * 
    * @param node Node whose neighbours are returned
    * @return A Vector with the neighbours
    */

   public NodeList neighbourhood(Node node) {

      NodeList neighbours = new NodeList();
      Link link;
      Enumeration e=linkList.elements();

      while (e.hasMoreElements()){
         link = (Link) e.nextElement();
         if (node == link.getHead())
            neighbours.insertNode (link.getTail());
         if (node == link.getTail())
            neighbours.insertNode (link.getHead());
      }

      return neighbours;
  
   }


   /**
    * Returns the List of accessible nodes of the node gives as parameter
    * 
    * @param node Used to calculate the accessibles nodes
    * @return A Vector with the accessibles nodes
    */

   public Vector accessibles(Node node){
 
      Vector accessibles = new Vector();
      SetVectorOperations S = new SetVectorOperations();
      Enumeration e=children(node).elements();
      Node child;

      while (e.hasMoreElements()){
         child = (Node) e.nextElement();
         accessibles.addElement (child);
         accessibles = S.union (accessibles, (Vector)accessibles(child));
      }

      return accessibles;
   }


   /**
    * Calculates the moral graph using the graph that receives 
    * this message. If the graph is directed returns the same graph
    * 
    * @return A moral graph
    */

   public Graph moral() {
  
      Graph moralGraph = (Graph) clone();
      moralGraph.kindOfGraph = UNDIRECTED;

      switch (getKindOfGraph()) {
         
         case UNDIRECTED: return moralGraph;
         
         case DIRECTED: 
         
            int j,k;
            Link l;
            NodeList nodeParents;
            Node n,n1,n2;
            Enumeration e1=moralGraph.nodeList.elements();

            while (e1.hasMoreElements()){
               n = (Node) e1.nextElement();
               nodeParents = parents(n);
      
               for (j=0; j<nodeParents.size()-1; j++){
                  n1=(Node) nodeParents.elementAt(j);
        
                  for (k=j+1; k<nodeParents.size(); k++){
                     n2=(Node) nodeParents.elementAt(k);
                  
                     if ((moralGraph.getLink(n1,n2)!=null) && 
                         (moralGraph.getLink(n2,n1)!=null))
                      
                         moralGraph.createLink(n1,n2,false);          
                  }
        
               }   
      
            } 
            return moralGraph;
         
         // case MIXED:   to do in future
      }  
      
      return null;      

   }



   /**
    * This method is used to know if a given graph is dag
    * 
    * @return True if the graph is a dag
    * False in other case
    */

   public boolean isADag(){
  
      boolean change = false;      
      Node node;
      int i,position;
      int nodeParents, nodeChildren;
      
      Graph aux = (Graph) clone();
  
      do {         
         
         change=false;
         
         for(i=0; i<aux.nodeList.size(); i++){
            node = (Node) aux.nodeList.elementAt(i);
            nodeParents = ((NodeList) aux.parents(node)).size();
            nodeChildren = ((NodeList) aux.children(node)).size();
      
            if ((nodeParents==0)||(nodeChildren==0)){
               change=true;
               aux.removeNode(i);     // remove the node        
               
               position=aux.getNodePosition(node);
               
               while (position!=-1) {                   
                  aux.removeLink(position);            // remove their links               
                  position=aux.getNodePosition(node);
               }
            }
      
         }
    
      } while (change);
  
      if (aux.nodeList.size() >= 2) 
         return false;
      else 
         return true;
   }



   /**
    * This method is used to know if a given graph is a tree too
    * 
    * @return True if the graph is a tree
    * False in other case
    */

   public boolean isATree(){
        
      Node node;
      Enumeration e=nodeList.elements();;

      while (e.hasMoreElements()) {
         node = (Node) e.nextElement();
         if (((NodeList)parents(node)).size() > 1)            
            return false;         
      }
  
      return true;
      
   }

   /**
    * Used to know if a given graph is a polytree
    * 
    * @return True if the graph is a polytree
    * False in other case
    */

   public boolean isAPolytree(){
  
      boolean change = false;      
      Node node;
      int i,position;      
      Graph aux = (Graph) clone();
      
      do {         
         
         for(i=0; i<aux.nodeList.size(); i++){ 
            node = (Node) aux.nodeList.elementAt(i);            
            
            if ((aux.neighbourhood(node)).size() == 1){
               change=true;
               aux.removeNode(i);     // remove the node
               
               while ((position=aux.getNodePosition(node))!=-1)
                  aux.removeLink(position);            // remove their links                                 
               
            }
            
         }
         
      } while (change);
  
      if (aux.nodeList.size() >= 2) 
         return false;
      else 
         return true;

   }



   /**
    * Used to know if a graph is a simple graph
    * 
    * @return True is the graph is a simple graph
    * False in other case
    */

   public boolean isASimpleGraph(){
  
      boolean change = false;      
      Node node;
      int i,position;      
      Graph aux = (Graph) clone();
      
      // first we remove the nodes out of the loops
      do {
         
         for(i=0; i<aux.nodeList.size(); i++){ 
            node = (Node) aux.nodeList.elementAt(i);            
            
            if ((aux.neighbourhood(node)).size()==1){
               change=true;
               aux.removeNode(i);     // remove the node
               
               while ((position=aux.getNodePosition(node))!=-1)
                  aux.removeLink(position);            // remove their links              
               
            }
            
         }
         
      } while (change);
  
      // now we test if the resultant lattice is a DAG, and if the answer is
      // true, we test if the simple graphs condition holds for the remaining
      // nodes
      
      if (aux.isADag()){
         int nChildren,j;
         Node child;
         Vector global=new Vector();
         Vector accessibles;
         SetVectorOperations S=new SetVectorOperations();

         for(i=0;i<aux.nodeList.size();i++){ 
            node = (Node) aux.nodeList.elementAt(i);
            nChildren = ((NodeList) aux.children(node)).size();
            
            if (nChildren>=2){
               global.removeAllElements();  // set global to an empty list
               
               for(j=0;j<nChildren;j++){
                  child = (Node) ((NodeList) aux.children(node)).elementAt(j);
                  accessibles = accessibles(child);
                  if (((Vector)S.intersection(global,accessibles)).size() != 0) 
                     return false;
                  else 
                     global = (Vector)S.union(global, accessibles);
               } 
               
            }
            
         }

         return true;
      }
      else 
         return false;

   }


   /**
    * This method is used to know if exists a undirected path among two nodes.
    * @param source node
    * @param destino node
    * @param tested a vector to be created empty before to call this method. 
    * This vector is used for testing the nodes visited.
    * @return boolean
    */


   public boolean undirectedPath (Node source, Node destino, Vector tested){
      boolean aux = false;
      NodeList vecinos;
      Enumeration e;
      Node node;
       
      vecinos = neighbourhood (source);
      tested.addElement(source);
      if ( vecinos.getId(destino) != -1 ){
         aux=true;
      }
      else {
         for (e=vecinos.elements(); e.hasMoreElements();){
               node = (Node) e.nextElement();
               if (tested.indexOf(node) == -1){
                  aux = undirectedPath(node,destino,tested);
               }
	       if(aux) break;
         }
      }
      return aux;
   }
    

   /**
    * This method computes the ancestral graph for a node.
    * @param Node x The node.
    * @return Graph . A directed graph.
    */

   public Graph ancestral(Node x){

      Graph aux;
      NodeList parents, nodes;
      LinkList links;
      Link link;
      int i,pos;

      nodes = new NodeList(); 
      links = new LinkList();
      nodes.insertNode(x);
      parents = parents(x);
      
      for (i=0 ; i< parents.size() ;i++){           
         pos = getLinkList().getID(parents.elementAt(i).getName(),x.getName());
         if (pos != -1) {
	         links.insertLink(getLinkList().elementAt(pos));
         }
         else 
            System.out.println("Algo va mal en ancestral");
            
         aux = ancestral (parents.elementAt(i));
         nodes.join (aux.getNodeList());
         links.join (aux.getLinkList());
      }
       
      aux = new Graph(nodes,links, DIRECTED);

      return aux;
   }



   /**
    * This method computes the ancestral graph for a list of nodes.
    * @param NodeList x The node list.
    * @return Graph . A directed graph.
    */

   public Graph ancestral(NodeList vars){

      int i;
      Graph aux;
      NodeList nodes;
      LinkList links;
      Node node;

      nodes = new NodeList();
      links = new LinkList();

      for(i=0 ; i< vars.size(); i++){
         node = (Node)vars.elementAt(i);
         aux = ancestral(node);
         nodes.join(aux.getNodeList());
         links.join(aux.getLinkList());
      }

       
      aux = new Graph(nodes,links, DIRECTED);
      return aux;
        
   }


    /**
     * This method computes if two nodes x,y are d-separated given a list of
     * nodes z.
     * @param Node x 
     * @param Node y
     * @return boolean
     */

   public boolean independents(Node x, Node y, NodeList z){
      Graph aux;
      Enumeration e,e1;   
      Node node;
      NodeList auxNodeList,nodes;
      LinkList auxLinkList;
      boolean delete;
      Vector tested;
      int pos;
       
      nodes = new NodeList();
      nodes.insertNode(x);
      nodes.insertNode(y);
      nodes.join(z);

      aux = ancestral(nodes);
      aux = aux.moral();
      auxNodeList = aux.getNodeList();
      auxLinkList = aux.getLinkList();
      
      for(e=z.elements(); e.hasMoreElements();){
         node = (Node)e.nextElement();
         
         if(auxNodeList.getId(node) !=-1){
               auxNodeList.removeNode(auxNodeList.getId(node));
               delete = true;
               
               while(delete){
                  if((pos=auxLinkList.getID(node)) != -1){
                     auxLinkList.removeLink(pos);
                     delete=true;
                  }
                  else 
                     delete=false;
               }
         }
         else 
            System.out.println("No existe el nodo "+ node.getName());
      }
      tested = new Vector();
      
      if (aux.undirectedPath(x, y, tested)){
         return false;
      }
      else 
         return true;
   }


    /* This method computes if two nodes x,y are d-separated given a list of
     * nodes z.
     * @param int degree. here, it's not used.
     * @param Node x 
     * @param Node y
     * @return boolean
     */

   public boolean independents(Node x, Node y, NodeList z, int degree){
      return(independents(x,y,z));
   }


    /**
     * This method computes the nodes topological ancestral order.
     * @return NodeList. A List of Nodes topologically sorted.
     */

   public NodeList topologicalOrder(){

	   NodeList index;
	   Node node;
	   int i;

	   index = new NodeList();
	   for(i=0 ;i< getNodeList().size();i++){
	      node = (Node)getNodeList().elementAt(i);
	      if(index.getId(node)==-1)
		   topological(node,index);
	   }

	   return index;
   	
   }


    /**
     * This method is private. It is a auxiliar method for Topological Order.
     */

   private void topological(Node node, NodeList index){

	   int i;
	   NodeList pa;
	   Node aux;

	   pa = parents(node);
	   for(i=0 ; i< pa.size() ; i++){
	      aux = (Node)pa.elementAt(i);
	      if(index.getId(aux)==-1)
		   topological(aux,index);
	   }

	   index.insertNode(node);

   }

    /**
     * This method computes the maximal number of adyacencies in the graph.
     * @return int. 
     */


   public int maxOfAdyacencies(){

      int max,i;
      NodeList nb;
      Node node;

      max=0;
      for(i=0 ; i<getNodeList().size();i++){
         node = (Node)getNodeList().elementAt(i);
         nb = neighbourhood(node);
         if(nb.size() > max) max = nb.size();
      }


      return max;

   }

    /**
     * This method computes the Markov Blanket of a node.
     * @param node.
     * @return NodeList. 
     */

   public NodeList markovBlanket(Node node){

      int i;
      NodeList mb,pa,ch;
      Node aux;

      mb=new NodeList();
      pa = parents(node);
      mb.join(pa);
      ch = children(node);
      mb.join(ch);

      for(i=0 ;i< ch.size();i++){
         aux = (Node)ch.elementAt(i);
         pa = parents(aux);
         pa.removeNode(node);
         mb.join(pa);
      }

      return mb;

   }


    
    /** 
     * This method computes the min d-separating set among two nodes 
     * in this graph.
     * @param Node x
     * @param Node y
     * @return A NodeList as the minumun d-separating set.
     */

    public NodeList minimunDSeparatingSet(Node x, Node y){
	
	int i,j;
	NodeList minimun,nodes;
	Graph auxGraph;
	Hashtable labelNodeplus,labelNodeminus;
	NodeList labelledNodes,scannedNodes,ch,pa,xMinusCut,queue;
	Node source,sink,Xi,Xj,z,node,nodelabel;
	boolean found = false;
	LinkList flow;
	Link link;

	flow = new LinkList();
	auxGraph = initializeMinDSep(x,y);
	source = (Node)auxGraph.getNodeList().getNode(x.getName()+"-");
	sink   = (Node)auxGraph.getNodeList().getNode(y.getName()+"+");

	do{
	    labelledNodes = new NodeList();
	    queue = new NodeList();
	    scannedNodes = new NodeList();
	    labelNodeplus = new Hashtable();
	    labelNodeminus = new Hashtable();
	    labelledNodes.insertNode(source);
	    queue.insertNode(source);
	    labelNodeplus.put(source,source);
	    
	    // Ahora se intenta buscar un camino no saturado
	    
	    while((labelledNodes.getId(sink)==-1) && (queue.size()>0)){
		Xi = (Node)queue.elementAt(0);
		if(scannedNodes.getId(Xi) == -1){ // Si no esta ya comprobado
		    // ahora comprueba hijos.
		    ch = auxGraph.children(Xi);
		    for(i=0; i< ch.size(); i++){
			Xj=(Node)ch.elementAt(i);
			link=(Link)auxGraph.getLinkList().
			    getLinks(Xi.getName(),Xj.getName());
			if((labelledNodes.getId(Xj) == -1) && 
			   (flow.indexOf(link) == -1)){
			    labelNodeplus.put(Xj,Xi);
			    labelledNodes.insertNode(Xj);
			    queue.insertNode(Xj);
			}
		    }
		    // ahora compruebo padres
		    pa = auxGraph.parents(Xi);
		    for(i=0;i< pa.size();i++){
			Xj=(Node)pa.elementAt(i);
			link=(Link)auxGraph.getLinkList().
			    getLinks(Xj.getName(),Xi.getName());
			if((labelledNodes.getId(Xj) == -1) && 
			   (flow.indexOf(link)!= -1)){
			    labelNodeminus.put(Xj,Xi);
			    labelledNodes.insertNode(Xj);
			    queue.insertNode(Xj);
			}
		    }
		    
		}
		scannedNodes.insertNode(Xi);
		queue.removeNode(Xi);
		
	    }

	    // Si se ha encontrado tal camino entonces se recupera y se marca.
	    
	    if(labelledNodes.getId(sink) != -1){
		Xj=sink;
		do{
		    if((z=(Node)labelNodeplus.get(Xj))!=null){
			link = (Link)auxGraph.getLinkList().
			    getLinks(z.getName(),Xj.getName());
			flow.insertLink(link);
		    }
		    else {
			z=(Node)labelNodeminus.get(Xj);
			link = (Link)auxGraph.getLinkList().
			    getLinks(Xj.getName(),z.getName());
			flow.removeLink(link);
		    }
		    
		    Xj=z;
		}while(z!=source);
		
	    }
	    if(queue.size()==0) found=true;
	    
	}while(!found); // Hasta que no pueda encontrar otro camino
	
	xMinusCut = new NodeList();
	xMinusCut = auxGraph.getNodeList();
	
	for(i=0;i<labelledNodes.size();i++){
	    node = (Node)labelledNodes.elementAt(i);
	    if((nodelabel=(Node)xMinusCut.getNode(node.getName())) != null)
		xMinusCut.removeNode(node);
	}

	minimun = new NodeList();

// El minimo separador seran los que no pertencezcan a los etiquetados

// Y ahora busco los nodos correspondientes a los arcos del minimo separador.

	for(i=0;i< labelledNodes.size();i++)
	    for(j=0;j<xMinusCut.size();j++){
		Xi = (Node)labelledNodes.elementAt(i);
		Xj = (Node)xMinusCut.elementAt(j);
		link=(Link)auxGraph.getLinkList().
		    getLinks(Xi.getName(),Xj.getName());
		if(link != null){
		    Xj.setName(Xj.getName().replace('+',' ').trim());
		    Xj.setName(Xj.getName().replace('-',' ').trim());
		    Xj =(Node)getNodeList().getNode(Xj.getName());
		    minimun.insertNode(Xj);
		}
	    }
	
	
	return minimun;
	
    }
    

    /**
     * Metodo privado para inicializar la busqueda el minimo d-separador
     */

    private Graph initializeMinDSep(Node x , Node y){

	NodeList nodes,nodesplus,nodesminus,auxNodes;
	Graph auxGraph;
	Link link,linkUminusVplus,linkVminusUplus;
	LinkList auxLinks;
	Node nodeplus,nodeminus,nodeUplus,nodeUminus,nodeVplus,nodeVminus;
	int i;
	
	nodes = new NodeList();
	nodes.insertNode(x); nodes.insertNode(y);

	auxGraph = ancestral(nodes);
	auxGraph = auxGraph.moral();

	nodesplus = (NodeList)auxGraph.getNodeList().clone();
	nodesminus = (NodeList)auxGraph.getNodeList().clone();
	auxLinks = new LinkList();

	for(i=0 ;i< nodesplus.size();i++){
	    nodeplus  = (Node)nodesplus.elementAt(i);
	    nodeminus = (Node)nodesminus.elementAt(i);
	    nodeplus.setName(nodeplus.getName()+"+");
	    nodeminus.setName(nodeminus.getName()+"-");
	    link = new Link(nodeplus,nodeminus);
	    auxLinks.insertLink(link);
	}

	auxNodes = new NodeList();
	auxNodes.join(nodesplus);
	auxNodes.join(nodesminus);
	
	
	for(i=0 ; i<auxGraph.getLinkList().size() ; i++){
	    link = (Link)auxGraph.getLinkList().elementAt(i);
	    nodeUplus  = 
		nodesplus.getNode(((Node)link.getTail()).getName()+"+");
	    nodeUminus = 
		nodesminus.getNode(((Node)link.getTail()).getName()+"-");
	    nodeVplus  = 
		nodesplus.getNode(((Node)link.getHead()).getName()+"+");
	    nodeVminus = 
		nodesminus.getNode(((Node)link.getHead()).getName()+"-");
	    linkUminusVplus = new Link(nodeUminus,nodeVplus);    
	    linkVminusUplus = new Link(nodeVminus,nodeUplus);
	    auxLinks.insertLink(linkUminusVplus);
	    auxLinks.insertLink(linkVminusUplus);
	}
	
	
	auxGraph = new Graph(auxNodes,auxLinks,DIRECTED);
	
	return auxGraph;
	
	
    }

    /**
     * This method generates a random NodeList from a poisson distribution for
     * a node, with mean average.
     * @param generator.
     * @param Node.
     * @param average.
     * @return NodeList
     */

    private NodeList randomParents(Random generator,Node node,double average){

	int i,r2,nParents,pos;
	NodeList nodes = new NodeList();

	nParents = poisson(generator,average);
	pos = getNodeList().size()-1;

	if(pos <= nParents){
	    for(i=0 ; i < pos ; i++)
		nodes.insertNode((Node)getNodeList().elementAt(i));
	}else{
	    for(i=0 ;i < nParents ; i++){
		r2 = (int)(generator.nextDouble()*pos);
		if(nodes.getId((Node)getNodeList().elementAt(r2)) == -1){
		    nodes.insertNode((Node)getNodeList().elementAt(r2));
		}else i--;
	    }
	} 
	
	return nodes;
	
    }

    /**
     * this methog generates a poisson distribution
     * @param Random. the generator of U(0,1).
     * @param double. average.
     */


    static public int poisson(Random generator, double average){

	int x,i;
	double Xi;
	double suma;
	double tem,TR,den,div;

	suma = 0.0;
	tem = (double) (-1.0/average);
	den = generator.nextDouble();
	Xi = tem * Math.log(den);
	suma = suma + Xi;
	for(x=1;suma<1;x++){
	    den = generator.nextDouble();
	    Xi = tem * Math.log(den);
	    suma = suma + Xi;
	}             
	
	return (x-1);
    }
    
    /** 
     * This method computes the component conex of a Graph from a node.
     * @param Node. 
     * @param NodeList. list of nodes where the method store the nodes in the 
     * component conex of the graph.
     */


    public void undirectedAccessibles(Node node, NodeList nodes){
	
	Enumeration e;
	Node nb;
	
	nodes.insertNode(node);
	for (e=neighbourhood(node).elements(); e.hasMoreElements(); ){
	    nb = (Node) e.nextElement();
	    if(nodes.getId(nb) == -1){
		nodes.insertNode(nb);
		undirectedAccessibles(nb,nodes);
	    }

	}
	
    }


  

} // end of Graph class








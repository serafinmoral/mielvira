import RelationList;
import NodeJoinTree;
import Graph;
import Bnet;
import java.util.Vector;
import java.io.*;

/**
 * This class implements the structure necessary for creating a Joint Tree.
 * Contains the methods needing for use this structure
 *
 * @since 27/08/99
 */

public class JoinTree { 
   
/**
 * Contains the list of the nodes of the Joint Tree.
 */
  
Vector joinTreeNodes;

/**
 * Data about the size of the joinTree
 */  

private JoinTreeStatistics statistics;


/**
 * Values to be used in the prunning of the potentials included in the
 * join tree in methods that manipulating the graphic structure and the
 * probability values stored in the nodes (like outerRestriction, ...) 
 */
private double limitForPotentialPrunning = 1e-10;
private int maximumSizeForPotentialPrunning = 2147483647; // maxint
    
/*public static void main(String args[]) throws ParseException, IOException {

  Bnet B;
  Evidence E;
  FileInputStream NetworkFile, EvidenceFile;
  Triangulation VE;
  JoinTree J;
  RelationList tmp1, tmp2;
  NodeList Numeration = new NodeList();
  int i;
  
  NetworkFile = new FileInputStream("ejemplo.elv");
  B = new Bnet(NetworkFile);     
    
  E = new Evidence();  
  VE = new Triangulation(B);
  tmp1 = new RelationList();
  tmp2 = new RelationList();
  J = new JoinTree();
  VE.getTriangulation();  
  Numeration = VE.maximaCardinalidad();
  tmp1 = VE.numerateCliques (Numeration);
  tmp2 = VE.getSeparators (tmp1);
  J.createJoinTree(tmp1, tmp2);
  J.Leaves(VE.getInitialRelations(B));
  J.binaryTree();
}
*/   

/**
 * Constructor. Creates a empty JoinTree 
 */
								 
JoinTree( ) {
  joinTreeNodes = new Vector();
  statistics = new JoinTreeStatistics();
}
   
/**
 * Constructor. Creates a new JoinTree for a given Bnet.
 * @param b a Bnet.
 */
								 
JoinTree(Bnet b) {
  
  RelationList cliques;
  Relation currentRel, inter, variablesInserted;
  NodeJoinTree n1, n2;
  Triangulation t;
  boolean found;
  int i, j;
  
  joinTreeNodes = new Vector();
  statistics = new JoinTreeStatistics();
  
  t = new Triangulation(b);
  
  cliques = t.getCliques();
  
  currentRel = cliques.elementAt(cliques.size()-1);
  n1 = new NodeJoinTree(currentRel);
  
  variablesInserted = new Relation(currentRel.getVariables().toVector());
  insertNodeJoinTree(n1);

  
  for (i=(cliques.size()-2) ; i>=0 ; i--) {
    currentRel = cliques.elementAt(i);
    inter = variablesInserted.intersection(currentRel);
    
    // Now look for one node containing the intersection.
    found = false;
    j = 0;
    
    while ((!found) && (j<joinTreeNodes.size())) {
      n1 = elementAt(j);
      if (n1.getNodeRelation().isContained(inter))
	found = true;
      else
	j++;
    }
    if (!found)
      System.out.println("ERROR");
    
    n2 = new NodeJoinTree(currentRel);
    insertNodeJoinTree(n2);
    
    n2.insertNeighbour(n1);
    n1.insertNeighbour(n2);
    
    variablesInserted.union(currentRel);
  }	   
}

/**
 * Constructor. Creates a new JoinTree for a given Bnet and a
 * a given Evidence.
 * @param b a Bnet.
 * @param ev an Evidence.
 */
								 
JoinTree(Bnet b,Evidence ev) {
  
  RelationList cliques;
  Relation currentRel, inter, variablesInserted;
  NodeJoinTree n1, n2;
  Triangulation t;
  boolean found;
  int i, j;
  
  joinTreeNodes = new Vector();
  statistics = new JoinTreeStatistics();
  
  t = new Triangulation(b);
  
  cliques = t.getCliques(ev);
  
  currentRel = cliques.elementAt(cliques.size()-1);
  n1 = new NodeJoinTree(currentRel);
  
  variablesInserted = new Relation(currentRel.getVariables().toVector());
  insertNodeJoinTree(n1);

  
  for (i=(cliques.size()-2) ; i>=0 ; i--) {
    currentRel = cliques.elementAt(i);
    inter = variablesInserted.intersection(currentRel);
    
    // Now look for one node containing the intersection.
    found = false;
    j = 0;
    
    while ((!found) && (j<joinTreeNodes.size())) {
      n1 = elementAt(j);
      if (n1.getNodeRelation().isContained(inter))
	found = true;
      else
	j++;
    }
    if (!found)
      System.out.println("ERROR");
    
    n2 = new NodeJoinTree(currentRel);
    insertNodeJoinTree(n2);
    
    n2.insertNeighbour(n1);
    n1.insertNeighbour(n2);
    
    variablesInserted.union(currentRel);
  }	   
}


   
   
   /**
    * Constructor. Creates a Joint Tree and introduces every relation in a
    * NodeJoinTree
    * 
    * @param R List of the relations used to creates the Joint Tree
    */
   
   JoinTree (RelationList R) {
      NodeJoinTree N;
      int i;
      
      for (i=0; i<R.size(); i++) {
         N = new NodeJoinTree(R.elementAt(i));
         insertNodeJoinTree (N);
      }
      
      statistics = new JoinTreeStatistics();
   }
   


/**
 * Creates a tree of cliques, that is, all the clusters in 
 * the join tree are maximal complete subgraph of the triangulated graph.
 *
 * @param b a Bnet.
 */
								 
public void treeOfCliques (Bnet b) {
  
  Triangulation t;
  NodeList ordering;
  RelationList R,S;

  t = new Triangulation(b);
  t.getTriangulation();
  ordering = t.maximumCardinalitySearch();
  R = t.numerateCliques(ordering);
  S = t.getSeparators(R);
  this.createJoinTree(R,S);
}
  
/**
 * Creates a tree of cliques in which the variables of set form a subtree of
 * the whole join tree.
 *
 * @param b a Bnet
 * @param set a list of variables
 */
								 
public void treeOfCliques (Bnet b, NodeList set) {
  
  Triangulation t;
  NodeList ordering;
  RelationList R,S;

  t = new Triangulation(b);
  t.getTriangulation(set);
  ordering = t.maximumCardinalitySearch(set);
  R = t.numerateCliques(ordering);
  S = t.getSeparators(R);
  this.createJoinTree(R,S);
}
  


   
   /**
    * This method is used to set the value of the joinTreeNodes variable
    * 
    * @param V The list of the nodes of the Joint Tree.
    */
   
   public void setJoinTreeNodes (Vector V) {
      joinTreeNodes = V;
   }
  
   
   /**
    * This method is used for accessing to the list of nodes.
    * 
    * @return The list of NodeJoinTree.
    */
   
   public Vector getJoinTreeNodes () {
      return joinTreeNodes;
   } 
   
   /**
    * Set the statistics
    */
   public void setStatistics(JoinTreeStatistics stat){
     statistics = stat;
   }

   /**
    * @return the statistics about the joinTree
    */
   public JoinTreeStatistics getStatistics(){
     return statistics;
   }

   
   /**
    * Returns the Relation in the position of parameter p
    * 
    * @param p The position of the relation to obtain.
    * @return The Relation at position p.
    */
   
   public NodeJoinTree elementAt(int p){
      return ((NodeJoinTree)joinTreeNodes.elementAt(p));
   }
        
        
   /**
    * Inserts a NodeJoinTree at the end of the list of nodes
    * 
    * @param N The node to insert.
    */

   public void insertNodeJoinTree (NodeJoinTree N) {
      joinTreeNodes.addElement(N);
   }
   
   
   /**
    * Removes a NodeJoinTree of the list of nodes
    * 
    * @param N Node to remove.
    */

   public void removeNodeJoinTree(NodeJoinTree N) {
      joinTreeNodes.removeElement(N);
   }
   
   
  /**
   * This method is used to get the position of a Node in the list of Nodes
   * in the Joint Tree
   * 
   * @param R A relation
   * @param N
   * @return The position of R in the list or -1 is R is not in it.
   */
  
   public int indexOf(NodeJoinTree N) {   
      return joinTreeNodes.indexOf(N);
   }

   
   /**
    * This method is used to calculate the number of relations that contains the 
    * join tree
    * 
    * @return The number of relations of the list.
    */	 
  
   public int size() {
      return joinTreeNodes.size();
   }
   
   
   /**
    * Creates the Joint Tree using a list of Cliques and a list of Separators.
    * 
    * @param Cliques Is the list of the relations that contains the Cliques of
    * the net
    * @param Separators Is the list of the messages that will be between the 
    * nodes of the Joint Tree
    */
   
   public void createJoinTree (RelationList Cliques, RelationList Separators) {
      
      int i,j;
      boolean find;
      NodeJoinTree Node;
      NeighbourTree Neighbour;
      
      Node = new NodeJoinTree (Cliques.elementAt(0));
      insertNodeJoinTree (Node);
      
      for (i=1; i<Cliques.size(); i++) {
         j=0;
         find = false;
         
         /* looking for a neighbour */
         
         while ((j<i) && (!find)) {
            if (Cliques.elementAt(j).isContained(Separators.elementAt(i))) {
               find = true;
               Node = new NodeJoinTree(Cliques.elementAt(i));
               
               Neighbour = new NeighbourTree();
               Neighbour.neighbour = this.elementAt(j);
               Neighbour.setMessage(Separators.elementAt(i));
               Node.neighbourList.insertNeighbour(Neighbour);
               insertNodeJoinTree(Node);
               
               Neighbour = new NeighbourTree();
               Neighbour.neighbour = Node;
               Neighbour.setMessage(Separators.elementAt(i));
               this.elementAt(j).neighbourList.insertNeighbour(Neighbour);              
            }
            else
               j++;
         }
      }
   }

   
   
   /**
    * Gets the list of messages (separators) of the Joint Tree. Its used for
    * the construction of the binary tree.
    * 
    * @return The list of the separators. Contains no repeated messages.
    */
   
   public RelationList getDifferentMessages() {
      int i,j;
      RelationList R = new RelationList();
      NodeJoinTree N = new NodeJoinTree();
      NeighbourTree Neighbour = new NeighbourTree();
      
      for (i=0; i<this.joinTreeNodes.size(); i++) {
         N = (NodeJoinTree) this.joinTreeNodes.elementAt(i);
         for (j=0; j<N.neighbourList.size(); j++) {
            Neighbour = (NeighbourTree) N.neighbourList.elementAt(j);
            if (!R.contains(Neighbour.getMessage())) {
               R.insertRelation(Neighbour.getMessage());
            }
         }
         
      }
      
      return R;
   }
   
   
   /**
    * Gets the list of all messages. Repeated messages may appear.
    * @return the list of messages.
    */
   
   public RelationList getMessages() {
    
     int i, j;
     RelationList R = new RelationList();
     NodeJoinTree N = new NodeJoinTree();
     NeighbourTree Neighbour = new NeighbourTree();
      
     for (i=0; i<this.joinTreeNodes.size(); i++) {
       N = (NodeJoinTree) this.joinTreeNodes.elementAt(i);
       for (j=0; j<N.neighbourList.size(); j++) {
	 Neighbour = (NeighbourTree) N.neighbourList.elementAt(j);
	 R.insertRelation(Neighbour.getMessage());
       }
     }
      
     return R;     
   }
   
   
/**
 * Converts the Joint Tree in a Tree where all the leaves contains one and
 * only one original relation. All the original relations are contained in 
 * a leaf
 * 
 * @param Relations A list with the original relations.
 */
   
public void Leaves (RelationList Relations) {      

  int i, position;
  NodeJoinTree N, newNode;
  NeighbourTree element;
  Relation R, R2, rel;
  Triangulation T = new Triangulation();
      
  /* Made a copy of the object because we need remove some elements */      
      
  for (i=0; i<Relations.size(); i++) {         
    position = this.containRelation(Relations.elementAt(i));
         
    if (position != -1) {
            
      /* N cotains the element neighbouring the new node */         
      N = new NodeJoinTree();            
      N = (NodeJoinTree) this.joinTreeNodes.elementAt(position);
            
      /* Creates the new node */            
      rel = new Relation();
      newNode = new NodeJoinTree();
      rel.setVariables(Relations.elementAt(i).getVariables().copy());
      rel.setValues(((PotentialMTree)Relations.elementAt(i).getValues()).copy());
      newNode.nodeRelation = rel;            
            
      /* Calculates the message that will be between the nodes */
      R = T.intersection(N.nodeRelation, newNode.nodeRelation);
      R2 = T.intersection(N.nodeRelation, newNode.nodeRelation);
            
      /* element contains the struct used to record the information 
	 about the neighbours */
      element = new NeighbourTree();
      element.setNeighbour(N);
      element.setMessage(R);
            
      /* Insert this in the Neighbours list */
      newNode.neighbourList.insertNeighbour(element);
            
      /* Got all the information, so insert the node */
      this.joinTreeNodes.addElement(newNode);
            
      /* Only one thing more. Add the new node to the neighbour's list
	 of the node N */
      element = new NeighbourTree();
      element.setNeighbour(newNode);
      element.setMessage(R2);
      N.neighbourList.insertNeighbour(element);
    }
         
  }
      
}
   
   
   /**
    * This method tells if any relation of the RelationList contain 
    * the Relation R
    * 
    * @param R The Relation to find in the Relationlist
    * @return True if the RelationList contains R, False in other case.
    */
  
   public int containRelation (Relation R) {
      NodeJoinTree N;
      
      int i = 0, position = -1;
      boolean find = false;      
            
      while ((i < this.size()) && (!find)) {         
         N = new NodeJoinTree();
         N = (NodeJoinTree) this.joinTreeNodes.elementAt(i);
         if (N.nodeRelation.isContained(R)) {
            find = true;
            position = i;
         }
         else
            i++;
      }
            
      return position;
	}

	/**
	 * This method construct the binary join tree from the join tree recorded in 
	 * a object of this class. The binary tree constructed is recorded in the same
	 * object that call this method, so the original tree is removed
	 * 
	 * @see joinNeighbours
	 * @see divideNode
	 */
	
	
	public void binaryTree () {
	   int i = 0, removed = 0;
	   NodeJoinTree N;
	   
	   while (i + removed < this.joinTreeNodes.size()) {
	      N = new NodeJoinTree();
	      N = (NodeJoinTree) this.joinTreeNodes.elementAt(i);
	      if (N.neighbourList.size() == 2) {	         
	         this.joinNeighbours(N);
	         this.joinTreeNodes.removeElementAt(i);
	         removed ++;
	      }
	      else
	         i++;
	   }
	      
	   for (i=0; i<this.joinTreeNodes.size(); i++) {
	      N = new NodeJoinTree();
	      N = (NodeJoinTree) this.joinTreeNodes.elementAt(i);
	      if (N.neighbourList.size() > 3) {
	         this.divideNode(N);
	      }
	   }
	   
	   
	}

	/**
	 * This method join the neighbours of the node that is gives as parameter.
	 * This method supposed that the node only have two neighbours.
	 * 
	 * @param node The node whose neighbours are going to join.
	 */
	
	
	public void joinNeighbours(NodeJoinTree node) {
	   
	   /* contains the message between the new neighbours */  
	   Relation R = new Relation();     
	   
      /* used for calling intersection method */	   
	   Triangulation T = new Triangulation(); 
	   
	   /* Contains the future neighbours */	   
	   NodeJoinTree Neighbour1 = new NodeJoinTree(); 
	   NodeJoinTree Neighbour2 = new NodeJoinTree();
	   
	   /* this is the struct to insert into the list of neighbours of each one */
	   NeighbourTree VariabletoInsert = new NeighbourTree();
	   NeighbourTree Temp;
	   
	   Temp = new NeighbourTree();	   
	   Temp = (NeighbourTree) node.neighbourList.elementAt(0);
	   Neighbour1 = Temp.getNeighbour();
	   
	   Temp = new NeighbourTree();
	   Temp = (NeighbourTree) node.neighbourList.elementAt(1);
	   Neighbour2 = Temp.getNeighbour();
	   
	   Neighbour1.neighbourList.removeNeighbour(node);
	   Neighbour2.neighbourList.removeNeighbour(node);
	   
	   R = T.intersection(Neighbour1.nodeRelation, Neighbour2.nodeRelation);
	   
	   VariabletoInsert.neighbour = Neighbour2;
	   VariabletoInsert.setMessage(R);	   
	   Neighbour1.neighbourList.insertNeighbour(VariabletoInsert);
	   
	   VariabletoInsert.neighbour = Neighbour1;	   
	   Neighbour2.neighbourList.insertNeighbour(VariabletoInsert);
	
	}

	/**
	 * Divides the node in two. The first node will contain three neighbours (two of the 
	 * original and the other new), and the second will contain the rest. If this second
	 * node contains more than 3 neighbours this method is called recursively.
	 * 
	 * @param N Node to divide.
	 */
	
	
	public void divideNode (NodeJoinTree N) {
	   int i;
	   NodeJoinTree Node1, Node2;
	   NeighbourTree newNode;
	   
	   NeighbourTree Element = new NeighbourTree();	   
	   Relation R = new Relation();
	   Triangulation T = new Triangulation();
	   
	   Node1 = new NodeJoinTree();
	   Node2 = new NodeJoinTree();
	   
	   for (i=0; i<2; i++) {	   
	      Element = (NeighbourTree) N.neighbourList.elementAt(i);
	      Node1.neighbourList.insertNeighbour(Element);
	      Element.neighbour.neighbourList.removeNeighbour(N);
	      
	      newNode = new NeighbourTree();
	      T.union (Node1.nodeRelation, Element.getMessage());
	      newNode.neighbour = Node1;
	      newNode.setMessage(Element.getMessage());
	      Element.neighbour.neighbourList.insertNeighbour(newNode);
	   }
	   	   
	   this.joinTreeNodes.addElement(Node1);
	   
	   for (i=2; i<N.neighbourList.size(); i++) {
	      Element = (NeighbourTree) N.neighbourList.elementAt(i);
	      Node2.neighbourList.insertNeighbour(Element);
	      Element.neighbour.neighbourList.removeNeighbour(N);	      
	      
	      newNode = new NeighbourTree();
	      T.union (Node2.nodeRelation, Element.getMessage());
	      newNode.neighbour = Node2;
	      newNode.setMessage(Element.getMessage());
	      Element.neighbour.neighbourList.insertNeighbour(newNode);
	   }
	   
	   this.joinTreeNodes.addElement(Node2);
	   
	   R = T.intersection(Node1.nodeRelation, Node2.nodeRelation);
	   
	   Element.neighbour = Node2;
	   Element.setMessage(R);
	   Node1.neighbourList.insertNeighbour(Element);
	   
	   Element.neighbour = Node1;
	   Element.setMessage(R);
	   Node2.neighbourList.insertNeighbour(Element);
	   
	   if (Node2.neighbourList.size() > 3)
	      divideNode (Node2);
	   
	}

  
/**
 * Gets the leaves of the join tree, i.e., those nodes with
 * only zero or one neigbours.
 * @return a vector of JoinTreeNodes corresponding to
 * the leaves.
 */
  
public Vector getLeaves() {

  Vector leaves;
  NodeJoinTree node;
  int i;
  
  leaves = new Vector();
  
  for (i=0 ; i<joinTreeNodes.size() ; i++) {
    node = (NodeJoinTree)joinTreeNodes.elementAt(i);
    if (node.isLeaf())
      leaves.addElement(node);
  }
  
  return leaves;
}


/**
 * Assigns a label to each node.
 */

public void setLabels() {
 
  int i;
  NodeJoinTree n;
  
  for (i=0 ; i<joinTreeNodes.size() ; i++) {
    n = (NodeJoinTree)joinTreeNodes.elementAt(i);
    n.setLabel(i);
  }
}


/**
 * Displays each node together with its neighbours (labels).
 */

public void display() {

  int i, j;
  NodeJoinTree n;
  NeighbourTree nt;
  
  for (i=0 ; i<joinTreeNodes.size() ; i++) {
    n = (NodeJoinTree)joinTreeNodes.elementAt(i);
    System.out.println("Node "+n.getLabel()+" has variables :");
    n.getNodeRelation().print();
    System.out.println("Node "+n.getLabel()+" has neighbours :");
    for (j=0 ; j<n.getNeighbourList().size() ; j++) {
      nt = n.getNeighbourList().elementAt(j);
      System.out.println("Label : "+nt.getNeighbour().getLabel());
      
      if(nt.getMessage()!=null){
	if(nt.getMessage().getValues()!=null) {
	  System.out.println("OUTGOING MESSAGE");
	  ((PotentialMTree)nt.getMessage().getValues()).print();
	}
	if(nt.getMessage().getOtherValues()!=null) {
	  System.out.println("INCOMING MESSAGE");
	  ((PotentialMTree)nt.getMessage().getOtherValues()).print();
	}
      }
    }
    System.out.println(" ");
  }
}


/**
 * Transforms a join tree into a binary join tree.
 */

public void binTree() {
  
  NeighbourTreeList neighbours, temp;
  NeighbourTree nt;
  NodeJoinTree node, newNode, tempNode;
  Relation rel;
  int i, n;
  
  node = elementAt(0);
  neighbours = node.getNeighbourList();
  n = neighbours.size();
  
  if (n>3) {
    temp = new NeighbourTreeList();
    newNode = new NodeJoinTree();
    insertNodeJoinTree(newNode);
    rel = new Relation();
    rel.setVariables(node.getVariables());
    newNode.setNodeRelation(rel);
    for (i=(n-1) ; i>1 ; i--) {
      nt = neighbours.elementAt(i);
      nt.getNeighbour().removeNeighbour(node);
      nt.getNeighbour().insertNeighbour(newNode);
      temp.insertNeighbour(nt);
      neighbours.removeElementAt(i);
    }
    newNode.setNeighbourList(temp);
    newNode.insertNeighbour(node);
    node.insertNeighbour(newNode);
  }
  
  neighbours = node.getNeighbourList();
  n = neighbours.size();
  
  for (i=0 ; i<n ; i++) {
    tempNode = neighbours.elementAt(i).getNeighbour();
    binTreeAux(node,tempNode);
  }
}


/**
 * Auxiliar to binTree.
 * @param parent a NodeJoinTree
 * @param child a NodeJointTree
 */

public void binTreeAux(NodeJoinTree parent, NodeJoinTree child) {
  
  NeighbourTreeList neighbours, temp;
  NeighbourTree nt;
  NodeJoinTree newNode, tempNode;
  Relation rel;
  int i, n, pos1, pos2;
  
  
  neighbours = child.getNeighbourList();
  n = neighbours.size();
  
  if (n>3) {
    temp = new NeighbourTreeList();
    newNode = new NodeJoinTree();
    insertNodeJoinTree(newNode);
    rel = new Relation();
    rel.setVariables(child.getVariables());
    newNode.setNodeRelation(rel);
    
    pos1 = neighbours.indexOf(parent);
    if (pos1==0)
      pos2 = 1;
    else
      pos2 = 0;
    
    for (i=(n-1) ; i>=0 ; i--) {
      if ((i!=pos1) && (i!=pos2)) {
	nt = neighbours.elementAt(i);
	nt.getNeighbour().removeNeighbour(child);
	nt.getNeighbour().insertNeighbour(newNode);
	temp.insertNeighbour(nt);
	neighbours.removeElementAt(i);
      }
    }
    newNode.setNeighbourList(temp);
    newNode.insertNeighbour(child);
    child.insertNeighbour(newNode);
  }
  
  neighbours = child.getNeighbourList();
  n = neighbours.size();
  
  for (i=0 ; i<n ; i++) {
    tempNode = neighbours.elementAt(i).getNeighbour();
    if (tempNode != parent)
      binTreeAux(child,tempNode);
  }  
}


/**
 * Initializes the potential tables of a jointree.
 *
 * @param b the Bnet which contains the initial relations
 */

public void initTables(Bnet b){

  RelationList ir;
  PotentialTable potTable,pot2;
  PotentialTree potTree;
  Potential potential;
  NodeJoinTree node;
  Relation r,r2;
  int i,j;

  // First we create unitary potentials for all the cliques
  
  for(i=0;i<getJoinTreeNodes().size(); i++){
    node = elementAt(i);
    r = node.getNodeRelation();
    potTable = new PotentialTable(r.getVariables());
    potTable.setValue(1.0);
    r.setValues(potTable);
  }

  // Now, we initialize the potentials using the network relations

  ir = b.getInitialTables( );
  for(i=0; i<ir.size();i++) {
    r = ir.elementAt(i);
    potTable = new PotentialTable();
    potential = (Potential)r.getValues();
    if (potential.getClass().getName().equals("PotentialTable"))
       potTable = (PotentialTable)potential;
    else if (potential.getClass().getName().equals("PotentialTree"))
       potTable = new PotentialTable((PotentialTree)potential);
    else{
      System.out.println(potential.getClass().getName() + 
                        " is not implemented in HuginPropagation");
      System.exit(0);
    }
  //if (potential ==null) System.out.println("ok");
 
    // searching for a clique containing the relation r
    for(j=0;j<getJoinTreeNodes().size();j++) {
      node = elementAt(j);
      r2 = node.getNodeRelation();
      if (r2.isContained(r)){
        pot2 = (PotentialTable)r2.getValues();
	//r2.setValues(pot2.combine(pot));
        pot2.combineWithSubset(potTable);        
        break;
      }
    }

  }

}


/**
 * Initializes the potential trees of a jointree.
 *
 * @param b the Bnet which contains the initial relations
 */

public void initTrees(Bnet b){

  RelationList ir;
  PotentialTree potTree,pot2;
  Potential potential;
  PotentialTable potTable;
  NodeJoinTree node;
  Relation r,r2;
  int i,j;

  // First we create unitary potentials for all the cliques
  
  for(i=0;i<getJoinTreeNodes().size(); i++){
    node = elementAt(i);
    r = node.getNodeRelation();
    potTree = new PotentialTree(r.getVariables());
    potTree.setTree(new ProbabilityTree(1.0));
    potTree.updateSize();
    r.setValues(potTree);
  }

  // Now, we initialize the potentials using the network relations

  ir = b.getInitialTables( );
  for(i=0; i<ir.size();i++) {
    r = ir.elementAt(i);
    potTree = new PotentialTree();
    potential = (Potential)r.getValues();
    if (potential.getClass().getName().equals("PotentialTree"))
       potTree = (PotentialTree)potential;
    else if (potential.getClass().getName().equals("PotentialTable"))
       potTree = ((PotentialTable)potential).toTree();
    else{
      System.out.println(potential.getClass().getName() + 
                        " is not implemented in HuginPropagation");
      System.exit(0);
    }
    //if (potential==null) System.out.println("ok");
 
    // searching for a clique containing the relation r
    for(j=0;j<getJoinTreeNodes().size();j++) {
      node = elementAt(j);
      r2 = node.getNodeRelation();
      if (r2.isContained(r)){
        pot2 = (PotentialTree)r2.getValues();
        pot2.combineWithSubset(potTree);
        r2.setValues(pot2);
        break;
      }
    }

  }

}



/**
 * Displays each node together with its neighbours (labels).
 */

public void display2() throws IOException {

  int i, j;
  NodeJoinTree n;
  NeighbourTree nt;
  
  for (i=0 ; i<joinTreeNodes.size() ; i++) {
    n = (NodeJoinTree)joinTreeNodes.elementAt(i);
    System.out.println("Node "+n.getLabel()+" has variables :");
    System.out.println("Tamanno : " + 
           FiniteStates.getSize(n.getNodeRelation().getVariables().toVector()));
    n.getNodeRelation().print();
    System.out.println("Node "+n.getLabel()+" has neighbours :");
    for (j=0 ; j<n.getNeighbourList().size() ; j++) {
      nt = n.getNeighbourList().elementAt(j);
      System.out.println("Label : "+nt.getNeighbour().getLabel());
      System.out.println("OUTGOING MESSAGE");
      nt.getMessage().print();
      //((PotentialTable)nt.getMessage().getValues()).print();
      /* System.out.println("INCOMING MESSAGE");
      ((PotentialMTree)nt.getMessage().getOtherValues()).print();*/
    }
    System.out.println(" ");
    System.out.println("Press any key.....");
    System.in.read();
    System.in.read();
  }
}

/**
 * @return a nodelist containing all the variables that appear in the jointree
 */

public NodeList getVariables( ){

  NodeList nl = new NodeList(),cliqueList;
  NodeJoinTree node;
  Node variable;
  int i,j;
  
  for(i=0; i<this.joinTreeNodes.size(); i++) {
    node = this.elementAt(i);
    cliqueList = node.getVariables();
    for(j=0; j<cliqueList.size(); j++) {
      variable = cliqueList.elementAt(j);
      if (nl.getId(variable) == -1)
        nl.insertNode(variable);
    }
  }
  return nl; 
}

/**
 * Sort the list of variables contained in the relations (cliques and
 * messages) of the jointree according to the order or variables 
 * specified in the nodelist pattern 
 *
 * @pararm pattern the nodelist used used to  test the order
 */

public void sortVariables(NodeList pattern){

  int i,j;
  NodeJoinTree node;
  NodeList cliqueList;
  Relation R;
  NeighbourTreeList ntl;
  NeighbourTree nt;  

  for(i=0; i<this.joinTreeNodes.size(); i++) {
    node = this.elementAt(i);
    R = node.getNodeRelation();
    cliqueList = R.getVariables();
    cliqueList.sort(pattern);
    R.setVariables(cliqueList);
    // now the messages
    ntl = node.getNeighbourList();
    for(j=0; j < ntl.size(); j++) {
      nt = ntl.elementAt(j);
      R = nt.getMessage();
      cliqueList = R.getVariables();
      cliqueList.sort(pattern);
      R.setVariables(cliqueList);
    }  
  }

}




/**
 * This method restrict the jointree by eliminating those variables which
 * are situated in the residual sets of the leaves cliques. So, the
 * restriction can be performed by passing sum-flows to the father node, or
 * simply summing in the node (if the residual set also contains variables
 * of the list passed as parameter)
 *
 * @param set the list of variables to be mantained in the jointree
 * @param divide if "yes" division is performed when a node is being removed
 */

public void outerRestriction(NodeList set, String divide) {

  boolean change=true;
  int i,j,s;
  NodeJoinTree node,father;
  NodeList varsNode,varsSep,varsRes,nl;
  NeighbourTreeList ntl;
  NeighbourTree nt;
  String type;
  Vector leaves = new Vector();
  Relation rel;
    

 

    // First we begin the outer restriction, in a bottom-up way
       
    s = joinTreeNodes.size();
    for(i=0; i<s; i++){
      node = (NodeJoinTree) joinTreeNodes.elementAt(i);
      if (node.isLeaf()) leaves.addElement(node); 
    }

    for(i=0;i<leaves.size(); ) {

      node = (NodeJoinTree) leaves.elementAt(i);

      // obtaining the variables in the residual
      varsNode = node.getVariables();
      if (joinTreeNodes.size()>1) { // el arbol no es un solo clique
        ntl = node.getNeighbourList();
        nt = ntl.elementAt(0); // the only neighbour
        varsSep =  nt.getMessage().getVariables();
        varsRes = varsNode.difference(varsSep); 
      }
      else{
        varsRes = varsNode;
        varsSep = new NodeList();
        nt = new NeighbourTree(); // this is only to avoid a compilatio
				  // error, because nt has to be initialized
      } 

      // identifying the kind of residual respect to set and
      // performing the addecuate restriction
         
      type = varsRes.kindOfInclusion(set);
      if (type.equals("subset")) { // nothing to do
        i++;
      }
      else if (type.equals("not empty")) { // removing the variables that
					   // in residual but not in set.
                                           // The node is not removed.
        nl = set.copy();
        nl.join(varsSep);
        (node.getNodeRelation()).restrictToVariables(nl);
        i++;
      }
      else { // "empty": remove the node and repeat the process
             // for the father of i
        // Absorbtion of node by father(node)
        father = nt.getNeighbour();
	father.absorbFromNode(node,divide);
        rel = father.getNodeRelation();
        rel = transformRelation(rel);                                     

        // removing node and its link with father (the only link, because
        // node is a leaf)
        this.removeNodeJoinTree(node);
        father.removeNeighbour(node);
	// If now father is a leaf, reexecuting loop for father. To avoid 
	// a more complex treatment, I use the trick to replace in leaves[i] 
	// node by father
        if (father.isLeaf()) leaves.setElementAt(father,i);
        else i++;
      }        

    } // end for

   
    // now we are going to remove (for each node) the variables in residual
    // but not in set. The node is not removed.

    s = joinTreeNodes.size();
    for(i=0;i<s;i++) {

      node = (NodeJoinTree) joinTreeNodes.elementAt(i);

      // obtaining the variables in the residual
      varsNode = node.getVariables();
      ntl = node.getNeighbourList();
      varsSep = new NodeList();
      for(j=0;j<ntl.size();j++){
        nt = ntl.elementAt(j);
        varsSep.join(nt.getMessage().getVariables());
      }
      varsRes = varsNode.difference(varsSep);  

      // identifying the kind of residual respect to set and
      // performing the addecuate restriction
         
      if ((varsRes.difference(set)).size()>0){
        nl = set.copy();
        nl.join(varsSep);
        (node.getNodeRelation()).restrictToVariables(nl);
      }  
         
      //type = new String(varsRes.kindOfInclusion(set));
      //if (type.equals("not empty")) { // removing the variables that
					   // in residual but not in set.
                                           // The node is not removed.
      //  nl = set.copy();
      //  nl.join(varsSep);
      //  (node.getNodeRelation()).restrictToVariables(nl);
      //}
    }




}


/**
 * @return a nodejointree from a cliquepair, this, and a the networks
 * variables, that are used to mantain the order among the variables
 *
 * @param cp the clique pair containig the nodes (head and tail) to
 *           be fused
 * @param order the nodelist used to mantain the same order in the
 *        variables of the new nodejointree
 * @param divide indicates if division have to be performed during the
 *        fusion of head and tail
 */
 
private NodeJoinTree fuseTwoNodes(CliquePair cp, NodeList order, 
                                    String divide){
  NodeJoinTree newNode;  
  Potential pot,pot2;  
  NodeList complete,restricted;
  Relation rel;
  NeighbourTreeList ntl;
  NeighbourTree nt;
  
  rel = new Relation();
  
  // building the new potential. The method is a bit complicated, but
  // it is necessary to mantain the order among the variables specified by order
  
  complete = cp.getCompleteList();
  complete.sort(order);

  pot2 = cp.getTail().getNodeRelation().getValues();
  if(pot2.getClass().getName().equals("PotentialTable")) {
    pot = new PotentialTable(complete,cp.getHead().getNodeRelation());
  }
  else if(pot2.getClass().getName().equals("PotentialTree")) {
    pot = new PotentialTree(complete,cp.getHead().getNodeRelation());
  }
  else{
    System.out.println("Potential class " + pot2.getClass().getName() + 
        " not implemented for method fuseTwoNodes");
    System.exit(0);
    pot = pot2; // the method never arrives here 
  }
    
  pot = pot.combine(cp.getTail().getNodeRelation().getValues());
  pot = transformPotentialAfterCombination(pot);
  
  // if divide is true, looking for the separator head-tail and performing
  // the division
  
  if (divide.equals("yes")){
    ntl = cp.getHead().getNeighbourList();
    nt = ntl.elementAt(ntl.indexOf(cp.getTail()));
    pot = pot.divide(nt.getMessage().getValues()); 
  }
  
  // maginalizing to restricted if necessary
  
  restricted = cp.getRestrictedList();
  if (restricted.size() != complete.size()){
    restricted.sort(order);
    pot = pot.marginalizePotential(restricted.toVector());
    pot = transformPotentialAfterAddition(pot);
    rel.setVariables(restricted.copy());
  }
  else rel.setVariables(complete.copy());
  
  rel.setValues(pot);
  newNode = new NodeJoinTree(rel);
  return newNode;
}

/**
 * This method is applied after the outer restriction. During the execution
 * the cliques neighbours with separator including variables not in set, are
 * fused using the method of Xu, but applying some heuristics guided by
 * criterion
 *
 * @return the sum of the cliques' size added in this process
 *
 * @param set the list of variables to be mantained in the jointree
 * @param divide if "yes" division is performed when a node is being removed
 * @param criterion the criterion used for the selection of the nodes to
 *        fuse in the inner restriction
 * @param order the nodelist representing the order to be mantained in
 *        the nodes of new creation
 */

public double innerRestriction(NodeList set, String divide, String criterion,
                                NodeList order) {

  CliquePairList cpl;
  CliquePair cp,cp2,cp3;
  int i,j,pos;
  NodeJoinTree newNode,head,tail,node,other;
  NeighbourTreeList ntl;
  NeighbourTreeList ntlNode,ntlNeighbour;
  NeighbourTree nt,nt2,ntNew;
  Vector headTail,auxVector;
  Vector subList;
  double extraSize = 0.0;
  NodeList nl,nl2;

  cpl = new CliquePairList(this,set,criterion);
  
  while (cpl.size() != 0) {
    // DEBUG
    /*System.out.println("listado: ---- ");
    System.out.println();
    cpl.print();*/
    // END DEBUG
    cp = cpl.getFirstAndRemove();
    extraSize+= cp.getSize();
    head = cp.getHead();
    tail = cp.getTail();
    headTail = new Vector();
    headTail.addElement(head);
    headTail.addElement(tail);

    // creating the new nodeJoinTree
    newNode = fuseTwoNodes(cp,order,divide);
    auxVector = new Vector();

    // setting as neighbour of the new node all the neighbours of head and tail
    for(j=0; j<2; j++){ 
      node = (NodeJoinTree)headTail.elementAt(j);
      other = (NodeJoinTree)headTail.elementAt((j+1)%2);
      ntlNode = node.getNeighbourList();
      //DEBUG
      //System.out.println("vecinos-head: " + head.getNeighbourList().size());
      //System.out.println("vecinos-tail: " + tail.getNeighbourList().size());
      //System.out.println("node: " + node.getLabel() + "vecinos: " + ntlNode.size());
      //END DEBUG
      for(i=0;i<ntlNode.size();i++){
        nt = ntlNode.elementAt(i);
        if (nt.getNeighbour().getLabel() != other.getLabel() ){
          //DEBUG
          //System.out.println("node: " + node.getLabel() + " ,nt: " +
		//nt.getNeighbour().getLabel() + " , other: " + other.getLabel()); 
          //System.out.println("Pasando : " + i);
          //END DEBUG
          // setting new neighbour for newNode
          ntNew = new NeighbourTree();
          ntNew.setNeighbour(nt.getNeighbour());
          ntNew.setMessage(nt.getMessage());
          auxVector.addElement(ntNew);
          //DEBUG
          //System.out.println("Despues de ntl.insert... vecinos-tail: " + tail.getNeighbourList().size());
          //END DEBUG
          // setting newNode as neighbour
          ntlNeighbour = nt.getNeighbour().getNeighbourList();
          pos = ntlNeighbour.indexOf(node);
          nt2 = ntlNeighbour.elementAt(pos);
          nt2.setNeighbour(newNode); 
        }
      }
    }

    // remove head and tail from the jointree and add newNode
    pos = joinTreeNodes.indexOf(head);
    newNode.setLabel(head.getLabel());
    //DEBUG
    //System.out.println("El numero de vecinos a colocar es " + auxVector.size());
    //END DEBUG
    ntl = new NeighbourTreeList();
    for(j=0;j<auxVector.size();j++) 
      ntl.insertNeighbour((NeighbourTree)auxVector.elementAt(j));
    newNode.setNeighbourList(ntl);
    joinTreeNodes.setElementAt(newNode,pos); // this also remove head
    joinTreeNodes.removeElement(tail);

    // Removing from cliquePairList the elements containing head or tail,
    // and replacing them by new cliquePairList containing newNode
    for(j=0; j<2; j++){
      node = (NodeJoinTree)headTail.elementAt(j);
      subList = cpl.getListAndRemoveElements(node);
      for(i=0; i<subList.size(); i++){
        cp2 = (CliquePair) subList.elementAt(i);
        if (cp2.getHead() == node){
          //testing if the separator contains variables not in set
          nl = (newNode.getVariables()).intersection(cp2.getTail().getVariables());
          nl2 = nl.intersection(set);
          if (nl.size() != nl2.size()){
            cp3 = new CliquePair(newNode,cp2.getTail(),set);
            cpl.addElement(cp3,criterion);
          }
          else if ( nl.equals(newNode.getVariables()) ||
			nl.equals(cp2.getTail().getVariables()) ){
            cp3 = new CliquePair(newNode,cp2.getTail(),set);
            cpl.addElement(cp3,criterion);
          }
        }
        else {
          //testing if the separator contains variables not in set
          nl = (newNode.getVariables()).intersection(cp2.getHead().getVariables());
          nl2 = nl.intersection(set);
          if (nl.size() != nl2.size()){ 
            cp3 = new CliquePair(cp2.getHead(),newNode,set);
            cpl.addElement(cp3,criterion);
          }
          else if ( nl.equals(newNode.getVariables()) ||
			nl.equals(cp2.getHead().getVariables()) ){
            cp3 = new CliquePair(newNode,cp2.getHead(),set);
            cpl.addElement(cp3,criterion);
          }
        } 
      }
    }    

  }
  
  return extraSize;

}

/**
 * This method restrict the jointree by eliminating the variables not
 * included in the set of variables passed as parameter. The method of
 * modification is based in the paper of Xu - Artificial Intelligence, 74
 * (1995) and in the modifications of this method outlined by Nilsson in
 * his paper Nilsson - Statistics and Computing, , (1998).
 *
 * @return the size of the cliques build during the inner restriction
 *
 * @param set the variables to be mantained in the jointree
 * @param divide if "yes" division is performed when a node is being removed
 * @param criterion the criterion used for the selection of the nodes to
 *        fuse in the inner restriction 
 * @param order the nodelist representing the order of variables that have
 *        to be mantained
 */

public double restrictToVariables(NodeList set, String divide, String criterion,
                                NodeList order) {

  double extraSize;

  this.outerRestriction(set,divide);
  this.setLabels();
  // DEBUG
  /*try {
    System.out.println("Printing the outer-pruned join tree");
    this.display2();
  } catch (IOException ioe){
    System.out.println("error al imprimir un arbol");
  }*/
  // END DEBUG
  extraSize = this.innerRestriction(set,divide,criterion,order);  
  this.setLabels();

  return extraSize;
}

/**

/**
 * Print some statistics about the join tree
 */

public void printStatistics( ) {
  statistics.print();
}

/**
 * Calculate the main data about the joinTree
 */
public void calculateStatistics(){
  int i;
  double min,max,mean,total,meanVars;
  int minVars, maxVars, totalVars;
  double val;
  int numVars;
  NodeJoinTree node;

  // initializing values with the clique at position 0
  node = this.elementAt(0);
  min = max = total = FiniteStates.getSize(
               node.getNodeRelation().getVariables().toVector());
  minVars = maxVars = totalVars = node.getNodeRelation().getVariables().size();
    
  for (i=1; i<this.joinTreeNodes.size(); i++) {
    node = this.elementAt(i);
    val=FiniteStates.getSize(node.getNodeRelation().getVariables().toVector());
    total += val;
    if (val < min) min = val;
    if (val > max) max = val;

    numVars = node.getNodeRelation().getVariables().size();
    totalVars += numVars;
    if (numVars < minVars) minVars = numVars;
    if (numVars > maxVars) maxVars = numVars;
  }
  
  mean = total/(double)this.joinTreeNodes.size();
  meanVars = totalVars/(double)this.joinTreeNodes.size();

  statistics.setNumCliques(this.joinTreeNodes.size());
  statistics.setMinVarsInClique(minVars);
  statistics.setMaxVarsInClique(maxVars);
  statistics.setMeanVarsInClique(meanVars);
  statistics.setMinCliqueSize(min);
  statistics.setMaxCliqueSize(max);
  statistics.setMeanCliqueSize(mean);
  statistics.setJTSize(total);
}


/**
 * This method 
 * @return a vector with a copy of the potentials in the cliques of the jt
 */

public Vector storePotentials( ){
  
  Vector v = new Vector();
  int i,s;
  NodeJoinTree node;
  Potential pot;

  s = joinTreeNodes.size();
  for(i=0;i<s;i++){
    node = (NodeJoinTree) joinTreeNodes.elementAt(i);
    pot = node.getNodeRelation().getValues();
    if(pot.getClass().getName().equals("PotentialTree")) {
      v.addElement(((PotentialTree)node.getNodeRelation().getValues()).copy());
    }
    else if(pot.getClass().getName().equals("PotentialTable")) {
      v.addElement(((PotentialTable)node.getNodeRelation().getValues()).copy());
    }
    else{
      System.out.println("Potential class: " + pot.getClass().getName() + 
              " is not implemented for the method storePotentials");
      System.exit(0);
    }

    

  }

  return v;
}

/**
 * This method restore the potential in the join tree from a vector of
 * potentials
 * @param v the Vector containing the potentials 
 */

public void restorePotentials(Vector v){

  int i,s;
  NodeJoinTree node;
  Potential pot;

  s=joinTreeNodes.size();
  for(i=0;i<s;i++){
    node = (NodeJoinTree) joinTreeNodes.elementAt(i);
    pot = (Potential) v.elementAt(i);
    node.getNodeRelation().setValues(pot); 
  }
}

/**
 * Transforms a potentialTree after a combination operator.
 *
 * IMPORTANT: the potential passed as parameter is modified
 * @ param pot the relation to be transformed
 */

public Potential transformPotentialAfterCombination(Potential pot){


  if (pot.getClass().getName().equals("PotentialTree")){    
    ((PotentialTree)pot).limitBound(limitForPotentialPrunning);
    if (maximumSizeForPotentialPrunning != 2147483647)
      if ( ((PotentialTree)pot).getTree().getLabel() == 1)
        pot = ((PotentialTree)pot).sortAndBound(maximumSizeForPotentialPrunning);
  }
  return pot;
} 

/**
 * Transforms a potentialTree after a marginalization-by-addition operation.
 *
 * IMPORTANT: the potential passed as parameter is modified
 * @ param pot the relation to be transformed
 */

public Potential transformPotentialAfterAddition(Potential pot){

  if (pot.getClass().getName().equals("PotentialTree")){    
    ((PotentialTree)pot).limitBound(limitForPotentialPrunning);
  }
  return pot;
} 

/**
 * Transforms a relation if it is a PotentialTree.
 * The only thing to do is the pruning of
 * nodes which children are equals, so we use a smallest value
 * for limit.
 * This method can be overcharged for special requirements.
 * @ param r the relation to be transformed
 */

public Relation transformRelation(Relation r){
  Potential pot;
  
  pot = r.getValues();
  if (pot.getClass().getName().equals("PotentialTree")){
    pot = transformPotentialAfterCombination((PotentialTree)pot);
    r.setValues((PotentialTree)pot);
  }
  return r;
}
               

}   // end of class
import java.util.Vector;
import java.util.Enumeration;
import java.io.*;


/**
 * Implements a list of object of type Node.
 *
 * @since 10/06/99.
 */

public class NodeList implements Cloneable{

/**
 * Contains the list of nodes
 */

Vector nodes;




/**
 * Construct. Creates an empty list of nodes.
 */

NodeList() {

  nodes=new Vector();
}

/**
 * Construct a NodeList from a vector of Nodes
 * @param varList is the vector of Nodes
 */

NodeList(Vector varList) {
  int i;

  nodes=new Vector();
  for(i=0;i<varList.size();i++) {
    nodes.addElement(varList.elementAt(i));
  }
}

/**
 * Constructs a new nodelist reading from a file, given a list
 * of all possible variables.
 * @param f the input file.
 * @param list the list of all possible variables.
 */

NodeList(FileInputStream f, NodeList list)
    throws ParseException ,IOException
{
  VariableListParse parser = new VariableListParse(f);

  parser.initialize(list);

  parser.CompilationUnit();
  nodes = parser.outputNodes.toVector();
}       


/**
 * Get the vector of nodes
 */

public Vector getNodes()
{ return(nodes); }


/**
 * Inserts a node at the end of the list.
 * @param n the node to insert.
 */

public void insertNode(Node n) {

  nodes.addElement(n);
}


/**
 * Removes a node from the list.
 * @param n the node to remove.
 */

public void removeNode(Node n) {

  nodes.removeElement(n);
}


/**
 * Removes a node from the list.
 * @param position the position of the node to be removed.
 */

public void removeNode(int position) {

  nodes.removeElementAt(position);
}


/**
 * Gets the position of a node in the list.
 * @param name the name of the node to search.
 * @return the position of the node. -1 if it is not found.
 */

public int getId(String name) {

  int position;

  for (position=0; position<nodes.size(); position++)
    if (name.trim().compareTo(((Node)nodes.elementAt(position)).getName().trim())==0)
      return position;

  return (-1);
}


/**
 * Gets the position of a node in the list.
 * @param node the node to search.
 * @return the position of the node. -1 if it is not found.
 */

public int getId(Node node) {

  return (nodes.indexOf(node));  
}


/**
 * Get the node with the name gives as parameter
 * 
 * @param name The name of a node.
 * @return The node with the name given or error if this name
 * can't be found in the NodeList
 */

public Node getNode(String name) {

  return ((Node)nodes.elementAt(getId(name)));
}


/**
 * Calculates the size of the NodeList
 * 
 * @return the number of nodes in the list.
 */

public int size() {

  return((int)nodes.size());
}


/**
 * Find the element in the given position
 * 
 * @param position an integer lower than the size of
 * the list -  1. Otherwise, an error is obtained.
 * @return the node at position 'position'.
 */

public Node elementAt(int position) {

  return ((Node)nodes.elementAt(position));
}


/**
 * Creates an enumeration with the elements of the list
 * 
 * @return The enumeration created
 */

public Enumeration elements() {

  return((Enumeration)nodes.elements());
}


/**
 * Saves the list.
 * @param p the PrintWriter where the list will be saved.
 */

public void save(PrintWriter p) {
  
  Enumeration enum;
  
  enum = nodes.elements();
  
  p.print("// Network Variables \n\n");
  
  while(enum.hasMoreElements())
    ((Node)enum.nextElement()).save(p);
}

/**
 * Print the list.
 * 
 */

public void print() {
  
  Enumeration enum;
    
  enum = nodes.elements();
  
  System.out.println("// Network Variables \n\n ");
  
  while(enum.hasMoreElements())
    ((Node)enum.nextElement()).print();
}



/**
 * Merges this list with the argument.
 * The current list is modified.
 * @param list a NodeList.
 */

public void merge(NodeList list) {
 
 int i;
 Node node;
 
 for (i=0 ; i<list.size() ; i++) {
   node = list.elementAt(i);
   if (getId(node)==-1)
     insertNode(node);
 }
}


/**
 * Join this list with the argument.
 * The current list is modified.
 * @param list a NodeList.
 */

public void join(NodeList list) {
 
 int i;
 Node node;
 
 for (i=0 ; i<list.size() ; i++) {
   node = list.elementAt(i);
   if (getId(node) == -1)
     insertNode(node);
 }
}

/**
 * @return a nodeList with the nodes in the nodeList which receives the message
 * minus the nodes in B
 *
 * @param B a nodelist
 */

public NodeList difference(NodeList B) {
  int i;
  Node node;
  NodeList nl = new NodeList();

  for(i=0;i<this.size();i++){
    node = this.elementAt(i);
    if (B.getId(node) == -1) 
      nl.insertNode(node);
  }
  
  return nl;  
}


/**
 * @return a nodeList with the intersection betweeen the nodelist which 
 * receives the message and B
 *
 * @param B a nodelist
 */

public NodeList intersection(NodeList B) {
  int i;
  Node node;
  NodeList nl = new NodeList();

  for(i=0;i<this.size();i++){
    node = this.elementAt(i);
    if (B.getId(node) != -1) 
      nl.insertNode(node);
  }
  
  return nl;  
}


/**
 * Creates a copy of the NodeList that receives the message. The copy
 * is done cloning the vector
 * 
 * @return The copy of the NodeList created.
 */

public NodeList copy() {
 
  NodeList list;
  
  list = new NodeList();
  
  list.nodes = (Vector)nodes.clone();
  
  return list;
}


/**
 * Return the list of variables as a vector.
 * @return a vector with the variables.
 */

public Vector toVector() {

  return nodes;
}

/**
 * Return the list of NodeList of size n as a vector.
 * @param n size of subsets.
 * @return a vector with the Subsets of Nodelists of size n.
 */


public Vector subSetsOfSize(int n){

    Vector aux = new Vector();
    NodeList subSet = new NodeList();
    int indexSubSet[];
    int i,j,k;
    boolean find = true;

    indexSubSet = new int[n];

    if ((n > 0) & (n <= nodes.size())){
	for(i=0;i<n;i++){
	    indexSubSet[i]=i;
	    subSet.insertNode((Node)nodes.elementAt(i));
	}
	aux.addElement(subSet);
	
	if(n < nodes.size()){
	    while (find){
		find = false;
		// System.out.println("Otra vuelta");
		for(i=n-1;i>=0;i--)
		    if(indexSubSet[i] < (nodes.size()+(i-n))){
			indexSubSet[i]=indexSubSet[i]+1;
			// System.out.println("Por aqui paso");
			if(i<(n-1)){
			    // System.out.println("Por aqui tambien");
			    for(j=i+1;j<n;j++)
				indexSubSet[j]=indexSubSet[j-1]+1;
			}
			// System.out.println("Por aqui si que paso");
			find=true;
			break;
		    }
	    
		if (find){
		    subSet = new NodeList();
		    for(k=0;k<n;k++){
			subSet.insertNode((Node)nodes.elementAt(
						      indexSubSet[k]));
			//subSet.print();
		    }
		    aux.addElement(subSet);
		}
	    }
	}
    }
    //for(i=0 ; i< aux.size(); i++){
    //subSet = (NodeList)aux.elementAt(i);
    //subSet.print();
    //}
    return aux;

}


/**
 * Checks whether two lists are the same.
 * @param a NodeList.
 * @return true if this is equal to the argument.
 */

public boolean equals(NodeList nl) {
 
  int i;
  
  if (size()!=nl.size())
    return false;
  
  for (i=0 ; i<size() ; i++) {
    if (getId(nl.elementAt(i))==-1)
      return false;
  }
  
  return true;
}

/**
 * Clone a NodeList
 * @return A Clone of the NodeList.
 */


public Object clone(){

NodeList o = null;
Node aux;
 try{
     o = (NodeList)super.clone();
     o.nodes = (Vector)nodes.clone();
     for(int i=0 ; i< nodes.size() ;i++){
	 aux = (Node)nodes.elementAt(i);
	 aux = (Node)aux.clone();
	 o.nodes.setElementAt(aux,i);
     }
 }catch (CloneNotSupportedException e){
     System.out.println("Can't clone NodeList");
 }

return o;

}



/**
 * sort the variables in the node list according to the order
 * of these variables in the node list given as argument (pattern). 
 * 
 * NOTE: the variables of the nodeList not included in pattern will be
 * situated in the fist positions of the new list. 
 *
 * @param pattern a superset of this
 */

public void sort(NodeList pattern) {

  int i,k,posMenor,indexMenor;
  Node menor,aux;

  for(i=0; i<(nodes.size()-1) ;i++){
    
    posMenor = i;
    menor = (Node) nodes.elementAt(i);
    indexMenor = pattern.getId(menor);  

    for(k=i+1;k<nodes.size();k++){
      aux = (Node)nodes.elementAt(k);
      if ( pattern.getId(aux) < indexMenor ){ 
        posMenor = k;
        menor = (Node)nodes.elementAt(k);
        indexMenor = pattern.getId(menor); 
      }
    }

    nodes.setElementAt((Node)nodes.elementAt(i),posMenor);
    nodes.setElementAt(menor,i);

  }  

}

/**
 * @return a string with the kind of inclusion of the nodelist which
 * receives the message and the node list pased as argument. The possible
 * outcomes are:
 *
 * - "subset": the nodelist is contained in set
 * - "empty" : nodelist \cap set = \emptyset
 * - "not empty": nodelist \cap set \neq emptyset, but there is nodes
 *                in nodelist not belongint to set 
 *
 * @param set 
 */

public String kindOfInclusion(NodeList set){

  int i,s,n=0;
  String c;

  s = this.size();
  for(i=0; i<s; i++)
    if (set.getId(this.elementAt(i)) != -1) n++;

  if (n==0) c = new String("empty");
  else{
     if (n==this.size()) c=new String("subset");
     else c = new String("not empty");
  }

  return c;
}

} // end of class



















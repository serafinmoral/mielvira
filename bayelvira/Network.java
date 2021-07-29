/* Network.java */

import java.io.*;
import java.net.URL;
import java.util.Vector;
import java.util.Enumeration;


/** The Network class is defined as a Graph -that contains a set of nodes
  * and a set of links- and a set of relations. 
  *
  * @see Graph
  * @since 14/09/99
  */

public abstract class Network extends Graph {

   private String name = "";
   private String title = "";
   private String comment = "";
   private String author = "";
   private String whoChanged = "";
   private String whenChanged = "";
   private boolean locked = false;
   private float version = (float) 1.0;   
   private Vector FSDefaultStates;
   public static String FiniteStatesDefaultStates[]={"absent", "present"};


   /**
    * Contains the list of relations of the Network
    */
   private Vector relationList;  


   /** 
    * Creates an empty Network 
    */

   public Network() {      
      setFSDefaultStates (FiniteStatesDefaultStates);
      relationList=new Vector();
   }
    

   /* ********** Access methods ************ */
       
   public String getName() {
      return name;
   }

   public String getTitle() {
      return title;
   }

   public String getComment() {
      return comment;
   }

   public String getAuthor() {
      return author;
   }

   public String getWhoChanged() {
      return whoChanged;
   }

   public String getWhenChanged() {
      return whenChanged;
   }

   public boolean getLocked() {
      return locked;
   }

   public float getVersion() {
      return version;
   }

   public Vector getFSDefaultStates(){
      return FSDefaultStates;
   }
   
   public Vector getRelationList() {
      return relationList;
   }



   /* ********* Modifiers ********* */

   public void setName(String s) {
      name=new String(s);
   }

   public void setTitle(String s) {
      title = new String(s);
   }

   public void setComment(String s) {
      comment = new String(s);
   }

   public void setAuthor(String s) {
      author = new String(s);
   }

   public void setWhoChanged(String s) {
      whoChanged = new String(s);
   }

   public void setWhenChanged(String s) {
      whenChanged = new String(s);
   }

   public void setLocked(boolean b) {
      locked = b;
   }

   public void setVersion(float v) {
      version  =  v;
   }

   public void setVersion(Float v) {
      version = v.floatValue();
   }

   public void setFSDefaultStates(Vector c) {
      FSDefaultStates = c;
   }

   public void setFSDefaultStates(String s[]){
      int i;

      FSDefaultStates = new Vector();

      for(i=0; i<s.length; i++) 
         FSDefaultStates.addElement(s[i]);
   }

   public void setRelationList(Vector r) {
      relationList = r;
   }


/* **** Methods for saving the data structures in files **** */
   
   /**
    * Saves the list of nodes in the text output stream given as parameter.
    * @param p the PrintWriter where the list will be saved.
    */

   public void saveNodeList (PrintWriter p) { 
  
      p.print("// Network Variables \n\n");
  
      for (int i=0; i<getNodeList().size(); i++){
         ((Node) getNodeList().elementAt(i)).save(p);              
      }
   }
   

   /**
    * Saves the links of the network in the variable given as argument
    * 
    * @param p Text output stream used to saved the links of the network
    */


   public void saveLinkList (PrintWriter p) {
        
      p.print("// links of the associated graph:\n\n");         
        
      for (int i=0; i<getLinkList().size(); i++){
         ((Link) getLinkList().elementAt(i)).save(p);              
      }  
   }


   /**
    * Saves the list of relations of the network using the text output 
    * stream gives as parameter.
    *
    * @param p Text output stream where the list will be saved.
    */

   public void saveRelationList (PrintWriter p) {
      
      p.print("//Network Relationships: \n\n");

      for (int i=0; i<getRelationList().size(); i++) {
         ((Relation)getRelationList().elementAt(i)).save(p);} 
   }
         
   
   /**
    * Saves all the variables of the network using the text output 
    * stream given as parameter. The network is saved with the Elvira
    * format.
    * 
    * @param p PrintWriter where the Network is saved    
    * @see saveNodeList
    * @see saveLinkList
    * @see saveRelationList
    */  
    
   public void save (PrintWriter p) throws IOException { 

      p.print("// Network Properties\n\n");

      if (!getTitle().equals(""))
         {p.print("title = \""+ getTitle()+"\";\n");}

      if (!getAuthor().equals(""))
         {p.print("author = \""+ getAuthor()+"\";\n");}

      if (!getWhoChanged().equals(""))
         {p.print("whochanged = \""+ getWhoChanged()+"\";\n");}

      if (!getWhenChanged().equals(""))
         {p.print("whenchanged = \""+ getWhenChanged()+"\";\n");}

      if (!getComment().equals(""))
         {p.print("title = \""+ getComment()+"\";\n");}

      if (getLocked())
         {p.print("locked = true;\n");} 

      p.print ("version = "   +getVersion() +";\n"); 
      p.print ("default node states = (");      
      
      for(int i=0; i<FSDefaultStates.size()-1; i++){
         p.print(FSDefaultStates.elementAt(i)+" , ");}

      p.print(FSDefaultStates.lastElement()+");\n\n");

      saveNodeList (p);
      saveLinkList (p);      
      saveRelationList (p);
   }


/* ******************* Other methods ********************** */               

   /**
    * Get the value for all the instance variables of the network
    * from the parser
    * 
    * @param parser Contains all the information about the 
    * network (read from a file)
    */   

   public void translate(BayesNetParse parser) {   
     RelationList L = new RelationList();

      setName(parser.Name);
      setTitle(parser.Title);
      setComment(parser.Comment);
      setAuthor(parser.Author);
      setWhoChanged(parser.WhoChanged);
      setWhenChanged(parser.WhenChanged);
      setVersion(new Float(parser.version));
    
      setNodeList (parser.Nodes);
      setLinkList (parser.Links);
      setRelationList (parser.Relations);      
      setFSDefaultStates (parser.DefaultFinite.getStates());



	   L.setRelations(relationList);
 	   L.repairPotFunctions();


      
   }


  /**
   * Gives a valid name to a variable. The names created by this function 
   * are like a..z, a1..z1, a2..z2, etc.
   * The name is generated using the variableNumber parameter that is 
   * asignated to the variable when is created.  
   *    
   * @param variableNumber Contain the number of the variable whose name it's going
   * to generate
   * @return The new name of the variable
   */
   
   private String generateName(int variableNumber) {
      Node node;

  	   // generate names of the form a..z, a1..z1, a2..z2, etc.
      char namec = (char) ((int) 'a' + variableNumber % 26);
      int suffix = variableNumber / 26;
      String name;
      
      if (suffix > 0)
         name = new String("" + namec + suffix);
      else
         name = new String("" + namec);
         
      //name="a";
      // check whether there is a variable with this name
      for (Enumeration e = getNodeList().elements(); e.hasMoreElements(); ) {
         node = (Node)(e.nextElement());
         if (node.getName().equalsIgnoreCase(name))
            return(generateName(variableNumber+1));
      }
      
      System.out.println(name);
      return(name);
   }

   
   /**
    * Get the position of a node in the nodelist using the name 
    * of the node. This method doesn't distinguish between upper and
    * lower case.
    *
    * @param name the name of the node to search.
    * @return the position of the node. -1 if it is not found.
    */

   public int getNodePosition (String name) {

      int position;

      for (position=0; position<getNodeList().size(); position++)
         if (name.equalsIgnoreCase( ((Node) getNodeList().elementAt(position)).getName()))
            return position;

      return (-1);
   }
   
   
   /**
    * Get the node with the name given as parameter
    * 
    * @param name The name of a node.
    * @see getNodePosition
    * @return The node with the name given or error if this name
    *         can't be found in the NodeList
    */

   public Node getNode(String name) {

      return ((Node) getNodeList().elementAt(getNodePosition(name)));
   }


  /**
    * Obtain a link given the names of the nodes called name1 
    * and name2. This method doesn't distinguish between upper 
    * and lower case.
    * 
    * @param name1 The name of the first node
    * @param name2 The name of the second node
    * @return A link from node1 to node2 or an undirected
              link between node1 and node2
    */
   

   public Link getLink (String nameNode1, String nameNode2) {
      
      Node node1 = getNode (nameNode1);
      Node node2 = getNode (nameNode2);
      return getLink (node1, node2);
   }


  /**
   * Create a new node in the Network. This node will be set in 
   * the position (x,y)
   */
   
   public void createNode(int x, int y) {
  
      Node node;
  
      System.out.println("Numero nodos:"+getNodeList().size());
      String n = generateName(getNodeList().size());
      node = new FiniteStates(n,x,y,getFSDefaultStates());
      addNode(node);
      addRelation(node);
      System.out.println("Nuevo numero nodos:"+getNodeList().size());
   }

  
  /**
   * Dettach a node from the network (without destroying it).
   * 
   * @param node Node to remove
   */
   
   public void removeNode(Node node) {
      
      Enumeration e;
      Node parent, child;
      int i;
      NodeList children;      
    
      // Third remove the node itself
      removeNode(node);

      // Remove the relations of all the node's children
      children = children(node);
      
      for(e=children.elements(); e.hasMoreElements(); ){
         child = (Node) e.nextElement();
         removeRelation(child);
      }

      // Remove the links that contains this node
      for(i=getLinkList().getID(node); i!=-1; i=getLinkList().getID(node))
         getLinkList().removeLink(i);

      // Add the new relations between the children of the node to remove
      for(e=children.elements(); e.hasMoreElements(); ){
         child = (Node) e.nextElement();
         addRelation(child);
      }

      // Removes the relation of the delete node
      removeRelation(node);     
  }


/**
 * Create a new link between head and tail in the network.
 * 
 * @param tail First node in the new link
 * @param head Second node in the new link
*/

   public void createLink(Node tail, Node head) {

      Vector V=new Vector();
      NodeList parents;
      Node node;
      int i;

      System.out.println("Numero enlaces:"+getLinkList().size());
      
      if (getLinkList().getID(tail.getName(),head.getName()) == -1){
         getLinkList().insertLink(new Link(tail,head));
         
         // Now took the node head's relation
         // First remove the old one
         removeRelation(head);
         
         // Now create the new relation and add it
         addRelation(head);      
      }
      
      System.out.println("Nuevo numero enlaces:"+getLinkList().size());
   }


  /**
   * Delete the link (head,tail) of the network, removing the relation
   * associated to it. This method creates the new relation that appears
   * when the link is removed.
   * 
   * @param tail Tail node of the arc to remove
   * @param head Head node of the arc to remove
   */
  
   public void removeLink (Node tail, Node head) {
      Enumeration e;
      Node parent, child;
      int p;   
    
      // Remove the link
      p = getLinkList().getID(tail.getName(),head.getName());
      getLinkList().removeLink(p);

      // now take the old relation from head
      // first remove the old one
      removeRelation(head);
      
      // now add the new relation
      addRelation(head);         
   }
   
   
  /**
   * Get the nodes in the network as an Enumeration object
   * 
   * @return The list of nodes as an Enumeration object
   */
   
   public Enumeration enumerateNodes() {
      return(getNodeList().elements());
   }


   /**
    * Determine whether or not a name is valid and/or repeated.
    * 
    * @param name Contains the name to check
    * @return The new checked if is valid or null in other case
    */
   
   
   public String checkName(String name) {
      
      Node node;
      String newName = validateValue(name);
      
      for (Enumeration e = getNodeList().elements(); e.hasMoreElements(); ) {
         node = (Node)(e.nextElement());
         if (node.getName().equals(newName))
            return(null);
      }
      return(newName);
   }


  /**
   * Get a valid name from the string given as parameter. 
   * This method change the blanks for '_'
   * 
   * @param value Contains the string to check
   * @return The string checked
   */
      
   public String validateValue(String value) {
      
  	   StringBuffer str = new StringBuffer(value);
  	   
      for (int i=0; i < str.length(); i++) {
         if (str.charAt(i) == ' ')
   	       str.setCharAt(i, '_');
      }
      
   	return str.toString();
   }


  /**
   * Change the values of a variable. Note that, if the number
   * of new values is different from the number of current values,
   * this operation resets the probability values of the variable 
   * and all its children.
   * 
   * @param node Node whose states are going to be set
   * @param values Contains the states of the node
   */

   public void changeValues(FiniteStates node, String values[]) {
      
      Node cnode;
      NodeList children;
      Enumeration e;

      if (node.getNumStates() == values.length) {
        node.setStates(values);
        return;
      }

      node.setStates(values);

    /* Now all the probabilty distributions must be reinicialiced
       for the actual node and for all its children*/

    // For actual node
    
      removeRelation(node);
      addRelation(node);
    
    // For all its children

      children = children (node);
      
      for (e=children.elements(); e.hasMoreElements(); ){
         cnode = (Node) e.nextElement();
         removeRelation(cnode);
         addRelation(cnode);
      }   
  }


  /**
   * Remove from RelationList the relation that contains the node
   * given as parameter
   * 
   * @param node Node whose relation is going to be removed
   */
    
   public void removeRelation(Node node){
  
      int i;
      Relation R;
  
      for(i=0;i<getRelationList().size();i++){
         R = (Relation) getRelationList().elementAt(i);
         if ((((Node)R.getVariables().elementAt(0)).getName()).equals(node.getName())){
	         getRelationList().removeElementAt(i);
	         System.out.println("Nuevo numero de relaciones: " + getRelationList().size());
	         break;
         }
      }
    
   }

    
  /**
   * Creates a relation for the given node and adds it
   * Add a probability table depending on its parents in 
   * the graph
   * 
   * @param node Node to add
   */  

   public void addRelation(Node node){

      Vector V = new Vector();
      NodeList parents;
      int i;
      Relation R;
  
      V.addElement(node);
      parents = parents(node);
      
      for(i=0; i<parents.size();i++){
         V.addElement((Node) parents.elementAt(i));	
      }
      
      R = new Relation(V);
      getRelationList().addElement(R);           
      System.out.println("Nuevo numero de relaciones: " + getRelationList().size());

   }


  /**
   * Gives the relation whose first variable is the same that
   * the given node
   * 
   * @param node Node to find
   * @return The relation found
   */
    
   public Relation getRelation(Node node){
  
      int i;
      Relation R;
  
      for(i=0;i<getRelationList().size();i++){
         R = (Relation) getRelationList().elementAt(i);
         if ((((Node)R.getVariables().elementAt(0)).getName()).equals(node.getName())){
	         return (R);
         }
      }
      return null;
    
   }


   /**
    * Gets the initial relations present in the network.
    * @return A copy of the relations in the Net.
    */

   public RelationList getInitialRelations() {
     
      Relation r, rNew;
      RelationList ir;
      int i;

 
      ir = new RelationList();
  
      for (i=0 ; i<relationList.size() ; i++) {
         r = (Relation)relationList.elementAt(i);
         rNew = new Relation();
         rNew.setVariables(r.getVariables().copy());            
         ir.insertRelation(rNew);
      }
  
      return ir;
   }

         
   /**
    * Gets the initial relations present in the network.
    * The difference with getInitialRelations(), is that the potentials
    * are copied too
    *
    * @see getInitialRelations
    * @return A copy of the relations in the Net.
    */

   public RelationList getInitialTables() {
     
      Relation r, rNew;
      RelationList ir;
      int i;
 
      ir = new RelationList();
  
      for (i=0 ; i<relationList.size() ; i++) {
         r = (Relation)relationList.elementAt(i);
         rNew = new Relation();
         rNew.setVariables(r.getVariables().copy());
         rNew.setValues(((PotentialTable)r.getValues()).copy());
         ir.insertRelation(rNew);
      }
  
      return ir;
   }
   

}  // end of Network class

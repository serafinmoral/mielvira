import java.io.*;


/**
 * Implements the basic structure of a node in a network.
 *
 * Last modified : 14/09/99
 */

public abstract class Node implements Cloneable{

   /* These constants are used to set the kind of the node */
   public static int CONTINUOUS = 0;
   public static int FINITE_STATES = 1;
   public static int INFINITE_DISCRETE = 2;
   public static int MIXED = 3;

   /* These constants are used to set the type of the node */
   public static int CHANCE = 0;
   public static int DECISION = 1;
   public static int UTILITY = 2;

   /**
    * An array of strings that contains the names of the different kinds of nodes.
    * The positions in this array are the same that the integers assigned to
    * the kindOfNode variable
    */

   static String kindNames[]={"continuous","finite-states","infinite-discrete","mixed"};

   /**
    * Contain the size of the kindNames array (the number of kinds).
    */
   static int totalKinds=4;

   /**
    * An array of strings that contains the different type of nodes
    * that could be. The position of the strings in the array are the
    * same that the integers assigned to kindOfNode variable
    */
   static String typeNames[]= {"chance","decision","utility"};

   /**
   * This variable contain the number of possible types of nodes
   */
   static int totalTypes=3;

   private String name;
   private String title;
   private String comment;

   /**
    * To know if it is a continuous, finite-states,
    * infinite-discrete, mixed
    */
   private int kindOfNode;

   /**
    * The kind of node is used to know if it is a chance, decision or
    * utility node.
    */
   private int typeOfVariable;


   /**
    * X-position of the node in the graphical representation
    */
   private int posX=0;

   /**
    * Y-position of the node in the graphical representation
    */
   private int posY=0;


   /** Graphic property used to know if the node is selected
    *  when the graphic mode is used
    */
   private boolean selected = false;


   /**
   * Creates an instance of Node. By default is a chance node
   * and it has finite states.
   */

   public void Node() {

      kindOfNode = FINITE_STATES;      // default :  finite-states
      typeOfVariable = CHANCE;  // default :  chance
      title = new String();
      comment = new String();
      name = new String();
   }


   public void setName(String s) {
      name = new String(s);
   }


   public void setTitle(String s) {
      title = new String(s);
   }


   public void setComment(String s) {
      comment = new String(s);
   }


   /**
   * Sets the kind of the node to s.
   * @param i a int value representing the new kind.
   */

   public void setKindOfNode(int i) {
      kindOfNode = i;
   }


   /**
    * Sets the kind of the node to s. This method is used
    * in the BayesNetParse class
    *
    * @param s a String with the new kind.
    */

   public void setKindOfNode(String s) {

      int i;

      // Looks for the number corresponding to s

      for (i=0 ; i<totalKinds; i++) {
         if (s.equals(kindNames[i])) {
            kindOfNode=i;
            return;
         }
      }
   }

   /**
   * Sets the type of variable to the given parameter.
   * @param i an integer value correponding to the new type.
   */

   public void setTypeOfVariable(int i) {
      typeOfVariable = i;
   }

   /**
   * Sets the type of variable to s. This method is mainly used in
   * the BayesNetParse class
   *
   * @param s a String with the new type.
   */

   public void setTypeOfVariable(String s) {

      int i;

      for(i=0 ; i<totalTypes ; i++) {
         if (s.equals(typeNames[i])) {
            typeOfVariable=i;
            return;
         }
      }
   }

   public void setPosX(int x) {
      posX = x;
   }


   public void setPosY(int y) {
      posY = y;
   }


   // Graphic method

   public void setSelected (boolean b) {

      selected = b;
   }


   public String getName() {
      return(name);
   }


   public String getTitle() {
      return(title);
   }


   public String getComment() {
      return(comment);
   }


   public int getKindOfNode() {
      return(kindOfNode);
   }


   /**
    * @returns A string that contains the kind of the node
    */

   public String getKind () {
      return kindNames[kindOfNode];
   }


   public int getTypeOfVariable() {
      return(typeOfVariable);
   }


   /**
    * @returns A string that contains the type of the variable
    */

   public String getType () {
      return typeNames[typeOfVariable];
   }


   public int getPosX() {
      return(posX);
   }


   public int getPosY() {
      return(posY);
   }

   // Graphic method

   public boolean isSelected() {
      return selected;
   }


   /**
   * Saves the node object using the text output stream gives
   * as parameter.
   * @param p a PrintWriter where the node will be printed.
   */

   public void save(PrintWriter p) {

      if ((getName()!=null) && (!getName().equals("")))
         p.print("name = \""+ getName()+"\";\n");

      if ((getTitle()!=null) && (!getTitle().equals("")))
         p.print("title = \""+ getTitle()+"\";\n");

      if ((getComment()!=null) && (!getComment().equals("")))
         p.print("comment = \""+ getComment()+"\";\n");

      p.print("kind-of-node = " +  getKind() + ";\n");
      p.print("type-of-variable = " + getType() + ";\n");

      if (posX != 0)
         p.print("pos_x ="+posX+";\n");

      if (posY != 0)
         p.print("pos_y ="+posY+";\n");
   }


   /**
    * Saves the node in the standard output.
    */

   public void print() {

      if ((getName()!=null) && (!getName().equals("")))
         System.out.print("name = \""+ getName()+"\";\n");

      if ((getTitle()!=null) && (!getTitle().equals("")))
         System.out.print("title = \""+ getTitle()+"\";\n");

      if ((getComment()!=null) && (!getComment().equals("")))
         System.out.print("comment = \""+ getComment()+"\";\n");

      System.out.print("kind-of-node = " +  getKind() + ";\n");
      System.out.print("type-of-variable = " + getType() + ";\n");

      if ( posX != 0)
         System.out.print("pos_x ="+posX+";\n");

      if ( posY != 0)
         System.out.print("pos_y ="+posY+";\n");
   }


   /**
    * Compares the node's name with the argument
    *
    * @param n the node to compare with this.
    * @return 0 if the name of the argument is the same as the name
    * of this; an int < 0 if it is lower and >0 if it is greater.
    */

   public int compareTo(Node n) {
      return (name.compareTo(n.name));
   }


   /**
    * @return A hard copy of this node.
    */

   public Object clone(){

      try {
	      return super.clone();
      }
      catch(CloneNotSupportedException e){
	      System.out.println("Can't clone Node: "+ name);
	      return this;
      }
   }

}  // end of Node class

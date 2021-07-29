/* Link.java */

import java.io.*;

/**
 * This class implements the structure necessary to stores all 
 * the information refered to a link.
 *
 * since 13/09/99
 */
  
public class Link {

  /**
   * Contains the tail of the link
   */

  private Node tail;		

  /**
   * Contains the head of the link
   */
  private Node head;		

  /**
   * Contains the comment of the link
   */
  private String comment;		

  /**
   * Contains the type of the link:
   * 	True - if the link is directed
   * 	False - if the link is not directed
   */
  private boolean directed;	
  
  // Graphical property
  
  private boolean selected;


  /* ******** Access methods ********* */

  /**
   * Using for accessing to variable Head
   * 
   * @return A node containing the head of the link
   */

  public Node getHead() {
    return head;
  }


  /**
   * Using for accesing to Tail
   * 
   * @return A node with the Tail of the node
   */

  public Node getTail() {
    return tail;
  }


  /**
   * Using for accesing to link's comment
   * 
   * @return A string with the comment
   */

  public String getComment() {
    return comment;
  }


  /**
   * Use for accessing to the link's type
   * 
   * @return True if is directed
   * False if is not directed
   */

  public boolean getDirected() {
    return directed;
  }
  
  public boolean isSelected() {
    return selected;
  }


   /* ******** Modifiers ******** */

  /**
   * Set the head of the link
   * 
   * @param h A node with the head of the link
   */
  
  public void setHead(Node h) {
    head = h;
  }


  /**
   * Set the tail of the link
   * 
   * @param t A node with the tail of the link
   */

  public void setTail(Node t) {
    tail = t;
  } 


  /**
   * Set the comment of the link
   * 
   * @param s A string that contains a comment about the link
   */

  public void setComment(String s) {
    comment = new String(s);
  }


  /**
   * Set if the link is directed or not
   * 
   * @param b True if the link will be directed
   * False if not
   */

  public void setDirected(boolean b) {
    directed = b;
  }
  
  
  // Graphical method
  
  public void setSelected (boolean b) {
    selected = b;
  }

   /* ******* Constructors ******** */

  /**
   * Constructor. Creates an empty link. Its class depends of
   * the parameter d.
   * 
   * @param d True if is directed and False if is undirected
   */  

  public Link(boolean d){
    setComment("");
    setDirected(d);
  }


  /**
   * Constructor. Creates a link between the nodes t and h. By default  
   * the link is directed
   * 
   * @param t Origin of the node
   * @param h Destination of the node
   */


  public Link (Node t, Node h){
    setTail(t);
    setHead(h);
    setComment("");
    setDirected(true);
  }


  /**
   * Constructor. Creates a link between t and h with the comment c.
   * 
   * @param t Origin
   * @param h Destination
   * @param c Comment
   */

  public Link (Node t, Node h, String c) {
    setTail(t);
    setHead(h);
    setComment(c);
    setDirected(true);
  }

  /**
   * Constructor. Creates a link between t and h and set with d
   * if the link is directed or not
   * 
   * @param t Origin
   * @param h Destination
   * @param d Type
   */

  public Link (Node t, Node h, boolean d) {
    setTail(t);
    setHead(h);
    setComment("");
    setDirected(d);
  }


  /**
   * Constructor. Creates a new link between t and h with comment c
   * and the type with d
   * 
   * @param t Origin
   * @param h Destination
   * @param c Comment
   * @param d Type
   */

  public Link (Node t, Node h, String c, boolean d) {
    setTail(t);
    setHead(h);
    setComment(c);
    setDirected(d);
  }


  /**
   * Stores the link in the parameter p   
   */

  public void save(PrintWriter p) {
   
    p.print("link ");
    p.print(tail.getName()+" "+head.getName());
   
    if( comment.equals("") && directed) {
      p.print(";\n\n");}
    else {
      
      if (!directed) {
         p.print("directed = false;\n");
      }
      
	   if (!comment.equals("")) {
	      p.print("comment =\""+comment+"\";\n");
	   }
	   
      p.print("\n");
    }
      
  }           


   /**
   * Clone a Link 
   * @return A clone of this link
   */      
   
   public Object clone(){

      Link l = null;
      try{
	   l = (Link)super.clone();
	   l.tail=(Node)tail.clone();
	   l.head=(Node)head.clone();
	   l.comment=new String(comment);
      }catch (CloneNotSupportedException e){
	   System.out.println("Can't clone Link");
   	
      }
      return l;


   }
        
} // end of Link class

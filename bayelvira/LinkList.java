
/* LinkList.java */

import java.util.Vector;
import java.util.Enumeration;
import Link;
import java.io.*;

/**
 * This class implements the list of links and all the needful
 * methods for use it.
 * 
 * Last modification: 14/09/98
 */


public class LinkList {

   /**
    * A list with the links of the network
    */

   private Vector links;


   /**
    * Constructor. Creates a vector of links
    */
   public LinkList() {
      links=new Vector();
   }


   public Vector getLinks() {
      return links;
   }

   public void setLinks(Vector L) {
      links = L;   
   }

   /**
    * Adds a link in the end of the list.
    * 
    * @param L Link to add
    */

   public void insertLink(Link L) {
      links.addElement(L);
   }


   /**
    * Deletes the link gives as argument. If the link doesn't exits
    * don't do anything
    * 
    * @param L Link to remove
    */
   
   public void removeLink(Link L) {
      links.removeElement(L);
   }
 

   /**
    * Saves the link in the variable gives as argument
    * 
    * @param p Object where the link is saved
    */


   public void save(PrintWriter p) {
  
      int i,j;
        
      p.print("// links of the associated graph:\n\n");         
      j = links.size();
        
      for(i=0; i<j; i++){
         ((Link) links.elementAt(i)).save(p);              
      }  
   }



   /**
    * This method return the position that the link, given as
    * parameter, occupies in the list
    * 
    * @param L Link to find
    * @return An integer with the position of the link
    * or -1 if the link isn't in the list
    */

   public int indexOf(Link L) {
      return links.indexOf(L);
   }


   /**
    * Deletes the link of the position given
    * 
    * @param p Position of the link to remove
    */

   public void removeLink(int p) {

      links.removeElementAt(p);
   }

   /**
    * This method is used to obtain the link between the two variables
    * whose names are given as parameters
    * Devuelve la posicion del enlace entre las dos variables cuyo nombre se
    * pasa. Devuelve -1 si no se encuentra
    * 
    * @param T First of the two nodes
    * @param H Second node
    * @return An integer with the position of the link between T and H 
    * or -1 if the link doesn't exists.
    */

   public int getID(String T, String H) {

      int pos;
      Node he,ta;
      Link l;

      for(pos=0;pos<links.size();pos++) {
         l = (Link) links.elementAt(pos);
         he = (Node) l.getHead();
         ta = (Node) l.getTail(); 
         if (l.getDirected()) {
            if ((T.equals(ta.getName())) && (H.equals(he.getName()))) 
               return pos;
         } 
         else 
            if (((T.equals(ta.getName())) && (H.equals(he.getName()))) || 
                ((H.equals(ta.getName())) && (T.equals(he.getName())))) 
                  return pos;
      }

      return (-1);
   }


   /**
    * This method is used to obtain the link that contains the node N
    * 
    * @param N The node to find in one of the links
    * @return An integer with the position of the link
    * or -1 if this link isn't in the list
    */

   public int getID(Node N) {

      int pos;
      Node he,ta;
      Link l;

      for(pos=0;pos<links.size();pos++){
         l = (Link) links.elementAt(pos);
         he = (Node) l.getHead();
         ta = (Node) l.getTail(); 
         if (N.getName().equals(ta.getName()) || N.getName().equals(he.getName())) return pos;
      }

      return (-1);
   }


   /**
    * This method is used to obtain the link between t y h
    * 
    * @param t The first link
    * @param h The second link
    * @return The link between h and t or null if the link can't be
    * found
    */

   public Link getLinks(String t, String h) {
      
      int id=getID(t,h);      
      
      if(id==-1)
         return null;
      else
         return ((Link)links.elementAt(id));
   }


   /**
    * Used to know how many links are in the LinkList
    * 
    * @return An integer with the number of links
    */

   public int size() {
      return((int)links.size());
   }


   /**
    * Used to get the link in the p position
    * 
    * @param p An integer with the position of the link to get
    * @return The link of the p position
    */

   public Link elementAt(int p){
      return ((Link)links.elementAt(p));
   }


   /**
    * Get the enumeration of the LinkList
    * 
    * @return An Enumeration with the LinkList
    */

   public Enumeration elements() {
      return((Enumeration)links.elements());
   }


   /**
    * This method is used to know if t is a parent of h (there is a
    * link from t to h).
    * 
    * @param t
    * @param h
    * @return True if there is a link from t to h 
    * False in other case
    */
    
   public boolean parent(Node t, Node h) {
      
      Link L=getLinks(t.getName(),h.getName());
            
      if(L==null)
         return false;
      else if(t.getName().equals(L.getTail().getName()))
         return true;
      else
         return false;
}


   /**
   * Join this list with the argument.
   * The current list is modified.
   * @param list a LinkList.
   */

   public void join(LinkList list) {
    
      int i;
      Link link;
       
      for (i=0 ; i<list.size() ; i++) {
         link = (Link)list.elementAt(i);
         if (getID(link.getTail().getName(),link.getHead().getName()) == -1)
            insertLink(link);
      }

   }


   /**
    * Creates a copy of the LinkList that receives the message. The copy
    * is done cloning the vector
    * 
    * @return The copy of the LinkList created.
    */

   public LinkList copy() {
    
      LinkList list;
        
      list = new LinkList();
        
      list.links = (Vector)links.clone();
        
      return list;
   }



   /**
   * Clone a LinkList
   * @return A Clone of the LinkList
   */

   public Object clone(){

      LinkList o = null;
      Link link;

      try {
         o = (LinkList)super.clone();
         for(int i=0 ; i < links.size() ; i++){
	         link = (Link)links.elementAt(i);
	         link = (Link)link.clone();
	         o.links.setElementAt(link,i);
         }
      }
      catch (CloneNotSupportedException e){
         System.out.println("Can't clone LinkList");
      }
      
      return o;

   }
   


} // end of class

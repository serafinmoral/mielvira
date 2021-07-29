import NetworkFrame;
import java.util.Vector;

/* NetworkFrameList.java */
/** This class implements the structure and the methods necesary to manage all
    the NetworkFrames of Elvira */

public class NetworkFrameList {
    
    /** A list with all the NetworkFrames */
    Vector frameList;
    
    
    /** Constructor. */
    
    NetworkFrameList() {
        frameList = new Vector();
    }
    
    
  /**
    * Adds the frame given as parameter at the end of the list.
    * 
    * @param frame The frame to add
    */  
    
    public void insertFrame (NetworkFrame frame) {
        frameList.addElement (frame);
    }
    
   
  /**
    * Creates a new NetworkFrame and adds it at the end of the list.
    */
    
    public void insertFrameIn (Elvira elvira) {
        frameList.addElement (new NetworkFrame(elvira));
    }
  

   /**
    * Deletes the frame gives as argument. If the frame doesn't exits
    * don't do anything
    * 
    * @param frame Frame to remove
    */
   
   public void removeFrame(NetworkFrame frame) {
      frameList.removeElement(frame);
   }
   

   /**
    * Deletes the frame of the position given
    * 
    * @param p Position of the frame to remove
    */

   public void removeFrame(int p) {

      frameList.removeElementAt(p);
   }
   
   
   /**
    * This method return the position that the frame, given as
    * parameter, occupies in the list
    * 
    * @param frame Frame to find
    * @return An integer with the position of the frame
    * or -1 if the frame isn't in the list
    */

   public int indexOf(NetworkFrame frame) {
      return frameList.indexOf(frame);
   }
   
      /**
    * Used to know how many links are in the LinkList
    * 
    * @return An integer with the number of links
    */

   public int size() {
      return((int)frameList.size());
   }


   /**
    * Used to get the link in the p position
    * 
    * @param p An integer with the position of the link to get
    * @return The link of the p position
    */

   public NetworkFrame elementAt(int p){
      return ((NetworkFrame)frameList.elementAt(p));
   }


   

} // end of class

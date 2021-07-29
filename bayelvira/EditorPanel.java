/* EditorPanel.java */

import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;

/**
 * Contains the work area for make modifications
 * in the net topology
 *
 * since 14/09/99
 */

public class EditorPanel extends Canvas implements MouseListener, MouseMotionListener, Cloneable{
       
    private ElviraFrame frame; 
    /**  Store the mode for events in the panel */
    private Bnet bayesNet;
    private int mode; 
    private boolean modifiedNetwork = false;   
    
    Dimension preferredSize = new Dimension(1000,800);
    Dimension minimunSize= new Dimension(10,10);
    
    ElviraPopupMenu contextMenu;
    MouseEvent lastEvent;

    
    /* Variables that store various quantities that must
       be shared among event handling functions */
    boolean newArc = false;
    Point newArcHead = null;
    Node moveNode = null;
    Node arcBottomNode = null;
    Node arcHeadNode = null;

    // constants for drawing entities
    private final int NODE_SIZE  = 26;
    private final int NODE_RADIUS = 13;
    private final int SPACE_DRAW_NODE_NAME = 24;
    private final double ARROW_SIZE = 6.0;
    private final double ARROW_HALF_SIZE = 3.0;
    private final double DISTANCE_HIT_ARC = 200.0;
    private final int SQUARE_SIDE = 7;
 
    // color constants for various graphical elements
    private static final Color NODE_COLOR 	= Color.green;
    private static final Color OBSERVED_NODE_COLOR = Color.blue;
    private static final Color EXPLANATION_NODE_COLOR = Color.orange;

    private static final Color NODE_BORDER_COLOR 	= Color.black;
    private static final Color NODE_NAME_COLOR 	= Color.black;
    private static final Color ARC_COLOR 	= Color.darkGray; 
    private static final Color BACKGROUND_COLOR 	= Color.white;
    
    // network editing modes
    private static final int CREATE_NODE_MODE  = 1;
    private static final int CREATE_LINK_MODE = 2;
    private static final int MOVE_MODE    = 3;
    private static final int DELETE_MODE  = 4;
    private static final int OBSERVE_MODE = 5;
    private static final int QUERY_MODE   = 6;
    private static final int EDIT_VARIABLE_MODE = 7;
    private static final int EDIT_FUNCTION_MODE = 8;
    private static final int EDIT_NETWORK_MODE = 9;
    
    // network type modes
    
    public static final String CREATE_NODE = "Create Node";
    public static final String CREATE_LINK = "Create Link";
    public static final String MOVE = "Move";
    public static final String DELETE = "Delete";
    public static final String EDIT_VARIABLE = "Edit Variable";
    public static final String EDIT_FUNCTION = "Edit Function";
    
    // fonts
    private Font ROMAN	   = new Font("TimesRoman", Font.BOLD, 12);
    private Font HELVETICA = new Font("Helvetica", Font.BOLD, 15);
    private FontMetrics FMETRICS = getFontMetrics(ROMAN);
    private int HEIGHT = (int)FMETRICS.getHeight()/3;

    // for double buffering
    public Image offScreenImage;
    public Graphics offScreenGraphics;
    public Dimension offScreenSize;


 /**
  * Default constructor for NetworkPanel object.
  * 
  * @param frame ElviraFrame
  */
  
   EditorPanel(ElviraFrame frame) {
      
    	this.frame = frame; 
    	
    	modifiedNetwork = false;
    	if (Elvira.getLanguaje()==Elvira.SPANISH) 
    	   contextMenu = new ElviraPopupMenu (this,"Menus_sp");
    	else
    	   contextMenu = new ElviraPopupMenu (this,"Menus");
    	
      // Set mode
      mode = MOVE_MODE;
      
      // set color for background
	   setBackground(BACKGROUND_COLOR); 
	   setVisible(true);
	   setBounds(2, 2, 1000, 800);                  
	   
	   addMouseListener(this);
	   addMouseMotionListener(this);
   }
   
   

   /* ************** Accesing methods ************* */

   /**
    * This method is used to get the mode of the EditorPanel
    * 
    * @return An integer with the actual mode of working
    */
    
   public int getMode () {
      return mode;
   }
   

   /**
    * This method allow access to the MinimumSize variable
    * 
    * @return The minimum size of the panel
    */
   
   public Dimension getMinimumSize() {
	   return minimunSize;
   }


   /**
    * This method allows access to the preferredSize variable
    * 
    * @return The preferred size of the panel
    */

   public Dimension getPreferredSize() {
      return preferredSize;
   }      
   
   
   /**
    * Used to see if the Elvira object has been modified.
    * 
    * @return True if the Elvira object has been modified
    * False in other case
    */
    
   public boolean isModifiedNetwork () {
      return modifiedNetwork;
   }
   
   
   /* ******************* Modifiers ********************* */   

   /**
    * Set the mode for the NetworkPanel.
    * 
    * @param label This string contains the name of the mode to be set
    */
   
   public void setMode(String label) {
      
	   if (label.equals(CREATE_NODE ))
	      mode = CREATE_NODE_MODE;
	   else if (label.equals(CREATE_LINK))
	      mode = CREATE_LINK_MODE;
	   else if (label.equals(MOVE))
	      mode = MOVE_MODE;
	   else if (label.equals(DELETE))
	      mode = DELETE_MODE;	
	   else if (label.equals(EDIT_VARIABLE))
	      mode = EDIT_VARIABLE_MODE;
	   else if (label.equals(EDIT_FUNCTION))
	      mode = EDIT_FUNCTION_MODE;
	   else // default mode;
	      mode = MOVE_MODE;
   }

 
   /**
    * Set the modified variable.
    * 
    * @param mod Contain the value to set
    * @param m
    */
     
    public void setModifiedNetwork (boolean m) {
	   modifiedNetwork = m;
	   frame.mainMenuBar.setModifiedMenus (m);
    } 
    

    public void setBayesNet (Bnet bn) {
      bayesNet = bn;
    }
  
   
   /**
    * Store the QuasiBayesNet object to be displayed in     
    * the NetworkPanel.
    * 
    * @param bn Bnet to be stored
    */
   
   public void load(Bnet bn) {
	   bayesNet = bn;
	   update(getGraphics());	// forced update to clear old network
	   repaint();
   }
   
   
   /**
    * Clear the NetworkPanel
    */
   void clear() {
      bayesNet = new Bnet();
      repaint();  
   }


   public void processMouseEvent (MouseEvent e) {
      Node node = nodeHit(e.getX(),e.getY());            
      
      if (e.isPopupTrigger()) {
         if (node != null) {
            contextMenu.show(this, e.getX(), e.getY());             
            lastEvent = e;
         }
      }
      else if (e.getID() == MouseEvent.MOUSE_PRESSED) {
         mousePressed(e);
      }
      else if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
         mouseDragged(e);
      }
      else if (e.getID() == MouseEvent.MOUSE_RELEASED) {
         mouseReleased(e);
      }
     
   }     

   public void mouseClicked(MouseEvent e){      
   }
   
   public void mouseEntered(MouseEvent e) {
   }
   
   public void mouseExited(MouseEvent e) {
   }
   
    /**
     * Process mouse down events.
     * 
     * @param evt Event produced
     */
   public void mousePressed(MouseEvent evt) {
      Node node = nodeHit(evt.getX(),evt.getY());                  
      Link link;
      arcHeadNode = null;
      arcBottomNode = null;

      if (node == null) { // If no node was clicked on.
      
         if (mode == DELETE_MODE) { // Delete arc
            arcHit(evt.getX(), evt.getY()); 
            
            // Affects arcHeadNode and arcBottomNode
            if ((arcHeadNode != null) && (arcBottomNode != null)) {
                deleteArc();
                arcHeadNode = null;
                arcBottomNode = null;
            }
            else
                ElviraHelpMessages.show(frame, ElviraHelpMessages.notnode);
        }
        
        else if (mode == CREATE_NODE_MODE) { // Create a node
            createNode(evt.getX(), evt.getY());
        }
        else if (mode == MOVE_MODE) {
         
            if (!evt.isShiftDown())
               unSelectAll();
               
            arcHit(evt.getX(), evt.getY()); 
            if ((arcHeadNode != null) && (arcBottomNode != null)) {
               link = bayesNet.getLinkList().getLinks(arcBottomNode.getName(), 
                                                      arcHeadNode.getName());
               if (link!=null)
                  link.setSelected(true);
            }        
            else 
               unSelectAll();
        }
      }
      
      else {   // If a node was clicked on.
      
	      if (mode == MOVE_MODE) { // Move node
              moveNode = node;
              if (!evt.isShiftDown())
                 unSelectAll();
                 
              node.setSelected(true);
         }
         else if (mode == DELETE_MODE) { // Delete node
              deleteNode(node);
         }
         else if (mode == CREATE_LINK_MODE) { // Create arc
              newArc = true;
              arcBottomNode = node;
              newArcHead = new Point(evt.getX(), evt.getY());
         }
         else if (mode == EDIT_VARIABLE_MODE) { // Edit variable node
              editVariable(node);
         }
         else if (mode == EDIT_FUNCTION_MODE) { // Edit function node
              editFunction(node);
         }
      }

      repaint();
      mode = MOVE_MODE;
   }


   /**
    * Process mouse drag events.
    * 
    * @param evt Event produced
    */
    
   public void mouseDragged(MouseEvent evt) {                 
      
      if (moveNode != null) {
         moveNode.setPosX(evt.getX());
         moveNode.setPosY(evt.getY());
      }
      else if (newArc == true) {
         newArcHead = new Point(evt.getX(), evt.getY());
      }
      
      repaint();
      mode = MOVE_MODE;
   }

   public void mouseMoved(MouseEvent evt) {
   }


   /**
    * Process mouse up events.
    * 
    * @param evt Event produced
    */
    
   public void mouseReleased(MouseEvent evt) {
	   int x=evt.getX();
	   int y=evt.getY();
	   if (moveNode != null) {
	      
	      if(x<0)
	         x=0;
	      else 
	         if(x>preferredSize.width)
	            x=preferredSize.width;
	            
	      if(y<0)
	         y=0;
	      else 
	         if(y>preferredSize.height)
	            y=preferredSize.height;
	    
         moveNode.setPosX( x);
         moveNode.setPosY(y);
	      setModifiedNetwork(true);
	      moveNode = null;
	   }
	   else 
	      if (newArc == true) {
            arcHeadNode = nodeHit(x, y);
            if ((arcHeadNode != null) && (arcBottomNode != null)) {
               
               if (arcHeadNode == arcBottomNode) {
                  ElviraHelpMessages.show(frame, ElviraHelpMessages.selfarc);
               }
               else 
               
                  if (bayesNet.hasCycle(arcBottomNode, arcHeadNode)) {
                     ElviraHelpMessages.show(frame, ElviraHelpMessages.circular);
                  }
                  else
                     createArc();
            }
            arcHeadNode = null;
            arcBottomNode = null;
            newArcHead = null;
	        newArc = false;
	      }

	      repaint();
   }


   /**
    * Determine whether a node was hit by a mouse click.
    * 
    * @param x X Position of the mouse click
    * @param y Y Position of the mouse click
    * @return The node hitted
    */
    
   private Node nodeHit(int x, int y) {
      Node node;
      for (Enumeration e = bayesNet.getNodeList().elements(); e.hasMoreElements(); ) {
	      node = (Node)(e.nextElement());
	      
	      if ( (x-node.getPosX()) * (x-node.getPosX()) +
	         (y-node.getPosY()) * (y-node.getPosY()) <
	         NODE_RADIUS * NODE_RADIUS ) {
	         return(node);
	      }
      }
      
      return(null);
   }


   /**
    * Determine whether an arc was hit by a mouse click.
    * 
    * @param x X-position of the mouse click
    * @param y Y-position of the mouse click
    */
    
   private void arcHit(int x, int y) {
      
      Node hnode, pnode;
      double sdpa;
      NodeList parents; // <--- jgamez
	   Graph gr;         // <--- jgamez    

	   for (Enumeration e = bayesNet.getNodeList().elements(); e.hasMoreElements(); ) {
         hnode = (Node)(e.nextElement());
	      gr = new Graph(bayesNet.getNodeList(),bayesNet.getLinkList(),Graph.DIRECTED);
	      parents = (NodeList) gr.parents(hnode);
         for (Enumeration ee = parents.elements(); ee.hasMoreElements(); ) {
            pnode = (Node)(ee.nextElement());
            sdpa = squareDistancePointArc(hnode, pnode, x, y);
            if ((sdpa >= 0.0) && (sdpa <= DISTANCE_HIT_ARC)) {
               arcHeadNode = hnode;
               arcBottomNode = pnode;
            }
         }
      }
	
   }


    /**
     * Determine whether a point is close to the segment    
     * between two nodes (hnode and pnode); if the point    
     * does not lie over or above the segment, return -1.0
     * 
     * @param hnode Head node
     * @param pnode Tail node
     * @param x3 X-position of the point
     * @param y3 Y-position of the point
     */
    
    double squareDistancePointArc(Node hnode,Node pnode, int x3, int y3) {
       int x1, y1, x2, y2;
       double area, squareBase, squareHeight, squareHyp;

       x1 = hnode.getPosX();  // las siguientes 4 descomentadas por jgamez
       y1 = hnode.getPosY();
       x2 = pnode.getPosX();
       y2 = pnode.getPosY();
	
       // Area of the triangle defined by the three points
       area = (double)(x1 * y2 + y1 * x3 + x2 * y3 -
                        x3 * y2 - y3 * x1 - x2 * y1);
       // Base of the triangle
       squareBase = (double)( (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) );
       
       // Height of the triangle
       squareHeight = 4.0 * (area*area) / squareBase;

       // Maximum possible distance from point to extreme points
       squareHyp = squareBase + squareHeight;
       
       // Check first extreme point
       if (squareHyp < ((double)( (x3-x1)*(x3-x1) + (y3-y1)*(y3-y1)) ))
          return(-1.0);
       // Check second extreme point
       if (squareHyp < ((double)( (x3-x2)*(x3-x2) + (y3-y2)*(y3-y2)) ))
          return(-1.0);

       // Requested distance is the height of the triangle
       return(squareHeight );
   }

   
   /**
    * Update the screen with the network.
    * 
    * @param g Graphics interface
    */
    
   public synchronized void update(Graphics g) {
      
      // prepare new image offscreen
    	Dimension d=getSize();
    	MediaTracker tracker;
    	
	   if ( (offScreenImage == null) ) {	    
	      offScreenImage = createImage(preferredSize.width,preferredSize.height);
	      tracker = new MediaTracker(this);
	      
	      try { // wait to image to be constructed
		      tracker.addImage(offScreenImage, 0);
		      tracker.waitForID(0,0);
	      } 
	      catch (InterruptedException e) { }	    
	      
	      offScreenSize=preferredSize;
    	   offScreenGraphics = offScreenImage.getGraphics();
	   }
	   
    	offScreenGraphics.setColor(BACKGROUND_COLOR);
	   offScreenGraphics.fillRect(0, 0, preferredSize.width, preferredSize.height);
    	paint(offScreenGraphics);
    	g.drawImage(offScreenImage, 0, 0, null);
   }
   
   
  /**
   * Paint the network.
   * 
   * @param g Graphics interface
   */
    
   public void paint(Graphics g) {
      
      //Node node, parent;
	   Node node;
	   Link link;    // añadido <----------- jgamez
      Enumeration e, ee;      
            
      if (bayesNet == null) return;

      g.setColor(ARC_COLOR);
    	// draw a new arc upto current mouse position
	
	   if (newArc)
    	   g.drawLine(arcBottomNode.getPosX(), arcBottomNode.getPosY(),
    	             newArcHead.x, newArcHead.y);

    	// draw all arcs
	   for (e=bayesNet.getLinkList().elements(); e.hasMoreElements(); ) {
	      link = (Link) e.nextElement();
	      drawArc(g, link);  // se llama al reves 
	   }		
	   
    	// draw the nodes
    	g.setFont(HELVETICA);
    	
    	for (e = bayesNet.getNodeList().elements(); e.hasMoreElements(); ) {
    	   node = (Node)e.nextElement();       
	      g.setColor(NODE_COLOR);
	      
    	   if (node.getPosX() >= 0)
            g.drawOval(node.getPosX()-NODE_RADIUS,
       	              node.getPosY()-NODE_RADIUS,
		   	           NODE_SIZE, NODE_SIZE);      

 	    	g.setColor(NODE_NAME_COLOR);
 	    	g.drawString(node.getName(),
 	    	        node.getPosX() - SPACE_DRAW_NODE_NAME,
 	    	        node.getPosY() + SPACE_DRAW_NODE_NAME);
 	    	        
 	    	if (node.isSelected()) 	    	    	    	    
 	    	   paintNodeSelected (g, new Point(node.getPosX(), node.getPosY()));
      }
      
   }
   
   /**
    * Auxiliary function that draws an arc.
    * 
    * @param g Graphics interface
    * @param node Node where the arc begin
    * @param parent Node where the arc finish
    */
    
   private void drawArc(Graphics g, Link link) {
   
      Node node = link.getHead();
      Node parent = link.getTail();      
      int nodeX, nodeY, parentX, parentY;
     	int x1, x2, x3, y1, y2, y3;
      double dirX, dirY, distance;
   	double headX, headY, bottomX, bottomY;

      // calculate archead
      nodeX = node.getPosX();
      nodeY = node.getPosY();
      parentX = parent.getPosX();
      parentY = parent.getPosY();
      
      dirX = (double)(nodeX - parentX);
      dirY = (double)(nodeY - parentY);

      distance = Math.sqrt(dirX * dirX + dirY * dirY);

      dirX /= distance;
      dirY /= distance;

      headX = nodeX - (NODE_RADIUS + ARROW_SIZE) * dirX;
      headY = nodeY - (NODE_RADIUS + ARROW_SIZE) * dirY;

      bottomX = parentX + NODE_RADIUS * dirX;
      bottomY = parentY + NODE_RADIUS * dirY;

      x1= (int)(headX - ARROW_HALF_SIZE*dirX + ARROW_SIZE*dirY);
	   x2= (int)(headX - ARROW_HALF_SIZE*dirX - ARROW_SIZE*dirY);
      x3= (int)(headX + ARROW_SIZE*dirX);

    	y1= (int)(headY - ARROW_HALF_SIZE*dirY - ARROW_SIZE*dirX);
      y2= (int)(headY - ARROW_HALF_SIZE*dirY + ARROW_SIZE*dirX);
	   y3= (int)(headY + ARROW_SIZE*dirY);

      int archeadX[] = { x1, x2, x3, x1 };
	   int archeadY[] = { y1, y2, y3, y1 };

      // draw archead
  	   g.drawLine((int)bottomX, (int)bottomY,
  	               (int)headX, (int)headY);
      g.fillPolygon(archeadX, archeadY, 4);
      
      if (link.isSelected())
	      paintArcSelected (g, new Point ((int) headX+9, (int) headY),	                                      
	                           new Point ((int) bottomX-16, (int) bottomY));
      
   }
   
   /** This function draws a node with a width according to its name's length */
   
   public void drawNode (Graphics g, Node node) {
      int nameLength = node.getName().length();
      int size = ROMAN.getSize();
      int namePixels = nameLength * size;
      int radiusX, radiusY, nodeWidth, nodeHeight;
      
      if (namePixels < NODE_SIZE) 
         g.drawOval(node.getPosX()-NODE_RADIUS,
    	              node.getPosY()-NODE_RADIUS,
			           NODE_SIZE, NODE_SIZE);      
	   else {
	      nodeHeight = NODE_SIZE;
	      nodeWidth = namePixels + 20;
	      radiusY = NODE_SIZE;
	      radiusX = nodeWidth / 2;
	      g.drawOval (node.getPosX()-radiusX,
	                  node.getPosY()-radiusY,
	                  nodeWidth, nodeHeight);	      
	   }
   }

   
      /**
       * Create a node.
       * 
       * @param x X-position where the node is going to be created
       * @param y Y-position where the node is going to be created
       */
    
   void createNode(int x, int y) {
      bayesNet.createNode(x, y);
	   setModifiedNetwork(true);
   }

   /**
    * Create an arc. The bottom and head nodes of the arc 
    * are stored in the variables arcBottomNode and         
    * arcHeadNode.
    */
    
   void createArc() {
      bayesNet.createLink(arcBottomNode, arcHeadNode);  // <-- jgamez
      setModifiedNetwork(true);
   }


   /**
    * Delete a node.
    * 
    * @param node Node to delete
    */
    
   void deleteNode(Node node) {
      bayesNet.removeNode(node);
	   setModifiedNetwork(true);
   }

   /**
    * Delete an arc. The bottom and head nodes of the arc  
    * are stored in the variables arcBottomNode and     
    * arcHeadNode.
    */
    
   void deleteArc() {
      bayesNet.removeLink(arcBottomNode, arcHeadNode);  // <--- jgamez
	   setModifiedNetwork(true);
   }

   
   /**
    * Edit the components of a  node.
    * 
    * @param node Node to edit
    */
    
   void editVariable(Node node) {
      Dialog d = new EditVariableDialog(frame, node);
      d.show();
	   setModifiedNetwork(true);
   }


   /**
    * Edit the function in a node.
    * 
    * @param node Node which function will be edited
    */
    
   void editFunction(Node node) {
    
	   switch (node.getTypeOfVariable()){
	      
	      case 0: ElviraHelpMessages.show(frame, ElviraHelpMessages.notContinuousYet);
		           break; // continuous
		           
          case 1: Dialog d = new FSEditFunctionDialog(frame, node);
        	        d.show(); 
		           break; // finiteStates
		           
	      case 2: ElviraHelpMessages.show(frame, ElviraHelpMessages.notInfiniteDiscreteYet);
		           break; // infiniteDiscrete
		           
	      case 3: ElviraHelpMessages.show(frame, ElviraHelpMessages.notMixedYet);
		           break; // mixed
	   }
	   
	   setModifiedNetwork(true);
	   
   }

   /**
    * Edit the network.
    */
    
   void editNetwork() {
      Dialog d = new EditNetworkDialog(frame, bayesNet);
      d.setVisible(true);
      setModifiedNetwork(true);
   }

   
/* ************** FUNCTIONS USED FOR SELECT-UNSELECT ************** */   
   
   public void paintNodeSelected (Graphics g, Point position) {
      g.drawRect(position.x-(NODE_RADIUS+SQUARE_SIDE), 
                 position.y-(NODE_RADIUS+SQUARE_SIDE), 
                 SQUARE_SIDE, SQUARE_SIDE);     
      g.drawRect(position.x+NODE_RADIUS, 
                 position.y-(NODE_RADIUS+SQUARE_SIDE), 
                 SQUARE_SIDE, SQUARE_SIDE);     
      g.drawRect(position.x-(NODE_RADIUS+SQUARE_SIDE), 
                 position.y+NODE_RADIUS, 
                 SQUARE_SIDE, SQUARE_SIDE);     
      g.drawRect(position.x+NODE_RADIUS, 
                 position.y+NODE_RADIUS, 
                 SQUARE_SIDE, SQUARE_SIDE);                                       
   }
   
   public void paintArcSelected (Graphics g, Point headPosition, Point tailPosition) {
      g.drawRect(headPosition.x-NODE_RADIUS, headPosition.y, 
                 SQUARE_SIDE, SQUARE_SIDE);
      g.drawRect(tailPosition.x+NODE_RADIUS, tailPosition.y, 
                 SQUARE_SIDE, SQUARE_SIDE);                              
   }
   
   
   public void unSelectAll () {
      int i;
      
      for (i=0; i<bayesNet.getNodeList().size(); i++) {
         bayesNet.getNodeList().elementAt(i).setSelected(false);
      }
      
      for (i=0; i<bayesNet.getLinkList().size(); i++) {
         bayesNet.getLinkList().elementAt(i).setSelected(false);
      }
   }
    
   
   
}  // end of EditorPanel class






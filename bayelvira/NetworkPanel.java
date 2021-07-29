import java.io.*;
import java.awt.*;
import java.util.*;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;


public class NetworkPanel extends Canvas implements MouseListener, MouseMotionListener{
    /** Used for changing mouse cursor */
    private EditorFrame Frame; 
    /**  Store the mode for events in the panel */
    private int Mode; 
    /** The object with the Bayes net */
    private Bnet Bnet; 
    
    Dimension PreferredSize = new Dimension(1000,800);
    Dimension MinimunSize= new Dimension(10,10);

    
    /* Variables that store various quantities that must
       be shared among event handling functions */
    boolean NewArc = false;
    Point NewArcHead = null;
    Node MoveNode = null;
    Node ArcBottomNode = null;
    Node ArcHeadNode = null;

    // constants for drawing entities
    private final int NODE_SIZE  = 26;
    private final int NODE_RADIUS = 13;
    private final int SPACE_DRAW_NODE_NAME = 24;
    private final double ARROW_SIZE = 6.0;
    private final double ARROW_HALF_SIZE = 3.0;
    private final double DISTANCE_HIT_ARC = 200.0;
 
    // color constants for various graphical elements
    private static final Color NODE_COLOR 	= Color.green;
    private static final Color OBSERVED_NODE_COLOR = Color.blue;
    private static final Color EXPLANATION_NODE_COLOR = Color.orange;

    private static final Color NODE_BORDER_COLOR 	= Color.black;
    private static final Color NODE_NAME_COLOR 	= Color.black;
    private static final Color ARC_COLOR 	= Color.darkGray; 
    private static final Color BACKGROUND_COLOR 	= Color.white;
    
    // network editing modes
    private static final int CREATE_MODE  = 1;
    private static final int MOVE_MODE    = 2;
    private static final int DELETE_MODE  = 3;
    private static final int OBSERVE_MODE = 4;
    private static final int QUERY_MODE   = 5;
    private static final int EDIT_VARIABLE_MODE    = 6;
    private static final int EDIT_FUNCTION_MODE = 7;
    private static final int EDIT_NETWORK_MODE = 8;
    
    // fonts
    private Font ROMAN	   = new Font("TimesRoman", Font.BOLD, 12);
    private Font HELVETICA = new Font("Helvetica", Font.BOLD, 15);
    private FontMetrics FMETRICS = getFontMetrics(ROMAN);
    private int HEIGHT = (int)FMETRICS.getHeight()/3;

    // for double buffering
    private Image OffScreenImage;
    private Graphics OffScreenGraphics;
    private Dimension OffScreenSize;


 /**
  * Default constructor for NetworkPanel object.
  * 
  * @param frame EditorFrame
  */
  
   NetworkPanel(EditorFrame frame) {
      
    	this.Frame = frame;
    	
    	// Create default Bnet
    	Bnet = new Bnet();
    	
      // Set mode
      Mode = CREATE_MODE;
      
      // set color for background
	   setBackground(BACKGROUND_COLOR);
	   
	   addMouseListener(this);
	   addMouseMotionListener(this);
   }


   /**
    * This method allow access to the MinimumSize variable
    * 
    * @return The minimum size of the panel
    */
   
   public Dimension getMinimumSize() {
	   return MinimunSize;
   }


   /**
    * This method allows access to the PreferredSize variable
    * 
    * @return The preferred size of the panel
    */

   public Dimension getPreferredSize() {
      return PreferredSize;
   }

   /**
    * Set the mode for the NetworkPanel.
    * 
    * @param label This string contains the name of the mode to be set
    */
   
   public void setMode(String label) {
      
	   if (label.equals(EditorFrame.createLabel))
	      Mode = CREATE_MODE;
	   else if (label.equals(EditorFrame.moveLabel))
	      Mode = MOVE_MODE;
	   else if (label.equals(EditorFrame.deleteLabel))
	      Mode = DELETE_MODE;	
	   else if (label.equals(EditorFrame.editVariableLabel))
	      Mode = EDIT_VARIABLE_MODE;
	   else if (label.equals(EditorFrame.editFunctionLabel))
	      Mode = EDIT_FUNCTION_MODE;
	   else // default mode;
	      Mode = CREATE_MODE;
   }
   
   
   /**
    * Return the Bnet object displayed in the NetworkPanel.
    */
   
   public Bnet getBnet() {
      return (Bnet);
   }
   
   
   /**
    * Store the QuasiBayesNet object to be displayed in     
    * the NetworkPanel.
    * 
    * @param bn Bnet to be stored
    */
   
   public void load(Bnet bn) {
	   Bnet = bn;
	   update(getGraphics());	// forced update to clear old network
	   repaint();
   }
   
   
   /**
    * Clear the NetworkPanel
    */
   void clear() {
      Bnet = new Bnet();
      repaint();  
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

      if (node == null) { // If no node was clicked on.
      
         if (Mode == DELETE_MODE) { // Delete arc
            arcHit(evt.getX(), evt.getY()); 
            
            // Affects ArcHeadNode and ArcBottomNode
            if ((ArcHeadNode != null) && (ArcBottomNode != null)) {
                deleteArc();
                ArcHeadNode = null;
                ArcBottomNode = null;
            }
            else
                ElviraHelpMessages.show(ElviraHelpMessages.notnode);
        }
        
        else if (Mode == CREATE_MODE) { // Create a node
            createNode(evt.getX(), evt.getY());
        }
        else
            ElviraHelpMessages.show(ElviraHelpMessages.notnode);
      }
      
      else {   // If a node was clicked on.
      
	      if (Mode == MOVE_MODE) { // Move node
              MoveNode = node;
         }
         else if (Mode == DELETE_MODE) { // Delete node
              deleteNode(node);
         }
         else if (Mode == CREATE_MODE) { // Create arc
              NewArc = true;
              ArcBottomNode = node;
              NewArcHead = new Point(evt.getX(), evt.getY());
         }
         else if (Mode == EDIT_VARIABLE_MODE) { // Edit variable node
              editVariable(node);
         }
         else if (Mode == EDIT_FUNCTION_MODE) { // Edit function node
              editFunction(node);
         }
      }

      repaint();
   }


   /**
    * Process mouse drag events.
    * 
    * @param evt Event produced
    */
    
   public void mouseDragged(MouseEvent evt) {
      
      if (MoveNode != null) {
         MoveNode.setPosX(evt.getX());
         MoveNode.setPosY(evt.getY());
      }
      else if (NewArc == true) {
         NewArcHead = new Point(evt.getX(), evt.getY());
      }
      
      repaint();
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
	   if (MoveNode != null) {
	      
	      if(x<0)
	         x=0;
	      else 
	         if(x>PreferredSize.width)
	            x=PreferredSize.width;
	            
	      if(y<0)
	         y=0;
	      else 
	         if(y>PreferredSize.height)
	            y=PreferredSize.height;
	    
         MoveNode.setPosX( x);
	      MoveNode.setPosY(y);
	      Frame.setModified(true);
	      MoveNode = null;
	   }
	   else 
	      if (NewArc == true) {
            ArcHeadNode = nodeHit(x, y);
            if ((ArcHeadNode != null) && (ArcBottomNode != null)) {
               
               if (ArcHeadNode == ArcBottomNode) {
                  ElviraHelpMessages.show(ElviraHelpMessages.selfarc);
               }
               else 
               
                  if (Bnet.hasCycle(ArcBottomNode, ArcHeadNode)) {
                     ElviraHelpMessages.show(ElviraHelpMessages.circular);
                  }
                  else
                     createArc();
            }
            ArcHeadNode = null;
            ArcBottomNode = null;
            NewArcHead = null;
	         NewArc = false;
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
      for (Enumeration e = Bnet.elements(); e.hasMoreElements(); ) {
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
    
   void arcHit(int x, int y) {
      
      Node hnode, pnode;
      double sdpa;
	   NodeList parents; // <--- jgamez
	   Graph gr;         // <--- jgamez    

	   for (Enumeration e = Bnet.elements(); e.hasMoreElements(); ) {
         hnode = (Node)(e.nextElement());
	      gr = new Graph(Bnet.getNodeList(),Bnet.getLinkList());
	      parents = (NodeList) gr.parents(hnode);
         for (Enumeration ee = parents.elements(); ee.hasMoreElements(); ) {
            pnode = (Node)(ee.nextElement());
            sdpa = squareDistancePointArc(hnode, pnode, x, y);
            if ((sdpa >= 0.0) && (sdpa <= DISTANCE_HIT_ARC)) {
               ArcHeadNode = hnode;
               ArcBottomNode = pnode;
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
    	
	   if ( (OffScreenImage == null) ) {	    
	      OffScreenImage = createImage(PreferredSize.width,PreferredSize.height);
	      tracker = new MediaTracker(this);
	      
	      try { // wait to image to be constructed
		      tracker.addImage(OffScreenImage, 0);
		      tracker.waitForID(0,0);
	      } 
	      catch (InterruptedException e) { }	    
	      
	      OffScreenSize=PreferredSize;
    	   OffScreenGraphics = OffScreenImage.getGraphics();
	   }
	   
    	OffScreenGraphics.setColor(BACKGROUND_COLOR);
	   OffScreenGraphics.fillRect(0, 0, PreferredSize.width, PreferredSize.height);
    	paint(OffScreenGraphics);
    	g.drawImage(OffScreenImage, 0, 0, null);
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
            
      if (Bnet == null) return;

      g.setColor(ARC_COLOR);
    	// draw a new arc upto current mouse position
	
	   if (NewArc)
    	   g.drawLine(ArcBottomNode.getPosX(), ArcBottomNode.getPosY(),
    	             NewArcHead.x, NewArcHead.y);

    	// draw all arcs
	   for (e=Bnet.getLinkList().elements(); e.hasMoreElements(); ) {
	      link = (Link) e.nextElement();
	      drawArc(g, link.getHead(),link.getTail());  // se llama al reves 
	   }		
	   
    	// draw the nodes
    	g.setFont(HELVETICA);
    	
    	for (e = Bnet.elements(); e.hasMoreElements(); ) {
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
      }
      
   }
   
   /**
    * Auxiliary function that draws an arc.
    * 
    * @param g Graphics interface
    * @param node Node where the arc begin
    * @param parent Node where the arc finish
    */
    
   private void drawArc(Graphics g, Node node, Node parent) {
      
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
      
   }

   
      /**
       * Create a node.
       * 
       * @param x X-position where the node is going to be created
       * @param y Y-position where the node is going to be created
       */
    
   void createNode(int x, int y) {
      Bnet.createNode(x, y);
	   Frame.setModified(true);
   }

   /**
    * Create an arc. The bottom and head nodes of the arc 
    * are stored in the variables ArcBottomNode and         
    * ArcHeadNode.
    */
    
   void createArc() {
      Bnet.createArc(ArcBottomNode, ArcHeadNode);  // <-- jgamez
	   Frame.setModified(true);
   }


   /**
    * Delete a node.
    * 
    * @param node Node to delete
    */
    
   void deleteNode(Node node) {
      Bnet.deleteNode(node);
	   Frame.setModified(true);
   }

   /**
    * Delete an arc. The bottom and head nodes of the arc  
    * are stored in the variables ArcBottomNode and     
    * ArcHeadNode.
    */
    
   void deleteArc() {
      Bnet.deleteArc(ArcBottomNode, ArcHeadNode);  // <--- jgamez
	   Frame.setModified(true);
   }

   
   /**
    * Edit the components of a  node.
    * 
    * @param node Node to edit
    */
    
   void editVariable(Node node) {
      Dialog d = new EditVariableDialog(this, Frame, Bnet, node);
      d.setVisible(true);
	   Frame.setModified(true);
   }


   /**
    * Edit the function in a node.
    * 
    * @param node Node which function will be edited
    */
    
   void editFunction(Node node) {
	   switch (node.getTypeOfVariable()){
	      
	      case 0: ElviraHelpMessages.show(ElviraHelpMessages.notContinuousYet);
		           break; // continuous
		           
         case 1: Dialog d = new FSEditFunctionDialog(Frame, Bnet, node);
        	        d.setVisible(true); 
		           break; // finiteStates
		           
	      case 2: ElviraHelpMessages.show(ElviraHelpMessages.notInfiniteDiscreteYet);
		           break; // infiniteDiscrete
		           
	      case 3: ElviraHelpMessages.show(ElviraHelpMessages.notMixedYet);
		           break; // mixed
	   }
	   
	   Frame.setModified(true);
	   
   }

   /**
    * Edit the network.
    */
    
   void editNetwork() {
      Dialog d = new EditNetworkDialog(Frame, Bnet);
      d.setVisible(true);
	   Frame.setModified(true);
   }
   
   
}  // end of class

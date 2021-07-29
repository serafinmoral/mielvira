import MultipleTree;

/**
 * Implements nodes of the queue used while sorting
 * and bounding Multiple Trees.
 *
 * @since 25/06/99
 */

public class NodeQueueM {
 
  /**
   * Result Tree
   */  
  MultipleTree res;           
  
  /**
   * Source Tree
   */
  MultipleTree source;        
  
  /**
   * Variable to be put in Res
   */
  FiniteStates var;   
  
  /**
   * Value of information of selecting var
   */
  double information; 
  

  /**
   * factor to update the normalization when this node is expanded
   */
  
  double updateNormalization; 
  

  
  /**
   * Constructs an empty NodeQueue.
   */
  
  public NodeQueueM() {
   
    information = 0.0;
  }
  
  
  /**
   * Constructs a NodeQueue with information as in the argument.
   * @param inf the information value.
   */
  
  public NodeQueueM(double inf) {
   
    information = inf;
  }

  
  /**
   * Creates a new NodeQueue with trees r and s.
   * Also, computes the information value and
   * obtains the node producing it.
   * @param r the tree to be put as res.
   * @param s the source Tree.
   * @param potentialSize size of the potential which
            correspondig tree is s if it were fully expanded.
   */
  
  public NodeQueueM(MultipleTree r, MultipleTree s,
		    long potentialSize,int method,double normalization) {
   
    NodeList list;
    FiniteStates y, yMax;
    double max, inf;
    int i, nv;
    double [] updateFactor;
    
    updateFactor = new double[1];
    
    res = r;
    list = s.getVarList();
    
    source = s;
    
    nv = list.size();
    max = MIN_INF;

    yMax = (FiniteStates)list.elementAt(0);
    
    for (i=0 ; i<nv ; i++) {
      y = (FiniteStates)list.elementAt(i);
      if (method==1) 
	{inf = s.conditionalInformation(y,potentialSize,normalization,updateFactor);}
      else if (method==2) 
        {inf = s.conditionalInformationSimple(y,potentialSize,normalization,updateFactor);}
           else {inf=0.0;}
    if (inf>max) {
	max = inf;
	yMax = y;
	updateNormalization = updateFactor[0];
      }
    }
    
    var = yMax;
    information = max;
  }

  
  
  /**
   * Compares this Node with the argument, according to the
   * information.
   * @param node a NodeQueue.
   * @return 0 if both are equal, -1 if the argument is
   * greater and 1 if the argumen is lower.
   */
  
  public int compares(NodeQueueM node) {
   
    if (information < node.information)
      return -1;
    else
      if (information > node.information)
	return 1;
    return 0;
  }
  
  
  /**
   * @param node a NodeQueue.
   * @return true if this Node is greater than the argument,
   * and false otherwise.
   */
  
  public boolean greaterThan(NodeQueueM node) {
   
    if (compares(node)>0)
      return true;
    return false;
  }

  
  private static final double MIN_INF = 0.0;
  
  private static final double MAX_INF = 1E20;
} // End of class
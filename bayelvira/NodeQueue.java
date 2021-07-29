import ProbabilityTree;

/**
 * Implements nodes of the queue used while sorting
 * and bounding Trees.
 *
 * Last modified: 25/06/99
 */

public class NodeQueue {
 
  /**
   * Result Tree
   */  
  ProbabilityTree res;           
  
  /**
   * Source Tree
   */
  ProbabilityTree source;        
  
  /**
   * Variable to be put in Res
   */
  FiniteStates var;   
  
  /**
   * Value of information of selecting var
   */
  double information; 
  

  
  /**
   * Constructs an empty NodeQueue.
   */
  
  public NodeQueue() {
   
    information = 0.0;
  }
  
  
  /**
   * Constructs a NodeQueue with information as in the argument.
   * @param inf the information value.
   */
  
  public NodeQueue(double inf) {
   
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
  
  public NodeQueue(ProbabilityTree r, ProbabilityTree s,
		   long potentialSize) {
   
    NodeList list;
    FiniteStates y, yMax;
    double max, inf;
    int i, nv;
    
    res = r;
    list = s.getVarList();
    
    source = s;
    
    nv = list.size();
    max = MIN_INF;

    yMax = (FiniteStates)list.elementAt(0);
    
    for (i=0 ; i<nv ; i++) {
      y = (FiniteStates)list.elementAt(i);
      inf = s.information(y,potentialSize);
      if (inf>max) {
	max = inf;
	yMax = y;
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
  
  public int compares(NodeQueue node) {
   
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
  
  public boolean greaterThan(NodeQueue node) {
   
    if (compares(node)>0)
      return true;
    return false;
  }

  
  private static final double MIN_INF = -1E20;
  
  private static final double MAX_INF = 1E20;
}
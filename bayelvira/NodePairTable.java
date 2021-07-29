import java.util.Vector;
import Relation;
import FiniteStates;

/**
 * Class description : Implements a pair of values
 *                     (FiniteStates,Vector), where Vector contains
 *                     Relations where X appears.
 *
 * Last modified : 28/04/99.
 */

public class NodePairTable {

  /**
   * A variable of the net
   */ 
  FiniteStates variable;

  /**
   * Is a vector that contains all the relations where variable 
   * appears
   */
  Vector relations;
  
  
  /**
   * Constructs an NodePairTable with variable var.
   * @param var a FiniteStates variable.
   **/
  
  NodePairTable(FiniteStates var) {
    
    variable = var;
    relations = new Vector();
  }


  /**
   * @return the variable
   */   
  
  public FiniteStates getVariable(){
    return variable;
  }
  
  /**
   * 
   * @return The size of the potential resulting from the
   * combination of all the relations in the list,
   * considering fully expanded potentials and
   * FiniteStates variables.
   */
  
  public double totalSize() {
   
    Relation r;
    double s = 1.0;
    int i;
    
    NodeList nl = new NodeList(); 
        
    for (i=0 ; i<relations.size() ; i++) {
      r = (Relation)relations.elementAt(i);
      nl.join(r.getVariables());
      //s *= FiniteStates.getSize(r.getVariables());
    }

    s = FiniteStates.getSize(nl);
    
    return s;
  }
}
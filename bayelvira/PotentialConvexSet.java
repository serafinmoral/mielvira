/* _________________________________________________________________________

			   PotentialConvexSet

			     Elvira Project

   File: PotentialConvexSet.java
   Path: /home/gte/acu/Desarrollo/bayelvira/PotentialConvexSet.java
   Description: Implements a potential as a list of Potentials. Each
   one for an extrem points of the convex set.
   Created: Wed Sep 29 20:24:33 CEST 1999
   Author: Andres Cano,,244258,,
   Modified: Wed Sep 29 20:26:48 CEST 1999
   Last maintained by: Andres Cano,,244258,,

   RCS $Revision: 1.2 $ $State: Exp $
   

   _________________________________________________________________________

   Note: 

   ________________________________________________________________________ */

import java.util.Vector;
import NodeList;
import FiniteStates;


/**
 * Class : PotentialConvexSet
 * Description: Potential to represent a convex set with its extrem
 * points.
 * Implements a potential as a PotentialTable.
 * There is a transparent variable in the PotentialTable with as
 * many cases as number of extrem points of the convex set.   
 */

public class PotentialConvexSet extends PotentialTable{
  
  PotentialConvexSet() {
  }
  
  /**
   * Constructs a new PotentialConvexSet for a list of
   * variables and a vector of potentials (PotentialTable).
   * @param vars is the list of variables
   * @param C is the vector of potentials
   */
  PotentialConvexSet(NodeList vars,Vector C) {
    super(vars);
    FiniteStates trans_node;
    int nv,i,j;
    Configuration conf; // Used to move by each one of the potentials 
    Configuration conf2; // Used to move by the PotentialConvexSet
    
    trans_node=new FiniteStates(C.size());
    trans_node.setTransparency(FiniteStates.TRANSPARENT);
    trans_node.setName("Transparent");
    variables.addElement(trans_node);

    nv = (int)FiniteStates.getSize(variables);
    values=new double[nv];

    nv=nv/trans_node.getNumStates();
    for(i=0;i<trans_node.getNumStates();i++){
      conf=new Configuration(((Potential)C.elementAt(i)).getVariables());
      for(j=0;j<nv;j++){
	conf2=new Configuration(getVariables(),conf);	
	conf2.putValue(trans_node,i);
	setValue(conf2,((Potential)C.elementAt(i)).getValue(conf));
	conf.nextConfiguration();
      }
    }
    print();
  }
  


}
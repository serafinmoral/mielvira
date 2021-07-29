package elvira.inference.uids;

import java.util.ArrayList;
import java.util.Vector;

import elvira.Configuration;
import elvira.InvalidEditException;
import elvira.Node;
import elvira.NodeList;
import elvira.Relation;
import elvira.RelationList;
import elvira.UID;
import elvira.inference.Propagation;
import elvira.potential.MaxFunction;
import elvira.potential.Potential;
import elvira.potential.PotentialTable;
import elvira.potential.SumFunction;
import elvira.tools.Crono;
import elvira.tools.PropagationStatisticsAOUID;

//Evaluates the GSDAG with dinamic programming (Jensen and Vomleova'02)
public class DinamicUID extends Propagation{

	GSDAG gsdag;
	double utilsForBranchOfRoot[];
	int indexOfOptimalDecBranchOfRoot;
	
	 /** Creates a new instance of BranchBound */
	  public DinamicUID(UID uid) {
	    network = uid;
	    statistics = new PropagationStatisticsAOUID();
	    utilsForBranchOfRoot = null;
	  }
	  
	  
	  public void propagate(){
		  UID uid;
		  NodeGSDAG last;
		  PotentialTable finalPot;
		  PropagationStatisticsAOUID stats;
		  Crono crono=new Crono();
		  double eu;
		  
		((UID)network).createGSDAG();
			
			try {
				gsdag = new GSDAG(network);
			} catch (InvalidEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			initializePotentialsInGSDAG();
			
			stats = (PropagationStatisticsAOUID)statistics;
			stats.addTime(0);
			//stats.addExpectedUtility(getEUOfCurrentStrategy());
			stats.addExpectedUtility(0.0);
			stats.addDecisionToTake(-1);
			
			crono.start();
			eliminateVariablesOfUID(crono,stats);
			stats.addToLastTime(crono.getTime());
			crono.stop();
			//eu = getEUOfCurrentStrategy();
			eu = 0.0;
			System.out.println("The EU of the current strategy of DP is:"+eu);
			finalPot = (PotentialTable)getGsdag().getRoot().getUtilityRelations().elementAt(0).getValues();
			eu = finalPot.maximumValue();
			stats.addExpectedUtility(eu);
			stats.addDecisionToTake(getFirstDecisionTaken());
			
			
			System.out.println("MEU: "+finalPot.maximumValue());
			
			statistics.setFinalExpectedUtility(finalPot);
			computeUtilitiesAndOptimalDecisionForTheFirstBranch();
			
			
	  }
	  
	  private void computeUtilitiesAndOptimalDecisionForTheFirstBranch() {
		// TODO Auto-generated method stub
		  NodeGSDAG root;
		  NodeList children;
		  int numChildren;
		  RelationList relChildren;
		  Relation auxRel;
		  double auxUtil;
		  double maxUtil;
		  
		  indexOfOptimalDecBranchOfRoot = -1;
		  maxUtil = Double.NEGATIVE_INFINITY;
		  
		  root = gsdag.root;
		  
		  children = root.getChildrenNodes();
		  
		  numChildren = children.size();
		  
		  utilsForBranchOfRoot = new double[numChildren];
		  
		  switch (root.type){
		  case CHANCE:
		  case DECISION:
			  break;
		  case BRANCH:
				relChildren = obtainDifferentUtilityRelations(new RelationList(),root);
				if (relChildren!=null){
					for (int i=0;i<relChildren.size();i++){
						auxRel = relChildren.elementAt(i);
						auxUtil=((PotentialTable)auxRel.getValues()).maximumValue();
						utilsForBranchOfRoot[i]=auxUtil;
						if (auxUtil>maxUtil){
							maxUtil=auxUtil;
							indexOfOptimalDecBranchOfRoot = i;
						}
					}
				}
				break;
		  }
	}


	private int getFirstDecisionTaken() {
		  NodeGSDAG rootGSDAG;
		  int first;
		//We don't implement completely this method by the moment.  
		  first = -1;
		  return first;
		  
	/*	  
		  rootGSDAG = gsdag.root;
		  
		  switch(rootGSDAG.type){
		  case CHANCE:
			  first = -1;
			  break;
		  case DECISION:
			  
			  
		  }
		// TODO Auto-generated method stub
		return 0;*/
	}


	public void initializePotentialsInGSDAG(){
		  gsdag.initializePotentials(network.getRelationList());
	  }

private void eliminateVariablesOfUID(Crono crono, PropagationStatisticsAOUID stats) {
		// TODO Auto-generated method stub
	NodeGSDAG last;
	
	last = gsdag.getLastNodeGSDAG();
	evaluateUID(last,crono,stats);	
	}

	//It evaluates the UID starting in the node 'node'. Branches are responsible of the synchronization
//to perform a correct evaluation.
	private void evaluateUID(NodeGSDAG node, Crono crono, PropagationStatisticsAOUID stats) {
	
		NodeGSDAG auxNodeGSDAG;
		NodeList children;
		NodeList parents;
		boolean evaluated = false;
		double eu;
		
		//Process the current node
		switch (node.type){
		case BRANCH:
			evaluated=collectRelationsInBranch(node);
			break;
		case CHANCE:
		case DECISION:
			collectRelationsInChanceOrDecision(node);
			eliminateAllVariablesInNodeGSDAGSequentially(node);
			node.setCompletelyEvaluated(true);
			evaluated = true;
			//We sample the statistics
			stats.addToLastTime(crono.getTime());
			crono.stop();
			//eu = getEUOfCurrentStrategy();
			eu = 0.0;
			System.out.println("The EU of the current strategy of DP is:"+eu);
			stats.addExpectedUtility(eu);
			stats.addDecisionToTake(-1);
			crono.start();
			break;
		}
		if (evaluated){
		//Process the parents
		parents = node.getParentNodes();
		for (int i=0;i<parents.size();i++){
			evaluateUID((NodeGSDAG) parents.elementAt(i),crono,stats);
		}
		}
		
	
}
	
	//It evaluates the node 'node'. Branches are responsible of the synchronization
//	to perform a correct evaluation. It's similar to evaluateUID, but here we only
	//evaluate a nodeGSDAG (i.e. all the variables contained)
		protected boolean evaluateNode(NodeGSDAG node) {
		
			NodeGSDAG auxNodeGSDAG;
			NodeList children;
			NodeList parents;
			boolean evaluated=false;
			
			//Process the current node
			switch (node.type){
			case BRANCH:
				evaluated=collectRelationsInBranch(node);
				break;
			case CHANCE:
			case DECISION:
				collectRelationsInChanceOrDecision(node);
				eliminateAllVariablesInNodeGSDAGSequentially(node);
				node.setCompletelyEvaluated(true);
				evaluated=false;
				break;
			}
			return evaluated;
		
			
		
	}

//It takes the relations of the child in the GSDAG
	protected void collectRelationsInChanceOrDecision(NodeGSDAG node) {
		// TODO Auto-generated method stub
		
		int numChildren;
		NodeGSDAG onlyChild;
		NodeList children;
		
		// TODO Auto-generated method stub
//		Collect the potentials of the children (if necessary)
		children = node.getChildrenNodes();
		
		numChildren = children.size();
			
		if (numChildren==1){
				onlyChild = (NodeGSDAG) children.elementAt(0);
			//Copy the probability potentials of a child
			node.copyRelationsFrom(onlyChild);
		
		}
		else{
			System.out.println("Error in method collectRelationsInChanceOrDecision of class DinamicGSDAG. Decision and chance nodes in GSDAG must have an only child");
			
		}
		
	}


/*	private void eliminateAllVariablesInNodeGSDAG(NodeGSDAG node) {
		// TODO Auto-generated method stub
	//Eliminate all the variables of the nodeGSDAG
		for(String auxName:node.getVariables()){
			System.out.println("Eliminating variable "+auxName);
			eliminateChanceOrDecisionVariable(node,auxName);
		}
		
	}*/

	private void eliminateAllVariablesInNodeGSDAGSequentially(NodeGSDAG node) {
		// TODO Auto-generated method stub
		ArrayList<String> vars;
		String auxName;
	//Eliminate all the variables of the nodeGSDAG
		vars = node.getVariables();
		for (int i=vars.size()-1;i>=0;i--){
		//for(String auxName:node.getVariables()){
			auxName = vars.get(i);
			System.out.println("Eliminating variable "+auxName);
			eliminateChanceOrDecisionVariable(node,auxName);
		}
		
	}


	protected boolean collectRelationsInBranch(NodeGSDAG node) {
	
		int numChildren;
		NodeGSDAG firstChild;
		RelationList commonRelations;
		RelationList differentRelations;
		NodeList children;
		
		// TODO Auto-generated method stub
//		Collect the potentials of the children (if necessary)
		children = node.getChildrenNodes();
		
		boolean evaluated=false;
		//We only do something if all the children have finished their 
		//evaluation
		if (node.areAllChildrenEvaluated()){
			System.out.println("Collecting potentials in branch");
			evaluated = true;
			numChildren = children.size();
			
			if (numChildren>0){
				firstChild = (NodeGSDAG) children.elementAt(0);
			//Copy the probability potentials of a child
			node.copyProbabilityRelationsFrom(firstChild);
			
			
			
			commonRelations = obtainCommonUtilityRelations(node);
						
			node.setUtilityRelations(commonRelations);
			
			if (commonRelations.size()<firstChild.getUtilityRelations().size()){
				//There are different relations in the children
				differentRelations = obtainDifferentUtilityRelations(commonRelations,node);
				node.utilityRelations.insertRelation(maximizeUtilityRelations(differentRelations));
			}
			
		
			}
		}
		else{
			evaluated = false;
			System.out.println("We can't still collect the potentials in the branch node of children:");
			children= node.getChildrenNodes();
			for (int i=0;i<children.size();i++){
				System.out.print(((NodeGSDAG)children.elementAt(i)).getVariables().toString());
			}
			
		}
		
		return evaluated;
				
		
		/*//Distribute the potentials to the parents (if necessary)
		NodeList parents;
		parents = node.getParentNodes();
		for (int i=0;i<parents.size();i++){
			auxNodeGSDAG = (NodeGSDAG) parents.elementAt(i);
			auxNodeGSDAG.copyRelationsFrom(node);
			*/
			
		//}
		
	}
		
		private Relation maximizeUtilityRelations(RelationList utilRels) {
		// TODO Auto-generated method stub
			Relation newRel;
			
			PotentialTable utilPot = (PotentialTable)(utilRels.elementAt(0).getValues());
			for(int j=1;j<utilRels.size();j++){
				utilPot = utilPot.combine((PotentialTable) utilRels.elementAt(j).getValues(),new MaxFunction());
			}
					
			//Create a new relation
		    newRel = new Relation();
		    // Set the kind for the final relation 
		    newRel.setKind(Relation.POTENTIAL);
		    newRel.getVariables().setNodes((Vector)utilPot.getVariables().clone());
		    newRel.setValues(utilPot);
		    
		    return newRel;
	}

//It returns a list with the agregated relation for each children, which will be used to determine the step policy
		private RelationList obtainDifferentUtilityRelations(RelationList commonRelations, NodeGSDAG node) {
		// TODO Auto-generated method stub
		NodeList children;
		NodeGSDAG auxChild;
		RelationList auxUtilRels;
		Relation auxRel;
		RelationList auxDifferents;
		Relation newRel;
		RelationList newDifferents;
		
		
		children = node.getChildrenNodes();
		
		auxDifferents = new RelationList();
		
		newDifferents = new RelationList();
		
		for(int i=0;i<children.size();i++){
			auxDifferents = new RelationList();
			auxChild = (NodeGSDAG) children.elementAt(i);
			auxUtilRels = auxChild.getUtilityRelations();
			//Find out the different relations of each child
			for (int j=0;j<auxUtilRels.size();j++){
				auxRel = auxUtilRels.elementAt(j);
				if (commonRelations.getRelations().contains(auxRel)==false){
					auxDifferents.insertRelation(auxRel);
				}
			}
			//Sum of the different relations for each child
			newRel = sumUtilityRelations(auxDifferents);
			//Add the new relation
			newDifferents.insertRelation(newRel);
			
		}
			
		return newDifferents;
	}

		//It creates a new relation by summing other utility relations
	public static Relation sumUtilityRelations(RelationList utilRels){
		Relation newRel;
		
		PotentialTable utilPot = (PotentialTable)(utilRels.elementAt(0).getValues());
		for(int j=1;j<utilRels.size();j++){
			utilPot = utilPot.combine((PotentialTable) utilRels.elementAt(j).getValues(),new SumFunction());
		}
				
		//Create a new relation
	    newRel = new Relation();
	    // Set the kind for the final relation 
	    newRel.setKind(Relation.POTENTIAL);
	    newRel.getVariables().setNodes((Vector)utilPot.getVariables().clone());
	    newRel.setValues(utilPot);
	    
	    return newRel;
		
	}
	
	


		//It returns the list of utility relations that are commmon to all the children,
		//so they don't have to be considered for the maximization
		public RelationList obtainCommonUtilityRelations(NodeGSDAG node){
			//RelationList utilsNode;
			RelationList utilsOfFirstChild;
			RelationList newRelList;
			int numChildren;
			NodeList children;
			Relation auxRel;
			
			newRelList =new RelationList(); 
			
			//utilsNode = node.getUtilityRelations();
			 
			
			children = node.getChildrenNodes();
			
			utilsOfFirstChild = ((NodeGSDAG)children.elementAt(0)).utilityRelations; 
			
			numChildren = children.size();
			//We insert the relations that are in common with the second child (they will be in common with the other children)
			for (int i=0;i<utilsOfFirstChild.size();i++){
				auxRel = utilsOfFirstChild.elementAt(i);
				//Checking the equality between relations is performed watching their pointers.
				//Else they are considered different.
				if ((numChildren == 1)||
						((NodeGSDAG)children.elementAt(1)).utilityRelations.getRelations().contains(auxRel)){
					newRelList.insertRelation(auxRel);
				}
			}
			return newRelList;
		}
		
		

	

	protected void eliminateChanceOrDecisionVariable(NodeGSDAG node, String name) {
		// TODO Auto-generated method stub
		Node nodeToElim;
		RelationList probRelsOfElim;
		RelationList utilRelsOfElim;
		PotentialTable newProbPot = null;
		PotentialTable probPot = null;
		Relation newProbRel;
		Relation newUtilRel;
		
		nodeToElim = this.network.getNodeList().getNode(name);
		
		
		probRelsOfElim =	node.probabilityRelations.getRelationsOfAndRemove(nodeToElim);
			
		//Elimination of the variable from the probability potentials	
		if (probRelsOfElim.size()>0){
			probPot = (PotentialTable) probRelsOfElim.elementAt(0).getValues();
			for(int j=1;j<probRelsOfElim.size();j++){
				probPot = (PotentialTable) probPot.combine(probRelsOfElim.elementAt(j).getValues());
			}
			//Sum or maximize over nodeToElim
			newProbPot = applyMarginalization(probPot,nodeToElim);
			
	        // Create a new relation that is added to probability relations
            newProbRel = new Relation();
            // Set the kind for the final relation 
            newProbRel.setKind(Relation.POTENTIAL);
            newProbRel.getVariables().setNodes((Vector)newProbPot.getVariables().clone());
            newProbRel.setValues(newProbPot);
      
            //Add the new relation to the remaining probability relations
            node.probabilityRelations.insertRelation(newProbRel);
		}
            
        //Elimination of the variable from the utility potentials
	
		//Combine the utility potentials
            utilRelsOfElim = node.utilityRelations.getRelationsOfAndRemove(nodeToElim);
         
            if (utilRelsOfElim.size()>0){
            
    		PotentialTable utilPot = (PotentialTable)(utilRelsOfElim.elementAt(0).getValues());
			for(int j=1;j<utilRelsOfElim.size();j++){
				utilPot = utilPot.combine((PotentialTable) utilRelsOfElim.elementAt(j).getValues(),new SumFunction());
			}
			if (probPot!=null){
			//Multiply the probability and the utility potential
			utilPot = utilPot.combine(probPot);
			}
			//Sum or maximize the utility potential over nodeToElim
			utilPot = (PotentialTable)(applyMarginalization(utilPot,nodeToElim));
			
		//Division by the probability potential
			if (newProbPot!=null){
			utilPot = (PotentialTable)(utilPot.divide(newProbPot));
			}
			
			//Create a new relation that is added to the utility relations
            newUtilRel = new Relation();
            // Set the kind for the final relation 
            newUtilRel.setKind(Relation.POTENTIAL);
            newUtilRel.getVariables().setNodes((Vector)utilPot.getVariables().clone());
            newUtilRel.setValues(utilPot);
      
            //Add the new relation to the remaining utility relations
            node.utilityRelations.insertRelation(newUtilRel);
            }
            //We annotate the variable that has just been eliminated
            node.setLastEliminatedVariable(name);
            
            //We update evaluated in node
            node.setCompletelyEvaluated(name==node.getVariables().get(0));
            
   }


	private PotentialTable applyMarginalization(PotentialTable pot, Node nodeToElim) {
		// TODO Auto-generated method stub
		PotentialTable newPot=null;
		Vector vars;
		
		switch(nodeToElim.getKindOfNode()){
		case Node.CHANCE:
			newPot = (PotentialTable) pot.addVariable(nodeToElim);
			break;
		case Node.DECISION:
			vars = new Vector(pot.getVariables());
			vars.removeElement(nodeToElim);
			newPot = (PotentialTable) pot.maxMarginalizePotential(vars);
			break;
		default:
			System.out.println("Error: Method applyMarginalization in class DinamicGSDAG. The nodeToElim must CHANCE or DECISION");
		}
		return newPot;
	}


	public GSDAG getGsdag() {
		return gsdag;
	}


	public double[] getUtilsForBranchOfRoot() {
		return utilsForBranchOfRoot;
	}


	public int getIndexOfOptimalDecBranchOfRoot() {
		return indexOfOptimalDecBranchOfRoot;
	}


	
	public double getEUOfCurrentStrategy() {
		ArrayList<NodeGSDAG> nearestDescsEvaluated;
		NodeGSDAG auxNodeGSDAG;
		RelationList auxInstantUtilRels;
		RelationList auxInstantProbRels;
		double euOfDescs[];
		double probSelectDesc[];
		double globalEU;
		NodeGSDAG nodeGSDAG;
		// TODO Auto-generated method stub
		
		nodeGSDAG = gsdag.root;
		
		nearestDescsEvaluated = nodeGSDAG.obtainNearestDescendantsWithSomeVariablesEliminated();
		
		
		globalEU = 0.0;
		euOfDescs = new double[nearestDescsEvaluated.size()];
		probSelectDesc = new double[nearestDescsEvaluated.size()];
		
		for (int i=0;i<nearestDescsEvaluated.size();i++){
			auxNodeGSDAG = nearestDescsEvaluated.get(i);
			//We instantiate the utility potentials
			auxInstantUtilRels = copyRelations(auxNodeGSDAG.utilityRelations);
			auxInstantProbRels = copyRelations(auxNodeGSDAG.probabilityRelations);
			//We calculate the value of the EU for the utility potentials instantiated
			euOfDescs[i] = NodeAOUID.getEU(auxInstantProbRels,auxInstantUtilRels);
			probSelectDesc[i]=nodeGSDAG.obtainProbabilityOfSelect(auxNodeGSDAG);
			globalEU = globalEU+euOfDescs[i]*probSelectDesc[i];
		}
		return globalEU;
		
		
	}
	
	
	/**
	 * It copies a list of relations
	 * It uses the atrribute 'instantiations'
	 * The relations of the parameter 'relations' are changed if 'nodeVar' appears in them
	 * @param relations A list of the relations after the instantiation
	 */
	protected RelationList copyRelations(RelationList relations) {
		// TODO Auto-generated method stub
	
		Relation auxNewRel;
		RelationList newRelations;
		Relation auxRel;
		
		ArrayList<Relation> auxRelations;
		
		newRelations = new RelationList();
		
		
			
		
		for (int i=0;i<relations.size();i++){
			auxRel = relations.elementAt(i);
				//Add the new relation restricted to the new configuration of variables
				auxNewRel = auxRel.copy();
				
				newRelations.insertRelation(auxNewRel);
			
		}
		
		return newRelations;
		
		
	}
	
	
	
	
	
}//end of class

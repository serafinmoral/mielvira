package elvira.inference.uids;

import java.util.ArrayList;

import elvira.Node;
import elvira.NodeList;
import elvira.RelationList;
import elvira.inference.uids.NodeAOUID.TypeOfNodeAOUID;

//It encapsulates the same information of the NodeGSDAG but with the utility and probability
//potentials during the evaluation of the GSDAG, and also the name of the next variable
//to be eliminated from the potentials in the NodeGSDAG

public class NodeGSDAG extends Node {
	

	
	
	public enum TypeOfNodeGSDAG {DECISION, CHANCE, BRANCH}

//	Names of the variables of the node of the GSDAG
	ArrayList<String> variables;
	
	TypeOfNodeGSDAG type;
	
	RelationList probabilityRelations;
	
	RelationList utilityRelations;
	
	//Indicates the next variable of the NodeGSDAG that must be eliminated from the 
	//probability and utility relations. In branch nodes this value is "". This value
	//"" is also used when there aren't more variables to eliminate in the NodeGSDAG,
	//but we leave the obtained potentials in the same NodeGSDAG
	String lastEliminatedVariable;
	
	//It indicates if all the variables of the potentials of the node have been eliminated,
	//so the parents of the node can get the obtained potentials
	boolean completelyEvaluated;
	
	
	
	
	public NodeGSDAG(TypeOfNodeGSDAG typeNode) {
		// TODO Auto-generated constructor stub
		type = typeNode;
		variables = new ArrayList();
		probabilityRelations=new RelationList();
		utilityRelations=new RelationList();
		lastEliminatedVariable = "";
		completelyEvaluated = false;
		
	}




	@Override
	public double undefValue() {
		// TODO Auto-generated method stub
		return 0;
	}




	public ArrayList<String> getVariables() {
		return variables;
	}




	public void setVariables(ArrayList<String> variables) {
		this.variables = variables;
	}

	public void print(){
		System.out.println(type.toString());
		for(String aux:variables){
			System.out.println(aux);
		}
	}




	public TypeOfNodeGSDAG getTypeOfNodeGSDAG() {
		// TODO Auto-generated method stub
		return type;
	}




	public RelationList getProbabilityRelations() {
		return probabilityRelations;
	}




	public void setProbabilityRelations(RelationList probabilityRelations) {
		this.probabilityRelations = probabilityRelations;
	}




	public RelationList getUtilityRelations() {
		return utilityRelations;
	}




	public void setUtilityRelations(RelationList utilityRelations) {
		this.utilityRelations = utilityRelations;
	}
	
	public void copyRelationsFrom(NodeGSDAG node){
		RelationList probRels;
		RelationList utilRels;
		
		probRels = node.getProbabilityRelations();
		utilRels = node.getUtilityRelations();
	//Copy of the pointers to the probability relations
		probabilityRelations = new RelationList();
	for (int i=0;i<probRels.size();i++){
		probabilityRelations.insertRelation(probRels.elementAt(i));
	}
	
//	Copy of the pointers to the utility relations
	utilityRelations = new RelationList();
	for (int i=0;i<utilRels.size();i++){
		utilityRelations.insertRelation(utilRels.elementAt(i));
	}
	}




	public boolean areAllChildrenEvaluated() {
		// TODO Auto-generated method stub
		boolean allEvaluated = true;
		NodeList childrenNodes;
		
		childrenNodes = this.getChildrenNodes();
		
		for (int i=0;(i<childrenNodes.size())&&allEvaluated;i++){
			if (((NodeGSDAG) childrenNodes.elementAt(i)).isCompletelyEvaluated()==false){
				allEvaluated = false;
			}
			
		}
		
		return allEvaluated;
	}




	public void copyProbabilityRelationsFrom(NodeGSDAG node) {
		// TODO Auto-generated method stub
		RelationList probRels;
		
		
		probRels = node.getProbabilityRelations();
		
	//Copy of the pointers to the probability relations
		probabilityRelations = new RelationList();
	for (int i=0;i<probRels.size();i++){
		probabilityRelations.insertRelation(probRels.elementAt(i));
	}
			
	}




	public void copyUtilityRelationsFrom(NodeGSDAG node) {
		// TODO Auto-generated method stub
		
	RelationList utilRels;
	

		utilRels = node.getUtilityRelations();

	
//	Copy of the pointers to the utility relations
	utilityRelations = new RelationList();
	for (int i=0;i<utilRels.size();i++){
		utilityRelations.insertRelation(utilRels.elementAt(i));
	}
	}




	public boolean isCompletelyEvaluated() {
		return completelyEvaluated;
	}




	public void setCompletelyEvaluated(boolean evaluated) {
		this.completelyEvaluated = evaluated;
	}




	public String getLastEliminatedVariable() {
		return lastEliminatedVariable;
	}




	public void setLastEliminatedVariable(String lastEliminatedVariable) {
		this.lastEliminatedVariable = lastEliminatedVariable;
	}




	/**
	 * @return list of NodeGSDAG that are descendants of this nodeGSDAG and have calculated their potentials.
	 * If all the parents of a node are included in this list then it's not included.
	 * This method is used to calculate the heuristic of the nodeGSDAGs
	 */
	public ArrayList<NodeGSDAG> obtainNearestDescendantsWithSomeVariablesEliminated() {
		// TODO Auto-generated method stub
		ArrayList<NodeGSDAG> list;
		
		
		list = new ArrayList();
		
		auxObtainNearestDescendantsWithSomeVariablesEliminated(list);
		
		return list;
		
	}
	
	
	/**
	 * @return list of NodeGSDAG that are descendants of this nodeGSDAG and have calculated their potentials.
	 * If all the parents of a node are included in this list then it's not included.
	 * This method is used to calculate the heuristic of the nodeGSDAGs
	 */
	public ArrayList<NodeGSDAG> obtainMinimalSetOfNearestDescendantsWithSomeVariablesEliminated() {
		// TODO Auto-generated method stub
		ArrayList<NodeGSDAG> cloneList;
		ArrayList<NodeGSDAG> list;
		ArrayList<NodeGSDAG> finalList= new ArrayList();
		boolean includeAuxNode1;
		
		
		list = new ArrayList();
		
		auxObtainNearestDescendantsWithSomeVariablesEliminated(list);
		
		for (NodeGSDAG auxNode1:list){
			//We see if auxNode1 has descendants in the list
			includeAuxNode1 = true;
			for (NodeGSDAG auxNode2:list){
				if (auxNode1!=auxNode2){
					if (auxNode2.isDescendantOf(auxNode1)){
						includeAuxNode1 = false;
					}
				}
			}
			//If it does not have descendants then it's included in the finalList
			if (includeAuxNode1){
				finalList.add(auxNode1);
			}
		
		}
		
		
		return finalList;
		
	}
	
	/**
	 * Auxiliar method of 'obtainNearestDescendantsEvaluated
	 * @param list
	 */
	private void auxObtainNearestDescendantsWithSomeVariablesEliminated(ArrayList<NodeGSDAG> list){
		NodeList children;
		
		//We check if some potentials have been calculated
		if (utilityRelations.size()==0){
			//We have to look for in the children
			children = this.getChildrenNodes();
			if (children!=null){
				for (int i=0;i<children.size();i++){
					((NodeGSDAG)children.elementAt(i)).auxObtainNearestDescendantsWithSomeVariablesEliminated(list);
				}
			}
		}
		else{//Some potentials are calculated in the node
			//We include this NodeGSDAG in the list of the descendants and we don't look for in the children
			if (list.contains(this)==false){
				list.add(this);
			}
		}
	}




	public String nextVariable(String nameOfVariable) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub		// TODO Auto-generated method stub
		int indexOfVar;
		ArrayList<String> vars;
		int newIndex;
		String nextVar;
		
		if (type == TypeOfNodeGSDAG.BRANCH){//The method shouldn't be invoked for branches
				nextVar = null;
				System.out.println("Error. The method nextNodeGSDAG shouldn't be invoked for branches");
		}
		else{
			vars = getVariables();
			indexOfVar = vars.indexOf(nameOfVariable);
			newIndex = indexOfVar+1;
			
			if (newIndex < vars.size()){//There's more variables to process in the same NodeGSDAG
				nextVar = vars.get(newIndex);
			}
			else{//All variables in the NodeGSDAG have been processed, so we have to process the sucessor
				vars = ((NodeGSDAG)getChildrenNodes().elementAt(0)).getVariables();
				if ((vars == null)||(vars.size()==0)){
					nextVar = "";//The next node is a branch

				}
				else{
					nextVar = vars.get(0); 
				}
				
			}
		}
		return nextVar;
	}




	@Override
	public boolean equals(Object n) {
		// TODO Auto-generated method stub
		return (this==n);
	}




	/**
	 * @param auxNodeGSDAG
	 * @return The probability of that auxNodeGSDAG is selected in a strategy that
	 * select randomly with uniform distribution any of the next branches.
	 */
	public double obtainProbabilityOfSelect(NodeGSDAG auxNodeGSDAG) {
		NodeList children;
		double probChild;
		double numChildren;
		double totalProb = 0;
		
		// TODO Auto-generated method stub
		//We check if some potentials have been calculated
		if (utilityRelations.size()==0){
			//We have to look for in the children
			children = this.getChildrenNodes();
			if (children!=null){
				numChildren = children.size();
				totalProb = 0.0;
				for (int i=0;i<numChildren;i++){
					probChild=((NodeGSDAG)children.elementAt(i)).obtainProbabilityOfSelect(auxNodeGSDAG);
					totalProb = totalProb + probChild/numChildren;
					
				}
			}
		}
		else{//The probability is 1.0 iff we have reached auxNodeGSDAG
			if (this==auxNodeGSDAG){
				totalProb = 1.0;
			}
			else{
				totalProb = 0.0;
			}
		}
		
		return totalProb;
	}
	
	/**
	 * @param node
	 * @return true if 'this' is descendant of node
	 */
	public boolean isDescendantOf(NodeGSDAG node){
		if (this.isChildrenOf(node)){
			return true;
		}
		else{
			NodeList children;
			children = node.getChildrenNodes();
			for (int i=0;i<children.size();i++){
				if (this.isDescendantOf((NodeGSDAG) children.elementAt(i))) return true;
			}
		}
		return false;
	
	}
	
	public boolean isChildrenOf(NodeGSDAG node){
		NodeList children;
		
		children = node.getChildrenNodes();
		
		for (int i=0;i<children.size();i++){
			if (children.elementAt(i)==this) return true;
		}
		return false;
	}


	public int distanceToLastNode() {
		// TODO Auto-generated method stub
		NodeList children;
		int dist=0;
		
		children = getChildrenNodes();
		if ((children==null)||(children.size()==0)){//It's the last node
			dist=0;
		}
		else{
			dist = 1+((NodeGSDAG) children.elementAt(0)).distanceToLastNode();
		}
		return dist;
	}
	
	public int distanceToRootNode() {
		// TODO Auto-generated method stub
		NodeList parents;
		int dist=0;
		
		parents = getParentNodes();
		if ((parents==null)||(parents.size()==0)){//It's the root node
			dist=0;
		}
		else{
			dist = 1+((NodeGSDAG) parents.elementAt(0)).distanceToRootNode();
		}
		return dist;
	}
		
	
}//end of class

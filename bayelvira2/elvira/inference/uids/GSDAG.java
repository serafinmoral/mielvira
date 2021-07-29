package elvira.inference.uids;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;

import elvira.Graph;
import elvira.InvalidEditException;
import elvira.Link;
import elvira.Network;
import elvira.Node;
import elvira.NodeList;
import elvira.Relation;
import elvira.RelationList;
import elvira.UID;
import elvira.inference.uids.NodeGSDAG.TypeOfNodeGSDAG;

/**
 * @author Manolo
 * 
 */
public class GSDAG extends Graph {

	NodeGSDAG root;
	UID uid;

	public GSDAG(Network network) throws InvalidEditException {
		MNode rootgsdagMarta;

		Graph gsdagMarta = ((UID) network).getGraph();
		
		uid = (UID)network;

		rootgsdagMarta = getRootOfGSDAGMarta(gsdagMarta);
		root = constructGSDAG(rootgsdagMarta, (UID) network);
		
		System.out.println("GSDAG with "+root.getChildren().size()+ "children of the root");

	}

	/**
	 * Inserts a node in the graph if it doesn't exist in the graph.
	 * 
	 * @param n
	 *            the node to insert.
	 */

	public void addNode(Node n) {

		this.nodeList.insertNode(n);
	}

	@Override
	public void createLink(Node tail, Node head) throws InvalidEditException {
		// TODO Auto-generated method stub

		Link l = new Link(tail, head, true);

		linkList.getLinks().addElement(l);

		tail.getSiblings().getLinks().addElement(l);
		head.getSiblings().getLinks().addElement(l);

		tail.getChildren().getLinks().addElement(l);
		head.getParents().getLinks().addElement(l);

	}

	/**
	 * Method that constructs a GSDAG like in the paper of UIDs from a GSDAG in
	 * Marta's way
	 * 
	 * @param rootgsdagMarta
	 * @return
	 * @throws InvalidEditException
	 */
	protected NodeGSDAG constructGSDAG(MNode rootgsdagMarta, UID uid)
			throws InvalidEditException {
		// TODO Auto-generated method stub
		NodeList childrenOfRoot;
		NodeGSDAG rootGSDAG;
		NodeGSDAG last;
		Link linkLast;
		NodeGSDAG nonObsNodeGSDAG;
		NodeGSDAG fatherOfLast;
		MNode initialMNode = null;

		if (hasMNodeToElim(rootgsdagMarta)){//There are variables to eliminate at the beginning
			rootGSDAG = auxConstructGSDAG(rootgsdagMarta);
		}
		else {
			initialMNode = rootgsdagMarta;
			childrenOfRoot = rootgsdagMarta.getChildrenNodes();
			if (childrenOfRoot == null) {
					rootGSDAG = null;
			} 
			else if (childrenOfRoot.size() == 0) {// An empty graph
				rootGSDAG = null;
			} else if (childrenOfRoot.size() == 1) {
				rootGSDAG = auxConstructGSDAG((MNode) (childrenOfRoot.elementAt(0)));
			} else {
				rootGSDAG = new NodeGSDAG(NodeGSDAG.TypeOfNodeGSDAG.BRANCH);
				this.addNode(rootGSDAG);
				// We add as children all the graphs of the sucessors
				for (int i = 0; i < childrenOfRoot.size(); i++) {
					NodeGSDAG auxNodeGSDAG = auxConstructGSDAG((MNode) (childrenOfRoot
							.elementAt(i)));
					try {
						createLink(rootGSDAG, auxNodeGSDAG);
					} catch (InvalidEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		//We add the variables known from the beginning as the root of the GSDAG
		if ((initialMNode!=null)&&(hasMNodeChanceToElim(initialMNode))){
			 	NodeGSDAG chanceNodeGSDAG = new NodeGSDAG(NodeGSDAG.TypeOfNodeGSDAG.CHANCE);
				chanceNodeGSDAG.setVariables(getNamesOfVariables(initialMNode.getObsEliminate()));

				addNode(chanceNodeGSDAG);
				// Link from the node of decisions to the node of chance
				// variables
				try {
					createLink(chanceNodeGSDAG,rootGSDAG);
					} catch (InvalidEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
				rootGSDAG = chanceNodeGSDAG;
		}
		
		
		
		
		
		
		// We add the non observable variables in a CHANCE node at the end of
		// the GS-DAG. If there isnt' non observable variables we do nothing else
		nonObsNodeGSDAG = computeNonObsNodeGSDAG(uid);
		if (nonObsNodeGSDAG != null) {
			addNode(nonObsNodeGSDAG);
			last = getLastNodeGSDAG(rootGSDAG);
			// The last node (branch) always have an only father
			// We redirect the links to insert the new node
			linkLast = last.getParents().elementAt(0);
			fatherOfLast = (NodeGSDAG) linkLast.getTail();
			removeLink(linkLast);
			createLink(fatherOfLast, nonObsNodeGSDAG);
			createLink(nonObsNodeGSDAG, last);
		}

		return rootGSDAG;

	}

	//Create a nodeGSDAG with the names of the non observable variables
	private NodeGSDAG computeNonObsNodeGSDAG(UID uid) {
		// TODO Auto-generated method stub
		ArrayList nonObservables;
		NodeGSDAG chanceNodeGSDAG;
		
		nonObservables = uid.getNonObservablesArrayList();
		
		if (nonObservables.size() > 0) {

			// Create the NodeGSDAG for the decisions
			chanceNodeGSDAG = new NodeGSDAG(NodeGSDAG.TypeOfNodeGSDAG.CHANCE);
			chanceNodeGSDAG.setVariables(nonObservables);
		} else {
			chanceNodeGSDAG = null;
		}
			
		
		return chanceNodeGSDAG;
		
	}

	
	/**
	 * @param node
	 * @return
	 * @throws InvalidEditException
	 */
	private NodeGSDAG auxConstructGSDAG(MNode node) throws InvalidEditException {
		// TODO Auto-generated method stub
		NodeGSDAG decNodeGSDAG, chanceNodeGSDAG;
		MNode sucessor;
		NodeGSDAG rootGSDAG;

		TreeSet obsEliminate = node.getObsEliminate();
		TreeSet toEliminate = node.getToEliminate();

		if (hasMNodeToElim(node)) {// A decision node of Marta's graph
			rootGSDAG = auxConstructGSDAGEliminateVariablesMarta(node);
		} else if (isMNodeSink(node)) {// sink node of Marta
			rootGSDAG = null;
		} else {// Chance node in Marta's graph
			rootGSDAG = auxConstructGSDAGChanceMarta(node);
		}
		return rootGSDAG;
	}

	/**
	 * @param mNode
	 * @return
	 */
	private NodeGSDAG auxConstructGSDAGEliminateVariablesMarta(MNode mNode)
			throws InvalidEditException {
		// TODO Auto-generated method stub
		NodeGSDAG decNodeGSDAG=null;
		NodeGSDAG chanceNodeGSDAG;
		NodeGSDAG sucessorOfCurrentLastNodeGSDAG;
		NodeGSDAG existingNodeGSDAG;
		MNode sucessorOfMNode;
		NodeGSDAG root = null;
		NodeGSDAG finalNodeGSDAG;
		NodeGSDAG currentLastNodeGSDAG;
		boolean hasDec;
		
		hasDec = hasMNodeDecisions(mNode);

		//We see if there are decisions and we create a node for them
		if (hasDec){
			hasDec = true;
//			 Create the NodeGSDAG for the decisions
			decNodeGSDAG = new NodeGSDAG(NodeGSDAG.TypeOfNodeGSDAG.DECISION);
			decNodeGSDAG.setVariables(getNamesOfVariables(mNode.getToEliminate()));
			addNode(decNodeGSDAG);
			//We set the return value
			root = decNodeGSDAG;
		}
		
		//We see if there are chance nodes and we create a node for them
		if (hasMNodeChanceObs(mNode)){
//			 Create the NodeGSDAG for the chance variables
			chanceNodeGSDAG = new NodeGSDAG(NodeGSDAG.TypeOfNodeGSDAG.CHANCE);
			chanceNodeGSDAG.setVariables(getNamesOfVariables(mNode
					.getObsEliminate()));

			addNode(chanceNodeGSDAG);
			
			if (hasDec) {

				// Link from the node of decisions to the node of chance
				// variables
				try {
					createLink(decNodeGSDAG, chanceNodeGSDAG);
				} catch (InvalidEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{//The chance node is the first in the tree
				root = chanceNodeGSDAG;
			}
			currentLastNodeGSDAG = chanceNodeGSDAG;
		}
		else{
			currentLastNodeGSDAG = decNodeGSDAG;
		}
		
		NodeList childrenOfMNode;
		
		childrenOfMNode =  (mNode.getChildrenNodes());
		if (childrenOfMNode.size()>1){
			NodeGSDAG newBranch;
			
			newBranch = new NodeGSDAG(NodeGSDAG.TypeOfNodeGSDAG.BRANCH);
			this.addNode(newBranch);
//			 Create a link
			try {
				createLink(currentLastNodeGSDAG, newBranch);
			} catch (InvalidEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			currentLastNodeGSDAG = newBranch;
		}

		
		for (int i=0;i<childrenOfMNode.size();i++){

		// We connect the currentLastNodeGSDAG with the future
		sucessorOfMNode = (MNode) (childrenOfMNode.elementAt(i));

		if (sucessorOfMNode.getParents().size() == 1) {// It's the only parent
			// of sucessor
			// We continue advancing to the right with sucessor
			sucessorOfCurrentLastNodeGSDAG = auxConstructGSDAG(sucessorOfMNode);
		} else {// The sucessor has several parents. The first must create a
				// BRANCH so the rest add link to this
			// The nodeGSDAG for the sucessor can exist so we have
			// to find out it
			existingNodeGSDAG = existingNodeGSDAG(sucessorOfMNode);
			if (existingNodeGSDAG == null) {// Several parents and this is the
											// first so it has to link to a
											// BRANCH
				sucessorOfCurrentLastNodeGSDAG = auxConstructGSDAG(sucessorOfMNode);
				// The first must create, if necessary, a BRANCH
				if (sucessorOfCurrentLastNodeGSDAG.getTypeOfNodeGSDAG() != TypeOfNodeGSDAG.BRANCH) {
					NodeGSDAG aux = sucessorOfCurrentLastNodeGSDAG;
					sucessorOfCurrentLastNodeGSDAG = new NodeGSDAG(
							NodeGSDAG.TypeOfNodeGSDAG.BRANCH);
					this.addNode(sucessorOfCurrentLastNodeGSDAG);
					// Create a link
					try {
						createLink(sucessorOfCurrentLastNodeGSDAG, aux);
					} catch (InvalidEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else {
				sucessorOfCurrentLastNodeGSDAG = existingNodeGSDAG;
			}
			
		}

		// Link from the node of chance variables to the future
		if (sucessorOfCurrentLastNodeGSDAG == null) {
			// I finish the graph with a branch node in any case
			sucessorOfCurrentLastNodeGSDAG = new NodeGSDAG(
					NodeGSDAG.TypeOfNodeGSDAG.BRANCH);
			this.addNode(sucessorOfCurrentLastNodeGSDAG);
		}

		// Create a link from the currentLastNodeGSDAG to the sucessor
		try {
			createLink(currentLastNodeGSDAG, sucessorOfCurrentLastNodeGSDAG);
		} catch (InvalidEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

		return root;

	}

	/**
	 * @param node
	 * @return
	 */
	private NodeGSDAG auxConstructGSDAGChanceMarta(MNode mNode)
			throws InvalidEditException {
		// TODO Auto-generated method stub
		NodeList childrenOfMNode;
		NodeList parentsOfMNode;
		int numChildrenOfMNode;
		int numParentsOfMNode;
		NodeGSDAG rootGSDAG;
		NodeGSDAG auxNodeGSDAG;

		childrenOfMNode = mNode.getChildrenNodes();
		parentsOfMNode = mNode.getParentNodes();

		numChildrenOfMNode = childrenOfMNode.size();
		numParentsOfMNode = parentsOfMNode.size();

		// We put branch points in three cases:
		// 1. At the end of my gsdag
		// 2. When the node have several children
		// 3. When the node have several parents
		if ((numChildrenOfMNode == 1) && (numParentsOfMNode == 1)) {// We don't
																	// have a
																	// branch
																	// point at
																	// the
																	// beginning
			rootGSDAG = auxConstructGSDAG((MNode) (childrenOfMNode.elementAt(0)));
		} else {// We have a branch point in the root
			// We create a branch node
			rootGSDAG = new NodeGSDAG(NodeGSDAG.TypeOfNodeGSDAG.BRANCH);
			this.addNode(rootGSDAG);
			// We add as children all the graphs of the sucessors
			for (int i = 0; i < numChildrenOfMNode; i++) {
				auxNodeGSDAG = auxConstructGSDAG((MNode) (childrenOfMNode
						.elementAt(i)));
				if (auxNodeGSDAG != null) {
					try {
						createLink(rootGSDAG, auxNodeGSDAG);
					} catch (InvalidEditException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return rootGSDAG;
	}

	public ArrayList<String> getNamesOfVariables(TreeSet treeNames) {
		ArrayList<String> names;

		names = new ArrayList();

		for (Object auxName : treeNames) {
			names.add((String) auxName);
		}
		return names;
	}

	public ArrayList<String> getNamesOfDecisions(MNode node) {
		return getNamesOfVariables(node.getToEliminate());
	}

	/**
	 * Method to obtain the root of the GSDAG constructed by Marta
	 * 
	 * @param gsdagMarta
	 * @return
	 */
	private MNode getRootOfGSDAGMarta(Graph gsdagMarta) {
		NodeList mNodes;
		boolean found = false;
		MNode auxMNode = null;
		MNode root = null;

		mNodes = gsdagMarta.getNodeList();
		// TODO Auto-generated method stub
		for (int i = 0; (i < mNodes.size()) && (found == false); i++) {
			auxMNode = (MNode) (mNodes.elementAt(i));
			if (auxMNode.getParents().size() == 0) {
				found = true;
				root = auxMNode;
			}
		}
		return root;
	}

	public NodeGSDAG existingNodeGSDAG(MNode branchMNode) {
		NodeGSDAG auxNodeGSDAG;
		NodeGSDAG existingNode = null;
		boolean found = false;

		for (int i = 0; (i < this.nodeList.size()) && (found == false); i++) {
			auxNodeGSDAG = (NodeGSDAG) nodeList.elementAt(i);
			if ((auxNodeGSDAG.type == NodeGSDAG.TypeOfNodeGSDAG.BRANCH)
					&& (auxNodeGSDAG.getParentNodes().size() > 0)
					&& (haveSameFuture(auxNodeGSDAG, branchMNode))) {
				// We admit that auxNodeGSDAG is not a BRANCH, but the method
				// that receives the result of this method
				// has to create a branch
				// if
				// ((auxNodeGSDAG.getParentNodes().size()>0)&&(haveSameFuture(auxNodeGSDAG,branchMNode))){
				found = true;
				existingNode = auxNodeGSDAG;
			}
		}
		return existingNode;

	}

	// Method to know if an existing branch node in MLuque's graph is equivalent
	// to other
	// branch in Marta's graph
	private boolean haveSameFuture(NodeGSDAG auxNodeGSDAG, MNode branchMNode) {
		// TODO Auto-generated method stub
		ArrayList<String> futureDecsBranchMNode;
		ArrayList<String> futureDecsBranchNodeGSDAG;
		MNode auxMNode;

		futureDecsBranchMNode = new ArrayList();
		futureDecsBranchNodeGSDAG = new ArrayList();

		// Compute the future decisions of branchMNode
		auxMNode = branchMNode;
		while (auxMNode != null) {
			if (hasMNodeDecisions(auxMNode)) {
				futureDecsBranchMNode.addAll(getNamesOfDecisions(auxMNode));
				auxMNode = (MNode) (auxMNode.getChildrenNodes().elementAt(0));
			} else if (isMNodeSink(auxMNode)) {
				auxMNode = null;
			} else if (auxMNode.getChildrenNodes().size()>0){// Circle in Marta's graph
				auxMNode = (MNode) (auxMNode.getChildrenNodes().elementAt(0));

			}
			else{
				auxMNode = null;
			}
		}

		// Compute the future decisions of auxNodeGSDAG
		while (auxNodeGSDAG != null) {
			if (auxNodeGSDAG.type == NodeGSDAG.TypeOfNodeGSDAG.DECISION) {
				futureDecsBranchNodeGSDAG.addAll(auxNodeGSDAG.getVariables());
			}
			if ((auxNodeGSDAG.getChildrenNodes() != null)
					&& (auxNodeGSDAG.getChildrenNodes().size() > 0)) {
				auxNodeGSDAG = (NodeGSDAG) (auxNodeGSDAG.getChildrenNodes()
						.elementAt(0));
			} else {
				auxNodeGSDAG = null;
			}

		}

		return (futureDecsBranchMNode.containsAll(futureDecsBranchNodeGSDAG))
				&& (futureDecsBranchNodeGSDAG
						.containsAll(futureDecsBranchMNode));

	}

	// It computes the descendant variables of a variable of a NodeGSDAG
	public ArrayList<String> getDescendantVariables(NodeGSDAG node,
			String variable) {
		ArrayList<String> variables, descendants;
		int indexOfVariable;
		NodeList auxChildren;
		NodeGSDAG auxNode;

		variables = node.getVariables();

		indexOfVariable = variables.indexOf(variable);

		descendants = new ArrayList();

		// Add the rest of variables of node
		descendants
				.addAll(variables.subList(indexOfVariable, variables.size()));

		// Add the variables of the descendant
		auxChildren = node.getChildrenNodes();

		// Compute the names of the future variables
		while ((auxChildren != null) && (auxChildren.size() > 0)) {
			auxNode = (NodeGSDAG) auxChildren.elementAt(0);
			descendants.addAll(auxNode.getVariables());
		}

		return descendants;
	}

	//It returns true if the mnode contains variables that have to be eliminated
	boolean hasMNodeToElim(MNode mnode){
		
		TreeSet obsElim;
		boolean has = false;
		TreeSet toElim = mnode.getToEliminate();
		obsElim = mnode.getObsEliminate();
		has = (hasVariablesOfKind(toElim,Node.DECISION)||
		(hasVariablesOfKind(toElim,Node.CHANCE)&&(hasVariablesOfKind(obsElim,Node.CHANCE))));
		
		return has;
		
		
	}
	
//	It returns true if the mnode has decision nodes in the attribute toElim
	boolean hasMNodeDecisions(MNode mnode){
		return hasVariablesOfKind(mnode.getToEliminate(),Node.DECISION);
	}
	
//	It returns true if the mnode has chance nodes in the attribute toElim
	boolean hasMNodeChanceToElim(MNode mnode){
		return hasVariablesOfKind(mnode.getToEliminate(),Node.CHANCE);
	}
	
	//It returns true if the mnode has chance nodes in the attribute obsEliminate
	boolean hasMNodeChanceObs(MNode mnode){
		return hasVariablesOfKind(mnode.getObsEliminate(),Node.CHANCE);
	}
	
	//It returns true if the treeset of variables contains the same of a variable
	//of kind 'kindOfNode'
	boolean hasVariablesOfKind(TreeSet variables,int kindOfNode) {
		boolean has;
		/*return ((mnode.getObsEliminate() != null) && (mnode.getObsEliminate()
				.size() > 0)*/
		
		if (variables!=null){
			if (variables.size()>0){
				if (uid.getNode((String)(variables.first())).getKindOfNode()==kindOfNode){
					has = true;
				}
				else{
					has=false;
				}
			}
			else{
				has = false;
			}
		}
		else{
			has = false;
		}
		return has;
	}

	boolean isMNodeSink(MNode mnode) {
		return ((mnode.getObsEliminate() != null) && (mnode.getObsEliminate()
				.size() == 0));

	}
	
	public NodeGSDAG getLastNodeGSDAG(){
		return getLastNodeGSDAG(root);
	}

	// It returns the last NodeGSDAG (sink node) of the GSDAG
	public NodeGSDAG getLastNodeGSDAG(NodeGSDAG rootGSDAG) {
		NodeGSDAG last = null;
		NodeGSDAG aux;
		boolean found;

		if (rootGSDAG != null) {
			found = false;
			aux = rootGSDAG;
			while (!found) {
				NodeList auxChildren;
				auxChildren = aux.getChildrenNodes();
				if ((auxChildren == null) || (auxChildren.size() == 0)) {
					found = true;
					last = aux;
				} else {
					aux = (NodeGSDAG) auxChildren.elementAt(0);
				}

			}

		} else {
			last = null;
		}
		return last;
	}

	public void print() {
		NodeGSDAG aux;
		NodeList children;

		for (int i = 0; i < this.getNodeList().size(); i++) {
			aux = (NodeGSDAG) getNodeList().elementAt(i);
			if (aux.getTypeOfNodeGSDAG() == TypeOfNodeGSDAG.BRANCH) {
				System.out.println("** Node BRANCH");
			} else {
				System.out.println("** Node " + aux.getVariables().toString());
			}
			System.out.println("*Children:");
			children = aux.getChildrenNodes();
			for (int j = 0; j < children.size(); j++) {
				System.out.println(((NodeGSDAG) children.elementAt(j))
						.getVariables().toString());
			}
			System.out.println("");
		}
	}

	public NodeGSDAG getRoot() {
		return root;
	}

	// It intializes the last node of the GSDAG to start the evaluation
	public void initializePotentials(Vector relationList) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		NodeGSDAG lastNodeGSDAG;
		Relation rel;
		int kind;

		lastNodeGSDAG = getLastNodeGSDAG();

		// Set separately the relations of the node
		RelationList probabilityRelations = new RelationList();
		RelationList utilityRelations = new RelationList();

		for (int i = 0; i < relationList.size(); i++) {
			rel = (Relation) relationList.elementAt(i);
			kind = rel.getKind();
			if (kind == Relation.UTILITY) {
				utilityRelations.insertRelation(rel);
			} else {// probability relation
				probabilityRelations.insertRelation(rel);
			}
		}

		// Prepare the last node to start the evaluation
		lastNodeGSDAG.setProbabilityRelations(probabilityRelations);
		lastNodeGSDAG.setUtilityRelations(utilityRelations);

	}
	
	
	/**
	 * @return true iff the GSDAG represents sequence of decisions and observations where
	 * there is no branches to choose the next decisions. I.e., the evaluation can be performed
	 * by an algorithhm for traditional IDs. The GSDAG is a linear sequence of NodeGSDAGs.
	 */
	public boolean isATraditionalID(){
		NodeGSDAG auxNode;
		auxNode = root;
		boolean finished = false;
		boolean isAnID=true;
		
		
		while (finished==false){
			NodeList children = auxNode.getChildrenNodes();
			
			if ((children==null)||(children.size()==0)){
				finished = true;
				isAnID = true;
			}
			else if (children.size()==1){
				auxNode = (NodeGSDAG) children.elementAt(0);
			}
			else{
				finished = true;
				isAnID = false;
			}
		}
		return isAnID;
		
		
	}

	public boolean hasBranchAtBeginning(int minNumChildrenFirstBranch) {
		// TODO Auto-generated method stub
		return ((root.type==NodeGSDAG.TypeOfNodeGSDAG.BRANCH)&&(root.getChildren().size()>=minNumChildrenFirstBranch));
	}

	
	
}

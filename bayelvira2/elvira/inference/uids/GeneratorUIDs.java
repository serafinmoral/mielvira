package elvira.inference.uids;

import java.util.Random;
import java.util.Vector;

import elvira.Continuous;
import elvira.FiniteStates;
import elvira.IDWithSVNodes;
import elvira.InvalidEditException;
import elvira.Link;
import elvira.LinkList;
import elvira.Node;
import elvira.NodeList;
import elvira.Relation;
import elvira.UID;
import elvira.potential.Function;
import elvira.potential.PotentialTable;
import elvira.potential.ProductFunction;
import elvira.potential.SumFunction;
import elvira.potential.UtilityPotential;

public class GeneratorUIDs {
	/**
	 * Genera diagramas de influencia siguiendo el email que envió Marta. Esto es, construye
	 * un simple tree, le añade y quita enlaces, ordena las decisiones, añade los nodos de utilidad
	 * y determina sus padres, genera probabilidades y utilidades, y a partir de ahí, hago
	 * lo especifico para DI con nodos SV: creo la estructura de nodos SV.
	 * @param nNodes Número de nodos (aleatorios y de decisión)
	 * @param decRation Probabilidad de que se escoja que un nodo sea decisión
	 * @param nUtils Número de nodos de utilidad (non-super)
	 * @param nParents Número máximo de padres de los nodos de azar y decisión. Además es el número exacto de padres para los nodos de utilidad
	 * @param iterations Número de iteraciones para el bucle que añade y quita enlaces del árbol simple ordenado.
	 * @param ratioRemoveParentsDecs2 
	 * @return UID generado aleatoriamente
	 */
	static UID generateUIDVomlelova(int nNodes, double decRation,double obsRation,
			int nUtils, int nParents, int maxNumChildrenDecs,int iterations,double ratioRemoveParentsDecs ) {

		boolean withDecisions=false;
		UID uid = new UID();

		while (withDecisions==false){
			uid = initializeSimpleOrderedTreeWithDecisionsVomlelova(nNodes, decRation,obsRation);
			withDecisions = (uid.getNodesOfKind(Node.DECISION).size()>0);
		}
		
		//I'm going to perform a number of iterations adapted to the number of nodes of the UID
		//I only consider 2*nNodes iterations because when a higher value the GSDAG is linear
		iterations = 2*nNodes;
		
		addAndRemoveLinksVomlelova(uid, nParents, maxNumChildrenDecs,iterations);
		
		generateProbabilities(uid);
		
		//id.orderDecisionsGreedilyIfNotOrdered();
		
		removeSomeParentsOfDecisions(uid, ratioRemoveParentsDecs);
		
		generateUtilityNodes(uid, nUtils, nParents);
		
		generateUtilities(uid);

		//generateSVNodes(id, nParentsOfSV);
		
		return uid;

	}
	
	static UID generateSpecialUIDThomas(int numberOfDecisions){
		String newNameDec;
		String newNameChance;
		Node hidden;
		
		UID id = new UID();
		
		//Create H
		newNameChance="H";
		id.createNode(0,0,"Helvetica",newNameChance,Node.CHANCE);
		hidden = id.getNode(newNameChance);
		//It's a hidden variable
		hidden.setComment("h");
		
		
		
		for (int i=0;i<numberOfDecisions;i++){
			//Create Di
			newNameDec="D"+i;
			id.createNode(0,0,"Helvetica",newNameDec,Node.DECISION);
			Node auxDec = id.getNode(newNameDec);
			
			//Create Xi
			newNameChance="X"+i;
			id.createNode(0,0,"Helvetica",newNameChance,Node.CHANCE);
			Node auxChance = id.getNode(newNameChance);
		
			//Link from Di to Xi
			try{
				id.createLink(auxDec,auxChance);
			} catch (InvalidEditException iee) {
			};
			
//			Link from Xi to H
			try{
				id.createLink(auxChance,hidden);
			} catch (InvalidEditException iee) {
			};
			
			
		}
						
		String nameU="U";
		id.createNode(0,0,"Helvetica",nameU,Node.UTILITY);
		
//		Link from H to U
		try{
			id.createLink(hidden,id.getNode(nameU));
		} catch (InvalidEditException iee) {
		};
			
		generateQuantitativeInformation(id);
		
		return id;
		
		
	}
	
	static UID generateSpecialUIDMLuque(int numberOfDecisions){
		String newNameDec;
		String newNameChance;
		Node hidden;
		Node nodeU;
		
		UID id = new UID();
		
		//Create U
		String nameU="U";
		id.createNode(0,0,"Helvetica",nameU,Node.UTILITY);
		nodeU=id.getNode(nameU);
		
		for (int i=0;i<numberOfDecisions;i++){
			//Create Di
			newNameDec="D"+i;
			id.createNode(0,0,"Helvetica",newNameDec,Node.DECISION);
			Node auxDec = id.getNode(newNameDec);
			
			//Create Xi
			newNameChance="X"+i;
			id.createNode(0,0,"Helvetica",newNameChance,Node.CHANCE);
			Node auxChance = id.getNode(newNameChance);
		
			//Link from Di to Xi
			try{
				id.createLink(auxDec,auxChance);
			} catch (InvalidEditException iee) {
			};
			
//			Link from Xi to U
			try{
				id.createLink(auxChance,nodeU);
			} catch (InvalidEditException iee) {
			};
			
//			Link from Di to U
			try{
				id.createLink(auxDec,nodeU);
			} catch (InvalidEditException iee) {
			};
			
			
		}
						
		
		generateQuantitativeInformation(id);
		
		return id;
		
		
	}
	/**
	 * It generates the quantitative part of an UID whose qualitative part is constructed
	 */
	public static void generateQuantitativeInformation(UID uid) {

		
		
		generateProbabilities(uid);
		
		//id.orderDecisionsGreedilyIfNotOrdered();
		
		generateUtilities(uid);

		//generateSVNodes(id, nParentsOfSV);
		
		

	}
	
	
	/**
	 * It removes randomly some parents of the decisions.
	 * Also, it removes all the links from non-observable variables to decisions
	 * @param uid
	 * @param ratioRemoveParentsDecs
	 */
	private static void removeSomeParentsOfDecisions(UID uid,double ratioRemoveParentsDecs) {
		// TODO Auto-generated method stub
		NodeList decisions;
		Node dec;
		LinkList links;
		Random r=new Random();
		double randomNumber;
		Link auxLink;
		Node auxParent;
		boolean removeLink = false;
		NodeList auxParents;
	
		
		decisions = uid.getNodesOfKind(Node.DECISION);
		
		for (int i=0;i<decisions.size();i++){
			dec = decisions.elementAt(i);
			auxParents = dec.getParentNodes();
			for (int j=0;j<auxParents.size();j++){
				 auxParent = auxParents.elementAt(j); 
				if (auxParent.getComment()=="h"){//Non observable variable
					removeLink=true;
				}
				else{
					randomNumber=r.nextDouble();
					removeLink=(randomNumber<ratioRemoveParentsDecs);
				}
				
				if (removeLink){
				try {
					uid.removeLink(auxParent,dec);
				} catch (InvalidEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
					
		}
		
	}


	/**
	 * @param nNodes
	 * @return
	 * @throws InvalidEditException
	 */
	private static UID initializeSimpleOrderedTreeWithDecisionsVomlelova(
			int nNodes, double decRation, double obsRation) {
		UID id;
		Random r = new Random();
		int kind;
		double randomNumber;
		String prefix;
		Node auxNode;
		NodeList generatedNodes = new NodeList();
		String newName;
		double randomNumberObs;
		Random rObs = new Random();
		String comment;
		Vector<String> statesOfDec;

		id = new UID();

		statesOfDec = new Vector();
		for (int i = 0; i < 3; i++) {
			statesOfDec.add("s" + i);
		}

		for (int i = 0; i < nNodes; i++) {
			// Generated a new node
			randomNumber = r.nextDouble();
			if (randomNumber < decRation) {// Decision
				kind = Node.DECISION;
				prefix = "D";
				comment = "";
				//newName = prefix + i;
				//auxNode = new FiniteStates(newName, statesOfDec);
			} else if (randomNumber < (decRation + obsRation)) {// Chance
																// observable

					kind = Node.CHANCE;
					prefix = "X";
					comment = "";

				} else {// Chance non observable
					kind = Node.CHANCE;
					prefix = "H";
					comment = "h";
				}
				newName = prefix + i;
				id.createNode(0, 0, "Helvetica", newName, kind);
				auxNode = id.getNode(newName);
				// We decide if it is observable
				auxNode.setComment(comment);
				if (auxNode.getKindOfNode()==Node.DECISION){
					((FiniteStates)auxNode).setStates(statesOfDec);
				}
			// Add the node to the diagram
			try {
				id.addNode(auxNode);
			} catch (InvalidEditException iee) {
			}
			;

			if (i > 0) {// The root node of the tree can't have any parents.
				// Add a link between from one of the previously generated nodes
				// to the new node
				try {
					id.createLink(chooseRandom(generatedNodes, r), auxNode);
				} catch (InvalidEditException iee) {
				}
				;
			}

			generatedNodes.insertNode(auxNode);

		}

		return id;

	}
	
	private static void addAndRemoveLinksVomlelova(UID uid,int nParents,int maxNumChildrenDecs,int iterations){
		NodeList nodes;
		int i,j;
		Link linkIJ;
		Node iNode;
		Node jNode;
		Random r=new Random();
		int nNodes;
		

	
		
		nodes=uid.getNodeList();
		nNodes=nodes.size();
		
		
		for (int k = 0;k<iterations;k++){
			i=r.nextInt(nNodes);
			j=r.nextInt(nNodes);
			if (i!=j){
				iNode=nodes.elementAt(i);
				jNode=nodes.elementAt(j);
				linkIJ=uid.getLink(iNode,jNode);
				if (linkIJ != null) {//Link between i and j exists
					try {
						uid.removeLink(iNode, jNode);
					} catch (InvalidEditException iee) {
						;
					}
					if (uid.connectedComponents().size() > 1){//It isn't connected
															 //without the link
						//Add the link again
						try { 
							uid.createLink(iNode, jNode);
						} catch (InvalidEditException iee) {
							;
						}
					}
					else{
						System.out.println("Removal arc "+iNode.getName()+"->"+jNode.getName());
					}
				}
				else{
					if ((jNode.getParentNodes().size() < nParents)&& 
						((iNode.getKindOfNode()!=Node.DECISION)||(jNode.getKindOfNode()==Node.DECISION)||(iNode.getChildren().size()<maxNumChildrenDecs))){
						// limit of in number of parents and children
						if (uid.hasCycle(iNode,jNode)==false){
						try {
							uid.createLink(iNode, jNode);
						} catch (InvalidEditException iee) {
							;
							
							
						}
						System.out.println("Adding arc "+iNode.getName()+"->"+jNode.getName());
						
						}
					}
				}
			}
		}
	}
	
	/**
	 * Generate random probabilities (uniform distribution) for all the potentials of probability.
	 * @param id Influence diagram
	 */
	private static void generateProbabilities(UID id) {
		// TODO Auto-generated method stub
		NodeList listNodes;
		Node node;
		NodeList nodesRel;
		NodeList pa;
		Relation relation;
		PotentialTable potentialTable;
		Random generator=new Random();
		int kind;
		
		listNodes=id.getNodeList();
		
		 for (int i=0 ; i< listNodes.size() ; i++) {
		    nodesRel = new NodeList();
		    node=listNodes.elementAt(i);
		    kind=node.getKindOfNode();
		    if (kind==Node.CHANCE){//We only consider the relations whose main node is CHANCE
			    node = (FiniteStates)listNodes.elementAt(i);
			    //Remove the relation of the node
			    id.removeRelation(node);
			    //Construct the new relation
			    nodesRel.insertNode(node);
			    pa = id.parents(node);
			    nodesRel.join(pa);
			    relation = new Relation();
			    relation.setVariables(nodesRel);
			    relation.setKind(Relation.CONDITIONAL_PROB);
			    //Generate a potental of probability with uniform random numbers
			    potentialTable = new PotentialTable(generator,nodesRel,1);
			    relation.setValues(potentialTable);
			    id.getRelationList().addElement(relation);
		    }
		  }
				 
	}
	
	public static void generateUtilityNodes(UID uid,int nUtils, int nParents){
		
			NodeList chanceAndDecNodes;
			NodeList auxParentsU;
			Random r=new Random();
			
			chanceAndDecNodes=uid.getNodesOfKind(Node.CHANCE);
			chanceAndDecNodes.join(uid.getNodesOfKind(Node.DECISION));
			
				
			//Generate utility nodes and their parents
			for (int i=0;i<nUtils;i++){
				String nameU;
				
				nameU="U"+i;
				uid.createNode(0,0,"Helvetica",nameU,Node.UTILITY);
				auxParentsU=chooseRandom(chanceAndDecNodes,nParents,r);
								
				Node auxU;
				auxU=uid.getNode(nameU);

				//Links from auxParentsU to auxU
				for (int j=0;j<auxParentsU.size();j++){
					try{
					uid.createLink(auxParentsU.elementAt(j),auxU);
					} catch (InvalidEditException iee) {
					};
				}
				
			}
		
		}
	
	/**
	 * @param id
	 */
	private static void generateUtilities(UID id) {
		
		NodeList listNodes;
		Node node;
		NodeList nodesRel;
		NodeList pa;
		Relation relation;
		PotentialTable potentialTable;
		Random generator=new Random();
		int kind;
		NodeList nodesPotential;
		
		listNodes=id.getNodeList();
		
		 for (int i=0 ; i< listNodes.size() ; i++) {
		    nodesRel = new NodeList();
		    node=listNodes.elementAt(i);
		    kind=node.getKindOfNode();
		    if (kind==Node.UTILITY){//We only consider the relations whose main node is UTILITY
			    
			    //Remove the relation of the node
			    id.removeRelation(node);
			    //Construct the new relation
			    nodesRel.insertNode(node);
			    pa = id.parents(node);
			    nodesRel.join(pa); //nodesRel= X and pa(X)
			    nodesPotential = nodesRel.copy();
			    nodesPotential.removeNode(node); //nodesPotential= pa(X)
			    relation = new Relation();
			    relation.setVariables(nodesRel);
			    relation.setKind(Relation.UTILITY);
			    //Generate a potental of probability with uniform random numbers
			    potentialTable = new PotentialTable(generator,nodesPotential,100.0);
			    relation.setValues(potentialTable);
			    id.getRelationList().addElement(relation);
		    }
		  }
		
	}


	
	
	public static NodeList chooseRandom(NodeList list, int nNodes,Random r) {
		NodeList auxList;
		
		
		int auxRandom = 0;
		boolean inserted = false;
		Node auxNode;
		int length;
		
		length=list.size();

		if (length <= nNodes) {
			auxList = list.copy();
		} else {
			auxList = new NodeList();

			for (int i = 0; i < nNodes; i++) {
				inserted = false;

				while (inserted == false){
					auxRandom = r.nextInt(length);
				auxNode = list.elementAt(auxRandom);
				if (auxList.getId(auxNode) == -1) {
					auxList.insertNode(auxNode);
					inserted = true;
				}
				}
			}
		}

		return auxList;

	}
	
	public static Node chooseRandom(NodeList list, Random r) {
		
		return chooseRandom(list,1,r).elementAt(0);
	}

	public static UID generateUIDVomlelovaWithNonLinearGSDAG(int auxNNodes, double decRation, double obsRation, int nUtils, int nParents, int maxNumChildrenDecs, int n, double ratioRemoveParentsDecs) {
		UID auxUID = null; 
		boolean linear=true;
		// TODO Auto-generated method stub
		while(linear){
			auxUID = generateUIDVomlelova(auxNNodes,decRation,obsRation,nUtils,nParents,maxNumChildrenDecs,n, ratioRemoveParentsDecs);
			linear = auxUID.hasLinearGSDAG();
			
		}
		return auxUID;
	}

	public static UID generateUIDVomlelovaWithNonLinearGSDAGAndBranchAtBeginning(int auxNNodes, double decRation, double obsRation, int nUtils, int nParents, int maxNumChildrenDecs, int n, double ratioRemoveParentsDecs, int minNumChildrenFirstBranch) {
		// TODO Auto-generated method stub
		boolean isNonLinearAndBranchBeginning=false;
		UID auxUID = null;
		
		while(isNonLinearAndBranchBeginning==false){				
				auxUID = generateUIDVomlelovaWithNonLinearGSDAG(auxNNodes,decRation,obsRation,nUtils,nParents,maxNumChildrenDecs,n, ratioRemoveParentsDecs);
				isNonLinearAndBranchBeginning = auxUID.hasNonLinearGSDAGAndBranchAtBeginning(minNumChildrenFirstBranch);
	}
		return auxUID;
		
	}

	




}

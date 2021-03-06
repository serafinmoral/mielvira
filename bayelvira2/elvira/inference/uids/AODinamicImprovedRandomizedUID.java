package elvira.inference.uids;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import elvira.InvalidEditException;
import elvira.NodeList;
import elvira.UID;
import elvira.potential.PotentialTable;
import elvira.tools.CronoNano;
import elvira.tools.PropagationStatisticsAOUID;

/**
 * @author Manuel Luque Gallego
 * This class implements basically the same method of propagation of the class
 * AODinamicImprovedUID, but not expanding nodes always in the optimal subgraph. I.e.
 * it selects randomly the branch where to explore in order to not discard promising
 * branches.
 */
public class AODinamicImprovedRandomizedUID extends AODinamicImprovedUID {
	

	
public AODinamicImprovedRandomizedUID(UID uid) {
		super(uid);
		// TODO Auto-generated constructor stub
	}

public void propagate(Vector paramsForCompile) {
		//For each child of the root we have a list of candidates
		//If the root is a chance node then we only have a list of candidates
		ArrayList<NodeAOUID>[] candidates;
		NodeAODinamicUID nodeToExpand;
		PotentialTable finalPot;
		PropagationStatisticsAOUID stats;
		CronoNano crono;
		double eu;
		Random r=new Random();
		int numExpansionsStat = 0;
		  int decTaken;
		  int numExpansionsBeforeStat=4;
		  int numExpansions = 0;
		  double oldF = Double.POSITIVE_INFINITY;
		  int numChildrenRootGSDAG;
		
		//Number of times that we have expanded nodes
		int numOfExpansions = 0;
		//Number of times that we have applied dinamic programming
		int numOfApplicationsDP =0;
		int step = 0;

		((UID) network).createGSDAG();

		try {
			gsdag = new GSDAG(network);
		} catch (InvalidEditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initializePotentialsInGSDAG();
		
		applyDinamicWeighting = (Boolean) paramsForCompile.get(1);


		tree = new GraphAODinamicRandomizedUID((UID) network, gsdag,applyDinamicWeighting);
		
		statistics.addTime(0);
		  
		  stats = (PropagationStatisticsAOUID)statistics;
		  stats.addExpectedUtility(getEUOfCurrentStrategy());
		  numChildrenRootGSDAG = gsdag.root.getChildren().size();
		  stats.addDecisionToTake(r.nextInt(numChildrenRootGSDAG));
		   
		  crono= new CronoNano();
			crono.start();
			
//			We apply DP at the end of the GSDAG
			  //this.applyDinamicProgrammingAtTheEndOfGSDAG();

		// We apply dinamic programming at the end of the GSDAG

		//candidates = obtainCandidatesToExpandForEachBranch();
		
		//We set the threshold that controls the DP and the search in the AO graph
		thresholdBranchingFactor = (Double) paramsForCompile.get(0);
		
		applyDinamicWeighting = (Boolean) paramsForCompile.get(1);
		
		minimumExpansionsBeforeDP = (Integer)paramsForCompile.get(2);
		

		while (areThereNodesToExpand()) {
			
			// By the moment we don't apply dinamic programming, except at the
			// beginning
			if (numExpansionsStat<numExpansionsBeforeStat){
			step++;
			System.out.println("** Step "+step);
			
			
			if (oldF<tree.root.f)
			  try {
				
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Partial optimal solution: f="+tree.root.f);
			  System.out.println("Depth of the tree: "+tree.getDepth());
			  System.out.println("Nodes in the tree: "+tree.getNumberOfNodes());
			  System.out.println("Effective branching factor: "+tree.getEffectiveBranchingFactor());
			
			  			
			if (doWeApplyDinamicProgramming(numOfExpansions,numOfApplicationsDP)) {
				// applyDinamicProgrammingAtTheEndOfGSDAG();
				//applyOneStepDinamicProgramming();
				System.out.println("***** Applying DP *****");
				numOfExpansions=0;
				//We are going to see what happens if we stop while applying DP
				//crono.stop();
				applyDinamicProgrammingAdvancingAllTheFrontiers();
				pruneTheTreeAfterDPAndMarkFInOpenNodesAsObsolete();
				
				//crono.start();
				//numOfApplicationsDP++;
				//We decrease the frequency of applying DP
				//minimumExpansionsBeforeDP++;
				// pruneTreeAfterDinamicProgramming();
			} else {// Expand a node of the tree
				numOfExpansions++;
				numExpansionsStat++;
				/*nodeToExpand = selectCandidate(candidates);
				tree.expand(nodeToExpand);
				tree.printValueOfFOfChildrenOfRoot();*/
				performOneExpansionRandomlyInBranchesOrDec();
				tree.printValueOfFOfChildrenOfRoot();
				
			}
			
			//candidates = tree.obtainAnOnlyCandidateToExpand();
			}
			else{//Compute statistics
				
				  numExpansionsStat=0;
				  stats.addToLastTime(crono.getTime());
				  crono.stop();
				  eu = getEUOfCurrentStrategy();
				  System.out.println("The EU of the current strategy is:"+eu);
				  stats.addExpectedUtility(eu);
				  decTaken = getFirstDecisionTakenInTheTree();
				  stats.addDecisionToTake(decTaken);
				  System.gc();
				  crono.start();
			}
		}
		
		  finalPot = new PotentialTable();
		  finalPot.setValue(tree.root.f);
		  statistics.setFinalExpectedUtility(finalPot);
		
		((PropagationStatisticsAOUID)statistics).setCreatedNodes(getNumberOfCreatedNodes());
		  System.out.println("Partial optimal solution: f="+tree.root.f);
		  System.out.println(tree.getNodeList().size()+" nodes were created by the algorithm AO*");
		  stats.addToLastTime(crono.getTime());
		  crono.stop();
		  eu = getEUOfCurrentStrategy();
		  stats.addExpectedUtility(eu);
		  decTaken = getFirstDecisionTakenInTheTree();
		  stats.addDecisionToTake(decTaken);
		  

		//candidates = tree.obtainCandidatesToExpand();

		}

private void performOneExpansionRandomlyInBranchesOrDec() {
	// TODO Auto-generated method stub
	int	numChildrenRootGSDAG;
	ArrayList<NodeAOUID> candidates;
	NodeList childrenOfRoot;
	NodeAODinamicUID root;
	int numChildrenRoot;
	boolean areThereMoreNodesToExpand;
	NodeAODinamicUID nodeToExpand;
	NodeAODinamicUID auxChild;
	
	root = (NodeAODinamicUID) tree.root;
	
	childrenOfRoot = root.getChildrenNodes();
	
	numChildrenRoot = childrenOfRoot.size();
	
	nodeToExpand = null;
	
	while (nodeToExpand==null){
		//We are in the initial stat or the root is chance, so we compute
		//the list of candidates in the traditional way
//		candidates = tree.obtainCandidatesToExpand();
		candidates = tree.obtainAnOnlyCandidateToExpand();
		nodeToExpand = selectCandidate(candidates);
	}
	
	tree.expand(nodeToExpand);
	

}

		
}

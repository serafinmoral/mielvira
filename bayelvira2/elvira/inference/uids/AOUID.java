package elvira.inference.uids;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Vector;

import elvira.Bnet;
import elvira.Evidence;
import elvira.InvalidEditException;
import elvira.Relation;
import elvira.RelationList;
import elvira.UID;
import elvira.inference.Propagation;
import elvira.potential.Potential;
import elvira.potential.PotentialTable;
import elvira.tools.Crono;
import elvira.tools.PropagationStatisticsAOUID;

public class AOUID extends Propagation {
	GraphAOUID tree;
	private GSDAG gsdag;
	
	
	 /** Creates a new instance of BranchBound */
	  public AOUID(UID uid) {
	    network = uid;
	    
	    statistics = new PropagationStatisticsAOUID();
	    
	    	    
	   // RelationList currentRelations = getInitialRelations();
	   
	  }
	  
	
	public void propagate(Vector paramsForCompile){
		  ArrayList<NodeAOUID> candidates;
		  NodeAOUID nodeToExpand;
		  PotentialTable finalPot;
		  int numExpansionsBeforeStat=20;
		  int numExpansions = 0;
		  //It indicates the minimum number of expansions to calculate the statistics
		  //about the EU
		  	Crono crono;
		  int auxTime;
		  double eu;
		  PropagationStatisticsAOUID stats;
		  int decTaken;
		  boolean applyDinamicW;
		  int step = 0;
		   
		
		  
			((UID)network).createGSDAG();
			
			try {
				gsdag = new GSDAG(network);
			} catch (InvalidEditException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  
			
			gsdag.initializePotentials(((UID)network).getRelationList());
			
			//applyDinamicW = (Boolean) paramsForCompile.get(1);
			applyDinamicW = false;
			
		  tree = new GraphAOUID((UID)network,gsdag,applyDinamicW);
		  
		  statistics.addTime(0);
		  
		  stats = (PropagationStatisticsAOUID)statistics;
		  stats.addExpectedUtility(0.0);
		  stats.addDecisionToTake(-1);
		  
		  crono= new Crono();
		crono.start();
		  
		  //candidates = tree.obtainCandidatesToExpand();
		candidates = tree.obtainAnOnlyCandidateToExpand();
		  
		  while(candidates.size()>0){
			  if (numExpansions<numExpansionsBeforeStat){
				  numExpansions++;
				  step++;
					System.out.println("** Step "+step);
			 
			  System.out.println("Partial optimal solution: f="+tree.root.f);
			  System.out.println("Depth of the tree: "+tree.getDepth());
			  System.out.println("Nodes in the tree: "+tree.getNumberOfNodes());
			  System.out.println("Effective branching factor: "+tree.getEffectiveBranchingFactor());
			 // System.out.println("Number of candidates to expand: "+candidates.size());
			  nodeToExpand = selectCandidate(candidates);
			  tree.expand(nodeToExpand);
			  tree.printValueOfFOfChildrenOfRoot();
//			candidates = tree.obtainCandidatesToExpand();
				candidates = tree.obtainAnOnlyCandidateToExpand();
			  }
			  else{//Computation of statistics in the middle of the evaluation
				  numExpansions=0;
				  stats.addToLastTime(crono.getTime());
				  crono.stop();
				  eu = getEUOfCurrentStrategy();
				  System.out.println("The EU of the current strategy is:"+eu);
				  stats.addExpectedUtility(eu);
				  decTaken = getFirstDecisionTakenInTheTree();
				  stats.addDecisionToTake(decTaken);
				  crono.start();
				  
			  }
		  }
		  
		  finalPot = new PotentialTable();
		  finalPot.setValue(tree.root.f);
		  //Statistics
		  statistics.setFinalExpectedUtility(finalPot);
		  System.out.println("Partial optimal solution: f="+tree.root.f);
		  System.out.println(getNumberOfCreatedNodes()+" nodes were created by the algorithm AO*");
		  stats.addToLastTime(crono.getTime());
		  crono.stop();
		  eu = getEUOfCurrentStrategy();
		  stats.addExpectedUtility(eu);
		  decTaken = getFirstDecisionTakenInTheTree();
		  stats.addDecisionToTake(decTaken);
		  System.out.println("The EU of the current strategy is:"+eu);
		  stats.setCreatedNodes(getNumberOfCreatedNodes());
		  return;
		  
	  }
	  
	 
	private int getFirstDecisionTakenInTheTree() {
		return tree.getFirstDecisionTakenInTheTree();
		
	}
		


	/**
	 * @return The expected utility of the current strategy when we are evaluating the UID
	 */
	private double getEUOfCurrentStrategy() {
		// TODO Auto-generated method stub
		return tree.root.getEUOfCurrentStrategy();
	}

	 //Select the candidate to expand when we have several possibilities
	protected NodeAOUID selectCandidate(ArrayList<NodeAOUID> candidates) {
		double fMax=Double.NEGATIVE_INFINITY;
		NodeAOUID nodeOfFMax = null;
		double auxF;
		// TODO Auto-generated method stub
		//By the moment we select any of them. For example, the first.
		for (NodeAOUID auxCandidate:candidates){
			auxF = auxCandidate.getF();
			if (auxF>fMax){
				fMax=auxF;
				nodeOfFMax = auxCandidate;
			}
		}
		return nodeOfFMax;
		
		
		
		
		
	}
	
/*	 //Select the candidate to expand when we have several possibilities
	protected NodeAOUID selectCandidate(ArrayList<NodeAOUID> candidates) {
		double minDepth=Double.POSITIVE_INFINITY;
		NodeAOUID nodeOfMinDepth = null;
		double auxDepth;
		// TODO Auto-generated method stub
		//By the moment we select any of them. For example, the first.
		for (NodeAOUID auxCandidate:candidates){
			auxDepth = auxCandidate.getInstantiations().size();
			if (auxDepth<minDepth){
				minDepth = auxDepth;
				nodeOfMinDepth = auxCandidate;
			}
		}
		return nodeOfMinDepth;
	}*/
	
	public int getNumberOfCreatedNodes(){
		return tree.getNodeList().size();
	}
	
	
}

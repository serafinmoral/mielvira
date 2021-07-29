/**
 * Created by dalgaard on Feb 20, 2007
 */

package elvira.learning.classification.supervised.discrete;


import java.util.*;
import java.io.*;

import elvira.parser.ParseException;
import elvira.probabilisticDecisionGraph.tools.*;
//import elvira.probabilisticDecisionGraph.tools.Comparator.classificationResult;

import elvira.probabilisticDecisionGraph.*;
import elvira.database.*;
import elvira.*;

import elvira.learning.classification.*;
import elvira.learning.classification.supervised.validation.ClassifierEvaluator;
import elvira.learning.classification.supervised.validation.ClassifierEvaluator.classificationResult;

/**
 * @author dalgaard
 *
 */
public class PDGClassifier extends PDG implements Classifier {

	private final int maxLevel;
	private final Random rnd = new Random(System.currentTimeMillis());
	private boolean collapseEnabled = true;
	private int minimumDataSupport = 1;
	private boolean mergeEnabled = true;
	private Vector<Long> learningTime = new Vector<Long>();
	private Vector<Integer> numCollapses = new Vector<Integer>();
	private int sumOfCollapses = 0;
	private Vector<Integer> numMerges = new Vector<Integer>();
	private int sumOfMerges = 0;
	private long mergeTimeLimit = -1;
	
	public PDGClassifier(){
		super();
		maxLevel = 1;
	}

	public PDGClassifier(int level){
		super();
		maxLevel = level;
	}

	public PDGClassifier(Vector<PDGVariableNode> vf){
		super(vf);
		maxLevel = 1;
	}
	
	public PDGClassifier(Vector<PDGVariableNode> vf, int level){
		super(vf);
		maxLevel = level;
	}
	
	public PDGClassifier(Vector<PDGVariableNode> f, String modelName){
		super(f,modelName);
		maxLevel = 1;
	}

	public PDGClassifier(Vector<PDGVariableNode> f, String modelName, int level){
		super(f,modelName);
		maxLevel = level;
	}
	
	public PDGClassifier copy(){
		PDG pdgClone = super.copy();
		return new PDGClassifier(pdgClone.getVariableForestCopy(), name, maxLevel);
	}

	/**
	 * Specify the seed to use for random operations. If not specified, the system time is
	 * used.
	 * 
	 * @param seed - the seed.
	 */
	public void setSeed(long seed){
		rnd.setSeed(seed);
	}
	
	/**
	 * Set the time limit for performing score uptimising merge operations. 
	 * The number of possible merge operations is polynomial in the number of
	 * parameter nodes, and as we need to compute the classification rate from a validation 
	 * set for every combination, if may be neccesary to specify a limit.
	 * 
	 * @param limit - the maximum allowed time for each PDGVariableNode in milliseconds.
	 */
	public void setMergeTimeLimit(long limit){
		mergeTimeLimit = limit;
	}
	
	public void learn(DataBaseCases dbc, int cVarIdx){
		long t0 = System.currentTimeMillis();
		try{
			learnHierarchicalNaiveClassifier(dbc, cVarIdx, false);
		} catch(Exception e){
			e.printStackTrace();
			System.exit(112);
		}
		learningTime.add(System.currentTimeMillis() - t0);
		numMerges.add(resetMergeCount());
		numCollapses.add(resetCollapseCount());
	}
	
	
	public void setMinimumDataSupport(int s){
		this.minimumDataSupport = s;
	}
	
	private int resetMergeCount(){
		int tmp = sumOfMerges;
		sumOfMerges = 0;
		return tmp;
	}
	
	private int resetCollapseCount(){
		int tmp = sumOfCollapses;
		sumOfCollapses = 0;
		return tmp;
	}
	
	private static int getClassId(NodeList nl){
		int i = nl.getId("class");
		if(i==-1){
			i=nl.getId("Class");
		}
		if(i == -1){
			i = nl.size() -1;
		}
		return i;
	}
	
	protected int mergeNodesLocal(PDGVariableNode pdgVar, DataBaseCases validationData, int classnumber) throws VectorOpsException, PDGException{
		int merged = 0;
		
		long t0 = System.currentTimeMillis();
		PDGParameterNode p1, p2;
		double mergeScore;
		boolean timeout = false;
		boolean mergeEvent = true;
		Vector<PDGParameterNode> parameterNodesCopy;
		// we will merge parameternodes as long as:
		// 1) there are still combinations of merges that have not been evaluated, and 
		// 2) we are still within the time limit
		while(!timeout && mergeEvent){
			parameterNodesCopy = pdgVar.getParameterNodesCopy();
			mergeEvent = false;
			outer: for(int i = 0; i<parameterNodesCopy.size()-1;i++){
				p1 = parameterNodesCopy.elementAt(i);
				for(int j = i+1;j< parameterNodesCopy.size(); j++){
					p2 = parameterNodesCopy.elementAt(j);
					mergeScore = this.mergeScoreGeneralisationRate(p1, p2, pdgVar, validationData, classnumber);
					if(mergeScore > 0.0){
						p1.safeMerge(p2, false);
						merged++;
						mergeEvent = true;
						break outer;
					}
				}
				if(mergeTimeLimit > 0) timeout = (mergeTimeLimit < (System.currentTimeMillis() - t0));
			}	
		}
		this.sumOfMerges += merged;
		return merged;
	}
	
	protected int mergeNodes(DataBaseCases validationData, int classnumber, long maxMergeTime) throws VectorOpsException, PDGException{
		Stack<PDGVariableNode> pending = getDepthFirstStack();
		int merged = 0;
		PDGVariableNode pdgVar;
		while(!pending.empty()){
			pdgVar = pending.pop();
			merged += mergeNodesLocal(pdgVar, validationData, classnumber);
		}
		return merged;
	}
	
	private void learnHierarchicalNaiveClassifier(DataBaseCases data, int idxClassVar, boolean verbose) throws PDGException, VectorOpsException{
		System.out.println("Building Hierachical PDG classifier:");
		//PDG resultPDG = new PDG();
		this.variableForest.clear();
		NodeList nl = data.getVariables();
		Vector<Node> variables = nl.getNodes();
		DataBaseCases valData = new DataBaseCases();
		DataBaseCases trainData = new DataBaseCases();
		
		// divide data into training and validation folds
		data.divideIntoTrainAndTest(trainData, valData, 0.7);
		
		CaseListMem trainDataCases = (CaseListMem)trainData.getCases();
		CaseListMem valDataCases = (CaseListMem)valData.getCases();
		
		Vector<Node> tmp_vars = new Vector<Node>(variables);
		FiniteStates classVar = (FiniteStates)tmp_vars.remove(idxClassVar);
		PDGVariableNode cVarNode = new PDGVariableNode(classVar);
		PDGParameterNode cParNode = new PDGParameterNode(cVarNode);
		cParNode.initializeReach(nl);
		cParNode.updateReach(trainDataCases);
		cParNode.updateValuesFromReach(0.0);
		this.addTree(cVarNode);
		// add each feature variable as a child of classVar
		Vector<CaseList> partitions;
		PDGVariableNode bestCandidateParent;
		double bestCandidatePval;
		double pval;
		Vector<PDGVariableNode> includedVariables;
		for(Node feature : tmp_vars){
			partitions = cVarNode.getPartitions();
			bestCandidateParent = cVarNode;
			bestCandidatePval = Measures.conditionalMutualInformation((FiniteStates)feature, classVar, partitions) / partitions.size();
			if(verbose) System.out.println("I("+feature.getName()+","+cVarNode.getName()+") = "+bestCandidatePval);
			includedVariables = this.getVariableNodesAboveDepth(maxLevel);
			includedVariables.remove(cVarNode);
			for(PDGVariableNode p : includedVariables){
				partitions = p.getPartitions();
				pval = Measures.conditionalMutualInformation((FiniteStates)feature, 
						p.getFiniteStates(), partitions) / partitions.size();
				if(verbose) System.out.println("I("+feature.getName()+","+p.getName()+"|predr("+p.getName()+")) = "+pval);
				if(pval > bestCandidatePval){
					bestCandidatePval = pval;
					bestCandidateParent = p;
				}
			}
			PDGVariableNode featureVarNode = new PDGVariableNode((FiniteStates)feature);
			bestCandidateParent.addFullyExpandedSuccessor(featureVarNode);
			System.out.println("Adding "+feature.getName()+" under "+bestCandidateParent.getName()+" ("+bestCandidatePval+")");
			if(this.collapseEnabled){
				System.out.print("collapsing zero nodes...");
				int collapsed = collapseZeroReachNodesLocal(featureVarNode,0);
				System.out.print(collapsed+" nodes removed\nMergeing nodes...");
			}
			if(this.mergeEnabled){
				int merged = mergeNodesLocal(featureVarNode, valData, idxClassVar);
				System.out.print(merged+" nodes removed\n");
				if(merged != 0){
					this.updateReach(trainDataCases);
					this.learnParametersFromReach(0.001);
				}
			}
		}
		//cParNode.setChildren(children);
		System.out.println("One last merge of the model");
		System.out.println(" done.");
		this.printStats();
	}

	public void learnParameters(CaseList cl) throws PDGVariableNotFoundException{
		clearCounts();
		for(int i=0;i<cl.getSize();i++){
			countConfiguration(cl.get(i));
		}
	}
	
	protected double mergeScore(PDGParameterNode p1, PDGParameterNode p2){
		double ms = p1.getInflow()*VectorOps.KLDivergence(p1.getValues(), p2.getValues()) + 
			p2.getInflow()*VectorOps.KLDivergence(p2.getValues(), p1.getValues());
		return ms;
	}
	
	protected double mergeScoreGeneralisationRate(PDGParameterNode p1, 
			PDGParameterNode p2, PDGVariableNode var, DataBaseCases validationData, int classnumber) throws PDGException, VectorOpsException{
		double oldRate = ClassifierEvaluator.testClassifier(this, validationData, classnumber).rate();
		PDGClassifier test = this.copy();
		FiniteStates fs = var.getFiniteStates();
		Vector<PDGParameterNode> vpn = var.getParameterNodesCopy();
		PDGVariableNode n = test.getPDGVariableNode(fs);
		Vector<PDGParameterNode> testvpn = n.getParameterNodesCopy();
		PDGParameterNode testp1 = testvpn.elementAt(vpn.indexOf(p1));
		PDGParameterNode testp2 = testvpn.elementAt(vpn.indexOf(p2));
		testp1.safeMerge(testp2);
		classificationResult cr = ClassifierEvaluator.testClassifier(test, validationData, classnumber);
		return cr.rate() - oldRate;
	}
	
	protected void redirectEdge(PDGParameterNode tail, PDGVariableNode variableSuccessor, int value, PDGParameterNode newHead){
		PDGParameterNode oldHead = tail.succ(variableSuccessor, value);
		tail.setSuccessor(newHead, variableSuccessor, value);
		//CaseList oldReach = tail.selectFromReach(value);
		//oldHead.removeFromReach(oldReach);
		oldHead.safeRemoveParent(tail);
	}
	
	protected int collapseZeroReachNodes()throws PDGException, VectorOpsException{
		return collapseZeroReachNodes(this.minimumDataSupport - 1);
	}
	
	protected int collapseZeroReachNodesLocal(PDGVariableNode pdgVar, int triviallityLevel) throws PDGException, VectorOpsException{
		PDGParameterNode trashNode = null;
		Vector<PDGParameterNode> pv = pdgVar.getParameterNodesCopy();
		int nodesCollapsed = 0;
		for(PDGParameterNode pnode : pv){
			if(pnode.getReach().getNumberOfCases() <= triviallityLevel * (pdgVar.getNumStates() - 1)){
				if(trashNode == null){
					trashNode = pnode; 
				} else {
					trashNode.safeMerge(pnode);
					nodesCollapsed++;
				}
			}
		}
		//if(trashNode != null){
		//	pdgVar.getMaxReachNode().safeMerge(trashNode);
		//	nodesCollapsed++;
		//}
		this.sumOfCollapses += nodesCollapsed;
		return nodesCollapsed;
	}
	
	protected int collapseZeroReachNodes(int triviallityLevel) throws PDGException, VectorOpsException{
		//Stack<PDGVariableNode> pending = new Stack<PDGVariableNode>();
		//for(PDGVariableNode p : variableForest)
		//	pending.push(p);
		Stack<PDGVariableNode> pending = super.getDepthFirstStack();
		PDGVariableNode current;
		int nodesCollapsed = 0;
		while(!pending.empty()){
			current = pending.pop();
			nodesCollapsed += collapseZeroReachNodesLocal(current, triviallityLevel);
			//for(PDGVariableNode p : current.getSuccessors())
			//	pending.push(p);
		}
		return nodesCollapsed;
	}
	
	/**
	 * Enable/disable collapse of zero-support nodes, that is, nodes that has no 
	 * data support.
	 * 
	 * @param t -
	 * 	true to enable, false to disable.
	 * 
	 */
	public void setCollapseEnabled(boolean t){
		collapseEnabled = t;
	}
	
	/**
	 * Enable/disable merging.
	 * 
	 * @param t -
	 * 			true to enable, false to disable.
	 */
	public void setMergeEnabled(boolean t){
		mergeEnabled = t;
	}
	
	/**
	 * This method prints various statistics collected from the learning
	 * processes that this object has performed. In a K-fold crossvalidation 
	 * setting where typically the learn method is invoked for each of the K
	 * training splits, this method prints interesting statistics. The following
	 * is collected for each invocation of the learn method:
	 * <ul>
	 * <li>learning time
	 * <li>number of merged nodes
	 * <li>number of collapsed nodes
	 * </ul>
	 * For each of the above quantities this method prints mean, variance and 
	 * standard deviation.
	 */
	public void printLearningStatistics(){
		double[] lt = new double[learningTime.size()];
		double[] merges = new double[numMerges.size()];
		double[] collapses = new double[numCollapses.size()];
		int i = 0;
		for(long t : learningTime) lt[i++] = t;
		i=0;
		for(int m : numMerges) merges[i++] = m;
		i=0;
		for(int c : numCollapses) collapses[i++] = c;
		System.out.println("Statistics of Learning procedure");
		System.out.println("--------------------------------");
		System.out.println("Time :");
		VectorOps.printMeanVarSD(lt);
		System.out.println("Merge operation : ");
		VectorOps.printMeanVarSD(merges);
		System.out.println("Collapse operations : ");
		VectorOps.printMeanVarSD(collapses);
	}
	
	public Vector<Double> classify(Configuration conf, int classVarIdx){
		FiniteStates cv = conf.getVariable(classVarIdx);
		conf.remove(cv);
		double[] retval = new double[cv.getNumStates()];
		try{
			insertEvidence(conf);
			updateBeliefs();
			retval = getBelief(cv);
		} catch(PDGException e){
			e.printStackTrace();
			System.out.println("Problems with classification of incompatible configuration. We will use the classlabel with maximal prior and continue, but you probably want to investigate this - it is not supposed to happen.");
			removeEvidence();
			updateBeliefs();
			try{
				retval = getBelief(cv);
			} catch(PDGException pdge){
				pdge.printStackTrace();
				retval = VectorOps.getNormalisedUniformDoubleArray(cv.getNumStates());
			}
		}
		Vector<Double> vretval = new Vector<Double>();
		for(int i=0;i<retval.length;i++) 
			vretval.add(i, retval[i]);
		return vretval;
	}

	
	private void addReachedParameterNodesBelow(PDGVariableNode pdgvn, 
			PDGVariableNode succ, NodeList variables, double pseudoCount){
		CaseListMem reach, reachNew;
		PDGParameterNode succPnode = null;
		Vector<Integer> zeroReachIndex = new Vector<Integer>();
		for(PDGParameterNode pnode : pdgvn.getParameterNodesCopy()){
			reach = pnode.getReach();
			for(int h=0;h<pdgvn.getNumStates();h++){
				reachNew = CasesOps.selectFromWhere(reach, pdgvn.getFiniteStates(), h);
				if(reachNew.getNumberOfCases() > 0){
					succPnode = new PDGParameterNode(succ);
					succPnode.initializeReach(variables);
					succPnode.updateReach(reachNew);
					pnode.setSuccessor(succPnode, succ, h);
					pnode.setCount(h, reachNew.getNumberOfCases());
				} else { zeroReachIndex.add(h); }
			}
			for(int h : zeroReachIndex){
				pnode.setSuccessor(succPnode, succ, h);
			}
			pnode.updateValuesFromCount(pseudoCount);
		}
	}
	
	/**
	 * This method builds a PDG representation of the training data, that is, 
	 * every unique data instance will correspond to a unique path in the PDG structure.
	 * This PDG will inherently overfit the training data and subsequent merges is usually 
	 * performed to increase classification rate.
	 * 
	 * @param classVar - This classification variable. It will be included as the last variable
	 * in the PDG, that is, it will be the leaf of the linear tree-structure.
	 * 
	 * @param pseudoCount - This value is used as pseudo count or smoothing value for parameter estimation.
	 * @return the constructed PDG model.
	 */
	public void buildLinearDataPDG(FiniteStates classVar, double pseudoCount, DataBaseCases data){
		System.out.print("Building a PDG representation of the data ");		
		CaseListMem cases = data.getCaseListMem();
		//PDG linearPdg = new PDG();
		this.variableForest.clear();
		PDGVariableNode varNode, lastVarNode = null;
		NodeList nl = new NodeList(cases.getVariables());
		Vector<FiniteStates> variables = new Vector<FiniteStates>(cases.getVariables());
		variables.remove(classVar);
		// construct root PDGVariableNode
		FiniteStates currentVar = variables.remove(0);
		varNode = new PDGVariableNode(currentVar);
		PDGParameterNode p = new PDGParameterNode(varNode);
		p.initializeReach(nl);
		// all cases will reach the root node
		p.updateReach(cases);
		lastVarNode = varNode;
		this.addTree(varNode);
		//System.out.println(currentVar.getName()+" ["+varNode.getParameterNodes().size()+"]");
		System.out.print(".");
		for(FiniteStates var : variables){
			varNode = new PDGVariableNode(var);
			lastVarNode.addSuccessor(varNode);
			addReachedParameterNodesBelow(lastVarNode, varNode, nl, pseudoCount);
			lastVarNode = varNode;
			System.out.print(".");
		}
		PDGVariableNode cVarNode = new PDGVariableNode(classVar);
		lastVarNode.addSuccessor(cVarNode);
		addReachedParameterNodesBelow(lastVarNode, cVarNode, nl, pseudoCount);
		
		for(PDGParameterNode pn : cVarNode.getParameterNodesCopy()) 
			pn.updateValuesFromReach(pseudoCount);
		System.out.println(". done.");
		this.printStats();
		//return linearPdg;
	}
	
	public static class NaiveBayesPDG{
		public static void main(String argv[]) throws IOException, ParseException, PDGException, InvalidEditException {
			DataBaseCases train = null, test = null;
			if (argv.length == 1){
				DataBaseCases tmp = new DataBaseCases(argv[0]);
				train = new DataBaseCases();
				test  = new DataBaseCases();
				tmp.divideIntoTrainAndTest(train, test, 1.0/3.0);
			} else if(argv.length == 2){
				train = new DataBaseCases(argv[0]);
				test = new DataBaseCases(argv[1]);
			} else {
				System.out.println("usage: PDGLearner$NaiveBayesPDG <training data> <test data>\n" +
						"The second argument optioinal - when only one argument is given 1/3 of the \n" +
						"training data is used for testing.");
				System.exit(0);
			}
			PDGClassifier classifier = new PDGClassifier(0);
			int cVarIdx = PDGClassifier.getClassId(train.getVariables());
			//classifier.learnHierarchicalNaiveClassifier(train, cVarIdx);
			PDGio.save(classifier, argv[0]+".pdg");
			ClassifierEvaluator comp = new ClassifierEvaluator(train, 1, cVarIdx);
			System.out.println("performing 5-fold crossvalidation.");
			ClassifierEvaluator.classificationResult[] cr = comp.kFoldCrossValidation(5, classifier);
			System.out.println("Result of 5-fold crossvalidation");
			ClassifierEvaluator.printKFoldStatisics(cr, false);
		}
	}

	public static class BuildDataPDG{
		public static void main(String argv[]) throws IOException, ParseException, PDGException{
			DataBaseCases train = null, test = null;
			FiniteStates classVar;
			String trainFile = "", testFile = "";
			if (argv.length == 1){
				trainFile = argv[0];
				DataBaseCases tmp = new DataBaseCases(trainFile);
				train = new DataBaseCases();
				test  = new DataBaseCases();
				tmp.divideIntoTrainAndTest(train, test, 1.0/3.0);
			} else if(argv.length == 2){
				trainFile = argv[0]; testFile = argv[1];
				train = new DataBaseCases(trainFile);
				test = new DataBaseCases(testFile);
			} else {
				System.out.println("usage: PDGLearner$BuildDataPDG <training data> <test data>\n" +
						"The second argument optioinal - when only one argument is given 1/3 of the \n" +
						"training data is used for testing.");
				System.exit(0);
			}

			PDGClassifier classifier = new PDGClassifier();
			int cVarIdx = PDGClassifier.getClassId(train.getVariables());
			classVar = (FiniteStates)train.getVariables().elementAt(cVarIdx);
			classifier.buildLinearDataPDG(classVar, 0.01, train);
			//dataPDG.learnParameters(train.getCases(), 0.1);
			
			PDGVariableNode pdgClassVar = classifier.getPDGVariableNode(classVar);
			if(pdgClassVar == null){
				System.out.println("did not find classVariable : "+classVar.getName());
				classifier.printNames();
			}
			PDGio.save(classifier, train.getName()+".pdg");
			ClassifierEvaluator.classificationResult cr;
			cr = ClassifierEvaluator.testClassifier(classifier, test, cVarIdx);
			cr.printStatistics();
			//cr = ClassifierEvaluator.getClassificationRate(classifier, cVarIdx, test.getCases(), true);
			//System.out.println("cr : "+cr.rate());
			//System.out.println("ll : "+cr.logLikelihoodPerCase());
		}
	}
	
	private static void printHelp(){
		System.out.println("usage : PDGClassifier <max-depth> <training-data> <random-seed> <collapse-zero-support> <merge>\n" +
				"----------------------------------------------------------------------------\n" +
				"<max-depth>           - the maximal depth of the PDG. Depth 0 corresponds to naive\n" +
				"                        bayes.\n" +
				"<training-data>       - data to use for training.\n" +
				"<random-seed>         - seed for random function, if negative the current system time will be used.\n" +
				"<collapse-zero-merge> - 'true' will enable collapsing, 'false' will disable\n" +
				"<merge>               - 'true' will enable merging, 'false' will disable\n" +
				"<compare-with-TAN>    - 'true' means to perform a comparison with a TAN model, 'false' will disable this\n"+
				"----------------------------------------------------------------------------");
	}
	
	public static void main(String argv[]){
		if(argv.length != 6){ 
			printHelp(); 
			System.exit(0);
		}
		try{
			int maxDepth = Integer.parseInt(argv[0]);
			DataBaseCases train = new DataBaseCases(argv[1]);
			//DataBaseCases test = new DataBaseCases(argv[2]);
			long seed = Long.parseLong(argv[2]);
			if(seed < 0){
				seed = System.currentTimeMillis();
			}
			boolean collapse = Boolean.parseBoolean(argv[3]);
			boolean merge = Boolean.parseBoolean(argv[4]);
			boolean includeTAN = Boolean.parseBoolean(argv[5]);
			PDGClassifier classifier = new PDGClassifier(maxDepth);
			classifier.setCollapseEnabled(collapse);
			classifier.setMergeEnabled(merge);
			classifier.setSeed(seed);
			classifier.setMergeTimeLimit(120000);
			classifier.setMinimumDataSupport(50);
			int cVarIdx = PDGClassifier.getClassId(train.getVariables());
			int K=5;
			ClassifierEvaluator cv = new ClassifierEvaluator(train, seed, cVarIdx);
			ClassifierEvaluator.classificationResult[] results = cv.kFoldCrossValidation(K, classifier);
			if(includeTAN){
				CMutInfTAN tan = new CMutInfTAN();
				cv = new ClassifierEvaluator(train, seed, cVarIdx);
				ClassifierEvaluator.classificationResult[] tanRes = cv.kFoldCrossValidation(K, tan);
				classifier.printLearningStatistics();
				System.out.println("TAN results:");
				ClassifierEvaluator.printKFoldStatisics(tanRes, true);
			}
			System.out.println("PDG results:");
			ClassifierEvaluator.printKFoldStatisics(results, false);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}

package elvira.probabilisticDecisionGraph;


import java.io.*;
import java.util.*;
//import elvira.Graph;
import elvira.Bnet;
import elvira.CaseListMem;
import elvira.Relation;
import elvira.RelationList;
import elvira.Node;
import elvira.NodeList;
//import elvira.inference.clustering.JoinTree;
import elvira.inference.clustering.Triangulation;
//import elvira.parser.ParseException;
import elvira.*;
import elvira.database.*;
import elvira.probabilisticDecisionGraph.tools.*;
import elvira.probabilisticDecisionGraph.*;


/**
 * This class implements the Probabilistic Decision Graph model.	
 * 
 * @author dalgaard
 * @since 07/02/07
 */
public class PDG implements PGM {

	/**
	 * The variable forest is represented as a Vector of root nodes.
	 */
	protected Vector<PDGVariableNode> variableForest;
	protected String name = "default";
	protected boolean isUpdated = false;
	
	/**
	 * Constructs an empty PDG model 
	 */
	public PDG(){
		variableForest = new Vector<PDGVariableNode>();
	}
	
	public PDG(Vector<PDGVariableNode> forest){
		variableForest = forest;
	}
	
	public PDG(Vector<PDGVariableNode> f, String modelName){
		variableForest = f;
		name = modelName;
	}
	
	
	public Vector<PDGVariableNode> getVariableForestCopy(){
		return new Vector<PDGVariableNode>(variableForest);
	}
	
	/**
	 * 
	 * Constructs a PDG model from the given bn model by the
	 * method described by Manfred Jaeger "Probabilistic Decision Graphs -- 
	 * Combining Verification and AI Techniques for Probabilistic Inference" in 
	 * International Journal of Uncertainty, Fuzziness and Knowledge-Based Systems, 
	 * 12:19-42, 2004. In short, we first constructs a Clique Tree model from the BN 
	 * model and then constructs an equivalent PDG model from the Clique Tree that is
	 * bounded in size.
	 * 
	 * 
	 * @param bn - The bn model from which to construct the PDG model.
	 */
	
	/*public PDG(Bnet bn){
		/**
		 * TODO: implement this
		 */
		/*
		//JoinTree jt = new JoinTree(bn);
		Triangulation t = new Triangulation(bn);
		RelationList rl = t.getCliques();
		Enumeration rEnum = rl.elements();
		int i = 1;
		while(rEnum.hasMoreElements()){
			Relation r = (Relation)rEnum.nextElement();
			System.out.println("Relation  :"+ i++);
			System.out.println("Name      : "+r.getName());
			NodeList nl = r.getVariables();
			System.out.println("Variables :"+printVars(nl));
			NodeList pars = r.getParents();
			System.out.println("Parents   :" + printVars(pars));
			System.out.println("================");
		}
		System.out.println("Size       : "+rl.size());
		System.out.println("Sum Size   : "+rl.sumSizes());
		System.out.println("Total Size : "+rl.totalSize());
	}
*/	
	public void addTree(PDGVariableNode tree){
		variableForest.add(tree);
	}
	
	protected Stack<PDGVariableNode> getDepthFirstStack(){
		Stack<PDGVariableNode> stack = new Stack<PDGVariableNode>();
		for(PDGVariableNode p : variableForest){
			p.getDepthFirstStack(stack);
		}
		return stack;
	}
	
	public PDG copy(){
		Vector<PDGVariableNode> forestCopy = new Vector<PDGVariableNode>();
		for(PDGVariableNode p : variableForest)
			forestCopy.add(p.copy(null));
		PDG cp = new PDG(forestCopy, new String(name));
		return cp;
	}
	
	/**
	 * 
	 * Returns a Vector of PDGVariableNodes containing all those PDGVariableNodes
	 * that are less than or exactly depth levels from the root, ie. 
	 * getVariableNodesAboveDepth(0) would return a Vector of all root PDGVariableNodes.
	 * 
	 * @param depth
	 * @return
	 */
	protected Vector<PDGVariableNode> getVariableNodesAboveDepth(int depth){
		Vector<PDGVariableNode> vect = new Vector<PDGVariableNode>();
		for(PDGVariableNode v : variableForest)
			v.getVarNodesAboveDepth(vect, depth);
		return vect;
	}
	
	void computeIflOfl(){
		try{
			for(PDGVariableNode t : variableForest){
				t.computeOutFlow();
			}
			for(PDGVariableNode t : variableForest){
				double oflProd = 1.0;
				for(PDGVariableNode tt : variableForest){
					if(t.equals(tt)) continue;
					oflProd *= tt.getParameterRootNode().inFlow;
				}
					
				t.propagateIfl(oflProd);
			}
		} catch(PDGException pdge){
			pdge.printStackTrace();
			System.exit(112);
		}
		isUpdated = true;
	}

	public void updateBeliefs(){
		computeIflOfl();
	}
	
	/**
	 * This method updates the all PDGParameterNodes such that their reference to
	 * data cases that reach them is set correct. That is, all references to data 
	 * cases are cleared, and data is sent through the structure from the root down.
	 * 
	 * This method can be used in different situations:
	 * 
	 * <ol>
	 * <li> during construction of the PDG structure (e.g. in learning), this method should 
	 * be used after performing local structural changes that may have 
	 * changed the path for some data instances through the structure - 
	 * which may be the case for the merge and redirect operations. 
	 * 
	 * <li> in the process of learning parameters from a new dataset.
	 * </ol>
	 * 
	 * To finally get the ML parameters for all parameters you should
	 * run {@link updateParametersFromReach} afterwards.
	 * 
	 * @param data - the data
	 */
	public void updateReach(CaseListMem data) {
		for(PDGVariableNode p : variableForest){
			try{
				p.reComputeReach(data);
			} catch(PDGException pde){
				System.out.println("PDGException in a call to reComputeReach on a 'root'-node in the variable forest.\n" +
						"This can only mean one thing:\n" +
						"Something is wrong with the variable forest of this PDG - at least one of the roots have a non-null predecessor!\n" +
						"\nI do not know how to continue - you should fire up the debugger.");
				System.exit(112);
			}
		}
	}
	
	public double[] getBelief(Node target) throws PDGVariableNotFoundException{
		PDGVariableNode targetNode = getPDGVariableNode(target);
		double[] retval = null;
		if(!isUpdated){
			updateBeliefs();
		}
		retval = targetNode.getMarginal();
		return retval;
	}
	
/*	
 * private double[] getBelief(PDGVariableNode target){
		double[] retval = null;
		if(!isUpdated){
			updateBeliefs();
		}
		retval = target.getMarginal();
		return retval;
	}
	*/
	
	private PDGVariableNode getVariableNodeByName(String name){
		PDGVariableNode retval = null;
		for(PDGVariableNode p : variableForest){
			retval = p.findNodeByName(name);
			if(retval != null) break;
		}
		return retval;
	}
	
	public PDGVariableNode getPDGVariableNode(Node n){
		PDGVariableNode retval = null;
		for(PDGVariableNode t : variableForest){
			retval = t.findPDGVariableNodeByElviraNode(n);
			if(retval != null) break;
		}
		return retval;
	}
	
	public void printNames(){
		for(PDGVariableNode p : variableForest){
			p.printNames();
		}
	}
	
	//public void insertFiniteStateEvidence(String varName, int state) throws PDGException {
	//	PDGVariableNode p = getVariableNodeByName(varName);
	//	if(p != null){ p.insertEvidence(state); }
	//	else{
	//		throw new PDGVariableNotFoundException("PDGVariableNode for '"+varName+"' not found!");
	//	} 
	//	isUpdated = false;
	//}
	
	public boolean insertEvidence(Configuration conf) throws PDGIncompatibleEvidenceException {
		boolean allVarsFound = true;
		for(PDGVariableNode v : variableForest){
			allVarsFound &= v.insertMultipleEvidence(conf);
		}
		return allVarsFound;
	}
	
	public void removeEvidence(){
		for(PDGVariableNode p : variableForest){
			p.removeEvidence();
		}
		isUpdated = false;
	}
	
	public double probabilityOfEvidence(){
		double prob = 1.0;
		if(!isUpdated){ 
			this.computeIflOfl(); 
		}
		for(PDGVariableNode p : variableForest)
			prob *= p.getParameterRootNode().outFlow;
		return prob;
	}
	
	private String printVars(NodeList nl){
		Vector v = nl.getNodes();
		String retval = "";
		for(int i=0;i<v.size();i++){
			Node n = (Node)v.elementAt(i);
			retval += n.getTitle()+"["+n.getName()+"] ";
		}
		return retval;
	}

	public void setName(String n){
		name = new String(n);
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		String retval = null;
		StringBuilder vars = new StringBuilder();
		StringBuilder struct = new StringBuilder();
		for(PDGVariableNode pvn : variableForest){
			pvn.toString(vars, struct);
		}
		retval = "pdg \""+name+"\"{\n"+vars.toString()+
		"\tstructure{\n"+struct.toString()+"\t}\n}\n";
		return retval;
	}
	
	void printMarginals(){
		computeIflOfl();
		for(PDGVariableNode pdgn : variableForest){
			printMarginal(pdgn);
		}
	}
	
	private void printMarginal(PDGVariableNode y){
		System.out.println(y.getName() + ":" + VectorOps.doubleArrayToString(y.getMarginal()));
		for(PDGVariableNode succ : y.getSuccessors()){
			printMarginal(succ);
		}
	}
	
	public NodeList getNodeList(){
		NodeList nl = new NodeList();
		for(PDGVariableNode p : variableForest){
			p.compileNodeList(nl);
		}
		return nl;
	}
	
	public Vector<Node> getNodes(){
		Vector<Node> vect = new Vector<Node>();
		for(PDGVariableNode p : variableForest){
			p.getNodes(vect);
		}
		return vect;
	}
	
	public void setMultipleEvidence(elvira.Configuration conf) throws PDGException{
		for(PDGVariableNode p : variableForest)
			p.insertMultipleEvidence(conf);
	}
	
	public static void main(String argv[]){		
		try {
			//Bnet bn = new Bnet(argv[0]);
			//PDG pdg = new PDG(bn);
			PDG pdg = PDGio.load(argv[0]);
			pdg.printMarginals();
			Vector<Node> nv = pdg.getNodes();
			elvira.Configuration c = new elvira.Configuration(nv);
			int card=c.possibleValues();
			double p, sum=0.0;
			for(int i=0;i<card;i++){
				pdg.setMultipleEvidence(c);
				pdg.computeIflOfl();
				p = pdg.probabilityOfEvidence();
				c.nextConfiguration();
				pdg.removeEvidence();
				sum+=p;
				System.out.println("P("+i+")\t="+p+"\t:"+sum);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (elvira.probabilisticDecisionGraph.ParseException e) {
			e.printStackTrace();
		} catch(PDGException e){
			e.printStackTrace();
		}
	}

	public void learnParameters(CaseList cases) throws PDGVariableNotFoundException{
		learnParameters(cases, 0.0);
	}
	
	public void resetCounts(){
		for(PDGVariableNode p : variableForest){
			p.getParameterRootNode().resetCounts();
		}
	}
	
	public void learnParameters(CaseList cases, double pseudoCount) throws PDGVariableNotFoundException{
		resetCounts();
		for(int i=0;i<cases.getNumberOfCases();i++){
			countConfiguration(cases.get(i));
		}
		updateParameters(pseudoCount);
	}
	
	public void learnParametersFromReach(double pseudoCount){
		for(PDGVariableNode p : variableForest)
			p.updateValues(pseudoCount, true);		
	}
	
	public void countConfiguration(Configuration conf) throws PDGVariableNotFoundException {
		for(PDGVariableNode p : variableForest){
			p.getParameterRootNode().countConfiguration(conf);
		}
	}
	
	public void updateParameters(double pc){
		for(PDGVariableNode p : variableForest){
			p.updateValues(pc);
		}
	}
	
	public CaseList sample(int sampleLength){
		
		CaseList cl = new CaseListMem(this.getNodes());
		Random rnd = new Random(System.currentTimeMillis());
		for(int i=0;i<sampleLength; i++){
			Configuration conf = new Configuration();
			for(PDGVariableNode p : variableForest){
				p.getParameterRootNode().generateSample(conf, rnd);
			}
			cl.put(conf);
		}
		return cl;
	}

	public void synchronizeVariables(Vector<Node> v){
		for(PDGVariableNode p : variableForest){
			p.synchronizeVariables(v);
		}
	}
	
	public int effectiveSize(){
		int es = 0;
		for(PDGVariableNode x : variableForest){
			es += x.effectiveSize();
		}
		return es;
	}
	
	public int numberOfVariables(){
		int retval = 0;
		for(PDGVariableNode p : variableForest)
			retval += p.countVariables();
		return retval;
	}
	
	public int numberOfNodes(){
		int c = 0;
		for(PDGVariableNode p : variableForest)
			c += p.countNodes();
		return c;
	}
	
	public int numberOfIndependentParameters(){
		int c = 0;
		for(PDGVariableNode p : variableForest)
			c += p.countIndependentParameters();
		return c;
	}
	
	public void printStats(){
		System.out.println("effective size      : "+effectiveSize()+"\n"+
				           "number of variables : "+numberOfVariables()+"\n"+
				           "number of nodes     : "+numberOfNodes()+"\n"+
				           "number of parameters: "+numberOfIndependentParameters()+"\n" +
				           "max depth           : "+depth()+"\n" +
				           "max branching       : "+maxbranching());
	}
	
	int maxbranching(){
		int maxbr = 0;
		for(PDGVariableNode p : variableForest){
			int b = p.maxbranching();
			maxbr = (b > maxbr ? b : maxbr);
		}
		return maxbr;
	}
	
	int depth(){
		int maxdepth = 0;
		for(PDGVariableNode p : variableForest){
			int d = p.depth();
			maxdepth = (d > maxdepth ? d : maxdepth);
		}
		return maxdepth;
	}
	
	public void smoothParameters(double smoothingValue){
		for(PDGVariableNode p : variableForest){
			p.smoothParameters(smoothingValue);
		}
	}
	
	protected void clearCounts(){
		for(PDGVariableNode p : variableForest)
			p.clearCounts();
	}
	
	
	
	//public void learn(DataBaseCases data, int classVarIdx){
	//	elvira.probabilisticDecisionGraph.tools.PDGLearner pdgl = new PDGLearner();
	//	pdgl.setTrain(data);
	//}
	
	public static class ParamLearn{
		public static void main(String argv[]){
			if(argv.length != 3){
				System.out.println("usage : PDG$ParamLearn <model.pdg> <data.dbc> <pseudo_count>");
				System.exit(112);
			}
			try{
				String pdgFile = argv[0];
				String dataFile = argv[1];
				double pc = Double.parseDouble(argv[2]);
				DataBaseCases db = new DataBaseCases(dataFile);
				CaseList cl = db.getCases();
				PDG pdg = PDGio.load(pdgFile);
				pdg.synchronizeVariables(cl.getVariables());
				pdg.learnParameters(cl, pc);
				PDGio.save(pdg, pdgFile+"2");
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static class Sample{
		
		//public Sample(){}
		
		public static void main(String argv[]){
			if(argv.length!=3){
				System.out.println("usage : PDG$Sample <model.pdg> <sample size> <outputfile>");
				System.exit(112);
			}
			try{
				String pdgFile = argv[0];
				int sampleLength = Integer.parseInt(argv[1]);
				String outputFile = argv[2];
				PDG pdg = PDGio.load(pdgFile);
				
				CaseList cl = pdg.sample(sampleLength);
				DataBaseCases db = new DataBaseCases(pdg.getName(), cl);
				db.saveDataBase(new FileWriter(outputFile));
			} catch(FileNotFoundException fnf){
				fnf.printStackTrace();
			} catch(PDGException pdge){
				pdge.printStackTrace();
			} catch(ParseException pe){
				pe.printStackTrace();
			} catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
	}
}

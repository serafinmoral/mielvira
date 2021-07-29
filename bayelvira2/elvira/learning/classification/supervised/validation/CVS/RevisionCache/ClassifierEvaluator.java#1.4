package elvira.learning.classification.supervised.validation;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import elvira.*;
import elvira.database.DataBaseCases;
import elvira.learning.classification.Classifier;
import elvira.learning.classification.supervised.discrete.CMutInfTAN;
import elvira.probabilisticDecisionGraph.PDGException;
import elvira.probabilisticDecisionGraph.PGM;
import elvira.probabilisticDecisionGraph.tools.MathUtils;
import elvira.probabilisticDecisionGraph.tools.VectorOps;

public class ClassifierEvaluator {

	private final DataBaseCases data;
	private final Random rand;
	private final int classIdx;
	private Vector<CaseListMem> folds = new Vector<CaseListMem>();
	
	public ClassifierEvaluator(DataBaseCases dbc, long seed, int classVariableId){
		classIdx = classVariableId;
		data = dbc;
		rand = new Random(seed);
	}
	
	public static classificationResult testClassifier(Classifier model, DataBaseCases test, int classnumber){
		FiniteStates target = (FiniteStates)test.getVariables().elementAt(classnumber);
		int numTargetStates = target.getNumStates();
		classificationResult cr = new classificationResult(numTargetStates);
		CaseListMem testcl = test.getCaseListMem();
		Double[] targetPosterior = new Double[numTargetStates];
		Configuration instance;
		int correctLabel, predictedLabel, numCases = testcl.getNumberOfCases();
		
		for(int i=0;i<numCases;i++){
			instance = test.getCaseListMem().get(i);
			correctLabel = instance.getValue(target);
			model.classify(instance, classnumber).toArray(targetPosterior);
			predictedLabel = VectorOps.getIndexOfMaxValue(targetPosterior);
			cr.addResult(correctLabel, predictedLabel, targetPosterior);
		}
		return cr;
	}
		
	public classificationResult[] kFoldCrossValidation(int K, Classifier model) throws InvalidEditException {
		splitCases(K);
		classificationResult[] results = new classificationResult[K];
		for(int fold = 0;fold < K;fold++){
			DataBaseCases test  = new DataBaseCases("test"+fold+".dbc",folds.elementAt(fold));
			DataBaseCases train = mergeCasesExcludeFold(fold);
			model.learn(train, classIdx);
			results[fold] = testClassifier(model, test, classIdx);
		}
		return results;
	}
	
	public static final void printKFoldStatisics(classificationResult[] results, boolean verbose){
		double[] rates = new double[results.length];
		for(int i=0; i < results.length;i++){
			if(verbose){
				System.out.println("-------Fold "+i+"-------");
				results[i].printStatistics();
			}
			rates[i] = results[i].rate();
		}
		VectorOps.printMeanVarSD(rates);
	}
	
	public DataBaseCases mergeCasesExcludeFold(int fold){
		CaseListMem retval = new CaseListMem(data.getVariables());
		for(int i=0; i< folds.size(); i++){
			if(i == fold) continue;
			retval.merge(folds.elementAt(i));
		}
		return new DataBaseCases("train.dbc",retval);
	}
	
	private void splitCases(int K) throws InvalidEditException{
		Vector<Configuration> casesVect = new Vector<Configuration>();
		CaseListMem clm = this.data.getCaseListMem();
		Configuration conf;
		for(int i=0;i<clm.getNumberOfCases();i++) casesVect.add(clm.get(i));
		
		CaseListMem[] folds = new CaseListMem[K];
		for(int i=0;i<K;i++) folds[i] = new CaseListMem(this.data.getVariables());
		while(!casesVect.isEmpty()){
			for(int i=0;i<K && !casesVect.isEmpty();i++){
				int pos = this.rand.nextInt(casesVect.size());
				conf = casesVect.elementAt(pos);
				casesVect.removeElementAt(pos);
				folds[i].put(conf);
			}
		}
		this.folds.clear();
		for(int i=0;i<K;i++){
			this.folds.add(folds[i]);
		}
	}

	
	public static class classificationResult{
		private double[] predictedCounts;
		private double[] counts;
		private int correct = 0, total = 0;
		private double ll;
		public classificationResult(int numLabels){
			predictedCounts = new double[numLabels];
			counts = new double[numLabels];
			ll = 0.0;
		}
		
		public double rate(){
			return ((double)correct)/total;
		}
		
		public double logLikelihood(){
			return ll;
		}
		
		public double logLikelihoodPerCase(){
			return ll/total;
		}
		
		public void addResult(int correctLabel, int predictedLabel, Double[] classPosterior){
			total++;
			predictedCounts[predictedLabel]++;
			counts[correctLabel]++;
			ll += MathUtils.log2(classPosterior[predictedLabel]);
			if(correctLabel == predictedLabel) correct++;
		}
		
		public void addResult(int correctLabel, int predictedLabel, double[] classPosterior){
			total++; 
			predictedCounts[predictedLabel]++; 
			counts[correctLabel]++; 
			ll += MathUtils.log2(classPosterior[predictedLabel]);
			if(correctLabel == predictedLabel) correct++;
		}
		
		public double[] getDistributionOfPredictions(){
			return VectorOps.copyAndNormalise(predictedCounts);
		}
		
		public double kFoldCVErrorRate(int k, DataBaseCases dbc){
			return -1.0;
		}
		
		public double[] getLabelDistributionOfLabels(){
			return VectorOps.copyAndNormalise(counts);
		}
		
		public void printStatistics(){
			System.out.println("Distribution of predictions : "+VectorOps.doubleArrayToString(VectorOps.copyAndNormalise(predictedCounts))+"\n" +
							   "Distribution of labels      : "+VectorOps.doubleArrayToString(VectorOps.copyAndNormalise(counts))+"\n" +
							   "Classification rate         : "+this.correct+"/"+this.total+" = "+this.rate()+"\n");
		}
	}
}

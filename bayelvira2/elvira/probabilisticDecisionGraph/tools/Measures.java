package elvira.probabilisticDecisionGraph.tools;



import java.util.*;

import elvira.CaseList;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.probabilisticDecisionGraph.tools.*;

//import java.io.*;

/**
 * 
 * 
 * @author dalgaard
 *
 */
public final class Measures {


	public static final double entropy(FiniteStates var, CaseList cases){
		double H = 0.0;
		int j = cases.getVariables().indexOf(var);
		double count[] = new double[var.getNumStates()];
		for(int i=0;i<cases.getNumberOfCases();i++){
			count[(int)cases.getValue(i, j)]++;
		}
		VectorOps.normalise(count);
		for(int i=0; i<count.length;i++){
			H += -1.0*count[i]* MathUtils.log2(count[i]);
		}
		return H;
	}
	
	public static final double giniImpurity(FiniteStates var, CaseList cases){
		double G = 0.0;
		int j = cases.getVariables().indexOf(var);
		double count[] = new double[var.getNumStates()];
		for(int i=0;i<cases.getNumberOfCases();i++){
			count[(int)cases.getValue(i,j)]++;
		}
		VectorOps.normalise(count);
		for(int i=0;i<count.length;i++){
			G += count[i]*count[i];
		}
		G = 1.0 - G;
		return G;
	}
	
	public static final double mutualInformation(FiniteStates x, FiniteStates y, CaseList cases, Integer zeroCells){
		double mi = 0.0;
		int statesx = x.getNumStates();
		int statesy = y.getNumStates();

		double[] marginalx = new double[statesx];
		for(int i=0;i<statesx;i++) marginalx[i] = 0.0;
		
		double[] marginaly = new double[statesy];
		for(int i=0;i<statesy;i++) marginaly[i] = 0.0;
		
		int numJoint = statesx * statesy;
		double[] joint = new double[numJoint];
		for(int i=0;i<numJoint;i++) joint[i] = 0.0;
		
		int numCases = cases.getNumberOfCases();
		int jointConf;
		// collect statistics
		for(int d = 0;d<numCases;d++){
			Configuration c = cases.get(d);
			marginalx[c.getValue(x)]++;
			marginaly[c.getValue(y)]++;
			jointConf = c.getValue(x) + statesx*c.getValue(y);
			joint[jointConf]++;
		}
		VectorOps.normalise(marginalx);
		VectorOps.normalise(marginaly);
		VectorOps.normalise(joint);
		
		// compute mutual information
		for(int valx = 0; valx < marginalx.length; valx++){
			if(marginalx[valx] == 0.0){ 
				zeroCells++;
				continue;
			}
			for(int valy=0;valy<marginaly.length;valy++){
				if(marginaly[valy] == 0.0){
					zeroCells++;
					continue;
				}
				jointConf = valx + statesx * valy;
				if(joint[jointConf] == 0.0){
					zeroCells++;
					continue;
				}
				mi += joint[jointConf] * MathUtils.log2(joint[jointConf] / (marginalx[valx] * marginaly[valy]));
			}
		}
		return mi;
	}
	
	public static final double conditionalMutualInformation(FiniteStates x, FiniteStates y, Vector<CaseList> partitions){
		return conditionalMutualInformation(x, y, partitions, new Integer(0), 5);
	}
	
	public static final double conditionalMutualInformation(FiniteStates x, FiniteStates y, Vector<CaseList> partitions, Integer zeroCells, int minimumCellCount){
		double cmi = 0.0;
		int cartProd = x.getNumStates() * y.getNumStates();
		for(CaseList cl : partitions){
			if(cl.getNumberOfCases() < cartProd * minimumCellCount){
				continue;
			}
			cmi += mutualInformation(x, y, cl, zeroCells);
		}
		return cmi;
	}
	/*
	public static final double pvalConditionalIndependenceHypothesis(FiniteStates x, FiniteStates y, Vector<CaseList> partitions){
		Integer zeroCells = new Integer(0);
		int numCases = 0;
		for(CaseList cl : partitions){
			numCases += cl.getNumberOfCases();
		}
		double cmi = conditionalMutualInformation(x, y, partitions, zeroCells, 5);
		int df = partitions.size() * (x.getNumStates() - 1) * (y.getNumStates() - 1) - zeroCells;
		double chisqstat = 2* numCases * cmi;
		double p = elvira.tools.Chi2.pochisq(chisqstat, df);
		return p;
	}*/
}

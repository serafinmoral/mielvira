package elvira.tools;

import java.util.ArrayList;

public class PropagationStatisticsAOUID extends PropagationStatistics {
	//Number of nodes created in the AO graph
	int createdNodes;
	//Expected utility in each instant of time
	public ArrayList<Double> expectedUtility;
	
	//First option selected in each instant of time, if we have a branch or a decision
	//Otherwise we have value -1 for each box of the array.
	//For the decision we store the index of the value selected
	//For the branches we store the index of the child selected
	public ArrayList<Integer> decisionToTake;

	public PropagationStatisticsAOUID() {
		super();
		createdNodes = 0;
		expectedUtility = new ArrayList();
		decisionToTake = new ArrayList();
		// TODO Auto-generated constructor stub
	}

	public int getCreatedNodes() {
		return createdNodes;
	}

	public void setCreatedNodes(int createdNodes) {
		this.createdNodes = createdNodes;
	}

	//It adds a new value for time, increasing the last time existing in 'times'.
	//It's used when we have to stop the crono and continue after a certain time.
	public void addToLastTime(double time) {
		// TODO Auto-generated method stub
		this.addTime((Double)(this.getTimes().lastElement())+time);
	}

	public void addExpectedUtility(double d) {
		// TODO Auto-generated method stub
		expectedUtility.add(d);
	}
	
	public void addDecisionToTake(int d) {
		// TODO Auto-generated method stub
		decisionToTake.add(d);
	}

	public ArrayList<Integer> getDecisionToTake() {
		return decisionToTake;
	}

	public void setDecisionTaken(ArrayList<Integer> decisionToTake) {
		this.decisionToTake = decisionToTake;
	}

	public ArrayList<Double> getExpectedUtility() {
		return expectedUtility;
	}

	public void setExpectedUtility(ArrayList<Double> expectedUtility) {
		this.expectedUtility = expectedUtility;
	}
	

}

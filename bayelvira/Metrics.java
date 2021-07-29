
/**
 * Metrics.java
 *
 * This class implements the abstract class of the all possible metrics that 
 * score a Bayes Net or a list of nodes from data.
 *
 * Created: Thu Nov  4 18:22:31 1999
 *
 * @author P. Elvira
 * @version 1.0
 */

import DataBaseCases;

public abstract class Metrics  {
    
    private DataBaseCases data;    // The data

    /**
     * This method scores a Bayes Net from data base of cases.
     * @param Bnet b. The Bayes Net to be scoring.
     * @return double. The score.
     */

    
    public abstract double score (Bnet b);

    /**
     * This method scores a Node List from data base of cases.
     * @param NodeList vars. The Node List.
     * @return double. The score.
     */


    public abstract double score (NodeList vars);

    /** Access methods **/

    public DataBaseCases getData(){
	return data;
    }

    public void setData(DataBaseCases data){
	this.data = data;
    }

} // Metrics


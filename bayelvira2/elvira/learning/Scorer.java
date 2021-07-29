/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT.learningdb;

import elvira.Node;
import elvira.FiniteStates;
import elvira.Configuration;
import elvira.database.DataBaseCases;
import elvira.learning.BDeMetrics;

import java.util.HashMap;
/**
 *
 * @author mgomez
 */
public class Scorer {

    /**
     * Data member to store the database
     */
    private DataBaseCases database;

    /**
     * Data member to store the metric
     */
    private BDeMetrics metric;

    /**
     * Data member to store the scores
     */
    private HashMap<Node,HashMap<Node,HashMap<Configuration,Double>>> store;

    /**
     * Data member to store the frequencies
     */
    private HashMap<Node,HashMap<Configuration,double[]>> frequencies;

    /**
     * Data member to count the number of tests retrieved from the store
     */
    private int testsRetrieved;

    /**
     * Data member to count the number of tests required
     */
    private int testsRequired;

    /**
     * Data member to count the number of freqs retrieved from the store
     */
    private int freqsRetrieved;

    /**
     * Data member to count the number of freqs required
     */
    private int freqsRequired;

    /**
     * Class constructor
     */
    public Scorer(DataBaseCases database){
        this.database=database;

        //  Creates the metric
        metric = new BDeMetrics(database);

        // Creates the data structure for storing the tests
        store=new HashMap<Node,HashMap<Node,HashMap<Configuration,Double>>>();

        // Creates the data structure for storing the frequencies
        frequencies=new HashMap<Node,HashMap<Configuration,double[]>>();

        // Set retrieved and required to 0
        testsRetrieved=0;
        testsRequired=0;
    }

    /**
     * Perform a new score or return its value if it is already computed
     * @param nodeX
     * @param nodeY
     * @param conf
     * @return score value
     */
    public double performTest(Node nodeX, Node nodeY, Configuration conf){
        HashMap<Node,HashMap<Configuration,Double>> testsForNodeX=store.get(nodeX);
        HashMap<Configuration,Double> testsForNodeY=null;
        Configuration configurationCopy;
        Double score=null;
//System.out.print("NodeX: "+nodeX.getName());
//if(nodeY != null){
//    System.out.print("  NodeY: "+nodeY.getName());
//}
//System.out.println();
//if (conf != null){
//    System.out.println(" Condicionado a .........");
//    conf.pPrint();
//    System.out.println();
//}

        // Add one to required
        testsRequired++;

        // Before performing the test check if it is already computed
        if (testsForNodeX == null){
//System.out.println("Creado almacenamiento para "+nodeX.getName());
            testsForNodeX=new HashMap<Node,HashMap<Configuration,Double>>();
            store.put(nodeX, testsForNodeX);
        }
        else{
           // Get test for nodeY
           testsForNodeY=testsForNodeX.get(nodeY);
        }
//System.out.println("Test para y null : "+(testsForNodeY == null));
        // If this is not null, look for the configuration
        if (testsForNodeY != null){
            score=testsForNodeY.get(conf);

            // Maybe score is null
            if (score != null){
               testsRetrieved++;              
               return score;
            }
        }

        // If score is null or tests is null, compute the score
        if (nodeY != null){
            score=metric.score((FiniteStates)nodeX, (FiniteStates)nodeY, conf);
        }
        else{
            score=metric.score((FiniteStates)nodeX, conf);
        }

        // Store the score
        if (testsForNodeY == null){
            testsForNodeY=new HashMap<Configuration,Double>();
            testsForNodeX.put(nodeY,testsForNodeY);
        }
        configurationCopy=conf.duplicate();
        testsForNodeY.put(configurationCopy,score);

        // return the result      
        return score;
    }

    /**
     * Perform a new score or return its value if it is already computed
     * @param nodeX
     * @param conf
     * @return score value
     */
    public double performTest(Node nodeX, Configuration conf){
        double score;

        // Call to perform test method
        score=this.performTest(nodeX, null, conf);

        // return the result
        return score;
    }

    public double[] computeFrequencies(Node nodeX, Configuration conf){
        HashMap<Configuration,double[]> freqsForNodeX=frequencies.get(nodeX);
        Configuration configurationCopy;
        double[] freqs=null;

        // Add one to required
        freqsRequired++;

        // Before performing the test check if it is already computed
        if (freqsForNodeX == null){
            freqsForNodeX=new HashMap<Configuration,double[]>();
            frequencies.put(nodeX, freqsForNodeX);
        }
        else{
            freqs=freqsForNodeX.get(conf);
        }

        // May be freqs is null: in this case it is needed to compute
        if (freqs != null){
            freqsRetrieved++;
            return freqs;
        }

        // It is needed to get the frequencies
        freqs=database.getFreq((FiniteStates)nodeX, conf);
        configurationCopy=conf.duplicate();
        freqsForNodeX.put(configurationCopy,freqs);

        // return the result
        return freqs;
    }

    /**
     * Gets the value of retrieved
     * @return
     */
    public int getTestsRetrieved(){
        return testsRetrieved;
    }

    /**
     * Gets the value of required
     */
    public int getTestsRequired(){
        return testsRequired;
    }

    /**
     * Gets the value of retrieved
     * @return
     */
    public int getFrequenciesRetrieved(){
        return freqsRetrieved;
    }

    /**
     * Gets the value of required
     */
    public int getFrequenciesRequired(){
        return freqsRequired;
    }

    /**
     * Gets access to the database
     */
    public DataBaseCases getDataBase(){
        return database;
    }
}

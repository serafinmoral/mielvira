package weka.filters.supervised.attribute;

import elvira.learning.preprocessing.Joining;

import weka.filters.*;
import weka.core.*;
import java.io.*;
import java.util.*;
import weka.core.Capabilities.Capability;

/**
 * This class provides an integration of Joining class of Elvira in Weka package.
 * @author Andres
 */
public class E_Joining extends E_Filter 
                      implements SupervisedFilter, OptionHandler {

    static final long serialVersionUID = 9072165625877380532L;

    /** Stores which columns to Joinining */
    protected Range m_JoiningCols = new Range();
    
    protected Joining Ejoining=null;
    
    int mMetricGrouping=Joining.GBDeMetric;
    
    int mJoiningAlgorithm=1;
    
    int mMetricJoining=Joining.JPValueChiTest;

    /*If states with null frequency are grouped in a unique state*/
    boolean GROUP_NULLFREQUENCY_STATES=true;

    /*If it is used a threshold to join the variables*/
    boolean THRESHOLD_JOIN=false;
    
    /*If it is used a threshold to group the states of the variables*/
    boolean THRESHOLD_GROUPING=false;
    
    /** If true, outputs debugging info */
    private boolean m_Debug = false;
    
    double alphalevel=0.05;

    
    
    
    
    public static final Tag[] TAGS_METRICS=
            {
        new Tag(Joining.JBDeMetric, "BDeMetric"),
        new Tag(Joining.JL1OMetric, "L1OMetric"),
        new Tag(Joining.JPValueChiTest, "PValueChiTest"),
        new Tag(Joining.JPValueTTest, "PValueTTest"),
            };
    
    
    public static final Tag[] TAGS_JOINING_ALGORITHM =
            {
            new Tag(Joining.SCHEME0, "GR+JOINxGR"),
            new Tag(Joining.SCHEME1, "JOINxGR+GR"),
            new Tag(Joining.SCHEME2, "(JOINxJOIN...|GR)xJOIN..."),
            new Tag(Joining.SCHEME3, "RECURSIVE JOINxGR"),
            new Tag(Joining.SCHEME4, "JOIN+GR"),
            new Tag(Joining.SCHEME5, "GR+JOINxGR"),
            new Tag(Joining.SCHEME6, "ONLY JOIN"),
            new Tag(Joining.SCHEME7, "ONLY GROUPING"),
            };
    
    
    
    
    /**
     * Creates a new instance of E_Joining
     */
    public E_Joining() {
        setAttributeIndices("first-last");
    }

  
    /** 
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();

        // attributes
        result.enableAllAttributes();
        result.enable(Capability.MISSING_VALUES);

        // class
        result.enable(Capability.NOMINAL_CLASS);

        return result;
    }    
    
    /**
    * Returns a string describing this filter
    *
    * @return a description of the filter suitable for
    * displaying in the explorer/experimenter gui
    */
    public String globalInfo() {
        String comment="An instance filter that Joinin attributes.\n\n";
	String filterOptions = "";
        filterOptions += "\nFilter options:\n\n";
	Enumeration enu = listOptions();
	while (enu.hasMoreElements()) {
	  Option option = (Option) enu.nextElement();
	  filterOptions += option.synopsis() + '\n'
	    + option.description() + "\n";
	}
        return comment+filterOptions;
    }

    public double getAlphaLevel(){
        return this.alphalevel;
    }

    public void setAlphaLevel(double alpha){
        this.alphalevel=alpha;
    }
    
    public SelectedTag getJoiningAlgorithm(){
        return new SelectedTag(this.mJoiningAlgorithm, TAGS_JOINING_ALGORITHM);
    }

    public void setJoiningAlgorithm(SelectedTag newJAlgorithm){
        if (newJAlgorithm.getTags() == TAGS_JOINING_ALGORITHM) {
                this.mJoiningAlgorithm = newJAlgorithm.getSelectedTag().getID();
        }
        
    }

    public SelectedTag getMetricGrouping(){
        return new SelectedTag(this.mMetricGrouping, TAGS_METRICS);
    }

    public void setMetricGrouping(SelectedTag newGAlgorithm){
        if (newGAlgorithm.getTags() == TAGS_METRICS) {
                this.mMetricGrouping = newGAlgorithm.getSelectedTag().getID();
        }
    }
    
    public SelectedTag getMetricJoining(){
        return new SelectedTag(this.mMetricJoining, TAGS_METRICS);
    }

    public void setMetricJoining(SelectedTag newmetric){
        if (newmetric.getTags() == TAGS_METRICS) {
                this.mMetricJoining = newmetric.getSelectedTag().getID();
        }
    }


    /**
     * Return if the states with null frequency are grouped in a unique state.
     */
    public boolean getGroupNFrequencyStates(){
        return this.GROUP_NULLFREQUENCY_STATES;
    }

    /**
     * Set if the states with null frequency are grouped in a unique state.
     */
    public void setGroupNFrequencyStates(boolean state){
        this.GROUP_NULLFREQUENCY_STATES=state;
    }

    public boolean getThesholdJoin(){
        return this.THRESHOLD_JOIN;
    }

    public void setThesholdJoin(boolean state){
        this.THRESHOLD_JOIN=state;
    }

    public boolean getThesholdGrouping(){
        return this.THRESHOLD_GROUPING;
    }

    public void setThesholdGrouping(boolean state){
        this.THRESHOLD_GROUPING=state;
    }
    
    public boolean getDebug(){
        return this.m_Debug;
    }

    public void setDebug(boolean state){
        this.m_Debug=state;
    }
    
    /**
    * Gets the current range selection
    *
    * @return a string containing a comma separated list of ranges
    */
    public String getAttributeIndices() {

        return m_JoiningCols.getRanges();
    }
    
    /**
    * Sets which attributes are to be Discretized (only numeric
    * attributes among the selection will be Discretized).
    *
    * @param rangeList a string representing the list of attributes. Since
    * the string will typically come from a user, attributes are indexed from
    * 1. <br>
    * eg: first-3,5,6-last
    * @exception IllegalArgumentException if an invalid range list is supplied 
    */
    public void setAttributeIndices(String rangeList) {
        this.m_JoiningCols.setRanges(rangeList);
    }
     
   /**
    * Gets the current settings of the filter.
    *
    * @return an array of strings suitable for passing to setOptions
    */
    public String [] getOptions() {    
        String [] options = new String [20];
        int current = 0;

        
        if (!getAttributeIndices().equals("")) {
          options[current++] = "-R"; options[current++] = getAttributeIndices();
        }

        options[current++] = "-GA";
        options[current++] = this.TAGS_METRICS[this.mMetricGrouping].getIDStr();
        
        options[current++] = "-M";
        options[current++] = this.TAGS_METRICS[this.mMetricJoining].getIDStr();

        options[current++] = "-JA";
        options[current++] = this.TAGS_JOINING_ALGORITHM[this.mJoiningAlgorithm].getIDStr();

        if (this.getGroupNFrequencyStates())
            options[current++] = "-GNFS";

        if (m_Debug) {
           options[current++] = "-Debug";
        }
        
        if (this.THRESHOLD_JOIN) {
           options[current++] = "-JTH";
        }
        
        if (this.THRESHOLD_GROUPING) {
           options[current++] = "-GTH";
        }

        options[current++] = "-Alpha"; options[current++] = ""+getAlphaLevel();        

        while (current < options.length) {
          options[current++] = "";
        }
        
        return options;
        
    }
    
    
    /**
    * Parses the options for this object. Valid options are: <p>
    *
    * -R col1,col2-col4,... <br>
    * Specifies list of columns to Discretize. First
    * and last are valid indexes. (default none) <p>
    *
    * -V <br>
    * Invert matching sense.<p>
    *
    * -D <br>
    * Make binary nominal attributes. <p>
    *
    * -E <br>
    * Use better encoding of split point for MDL. <p>
    *   
    * -K <br>
    * Use Kononeko's MDL criterion. <p>
    * 
    * @param options the list of options as an array of strings
    * @exception Exception if an option is not supported
    */
    public void setOptions(String[] options) throws Exception {    
        
        String convertList = Utils.getOption('R', options);
        if (convertList.length() != 0) {
          setAttributeIndices(convertList);
        } else {
          setAttributeIndices("first-last");
        }

        convertList = Utils.getOption("Alpha",options);
        if (convertList.length() != 0) {
          this.setAlphaLevel(Double.parseDouble(convertList));
        } else {
          this.setAlphaLevel(0.05);
        }

        convertList = Utils.getOption("GA",options);
        for (int i=0; i<this.TAGS_METRICS.length; i++)
            if (convertList.compareTo(this.TAGS_METRICS[i].getIDStr()) == 0) {
                    this.setMetricGrouping(new SelectedTag(this.TAGS_METRICS[i].getID(),TAGS_METRICS));
                    break;
            }
        
        convertList = Utils.getOption("M",options);
        for (int i=0; i<this.TAGS_METRICS.length; i++)
            if (convertList.compareTo(this.TAGS_METRICS[i].getIDStr()) == 0) {
                    this.setMetricJoining(new SelectedTag(this.TAGS_METRICS[i].getID(),TAGS_METRICS));
                    break;
            }

        convertList = Utils.getOption("JA",options);
        for (int i=0; i<this.TAGS_JOINING_ALGORITHM.length; i++)
            if (convertList.compareTo(this.TAGS_JOINING_ALGORITHM[i].getIDStr()) == 0) {
                    this.setJoiningAlgorithm(new SelectedTag(this.TAGS_JOINING_ALGORITHM[i].getID(),TAGS_JOINING_ALGORITHM));
                    break;
            }
        
        this.m_Debug = Utils.getFlag("Debug", options);
        
        if (Utils.getFlag("GNFS", options))
            this.setGroupNFrequencyStates(true);
        else
            this.setGroupNFrequencyStates(false);
        
        if (Utils.getFlag("JTH", options))
            this.THRESHOLD_JOIN=true;
        else
            this.THRESHOLD_JOIN=false;
        
        if (Utils.getFlag("GTH", options))
            this.THRESHOLD_GROUPING=true;
        else
            this.THRESHOLD_GROUPING=false;

    }
    
   
    /**
    * Gets an enumeration describing the available options.
    *
    * @return an enumeration of all the available options.
    */
    
    public Enumeration listOptions() {

        Vector newVector = new Vector(8);

        newVector.addElement(new Option(
              "\tSpecifies list of columns to Discretize. First"
              + " and last are valid indexes.\n"
              + "\t(default none)",
              "R", 1, "-R <col1,col2-col4,...>"));

        newVector.addElement(new Option(
              "\tGrouping Metric (BDeMetric, L10Metric, PValueChiTest, PValueTTest)\n"
              + "\t(default BDeMetric)",
              "GA", 1, "-GA <type>"));

        newVector.addElement(new Option(
              "\tSpecificies the joining algorithm.\n"
              +"\t\t 0: => Joining by first search in O(n^2)."
              +"\t\t 1: => Joining by whole search in O(n^3)."
              + "\t(default 1 => Joining by whole search in O(n^2).)",
              "JA", 1, "-JA <type>"));

        newVector.addElement(new Option(
              "\tJoining  Metric (BDeMetric, L10Metric, PValueChiTest, PValueTTest)\n"
              + "\t(default PValueChiTest)",
              "M", 1, "-M <type>"));
        
        newVector.addElement(
           new Option("\tOutput debugging information\n",
                      "Debug", 0,"-Debug"));
        
        newVector.addElement(
            new Option("\tSpecificies if the states with null frequency are grouped"
                +"\t in a unique state\n",
                "GNFS", 0,"-GNFS"));

        newVector.addElement(
            new Option("\tSpecificies if it is used a threshold to join the variables",
                "JTH", 0,"-JTH"));

        newVector.addElement(
            new Option("\tSpecificies if it is used a threshold to group the states of the variables",
                "GTH", 0,"-GTH"));

        newVector.addElement(
            new Option("\tSpecificies if it is used a threshold to group the states of the variables",
                "Alpha", 1,"-Alpha value"));
        
        return newVector.elements();
    }
        
  /**
   * Sets the format of the input instances.
   *
   * @param instanceInfo an Instances object containing the input instance
   * structure (any instances contained in the object are ignored - only the
   * structure is required).
   * @return true if the outputFormat may be collected immediately
   * @exception Exception if the input format can't be set successfully
   */
  public boolean setInputFormat(Instances instanceInfo) throws Exception {

    super.setInputFormat(instanceInfo);

    this.m_JoiningCols.setUpper(instanceInfo.numAttributes() - 1);
    this.Ejoining = null;
    
    for (int i=0; i< instanceInfo.numAttributes() ; i++){
        Attribute att=instanceInfo.attribute(i);
        if (!att.isNominal()) {
          throw new UnsupportedClassTypeException("Joining not possible:"+ att.name() +"  is not nominal!");
        }

    }
    

    // If we implement loading cutfiles, then load 
    //them here and set the output format
    return false;
  }

  /**
   * Signifies that this batch of input to the filter is finished. If the 
   * filter requires all instances prior to filtering, output() may now 
   * be called to retrieve the filtered instances.
   *
   * @return true if there are instances pending output
   * @exception IllegalStateException if no input structure has been defined
   */
  public boolean batchFinished() {

    if (getInputFormat() == null) {
      throw new IllegalStateException("No input instance format defined");
    }
    if (this.Ejoining == null) {
        Instances result=null;
        try{
            this.Ejoining=new Joining();

            this.Ejoining.setTest(false);

            this.Ejoining.loadData(elvira2weka.Dbc2Intances.Instances2Dbc(this.getInputFormat()));
            this.Ejoining.setClassVar(this.getInputFormat().classIndex()+1);
            this.Ejoining.setMetricGrouping(this.getMetricGrouping().getSelectedTag().getID());
            this.Ejoining.setJoiningAlgorithm(this.getJoiningAlgorithm().getSelectedTag().getID());
            this.Ejoining.setGroupNFrequencyStates(this.getGroupNFrequencyStates());
            this.Ejoining.setMetricJoining(this.getMetricJoining().getSelectedTag().getID());
            this.Ejoining.setAlphaLevel(this.getAlphaLevel());
            this.Ejoining.THRESHOLD_JOIN=this.THRESHOLD_JOIN;
            this.Ejoining.THRESHOLD_GROUPING=this.THRESHOLD_GROUPING;
            this.Ejoining.debug=this.m_Debug;

            Vector target=new Vector();
            target.addElement(this.getAttributeIndices());
            this.Ejoining.setTargetVariables(target);
            this.Ejoining.selfapply();
            result=elvira2weka.Dbc2Intances.Dbc2Intances(this.Ejoining.getDataBaseCases());              
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        result.setClassIndex(-1);
        Enumeration enu=result.enumerateAttributes();
        while(enu.hasMoreElements()){
            Attribute att=(Attribute)enu.nextElement();
            if (att.equals(this.getInputFormat().classAttribute())){
                result.setClass(att);
                break;
            }
        }
        setOutputFormat(result);

        // Convert pending input instances
        for(int i = 0; i < result.numInstances(); i++) {
            push(result.instance(i));
        }
    }
        
        flushInput();
        m_NewBatch = true;
        return (numPendingOutput() != 0);
  }
 
  
  private void convertInstance(Instance inst){
      
      Instances instances=new Instances(this.getInputFormat());
      instances.delete();
      instances.add(inst);
      try{
        instances=elvira2weka.Dbc2Intances.Dbc2Intances(this.Ejoining.applyAgain(elvira2weka.Dbc2Intances.Instances2Dbc(instances)));
      }catch(Exception e){
          e.printStackTrace();
          System.exit(0);
      }
      inst=instances.firstInstance();
      inst.setDataset(getOutputFormat());
      push(inst);      
  }
  /**
   * Input an instance for filtering. Ordinarily the instance is processed
   * and made available for output immediately. Some filters require all
   * instances be read before producing output.
   *
   * @param instance the input instance
   * @return true if the filtered instance may now be
   * collected with output().
   * @exception IllegalStateException if no input format has been defined.
   */
  public boolean input(Instance instance) {

    if (getInputFormat() == null) {
      throw new IllegalStateException("No input instance format defined");
    }
    if (m_NewBatch) {
      resetQueue();
      m_NewBatch = false;
    }
    
    if (this.Ejoining != null) {
      this.convertInstance(instance);
      return true;
    }else{
        bufferInput(instance);
        return false;
    }
  }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] argv) {
        // TODO code application logic here
        try {
          if (Utils.getFlag('b', argv)) {
            Filter.batchFilterFile(new E_Joining(), argv);
          } else {
            Filter.filterFile(new E_Joining(), argv);
          }
        } catch (Exception ex) {
          System.out.println(ex.getMessage());
        }
    }
    
}

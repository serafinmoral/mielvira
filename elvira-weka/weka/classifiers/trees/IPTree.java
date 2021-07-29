/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    IPTree.java
 *    Copyright (C) 1999 Eibe Frank
 *
 */

package weka.classifiers.trees;

import weka.core.Capabilities.Capability;
import weka.core.Capabilities;
import weka.classifiers.*;
import weka.core.*;
import java.io.*;
import java.util.*;

/**
 * Class implementing an IPTree decision tree classifier. For more
 * information, see<p>
 *
 * R. Quinlan (1986). <i>Induction of decision
 * trees</i>. Machine Learning. Vol.1, No.1, pp. 81-106.<p>
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision: 1.1 $ 
 */
public class IPTree extends Classifier implements OptionHandler, AdditionalMeasureProducer{

  /** The node's successors. */ 
  IPTree[] m_Successors;

  /** Attribute used for splitting. */
  Attribute m_Attribute;

  /** Class value if node is leaf. */
  double m_ClassValue;

  /** Class distribution if node is leaf. */
  double[] m_Distribution;

  /** Class attribute of dataset. */
  Attribute m_ClassAttribute;

  
  
  /** s value of the Imprecise Dirichlet Model that it is used for build the model*/
  double m_SValue=1.0;
  
  /** Level of the tree when the building of tree stops altought there is an improvement in the entropy. 
   *  If it sets to -1, the unique stop criterium is the criterium of deterioration of the entropy*/
  int m_StopLevel=-1; 
  
  /** Constant values that indicate the entropy that it is used to build the decision tree.*/
  public static final int ENTROPY=0;
  public static final int IMPRECISE_ENTROPY=1;
  public static final int GINI_INDEX=2;
  public static final int J48_INFOGAINRATIO=3;
  
  /** This field stores the index of the entropy criterium used to build the decision tree*/
  int m_SplitMetric=IMPRECISE_ENTROPY;
  
  public static final Tag[] TAGS_SPLIT_METRIC =
    {
        new Tag(ENTROPY, "Entropy"),
        new Tag(IMPRECISE_ENTROPY, "ImpreciseEntropy"),
        new Tag(GINI_INDEX, "GiniIndex"),
        new Tag(J48_INFOGAINRATIO, "J48InfoGainRatio"),
  };
  
  
  /** if this field is set to false, the class of a leave where the number of occurrences of the configuration
        that defines is zero is set to the most probable class that defines its parent if if was a leave*/
  boolean m_MisclassifiedAllowed=false;
  
  /** If this field contains a value k then the attribute with k-th hightest value of Information Gain will be used
      as a root of the node. The default value is k=1.*/
  int m_KThRootAttribute=1;
 
  
  
  /**
   * Returns default capabilities of the classifier.
   *
   * @return      the capabilities of this classifier
   */
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();

    // attributes
    result.enable(Capability.NOMINAL_ATTRIBUTES);

    // class
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    // instances
    result.setMinimumNumberInstances(0);
    
    return result;
  }
  
  /**
   * Returns a superconcise version of the model
   */
  public String toSummaryString() {

    return "Number of leaves: " + numLeaves() + "\n"
         + "Size of the tree: " + numNodes() + "\n";
  }
  /**
   * Returns number of leaves in tree structure.
   */
  public double numLeaves() {
    
    double num = 0;
    int i;
    
    if (this.m_Attribute==null)
      return 1;
    else
      for (i=0;i<this.m_Successors.length;i++)
	num += this.m_Successors[i].numLeaves();
        
    return num;
  }

  /**
   * Returns number of nodes in tree structure.
   */
  public double numNodes() {
    
    double no = 1;
    int i;
    
    if (this.m_Attribute!=null){
       no=1; 
       for (i=0;i<this.m_Successors.length;i++)
	no = no+this.m_Successors[i].numNodes();
    }
    return no;
  }

  /**
   * Returns the size of the tree
   * @return the size of the tree
   */
  public double measureTreeSize() {
    return numNodes();
  }

  /**
   * Returns the number of leaves
   * @return the number of leaves
   */
  public double measureNumLeaves() {
    return numLeaves();
  }

  /**
   * Returns the number of rules (same as number of leaves)
   * @return the number of rules
   */
  public double measureNumRules() {
    return numLeaves();
  }
  
  /**
   * Returns an enumeration of the additional measure names
   * @return an enumeration of the measure names
   */
  public Enumeration enumerateMeasures() {
    Vector newVector = new Vector(3);
    newVector.addElement("measureTreeSize");
    newVector.addElement("measureNumLeaves");
    newVector.addElement("measureNumRules");
    return newVector.elements();
  }

  /**
   * Returns the value of the named measure
   * @param measureName the name of the measure to query for its value
   * @return the value of the named measure
   * @exception IllegalArgumentException if the named measure is not supported
   */
  public double getMeasure(String additionalMeasureName) {
    if (additionalMeasureName.compareToIgnoreCase("measureNumRules") == 0) {
      return measureNumRules();
    } else if (additionalMeasureName.compareToIgnoreCase("measureTreeSize") == 0) {
      return measureTreeSize();
    } else if (additionalMeasureName.compareToIgnoreCase("measureNumLeaves") == 0) {
      return measureNumLeaves();
    } else {
      throw new IllegalArgumentException(additionalMeasureName 
			  + " not supported (j48)");
    }
  }
 
  /**
   * Returns a string describing the classifier.
   * @return a description suitable for the GUI.
   */
  public String globalInfo() {

    return  "Class for constructing an unpruned decision tree based on the IPTree "
      + "algorithm. Can only deal with nominal attributes. No missing values "
      + "allowed. Empty leaves may result in unclassified instances. For more "
      + "information see: \n\n"
      + " R. Quinlan (1986). \"Induction of decision "
      + "trees\". Machine Learning. Vol.1, No.1, pp. 81-106";
  }

  /**
   * Builds IPTree decision tree classifier.
   *
   * @param data the training data
   * @exception Exception if classifier can't be built successfully
   */
  public void buildClassifier(Instances data) throws Exception {

    if (!data.classAttribute().isNominal()) {
      throw new UnsupportedClassTypeException("IPTree: nominal class, please.");
    }
    Enumeration enumAtt = data.enumerateAttributes();
    while (enumAtt.hasMoreElements()) {
      if (!((Attribute) enumAtt.nextElement()).isNominal()) {
        throw new UnsupportedAttributeTypeException("IPTree: only nominal " +
                                                    "attributes, please.");
      }
    }
    Enumeration enu = data.enumerateInstances();
    while (enu.hasMoreElements()) {
      if (((Instance) enu.nextElement()).hasMissingValue()) {
        throw new NoSupportForMissingValuesException("IPTree: no missing values, "
                                                     + "please.");
      }
    }
    data = new Instances(data);
    data.deleteWithMissingClass(); 
    makeTree(data,0);
  }

  /**
   * Method for building an IPTree tree.
   *
   * @param data the training data
   * @exception Exception if decision tree can't be built successfully
   */
  void makeTree(Instances data, int level) throws Exception {

    // Check if no instances have reached this node.
    if (data.numInstances() == 0) {
      m_Attribute = null;
      m_ClassValue = Instance.missingValue();
      m_Distribution = new double[data.numClasses()];
      return;
    }

    // Compute attribute with maximum information gain.
    double[] infoGains = new double[data.numAttributes()];
    Enumeration attEnum = data.enumerateAttributes();
    while (attEnum.hasMoreElements()) {
      Attribute att = (Attribute) attEnum.nextElement();
      infoGains[att.index()] = computeInfoGain(data, att);
    }
    
    if (level==0){
        m_Attribute = data.attribute(Utils.sort(infoGains)[infoGains.length-this.getKTHRootAttribute()]);
    }else{
        m_Attribute = data.attribute(Utils.maxIndex(infoGains));
    }
    
    // Make leaf if information gain is zero. 
    // Otherwise create successors.
    if (Utils.eq(infoGains[m_Attribute.index()], 0) ||  (this.getStopLevel()!=-1 && this.getStopLevel()==(level-1))) {
      m_Attribute = null;
      m_Distribution = new double[data.numClasses()];
      Enumeration instEnum = data.enumerateInstances();
      while (instEnum.hasMoreElements()) {
        Instance inst = (Instance) instEnum.nextElement();
        m_Distribution[(int) inst.classValue()]++;
      }
      Utils.normalize(m_Distribution);
      m_ClassValue = Utils.maxIndex(m_Distribution);
      m_ClassAttribute = data.classAttribute();
    } else {
      Instances[] splitData = splitData(data, m_Attribute);
      m_Successors = new IPTree[m_Attribute.numValues()];
      for (int j = 0; j < m_Attribute.numValues(); j++) {
        m_Successors[j] = new IPTree();
        m_Successors[j].setOptions(this.getOptions());
        
        if (!m_MisclassifiedAllowed && splitData[j].numInstances()==0){
          m_Successors[j].m_Attribute = null;
          m_Successors[j].m_Distribution = new double[data.numClasses()];
          Enumeration instEnum = data.enumerateInstances();
          while (instEnum.hasMoreElements()) {
            Instance inst = (Instance) instEnum.nextElement();
            m_Successors[j].m_Distribution[(int) inst.classValue()]++;
          }
          Utils.normalize(m_Successors[j].m_Distribution);
          m_Successors[j].m_ClassValue = Utils.maxIndex(m_Successors[j].m_Distribution);
          m_Successors[j].m_ClassAttribute = data.classAttribute();
        }else{
            m_Successors[j].makeTree(splitData[j],level+1);
        }
        
      }
    }
  }

  /**
   * Classifies a given test instance using the decision tree.
   *
   * @param instance the instance to be classified
   * @return the classification
   */
  public double classifyInstance(Instance instance) 
    throws NoSupportForMissingValuesException {

    if (instance.hasMissingValue()) {
      throw new NoSupportForMissingValuesException("IPTree: no missing values, "
                                                   + "please.");
    }
    if (m_Attribute == null) {
      return m_ClassValue;
    } else {
      return m_Successors[(int) instance.value(m_Attribute)].
        classifyInstance(instance);
    }
  }

  /**
   * Computes class distribution for instance using decision tree.
   *
   * @param instance the instance for which distribution is to be computed
   * @return the class distribution for the given instance
   */
  public double[] distributionForInstance(Instance instance) 
    throws NoSupportForMissingValuesException {

    if (instance.hasMissingValue()) {
      throw new NoSupportForMissingValuesException("IPTree: no missing values, "
                                                   + "please.");
    }
    if (m_Attribute == null) {
      return m_Distribution;
    } else { 
      return m_Successors[(int) instance.value(m_Attribute)].
        distributionForInstance(instance);
    }
  }

  /**
   * Prints the decision tree using the private toString method from below.
   *
   * @return a textual description of the classifier
   */
  public String toString() {

    if ((m_Distribution == null) && (m_Successors == null)) {
      return "IPTree: No model built yet.";
    }
    StringBuffer text = new StringBuffer();
    text.append("IPTree\n\n" + toString(0));
    text.append("\n\nNumber of Leaves  : \t"+numLeaves()+"\n");
    text.append("\nSize of the tree : \t"+numNodes()+"\n");
    
    return text.toString();
  }

  /**
   * Computes information gain for an attribute.
   *
   * @param data the data for which info gain is to be computed
   * @param att the attribute
   * @return the information gain for the given attribute and data
   */
  double computeInfoGain(Instances data, Attribute att) 
    throws Exception {

    double infoGain = computeEntropy(data);
    Instances[] splitData = splitData(data, att);
    for (int j = 0; j < att.numValues(); j++) {
      if (splitData[j].numInstances() > 0) {
        infoGain -= ((double) splitData[j].numInstances() /
                     (double) data.numInstances()) *
          computeEntropy(splitData[j]);
      }
    }
    if (this.m_SplitMetric==IPTree.J48_INFOGAINRATIO){
        if (infoGain==0.0)
            return 0.0;
        double divinfogain= 0.0;
        for (int j = 0; j < att.numValues(); j++) {
          if (splitData[j].numInstances() > 0) {
            divinfogain -= (splitData[j].numInstances()/(double)data.numInstances())*Utils.log2(splitData[j].numInstances()/(double)data.numInstances());
          }
        }
        return infoGain/divinfogain;
    }else
        return infoGain;
  }

  /**
   * Computes the entropy of a dataset.
   * 
   * @param data the data for which entropy is to be computed
   * @return the entropy of the data's class distribution
   */
  double computeEntropy(Instances data) throws Exception {

    double [] classCounts = new double[data.numClasses()];
    Enumeration instEnum = data.enumerateInstances();
    while (instEnum.hasMoreElements()) {
      Instance inst = (Instance) instEnum.nextElement();
      classCounts[(int) inst.classValue()]++;
    }
    
    
    if (this.m_SplitMetric==IPTree.ENTROPY)
        return E_ContingencyTables.entropy(classCounts);
    else if (this.m_SplitMetric==IPTree.IMPRECISE_ENTROPY)
        return E_ContingencyTables.entropyImprecise(classCounts,this.getSValue());
    else if (this.m_SplitMetric==IPTree.GINI_INDEX)
        return E_ContingencyTables.entropyGiniIndex(classCounts);
    else if (this.m_SplitMetric==IPTree.J48_INFOGAINRATIO)
        return E_ContingencyTables.entropy(classCounts);
    else
        return 0.0;
        
  }

  /**
   * Splits a dataset according to the values of a nominal attribute.
   *
   * @param data the data which is to be split
   * @param att the attribute to be used for splitting
   * @return the sets of instances produced by the split
   */
  Instances[] splitData(Instances data, Attribute att) {

    Instances[] splitData = new Instances[att.numValues()];
    for (int j = 0; j < att.numValues(); j++) {
      splitData[j] = new Instances(data, data.numInstances());
    }
    Enumeration instEnum = data.enumerateInstances();
    while (instEnum.hasMoreElements()) {
      Instance inst = (Instance) instEnum.nextElement();
      splitData[(int) inst.value(att)].add(inst);
    }
    for (int i = 0; i < splitData.length; i++) {
      splitData[i].compactify();
    }
    return splitData;
  }

  /**
   * Outputs a tree at a certain level.
   *
   * @param level the level at which the tree is to be printed
   */
  String toString(int level) {

    StringBuffer text = new StringBuffer();
    
    if (m_Attribute == null) {
      if (Instance.isMissingValue(m_ClassValue)) {
        text.append(": null");
      } else {
        text.append(": " + m_ClassAttribute.value((int) m_ClassValue));
      } 
    } else {
      for (int j = 0; j < m_Attribute.numValues(); j++) {
        text.append("\n");
        for (int i = 0; i < level; i++) {
          text.append("|  ");
        }
        text.append(m_Attribute.name() + " = " + m_Attribute.value(j));
        text.append(m_Successors[j].toString(level + 1));
      }
    }
    return text.toString();
  }
  
    public Attribute getAttribute(){
      return this.m_Attribute;
    }
  
    public IPTree[] getSuccesors(){
      return this.m_Successors;
    }
  
    public double getSValue(){
        return this.m_SValue;
    }
    
    public void setSValue(double value){
        this.m_SValue=value;
    }
    
    public SelectedTag getSplitMetric(){
        return new SelectedTag(this.m_SplitMetric, IPTree.TAGS_SPLIT_METRIC);
    }

    public void setSplitMetric(SelectedTag newmetric){
        if (newmetric.getTags() == IPTree.TAGS_SPLIT_METRIC) {
                this.m_SplitMetric = newmetric.getSelectedTag().getID();
        }
    }
    
    public boolean getMissclassifiedAllowed(){
        return this.m_MisclassifiedAllowed;
    }

    public void setMissclassifiedAllowed(boolean state){
        this.m_MisclassifiedAllowed=state;
    }
    
    public void setStopLevel(int level){
        this.m_StopLevel=level;
    }
    
    public int getStopLevel(){
        return this.m_StopLevel;
    }
    
    public void setKTHRootAttribute(int number){
        this.m_KThRootAttribute=number;
    }
    
    public int getKTHRootAttribute(){
        return this.m_KThRootAttribute;
    }
    
    
   /**
    * Gets the current settings of the filter.
    *
    * @return an array of strings suitable for passing to setOptions
    */
    public String [] getOptions() {    
        String [] options = new String [14];
        int current = 0;

        if (m_Debug) {
           options[current++] = "-Debug";
        }

        options[current++] = "-S"; options[current++] = ""+getSValue();
        
        options[current++] = "-StopLevel"; options[current++] = ""+ getStopLevel();
        
        options[current++] = "-SM";
        switch (this.m_SplitMetric) {

                case (IPTree.ENTROPY) :
                        options[current++] = "Entropy";
                        break;

                case (IPTree.IMPRECISE_ENTROPY) :
                        options[current++] = "ImpreciseEntropy";
                        break;

                case (IPTree.GINI_INDEX) :
                        options[current++] = "GiniIndex";
                        break;

                case (IPTree.J48_INFOGAINRATIO) :
                        options[current++] = "J48InfoGainRatio";
                        break;

        }
        
        if (this.m_MisclassifiedAllowed)
            options[current++] = "-MissClassified";
            
        options[current++] = "-KTH"; options[current++] = ""+getKTHRootAttribute();
        
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
        
        String convertList = Utils.getOption("S",options);
        if (convertList.length() != 0) {
            this.setSValue(Double.parseDouble(convertList));
        } else {
            this.setSValue(1);
        }
        
        convertList = Utils.getOption("StopLevel",options);
        if (convertList.length() != 0) {
            this.setStopLevel(Integer.parseInt(convertList));
        } else {
            this.setStopLevel(-1);
        }

        convertList = Utils.getOption("SM",options);
        
        if (convertList.compareTo("Entropy") == 0) {
                this.setSplitMetric(new SelectedTag(IPTree.ENTROPY, IPTree.TAGS_SPLIT_METRIC));
        }
        
        if (convertList.compareTo("ImpreciseEntropy") == 0) {
                this.setSplitMetric(new SelectedTag(IPTree.IMPRECISE_ENTROPY, IPTree.TAGS_SPLIT_METRIC));
        }
        
        if (convertList.compareTo("GiniIndex") == 0) {
                this.setSplitMetric(new SelectedTag(IPTree.GINI_INDEX, IPTree.TAGS_SPLIT_METRIC));
        }

        if (convertList.compareTo("J48InfoGainRatio") == 0) {
                this.setSplitMetric(new SelectedTag(IPTree.J48_INFOGAINRATIO, IPTree.TAGS_SPLIT_METRIC));
        }
        
        if (Utils.getFlag("MissClassified", options))
            this.setMissclassifiedAllowed(true);
        else
            this.setMissclassifiedAllowed(false);
        
        convertList = Utils.getOption("KTH",options);
        if (convertList.length() != 0) {
            this.setKTHRootAttribute(Integer.parseInt(convertList));
        } else {
            this.setKTHRootAttribute(1);
        }
        
    }
    
   
    /**
    * Gets an enumeration describing the available options.
    *
    * @return an enumeration of all the available options.
    */
    
    public Enumeration listOptions() {

        Vector newVector = new Vector(4);

        newVector.addElement(new Option(
              "\tSpecifies the s value",
              "S", 1, "-S svalue"));
        
        newVector.addElement(new Option(
              "\tSpecifies the level where stop the building of the tree",
              "StopLevel", 1, "-StopLevel level"));

        newVector.addElement(new Option(
              "\tSplit Metric (Entropy, ImpreciseEntropy, GiniIndex, J48InfoGainRatio)\n"
              + "\t(default ImpreciseEntropy)",
              "SM", 1, "-SM <value>"));

        newVector.addElement(new Option(
              "\tSpecifies if the missclassifications are allowed",
              "MissClassified", 0, "-MissClassified"));

        newVector.addElement(new Option(
              "\tK-th InfoGain Attribute as root node\n"
              + "\t(default the first attribute)",
              "KTH", 1, "-KTH <type>"));
        
        return newVector.elements();
    }
  
  /**
   * Main method.
   *
   * @param args the options for the classifier
   */
  public static void main(String[] args) {

    try {
      System.out.println(Evaluation.evaluateModel(new IPTree(), args));
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
    

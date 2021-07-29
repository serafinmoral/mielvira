package weka.classifiers.meta;

import weka.core.Capabilities.Capability;
import weka.core.Capabilities;
import weka.classifiers.Evaluation;
import weka.core.AdditionalMeasureProducer;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class inherits from the FileteredClassifier class of weka. The only added functionality is the
 * implementation of AdditionalMeasureProducer interface.
 */
public class E_FilteredClassifier extends FilteredClassifier implements  AdditionalMeasureProducer {

    /**
    * Default constructor.
    */
    public E_FilteredClassifier() {
        super();
    }

    /**
    * Returns default capabilities of the classifier.
    *
    * @return   the capabilities of this classifier
    */
    public Capabilities getCapabilities() {
        return super.getCapabilities();
    }

    /**
    * Returns an enumeration of any additional measure names that might be
    * in the classifier
    * @return an enumeration of the measure names
    */
    public Enumeration enumerateMeasures() {
        Vector newVector = new Vector();

        if (this.m_Classifier instanceof AdditionalMeasureProducer) {
          Enumeration en = ((AdditionalMeasureProducer)this.m_Classifier).
            enumerateMeasures();
          while (en.hasMoreElements()) {
            String mname = (String)en.nextElement();
            newVector.addElement(mname);
          }
        }

        if (this.m_Filter instanceof AdditionalMeasureProducer) {
          Enumeration en = ((AdditionalMeasureProducer)this.m_Filter).
            enumerateMeasures();
          while (en.hasMoreElements()) {
            String mname = (String)en.nextElement();
            newVector.addElement(mname);
          }
        }
        return newVector.elements();
    }
    
    
    /**
    * Returns the value of the named measure
    * @param measureName the name of the measure to query for its value
    * @return the value of the named measure
    * @exception IllegalArgumentException if the named measure is not supported
    */
    public double getMeasure(String additionalMeasureName) {
        if (this.m_Classifier instanceof AdditionalMeasureProducer) {
          try{
            return ((AdditionalMeasureProducer)m_Classifier).getMeasure(additionalMeasureName);
          }catch (Exception e){
              if (this.m_Filter instanceof AdditionalMeasureProducer) {
                return ((AdditionalMeasureProducer)m_Filter).getMeasure(additionalMeasureName);
              }else {
                  throw new IllegalArgumentException("FilterClassifer: "
                                      +"Can't return value for : "+additionalMeasureName
                                      +". "+m_Classifier.getClass().getName()+" "
                                      +"is not an AdditionalMeasureProducer");
              }
          }
        }else if (this.m_Filter instanceof AdditionalMeasureProducer) {
            return ((AdditionalMeasureProducer)m_Filter).getMeasure(additionalMeasureName);
        }else {
            throw new IllegalArgumentException("FilterClassifer: "
                              +"Can't return value for : "+additionalMeasureName
                              +". "+m_Classifier.getClass().getName()+" "
                              +"is not an AdditionalMeasureProducer");
        }

    }
    
  /**
   * Main method for testing this class.
   *
   * @param argv should contain the following arguments:
   * -t training file [-T test file] [-c class index]
   */
  public static void main(String [] argv) {

    try {
      System.out.println(Evaluation.evaluateModel(new E_FilteredClassifier(),
						  argv));
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }

}

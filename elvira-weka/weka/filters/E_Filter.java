/*
 * E_Filter.java
 *
 * Created on 19 de abril de 2006, 15:05
 */

package weka.filters;


import weka.core.Utils;
import weka.core.Capabilities;



/**
 *
 * @author Andres
 */

public class E_Filter extends Filter{
    
    /**
     * Creates a new instance of E_Filter
     */
    public E_Filter() {
        super();
    }
    /**
    * Creates a new instance of a classifier given it's class name and
    * (optional) arguments to pass to it's setOptions method. If the
    * classifier implements OptionHandler and the options parameter is
    * non-null, the classifier will have it's options set.
    *
    * @param classifierName the fully qualified class name of the classifier
    * @param options an array of options suitable for passing to setOptions. May
    * be null.
    * @return the newly created classifier, ready for use.
    * @exception Exception if the classifier name is invalid, or the options
    * supplied are not acceptable to the classifier
    */
    public static Filter forName(String FilterName,
                                   String [] options) throws Exception {

    return (Filter)Utils.forName(Filter.class,
                                     FilterName,
                                     options);
    }

  /** 
   * Returns the Capabilities of this filter. Derived filters have to
   * override this method to enable capabilities.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  public Capabilities getCapabilities() {
    Capabilities 	result;

    result = new Capabilities(this);
    result.setMinimumNumberInstances(0);
    
    return result;
  }
    
}

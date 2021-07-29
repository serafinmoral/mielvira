/* NetworkPane.java */

import java.awt.ScrollPane;
import java.awt.Panel;
import java.io.File;

/** 
 * This is the work area where the user can modify and 
 * make inference with his networks
 *
 * @since 14/09/99
 */

public class NetworkPane extends ScrollPane {
    
   /**
    * Working modes 
    */
   protected static final int EDITION = 0;
   protected static final int INFERENCE = 1;
   
   private int workingMode;
   private EditorPanel editorPanel;
   private Panel inferencePanel;    // In the future the type must be InferencePanel
   private Bnet bayesNet;
   private int kindOfNetwork;
   private String networkName;
   
   private Evidence evidence;
   private boolean modifiedEvidence = false;
   private File evidenceFile;
      
   
   /**
    * Creates a NetworkPane using the bnet gives as parameter. By default the
    * working mode is Edit
    */
   
   NetworkPane (ElviraFrame f, String name) {
      super(SCROLLBARS_AS_NEEDED);
      workingMode = EDITION;
      editorPanel = new EditorPanel(f);
      // inferencePanel = new InferencePanel(); 
      add(editorPanel);
      editorPanel.setVisible(true);
      editorPanel.setBounds(0, 0, 1000, 800);
      bayesNet = new Bnet();
      networkName = name;
      editorPanel.setBayesNet(bayesNet);
   }
    
      
/* ******************* Access methods ********************** */      
      
   /**
    * Method for accesing to the bayes net of the frame.
    * 
    * @return A bnet with the structure of the net in the frame
    */
   
   public Bnet getBayesNet() {
      return bayesNet;
   }
   
   
   /** 
    * Returns the editorPanel of the NetworkPane
    */
   
   public EditorPanel getEditorPanel() {
      return editorPanel;
   }

   
   /**
    * Gets the name of the network. This name is the name of the file that 
    * contains it.
    */
    
   public String getNetworkName () {
      return networkName;
   }

   
   /**
    * Method to access evidence
    */
   
   public Evidence getEvidence () {
      return evidence;
   }
       

/* ******************** Modifiers ************************* */
      
   /**
    * Set the structure with the network that appears in the frame.
    * 
    * @param bn Bayessian Net of the frame
    */
    
    public void setBayesNet (Bnet bn) {
      bayesNet = bn;
    }

   
   
   /** 
    * Set the working mode given as parameter
    */
   
   public void setWorkingMode (int n) {
      workingMode = n;
   }
   
    
   /**
    * Set the evidence of the network
    */
    
   public void setEvidence (Evidence e) {
      evidence = e;
   }
   
   
   
   /**
    * Sets the visible panel according to the parameter given 
    */
   
   public void setVisiblePanel (int n) {
      setWorkingMode (n);
      if (n==EDITION) {
         remove (inferencePanel);
         inferencePanel.setVisible(false);
         add (editorPanel);
         editorPanel.setVisible(true);
      }         
      else {
         remove (editorPanel);
         editorPanel.setVisible(false);
         add (inferencePanel);
         inferencePanel.setVisible(true);
      }
         
   }    
  
   
   /**
    * Sets the name of the network. This name is the name of the file that 
    * contains it.
    */
    
   public void setNetworkName (String name) {
      networkName = name;
   }


}    // end of NetworkPane class
/* EditVariableDialog.java */

import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;


/**
 * This class implements the dialog window that appears when 
 * a node it's going to be edited.
 * Implements the necessary methods to handle the events in 
 * this dialog window
 */

class EditVariableDialog extends Dialog {
   
   ElviraFrame parentFrame;
   ResourceBundle bundle;                     
       
   EditorPanel activePanel;
   Bnet bayesNet;
   Node node;
   
   EditVariableDialogListener listener;

   int displayedVariablePropertyIndex, displayedFunctionPropertyIndex;    

   Panel np, nvp, kp, kkp, vpp, fpp, npp, cbp, pp, gnp, gncp, okp, qbp, qbpp,
         ncomment, ntitle, cbp2, cbp3, tp, ttp, t3p; 
   Label name, newValue, kind, type, comment, title, variableProperties, 
         functionProperties, localParameter;
   TextField textName, textNewValue, textLocalParameter, textTitle, 
            textComment; 
   CheckboxGroup kinds, types;
   Checkbox chanceKind, decisionKind, utilityKind, finiteStatesType, 
            continuousType, infiniteDiscreteType, mixedType;     
   Button distButton, returnButton;

   private final static String DIALOG_NAME = "EditVariable";
   private final static String NAME = "Name";
   private final static String NEW_VALUE = "Values";
   private final static String COMMENT = "Comment"; 
   private final static String TITLE = "Title"; 
   private final static String KIND = "Kind";
   private final static String CHANCE_KIND = "Chance_Node";
   private final static String DECISION_KIND = "Decision_Node"; 
   private final static String UTILITY_KIND = "Utility_Node";
   private final static String TYPE = "Type"; 
   private final static String CONTINUOUS_TYPE = "Continuous";
   private final static String FINITE_STATES_TYPE = "Finite_States";  
   private final static String INFINITE_DISCRETE_TYPE = "Infinite_Discrete";
   private final static String MIXED_TYPE = "Mixed";   
   private final static String EDIT_FUNCTION = "Edit_Function";
   private final static String RETURN = "Close_Window";
   private final static String EDIT = "Edit_Node";


    /**
     * Default constructor for an EditVariableDialog object.  
     */
     
   public EditVariableDialog (ElviraFrame parent, Node node) {
      super (parent, "Edit Variable Dialog", true);        
           
      Rectangle bounds = getParent().getBounds();
      Rectangle abounds = getBounds();

      setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
                     bounds.y + (bounds.height - abounds.height)/2);
                       
      setResizable(false);
           
      if (Elvira.getLanguaje() == Elvira.SPANISH) 
         bundle = ResourceBundle.getBundle ("Dialogs_sp");
      else
         bundle = ResourceBundle.getBundle ("Dialogs");
      
    	activePanel = parent.getCurrentNetworkPane().getEditorPanel();
    	bayesNet = parent.getCurrentNetworkPane().getBayesNet();
    	this.node = node;
    	setTitle (getString(DIALOG_NAME,EDIT)+" "+node.getName());

    	// Compose the frame

      // Panel for name, values and type

      // Panel for the name
      np = new Panel();
      np.setLayout(new BorderLayout());
      name = new Label(getString(DIALOG_NAME,NAME));
      textName = new TextField(30);
      np.add("West", name);
      np.add("Center", textName);

	   // Panel for the title <--- jgamez
      ntitle = new Panel();
      ntitle.setLayout(new BorderLayout());
      title = new Label(getString(DIALOG_NAME,TITLE));
      textTitle = new TextField(60);
      ntitle.add("West", title);
      ntitle.add("Center", textTitle);


	   // Panel for the comment <---- jgamez
	   ncomment = new Panel();
	   ncomment.setLayout(new BorderLayout());
	   comment = new Label(getString(DIALOG_NAME,COMMENT));
	   textComment = new TextField(80);
	   ncomment.add("West",comment);
	   ncomment.add("Center", textComment);

      // Panel for the values
      nvp = new Panel();
      nvp.setLayout(new BorderLayout());
      newValue = new Label(getString(DIALOG_NAME,NEW_VALUE));
      textNewValue = new TextField(60);
      nvp.add("West", newValue);
      nvp.add("Center", textNewValue);

      // Panel for the kind-of-node
      kp = new Panel();
      kp.setLayout(new BorderLayout());
      kind = new Label(getString(DIALOG_NAME,KIND));

      kkp = new Panel();
      kkp.setLayout(new GridLayout(1, 3));
      kinds = new CheckboxGroup();
      chanceKind = new Checkbox(getString(DIALOG_NAME,CHANCE_KIND), kinds, 
   		                       (boolean)(node.getKindOfNode()==0));                       
	   
	   decisionKind = new Checkbox(getString(DIALOG_NAME,DECISION_KIND), kinds, 
		                            (boolean)(node.getKindOfNode()==1)); 
	   utilityKind = new Checkbox(getString(DIALOG_NAME,UTILITY_KIND), kinds, 
		                           (boolean)(node.getKindOfNode()==2));
        
      kkp.add(chanceKind);
   	kkp.add(decisionKind);
	   kkp.add(utilityKind);
      	
	   // Panel for the type-of-variable
	
      tp = new Panel();
      tp.setLayout(new BorderLayout());
      type = new Label(getString(DIALOG_NAME,TYPE));

      ttp = new Panel();
      ttp.setLayout(new GridLayout(1, 4)); 
      types = new CheckboxGroup();
      finiteStatesType = new Checkbox(getString(DIALOG_NAME,FINITE_STATES_TYPE), types, 
	                                   (boolean)(node.getTypeOfVariable()==1));
	   continuousType = new Checkbox(getString(DIALOG_NAME,CONTINUOUS_TYPE), types, 
		                              (boolean)(node.getTypeOfVariable()==0)); 
	   infiniteDiscreteType = new Checkbox(getString(DIALOG_NAME,INFINITE_DISCRETE_TYPE), types,
		                                    (boolean)(node.getTypeOfVariable()==2));
	   mixedType = new Checkbox(getString(DIALOG_NAME,MIXED_TYPE), types, 
		                         (boolean)(node.getTypeOfVariable()==3)); 
	   ttp.add(continuousType);
	   ttp.add(finiteStatesType);
	   ttp.add(infiniteDiscreteType);
	   ttp.add(mixedType);

	   t3p = new Panel();
	   t3p.setLayout(new BorderLayout());
	   t3p.add("North",type);
	   t3p.add("Center",ttp);

      kp.add("North", kind);
      kp.add("Center", kkp); 
	   kp.add("South",t3p);
        
      // Finish panel for name, title, comment, values and type
	
      cbp = new Panel();
      cbp.setLayout(new BorderLayout(10,10));
      cbp.add("North", np);
      cbp.add("Center", ntitle);
      cbp.add("South",ncomment);
      cbp2 = new Panel();
      cbp2.setLayout(new BorderLayout(10,10));
      cbp2.add("North", nvp);
      cbp2.add("Center", kp);

      cbp3 = new Panel();
      cbp3.setLayout(new BorderLayout(10,10));
      cbp3.add("North",cbp);
      cbp3.add("Center",cbp2);

      // Panel for properties (variable, function and network)
      pp = new Panel();
      pp.setLayout(new BorderLayout());

      // Return buttons
     	okp = new Panel();
	   okp.setLayout(new FlowLayout(FlowLayout.CENTER));
    	distButton = new Button(getString(DIALOG_NAME,EDIT_FUNCTION));
    	okp.add(distButton);
      returnButton =	new Button(getString(DIALOG_NAME,RETURN));
      okp.add(returnButton);
      setLayout(new BorderLayout());
    	add("North", cbp3);
    	add("South", okp);
    	
    	listener = new EditVariableDialogListener(this,node);
    	distButton.addActionListener(listener);
    	returnButton.addActionListener(listener);    	
    	
	   // Pack the whole window
	   pack();

    	// Initialize values
    	fillDialog();
   }
 

    /**
     * Fill the values in the dialog area.
     */
     
   private void fillDialog() {
      
      String values[], all_values = "";
	   Vector states;
      Vector prop;
      String property;

      // Fill name
      textName.setText(node.getName());
	 
	   // Fill title <--- jgamez
	   textTitle.setText(node.getTitle());
	 
	   // Fill comment <-- jgamez
	   textComment.setText(node.getComment());	
	 
      // Fill values (states)
	
	   if (node.getTypeOfVariable()==1){ // finite-states
	      FiniteStates fs;
	      fs = (FiniteStates) node;
         states = (Vector)fs.getStates();
           
         for (int i=0; i<fs.getNumStates(); i++) {
            all_values += states.elementAt(i);
            if (i!=(fs.getNumStates()-1))
               all_values += ", ";
         }
        
         textNewValue.setText(all_values);
	   }
	   else {
	   
	   }
	
      // Set kind-of-node
      if (node.getKindOfNode()==0){ // chance node
         kinds.setSelectedCheckbox(chanceKind);
      } 
	   else 
	      if (node.getKindOfNode()==1){ // decision node
	         // kinds.setCurrent(decisionKind);
	      } 
	      else { // utility node
           // kinds.setCurrent(utilityKind);
	      }

	   // set type-of-variable 
	   if (node.getTypeOfVariable()==1){ // finite states
	      types.setSelectedCheckbox(finiteStatesType);
	   }
	   else 
	      if (node.getTypeOfVariable()==0){ // continuous
	         // types.setCurrent(continuousType);
	      }
	      else 
   	      if (node.getTypeOfVariable()==2){ // infinite discrete
	            // types.setCurrent(infiniteDiscreteType);
	         }
	         else { // mixed
	            // types.setCurrent(mixedType);
	         }
	}

  
   /** 
    * Gets from the ResourceBundle the string with the given name.
    * If it isn't found, the string name is the new menu created
    *
    * @param b Contains all the strings for the menus
    * @param name Used to find the correct string in b
    */   
   
   public String getString (String dialogName, String sourceName) {
      String dialogLabel;
      
      try {
         dialogLabel = bundle.getString (dialogName+"."+sourceName+".label");             	   
      }
      catch (MissingResourceException e) {dialogLabel = sourceName;}
      
      return dialogLabel;
   }
   
   public void printLabel (Panel p, String label) {
      int numberLines, i;
      Label l;
      
      numberLines = label.length() / 40;
      if (numberLines>1) 
         setSize(450,(100+(20*(numberLines-1))));
      for (i=1; i<=numberLines; i++) {
        if (i!=numberLines) 
           l = new Label (label.substring((i-1)*40, (i*40))); 
        else
           l = new Label (label.substring((i-1)*40, label.length()));
        p.add(l);
      }
   }

	public void getInformationFromDialog (Node n) {
	   
      String values[];
      
      if (!node.getName().equals(textName.getText()))       
	      node.setName(bayesNet.checkName(textName.getText()));
	      
      node.setTitle(textTitle.getText());
      node.setComment(textComment.getText());  
      
      values = parseValues(textNewValue.getText());
      if (values != null)
         bayesNet.changeValues((FiniteStates)node, values);
	   	   
	   if (kinds.getSelectedCheckbox() == chanceKind) {
	      node.setKindOfNode(0);
	      activePanel.repaint();
      }
	   else if (kinds.getSelectedCheckbox() == decisionKind){   		
		   ElviraHelpMessages.show(parentFrame, ElviraHelpMessages.notDecisionYet);
		   kinds.setSelectedCheckbox(chanceKind);
	   }	    
	   else {
		   ElviraHelpMessages.show(parentFrame, ElviraHelpMessages.notUtilityYet);
		   kinds.setSelectedCheckbox(chanceKind);
	   } 
	   
	   if (types.getSelectedCheckbox() == finiteStatesType) {
	      node.setTypeOfVariable(1);
	      activePanel.repaint();
      }
	   else if (types.getSelectedCheckbox() == continuousType){		
		   ElviraHelpMessages.show(parentFrame, ElviraHelpMessages.notContinuousYet);
		   types.setSelectedCheckbox(finiteStatesType);
      }	    
	   else if (types.getSelectedCheckbox() == infiniteDiscreteType){	
		   ElviraHelpMessages.show(parentFrame, ElviraHelpMessages.notInfiniteDiscreteYet);
		   types.setSelectedCheckbox(finiteStatesType);
	   }
	   else {		
		   ElviraHelpMessages.show(parentFrame, ElviraHelpMessages.notMixedYet);
		   types.setSelectedCheckbox(finiteStatesType);
      }  
   }

   
    /**
     * Parse the values stated in the values TextField.          
     */
    
   private String[] parseValues(String allValues) {
      String token = null, delimiters = " ,\n\t\r";
      StringTokenizer st = new StringTokenizer(allValues, delimiters);
      String vals[] = new String[ st.countTokens() ];
      int i = 0;

      while (st.hasMoreTokens()) {
         vals[i] = bayesNet.validateValue(st.nextToken());
         i++;
      }
    
      return(vals);
  } 

}  // end of EditVariableDialog class



class EditVariableDialogListener implements ActionListener {
   
   EditVariableDialog dialog;
   Node node;
   
   public EditVariableDialogListener (EditVariableDialog d, Node n) {
      dialog = d;
      node = n;
   }

   public void windowClosing(java.awt.event.WindowEvent event) {
      Object object = event.getSource();
		if (object == dialog) {
		   dialog.dispose();
		}
	}
   	     

   /**
    * Handle the observation events.
    * 
    * @param evt Event to handle    
    */
     
   public void actionPerformed (ActionEvent evt) {

      Object eventObject = evt.getSource();

      if (eventObject == dialog.returnButton) {
         dialog.getInformationFromDialog (node);
	      dialog.dispose();
	   }
      else if (eventObject == dialog.distButton) {
         dialog.activePanel.editFunction(node);
        }
	}

} // end of EditVariableDialogListener class
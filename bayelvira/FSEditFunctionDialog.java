/* FSEDitFunctionDialog.java */

import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

/**
 * Used only to edit Finite States Variables
 */

class FSEditFunctionDialog extends Dialog implements ActionListener{
   
   ElviraFrame parentFrame;
   ResourceBundle bundle;

   Bnet bayesNet;
   Node node;

   int indexExtremePoint;

   Panel gp, dp, pp, ap, csp, okp;
   Label parentsAnnounce, parentsLabels[];
   Label nodeStatesLabels[];
   Label firstParentNameLabel, firstParentStatesLabels[] ,credalSetLabel;
   TextField fields[][];
   Choice parentChoices[] ,credalSetChoice;
   Button returnButton;

   private final static String DIALOG_NAME = "FSEditFunction";
   private final static String PARENTS_ANNOUNCE = "Parents";
   private final static String CREDAL_SET = "Index_of_Distribution";
   private final static String RETURN = "Close_Window";
   private final static String EDIT = "Edit_Node";


   /**
    * Default constructor for an EditFunctionDialog object.
    * 
    * @param parent Parent's frame of the new FSEditFunctionDialog that is created
    * @param bnet Bnet that is editing
    * @param node Node whose function is going to be edit
    */
   
   public FSEditFunctionDialog(ElviraFrame parent, Node node) {
      
      super(parent, "",true);
      
      Rectangle bounds = getParent().getBounds();
      Rectangle abounds = getBounds();

    	setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
                    bounds.y + (bounds.height - abounds.height)/2);
                    
      setResizable(false);
        
      if (Elvira.getLanguaje() == Elvira.SPANISH) 
         bundle = ResourceBundle.getBundle ("Dialogs_sp");
      else
         bundle = ResourceBundle.getBundle ("Dialogs");
      
      bayesNet = parent.getCurrentNetworkPane().getBayesNet();
      this.node = node;
      NodeList parents = bayesNet.parents(node);            
      
      setTitle(getString(DIALOG_NAME,EDIT)+" "+ node.getName());
      	
      int parentsSize = parents.size();
      	
      if (parentsSize > 1)
     	   buildMoreOneParent ();
      else if (parentsSize == 1)
     	   buildOneParent();
      else if (parentsSize == 0)
     	   buildNoParent();

      // Set the bottom panel with the return button
      okp = new Panel();
      okp.setLayout(new FlowLayout(FlowLayout.CENTER));
      returnButton = new Button(getString(DIALOG_NAME,RETURN));
      okp.add(returnButton); 
      returnButton.addActionListener(this);
      
      addWindowListener (new FSEditFunctionDialogListener());
      
      // Set the final layout
      setLayout(new BorderLayout());
      add("Center", gp);
      add("South", okp);

      pack();
   }

   
   /**
    * Build the widget when the variable has more than one    
    * parent.
    */
   
   private void buildMoreOneParent() {
       
      // To avoid repetition, collect some information
      // about the node.
      
      NodeList nodeParents = bayesNet.parents(node);
      String nodeStates[] = ((FiniteStates)node).getStringStates();
      int parentsSize = nodeParents.size();

      // Information about a generic node that is parent of
      // the given node.
      
      Node parentNode;
      String parentNodeStates[];

      // Information about the first parent of the given node.
      
      parentNode = (Node)(nodeParents.elementAt(0));
      String firstParentName = parentNode.getName();
      String firstParentStates[] = ((FiniteStates)parentNode).getStringStates();

      // Construct the panel with the values of the parents.
      
      pp = new Panel();
      pp.setLayout(new GridLayout(parentsSize - 1, 2));
      parentsLabels = new Label[parentsSize - 1];
      parentChoices = new Choice[parentsSize - 1];

      // Fill the labels and choices for the values of the parents.
      
      for (int i = 0; i<parentsLabels.length; i++) {
         
         // Get a parent
         parentNode = (Node)(nodeParents.elementAt(i+1));
         parentsLabels[i] = new Label(parentNode.getName());
         parentChoices[i] = new Choice();
         parentNodeStates = ((FiniteStates)parentNode).getStringStates();
         
         // Fill the choice item with the values for the parent.
         for (int j=0; j<parentNodeStates.length; j++) {
               parentChoices[i].addItem(parentNodeStates[j]);
         }
         
         // Insert the label and choice
         pp.add(parentsLabels[i]);
         pp.add(parentChoices[i]);
      }

      // Just use a panel to put the title in the parent choices.
      
      ap = new Panel();
      ap.setLayout(new BorderLayout());
      parentsAnnounce = new Label(getString(DIALOG_NAME,PARENTS_ANNOUNCE));
      ap.add("North", parentsAnnounce);
      ap.add("Center", pp);

      // Now set the distribution panel
      
      buildPanelForParents (nodeStates, firstParentName, firstParentStates);

      // Finally set the panel with all choices and fields.
      
      gp = new Panel();
      gp.setLayout(new BorderLayout());
      gp.add("North", ap);
      gp.add("Center", dp);    

      // Fill the distribution
      fillDistribution();
   	
   }


   /**
    * Build the widget when the variable has one parent.
    */
   
   private void buildOneParent() {
       
      NodeList nodeParents = bayesNet.parents(node);
      String nodeStates[] = ((FiniteStates)node).getStringStates();

      // Information about the first parent of the given node.
      
      Node firstParentNode = (Node)(nodeParents.elementAt(0));
      String firstParentName = firstParentNode.getName();
      String firstParentStates[] = ((FiniteStates)firstParentNode).getStringStates();

      // For a node one parent, do not
      // set the parent panel; go directly to the distribution panel.
      
      buildPanelForParents (nodeStates, firstParentName, firstParentStates);

      // Finally set the panel with all choices and fields.
      
      gp = new Panel();
      gp.setLayout(new BorderLayout());
      gp.add("Center", dp);

      // Fill the distribution
      fillDistribution();
   	
   }


   /**
    * Build the widget when the variable has no parents.
    */
   
   private void buildNoParent() {
      Vector V;
      int j;
      String nodeStates[];
   	
      // Get the states of the node as an array of strings
      nodeStates = ((FiniteStates)node).getStringStates();
   	
      // For a node with no parent, do not
      // set the parent panel; go directly to the distribution panel.
      
      dp = new Panel();
      dp.setLayout(new GridLayout(nodeStates.length, 2));

      nodeStatesLabels = new Label[nodeStates.length];
      fields = new TextField[nodeStates.length][1];

      for (j=0; j<nodeStates.length; j++) {
         nodeStatesLabels[j] = new Label(nodeStates[j]);
         dp.add(nodeStatesLabels[j]);
         fields[j][0] = new TextField();
         dp.add(fields[j][0]);
      }

      // Set the panel with all choices and fields.
      
      gp = new Panel();
      gp.setLayout(new BorderLayout());
      gp.add("Center", dp);

      generateCredalSetPanel();

      // Fill the distribution
      fillDistribution();
   }


   /** 
   * Build the distribution panel for one or more parents.                           
   */
   
   private void buildPanelForParents (String nodeStates[], 
                  String firstParentName, String firstParentStates[]) {
      int i, j;
      NodeList parents = bayesNet.parents(node);

      dp = new Panel();
      dp.setLayout(new GridLayout(nodeStates.length + 1,
                                 firstParentStates.length + 1));
                                 
      nodeStatesLabels = new Label[nodeStates.length];
      firstParentStatesLabels = new Label[firstParentStates.length];
      fields = new TextField[nodeStates.length][firstParentStates.length];

      firstParentNameLabel = new Label(firstParentName);
      dp.add(firstParentNameLabel);
      
      for (i=0; i<firstParentStates.length; i++) {
         firstParentStatesLabels[i] = new Label(firstParentStates[i]);
         dp.add(firstParentStatesLabels[i]);
      }

      for (j=0; j<nodeStates.length; j++) {
         nodeStatesLabels[j] = new Label(nodeStates[j]);
         dp.add(nodeStatesLabels[j]);

         for (i=0; i<firstParentStates.length; i++) {
               fields[j][i] = new TextField();
               dp.add(fields[j][i]);
         }
      }
   	
   }


   /**
    * Fill all the TextField objects with the values of the  
    * correct distribution.
    */
   
   private void fillDistribution() {
      
      NodeList parents = bayesNet.parents(node);
      int parentsSize = parents.size();

      if (parentsSize > 1)
     	   distributionMoreParents();
      else if (parentsSize == 1)
     	   distributionOneParent();
      else if (parentsSize == 0)
     	   distributionNoParent();
   }


   /**
    * Generate a panel for credal sets, if necessary.
    */
   
   private void generateCredalSetPanel() {
      
   /*    if (node.is_credal_set()) {
         csp = new Panel();
         csp.setLayout(new BorderLayout());
         credalSetLabel = new Label(getString(CREDAL_SET));
         credalSetChoice = new Choice();
         for (int i=0; i<node.number_extreme_distributions(); i++)
               credalSetChoice.addItem( String.valueOf(i) );
         csp.add("West", credalSetLabel);
         csp.add("Center", credalSetChoice);

         gp.add("South", csp);
      }
   */
   }


   /**
    * Fill all the TextField when the variable has more than 
    * one parent.
    */
   
   private void distributionMoreParents() {
      
      int i, j, k;
      NodeList parents=bayesNet.parents(node);
      
      String nodeName = node.getName();
      String nodeStates[] = ((FiniteStates)node).getStringStates();
      String variableStatePairs[][] = new String[parents.size() + 1][2];
      
      Relation R = bayesNet.getRelation(node);
      Configuration C = new Configuration(R.getVariables());
      int index,pos;
      FiniteStates pnode;
      
      for (i=0; i<parentChoices.length; i++) {
	      variableStatePairs[i][0] = parentsLabels[i].getText();
	      variableStatePairs[i][1] = parentChoices[i].getSelectedItem();
	      
	      // Get the configuration for parents except the first one
	      // because this first one is missing
	      pnode = (FiniteStates) parents.elementAt(i+1); 
	      pos = pnode.getStates().indexOf(parentChoices[i].getSelectedItem());
	      
	      if (pos==-1) 
	         System.out.println("No encuentro ese estado");
	         
	      C.values.setElementAt(new Integer(pos), i+2);
      }

      variableStatePairs[i][0] = firstParentNameLabel.getText();
      variableStatePairs[i+1][0] = nodeName;

      for (j=0; j<fields.length; j++) {
       	for (k=0; k<fields[j].length; k++) {
            variableStatePairs[i][1] = firstParentStatesLabels[k].getText();
            variableStatePairs[i+1][1] = nodeStates[j];
               
            // Obtain the right configuration
            C.values.setElementAt(new Integer(j),0); // 0=node
            C.values.setElementAt(new Integer(k),1); // 1=parent
            
            // Get its y position and write it
            index = C.getIndexInTable();
            fields[j][k].setText(String.valueOf(((PotentialTable)R.getValues()).getValue(index)));                                     
   						
         }
      }
   	
   }


   /**
    * Fill all the TextField when the variable has one       
    * parent.
    */
   
   private void distributionOneParent() {
      
      int j, k;
      String nodeName = node.getName();
      String nodeStates[] = ((FiniteStates)node).getStringStates();
      String variableStatePairs[][] = new String[2][2];
      
      Relation R=bayesNet.getRelation(node);
      Configuration C = new Configuration(R.getVariables());
      int index;      

      variableStatePairs[0][0] = firstParentNameLabel.getText();
      variableStatePairs[1][0] = nodeName;

      for (j=0; j<fields.length; j++) {
       	for (k=0; k<fields[j].length; k++) {
            variableStatePairs[0][1] = 
			   firstParentStatesLabels[k].getText();
            variableStatePairs[1][1] = nodeStates[j];
   		
            // Obtain the right configuration
            C.values.setElementAt(new Integer(j),0); // 0=node
            C.values.setElementAt(new Integer(k),1); // 1=parent
            
            // Get its y position and write it
            index = C.getIndexInTable();
            fields[j][k].setText(String.valueOf(((PotentialTable)R.getValues()).getValue(index)));
	      }
      }
   	
   }


   /**
    * Fill all the TextField when the variable has no        
    * parent.
    */
   
   private void distributionNoParent() {
      
      int j;
      String nodeName = node.getName();
      String nodeStates[];
      Relation R;
      String variable_value_pairs[][] = new String[1][2];

      // Get the states as a string
      nodeStates=((FiniteStates)node).getStringStates();
      
      // Get the relation asociate to the node
      R = (Relation) bayesNet.getRelation(node);
      
      for(j=0; j<fields.length; j++) 
         fields[j][0].setText( String.valueOf(((PotentialTable)R.getValues()).getValue(j)) );
   	
   }


   /**
    * Handle the events.
    * 
    * @param evt Event produced
    */

   public void actionPerformed (ActionEvent evt) {
      
      int i, j, k;
      double valueSet;
      String variableStatePairs[][];
      String nodeName = node.getName();
      String nodeStates[];
      
      NodeList parents=bayesNet.parents(node);
      int parentsSize = parents.size();
      Relation R = bayesNet.getRelation(node);
      Configuration C = new Configuration(R.getVariables());
      FiniteStates pnode;
      int pos;
      
      Object eventObject = evt.getSource();

      // Get the states of the node as an string[]
      nodeStates=((FiniteStates)node).getStringStates();

      // Check whether to dismiss
      if (eventObject == returnButton) {
	      dispose();	      
	   }

      // In case the node has more than one parent, check
      // whether parent values have been changed.
      if (parentsSize > 1) {
         for (i=0; i<parentChoices.length; i++) {
	         if (eventObject == parentChoices[i]) {
	               fillDistribution();
    	      }
         }
      }

      // Check whether the distribution has been changed      
      for (i=0; i<fields.length; i++) {
	      for (j=0; j<fields[i].length; j++) {
	         
	         if (eventObject == fields[i][j]) {
	            valueSet = (new Double( fields[i][j].getText() ).doubleValue() );
              
		         if (parentsSize > 1) {
		            
		            // Construct parent's configuration except for the first
		            for(k=0; k<parentChoices.length;k++){		            
		               pnode = (FiniteStates) parents.elementAt(k+1); // pq el primero no esta
		               pos = pnode.getStates().indexOf(parentChoices[k].getSelectedItem());
		               if (pos==-1) System.out.println("No encuentro ese estado");
		               C.values.setElementAt(new Integer(pos), k+2);
		            }
		            
		            // Now for the node and the first parent
		            C.values.setElementAt(new Integer(i),0); //0=node 
		            C.values.setElementAt(new Integer(j),1); //1=parent
		            
		            // Set value
		            ((PotentialTable)R.getValues()).setValue(C,valueSet);                    	
               }
               else 
                  if (parentsSize == 1) {
	                  C.values.setElementAt(new Integer(i),0); //0=node 
	                  C.values.setElementAt(new Integer(j),1); //1=parent
	                  ((PotentialTable)R.getValues()).setValue(C,valueSet);
                  }
		            else 
		               if (parentsSize == 0) { 
	                     ((PotentialTable)R.getValues()).setValue(i,valueSet);
                     }
            
               
            } // if (eventObject == fields[i][j]) 
            
         } // for (j=0; j<fields[i].length; j++) 
         
      } // for (i=0; i<fields.length; i++)             
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
  
   
  class FSEditFunctionDialogListener extends WindowAdapter {
	
      public void windowClosing(java.awt.event.WindowEvent event) {
		   Object object = event.getSource();
		   if (object == FSEditFunctionDialog.this) {
		      dispose();
		   }
	   }
   	     
   }  // end of QuitEditorDialogListener class  

   
   
} // end of FSEditFunctionDialog class


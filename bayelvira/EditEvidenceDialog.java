import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import EvidenceParse;

class EditEvidenceDialog extends Dialog implements ActionListener, ItemListener {

  /**
   * Contains a Bayessian Net
   */
  Bnet bn;

  /**
   * Parent frame of this dialog
   */
  private ElviraFrame frame;

  /**
   * Title of the window
   */
  private Label title;

  /**
   * Button for close the window
   */
  private Button close;

  /**
   * Button for edit the evidence
   */
  private Button editCaseProperties;

  /**
   * Contain a set with the variables of the node
   */
  private List variableList;

  /**
   * Evidence that appears in the dialog when it's open
   */
  Evidence evidence;


  /**
   * Default constructor
   * 
   * @param elvcframe Contains the parent's frame of the EditEvidenceDialog created
   * @param evidence Contains the evidence that is going to be consult / edit
   * @param bn Bnet whose evidence is going to edit in the dialog created
   */

  public EditEvidenceDialog(ElviraFrame frame, Evidence evidence, Bnet bn) 
  {

    super(frame,true);
    this.bn = bn;
    this.evidence = evidence;
    this.frame = frame;    

    setLayout(new BorderLayout());

    // set the title
    
    title = new Label("Evidence Window",Label.CENTER);
    add("North",title);

    
    // creates the evidence for the first time
    if (evidence==null) evidence=new Evidence();  
    
    // check if the evidence is coherent with the network in networkPanel     
    if (!evidence.coherentEvidence(bn)) evidence=new Evidence(); 

    // set the evidence list

    int i;
    i = bn.getNodeList().size();
    if (i>10) i=10;
    variableList = new List(i,false);

    Enumeration e;
    Node n;
    FiniteStates fs;
    String nameItem;
    
    for (e=bn.getNodeList().elements();e.hasMoreElements();){
      n = (Node) e.nextElement();
      
      // check if the node is observed and set de right label
      nameItem = n.getName();
      fs = (FiniteStates) n; 
      i = evidence.indexOf(n);
      
      if (i!=-1) {
         nameItem += " (" + fs.getState(evidence.getValue(i)) + ")";
      }

      variableList.addItem(nameItem);
    }
    
    add("Center",variableList);
    variableList.addItemListener(this);

    // set the buttons panel

    Panel botones = new Panel();
    botones.setLayout(new FlowLayout(FlowLayout.CENTER));

    editCaseProperties = new Button("Edit Properties");
    editCaseProperties.addActionListener(this);
    close = new Button("Close");
    close.addActionListener(this);    

    botones.add(editCaseProperties);
    botones.add(close);
    add("South",botones);        
         
  }


  /**
   * Handle events
   * 
   * @param evt Contains the event produced
   */  

  public void actionPerformed(ActionEvent evt){

    Object source = evt.getSource();

    if (source== close){               
       dispose();
    }
    else if (source == editCaseProperties){
      System.out.println("\nVoy a editar las propiedades del caso");
      Dialog d = new EditCasePropertiesDialog(frame,evidence);
      d.setSize(400,330);
      d.setVisible(true);
    }
  }


  public void itemStateChanged(ItemEvent evt){
    int lindex;

    Object source = evt.getSource();

    if (source instanceof List){
      List list = (List)(evt.getItemSelectable());
      Node node;
      int i;
      String itemName;
      
      switch (evt.getStateChange()) {
         
        case ItemEvent.SELECTED:    // Event.LIST_SELECT:
          lindex = ((Integer)(evt.getItem())).intValue();
          System.out.println("Se ha seleccionado el nodo" + lindex);
          
          // GetObservedValue((Node)bn.node_list.elementAt(lindex));
          node = (Node) bn.getNodeList().elementAt(lindex);
          Dialog d = new ObservedNodeDialog(frame,node,evidence);
          d.setSize(150,150);
          d.setVisible(true);
          
          // Actualize the itemLabel with the new evidence
          i = evidence.indexOf(node);
          if (i!=-1){  // if observed
	         itemName = node.getName() + " (" + 
                       ((FiniteStates) node).getState(evidence.getValue(i))+ ")";
            variableList.replaceItem(itemName,lindex);
          }
          else variableList.replaceItem(node.getName(),lindex);
          
          break;
      }
    }

  }

} // end of class 
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class ObservedNodeDialog extends Dialog implements ActionListener, ItemListener {
    
    Choice choice;   
    
    /**
     * Button of the window
     */
    Button ok;

    /**
     * Title of the dialog window
     */
    Label title;

    /**
     * Contains the node whose information appers when the dialog
     * window is open.
     */
    Node node;

    /**
     * Evidence of the node
     */
    Evidence evidence;


  /**
   * Default Constructor
   * 
   */

  public ObservedNodeDialog(ElviraFrame f, Node node, Evidence evidence){

    super(f,true);
    this.node = node;
    this.evidence = evidence;

    setLayout(new BorderLayout());

    // set the title
    
    title = new Label("Variable: "+node.getName(),Label.CENTER);
    add("North",title);

    // set the choice menu
    
    int i;
    FiniteStates fs;
    String[] states;

    choice = new Choice();
    choice.addItem("Not Observed");
    fs = (FiniteStates) node;
    states = fs.getStringStates();
    for (i=0;i<fs.getNumStates();i++) choice.addItem(states[i]);

    // if there is evidence mark the state observed

    i = evidence.indexOf((Node)fs);
    if (i!=-1) choice.select(evidence.getValue(i)+1);  // +1 because notObserved is 0
    else choice.select(0);
    
    choice.addItemListener(this);
    
    // OK Button 
    
    ok = new Button("OK");
    ok.addActionListener(this);
    
    add("Center",choice);
    add("South",ok);
         
  }


 /* For handle events */

  /* public boolean handleEvent (Event evt){
    int selection,i;
    boolean observed;
    FiniteStates fs;
    

    if (evt.target == ok){
       System.out.println("DISPOSE");
       dispose();
    }
    else if (evt.target instanceof Choice){
      fs = (FiniteStates) node;
      i = evidence.indexOf(node);
      if (i!=-1) observed=true;
      else observed=false;
      
      selection = choice.getSelectedIndex();
      if (observed) {
        evidence.remove(i);
      }
      if (selection != 0) evidence.insert(fs,selection-1);                 
      

      System.out.println("El estado seleccionado es " +
                              choice.getSelectedItem());
    }

    return true;
  
  }*/

  /**
   * Called when a action is produced
   * 
   * @param evt Event produced
   */

  public void actionPerformed(ActionEvent evt) {
    
    Object source = evt.getSource();

    if (source == ok){
       System.out.println("DISPOSE");
       dispose();
    }
  }
  
  
  /**
   * This method is called when the state of a node is changed
   * 
   * @param evt Event produced
   */

  public void itemStateChanged(ItemEvent evt){
    int selection,i;
    boolean observed;
    FiniteStates fs;

    Object source = evt.getSource();

    if (source instanceof Choice){
      fs = (FiniteStates) node;
      i = evidence.indexOf(node);
      if (i!=-1) observed=true;
      else observed=false;
      
      selection = choice.getSelectedIndex();
      if (observed) {
        evidence.remove(i);
      }
      if (selection != 0) evidence.insert(fs,selection-1);                 
      

      System.out.println("El estado seleccionado es " +
                              choice.getSelectedItem());
    }


  }

} // end of class 
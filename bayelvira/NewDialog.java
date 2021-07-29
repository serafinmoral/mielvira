/* NewDialog.java */

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class NewDialog extends Dialog implements ActionListener {
    
    ElviraFrame parentFrame;
    ResourceBundle bundle;

    Button okButton;
    Button cancelButton;        
    
    boolean cancel;
    
    private final static String TITLE = "Title";
    private final static String DIALOG_NAME = "NewDialog";
    private final static String KIND_MESSAGE = "Kind";
    private final static String OK = "Ok";
    private final static String CANCEL = "Cancel";
    private final static String BNET = "Bayesian_Network";
    private final static String INFLUENCE = "Influence_Diagram";
    private final static String MARKOV = "Markov_Network";
    private final static String UNTITLED = "Untitled";
    
   public NewDialog (ElviraFrame parent) {
      
       	   super(parent, "Elvira", true);
	   Panel p1,p2;	 
	   CheckboxGroup options;
	   Label l;
	   Checkbox check;
	   
      	   Rectangle bounds = getParent().getBounds();
      	   Rectangle abounds = getBounds();

    	   setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
                       bounds.y + (bounds.height - abounds.height)/2);
                    
      	   setResizable(false);
       
           if (Elvira.getLanguaje() == Elvira.SPANISH) 
               bundle = ResourceBundle.getBundle ("Dialogs_sp");
      	   else
               bundle = ResourceBundle.getBundle ("Dialogs");
	   
	   cancel=false;
	   parentFrame = parent;
	
	   p1=new Panel();
	   p2=new Panel();
	   p1.setLayout (null);
	   p2.setLayout(new FlowLayout());		  
	   	   
	   setLayout(new BorderLayout());
	   addNotify();
	   	   
	   l = new Label (getString(DIALOG_NAME,KIND_MESSAGE));
	   p1.add (l);
	   l.setBounds(100, 20, 200, 20);
	   
	   options = new CheckboxGroup();
	   check = new Checkbox(getString (DIALOG_NAME, BNET),options,true);
	   p1.add (check);
	   check.setBounds(100, 40, 200, 20);
	   check = new Checkbox(getString (DIALOG_NAME, INFLUENCE),options,false);
	   p1.add (check);
	   check.setBounds(100, 65, 200, 20);
	   check = new Checkbox(getString (DIALOG_NAME, MARKOV),options,false);
	   p1.add (check);
	   check.setBounds(100, 90, 200, 20);
	   
	   okButton = new Button(getString (DIALOG_NAME,OK));
	   p2.add(okButton);	   
	   cancelButton= new Button(getString (DIALOG_NAME,CANCEL));
	   p2.add(cancelButton);
	   add("Center",p1);
	   add("South",p2);
	   setSize(450,200);	  	      
	   
	   okButton.addActionListener(this);	   
	   cancelButton.addActionListener(this);
	   
	   addWindowListener (new NewDialogListener());
	
   }
   
   private void okButtonClicked() {

      String counter = new String();
      String name = new String();
      
      counter = Integer.toString(ElviraFrame.untitledNetworkCounter);
      name = getString (DIALOG_NAME, UNTITLED)+counter;
      parentFrame.setNewNetworkPane(name);
      ElviraFrame.untitledNetworkCounter++;
      if (parentFrame.numberOfPanes()==1)
         parentFrame.thereIsPanes();
      
      setVisible (false);
   }

   private void cancelButtonClicked() {
      cancel = true;
      setVisible(false);
   }   
   
   public boolean isCancel() {
      return cancel;
   }
   
    
   public void actionPerformed(ActionEvent e) {
      if (e.getSource()==okButton)
	      okButtonClicked();
      else if (e.getSource()==cancelButton) {
	      cancelButtonClicked();
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

 
    class NewDialogListener extends WindowAdapter {
	
      public void windowClosing(java.awt.event.WindowEvent event) {
		   Object object = event.getSource();
		   if (object == NewDialog.this) {
		      cancelButtonClicked();		      
		   }
	   }
   	     
   }  // end of NewDialogListener class  

}

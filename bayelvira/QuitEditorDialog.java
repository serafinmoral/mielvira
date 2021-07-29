/* QuitEditorDialog.java */

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import java.awt.event.WindowAdapter;
import java.util.MissingResourceException;

/** 
 * This class is used to generate the dialog that appears
 * when the user exits Elvira and there is changes that 
 * have not been saved 
 *
 * since 14/09/99
 */

public class QuitEditorDialog extends Dialog implements ActionListener {
    
    ElviraFrame parentFrame;
    ResourceBundle bundle;
       
    /** 
     * Used for known if the cancel button of the dialog had been clicked 
     */
    private boolean cancel;
    private int position;
   
    Button saveButton;
    Button discardButton;
    Button cancelButton;  
    
    private final static String DIALOG_NAME = "QuitEditor";
    private final static String MESSAGE1 = "FirstMessage";
    private final static String MESSAGE2 = "SecondMessage";
    private final static String YES = "Yes";
    private final static String NO = "No";
    private final static String CANCEL = "Cancel";

   
   /**
    * Inicialices all the variables of the frame and create all 
    * its components.
    * 
    * @param parent parent component of the dialog that will be created
    * @param name Name of the NetworkPane related with the dialgo that is
    * going to be created. This name will appear in the dialog.
    * @param pos Position of the NetworkPane related with the dialog that 
    *            is going to be created
    */

   public QuitEditorDialog (ElviraFrame parent, String name, int pos) {
      
	   super(parent, "Elvira", true);
	   Panel p1,p2;	   

      Rectangle bounds = getParent().getBounds();
      Rectangle abounds = getBounds();

    	setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
                    bounds.y + (bounds.height - abounds.height)/2);
                    
      setResizable(false);
        
      if (Elvira.getLanguaje() == Elvira.SPANISH) 
         bundle = ResourceBundle.getBundle ("Dialogs_sp");
      else
         bundle = ResourceBundle.getBundle ("Dialogs");
	
	   p1=new Panel();
	   p2=new Panel();
	   p2.setLayout(new FlowLayout());		  
	   
	   parentFrame = parent;	   
	   cancel = false;
	   position = pos;
	   
	   setLayout(new BorderLayout());
	   addNotify();
	   setSize(450,100);

	   printLabel (p1, getString(DIALOG_NAME,MESSAGE1)+" "+name+
	                      " "+getString(DIALOG_NAME,MESSAGE2) );
	   saveButton = new Button(getString (DIALOG_NAME,YES));

	   p2.add(saveButton);
	   discardButton = new Button(getString (DIALOG_NAME,NO));

	   p2.add(discardButton);
	   cancelButton= new Button(getString (DIALOG_NAME,CANCEL));
	   p2.add(cancelButton);
	   add("South",p2);
	   add("Center",p1);	   	  	      
	   
	   saveButton.addActionListener(this);
	   discardButton.addActionListener(this);
	   cancelButton.addActionListener(this);
	   
	   addWindowListener (new QuitEditorDialogListener());
	
   }


    private void saveButtonClicked () {     
      
      setVisible(false);
	   parentFrame.mainMenuBar.saveNetwork(parentFrame,bundle,position);	   
	   
	   if (parentFrame.getCurrentNetworkPane().getEditorPanel().isModifiedNetwork())
	     cancel = true;	      
	   
	   this.setVisible(false);
	      
    }
    
   private void discardButtonClicked() {      
	   setVisible(false);      	   	   
   }

   private void cancelButtonClicked() {
      cancel = true;
      setVisible(false);
   }   
   
   public boolean isCancel() {
      return cancel;
   }
   
    
   public void actionPerformed(ActionEvent e) {
      if (e.getSource()==saveButton)
	      saveButtonClicked();
      else if (e.getSource()==discardButton){
	      discardButtonClicked();
      }
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
   
   
   /**
    * Print the label in a panel. If the label needs more
    * than one line, this method divide the label in others
    */

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


   /** 
    * This class is used to manipulate the events in 
    * the dialog 
    */
    
    class QuitEditorDialogListener extends WindowAdapter {
	
      public void windowClosing(java.awt.event.WindowEvent event) {
		   Object object = event.getSource();
		   if (object == QuitEditorDialog.this) {
		      cancelButtonClicked();		      
		   }
	   }
   	     
   }  // end of QuitEditorDialogListener class  
   
} // end of QuitEditorDialog class

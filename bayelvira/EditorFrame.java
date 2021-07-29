
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EditorFrame extends Frame {

   /**
    * Used to call some Elvira methods
    */
   Elvira elvira;

   /**
    * Is the principal panel. Contains all the objects of
    * the window.
    */
   Panel cmdPanel;

   /**
    * Is the panel where the network appears and where it can
    * be modified
    */
   NetworkPanel netPanel;

   /**
    * Used to put the netPanel panel. That's why scroll bars appears
    * when the network doesn't fit in this panel.
    */
   ScrollPane scrollpane;

   /**
    * Flag to know if the file loaded has any modification
    */
   private boolean modified=false;

   /**
    * Contain the main menu
    */
   java.awt.MenuBar mainMenuBar;
   java.awt.FileDialog openFileDialog;
   java.awt.FileDialog saveFileDialog;

   /**
    * Contains the name of the file opened. This name will be used to
    * save any modification
    */
   private String currentSaveFilename;


   // constants for caption text of all buttons
    
   public static final String createLabel  = "Create";
   public static final String moveLabel    = "Move";
   public static final String deleteLabel  = "Delete";
   public static final String editVariableLabel = "Edit Variable";
   public static final String editFunctionLabel = "Edit Function";
   public static final String editNetworkLabel = "Edit Network";


    /**
     * Default constructor for an EditorFrame object.
     * 
     * @param elv Is the elvira object that will be contained in the EditorFrame created
     */
    
   public EditorFrame(Elvira elv) {
	   super("Editor");
	  
	   if (!(elv.isAnApplet()))
	      setIconImage(Toolkit.getDefaultToolkit().getImage("icon_editor.gif"));
	      
      elvira = elv;
	   netPanel = new NetworkPanel(this);
	   scrollpane = new ScrollPane();
	   
		cmdPanel = new Panel();
	   cmdPanel.setLayout(new GridLayout(1,6));
	   mainMenuBar = new java.awt.MenuBar();    
 
      openFileDialog = new java.awt.FileDialog(this, "Open",FileDialog.LOAD);
      saveFileDialog = new java.awt.FileDialog(this, "Save",FileDialog.SAVE);
    
      ListenerButtonsEditor listenerButtonsEditor=new ListenerButtonsEditor(this);
      Button button;
      button = new Button(createLabel);
      button.addActionListener(listenerButtonsEditor);
      cmdPanel.add(button);
      button = new Button(moveLabel);
      button.addActionListener(listenerButtonsEditor);
      cmdPanel.add(button);
      button = new Button(deleteLabel);
      button.addActionListener(listenerButtonsEditor);
      cmdPanel.add(button);

      button = new Button(editVariableLabel);
      button.addActionListener(listenerButtonsEditor);
      cmdPanel.add(button);
      button = new Button(editFunctionLabel);
      button.addActionListener(listenerButtonsEditor);
      cmdPanel.add(button);
      button = new Button(editNetworkLabel);
      button.addActionListener(listenerButtonsEditor);
      cmdPanel.add(button);

      setLayout(new BorderLayout(0, 0));
      scrollpane.add(netPanel);
      add("North", cmdPanel);
      add("Center",scrollpane);      
   }


   /**
    * This method is called when the option Open... from the Network menu is
    * clicked. When it is called, open a dialog where a file is selected, and
    * the network is load in the variable elvira of this class
    * 
    * @see Elvira#open
    */
    
   public void openAction() {
      
      if (elvira.isAnApplet()) 
         return;
         
	   openFileDialog.show();
    	String filename = openFileDialog.getFile();
    	
    	if (filename == null) return;
         filename = openFileDialog.getDirectory() + filename;

      if (elvira.open(filename) == true) {	    
	      setTitle(filename);
         elvira.appendText("\tFile loaded.\n\n");
	      }
      else	    
         elvira.appendText("\tFile not loaded correctly.\n\n");
   }

   /**
    * Used to open a network of a URL direction
    */
    
    
   public void openURLAction() {
      (new OpenURLDialog(this, elvira, "Insert URL of network", true)).setVisible(true);
    }


   /**
    * This method saves the network recorded in the variable
    * elvira of this class. As name of file uses the string contained 
    * in CurrentFile. If there's no string on it calls at saveAsAction.
    * If the network is successful saved, set the value of modified to
    * False.
    * 
    * @see Elvira#save
    */
    
    
   public void saveAction() {
      
	   if (elvira.isAnApplet()) 
	      return;
	
      if (currentSaveFilename == null)
         saveAsAction();
      else {
         elvira.save(currentSaveFilename);
         elvira.appendText("\tFile saved.\n\n");
	      modified=false;
      }
      
   }


   /**
    * Used when the saveFileDialog must be open for introduce
    * the name of the file to save
    */
    
   public void saveAsAction() {
      
	   if (elvira.isAnApplet()) 
	      return;
	      
	   saveFileDialog.show();
    	String filename = saveFileDialog.getFile();
    	
    	if (filename == null) return;
         filename = saveFileDialog.getDirectory() + filename;
         
         if (elvira.save(filename) == true) {
            elvira.appendText("\tFile saved.\n\n");
	         modified=false;
	         setTitle(filename);
	      }
         else
            elvira.appendText("\tFile not saved correctly.\n\n");
            
         currentSaveFilename = filename;
      }

   /**
    * Use to clear the editor frame
    */
    
   public void clearAction() {
	   (new ClearDialog(this, this,
		   "Clear the Bayesian network?", true)).setVisible(true);
   }


   /**
    * Use to hide the Editor Frame
    */   
    
   public void hideAction() {
       this.setVisible(false);
   }


   /**
    * Clear the network screen.
    */
    
   public void clear() {
      setTitle("Editor");
      setCursor(Cursor.getPredefinedCursor(WAIT_CURSOR));
      netPanel.clear();
      modified=false;
      setCursor(Cursor.getPredefinedCursor(DEFAULT_CURSOR));
   }

/** ******************************************************** *
 * ******************************************************** */   
/*    public boolean action(Event event, Object arg) {
    if (event.target instanceof MenuItem) {
	String label = (String) ( ((MenuItem)event.target).getLabel());

	if (label.equalsIgnoreCase("Clear")) {
	    Clear_Action();
	    return true;
	} else
	if (label.equalsIgnoreCase("Save")) {
	    Save_Action();
	    return true;
	} else
	if (label.equalsIgnoreCase("Save As...")) {
	    SaveAs_Action();
	    return true;
	} else
	if (label.equalsIgnoreCase("Open...")) {
	    Open_Action();
	    return true;
	} else
	if (label.equalsIgnoreCase("Open URL...")) {
		Open_URL_Action();
		return true;
	} else 
	if (label.equalsIgnoreCase("Hide")) {
	    Hide_Action();
	    return true;
	}
    }else
    if (event.target instanceof Button) {
	String label =  ((Button)(event.target)).getLabel();
	if ( ((String)arg).equals(createLabel)) {
	    netPanel.set_mode(label);
	    elviraHelpMessages.show(ElviraHelpMessages.create_message);
	    setCursor(Cursor.getPredefinedCursor(DEFAULT_CURSOR));
	}
	else if ( ((String)arg).equals(moveLabel)) {
	    netPanel.set_mode(label);
	    ElviraHelpMessages.show(ElviraHelpMessages.move_message);
	    setCursor(Cursor.getPredefinedCursor(MOVE_CURSOR));
	}
	else if ( ((String)arg).equals(deleteLabel)) {
	    netPanel.set_mode(label);
	    ElviraHelpMessages.show(ElviraHelpMessages.delete_message);
	    setCursor(Cursor.getPredefinedCursor(HAND_CURSOR));
	}
	else if ( ((String)arg).equals(editVariableLabel)) {
	    set_edit_variable_mode();
	}
	else if ( ((String)arg).equals(editFunctionLabel)) {
	    set_edit_function_mode();
	}
	else if ( ((String)arg).equals(editNetworkLabel)) {
	    set_edit_network_mode();
	}
    }
    return super.action(event, arg);
    }*/
     
        
    /**
     * Load an InferenceGraph object into the NetworkPanel.
     * 
     * @param bn Contains the network to load
     * @see NetworkPanel#load
     */
     
   public void load(Bnet bn) {
        netPanel.load(bn);
   }


   /**
    * Interact with menu options: edit variable.
    */
    
   public void setEditVariableMode() {
     setCursor(Cursor.getPredefinedCursor(TEXT_CURSOR));
     netPanel.setMode(editVariableLabel);
     ElviraHelpMessages.show(ElviraHelpMessages.edit_message);
   }


   /**
    * Interact with menu options: edit function.
    */
    
   public void setEditFunctionMode() {
     setCursor(Cursor.getPredefinedCursor(TEXT_CURSOR));
     netPanel.setMode(editFunctionLabel);
     ElviraHelpMessages.show(ElviraHelpMessages.edit_message);
   }


  /**
   * Interact with menu options: edit network.
   */
   
   public void setEditNetworkMode() {
     netPanel.editNetwork();
   }
   
   
   /**
    * Get the Bnet object in the NetworkPanel.
    * 
    * @return The bnet contained in the NetworkPanel
    */
    
   public Bnet getBnet() {
     return(netPanel.getBnet());
   }
   
   
   /**
    * Used to see if the Elvira object has been modified.
    * 
    * @return True if the Elvira object has been modified
    * False in other case
    */
   boolean isModified() {
     return(modified);
   }
   
   
    /**
     * Set the modified variable.
     * 
     * @param mod Contain the value to set
     */
     
    public void setModified (boolean mod) {
	   modified=mod;
    }
    
}  // end of class




/*class ListenerMenuNetwork implements ActionListener {
    EditorFrame f;
    public ListenerMenuNetwork(EditorFrame frame){
       f=frame;
    }
    public void actionPerformed(ActionEvent event) {
	//String label = (String) ( ((MenuItem)event.getSource()).getActionCommand());
	String label=event.getActionCommand();
	//System.out.println("La etiqueta es
	if (label.equalsIgnoreCase("Clear")) {
	    f.Clear_Action();
	} else
	if (label.equalsIgnoreCase("Save")) {
	    f.Save_Action();
	} else
	if (label.equalsIgnoreCase("Save As...")) {
	    f.SaveAs_Action();
	} else
	if (label.equalsIgnoreCase("Open...")) {
	    f.Open_Action();
	} else
	if (label.equalsIgnoreCase("Open URL...")) {
	    f.Open_URL_Action();
	} else 
	if (label.equalsIgnoreCase("Hide")) {
	    f.Hide_Action();
	}
    }
}*/

class ListenerButtonsEditor implements ActionListener {
    EditorFrame f;
    public ListenerButtonsEditor(EditorFrame frame) {
	    f=frame;
    }
    
    public void actionPerformed(ActionEvent event) {
    	String label =  ((Button)(event.getSource())).getLabel();
    	
	   if ( ((String)label).equals(f.createLabel)) {
	     f.netPanel.setMode(label);
	     ElviraHelpMessages.show(ElviraHelpMessages.create_message);
	     f.setCursor(Cursor.getPredefinedCursor(f.DEFAULT_CURSOR));
	   }
	   else if ( ((String)label).equals(f.moveLabel)) {
	     f.netPanel.setMode(label);
	     ElviraHelpMessages.show(ElviraHelpMessages.move_message);
	     f.setCursor(Cursor.getPredefinedCursor(f.MOVE_CURSOR));
	   }
   	else if ( ((String)label).equals(f.deleteLabel)) {
	     f.netPanel.setMode(label);
	     ElviraHelpMessages.show(ElviraHelpMessages.delete_message);
	     f.setCursor(Cursor.getPredefinedCursor(f.HAND_CURSOR));
	   }
	   else if ( ((String)label).equals(f.editVariableLabel)) {
	     f.setEditVariableMode();
	   }
	   else if ( ((String)label).equals(f.editFunctionLabel)) {
	     f.setEditFunctionMode();
	   }
	   else if ( ((String)label).equals(f.editNetworkLabel)) {
	     f.setEditNetworkMode();
	   }
    }
    
}  // end of ListenerButtonsEditor class


/* ElviraFrame.java */

import java.awt.*;
import java.io.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.util.Vector;
import java.util.ResourceBundle;

/** 
 * Main window. Every ElviraFrame contains several NetworkPanes 
 *
 * @since 14/09/99
 */

public class ElviraFrame extends Frame {   
   
   /** 
    * Graphical elements
    */   
   private Vector networkPaneList;
   private int currentPaneIndex;   
   private TextArea messageArea;
   MainMenu mainMenuBar; 
   private static Rectangle panelDimensions;
   public static int untitledNetworkCounter;
   
   private static final String UNTITLED = "Untitled.label";
   
   
   /**
    * Default constructor for an ElviraFrame object.
    */
    
   public ElviraFrame() {      
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            
      setTitle("Elvira");
      setSize((int) (screenSize.width * .75), (int) (screenSize.height * .75));
      setBackground(Color.lightGray);
	  	   
	   setIconImage(Toolkit.getDefaultToolkit().getImage("icon_editor.gif"));	    	   	   
	   
	   // Create and add the menu to the frame
	   
	   if (Elvira.getLanguaje() == Elvira.SPANISH) 
         mainMenuBar = new MainMenu("Menus_sp", this);
      else 
         mainMenuBar = new MainMenu("Menus", this);
         
      setMenuBar (mainMenuBar);	                   
	   untitledNetworkCounter = 1;
	  
	   networkPaneList = new Vector();
	   	   
	   messageArea = new TextArea();
	   messageArea.setBackground(Color.white);
	   messageArea.setEditable(false);
	   	   
	   setLayout(new BorderLayout(0, 0));     
      add("South",messageArea);           

	   thereIsNoPanes(); // Disable the menu options that can not be used
      
      setVisible(true);	
	   setTitle("Elvira");
		
	   addWindowListener (new ElviraFrameListener());
   }
         
            
   
   /* ***************** Access methods ******************** */
   
   
   /**
    * Return the current NetworkPane (that is used in a specific moment)
    */
   
   public NetworkPane getCurrentNetworkPane() {
      return (NetworkPane) networkPaneList.elementAt(currentPaneIndex);
   }
        
   
   /**
    * Method to get the visible panel
    */
    
   public int getCurrentPaneIndex () {
      return currentPaneIndex;
   }


   /**
    * Used to know how many networks are open in the ElviraFrame
    */
   
   public int numberOfPanes() {
      return networkPaneList.size();
   }
   
   
   /**
    * Get the structure that contains the list of networkPanes
    */
    
   public Vector getNetworkPaneList() {
      return networkPaneList;
   }
           
   
   
   /* ******************** Modifiers ********************** */

    
   /**
    * Method to set the panel that is visible
    */
    
   public void setCurrentPaneIndex (int i) {
      currentPaneIndex = i;
   }
    
        
    /** 
     * Add a new NetworkPane, set it as the current pane and set it visible
     *
     * @param name Contains the name of the file that contains the network 
     */
     
    public void setNewNetworkPane (String name) {
      NetworkPane oldPane,newPane;      
      CheckboxMenuItem option;
      
      newPane = new NetworkPane (this,name);     
            
      networkPaneList.addElement (newPane);
      
      if (networkPaneList.size()==1) 
         mainMenuBar.windowMenu.addSeparator();      
         
      // Insert the name of the file in the Window menu and sets checked
         
      option = new CheckboxMenuItem(name,true);      
      option.addItemListener ((ItemListener) new WindowMenuItemListener(this));      
	  mainMenuBar.windowMenu.add(option);	 
	   
      newPane.setBounds(0,0,1000,800);   
               
	  changeCurrentNetworkPane (networkPaneList.size()-1);    
	  newPane.setVisible(true);       	      	   
      
    }
    
    
    /**
     * Set as active editor panel the panel in the position given as parameter
     *
     * @param index The position of the new active panel
     */
    
    public void changeCurrentNetworkPane (int index) {
      NetworkPane oldActivePane, newActivePane;
      CheckboxMenuItem option;
      
      if (this.numberOfPanes()>1) {
         oldActivePane = (NetworkPane) networkPaneList.elementAt(currentPaneIndex);
      
         // Set the actual panel name to false
      
         option = (CheckboxMenuItem) mainMenuBar.windowMenu.getItem(currentPaneIndex+5);
         option.setState(false);
      
         // Set invisible the old panel inserted      
      
         oldActivePane.setVisible(false);
         remove(oldActivePane);
      }
      
      // Actions to create and set visible the new panel active
      
      currentPaneIndex = index;
      newActivePane = (NetworkPane) networkPaneList.elementAt(currentPaneIndex);
      add("Center",newActivePane);        
      newActivePane.setVisible(true);    
      newActivePane.getEditorPanel().setVisible(true);
      
      // The window menu must change. Now it is marked the menu item related
      // with the new active panel
      
      option = (CheckboxMenuItem) mainMenuBar.windowMenu.getItem(currentPaneIndex+5);
      option.setState(true);
      
      // Add to the close label of the fileMenu the name of the network to 
      // identificate better
      
      mainMenuBar.setCloseName (newActivePane.getNetworkName());
      
      setTitle ("Elvira - "+newActivePane.getNetworkName());
      mainMenuBar.setModifiedMenus(newActivePane.getEditorPanel().isModifiedNetwork());
      
    }
    
    
    /* **************** Other methods **************** */
    
    /** 
     * Used to write messages about the execution
     */
    
    public void appendText (String text) {
      messageArea.append (text);
    }
                     
   /**
    * Show a dialog window that allows edit the evidence
    */  
   
   public void editEvidence(){
   
       Bnet bn;
       bn = getCurrentNetworkPane().getBayesNet();

       Dialog d = new EditEvidenceDialog(this,getCurrentNetworkPane().getEvidence(),bn);
       d.setSize(300,300);
       d.show();

   }

    
  /* ******************************************************************** */
  /*                    ACCIONS USED IN THE MENU OPTIONS                  */
  /* ******************************************************************** */
  
  
  /* ***************** ACTIONS FOR THE FILE MENU ************************ */
  
   /**
    * Do all the operations related with the opening of a file: create a new
    * pane for the file, load the network in the editorPanel, and sets the 
    * name of the file in all the menuItems where it is necessary
    */
   
   public void openFile (String nameOfFile){
      
      boolean change = false;      
      // Used  to access more easily to the Editor of the NetworkPane
      NetworkPane pane = new NetworkPane(this,"");     
      MenuItem closeOption = mainMenuBar.fileMenu.getItem(9);
 
      if (numberOfPanes()>0) {      
         pane = getCurrentNetworkPane();          
         
         if ( (!pane.getEditorPanel().isModifiedNetwork()) &&
              (pane.getNetworkName().equals("Untitled1")) && 
               (numberOfPanes()==1) ) {      
                  change = true;               
              }
         else 
            setNewNetworkPane(nameOfFile);         
      }
      else
         setNewNetworkPane(nameOfFile);
                    
      try {      
         FileInputStream f;	    
	      appendText("\nLoading " + nameOfFile + "\n");
	      System.out.println("\nLoading " + nameOfFile + "\n");
	      f = new FileInputStream(nameOfFile);	 
	      pane = getCurrentNetworkPane();
	      pane.setBayesNet(new Bnet(f));
	      f.close();	      	      
      } 
    
      catch (IOException e) {         
         appendText("Exception: " + e +"\n");
         appendText("\tFile not loaded correctly.\n\n");
	      System.out.println("Exception: " + e +"\n");
         return;
      } 
    
      catch (ParseException e) {            
         appendText("Parse error: " + e + "\n");
         appendText("\tFile not loaded correctly.\n\n");
	      System.out.println("Parse error: " + e + "\n");
	      closeNetworkPane(getCurrentPaneIndex());
         return;
      }
      
      // Set the name of the file into the variable
      
      pane.setNetworkName(nameOfFile);      
      
      if (change == true) {
         int position = getCurrentPaneIndex();         
         mainMenuBar.windowMenu.getItem(position+5).setLabel(nameOfFile);
      }
      
      
      // Put the network into the graphical interface                
                     
      pane.getEditorPanel().load(pane.getBayesNet());
      setTitle ("Elvira - " + nameOfFile);
      appendText ("\tFile loaded.\n\n");      
      thereIsPanes();
   }

   
   /**
    * This method is called when the option Open... from the Network menu is
    * clicked. When it is called, open a dialog where a file is selected, and
    * the network is load in the variable elvira of this class        
    * 
    * @see ElviraFrame#openFile
    */
    
   public void openAction() {           
      
      FileDialog openFileDialog = new FileDialog(this,"Open",FileDialog.LOAD);
      String nameOfFile = new String ();            
         
	   openFileDialog.show();
      nameOfFile = openFileDialog.getFile();
    	
      if (nameOfFile == null) return;
      nameOfFile = openFileDialog.getDirectory() + nameOfFile;     
          
      // Get a new panel if the actual is not empty
      openFile(nameOfFile);              
     
   }   

 
   /**
    * This method saves the network recorded in the actual NetworkPane.
    * As name of file uses the string contained in CurrentFile. If there 
    * is no string on it calls at saveAsAction. If the network is successful 
    * saved, set the value of modified to False.    
    *
    * @see ElviraFrame#saveAsAction
    */    
    
   public void saveAction(int i) {                    
      
      NetworkPane pane = (NetworkPane) getNetworkPaneList().elementAt(i);      
      
      String nameOfFile = new String (pane.getNetworkName());
	
      if ((nameOfFile == null) || (nameOfFile.equals("")) || (nameOfFile.startsWith(mainMenuBar.menuBundle.getString(UNTITLED))))
         saveAsAction();
      else {         
         
         if (pane.getBayesNet() == null) {
            appendText("\n No Bayesian network to be saved.\n\n");
            return;
         }

         try {
	         FileWriter fileout = new FileWriter(nameOfFile);	
    	      pane.getBayesNet().saveBnet(fileout);        
            fileout.close();
         } 
         catch (IOException e) {
            appendText ("Exception: " + e + "\n");         
         }
         
         appendText ("\tFile saved.\n\n");
	     pane.getEditorPanel().setModifiedNetwork(false);
      }
      
   }

   
   
   /**
    * Used when the saveFileDialog must be open for introduce
    * the name of the file to save
    */
    
   public void saveAsAction() {     	   
      
      NetworkPane pane = getCurrentNetworkPane();
      
      String nameOfFile = new String (pane.getNetworkName());
      FileDialog saveFileDialog = new FileDialog(this,"Save",FileDialog.SAVE);	      
      
	   saveFileDialog.show();
      nameOfFile = saveFileDialog.getFile();
    	
      if (nameOfFile == null) 
    	 return;
    	   
      nameOfFile = saveFileDialog.getDirectory() + nameOfFile;                   
         
      if (pane.getBayesNet() == null) {
         this.appendText ("\n No Bayesian network to be saved.\n\n");
         return;
      }

      try {
	      FileWriter fileout = new FileWriter(nameOfFile);	
         pane.getBayesNet().saveBnet(fileout);        
         fileout.close();
      } 
      catch (IOException e) {
         appendText("Exception: " + e + "\n");         
         appendText("\tFile not saved correctly.\n\n");                 
      }
      
	  pane.getEditorPanel().setModifiedNetwork(false);
	  setTitle("Elvira - " + nameOfFile);	 
	  mainMenuBar.setCloseName(nameOfFile);
	  getCurrentNetworkPane().setNetworkName(nameOfFile);      
   }

      
   /**
    * Take all the necessary actions (shows a dialog to select a file,
    * get the name of the file, open the file and get the bnet from it)
    * to load an evidence file
    * 
    * @return True if there is no problem in the load action
    * False in other case
    * @see loadFromFile
    */

   public boolean loadEvidenceAction(){
 
     Bnet bn = getCurrentNetworkPane().getBayesNet();
     FileDialog openFileDialog = new FileDialog(this,"Open",FileDialog.LOAD);
     
     openFileDialog.show();
     String filename = openFileDialog.getFile();
     if (filename == null) return false;
     filename = openFileDialog.getDirectory() + filename;
   
     if (loadFromFile(filename)) {
       appendText("\tEvidence file loaded.\n\n");
       
       // check if the evidence is coherent with the actual belief network
      
       if (!getCurrentNetworkPane().getEvidence().coherentEvidence(bn)){
         getCurrentNetworkPane().setEvidence (new Evidence());
         appendText("\tThe loaded evidence is not coherent with the belief network\n\n"); 
       }
     }
     else 
        appendText("\tEvidence file not loaded correctly.\n\n");

     return true;
   }


  /**
   * Load the evidence from a file
   * 
   * @param filename The name of the file to open
   * @return True if there is no problem loading the file
   * False in other case
   */

  private boolean loadFromFile(String filename){
    
    Bnet bn = getCurrentNetworkPane().getBayesNet();    

    try {    
       appendText("\nLoading " + filename + "\n");
       FileInputStream f = new FileInputStream(filename);
       getCurrentNetworkPane().setEvidence (new Evidence(f,bn.getNodeList()));
       f.close();
    }    
    catch (ParseException e) {
        appendText("Parse error: " + e + "\n");
        return(false);
    }    
    catch (IOException e) {
        appendText("Exception: " + e +"\n");
        return(false);
    }    
    
    return(true);

  }


 /**
    * Take all the actions to save the evidence in a file
    * 
    * @return True if there is no problem in the save action
    * False in other case
    * @see saveToFile
    */

   public boolean saveEvidenceAction(){
 
     Bnet bn = getCurrentNetworkPane().getBayesNet();
     FileDialog saveFileDialog = new FileDialog(this,"Save",FileDialog.SAVE);	      
     
     saveFileDialog.show();
     String filename = saveFileDialog.getFile();
     if (filename == null)
        return false;
     filename = saveFileDialog.getDirectory() + filename;
   
     if (saveToFile(this,filename)) {
       appendText("\tEvidence file saved.\n\n");
     }
     else appendText("\tEvidence object not saved to file correctly.\n\n");

     return true;
   }


  /**
   * Save the evidence in a file
   * 
   * @param filename The file where the evidence be saved
   * @return True if there is no problem saving the file
   * False in other case.
   */

  public boolean saveToFile(ElviraFrame networkFrame, String filename){
    
    Bnet bn = getCurrentNetworkPane().getBayesNet();
    Evidence evidenceToSave = new Evidence();
    evidenceToSave = getCurrentNetworkPane().getEvidence();

    if (evidenceToSave == null) {
      appendText("\t No Evidence object to be saved.\n\n");
      return(false);
    }
    else if ( ((Vector) evidenceToSave.variables).size() == 0){
      appendText("\t No Evidence object to be saved.\n\n");
      return(false);
    }     

    try {
      
       if ( ((String) evidenceToSave.getName()).equals("") ) 
          evidenceToSave.setName("noName");
          
       appendText ("\nSaving " + filename + "\n");    
	   FileWriter f = new FileWriter(filename);
       evidenceToSave.save(f);
       f.close();
    }    
    catch (IOException e) {
       appendText ("Exception: " + e +"\n");
       return(false);
    }    
    
    return(true);

  }         

 
  /** 
   * Removes a Pane from elvira 
   *
   * @param i Position if the networkPaneList of the pane to remove
   */
	
	public void closeNetworkPane (int i) {
	   
	   CheckboxMenuItem option;	   
	   NetworkPane newOpenPane, oldPane;	   
	   
	   oldPane = (NetworkPane) getNetworkPaneList().elementAt(i);	   
	   
	   oldPane.setVisible(false);
	   networkPaneList.removeElementAt(i);
       option = (CheckboxMenuItem) mainMenuBar.windowMenu.getItem(i+5);
       mainMenuBar.windowMenu.remove(option);

       if (i == currentPaneIndex) {
	      currentPaneIndex = 0;
	      
	      if (this.numberOfPanes()>0) {
	         newOpenPane = (NetworkPane) networkPaneList.elementAt(currentPaneIndex);
             add("Center",newOpenPane);        
             newOpenPane.setVisible(true);    
             newOpenPane.getEditorPanel().setVisible(true); 
             option = (CheckboxMenuItem) mainMenuBar.windowMenu.getItem(currentPaneIndex+5);
             option.setState(true);
	         mainMenuBar.setCloseName(newOpenPane.getNetworkName());  
	         this.setTitle("Elvira - "+newOpenPane.getNetworkName());
         }
         
      }     
      
      if (!(oldPane.getNetworkName().startsWith(mainMenuBar.menuBundle.getString(UNTITLED)))) 
         mainMenuBar.insertLastReference(oldPane.getNetworkName(),this);           
	         
	}   	
	
   
   /**
	* Close the Network editor showing a dialog to accept or cancel this 
    * action. If the network in the editor has been modified, shows other
    * dialog for stores this modifications.
    *
    * @see closeNetworkPane
	*/
	
	public void closeAction() {
       NetworkPane pane =  getCurrentNetworkPane();	   
	   QuitEditorDialog dialog = new QuitEditorDialog(this, pane.getNetworkName(), 
	                                                   getCurrentPaneIndex());
	   
	   if (pane.getEditorPanel().isModifiedNetwork()) 	      	   
	      dialog.show();	   
	   
	   if (!dialog.isCancel()) 
	      closeNetworkPane(getCurrentPaneIndex());	
	   
	   if (numberOfPanes()==0) { 	      
	      thereIsNoPanes();
	   }	   
	   
	   dialog.dispose();	   
	}	


   /** Close all the editors open (asking if is necessary to save the 
       changes) and exits elvira */

   public void exitAction () {
      
      int i;      
      boolean cancel = false;
      NetworkPane pane;  
      QuitEditorDialog dialog;
      
      i = numberOfPanes();
      
      while ((!cancel) && (i>0)) {
         pane = (NetworkPane) getNetworkPaneList().elementAt(i-1);
         if (pane.getEditorPanel().isModifiedNetwork()) {
            dialog = new QuitEditorDialog(this, pane.getNetworkName(), i-1);
            dialog.show();
            if (dialog.isCancel()) 
               cancel = true;                        
            dialog.dispose();            
         }         
         i--;
      } 
      
      if (!cancel) {
         for (i=numberOfPanes()-1; i>=0; i--) 
            closeNetworkPane(i);
         dispose();
         System.exit(0);
      }
            
   }


   /**
    * Called when the first pane is open the frame. This function enables
    * all the option of the menus that was disable.
    */

   public void thereIsPanes() {
    
      int i;
      
      mainMenuBar.fileMenu.getItem(2).setEnabled(true);
     
      for (i=5; i<8; i++) {      
	      mainMenuBar.fileMenu.getItem(i).setEnabled(true);  	   
	   }
      
      // 9 is the position of the option close in the menu	      
	   MenuItem m = (MenuItem) mainMenuBar.fileMenu.getItem(9);
	   m.setLabel(mainMenuBar.menuBundle.getString(mainMenuBar.FILE+"."+mainMenuBar.CLOSE+".label"));
	   m.setEnabled(true);
	   
	   mainMenuBar.modifyMenu.setEnabled(true);
	   
	   for (i=2; i<4; i++) {
	      mainMenuBar.windowMenu.getItem(i).setEnabled(true);
	   }	   	   
	   
   }


   /**
    * Called if there is no panes open in the frame. This function disables
    * all the option of the menus that can't be used when this happens.
    */

   public void thereIsNoPanes() {
      int i;
      
      for (i=3; i<8; i++) {      
	      mainMenuBar.fileMenu.getItem(i).setEnabled(false);  	   
	   }
      
      // 9 is the position of the option close in the menu	      
	   MenuItem m = (MenuItem) mainMenuBar.fileMenu.getItem(9);
	   m.setLabel(mainMenuBar.menuBundle.getString(mainMenuBar.FILE+"."+mainMenuBar.CLOSE+".label"));
	   m.setEnabled(false);
	   
	   mainMenuBar.modifyMenu.setEnabled(false);
	   
	   for (i=2; i<4; i++) {
	      mainMenuBar.windowMenu.getItem(i).setEnabled(false);
	   }
	   
	   mainMenuBar.windowMenu.remove(4);
	   
   }

	
   class ElviraFrameListener extends WindowAdapter {
	
      public void windowClosing(java.awt.event.WindowEvent event) {
		   Object object = event.getSource();
		   if (object == ElviraFrame.this) {
		      exitAction();			   
		   }
	   }
   	     
   }  // end of ElviraFrameListener class  


}  // end of ElviraFrame class   

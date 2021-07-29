import java.io.*;
import java.applet.*;
import java.awt.*;
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This class is used to create and controle the main window
 */

public class ElviraConsoleFrame extends Frame{

  /**
   * Used to call some Elvira methods
   */
  Elvira elvira;

  /**
   * This variable contains all the elements of the window: buttons 
   * and a textarea
   */
  Panel consolePanel;

  /**
   * Area where appears the messages
   */
  java.awt.TextArea consoleTextArea;
  
  /* Labels for the buttons of the frame */
  final public static String editorlabel = "Editor";
  final public static String inferencelabel = "Do Inference";
  final public static String learninglabel = "Learning";
  final public static String quitlabel = "Quit";

  /**
   * Main menu object
   */
  
  java.awt.MenuBar mainMenuBar;

  /**
   * Used to load/save the evidence from/to files
   */
  
  static public Evidence evidence = new Evidence();

  /**
   * Contains the options for inference
   */
  java.awt.Menu inferenceMenu;

  /**
   * Contains the options for networks
   */
  java.awt.Menu networkMenu;

  /**
   * Contains the dialog to open a file
   */
  java.awt.FileDialog openFileDialog;

  /**
   * Contains the dialog to save a file
   */
  java.awt.FileDialog saveFileDialog;


  /**
   * Constructor. Creates the main window
   * 
   * @param elv An elvira object. Use to call this class' methods
   * @param title The title of the frame
   */
  
  public ElviraConsoleFrame(Elvira elv,String title){
	   super(title);
	   setIconImage(Toolkit.getDefaultToolkit().getImage("elvira.jpg"));
	   elvira=elv;
	   consolePanel= new Panel();
	   consolePanel.setLayout(new GridLayout(1,4));
	   
	   ListenerButtonsConsole listenerButtonsConsole=new ListenerButtonsConsole(this);
	   Button button;
	   button=new Button(editorlabel);
	   button.addActionListener(listenerButtonsConsole);
	   consolePanel.add(button);
	   button=new Button(inferencelabel);
	   button.addActionListener(listenerButtonsConsole);
	   consolePanel.add(button);
	   button=new Button(learninglabel);
	   button.addActionListener(listenerButtonsConsole);
	   consolePanel.add(button);
	   button=new Button(quitlabel);
	   button.addActionListener(listenerButtonsConsole);
	   consolePanel.add(button);
	   setLayout(new BorderLayout(0, 0));
	   add("North", consolePanel);
	   
	   consoleTextArea = new java.awt.TextArea();
	   consoleTextArea.setBounds(getInsets().left + 173,getInsets().top + 159,80,22);
	   add("Center", consoleTextArea);

      mainMenuBar = new java.awt.MenuBar();   
         
      inferenceMenu = new java.awt.Menu("Inference");
      inferenceMenu.add("Edit Evidence");
      inferenceMenu.add("Load Evidence");
      inferenceMenu.add("Save Evidence");
      inferenceMenu.add("Select Inference Kind");
      inferenceMenu.add("Select Inference Method");
      mainMenuBar.add(inferenceMenu);
	   
	   networkMenu = new java.awt.Menu("Network");
      networkMenu.add("Open...");
      networkMenu.add("Open URL...");
      networkMenu.add("Save");
      networkMenu.add("Save As...");
      networkMenu.add("Clear");
      networkMenu.addSeparator();
      networkMenu.add("Hide");
      mainMenuBar.add(networkMenu);
      networkMenu.addActionListener(new ListenerMenuNetwork(elvira.getEditorFrame())); 
	   	   
      setMenuBar(mainMenuBar);

      openFileDialog = new java.awt.FileDialog(this,"Open",FileDialog.LOAD);
      saveFileDialog = new java.awt.FileDialog(this,"Save",FileDialog.SAVE);
	 }


	/**
	 * Quit Elvira showing a dialog window
	 */
	
	void quitAction() {
	  if (elvira.getEditorFrame().isModified())
	    (new QuitEditorDialog(this,elvira,"Quit Elvira?",false,true)).setVisible(true);
	  else
	    (new QuitDialog(this, elvira, "Quit Elvira?", false)).setVisible(true);
	}
	 
	 
	 /**
	  * Handle button events.
	  * 
	  * @param evt Event to handle
	  * @param arg Object that produces the event
	  * @return True if there is no problem handling the event
	  */
	  
	public boolean action(Event evt, Object arg) {
	   /*if (evt.target instanceof Button) {
	     String label =  ((Button)(evt.target)).getLabel();
	     if ( ((String)arg).equals(editorlabel)) {
	       elvira.getEditorFrame().setVisible(true);
	     }
	     else if ( ((String)arg).equals(inferencelabel)) {
               if (elvira.getEditorFrame() != null){
                  if (elvira.getEditorFrame().get_bnet().node_list.size()!=0)
                     System.out.println("Run ... Run ... Run");
                  else elvira.appendText("Please, load or create a Network\n");
               }else elvira.appendText("Please, load or create a Network\n");
               
	     }
	     else if ( ((String)arg).equals(learninglabel)) {
	     }
	     else if ( ((String)arg).equals(quitlabel)) {
		if(elvira.getEditorFrame().is_modified())
		  elvira.appendText("The Elvira object has been modified and not saved");
	       Quit_Action();
	     }
	   }
           else */
           
             if (evt.target instanceof MenuItem) { 
               String label = (String) ( ((MenuItem)evt.target).getLabel());
               if (label.equalsIgnoreCase("Edit Evidence")){
                 //System.out.println("Edit Evidence");
                 if (elvira.getEditorFrame() != null)
                   if (elvira.getEditorFrame().getBnet().getNodeList().size()!=0){
                     elvira.appendText("To observe a node click in the list");
                     editEvidence();
                     //elvira.inferenceFrame.setVisible(true);
                   }
                   else elvira.appendText("Please, load or create a Network\n");
                 return true;
               }
               else if (label.equalsIgnoreCase("Load Evidence")){
                 if (loadEvidenceAction()==false) return false;
                 return true;
               }
               else if (label.equalsIgnoreCase("Save Evidence")){
                 System.out.println("Save Evidence");
                 if (saveEvidenceAction()==false) return false;
                 return true;
               }
               else if (label.equalsIgnoreCase("Select Inference Kind")){
	         System.out.println("Select Inference Kind");
                 return true;
               }
               else if (label.equalsIgnoreCase("Select Inference Method")){
	         System.out.println("Select Inference Method");
		 return true;
               }
             }
	   return true;
	 }


   /**
    * Show a dialog window that allows edit the evidence
    */  
   
   public void editEvidence(){
   
       Bnet bn;
       bn = elvira.getEditorFrame().getBnet();

       Dialog d = new EditEvidenceDialog(this,evidence,bn);
       d.setSize(300,300);
       d.show();

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
 
     Bnet bn = elvira.getEditorFrame().getBnet();

     openFileDialog.show();
     String filename = openFileDialog.getFile();
     if (filename == null) return false;
     filename = openFileDialog.getDirectory() + filename;
   
     if (loadFromFile(filename)) {
       elvira.appendText("\tEvidence file loaded.\n\n");
       
       // check if the evidence is coherent with the actual belief network
      
       if (!evidence.coherentEvidence(bn)){
         evidence=new Evidence();
         elvira.appendText("\tThe loaded evidence is not coherent with the belief network\n\n"); 
       }
     }
     else 
        elvira.appendText("\tEvidence file not loaded correctly.\n\n");

     return true;
   }


  /**
   * Load the evidence from a file
   * 
   * @param filename The name of the file to open
   * @return True if there is no problem loading the file
   * False in other case
   */

  boolean loadFromFile(String filename){
    
    Bnet bn;
    bn = elvira.getEditorFrame().getBnet();

    try {    
       elvira.appendText("\nLoading " + filename + "\n");
       FileInputStream f = new FileInputStream(filename);
       evidence = new Evidence(f,bn.getNodeList());
       f.close();
    }    
    catch (ParseException e) {
        elvira.appendText("Parse error: " + e + "\n");
        return(false);
    }    
    catch (IOException e) {
        elvira.appendText("Exception: " + e +"\n");
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

   public boolean saveEvidenceAction( ){
 
     Bnet bn = elvira.getEditorFrame().getBnet();

     saveFileDialog.show();
     String filename = saveFileDialog.getFile();
     if (filename == null) return false;
     filename = saveFileDialog.getDirectory() + filename;
   
     if (saveToFile(filename)) {
       elvira.appendText("\tEvidence file saved.\n\n");
     }
     else elvira.appendText("\tEvidence object not saved to file correctly.\n\n");

     return true;
   }


  /**
   * Save the evidence in a file
   * 
   * @param filename The file where the evidence be saved
   * @return True if there is no problem saving the file
   * False in other case.
   */

  public boolean saveToFile(String filename){
    
    Bnet bn;
    bn = elvira.getEditorFrame().getBnet();

    if (evidence == null) {
      elvira.appendText("\t No Evidence object to be saved.\n\n");
      return(false);
    }
    else if ( ((Vector)evidence.variables).size() == 0){
      elvira.appendText("\t No Evidence object to be saved.\n\n");
      return(false);
    }     

    try {
       if ( ((String)evidence.getName()).equals("") ) 
          evidence.setName("noName");
       elvira.appendText("\nSaving " + filename + "\n");    
	    FileWriter f = new FileWriter(filename);
       evidence.save(f);
       f.close();
    }    
    catch (IOException e) {
        elvira.appendText("Exception: " + e +"\n");
        return(false);
    }    
    
    return(true);

  }
}



class ListenerMenuNetwork implements ActionListener {
    EditorFrame f;
    
    public ListenerMenuNetwork(EditorFrame frame){
       f=frame;
    }
    
    public void actionPerformed(ActionEvent event) {
	   //String label = (String) ( ((MenuItem)event.getSource()).getActionCommand());
	   String label=event.getActionCommand();
	   //System.out.println("La etiqueta es
	   if (label.equalsIgnoreCase("Clear")) {
	      f.clearAction();
	   }
	   else if  (label.equalsIgnoreCase("Save")) {
	      f.saveAction();
	   } 
	   else if (label.equalsIgnoreCase("Save As...")) {
	      f.saveAsAction();
	   } 
	   else if (label.equalsIgnoreCase("Open...")) {
	      f.openAction();
	   } 
	   else if (label.equalsIgnoreCase("Open URL...")) {
	      f.openURLAction();
	   } 
	   else if (label.equalsIgnoreCase("Hide")) {
	      f.hideAction();
	   }
    }
}



class ListenerButtonsConsole implements ActionListener {
    ElviraConsoleFrame f;
    public ListenerButtonsConsole(ElviraConsoleFrame frame) {
	f=frame;
    }
    public void actionPerformed(ActionEvent event) {
    	     String label =  ((Button)(event.getSource())).getLabel();
	     if ( ((String)label).equals(f.editorlabel)) {
	       f.elvira.getEditorFrame().setVisible(true);
	     }
	     else if ( ((String)label).equals(f.inferencelabel)) {
               // toco lo siguiente
               if (f.elvira.getEditorFrame() != null){
                  if (f.elvira.getEditorFrame().getBnet().getNodeList().size()!=0)
                     System.out.println("Run ... Run ... Run");
                  else f.elvira.appendText("Please, load or create a Network\n");
               }else f.elvira.appendText("Please, load or create a Network\n");
               
	     }
	     else if ( ((String)label).equals(f.learninglabel)) {
	     }
	     else if ( ((String)label).equals(f.quitlabel)) {
	       //Elvira.quit();
	       if(f.elvira.getEditorFrame().isModified())
		  f.elvira.appendText("The Elvira object has been modified and not saved");
	       f.quitAction();
	     }
    }
}
	 
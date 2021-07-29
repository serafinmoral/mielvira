/* MainMenu.class */

import java.awt.*;
import java.io.*;
import java.util.ResourceBundle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.MissingResourceException;


/** 
 * This class creates all the menus of the ElviraFrame. For this uses a file
 * that contains all the strings to be displayed in this menu 
 */

public class MainMenu extends MenuBar {
    
    Menu fileMenu, editMenu, modifyMenu, windowMenu;        
    ResourceBundle menuBundle;
    
    public static final String FILE = "File";
    public static final String NEW = "New";
    public static final String OPEN = "Open";
    public static final String OPEN_URL = "OpenURL";
    public static final String SAVE = "Save";
    public static final String SAVE_AS = "SaveAs";
    public static final String LOAD_EVIDENCE = "LoadEvidence";
    public static final String SAVE_EVIDENCE = "SaveEvidence";
    public static final String CLOSE = "Close";
    public static final String EXIT = "Exit";
    
    public static final String EDIT = "Edit";
    public static final String UNDO = "Undo";
    public static final String CUT = "Cut";
    public static final String COPY = "Copy";
    public static final String PASTE = "Paste";
    public static final String SELECT_ALL = "SelectAll";
    
    public static final String MODIFY = "Modify";
    public static final String CHANCE = "Chance";
    public static final String DECISION = "Decision";
    public static final String UTILITY = "Utility";
    public static final String LINK = "Link";
    
    public static final String WINDOW = "Window";
    public static final String NEW_WINDOW = "New_Window";
    public static final String NEXT = "Next";
    public static final String PREVIOUS = "Previous";
    
    
   /**
    * Constructor. Creates the menus of the frame.
    */
   
   public MainMenu(String bundleName, ElviraFrame frame) {            
      
      // Use a ResourceBundle variable to internacionalizate the menus      
      menuBundle =  ResourceBundle.getBundle (bundleName);            
      
      // Actions to create the File menu                          
      
	  fileMenu = createMenu(menuBundle,FILE);	      
	  FileMenuListener f = new FileMenuListener(frame,menuBundle);     
	   
	  fileMenu.add(createMenuItem (menuBundle, f, FILE, NEW));	            	        
      fileMenu.add(createMenuItem (menuBundle, f, FILE, OPEN));
      fileMenu.add(createMenuItem (menuBundle, f, FILE, OPEN_URL));
      fileMenu.add(createMenuItem (menuBundle, f, FILE, SAVE));
      fileMenu.add(createMenuItem (menuBundle, f, FILE, SAVE_AS));
      fileMenu.addSeparator();
      fileMenu.add(createMenuItem (menuBundle, f, FILE, LOAD_EVIDENCE));
      fileMenu.add(createMenuItem (menuBundle, f, FILE, SAVE_EVIDENCE));
      fileMenu.addSeparator();
      fileMenu.add(createMenuItem (menuBundle, f, FILE, CLOSE));      
      fileMenu.add(createMenuItem (menuBundle, f, FILE, EXIT));
      add(fileMenu);
	   
	   // Actions to create the Edit menu         
	   	   
      editMenu = createMenu(menuBundle,EDIT);
      
      editMenu.add (createMenuItem (menuBundle, f, EDIT,UNDO));
      editMenu.addSeparator();
      editMenu.add (createMenuItem (menuBundle, f, EDIT,CUT));
      editMenu.add (createMenuItem (menuBundle, f, EDIT,COPY));
      editMenu.add (createMenuItem (menuBundle, f, EDIT,PASTE));
      editMenu.addSeparator();
      editMenu.add (createMenuItem (menuBundle, f, EDIT,SELECT_ALL));
      add(editMenu);
      
      // Actions to create the Modify menu
            
      modifyMenu = createMenu(menuBundle,MODIFY);
      ModifyMenuListener m = new ModifyMenuListener (frame,menuBundle);
      
      modifyMenu.add (createMenuItem (menuBundle, m, MODIFY, CHANCE));
      modifyMenu.add (createMenuItem (menuBundle, m, MODIFY, DECISION));
      modifyMenu.add (createMenuItem (menuBundle, m, MODIFY, UTILITY));
      modifyMenu.add (createMenuItem (menuBundle, m, MODIFY, LINK));
      add(modifyMenu);
      
      
      // Actions to create the Window menu      
      
      windowMenu = createMenu(menuBundle,"Window");
      WindowMenuListener w = new WindowMenuListener (frame,menuBundle);
      
      windowMenu.add (createMenuItem (menuBundle,w,"Window","New_Window"));
      windowMenu.addSeparator();
      windowMenu.add (createMenuItem (menuBundle,w,"Window","Next"));
      windowMenu.add (createMenuItem (menuBundle,w,"Window","Previous"));
      windowMenu.addSeparator();
      add (windowMenu);     
      
      loadLastReferences (frame);
      
   }
   
   
   /** 
    * Creates a new menu getting the string getting the string associated to
    * the name parameter in the resourcebundle b. If it isn't found, the 
    * string name is the new menu created
    *
    * @param b Contains all the strings for the menus
    * @param name Used to find the correct string in b
    */
   
   public Menu createMenu (ResourceBundle b, String name) {
      Menu m;
      String menuLabel;           
      
      try {
         menuLabel = b.getString (name+".label");             	   
      }
      catch (MissingResourceException e) {menuLabel = name;}
      
	   m = new Menu(menuLabel);
	   return m;
   }
   
   
   /**
    * Creates a new menu item using the menuName and menuItemName strings to
    * get the string of the menu item to create in the ResourceBundle. If it 
    * is not found, the menuItemName is the new menuItem created.
    *
    * @param bundle Contains all the strings for the menus
    * @param menuName Used to know the name of the menu where the menuItem
    *                 will be added
    * @param menuItemName Used with menuName to get the correct string for 
    *                     the menuItem to create.
    */
   
   public MenuItem createMenuItem (ResourceBundle bundle, ActionListener listener,
                                   String menuName, String menuItemName) {
      MenuItem item;
      MenuShortcut shortcut;
      String menuItemLabel, shortcutString;
      
      // Get the label for the menu item
      
      try {
         menuItemLabel = bundle.getString(menuName+"."+menuItemName+".label");
      }
      catch (MissingResourceException e) {menuItemLabel = menuItemName;}
      
      // Get the shortcut for the menu item
      
      try {
         shortcutString = bundle.getString(menuName+"."+menuItemName+".shortcut");
      }
      catch (MissingResourceException e) {shortcutString = null;}
      
      if (shortcutString != null) {
         shortcut = new MenuShortcut (shortcutString.charAt(0));
         item = new MenuItem (menuItemLabel,shortcut);      
      }
      else 
         item = new MenuItem (menuItemLabel);
         
      item.addActionListener(listener);
      item.setActionCommand(menuItemName);
         
      return item;
   }
      
   
   
   /** 
    * This method is used to save a network when it is need out of this source
    * file
    * 
    * @param i Contains the position of the pane which network is going to 
    *          be saved
    */
   
   public void saveNetwork (ElviraFrame f, ResourceBundle b, int i) {
      FileMenuListener listener = new FileMenuListener (f,b);
      f.saveAction(i);
   }
   
   
   /**
    * Used to set by the option close the name of the file to close
    *
    * @param networkName Contains the string to set
    */
   
   public void setCloseName (String networkName) {
      String closeLabel;
      MenuItem closeItem;
      
      closeLabel = menuBundle.getString("File.Close.label");      
      closeItem = fileMenu.getItem(9);
      closeItem.setLabel(closeLabel+networkName);
      fileMenu.remove(9);
      fileMenu.insert(closeItem, 9);
   }

   
   /**
    * Inserts in the file menu the name of the last file closed
    * 
    * @param fileName The name of the file to insert in the menu 
    */
   
   public void insertLastReference(String fileName, ElviraFrame frame) {
      int position;
	  String menuItemName;      
   
      // The file had a name, so we add to the list of last files 
      // used	      
      if (fileMenu.getItemCount()==11) {
  	         
          // There isn't any name in the list of last reference files, so
          // add two separators for the list
	       fileMenu.insertSeparator(10);
	       fileMenu.insertSeparator(10);
	    }
	    else {
	         
	       // The list has 4 elements, so we have to remove the first
	       if (fileMenu.getItemCount()==17) 
	          fileMenu.remove(11);
	    }
	    
	    position = fileMenu.getItemCount()-12;
	    menuItemName = Integer.toString(position) +". "+fileName;    
	    
	    MenuItem m = new MenuItem(menuItemName);
	    fileMenu.insert(m, position+10);
	    m.addActionListener(new FileMenuListener(frame,menuBundle));
	}
   
   
   /**
    * Saves in a file called "elvira.lst" the list of the last references.
    * This method is called when the option exit is clicked 
    */
   
   public void saveLastReferences () {
      FileWriter f;
      PrintWriter p;
      int numberFiles,i;
      
      try {
         f = new FileWriter("elvira.lst");
         p = new PrintWriter(f);
      
         // 13 is the number of references in the file menu that are not
         // name of files to save
      
         numberFiles=fileMenu.getItemCount()-13;      
         
         p.print(Integer.toString(numberFiles)+"\n");
      
         for (i=0; i<numberFiles; i++) {
            p.print(fileMenu.getItem(i+11).getLabel().substring(3)+"\n");
         }        
         
         f.close();
      }
      catch (IOException e) {  // There is a problem the list of last reference
                               // files is not created
      }
   }


   /**
    * Load the last references to a files from "elvira.lst". This method is
    * called when the menu constructor is called 
    */

   public void loadLastReferences (ElviraFrame frame) {
      FileReader f;
      LineNumberReader ln;
      String line;
      char car;
      int numberFiles,i;
      
      try {
         f = new FileReader("elvira.lst");
         ln = new LineNumberReader (f);
      
         // 13 is the number of references in the file menu that are not
         // name of files to save
      
         line = ln.readLine();         
         car = line.charAt(0);
         numberFiles = Character.digit(car, 10);         
      
         for (i=0; i<numberFiles; i++) {
            insertLastReference(ln.readLine(),frame);
         }        
         
         f.close();
      }
      catch (IOException e) {  // There is a problem the list of last reference
                               // files is not created
      }
   }
   
   
   /**
    * This method is used to enable the options of the menus that must be
    * active when the network has been modified
    */
   
   public void setModifiedMenus (boolean m) {
  	  if (m) {
	     fileMenu.getItem(3).setEnabled(true);
	     fileMenu.getItem(4).setEnabled(true);
	  }
	  else {
	     fileMenu.getItem(3).setEnabled(false);
	     fileMenu.getItem(4).setEnabled(false);
      }  	      
   }

     
   
}   // end of MainMenu class



/** 
 * Controles all the events produced in the File menu 
 */
   
class FileMenuListener implements ActionListener {
    ElviraFrame frame;
    ResourceBundle bundle;
    
    public FileMenuListener(ElviraFrame f, ResourceBundle b){
       frame=f;
       bundle=b;
    }
    
    public void actionPerformed(ActionEvent event) {	
      
	   MenuItem option= (MenuItem) event.getSource();		   
	   
	   if (option.getActionCommand().equals(frame.mainMenuBar.SAVE)) {  
	      frame.saveAction(frame.getCurrentPaneIndex());
	   } 
	   else if (option.getActionCommand().equals(frame.mainMenuBar.SAVE_AS)) {
	      frame.saveAsAction();
	   } 
	   else if (option.getActionCommand().equals(frame.mainMenuBar.OPEN)) {
	      frame.openAction();
	   } 
	   else if (option.getActionCommand().equals(frame.mainMenuBar.OPEN_URL)) {
	      (new OpenURLDialog(frame, true)).show();
	   } 
	   else if (option.getActionCommand().equals(frame.mainMenuBar.CLOSE)) {
	      frame.closeAction();	    
	   } 
	   else if (option.getActionCommand().equals(frame.mainMenuBar.NEW)) {
	      (new NewDialog (frame)).show();
	   }
	   else if (option.getActionCommand().equals(frame.mainMenuBar.LOAD_EVIDENCE)) {
	      frame.loadEvidenceAction();
	   }
	   else if (option.getActionCommand().equals(frame.mainMenuBar.SAVE_EVIDENCE)) {
	      frame.saveEvidenceAction();
	   }
	   else if (option == frame.mainMenuBar.fileMenu.getItem(frame.mainMenuBar.fileMenu.getItemCount()-1)) {
	      frame.mainMenuBar.saveLastReferences();
	      frame.exitAction();	   	      
	   } 
	   else { // Event produced in the last reference list    
	      
	      String name = option.getLabel();	      
	      
	      // Every item in this list has the estructure      <Number>. <Name of file>
	      // The first thing to do is remove the <Number>. 
	      name = name.substring(3);  
	      
         frame.openFile(name);
	      
	      // if the list only have this element we have to remove the Menu's separators
	      if (frame.mainMenuBar.fileMenu.getItemCount()==14) {
	         frame.mainMenuBar.fileMenu.remove(10);
	         frame.mainMenuBar.fileMenu.remove(11);
	      }
	      frame.mainMenuBar.fileMenu.remove(option);
	   }
	   
    }            
    
} // end of FileMenuListener class



/** 
 * Controles all the events produced in the Modify menu 
 */ 
    
class ModifyMenuListener implements ActionListener {
    ElviraFrame frame;
    ResourceBundle bundle;
    
    public ModifyMenuListener(ElviraFrame f, ResourceBundle b){
       frame=f;
       bundle=b;
    }
    
   public void actionPerformed(ActionEvent event) {	   
      
	   MenuItem option= (MenuItem) event.getSource();		   
	   EditorPanel editor = frame.getCurrentNetworkPane().getEditorPanel();

	   if (option.getActionCommand().equals(frame.mainMenuBar.CHANCE)) {  
	      editor.setMode ("Create Node");
	   } 
	   else if (option.getActionCommand().equals(frame.mainMenuBar.LINK)) {
	      editor.setMode ("Create Link");
	   } 
   
	 }
	 
}  // end of ModifyMenuListener class


	   


/** 
 * Controles all the events produced in the Window menu, excepts the actions 
 * related with the names of the opened files
 */ 
    
class WindowMenuListener implements ActionListener {
    ElviraFrame frame;
    ResourceBundle bundle;
    
    public WindowMenuListener(ElviraFrame f, ResourceBundle b){
       frame=f;
       bundle=b;
    }
    
    public void actionPerformed(ActionEvent event) {	   
      
	   MenuItem option= (MenuItem) event.getSource();		   	   	   
	   NetworkPane pane = frame.getCurrentNetworkPane();
	   NetworkPane copyPane;

	   if (option == frame.mainMenuBar.windowMenu.getItem(0)) {  	      
	      /* Opcion casi operativa. Si se descomenta el codigo funciona
	         excepto en un detalle 
	      frame.setNewNetworkPane(pane.getNetworkName());
	      
	      copyPane = frame.getCurrentNetworkPane();
	      copyPane.setBayesNet(pane.getBayesNet());
	      copyPane.getEditorPanel().load(copyPane.getBayesNet());*/
	      
	   } 
	   
	   else if (option.getActionCommand().equals(frame.mainMenuBar.NEXT)) {
	      if (frame.numberOfPanes()>1) {
	         if (frame.getCurrentPaneIndex()!=frame.numberOfPanes()-1)
	            frame.changeCurrentNetworkPane(frame.getCurrentPaneIndex()+1);
	         else
	            frame.changeCurrentNetworkPane(0);
	      }
	   }   
	   
	   else if (option.getActionCommand().equals(frame.mainMenuBar.PREVIOUS)) {
	      if (frame.numberOfPanes()>1) {
	         if (frame.getCurrentPaneIndex()!=0)
	            frame.changeCurrentNetworkPane(frame.getCurrentPaneIndex()-1);
	         else
	            frame.changeCurrentNetworkPane(frame.numberOfPanes()-1);
	      }
	   }      	    
	   
	 }
	 	 	 
}  // end of WindowMenuListener class



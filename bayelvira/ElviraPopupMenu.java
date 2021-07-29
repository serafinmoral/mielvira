/* ElviraPopupMenu.java */

import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

/** This class creates a PopupMenu that is displayed when the left button
 *  of the mouse is clicked */

public class ElviraPopupMenu extends PopupMenu {
   
   /**
    * Creates a new ElviraPopupMenu.
    * 
    * @param panel Is where the PopupMenu will be appeared
    * @param bundleName Contains the name of the file where the string for
    *        the menuItems are allocated
    */
    
   ElviraPopupMenu (EditorPanel panel, String bundleName) {
      
      ResourceBundle b =  ResourceBundle.getBundle (bundleName);
      
      add(createPopupItem(b,"Popup","Delete"));
      add(createPopupItem(b,"Popup","EditVariable"));
      add(createPopupItem(b,"Popup","EditFunction"));
      addActionListener (new PopupMenuListener(panel,b));  
      panel.add(this);
      
   }
   
      
   /**
    * Creates a new menu item in the ElviraPopupMenu getting the string
    * of the menu item to create in the ResourceBundle. If it isn't found,
    * the menuItemName is the new menuItem created.
    *
    * @param b Contains all the strings for the menus
    * @param menuName Used to know the name of the menu where the menuItem
    *                 will be added
    * @param menuItemName Used with menuName to get the correct string for 
    *                     the menuItem to create.
    */
   
   public MenuItem createPopupItem (ResourceBundle b, String menuName, 
                                   String menuItemName) {
      MenuItem m;
      String menuItemLabel;
      
      try {
         menuItemLabel = b.getString(menuName+"."+menuItemName+".label");
      }
      catch (MissingResourceException e) {menuItemLabel = menuItemName;}
      
      m = new MenuItem (menuItemLabel);
      return m;
   }
   
} // end of ElviraPopupMenu



/** This class controles all the events produced in the PopupMenu. All the 
 *  methods using here are in EditorPanel.java */

class PopupMenuListener implements ActionListener {
    EditorPanel panel;
    ResourceBundle bundle;
    
    public PopupMenuListener(EditorPanel p, ResourceBundle b){
       panel=p;
       bundle=b;
    }
    
    
    public void actionPerformed(ActionEvent event) {
       
	    String label=event.getActionCommand();		   
       if (label.equalsIgnoreCase(bundle.getString("Popup.Delete.label")) ) {
	       panel.setMode("Delete");	    
	    } 
	    else if (label.equalsIgnoreCase(bundle.getString("Popup.EditVariable.label")) ) {	       
	       panel.setMode("Edit Variable");	        	       
	    } 
	    else if (label.equalsIgnoreCase(bundle.getString("Popup.EditFunction.label")) ) {
	       panel.setMode("Edit Function");	       
	    } 
	    
	    panel.mousePressed(panel.lastEvent);
	    panel.setMode("Move");
	 }
   
}  // end of PopupMenuListener
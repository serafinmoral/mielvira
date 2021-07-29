/* WindowMenuListener.java */

import java.awt.CheckboxMenuItem;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

/** This class controls all the events produced in the network menu. This class
    is implemented in a source file separate of MainMenu.java (the others
    classes that processes the events are implemented there) because there
    are more classes than this that need to use object of this class */

public class WindowMenuItemListener implements ItemListener {
   ElviraFrame frame;
   ResourceBundle bundle;
   
   public WindowMenuItemListener (ElviraFrame f/*, ResourceBundle b*/) {
      frame = f;
      /*bundle=b;*/
   }   
   
   public void itemStateChanged (ItemEvent event) {
      CheckboxMenuItem item = (CheckboxMenuItem) event.getItemSelectable();
      String label = item.getLabel();
      int i=0;
      boolean salir = false;
      
      while (!salir) {
         if (frame.mainMenuBar.windowMenu.getItem(i+5).getLabel().equals(label)) {
            salir = true;
            frame.setCurrentPaneIndex(i);
         }
         else
            i++;
      }
   }
   
} // end of class
            

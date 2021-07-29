/* GeneralDialog.java */

import java.awt.*;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.awt.event.WindowAdapter;

/* This class contains all variables and methods that will be used in every
   particulary dialog */

public class GeneralDialog extends Dialog {
    
    ElviraFrame parentFrame;
    ResourceBundle bundle;
    
    GeneralDialog (ElviraFrame f, String title) {
        super (f, title, true);        
        
        Rectangle bounds = getParent().getBounds();
        Rectangle abounds = getBounds();

    	  setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
                    bounds.y + (bounds.height - abounds.height)/2);
                    
        setResizable(false);
    }
    
    
    GeneralDialog (ElviraFrame f, String bundleName, String title) {
       this(f,title);
       bundle = ResourceBundle.getBundle (bundleName);
       /*addWindowListener (new GeneralDialogListener());*/
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

   
   /*class GeneralDialogListener extends WindowAdapter {
	
      public void windowClosing(java.awt.event.WindowEvent event) {
		   Object object = event.getSource();
		   if (object == GeneralDialog.this) {
		      setVisible(false);		      
		   }
	   }
   	     
   }*/  // end of ElviraFrameListener class  


    
} // end of GeneralDialog class
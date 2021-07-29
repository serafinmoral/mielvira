import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class OpenURLDialog extends Dialog implements ActionListener { 
    
  ElviraFrame parentFrame;
  ResourceBundle bundle;

  TextField textURL;
  Panel panelButtons;
  Button okButton;
  Button cancelButton;
  
  private static final String DIALOG_NAME = "OpenURL";
  private static final String TITLE = "Title";
  private final static String OK = "Ok";  
  private final static String CANCEL = "Cancel";
  
  
/**
  * Constructor. Creates a new dialog with the parent and model gives. 
  * Inicialices the elvira variable of the class using the elv parameter
  *   
  */
  
  public OpenURLDialog(ElviraFrame parent, boolean modal) {
	   super(parent, "Elvira", false); 
	   
           Rectangle bounds = getParent().getBounds();
           Rectangle abounds = getBounds();

    	   setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
                       bounds.y + (bounds.height - abounds.height)/2);
                    
      	   setResizable(false);
       
      	   if (Elvira.getLanguaje() == Elvira.SPANISH) 
         	bundle = ResourceBundle.getBundle ("Dialogs_sp");
      	   else
         	bundle = ResourceBundle.getBundle ("Dialogs");
	   
	   setTitle (getString(DIALOG_NAME, TITLE));

	   setLayout(new BorderLayout());
	   addNotify();
	   setSize(getInsets().left + getInsets().right + 295,getInsets().top + getInsets().bottom + 92);
	   
	   textURL = new TextField();
	   add("North", textURL);
	   
	   panelButtons = new Panel();
	   panelButtons.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
	   okButton = new Button(getString(DIALOG_NAME, OK));
	   panelButtons.add(okButton);
	   cancelButton = new Button(getString(DIALOG_NAME, CANCEL));
	   panelButtons.add(cancelButton);
	   add("South", panelButtons);
	   
	   okButton.addActionListener(this);
	   cancelButton.addActionListener(this);

	 }
	  
  
  private void okButtonClicked() {
    openURL(textURL.getText());
    dispose();
  }
  
  private void cancelButtonClicked() {
    dispose();
  }

	
	/** 
	 * Handle events 
	 */
	
	public void actionPerformed(ActionEvent e) {
	   
	  if (e.getSource() == cancelButton) {	  
	    cancelButtonClicked();
	  }
	  else if (e.getSource() == okButton) {	   
	    okButtonClicked();
	  }
	}


	/**
	 * Open a net containe in the URL direction given as parameter
	 */
	
   public boolean openURL(String filename) {
      Bnet bn;

      try {
         parentFrame.appendText("\nLoading " + filename + "\n");
         bn = new Bnet(new URL(filename));
      } catch (IOException e) {
         parentFrame.appendText("Exception: " + e +"\n");
         return(false);
      } catch (ParseException e) {
         parentFrame.appendText("Parse error: " + e + "\n");
         return(false);
      }

    // Put the network into the graphical interface
      parentFrame.setNewNetworkPane(filename);
      (parentFrame.getCurrentNetworkPane()).setBayesNet(bn);      

    return(true);
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

}

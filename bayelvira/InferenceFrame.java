/*
 *  InferenceFrame.java
 *
 *  Frame that houses everything
 *
 *  Written by  Sreekanth Nagarajan, modified by Fabio Cozman
 *
 */

// MIO import InferenceGraphs.*;

import java.awt.*;

public class InferenceFrame extends Frame {
   Elvira elvira;
   Panel cmdPanel,editPanel;
   // NetworkPanel netPanel;
   java.awt.MenuBar mainMenuBar;
   java.awt.Menu menu1;
   java.awt.FileDialog OpenFileDialog;
   java.awt.FileDialog SaveFileDialog;
   private String current_save_filename;


    // constants for caption text of all buttons
   public static final String evidenceLabel  = "Evidence";
   public static final String selectMethodLabel    = "Select Method";
   public static final String doInferenceLabel  = "Do Inference";
   // public static final String queryLabel   = "Query";
   // public static final String observeLabel  = "Observe";
   // public static final String editVariableLabel = "Edit Variable";
   // public static final String editFunctionLabel = "Edit Function";
   // public static final String editNetworkLabel = "Edit Network";

    /* ******************************************************** *
     * Default constructor for an InferenceFrame object.           *
     * ******************************************************** */
    public InferenceFrame(Elvira elv
		       //JavaBayes java_bayes, String title
		       ) {
//      super(title);

//      jb = java_bayes;
	super("Inference");
	if(!(elv.is_applet))
	  setIconImage(Toolkit.getDefaultToolkit().getImage("icon_editor.gif"));
	elvira = elv;
	// netPanel = new NetworkPanel((EditorFrame) this);
	cmdPanel = new Panel();
	cmdPanel.setLayout(new GridLayout(1,5));
	mainMenuBar = new java.awt.MenuBar();
	

     menu1 = new java.awt.Menu("File");
     menu1.add("Open...");
     menu1.add("Open URL...");
     menu1.add("Save");
     menu1.add("Save As...");
     menu1.add("Clear");
     menu1.addSeparator();
     menu1.add("Hide");
     mainMenuBar.add(menu1);
     setMenuBar(mainMenuBar);
 
    OpenFileDialog = new java.awt.FileDialog(this, "Open",FileDialog.LOAD);
    SaveFileDialog = new java.awt.FileDialog(this, "Save",FileDialog.SAVE);
    cmdPanel.add(new Button(evidenceLabel));
    cmdPanel.add(new Button(selectMethodLabel));
    cmdPanel.add(new Button(doInferenceLabel));
//	  cmdPanel.add(new Button(queryLabel));
//	  cmdPanel.add(new Button(observeLabel));

      editPanel = new Panel();
      editPanel.setLayout(new GridLayout(1,3));
      //editPanel.add(new Button(editVariableLabel));
      //editPanel.add(new Button(editFunctionLabel));
      //editPanel.add(new Button(editNetworkLabel));

      setLayout(new BorderLayout(0, 0));
      add("North", cmdPanel);
      // add("Center", netPanel);
      add("South", editPanel);
    }
   // public void init(){
   //     Toolkit t = Toolkit.getDefaultToolkit();
//	Dimension d = t.getScreenSize();
	//d.width = d.width / 2;
	//d.height = d.height * 3 / 4;
	// editor= new EditorFrame();
	//resize(d);
	//show();
    //}

    void Open_Action() {
	if (elvira.is_applet) return;
	OpenFileDialog.show();
    	String filename = OpenFileDialog.getFile();
    	if (filename == null) return;
        filename = OpenFileDialog.getDirectory() + filename;

        if (elvira.open(filename) == true) {
	    //System.out.println("\tFile loaded.\n\n");
	    setTitle(filename);
            elvira.appendText("\tFile loaded.\n\n");
	}
        else
	    //System.out.println("\tFile not loaded correctly.\n\n");
            elvira.appendText("\tFile not loaded correctly.\n\n");
    }
    
    void Save_Action() {
	if (elvira.is_applet) return;
	/*elvira.appendText(current_save_filename);*/
        if (current_save_filename == null)
            SaveAs_Action();
        else {
            elvira.save(current_save_filename);
            elvira.appendText("\tFile saved.\n\n");
        }
    }
    
    void SaveAs_Action() {
	if (elvira.is_applet) return;
	SaveFileDialog.show();
    	String filename = SaveFileDialog.getFile();
    	if (filename == null) return;
        filename = SaveFileDialog.getDirectory() + filename;
        if (elvira.save(filename) == true)
            elvira.appendText("\tFile saved.\n\n");
        else
            elvira.appendText("\tFile not saved correctly.\n\n");
        current_save_filename = filename;
    }
    
    /* void Clear_Action() {
	(new ClearDialog(this, this,
		"Clear the Bayesian network?", true)).show();
    }*/
    
    void Hide_Action() {
        this.setVisible(false);
    }


/* ******************************************************** *
 * Clear the network screen.                                *
 * ******************************************************** */
void clear() {
/*    setCursor(Frame.WAIT_CURSOR);
    netPanel.clear();
    setCursor(Frame.DEFAULT_CURSOR);
*/
}

 
    public boolean action(Event event, Object arg) {
    if (event.target instanceof MenuItem) {
	String label = (String) ( ((MenuItem)event.target).getLabel());
	    /*if (label.equalsIgnoreCase("Buckets")) {
		Buckets_Action();
		return true;
	    } else
	    if (label.equalsIgnoreCase("Bayesian network")) {
	        BayesianNetwork_Action();
	        return true;
	    } else
	    if (label.equalsIgnoreCase("Posterior expectations")) {
		PosteriorExpectation_Action();
		return true;
	    } else
	    if (label.equalsIgnoreCase("Posterior marginals")) {
		PosteriorMarginal_Action();
		return true;
	    } else
	    if (label.equalsIgnoreCase("Find complete explanation")) {
		EstimateBestConfiguration_Action();
		return true;
	    } else
	    if (label.equalsIgnoreCase("Estimate explanatory variables")) {
		EstimateExplanationVariables_Action();
		return true;
	    } else*/
	/*if (label.equalsIgnoreCase("Clear")) {
	    Clear_Action();
	    return true;
	} else*/ 
        /*
		if (label.equalsIgnoreCase("Dump console to file")) {
		DumpConsoleToFile_Action();
	    } else*/
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
	} else/*
	    if (label.equalsIgnoreCase("Open URL...")) {
		Open_URL_Action();
		return true;
	    } 
	    else */
	if (label.equalsIgnoreCase("Hide")) {
	    Hide_Action();
	    return true;
	}/* else
	    if (label.equalsIgnoreCase("About")) {
		About_Action();
		return true;
	    }*/
    }else
    if (event.target instanceof Button) {
	String label =  ((Button)(event.target)).getLabel();
	if ( ((String)arg).equals(evidenceLabel)) {
	    /* netPanel.set_mode(label);*/
	    ElviraHelpMessages.show(ElviraHelpMessages.create_message);
	    setCursor(Cursor.getPredefinedCursor(DEFAULT_CURSOR));
	}
	else if ( ((String)arg).equals(selectMethodLabel)) {
	    /* netPanel.set_mode(label); */
	    ElviraHelpMessages.show(ElviraHelpMessages.move_message);
	    setCursor(Cursor.getPredefinedCursor(MOVE_CURSOR));
	}
	else if ( ((String)arg).equals(doInferenceLabel)) {
	    /* netPanel.set_mode(label); */
	    ElviraHelpMessages.show(ElviraHelpMessages.delete_message);
	    setCursor(Cursor.getPredefinedCursor(HAND_CURSOR));
	}
    	/*else if ( ((String)arg).equals(queryLabel)) {
            set_query_mode();
		}
		else if ( ((String)arg).equals(observeLabel)) {
            set_observe_mode();
		}
	else if ( ((String)arg).equals(editVariableLabel)) {
	    set_edit_variable_mode();
	}
	else if ( ((String)arg).equals(editFunctionLabel)) {
	    set_edit_function_mode();
	}
	else if ( ((String)arg).equals(editNetworkLabel)) {
	    set_edit_network_mode();
	}*/
    }
    return super.action(event, arg);
    }
    /* ******************************************************** *
    * Load an InferenceGraph object into the NetworkPanel.     *
    * ******************************************************** */
   void load(Bnet bn) {
/*        netPanel.load(bn);
*/
   }

   /* ******************************************************** *
   * Interact with menu options: edit variable.               *
   * ******************************************************** */
   void set_edit_variable_mode() {
/*     setCursor(Frame.TEXT_CURSOR);
     netPanel.set_mode(editVariableLabel);
     ElviraHelpMessages.show(ElviraHelpMessages.edit_message);
*/
   }

   /* ******************************************************** *
   * Interact with menu options: edit function.               *
   * ******************************************************** */
   void set_edit_function_mode() {
/*     setCursor(Frame.TEXT_CURSOR);
     netPanel.set_mode(editFunctionLabel);
*/     ElviraHelpMessages.show(ElviraHelpMessages.edit_message);
   }

  /* ******************************************************** *
   * Interact with menu options: edit network.                *
   * ******************************************************** */
   void set_edit_network_mode() {
/*     netPanel.edit_network();
*/
   }
   /* ******************************************************** 
    * Get the Bnet object in the NetworkPanel.		       *
    * ******************************************************** */
   /* Bnet get_bnet() {
     return(netPanel.get_bnet());

   }*/

}


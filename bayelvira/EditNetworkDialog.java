import java.awt.*;
import java.util.*;

class EditNetworkDialog extends Dialog {
   Bnet bnet;

   int displayedNetworkPropertyIndex;
   Panel np, tp, cp, ntcp, authorp, whop, whenp, awwp, vp, FSp, vFp, 
	      lp, llp, lllp, allp, okp;
   Label labelName, labelTitle, labelComment, labelAuthor, labelWhoChanged, 
         labelWhenChanged, labelVersion, labelFSdefault, labelLocked;
   TextField textName, textTitle, textComment, textAuthor, textWhenChanged, 
            textWhoChanged, textVersion, textFSdefault;    
   CheckboxGroup Locked;
   Checkbox yesLocked, noLocked;    
   Button returnButton;

   private final static String NAME = "Name:";
   private final static String TITLE = "Title:";
   private final static String COMMENT = "Comment:";
   private final static String AUTHOR = "Author:";
   private final static String WHOCHANGED = "Who changed:";
   private final static String WHENCHANGED = "When changed:";
   private final static String VERSION = "Version:";
   private final static String FSDEFAULT = "Finite States Default Values:";
   private final static String LOCKED = "Locked:";
   private final static String YESLOCKED = "Yes";
   private final static String NOLOCKED = "No";
   private final static String RETURN = "Close window";


   /**
   * Default constructor for an EditNetworkDialog object.  
   */
     
   public EditNetworkDialog(Frame parent, Bnet bnet) {
      
      super(parent, "Edit Node", true);
      this.bnet = bnet;

      // Compose the frame

         // Panel for the name, title and comment

	   np = new Panel();
      np.setLayout(new BorderLayout());
      labelName = new Label(NAME);
      textName = new TextField(30);
      np.add("West", labelName);
      np.add("Center", textName);
   	
	   tp = new Panel();
	   tp.setLayout(new BorderLayout());
	   labelTitle = new Label(TITLE);
	   textTitle = new TextField(40);
	   tp.add("West",labelTitle);
	   tp.add("Center",textTitle);
   	
	   cp = new Panel();
	   cp.setLayout(new BorderLayout());
	   labelComment = new Label(COMMENT);
	   textComment = new TextField(50);
	   cp.add("West",labelComment);
	   cp.add("Center",textComment);	

	   ntcp = new Panel();
	   ntcp.setLayout(new BorderLayout());
	   ntcp.add("North",np);
	   ntcp.add("Center",tp);
	   ntcp.add("South",cp);
   	
   // Panel for the author, who-changed and when-changed

	   authorp = new Panel();
      authorp.setLayout(new BorderLayout());
      labelAuthor = new Label(AUTHOR);
      textAuthor = new TextField(30);
      authorp.add("West", labelAuthor);
      authorp.add("Center", textAuthor);
   	
	   whop = new Panel();
	   whop.setLayout(new BorderLayout());
	   labelWhoChanged = new Label(WHOCHANGED);
	   textWhoChanged = new TextField(30);
	   whop.add("West",labelWhoChanged);
	   whop.add("Center",textWhoChanged);
   	
	   whenp = new Panel();
	   whenp.setLayout(new BorderLayout());
	   labelWhenChanged = new Label(WHENCHANGED);
	   textWhenChanged = new TextField(30);
	   whenp.add("West",labelWhenChanged);
	   whenp.add("Center",textWhenChanged);	

	   awwp = new Panel();
	   awwp.setLayout(new BorderLayout());
	   awwp.add("North",authorp);
	   awwp.add("Center",whop);
	   awwp.add("South",whenp);
   	
   // Panel for locked, version and FS default states
   	
	   lp = new Panel();
      lp.setLayout(new BorderLayout());
      labelLocked = new Label(LOCKED);
   	
	   llp = new Panel();
	   llp.setLayout(new GridLayout(1,2));
	   Locked = new CheckboxGroup();
	   yesLocked = new Checkbox(YESLOCKED, Locked, bnet.getLocked());
	   noLocked = new Checkbox(NOLOCKED, Locked, bnet.getLocked());
	   llp.add(yesLocked);
	   llp.add(noLocked);
   	
	   lllp = new Panel();
	   lllp.setLayout(new BorderLayout());
	   lllp.add("West",labelLocked);
	   lllp.add("Center",llp);
   	
	   vp = new Panel();
	   vp.setLayout(new BorderLayout());
	   labelVersion = new Label(VERSION);
	   textVersion = new TextField(20);
	   vp.add("West",labelVersion);
	   vp.add("Center",textVersion);
   	
	   FSp = new Panel();
	   FSp.setLayout(new BorderLayout());
	   labelFSdefault = new Label(FSDEFAULT);
	   textFSdefault = new TextField(20);
	   FSp.add("West",labelFSdefault);
	   FSp.add("Center",textFSdefault);	

	   vFp = new Panel();
	   vFp.setLayout(new BorderLayout());
	   vFp.add("North",lllp);
	   vFp.add("Center",vp);
	   vFp.add("South",FSp);    
   	
      // All the network parameters
      allp = new Panel();
      allp.setLayout(new BorderLayout());
      allp.add("North", ntcp);
      allp.add("Center", awwp);
      allp.add("South", vFp);

      // Return buttons
      okp = new Panel();
      okp.setLayout(new FlowLayout(FlowLayout.CENTER));
      returnButton =	new Button(RETURN);
      okp.add(returnButton);

      setLayout(new BorderLayout());
      add("North", allp);
      add("Center", okp);

	   // Pack the whole window
      pack();

      // Initialize values
      fillDialog();
   }


   /**
   * Customized show method
   */
   
   public synchronized void setVisible(boolean b) {
      Rectangle bounds = getParent().getBounds();
      Rectangle abounds = getBounds();

      setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
    	            bounds.y + (bounds.height - abounds.height)/2);

      super.setVisible(b);
   }


   /**
   * Fill the values in the dialog area.                
   */
   
   private void fillDialog() {
      String values[], allValues = "";
      Vector states;      

      // Fill name, title and comment
      textName.setText(bnet.getName());
      textTitle.setText(bnet.getTitle());
      textComment.setText(bnet.getComment());
	
      // Fill author, who-changed and when-changed
      textAuthor.setText(bnet.getAuthor());
      textWhoChanged.setText(bnet.getWhoChanged());
      textWhenChanged.setText(bnet.getWhenChanged());

      // Fill locked, version and FiniteStates default values
      if (bnet.getLocked()) { // locked=true
	      Locked.setSelectedCheckbox(yesLocked);
      }
      else { // locked=false
	      Locked.setSelectedCheckbox(noLocked);
      }
      
      textVersion.setText(String.valueOf(bnet.getVersion()));
      states = (Vector) bnet.getFSDefaultStates();
      
      if (states==null)
	      System.out.println("states was null\n");
	      
      for (int i=0; i<states.size(); i++) {
	      allValues += states.elementAt(i);
	      if (i!=(states.size()-1)) 
	         allValues += ", ";
      }
      
      textFSdefault.setText(allValues);            
   }

   
   /**
   * Handle the observation events.                   
   */
   
   public boolean action(Event evt, Object arg) {
            
      String values[], checkedName;
      

      if (evt.target == returnButton)
	      dispose();
      else 
         if (evt.target == textName) {
	         checkedName = bnet.checkName(textName.getText());
	         if (checkedName != null)
    	         bnet.setName(checkedName);
         }
         else if (evt.target == textTitle) {
	         bnet.setTitle(textTitle.getText());
         }
         else if (evt.target == textComment) {
	         bnet.setComment(textComment.getText());
         } 
         else if (evt.target == textAuthor) {
	         bnet.setAuthor(textAuthor.getText());
         }
         else if (evt.target == textWhoChanged) {
	         bnet.setWhoChanged(textWhoChanged.getText());
         }
         else if (evt.target == textWhenChanged) {
	         bnet.setWhenChanged(textWhenChanged.getText());
         }
         else if (evt.target == yesLocked) {
	         bnet.setLocked(true);
	         Locked.setSelectedCheckbox(yesLocked);
         }
         else if (evt.target == noLocked) {
	         bnet.setLocked(false);
	         Locked.setSelectedCheckbox(noLocked);
         }
         else if (evt.target == textVersion) {
	         Float f = new Float(textVersion.getText());
	         bnet.setVersion(f.floatValue());
         }
         else if (evt.target == textFSdefault) {
	         values = parseValues(textFSdefault.getText());
	         if (values != null)
               bnet.setFSDefaultStates(values);
         }	      
	   
      return true;
   }


   /** 
    * Parse the values stated in the values TextField.  
    */  

   private String[] parseValues(String allValues) {
      
      String token = null, delimiters = " ,\n\t\r";
      StringTokenizer st = new StringTokenizer(allValues, delimiters);
      String vals[] = new String[ st.countTokens() ];
      int i = 0;

      while (st.hasMoreTokens()) {
         vals[i] = bnet.validateValue(st.nextToken());
         i++;
      }
    
      return(vals);
   } 


}  // end of class

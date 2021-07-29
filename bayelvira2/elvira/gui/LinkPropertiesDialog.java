/* LinkPropertiesDialog.java */

package elvira.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.undo.*;
import elvira.*;
import elvira.Elvira;
import java.util.ResourceBundle;

/**
 * A class that produce a Dialog for editing the properties
 * of a selected link
 *
 * @author fjdiez, ratienza
 * @version 0.1
 * @since 22/10/99
 */

public class LinkPropertiesDialog extends javax.swing.JDialog
{

   /**
    * Creates a LinkPropertiesDialog with all its elements and
    * fill the comboBoxes and RadioButtons with the information get
    * from the selected link
    *
    * @param frame The owner of the dialog
    */


    public static String[] relationskind={"Produce", "Favorece", "Se diagnostica", "Es-un", "tiene", "otro", ""};
	public LinkPropertiesDialog(Frame parent)
	{
		super(parent);
		dialogBundle = Elvira.getElviraFrame().getDialogBundle();
		menuBundle = Elvira.getElviraFrame().getMenuBundle();
		int languaje=Elvira.getLanguaje();

	   	switch (languaje) {
		   case 0: explanationBundle = ResourceBundle.getBundle ("elvira/localize/Explanation_sp");
		                         break;
		   case 1: explanationBundle = ResourceBundle.getBundle ("elvira/localize/Explanation");
		                        break;
		}


		// This code is automatically generated by Visual Cafe when you add
		// components to the visual environment. It instantiates and initializes
		// the components. To modify the code, only use code syntax that matches
		// what Visual Cafe can generate, or Visual Cafe may be unable to back
		// parse your Java file into its visual environment.
		//{{INIT_CONTROLS
		setResizable(false);
		setModal(true);
		setTitle(localize(dialogBundle,
		      "LinkProperties.Title.label"));
		getContentPane().setLayout(null);
                setSize(320,250);
		setVisible(false);
		directedRadioButton.setText(localize(dialogBundle,
		      "LinkProperties.Directed.label"));
		directedRadioButton.setActionCommand("Directed");
		getContentPane().add(directedRadioButton);
		directedRadioButton.setBounds(60,24,72,36);
		undirectedRadioButton.setText(localize(dialogBundle,
		      "LinkProperties.Undirected.label"));
		undirectedRadioButton.setActionCommand("Undirected");
		undirectedRadioButton.setEnabled(false);
		getContentPane().add(undirectedRadioButton);
		undirectedRadioButton.setBounds(192,24,108,36);

		nameLabel.setText(localize(explanationBundle,"Name.label"));
		getContentPane().add(nameLabel);
		nameLabel.setBounds(56,70,48,24);
		nameTextField.setEditable(true);
		getContentPane().add(nameTextField);
		nameTextField.setBounds(108,70,168,24);
		relationLabel.setText(localize(explanationBundle,"RelationKind.label"));
		getContentPane().add(relationLabel);
		relationLabel.setBounds(12,100,96,24);
		relationCombo.setBounds(108,100,168,24);
		relationCombo.setEditable(false);
		getContentPane().add(relationCombo);


		parentLabel.setText(localize(dialogBundle,
		      "LinkProperties.Parent.label"));
		getContentPane().add(parentLabel);
		parentLabel.setBounds(48,134,60,24);
		getContentPane().add(parentComboBox);
		parentComboBox.setBounds(108,134,168,24);
		childLabel.setText(localize(dialogBundle,
		      "LinkProperties.Child.label"));
		getContentPane().add(childLabel);
		childLabel.setBounds(48,164,60,26);
		getContentPane().add(childComboBox);
		childComboBox.setBounds(108,164,168,25);
		okButton.setText("OK");
		okButton.setActionCommand("OK");
		getContentPane().add(okButton);
		okButton.setBounds(60,200,100,36);
		cancelButton.setText(localize(dialogBundle,
		      "Cancel.label"));
		cancelButton.setActionCommand("Cancel");
		getContentPane().add(cancelButton);
		cancelButton.setBounds(192,200,100,36);
		//}}

		linkTypeGroup.add(directedRadioButton);
		linkTypeGroup.add(undirectedRadioButton);

                bayesNet = ((NetworkFrame) Elvira.getElviraFrame().getCurrentNetworkFrame()).getEditorPanel().getBayesNet();

		setLocationRelativeTo(Elvira.getElviraFrame());

		//{{REGISTER_LISTENERS
		SymAction lSymAction = new SymAction();
                NSymAction kSymAction = new NSymAction();
                directedRadioButton.registerKeyboardAction(kSymAction, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0,false),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                undirectedRadioButton.registerKeyboardAction(kSymAction, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0,false),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                nameTextField.registerKeyboardAction(kSymAction, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0,false),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                parentComboBox.registerKeyboardAction(kSymAction, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0,false),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                childComboBox.registerKeyboardAction(kSymAction, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0,false),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                relationCombo.registerKeyboardAction(kSymAction, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0,false),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                okButton.registerKeyboardAction(kSymAction, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0,false),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
                cancelButton.registerKeyboardAction(kSymAction, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE,0,false),JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		okButton.addActionListener(lSymAction);
                okButton.registerKeyboardAction(lSymAction, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,0,false),JComponent.WHEN_FOCUSED);
		cancelButton.addActionListener(lSymAction);
                cancelButton.registerKeyboardAction(lSymAction, javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,0,false),JComponent.WHEN_FOCUSED);
		//}}
	}

	/**
	 * Creates a new LinkPropertiesDialog with no parent frame
	 */

	public LinkPropertiesDialog()
	{
		this((Frame)null);
	}


	/**
	 * Creates a new LinkPropertiesDialog and set its title
	 */

	public LinkPropertiesDialog(String sTitle)
	{
		this();
		setTitle(sTitle);
	}


	/**
	 * Creates a new LinkPropertiesDialog setting the link
	 * property
	 */

   public LinkPropertiesDialog(Link link, boolean isEditable)
	{
		this();
		this.link=link;
		setEditableFields (isEditable);
		fillDialog();
	}


	/**
	 * Shows or hide the LinkPropertiesDialog and set its location
	 */

	public void setVisible(boolean b)
	{
		if (b)
			setLocation(50, 50);
		super.setVisible(b);
	}

	static public void main(String args[])
	{
		(new LinkPropertiesDialog()).setVisible(true);
	}

	public void addNotify()
	{
		// Record the size of the window prior to calling parents addNotify.
		Dimension size = getSize();

		super.addNotify();

		if (frameSizeAdjusted)
			return;
		frameSizeAdjusted = true;

		// Adjust size of frame according to the insets
		Insets insets = getInsets();
		setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height);
	}

	// Used by addNotify
	boolean frameSizeAdjusted = false;

	//{{DECLARE_CONTROLS
	javax.swing.JRadioButton directedRadioButton = new javax.swing.JRadioButton();
	javax.swing.JRadioButton undirectedRadioButton = new javax.swing.JRadioButton();
	javax.swing.JLabel nameLabel = new javax.swing.JLabel();
	javax.swing.JTextField nameTextField = new javax.swing.JTextField();
	javax.swing.JLabel relationLabel = new javax.swing.JLabel();
	javax.swing.JComboBox relationCombo = new javax.swing.JComboBox(relationskind);
	javax.swing.JLabel parentLabel = new javax.swing.JLabel();
	javax.swing.JComboBox parentComboBox = new javax.swing.JComboBox();
	javax.swing.JLabel childLabel = new javax.swing.JLabel();
	javax.swing.JComboBox childComboBox = new javax.swing.JComboBox();
	javax.swing.JButton okButton = new javax.swing.JButton();
	javax.swing.JButton cancelButton = new javax.swing.JButton();
	//}}

	ButtonGroup linkTypeGroup = new ButtonGroup();
	Link link;
	Bnet bayesNet;
	ResourceBundle menuBundle, dialogBundle, explanationBundle;



   public void setEditableFields (boolean isEditable) {
	parentComboBox.setEnabled(isEditable);
	childComboBox.setEnabled(isEditable);
   }


   /**
    * <P>Fill the values in the dialog area. First fill the
    * ComboBoxes using the network store in the bayesNet property, and
    * set the parent a child nodes using the information store
    * in the link property. Finally, set the directed or undirected
    * RadioButton selected using this link
    */

   private void fillDialog() {
      String name;
			System.out.println("Relaci�n"+link.getKindofRelation());
		relationCombo.setSelectedItem(link.getKindofRelation());

	   for (int i=0; i<bayesNet.getNodeList().size(); i++) {
	      name = ((Node) bayesNet.getNodeList().elementAt(i)).getTitle();
	      if (name.equals(""))
	         name=((Node) bayesNet.getNodeList().elementAt(i)).getName();

	      parentComboBox.addItem(name);
  	      childComboBox.addItem(name);

  	      String tailn=link.getTail().getTitle();
  	      if (tailn.equals(""))
  	         tailn=link.getTail().getName();

  	      String headn=link.getHead().getTitle();
  	      if (headn.equals(""))
  	         headn=link.getHead().getName();

	      if (name.equals(tailn))
	         parentComboBox.setSelectedIndex(i);
	      else
	         if (name.equals(headn))
	            childComboBox.setSelectedIndex(i);
	   }


      // Fill the type of the link RadioButton group

	   if (link.getDirected())
	      directedRadioButton.setSelected(true);
	   else
	      undirectedRadioButton.setSelected(false);

	}


   /**
    * Manage the action produced in the LinkPropertiesDialog
    */
   
        class NSymAction implements java.awt.event.ActionListener
        {
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == okButton)
				cancelButton_actionPerformed(event);
			else if (object == cancelButton)
				cancelButton_actionPerformed(event);
                        else if (object == parentComboBox)
                                cancelButton_actionPerformed(event);
                        else if (object == childComboBox)
                                cancelButton_actionPerformed(event);
                        else if (object == relationCombo)
                                cancelButton_actionPerformed(event);
                        else if (object == nameTextField)
                                cancelButton_actionPerformed(event);
                        else if (object == directedRadioButton)
                                cancelButton_actionPerformed(event);
                        else if (object == undirectedRadioButton)
                                cancelButton_actionPerformed(event);
		}
        }

	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
			Object object = event.getSource();
			if (object == okButton)
				okButton_actionPerformed(event);
			else if (object == cancelButton)
				cancelButton_actionPerformed(event);
                        else if (object == parentComboBox)
                                cancelButton_actionPerformed(event);
                        else if (object == childComboBox)
                                cancelButton_actionPerformed(event);
                        else if (object == relationCombo)
                                cancelButton_actionPerformed(event);
                        else if (object == nameTextField)
                                cancelButton_actionPerformed(event);
                        else if (object == directedRadioButton)
                                cancelButton_actionPerformed(event);
                        else if (object == undirectedRadioButton)
                                cancelButton_actionPerformed(event);
		}
	}

	/**
	 * <P>Manage the action produced when the OK Button is clicked. There is
	 * two actions to be checked: </P>
	 * <LI>The head and the tail of the link can't be the same.
	 * <LI>The edition of the node do not introduce a cycle in the network
	 */

	void okButton_actionPerformed(java.awt.event.ActionEvent event)
	{
		Node oldHead = link.getHead();
		Node oldTail = link.getTail();

		link.setKindofRelation((String) relationCombo.getSelectedItem());

	   Node newTail = bayesNet.getNodeList().getNodeString((String) parentComboBox.getSelectedItem(),true);
	   Node newHead = bayesNet.getNodeList().getNodeString((String) childComboBox.getSelectedItem(),true);

	   Link newLink;

	   if (parentComboBox.getSelectedIndex()==childComboBox.getSelectedIndex()) {
  	    	ShowMessages.showMessageDialog(
  	    	    ShowMessages.WRONG_LINK, JOptionPane.ERROR_MESSAGE);
  	    	}
	   else {
	      if ((oldHead!=newHead) || (oldTail!=newTail)) {
	         try {
	            bayesNet.removeLink(link);
	         }
	         catch (InvalidEditException iee) {};
  	         if (bayesNet.getLink(newTail, newHead)!=null) {
  	            ShowMessages.showMessageDialog(
  	                     ShowMessages.EXISTS_LINK,
  	                     JOptionPane.ERROR_MESSAGE);
  	         }
	         else if (bayesNet.hasCycle(newTail, newHead)) {
               Object[] names = {newTail.getName(), newHead.getName()};
              	ShowMessages.showMessageDialogPlus(
              	         ShowMessages.CYCLE,
              	         JOptionPane.ERROR_MESSAGE, names);
  	            try {bayesNet.createLink(oldTail, oldHead);} catch (InvalidEditException iee) {};
  	         }
  	         else {
  	            newLink=new Link(newTail, newHead);
  	            newLink.setKindofRelation((String) relationCombo.getSelectedItem());
  	            try {bayesNet.removeRelation(oldHead);
  	                 bayesNet.createLink(newTail, newHead);
  	                 bayesNet.addRelation(newTail);
  	                 } catch (InvalidEditException iee) {};
  	            ((NetworkFrame) Elvira.getElviraFrame().getCurrentNetworkFrame()).getEditorPanel().repaint();

            	ChangeLinkEdit changeLinkAction = new ChangeLinkEdit(link,newLink);
	            Elvira.getElviraFrame().getUndoItem().setText(changeLinkAction.getUndoPresentationName());
	            Elvira.getElviraFrame().getRedoItem().setText(changeLinkAction.getRedoPresentationName());
	            Elvira.getElviraFrame().getCurrentEditorPanel().getUndoManager().addEdit(changeLinkAction);
	            Elvira.getElviraFrame().enableUndo(true);

   	         }
	      }
	     }

	dispose();
	}

	void cancelButton_actionPerformed(java.awt.event.ActionEvent event)
	{
		dispose();
	}

   public String localize (ResourceBundle bundle, String name) {
      return ElviraFrame.localize(bundle, name);
   }



	class ChangeLinkEdit extends AbstractUndoableEdit {
      Link oldLink, newLink;

	   public ChangeLinkEdit (Link ol, Link nl) {
	      super();
	      oldLink = ol;
	      newLink = nl;
	   }

	   public void undo() throws CannotUndoException {
	      super.undo();
	      try {
	         bayesNet.removeLink(newLink);
            bayesNet.createLink(oldLink.getTail(),oldLink.getHead());
         } catch (InvalidEditException e) { }
         Elvira.getElviraFrame().getCurrentEditorPanel().updateUndoRedo();
	   }

	   public void redo() throws CannotRedoException {
	      super.redo();
	      try {
	         bayesNet.removeLink(oldLink);
            bayesNet.createLink(newLink.getTail(),newLink.getHead());
         } catch (InvalidEditException e) { }
	      Elvira.getElviraFrame().getCurrentEditorPanel().updateUndoRedo();
	   }

	   public String getUndoPresentationName() {
	      return localize (menuBundle, "Edit.Undo.label")+" "+
	             localize(menuBundle, "Action.changeLink.label");
	   }

	   public String getRedoPresentationName() {
	      return localize (menuBundle, "Edit.Redo.label")+" "+
	             localize(menuBundle, "Action.changeLink.label");
	   }

	}

}
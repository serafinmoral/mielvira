import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClearDialog extends Dialog implements ActionListener{
    ElviraFrame ed;
  
    private java.awt.Button yesButton;
    private java.awt.Button noButton;
  

    public ClearDialog(Frame parent, ElviraFrame editor,
	                   String title, boolean modal) {
	super(parent, title, modal);
	ed = editor;
	
	setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
	addNotify();
	//resize(getInsets().left + getInsets().right + 295,getInsets().top + getInsets().bottom + 92);
	setSize(getInsets().left + getInsets().right + 295,getInsets().top + getInsets().bottom + 92);
	yesButton = new java.awt.Button("Yes");
	yesButton.setBounds(getInsets().left + 51,getInsets().top + 20,60,40);
	add(yesButton);
	noButton = new java.awt.Button("No");
	noButton.setBounds(getInsets().left + 165,getInsets().top + 20,60,40);
	add(noButton);
	setResizable(false);
	yesButton.addActionListener(this);
	noButton.addActionListener(this);
	//}}
    }

    public synchronized void setVisible(boolean b) {
    	Rectangle bounds = getParent().getBounds();
    	Rectangle abounds = getBounds();

    	setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
    	     bounds.y + (bounds.height - abounds.height)/2);

    	super.setVisible(b);
    }

    //public boolean handleEvent(Event event) {
   /* public void processEvent(AWTEvent event) {
	if (event.target == noButton && event.id == Event.ACTION_EVENT) {
	    dispose();
	}
	if (event.target == yesButton && event.id == Event.ACTION_EVENT) {
            ed.clear();
            dispose();
	}
	return super.handleEvent(event);
    }*/
    public void actionPerformed(ActionEvent e) {
      if(e.getActionCommand().equals("No"))
	dispose();
      else if(e.getActionCommand().equals("Yes")){
	ed.clear();
	dispose();
      }
    }
}

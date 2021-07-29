import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class QuitDialog extends Dialog implements ActionListener{
    // Variables
   ElviraFrame parentFrame;

   java.awt.Button yesButton;
   java.awt.Button noButton;
    
   
   void yesButtonClicked() {	   
	   dispose();
	   System.exit(0);
   }

   void noButtonClicked() {
	   dispose();
   }

   public QuitDialog(ElviraFrame parent, String title, boolean modal) {
      
	   super(parent, title, modal);
	   parentFrame = parent;

	
	   setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
	   addNotify();
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
	
   }
    
   public synchronized void setVisible(boolean b) {
    	Rectangle bounds = getParent().getBounds();
    	Rectangle abounds = getBounds();

    	setLocation(bounds.x + (bounds.width - abounds.width)/ 2,
    	     bounds.y + (bounds.height - abounds.height)/2);
    	super.setVisible(b);
   }

    /*public boolean handleEvent(Event event) {
	if (event.target == noButton && event.id == Event.ACTION_EVENT) {
	    noButton_Clicked(event);
	}
	if (event.target == yesButton && event.id == Event.ACTION_EVENT) {
	    yesButton_Clicked(event);
	}
	return super.handleEvent(event);
    }*/
    
    
   public void actionPerformed(ActionEvent e) {
      if(e.getSource()==noButton)
	      noButtonClicked();
      else if(e.getSource()==yesButton){
	      yesButtonClicked();
      }
   }
}

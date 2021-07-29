import java.awt.*;
import java.util.*;
import java.awt.event.*;

/**
 * This class implements the dialog window used to edit
 * the case properties and all the methods necessary to handle
 * the events produced on it
 */

public class EditCasePropertiesDialog extends Dialog implements ActionListener, ItemListener {

    TextField name, title, comment;
    TextField author, who, when;
    TextField version;
    CheckboxGroup locked;
    Checkbox yesLocked, noLocked;
    Button ok;
    Label titleDialog;
    Evidence evidence;

  /**
   * Default constructor
   * 
   * @param f Is the parent's frame of this dialog window
   * @param evidence Contain the evidence
   */

  public EditCasePropertiesDialog(ElviraFrame f, Evidence evidence){

    super(f,true);
    this.evidence = evidence;

    setLayout(new BorderLayout());

    // set the title
    
    titleDialog = new Label("Edit Evidence Case Properties",Label.CENTER);
    add("North",titleDialog);

    // set the data

    // first three textfield
    
    Panel data1 = new Panel();
    data1.setLayout(new BorderLayout());

    Panel pname = new Panel();
    pname.setLayout(new BorderLayout());
    Label lname = new Label("Name: ");
    name = new TextField(evidence.getName(),60);
    name.addActionListener(this);
    pname.add("West",lname);
    pname.add("Center",name);

    Panel ptitle = new Panel();
    ptitle.setLayout(new BorderLayout());
    Label ltitle = new Label("Title: ");
    title = new TextField(evidence.getTitle(),60);
    title.addActionListener(this);
    ptitle.add("West",ltitle);
    ptitle.add("Center",title);

    Panel pcomment = new Panel();
    pcomment.setLayout(new BorderLayout());
    Label lcomment = new Label("Comment: ");
    comment = new TextField(evidence.getComment(),60);
    comment.addActionListener(this);
    pcomment.add("West",lcomment);
    pcomment.add("Center",comment);

    data1.add(pname,"North");
    data1.add(ptitle,"Center");
    data1.add(pcomment,"South");  

    // second three textfield

    Panel data2 = new Panel();
    data2.setLayout(new BorderLayout());

    Panel pauthor = new Panel();
    pauthor.setLayout(new BorderLayout());
    Label lauthor = new Label("Author: ");
    author = new TextField(evidence.getAuthor(),60);
    author.addActionListener(this);
    pauthor.add("West",lauthor);
    pauthor.add("Center",author);

    Panel pwho = new Panel();
    pwho.setLayout(new BorderLayout());
    Label lwho = new Label("Who Changed: ");
    who = new TextField(evidence.getWhoChanged(),60);
    who.addActionListener(this);
    pwho.add("West",lwho);
    pwho.add("Center",who);

    Panel pwhen = new Panel();
    pwhen.setLayout(new BorderLayout());
    Label lwhen = new Label("When Changed: ");
    when = new TextField(evidence.getWhenChanged(),60);
    when.addActionListener(this);
    pwhen.add("West",lwhen);
    pwhen.add("Center",when);

    data2.add(pauthor,"North");
    data2.add(pwho,"Center");
    data2.add(pwhen,"South");

    // rest of data

    Panel data3 = new Panel();
    data3.setLayout(new BorderLayout());

    Panel pversion = new Panel();
    pversion.setLayout(new BorderLayout());
    Label lversion = new Label("Version: ");
    version = new TextField(String.valueOf(evidence.getVersion()),60);
    version.addActionListener(this);
    pversion.add("West",lversion);
    pversion.add("Center",version);

    Panel plocked = new Panel();
    plocked.setLayout(new GridLayout(1,2));
    locked = new CheckboxGroup();
    yesLocked = new Checkbox("Yes", locked, (evidence.getLocked()==true));
    noLocked = new Checkbox("No", locked, (evidence.getLocked()==false));
    plocked.add(yesLocked);
    plocked.add(noLocked);
    yesLocked.addItemListener(this);
    noLocked.addItemListener(this);

    Panel pplocked = new Panel();
    pplocked.setLayout(new BorderLayout());
    Label llocked = new Label("Locked: ");
    pplocked.add("West",llocked);
    pplocked.add("Center",plocked);

    data3.add(pversion,"North");
    data3.add(pplocked,"Center");

    // compose and add 

    Panel data = new Panel();
    data.setLayout(new FlowLayout(FlowLayout.LEFT));
    
    data.add("North",data1);
    data.add("Center",data2);
    data.add("South",data3);

    add("Center",data);

    // OK Button
    
    ok = new Button("OK");
    ok.addActionListener(this);
    
    add("South",ok);
         
  }


  /**
   * Handle event
   * 
   * @param evt Event to handle
   */
  

  public void actionPerformed(ActionEvent evt) {
    
    Object source = evt.getSource();

    if (source == ok){
       dispose();
    }
    else if (source == name) {
      evidence.setName(name.getText());
    }
    else if (source == title) {
      evidence.setTitle(title.getText());
    }
    else if (source == comment) {
      evidence.setComment(comment.getText());
    }
    else if (source == author) {
      evidence.setAuthor(author.getText());
    }
    else if (source == who) {
      evidence.setWhoChanged(who.getText());
    }
    else if (source == when) {
      evidence.setWhenChanged(when.getText());
    }
    else if (source == version) {
      Float f = new Float(version.getText());
      evidence.setVersion(f.floatValue());
    }
  }
  
  

  public void itemStateChanged(ItemEvent evt){
    Object source = evt.getSource();

    if (source == yesLocked) {
      evidence.setLocked(true);
      locked.setSelectedCheckbox(yesLocked);
    }
    else if (source == noLocked) {
      evidence.setLocked(false);
      locked.setSelectedCheckbox(noLocked);
    }

  }

} // end of class 
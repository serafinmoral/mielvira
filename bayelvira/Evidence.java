import java.util.Vector;
import FiniteStates;
import java.io.*;
import EvidenceParse;
import NodeList;


/**
 * Class : Evidence. Implements the observations over a network.
 *
 * Last modified : 20/04/99
 */

public class Evidence extends Configuration  {


String  name;
String  title;
String  comment;
String  author;
String  whoChanged;
String  whenChanged;
String  networkName;
boolean locked;
float   version;


static String FiniteStatesDefaultStates[]={"absent","present"};

/**
 * Just to check the class.
 */

public static void main(String args[]) throws ParseException, IOException
{ 
  Bnet b;
  Evidence e;
  FileInputStream f, f1;
  FileWriter f2;
  
  Vector var; //Quitar
  DSeparation dsep;
  int i;
  
  f = new FileInputStream("ejemplo.elv");
  b = new Bnet(f);
  
  f1 = new FileInputStream("ejemplo.evi");
  e = new Evidence(f1,b.getNodeList());
  f1.close(); 
  
  dsep=new DSeparation(b,e);  // Quitar
  var=dsep.allAffecting(b.getNodeList().getId("Coma"));
  System.out.println("Numero variables afectadas: "+var.size());
  for(i=0;i<var.size();i++) {
    System.out.println("Nodo: "+((FiniteStates)(var.elementAt(i))).getName());
  }
  
  
  //f2 = new FileOutputStream("salida.evi");
  f2=new FileWriter("salida.evi");
  e.save(f2);
  f2.close();
}  


/**
 * Construct an empty evidence.
 */

Evidence() {

  variables = new Vector();
  values = new Vector();
  setName("");
  setTitle("");
  setComment("");
  setAuthor("");
  setWhoChanged("");
  setWhenChanged("");
  setLocked(false);
  setVersion((float)1.0);
  setNetworkName("");
}


/**
 * Construct an empty evidence.
 */

Evidence(Configuration conf) {

  variables = (Vector) conf.getVariables().clone();
  values = (Vector) conf.getValues().clone();
  setName("");
  setTitle("");
  setComment("");
  setAuthor("");
  setWhoChanged("");
  setWhenChanged("");
  setLocked(false);
  setVersion((float)1.0);
  setNetworkName("");
}


/**
 * Constructs a new evidence reading from a file, given a list
 * of all possible variables.
 * @param f the input file.
 * @param list the list of all possible variables.
 */

Evidence(FileInputStream f, NodeList list)
    throws ParseException ,IOException
{			     
  EvidenceParse parser = new EvidenceParse(f);
  
  parser.initialize(list);
 
  parser.CompilationUnit();
  translate(parser);  
}


/**
 * Saves an evidence to a file.
 * @param f the file.
 */

void save(FileWriter f) throws IOException
  
{
  PrintWriter p;
  
  p = new PrintWriter(f);
  
  p.print("// Evidence case \n");
  p.print("//   Elvira format \n\n");
  
  
  p.print("evidence  "+getName()+" { \n\n");
  
  saveAux(p);
  p.print ("}\n");        
}


/**
 * Used by save.
 * @param p the PrintWriter where the evidence will be written.
 */

void saveAux(PrintWriter p) throws IOException { 
 
  int i,j,k;
  String n;

  p.print("// Evidence Properties\n\n");

  if (!getTitle().equals(""))
    p.print("title = \""+ getTitle()+"\";\n");

  if (!getAuthor().equals(""))
    p.print("author = \""+ getAuthor()+"\";\n");

  if (!getWhoChanged().equals(""))
    p.print("whoChanged = \""+ getWhoChanged()+"\";\n");

  if (!getWhenChanged().equals(""))
    p.print("whenChanged = \""+ getWhenChanged()+"\";\n");

  if (!getComment().equals(""))
    p.print("title = \""+ getComment()+"\";\n");

  if (!getNetworkName().equals(""))
    p.print("networkName = \""+ getNetworkName()+"\";\n");

  if (getLocked())
    p.print("locked = true;\n");

  p.print("version = "   +getVersion() +";\n\n"); 

  j = values.size();
 
  for(i=0 ; i<j ; i++) {
    p.print( ((Node) variables.elementAt(i)).getName()+" = ");
    
    n = new String((String) 
		   ((FiniteStates)variables.elementAt(i)).
		   getState(((Integer)
			     values.elementAt(i)).intValue()));
    try {
      k = Integer.parseInt(n);
      p.print("\""+n+"\"");
    }
    catch (NumberFormatException e)
      {p.print(n);}
    p.print(",\n");	 
  }
}

/* ********* Accessing methods ************** */

void translate(EvidenceParse parser) {   
   
    Float F;
   
    setName(parser.Name);
    setTitle(parser.Title);
    setComment(parser.Comment);
    setAuthor(parser.Author);
    setWhoChanged(parser.WhoChanged);
    setWhenChanged(parser.WhenChanged);
    setNetworkName(parser.NetworkName);
    setVersion(F = new Float(parser.version));
    values = parser.C.values;
    variables = parser.C.variables;  
}


String getName() {
  return name;
}

String getTitle() {
  return title;
}

String getComment() {
  return comment;
}

String getAuthor() {
  return author;
}

String getWhoChanged() {
  return whoChanged;
}

String getWhenChanged() {
  return whenChanged;
}

String getNetworkName() {
  return networkName;
}

boolean getLocked() {
  return locked;
}

float getVersion() {
  return version;
}


/* ************** Modifiers *************** */

void setName(String s) {
  name=new String(s);
}

void setTitle(String s) {
  title=new String(s);
}

void setComment(String s) {
  comment=new String(s);
}

void setAuthor(String s) {
  author=new String(s);
}

void setWhoChanged(String s) {
  whoChanged=new String(s);
}

void setWhenChanged(String s) {
  whenChanged=new String(s);
}

void setVersion(float s) {
 version = s;
}


void setVersion(Float v) {
  version=v.floatValue();
}


void setLocked(boolean b) {
 locked = b;
}

void setNetworkName(String s) {
   networkName = new String(s);
}

  
  /**
   * Check if the nodes in the evidence are coherent with the nodes in the network
   * 
   * @param bnet Contains the nodes of the network to check
   * @return True if the nodes in the evidence are coherent with the nodes in the network
   * False if not
   */  

  boolean coherentEvidence(Bnet bnet){
    int sizeEvidence;
    int i,j;
    boolean result=true;

    sizeEvidence=((Vector)variables).size();
    for(i=0; i<sizeEvidence; i++){
      FiniteStates fs = getVariable(i);
      if ( (j=bnet.getNodeList().getId(fs.getName()))==-1){
        result=false;
        break;
      } 
      else if ( fs.getNumStates() !=
                 ((FiniteStates)bnet.getNodeList().elementAt(j)).getNumStates()){
             result=false;
             break;
      } 
    }
    
    return result;
  }


   /**
   * This method is used to know if the current evidence is observed
   * 
   * @param node Node whose evidence is observed or not
   * @return True if the evidence is observed
   * False if not
   */
   
   boolean isObserved(Node node) {
      if(indexOf(node)<0)
         return(false);
      else
         return(true);
   }

}

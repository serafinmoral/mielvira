/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.learning;

import elvira.Bnet;
import elvira.NodeList;
import elvira.Node;
import elvira.Graph;
import java.util.Vector;
import java.util.Random;
import java.io.*;


/**
 *
 * @author smc
 */
public class TestBayes {
    private String Name;
    private String[] Networks;
    private int Nsizes;
    private int Nabs;
    private int Nrel;
    private int Nnets;
    private int[] sizes,abs,rel;
    private String[] ssizes,sabs,srel;
    private int Ncouples0,Ncouples1,Ncouples2;
    
    
    private  boolean Simulatevar = false;
    private  boolean Simulatedat = false;
    private  boolean Compute = false;
    
    private Random generator;
    
    
    
 public static void main(String args[])throws elvira.parser.ParseException , IOException  { 

      String argument; 
      TestBayes experiment;
     
      if(!(args.length == 1)){
      System.out.println("Only one argument with the name of the experiment. A directory with the experiment data should be created");
      System.exit(0);
      }

      argument = args[0];
      experiment = new TestBayes(argument);
      
      if(experiment.Simulatevar){
          experiment.getCouples();
          
      }
    

      
   }  
    
    
    
    
    
    
    
    
    public TestBayes(String s)throws IOException  {
      File Data;
      BufferedReader Input;
      String line,operand,value;
      String[] Div;
      int i;
      
      
        generator = new Random();
        Name = s.trim();
        Data = new File( Name.concat("/data") );
        Input = new BufferedReader(new FileReader(Data));
        while (Input.ready()) {
            line = Input.readLine();
            Div = line.split("=",2);
            operand = Div[0].trim();
            value = Div[1].trim();
            if (operand.equals("nets")){
                Networks = value.split("\\s+");
                Nnets = Networks.length;
            }
            else if (operand.equals("samplesizes")){
                ssizes = value.split("\\s+");
                Nsizes = ssizes.length;
                sizes = new int[Nsizes];
                for (i=0;i<Nsizes;i++) { sizes[i] = Integer.parseInt(ssizes[i]);}
            }
            else if (operand.equals("sabs")){
                sabs = value.split("\\s+");
                Nabs = sabs.length;
                abs = new int[Nabs];
  //                 for (i=0;i<Nabs;i++) { System.out.println(sabs[i]);}
                for (i=0;i<Nabs;i++) { abs[i] = Integer.parseInt(sabs[i]);}
            }
            else if (operand.equals("srel")){
                srel = value.split("\\s+");
                Nrel = srel.length;
                rel = new int[Nrel];
                for (i=0;i<Nrel;i++) { rel[i] = Integer.parseInt(srel[i]);}
            }
            else if (operand.equals("couples0")){
                Ncouples0  = Integer.parseInt(value);
            } 
              else if (operand.equals("couples1")){
                Ncouples1  = Integer.parseInt(value);
            } 
              else if (operand.equals("couples2")){
                Ncouples2  = Integer.parseInt(value);
            } 
               else if (operand.equals("simulatevar")){
                Simulatevar  = Boolean.valueOf(value);
            } 
               else if (operand.equals("simulatedat")){
                Simulatedat  = Boolean.valueOf(value);
            } 
               else if (operand.equals("compute")){
                Compute  = Boolean.valueOf(value);
            } 
        }
        
        
    }
    
   static public boolean deleteDirectory(File path) {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           deleteDirectory(files[i]);
         }
         else {
           files[i].delete();
         }
      }
    }
    return( path.delete() );
  }   
    
public void getCouples() throws elvira.parser.ParseException ,IOException  {
    int i;
    boolean status;
    File directory;
    Bnet Ornet;
    
     directory = new File(Name.concat("/couples")) ;
     status = true;
     if(!directory.exists()) {
     status = new File(Name.concat("/couples")).mkdir();
     }
     else {
       System.out.println("Directory couples exists. Removing it");
       deleteDirectory(directory);
        status = new File(Name.concat("/couples")).mkdir();
     }
     
     if(!status) {
         System.out.println("Directory couples can not be created");
         System.exit(0);
     }
     
     
     for(i=0;i<Nnets;i++) {
         Ornet = new Bnet(Name.concat("/").concat(Networks[i]).concat(".elv"));
         Ornet.save("file");
         getCouples0(Ornet,Networks[i]);   
//         getCouples1(Ornet);   
//         getCouples2(Ornet);    
     }
     
    
}    
    

public void getCouples0(Bnet Ornet, String fileName)  throws IOException  {
    int i,j,k,nnodes,ncouples;
    NodeList l,indep,one,two,dep;
    Node n1,n2;
     FileWriter f;
  PrintWriter p;

 
 
    
    one = new NodeList();
    two = new NodeList();
    l = Ornet.getNodeList();
    nnodes = l.size();
    for (i=0;i<nnodes;i++) {
        n1 = l.elementAt(i);
        indep = Ornet.getIndep(n1);
        for (j=0;j<indep.size();j++) {
            n2 = indep.elementAt(j);
            one.insertNode(n1);
            two.insertNode(n2);
        }
    }
    
   ncouples = one.size();
   if (ncouples == 0) {System.out.println("It is not possible to generate independent couples in " + Networks[i] + " network ");}
   else {
        System.out.println(ncouples);
              System.out.println(Ncouples0);
        f = new FileWriter(Name.concat("/couples/").concat(fileName).concat("i0"));
        p = new PrintWriter(f);
        for(i=0;i<Ncouples0;i++) {
            k = generator.nextInt(ncouples);
            n1 = one.elementAt(k);
            n2 = two.elementAt(k);
            p.print(n1.getName()+" "+n2.getName() +"\n");
            
        }
        
        
     f.close();
   }
    
        
        one = new NodeList();
        two = new NodeList();
     for (i=0;i<nnodes;i++) {
           n1 = l.elementAt(i);
           dep  = Ornet.parents(n1);
           for (j=0;j<dep.size();j++) {
               n2 = dep.elementAt(j);
               one.insertNode(n1);
               two.insertNode(n2);
           }  
              dep  = Ornet.children(n1);
           for (j=0;j<dep.size();j++) {
               n2 = dep.elementAt(j);
               one.insertNode(n1);
               two.insertNode(n2);
           }  
           
     } 
        
    ncouples = one.size();
   if (ncouples == 0) {System.out.println("It is not possible to generate dependent couples in " + Networks[i] + " network ");}
   else {
        System.out.println(ncouples);
              System.out.println(Ncouples0);
        f = new FileWriter(Name.concat("/couples/").concat(fileName).concat("d0"));
        p = new PrintWriter(f);
        for(i=0;i<Ncouples0;i++) {
            k = generator.nextInt(ncouples);
          
            n1 = one.elementAt(k);
            n2 = two.elementAt(k);
            p.print(n1.getName()+" "+n2.getName() +"\n");
              System.out.println(k+n1.getName()+" "+n2.getName() );
        }
        
        
     f.close();
   }       
        
        
        
    
}

}

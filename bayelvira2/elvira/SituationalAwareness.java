/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package elvira;


import elvira.parser.ParseException;
import elvira.potential.*;
import elvira.learning.DynamicLearning;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import elvira.CaseListMem;
import static elvira.DynamicNetwork.computeIndex;
import static elvira.DynamicNetwork.locate;
import static elvira.DynamicNetwork.locaten;
import elvira.learning.DynamicLearning;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;



/**
 *
 * @author smc
 */
public class SituationalAwareness {
    
    private DynamicNetwork correctFlight;
    private int nSA;
    private Vector namesSA;
    private Vector groupsActionsString;
    private Vector groupsActionsVar;
    private Vector groupsActionsArray;
    private Vector partialDNs;
    private Vector SAvalues;
    private double[] contyy;
    private double[] contnn;
    private double[] prior;
    private NodeList initialnodes;
    
    SituationalAwareness ()  {
        namesSA = new Vector();
        groupsActionsString = new Vector();
        groupsActionsVar = new Vector();
        groupsActionsArray = new Vector();
        partialDNs= new Vector();
        SAvalues = new Vector();
    }
     
     
     static void Experiment(String directory, String description, String output) throws IOException {

         
        SituationalAwareness SA;
        String line;
        String variables;
        int n;
        
        String[] fls;
        String[] parts;
       
        String test;
        PrintStream ps;
        PrintWriter psred;
        int i, nfls;
      
        DynamicLearning learn;
       

         SA = new SituationalAwareness();
        
        directory.trim();
        description.trim();
        output.trim();
        
        
        BufferedReader reader = new BufferedReader(new FileReader(directory + "/" + description));

        line = reader.readLine();

        variables = line.trim();
        
        line = reader.readLine();
        n = Integer.parseInt(line);
        
      
        
        
        SA.nSA = n;
        SA.contnn = new double[n];
        SA.contyy  = new double[n];
        SA.prior = new double[n];
      
      
        
        for (i=0;i<n;i++) {
            line = reader.readLine();
            SA.namesSA.add(line.trim());
            line = reader.readLine();
            fls = line.split("\\s*,\\s*");
            SA.groupsActionsString.add(fls);
            line= reader.readLine();
           
            parts = line.split("\\s*,\\s*");
            SA.prior[i] = Double.parseDouble(parts[0]);
            SA.contyy[i] = Double.parseDouble(parts[1]);
            SA.contnn[i] = Double.parseDouble(parts[2]);
            
        }
        
        line = reader.readLine();
         

        fls = line.split("\\s*,\\s*");

        nfls = fls.length;
        
         line = reader.readLine();
         test = line.trim();
        
        learn = new DynamicLearning(directory, variables, fls, 1,0);
        
        
        SA.initialnodes = learn.getNodes();
        
    
        
         learn.learning();
         learn.estimateParameters();
        
        
        
         
        SA.correctFlight  = learn.getOutput();
         SA.groupstoVar();
         
        for (i=0;i<n;i++) {
            
          learn = new DynamicLearning(directory, variables, fls, 1,0);
          
          learn.learning(((NodeList) SA.groupsActionsVar.elementAt(i)));
          learn.estimateParameters();
           
          SA.partialDNs.add(learn.getOutput());
      
        }
        
          
          
        
        SA.computeSA(directory,test,output );
}
     
     
     public double initialSA(int i, int[] values, int[] values2, Vector totalvars) {
         double y=prior[i];
         double ny = 1.0-y;
         Node var,var2;
         boolean[] savar= ((boolean[]) groupsActionsArray.elementAt(i)); 
         PotentialTable pot,pot2;
         int[] vartypes;
         int index,index2;
         int val,val2;
         DynamicNetwork saFlight = (DynamicNetwork) partialDNs.elementAt(i);
         
         int si = values.length;
         
         vartypes = correctFlight.vartypes;
         
         
         
         for(int j=0; j<si; j++) {
             
                if ( (((vartypes[j] == 1) || (vartypes[j] == 3)|| (vartypes[j] == 6)) && savar[j])){
                    
                  
                        
                    pot = (PotentialTable) correctFlight.time0potentials[j];
                    pot2 = (PotentialTable) saFlight.time0potentials[j];
                   
                    var = (Node) pot.getVariables().elementAt(0);
                    var2 = (Node) pot.getVariables().elementAt(0);


                        index = computeIndex(values, pot.getVariables(), totalvars);
                        index2 = computeIndex(values2, pot2.getVariables(), totalvars);
                        
                        System.out.println("initial SA "  + "Potential correct " + pot.getValues()[index] + "Potential SA " + pot2.getValues()[index2]);
                        y *= pot.getValues()[index];
                        ny *= pot2.getValues()[index2];
         
                        if (var.getTypeOfVariable() == Node.MIXED) {
                            index = locate(var, totalvars);
                            index2 = locate(var2,totalvars);
                            val = values[index];
                            val2 = values2[index2];
                            y /= (((ContinuousDiscrete) var).getLimit(val + 1) - ((ContinuousDiscrete) var).getLimit(val));
                           ny /= (((ContinuousDiscrete) var2).getLimit(val2 + 1) - ((ContinuousDiscrete) var2).getLimit(val2));
                        }
                    }
                }
            
             
         y = (y/(y+ny));
         return y;
     }
             
         
         
      
     public double instantSA(int i, int[] values, int[] ovalues, int[] values2, int[] ovalues2, double osa, Vector totalvars) {
         double y;
         double ny;
         Node var,var2;
         boolean[] savar= ((boolean[]) groupsActionsArray.elementAt(i)); 
         PotentialTable pot,pot2;
         int[] vartypes;
         int index,index2;
         int val,val2;
         DynamicNetwork saFlight = (DynamicNetwork) partialDNs.elementAt(i);
         
         int si = values.length;
         
         vartypes = correctFlight.vartypes;
         
         y = osa*contyy[i] + (1-osa)*(1-contnn[i]);
         
         ny = 1-y;
         
         for(int j=0; j<si; j++) {
                if ( (((vartypes[j] == 1) || (vartypes[j] == 3)|| (vartypes[j] == 6)) && savar[j])){
             
                    pot = (PotentialTable)   (PotentialTable) correctFlight.temporalpotentials[j];
                    pot2 = (PotentialTable) saFlight.temporalpotentials[j];
                   
                    var = (Node) pot.getVariables().elementAt(0);
                    var2 = (Node) pot2.getVariables().elementAt(0);

                    System.out.println("Variable " + var.getName());
                      System.out.println("Variable " + var2.getName());

                        index = computeIndex(ovalues,values, pot.getVariables(), totalvars);
                        index2 = computeIndex(ovalues2,values2, pot2.getVariables(), totalvars);
                        
                        System.out.println("Indexes " + index + " , " + index2);
                        
                        y *= pot.getValues()[index];
                        ny *= pot2.getValues()[index2];
         
                          System.out.println("instant SA "  + "Potential correct " + pot.getValues()[index] + "Potential SA " + pot2.getValues()[index2]);
                        
                        if (var.getTypeOfVariable() == Node.MIXED) {
                            index = locaten(var, totalvars);
                            val = values[index];
                            val2 = values2[index];
                            y /= (((ContinuousDiscrete) var).getLimit(val + 1) - ((ContinuousDiscrete) var).getLimit(val));
                           ny /= (((ContinuousDiscrete) var2).getLimit(val2 + 1) - ((ContinuousDiscrete) var2).getLimit(val2));
               System.out.println("Interval correct "  + (((ContinuousDiscrete) var).getLimit(val + 1) - ((ContinuousDiscrete) var).getLimit(val)) +
                                "Interval sa"  + (((ContinuousDiscrete) var2).getLimit(val2 + 1) - ((ContinuousDiscrete) var2).getLimit(val2)) ); 

                        }
                    }
                }
            
             
         y = (y/(y+ny));
         return y;
     }
                   
     public  void computeSA(String dir, String filei, String fileo)   throws IOException  {
         
        BufferedReader casesreader;
        DynamicNetwork flightsa;
        String line;
        int[] crow, var, crowsa, crowo,crowsao;
        int i, j, nnodes, totaln, l,k;
        String[] parts;
        double[] rawline, rawlineold, coe, rawlinesa,rawlineoldsa;
        Vector<Double> partialsa;
        NodeList nodes, nodessa;
        int[][] crowsaog;
        double[][] rawlineoldsag;
        double[] oldsag;
        
        
        double y,z;
        
        
        casesreader = new BufferedReader(new FileReader(dir + "/" + filei));
        line = casesreader.readLine();
        line = casesreader.readLine();
        
        PrintWriter bw = new PrintWriter(new FileWriter(dir + "/" + fileo));
        
        
        nodes = correctFlight.nodes;
        totaln = nodes.size();
        
        
        parts = line.split(",");
        rawlineold = new double[totaln];
        rawline = new double[totaln];
        crow = new int[totaln];
        rawlineoldsa = new double[totaln];
        rawlinesa = new double[totaln];
        crowsa = new int[totaln];
        crowo = new int[totaln];
        crowsao = new int[totaln];
       
        crowsaog = new int[nSA][totaln];
        rawlineoldsag = new double[nSA][totaln];
        oldsag = new double[nSA];
        
        
        
        
        
        
        correctFlight.span(parts, crow, rawlineold);
        for(j=0;j<crow.length;j++) {crowo[j] = crow[j];}
        
        for(i=0;i<nSA;i++) {
            flightsa = (DynamicNetwork) partialDNs.elementAt(i);
            partialsa = new Vector<Double>();
            flightsa.span(parts, crowsa, rawlineoldsa);
            for(j=0;j<rawlineoldsa.length;j++) 
                     {rawlineoldsag[i][j] = rawlineoldsa[j];}
             for(j=0;j<crowsa.length;j++) 
                     {crowsaog[i][j] = crowsa[j];}
        
            y = initialSA(i,crow,crowsa,nodes.getNodes());
           
            oldsag[i] = y;
            partialsa.add(new Double(y));
            SAvalues.add(partialsa);
        }
        
        
             while ((line = casesreader.readLine()) != null) {
                    System.out.println("New time");
                    parts = line.split(",");
                     correctFlight.span(parts, crow, rawline, rawlineold);
                      for(i=0;i<rawline.length;i++) {rawlineold[i] = rawline[i];}
                         for(i=0;i<nSA;i++) {
                                flightsa = (DynamicNetwork) partialDNs.elementAt(i);
                                 for(j=0;j<rawlinesa.length;j++) {rawlineoldsa[j]= rawlineoldsag[i][j];}
                                flightsa.span(parts, crowsa, rawlinesa, rawlineoldsa);
                                for(j=0;j<rawlinesa.length;j++) {rawlineoldsag[i][j] = rawlinesa[j];}
                                 for(j=0;j<crowsa.length;j++)  { crowsao[j] = crowsaog[i][j];}
                                y = instantSA(i,crow,crowo,crowsa,crowsao, oldsag[i] ,nodes.getNodes());
                                 for(j=0;j<crowsa.length;j++)  { crowsaog[i][j] = crowsa[j];}
                                oldsag[i] = y;
                                 partialsa = (Vector<Double>) SAvalues.elementAt(i);
                                 partialsa.add(new Double(y));
                                
             }
      
          for(j=0;j<crow.length;j++) {crowo[j] = crow[j];}
         
     }
           for(i=0;i<nSA;i++){
               System.out.println("SA " + i);
                partialsa = (Vector<Double>) SAvalues.elementAt(i);
               for(j=0;j<partialsa.size();j++) {
                   System.out.println(partialsa.elementAt(j).doubleValue());
                     bw.print(partialsa.elementAt(j).doubleValue());
                     bw.print("\n");
                    bw.flush();
               }
           }
             
             
             
     }
         
         
   
     
     public void groupstoVar() {
         
         NodeList varsa;
         Node va;
         String nam;
         int i,j,k,l;
         boolean satype[];
         
              
             
             for(i=0;i<nSA;i++) {
                 
                 satype = new boolean[correctFlight.nodes.size()];
                 varsa = new NodeList();
                 String [] listnames = ( (String[]) groupsActionsString.elementAt(i));
                 for(j=0;j<listnames.length;j++) {
                  nam = listnames[j];
                  va = initialnodes.getNode(nam);
                  k = initialnodes.getId(nam);
                  satype[k] = true;
                  varsa.insertNode(va);
                  }
                 for (j=0; j<satype.length; j++)  {
                     if (correctFlight.vartypes[j] == 6) {
                         satype[j] = satype[ ( (int[]) correctFlight.linearvar[j])[1]];
                     }
                     
                 }
                 
                  
            
                 
             groupsActionsVar.add(varsa);
             groupsActionsArray.add(satype);
         }
         
         
     }
     
     public static void main(String[] args) throws IOException {

        SituationalAwareness.Experiment(args[0], args[1], args[2]);
        
        System.out.println("Main final");
        
        System.exit(0);

        // just making sure we have the right version of everything
    }
     
     
}

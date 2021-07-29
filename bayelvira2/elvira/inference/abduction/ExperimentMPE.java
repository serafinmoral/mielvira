/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.inference.abduction;

import elvira.Bnet;
import elvira.Configuration;
import elvira.Evidence;
import elvira.FiniteStates;
import elvira.Node;
import elvira.NodeList;
import elvira.inference.abduction.MPE;
import elvira.parser.ParseException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 *
 * @author Serafin
 */

public class ExperimentMPE {
    
    public static void main(String[] args) throws IOException, ParseException, Throwable {
        
        
        
        
        
        
        
        Evidence evidencia;
        int numero_ejecuciones;
        String filei = args[0];
        String fileo = args[1];
        long tiempo_inicio, tiempo_fin, tiempo_inter;
        int numero_nodos;
        Configuration configuracion_evidencial;
        Vector variables_configuracion, valores_configuracion;
        Vector<Integer> indice_nodos;
        int numero_variables_evidenciales;
        int indice_evidencia;
        Node nodo_evidencial;
        NodeList lista_nodos;
        int indice_nodo_evidencial;
        int numero_estados_posibles;
        ArrayList<String> valores_posibles;
        String estado; 
        int valor_evidencial;
        int indice_valor;
        BufferedReader reader,readerevi,readdel;
        String line;
        String[] parts,partsevi;
        int[] vmethods;
        String[] pmethods;
        int method;
        int format;
        NodeList deletionSequence;
        int nvar;
        long tiempo;
        Bnet red_bayesiana,rbcopy;
        int numero_tamanios;
       
        long tamanio_potencial = 200;
        long [][][] tiempos;
        long suma_tiempos, media_tiempos;
        MPE mpec;
        int numero_opciones = 7;
        int i,j,k;
   
    /*    int n= 100; 
        
        Random generator = new Random();
        
           red_bayesiana =  new Bnet(generator,n,2.0,3,true,1);
       FileWriter  f = new FileWriter("red1.elv");
       red_bayesiana.saveBnet(f);
                f.close();
           
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red2.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
         red_bayesiana =  new Bnet(generator,n,2.0,3,true,1);
         f = new FileWriter("red3.elv");
       red_bayesiana.saveBnet(f);
                f.close();
           
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red4.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
                 
         red_bayesiana =  new Bnet(generator,n,2.0,3,true,1);
        f = new FileWriter("red5.elv");
       red_bayesiana.saveBnet(f);
                f.close();
           
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red6.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
                
          
                
                    
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red7.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
                 
         red_bayesiana =  new Bnet(generator,n,2.0,3,true,1);
        f = new FileWriter("red8.elv");
       red_bayesiana.saveBnet(f);
                f.close();
           
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red9.elv");
       red_bayesiana.saveBnet(f);
                f.close();
      
         
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red10.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
                    
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red11.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
                 
         red_bayesiana =  new Bnet(generator,n,2.0,3,true,1);
        f = new FileWriter("red12.elv");
       red_bayesiana.saveBnet(f);
                f.close();
           
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red13.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
                   
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red14.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
                    
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red15.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
                 
         red_bayesiana =  new Bnet(generator,n,2.0,3,true,1);
        f = new FileWriter("red16.elv");
       red_bayesiana.saveBnet(f);
                f.close();
           
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red17.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                   
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red18.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
                    
          red_bayesiana =  new Bnet(generator,n,1.5,3,true,1);
         f = new FileWriter("red19.elv");
       red_bayesiana.saveBnet(f);
                f.close();
                
                 
         red_bayesiana =  new Bnet(generator,n,2.0,3,true,1);
        f = new FileWriter("red20.elv");
       red_bayesiana.saveBnet(f);
                f.close();
           
       
       */ 
        deletionSequence = new NodeList();
       reader = new BufferedReader(new FileReader(filei));
        
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileo+"t"));
           BufferedWriter bw2 = new BufferedWriter(new FileWriter(fileo+"s"));
  /*      bw.write("Inference Results " + "\n");
        bw.flush();
     */  
           
         line = reader.readLine(); 
         
         format = Integer.parseInt(line);
       
         line = reader.readLine(); 

          String direc = line.trim();
         
         line = reader.readLine(); 
       
         parts = line.split("\\s+");
         int  nnetworks  = parts.length;
       
        
 /*          bw.write("NNetworks = " + nnetworks + "\n");
           bw.flush();
   */      
       line = reader.readLine(); 
       numero_ejecuciones = Integer.parseInt(line);
       
   /*     bw.write("NRuns = " + numero_ejecuciones + "\n");
        bw.flush();
    */   
        line = reader.readLine(); 
        double propevi = Double.parseDouble(line);
        
   /*      bw.write("PertcEvidence = " + propevi + "\n");
         bw.flush();
     */ 
       line   = reader.readLine(); 
       int nmethods = Integer.parseInt(line);
       
       vmethods = new int[nmethods];
       pmethods = new String[nmethods];
       
 /*      bw.write("Nmethods = " + nmethods + "\n");
        bw.flush();
   */    
       
       for(i=0;i<nmethods;i++) {
           
         line = reader.readLine(); 
         vmethods[i] = Integer.parseInt(line);
          pmethods[i] = reader.readLine(); 
         
       }
       
       
       for(i=0;i<nnetworks;i++) {
       variables_configuracion = new Vector();
                  valores_configuracion = new Vector();
          if (format==0)  {
              red_bayesiana = new Bnet(direc+"/"+parts[i]);
          }  
          else {
              partsevi = new String[1];
              red_bayesiana = new Bnet(direc+"/"+parts[i],1);
               if(format == 1) 
               {readerevi = new BufferedReader(new FileReader(direc+"/"+parts[i]+".evid"));
                 line = readerevi.readLine(); 
                  partsevi = line.split("\\s+");
                  numero_variables_evidenciales = Integer.parseInt(partsevi[0]);
               }
               else{numero_variables_evidenciales=0;}
                     variables_configuracion = new Vector();
                  valores_configuracion = new Vector();
                   lista_nodos = red_bayesiana.getNodeList();
                  for (int l = 0; l < numero_variables_evidenciales; l++){
                      System.out.println("Añadiendo Evidencia");
                      nodo_evidencial = lista_nodos.elementAt(Integer.parseInt(partsevi[2*l+1]));  
                      valor_evidencial = Integer.valueOf(partsevi[2*l+2]);
                      variables_configuracion.add(nodo_evidencial);
                      valores_configuracion.add(valor_evidencial);
                  }
             /*   readdel = new BufferedReader(new FileReader(direc+"/"+parts[i]+".ord"));
                deletionSequence = new NodeList();
                  line = readdel.readLine(); 
                  partsevi = line.split("\\s+");
                  nvar = Integer.parseInt(partsevi[0]);
                   for (int l = 1; l <= nvar; l++){
                       deletionSequence.insertNode(lista_nodos.elementAt(Integer.parseInt(partsevi[l])));
                   }
*/
          }
          
          
           
           
            
           numero_nodos = red_bayesiana.getNodeList().size();
           numero_variables_evidenciales = (int) (numero_nodos* propevi);
           lista_nodos = red_bayesiana.getNodeList();
       
           for(j=0; j< numero_ejecuciones; j++) {  
           
              indice_nodos = new Vector();
  
             for(int l = 0; l < numero_nodos; l++)
                indice_nodos.add(new Integer(l));
 
                
                  
                 if (format==0)  { 
                   variables_configuracion = new Vector();
                  valores_configuracion = new Vector();

                for(int l = 0; l < numero_variables_evidenciales; l++){
                indice_evidencia = (int) (Math.random()*indice_nodos.size());
                indice_nodo_evidencial = indice_nodos.get(indice_evidencia).intValue();
                nodo_evidencial = lista_nodos.elementAt(indice_nodo_evidencial);
                variables_configuracion.add(nodo_evidencial);
                indice_nodos.remove(indice_evidencia);
                numero_estados_posibles = ((FiniteStates)nodo_evidencial).getNumStates();
           

                
                indice_valor = (int) (Math.random()*numero_estados_posibles);
                
                valor_evidencial = new Integer(indice_valor);
                valores_configuracion.add(valor_evidencial);
                
            }
                 }

            configuracion_evidencial = new Configuration(variables_configuracion, valores_configuracion);
            evidencia = new Evidence(configuracion_evidencial);
            
            
    for(k=0;k<nmethods;k++) {       
            
        rbcopy = red_bayesiana.copyBnet2();
             mpec = new MPE(rbcopy, evidencia, 1);
             
       switch (vmethods[k]) {
          
          case 1: {
                    mpec = new MPE(rbcopy, evidencia, 1);
                    System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
                /*    bw.write("Network = " + parts[i] + ", Run = " + j + " Method = minibucket, " + " Size = " + parts2[0] + 
                            ", Combinationp = " + parts2[1] + "\n");
                    bw.flush();*/
                    System.gc();
                    tamanio_potencial = Integer.parseInt(parts2[0]);
                     mpec.setLimitSize(tamanio_potencial);
                       
                    tiempo_inicio = System.currentTimeMillis(); 
                    int opt = Integer.parseInt(parts2[1]);
                     mpec.getApproxMaxPotentials2(opt);
                     System.out.println("Approximation finished");
                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeApproxMaxConfigurationss();
     //                System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + " , ");
               //     System.out.println(mpec.getBest().getConf());
                    bw.flush();
                    break;
                       
          }  
                  
          case 2: {
               mpec = new MPE(rbcopy, evidencia, 1);
                    System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
                  /*  bw.write("Network = " + parts[i] + ", Run = " + j + " Method = probtree, " + " Epsilon = " + parts2[0] + 
                            ", Distance = " + parts2[1] + "\n");
                    bw.flush();*/
                    
                    double epsilon = Double.parseDouble(parts2[0]);
                    
                    tiempo_inicio = System.currentTimeMillis(); 
                    int opt = Integer.parseInt(parts2[1]);
                    int opt2 = Integer.parseInt(parts2[2]);
                     mpec.getApproxMaxPotentials3(epsilon,opt2,opt);
                                          System.out.println("Approximation finished");

                     tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeApproxMaxConfigurations();
                  //    System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + " , ");
   //               System.out.println(mpec.getBest().getConf());
                    bw.flush();
                    break;
                       
          }  
            case 3: {
                mpec = new MPE(rbcopy, evidencia, 1);
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
                   /* bw.write("Network = " + parts[i] + ", Run = " + j + " Method = minibucket + Probtree " +
                            " Size = " + parts2[0] + 
                            " Epsilon " + parts2[1] + 
                             " Combinationp " + parts2[2] + 
                            ", Distance = " + parts2[3] + "\n");*/
                    bw.flush();
                    
                   
                    tamanio_potencial = Integer.parseInt(parts2[0]);
                     mpec.setLimitSize(tamanio_potencial);
                     System.gc();
                    tiempo_inicio = System.currentTimeMillis(); 
                    int opt = Integer.parseInt(parts2[2]);
                   double epsilon = Double.parseDouble(parts2[1]);
                    int opt2 = Integer.parseInt(parts2[3]);
                     mpec.getApproxMaxPotentials3s(epsilon,opt2,opt);
                              System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeApproxMaxConfigurationss();
     //                System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + " , ");
           //         System.out.println(mpec.getBest().getConf());
                    bw.flush();
                   
                    break;
                       
          }  
                  
             case 4: {
                   mpec = new MPE(rbcopy, evidencia, 1);
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
                 /*   bw.write("Network = " + parts[i] + ", Run = " + j + " Method = Probtree + updating " +
                            " Epsilon = " + parts2[0] +  
                            ", Combinationp = " + parts2[1] + " , "); */
                    bw.flush();
                   
                  
                  
                    tiempo_inicio = System.currentTimeMillis(); 
                   double epsilon = Double.parseDouble(parts2[0]);
                    int opt = Integer.parseInt(parts2[1]);
                     mpec.getApproxMaxPotentials4(epsilon,opt);
                              System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeMaxConfigurationDynamic();
     //                System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + "\n");
                    System.out.println(mpec.getBest().getConf());
                    bw.flush();
                    break;
                       
          }  
             
               case 5: {
                     mpec = new MPE(rbcopy, evidencia, 1);
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
                    bw.write("Network = " + parts[i] + ", Run = " + j + " Method = Probtree + updating " +
                            " Epsilon = " + parts2[0] +  
                            ", Combinationp = " + parts2[1] + "\n");
                    bw.flush();
                   
                  
                  
                    tiempo_inicio = System.currentTimeMillis(); 
                   double epsilon = Double.parseDouble(parts2[0]);
                    int opt = Integer.parseInt(parts2[1]);
                     mpec.getApproxMaxPotentials4(epsilon,opt);
                              System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeMaxConfigurationDynamic2();
     //                System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + "\n");
                     
                    (mpec.getBest().getConf()).pPrint();
                    bw.flush();
                    break;
                       
          }  
               
                 case 6: {
                       mpec = new MPE(rbcopy, evidencia, 1);
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
                    bw.write("Network = " + parts[i] + ", Run = " + j + " Method = Probtree + updating " +
                            " Epsilon = " + parts2[0] +  
                            ", Combinationp = " + parts2[1] + "\n");
                    bw.flush();
                   
                  
                  
                    tiempo_inicio = System.currentTimeMillis(); 
                   double epsilon = Double.parseDouble(parts2[0]);
                    int opt = Integer.parseInt(parts2[1]);
                     mpec.getApproxMaxPotentials4(epsilon,opt);
                              System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeMaxConfigurationDynamic3();
               //    System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + "\n");
                    
                    (mpec.getBest().getConf()).pPrint();
                    bw.flush();
                    break;
                       
          }  
                 
                    case 7: {
                          mpec = new MPE(rbcopy, evidencia, 1);
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
                    bw.write("Network = " + parts[i] + ", Run = " + j + " Method = minibucket + Probtree + Dynamic " +
                            " Size = " + parts2[0] + 
                            " Epsilon " + parts2[1] + 
                             " Distance " + parts2[2] + 
                            ", Combinationp = " + parts2[3] + "\n");
                    bw.flush();
                   
                    tamanio_potencial = Integer.parseInt(parts2[0]);
                     mpec.setLimitSize(tamanio_potencial);
                    tiempo_inicio = System.currentTimeMillis(); 
                    int opt = Integer.parseInt(parts2[2]);
                   double epsilon = Double.parseDouble(parts2[1]);
                    int opt2 = Integer.parseInt(parts2[3]);
                     mpec.getApproxMaxPotentials5(epsilon,opt2,opt);
                              System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                      mpec.computeMaxConfigurationDynamic4();
     //                System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + "\n");
     //               System.out.println(mpec.getBest().getConf());
                    bw.flush();
                    break;
                       
          } 
                    
                     case 8: {
                           mpec = new MPE(rbcopy, evidencia, 1);
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
                /*    bw.write("Network = " + parts[i] + ", Run = " + j + " Method = minibucket + Probtree + saving" +
                            " Size = " + parts2[0] + 
                            " Epsilon " + parts2[1] + 
                             " Distance " + parts2[2] + 
                            ", Combinationp = " + parts2[3] + "\n");
                    bw.flush(); */
                   
                    tamanio_potencial = Integer.parseInt(parts2[0]);
                     mpec.setLimitSize(tamanio_potencial);
                    tiempo_inicio = System.currentTimeMillis(); 
                    int opt = Integer.parseInt(parts2[2]);
                   double epsilon = Double.parseDouble(parts2[1]);
                    int opt2 = Integer.parseInt(parts2[3]);
                     mpec.getApproxMaxPotentials3s(epsilon,opt,opt2);
                              System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeApproxMaxConfigurationss();
     //                System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + "\n");
          //          System.out.println(mpec.getBest().getConf());
                    bw.flush();
                    break;
                       
          }  
           case 9: {
                 mpec = new MPE(rbcopy, evidencia, 1);
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
             /*       bw.write("Network = " + parts[i] + ", Run = " + j + " Method = minibucket + Probtree + saving + previous solution" +
                            " Size = " + parts2[0] + 
                            " Epsilon " + parts2[1] + 
                             " Distance " + parts2[2] + 
                            ", Combinationp = " + parts2[3] + "\n");
                    bw.flush();*/
                   
                    tamanio_potencial = Integer.parseInt(parts2[0]);
                     mpec.setLimitSize(tamanio_potencial);
                    tiempo_inicio = System.currentTimeMillis(); 
                    int opt = Integer.parseInt(parts2[2]);
                   double epsilon = Double.parseDouble(parts2[1]);
                    int opt2 = Integer.parseInt(parts2[3]);
                     mpec.getApproxMaxPotentials3s(epsilon,opt2,opt);
                     System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeApproxMaxConfigurationssprev();
            //         System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + "\n");
            //       System.out.println(mpec.getBest().getConf());
                    bw.flush();
                    break;
                       
          }  
                             
            case 10: {
                  mpec = new MPE(rbcopy, evidencia, 1);
                 // if (format==1) { mpec.deletionSequence = deletionSequence;}
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
             /*       bw.write("Network = " + parts[i] + ", Run = " + j + " Method = minibucket + Probtree + saving + 
                          previous solution + Deep " +
                            " Size = " + parts2[0] + 
                            " Epsilon " + parts2[1] + 
                             " Distance " + parts2[2] + 
                            ", Combinationp = " + parts2[3] + "\n");
                    bw.flush();*/
                   
                    tamanio_potencial = Integer.parseInt(parts2[0]);
                     mpec.setLimitSize(tamanio_potencial);
                    tiempo_inicio = System.currentTimeMillis(); 
                    int opt = Integer.parseInt(parts2[2]);
                   double epsilon = Double.parseDouble(parts2[1]);
                    int opt2 = Integer.parseInt(parts2[3]);
                     mpec.getApproxMaxPotentials3s(epsilon,opt2,opt);
                     System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeApproxMaxConfigurationssprevdeep();
            //         System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + "\n");
                //   System.out.println(mpec.getBest().getConf());
                    bw.flush();
                    break;
                       
          }         
            case 11: {
                  mpec = new MPE(rbcopy, evidencia, 1);
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
             /*       bw.write("Network = " + parts[i] + ", Run = " + j + " Method = minibucket + Probtree + saving + 
                          previous solution + Deep " +
                            " Size = " + parts2[0] + 
                            " Epsilon " + parts2[1] + 
                             " Distance " + parts2[2] + 
                            ", Combinationp = " + parts2[3] + "\n");
                    bw.flush();*/
                   
                    tamanio_potencial = Integer.parseInt(parts2[0]);
                     mpec.setLimitSize(tamanio_potencial);
                    tiempo_inicio = System.currentTimeMillis(); 
                    
                   double epsilon = Double.parseDouble(parts2[1]);
                   int opt = Integer.parseInt(parts2[2]);
                   
                    int opt2 = Integer.parseInt(parts2[3]);
                    
                    double M = Double.parseDouble(parts2[4]);
                    boolean prev = Boolean.parseBoolean(parts2[5]);
                    
                    mpec.getApproxMaxPotentials3s(epsilon,opt2,opt);
                     System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeApproxMaxConfigurationsMixed(M,prev);
            //         System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + "\n");
                //   System.out.println(mpec.getBest().getConf());
                    bw.flush();
                    break;
                       
          }         
            
             case 12: {
                mpec = new MPE(rbcopy, evidencia, 1);
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
                    bw.write("Network = " + parts[i] + ", Run = " + j + " Method = minibucket + Probtree " +
                            " Size = " + parts2[0] + 
                            " Epsilon " + parts2[1] + 
                             " Combinationp " + parts2[2] + 
                            ", Distance = " + parts2[3] + "\n");
                    bw.flush();
                    
                   
                    tamanio_potencial = Integer.parseInt(parts2[0]);
                     mpec.setLimitSize(tamanio_potencial);
                     System.gc();
                    tiempo_inicio = System.currentTimeMillis(); 
                    int opt = Integer.parseInt(parts2[2]);
                   double epsilon = Double.parseDouble(parts2[1]);
                    int opt2 = Integer.parseInt(parts2[3]);
                     mpec.getApproxMaxPotentials3(epsilon,opt2,opt);
                              System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeApproxMaxConfigurationsLowMem();
     //                System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + "\n");
           //         System.out.println(mpec.getBest().getConf());
                    bw.flush();
                   
                    break;
                       
          }  
               case 13: {   //ProbabilityTree with Splitting
                mpec = new MPE(rbcopy, evidencia, 1);
                System.gc();
                    String[] parts2;
                    
                    parts2 =  pmethods[k].split("\\s*,\\s*");
                    
                    bw.write("Network = " + parts[i] + ", Run = " + j + " Method = minibucket + Probtree + Splitting" +
                            " Size = " + parts2[0] + 
                            " Epsilon " + parts2[1] + 
                             " Combinationp " + parts2[2] + 
                            ", Distance = " + parts2[3] + "\n");
                    bw.flush();
                    
                   
                    tamanio_potencial = Integer.parseInt(parts2[0]);
                     mpec.setLimitSize(tamanio_potencial);
                     System.gc();
                    tiempo_inicio = System.currentTimeMillis(); 
                    int opt = Integer.parseInt(parts2[2]);
                   double epsilon = Double.parseDouble(parts2[1]);
                    int opt2 = Integer.parseInt(parts2[3]);
                     mpec.getApproxMaxPotentialsSplit(epsilon,opt2,opt);
                              System.out.println("Approximation finished");

                    tiempo_inter =  System.currentTimeMillis(); 
                     mpec.computeApproxMaxConfigurations();
     //                System.out.println((Explanation) mpec.kBest.elementAt(0));
                    tiempo_fin = System.currentTimeMillis();
                    double tiempo2 = tiempo_fin - tiempo_inter;
                    double tiempo1 = tiempo_inter - tiempo_inicio;
                    bw.write(tiempo1 + " , "  +  tiempo2  + "\n");
           //         System.out.println(mpec.getBest().getConf());
                    bw.flush();
                   
                    break;
                       
          }      
            
        }
         bw2.write( mpec.na + " , ");
                     bw2.flush();
    }
       
        
        
         bw.write("\n");
         bw2.write("\n");
    

         
         
            }
        }
    
         
       }
       

    
}

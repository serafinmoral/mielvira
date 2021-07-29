/*
 * This class is done to validate a learning algorithm
 * 
 * 
 */
package elvira.learning;
import elvira.*;
import elvira.database.*;
import elvira.learning.*;
import elvira.learning.classification.supervised.discrete.Naive_Bayes;
import elvira.learning.classification.supervised.discrete.TAN;
import elvira.learning.classification.supervised.discrete.BANLearning;
import elvira.learning.classification.supervised.discrete.CRPDAGLearning;
import elvira.learning.classification.supervised.discrete.RPDAGClassifier;

import elvira.learning.classification.ClassifierValidator;


import elvira.learning.classification.supervised.discrete.CMutInfTAN;
import elvira.learning.classification.supervised.discrete.SBND;
import elvira.learning.classification.supervised.discrete.DiscreteClassifier;
import elvira.learning.classification.supervised.discrete.MarkovBlanketLearning;


import elvira.inference.clustering.HuginPropagation;
import elvira.potential.*;
import elvira.inference.elimination.VariableElimination;
import java.io.IOException;
import elvira.parser.ParseException;
import java.util.Random;
import elvira.learning.classification.unsupervised.discrete.NBayesMLEMSelective; 
import elvira.learning.classification.unsupervised.discrete.NBayesMLEM; 
import elvira.learning.classification.supervised.discrete.Parentsgraph; 

import java.io.*;
/**
 *
 * @author smc
 */
public class Validation {
    
    DataBaseCases cases;
    DataBaseCases testcases;
    
    int procedure=0; // 0: 10 cross validation
    
    int learnalg; // 0: PC   1:K2 4: TAN 8: SBN Simple Bayes Net  5:Naive Bayes 6:AutoClass (NBayesMLEM) 7: NBayesMLEMSelective 8:SNBD 9:ParentsGraph
    
    int score; // 0: ScoreLog   1: Error percentage 2: LogClass
    String arguments;
    
    Node classvar;
    int indexclass;
    
    double finalvalue;
    
    
    public static void main(String args[]) throws ParseException, Exception, IOException,elvira.InvalidEditException { 
        Validation.Experiment(args[0], args[1]);
        
    }

    
    
     public Validation (DataBaseCases d, int proc,  int s, int alg,String a) {
        
        cases = d;
       learnalg = alg;
        procedure = proc;
        score = s;
        finalvalue = 0.0;
        arguments = a;
         indexclass = cases.getNodeList().size()-1;
            classvar = cases.getNodeList().lastElement();
     
     }
     
     public void setTest(DataBaseCases d){
         testcases = d;
     }
     
     public void compute() throws IOException, elvira.InvalidEditException, Exception{
         switch  (procedure) {
             case 0: {compute10cross();
                      break;}
             
             }
         }
         
         
         
    private void compute10cross() throws IOException,elvira.InvalidEditException, Exception {
        
   
        DataBaseCases train,test;
        DiscreteClassifier dclas;
        String[] parts;
        MarkovBlanketLearning bnclass;
        
        dclas = new Naive_Bayes();
        bnclass = new BANLearning();
        
        Bnet gr;
        int i;
        gr = new Bnet();
        
        for(i=0; i<10;i++) {
            System.out.println("Cross validation " + i);
            train = cases.getTrainCV(i, 10);
            test = cases.getTestCV(i, 10);
            train.getNodeList().cleanNodes();
           
            switch (learnalg) {
                case 7: { //NBayesMLEMSelective an EM for cluster (AUTOCLASS) with a previous cluster of variables and a tree structure of artificial variables
                     NBayesMLEMSelective lep = new   NBayesMLEMSelective(train, arguments);
                     lep.learning();
                     gr = lep.getOutput();
                    break;
                }
                case 0: { //PC Algorithm
                    PCLearning lep = new PCLearning(train);
                     lep.learning();
                     gr = lep.getOutput();
                     DELearning h = new DELearning(train, gr);
                     h.learning(2.0);
                     gr = h.getOutput();
                    break;
                }
                 case 1: { //K2 learning
                    K2Learning lep = new K2Learning(train,7);
                     lep.learning();
                     gr = lep.getOutput();
                     DELearning h = new DELearning(train, gr);
                     h.learning(2.0);
                     gr = h.getOutput();
                    break;
                }
                  case 5: { //Naive Bayes
                     dclas = new Naive_Bayes(train,true);
                     dclas.train();
                    
                    break;
                }
                   case 4: { //Naive Bayes
                     dclas = new CMutInfTAN(train,true);
                     dclas.train();
                    
                    break;
                }
                    case 8: { 
                          parts = arguments.split("\\s+");
                   
                      int method = Integer.parseInt(parts[0]);
                      int s = Integer.parseInt(parts[1]);
                        dclas = new SBND(train,true,method,s);
                     dclas.train();
                    
                    break;
                }
                    
                     case 9: { //Naive Bayes
                     parts = arguments.split("\\s+");
                     dclas = new Parentsgraph(train,Integer.parseInt(parts[3]));
                     dclas.structuralLearning();
                     DELearning h = new DELearning(train, dclas.getClassifier());
                     int method = Integer.parseInt(parts[0]);
                     if (method==0) {
                         h.learningmix(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])  );
                     }
                     else {
                         h.learninglaplace();
                     }
                     gr = dclas.getClassifier();
                    break;
                }
                   case 6: { // AutoClass 
                    NBayesMLEM lep = new NBayesMLEM(train, Integer.parseInt(arguments));
                   
                     gr = lep.getClassifier();
                     DELearning h = new DELearning(train, gr);
                     h.learningmissing();
                      gr = h.getOutput();
                    break;
                }
                    case 10: { 
                       parts = arguments.split("\\s+");
                   
                     // int method = Integer.parseInt(parts[0]);
                      
                      
                     bnclass = new BANLearning(train,train.getNodeList().size()-1,true,parts[0]);
                     bnclass.learning();
                     gr = bnclass.getOutput();
                        DELearning h = new DELearning(train, gr);
                     h.learning(2.0);
                     gr = h.getOutput();
                    break;
                }
                    case 11: { 
                           parts = arguments.split("\\s+");
                   Metrics m;
              if(parts[0].equals("K2")) m = (Metrics)new K2Metrics(train);
	else if (parts[0].equals("BIC")) m = (Metrics)new BICMetrics(train);
	//	 if(args[3].equals("BDe"))
	else m = (Metrics)new BDeMetrics(train);
                    bnclass = new RPDAGClassifier(train,train.getNodeList().size()-1,true,m);
                    bnclass.learning();
                        gr = bnclass.getOutput();
                           DELearning h = new DELearning(train, gr);
                     h.learning(2.0);
                     gr = h.getOutput();
                     
                    break;
                }
                     case 12: { 
                           parts = arguments.split("\\s+");
                   
              
                    bnclass = new CRPDAGLearning(train,train.getNodeList().size()-1,true,parts[0]);
                    bnclass.learning();
                        gr = bnclass.getOutput();
                           DELearning h = new DELearning(train, gr);
                     h.learning(2.0);
                     gr = h.getOutput();
                     
                    break;
                }
            }
            switch (score) {
                case 0: {// Score with the logarithm of the probability of the test database;
                    ScoreLog sc = new ScoreLog(test, gr);
                    finalvalue+= sc.avercomputeScoreLog();
                    break;
                }
                
                case 2: {// Score with the logarithm of the probability of the test database;
                    ScoreLog sc = new ScoreLog(test, dclas);
                    finalvalue+= sc.avercomputeScoreLog();
                    break;
                }
                
                case 1: {
                    if ((learnalg== 5) ||(learnalg== 4)||(learnalg== 8) ) {finalvalue += dclas.test(test);}
                    if ((learnalg== 10) ||(learnalg== 11)  ||(learnalg== 12)) {
                        ClassifierValidator classval = new ClassifierValidator(bnclass,train,train.getNodeList().size()-1);
                        
                        finalvalue += 100*(1.0-classval.error(bnclass,test,test.getNodeList().size()-1));}
                    break;
                }
                
                  
            
            
        }
        
        }
        
        finalvalue /= 10.0;
    }
    /*
    * This procedure has to be called to carry out an experiment
    * filei is the name of the input file, an ASCII file containing the
    * experiment description:
    * The first line contains an integer with the procedure to carry out the evaluation (0: 10 cross validation)
    * The second line contains an integer with the score for the validation (0: for the Log of the probability score)
    * The third line contains a code for output 0: plain text output, 1: Latex table output
    * The forth line contains the codes of the algorithms employed (0: PC algorithm, 7: NBayesMLEMSelective)
    * Then there is a line for each algorithm with optional arguments. If there is no optional argument, then the line is empty
    * The remaining lines contain the names of the databases used in the comparison
    * if the procedure is train and test with specific databases, then the two databases (train and test appears in one line)
    */ 
    
    
    public static void Experiment(String filei, String fileo) throws IOException, Exception, ParseException, elvira.InvalidEditException {
         
      String line;  
        
      BufferedReader reader;
      Validation exp;
      int pr,sc,sal=0,nalg,i;
      int[] alg;
      String[] parts;
      String[] arguments;
      PrintStream ps;
      DataBaseCases data;
      int ndata;
      double[] x; 
      double[] xs;
      double[] xn;
      double[] xns;
      double[] par;
      double z;
      
       Random r;
         r = new Random();
     
         
      reader = new BufferedReader(new FileReader(filei));
        ps = new PrintStream( fileo );
        
      line = reader.readLine(); 
      pr = Integer.parseInt(line);
      
      line = reader.readLine(); 
      sc = Integer.parseInt(line);
      
       line  = reader.readLine(); 
        sal = Integer.parseInt(line);
      
       line = reader.readLine();
       parts = line.split("\\s+");
       nalg = parts.length;
       alg = new int[nalg];
      
      
      x = new double[nalg]; 
      xs = new double[nalg]; 
      
        xn = new double[nalg]; 
      xns = new double[nalg]; 
      
        par = new double[nalg]; 
       for(i=0;i<nalg; i++) {
           alg[i] = Integer.parseInt(parts[i]);
       }
       
       arguments = new String[nalg];
         for(i=0;i<nalg;i++) {
             line = reader.readLine(); 
             arguments[i] = line;
         }
       
        
     
    
    if (sal == 0) {
            ps.println("Experiment \n");
             ps.print("Database \t");
           for(i=0;i<nalg;i++) {
              switch (alg[i]) {
                  case 0: { ps.print("  PC \t"); 
                            break;}
                  case 1: { ps.print("  K2 \t"); 
                            break;}
                    case 4: { ps.print("  TAN \t"); 
                            break;}
                  case 5: { ps.print("  NaiveBayes \t"); 
                            break;}
                   case 6: { ps.print("  AutoClass" + arguments[i]+ " \t"); 
                            break;}
                  case 7: { ps.print("  HierNBayesEM" + arguments[i]+ " \t"); 
                            break;}
                   case 8: { ps.print("  SBND" + arguments[i]+ " \t"); 
                            break;}
                  case 9: { ps.print("  ParentsGraph " + arguments[i]+ " \t"); 
                            break;}
                  case 10: { ps.print("  BAN Learning " + arguments[i]+ " \t"); 
                            break;}
                   case 11: { ps.print("  RPDag Learning " + arguments[i]+ " \t"); 
                            break;}
                   
                case 12: { ps.print("  CRPDag Learning " + arguments[i]+ " \t"); 
                            break;}
              }
          } 
           ps.print("\n");
    }
    else {
          ps.print("\\begin{tabular}{l||");
          for(i=0;i<nalg;i++) {
               ps.print("r|");
          }
          ps.print("}\n");
            ps.print("Database ");
           for(i=0;i<nalg;i++) {
              switch (alg[i]) {
                  case 0: { ps.print(" & PC "); 
                            break;}
                   case 1: { ps.print(" & K2 "); 
                            break;}
                    case 4: { ps.print(" & TAN "); 
                            break;}
                     case 5: { ps.print(" & NaiveBayes "); 
                            break;}
                      case 6: { ps.print(" & AutoClass " + arguments[i]+ " "); 
                            break;}
                  case 7: { 
                            ps.print(" & HierNBayesEM "+ arguments[i]+ " "); 
                            break;}
                   case 8: { 
                            ps.print(" & SBND "+ arguments[i]+ " "); 
                            break;}
                     case 9: { 
                            ps.print(" & Parents Graphs "+ arguments[i]+ " "); 
                            break;}
                      case 10: { 
                            ps.print(" & BAN " + arguments[i]+ " "); 
                            break;}
                       case 11: { 
                            ps.print(" & RPDag " + arguments[i]+ " "); 
                            break;}
                        case 12: { 
                            ps.print(" & CRPDag " + arguments[i]+ " "); 
                            break;}
              }
          } 
            ps.println("\\\\\\hline");
    }
    
    ndata = 0;
     while ((line = reader.readLine()) != null) { 
         System.out.println("Database" + line);
         data = new DataBaseCases(line);
         ndata++;
          data.getCaseListMem().randomize(r);
         ps.print(line + " ");
         z=0.0;
         for(i=0;i<nalg;i++) {
             System.out.println("Algorithm " + alg[i]);
             exp = new Validation(data,pr,sc,alg[i],arguments[i]);
              exp.compute();
              x[i] += exp.finalvalue;
              xs[i] += exp.finalvalue * exp.finalvalue;
              par[i] = exp.finalvalue;
              z+= exp.finalvalue;
              if (sal == 0) {
                   ps.print(" \t " + exp.finalvalue);
              }
              else {
                  ps.print(" &  " + exp.finalvalue);
              }
         }
       for(i=0;i<nalg;i++) {
             xn[i] += nalg*par[i]/z;
              xns[i] +=  ( nalg*par[i]/z)*(  nalg*par[i]/z);
           
       }
         if (sal == 0) { 
             ps.print("\n");
         }
         else {
             ps.print(" \\\\ \n");
         }
        
         
     }
    
     if (sal == 0) { 
             ps.print("\n");
         }
         else {
             ps.print(" \\end{tabular} \n");
         }
     
     ps.print("media ");
    for(i=0;i<nalg;i++) {     
      ps.print(" \t " + x[i]/ndata);
              
    }
      ps.print("\n");
        ps.print("media normalizada");
      
     for(i=0;i<nalg;i++) {     
      ps.print(" \t " + xn[i]/ndata);
              
    }
      ps.print("\n");
       
     ps.print("varianza ");
    for(i=0;i<nalg;i++) {     
      ps.print(" \t " + (xs[i]/ndata - (x[i]/ndata)*(x[i]/ndata) )  );
              
    }
      ps.print("\n");
        ps.print("varianza normalizada");
      
     for(i=0;i<nalg;i++) {     
      ps.print(" \t " + (xns[i]/ndata - (xn[i]/ndata)*(xn[i]/ndata) ) );
              
    }
      ps.print("\n");
    reader.close();
    ps.close();
    }
    
}

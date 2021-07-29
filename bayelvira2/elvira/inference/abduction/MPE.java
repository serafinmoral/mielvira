/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.inference.abduction;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;
import java.io.*;
import elvira.*;
import elvira.inference.clustering.JoinTree;
import elvira.parser.ParseException;
import elvira.potential.*;
import java.util.Comparator;

/**
 *
 * @author smc
 */
public class MPE  extends AbductiveInference {
    
    
    protected NodeList deletionSequence;
    
    protected Vector maxPotentials;
    protected Vector maxsPotentials;
    protected Vector sentPotentials;
    protected Vector combPotentials;
    
    protected double maxvalue;
    protected long maxsize=1;
    protected long na = 0;
    
    protected double theres; 
    protected long limitsize = 0;
    protected boolean settheres = false;
    protected boolean setlimit = false;
    
    
    
public MPE(Bnet b, Evidence e) {

  nExplanations = 1;
  partial = false;
  explanationSet = new NodeList();
  interest = new NodeList();
  kBest = new Vector();
  setProblem("Most Probable Explanations");
  setMethod("Exhaustive Search");
  network = b; 
  observations = e;  
}


public MPE(Bnet b, Evidence e, double t) {

  nExplanations = 1;
  partial = false;
  explanationSet = new NodeList();
  interest = new NodeList();
  kBest = new Vector();
  setProblem("Most Probable Explanations");
  setMethod("Exhaustive Search");
  network = b; 
  observations = e;  
  theres = t;
  settheres = true;
}


public MPE(Bnet b, Evidence e, double t, int k) {

  nExplanations = k;
  setlimit = true;
  partial = false;
  explanationSet = new NodeList();
  interest = new NodeList();
  kBest = new Vector();
  setProblem("Most Probable Explanations");
  setMethod("Exhaustive Search");
  network = b; 
  observations = e;  
  theres = t;
  settheres = true;
}



public MPE(Bnet b, Evidence e,  int k) {

  nExplanations = k;
  setlimit = true;
  partial = false;
  explanationSet = new NodeList();
  interest = new NodeList();
  kBest = new Vector();
  setProblem("Most Probable Explanations");
  setMethod("Exhaustive Search");
  network = b; 
  observations = e;  
}

public Explanation getBest() {
    return (Explanation) kBest.elementAt(0);
    
}

public void setLimitSize(long x) {
        limitsize = x;
    }
    
    public void setK(int k){
        setlimit = true;
        nExplanations = k;
    }
    
    public void anulaK(){
        setlimit = true;
        nExplanations = 1;
    }
    
    public void setUmbral(double t){
        theres = t;
        settheres = true;
    }
    
    public double getMaxValue(){
        return maxvalue;
    }


 public Explanation computeMaxConfiguration() {
        Explanation exp;
        Configuration c, c2;
        int i, nvar, j, jmax;
        FiniteStates var;
        Potential maxp, maxr;
        double maxv;
        c = new Configuration();

        nvar = deletionSequence.size();

        for (i = nvar - 1; i >= 0; i--) {
            var = (FiniteStates) deletionSequence.elementAt(i);
            maxp = ((Potential) maxPotentials.elementAt(i));
            maxr = maxp.restrictVariable(c);
            maxv = -1.0;
            jmax = 0;

            for (j = 0; j < var.getNumStates(); j++) {
                c2 = new Configuration();
                c2.insert(var, j);

                if (maxr.getValue(c2) > maxv) {
                    maxv = maxr.getValue(c2);
                    jmax = j;
                }
            }
            c.insert(var, jmax);
        }

        exp = new Explanation(c, maxvalue);

        return exp;

    }
 
 
   public static Comparator<MPENode> comparator = new Comparator<MPENode>(){
		
		@Override
		 public int compare(MPENode p1,MPENode p2) {
      if (p1.p < p2.p) {return 1;}
      else { if (p1.p > p2.p ) return -1;
      else {if (p1.i > p2.i) {
          return 1;}
      else {return -1;}
      } 
          
      }
        }
	};

  public void computeMaxConfigurations() {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases;
        FiniteStates var;
        Potential maxp;
        double value, mv;
        double[] rv;
        Queue<MPENode> conflist;
        MPENode x;
        boolean finish = false;
        double ratio;

        conflist = new PriorityQueue(comparator);

        c = new Configuration();

        nvar = deletionSequence.size();

        x = new MPENode(c, maxvalue, nvar);

        conflist.add(x);

        while (!finish) {
            x = conflist.poll();
            i = x.getPos() - 1;
            c = x.getConfig();
            value = x.getVal();

            if (i == -1) {
                exp = new Explanation(c, value);
                kBest.add(exp);
                if (setlimit) {
                    if (kBest.size() == nExplanations) {
                        finish = true;
                    }
                }
                if (conflist.isEmpty()) {
                    finish = true;
                }
            } else {
                var = (FiniteStates) deletionSequence.elementAt(i);
                maxp = ((Potential) maxPotentials.elementAt(i));
                ncases = var.getNumStates();
                rv = new double[ncases];
                mv = -1.0;

                for (j = 0; j < ncases; j++) {
                    cv = c.duplicate();
                    cv.insert(var, j);
                    rv[j] = maxp.getValue(cv);

                    if (rv[j] > mv) {
                        mv = rv[j];
                    }
                }

                ratio = value / mv;

                for (j = 0; j < ncases; j++) {
                    rv[j] *= ratio;
                    if ((!settheres) || (settheres && (rv[j] / maxvalue >= theres))) {
                        cv = c.duplicate();
                        cv.insert(var, j);
                        x = new MPENode(cv, rv[j], i);
                        conflist.add(x);
                    }
                }

                if (conflist.isEmpty()) {
                    finish = true;
                }

            }
        }

    }

  
  
   public void computeApproxMaxConfigurations() throws IOException  {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k;
        FiniteStates var;
        Vector listp;
        double value;
        double[] rv;
        Queue<MPENode> conflist;
        MPENode x;
        boolean finish = false;
        boolean complete = false;
        long ad=0;
        
        
        conflist = new PriorityQueue(comparator);
        
      
       
        
        c = new Configuration();

        nvar = deletionSequence.size();

        x = new MPENode(c, maxvalue, nvar);

        conflist.add(x);

        while (!finish) {
            x = conflist.poll();
            i = x.getPos() - 1;
            c = x.getConfig();
            ad--;
            
            if((conflist.size()% 10000) == 0) {
                System.out.println(conflist.size()+ " " + i + " " +   x.getVal()) ;
            }
            
        
            value = x.getVal();

            if (i == -1) {
                exp = new Explanation(c, value);
          //      System.out.println(c);
                System.out.println("Value = " + value);
                kBest.add(exp);
                if (!complete) {
                    complete = true;
                    maxvalue = value;
                }
                
                if (setlimit) {
                    if (kBest.size() == nExplanations) {
                        finish = true;
                    }
                }
                if (conflist.isEmpty()) {
                    finish = true;
                }
            } else {
                na++;
                var = (FiniteStates) deletionSequence.elementAt(i);
                listp = ((Vector) maxPotentials.elementAt(i));
                ncases = var.getNumStates();
                rv = new double[ncases];

                for (j = 0; j < ncases; j++) {
                    cv = c.duplicate();
                    cv.insert(var, j);
                    rv[j] = 1.0;
                    for (k = 0; k < listp.size(); k++) {
                        rv[j] *= ((Potential) listp.elementAt(k)).getValue(cv);
                    }

                }

                for (j = 0; j < ncases; j++) {
                    if ((!settheres) || (!complete) || (settheres && (rv[j] / maxvalue >= theres))) {
                      if(rv[j]>0) {  cv = c.duplicate();
                        cv.insert(var, j);
                        ad++;
                        x = new MPENode(cv, rv[j], i);
                         conflist.add(x);}
                    }
                }

                if(ad>maxsize) {maxsize = ad;}
                if (conflist.isEmpty()) {
                    finish = true;
                }

            }
        }
          System.out.println("Maxsize " + maxsize);
          

    }
   
   
    
  
   public void computeApproxMaxConfigurationsLowMem() throws IOException  {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k;
        FiniteStates var;
        Vector listp;
        double value;
        double[] rv;
        Queue<MPENode> conflist;
        MPENode x;
        boolean finish = false;
        boolean complete = false;
        long ad=0;
        double ther;
        
        
        
        
      
       
        ther = maxvalue;
       

        nvar = deletionSequence.size();

        while(!complete) {
         
            ther = ther/2;
            System.out.println("New Itereation" + "ther" + ther);
         c = new Configuration();
        
        x = new MPENode(c, maxvalue, nvar);
         conflist = new PriorityQueue(comparator);
        
         finish = false;

        conflist.add(x);

        while (!finish) {
            x = conflist.poll();
            i = x.getPos() - 1;
            c = x.getConfig();
            ad--;
            
            if((conflist.size()% 10000) == 0) {
                System.out.println(conflist.size()+ " " + i + " " +   x.getVal()) ;
            }
            
        
            value = x.getVal();

            if (i == -1) {
                exp = new Explanation(c, value);
          //      System.out.println(c);
                System.out.println("Value = " + value);
                kBest.add(exp);
                if (!complete) {
                    complete = true;
                    maxvalue = value;
                }
                
                if (setlimit) {
                    if (kBest.size() == nExplanations) {
                        finish = true;
                    }
                }
                
            } else {
                na++;
                var = (FiniteStates) deletionSequence.elementAt(i);
                listp = ((Vector) maxPotentials.elementAt(i));
                ncases = var.getNumStates();
                rv = new double[ncases];

                for (j = 0; j < ncases; j++) {
                    cv = c.duplicate();
                    cv.insert(var, j);
                    rv[j] = 1.0;
                    for (k = 0; k < listp.size(); k++) {
                        rv[j] *= ((Potential) listp.elementAt(k)).getValue(cv);
                    }

                }

                for (j = 0; j < ncases; j++) {
                    if ((!settheres) || (!complete) || (settheres && (rv[j] / maxvalue >= theres))) {
                      if(rv[j]>ther) {  cv = c.duplicate();
                        cv.insert(var, j);
                        ad++;
                        x = new MPENode(cv, rv[j], i);
                         conflist.add(x);}
                    }
                }

                if(ad>maxsize) {maxsize = ad;}
                if (conflist.isEmpty()) {
                    finish = true;
                }

            }
        }
        }
          System.out.println("Maxsize " + maxsize);
          

    }
   
   
   public void computeApproxMaxConfigurationss() throws IOException  {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k;
        FiniteStates var;
        Vector listp,listp2;
        double value;
        double y;
        double[] rv;
        Queue<MPENode> conflist;
        MPENode x;
        boolean finish = false;
        boolean complete = false;
        long ad = 0;
        
        conflist = new PriorityQueue(comparator);
        
      
       
        
        c = new Configuration();

        nvar = deletionSequence.size();

        x = new MPENode(c, maxvalue, nvar);

        conflist.add(x);

        while (!finish) {
            x = conflist.poll();
            i = x.getPos() - 1;
            c = x.getConfig();
        ad--; 
            value = x.getVal();
  if((conflist.size()% 10000) == 0) {
                System.out.println(conflist.size()+ " " + i + " " +   x.getVal()) ;
            }
            
            if (i == -1)  {
                exp = new Explanation(c, value);
          //      System.out.println(c);
                System.out.println("Value = " + value + Math.log10(value));
                kBest.add(exp);
                if (!complete) {
                    complete = true;
                    maxvalue = value;
                }
                
                if (setlimit) {
                    if (kBest.size() == nExplanations) {
                        finish = true;
                    }
                }
                if (conflist.isEmpty()) {
                    finish = true;
                }
            } else {
                na++;
                var = (FiniteStates) deletionSequence.elementAt(i);
                listp = ((Vector) maxPotentials.elementAt(i));
                listp2 = ((Vector) maxsPotentials.elementAt(i));
                ncases = var.getNumStates();
                rv = new double[ncases];
                y=1.0;
                for (k = 0; k < listp2.size(); k++) {
                        y *= ((Potential) listp2.elementAt(k)).getValue(c);
                 }
                
                
                 for (j = 0; j < ncases; j++) {
                     rv[j] = y;
                      c.insert(var, j);
                      for (k = 0; k < listp.size(); k++) {
                        rv[j] *= ((Potential) listp.elementAt(k)).getValue(c);
                 }
                      c.remove(var);
                 }

                

                for (j = 0; j < ncases; j++) {
                   if(rv[j]>0) {
                    if ((!settheres) || (!complete) || (settheres && (rv[j] / maxvalue >= theres))) {
                       cv = c.duplicate();
                        cv.insert(var, j);
                        ad++;
                        x = new MPENode(cv, rv[j], i);
                        conflist.add(x);
                    }}
                }
                
                if(ad>maxsize) {maxsize = ad;}

                if (conflist.isEmpty()) {
                    finish = true;
                }

            }
        }
        
        System.out.println("Maxsize " + maxsize);

    }
   
   
 public void       computeInitialGreedy() {
     Vector listp;
     Vector listp2;
     Vector listaux;
     Vector listtotal;
     Vector listvar;
     int i,j,nvar,ncases;
     Configuration c;
     Potential pot;
     FiniteStates var;
     double[] rv;
     double vmax;
     int jmax,jo;
     boolean end;
     
             listp = ((Vector) maxPotentials.elementAt(0));
             listp2 = ((Vector) maxsPotentials.elementAt(0));
                
          listtotal = (Vector) listp.clone();
          listtotal.addAll(listp2);
          
          listvar = new Vector();
          nvar = deletionSequence.size();
          listvar.add(listtotal);
          
          for(i=1;i<nvar;i++) {
              listaux = new Vector();
              var = (FiniteStates) deletionSequence.elementAt(i);
              for(j=0;j<listtotal.size();j++) {
                  pot = (Potential)  listtotal.elementAt(j);
                  if (pot.getVariables().contains(var)) {
                      listaux.add(pot);
                  }
              }
              listvar.add(listaux);
          }
            
          c = new Configuration();
         
          // Initial Configuration
          
          for(i=nvar-1; i>=0; i--) {
               var = (FiniteStates) deletionSequence.elementAt(i);
               listp = ((Vector) maxPotentials.elementAt(i));
               ncases = var.getNumStates();
              
                  rv = new double[ncases];
                 for (j = 0; j < ncases; j++) {
                     rv[j] = 1.0;
                   c.insert(var, j);
                      for (int k = 0; k < listp.size(); k++) {
                        rv[j] *= ((Potential) listp.elementAt(k)).getValue(c);
                      }
                      c.remove(var);
                 }
                 jmax = 0;
                 vmax = rv[0];
                 for (j = 1; j < ncases; j++) {
                     if(rv[j]>vmax) {
                         jmax = j;
                         vmax = rv[j];
                     }
                 }
                  c.insert(var,jmax);   
                 
                 
                 
          }
          
          // Local Search
          
          end = false;
          while(!end) {
              end = true;
              for(i=nvar-1; i>=0; i--) {
               var = (FiniteStates) deletionSequence.elementAt(i);
               listp = ((Vector) listvar.elementAt(i));
               ncases = var.getNumStates();
               jo = c.getValue(var);
               c.remove(var);
               
                 rv = new double[ncases];
                 for (j = 0; j < ncases; j++) {
                     rv[j] = 1.0;
                   c.insert(var, j);
                      for (int k = 0; k < listp.size(); k++) {
                        rv[j] *= ((Potential) listp.elementAt(k)).getValue(c);
                      }
                      c.remove(var);
                 }
                 jmax = jo;
                 vmax = rv[jo];
                 for (j = 0; j < ncases; j++) {
                     if(rv[j]>vmax) {
                         jmax = j;
                         vmax = rv[j];
                     }
                     
                 }
                 
                 if (jo != jmax) {
                     end=false;
                 }
                 
                 c.insert(var, jmax);
              
          }
          }
                
         // Value of this configuration
            listp = ((Vector) maxPotentials.elementAt(0));
            
                
          maxvalue = 1.0;
           for (int k = 0; k < listp.size(); k++) {
                        maxvalue *= ((Potential) listp.elementAt(k)).getValue(c);
                      }
           listp = ((Vector) maxsPotentials.elementAt(0));
          for (int k = 0; k < listp.size(); k++) {
                        maxvalue *= ((Potential) listp.elementAt(k)).getValue(c);
                      }
          
        Explanation   exp = new Explanation(c, maxvalue);
          //      System.out.println(c);
                
                kBest.add(exp);
          System.out.println("Initial Greedy " + maxvalue);
         
                
        }
    
   public void computeApproxMaxConfigurationssprev() throws IOException  {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k;
        FiniteStates var;
        Vector listp,listp2;
        double value;
        double y;
        double[] rv;
        Queue<MPENode> conflist;
        MPENode x;
        boolean finish = false;
        boolean complete = false;
        
        long ad;
        
        computeInitialGreedy();
        
        
        conflist = new PriorityQueue(comparator);
        
       ad=0;
       
        
        c = new Configuration();

        nvar = deletionSequence.size();

        x = new MPENode(c, maxvalue, nvar);

        conflist.add(x);
        ad++;
        maxsize = 1;
        
        while (!finish) {
            x = conflist.poll();
            i = x.getPos() - 1;
            c = x.getConfig();
        ad--; 
            value = x.getVal();

            if (i == -1) {
                exp = new Explanation(c, value);
          //      System.out.println(c);
                System.out.println("Value = " + value);
                   if (!complete) {
                    complete = true;
                    maxvalue = value;
                    kBest.clear();
                    
                }
                kBest.add(exp);
             
                
                if (setlimit) {
                    if (kBest.size() == nExplanations) {
                        finish = true;
                    }
                }
                if (conflist.isEmpty()) {
                    finish = true;
                }
            } else {
                na++;
                var = (FiniteStates) deletionSequence.elementAt(i);
                listp = ((Vector) maxPotentials.elementAt(i));
                listp2 = ((Vector) maxsPotentials.elementAt(i));
                ncases = var.getNumStates();
                rv = new double[ncases];
                y=1.0;
                for (k = 0; k < listp2.size(); k++) {
                        y *= ((Potential) listp2.elementAt(k)).getValue(c);
                 }
                
                
                 for (j = 0; j < ncases; j++) {
                     rv[j] = y;
                      c.insert(var, j);
                      for (k = 0; k < listp.size(); k++) {
                        rv[j] *= ((Potential) listp.elementAt(k)).getValue(c);
                 }
                      c.remove(var);
                 }

                

                for (j = 0; j < ncases; j++) {
                    if (rv[j] > maxvalue) {
                        cv = c.duplicate();
                        cv.insert(var, j);
                        x = new MPENode(cv, rv[j], i);
                        conflist.add(x);
                        ad++;
                    }
                    
                }
                
                if (ad>maxsize ) {maxsize = ad;}

                if (conflist.isEmpty()) {
                    finish = true;
                }

            }
        }
        
        System.out.println("Maxsize = " + maxsize);

    }
   
   public void computeApproxMaxConfigurationssprevdeep() throws IOException  {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k, jmax;
        FiniteStates var;
        Vector listp,listp2;
        double value;
        double y,vmax;
        double[] rv;
        Queue<MPENode> conflist;
        MPENode x;
        boolean finish = false;
        boolean endi = false;
        boolean complete = false;
       
        long ad;
        
        computeInitialGreedy();
        
        
        conflist = new PriorityQueue(comparator);
        
       ad=0;
       
       
        
        c = new Configuration();

        nvar = deletionSequence.size();

        x = new MPENode(c, 1.0, nvar);

        conflist.add(x);
        ad++;
        maxsize = 1;
        
        while (!finish) {
            x = conflist.poll();
            i = x.getPos() - 1;
            c = x.getConfig();
        
                 
            if((conflist.size()% 10) == 0) {
                System.out.println(conflist.size()+ " " + i + " " +   x.getVal()) ;
                System.out.println("Best value " + maxvalue);
            
            }
            
            value = x.getVal();
            ad--;
            if(value > maxvalue) {

            endi = false;
            
              if (i == -1) {
                endi = true;
                
                if (value > maxvalue) {
                exp = new Explanation(c, value);
          //      System.out.println(c);
               
                   if (!complete) {
                    complete = true;
                    maxvalue = value;
                    kBest.clear();
                    
                }
                kBest.add(exp);
                }
                
                if (setlimit) {
                    if (kBest.size() == nExplanations) {
                        finish = true;
                    }
                }
                if (conflist.isEmpty()) {
                    finish = true;
                }
            } else {
            
            while(!endi) {
            
               na++;
                var = (FiniteStates) deletionSequence.elementAt(i);
                listp = ((Vector) maxPotentials.elementAt(i));
                listp2 = ((Vector) maxsPotentials.elementAt(i));
                ncases = var.getNumStates();
                rv = new double[ncases];
                y=1.0;
                for (k = 0; k < listp2.size(); k++) {
                        y *= ((Potential) listp2.elementAt(k)).getValue(c);
                 }
                
                
                 for (j = 0; j < ncases; j++) {
                     rv[j] = y;
                      c.insert(var, j);
                      for (k = 0; k < listp.size(); k++) {
                        rv[j] *= ((Potential) listp.elementAt(k)).getValue(c);
                 }
                      c.remove(var);
                 }

                 jmax=0;
                 vmax = rv[0];
                  for (j = 1; j < ncases; j++) {
                       if (rv[j]>vmax) {
                           jmax = j;
                           vmax = rv[j];
                       }
                  }
                
                  

                for (j = 0; j < ncases; j++) {
                    if (rv[j] > maxvalue) {
                        if (j!=jmax) {
                        cv = c.duplicate();
                        cv.insert(var, j);
                        x = new MPENode(cv, rv[j], i);
                        conflist.add(x);
                        ad++;
                    }
                    }
                   
                }
                
                if (vmax > maxvalue) {
                    ad++;
                    c.insert(var, jmax);
                    i--;
                    if(i==-1) {
                        endi=true;
                        maxvalue = vmax;
                        kBest.clear();
                        exp = new Explanation(c, vmax);
                        kBest.add(exp);
                    }
                }
                
                if (ad> maxsize) {maxsize = ad;}
                else {endi=true;}

                

            }
            }
            
            if (conflist.isEmpty()) {
                    finish = true;
                }
            }
            else{finish = true;}
        }
            
         System.out.println("Maxsize  " + maxsize);
        

    }
   
   public void computeApproxMaxConfigurationsMixed(double M, boolean prev) throws IOException  {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k, jmax;
        FiniteStates var;
        Vector listp,listp2;
        double value;
        double y,vmax;
        double[] rv;
        Queue<MPENode> conflist;
        MPENode x;
        boolean finish = false;
        boolean endi = false;
        boolean complete = false;
        
        long ad;
        
        
        if (prev) {computeInitialGreedy();}
        else {maxvalue = -1.0;}
        
        
        conflist = new PriorityQueue(comparator);
        
       ad=0;
       
       
        
        c = new Configuration();

        nvar = deletionSequence.size();

        x = new MPENode(c, 1.0, nvar);

        conflist.add(x);
        ad++;
        
        maxsize = 1;
        
        while (!conflist.isEmpty()) {
            x = conflist.poll();
            i = x.getPos() - 1;
            c = x.getConfig();
            ad--; 
            
            value = x.getVal();
            
            value *= Math.pow(i+1,3);
            
              if((conflist.size()% 10000) == 0) {
                System.out.println(conflist.size()+ " " + i + " " +  value) ;
                
            
            }
            
            if(value > maxvalue) {

          
            
              if (i == -1) {
                
                
               maxvalue = value;
               System.out.println("NEw MAx Value" + maxvalue);
                exp = new Explanation(c, value);
                    kBest.clear();
                    kBest.add(exp);
              }
               
              else {
           na++;
                var = (FiniteStates) deletionSequence.elementAt(i);
                listp = ((Vector) maxPotentials.elementAt(i));
                listp2 = ((Vector) maxsPotentials.elementAt(i));
                ncases = var.getNumStates();
                rv = new double[ncases];
                y=1.0;
                for (k = 0; k < listp2.size(); k++) {
                        y *= ((Potential) listp2.elementAt(k)).getValue(c);
                 }
                
                
                 for (j = 0; j < ncases; j++) {
                     rv[j] = y;
                      c.insert(var, j);
                      for (k = 0; k < listp.size(); k++) {
                        rv[j] *= ((Potential) listp.elementAt(k)).getValue(c);
                 }
                      c.remove(var);
                 }

                 jmax=0;
                 vmax = rv[0];
                  for (j = 1; j < ncases; j++) {
                       if (rv[j]>vmax) {
                           jmax = j;
                           vmax = rv[j];
                       }
                  }
                
                  

                for (j = 0; j < ncases; j++) {
                    if (rv[j] >= maxvalue) {
                        cv = c.duplicate();
                        cv.insert(var, j);
                        x = new MPENode(cv, rv[j]/Math.pow(i, 3), i);
                        conflist.add(x);
                        ad++;
                       
                  
                    }
                  
                }
                
                if (ad>maxsize) {maxsize = ad;} 

                

            }
            }
            
           
        }
            
         System.out.println("Value = " + maxvalue);
        System.out.println("Maxsize " + maxsize);

    }
   
  
   public void computeMaxConfigurationDynamic() throws IOException  {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k,jmax;
        FiniteStates var;
        PotentialTree pot,pot2,potl;
        Vector listp;
        double value,x;
        double[] rv;
        boolean finish = false;
        double[] tvalues;
        double vmax=0.0;
        Vector important;
       
        
        
        c = new Configuration();
          
        
        
        while(!finish) {
      
       
        finish = true;
        
        

        nvar = deletionSequence.size()-1;

  
          for (i=nvar ; i>=0 ; i--) {
              var = (FiniteStates)deletionSequence.elementAt(i);
              pot = (PotentialTree)sentPotentials.elementAt(i);
//               pot2 = (PotentialTree)combPotentials.elementAt(i);
              listp = (Vector) maxPotentials.elementAt(i);
              
       //       System.out.println("i= " + i);
              
              value = pot.getValue(c);
              
              ncases = var.getNumStates();
              tvalues = new double[ncases];
              for (j=0;j<ncases; j++ )   {tvalues[j] = 1.0;}
              
              for (j=0;j<ncases;j++) {
                    c.insert(var, j);
                    for (k=0; k<listp.size();k++) {
                      x =  ((PotentialTree) listp.elementAt(k)).getValue(c);
                      tvalues[j] = tvalues[j]*x;
                    }
                    c.remove(var);
              }
              
              jmax=0;
              vmax = tvalues[0];
              for(j=1;j<ncases;j++) {
                  if(tvalues[j]>vmax) {
                      jmax=j;
                      vmax = tvalues[j];
                  }
              }
              
              if (vmax == value) {
                  c.insert(var, jmax);
              }
              else {
                  finish = false;
                   important = new Vector();
                   for(j=0;j<pot.getVariables().size();j++) {
                       important.add(pot.getVariables().elementAt(j));
                   }
                   pot.getTree().update2(c,vmax, important);
                   c = new Configuration();
                  break;
              }
              
              

          }
                  
        

            
          
          }
        
                    
            
  exp = new Explanation(c, vmax);
                kBest.add(exp);
        
  
    }
   
   
   public void computeMaxConfigurationDynamic2() throws IOException  {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k,jmax;
        FiniteStates var;
        PotentialTree pot,pot2,potl;
        Vector listp;
        double value,x;
        double[] rv;
        boolean finish = false;
        double[] tvalues;
        double vmax=0.0;
        Vector important;
        
        
        c = new Configuration();

        nvar = deletionSequence.size()-1;

          i = nvar;
  
          while (i>=0) {
              
          //    System.out.println(c);
              
                System.out.println("i= " + i);
              var = (FiniteStates)deletionSequence.elementAt(i);
              pot = (PotentialTree)sentPotentials.elementAt(i);
    //           pot2 = (PotentialTree)combPotentials.elementAt(i);
              listp = (Vector) maxPotentials.elementAt(i);
              
              
              value = pot.getValue(c);
              
              ncases = var.getNumStates();
              tvalues = new double[ncases];
              for (j=0;j<ncases; j++ )   {tvalues[j] = 1.0;}
              
              for (j=0;j<ncases;j++) {
                    c.insert(var, j);
                    for (k=0; k<listp.size();k++) {
                      x =  ((PotentialTree) listp.elementAt(k)).getValue(c);
                      tvalues[j] = tvalues[j]*x;
                    }
                    c.remove(var);
              }
              
              jmax=0;
              vmax = tvalues[0];
              for(j=1;j<ncases;j++) {
                  if(tvalues[j]>vmax) {
                      jmax=j;
                      vmax = tvalues[j];
                  }
              }
              
              if ((vmax == value)||(i==nvar)) {
                  c.insert(var, jmax);
                  i--;
              }
              else {
                    important = new Vector();
                   for(j=0;j<pot.getVariables().size();j++) {
                       important.add(pot.getVariables().elementAt(j));
                   }
                   pot.getTree().update2(c,vmax, important);
                   c.remove(c.size()-1);
                   i++;
                   finish = false;
                   while(!finish) {
                         var = (FiniteStates)deletionSequence.elementAt(i);
                         if ((pot.getVariables().contains(var))||(i==nvar)) 
                         {finish=true;}
                         else {i++;
                          c.remove(c.size()-1);
                         }
                   } 
                  
                   
                   
                  
              }
              
              

          }
          
          
          

  exp = new Explanation(c, vmax);
                kBest.add(exp);
    }
   
   
   
   public void computeMaxConfigurationDynamic3() throws IOException  {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k,jmax;
        FiniteStates var;
        PotentialTree pot,pot2,potl;
        Vector listp;
        double value,x;
        double[] rv;
        boolean finish = false;
        double[] tvalues;
        double vmax=0.0;
        Vector important;
        
        
        c = new Configuration();

        nvar = deletionSequence.size()-1;

          i = nvar;
  
          while (i>=0) {
              
          //    System.out.println(c);
              
                System.out.println("i= " + i);
              var = (FiniteStates)deletionSequence.elementAt(i);
              pot = (PotentialTree)sentPotentials.elementAt(i);
    //           pot2 = (PotentialTree)combPotentials.elementAt(i);
              listp = (Vector) maxPotentials.elementAt(i);
              
              
              value = pot.getValue(c);
               System.out.println("Value= " + value);
              ncases = var.getNumStates();
              tvalues = new double[ncases];
              for (j=0;j<ncases; j++ )   {tvalues[j] = 1.0;}
              
              for (j=0;j<ncases;j++) {
                    c.insert(var, j);
                    for (k=0; k<listp.size();k++) {
                      x =  ((PotentialTree) listp.elementAt(k)).getValue(c);
                      tvalues[j] = tvalues[j]*x;
                    }
                    c.remove(var);
              }
              
              jmax=0;
              vmax = tvalues[0];
              for(j=1;j<ncases;j++) {
                  if(tvalues[j]>vmax) {
                      jmax=j;
                      vmax = tvalues[j];
                  }
              }
              
              if ((vmax == value)||(i==nvar)) {
                  c.insert(var, jmax);
                  i--;
              }
              else {
                  
                   
                   cv = new Configuration();
                   
                   
                   pot.getTree().update3(c,listp,cv,var);
                   c.remove(c.size()-1);
                   i++;
                   finish = false;
                   while(!finish) {
                         var = (FiniteStates)deletionSequence.elementAt(i);
                         if ((pot.getVariables().contains(var))||(i==nvar)) 
                         {finish=true;}
                         else {i++;
                          c.remove(c.size()-1);
                         }
                   } 
                  
                   
                   
                  
              }
              
              

          }
          
          
          

  exp = new Explanation(c, vmax);
                kBest.add(exp);
    }
   
   
   
   public void computeMaxConfigurationDynamic4() throws IOException  {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k,jmax;
        FiniteStates var;
        PotentialTree pot,pot2,potl;
        Vector listp;
        Vector lists;
        double value,x;
        double[] rv;
        boolean finish = false;
        double[] tvalues;
        double vmax=0.0;
        Vector important;
        NodeList nl;
        
        
        c = new Configuration();

        nvar = deletionSequence.size()-1;

          i = nvar;
  
          while (i>=0) {
              
         //    System.out.println(c);
              
                System.out.println("i= " + i);
              var = (FiniteStates)deletionSequence.elementAt(i);
             
    //           pot2 = (PotentialTree)combPotentials.elementAt(i);
              listp = (Vector) maxPotentials.elementAt(i);
              lists = (Vector) sentPotentials.elementAt(i);
              
              
              value = 1;
              
              
              for(j=0; j<lists.size();j++) {
               pot = (PotentialTree)lists.elementAt(j);
              value *= pot.getValue(c);
              }
              
              
              System.out.println("Value = "+ value);
            ncases = var.getNumStates();
              tvalues = new double[ncases];
              for (j=0;j<ncases; j++ )   {tvalues[j] = 1.0;}
              
              for (j=0;j<ncases;j++) {
                    c.insert(var, j);
                    for (k=0; k<listp.size();k++) {
                      x =  ((PotentialTree) listp.elementAt(k)).getValue(c);
                      tvalues[j] = tvalues[j]*x;
                    }
                    c.remove(var);
              }
              
              jmax=0;
              vmax = tvalues[0];
              for(j=1;j<ncases;j++) {
                  if(tvalues[j]>vmax) {
                      jmax=j;
                      vmax = tvalues[j];
                  }
              }
              
              if (( (vmax / value)> 0.999999)||(i==nvar)) {
                  c.insert(var, jmax);
                  i--;
              }
              else {
                  
                 
                   cv = new Configuration();
                   
                   for (j=0; j<listp.size();j++) {
                       pot = (PotentialTree)listp.elementAt(j);
                       pot.getTree().upgradeList(cv,c);
                   }
                   
                   pot = (PotentialTree ) ((Vector) sentPotentials.elementAt(i)).elementAt(0);

                   pot.upgrade(cv,listp,var,value);
                  
                   ((Vector) sentPotentials.elementAt(i)).addElement(pot);
                   
                   c.remove(c.size()-1);
                   i++;
                   finish = false;
                   while(!finish) {
                         var = (FiniteStates)deletionSequence.elementAt(i);
                         if ((pot.getVariables().contains(var))||(i==nvar)) 
                         {finish=true;}
                         else {i++;
                          c.remove(c.size()-1);
                         }
                   } 
                   
       //            ((Vector) maxPotentials.elementAt(i)).addElement(pot);
                  
                   
                   
                  
              }
              
              

          }
          
          
          

  exp = new Explanation(c, vmax);
                kBest.add(exp);
    }

    public void computeApproxMaxConfigurations2() {
        Explanation exp;
        Configuration c, cv;
        int i, nvar, j, ncases, k;
        FiniteStates var;
        Vector listp;
        double value;
        double[] rv;
        Queue<MPENode> conflist;
        MPENode x;
        boolean finish = false;
        double ratio, mv;
        boolean complete = false;

        conflist = new PriorityQueue(comparator);

        c = new Configuration();

        nvar = deletionSequence.size();

        x = new MPENode(c, maxvalue, nvar);

        conflist.add(x);

        while (!finish) {
            x = conflist.poll();
            i = x.getPos() - 1;
            c = x.getConfig();
            value = x.getVal();

            if (i == -1) {
                listp = ((Vector) maxPotentials.elementAt(0));
                value = 1.0;
                for (k = 0; k < listp.size(); k++) {
                    value *= ((Potential) listp.elementAt(k)).getValue(c);
                }
    //            System.out.println("Value = " + value);
                exp = new Explanation(c, value);
                kBest.add(exp);
                if (!complete) {
                    complete = true;
                    maxvalue = value;
                }
                if (setlimit) {
                    if (kBest.size() == nExplanations) {
                        finish = true;
                    }
                }
                if (conflist.isEmpty()) {
                    finish = true;
                }
            } else {
                var = (FiniteStates) deletionSequence.elementAt(i);
                listp = ((Vector) maxPotentials.elementAt(i));
                ncases = var.getNumStates();
                rv = new double[ncases];
                mv = -1.0;

                for (j = 0; j < ncases; j++) {
                    cv = c.duplicate();
                    cv.insert(var, j);
                    rv[j] = 1.0;
                    for (k = 0; k < listp.size(); k++) {
                        rv[j] *= ((Potential) listp.elementAt(k)).getValue(cv);
                    }
                    if (rv[j] > mv) {
                        mv = rv[j];
                    }
                }

                ratio = value / mv;

                for (j = 0; j < ncases; j++) {
                    rv[j] *= ratio;
                    if ((!settheres) || (!complete) || (settheres && (rv[j] / maxvalue >= theres))) {
                        cv = c.duplicate();
                        cv.insert(var, j);
                        x = new MPENode(cv, rv[j], i);
                        conflist.add(x);
                    }
                }

                if (conflist.isEmpty()) {
                    finish = true;
                }

            }
        }

    }





  /**
     * Compute the max potentials while removing variables. There will be a max
     * potential for each variable of the problem. The deletion sequence will be
     * stored in the list <code>deletionSequence</code>. For each variable in
     * that list, its max potential will be stored at the same position in list
     * <code>maxPotentials</code>.
     *
     * Note that observed variables are not included in the deletion sequence,
     * since they need not be simulated.
     */
    public void getMaxPotentials() {
        NodeList notRemoved;
        Node variableX;
        Relation rel;
        RelationList currentRelations, tempList;
        Potential pot;
        PairTable pairTable;
        int i, j, s;
        Vector varsMax;
        Configuration c;

        notRemoved = new NodeList();
        pairTable = new PairTable();

        deletionSequence = new NodeList();
        maxPotentials = new Vector();

        // Select the variables to remove (those not observed).
        s = network.getNodeList().size();

        for (i = 0; i < s; i++) {
            variableX = (FiniteStates) network.getNodeList().elementAt(i);

            if (!observations.isObserved(variableX)) {
                notRemoved.insertNode(variableX);
                pairTable.addElement(variableX);
            }
        }

        currentRelations = getInitialRelations();

        // Now restrict the initial relations to the obervations.
        if (observations.size() > 0) {
            restrictToObservations(currentRelations);
        }

        for (i = 0; i < currentRelations.size(); i++) {
            pairTable.addRelation(currentRelations.elementAt(i));
        }

        for (i = notRemoved.size(); i > 0; i--) {
            // Next variable to remove
            variableX = pairTable.nextToRemove();

            // This variable will be in position (i-1) in results
            // and in currentConf[].
            //positions.put(variableX,new Integer(i-1));
            notRemoved.removeNode(variableX);
            pairTable.removeVariable(variableX);
            deletionSequence.insertNode(variableX);

            // Get the relations containing the variable and remove them
            // from the list.
            tempList = currentRelations.getRelationsOfAndRemove(variableX);

            // Remove them also from the search table.
            rel = tempList.elementAt(0);
            pairTable.removeRelation(rel);
            pot = rel.getValues();

            for (j = 1; j < tempList.size(); j++) {
                rel = tempList.elementAt(j);
                pairTable.removeRelation(rel);
                pot = pot.combine(rel.getValues());
            }

            // Put the obtained list of relations as the sampling
            // distribution of the variable (initially).
            maxPotentials.addElement(pot);

            varsMax = (Vector) pot.getVariables().clone();
            varsMax.remove(variableX);

            pot = pot.maxMarginalizePotential(varsMax);

            rel = new Relation();

            rel.setKind(Relation.POTENTIAL);
            rel.getVariables().setNodes((Vector) pot.getVariables().clone());
            rel.setValues(pot);
            currentRelations.insertRelation(rel);
            pairTable.addRelation(rel);
        }

        maxvalue = 1.0;
        c = new Configuration();

        for (i = 0; i < currentRelations.size(); i++) {
            maxvalue *= currentRelations.elementAt(i).getValues().getValue(c);
        }

    }

    
    public void getApproxMaxPotentials4(double epsilon, int option){
        NodeList notRemoved;
        Node variableX;
        Relation rel;
        RelationList currentRelations, tempList;
        Potential pot;
        PairTable pairTable;
        int i, j, s;
        Vector varsMax;
        Configuration c;
        Vector listpotentials;

        notRemoved = new NodeList();
        pairTable = new PairTable();

        deletionSequence = new NodeList();
        maxPotentials = new Vector();
        sentPotentials = new Vector();
  //      combPotentials = new Vector();

        // Select the variables to remove (those not observed).
        s = network.getNodeList().size();

        for (i = 0; i < s; i++) {
            variableX = (FiniteStates) network.getNodeList().elementAt(i);
              System.out.println("Borrando variable" + i);
            if (!observations.isObserved(variableX)) {
                notRemoved.insertNode(variableX);
                pairTable.addElement(variableX);
            }
        }

        currentRelations = getInitialRelations();
        restrictToObservationstoTree(currentRelations);
         approximateInitial(currentRelations,epsilon,option);

        // Now restrict the initial relations to the obervations.
        if (observations.size() > 0) {
            restrictToObservations(currentRelations);
        }

        for (i = 0; i < currentRelations.size(); i++) {
            pairTable.addRelation(currentRelations.elementAt(i));
        }

        for (i = notRemoved.size(); i > 0; i--) {
            // Next variable to remove
             variableX = pairTable.nextToRemove();
               System.out.println("Deleting variable " + i);
            // This variable will be in position (i-1) in results
            // and in currentConf[].
            //positions.put(variableX,new Integer(i-1));
            notRemoved.removeNode(variableX);
            pairTable.removeVariable(variableX);
            deletionSequence.insertNode(variableX);

            // Get the relations containing the variable and remove them
            // from the list.
            tempList = currentRelations.getRelationsOfAndRemove(variableX);
            listpotentials = new Vector();
            // Remove them also from the search table.
            rel = tempList.elementAt(0);
            pairTable.removeRelation(rel);
            pot = rel.getValues();
            listpotentials.add(pot);

            for (j = 1; j < tempList.size(); j++) {
                rel = tempList.elementAt(j);
                listpotentials.add(rel.getValues());
                pairTable.removeRelation(rel);
                pot = pot.combine(rel.getValues());
                ((PotentialTree) pot).getTree().maxprune(epsilon,option);
            }

            // Put the obtained list of relations as the sampling
            // distribution of the variable (initially).
            maxPotentials.addElement(listpotentials);
  //          combPotentials.addElement(pot);

            varsMax = (Vector) pot.getVariables().clone();
            varsMax.remove(variableX);

            pot = pot.maxMarginalizePotential(varsMax);

            ((PotentialTree) pot).getTree().maxprune(epsilon,2);
            
            sentPotentials.addElement(pot);
            
            rel = new Relation();

            rel.setKind(Relation.POTENTIAL);
            rel.getVariables().setNodes((Vector) pot.getVariables().clone());
            rel.setValues(pot);
            currentRelations.insertRelation(rel);
            pairTable.addRelation(rel);
        }

        maxvalue = 1.0;
        c = new Configuration();

        for (i = 0; i < currentRelations.size(); i++) {
            maxvalue *= currentRelations.elementAt(i).getValues().getValue(c);
        }

    }

    
    public void getApproxMaxPotentials() {
        NodeList notRemoved;
        Node variableX;
        Relation rel, rel2;
        RelationList currentRelations, tempList;
        Potential pot, pot2;
        PairTable pairTable;
        int i, j, s;
        Vector varsMax;
        Configuration c;
        Vector listpotentials;

        notRemoved = new NodeList();
        pairTable = new PairTable();

        deletionSequence = new NodeList();
        maxPotentials = new Vector();

        // Select the variables to remove (those not observed).
        s = network.getNodeList().size();

        for (i = 0; i < s; i++) {
            variableX = (FiniteStates) network.getNodeList().elementAt(i);

            if (!observations.isObserved(variableX)) {
                notRemoved.insertNode(variableX);
                pairTable.addElement(variableX);
            }
        }

        currentRelations = getInitialRelations();

        // Now restrict the initial relations to the obervations.
        if (observations.size() > 0) {
            restrictToObservations(currentRelations);
        }

        for (i = 0; i < currentRelations.size(); i++) {
            pairTable.addRelation(currentRelations.elementAt(i));
        }

        for (i = notRemoved.size(); i > 0; i--) {
            // Next variable to remove
            variableX = pairTable.nextToRemove();

            // This variable will be in position (i-1) in results
            // and in currentConf[].
            //positions.put(variableX,new Integer(i-1));
            notRemoved.removeNode(variableX);
            pairTable.removeVariable(variableX);
            deletionSequence.insertNode(variableX);

            listpotentials = new Vector();

            for (j = 0; j < currentRelations.size(); j++) {
                listpotentials.add(currentRelations.elementAt(j).getValues());
            }

            // Get the relations containing the variable and remove them
            // from the list.
            tempList = currentRelations.getRelationsOfAndRemove(variableX);

            // Remove them also from the search table.
            rel = tempList.elementAt(0);
            pairTable.removeRelation(rel);
            pot = rel.getValues();

            for (j = 1; j < tempList.size(); j++) {
                rel = tempList.elementAt(j);
                pairTable.removeRelation(rel);
                pot2 = pot.combine(rel.getValues());
                if (pot2.getSize() <= limitsize) {
                    pot = pot2;
                } else {
                    varsMax = (Vector) pot.getVariables().clone();
                    varsMax.remove(variableX);

                    pot = pot.maxMarginalizePotential(varsMax);

                    rel2 = new Relation();

                    rel2.setKind(Relation.POTENTIAL);
                    rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                    rel2.setValues(pot);
                    currentRelations.insertRelation(rel2);
                    pairTable.addRelation(rel2);
                    pot = rel.getValues();
                }
            }

            // Put the obtained list of relations as the sampling
            // distribution of the variable (initially).
            maxPotentials.addElement(listpotentials);

            varsMax = (Vector) pot.getVariables().clone();
            varsMax.remove(variableX);

            pot = pot.maxMarginalizePotential(varsMax);

            rel = new Relation();

            rel.setKind(Relation.POTENTIAL);
            rel.getVariables().setNodes((Vector) pot.getVariables().clone());
            rel.setValues(pot);
            currentRelations.insertRelation(rel);
            pairTable.addRelation(rel);
        }

        maxvalue = 1.0;
        c = new Configuration();

        for (i = 0; i < currentRelations.size(); i++) {
            maxvalue *= currentRelations.elementAt(i).getValues().getValue(c);
        }

    }
    
    public long posiblesValores(Vector variables){
        long numero_valores = 1;
        FiniteStates variable;
        int numero_variables = variables.size();
        int numero_estados;
        
        for(int i = 0; i < numero_variables; i++){
            variable = (FiniteStates) variables.elementAt(i);
            numero_estados = variable.getStates().size();
            numero_valores = numero_valores*numero_estados;
        }
        
        return numero_valores;
    }
    
    public int indicePotencialMaximoTamanio(RelationList relaciones){
        int indice = 0;
        long tamanio, maximo_tamanio;
        int numero_relaciones = relaciones.size();
        
        Relation relacion = relaciones.elementAt(0);
        Potential potencial = relacion.getValues();
        maximo_tamanio = posiblesValores(potencial.getVariables());
        
        for(int i = 1; i < numero_relaciones; i++){
            relacion = relaciones.elementAt(i);
            potencial = relacion.getValues();
            tamanio = posiblesValores(potencial.getVariables());
            
            if(tamanio > maximo_tamanio){
                maximo_tamanio = tamanio;
                indice = i;
            }
        }
        
        return indice;
    }
    
    public boolean pertenencia(FiniteStates variable, Vector conjunto_variables){
        boolean pertenece = false;
        int numero_variables = conjunto_variables.size();
        FiniteStates candidata;
        
        for(int i = 0; i < numero_variables && !pertenece; i++){
            candidata = (FiniteStates) conjunto_variables.elementAt(i);
            
            if(candidata.equals(variable))
                pertenece = true;
        }
        
        return pertenece;
    }
    
    public Vector unionVariables(Vector variables1, Vector variables2){
        Vector union = new Vector();
        int numero_variables1 = variables1.size();
        int numero_variables2 = variables2.size();
        FiniteStates variable;
        boolean pertenece;
        
        for(int i = 0; i < numero_variables1; i++){
            variable = (FiniteStates) variables1.elementAt(i);
            union.add(variable);
        }
        
        for(int i = 0; i < numero_variables2; i++){
            variable = (FiniteStates) variables2.elementAt(i);
            pertenece = pertenencia(variable, variables1);
            
            if(!pertenece)
                union.add(variable);
        }
        
        return union;
    }
    
    public Vector interseccionVariables(Vector variables1, Vector variables2){
        Vector interseccion = new Vector();
        int numero_variables = variables1.size();
        FiniteStates variable;
        boolean pertenece;
        
        for(int i = 0; i < numero_variables; i++){
            variable = (FiniteStates) variables1.elementAt(i);
            pertenece = pertenencia(variable, variables2);
            
            if(pertenece)
                interseccion.add(variable);
        }
        
        return interseccion;
    }
    
    public Vector diferencia(Vector variables1, Vector variables2){
        Vector diferencia = new Vector();
        
        int numero_variables = variables1.size();
        FiniteStates variable;
        boolean pertenece;
        
        for(int i = 0; i < numero_variables; i++){
            variable = (FiniteStates) variables1.elementAt(i);
            pertenece = pertenencia(variable, variables2);
            
            if(!pertenece)
                diferencia.add(variable);
        }
        
        
        return diferencia;
    }
    
    
    public boolean potencialCandidato(Potential potencial_acumulado, Potential potencial){
        Vector variables1 = potencial_acumulado.getVariables();
        Vector variables2 = potencial.getVariables();
        Vector variables_union = unionVariables(variables1, variables2);
        long tamanio_potencial;
        boolean candidato;
   
        tamanio_potencial = posiblesValores(variables_union);
        
        candidato = (tamanio_potencial <= limitsize)||(limitsize==0);
        
        return candidato;
    }
    
     public int seleccionCandidato6(Potential potencial_acumulado,RelationList relaciones){
        int indice_candidato = -1;
        boolean candidato;
        long tamanio;
        long tamanio_minimo = (long) Double.POSITIVE_INFINITY;
        int numero_relaciones = relaciones.size();
        Vector variables1 = potencial_acumulado.getVariables();
        Vector variables2;
        Vector diferencia_variables;
        Relation relacion;
        Potential potencial;       
        
        for(int i = 0; i < numero_relaciones; i++){
            relacion = relaciones.elementAt(i);
            potencial = relacion.getValues();
            candidato = potencialCandidato(potencial_acumulado, potencial);
            
            if(candidato){            
                variables2 = potencial.getVariables();
                tamanio = posiblesValores(unionVariables(variables2, variables1));

                if(tamanio < tamanio_minimo){
                    indice_candidato = i;
                    tamanio_minimo = tamanio;
                }
            }
        }
        
        return indice_candidato;
    }
     
      public int seleccionCandidato0(Potential potencial_acumulado,RelationList relaciones){
        int indice_candidato = -1;
        boolean candidato;
        long tamanio;
        long tamanio_maximo = (long) Double.NEGATIVE_INFINITY;
        int numero_relaciones = relaciones.size();
        Vector variables1 = potencial_acumulado.getVariables();
        Vector variables2;
        Vector diferencia_variables;
        Relation relacion;
        Potential potencial;       
        
        for(int i = 0; i < numero_relaciones; i++){
            relacion = relaciones.elementAt(i);
            potencial = relacion.getValues();
            candidato = potencialCandidato(potencial_acumulado, potencial);
            
            if(candidato){            
                variables2 = potencial.getVariables();
                tamanio = posiblesValores(unionVariables(variables2, variables1));

                if(tamanio > tamanio_maximo){
                    indice_candidato = i;
                    tamanio_maximo = tamanio;
                }
            }
        }
        
        return indice_candidato;
    }
    
    public int seleccionCandidato1(Potential potencial_acumulado,RelationList relaciones){
        int indice_candidato = -1;
        boolean candidato;
        long tamanio;
        long tamanio_minimo = (long) Double.POSITIVE_INFINITY;
        int numero_relaciones = relaciones.size();
        Vector variables1 = potencial_acumulado.getVariables();
        Vector variables2;
        Vector diferencia_variables;
        Relation relacion;
        Potential potencial;       
        
        for(int i = 0; i < numero_relaciones; i++){
            relacion = relaciones.elementAt(i);
            potencial = relacion.getValues();
            candidato = potencialCandidato(potencial_acumulado, potencial);
            
            if(candidato){            
                variables2 = potencial.getVariables();
                diferencia_variables = diferencia(variables2, variables1);
                tamanio = posiblesValores(diferencia_variables);

                if(tamanio < tamanio_minimo){
                    indice_candidato = i;
                    tamanio_minimo = tamanio;
                }
            }
        }
        
        return indice_candidato;
    }
    
    
    
    
    public int seleccionCandidato2(Potential potencial_acumulado,RelationList relaciones){
        int indice_candidato = -1;
        boolean candidato;
        long tamanio;
        long tamanio_maximo = (long) Double.NEGATIVE_INFINITY;
        int numero_relaciones = relaciones.size();
        Vector variables1 = potencial_acumulado.getVariables();
        Vector variables2;
        Vector interseccion_variables;
        Relation relacion;
        Potential potencial;       
        
        for(int i = 0; i < numero_relaciones; i++){
            relacion = relaciones.elementAt(i);
            potencial = relacion.getValues();
            candidato = potencialCandidato(potencial_acumulado, potencial);
            
            if(candidato){            
                variables2 = potencial.getVariables();
                interseccion_variables = interseccionVariables(variables2, variables1);
                tamanio = posiblesValores(interseccion_variables);

                if(tamanio > tamanio_maximo){
                    indice_candidato = i;
                    tamanio_maximo = tamanio;
                }
            }
        }
        
        return indice_candidato;
    }
    
    public int seleccionCandidato3(Potential potencial_acumulado,RelationList relaciones){
        int indice_candidato = -1;
        boolean candidato;
        long tamanio1, tamanio2;
        long tamanio;
        long tamanio_minimo = (long) Double.POSITIVE_INFINITY;
        int numero_relaciones = relaciones.size();
        Vector variables1 = potencial_acumulado.getVariables();
        Vector variables2;
        Vector interseccion_variables;
        Vector diferencia_variables;
        Relation relacion;
        Potential potencial;       
        
        for(int i = 0; i < numero_relaciones; i++){
            relacion = relaciones.elementAt(i);
            potencial = relacion.getValues();
            candidato = potencialCandidato(potencial_acumulado, potencial);
            
            if(candidato){            
                variables2 = potencial.getVariables();
                interseccion_variables = interseccionVariables(variables2, variables1);
                tamanio2 = posiblesValores(interseccion_variables);
                diferencia_variables = diferencia(variables2, variables1);
                tamanio1 = posiblesValores(diferencia_variables);
                tamanio = tamanio1/tamanio2;

                if(tamanio < tamanio_minimo){
                    indice_candidato = i;
                    tamanio_minimo = tamanio;
                }
            }
        }
        
        return indice_candidato;
    }
    
    public int seleccionCandidato4(Potential potencial_acumulado,RelationList relaciones){
        int indice_candidato = -1;
        boolean candidato;
        long tamanio1, tamanio2;
        long tamanio;
        long tamanio_minimo = (long) Double.POSITIVE_INFINITY;
        int numero_relaciones = relaciones.size();
        Vector variables1 = potencial_acumulado.getVariables();
        Vector variables2;
        Vector interseccion_variables;
        Vector diferencia_variables;
        Relation relacion;
        Potential potencial;       
        
        for(int i = 0; i < numero_relaciones; i++){
            relacion = relaciones.elementAt(i);
            potencial = relacion.getValues();
            candidato = potencialCandidato(potencial_acumulado, potencial);
            
            if(candidato){            
                variables2 = potencial.getVariables();
                interseccion_variables = interseccionVariables(variables2, variables1);
                tamanio2 = posiblesValores(interseccion_variables);
                diferencia_variables = diferencia(variables2, variables1);
                tamanio1 = posiblesValores(diferencia_variables);
                tamanio = tamanio1 - tamanio2;

                if(tamanio < tamanio_minimo){
                    indice_candidato = i;
                    tamanio_minimo = tamanio;
                }
            }
        }
        
        return indice_candidato;
    }
    
    public int seleccionCandidato5(Potential potencial_acumulado,RelationList relaciones){
        int indice_candidato = -1;
        boolean candidato;
        long tamanio1, tamanio2;
        long tamanio;
        long tamanio_minimo = (long) Double.POSITIVE_INFINITY;
        int numero_relaciones = relaciones.size();
        Vector variables1 = potencial_acumulado.getVariables();
        Vector variables2;
        Vector union_variables;
        Relation relacion;
        Potential potencial;       
        
        for(int i = 0; i < numero_relaciones; i++){
            relacion = relaciones.elementAt(i);
            potencial = relacion.getValues();
            candidato = potencialCandidato(potencial_acumulado, potencial);
            
            if(candidato){            
                variables2 = potencial.getVariables();
                union_variables = unionVariables(variables2, variables1);
                tamanio1 = posiblesValores(union_variables);
                tamanio2 = posiblesValores(variables2);
                tamanio = tamanio1 - tamanio2;

                if(tamanio < tamanio_minimo){
                    indice_candidato = i;
                    tamanio_minimo = tamanio;
                }
            }
        }
        
        return indice_candidato;
    }
    
     public int seleccionCandidatoTree(Potential potencial_acumulado,RelationList relaciones){
        int indice_candidato = -1;
        long tamanio1, tamanio2;
        long tamanio;
        long tamanio_minimo = (long) Double.POSITIVE_INFINITY;
        int numero_relaciones = relaciones.size();
        Vector variables1 = potencial_acumulado.getVariables();
        Vector variables2;
        Vector union_variables;
        Relation relacion;
        Potential potencial;       
        
        for(int i = 0; i < numero_relaciones; i++){
                relacion = relaciones.elementAt(i);
                potencial = relacion.getValues();
                variables2 = potencial.getVariables();
                union_variables = unionVariables(variables2, variables1);
                tamanio1 = posiblesValores(union_variables);
                tamanio2 = posiblesValores(variables2);
                tamanio = tamanio1 - tamanio2;

                if(tamanio < tamanio_minimo){
                    indice_candidato = i;
                    tamanio_minimo = tamanio;
                }
         
        }
        
        return indice_candidato;
    }
    
     public void getApproxMaxPotentials2(int opcion)   throws IOException {
        NodeList notRemoved;
        Node variableX;
        Relation rel, rel2;
        RelationList currentRelations, tempList;
        Potential pot, pot2;
        PairTable pairTable;
        int i, j, s;
        Vector varsMax;
        Configuration c;
        Vector listpotentials,listpotentials2;
        int indice_maximo_tamanio;
        int numero_potenciales;
        int indice_candidato;
        
             

      //     BufferedWriter bw = new BufferedWriter(new FileWriter("calculos2",true));
        

      //  bw.write("**********************\n");
      //  bw.flush();
        
        notRemoved = new NodeList();
        pairTable = new PairTable();

        deletionSequence = new NodeList();
        maxPotentials = new Vector();
maxsPotentials = new Vector();
        // Select the variables to remove (those not observed).
        s = network.getNodeList().size();

        for (i = 0; i < s; i++) {
            variableX = (FiniteStates) network.getNodeList().elementAt(i);

            if (!observations.isObserved(variableX)) {
                notRemoved.insertNode(variableX);
                pairTable.addElement(variableX);
            }
        }

        currentRelations = getInitialRelations();

        // Now restrict the initial relations to the obervations.
        if (observations.size() > 0) {
            restrictToObservations(currentRelations);
        }

        for (i = 0; i < currentRelations.size(); i++) {
            pairTable.addRelation(currentRelations.elementAt(i));
        }
    pot = new PotentialTable();
        
        for (i = notRemoved.size(); i > 0; i--) {
            // Next variable to remove
            variableX = pairTable.nextToRemove();
            System.out.println("Borrando Variable " + i);
     //     bw.write("Removed variable " + variableX.getName()+"\n");
     //     bw.flush();
          
            // This variable will be in position (i-1) in results
            // and in currentConf[].
            //positions.put(variableX,new Integer(i-1));
            notRemoved.removeNode(variableX);
            pairTable.removeVariable(variableX);
            deletionSequence.insertNode(variableX);
            listpotentials = new Vector();

            
             listpotentials = new Vector();
              listpotentials2 = new Vector();

            for (j = 0; j < currentRelations.size(); j++) {
                pot = currentRelations.elementAt(j).getValues();
               if(pot.getVariables().contains(variableX)) {
                listpotentials.add(pot);
               }
               else{
                   listpotentials2.add(pot);
               }
            }
            

            // Get the relations containing the variable and remove them
            // from the list.
            tempList = currentRelations.getRelationsOfAndRemove(variableX);
            indice_maximo_tamanio = indicePotencialMaximoTamanio(tempList);
            
            // Remove them also from the search table.
            rel = tempList.elementAt(indice_maximo_tamanio);
            pairTable.removeRelation(rel);
            pot = rel.getValues();
       //     System.out.println("First Potential " + pot.getVariables() + pot.getSize());
            tempList.removeElementAt(indice_maximo_tamanio);
            numero_potenciales = tempList.size();

            for (j = 1; j <= numero_potenciales; j++) {
                
                switch (opcion) {
                    case 0: 
                        indice_candidato = seleccionCandidato0(pot,tempList);
                        break;
                    case 1:
                        indice_candidato = seleccionCandidato1(pot,tempList);
                        break;
                    case 2:
                        indice_candidato = seleccionCandidato2(pot,tempList);
                        break;
                    case 3:
                        indice_candidato = seleccionCandidato3(pot,tempList);
                        break;
                    case 4:
                        indice_candidato = seleccionCandidato4(pot,tempList);
                        break;
                    case 5:
                        indice_candidato = seleccionCandidato5(pot,tempList);
                        break;
                           case 6:
                        indice_candidato = seleccionCandidato6(pot,tempList);
                        break;
                    default:
                        indice_candidato = indicePotencialMaximoTamanio(tempList);
                        break;
                }
                
                if(indice_candidato != -1){
                
                      //      System.out.println("First Potential " + pot.getVariables()+ pot.getSize());
                    
                    
          //        
          
                    
                    rel = tempList.elementAt(indice_candidato);
                   //  System.out.println("Combining with  " + rel.getValues().getVariables()+ rel.getValues().getSize());
                    pot2 = pot.combine(rel.getValues());
                  // System.out.println("Result " + pot2.getVariables()+ pot2.getSize());

                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel);

                    if (pot2.getSize() <= limitsize) {
                        pot = pot2;    
                    }
                
                    else {
                        varsMax = (Vector) pot.getVariables().clone();
                        varsMax.remove(variableX);

                        pot = pot.maxMarginalizePotential(varsMax);

                        rel2 = new Relation();

                        rel2.setKind(Relation.POTENTIAL);
                        rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                        rel2.setValues(pot);
                        currentRelations.insertRelation(rel2);
                        pairTable.addRelation(rel2);
                        pot = rel.getValues();
                    }
                }
                
                else{
                    
                    indice_candidato = indicePotencialMaximoTamanio(tempList);
                    rel = tempList.elementAt(indice_candidato);
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel); 
                    varsMax = (Vector) pot.getVariables().clone();
                    varsMax.remove(variableX);
                    pot = pot.maxMarginalizePotential(varsMax);

                    rel2 = new Relation();

                    rel2.setKind(Relation.POTENTIAL);
                    rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                    rel2.setValues(pot);
                    currentRelations.insertRelation(rel2);
                    pairTable.addRelation(rel2);
                    pot = rel.getValues();
                    
                }
            }
            
            // Put the obtained list of relations as the sampling
            // distribution of the variable (initially).
           maxPotentials.addElement(listpotentials);
            maxsPotentials.addElement(listpotentials2);

            varsMax = (Vector) pot.getVariables().clone();
            varsMax.remove(variableX);

            pot = pot.maxMarginalizePotential(varsMax);

            rel = new Relation();

            rel.setKind(Relation.POTENTIAL);
            rel.getVariables().setNodes((Vector) pot.getVariables().clone());
            rel.setValues(pot);
            
            currentRelations.insertRelation(rel);
            pairTable.addRelation(rel);
        }
        
        pot.print();

        maxvalue = 1.0;
        c = new Configuration();

        for (i = 0; i < currentRelations.size(); i++) {
            maxvalue *= currentRelations.elementAt(i).getValues().getValue(c);
        }
        
     }
     
        public void getApproxMaxPotentials3(double epsilon, int optionm, int options)  throws IOException {
        NodeList notRemoved;
        Node variableX;
        Relation rel, rel2;
        RelationList currentRelations, tempList;
        Potential pot, pot2;
        PairTable pairTable;
        int i, j, s;
        Vector varsMax;
        Configuration c;
        Vector listpotentials;
        int indice_maximo_tamanio;
        int numero_potenciales;
        int indice_candidato;
        
        notRemoved = new NodeList();
        pairTable = new PairTable();

     //      BufferedWriter bw = new BufferedWriter(new FileWriter("calculos3",true));
        

     //   bw.write("**********************\n");
     //   bw.flush();
        deletionSequence = new NodeList();
        maxPotentials = new Vector();

        // Select the variables to remove (those not observed).
        s = network.getNodeList().size();

        for (i = 0; i < s; i++) {
            variableX = (FiniteStates) network.getNodeList().elementAt(i);

            if (!observations.isObserved(variableX)) {
                notRemoved.insertNode(variableX);
                pairTable.addElement(variableX);
            }
        }

        currentRelations = getInitialRelations();

        // Now restrict the initial relations to the obervations.
      
        System.out.println("empizo pasando a arboles");
        
            restrictToObservationstoTree(currentRelations);
           approximateInitial(currentRelations,epsilon,optionm);

        for (i = 0; i < currentRelations.size(); i++) {
            pairTable.addRelation(currentRelations.elementAt(i));
        }

        pot = new PotentialTree();
        for (i = notRemoved.size(); i > 0; i--) {
            // Next variable to remove
            variableX = pairTable.nextToRemove();
       //       bw.write("Removed variable " + variableX.getName()+"\n");
       //       bw.flush();
         //    if((i % 10) == 0) {
                 System.out.println("Borrando Variable " + i);
           // }
           
            
            // System.out.println("Removed variable " + variableX.getName());

            // This variable will be in position (i-1) in results
            // and in currentConf[].
            //positions.put(variableX,new Integer(i-1));
            notRemoved.removeNode(variableX);
            pairTable.removeVariable(variableX);
            deletionSequence.insertNode(variableX);
            listpotentials = new Vector();

            for (j = 0; j < currentRelations.size(); j++) {
                listpotentials.add(currentRelations.elementAt(j).getValues());
            }

            // Get the relations containing the variable and remove them
            // from the list.
            tempList = currentRelations.getRelationsOfAndRemove(variableX);
            indice_maximo_tamanio = indicePotencialMaximoTamanio(tempList);
            
            // Remove them also from the search table.
            rel = tempList.elementAt(indice_maximo_tamanio);
            pairTable.removeRelation(rel);
            pot = rel.getValues();
            // System.out.println("First Potential " + pot.getVariables()+ pot.getSize());
           //  pot.print();
            tempList.removeElementAt(indice_maximo_tamanio);
            numero_potenciales = tempList.size();

            for (j = 0; j < numero_potenciales; j++) {
                
               indice_candidato = seleccionCandidato5(pot,tempList);
                
                if(indice_candidato != -1){
                    rel = tempList.elementAt(indice_candidato);
           //          System.out.println("Starting COmbination");
                    
              //        System.out.println("First Potential " + pot.getVariables()+ pot.getSize());
                    
                    pot2 = pot.combine(rel.getValues());
          //        
             //      System.out.println("Combining with  " + rel.getValues().getVariables()+ rel.getValues().getSize());
          
            //       System.out.println("Result " + pot2.getVariables()+ pot2.getSize());

        //            System.out.println("Finishing COmbination");
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel);
            //        System.out.println("Starting Max prune");
            //        System.out.println( ((PotentialTree) pot2).getTree().getSize());
                //  System.out.println( ((PotentialTree) pot2).getTree().getSize());
                 ((PotentialTree) pot2).getTree().maxprune(epsilon,optionm);
                //  System.out.println( ((PotentialTree) pot2).getTree().getSize());
              //       System.out.println("Result after prunning" + pot2.getVariables()+ pot2.getSize());
             //       System.out.println("Finishing Max prune");
            //        System.out.println( ((PotentialTree) pot2).getTree().getSize());
                    if (( ((PotentialTree) pot2).getTree().getSize() <= limitsize)||(limitsize==0)) {
                        
                        pot = pot2;    
                    }
                
                    else {
                         System.out.println("Limite Excedido");
                        varsMax = (Vector) pot.getVariables().clone();
                        varsMax.remove(variableX);

                        pot = pot.maxMarginalizePotential(varsMax);
                         ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);
      //                  ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);
                        rel2 = new Relation();

                        rel2.setKind(Relation.POTENTIAL);
                        rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                        rel2.setValues(pot);
                        currentRelations.insertRelation(rel2);
                        pairTable.addRelation(rel2);
                        pot = rel.getValues();
                    }
                }
                
                else{
                    
                    indice_candidato = indicePotencialMaximoTamanio(tempList);
                    rel = tempList.elementAt(indice_candidato);
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel); 
                    varsMax = (Vector) pot.getVariables().clone();
                    varsMax.remove(variableX);
                    pot = pot.maxMarginalizePotential(varsMax);
                     ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);
        //            ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);
                    rel2 = new Relation();

                    rel2.setKind(Relation.POTENTIAL);
                    rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                    rel2.setValues(pot);
                    currentRelations.insertRelation(rel2);
                    pairTable.addRelation(rel2);
                    pot = rel.getValues();
                    
                }
            }
            
            // Put the obtained list of relations as the sampling
            // distribution of the variable (initially).
            maxPotentials.addElement(listpotentials);

            varsMax = (Vector) pot.getVariables().clone();
            varsMax.remove(variableX);

            pot = pot.maxMarginalizePotential(varsMax);
           ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);
            rel = new Relation();

            rel.setKind(Relation.POTENTIAL);
            rel.getVariables().setNodes((Vector) pot.getVariables().clone());
            rel.setValues(pot);
            
            currentRelations.insertRelation(rel);
            pairTable.addRelation(rel);
        }
         
        pot.print();
        
        maxvalue = 1.0;
        c = new Configuration();

        for (i = 0; i < currentRelations.size(); i++) {
            maxvalue *= currentRelations.elementAt(i).getValues().getValue(c);
        }
        
     }

           public void getApproxMaxPotentialsSplit(double epsilon, int optionm, int options)  throws IOException {
        NodeList notRemoved;
        Node variableX;
        Relation rel, rel2;
        RelationList currentRelations, tempList;
        PotentialTree pot, potfree, pot2;
        PairTable pairTable;
        int i, j, s;
        Vector varsMax;
        Configuration c;
        Vector listpotentials;
        int indice_maximo_tamanio;
        int numero_potenciales;
        int indice_candidato;
        Vector resplit;
        
        notRemoved = new NodeList();
        pairTable = new PairTable();

     //      BufferedWriter bw = new BufferedWriter(new FileWriter("calculos3",true));
        

     //   bw.write("**********************\n");
     //   bw.flush();
        deletionSequence = new NodeList();
        maxPotentials = new Vector();

        // Select the variables to remove (those not observed).
        s = network.getNodeList().size();

        for (i = 0; i < s; i++) {
            variableX = (FiniteStates) network.getNodeList().elementAt(i);

            if (!observations.isObserved(variableX)) {
                notRemoved.insertNode(variableX);
                pairTable.addElement(variableX);
            }
        }

        currentRelations = getInitialRelations();

        // Now restrict the initial relations to the obervations.
      
        System.out.println("empizo pasando a arboles");
        
            restrictToObservationstoTree(currentRelations);
           approximateInitial(currentRelations,epsilon,optionm);

        for (i = 0; i < currentRelations.size(); i++) {
            pairTable.addRelation(currentRelations.elementAt(i));
        }

        pot = new PotentialTree();
        for (i = notRemoved.size(); i > 0; i--) {
            // Next variable to remove
            variableX = pairTable.nextToRemove();
       //       bw.write("Removed variable " + variableX.getName()+"\n");
       //       bw.flush();
         //    if((i % 10) == 0) {
                 System.out.println("Borrando Variable numero" + i);
           // }
           
            
            // System.out.println("Removed variable " + variableX.getName());

            // This variable will be in position (i-1) in results
            // and in currentConf[].
            //positions.put(variableX,new Integer(i-1));
            notRemoved.removeNode(variableX);
            pairTable.removeVariable(variableX);
            deletionSequence.insertNode(variableX);
            listpotentials = new Vector();

            for (j = 0; j < currentRelations.size(); j++) {
                listpotentials.add(currentRelations.elementAt(j).getValues());
            }

            // Get the relations containing the variable and remove them
            // from the list.
            tempList = currentRelations.getRelationsOfAndRemove(variableX);
            
           
            
            indice_maximo_tamanio = indicePotencialMaximoTamanio(tempList);
            
            // Remove them also from the search table.
            
            rel = tempList.elementAt(indice_maximo_tamanio);
            pairTable.removeRelation(rel);
            pot = (PotentialTree) rel.getValues();
            System.out.println("calling split");
            resplit = pot.split2((FiniteStates) variableX);
            potfree = (PotentialTree) resplit.elementAt(0);
            pot = (PotentialTree) resplit.elementAt(1);
            rel2 = new Relation();

                    rel2.setKind(Relation.POTENTIAL);
                    rel2.getVariables().setNodes((Vector) potfree.getVariables().clone());
                    rel2.setValues(potfree);
                    currentRelations.insertRelation(rel2);
                    pairTable.addRelation(rel2);
            
            // System.out.println("First Potential " + pot.getVariables()+ pot.getSize());
           //  pot.print();
            tempList.removeElementAt(indice_maximo_tamanio);
            numero_potenciales = tempList.size();

            for (j = 0; j < numero_potenciales; j++) {
                
               indice_candidato = seleccionCandidato5(pot,tempList);
                
                if(indice_candidato != -1){
                    rel = tempList.elementAt(indice_candidato);
           //          System.out.println("Starting COmbination");
                     pot2 = (PotentialTree) rel.getValues();
                     resplit = pot2.split2((FiniteStates) variableX);
                     potfree = (PotentialTree) resplit.elementAt(0);
                      pot2 = (PotentialTree) resplit.elementAt(1);
                      rel2 = new Relation();

                    rel2.setKind(Relation.POTENTIAL);
                    rel2.getVariables().setNodes((Vector) potfree.getVariables().clone());
                    rel2.setValues(potfree);
                    currentRelations.insertRelation(rel2);
                    pairTable.addRelation(rel2);
              //        System.out.println("First Potential " + pot.getVariables()+ pot.getSize());
                    
                    pot2 = (PotentialTree) pot.combine(pot2);
          //        
             //      System.out.println("Combining with  " + rel.getValues().getVariables()+ rel.getValues().getSize());
          
            //       System.out.println("Result " + pot2.getVariables()+ pot2.getSize());

        //            System.out.println("Finishing COmbination");
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel);
            //        System.out.println("Starting Max prune");
            //        System.out.println( ((PotentialTree) pot2).getTree().getSize());
                //  System.out.println( ((PotentialTree) pot2).getTree().getSize());
                 ((PotentialTree) pot2).getTree().maxprune(epsilon,optionm);
                //  System.out.println( ((PotentialTree) pot2).getTree().getSize());
              //       System.out.println("Result after prunning" + pot2.getVariables()+ pot2.getSize());
             //       System.out.println("Finishing Max prune");
            //        System.out.println( ((PotentialTree) pot2).getTree().getSize());
                    if (( ((PotentialTree) pot2).getTree().getSize() <= limitsize)||(limitsize==0)) {
                        
                        pot = pot2;    
                    }
                
                    else {
                         System.out.println("Limite Excedido");
                        varsMax = (Vector) pot.getVariables().clone();
                        varsMax.remove(variableX);

                        pot = (PotentialTree) pot.maxMarginalizePotential(varsMax);
                         ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);
      //                  ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);
                        rel2 = new Relation();

                        rel2.setKind(Relation.POTENTIAL);
                        rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                        rel2.setValues(pot);
                        currentRelations.insertRelation(rel2);
                        pairTable.addRelation(rel2);
                        pot = (PotentialTree) rel.getValues();
                    }
                }
                
                else{
                    
                    indice_candidato = indicePotencialMaximoTamanio(tempList);
                    rel = tempList.elementAt(indice_candidato);
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel); 
                    varsMax = (Vector) pot.getVariables().clone();
                    varsMax.remove(variableX);
                    pot = (PotentialTree) pot.maxMarginalizePotential(varsMax);
                     ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);
        //            ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);
                    rel2 = new Relation();

                    rel2.setKind(Relation.POTENTIAL);
                    rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                    rel2.setValues(pot);
                    currentRelations.insertRelation(rel2);
                    pairTable.addRelation(rel2);
                    pot = (PotentialTree) rel.getValues();
                    
                }
            }
            
            // Put the obtained list of relations as the sampling
            // distribution of the variable (initially).
            maxPotentials.addElement(listpotentials);

            varsMax = (Vector) pot.getVariables().clone();
            varsMax.remove(variableX);

            pot =(PotentialTree) pot.maxMarginalizePotential(varsMax);
           ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);
            rel = new Relation();

            rel.setKind(Relation.POTENTIAL);
            rel.getVariables().setNodes((Vector) pot.getVariables().clone());
            rel.setValues(pot);
            
            currentRelations.insertRelation(rel);
            pairTable.addRelation(rel);
        }
         
        pot.print();
        
        maxvalue = 1.0;
        c = new Configuration();

        for (i = 0; i < currentRelations.size(); i++) {
            maxvalue *= currentRelations.elementAt(i).getValues().getValue(c);
        }
        
     }

        public void getApproxMaxPotentialstest(int opcion, boolean istree)   throws IOException {
        NodeList notRemoved;
        Node variableX;
        Relation rel, rel2;
        RelationList currentRelations, tempList;
        Potential pot, pot2;
        PairTable pairTable;
        int i, j, s;
        Vector varsMax;
        Configuration c;
        Vector listpotentials;
        int indice_maximo_tamanio;
        int numero_potenciales;
        int indice_candidato;
        
             

      //     BufferedWriter bw = new BufferedWriter(new FileWriter("calculos2",true));
        

      //  bw.write("**********************\n");
      //  bw.flush();
        
        notRemoved = new NodeList();
        pairTable = new PairTable();

        deletionSequence = new NodeList();
        maxPotentials = new Vector();

        // Select the variables to remove (those not observed).
        s = network.getNodeList().size();

        for (i = 0; i < s; i++) {
            variableX = (FiniteStates) network.getNodeList().elementAt(i);

            if (!observations.isObserved(variableX)) {
                notRemoved.insertNode(variableX);
                pairTable.addElement(variableX);
            }
        }

        currentRelations = getInitialRelations();
      if (istree) {
         // restrictToObservations(currentRelations);}
          restrictToObservationstoTree(currentRelations);}
      else {
        // Now restrict the initial relations to the obervations.
     //   if (observations.size() > 0) {
            restrictToObservations(currentRelations);
       // }
      }
        
        
        for (i = 0; i < currentRelations.size(); i++) {
          //   currentRelations.elementAt(i).setValues(new PotentialTable(currentRelations.elementAt(i).getValues()));
            pairTable.addRelation(currentRelations.elementAt(i));
        }
    pot = new PotentialTable();
        
        for (i = notRemoved.size(); i > 0; i--) {
            // Next variable to remove
            variableX = pairTable.nextToRemove();
            System.out.println("Borrando Variable " + variableX.getName());
     //     bw.write("Removed variable " + variableX.getName()+"\n");
     //     bw.flush();
          
            // This variable will be in position (i-1) in results
            // and in currentConf[].
            //positions.put(variableX,new Integer(i-1));
            notRemoved.removeNode(variableX);
            pairTable.removeVariable(variableX);
            deletionSequence.insertNode(variableX);
            listpotentials = new Vector();

            for (j = 0; j < currentRelations.size(); j++) {
               
                listpotentials.add(currentRelations.elementAt(j).getValues());
                
//                  bw.flush();
            }

            // Get the relations containing the variable and remove them
            // from the list.
            tempList = currentRelations.getRelationsOfAndRemove(variableX);
            indice_maximo_tamanio = indicePotencialMaximoTamanio(tempList);
            
            // Remove them also from the search table.
            rel = tempList.elementAt(indice_maximo_tamanio);
            pairTable.removeRelation(rel);
            pot = rel.getValues();
    //  pot =    new PotentialTable(rel.getValues());
         //   System.out.println("First Potential " + pot.getVariables() + pot.getSize());
            tempList.removeElementAt(indice_maximo_tamanio);
            numero_potenciales = tempList.size();

            for (j = 1; j <= numero_potenciales; j++) {
                
                switch (opcion) {
                    case 0: 
                        indice_candidato = seleccionCandidato0(pot,tempList);
                        break;
                    case 1:
                        indice_candidato = seleccionCandidato1(pot,tempList);
                        break;
                    case 2:
                        indice_candidato = seleccionCandidato2(pot,tempList);
                        break;
                    case 3:
                        indice_candidato = seleccionCandidato3(pot,tempList);
                        break;
                    case 4:
                        indice_candidato = seleccionCandidato4(pot,tempList);
                        break;
                    case 5:
                        indice_candidato = seleccionCandidato5(pot,tempList);
                        break;
                           case 6:
                        indice_candidato = seleccionCandidato6(pot,tempList);
                        break;
                    default:
                        indice_candidato = indicePotencialMaximoTamanio(tempList);
                        break;
                }
                
                if(indice_candidato != -1){
                
                      //      System.out.println("First Potential " + pot.getVariables()+ pot.getSize());
                    
                    
          //        
          
                    
                    rel = tempList.elementAt(indice_candidato);
                    
                    
           //          System.out.println("Combining with  " + rel.getValues().getVariables()+ rel.getValues().getSize());
                  pot2 = pot.combine(rel.getValues());
//  pot2 = pot.combine(new PotentialTable(rel.getValues()));
           //        System.out.println("Result " + pot2.getVariables()+ pot2.getSize());
               //   pot = new PotentialTable(pot);
               //     pot2 = new PotentialTable(pot2);
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel);
               //     System.out.println(pot2.getSize());
                    if (posiblesValores(pot2.getVariables()) <= limitsize) {
                        pot = pot2;    
                    }
                
                    else {
                        varsMax = (Vector) pot.getVariables().clone();
                        varsMax.remove(variableX);
                //        pot = new PotentialTree(pot);
                        pot = pot.maxMarginalizePotential(varsMax);
                  //      pot = new PotentialTable(pot);
                        rel2 = new Relation();

                        rel2.setKind(Relation.POTENTIAL);
                        rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                        rel2.setValues(pot);
                        currentRelations.insertRelation(rel2);
                        pairTable.addRelation(rel2);
                         pot = rel.getValues();
                 //       pot = new PotentialTable(rel.getValues());
                    }
                }
                
                else{
                    
                    indice_candidato = indicePotencialMaximoTamanio(tempList);
                    rel = tempList.elementAt(indice_candidato);
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel); 
                    varsMax = (Vector) pot.getVariables().clone();
                    varsMax.remove(variableX);
                   // pot = new PotentialTree(pot);
                        pot = pot.maxMarginalizePotential(varsMax);
                 //       pot = new PotentialTable(pot);
                    rel2 = new Relation();

                    rel2.setKind(Relation.POTENTIAL);
                    rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                    rel2.setValues(pot);
                    currentRelations.insertRelation(rel2);
                    pairTable.addRelation(rel2);
                    pot = rel.getValues();
                    // pot = new PotentialTable(rel.getValues());
                    
                }
            }
            
            // Put the obtained list of relations as the sampling
            // distribution of the variable (initially).
            maxPotentials.addElement(listpotentials);

            varsMax = (Vector) pot.getVariables().clone();
            varsMax.remove(variableX);

      //     pot = new PotentialTree(pot);
                        pot = pot.maxMarginalizePotential(varsMax);
        //                pot = new PotentialTable(pot);

            rel = new Relation();

            rel.setKind(Relation.POTENTIAL);
            rel.getVariables().setNodes((Vector) pot.getVariables().clone());
            rel.setValues(pot);
            
            currentRelations.insertRelation(rel);
            pairTable.addRelation(rel);
        }
        
        pot.print();

        maxvalue = 1.0;
        c = new Configuration();

        for (i = 0; i < currentRelations.size(); i++) {
            maxvalue *= currentRelations.elementAt(i).getValues().getValue(c);
        }
        
     }
     
  public void getApproxMaxPotentials3s(double epsilon, int optionm, int options)  throws IOException {
        NodeList notRemoved;
        Node variableX;
        Relation rel, rel2;
        RelationList currentRelations, tempList;
        Potential pot, pot2;
        PairTable pairTable;
        int i, j, s;
        Vector varsMax;
        Configuration c;
        Vector listpotentials,listpotentials2;
        int indice_maximo_tamanio;
        int numero_potenciales;
        int indice_candidato;
        boolean knowndel=false;
        
        notRemoved = new NodeList();
        pairTable = new PairTable();

     //      BufferedWriter bw = new BufferedWriter(new FileWriter("calculos3",true));
        

     //   bw.write("**********************\n");
     //   bw.flush();
        if (deletionSequence==null) {
        deletionSequence = new NodeList();
       
        }
        else { knowndel = true;}
        maxPotentials = new Vector();
        maxsPotentials = new Vector();

        // Select the variables to remove (those not observed).
        s = network.getNodeList().size();
        
        
        
        for (i = 0; i < s; i++) {
            variableX = (FiniteStates) network.getNodeList().elementAt(i);

            if (!observations.isObserved(variableX)) {
                notRemoved.insertNode(variableX);
                pairTable.addElement(variableX);
            }
            else {deletionSequence.removeNode(variableX);}
        }

        System.out.println(notRemoved.size() + " " + deletionSequence.size());
        s = deletionSequence.size();
        currentRelations = getInitialRelations();
        System.out.println("empizo pasando a arboles");
        // Now restrict the initial relations to the obervations.
      
            restrictToObservationstoTree(currentRelations);
            approximateInitial(currentRelations,epsilon,optionm);

        for (i = 0; i < currentRelations.size(); i++) {
            pairTable.addRelation(currentRelations.elementAt(i));
        }

        for (i = notRemoved.size(); i > 0; i--) {
            // Next variable to remove
        if(knowndel) {
            variableX = deletionSequence.elementAt(s-i);
        }
        else {
            variableX = pairTable.nextToRemove();
        }
       //       bw.write("Removed variable " + variableX.getName()+"\n");
       //       bw.flush();
            
             System.out.println("Removed variable " + i);

            // This variable will be in position (i-1) in results
            // and in currentConf[].
            //positions.put(variableX,new Integer(i-1));
            notRemoved.removeNode(variableX);
            pairTable.removeVariable(variableX);
            if(!knowndel) {deletionSequence.insertNode(variableX);}
            listpotentials = new Vector();
              listpotentials2 = new Vector();

            for (j = 0; j < currentRelations.size(); j++) {
                pot = currentRelations.elementAt(j).getValues();
               if(pot.getVariables().contains(variableX)) {
                listpotentials.add(pot);
               }
               else{
                   listpotentials2.add(pot);
               }
            }

            // Get the relations containing the variable and remove them
            // from the list.
            tempList = currentRelations.getRelationsOfAndRemove(variableX);
            indice_maximo_tamanio = indicePotencialMaximoTamanio(tempList);
            
            // Remove them also from the search table.
            rel = tempList.elementAt(indice_maximo_tamanio);
            pairTable.removeRelation(rel);
            pot = rel.getValues();
            // System.out.println("First Potential " + pot.getVariables()+ pot.getSize());
           //  pot.print();
            tempList.removeElementAt(indice_maximo_tamanio);
            numero_potenciales = tempList.size();

            for (j = 0; j < numero_potenciales; j++) {
                
               indice_candidato = seleccionCandidato5(pot,tempList);
                
                if(indice_candidato != -1){
                    rel = tempList.elementAt(indice_candidato);
           //          System.out.println("Starting COmbination");
                    
              //        System.out.println("First Potential " + pot.getVariables()+ pot.getSize());
                    
                    pot2 = pot.combine(rel.getValues());
          //        
             //      System.out.println("Combining with  " + rel.getValues().getVariables()+ rel.getValues().getSize());
          
            //       System.out.println("Result " + pot2.getVariables()+ pot2.getSize());

        //            System.out.println("Finishing COmbination");
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel);
            //        System.out.println("Starting Max prune");
            //        System.out.println( ((PotentialTree) pot2).getTree().getSize());
                    ((PotentialTree) pot2).getTree().maxprune(epsilon,optionm);
              //       System.out.println("Result after prunning" + pot2.getVariables()+ pot2.getSize());
             //       System.out.println("Finishing Max prune");
            //        System.out.println( ((PotentialTree) pot2).getTree().getSize());
                    if (( ((PotentialTree) pot2).getTree().getSize() <= limitsize)||(limitsize==0)) {
                        
                        pot = pot2;    
                    }
                
                    else {
                        varsMax = (Vector) pot.getVariables().clone();
                        varsMax.remove(variableX);

                        pot = pot.maxMarginalizePotential(varsMax);
                    ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);

                        rel2 = new Relation();

                        rel2.setKind(Relation.POTENTIAL);
                        rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                        rel2.setValues(pot);
                        currentRelations.insertRelation(rel2);
                        pairTable.addRelation(rel2);
                        pot = rel.getValues();
                    }
                }
                
                else{
                    
                    indice_candidato = indicePotencialMaximoTamanio(tempList);
                    rel = tempList.elementAt(indice_candidato);
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel); 
                    varsMax = (Vector) pot.getVariables().clone();
                    varsMax.remove(variableX);
                    pot = pot.maxMarginalizePotential(varsMax);
                    ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);

                    rel2 = new Relation();

                    rel2.setKind(Relation.POTENTIAL);
                    rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                    rel2.setValues(pot);
                    currentRelations.insertRelation(rel2);
                    pairTable.addRelation(rel2);
                    pot = rel.getValues();
                    
                }
            }
            
            // Put the obtained list of relations as the sampling
            // distribution of the variable (initially).
            maxPotentials.addElement(listpotentials);
            maxsPotentials.addElement(listpotentials2);

            varsMax = (Vector) pot.getVariables().clone();
            varsMax.remove(variableX);

            pot = pot.maxMarginalizePotential(varsMax);
                    ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);

            rel = new Relation();

            rel.setKind(Relation.POTENTIAL);
            rel.getVariables().setNodes((Vector) pot.getVariables().clone());
            rel.setValues(pot);
            
            currentRelations.insertRelation(rel);
            pairTable.addRelation(rel);
        }

        maxvalue = 1.0;
        c = new Configuration();

        for (i = 0; i < currentRelations.size(); i++) {
            maxvalue *= currentRelations.elementAt(i).getValues().getValue(c);
        }
        
     }

  
  

         public void getApproxMaxPotentials5(double epsilon, int optionm, int options)  throws IOException {
        NodeList notRemoved;
        Node variableX;
        Relation rel, rel2;
        RelationList currentRelations, tempList;
        Potential pot, pot2;
        PairTable pairTable;
        int i, j, s;
        Vector varsMax;
        NodeList vars;
        Configuration c;
        Vector listpotentials;
        Vector sentpot;
        int indice_maximo_tamanio;
        int numero_potenciales;
        int indice_candidato;
        
        notRemoved = new NodeList();
        pairTable = new PairTable();

     //      BufferedWriter bw = new BufferedWriter(new FileWriter("calculos3",true));
        

     //   bw.write("**********************\n");
     //   bw.flush();
        deletionSequence = new NodeList();
        maxPotentials = new Vector();
        sentPotentials = new Vector();
        // Select the variables to remove (those not observed).
        s = network.getNodeList().size();

        for (i = 0; i < s; i++) {
            variableX = (FiniteStates) network.getNodeList().elementAt(i);

            if (!observations.isObserved(variableX)) {
                notRemoved.insertNode(variableX);
                pairTable.addElement(variableX);
            }
        }

        currentRelations = getInitialRelations();

        // Now restrict the initial relations to the obervations.
      
            restrictToObservationstoTree(currentRelations);
            approximateInitial(currentRelations,epsilon,optionm);

        for (i = 0; i < currentRelations.size(); i++) {
            pairTable.addRelation(currentRelations.elementAt(i));
        }

        for (i = notRemoved.size(); i > 0; i--) {
            // Next variable to remove
            variableX = pairTable.nextToRemove();
       //       bw.write("Removed variable " + variableX.getName()+"\n");
       //       bw.flush();
            
            // System.out.println("Removed variable " + variableX.getName());
            vars = new NodeList();
            // This variable will be in position (i-1) in results
            // and in currentConf[].
            //positions.put(variableX,new Integer(i-1));
            notRemoved.removeNode(variableX);
            pairTable.removeVariable(variableX);
            deletionSequence.insertNode(variableX);
            listpotentials = new Vector();
            sentpot = new Vector();
          
            // Get the relations containing the variable and remove them
            // from the list.
            tempList = currentRelations.getRelationsOfAndRemove(variableX);
              for (j = 0; j < tempList.size(); j++) {
                listpotentials.add(tempList.elementAt(j).getValues());
                vars.join(new NodeList(((Relation) tempList.elementAt(j)).getValues().getVariables()));
                
            }
  System.out.println("Removed variable " + i);
            indice_maximo_tamanio = indicePotencialMaximoTamanio(tempList);
            
            // Remove them also from the search table.
            rel = tempList.elementAt(indice_maximo_tamanio);
            pairTable.removeRelation(rel);
            pot = rel.getValues();
            
            // System.out.println("First Potential " + pot.getVariables()+ pot.getSize());
           //  pot.print();
            tempList.removeElementAt(indice_maximo_tamanio);
            numero_potenciales = tempList.size();

            for (j = 0; j < numero_potenciales; j++) {
                
                switch (options) {
                    case 1:
                        indice_candidato = seleccionCandidato1(pot,tempList);
                        break;
                    case 2:
                        indice_candidato = seleccionCandidato2(pot,tempList);
                        break;
                    case 3:
                        indice_candidato = seleccionCandidato3(pot,tempList);
                        break;
                    case 4:
                        indice_candidato = seleccionCandidato4(pot,tempList);
                        break;
                    case 5:
                        indice_candidato = seleccionCandidato5(pot,tempList);
                        break;
                         case 6:
                        indice_candidato = seleccionCandidato6(pot,tempList);
                        break;
                    default:
                        indice_candidato = indicePotencialMaximoTamanio(tempList);
                        break;
                }
                
                if(indice_candidato != -1){
                    rel = tempList.elementAt(indice_candidato);
           //          System.out.println("Starting COmbination");
                    
              //        System.out.println("First Potential " + pot.getVariables()+ pot.getSize());
                    
                    pot2 = pot.combine(rel.getValues());
          //        
             //      System.out.println("Combining with  " + rel.getValues().getVariables()+ rel.getValues().getSize());
          
            //       System.out.println("Result " + pot2.getVariables()+ pot2.getSize());

        //            System.out.println("Finishing COmbination");
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel);
            //        System.out.println("Starting Max prune");
            //        System.out.println( ((PotentialTree) pot2).getTree().getSize());
                    ((PotentialTree) pot2).getTree().maxprune(epsilon,optionm);
              //       System.out.println("Result after prunning" + pot2.getVariables()+ pot2.getSize());
             //       System.out.println("Finishing Max prune");
            //        System.out.println( ((PotentialTree) pot2).getTree().getSize());
                    if ((pot2.getSize() <= limitsize)||(limitsize==0)) {
                        
                        pot = pot2;    
                    }
                
                    else {
                      
                      if(sentpot.size()==0)  pot.setVariables(vars.getNodes());
                      varsMax = (Vector) pot.getVariables().clone();
                        varsMax.remove(variableX);
                        pot = pot.maxMarginalizePotential(varsMax);
                    ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);

                        rel2 = new Relation();
                         
                        sentpot.add(pot);
                        rel2.setKind(Relation.POTENTIAL);
                        rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                        rel2.setValues(pot);
                        currentRelations.insertRelation(rel2);
                        pairTable.addRelation(rel2);
                        pot = rel.getValues();
                    }
                }
                
                else{
                    
                    indice_candidato = indicePotencialMaximoTamanio(tempList);
                    rel = tempList.elementAt(indice_candidato);
                    tempList.removeElementAt(indice_candidato);
                    pairTable.removeRelation(rel); 
                      if(sentpot.size()==0)  pot.setVariables(vars.getNodes());
                      varsMax = (Vector) pot.getVariables().clone();
                        varsMax.remove(variableX);
                  
                    pot = pot.maxMarginalizePotential(varsMax);
                    ((PotentialTree) pot).getTree().maxprune(epsilon,optionm);

                    sentpot.add(pot);
                    rel2 = new Relation();
                      
                    rel2.setKind(Relation.POTENTIAL);
                    rel2.getVariables().setNodes((Vector) pot.getVariables().clone());
                    rel2.setValues(pot);
                    currentRelations.insertRelation(rel2);
                    pairTable.addRelation(rel2);
                    pot = rel.getValues();
                    
                }
            }
            
            // Put the obtained list of relations as the sampling
            // distribution of the variable (initially).
            maxPotentials.addElement(listpotentials);
            sentPotentials.addElement(sentpot);

            
         if(sentpot.size()==0)  pot.setVariables(vars.getNodes());
                      varsMax = (Vector) pot.getVariables().clone();
                        varsMax.remove(variableX);

            pot = pot.maxMarginalizePotential(varsMax);
            sentpot.add(pot);
            rel = new Relation();

            rel.setKind(Relation.POTENTIAL);
            rel.getVariables().setNodes((Vector) pot.getVariables().clone());
            rel.setValues(pot);
            
            currentRelations.insertRelation(rel);
            pairTable.addRelation(rel);
        }

        maxvalue = 1.0;
        c = new Configuration();

        for (i = 0; i < currentRelations.size(); i++) {
            maxvalue *= currentRelations.elementAt(i).getValues().getValue(c);
        }
        
     }
        
        
/**
 * Restricts a list of relations to the observations.
 * @param list the <code>RelationList</code> to restrict.
 */

public void restrictToObservations(RelationList list) {

  Relation rel;
  int i, s;

  s = list.size();

  for (i=0 ; i<s ; i++) {
    rel = list.elementAt(i);
    rel.setValues((rel.getValues()).restrictVariable(observations));
    rel.getVariables().setNodes(rel.getValues().getVariables());
  }
}

/**
 * Approximate initial relations.
 * @param list the <code>RelationList</code> to restrict.
 */

public void approximateInitial(RelationList list, double epsilon, int optionm) {

  Relation rel;
  int i, s;

  s = list.size();

  for (i=0 ; i<s ; i++) {
    rel = list.elementAt(i);
   // rel.getValues().print();
    ((PotentialTree) rel.getValues()).getTree().maxprune(epsilon,optionm);
   // rel.getValues().print();
  }
}

/**
 * Restricts a list of relations to the observations.
 * @param list the <code>RelationList</code> to restrict.
 */

public void restrictToObservationstoTree(RelationList list) {

  Relation rel;
  int i, s;

  s = list.size();

  for (i=0 ; i<s ; i++) {
      System.out.println("RElation "+i);
    rel = list.elementAt(i);
   // rel.getValues().print();
    rel.setValues((rel.getValues()).restrictVariabletoTree(observations));
   // rel.setValues(((PotentialTree) (rel.getValues())).sort());
    rel.getVariables().setNodes(rel.getValues().getVariables());
   // rel.getValues().print();
  }
}

    
public static void main(String args[]) throws ParseException, IOException {

  Bnet b;
  Evidence e;
  FileInputStream networkFile, evidenceFile, interestFile;
  MPE ain;
  int i;
  NodeList expSet;

  Vector explanations,postProb;
  Explanation best;

  
  if (args.length < 3){
    System.out.println("Too few arguments, the arguments are:");
    System.out.println("\tNetwork evidencefile outputfile");
  }
  else {
    networkFile = new FileInputStream(args[0]);
    b = new Bnet(networkFile);

  
    expSet = new NodeList();


      // trying if args[6] is the evidence
      
        evidenceFile = new FileInputStream(args[1]);
        e = new Evidence(evidenceFile,b.getNodeList());
   

    ain = new MPE(b,e,0.05,5000);
    
    ain.getMaxPotentials();
    
    ain.computeMaxConfigurations();
   

    ain.saveResults(args[2]);
    //System.in.read();

    // nuevo

    explanations = ain.getKBest();
 //   best = (Explanation) explanations.elementAt(0);
 //   postProb = best.toPosteriorProbability(b.getNodeList(),e);
    //for(i=0;i<postProb.size();i++)
    //  ((PotentialTable)postProb.elementAt(i)).print();
  }
}


}

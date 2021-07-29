/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira;

/**
 *
 * @author smc
 */

import java.io.*;
import elvira.Node;
import elvira.Continuous;

public class ContinuousDiscrete extends Continuous {
    
    
    /* The number of classes in which it has been discretized */
    
    private int Nclasses;
    
    /*The vector with the limits of the classes */
    
    double[] limits;
    
    
/**
 * Creates a new empty <code>Continuous</code> object.
 */

public ContinuousDiscrete() {

  super();
  setTypeOfVariable(MIXED);
  setPrecision(2);
  setMin(0.0);
  setMax(1.0);
  setUnit("");
  setUndefVal(Double.NaN);
  setNumberIntervals(2);
  setLimit(1,0.5);
  
}
public ContinuousDiscrete(Continuous node, int nintervals, double[] thelimits) {

  super();
  setName(node.getName());
  setTypeOfVariable(MIXED);
  setPrecision(node.getPrecision());
  setMin(node.getMin());
  setMax(node.getMax());
  setUnit(node.getUnit());
  setUndefVal(node.undefValue());
  setNumberIntervals(nintervals);
  setTitle(node.getTitle());
  setPosX(node.getPosX());
  setPosY(node.getPosY());
  limits = thelimits;
  
}

public ContinuousDiscrete(String name, int nintervals, double[] thelimits) {
 super();
  setName(name);
  setTypeOfVariable(MIXED);
 setMin(thelimits[0]);
  setMax(thelimits[nintervals]);setNumberIntervals(nintervals);
  limits = thelimits;
}
    
    


 
/**
 * To set the number of intervals for discretizing
 * @param n the number of intervals.
 */

public void setNumberIntervals(int n) {

  Nclasses= n;
  limits = new double[Nclasses+1];
  limits[0] = this.getMin();
  limits[Nclasses] = this.getMax();
  
}   
  

public double getLimit(int i) {

  if (i>Nclasses) {
      System.out.println("Error: No interval");}
  else {
    return(limits[i]);
  }
  return(-1);
  
}   


public void split(int i) {
    int j;
    double[] oldl;
     Nclasses++;
       oldl = limits;
       limits = new double[Nclasses+1];
       
    if (i < Nclasses) {
       
      
        limits = new double[Nclasses+1];
        for(j=0;j<=i;j++) {
            limits[j] = oldl[j];
        }
        limits[i+1] = (oldl[i]+oldl[i+1])/2.0;
         for(j=i+2;j<=Nclasses;j++) {
            limits[j] = oldl[j-1];
        }
    }
    
}


public void split(int i, double y) {
    int j;
    double[] oldl;
  //  System.out.println("Splittin " + this.getName());
    Nclasses++;
     oldl = limits;
        limits = new double[Nclasses+1];
    if (i < Nclasses) {
        
       
        for(j=0;j<=i;j++) {
            limits[j] = oldl[j];
        }
        limits[i+1] = y;
         for(j=i+2;j<=Nclasses;j++) {
            limits[j] = oldl[j-1];
        }
    }
    
    
}


public double join(int i) {
    int j;
    double[] oldl;
    double y=0;
      //  System.out.println("Joining " + this.getName());
      //   System.out.println("Numero intervalos " + Nclasses);
        Nclasses--;
        oldl = limits;
        limits = new double[Nclasses+1];
        
    if (i < Nclasses) {
        
        
        for(j=0;j<=i;j++) {
            limits[j] = oldl[j];
        }
         for(j=i+1;j<=Nclasses;j++) {
            limits[j] = oldl[j+1];
        }
         y = oldl[i+1];
    }
    
 //   if (Nclasses == 1) {System.out.println(limits[0] + " - " + limits[1]);}
    
    return y;
    
}


public void setLimit(int i, double x) {

  if (i>Nclasses) {
      System.out.println("Error: No interval");}
  else {
    limits[i]=x;
  }
  
} 

public int getNumberofIntevals() {
    return Nclasses;
}



public int getCase(double x) {
 int i=-1;
 int j;
 if (x< getMin()) {//System.out.println("Warning: Value lower than the minimum"+ x + " " + this.getName());
                  i=0;}  
    else {
    if (x> getMax()) {//System.out.println("Warning: Value greater than the maximum " + x + " " + this.getName()); 
                     i=Nclasses-1;} 
    else {
      //  System.out.println("Value of variable " + this.getName() + "numero intervalos " + Nclasses);
   for(j=1;j<=Nclasses; j++) {
      // System.out.println("testing " + j + "with imit " + limits[j]);
       if (x <= limits[j]) {i=j-1;break;}
   }
    }
 }
 
// System.out.println("Value " + x + " discrete " + i);
 // System.out.println("Limits " + limits[i] + " - " + limits[i+1]);
 
 return(i);   
}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.inference.abduction;
import elvira.*;
import java.util.Vector;
import java.util.Comparator;
/**
 *
 * @author smc
 */
public class MPENode {

  public  Configuration c;
  public double p;
  public int i;
  
  public MPENode(Configuration c1, double prob, int pos) {
  c = c1;
  p = prob;
  i = pos;

}

  public Configuration getConfig() {
      return c;
  }
  
  
  public double getVal() {
      return p;
  }
  
  public int getPos() {
      return i;
  }  


  public void setConfig(Configuration con) {
      c = con;
  }
  
  
  public void setVal(double pn) {
       p = pn;
  }
  
  public void setPos(int in) {
      i = in;
  }  


  }
  


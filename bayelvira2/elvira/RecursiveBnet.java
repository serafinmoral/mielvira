/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira;

import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import elvira.inference.*;

import elvira.potential.*;
import elvira.gui.KmpesDialog;
import elvira.tools.SampleGenerator;

/**
 *
 * @author smc
 */
public class RecursiveBnet {

     /**
     * The variable associated with the node of the tree, if the node is
     * internal.
     */
    FiniteStates var;

    /**
     * The Bnet.
     */
    Bnet graph;

    /**
     * A label that indicates the type of the node:
     * 0: Bnet
     * 1: List
     * 2: Tree
     * 3: Upgraph with two recursive Bnets in a list.
     */
    int label;

    /**
     * A vector containing references to the successord of this node or the list of Bnets
     */
    Vector list;



 public   RecursiveBnet() {
    label = 1;
    list = new Vector();


}

public RecursiveBnet(Bnet x) {
    label = 0;
    graph = x;
    list = new Vector();

}

public   RecursiveBnet(RecursiveBnet x, RecursiveBnet y,Bnet z) {
    label = 2;
    list = new Vector();
    list.addElement(x);
    graph = z;

list.addElement(y);
}

public RecursiveBnet(Vector x) {
    label = 1;
    list = (Vector) x.clone();


}

public void setLabel(int i) {

    label=i;
   // System.out.println("Setting label to " +i);
}

public void printLabel() {


    System.out.println("Label "  + label);
}

public void saver(int level, PrintWriter p) throws IOException {

    int i;

    p.println("Recursive Bnet level " + level);
    if (label==0) {
        p.println("Type Bnet");
        graph.save(p);

    }

     if (label==2) {
        p.println("Type Tree");
         p.println("Root");
        p.println("BEGIN");
        graph.save(p);
       p.println("END");
        p.println("Left Child");
        p.println("BEGIN");
      ((RecursiveBnet)  list.elementAt(0)).saver(level+1,p);
       p.println("END");
          p.println("BEGIN");
        p.println("Rigt Child");
      ((RecursiveBnet)  list.elementAt(0)).saver(level+1,p);
      p.println("END");

    }

      if (label==1) {
        p.println("Type List");
        p.println("LISTBEGIN");
        for(i=0;i<list.size();i++){
        p.println("BEGIN");
      ((RecursiveBnet)  list.elementAt(i)).saver(level+1,p);
       p.println("END");
         

    }
p.println("LISTEND");
      }
}

public void save(int level, PrintWriter p) throws IOException {

    int i;

    {
   
System.out.println("Entering Saving upgraphs " + level+ " Type " + label );

    p.println("Recursive Upgraph Level " + level);

    if (label==0) {
        p.println("Type Bnet");
        graph.save(p);

    }

     if (label==2) {
        p.println("Type Tree");
         p.println("Root");
        p.println("BEGIN");
        graph.save(p);
       p.println("END");
       

    }

      if (label==1) {
        p.println("Type List");
        p.println("LISTBEGIN");
      
        for(i=0;i<list.size();i++){
         
        p.println("BEGIN");
      ((RecursiveBnet)  list.elementAt(i)).save(level,p);
       p.println("END");


    }
p.println("LISTEND");
      }

      if (label==3) {
           level--;
          ((RecursiveBnet)  list.elementAt(0)).save(level,p);
        if( ((RecursiveBnet)  list.elementAt(1)) !=null) {    ((RecursiveBnet)  list.elementAt(1)).save(level,p); }


      }

    }


   
}

public void  addBnet(RecursiveBnet x) {
   
    list.addElement(x);


}
    public void  setVar(FiniteStates x) {

    var=x;


}

}

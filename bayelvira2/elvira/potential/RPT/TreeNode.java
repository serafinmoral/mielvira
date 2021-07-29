/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;

import elvira.Node;
import elvira.Configuration;
import elvira.potential.ProbabilityTree;
import elvira.potential.PotentialTable;
import elvira.potential.Potential;

import java.util.ArrayList;
import java.util.HashSet;
import java.io.Serializable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author mgomez
 */
public abstract class TreeNode implements Cloneable, Serializable {
   /**
    * Data member for containing the parent
    */
   protected TreeNode parent;
   public double maxSize;
   public double maxNumnodos;

   /**
    * Method for printing the node
    */
   public abstract void print(int spaces);

   /**
    * Method for returning the vector of vars related to the
    * treeNode. 
    */
   public abstract HashSet<Node> getVariables();

   /**
    * Method for returning the parent of a given TreeNode
    */
   public TreeNode getParent(){
       return parent;
   }

   /**
    * Method for setting the parent
    */
   public void setParent(TreeNode parent){
       this.parent=parent;
   }

   /**
    * deletes the variable given as an argument by summing it out
    * 
    * @param var
    * @return
    */
   public abstract TreeNode addVariable(Node var);

   /**
    * restricts this potential to the configuration given as an argument
    * 
    * @param conf
    * @return
    */
   public abstract TreeNode restrictVariable(Configuration conf);

   /**
    * returns an exact copy of this
    * @return
    */
    public abstract TreeNode copy();

    /*
     * Adds this TreeNode with the TreeNode in the parameter
     **/
    public abstract TreeNode addNodes(TreeNode node);

    /*
     * Replace the leaves for the node given as argument
     **/
    public abstract void replaceLeaves(TreeNode node);

    /*
     * Replace the leaves consistent with a given configuration for the node given as argument
     **/
    public abstract void replaceLeavesConf(TreeNode node, Configuration conf);


    /**
     * multiplies this node to the one given as an argument
     * 
     * @param node
     * @return
     */
    public abstract TreeNode directMultiplication(TreeNode node);

    /**
     * do not use. For testing purpose
     *
     * @return
     */
    public abstract double toValue();
    public abstract long getTreeSize();
    public abstract long getNumberOfNodes();


    public abstract long getNumberOfFreeParameters();

    public abstract double getValue(Configuration conf);
    public abstract void setValue(Configuration conf, double value);
    public abstract double getTotalPotential();

    public abstract void changeTablesIntoTrees();
    public abstract ProbabilityTree RPTtoPotentialTree();

    public abstract boolean containsLists();

    public abstract int generateDotCode(DataOutputStream myDOS, int par, int chil) throws IOException;

    protected abstract String getNodeDotCode(int index);

    protected abstract void findAndReplaceSplit(Node lastParent, Configuration randomConf, Node newParent, Potential p, Configuration variables);

}

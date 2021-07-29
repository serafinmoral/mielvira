/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;

import elvira.Node;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.potential.ProbabilityTree;
import elvira.potential.PotentialTable;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.Serializable;

/**
 *
 * @author Cora
 */
public class ValueTreeNode extends TreeNode implements Serializable{

    private double value;

    /**
     * default constructor
     * @param val
     */
    public ValueTreeNode(double val){
        this.value = val;
    }

    /**
     * prints this node
     * @param spaces
     */
    public void print(int spaces){
    
        System.out.println("        Value Node         ");
        System.out.println(" value: "+this.value);
        System.out.println("        ----------         ");
        
    }

    public void changeTablesIntoTrees(){
        //no hacer nada
    }



    /**
     * returns an array with the variables involved in this potential. In this
     * case it returns a null object
     * @return
     */
    public HashSet<Node> getVariables(){
        return null;
    }

    /**
     * adds the value within this node to the one given as an argument
     * @param var
     * @return
     */
    public TreeNode addVariable(Node var){
        TreeNode result = new ValueTreeNode(this.value*(((FiniteStates)var).getNumStates()));
        result.setParent(this.parent);
        return result;
    }

    /**
     * restricts the potential to a given configuration. In this case returns an exact
     * copy of this.
     *
     * @param conf
     * @return
     */
    public TreeNode restrictVariable(Configuration conf){return this.copy();}

    /**
     * returns an exact copy of this node
     * @return
     */
    @Override
    public TreeNode copy(){
        TreeNode result = new ValueTreeNode(this.value);
        result.setParent(parent);
        return result;
    }

    /**
     * returns the value encapsulated within this node
     * @return
     */
    public double getValue(){
        return this.value;
    }

    /**
     * sums this node to the one given as an argument
     * @param node
     * @return
     */
    public TreeNode addNodes(TreeNode node){
        Class argument = node.getClass();
        if(argument == ValueTreeNode.class){
            TreeNode result = new ValueTreeNode(this.value+((ValueTreeNode)node).value);
            result.parent=this.parent;
            return result;
        }
        if(argument == PotentialTreeNode.class){
            TreeNode result = node.addNodes(this);
            result.parent=this.parent;
            return result;
        }
        if(argument == SplitTreeNode.class){
            TreeNode result = node.addNodes(this);
            result.parent=this.parent;
            return result;
        }
        if(argument == ListTreeNode.class){
            TreeNode resultNode = node.addNodes(this);
            resultNode.parent = this.parent;
            return resultNode;
        }
        return null;
    }

    /**
     * multiplies this node with the one given as an argument
     * @param node
     * @return
     */
    public TreeNode directMultiplication(TreeNode node){
        Class argument = node.getClass();
        if(argument == ValueTreeNode.class){
            TreeNode result = new ValueTreeNode(this.value*((ValueTreeNode)node).getValue());
            return result;
        }
        else{
            return node.directMultiplication(this);
        }
    }

    /**
     * do not use. For testing purpose
     *
     * @return
     */
    public double toValue(){
        return this.value;
    }

    public long getTreeSize(){
        return 1;
    }

    public long getNumberOfNodes(){
        return 1;
    }

    public double getValue(Configuration conf){
        return this.value;
    }

    public double getTotalPotential(){
        return this.value;
    }

    public ProbabilityTree RPTtoPotentialTree(){
    ProbabilityTree aux = new ProbabilityTree(this.value);
    return aux;
    }


}

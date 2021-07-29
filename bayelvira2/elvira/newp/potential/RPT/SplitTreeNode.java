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
import java.util.HashMap;
import java.util.Vector;
import java.util.HashSet;
import java.util.Iterator;
import java.io.Serializable;
/**
 *
 * @author Cora
 */
public class SplitTreeNode extends TreeNode implements Serializable{

    private Node variable;
    private ArrayList<TreeNode> values;

    /**
     * default constructor
     * 
     * @param var
     */
    public SplitTreeNode(Node var){
        this.variable = var;
        values = new ArrayList<TreeNode>(((FiniteStates)var).getNumStates());
    }

    /**
     * prints this node
     * @param spaces
     */
    public void print(int spaces){
    
        
        int size = this.values.size();
       System.out.println("        Split Node of variable "+this.variable.getName()+" ("+size+" states)         ");
        for(int i=0; i<size; i++){
            System.out.println("\tState "+i+":");
            if(this.values.get(i)!=null){
                this.values.get(i).print(0);
            }
            else{
                System.out.println("NULL");
            }
        }    
        System.out.println("        --------------         ");    
    }

    /**
     * returns the variable represented by this split
     * @return
     */
    public Node getVariable(){
        return this.variable;
    }

     /**
     * returns the variable represented by this split
     * @return
     */
    public TreeNode getSon(int state){
        return this.values.get(state);
    }

    /**
     * returns an array with the variables involved in this node and below
     * @return
     */
    public HashSet<Node> getVariables(){
        HashSet<Node> result = new HashSet<Node>();
        result.add(variable);
        int size = this.values.size();
        for(int i=0; i< size; i++){
            HashSet<Node> vars = this.values.get(i).getVariables();
            if(vars!=null){
                result.addAll(vars);
            }
        }
        return result;
    }

    /**
     * deletes the variable given as an argument by summing it out
     *
     * @param var
     * @return
     */
    public TreeNode addVariable(Node var){

        int size = this.values.size();
        if(this.variable.getName() == var.getName()){
            //Sumar los hijos
            TreeNode resultTN = this.values.get(0).copy();
            for(int i=1; i< size; i++){
                resultTN = resultTN.addNodes(this.values.get(i));
            }

            return resultTN;
        }//fin de eliminar la variable que etiqueta el split
        else{
            //propagar la operación a los hijos
            //SplitTreeNode result = (SplitTreeNode)this.copy();
            SplitTreeNode result = new SplitTreeNode(this.variable);
            result.setParent(parent);
            for(int i=0; i<size; i++){
                result.values.add(this.values.get(i).addVariable(var));
            }
            return result;
        }    
    }

    /**
     * restricts this potential to the configuration given as an argument
     * @param conf
     * @return
     */
    public TreeNode restrictVariable(Configuration conf){
        TreeNode result = null;
        Vector<Node> variables = conf.getVariables();
        if(variables.contains(this.variable)){
            int state = conf.getValue((FiniteStates)this.variable);
            result = this.values.get(state).restrictVariable(conf);
            result.setParent(parent);
        }
        else{
            result = new SplitTreeNode(this.variable);
            result.setParent(parent);
            int size = this.values.size();
            for(int i=0; i<size; i++){
                ((SplitTreeNode)result).addSon(this.values.get(i).restrictVariable(conf));
            }
            
        }
        return result;
    }


    /**
     * returns an exact copy of this node
     * @return
     */
    public TreeNode copy(){
        SplitTreeNode result = new SplitTreeNode(this.variable);
        result.setParent(parent);
        int size = this.values.size();
        for(int i=0;i<size;i++){
            result.addSon(this.values.get(i).copy());
        }
        return result;
    }

    /**
     * adds a son to this split. The first son added will correspond to the
     * first state of the variable represented in the split and so on
     * @param son
     */
    public void addSon(TreeNode son){
        if(son!=null){
            son.setParent(this);
        }
            this.values.add(son);
    }

        /**
     * changes the TreeNode son of this, according to the state given as a parameter
     * @param son
     */
    public void setSon(TreeNode son, int state){
        if(son!=null){
            son.setParent(this);
        }
            this.values.set(state, son);
    }

     /**
     * adds this node with the one given as an argument
     * @param node
     * @return
     */
    public TreeNode addNodes(TreeNode node){

        Class argument = node.getClass();
        if(argument == ValueTreeNode.class){
            //System.out.println("inside addNodes split + value");
            SplitTreeNode result = new SplitTreeNode(this.variable);
            int size = this.values.size();
            for(int i=0; i<size; i++){
                result.addSon(this.values.get(i).addNodes(node));
            }
            result.setParent(parent);
            return result;
        } //Esto es igual que el potencial!!
        if(argument == SplitTreeNode.class){
            int size = this.values.size();
            HashSet<Node> vars = node.getVariables();
            if(vars.contains(this.variable)){
                Vector<Node> var = new Vector<Node>();
                var.add(variable);
                Configuration conf = new Configuration(var);
                SplitTreeNode result = new SplitTreeNode(this.variable);
                TreeNode restricted;
                for(int i=0; i<size; i++){
                    restricted = node.restrictVariable(conf);
                    result.addSon(this.values.get(i).addNodes(restricted));
                    conf.nextConfiguration();
                }
                result.setParent(parent);
                return result;
            }else{
                SplitTreeNode result = new SplitTreeNode(this.variable);
                for(int i=0; i<size; i++){
                    result.addSon(this.values.get(i).addNodes(node));
                }
                result.setParent(parent);
                return result;
            }

        }
        if(argument == PotentialTreeNode.class){
            int size = this.values.size();
            HashSet<Node> vars = node.getVariables();
            if(vars.contains(this.variable)){
                //Restringir antes de pasar a los hijos
                Vector<Node> var = new Vector<Node>();
                var.add(variable);
                Configuration conf = new Configuration(var);
                SplitTreeNode result = new SplitTreeNode(this.variable);
                TreeNode restricted;
                for(int i=0; i<size; i++){
                    restricted = node.restrictVariable(conf);
                    result.addSon(this.values.get(i).addNodes(restricted));
                    conf.nextConfiguration();
                }
                result.setParent(parent);
                return result;                
            }
            else{
                SplitTreeNode result = new SplitTreeNode(this.variable);
                for(int i=0; i<size; i++){
                    result.addSon(this.values.get(i).addNodes(node));
                }
                result.setParent(parent);
                return result;
            }
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
     * 
     * @param node
     * @return
     */
    public TreeNode directMultiplication(TreeNode node){
        TreeNode result;
        Configuration conf = null;
        Class argument = node.getClass();
        if(argument == ListTreeNode.class){
            return node.directMultiplication(this);
        }
        else{
            if(argument == ValueTreeNode.class){
                result = new SplitTreeNode(this.variable);
                result.setParent(this.parent);
                int size = this.values.size();

                for(int i=0; i< size; i++){
                    ((SplitTreeNode)result).addSon(this.values.get(i).directMultiplication(node));
                }
            }else{
                result = new SplitTreeNode(this.variable);
                result.setParent(this.parent);
                int size = this.values.size();

                boolean restrict = false;
                if(node.getVariables().contains(this.variable)){
                    restrict = true;
                    Vector<Node> variables = new Vector<Node>();
                    variables.add(this.variable);
                    conf = new Configuration(variables);
                }
                for(int i=0; i< size; i++){
                    if(restrict){
                        TreeNode aux = node.restrictVariable(conf);
                        ((SplitTreeNode)result).addSon(this.values.get(i).directMultiplication(aux));
                        conf.nextConfiguration();
                    }
                    else{
                        ((SplitTreeNode)result).addSon(this.values.get(i).directMultiplication(node));
                    }
                }
            }//end else
        }
        return result;
    }
    
    /**
     * do not use. For testing purpose
     *
     * @return
     */
    public double toValue(){
        return 1.0;
    }

    public long getTreeSize(){
        long size = 0;
        for(int i=0; i<this.values.size();i++){
            size = size + this.values.get(i).getTreeSize();
        }
        return size;
    }

    public long getNumberOfNodes(){
        long size = 1;
        for(int i=0; i<this.values.size();i++){
            size = size + this.values.get(i).getTreeSize();
        }
        return size;
    }

    public double getValue(Configuration conf){
        int state = conf.getValue((FiniteStates)this.variable);
//        System.out.println(state);
//        if(state!=-1){
            return this.values.get(state).getValue(conf);
//        }
//        else{
//            double total =0.0;
//            for(int i=0; i<this.values.size();i++){
//            total += this.values.get(i).getValue(conf);
//            }
//            return total;
//        }
    }

    public double getTotalPotential(){
        int size = this.values.size();
        HashSet<Node> vars = this.getVariables();
        Vector<Node> varsVect = new Vector<Node>();
        varsVect.addAll(vars);
        Configuration conf = new Configuration(varsVect);
        double total = 0.0;
        
        for(int j=0; j<conf.possibleValues();j++){
            //conf.print();
            total += this.getValue(conf);
            conf.nextConfiguration();
        }
        //System.out.println("Total split: "+total);
        return total;
    }

    public void changeTablesIntoTrees(){
        for(int i=0; i<this.values.size();i++){
            this.values.get(i).changeTablesIntoTrees();
        }
    }

     public ProbabilityTree RPTtoPotentialTree(){
     ProbabilityTree aux = new ProbabilityTree((FiniteStates)this.variable);
     for(int i=0; i<this.values.size();i++){
         aux.setNewChild(this.getSon(i).RPTtoPotentialTree(), i);
     }
    return aux;
    }



 }



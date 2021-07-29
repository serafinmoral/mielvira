/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;


import elvira.Node;
import elvira.Configuration;
import elvira.potential.PotentialTable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import elvira.potential.ProbabilityTree;
import java.io.Serializable;
/**
 *
 * @author Cora
 */
public class ListTreeNode extends TreeNode implements Serializable{

    /**
     * ArrayList containing the factors of the decomposition
     */
    private ArrayList<TreeNode> values;

    /**
     * default constructor
     */
    public ListTreeNode(){
        values = new ArrayList<TreeNode>();
    }

    /**
     * method for printing this node
     * @param spaces
     */
    public void print(int spaces){
    
        int size = this.values.size();
        System.out.println("        List Node of "+size+" children         ");
        for(int i=0; i<size; i++){
            this.values.get(i).print(0);
        }        
        System.out.println("        -----end list-----         ");
    
    }

    /**
     * returns an array with the variables involved in the potential represented by this
     * @return
     */
    public HashSet<Node> getVariables(){
        HashSet<Node> result = new HashSet<Node>();
        int size = this.values.size();
        HashSet<Node> childVars = null;
        for(int i=0; i< size; i++){
            childVars=this.values.get(i).getVariables();
            if(childVars!=null){
                result.addAll(childVars);
            }
        }
        return result;
    }

    /**
     * deletes the variable given as an argument by summing it out
     * @param var
     * @return
     */
    public TreeNode addVariable(Node var){
        TreeNode result=null;
//System.out.println("Trabajando con ListTreeNode de tamaño: "+this.getNumberOfChildren());
        if (this.getNumberOfChildren() > 1){
           result = this.combineSons();
           result = result.addVariable(var);
        }else{
            result = this.getChild(0).addVariable(var);
        }
        return result;
    }

    /**
     * restricts this potential to the configuration given as an argument
     * @param conf
     * @return
     */
    public TreeNode restrictVariable(Configuration conf){
        ListTreeNode result = new ListTreeNode();
        result.setParent(parent);
        int size = this.values.size();
        for(int i=0; i<size; i++){
            result.addChild(this.values.get(i).restrictVariable(conf));
        }
        return result;
    }

    /**
     * returns an exact copy of this object
     * @return
     */
    @Override
    public Object clone(){
        ListTreeNode result = new ListTreeNode();
        int size = this.values.size();
        for(int i=0;i<size;i++){
            result.addChild(this.values.get(i).copy());
        }
        return result;
    }

    /**
     * returns an exact copy of this object
     * @return
     */
    public TreeNode copy(){
        ListTreeNode result = new ListTreeNode();
        int size = this.values.size();
        result.setParent(parent);
        for(int i=0;i<size;i++){
            result.addChild(this.values.get(i).copy());
        }
        return result;
    }

    /**
     * adds a new factor to the list, the one given as an argument
     * @param node
     */
    public void addChild(TreeNode node){
        values.add(node);
        node.parent = this;
    }

    /**
     * sums this node to the one given as an argument
     * 
     * @param node
     * @return
     */
    public TreeNode addNodes(TreeNode node){
        TreeNode result = this.combineSons().addNodes(node);
        result.setParent(this.parent);
        return result;
    }

    /**
     * multiplies all the factor in this decomposition
     * @return
     */
    public TreeNode combineSons(){
       int size = this.values.size();
       TreeNode result = this.values.get(0).copy();
       for(int i=1; i<size; i++){
           // multiplicar dos a dos
           result = result.directMultiplication(this.values.get(i));
       }
//       System.out.println("lista multiplicada");
//       result.print(0);
//       System.out.println("END lista multiplicada");
       return result;
    }

    /**
     * multiplies this node to the one given as an argument
     * @param node
     * @return
     */
    public TreeNode directMultiplication(TreeNode node){
        ListTreeNode result = (ListTreeNode)this.copy();
        result.addChild(node);
        return result;
//        Class argument = node.getClass();
//        TreeNode result = this.combineSons();
//        result.parent = this.parent;
//        if(argument == ListTreeNode.class){
//            return result.directMultiplication(node);
//        }else{
//            return node.directMultiplication(result);
//        }
    }

    /**
     * divides this list in two parts, one containing those factors related to
     * the variable given as an argument and another with the factors that aren't
     * related to it
     * @param var
     * @param tNode
     */
    public void divideTree(Node var,ListTreeNode tNode){
       int size = this.values.size();
       Class argument;
       for(int i=0; i<size; i++){
           argument = this.values.get(i).getClass();
           if(argument == ListTreeNode.class){
               ((ListTreeNode)this.values.get(i)).divideTree(var, tNode);
           }else{
                if(argument == ValueTreeNode.class){
                   ((ListTreeNode)tNode.values.get(1)).addChild(this.values.get(i));
                  // System.out.println("Value added");
                }
                else{
                    //mirar si contiene a la variable
                    //meter en el lugar correspondiente
                    HashSet<Node> vars = this.values.get(i).getVariables();
                    if(vars.contains(var)){
                        ((ListTreeNode)tNode.values.get(0)).addChild(this.values.get(i));
                    }else{
                        ((ListTreeNode)tNode.values.get(1)).addChild(this.values.get(i));
                    }
                }
           }
       }
//       System.out.println("Resultado de dividir");
//       tNode.print(0);
//       System.out.println("END Resultado de dividir");
    }

    /**
     * returns the number of factors currently hanging from the list node
     * @return
     */
    public int getNumberOfChildren(){
        return this.values.size();
    }

    /**
     * returns the factor which index is the one given as a parameter
     * @param index
     * @return
     */
    public TreeNode getChild(int index){
        return this.values.get(index);
    }

    /**
     * do not use. For testing purpose
     *
     * @return
     */
    public double toValue(){
        double total = 1.0;
        for(int i=0; i<this.values.size();i++){
            total *= this.getChild(i).toValue();
        }
        return total;
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
        double total = 1;
        for(int i=0; i<this.values.size();i++){
            total *= this.values.get(i).getValue(conf);
        }
        return total;
    }

    public double getTotalPotential(){

        RecursivePotentialTree rpt = new RecursivePotentialTree();
        rpt.setValues(this.combineSons());
        //rpt.print(0);
        return rpt.totalPotential();
    }

    public void changeTablesIntoTrees(){
        for(int i=0; i<this.values.size();i++){
            this.values.get(i).changeTablesIntoTrees();
        }
    }

    public ProbabilityTree RPTtoPotentialTree(){
    return this.combineSons().RPTtoPotentialTree();
}


}

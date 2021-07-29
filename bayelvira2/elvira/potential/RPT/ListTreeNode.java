/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;


import elvira.Node;
import elvira.Configuration;
import elvira.potential.PotentialTable;
import elvira.FiniteStates;
import elvira.potential.Potential;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import elvira.NodeList;
import elvira.potential.ProbabilityTree;
import java.io.Serializable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
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
        maxSize = 0;
        maxNumnodos = 1;
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

    public TreeNode pairwiseMultiplication(){
        int size = this.values.size();
       TreeNode root1 = this.values.get(0).copy();

       for(int i=1; i<size; i++){
           TreeNode root2 = this.values.get(i).copy();
           root1 = this.pairwiseMultiplicationAux(root1, root2);
       }

        return root1;
    }

    private TreeNode pairwiseMultiplicationAux(TreeNode root1, TreeNode root2){


       TreeNode newroot = null;
       Class argument = root1.getClass();
       Class argument2 = root2.getClass();

       if(argument == ListTreeNode.class){
           newroot = new ListTreeNode();
           int size = ((ListTreeNode)root1).getNumberOfChildren();
           for(int j=0; j<size; j++){
               ((ListTreeNode)newroot).addChild(((ListTreeNode)root1).getChild(j));
           }

           if(argument2==ListTreeNode.class){
               int size2 = ((ListTreeNode)root2).getNumberOfChildren();
               for(int j=0; j<size2; j++){
                    ((ListTreeNode)newroot).addChild(((ListTreeNode)root2).getChild(j));
               }
           }else{
               ((ListTreeNode)newroot).addChild(root2);
           }

           return newroot;
       }
        if(argument2 == ListTreeNode.class){
           newroot = new ListTreeNode();
           int size = ((ListTreeNode)root2).getNumberOfChildren();
           for(int j=0; j<size; j++){
               ((ListTreeNode)newroot).addChild(((ListTreeNode)root2).getChild(j));
           }
           ((ListTreeNode)newroot).addChild(root1);

           return newroot;
       }
       if(argument == SplitTreeNode.class){
            Node var = ((SplitTreeNode)root1).getVariable();
            newroot = new SplitTreeNode(var);
            NodeList listvar = new NodeList();
            listvar.insertNode(var);
            Configuration conf = new Configuration(listvar);
            int size2 = ((SplitTreeNode)root1).getNumberOfStates();
            for(int j=0; j<size2; j++){
//                 ((SplitTreeNode)newroot).addSon(root2.restrictVariable(conf));
//                 conf.nextConfiguration();
                TreeNode list = new ListTreeNode();
               ((ListTreeNode)list).addChild(root2.restrictVariable(conf));
               ((ListTreeNode)list).addChild(((SplitTreeNode)root1).getSon(j));
               ((SplitTreeNode)newroot).addSon(list);
               conf.nextConfiguration();

            }

            return newroot;
       }
       if(argument2 == SplitTreeNode.class){
           Node var = ((SplitTreeNode)root2).getVariable();
           newroot = new SplitTreeNode(var);
           NodeList listvar = new NodeList();
           listvar.insertNode(var);
           Configuration conf = new Configuration(listvar);
           int size2 = ((SplitTreeNode)root2).getNumberOfStates();
           for(int j=0; j<size2; j++){
               TreeNode list = new ListTreeNode();
               ((ListTreeNode)list).addChild(root1.restrictVariable(conf));
               ((ListTreeNode)list).addChild(((SplitTreeNode)root2).getSon(j));
               ((SplitTreeNode)newroot).addSon(list);
               conf.nextConfiguration();
           }
           return newroot;
       }
       //both Value or PotentialNodes
       newroot = root1.directMultiplication(root2);

       return newroot;

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
//       root1.print(0);
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
//        TreeNode root1 = this.combineSons();
//        root1.parent = this.parent;
//        if(argument == ListTreeNode.class){
//            return root1.directMultiplication(node);
//        }else{
//            return node.directMultiplication(root1);
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
     * removes the factor which index is the one given as a parameter
     * @param index
     * @return
     */
    public TreeNode removeChild(int index){
        return this.values.remove(index);
    }

    public void replaceChild(int index, TreeNode newChild){
        this.values.set(index, newChild);
    }

    public boolean containsLists(){
        return true;
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
            size = size + this.values.get(i).getNumberOfNodes();
        }
        return size;
    }


    public long getNumberOfFreeParameters(){
        long size = 0;
        for(int i=0; i<this.values.size();i++){
            size = size + this.values.get(i).getNumberOfFreeParameters();
        }
        return size-1;
    }

    public double getValue(Configuration conf){
        double total = 1;
        for(int i=0; i<this.values.size();i++){
            total *= this.values.get(i).getValue(conf);
        }
        return total;
    }

    public void setValue(Configuration conf, double val){
        System.err.println("not implemented for list nodes");
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

    /*
     * Replace the Value TreeNode leaves for the node given as argument
     *
     * CUIDADO CON EL CASO DE LAS FACTORIZACIONES: A IMPLEMENTAR
     *
     **/
    public void replaceLeaves(TreeNode node){
        for(int i=0; i<this.values.size();i++){
            if(this.values.get(i).getClass() == ValueTreeNode.class){
                this.values.set(i, node.copy());
            }
            else{
                this.values.get(i).replaceLeaves(node);
            }
        }
    }

    /*
    * Replace the leaves consistent with a given configuration for the node given as argument
     *
     * CUIDADO CON EL CASO DE LAS FACTORIZACIONES: A IMPLEMENTAR
     *
    **/
    public void replaceLeavesConf(TreeNode node, Configuration conf){
        for(int i=0; i<this.values.size();i++){
            //solo añadir en el caso de que el subarbol contenga a la variable, a implementar!
                this.values.get(i).replaceLeaves(node);
        }
    }

    public int generateDotCode(DataOutputStream myDOS, int par, int chil) throws IOException{

        int size = this.values.size();
        //myDOS.writeBytes(this.getNodeDotCode(par));
        for(int i=0; i<size; i++){
            chil++;
            myDOS.writeBytes(this.values.get(i).getNodeDotCode(chil));
            myDOS.writeBytes(par + " -> " + chil + ";\n");
            if(this.values.get(i).getClass()!= ValueTreeNode.class && this.values.get(i).getClass()!=PotentialTreeNode.class){
                //int aux = chil + 1;
                //chil++;
                chil = this.values.get(i).generateDotCode(myDOS, chil, chil);
                //chil = aux + 1;
            }
        }
        return chil;

    }

    protected String getNodeDotCode(int index){
        String code = index + "[ label = \"List Node\", color = green,style=filled];\n" ;
        return code;
    }

    protected void findAndReplaceSplit(Node lastParent, Configuration randomConf, Node newParent, Potential p, Configuration variables){
    }

}

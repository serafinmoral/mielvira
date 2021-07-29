/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;


import elvira.Node;
import elvira.Configuration;
import elvira.potential.Potential;
import elvira.potential.PotentialTable;
import elvira.potential.PotentialTree;
import elvira.potential.ProbabilityTree;
import elvira.NodeList;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.HashSet;
import java.io.Serializable;
/**
 *
 * @author Cora
 */
public class PotentialTreeNode extends TreeNode implements Serializable{

    /**
     * potential encapsulated in this object
     */
    private Potential potential;
    private Configuration info;
    private Vector<Node> conditioning;

    /**
     * default constructor
     * @param pot
     */
    public PotentialTreeNode(Potential pot){
        this.potential=pot.copy();
        maxSize = pot.getSize();
        maxNumnodos = 1;
       info = new Configuration();
       conditioning = new Vector();
    }
    
    public void setInfo(Configuration conf){
        info = conf.duplicate();
    }
    
    public void setCond(Vector<Node> aux){
        conditioning = (Vector<Node>)aux.clone();
    }

    public void changeTablesIntoTrees(){
         this.potential=new PotentialTree(this.potential);
    }

    /**
     * prints this node
     * @param spaces
     */
    public void print(int spaces){

        System.out.println("        Potential Node         ");
        System.out.println("Context: size "+this.info.possibleValues());
        this.info.pPrint();
        System.out.println();
        NodeList inf = new NodeList(this.conditioning);
        System.out.println("Conditioning: "+inf.toString2());
        //this.potential.print();
        for(int i=0; i<this.potential.getVariables().size();i++){
            System.out.print(((Node)this.potential.getVariables().get(i)).getName()+" , ");
        }
        this.potential.print();
        System.out.println("        ----------         ");

    }

    /**
     * returns the potential encapsulated in this object
     * @return
     */
    public Potential getPotential(){
        return this.potential;
    }

    public boolean containsLists(){
        return false;
    }

    /**
     * returns an arrayList with the variables involved in this potential
     * @return
     */
    public HashSet<Node> getVariables(){
        Vector<Node> vars = this.potential.getVariables();
        HashSet<Node> result = new HashSet<Node>();
//        if(vars.size()==0){
//            System.out.println("Potencial sin variables");
//        }
        if(vars!=null){
            result.addAll(vars);
        }else{
            System.out.println("Potencial sin instanciar variables");
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

        Potential result = this.potential.addVariable(var);
        PotentialTreeNode nodeResult= new PotentialTreeNode(result);
        nodeResult.setParent(this.parent);
        return nodeResult;

    }

    /**
     * restricts this potential to the configuration given as an argument
     *
     * @param conf
     * @return
     */
    public TreeNode restrictVariable(Configuration conf){
        Potential result = this.potential.restrictVariable(conf);
        TreeNode nodeResult;

        // Si el resultado contiene un unico valor, que se devuelva un
        // value node en lugar de un potential node
        if (result.getVariables().size() == 0){
             nodeResult=new ValueTreeNode(result.totalPotential());
        }
        else {
             nodeResult= new PotentialTreeNode(result);
        }
        nodeResult.setParent(this.parent);
        return nodeResult;

    }

    /**
     * returns an exact copy of this node
     * @return
     */
    public TreeNode copy(){
        TreeNode result = new PotentialTreeNode(this.potential.copy());
        result.setParent(parent);
        return result;
    }

    /**
     * adds this node to the one given as an argument
     *
     * @param node
     * @return
     */
    public TreeNode addNodes(TreeNode node){
        Class argument = node.getClass();
        if(argument == ValueTreeNode.class){
            Potential result = this.addValue(((ValueTreeNode)node).getValue());
            TreeNode resultNode = new PotentialTreeNode(result);
            resultNode.parent = this.parent;
            return resultNode;
        }
        if(argument == PotentialTreeNode.class){
            Potential result = this.potential.addition(((PotentialTreeNode)node).potential);
            TreeNode resultNode = new PotentialTreeNode(result);
            resultNode.parent = this.parent;
            return resultNode;
        }
        if(argument == SplitTreeNode.class){
            TreeNode resultNode = node.addNodes(this);
            resultNode.parent = this.parent;
            return resultNode;
        }
        if(argument == ListTreeNode.class){
            TreeNode resultNode = node.addNodes(this);
            resultNode.parent = this.parent;
            return resultNode;
        }
        return null;
    }

    /**
    * Private method for adding a value to the potential
    */
   private Potential addValue(double value){
       Potential copy=potential.copy();
       double prevValue;
       Configuration conf=new Configuration(potential.getVariables());
       for(int i=0; i < conf.possibleValues();i++){
           prevValue=potential.getValue(conf);
           copy.setValue(conf, value+prevValue);
           conf.nextConfiguration();
       }
       // Return copy
       return copy;
   }

   /**
    * multiplies this node with the one given as an argument
    * @param node
    * @return
    */
       public TreeNode directMultiplication(TreeNode node){
        Class argument = node.getClass();
        TreeNode result;
        if(argument == ValueTreeNode.class){
            Potential combination = this.potential.mul(((ValueTreeNode) node).getValue());
            result = new PotentialTreeNode(combination);
            return result;
        }
        if(argument == SplitTreeNode.class){
            return node.directMultiplication(this);
        }
        if(argument == PotentialTreeNode.class){
            Potential combination = this.potential.combine(((PotentialTreeNode) node).getPotential());
            result = new PotentialTreeNode(combination);
            return result;
        }
        if(argument == ListTreeNode.class){
            return node.directMultiplication(this);
        }
        return null;
    }

    /**
     * do not use. For testing purpose
     *
     * @return
     */
     public double toValue(){
        return ((PotentialTable)this.potential).getValue(0);
    }

    public long getTreeSize(){
        return this.potential.getSize();
    }

    public long getNumberOfNodes(){
        //return 1;
        if(this.potential.getClass()==PotentialTree.class){
            return ((PotentialTree)this.potential).getNumberOfNodes();
        }else{
        return this.potential.getSize();
        }
    }

    
    public long getNumberOfFreeParameters(){
        long size;
        size = this.potential.getSize();
        return size;
    }

    public double getValue(Configuration conf){
        return this.potential.getValue(conf);
    }

    public void setValue(Configuration conf, double val){
        System.err.println("not implemented for potential nodes");
    }

    public double getTotalPotential(){
        return this.potential.totalPotential();
    }

    public ProbabilityTree RPTtoPotentialTree(){
        PotentialTree aux = new PotentialTree(this.potential);
        return aux.getTree();
    }

    /*
    * Replace the Value TreeNode leaves for the node given as argument
    **/
    public void replaceLeaves(TreeNode node){
        //DO NOTHING
    }

        /*
    * Replace the leaves consistent with a given configuration for the node given as argument
    **/
    public void replaceLeavesConf(TreeNode node, Configuration conf){
        //DO NOTHING
    }

    public int generateDotCode(DataOutputStream myDOS, int par, int chil)throws IOException{
        //String potential = this.potential.toString();
        //myDOS.writeBytes(parent.getNodeDotCode() + " -> "+potential+";\n");
//        myDOS.writeBytes(parent.getNodeDotCode() + " -> potential"+ (this.hashCode()/100000)+"\n");

        //myDOS.writeBytes(this.getNodeDotCode(chil));
        myDOS.writeBytes(par + " -> "+chil+";\n");
        chil++;
        return chil;

    }

    protected String getNodeDotCode(int index){
    //    return this.potential.toString() + "potential" + (this.hashCode()/100000);
        String stringPot = "Potential Node: ";
        Vector<Node> vars = this.potential.getVariables();
        int size = vars.size();
        //System.out.println(size);

        for(int i=0; i< size; i++){
           // System.out.println(vars.get(i).getName());
            stringPot = stringPot.concat(vars.get(i).getName() + " ");
        }

    return index + "[ label = \"" + stringPot + "\", color = red,style=filled];\n" ;
    }

    protected void findAndReplaceSplit(Node lastParent, Configuration randomConf, Node newParent, Potential p, Configuration variables){
    }

}

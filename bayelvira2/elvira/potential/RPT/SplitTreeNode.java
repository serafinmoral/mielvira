/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;

import elvira.Node;
import elvira.NodeList;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.potential.ProbabilityTree;
import elvira.potential.PotentialTree;
import elvira.potential.PotentialTable;
import elvira.potential.Potential;

import java.io.IOException;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.HashSet;
import java.util.Iterator;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
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
        maxSize = 0;
        maxNumnodos = 1;
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

    public void replaceChild(int index, TreeNode newChild){
        //System.out.println("Replace child here!");

        this.values.set(index, newChild);
    }

    public boolean containsLists(){
        int size = this.values.size();
        for(int i=0;i<size;i++){
            if(this.getSon(i).containsLists() == true){
                return true;
            }
        }
        return false;
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
            size = size + this.values.get(i).getNumberOfNodes();
        }
        return size;
    }

     public long getNumberOfFreeParameters(){
        long size = 0;
        for(int i=0; i<this.values.size();i++){
            size = size + this.values.get(i).getTreeSize();
        }
        return size;
    }

     public Node getNodeAtLeaves(){
         //if(this.childrenAreSplit()){
         for(int i=0; i<this.values.size(); i++){
            if(this.values.get(i).getClass()!=SplitTreeNode.class){
                return this.variable;
            }else{
                return ((SplitTreeNode)this.values.get(i)).getNodeAtLeaves();
            }
            }
         //}
        return null;
     }

    public boolean childrenAreSplit(){
        Node var = null;
        if(this.values.get(0).getClass()==SplitTreeNode.class){
                var = ((SplitTreeNode)this.getSon(0)).getVariable();
            }

        for(int i=1; i<this.values.size(); i++){
            if(this.values.get(i).getClass()!=SplitTreeNode.class){
                return false;
            }else{
                if(((SplitTreeNode)this.getSon(0)).getVariable() != var){
                    return false;
                }
            }
        }
        return true;
    }


    public void setValue(Configuration conf, double val){
        int state = conf.getValue((FiniteStates)this.variable);
        //System.out.println(state);
        if(state!=-1){
            for(int i=0; i<this.values.size();i++){
                this.values.get(i).setValue(conf,val);
            }
        }
        else{
            this.values.get(state).setValue(conf, val);
        }
    }

    public double getValue(Configuration conf){
        int state = conf.getValue((FiniteStates)this.variable);
        //System.out.println(state);
        if(state!=-1){
            return this.values.get(state).getValue(conf);
        }
        else{
            double total =0.0;
            for(int i=0; i<this.values.size();i++){
            total += this.values.get(i).getValue(conf);
            }
            return total;
        }
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

          public ProbabilityTree RPTtoPotentialTree2(){
     ProbabilityTree aux = new ProbabilityTree((FiniteStates)this.variable);
     for(int i=0; i<this.values.size();i++){
         aux.setNewChild(this.getSon(i).RPTtoPotentialTree(), i);
     }
     PotentialTree aux2 = new PotentialTree(aux);
     aux2.sortAndBound(0.0001);
    return aux2.getTree();
    }

    public int getNumberOfStates(){
        return values.size();
    }


    public TreeNode pairwiseSum(){
        int size = this.values.size();
       TreeNode root1 = this.values.get(0);

       for(int i=1; i<size; i++){
           TreeNode root2 = this.values.get(i);
           root1 = this.pairwiseSumAux(root1, root2);
       }
       return root1;
    }

    private TreeNode pairwiseSumAux(TreeNode root1, TreeNode root2){


       TreeNode newroot = null;
       Class argument = root1.getClass();
       Class argument2 = root2.getClass();

       if(argument == ListTreeNode.class){
           newroot = ((ListTreeNode)root1).pairwiseMultiplication();

           /**
            *
            */
           double sizeaux = newroot.getTreeSize();
           double numnodosaux = newroot.getNumberOfNodes();
           TreeNode aux = this.pairwiseSumAux(newroot,root2);

           if(aux.maxSize<sizeaux)
                aux.maxSize = sizeaux;
           if(aux.maxNumnodos<numnodosaux)
                aux.maxNumnodos = numnodosaux;

           return aux;
       }

       if(argument2 == ListTreeNode.class){
           newroot = ((ListTreeNode)root2).pairwiseMultiplication();

           /**
            *
            */
           double sizeaux = newroot.getTreeSize();
           double numnodosaux = newroot.getNumberOfNodes();
           TreeNode aux = this.pairwiseSumAux(newroot,root1);

           if(aux.maxSize<sizeaux)
                aux.maxSize = sizeaux;
           if(aux.maxNumnodos<numnodosaux)
                aux.maxNumnodos = numnodosaux;

           return aux;
       }

       if(argument == SplitTreeNode.class){
            Node var = ((SplitTreeNode)root1).getVariable();
            newroot = new SplitTreeNode(var);
           // System.out.println("Created Split Node of var "+var.getName());
            NodeList listvar = new NodeList();
            listvar.insertNode(var);
            Configuration conf = new Configuration(listvar);
            int size2 = ((SplitTreeNode)root1).getNumberOfStates();
            for(int j=0; j<size2; j++){
                 ((SplitTreeNode)newroot).addSon(this.pairwiseSumAux(((SplitTreeNode)root1).getSon(j), root2.restrictVariable(conf)));
                // System.out.println("... added state ...");
                 conf.nextConfiguration();
            }
            return newroot;
       }

       if(argument2 == SplitTreeNode.class){
           Node var = ((SplitTreeNode)root2).getVariable();
           newroot = new SplitTreeNode(var);
          // System.out.println("Created Split Node");
           NodeList listvar = new NodeList();
           listvar.insertNode(var);
           Configuration conf = new Configuration(listvar);
           int size2 = ((SplitTreeNode)root2).getNumberOfStates();
           for(int j=0; j<size2; j++){
                ((SplitTreeNode)newroot).addSon(this.pairwiseSumAux(((SplitTreeNode)root2).getSon(j), root1.restrictVariable(conf)));
               // System.out.println("... added state ...");
                conf.nextConfiguration();
           }
           return newroot;
       }

       //both Value or PotentialNodes
      // System.out.println("Either of them, sum directly");
       newroot = root1.addNodes(root2);

       return newroot;

    }

    /*
    * Replace the Value TreeNode leaves for the node given as argument
    **/
    public void replaceLeaves(TreeNode node){
        for(int i=0; i<this.values.size();i++){
            if(this.values.get(i).getClass() == ValueTreeNode.class){
                this.values.set(i, node.copy());
            }else{
                this.values.get(i).replaceLeaves(node);
            }
        }
    }

   /*
    * Replace the leaves consistent with a given configuration for the node given as argument
    **/
    public void replaceLeavesConf(TreeNode node, Configuration conf){

        if(node.getClass()==SplitTreeNode.class){
            if(((SplitTreeNode)node).getVariable()!=this.variable){

               int index = conf.indexOf(variable);
        //        System.out.println(variable.getName() + " on it state " + index + " , but we want to restric to " + conf.getValue(index));
                if( index != -1){
        //            System.out.println("Restrict " + this.variable.getName() + " to val " + conf.getValue(index));
                    if(this.values.get(conf.getValue(index)).getClass() != ValueTreeNode.class){
                        this.values.get(conf.getValue(index)).replaceLeavesConf(node, conf);
                    }else{
        //                System.out.println("Add subtree! la variable está, restringir");
        //                this.print(3);
                        this.values.set(conf.getValue(index), node);
                    }
                }else{
                    for(int i=0; i<this.values.size();i++){
                       if(this.values.get(i).getClass() != ValueTreeNode.class){
                           this.values.get(i).replaceLeavesConf(node, conf);
                        }else{
        //                   System.out.println("Add subtree! la variable no esta");
        //                   this.print(3);
                            this.values.set(i, node);
                        }
                    }
                }

            }
        }else{

        int index = conf.indexOf(variable);
//        System.out.println(variable.getName() + " on it state " + index + " , but we want to restric to " + conf.getValue(index));
        if( index != -1){
//            System.out.println("Restrict " + this.variable.getName() + " to val " + conf.getValue(index));
            if(this.values.get(conf.getValue(index)).getClass() != ValueTreeNode.class){
                this.values.get(conf.getValue(index)).replaceLeavesConf(node, conf);
            }else{
//                System.out.println("Add subtree! la variable está, restringir");
//                this.print(3);
                this.values.set(conf.getValue(index), node);
            }
        }else{
            for(int i=0; i<this.values.size();i++){
               if(this.values.get(i).getClass() != ValueTreeNode.class){
                   this.values.get(i).replaceLeavesConf(node, conf);
                }else{
//                   System.out.println("Add subtree! la variable no esta");
//                   this.print(3);
                    this.values.set(i, node);
                }
            }
        }
        }
    }//end


    public int generateDotCode(DataOutputStream myDOS, int par, int chil) throws IOException{

        int size = this.values.size();
        myDOS.writeBytes(this.getNodeDotCode(par));
        for(int i=0; i<size; i++){
            chil++;
            myDOS.writeBytes(this.values.get(i).getNodeDotCode(chil));
            myDOS.writeBytes(par + " -> " + chil + ";\n");
            if(this.values.get(i).getClass()!= ValueTreeNode.class && this.values.get(i).getClass()!=PotentialTreeNode.class){
                //int aux = chil + 1;
                chil = this.values.get(i).generateDotCode(myDOS, chil, chil);
                //chil = aux + 1;
            }
        //chil++;
        }
        return chil;
    }

    protected String getNodeDotCode(int index){
        String code = index + "[ label = \"Split Node of "+ this.variable.getName()+"\", color = blue,style=filled];\n";
        return code;
    }

    protected void findAndReplaceSplit(Node lastParent, Configuration randomConf, Node newParent, Potential p, Configuration variables){
        
        if(this.variable.getName() == lastParent.getName()){
            //reemplazar la hoja consistente
            //System.out.println("CREAR EL SPLIT!! ");
            int states = ((FiniteStates)newParent).getNumStates();
            TreeNode newNode = new SplitTreeNode(newParent);

            for(int i=0; i<states; i++){
                variables.putValue((FiniteStates)newParent, i);
                ((SplitTreeNode)newNode).addSon(new ValueTreeNode(elvira.potential.RPT.learningRPTs.CPT_rpt.getValueSafe(p,variables)));
            }
            int stateForSplit = randomConf.getValue((FiniteStates)lastParent);
            this.values.set(stateForSplit, newNode);

            //Y en los otros estados de la variable, actualizar tambien!
            Configuration aux = new Configuration(variables.getVariables());
            aux.setValues(variables.getValues());
            int size = this.values.size();

            for(int i=0; i< size; i++){
                double value = 0.0;
                if(stateForSplit!=i){
                    aux.putValue((FiniteStates)this.variable, i);
                    for(int j=0; j<states; j++){
                        aux.putValue((FiniteStates)newParent, j);
                        //System.out.println("valor del potencial.... " +p.getValue(aux) );
                        value += (elvira.potential.RPT.learningRPTs.CPT_rpt.getValueSafe(p,aux));
                    }
                    //System.out.println("valor A ACTUALIZAR.... " +(value/states) );
                    this.values.set(i,new ValueTreeNode(value/states));
                }
            }
            // ***
            variables.remove(newParent);
        }else{
            //System.out.println("SIGUE BUSCANDO ");
            int size = this.values.size();
            for(int i=0; i< size; i++){
                variables.putValue((FiniteStates)this.variable, i);
                this.values.get(i).findAndReplaceSplit(lastParent, randomConf, newParent, p, variables);
            }
            variables.remove(this.variable);
        }

    }

 }



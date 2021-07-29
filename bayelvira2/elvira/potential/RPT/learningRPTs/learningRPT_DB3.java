package elvira.potential.RPT.learningRPTs;

import elvira.potential.RPT.RecursivePotentialTree;
import elvira.potential.RPT.TreeNode;
import elvira.potential.RPT.ListTreeNode;
import elvira.potential.RPT.ValueTreeNode;
import elvira.potential.PotentialTable;
import elvira.database.DataBaseCases;
import elvira.Node;
import elvira.Configuration;
import elvira.NodeList;
import elvira.potential.PotentialTree;
import elvira.FiniteStates;
import elvira.CaseListMem;
import elvira.potential.RPT.ListTreeNode;
import elvira.potential.RPT.PotentialTreeNode;
import elvira.potential.RPT.RecursivePotentialTree;
import elvira.potential.RPT.SplitTreeNode;
import elvira.potential.RPT.TreeNode;
import elvira.potential.RPT.Util;
import elvira.potential.RPT.ValueTreeNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;


/**
 * @author cora
 */
public class learningRPT_DB3 {

    private RecursivePotentialTree result;
    private DataBaseCases cases;
    private ArrayList<ArrayList<Node>> candidates;
    private NodeList variables;
    private Hashtable candidateIndex;

    public learningRPT_DB3(DataBaseCases dat){
        cases = dat;
        variables = new NodeList();
        variables = cases.getVariables();
        result = new RecursivePotentialTree();
        this.buildRoot();
       
    }

    private Node chooseCandidate(int index){
        Random generator = new Random();
        int value = generator.nextInt((candidates.get(index)).size());
        return candidates.get(index).get(value);
    }

    private void addParent(int index){

        TreeNode modified = ((ListTreeNode)result.getRoot()).getChild(index).copy();
        Node parent = this.chooseCandidate(index);

        HashSet<Node> vars=modified.getVariables();
        // Now add the content of vars to variables
        Vector<Node> collectedVars=new Vector<Node>();
        if(vars!=null){
            collectedVars.addAll(vars);
        }

        Configuration conf = new Configuration(collectedVars);

        int possibleConf = conf.size();

        for(int i=0; i< possibleConf; i++){

        }

        TreeNode newSplit = new SplitTreeNode(parent);



    }

//    private void addSN(int index){
//
//        TreeNode modified = ((ListTreeNode)result.getRoot()).getChild(index).copy();
//
//        //El padre debe ser elegido de candidates[index]
//        Node parent = null;
//
//        TreeNode newSplit = new SplitTreeNode(parent);
//
//        int states = ((FiniteStates)parent).getNumStates();
//        for(int j=0; j<states; j++){
//             ((SplitTreeNode)newSplit).addSon(modified);
//        }
//
//        //faltaria recalcular los parámetros
//        NodeList vars = new NodeList();
//        HashSet<Node> varsInset = newSplit.getVariables();
//        Iterator it = varsInset.iterator();
//        while(it.hasNext()){
//            vars.insertNode((Node)it.next());
//        }
//        PotentialTable pot = cases.getPotentialTable(vars);
//
//        Configuration conf = new Configuration(vars);
//        int size = conf.possibleValues();
//        for(int i=0; i<size; i++){
//            ValueTreeNode val = new ValueTreeNode(pot.getValue(conf));
//            Util.updateSplitChainLeaf(newSplit, conf, val);
//            conf.nextConfiguration();
//        }
//
//        ((ListTreeNode)result.getRoot()).replaceChild(index, newSplit);
//
//    }

//    private void removeSN(int index, Node node){
//        TreeNode modified = ((ListTreeNode)result.getRoot()).getChild(index).copy();
//        modified = this.removeSNaux(modified, node);
//         ((ListTreeNode)result.getRoot()).replaceChild(index, modified);
//    }

//    private TreeNode removeSNaux(TreeNode toremove, Node node){
//        if(toremove.getClass()==ListTreeNode.class){
//            int size = ((ListTreeNode)toremove).getNumberOfChildren();
//            for(int i=0; i<size; i++){
//                ((ListTreeNode)toremove).replaceChild(i, this.removeSNaux(((ListTreeNode)toremove).getChild(i), node));
//            }
//        }
//        if(toremove.getClass()==SplitTreeNode.class){
//            SplitTreeNode newNode = new SplitTreeNode(((SplitTreeNode)toremove).getVariable());
//            if(((SplitTreeNode)toremove).getVariable().equals(node)){
//                newNode = (SplitTreeNode) toremove.addVariable(node);
//            }else{
//                int size = ((SplitTreeNode)toremove).getNumberOfStates();
//                for(int i=0; i<size; i++){
//                    newNode.addSon(this.removeSNaux(((SplitTreeNode)toremove).getSon(i), node));
//                }
//            }
//            return newNode;
//        }
//        if(toremove.getClass()==PotentialTreeNode.class){
//            return ((PotentialTreeNode)toremove).addVariable(node);
//        }
//        return toremove;
//    }

    private void factorize(){

    }

    private void colapseLN(){

    }

    private void buildRoot(){
        TreeNode root = new ListTreeNode();
        candidates = new ArrayList<ArrayList<Node>>();
        candidateIndex = new Hashtable();

        int size = variables.size();
        for(int i=0; i<size; i++){

            Node variable = variables.elementAt(i);
            TreeNode child = new SplitTreeNode(variable);
            ((ListTreeNode)root).addChild(child);
            candidates.add(new ArrayList<Node>());
            candidateIndex.put(variable, i);

            for(int j=0; j<size; j++){
                Node candidate = variables.elementAt(j);
                if(!candidate.equals(child)){
                    candidates.get(i).add(candidate);
                }
            }

            int states = ((SplitTreeNode)child).getNumberOfStates();
            NodeList var = new NodeList();
            var.insertNode(variable);
            PotentialTree tree = cases.getPotentialTree(var);
            Configuration conf = new Configuration(var);
            for(int j=0; j<states; j++){
                TreeNode value = new ValueTreeNode(tree.getValue(conf));
                conf.nextConfiguration();
                ((SplitTreeNode)root).addSon(value);

            }
            ((ListTreeNode)root).addChild(child);
        }

        result.setValues(root);
    }

    public RecursivePotentialTree learn(){

        CaseListMem clm = cases.getCaseListMem();
        Vector<Configuration> clmvect = new Vector<Configuration>();
        int sizeConf = clm.getNumberOfCases();
        for(int i=0; i<sizeConf; i++){
            clmvect.add(clm.get(i));
        }

        double likelihood = result.evaluateSetOfConfigurations(clmvect);
        double variation = 1;

        //elegir factor
        //elegir operador
        //calcular likelihood

        return null;
    }
    

}

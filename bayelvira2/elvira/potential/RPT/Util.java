/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT;

import elvira.Node;
import elvira.potential.Potential;
import elvira.FiniteStates;
import elvira.Configuration;

import java.util.Vector;

/**
 *
 * @author mgomez
 */
public class Util {
   
   /**
    * Method for printing a string of a certain length
    */
   public static String getSpacesString(int spaces){
       String space=new String(" ");
       String result=new String();
       
       for(int i=0; i < spaces; i++){
         result=result+space;    
       }
       
       // return result
       return result;
   }
   
   /**
    * Static method for checking if a node is contained in the domain
    * of the potential passed as argument
    * @param node
    * @param potential
    * @return boolean value
    */
   public static boolean checkVarInDomain(Node node, Potential potential){
       boolean result;
       
       Vector varsInPot=potential.getVariables();
       result=varsInPot.contains(node);
        
       // Return result
       return result;
   }

   public static TreeNode createSplitChain(Vector<Node> variables){

       TreeNode root = null;

       FiniteStates var = (FiniteStates) variables.get(0);
       variables.remove(var);
       root = new SplitTreeNode(var);
       for(int i=0; i< var.getNumStates(); i++){
           //System.out.println("DENTRO DEL BUCLE...."+i);
           if(variables.isEmpty()){
               ((SplitTreeNode)root).addSon(null);
           }
           else{
                ((SplitTreeNode)root).addSon(createSplitChain(variables));
           }
       }
       variables.add(0, var);
       return root;
   }

   /**
    *
    * @param chain should be modified
    * @param conf is modified!!!
    * @param tree
    */
   public static void updateSplitChainLeaf(TreeNode chain, Configuration conf, TreeNode tree){
       //System.out.println(" ++ EMPIEZA EL METODO DE METER EN LA CADENA DE SPLIT UN TREENODE ++ ");
       if(conf.size()==1){
           //System.out.println(" ++ Almacena el treenode ++ ");
           //tree.print(0);
           //System.out.println(" ++ en configuracion ++ ");
           //conf.print();
            ((SplitTreeNode)chain).setSon(tree, conf.getValue(0));
        }
        else{
            FiniteStates node = (FiniteStates)((SplitTreeNode)chain).getVariable();
            int state = conf.getValue(node);
            conf.remove(node);
            updateSplitChainLeaf(((SplitTreeNode)chain).getSon(state), conf,tree);
        }
        //System.out.println(" ++ ACABA EL METODO DE METER EN LA CADENA DE SPLIT UN TREENODE ++ ");
   }

}

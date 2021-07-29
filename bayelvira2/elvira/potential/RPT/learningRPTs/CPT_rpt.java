/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.potential.RPT.learningRPTs;

import elvira.potential.RPT.RecursivePotentialTree;
import elvira.potential.RPT.ListTreeNode;
import elvira.potential.RPT.ValueTreeNode;
import elvira.potential.RPT.TreeNode;
import elvira.potential.RPT.SplitTreeNode;
import elvira.potential.PotentialTable;
import elvira.Node;
import elvira.NodeList;
import elvira.Configuration;
import elvira.FiniteStates;
import elvira.database.DataBaseCases;
import elvira.potential.PotentialTree;
import elvira.potential.Potential;

import java.util.Random;
import java.util.Vector;
import java.util.HashSet;
import java.io.FileInputStream;

/**
 *
 * @author cora
 */
public class CPT_rpt {

    private DataBaseCases db;
    private RecursivePotentialTree tree;
    private NodeList candidates;
    private Node headVar;
    private NodeList parents;
    private Node lastParent;
    private boolean FACTORIZED = false;
    
    //private PotentialTree pt;

    public CPT_rpt(Node node, NodeList cand, DataBaseCases data){
        db = data;
        NodeList aux = new NodeList();
        aux.insertNode(node);
        PotentialTable ptaux = db.getPotentialTable(aux);
        ptaux.LPNormalize();
       // pt = ptaux;
        headVar = node;
        candidates = cand;
        parents = new NodeList();
        tree = new RecursivePotentialTree();
        this.lastParent = this.headVar;
        SplitTreeNode root = new SplitTreeNode(headVar);

        Configuration conf = new Configuration(aux);
        int size = conf.possibleValues();
        for(int i=0; i<size; i++){
            root.addSon(new ValueTreeNode(this.getValueSafe(ptaux, conf)));
            conf.nextConfiguration();
        }

        tree.setValues(root);

    }

    protected Node getHeadNode(){
        return this.headVar;
    }

    protected void printCandidateList(){
        System.out.println("Candidates list for: "+this.headVar.getName());
        int size = candidates.size();
        Vector<Node> cands = this.candidates.getNodes();
        for(int i=0; i<size; i++){
            System.out.println(cands.get(i).getName());
        }
        System.out.println("******************************");
    }

    protected int getNumberOfVarsInTree(){
        return this.tree.getVariables().size();
    }



    protected boolean containsParent(Node node){
        return ((this.parents.getId(node)!= -1)? true:false);
    }

    private Node getOptimalNode(){
        
        
        
        return null;
    }

    protected Node getRandomNode(){
        Random seed = new Random();
        int randomIndex = seed.nextInt(candidates.size());
        Node parent = (Node) candidates.getNodes().get(randomIndex);
        //System.out.println("remove node from cand. list");
        candidates.removeNode(randomIndex);
       // lastParent = parent;
        return parent;
    }

    protected boolean hasCandidates(){
        return (this.candidates.size()>0);
    }

    /**
     * adds a SplitTreeNode of a random variable from the set of candidates
     * to all the leaves of the current RPT
     */
    protected void addParent(Node parent){

        parents.insertNode(parent);

        // Conf1: variables del arbol
        NodeList conf1 = new NodeList(this.tree.getVariables());
        Configuration confi1 = new Configuration(conf1);

        //Conf2: variables del arbol mas el nuevo padre
        NodeList conf2 = new NodeList(this.tree.getVariables());
        conf2.insertNode(parent);
        //Configuration confi2 = new Configuration(conf2);

        //Conf4: todas las variables menos la variable head
        NodeList conf4 = new NodeList(this.tree.getVariables());
        conf4.insertNode(parent);
        conf4.removeNode(this.headVar);
        //Configuration confi4 = new Configuration(conf4);

        //Potencial P(xh|x1..xp) = P(xh,x1..xp) / P(x1..xp)
        PotentialTable ptaux = db.getPotentialTable(conf2);
        ptaux.LPNormalize();
        PotentialTable ptauxpar = db.getPotentialTable(conf4);
        ptauxpar.LPNormalize();
        ptaux = ptaux.divide(ptauxpar);
        ptaux.normalizeOver((FiniteStates)this.headVar);

        int size = confi1.possibleValues();

        //Conf3: solo el padre
        NodeList conf3 = new NodeList();
        conf3.insertNode(parent);

        for(int i=0; i<size; i++){

            SplitTreeNode parentTree = new SplitTreeNode(parent);

            Configuration confi3 = new Configuration(conf3);

            //Configuracion para meter bien los valores: hacer confi1 U confi2
            
            for(int j=0; j<confi3.possibleValues(); j++){

                Configuration actual = new Configuration(conf2);
                actual.setValues(confi1, confi3);
                parentTree.addSon(new ValueTreeNode(this.getValueSafe(ptaux, actual)));
                confi3.nextConfiguration();
            }

            tree.getRoot().replaceLeavesConf(parentTree, confi1);
            confi1.nextConfiguration();
        }

        lastParent = parent.copy();
        this.removeNodeFromCandidates(parent);
        this.tree.refreshVariables();

    }


    protected void setCandidates(NodeList cand){
        this.candidates = cand.copy();
    }
    protected NodeList getCandidates(){
        return this.candidates.copy();
    }


    protected CPT_rpt duplicate(){
        CPT_rpt aux = new CPT_rpt(this.headVar.copy(),this.candidates.duplicate(), this.db);
        aux.tree = (RecursivePotentialTree)this.tree.copy();
        aux.parents = this.parents.duplicate();
        aux.lastParent = this.lastParent.copy();
        aux.FACTORIZED = this.FACTORIZED;
        aux.db = this.db.copy();
//        aux.pt = (PotentialTree)this.pt.copy();

        return aux;
    }

    protected void removeNodeFromCandidates(Node node){
       // System.out.println("Remove "+node.getName()+" from "+this.headVar.getName()+" candidate list");
        this.candidates.removeNode(node);
    }

    protected NodeList getParentsCopy(){
        if(this.parents.size()>0){
            return new NodeList(this.parents.copy().getNodes());
        }
        else{
            return null;
        }
    }

    protected void addParentConf(Node parent){

        //1. Random configuration del ultimo padre añadido
        Vector<Node> var = new Vector<Node>();
        var.add(this.lastParent);
        Vector<Integer> val = new Vector<Integer>();
        Random seed = new Random();
        int numStates = ((FiniteStates)this.lastParent).getNumStates();
        val.add(seed.nextInt(numStates));
        Configuration confRandom = new Configuration(var,val);
//        System.out.println("Random conf: ");
//        confRandom.pPrint();

        // 2. compute P(xh|x1..xp)
        //Conf2: variables del arbol mas el nuevo padre
        NodeList conf2 = new NodeList(this.tree.getVariables());
        conf2.insertNode(parent);
        //Conf4: todas las variables menos la variable head
        NodeList conf4 = new NodeList(this.tree.getVariables());
        conf4.insertNode(parent);
        conf4.removeNode(this.headVar);

        //Potencial P(xh|x1..xp) = P(xh,x1..xp) / P(x1..xp)

        PotentialTable ptaux = new PotentialTable(conf2);

        Configuration conf = new Configuration(conf2);
        Configuration confparents = new Configuration(conf4);
        int totalconf = confparents.possibleValues();
        int totalstates = ((FiniteStates)this.headVar).getNumStates();

        for(int i=0; i<totalconf; i++){
            PotentialTable ptaux2 = db.getPotentialTable(((FiniteStates)this.headVar), confparents);
            conf.setValues(confparents, confparents);
            for(int j=0; j<totalstates; j++){
                conf.putValue(((FiniteStates)this.headVar), j);
                ptaux.setValue(conf, ptaux2.getValue(conf));
            }
            confparents.nextConfiguration();
        }

        //ptaux.LPNormalize();
//        PotentialTable ptaux = db.getPotentialTable(conf2);
//        ptaux.LPNormalize();
//        PotentialTable ptauxpar = db.getPotentialTable(conf4);
//        ptauxpar.LPNormalize();
//        ptaux = ptaux.divide(ptauxpar);

        //3. introduce the cs independency

        NodeList original = new NodeList(this.tree.getVariables());
        Configuration confaux = new Configuration(original);

        Vector<Node> varnp = new Vector<Node>();
        varnp.add(parent);
        Configuration confnp = new Configuration(varnp);
        
        Configuration extra = new Configuration(conf2);

        int states = confaux.possibleValues();
        int statesParent = ((FiniteStates)parent).getNumStates();

        for(int i=0; i<states; i++){
            if(!confaux.isCompatibleWeak(confRandom)){
                extra.setValues(confaux, confnp);
                double value = ptaux.getValue(extra);
                for(int j=1; j<statesParent;j++){
                    confnp.nextConfiguration();
                    extra.setValues(confaux, confnp);
                    ptaux.setValue(extra, value);
                }
            }
            confaux.nextConfiguration();
        }

       //4. normalize wrt head var
        ptaux.LPNormalize();
        ptaux.normalizeOver((FiniteStates)this.headVar);
//        System.out.println("Potential should be normalized");
//        ptaux.print(2);
//        System.out.println(" *************************   ");
        
        parents.insertNode(parent);

        //5. actualize tree (insert split nodes) 
        this.tree.findAndReplaceSplit(lastParent, confRandom, parent, ptaux);

        //other stuff
        lastParent = parent.copy();
        this.removeNodeFromCandidates(parent);
        this.tree.refreshVariables();
    }

    protected void addParentConf2(Node parent){

         //Random configuration del ultimo padre añadido
        Vector<Node> var = new Vector<Node>();
        var.add(this.lastParent);
        Vector<Integer> val = new Vector<Integer>();
        Random seed = new Random();
        int numStates = ((FiniteStates)this.lastParent).getNumStates();
        val.add(seed.nextInt(numStates));
        Configuration confRandom = new Configuration(var,val);

//        System.out.println("Random conf: ");
//        confRandom.pPrint();

        //System.out.println("Current tree: ");
        //this.tree.print(0);

        //Conf2: variables del arbol mas el nuevo padre
        NodeList conf2 = new NodeList(this.tree.getVariables());
        conf2.insertNode(parent);
        //Configuration confi2 = new Configuration(conf2);

        //Conf4: todas las variables menos la variable head
        NodeList conf4 = new NodeList(this.tree.getVariables());
        conf4.insertNode(parent);
        conf4.removeNode(this.headVar);
        //Configuration confi4 = new Configuration(conf4);

        //Potencial P(xh|x1..xp) = P(xh,x1..xp) / P(x1..xp)
        PotentialTable ptaux = db.getPotentialTable(conf2);
        ptaux.LPNormalize();
        PotentialTable ptauxpar = db.getPotentialTable(conf4);
        ptauxpar.LPNormalize();
        ptaux = ptaux.divide(ptauxpar);

        ptaux.normalizeOver((FiniteStates)this.headVar);

//        System.out.println("Potencial a testear......");
//        ptaux.print(2);
//        System.out.println("-------------------------");

        parents.insertNode(parent);



        this.tree.findAndReplaceSplit(lastParent, confRandom, parent, ptaux);

        lastParent = parent.copy();
        this.removeNodeFromCandidates(parent);
        this.tree.refreshVariables();

//        System.out.println("Potencial a testear, el guardado en el RPT......");
//        PotentialTable generated = this.tree.getPotentialTable();
//        generated.print(2);
//        System.out.println("-------------------------");

    }
    
    protected TreeNode factorize(SplitTreeNode root){

        if(root.getVariables().size() >= 1){
//            System.out.println("FACTORIZE! ");
            ListTreeNode newroot = new ListTreeNode();
            //right part
            TreeNode right = root.getSon(0);
            //left part
            SplitTreeNode left = new SplitTreeNode(root.getVariable());
            TreeNode value = new ValueTreeNode(1.0);
            //TreeNode prop = right.copy();
            left.addSon(value);
            left.addSon(new ValueTreeNode(2.0));
            newroot.addChild(left);
            newroot.addChild(right);
            this.FACTORIZED = true;
            return newroot;
        }else{
//            System.out.println("No hay mas de una variable");
            return root;
        }
    }

    protected TreeNode tryFactorize(TreeNode root){
        //elegir una rama de root de tree
        Random seed = new Random();
        if(root.getClass() == ListTreeNode.class){
            return this.specialFactorization(root);
        }else{
            if(root.getClass() == SplitTreeNode.class){
                int states = ((SplitTreeNode)root).getNumberOfStates();
                if(!root.containsLists()){
                    HashSet vars = ((SplitTreeNode)root).getSon(0).getVariables();
                    for(int i=1; i< states; i++){
                        HashSet vars2 = ((SplitTreeNode)root).getSon(i).getVariables();
                        if(vars2!=null){
                            if(!(vars.size() == vars2.size() && vars.containsAll(vars2))){
                                int son = seed.nextInt(states);
                                TreeNode newson = this.factorize((SplitTreeNode)((SplitTreeNode)root).getSon(son));
                                ((SplitTreeNode)root).replaceChild(son, newson);
                                return root;
                            }
                        }
                    }
                    root = this.factorize((SplitTreeNode)root);
                    return root;
                }else{
                    int son = seed.nextInt(states);
                    return this.tryFactorize(((SplitTreeNode)root).getSon(son));
                }
            }else{
                return root; //Value node
            }
        }
        //es factorizable?
            //all subbranches have the same set of vars?
                //NO -> factorize branches separately (recursive call?)
            //contains factorizations?
                //YES -> special factorization
            //num de variables mayor que 2?
            //componente aleatoria para decidir si factorizar o no (alpha)
                //SI: factorizar
    }

    protected boolean isFactorized(){
        return this.FACTORIZED;
    }

    protected void factorize2(){

        if(this.FACTORIZED){
//            System.out.println("Already factorized");
            //return this.getRPT();
        }else{
            if(this.getRPT().getClass() == SplitTreeNode.class){
                int states = ((SplitTreeNode)this.getRPT()).getNumberOfStates();
              //  for(int i=0; i< states; i++){
                Random seed = new Random();
                int son = seed.nextInt(states);
                //for(int i=0; i<states; i++);
                if((((SplitTreeNode)this.getRPT()).getSon(son)).getClass() == SplitTreeNode.class){
                   // if(((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(son)).childrenAreSplit()){
                    HashSet vars2 = ((SplitTreeNode)this.getRPT()).getSon(son).getVariables();
                    if(vars2!=null){
                        TreeNode newson = this.factorize((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(son));

                        if(newson.getClass()==ListTreeNode.class){
                        //Now normalize
                        Vector<Node> vars = new Vector<Node>();
                        FiniteStates parentVar = (FiniteStates)((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(1-son)).getVariable();
                        vars.add(parentVar);
                        vars.addAll(((ListTreeNode)newson).getChild(1).getVariables());

                        Configuration conST = new Configuration(vars);
                        conST.setValue(0, 1);
                        int sizeSubtree = conST.possibleValues() - 4;

                        for(int j=0; j<sizeSubtree; j++){
                            double right = newson.getValue(conST);
//                            conST.pPrint();
//                            System.out.println("Valor a actualizar: "+(1-right));
                            ((SplitTreeNode)this.getRPT()).getSon(1-son).replaceLeavesConf(new ValueTreeNode(1-right), conST);
                            conST.nextConfiguration();
                            //conST.insert((FiniteStates)((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(1-son)).getVariable(),1);
                        }
                    }else{
//                            System.out.println("No ha factorizado");
                    }
                        ((SplitTreeNode)this.getRPT()).replaceChild(son, newson);
                        if(this.FACTORIZED){
//                            System.out.println("FACTORIZATION PERFORMED!");
                        }
                        }else{
//                            System.out.println("Son doesn't have children");
                        }
                }else{
//                    System.out.println("Son is not Split Node");
                }
            }else{
//            System.out.println("This RPT's root is not a Split Node");
            }
        }//end else
    }

   protected void factorize3(){

        if(this.FACTORIZED){
//            System.out.println("Already factorized");
            //return this.getRPT();
        }else{
            if(this.getRPT().getClass() == SplitTreeNode.class){
                int states = ((SplitTreeNode)this.getRPT()).getNumberOfStates();
              //  for(int i=0; i< states; i++){
                Random seed = new Random();
                int son = seed.nextInt(states);
                int otro;
                if(son == 0) otro = 1; else otro = 0;
                //for(int i=0; i<states; i++);
                if((((SplitTreeNode)this.getRPT()).getSon(son)).getClass() == SplitTreeNode.class){
                   // if(((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(son)).childrenAreSplit()){
                    HashSet vars2 = ((SplitTreeNode)this.getRPT()).getSon(son).getVariables();
                    if(vars2!=null){

                        //////////////////////////////////////////////////////////////
                        TreeNode newson = this.factorize((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(son));

                        if(newson.getClass()==ListTreeNode.class){

                            //comprobar que el hijo no es un nodo valor!

                            if(((ListTreeNode)newson).getChild(1).getClass() == SplitTreeNode.class){
                            Configuration confAux = new Configuration(new Vector(((ListTreeNode)newson).getChild(1).getVariables()));
                            confAux.putValue((FiniteStates)((SplitTreeNode)((ListTreeNode)newson).getChild(1)).getVariable(), 1);
                            //compute p
                            double p = ((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(otro)).getValue(confAux) / ((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(son)).getValue(confAux)  ;
                            //actualize p
                            ((SplitTreeNode)((ListTreeNode)newson).getChild(0)).setSon(new ValueTreeNode(p), 1);
                            //make a copy of the tied parameters
                            TreeNode copy = ((SplitTreeNode)((ListTreeNode)newson).getChild(1)).copy();
                            RecursivePotentialTree copyRPT = new RecursivePotentialTree();
                            copyRPT.setValues(copy);
                            Configuration copyconf = new Configuration(new Vector(copy.getVariables()));
                            int numStates = copyconf.possibleValues();
                            for(int i=0; i<numStates; i++){
                                copyRPT.setValue(copyconf, (1 - copyRPT.getValue(confAux)));
                                copyconf.nextConfiguration();
                            }
                            //set the copy as child of the other son
                            ((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(otro)).replaceChild(1, copyRPT.getRoot());
                            ((SplitTreeNode)this.getRPT()).replaceChild(son, newson);
                          }else{
                            //compute p
                            double p = ((ValueTreeNode)((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(otro)).getSon(1)).getValue() / ((ValueTreeNode)((ListTreeNode)newson).getChild(1)).getValue();
                            //actualize p
                            ((SplitTreeNode)((ListTreeNode)newson).getChild(0)).setSon(new ValueTreeNode(p), 1);
                            //make a copy of the tied parameters
                            double val = 1 - ((ValueTreeNode)((ListTreeNode)newson).getChild(1)).getValue();
                            //set the copy as child of the other son
                            ((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(otro)).replaceChild(1, new ValueTreeNode(val));
                            ((SplitTreeNode)this.getRPT()).replaceChild(son, newson);
                          }

//                        //Now normalize
//                        Vector<Node> vars = new Vector<Node>();
//                        FiniteStates parentVar = (FiniteStates)((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(1-son)).getVariable();
//                        vars.add(parentVar);
//                        vars.addAll(((ListTreeNode)newson).getChild(1).getVariables());
//
//                        Configuration conST = new Configuration(vars);
//                        conST.setValue(0, 1);
//                        int sizeSubtree = conST.possibleValues() - 4;
//
//                        for(int j=0; j<sizeSubtree; j++){
//                            double right = newson.getValue(conST);
//                            conST.pPrint();
//                            System.out.println("Valor a actualizar: "+(1-right));
//                            ((SplitTreeNode)this.getRPT()).getSon(1-son).replaceLeavesConf(new ValueTreeNode(1-right), conST);
//                            conST.nextConfiguration();
//                            //conST.insert((FiniteStates)((SplitTreeNode)((SplitTreeNode)this.getRPT()).getSon(1-son)).getVariable(),1);
//                        }//end for
                    }else{
//                            System.out.println("No ha factorizado");
                    }
                    ((SplitTreeNode)this.getRPT()).replaceChild(son, newson);
                    //falta sustituir el hijo(1) del hijo de la derecha

                    //////////////////////////////////////////////////////////////

                    if(this.FACTORIZED){
//                        System.out.println("FACTORIZATION PERFORMED!");
                    }
                    }else{
//                        System.out.println("Son doesn't have children");
                    }
                }else{
//                    System.out.println("Son is not Split Node");
                }
            }else{
//            System.out.println("This RPT's root is not a Split Node");
            }
        }//end else
    }

    protected void setRPT(TreeNode root){
        this.tree.setValues(root);
    }

    private TreeNode specialFactorization(TreeNode root){
        //factorize a factorization! only apply it to right part
        TreeNode newson = ((ListTreeNode) root).getChild(1);
        newson = this.tryFactorize(newson);
//        if(result){
            ((ListTreeNode)root).replaceChild(1, newson);
//        }
        return root;
    }

    public TreeNode getRPT(){
        return this.tree.getRoot();
    }

    /**
     * retrieves the value of configuration conf in potential p. If p contains more variables than
     * conf, then it retrieves the value of the p marginalizing out all the variables not contained
     * in conf.
     *
     * @param p
     * @param conf
     * @return
     */
    static public double getValueSafe(Potential p, Configuration conf){

        Vector vars = (Vector)p.getVariables().clone();
        vars.removeAll(conf.getVariables());
        if(!vars.isEmpty()){

            Configuration auxc = new Configuration(vars);
            auxc.setValues(conf, conf);
            return p.getValue(auxc);

//            Potential aux = p.marginalizePotential(vars);
//            return aux.getValue(conf);
        }

        return p.getValue(conf);
    }

}

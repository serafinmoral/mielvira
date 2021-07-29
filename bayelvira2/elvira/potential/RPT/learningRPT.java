/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.potential.RPT;


import elvira.LinkList;
import elvira.potential.Potential;
import elvira.potential.PotentialTable;
import elvira.Node;
import elvira.FiniteStates;
import elvira.Configuration;
import elvira.Link;
import elvira.NodeList;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Cora
 */

public class learningRPT {

    double epsilon;
    double delta;

    /**
     * constructs an instance of potential factorization algorithm.
     *
     * @param eps factor for the mutual information 
     * @param delt factor for the gain of information, goes between 0 and 1
     */
    public learningRPT(double eps, double delt){
        epsilon = eps;
        delta = delt;
    }

    public LinkList buildGraph(Potential pot) {
        //System.out.println("Build graph with potential: ");
        //pot.print();
        Vector<Node> vars = (Vector<Node>) pot.getVariables().clone();
        LinkList list = new LinkList();
        int size = vars.size();
        Potential pi, pj, pij;
        Vector<Node> vars2 = (Vector<Node>) vars.clone();
        //System.out.println(vars2.size());
        double entropy;
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {

                Vector<Node> vars3 = new Vector<Node>();
                vars3.add(vars2.get(j));
                vars3.add(vars2.get(i));

                pij = pot.marginalizePotential(vars3);

                // Creo que hay que normalizar antes de calcular la información
                // mutua, porque si no puede salir negativa.
                pij.normalize();

                //System.out.println("POTENCIAL CONJUNTO");
                //pij.print();

                pi = pij.addVariable(vars.get(j));
                pj = pij.addVariable(vars.get(i));
                //System.out.println("MARGINALES");
                //pi.print();
                //pj.print();


                //entropy = conditionedCrossEntropy(pi, pj, pij, pij);
                entropy = crossEntropy(pi,pj,pij);
                double newEpsilon = epsilon * (Math.log(((FiniteStates)vars2.get(i)).getNumStates()) + Math.log(((FiniteStates)vars2.get(j)).getNumStates()));
                //System.out.println("entropía " + entropy);
                //System.out.println("Entropía entre "+vars.get(j).getName()+" y "+vars.get(i).getName()+" = "+entropy);
                // asc begin 13/4
                //if (entropy > newEpsilon) {
                if (Math.abs(entropy) > newEpsilon) {
                // asc end
                    Link link = new Link(vars.get(i), vars.get(j));
                    link.setWidth(entropy);
                    list.insertLink(link);
                }
            }
        }
        return list;
    }

    public static double crossEntropy(Potential pi, Potential pj, Potential pij) {

        Vector<Node> varY = new Vector<Node>();
        varY.addAll(pi.getVariables());
        Vector<Node> varJ = new Vector<Node>();
        varJ.addAll(pj.getVariables());
        Vector<Node> varYJ = new Vector<Node>();
        varYJ.addAll(varY);
        int size = varJ.size();

        // no recuerdo por que diablos hice esto aqui
        // asc begin
        // jajaja! creo que se puede quitar.
        // Creo que era por lo del marginalize y addVariable.
        //Ya lo veo, en varYJ tienen que ir las variables de Y y las de J,
        //arriba le meto las de Y, y aqui las de J

        for(int i=0; i<size;i++){
            Node element = varJ.get(i);
            if(!varYJ.contains(element)){
                varYJ.add(element);
            }
        }



        Configuration confYJ = new Configuration(varYJ);
        double entropy = 0;

        for (int i = 0; i < confYJ.possibleValues(); i++) {
                //double aux = pij.getValue(confYJ) * (Math.log(pij.getValue(confYJ)) - Math.log(pi.getValue(confYJ)) - Math.log(pj.getValue(confYJ)));
                double aux = plogp(pij.getValue(confYJ),Math.log(pij.getValue(confYJ)) - Math.log(pi.getValue(confYJ)) - Math.log(pj.getValue(confYJ)));
                entropy = entropy + aux;
                confYJ.nextConfiguration();
            }
        return entropy;
    }

    /**
     * elige la variable candidata a eliminar, la que la sumatoria de pesos de los arcos
     * hacia sus vecinos sea mayor.
     *
     * También se puede hacer recorriendo la lista de enlaces y sumando a cada variable
     * (cabeza y cola) el peso del link. Esto puede ser más rápido
     *
     * @param graph
     * @param vars
     * @return
     */
    public Node chooseVarByNeightbourhood(LinkList graph, Vector<Node> vars) {
        int varToDel = 0;
        int size = vars.size();
        double maxSum = -10000000.0;
        for (int i = 0; i < size; i++) {
            double sum = 0.0;
            for (int j = 0; j < size; j++) {
                Link searched = graph.getLinks(vars.get(i).getName(), vars.get(j).getName());
                double weight = 0;
                if (searched != null) {
                    weight = searched.getWidth();
                    //System.out.println("peso del enlace "+ weight);
                    sum = sum + weight;
                }

                // Repetimos para el link en sentido inverso

                searched = graph.getLinks(vars.get(j).getName(), vars.get(i).getName());
                if (searched != null) {
                    weight = searched.getWidth();
                    //System.out.println("peso del enlace "+ weight);
                    sum = sum + weight;
                }
            }
            //System.out.println("Para la variable "+vars.get(i).getName()+" la suma de pesos de sus vecinos es "+sum);
            if (sum > maxSum) {
                maxSum = sum;
                varToDel = i;
            }
        //System.out.println(" maximo valor: "+maxSum+" de la variable "+vars.get(varToDel).getName());
        }
        return vars.get(varToDel);
    }

    public TreeNode factorize(Potential argPot) {

        Potential potential = argPot.copy();
        LinkList graph = this.buildGraph(potential);
        Vector<Set<Node>> clusters = getConnectedComponents(graph, potential.getVariables());
        TreeNode recTree = null;
        Vector<Node> vars = (Vector<Node>)potential.getVariables().clone();
        if (clusters.size() > 1) { // grafo desconectado
            recTree = dealWithDisconnectedGraph(potential, clusters);
        } else { //grafo conectado
            if (clusters.size() == 1 && clusters.get(0).size() <= 2) {
                recTree = new PotentialTreeNode(potential.copy());
                //((PotentialTreeNode)recTree).storeAsTree();
            }else {
                recTree = dealWithConnectedGraph(potential, clusters, graph);
            }
        }
        return recTree;
    }//fin del algoritmo

    private TreeNode dealWithConnectedGraph(Potential potential,Vector<Set<Node>> clusters,LinkList graph){
        TreeNode recTree = null;
        Vector<Node> vars = (Vector<Node>)potential.getVariables().clone();
        Vector<Node> condVars = new Vector<Node>();
        Vector<Node> y1 = new Vector<Node>();
        Vector<Node> y2 = new Vector<Node>();
        boolean independentSplit = false;
        while (clusters.size() == 1 && clusters.get(0).size() > 2 && !independentSplit) { //mientras siga conectado y haya mas de dos variables
            Node varToDel = chooseVarByNeightbourhood(graph, vars);
            condVars.add(varToDel);
            LinkList graphConditioned = this.buildConditionedGraph(potential, condVars);
            Vector<Node> remainVars = (Vector<Node>) potential.getVariables().clone();
            remainVars.removeAll(condVars);
            if (this.connectsAll(varToDel, graph, remainVars)) {
                y1.add(varToDel);
                independentSplit = this.createSplit(potential, (FiniteStates)varToDel);
            } else {
                y2.add(varToDel);
            }

            clusters = getConnectedComponents(graphConditioned, remainVars);
            if (clusters.size() > 1 || (clusters.size()==1 && clusters.get(0).size()<=2)) { //Si el grafo se desconecta, ramificamos
                if (y1.isEmpty()) {
                     recTree = dealWithoutSplitChain(potential, clusters, y2);
                } else {
                    recTree = dealWithSplitChain(potential, clusters, y1, y2);
                }
            }
            else{
                if(independentSplit){
                    recTree = dealWithIndependentSplit(potential, y1);
                }
            }
            vars.remove(varToDel);

        }// end of while
        return recTree;
    }

    private TreeNode dealWithIndependentSplit(Potential pot, Vector<Node> y1){
       // System.out.println("CREATE INDEPENDENT SPLIT CHAIN!   **************************");
        TreeNode recTree = null;
        Configuration conf = new Configuration(y1);
        recTree = elvira.potential.RPT.Util.createSplitChain(y1);
        for (int i = 0; i < conf.possibleValues(); i++) {
            Configuration auxConf = conf.duplicate();
            Potential auxPot = pot.restrictVariable(auxConf);
            TreeNode tree = factorize(auxPot);
            elvira.potential.RPT.Util.updateSplitChainLeaf(recTree, conf.duplicate(), tree);
            conf.nextConfiguration();
        }

        return recTree;
    }

    private TreeNode dealWithSplitChain(Potential potential,Vector<Set<Node>> clusters, Vector<Node> y1, Vector<Node> y2){
        TreeNode recTree = null;
        Configuration conf = new Configuration(y1);
        recTree = elvira.potential.RPT.Util.createSplitChain(y1);
        for (int i = 0; i < conf.possibleValues(); i++) {
            Configuration auxConf = conf.duplicate();
            Potential auxPot = potential.restrictVariable(auxConf);
            double totPot = auxPot.totalPotential();
            Vector<Node> aux = (Vector<Node>) y2.clone();
            TreeNode tree = null;
            if (clusters.size() > 1) {
                tree = new ListTreeNode();
                for (int j = 0 ; j < clusters.size() ; j++) {
                    aux = (Vector<Node>) y2.clone();
                    aux.addAll(clusters.get(j));
                    Potential pot = auxPot.marginalizePotential(aux);
                    TreeNode childTree = factorize(pot);
                    ((ListTreeNode)tree).addChild(childTree);
                }

                if(y2.isEmpty()){
                RecursivePotentialTree rt = new RecursivePotentialTree();
                rt.setValues(tree);
                Potential pt = rt.getPotentialTable();
//                pt = pt.invert();
//                ((ListTreeNode) tree).addChild(new PotentialTreeNode(pt));
                double t = pt.totalPotential();
                ((ListTreeNode) tree).addChild(new ValueTreeNode(totPot / t));
                }else{
                Potential pt = potential.marginalizePotential(y2);
                pt = this.sptPowerOf(pt, (1 - clusters.size()));
                //pt = pt.invert();
                ((ListTreeNode) tree).addChild(new PotentialTreeNode(pt));
                }


            }
            else {
                // System.out.println("DEAL WITH SPLIT CHAIN ********************************************************************************************************************:        ");
                Potential operatedPot = auxPot.copy();
                tree = factorize(operatedPot);
            }
            elvira.potential.RPT.Util.updateSplitChainLeaf(recTree, conf.duplicate(), tree);
            conf.nextConfiguration();
        }

        return recTree;
    }
    
    private Potential sptPowerOf(Potential pt, int exp){
        
        
        //System.out.println("the one to elevate to "+exp);
        //pt.print();
        
        Potential aux = new PotentialTable(pt);
        
        Configuration conf = new Configuration(aux.getVariables());
        
        for(int i=0; i<conf.possibleValues(); i++){
            aux.setValue(conf, Math.pow(aux.getValue(conf),exp));
            conf.nextConfiguration();
        }
        
        //aux.print();
        
        //System.out.println("that was the elevated");
        return aux;
    }

    private TreeNode dealWithoutSplitChain(Potential potential,Vector<Set<Node>> clusters, Vector<Node> y2){
        TreeNode recTree = null;

        double totPot = potential.totalPotential();
        int size = clusters.size();
        Vector<Node> aux = (Vector<Node>) y2.clone();
        if(size == 1){
          //  System.out.println("DEAL WITHOUT SPLIT CHAIN ********************************************************************************************************************:        ");
                aux.addAll(clusters.get(0));
                Potential pot = potential.copy();
                recTree = new PotentialTreeNode(pot);
                 //((PotentialTreeNode)recTree).storeAsTree();
        }
        else{
            recTree = new ListTreeNode();
            for (int i = 0; i < size; i++) {
                aux.addAll(clusters.get(i));
                Potential pot = potential.marginalizePotential(aux);
                ((ListTreeNode) recTree).addChild(factorize(pot));
                aux = (Vector<Node>) y2.clone();
            }

            if(y2.isEmpty()){
                RecursivePotentialTree rt = new RecursivePotentialTree();
                rt.setValues(recTree);
                PotentialTable pt = rt.getPotentialTable();
                double t = pt.totalPotential();
                ((ListTreeNode) recTree).addChild(new ValueTreeNode(totPot / t));
            }else{
                Potential pt = potential.marginalizePotential(y2);
                //pt = pt.invert();
                pt = this.sptPowerOf(pt, (1 - clusters.size()));
                ((ListTreeNode) recTree).addChild(new PotentialTreeNode(pt));
            }
        }

        return recTree;
    }

    private TreeNode dealWithDisconnectedGraph(Potential potential,Vector<Set<Node>> clusters){
        TreeNode recTree = null;
        Vector<Node> vars = (Vector<Node>)potential.getVariables().clone();
            recTree = new ListTreeNode();
            double sum = potential.totalPotential();
            for (int i = 0; i < clusters.size(); i++) {
                vars.clear();
                vars.addAll(clusters.get(i));
                Potential aux = potential.marginalizePotential(vars);
                if (clusters.get(i).size() > 2) {
                    ((ListTreeNode) recTree).addChild(factorize(aux));
             } else {

//                    PotentialTreeNode auxnode = new PotentialTreeNode(aux.copy());
//                    auxnode.storeAsTree();
                 ((ListTreeNode) recTree).addChild(new PotentialTreeNode(aux.copy()));
             }
         }
         RecursivePotentialTree rt = new RecursivePotentialTree();
         rt.setValues(recTree);
         PotentialTable pt = rt.getPotentialTable();
         double t = pt.totalPotential();
        ((ListTreeNode)recTree).addChild(new ValueTreeNode(sum / t));

        return recTree;
    }

    /**
     * @param pot
     * @param epsilon
     * @param varToCond
     * @return
     * 
     */
    public LinkList buildConditionedGraph(Potential potential, Vector<Node> varToCond) {

        int statesCond = 1;
                for(int k=0; k<varToCond.size(); k++){
                    statesCond = statesCond * ((FiniteStates)varToCond.get(k)).getNumStates();
                }
        Potential pot = potential.copy();
        // asc begin
        // le añado el .clone
        Vector<Node> vars = (Vector<Node>) pot.getVariables().clone();
        // asc end
        vars.removeAll(varToCond);
        LinkList list = new LinkList();
        int size = vars.size();
        Potential pi, pj, pij, pijc, pc;
        double entropy;
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {

                Vector<Node> varsijc = new Vector<Node>();
                varsijc.add(vars.get(j));
                varsijc.add(vars.get(i));
                varsijc.addAll(varToCond);
                pijc = pot.marginalizePotential(varsijc);
                // Ahora tengo que normalizarlo
                pijc.normalize();

                // Ahora tengo que calcular el potencial condicionado,
                // dividiendo sobre la marginal sobre las variables
                // a las que condicionar.
                pc = pijc.marginalizePotential(varToCond);
                Potential pijcConditioned = null;
                pijcConditioned = pijc.divide(pc);

                pi = pijc.addVariable(vars.get(j)).divide(pc);
                pj = pijc.addVariable(vars.get(i)).divide(pc);

                entropy = conditionedCrossEntropy(pi, pj, pijc, pijcConditioned);
                //System.out.println("Entropía condicionada entre "+vars.get(j).getName()+" y "+vars.get(i).getName()+" = "+entropy);


                int statesi = ((FiniteStates)vars.get(i)).getNumStates() + statesCond;
                int statesj = ((FiniteStates)vars.get(j)).getNumStates() + statesCond;

                double newEpsilon = epsilon * (Math.log(statesi) + Math.log(statesj));

                //System.out.println("entropía condicionada " + entropy);
                // asc begin 13/4
                //if (entropy > newEpsilon) {
                if (Math.abs(entropy) > newEpsilon) {
                // asc end
                    Link link = new Link(vars.get(i), vars.get(j));
                    link.setWidth(entropy);
                    list.insertLink(link);
                }
            }
        }
        return list;
    }

    public static double conditionedCrossEntropy(Potential pi, Potential pj, Potential pij, Potential pijc) {


        Vector<Node> varYJC = new Vector<Node>();
        varYJC.addAll(pijc.getVariables());

        Configuration confYJC = new Configuration(varYJC);
        double entropy = 0;

        for (int j = 0; j < confYJC.possibleValues(); j++) {

            double aux = plogp(pij.getValue(confYJC),(Math.log(pijc.getValue(confYJC)) - Math.log(pi.getValue(confYJC)) - Math.log(pj.getValue(confYJC))));

            entropy = entropy + aux;
            confYJC.nextConfiguration();
        }

        return entropy;
    }

    private static double plogp(double p, double logp){
        if(p==0){
            return 0.0;
        }
        else{
            return p*logp;
        }
    }

    /**
     * Checks if the variable is connected to all the variables in the graph
     * 
     * @param varToSort
     * @param graph
     * @param variables
     * @return
     */
    public boolean connectsAll(Node varToSort, LinkList graph, Vector<Node> variables) {
        for (int i = 0; i < variables.size(); i++) {
            if ( (graph.getLinks(varToSort.getName(), variables.get(i).getName()) == null) && (graph.getLinks(variables.get(i).getName(),varToSort.getName()) == null)) {
                return false;
            }
        }
        return true;
    }

    /**
     * calcula los conjuntos de variables de un grafo desconectado
     * @param list Lista de enlaces del grafo
     * @return devuelve un vector de conjuntos de nodos, tantos como partes
     * independientes tenga el grafo desconectado. Si el grafo es conectado,
     * entonces el vector sólo tendrá un elemento que será un conjunto con
     * todas las variables del potencial.
     *
     * NOTA:
     * Si el conjunto no posee todas las variables del potencial que se esté
     * analizando es porque no estaban en la lista de enlaces, por lo que
     * cada variable será independiente de las demás (grafo no conectado)
     *
     */
    // asc begin 26/3/2010
    // public Vector<Set<Node>> getConnectedComponents(LinkList list, Vector<Node> variables) {
    public Vector<Set<Node>> getConnectedComponents(LinkList list, Vector<Node> variables2) {
        Vector<Node> variables = (Vector<Node>)variables2.clone();
        // one cluster for each variable
        Vector<Set<Node>> clusters = new Vector<Set<Node>>();
        for(int i=0; i< variables.size(); i++){
            Set<Node> var = new HashSet<Node>();
            var.add(variables.get(i));
            clusters.add(var);
        }

        //merge clusters if variabes are connected
        Vector<Link> links = list.getLinks();
        for(int i=0; i<links.size(); i++){
            Node head = links.get(i).getHead();
            Node tail = links.get(i).getTail();
            Set<Node> setHead = find(clusters, head);
            Set<Node> setTail = find(clusters, tail);
            if(setHead!=setTail){
                merge(clusters, setHead, setTail);
            }
        }
        return clusters;
    }

    private Set<Node> find(Vector<Set<Node>> clusters,Node node){
        //System.out.println(node.getName());
        for(int i=0; i<clusters.size(); i++){
            if(clusters.get(i).contains(node)){
                return clusters.get(i);
            }
        }
        return null;
    }

    private void merge(Vector<Set<Node>> clusters, Set<Node> setHead, Set<Node> setTail){
        int indexHead = clusters.indexOf(setHead);
        int indexTail = clusters.indexOf(setTail);
        clusters.get(indexHead).addAll(setTail);
        clusters.remove(indexTail);
    }

    public double informationGain(Potential pot, FiniteStates var){

        double s = pot.totalPotential();
        double s_y = 0.0;
        int numStates = var.getNumStates();
        Vector<Node> vars = new Vector<Node>();
        vars.add(var);
        Configuration conf = new Configuration(vars);

        for(int i=0; i<numStates; i++){
            Potential aux = pot.restrictVariable(conf);
            double totPot = aux.totalPotential();
            s_y += totPot * Math.log(totPot);
            conf.nextConfiguration();
        }

        double infGain = s * (Math.log(numStates) - Math.log(s)) + s_y;
        return infGain;
    }

    public double computeBoundary(Potential pot, FiniteStates var){
        double s = pot.totalPotential();
        int states = var.getNumStates();

        double s_y = 0.0;
        int numStates = var.getNumStates();
        Vector<Node> vars = new Vector<Node>();
        vars.add(var);
        Configuration conf = new Configuration(vars);
        double suma = 0.0;

        for(int i=0; i<numStates; i++){
            Potential aux = pot.restrictVariable(conf);
            double totPot = aux.totalPotential();
            suma += totPot;
            conf.nextConfiguration();
        }

        s_y = suma*Math.log(suma);
        
        double boundary = delta * s * (Math.log(states) - Math.log(s) + s_y);
        return boundary;
    }

    public boolean createSplit(Potential pot, FiniteStates var){

        double infGain = this.informationGain(pot, var);
        //System.out.println("GANANCIA DE INFORMACIÓN:        "+ infGain);
        double boundary = this.computeBoundary(pot, var);

      //  System.out.println("LIMITE GANANCIA DE INFORMACIÓN:        "+ boundary);

        if(infGain >= boundary){
            return true;
        }

        return false;
    }

}






/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.potential.RPT;

import elvira.*;
import elvira.database.DataBaseCases;
import elvira.learning.*;
import elvira.parser.ParseException;
import elvira.potential.Potential;
import elvira.potential.PotentialTable;
import elvira.potential.PotentialTree;
import elvira.potential.RPT.SplitTreeNode;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author Cora
 */
public class learningRPTDB {
    
    private DataBaseCases cases;
    private Configuration branch;
    private int metric;
    private Vector<Node> conditioning;
    private double epsilon;
    
    /**
     * builds an instance of the algorithm
     * sets the metric to the default value, 0 (BDe metric)
     * 
     * @param dat database 
     */
    public learningRPTDB(DataBaseCases dat, int metricaux){
        cases = dat;
        metric = metricaux;
        branch = new Configuration();
        conditioning = new Vector<Node>();
        epsilon = 0.02;
    }
    
    public void setEpsilon(double val){
        epsilon = val;
    }
    /**
     * changes the metric to compute the links in the graphs, that by default is BDe (0)
     * @param newMode BDe (0)
     */
    public void changeMetric(int newMetric){
        metric = newMetric;
    }
    
    
    /**
     * performs the learing process, to get a Recursive Probability Tree from a DB.
     *
     * @param variables the set of variables of the model
     * @return a TreeNode that would be the root of the learned RPT
     */
    public TreeNode learn(NodeList variables) {

//        System.out.println();
//        variables.printNames();
//        System.out.println();
        TreeNode recTree = null;
        if(variables.size()> 2){ //if there are two or less vars in the database, don't factorize them, just store as Potential Node
            LinkList graph = this.buildGraph(variables);
            Vector<Set<Node>> clusters = getConnectedComponents(graph, (Vector<Node>) variables.getNodes());
        
            if (clusters.size() > 1) { // grafo desconectado
                recTree = dealWithDisconnectedGraph(variables,clusters);
            }else{ //grafo conectado
            if (clusters.size() == 1 && clusters.get(0).size() <= 2) {
                
                Vector<Node> auxVect = new Vector<Node>();
                auxVect.addAll(clusters.get(0));
                NodeList auxList = new NodeList(auxVect);
                PotentialTree tree = null;
                PotentialTable auxpot;
                auxpot = cases.getPotentialTable(auxList,branch);
            /**
             * here check conditioning list for normalizing 
             */                
                this.LPNormalizeAux(auxpot);
                //auxpot.LPNormalize();
                tree = new PotentialTree(auxpot);
                recTree = new PotentialTreeNode(tree);
                ((PotentialTreeNode)recTree).setInfo(branch);
                ((PotentialTreeNode)recTree).setCond(this.conditioning);
            }else {
                //System.out.println("connected graph");
                recTree = dealWithConnectedGraph(variables, clusters, graph);
            }}
        }else{
            //System.out.println("cluster of #vars = "+variables.size());
            //PotentialTree tree = null;
            PotentialTable auxpot;
            auxpot = cases.getPotentialTable(variables,branch);
            /**
             * here check conditioning list for normalizing 
             */
            
            this.LPNormalizeAux(auxpot);
            //auxpot.LPNormalize();
            //System.out.println("cluster of final #vars = "+auxpot.getVariables().size());
            PotentialTree tree = new PotentialTree(auxpot);
            recTree = new PotentialTreeNode(tree);
            ((PotentialTreeNode)recTree).setInfo(branch);
            ((PotentialTreeNode)recTree).setCond(this.conditioning);
        }
        return recTree;
    }//fin del algoritmo
 
    /**
     * Builds the initial graph, considering scores only between pairs of variables,
     * without dependency to others.
     * It adds a link when score(X|Y) is bigger than score(X), and the weight of
     * the link would be the positive difference
     *
     * @param cases database sampled from the original model
     * @return linklist with the initial graph.
     */
    private LinkList buildGraph(NodeList variables){
        if(metric==0){
            return this.buildGraphBDe(variables);
        }else{
            return this.buildGraphIM(variables);
        }
    }
    
    
    private LinkList buildGraphBDe(NodeList variables) {
        Vector<Node> vars = (Vector<Node>) variables.getNodes();
        LinkList list = new LinkList();
        int size = vars.size();
        Metrics met;
        met = new BDeMetrics(cases, 2);
        
        NodeList listi = new NodeList();
        NodeList listj = new NodeList();
        double score = 0.0;
        
        for (int i = 0; i < size; i++) {
            listi.insertNode(vars.get(i));
            listj.insertNode(vars.get(i));
            double partialScore = met.score(listi);
            for (int j = i+1; j < size; j++) {
                listj.insertNode(vars.get(j));
                double condScore = met.score(listj);
                score = condScore - partialScore;
                if (score > 0) {
                    Link link = new Link(vars.get(i), vars.get(j));
                    link.setWidth(score);
                    list.insertLink(link);
                }
              listj.removeNode(vars.get(j));

            }
            listi.removeNode(vars.get(i));
            listj.removeNode(vars.get(i));
        }
        return list;
    }
    
    public LinkList buildGraphIM(NodeList variables) {
        Vector<Node> vars = (Vector<Node>) variables.getNodes();
        //System.out.println("Build graph with potential: ");
        //pot.print();
        //Vector<Node> vars = (Vector<Node>) pot.getVariables().clone();
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
                NodeList vars3aux = new NodeList(vars3);

                pij = this.cases.getPotentialTable(vars3aux,branch);
                        //pot.marginalizePotential(vars3);

                // Creo que hay que normalizar antes de calcular la información
                // mutua, porque si no puede salir negativa.
                this.LPNormalizeAux((PotentialTable)pij);
                //pij.normalize();

                //System.out.println("POTENCIAL CONJUNTO");
                //pij.print();

                pi = pij.addVariable(vars.get(j));
                pj = pij.addVariable(vars.get(i));
                //System.out.println("MARGINALES");
                //pi.print();
                //pj.print();


                //entropy = conditionedCrossEntropy(pi, pj, pij, pij);
                entropy = crossEntropy(pi,pj,pij);
                double newEpsilon = this.epsilon * (Math.log(((FiniteStates)vars2.get(i)).getNumStates()) + Math.log(((FiniteStates)vars2.get(j)).getNumStates()));
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
        //System.out.println(list.toString());
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

    private static double plogp(double p, double logp){
        if(p==0){
            return 0.0;
        }
        else{
            return p*logp;
        }
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
    private Vector<Set<Node>> getConnectedComponents(LinkList list, Vector<Node> variables2) {
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
    
    /**
     * looks for a variable in a vector of clusters.
     *
     * @param clusters vector containing all the clusters
     * @param node variable to search for
     * @return the cluster where the variable was found. If the variable was not in any cluster, it returns null.
     */
    private Set<Node> find(Vector<Set<Node>> clusters,Node node){
        //System.out.println(node.getName());
        for(int i=0; i<clusters.size(); i++){
            if(clusters.get(i).contains(node)){
                return clusters.get(i);
            }
        }
        return null;
    }

    /**
     * combines two clusters and replaces both for their combination in the vector
     *
     * @param clusters vector containing all the clusters
     * @param setHead cluster to combine (1)
     * @param setTail cluster to combine (2)
     */
    private void merge(Vector<Set<Node>> clusters, Set<Node> setHead, Set<Node> setTail){
        int indexHead = clusters.indexOf(setHead);
        int indexTail = clusters.indexOf(setTail);
        clusters.get(indexHead).addAll(setTail);
        clusters.remove(indexTail);
    }

    /**
     * builds a multiplicative factorization, by creating the list node and applying
     * the learn(DB) method recursively to the children
     *
     * @param cases database to learn from
     * @param clusters vector that contains the different clusters of variables that compose the decomposition
     * @return a TreeNode containing the multiplicative factorization
     */
   private TreeNode dealWithDisconnectedGraph(NodeList variables, Vector<Set<Node>> clusters){
        TreeNode recTree = new ListTreeNode();
        NodeList varsInClusters = new NodeList();
        //Configuration originalBranch = branch.duplicate();

        //System.out.println("Disconnected");
        for (int i = 0; i < clusters.size(); i++) {
            Vector<Node> vars = new Vector<Node>();
            vars.addAll(clusters.get(i));

            Iterator it = clusters.get(i).iterator();
            while(it.hasNext()){
                varsInClusters.insertNode((Node)it.next());
            }
            if (clusters.get(i).size() > 2) {
                //System.out.println("Disconnected BIG");
                NodeList auxrestricted = new NodeList(vars);
                ((ListTreeNode) recTree).addChild(learn(auxrestricted));
            }else {
                //System.out.println("Disconnected SMALL");
                Vector<Node> auxVect = new Vector<Node>();
                auxVect.addAll(clusters.get(i));
                NodeList auxList = new NodeList(auxVect);
                PotentialTree tree = null;
                PotentialTable auxpot;
                auxpot = cases.getPotentialTable(auxList,branch);
            /**
             * here check conditioning list for normalizing 
             */        
                this.LPNormalizeAux(auxpot);
                //auxpot.LPNormalize();
                tree = new PotentialTree(auxpot);
                PotentialTreeNode child = new PotentialTreeNode(tree);
                child.setInfo(branch);
                ((PotentialTreeNode)child).setCond(this.conditioning);
                ((ListTreeNode) recTree).addChild(child);
            }
         }
        //branch = originalBranch;
        return recTree;
    }
    
    /**
     * tries to decompose a cluster of variables into a factorization, and if it can't find it,
     * tries to find c-s independencies.
     *
     * @param cases database to learn from
     * @param clusters vector of only one element containing the cluster of nodes to analyze
     * @param graph contains the links of the connected component
     * @return a TreeNode that would be the root of the RPT learned for the cluster of variables
     */
    private TreeNode dealWithConnectedGraph(NodeList variables, Vector<Set<Node>> clusters,LinkList graph){
        //Configuration originalBranch = branch.duplicate();
        TreeNode recTree = null;
        Vector<Node> vars = (Vector<Node>)variables.getNodes().clone();
        Vector<Node> condVars = new Vector<Node>();
        Vector<Node> y1 = new Vector<Node>();
        Vector<Node> y2 = new Vector<Node>();
        Vector<Node> datavars = (Vector<Node>)variables.getNodes().clone();

        while (clusters.size() == 1 && clusters.get(0).size() > 2) { //mientras siga conectado y haya mas de dos variables

            Node varToDel = chooseVarByNeightbourhood(graph, vars);
            condVars.add(varToDel);
            datavars.remove(varToDel);
            LinkList graphConditioned = this.buildConditionedGraph(condVars, datavars);
            //System.out.println(graphConditioned.toString());

            if (this.connectsAll(varToDel, graph, datavars)) {
                //System.out.println("Variable "+varToDel.getName()+" in y1");
                y1.add(varToDel);
            } else {
                //System.out.println("Variable "+varToDel.getName()+" in y2");
                y2.add(varToDel);
            }

            clusters = getConnectedComponents(graphConditioned, datavars);

            if (clusters.size() > 1 || (clusters.size()==1 && clusters.get(0).size()<=2)) { //Si el grafo se desconecta, ramificamos
                if (y1.isEmpty()) {
                    //System.out.println("Desconectado, sin cadena de splits");
                    recTree = dealWithoutSplitChain(variables, clusters, y2);
                } else {
                    //System.out.println("Desconectado, con cadena de splits o dos o menos variables");
                    recTree = dealWithSplitChain(variables, clusters, y1, y2);
                }
            }
            vars.remove(varToDel);

        }// end of while
        //branch = originalBranch;
        return recTree;
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
                    sum = sum + weight;
                }

                // Repetimos para el link en sentido inverso

                searched = graph.getLinks(vars.get(j).getName(), vars.get(i).getName());
                if (searched != null) {
                    weight = searched.getWidth();
                    sum = sum + weight;
                }
            }
            if (sum > maxSum) {
                maxSum = sum;
                varToDel = i;
            }
        }
        return vars.get(varToDel);
    }

    
    public LinkList buildConditionedGraph(Vector<Node> varToCond, Vector<Node> dataVars) {
        if(this.metric==0){
            return this.buildConditionedGraphBDe(conditioning, conditioning);
        }else{
            return this.buildConditionedGraphIM(varToCond, dataVars);
        }
    }
    
    
    /**
     * Builds a graph considering the scores between pairs of variables, but conditioning
     * it to a set of variables given as argument. The nodes to be considered are stated
     * in a vector given as argument. It may not contain all the variables of the DB.
     *
     * @param cases database sampled from the original model
     * @param varToCond set of variables for conditioning
     * @param dataVars set of nodes for the graph
     * @return
     */
    public LinkList buildConditionedGraphBDe(Vector<Node> varToCond, Vector<Node> dataVars) {

        Vector<Node> vars = dataVars;
        vars.removeAll(varToCond);
        LinkList list = new LinkList();
        int size = vars.size();
        double score = 0.0;
        Metrics met;
        
        if(metric==0){
            met = (Metrics) new BDeMetrics(cases, 2);
        }else{
            met = (Metrics) new BDeMetrics(cases, 2);
        }
        
        NodeList varsToCond = new NodeList();
        NodeList varsToCond2 = new NodeList();

        for (int i = 0; i < size; i++) {
            varsToCond.insertNode(vars.get(i));
            varsToCond2.insertNode(vars.get(i));

            for (int j = i+1; j < size; j++) {
                varsToCond.insertNode(vars.get(j));
                for(int k=0; k<varToCond.size(); k++){
                    varsToCond.insertNode(varToCond.get(k));
                    varsToCond2.insertNode(varToCond.get(k));
                }
                double partialScore = met.score(varsToCond);
                score = met.score(varsToCond2) - partialScore;
                if (score < 0) {
                    Link link = new Link(vars.get(i), vars.get(j));
                    link.setWidth(score);
                    list.insertLink(link);
                }
                varsToCond2 = new NodeList();
                varsToCond2.insertNode(vars.get(i));
                varsToCond = new NodeList();
                varsToCond.insertNode(vars.get(i));
            }
            
            varsToCond = new NodeList();

        }
        return list;
    }


    public LinkList buildConditionedGraphIM(Vector<Node> varToCond, Vector<Node> dataVars) {

        int statesCond = 1;
                for(int k=0; k<varToCond.size(); k++){
                    statesCond = statesCond * ((FiniteStates)varToCond.get(k)).getNumStates();
                }
                
        //        
            
                
        //Potential pot = potential.copy();
        // asc begin
        // le añado el .clone
        Vector<Node> vars = (Vector<Node>) dataVars.clone();
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
                NodeList vars3aux = new NodeList(varsijc);
                
                pijc = this.cases.getPotentialTable(vars3aux,branch);    
                this.LPNormalizeAux((PotentialTable)pijc);
                //pijc = pot.marginalizePotential(varsijc);
                
                // Ahora tengo que normalizarlo
                //pijc.normalize();

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
     * creates a multiplicative factorization, because no c-s independencies were found
     *
     * @param cases database to learn the model from
     * @param clusters set of clusters that composes the decomposition
     * @param y2 set of variables to be added to each cluster
     * @return a TreeNode that would be the root of the RPT learned
     */
    private TreeNode dealWithoutSplitChain(NodeList variables, Vector<Set<Node>> clusters, Vector<Node> y2){
        TreeNode recTree = null;
        int size = clusters.size();
        Vector<Node> aux = (Vector<Node>) y2.clone();
        //Configuration originalBranch = branch.duplicate();
        
        if(size == 1){
            //APRENDER DISTRIBUCION
                aux.addAll(clusters.get(0));
                NodeList auxList = new NodeList(aux);
                PotentialTable auxpot = cases.getPotentialTable(auxList, branch);
            /**
             * here check conditioning list for normalizing 
             */
                this.LPNormalizeAux(auxpot);
                //auxpot.LPNormalize();
                PotentialTree tree = new PotentialTree(auxpot);
                recTree = new PotentialTreeNode(tree);
                ((PotentialTreeNode)recTree).setInfo(branch);
                ((PotentialTreeNode)recTree).setCond(this.conditioning);
        }
        else{
            
            recTree = new ListTreeNode();
            //NodeList varsInClusters = new NodeList();
            //Hacemos el primer cluster, actualizamos lista cond y hacemos los demas
                aux.addAll(clusters.get(0));
//                Iterator ita = clusters.get(0).iterator();
//                while(ita.hasNext()){
//                        varsInClusters.insertNode((Node)ita.next());
//                }
                NodeList auxrestricteda = new NodeList(aux);
                ((ListTreeNode) recTree).addChild(learn(auxrestricteda));
                aux = (Vector<Node>) y2.clone();
                
                this.conditioning.addAll(y2);
                
            for (int i = 1; i < size; i++) {
                aux.addAll(clusters.get(i));
//                Iterator it = clusters.get(i).iterator();
//                while(it.hasNext()){
//                        varsInClusters.insertNode((Node)it.next());
//                }
                NodeList auxrestricted = new NodeList(aux);
                
                ((ListTreeNode) recTree).addChild(learn(auxrestricted));
                aux = (Vector<Node>) y2.clone();
            }
            this.conditioning.removeAll(y2);
            
        }
        //branch = originalBranch;
        return recTree;
    }

    /**
     * constructs a Split Chain and applies the learn(DB) method to the leaves of it, restricting
     * the database given as argument.
     *
     * @param cases database to learn the model from
     * @param clusters set of clusters of variables containing the decomposition
     * @param y1 Variables to create the split chain
     * @param y2 Variables to be added to each cluster of the decomposition
     * @return a TreeNode that would be the root of the RPT learned
     */
    private TreeNode dealWithSplitChain(NodeList variables, Vector<Set<Node>> clusters, Vector<Node> y1, Vector<Node> y2){

        TreeNode recTree = null;
        Configuration originalBranch = branch.duplicate();
        
        recTree = this.createSplitChain(y1);
        
        Configuration conf = new Configuration(y1);
        int states = conf.possibleValues();
        
        for (int i = 0; i < states; i++) {
           
            Vector<Node> aux = (Vector<Node>) y2.clone();
            aux.addAll(branch.getVariables());
            //NodeList auxListconf = new NodeList(aux);
            //branch = new Configuration(branch, conf,auxListconf);
            branch.setValues(branch, conf);
            //System.out.println("branch actualizada");
            //branch.pPrint();
            TreeNode tree = null;
            
            if (clusters.size() > 1) {
                
                tree = new ListTreeNode();
                
                //Hacemos el primero y actualizamos conditioning
                    NodeList auxrestricteda = new NodeList(y2);
                    Iterator ita = clusters.get(0).iterator();
                    while(ita.hasNext()){
                        Node node = (Node)ita.next();
                        auxrestricteda.insertNode(node);
                    }
                    ((ListTreeNode)tree).addChild(learn(auxrestricteda));
                    this.conditioning.addAll(y2);
                    
                for (int j = 1 ; j < clusters.size() ; j++) {
                    NodeList auxrestricted = new NodeList(y2);
                    Iterator it = clusters.get(j).iterator();
                    while(it.hasNext()){
                        Node node = (Node)it.next();
                        auxrestricted.insertNode(node);
                    }
                    ((ListTreeNode)tree).addChild(learn(auxrestricted));
                }
                this.conditioning.removeAll(y2);
            }
            else {
                //APRENDER DISTRIBUCION ok
                aux.addAll(clusters.get(0));
                NodeList auxList = new NodeList(aux);
                PotentialTable auxpot = cases.getPotentialTable(auxList, branch);
            /**
             * here check conditioning list for normalizing 
             */
                this.LPNormalizeAux(auxpot);
                //auxpot.LPNormalize();
                PotentialTree treepot = new PotentialTree(auxpot);
                tree = new PotentialTreeNode(treepot);
                ((PotentialTreeNode)tree).setInfo(branch);
                ((PotentialTreeNode)tree).setCond(this.conditioning);
            }
            
            //modificarlo para la nueva estructura
            this.updateSplitChainLeaf(recTree, conf.duplicate(), tree);
            //elvira.potential.RPT.Util.updateSplitChainLeaf(recTree, conf.duplicate(), tree);
             
             conf.nextConfiguration();
        }//del for
        branch = originalBranch;
        return recTree;
    }
  
    private TreeNode createSplitChain(Vector<Node> y1){
        Configuration originalBranch = branch.duplicate();
        if(y1.size()==1){
            TreeNode root = new ListTreeNode();
            //computes the marginal of the first variable, consistent with the branch
            PotentialTable marginal = new PotentialTable();
            marginal = cases.getPotentialTable((FiniteStates)y1.get(0), branch);
            /**
             * here check conditioning list for normalizing 
             */
            this.LPNormalizeAux(marginal);
            //marginal.LPNormalize();
            //marginal.print();
            PotentialTree tree = new PotentialTree(marginal);
            PotentialTreeNode marginalnode = new PotentialTreeNode(tree);
            ((PotentialTreeNode)marginalnode).setInfo(branch);
            ((PotentialTreeNode)marginalnode).setCond(this.conditioning);
            //computes the split of the first variable
            TreeNode split = new SplitTreeNode(y1.get(0));
            int possibleStates = ((FiniteStates)y1.get(0)).getNumStates();

            for(int i=0; i<possibleStates; i++){
                originalBranch.putValue((FiniteStates)y1.get(0), i);
                ((SplitTreeNode) split).addSon(new ValueTreeNode(tree.getValue(originalBranch)));
            } 
            ((ListTreeNode)root).addChild(split);
            ((ListTreeNode)root).addChild(marginalnode);
            
            return root;
        }
        else{
            TreeNode root = new ListTreeNode();
            //computes the marginal of the first variable, consistent with the branch
            PotentialTable marginal = new PotentialTable();
            marginal = cases.getPotentialTable((FiniteStates)y1.get(0), branch);
            /**
             * here check conditioning list for normalizing 
             */
            this.LPNormalizeAux(marginal);
            //marginal.LPNormalize();
            PotentialTree tree = new PotentialTree(marginal);
            TreeNode marg = new PotentialTreeNode(tree);
            ((PotentialTreeNode)marg).setInfo(branch);
            ((PotentialTreeNode)marg).setCond(this.conditioning);
            //computes the split of the first variable
            TreeNode split = new SplitTreeNode(y1.get(0));
            int possibleStates = ((FiniteStates)y1.get(0)).getNumStates();
            Vector<Node> y1aux = new Vector<Node>();
            y1aux.addAll(y1);
            y1aux.remove(y1.get(0));

            originalBranch = branch.duplicate();
            for(int i=0; i<possibleStates; i++){
                branch.putValue((FiniteStates)y1.get(0), i);
                TreeNode son = this.createSplitChain(y1aux);
                ((SplitTreeNode) split).addSon(son);
            }
            branch = originalBranch;

            ((ListTreeNode)root).addChild(split);
            ((ListTreeNode)root).addChild(marg);
            //branch = originalBranch;
            
            return root;
        }
    }

    
    private void updateSplitChainLeaf(TreeNode recTree,Configuration conf,TreeNode tree){
        if(recTree.getClass()==ListTreeNode.class){
            this.updateSplitChainLeaf(((ListTreeNode)recTree).getChild(0),conf,tree);
        }else{
            FiniteStates splitvar = (FiniteStates)((SplitTreeNode)recTree).getVariable();
            TreeNode aux = ((SplitTreeNode)recTree).getSon(conf.getValue(splitvar));
            if(aux.getClass()==SplitTreeNode.class){
                FiniteStates splitson = (FiniteStates)((SplitTreeNode)aux).getVariable();
                ((SplitTreeNode)aux).setSon(tree,conf.getValue(splitson));
                ((SplitTreeNode)recTree).setSon(aux,conf.getValue(splitvar));
            }
            if(aux.getClass()==ListTreeNode.class){
                this.updateSplitChainLeaf(aux,conf,tree);
            }
            if(aux.getClass()==ValueTreeNode.class){
                ((SplitTreeNode)recTree).setSon(tree,conf.getValue(splitvar));
            }
            
        }
    }
    
    /**
     * normalizes conditioning to the set "conditioning" using Laplace correction
     * @param pot 
     */
    private void LPNormalizeAux(PotentialTable pot){
        
        //System.out.println("tam potential begin"+pot.getVariables().size());
        Vector<Node> varsPot = (Vector<Node>)pot.getVariables().clone();
        
        //if all vars in pot are contained in conditioning, return unity potential OK
        if(this.conditioning.containsAll(varsPot)){
            //System.out.println("Build unity potential");
            this.unityPotential(pot);
            //pot.print(0);
        }else{
        //else return potential normalized for each configuration of conditioning
            int size = varsPot.size();
            varsPot.removeAll(this.conditioning);
            if(size != varsPot.size()){
                //System.out.println("Return conditionally normalized potential "+pot.getVariables().size());
                //Vector<Node> varsCond = pot.getVariables();
                //varsCond.retainAll(this.conditioning);
                Configuration conditConf = new Configuration(this.conditioning);
                //conditConf.pPrint();
                int numConfs = conditConf.possibleValues();
                //varsPot las vars a condicionar
                //varsCond las vars condicionantes
                PotentialTable aux = new PotentialTable(varsPot);
                //System.out.println("mnumvars antes de la copia "+pot.getVariables().size());
                PotentialTable potclone = (PotentialTable)pot.copy();
                for(int i=0; i<numConfs; i++){
                    aux = (PotentialTable)potclone.restrictVariable(conditConf);
                    aux.LPNormalize();
                    //System.out.println("numvars despues de las restricciones "+pot.getVariables().size());
                    //Set pot in conditConf with aux
                    int confsAux = (int) aux.getSize();
                    Configuration potConf = new Configuration(aux.getVariables());
                    for(int j=0; j<confsAux; j++){
                        Configuration aux2 = new Configuration();
                        aux2.setValues(conditConf, potConf);
                        pot.setValue(aux2, aux.getValue(potConf));
                        potConf.nextConfiguration();
                        //System.out.println(j);
                    }
                    //conditConf.pPrint();
                    conditConf.nextConfiguration();
                }
                //System.out.println("Returned potential "+pot.getVariables().size());
            }else{
                //System.out.println("Return normlized potential");
                pot.LPNormalize();
            }

            //else if there are no vars in common, return pot normalized with Laplace    OK    
        }
        
       //System.out.println("tam potential end"+pot.getVariables().size());
    }
    
    /**
     * sets the potential to all 1s
     * @param pot 
     */
    private void unityPotential(PotentialTable pot){
        int size = (int)pot.getSize();
        double v[] = new double[size];
        for(int i=0; i<size; i++) v[i]=1;
        pot.setValues(v);
        
    }
    
 public static void main(String args[]) throws ParseException, IOException, Throwable { 
     
    // learningRPTDB.networks();
     //learningRPTDB.randomRPTs();
     
//     FileInputStream f = new FileInputStream("BDs/asiaDB.dbc");
//     
//     DataBaseCases cases = new DataBaseCases(f);
//     learningRPTDB instance = new learningRPTDB(cases,1);
//     Vector<Node> vect = new Vector<Node>();
//     vect.addAll(cases.getNewVectorOfNodes());
//     NodeList vars2 = new NodeList(vect);
//     TreeNode result = instance.learn(vars2);
//////     TreeNode result = instance.createSplitChain(vect);
//////     Configuration cero = new Configuration(vect);
//////     cero.pPrint();
//////     instance.updateSplitChainLeaf(result, cero, new ValueTreeNode(0.1));
//////     cero.nextConfiguration();
//////     cero.pPrint();
//////     instance.updateSplitChainLeaf(result, cero, new ValueTreeNode(0.2));
//////          cero.nextConfiguration();
//////     cero.pPrint();
//////          instance.updateSplitChainLeaf(result, cero, new ValueTreeNode(0.3));
//////          cero.nextConfiguration();
//////     cero.pPrint();
//////          instance.updateSplitChainLeaf(result, cero, new ValueTreeNode(0.4));
//////          cero.nextConfiguration();
//////     cero.pPrint();
//////          instance.updateSplitChainLeaf(result, cero, new ValueTreeNode(0.5));
////     result.print(0);
////     
//     RecursivePotentialTree newTree = new RecursivePotentialTree();
//     newTree.setValues(result);
//     newTree.print(0);
////     //newTree.normalize();
////     
//     PotentialTable display = newTree.RPTtoPotentialTable();
//     //display.print();
////
//     CaseListMem clm = cases.getCaseListMem();
//     Vector<Configuration> clmvect = new Vector<Configuration>();
//     int sizeConf = clm.getNumberOfCases();
//     for(int i=0; i<sizeConf; i++){
//        clmvect.add(clm.get(i));
//     }
//     Vector<Configuration> clmvect2 = (Vector<Configuration>) clmvect.clone();
//     
//     double likelihoodTree = newTree.evaluateSetOfConfigurations(clmvect);
//     System.out.print(likelihoodTree);  
//     Vector<Node> vect2 = new Vector<Node>();
//     vect2.addAll(cases.getNewVectorOfNodes());
//     NodeList vars = new NodeList(vect2);
//     PotentialTable newres = cases.getPotentialTable(vars);
//     
//            /**
//             * here check conditioning list for normalizing 
//             */
//     newres.LPNormalize();
//     //newres.print();
//     
//     //newres y display
//     Configuration conf = new Configuration(newres.getVariables());
//     for(int i=0; i<newres.getSize();i++){
//         System.out.println("RPT "+display.getValue(conf)+" tabla "+newres.getValue(conf));
//         conf.nextConfiguration();
//     }
//     
//     RecursivePotentialTree newTree2 = new RecursivePotentialTree();
//     PotentialTreeNode child = new PotentialTreeNode(newres);
//     //child.setInfo(branch);
//     newTree2.setValues(child);
//     double likelihoodTree2 = newTree2.evaluateSetOfConfigurations(clmvect2);
//     System.out.print("\t"+likelihoodTree2);     
//     
//     PC01Learning outputNet2 = new PC01Learning(cases);
//     outputNet2.setLevelOfConfidence(0);
//     outputNet2.learning();
//     LPLearning outputNet3 = new LPLearning(cases,outputNet2.getOutput());
//     outputNet3.learning();
//     
//   
//     
//        double likelihoodBnetPC = cases.logLikelihood(outputNet3.getOutput());
//        System.out.print("\t"+likelihoodBnetPC);  
//        
//        K2Learning outputNet6 = new K2Learning(cases, 3);
//        outputNet6.learning();
//        DELearning outputNet7 = new DELearning(cases,outputNet6.getOutput());
//        outputNet7.learning(2.0);
//        Bnet auxNet2 = outputNet7.getOutput();
//        double likelihoodBnetK2 = cases.logLikelihood(auxNet2);
//        System.out.print("\t"+likelihoodBnetK2);
     
     Bnet aux1 = new Bnet("BDs/nuevasRedes/munin1.elv");
     System.out.println("Munin links "+aux1.getLinkList().size()+" and nodes: "+aux1.getNodeList().size());
     
     Bnet aux2= new Bnet("BDs/nuevasRedes/andes.elv");
     System.out.println("Andes links "+aux2.getLinkList().size()+" and nodes: "+aux2.getNodeList().size());
     
     Bnet aux3 = new Bnet("BDs/nuevasRedes/water.elv");
     System.out.println("Water links "+aux3.getLinkList().size()+" and nodes: "+aux3.getNodeList().size());
     
     Bnet aux4 = new Bnet("BDs/nuevasRedes/Barley.elv");
     System.out.println("Barley links "+aux4.getLinkList().size()+" and nodes: "+aux4.getNodeList().size());

     Bnet aux5 = new Bnet("BDs/ELV/prostanetE.elv");
     System.out.println("Prostanet links "+aux5.getLinkList().size()+" and nodes: "+aux5.getNodeList().size());
     
     Bnet aux6 = new Bnet("BDs/ELV/pedigree4.elv");
     System.out.println("Pedigree links "+aux6.getLinkList().size()+" and nodes: "+aux6.getNodeList().size());
     
     Bnet aux7 = new Bnet("BDs/ELV/alarm.elv");
     System.out.println("Alarm links "+aux7.getLinkList().size()+" and nodes: "+aux7.getNodeList().size());
     
     Bnet aux8 = new Bnet("BDs/ELV/cancer.elv");
     System.out.println("Cancer links "+aux8.getLinkList().size()+" and nodes: "+aux8.getNodeList().size());
     
   }    
 
 

       public static void networks3()throws elvira.parser.ParseException ,java.io.IOException{

           int reps = 1;
           double init = 0;
           double fin = 30;
           double incr = 1;
           System.out.println();
           System.out.println();
           System.out.println("RPT\tPC\tK2\tsize RPT\tsize PC\t size K2");
           for(double ep = init; ep<=fin; ep=ep+incr){
                 for(int i=0; i<reps; i++)
                       learningRPTDB.asiaTest2("BDs/working/breast-cancer.dbc", "breast",286,ep);
                 System.out.println();
                 System.out.println();
           }
           System.out.println();
           System.out.println("**");
           
           for(double ep = init; ep<=fin; ep=ep+incr){
                for(int i=0; i<reps; i++)
                    learningRPTDB.asiaTest2("BDs/working/diabetesDiscretized.dbc","diabetes",768,ep);
                System.out.println();
                System.out.println();
           }
           System.out.println();
           System.out.println("**");
           
           for(double ep = init; ep<=fin; ep=ep+incr){
           for(int i=0; i<reps; i++)
           learningRPTDB.asiaTest2("BDs/working/ecoliDiscretized.dbc","ecoli",336,ep);
           System.out.println();
           System.out.println();
           }
           System.out.println();
           System.out.println("**");
           
           for(double ep = init; ep<=fin; ep=ep+incr){
           for(int i=0; i<reps; i++)
           learningRPTDB.asiaTest2("BDs/working/glass2Discretized.dbc","glass",163,ep);
           System.out.println();
           System.out.println();
           }
           System.out.println();
           System.out.println("**");
           
           for(double ep = init; ep<=fin; ep=ep+incr){
           for(int i=0; i<reps; i++)
           learningRPTDB.asiaTest2("BDs/working/heart-hDiscretized.dbc","heart",294,ep);
           System.out.println();
           System.out.println();
           }
           System.out.println();
           System.out.println("**");
           
           for(double ep = init; ep<=fin; ep=ep+incr){
           for(int i=0; i<reps; i++)
           learningRPTDB.asiaTest2("BDs/working/hepatitisDiscretized.dbc","hepatitis",155,ep);
           System.out.println();
           System.out.println();
           }
           System.out.println();
           System.out.println("**");
           
//           for(double ep = init; ep<=fin; ep=ep+incr){
//           for(int i=0; i<reps; i++)
//           learningRPTDB.asiaTest2("BDs/working/solar-flare_1.dbc","solar",323,ep);
//           System.out.println("");
//           System.out.println("");
//           }
//           System.out.println("");
//           System.out.println("**");
           
           System.out.println("breast cancer");
           System.out.println("diabetes");
           System.out.println("ecoli");
           System.out.println("glass");
           System.out.println("heart h");
           System.out.println("hepatitis");
           //System.out.println("solar flare");
       }

       //@Ignore
    //@Test
    public static void asiaTest2(String name, String apend, int numcases, double epsilon)throws elvira.parser.ParseException ,java.io.IOException{
        FileInputStream fis = new FileInputStream(name);

        DataBaseCases data = new DataBaseCases(fis);
        data.setNumberOfCases(numcases);

        DataBaseCases tr = new DataBaseCases();
        DataBaseCases ts = new DataBaseCases();

        data.divideIntoTrainAndTest(tr, ts, 0.8);

        FileWriter ftr = new FileWriter(apend+"_train.dbc");
        FileWriter fts = new FileWriter(apend+"_test.dbc");

        tr.saveDataBase(ftr);
        ts.saveDataBase(fts);

        learningRPTDB instance = new learningRPTDB(tr,0);
        instance.setEpsilon(epsilon);

        TreeNode result = instance.learn(data.getVariables());
        RecursivePotentialTree newTree = new RecursivePotentialTree();
        newTree.setValues(result);
        //newTree.normalize();

        CaseListMem clm = ts.getCaseListMem();
        Vector<Configuration> clmvect = new Vector<Configuration>();
        int sizeConf = clm.getNumberOfCases();
        for(int i=0; i<sizeConf; i++){
            clmvect.add(clm.get(i));
        }
        double likelihoodTree = newTree.evaluateSetOfConfigurations(clmvect);
        System.out.print(likelihoodTree);

        PC01Learning outputNet2 = new PC01Learning(tr);

        outputNet2.setLevelOfConfidence(0);
        outputNet2.learning();
//        DELearning outputNet3 = new DELearning(tr,outputNet2.getOutput());
//        outputNet3.learning(2.0);
      LPLearning outputNet3 = new LPLearning(tr,outputNet2.getOutput());
      outputNet3.learning();
        double likelihoodBnetPC = ts.logLikelihood(outputNet3.getOutput());
        System.out.print("\t"+likelihoodBnetPC);

        K2Learning outputNet6 = new K2Learning(tr, 3);
        outputNet6.learning();
        DELearning outputNet7 = new DELearning(tr,outputNet6.getOutput());
        outputNet7.learning(2.0);
        Bnet auxNet2 = outputNet7.getOutput();
        double likelihoodBnetK2 = ts.logLikelihood(auxNet2);
        System.out.print("\t"+likelihoodBnetK2);

        Vector relat = outputNet3.getOutput().getRelationList();
        int sizePC = 0;
        for(int i=0; i<relat.size();i++){
            sizePC += ((Relation)relat.get(i)).getValues().getSize();
        }

        relat = auxNet2.getRelationList();
        int sizeK2 = 0;
        for(int i=0; i<relat.size();i++){
            sizeK2 += ((Relation)relat.get(i)).getValues().getSize();
        }

        System.out.println("\t"+newTree.getSize()+"\t"+sizePC+"\t"+sizeK2);

        
    }
    
      public static double KLdivergence(Potential p1, Potential p2){

        Configuration conf = new Configuration(p1.getVariables());
        double KL = 0;
        for(int i=0; i<conf.possibleValues(); i++){
            //KL = KL + (p1.getValue(conf)*Math.log(p1.getValue(conf)/p2.getValue(conf)));
            double a = p1.getValue(conf);
            double b = p2.getValue(conf);
            if((a!=0)&&(b!=0)){
                KL = KL + (p1.getValue(conf)*(Math.log(a) - Math.log(b)));
            }
            //System.out.println(KL);
            conf.nextConfiguration();
        }

        return KL;
    }

       public static double meanSquaredError(Potential p1, Potential p2) {

        Configuration conf = new Configuration(p1.getVariables());
        double MSE = 0;
        for (int i = 0; i < conf.possibleValues(); i++) {
            //KL = KL + (p1.getValue(conf)*Math.log(p1.getValue(conf)/p2.getValue(conf)));
            MSE = MSE + Math.pow(p1.getValue(conf) - p2.getValue(conf),2);
            conf.nextConfiguration();
        }

        MSE = Math.sqrt(MSE/conf.possibleValues());

        return MSE;
    }

       
       public static void networks()throws elvira.parser.ParseException ,java.io.IOException{
           System.out.println("KL\tmse");
           System.out.println("Asia");
           System.out.println("Cancer");
           System.out.println("eMilk");
           System.out.println("eMetcancer");
           int samplesvect[] = {100,500,1000,2000,5000};
           
           for(int k=0; k<samplesvect.length;k++){
//            for(int i=0; i<30; i++)
//            //learningRPTDB.asiaTest("BDs/ELV/water.elv",samplesvect[k]);
//            System.out.println();
//            System.out.println();
            for(int i=0; i<30; i++)
            learningRPTDB.asiaTest("BDs/ELV/asia.elv",samplesvect[k]);
            System.out.println();
            System.out.println();
            for(int i=0; i<30; i++)
            learningRPTDB.asiaTest("BDs/ELV/cancer.elv",samplesvect[k]);
            System.out.println();
            System.out.println();
            for(int i=0; i<30; i++)
            learningRPTDB.asiaTest("BDs/ELV/eMilk.elv",samplesvect[k]);
            System.out.println();
            System.out.println();
            for(int i=0; i<30; i++)
            learningRPTDB.asiaTest("BDs/ELV/eMetCancer.elv",samplesvect[k]);
            
            System.out.println();
            System.out.println("+++");
            System.out.println();
            
//           System.out.println("pedigree4");
//           this.asiaTest("BDs/ELV/pedigree4.elv");
//           System.out.println("prostanetE");
//           this.asiaTest("BDs/ELV/prostanetE.elv");
           }
       }

       //@Ignore
    //@Test
    public static void asiaTest(String name,int samples)throws elvira.parser.ParseException ,java.io.IOException{
        
        FileInputStream fis = new FileInputStream(name);
        Bnet asia = new Bnet(fis);
        Vector<Relation> rels = asia.getRelationList();
        Potential combined = rels.get(0).getValues();
        for(int i=1; i<rels.size(); i++){
            combined = combined.combine(rels.get(i).getValues());
        }

        //System.out.println("relations combined");

        FileWriter ftr = new FileWriter("DBsampledBarley.dbc");
        
        DataBaseCases data = new DataBaseCases(asia,ftr,samples,true);
        //System.out.println("relations combined");
        //System.out.println("model learned");
        learningRPTDB instance = new learningRPTDB(data,0);
        TreeNode result = instance.learn(data.getVariables());

        
        

        RecursivePotentialTree newTree = new RecursivePotentialTree();
        newTree.setValues(result);

        System.out.println("total potential is "+newTree.totalPotential());
        //newTree.normalize();
        PotentialTable potab = newTree.getPotentialTable();
        
        

        double KL = learningRPTDB.KLdivergence(potab, combined);
        //double mse = learningRPTDB.meanSquaredError(combined, potab);
        
//
//        
//        PC01Learning outputNet2 = new PC01Learning(data);
//
//        outputNet2.setLevelOfConfidence(0);
//        outputNet2.learning();
////        DELearning outputNet3 = new DELearning(tr,outputNet2.getOutput());
////        outputNet3.learning(2.0);
//        LPLearning outputNet3 = new LPLearning(data,outputNet2.getOutput());
//        outputNet3.learning();
//        //System.out.println("aqui?");
//        
//        Bnet bnetPC = outputNet3.getOutput();
//        Vector<Relation> relsPC = bnetPC.getRelationList();
//        Potential combinedPC = relsPC.get(0).getValues();
//        for(int i=1; i<relsPC.size(); i++){
//            combinedPC = combinedPC.combine(relsPC.get(i).getValues());
//        }        
//        
//        
//        double klPC = learningRPTDB.KLdivergence(combinedPC, combined);
//        
//
//        K2Learning outputNet6 = new K2Learning(data, 3);
//        outputNet6.learning();
//        DELearning outputNet7 = new DELearning(data,outputNet6.getOutput());
//        outputNet7.learning(2.0);
//        
//        
//        Bnet bnetK2 = outputNet7.getOutput();
//        Vector<Relation> relsK2 = bnetK2.getRelationList();
//        Potential combinedK2 = relsK2.get(0).getValues();
//        for(int i=1; i<relsK2.size(); i++){
//            combinedK2 = combinedK2.combine(relsK2.get(i).getValues());
//        }    
//        double klK2 = learningRPTDB.KLdivergence(combinedK2, combined);
//        
//        System.out.println(KL + "\t"+klPC+ "\t"+klK2);
        System.out.println(KL);
//        System.out.println("And the tree: ");
//        newTree.print(2);
//        System.out.println("****");
    }       
 
       public static void randomRPTs()throws ParseException, FileNotFoundException, IOException, Throwable{
        System.out.println("Learning from random RPTs ");
        System.out.println("KL divergence\tMSE\tSIZEoriginal\tSIZE learned ");

        /**
         * random RPT
         */
        Node X1 = new FiniteStates("A", 2);
        Node X2 = new FiniteStates("B", 2);
        Node X3 = new FiniteStates("C", 2);
        Node X4 = new FiniteStates("D", 2);
        Node X5 = new FiniteStates("E", 2);
        Node X6 = new FiniteStates("F", 2);
        Node X7 = new FiniteStates("G", 2);
        Node X8 = new FiniteStates("H", 2);
        Node X9 = new FiniteStates("I", 2);
        Node X10 = new FiniteStates("J", 2);
        Node X11 = new FiniteStates("K", 2);
        Node X12 = new FiniteStates("L", 2);
        Node X13 = new FiniteStates("M", 2);
        Node X14 = new FiniteStates("N", 2);
        Node X15 = new FiniteStates("O", 2);
        Vector<Node> variables = new Vector<Node>();
        variables.add(X1);
        variables.add(X2);
        variables.add(X3);
        variables.add(X4);
        variables.add(X5);
        variables.add(X6);
        variables.add(X7);
        variables.add(X8);
        variables.add(X9);
        variables.add(X10);

        int depth = variables.size();
        double probPotential = 0.8;

        int samplesvect[] = {100,500,1000,2000,5000};
           
        for(int j=0; j<samplesvect.length;j++){
        for(double k=0.0; k<=1.0; k+=0.1){

        for(int iter = 0; iter < 30; iter++){
        RecursivePotentialTree instance = new RecursivePotentialTree();
        instance.setRandomRPT(variables,depth,k,probPotential);
        instance.setVariables(variables);
        instance.normalize();

        /**
         * sample and create database
         */
         CaseList database = new CaseListMem(variables);
         int samplesize = samplesvect[j];
         Vector<Configuration> samples = instance.simulateSample(samplesize);
         for(int i=0; i< samplesize; i++){
            database.put(samples.get(i));
         }

         DataBaseCases cases = new DataBaseCases("sampled", database);

        /**
         * build RPT
         */
        learningRPTDB instance2 = new learningRPTDB(cases,1);
        TreeNode result = instance2.learn(cases.getVariables());
        RecursivePotentialTree newTree = new RecursivePotentialTree();
        newTree.setValues(result);
        newTree.normalize();

        double KL = learningRPTDB.KLdivergence(newTree, instance);
        double mse = learningRPTDB.meanSquaredError(newTree, instance);
        System.out.println(KL + "\t"+mse+"\t"+ instance.getSize() + "\t"+newTree.getSize());

        //instance.print(0);
        //instance.generateDotFile("ejemploRPTaleatorio2.dot");
        //System.out.println("variables: "+instance.getVariables().size());

       }
        System.out.println();
        }
        System.out.println();
        System.out.println("****");
        System.out.println();
        }

       }    
    
}

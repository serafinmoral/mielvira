/*
 * method for learning an RPT from data
 */
package elvira.potential.RPT;

import elvira.LinkList;
import elvira.potential.Potential;
import elvira.potential.PotentialTable;
import elvira.Node;
import elvira.FiniteStates;
import elvira.CaseList;
import elvira.CaseListMem;
import elvira.Configuration;
import elvira.Link;
import elvira.NodeList;
import elvira.potential.PotentialTree;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import elvira.database.DataBaseCases;
import elvira.learning.BDeMetrics;
import elvira.learning.Metrics;
/**
 * Modifies the algorithm proposed in learningRPT.java, but this time
 * it learns the RPT from a database.
 *
 * @author Cora
 */

public class learningRPT_DB {

    //dataBase that we use to learn
    private DataBaseCases cases;
    private Configuration branch = null;
    private int mode;
    //private RecursivePotentialTree output;


    /**
     * constructs an instance of the learning algorithm.
     *
     * @param eps factor for the mutual information
     * @param delt factor for the gain of information, goes between 0 and 1
     */
    public learningRPT_DB(DataBaseCases dat){
        cases = dat;
        mode = 0;
    }
    
    /**
     * changes the metric to compute the links in the graphs, that by default is BDe (0)
     * @param newMode BDe (0)
     */
    public void changeMode(int newMode){
        mode = newMode;
    }

    /**
     * Builds the initial graph, considering scores only between pairs of variables,
     * without dependency to others.
     * It adds a link when score(X|Y) is bigger than score(X), and the weight of
     * the link would be the positive difference
     *
     * @param cases database sampled from the original model
     * @return linklist with the initial graph.
     */
    private LinkList buildGraph(NodeList variables) {
        //System.out.println("Build graph with potential: ");
        //pot.print();
        Vector<Node> vars = (Vector<Node>) variables.getNodes();
        LinkList list = new LinkList();
        int size = vars.size();
        Metrics met;
        
        if(mode==0){
            met = new BDeMetrics(cases, 2);
        }else{
            met = new BDeMetrics(cases, 2);
        }
        
        NodeList listi = new NodeList();
        NodeList listj = new NodeList();

        double score = 0.0;
        for (int i = 0; i < size; i++) {
            listi.insertNode(vars.get(i));
            listj.insertNode(vars.get(i));
            double partialScore = met.score(listi);
            //System.out.println(" hey i " + i+ " is " + listi.toString2());
                

            for (int j = i+1; j < size; j++) {

                listj.insertNode(vars.get(j));
                //System.out.println(" hey j " + j+ " is " + listj.toString2());
                double condScore = met.score(listj);
                score = condScore - partialScore;
                //System.out.println("Score between "+ vars.get(i).getName() + " and "+vars.get(j).getName() +" is: "+ condScore + " less the simple "+ partialScore);
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
        //System.out.println(list);
        return list;
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
    public LinkList buildConditionedGraph(Vector<Node> varToCond, Vector<Node> dataVars) {

        Vector<Node> vars = dataVars;
        vars.removeAll(varToCond);
        LinkList list = new LinkList();
        int size = vars.size();
        double score = 0.0;
        Metrics met;
        
        if(mode==0){
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
                //Aqui meter el resto de variables de varToCond en varsToCond
                for(int k=0; k<varToCond.size(); k++){
                    varsToCond.insertNode(varToCond.get(k));
                    varsToCond2.insertNode(varToCond.get(k));
                }
                double partialScore = met.score(varsToCond);
                score = met.score(varsToCond2) - partialScore;
              //  System.out.println("Conditioned score between "+ vars.get(i).getName() + " and "+vars.get(j).getName() +" is: "+score);
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
      //  System.out.println(list);
        return list;
    }

    /**
     * performs the learing process, to get a Recursive Probability Tree from a DB.
     *
     * @param cases the database to learn the model from
     * @return a TreeNode that would be the root of the learned RPT
     */
    public TreeNode learn(NodeList variables) {
     //   System.out.println("Starting learning process");
        //System.out.println("Cases: "+cases.getNumberOfCases()+" and vars: "+ cases.getVariables().toString2());

        TreeNode recTree = null;

        if(variables.size()> 2){ //if there are two or less vars in the database, don't factorize them, just store as Potential Node

        LinkList graph = this.buildGraph(variables);
        Vector<Set<Node>> clusters = getConnectedComponents(graph, (Vector<Node>) variables.getNodes());
        
        //Vector<Node> vars = (Vector<Node>)potential.getVariables().clone();
        if (clusters.size() > 1) { // grafo desconectado
          //  System.out.println("Disconnected");
            // Need to know how to learn the cases from the database for the clusters
         //   System.out.println("Cases: "+cases.getNumberOfCases()+" and vars: "+ variables.toString2());
            recTree = dealWithDisconnectedGraph(variables,clusters);
        } else { //grafo conectado
            if (clusters.size() == 1 && clusters.get(0).size() <= 2) {
                //APRENDER DISTRIBUCION
                Vector<Node> auxVect = new Vector<Node>();
                auxVect.addAll(clusters.get(0));
                NodeList auxList = new NodeList(auxVect);
             //   System.out.println("Vars in the cluster: "+auxList.toString2());
                //PotentialTree pot = cases.getPotentialTree(auxList);
                //((ListTreeNode) recTree).addChild(new PotentialTreeNode(pot));


                PotentialTree tree = null;
                PotentialTable auxpot;
                if(branch == null){
                    auxpot = cases.getPotentialTable(auxList);
                    auxpot.LPNormalize();
                    tree = new PotentialTree(auxpot);
                }
                else{
                    auxpot = cases.getPotentialTable(auxList,branch);
                    auxpot.LPNormalize();
                    tree = new PotentialTree(auxpot);
//                    System.out.println("Disconnected learn, branch to consider: ");
//                    this.branch.pPrint();
//                    auxpot.print();
//                    System.out.println("************************************");
                }
                recTree = new PotentialTreeNode(tree);
               // System.out.println("Potential node created (empty)");


            }else {
                //Aqui seguimos con la parte estructural
              //  System.out.println("deal with Connected graph");
                //recTree = new ValueTreeNode(0.5);
                recTree = dealWithConnectedGraph(variables, clusters, graph);
            }
        }

        }else{

                PotentialTree tree = null;
                PotentialTable auxpot;
                if(branch == null){
                    auxpot = cases.getPotentialTable(variables);
                    auxpot.LPNormalize();
                    tree = new PotentialTree(auxpot);
                }
                else{
                    auxpot = cases.getPotentialTable(variables,branch);
                    auxpot.LPNormalize();
                    tree = new PotentialTree(auxpot);
//                                        System.out.println("Disconnected, branch to consider: ");
//                    this.branch.pPrint();
//                    auxpot.print();
//                    System.out.println("************************************");
                }
                recTree = new PotentialTreeNode(tree);            
        }
        return recTree;
    }//fin del algoritmo

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

        for (int i = 0; i < clusters.size(); i++) {
            Vector<Node> vars = new Vector<Node>();
            vars.addAll(clusters.get(i));

            Iterator it = clusters.get(i).iterator();
            while(it.hasNext()){
                varsInClusters.insertNode((Node)it.next());
            }


            if (clusters.get(i).size() > 2) {
                //Vector<Node> varsToRemove = (Vector<Node>)variables.getNodes().clone();
                //varsToRemove.removeAll(clusters.get(i));
                NodeList auxrestricted = new NodeList(vars);
                //System.out.println("List of variables: "+auxrestricted.toString2());
                //System.out.println("Cases: "+cases.getNumberOfCases()+" and vars: "+ cases.getVariables().toString2());
                //DataBaseCases restricted = cases.copy();
               // System.out.println("Cases: "+restricted.getNumberOfCases()+" and vars: "+ restricted.getVariables().toString2());
                //restricted.removeVariables(auxrestricted);
                //System.out.println("Working with cluster "+i+"\nCases: "+restricted.getNumberOfCases()+" and vars: "+ restricted.getVariables().toString2());
              //  System.out.println("recursive learning on children");
                ((ListTreeNode) recTree).addChild(learn(auxrestricted));
            }else {

                //APRENDER DISTRIBUCION
               // System.out.println("Create potential Node\nCases: "+cases.getNumberOfCases()+" and vars: "+ cases.getVariables().toString2());
                Vector<Node> auxVect = new Vector<Node>();
                auxVect.addAll(clusters.get(i));
                NodeList auxList = new NodeList(auxVect);
            //    System.out.println("Vars in the cluster: "+auxList.toString2());
                //PotentialTree pot = cases.getPotentialTree(auxList);
                //((ListTreeNode) recTree).addChild(new PotentialTreeNode(pot));

//                Configuration auxconf = new Configuration(auxList);
//                PotentialTree tree = new PotentialTree(auxList);
//                PotentialTable table = cases.getPotentialTable(auxList);
//
//                for(int c=0; c<auxconf.possibleValues(); c++){
//                    double value = table.getValue(auxconf)/cases.getTotalPotential(auxconf);
//                    System.out.println("The value is: "+value+" of "+table.getValue(auxconf)+" and "+cases.getTotalPotential(auxconf));
//                    tree.addValue(auxconf, value);
//                    auxconf.nextConfiguration();
//                }

                PotentialTree tree = null;
                PotentialTable auxpot;

                if(branch == null){
                    auxpot = cases.getPotentialTable(auxList);
                    auxpot.LPNormalize();
                    tree = new PotentialTree(auxpot);
                }
                else{
                    auxpot = cases.getPotentialTable(auxList,branch);
                    auxpot.LPNormalize();
                    tree = new PotentialTree(auxpot);
//                    System.out.println("Disconnected graph, branch to consider: ");
//                    this.branch.pPrint();
//                    auxpot.print();
//                    System.out.println("************************************");
                }

                ((ListTreeNode) recTree).addChild(new PotentialTreeNode(tree));
              //  System.out.println("Potential node created");
            }
         }


        //double sum = 0.0;
        //if(branch == null)
          //  sum = cases.getPotentialTable(varsInClusters).totalPotential();
        //else
          //  sum = cases.getPotentialTable(varsInClusters,branch).totalPotential();


         //RecursivePotentialTree rt = new RecursivePotentialTree();
         //rt.setValues(recTree);
         //PotentialTable pt = rt.getPotentialTable();
         //double t = pt.totalPotential();
        //((ListTreeNode)recTree).addChild(new ValueTreeNode(sum / t));

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
     * tries to decompose a cluster of variables into a factorization, and if it can't find it,
     * tries to find c-s independencies.
     *
     * @param cases database to learn from
     * @param clusters vector of only one element containing the cluster of nodes to analyze
     * @param graph contains the links of the connected component
     * @return a TreeNode that would be the root of the RPT learned for the cluster of variables
     */
    private TreeNode dealWithConnectedGraph(NodeList variables, Vector<Set<Node>> clusters,LinkList graph){
        TreeNode recTree = null;
        Vector<Node> vars = (Vector<Node>)variables.getNodes().clone();
        Vector<Node> condVars = new Vector<Node>();
        Vector<Node> y1 = new Vector<Node>();
        Vector<Node> y2 = new Vector<Node>();
        //boolean independentSplit = false;
        Vector<Node> datavars = (Vector<Node>)variables.getNodes().clone();

        while (clusters.size() == 1 && clusters.get(0).size() > 2) { //mientras siga conectado y haya mas de dos variables

//            System.out.println("Elegir variable");
            Node varToDel = chooseVarByNeightbourhood(graph, vars);

            condVars.add(varToDel);
  //          System.out.println("Var chosen: "+ varToDel.getName() +" Now build the new graph");
            
            datavars.remove(varToDel);


           // System.out.println(condVars);
           // System.out.println(datavars);
            //System.out.println("Cases: "+cases.getNumberOfCases()+" and vars: "+ cases.getVariables().toString2());

            LinkList graphConditioned = this.buildConditionedGraph(condVars, datavars);

            if (this.connectsAll(varToDel, graph, datavars)) {
                y1.add(varToDel);
              //  System.out.println("Variable "+varToDel.getName()+" added to y1");
              //  independentSplit = this.createSplit(potential, (FiniteStates)varToDel);
            } else {
               // System.out.println("Variable "+varToDel.getName()+" added to y2");
                y2.add(varToDel);
            }

            clusters = getConnectedComponents(graphConditioned, datavars);

            if (clusters.size() > 1 || (clusters.size()==1 && clusters.get(0).size()<=2)) { //Si el grafo se desconecta, ramificamos
                if (y1.isEmpty()) {
                   // System.out.println("Desconectado, sin cadena de splits");
                    recTree = dealWithoutSplitChain(variables, clusters, y2);
                } else {
                   // System.out.println("Desconectado, con cadena de splits o dos o menos variables");
                    recTree = dealWithSplitChain(variables, clusters, y1, y2);
                }
            }
            //else{
                //if(independentSplit){
                  //  System.out.println("Desconectado, con split independent: NO IMPLEMENTADO PARA BD");
                   // recTree = dealWithIndependentSplit(potential, y1);
                //}
            //}
            vars.remove(varToDel);

        }// end of while
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

        recTree = elvira.potential.RPT.Util.createSplitChain(y1);

        Configuration conf = new Configuration(y1);

        NodeList varsInY1 = new NodeList(y1);
        NodeList varsInbranch = null;
        Configuration originalBranch = null;

        if(branch == null){
             branch = conf;
             varsInbranch =  new NodeList(y1);
        }else{
            originalBranch = branch.duplicate();
            varsInbranch = new NodeList(branch.getVariables());
            varsInbranch.merge(varsInY1);
            branch = new Configuration(branch, conf,varsInbranch);
        }


        for (int i = 0; i < conf.possibleValues(); i++) {

//        System.out.println("La configuracion de la cadena de Splits");
//        branch.pPrint();
//        System.out.println();

//            Configuration auxConf = conf.duplicate();
//            Potential auxPot = potential.restrictVariable(auxConf);
//            double totPot = auxPot.totalPotential();
            Vector<Node> aux = (Vector<Node>) y2.clone();
            TreeNode tree = null;
            if (clusters.size() > 1) {
                NodeList varsInClusters = new NodeList(y2);
                tree = new ListTreeNode();
                for (int j = 0 ; j < clusters.size() ; j++) {
                    NodeList auxrestricted = new NodeList(y2);

                    Iterator it = clusters.get(j).iterator();
                    while(it.hasNext()){
                        Node node = (Node)it.next();
                        varsInClusters.insertNode(node);
                        auxrestricted.insertNode(node);
                    }

                    aux = (Vector<Node>) y2.clone();
                    aux.addAll(clusters.get(j));
 
                    ((ListTreeNode)tree).addChild(learn(auxrestricted));
                }

//                double sum = 0.0;
//                if(branch == null)
//                    sum = cases.getPotentialTable(varsInClusters).totalPotential();
//                else
//                    sum = cases.getPotentialTable(varsInClusters,branch).totalPotential();
//
//                RecursivePotentialTree rt = new RecursivePotentialTree();
//                rt.setValues(tree);
//                PotentialTable pt = rt.getPotentialTable();
//                double t = pt.totalPotential();
//                ((ListTreeNode)tree).addChild(new ValueTreeNode(sum / t));

            }
            else {
//                // System.out.println("DEAL WITH SPLIT CHAIN ********************************************************************************************************************:        ");
//                Potential operatedPot = auxPot.copy();

                //APRENDER DISTRIBUCION
                aux.addAll(clusters.get(0));
               // Potential pot = new PotentialTree(aux);
                NodeList auxList = new NodeList(aux);

                //Si la split chain
//                System.out.println("La configuracion que restringe la base de datos es: ");
//                conf.pPrint();
                PotentialTable auxpot = cases.getPotentialTable(auxList, conf);
                auxpot.LPNormalize();
                PotentialTree treepot = new PotentialTree(auxpot);
                tree = new PotentialTreeNode(treepot);
            }
            elvira.potential.RPT.Util.updateSplitChainLeaf(recTree, conf.duplicate(), tree);
            conf.nextConfiguration();
             branch = new Configuration(conf,branch,varsInbranch);
        }

        branch = originalBranch;
        return recTree;
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

//        double totPot = potential.totalPotential();
        int size = clusters.size();
        Vector<Node> aux = (Vector<Node>) y2.clone();
        if(size == 1){
            //APRENDER DISTRIBUCION
//          //  System.out.println("DEAL WITHOUT SPLIT CHAIN ********************************************************************************************************************:        ");
                aux.addAll(clusters.get(0));
//                Potential pot = potential.copy();
                NodeList auxList = new NodeList(aux);

                PotentialTree tree = null;
                PotentialTable auxpot;
                if(branch == null){
                    auxpot = cases.getPotentialTable(auxList);
                    auxpot.LPNormalize();
                    tree = new PotentialTree(auxpot);
                }
                else{
                    auxpot = cases.getPotentialTable(auxList, branch);
                    auxpot.LPNormalize();
                    tree = new PotentialTree(auxpot);
//                    System.out.println("Disconnected, without split branch to consider: ");
//                    this.branch.pPrint();
//                    auxpot.print();
//                    System.out.println("************************************");
                }

                recTree = new PotentialTreeNode(tree);
//                 //((PotentialTreeNode)recTree).storeAsTree();
        }
        else{
            recTree = new ListTreeNode();
            NodeList varsInClusters = new NodeList();
            for (int i = 0; i < size; i++) {
                aux.addAll(clusters.get(i));
//                Potential pot = potential.marginalizePotential(aux);
                //This should be recursive!
                Iterator it = clusters.get(i).iterator();
                while(it.hasNext()){
                        varsInClusters.insertNode((Node)it.next());
                }


                NodeList auxrestricted = new NodeList(aux);
                ((ListTreeNode) recTree).addChild(learn(auxrestricted));

                aux = (Vector<Node>) y2.clone();
            }

        }

        return recTree;
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
     * removes from the database all the entries that are not consistent with a
     * given configuration of the variables.
     *
     * @param conf The configuration of variables to restrict the database to
     * @param data the database to be restricted
     * @return the restricted database
     */
    private DataBaseCases restrictDBC(Configuration conf, DataBaseCases data){

        CaseListMem clm = (CaseListMem)data.getCases();
        CaseListMem aux = new CaseListMem(data.getVariables());
        Configuration caseInDB;
        long casesInDB=clm.getNumberOfCases();

        int numcases = 0;
        for(int i=0; i<casesInDB; i++){
            caseInDB=clm.get(i);
            if(caseInDB.isCompatibleWeak(conf)){
                aux.put(caseInDB);
                numcases++;
            }
        }
        DataBaseCases restrictedData = data.copy();
        restrictedData.replaceCases(aux);
        restrictedData.setNumberOfCases(numcases);
        return restrictedData;
    }
 
}


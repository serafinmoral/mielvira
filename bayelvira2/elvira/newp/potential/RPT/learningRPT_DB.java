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
    //private DataBaseCases cases;
    //private RecursivePotentialTree output;


    /**
     * constructs an instance of the learning algorithm.
     *
     * @param eps factor for the mutual information
     * @param delt factor for the gain of information, goes between 0 and 1
     */
    public learningRPT_DB(/**DataBaseCases dat*/){
        //cases = dat;
    }

    /**
     * Builds the initial graph, considering scores only between pairs of variables,
     * without dependency to others.
     * It adds a link when score(X|Y) is bigger than score(X), and the weight of
     * the link would be the positive difference
     *
     * @param db database sampled from the original model
     * @return linklist with the initial graph.
     */
    private LinkList buildGraph(DataBaseCases db) {
        //System.out.println("Build graph with potential: ");
        //pot.print();
        Vector<Node> vars = (Vector<Node>) db.getVariables().getNodes();
        LinkList list = new LinkList();
        int size = vars.size();

        BDeMetrics met = new BDeMetrics(db, 2);

        NodeList listi = new NodeList();
        NodeList listj = new NodeList();

        double score = 0.0;
        for (int i = 0; i < size; i++) {
            listi.insertNode(vars.get(i));
            listj.insertNode(vars.get(i));
            double partialScore = met.score(listi);

            for (int j = i+1; j < size; j++) {

                listj.insertNode(vars.get(j));
               // System.out.println(" hey j " + j+ " is " + listj.toString2());
                double condScore = met.score(listj);
                score = partialScore - condScore;
                System.out.println("Score between "+ vars.get(i).getName() + " and "+vars.get(j).getName() +" is: "+ condScore + " less the simple "+ partialScore);
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
        System.out.println(list);
        return list;
    }

    /**
     * Builds a graph considering the scores between pairs of variables, but conditioning
     * it to a set of variables given as argument. The nodes to be considered are stated
     * in a vector given as argument. It may not contain all the variables of the DB.
     *
     * @param data database sampled from the original model
     * @param varToCond set of variables for conditioning
     * @param dataVars set of nodes for the graph
     * @return
     */
    public LinkList buildConditionedGraph(DataBaseCases data, Vector<Node> varToCond, Vector<Node> dataVars) {

        Vector<Node> vars = dataVars;
        // asc end
        vars.removeAll(varToCond);
        LinkList list = new LinkList();
        int size = vars.size();
        double score = 0.0;
        Metrics met = (Metrics) new BDeMetrics(data, 2);
        NodeList varsToCond = new NodeList();
        NodeList varsToCond2 = new NodeList();
        for (int i = 0; i < size; i++) {
            varsToCond.insertNode(vars.get(i));

            //Aqui meter el resto de variables de varToCond en varsToCond
            for(int k=0; k<varToCond.size(); k++){
                varsToCond.insertNode(varToCond.get(k));
            }

            double partialScore = met.score(varsToCond);
            varsToCond2.insertNode(vars.get(i));
            for (int j = i+1; j < size; j++) {

                varsToCond2.insertNode(vars.get(j));

            //Aqui meter el resto de variables de varToCond en varsToCond2
            
                for(int k=0; k<varToCond.size(); k++){
                    varsToCond2.insertNode(varToCond.get(k));
                }
                
                score = partialScore - met.score(varsToCond2);
                System.out.println("Conditioned score between "+ vars.get(i).getName() + " and "+vars.get(j).getName() +" is: "+score);
                if (score > 0) {
                    Link link = new Link(vars.get(i), vars.get(j));
                    link.setWidth(score);
                    list.insertLink(link);
                }
                varsToCond2.removeNode(vars.get(j));
                
            }
            varsToCond2.removeNode(vars.get(i));
            varsToCond = new NodeList();
        }
        System.out.println(list);
        return list;
    }

    /**
     * performs the learing process, to get a Recursive Probability Tree from a DB.
     *
     * @param data the database to learn the model from
     * @return a TreeNode that would be the root of the learned RPT
     */
    public TreeNode learn(DataBaseCases data) {
        System.out.println("Starting learning process");
        System.out.println("Cases: "+data.getNumberOfCases()+" and vars: "+ data.getVariables().toString2());

        TreeNode recTree = null;

        NodeList variables = data.getVariables();

        if(variables.size()> 2){ //if there are two or less vars in the database, don't factorize them, just store as Potential Node

        LinkList graph = this.buildGraph(data);
        Vector<Set<Node>> clusters = getConnectedComponents(graph, (Vector<Node>) variables.getNodes());
        
        //Vector<Node> vars = (Vector<Node>)potential.getVariables().clone();
        if (clusters.size() > 1) { // grafo desconectado
            System.out.println("Disconnected");
            // Need to know how to learn the data from the database for the clusters
            System.out.println("Cases: "+data.getNumberOfCases()+" and vars: "+ variables.toString2());
            recTree = dealWithDisconnectedGraph(data, clusters);
        } else { //grafo conectado
            if (clusters.size() == 1 && clusters.get(0).size() <= 2) {
                //APRENDER DISTRIBUCION
                Vector<Node> auxVect = new Vector<Node>();
                auxVect.addAll(clusters.get(0));
                NodeList auxList = new NodeList(auxVect);
                System.out.println("Vars in the cluster: "+auxList.toString2());
                //PotentialTree pot = data.getPotentialTree(auxList);
                //((ListTreeNode) recTree).addChild(new PotentialTreeNode(pot));
                
                PotentialTree tree = new PotentialTree(data.getPotentialTable(auxList));
                tree.normalize();
                recTree = new PotentialTreeNode(tree);
                System.out.println("Potential node created (empty)");


            }else {
                //Aqui seguimos con la parte estructural
                System.out.println("deal with Connected graph");
                //recTree = new ValueTreeNode(0.5);
                recTree = dealWithConnectedGraph(data, clusters, graph);
            }
        }

        }else{
            PotentialTree aux = data.getPotentialTree(variables);
            aux.normalize();
            recTree = new PotentialTreeNode(aux);
        }
        return recTree;
    }//fin del algoritmo

    /**
     * builds a multiplicative factorization, by creating the list node and applying
     * the learn(DB) method recursively to the children
     *
     * @param data database to learn from
     * @param clusters vector that contains the different clusters of variables that compose the decomposition
     * @return a TreeNode containing the multiplicative factorization
     */
   private TreeNode dealWithDisconnectedGraph(DataBaseCases data,Vector<Set<Node>> clusters){
        TreeNode recTree = new ListTreeNode();

        for (int i = 0; i < clusters.size(); i++) {
            Vector<Node> vars = new Vector<Node>();
            vars.addAll(clusters.get(i));

            if (clusters.get(i).size() > 2) {
                Vector<Node> varsToRemove = (Vector<Node>)data.getVariables().getNodes().clone();
                varsToRemove.removeAll(clusters.get(i));
                NodeList auxrestricted = new NodeList(varsToRemove);
                //System.out.println("List of variables: "+auxrestricted.toString2());
                //System.out.println("Cases: "+data.getNumberOfCases()+" and vars: "+ data.getVariables().toString2());
                DataBaseCases restricted = data.copy();
               // System.out.println("Cases: "+restricted.getNumberOfCases()+" and vars: "+ restricted.getVariables().toString2());
                restricted.removeVariables(auxrestricted);
                System.out.println("Working with cluster "+i+"\nCases: "+restricted.getNumberOfCases()+" and vars: "+ restricted.getVariables().toString2());
                System.out.println("recursive learning on children");
                ((ListTreeNode) recTree).addChild(learn(restricted));
            }else {

                //APRENDER DISTRIBUCION
                System.out.println("Create potential Node\nCases: "+data.getNumberOfCases()+" and vars: "+ data.getVariables().toString2());
                Vector<Node> auxVect = new Vector<Node>();
                auxVect.addAll(clusters.get(i));
                NodeList auxList = new NodeList(auxVect);
                System.out.println("Vars in the cluster: "+auxList.toString2());
                //PotentialTree pot = data.getPotentialTree(auxList);
                //((ListTreeNode) recTree).addChild(new PotentialTreeNode(pot));

//                Configuration auxconf = new Configuration(auxList);
//                PotentialTree tree = new PotentialTree(auxList);
//                PotentialTable table = data.getPotentialTable(auxList);
//
//                for(int c=0; c<auxconf.possibleValues(); c++){
//                    double value = table.getValue(auxconf)/data.getTotalPotential(auxconf);
//                    System.out.println("The value is: "+value+" of "+table.getValue(auxconf)+" and "+data.getTotalPotential(auxconf));
//                    tree.addValue(auxconf, value);
//                    auxconf.nextConfiguration();
//                }

                PotentialTree tree = new PotentialTree(data.getPotentialTable(auxList));
                tree.normalize();
                ((ListTreeNode) recTree).addChild(new PotentialTreeNode(tree));
                System.out.println("Potential node created (empty)");
            }
         }
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
     * @param data database to learn from
     * @param clusters vector of only one element containing the cluster of nodes to analyze
     * @param graph contains the links of the connected component
     * @return a TreeNode that would be the root of the RPT learned for the cluster of variables
     */
    private TreeNode dealWithConnectedGraph(DataBaseCases data,Vector<Set<Node>> clusters,LinkList graph){
        TreeNode recTree = null;
        Vector<Node> vars = (Vector<Node>)data.getVariables().getNodes().clone();
        Vector<Node> condVars = new Vector<Node>();
        Vector<Node> y1 = new Vector<Node>();
        Vector<Node> y2 = new Vector<Node>();
        boolean independentSplit = false;
        Vector<Node> datavars = (Vector<Node>)data.getVariables().getNodes().clone();

        while (clusters.size() == 1 && clusters.get(0).size() > 2 && !independentSplit) { //mientras siga conectado y haya mas de dos variables

            System.out.println("Elegir variable");
            Node varToDel = chooseVarByNeightbourhood(graph, vars);

            condVars.add(varToDel);
            System.out.println("Var chosen: "+ varToDel.getName() +" Now build the new graph");
            
            datavars.remove(varToDel);


            System.out.println(condVars);
            System.out.println(datavars);
            System.out.println("Cases: "+data.getNumberOfCases()+" and vars: "+ data.getVariables().toString2());

            LinkList graphConditioned = this.buildConditionedGraph(data, condVars, datavars);

            if (this.connectsAll(varToDel, graph, datavars)) {
                y1.add(varToDel);
                System.out.println("Variable "+varToDel.getName()+" added to y1");
              //  independentSplit = this.createSplit(potential, (FiniteStates)varToDel);
            } else {
                System.out.println("Variable "+varToDel.getName()+" added to y2");
                y2.add(varToDel);
            }

            clusters = getConnectedComponents(graphConditioned, datavars);

            if (clusters.size() > 1 || (clusters.size()==1 && clusters.get(0).size()<=2)) { //Si el grafo se desconecta, ramificamos
                if (y1.isEmpty()) {
                    System.out.println("Desconectado, sin cadena de splits");
                    recTree = dealWithoutSplitChain(data, clusters, y2);
                } else {
                    System.out.println("Desconectado, con cadena de splits o dos o menos variables");
                    recTree = dealWithSplitChain(data, clusters, y1, y2);
                }
            }
            else{
                if(independentSplit){
                    System.out.println("Desconectado, con split independent: NO IMPLEMENTADO PARA BD");
                   // recTree = dealWithIndependentSplit(potential, y1);
                }
            }
            vars.remove(varToDel);

        }// end of while
        return recTree;
    }

    /**
     * constructs a Split Chain and applies the learn(DB) method to the leaves of it, restricting
     * the database given as argument.
     *
     * @param data database to learn the model from
     * @param clusters set of clusters of variables containing the decomposition
     * @param y1 Variables to create the split chain
     * @param y2 Variables to be added to each cluster of the decomposition
     * @return a TreeNode that would be the root of the RPT learned
     */
    private TreeNode dealWithSplitChain(DataBaseCases data,Vector<Set<Node>> clusters, Vector<Node> y1, Vector<Node> y2){

        TreeNode recTree = null;

        recTree = elvira.potential.RPT.Util.createSplitChain(y1);

        Configuration conf = new Configuration(y1);
        for (int i = 0; i < conf.possibleValues(); i++) {
//            Configuration auxConf = conf.duplicate();
//            Potential auxPot = potential.restrictVariable(auxConf);
//            double totPot = auxPot.totalPotential();
            Vector<Node> aux = (Vector<Node>) y2.clone();
            TreeNode tree = null;
            if (clusters.size() > 1) {                
                tree = new ListTreeNode();
                for (int j = 0 ; j < clusters.size() ; j++) {
                    aux = (Vector<Node>) y2.clone();
                    aux.addAll(clusters.get(j));
                    //Potential pot = new PotentialTree(aux);
//                    TreeNode childTree = factorize(pot);
                    //This should be recursive!

                    Vector<Node> varsToRemove = (Vector<Node>)data.getVariables().getNodes().clone();
                    varsToRemove.removeAll(aux);
                    NodeList auxrestricted = new NodeList(varsToRemove);
                //System.out.println("List of variables: "+auxrestricted.toString2());
                //System.out.println("Cases: "+data.getNumberOfCases()+" and vars: "+ data.getVariables().toString2());
                    DataBaseCases restricted = data.copy();

                    //************
                    //NO BASTA CON RESTRINGIR LAS VARIABLES, HAY QUE RESTRINGIR A LA CONF!!
                    //************

               // System.out.println("Cases: "+restricted.getNumberOfCases()+" and vars: "+ restricted.getVariables().toString2());
                    restricted.removeVariables(auxrestricted);

                    if(restricted.getVariables().size()>1 && restricted.getNumberOfCases()>0){
                        conf.pPrint();
                    restricted = this.restrictDBC(conf, restricted);
                    }
                    ((ListTreeNode)tree).addChild(learn(restricted));
                }
            }
            else {
//                // System.out.println("DEAL WITH SPLIT CHAIN ********************************************************************************************************************:        ");
//                Potential operatedPot = auxPot.copy();

                //APRENDER DISTRIBUCION
                aux.addAll(clusters.get(0));
               // Potential pot = new PotentialTree(aux);
                NodeList auxList = new NodeList(aux);

//                Configuration auxconf = new Configuration(auxList);
//                PotentialTree treepot = new PotentialTree(auxList);
//                PotentialTable table = data.getPotentialTable(auxList);
//
//                for(int c=0; c<auxconf.possibleValues(); c++){
//                    double value = table.getValue(auxconf)/data.getTotalPotential(auxconf);
//                    System.out.println("The value is: "+value+" of "+table.getValue(auxconf)+" and "+data.getTotalPotential(auxconf));
//
//                    treepot.addValue(auxconf, value);
//                    auxconf.nextConfiguration();
//                }

                //Si la split chain
                System.out.println("La configuracion que restringe la base de datos es: ");
                conf.pPrint();
                PotentialTree treepot = new PotentialTree(data.getPotentialTable(auxList, conf));
                treepot.normalize();
                tree = new PotentialTreeNode(treepot);
            }
            elvira.potential.RPT.Util.updateSplitChainLeaf(recTree, conf.duplicate(), tree);
            conf.nextConfiguration();
        }

        return recTree;
    }

    /**
     * creates a multiplicative factorization, because no c-s independencies were found
     *
     * @param data database to learn the model from
     * @param clusters set of clusters that composes the decomposition
     * @param y2 set of variables to be added to each cluster
     * @return a TreeNode that would be the root of the RPT learned
     */
    private TreeNode dealWithoutSplitChain(DataBaseCases data,Vector<Set<Node>> clusters, Vector<Node> y2){
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
//                Configuration auxconf = new Configuration(auxList);
//                PotentialTree tree = new PotentialTree(auxList);
//                PotentialTable table = data.getPotentialTable(auxList);
//
//                for(int c=0; c<auxconf.possibleValues(); c++){
//                    double value = table.getValue(auxconf)/data.getTotalPotential(auxconf);
//                    System.out.println("The value is: "+value+" of "+table.getValue(auxconf)+" and "+data.getTotalPotential(auxconf));
//
//                    tree.addValue(auxconf, value);
//                    auxconf.nextConfiguration();
//                }

                PotentialTree tree = new PotentialTree(data.getPotentialTable(auxList));
                tree.normalize();
                recTree = new PotentialTreeNode(tree);
//                 //((PotentialTreeNode)recTree).storeAsTree();
        }
        else{
            recTree = new ListTreeNode();
            for (int i = 0; i < size; i++) {
                aux.addAll(clusters.get(i));
//                Potential pot = potential.marginalizePotential(aux);
                //This should be recursive!

                Vector<Node> varsToRemove = (Vector<Node>)data.getVariables().getNodes().clone();
                varsToRemove.removeAll(aux);
                NodeList auxrestricted = new NodeList(varsToRemove);
                //System.out.println("List of variables: "+auxrestricted.toString2());
                //System.out.println("Cases: "+data.getNumberOfCases()+" and vars: "+ data.getVariables().toString2());
                DataBaseCases restricted = data.copy();
               // System.out.println("Cases: "+restricted.getNumberOfCases()+" and vars: "+ restricted.getVariables().toString2());
                restricted.removeVariables(auxrestricted);

                ((ListTreeNode) recTree).addChild(learn(restricted));

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


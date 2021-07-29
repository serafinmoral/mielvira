/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.potential.RPT.learningdb;

import elvira.Bnet;
import elvira.Node;
import elvira.Link;
import elvira.NodeList;
import elvira.LinkList;
import elvira.Relation;
import elvira.database.DataBaseCases;
import elvira.potential.PotentialTable;
import elvira.potential.PotentialTree;
import elvira.potential.RPT.TreeNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author mgomez
 */
public class BuildGraph {

    /**
     * Data member to store the Scorer
     */
    private Scorer scorer;

    /**
     * Data member to store the result: a tree per variable
     */
    private HashMap<Node, TreeForVariable> result;
    /**
     * Database
     */
    private DataBaseCases database;
    /**
     * Resultant bnet
     */
    private Bnet net;
    /**
     * Auxiliar data member for storing the paths between the nodes
     */
    private HashMap<Node, HashMap<Integer, ArrayList<NodeList>>> paths;

    /**
     * Global score value
     */
    private double score;

    /**
     * Class constructor
     */
    public BuildGraph(DataBaseCases cases) {
        database = cases;
        // Create the storer
        scorer=new Scorer(cases);

        // Create result data member
        result = new HashMap<Node, TreeForVariable>();

        // Make a bnet without links
        net = new Bnet(database.getVariables());

        // Initialize paths structure
        paths = new HashMap<Node, HashMap<Integer, ArrayList<NodeList>>>();
//System.out.println("Finalizado constructor de BuildGraph");
    }

    public void learn(){
        double time=System.currentTimeMillis();
        learnTrees();
        System.out.println("Aprendidos los arboles iniciales......");
        time=System.currentTimeMillis()-time;
        System.out.println("                   Tiempo: "+time);
        time=System.currentTimeMillis();
        setLinks();
        System.out.println("Fijados los enlaces tras aprendizaje de arboles......");
        time=System.currentTimeMillis()-time;
        System.out.println("                   Tiempo: "+time);
        time=System.currentTimeMillis();
        
        // Cycles removal stage
        removeCycles();
        System.out.println("Eliminados los ciclos......");
        time=System.currentTimeMillis()-time;
        System.out.println("                   Tiempo: "+time);
        time=System.currentTimeMillis();

        // Greedy state
        greedyStep();
        System.out.println("Fase greedy......");
        time=System.currentTimeMillis()-time;
        System.out.println("                   Tiempo: "+time);
        time=System.currentTimeMillis();

        // Finally, sets the parents to the nodes in order to perform
        // the comparison
        setRelations();
        System.out.println("Fijadas las relaciones......");
        time=System.currentTimeMillis()-time;
        System.out.println("                   Tiempo: "+time);
    }

    /**
     * Returns the learnt net
     * @return
     */
    public Bnet getOutput(){
        return net;
    }

    /**
     * Learn the trees for the variables
     */
    private void learnTrees() {
        TreeForVariable classifierForVar;

        // Make a tree for every variable
        NodeList nodes = database.getVariables();
        for (int i = 0; i < nodes.size(); i++) {
            // Make a classifier
            classifierForVar = new TreeForVariable(scorer, i);

            // Call learn method
            classifierForVar.structuralLearning();

            // Get the result and store it
            result.put(nodes.elementAt(i), classifierForVar);
        }
    }

    /**
     * Sets the relations for the net
     */
    private void setRelations(){
        Relation relationForVar;
        LinkList links;
        Link link;
        Node node;

        // Consider every tree for every node
        NodeList nodes=database.getVariables();
        for(int i=0; i < nodes.size(); i++){
            node=nodes.elementAt(i);
            relationForVar=createRelationForVar(node);

            // Add the relation
            net.addRelation(relationForVar);

            // Add the parents to the node
            links=net.getLinkList();

            for(int j=0; j < links.size(); j++){
                // Get the links related to node
                link=links.elementAt(j);
                if (link.getHead() == node){
                    node.addParent(link);
                }
            }
        }
    }

    private Relation createRelationForVar(Node node){
        TreeForVariable treeForVar=result.get(node);
        NodeList variablesInRelation=new NodeList();

        // Get the variables
        HashSet<Node> variablesInTree=treeForVar.getTree().getVariables();

        // Add node as first variable
        variablesInRelation.insertNode(node);

        // Now insert the rest of variables
        for(Node variableInTree : variablesInTree){
            if (!variableInTree.getName().equals(variableInTree.getName())){
                variablesInRelation.insertNode(variableInTree);
            }
        }

        // Creates the relation
        Relation relation=new Relation();
        relation.setVariables(variablesInRelation);

        // Return relation
        return relation;
    }

    /**
     * Make a graph from the trees
     */
    private void setLinks() {
        HashSet<Node> parentVariables;

        // Consider every variable in order to consider its parents
        for (Node node : result.keySet()) {
            setParents(node);
        }
    }

    /**
     * General method for removing cycles
     */
    private void removeCycles() {
        NodeList nodes = net.getNodeList();
        ArrayList<NodeList> computedPaths;
        NodeList path;
        Node node;
        boolean stop;

        // General loop considering cycle lengths
        for (int i = 2; i < nodes.size(); i++) {
            // Loop for considering every node
            stop=false;
            for (int j = 0; j < nodes.size() && !stop; j++) {
                node = nodes.elementAt(j);

                // Compute the paths for this node and with length i
                getPaths(node, node, new NodeList(), i);

                // Check if there are paths
                if (paths.get(node) != null) {
                    computedPaths = paths.get(node).get(i);
                    if (computedPaths != null && !computedPaths.isEmpty()) {

                        // Get the first path
                        path = computedPaths.get(0);

                        // Remove the cycle
                        removeCycle(path);

                        // Clean the paths: the removal of this path may
                        // invalidate computed paths
                        cleanPaths();

                        // After removing a link it is needed the whole
                        // set of paths
                        stop=true;

                        // Make i be 1 in orden to begin the consideration
                        // of loops
                        i=1;
                    }
                }
            }
        }
    }

    /**
     * Computes global score
     */
    private void computeScore(){
        TreeForVariable tree=null;
        double treeScore=0;
        score=0;

        // Computes the score for all the trees in result
        for(Node node : result.keySet()){
            tree=result.get(node);
            treeScore=tree.getScore();
            score+=treeScore;
        }
    }

    /**
     * Compute the paths between two nodes
     * @param start
     * @param target
     * @param visited
     * @param limitLength
     * NOTE: use paths data member
     */
    private void getPaths(Node start, Node target, NodeList visited, int limitLength) {
        Node child;

        // Check if the target is reached
        if (start == target && visited.size() != 0) {
            if (visited.size() == limitLength) {
                // Target reached, store the path
                storePath(start, visited);
            }
        } else {
            // Get the children for start (and not visited)
            NodeList children = getNonVisitedChildren(start, visited);

            // Consider this nodes one by one
            for (int i = 0; i < children.size(); i++) {
                child = children.elementAt(i);

                // Now make a new recursive call if the limit distance
                // is not reachedB B
                if (visited.size() + 1 <= limitLength) {
                    // Add child to visited
                    visited.insertNode(child);

                    // New recursive call
                    getPaths(child, target, visited, limitLength);

                    // Remove child from visited to allow new
                    // explorations for other nodes
                    visited.removeNode(child);
                }
            }
        }
    }

    /**
     * Method for removing cycles. The cycle is defined between the
     * nodes in nodelist passed as argument
     * @param nodesInCycle
     */
    private void removeCycle(NodeList nodesInCycle) {
        HashMap<Node, TreeForVariable> newTrees = new HashMap<Node, TreeForVariable>();
        HashMap<Node, Node> prevNodes = new HashMap<Node, Node>();
        double diffScore;
        Node nodeX = null, nodeY = null;
        TreeForVariable newTree, oldTree;

        // Compute new trees
        for (int i = 0; i < nodesInCycle.size(); i++) {
            nodeY = nodesInCycle.elementAt(i);
            if (i != 0) {
                nodeX = nodesInCycle.elementAt(i - 1);
            } else {
                nodeX = nodesInCycle.elementAt(nodesInCycle.size() - 1);
            }
            prevNodes.put(nodeY, nodeX);

            // Learn a new tree
            //newTree = new TreeForVariable(scorer, nodeY, nodeX);
            newTree = new TreeForVariable(result.get(nodeY),nodeX);
            newTree.structuralLearning();
            newTrees.put(nodeY, newTree);
        }

        // Now compute the differences in score, keeping the difference
        double minDifference = Double.POSITIVE_INFINITY;
        Node nodeMax = nodesInCycle.elementAt(0);
        for (int i = 0; i < nodesInCycle.size(); i++) {
            nodeX = nodesInCycle.elementAt(i);
            newTree = newTrees.get(nodeX);
            oldTree = result.get(nodeX);
            diffScore = oldTree.getScore() - newTree.getScore();
            if (diffScore < minDifference) {
                minDifference = diffScore;
                nodeMax = nodeX;
            }
        }

        // Now remove the link between nodeMax and the parent in the
        // loop
        removeLink(prevNodes.get(nodeMax), nodeMax);

        // The new tree will be stored as the proper for nodeX
        result.put(nodeMax, newTrees.get(nodeMax));

        // Now reconsider the links in according to the new learnt
        // tree for nodeMax
        reconsiderParents(nodeMax);
    }

    /**
     * Reconsider the parents of node, after learning a new tree
     * @param node
     */
    private void reconsiderParents(Node node){
        LinkList links=net.getLinkList();
        String nodeName=node.getName();

        // Get the tree for node
        TreeForVariable treeForNode=result.get(node);

        // Get the variables
        HashSet<Node> nodesInTree=treeForNode.getTree().getVariables();

        // Consider every node in the tree
        for(Node nodeInTree : nodesInTree){
            // Check if there is a link between nodeInTree and node
            if (node != nodeInTree){
                if (links.getLinks(nodeInTree.getName(), nodeName) == null){
                    links.insertLink(new Link(nodeInTree,node));
                }
            }
        }

        // Moreover, some variables that were considered now must be
        // discarded as parents
        Vector<Node> parentsInNet=links.getParentsInList(node);

        // Check if the parents are in nodeInTree
        for(Node parent : parentsInNet){
            if (!nodesInTree.contains(parent)){
                removeLink(parent,node);
            }
        }
    }

    /**
     * Performs the greedy step testing the links between the variables
     */
    private void greedyStep() {
        LinkList links;
        Link link;
        boolean removed = false;

        while (true) {
            // It is needed to test the modification on every link
            links= net.getLinkList();
            for (int i = 0; i < links.size(); i++) {
                link = links.elementAt(i);
                // Make the corresponding test on this link
                removed = checkRemoval(link);
                // If the links was removed, new consideration
                // over the whole set of links
                if (removed == true) {
                    break;
                }
            }
            
            // If this point is reached and removed is false
            // stop the main loop
            if (removed == false){
                break;
            }
        }
    }

    /**
     * Alternative greedy method based on nodes
     */
    private void greedyNodeStep(){
        HashSet<Node> nodeDescendants;
        TreeForVariable prevTree, newTree;
        double scoreDifference;

        // Consider every node
        NodeList nodes=net.getNodeList();
        Node node;

        for(int i=0; i < nodes.size(); i++){
            node=nodes.elementAt(i);

            // Get the descendants for this node
            nodeDescendants = new HashSet<Node>();
            getDescendants(node,net.getLinkList(),nodeDescendants);

            // Get the previous tree for this node
            prevTree=result.get(node);

            // Compute a new tree considering the descendants
            newTree = new TreeForVariable(scorer, node, nodeDescendants);
            newTree.structuralLearning();
System.out.println(" Arbol previo para nodo.........."+node.getName());
prevTree.print();
System.out.println("\n");
System.out.println(" Nuevo arbol para nodo.........."+node.getName());
newTree.print();
System.out.println("\n");

            // Compute score diference
            scoreDifference=newTree.getScore()-prevTree.getScore();
System.out.println("\n DIFERENCIA: "+scoreDifference);

            // If the difference is positive, make the change
            if (scoreDifference > 0){
                result.put(node, newTree);

                // Update the parents
                reconsiderParents(node);
System.out.println("Enlaces disponibles al final: ");
print();
System.out.println("..............................");
                // If there are changes, begin a new loop
                i=-1;
            }
        }
    }

    /**
     * Gets the value of retrieved
     * @return
     */
    public int getTestsRetrieved(){
        return scorer.getTestsRetrieved();
    }

    /**
     * Gets the value of required
     */
    public int getTestsRequired(){
        return scorer.getTestsRequired();
    }

    /**
     * Gets the value of retrieved
     * @return
     */
    public int getFrequenciesRetrieved(){
        return scorer.getFrequenciesRetrieved();
    }

    /**
     * Gets the value of required
     */
    public int getFrequenciesRequired(){
        return scorer.getFrequenciesRequired();
    }

    /**
     * Check the convenience for removing a link (if this is the
     * case the link is removed)
     * @param link
     * @return result of the operation
     */
    private boolean checkRemoval(Link link) {
        HashMap<Node, TreeForVariable> newTrees = new HashMap<Node, TreeForVariable>();
        TreeForVariable newTreeForHead, prevTreeForHead, newTreeForTail, prevTreeForTail;
        HashSet<Node> headDescendants, tailDescendants;
        HashSet<Node> newTreeVariables;
        Node head, tail;
        double difScore;
        boolean changed = false;

        // Get head and tail nodes
        head = link.getHead();
        tail = link.getTail();

        // Before obtaining the list of descendants, remove the
        // link
        removeLink(tail, head);

        // Get the previous trees for head and tail
        prevTreeForTail = result.get(tail);
        prevTreeForHead = result.get(head);

        // It is needed to get the list of descendants for tail
        tailDescendants = new HashSet<Node>();
        getDescendants(tail,net.getLinkList(),tailDescendants);

        // Now learn a new tree for tail
        newTreeForTail = new TreeForVariable(scorer, tail, tailDescendants);
        //newTreeForTail = new TreeForVariable(result.get(tail), tailDescendants);
        newTreeForTail.structuralLearning();

        // Get the descendants for head
        headDescendants = new HashSet<Node>();
        getDescendants(head,net.getLinkList(),headDescendants);

        // If now head appears as parent for new tail tree, before
        // learning the new tree for head it is needed to include
        // tail as forbidden as well as the set of tail descendants.
        // The same is needed if a descendant of head is a parent
        // of new tree for tail
        headDescendants.add(tail);
        newTreeVariables=newTreeForTail.getTree().getVariables();
        boolean match=nonEmptyIntersection(headDescendants,newTreeVariables);
        if(match || newTreeVariables.contains(head)){
            headDescendants.addAll(tailDescendants);
        }
        
        newTreeForHead = new TreeForVariable(scorer, head, headDescendants);
        //newTreeForHead = new TreeForVariable(result.get(head), headDescendants);
        newTreeForHead.structuralLearning();

        // Compute the score differences
        difScore = (newTreeForTail.getScore() - prevTreeForTail.getScore())
                + (newTreeForHead.getScore() - prevTreeForHead.getScore());

        // The difference must be non negative in order to approve the removal
        if (difScore > 0) {
            // Keep the new trees for tail and head as the new ones
            result.put(head, newTreeForHead);
            result.put(tail, newTreeForTail);

            // Now the net will be changed according to the new set of
            // parents for head an tail
            //setParents(tail);
            //setParents(head);
            reconsiderParents(tail);
            reconsiderParents(head);
            changed = true;
        } else {
            // Add again the link
            net.getLinkList().insertLink(link);
        }
        // return changed
        return changed;
    }

    /**
     * Get the list of descendants given a start node
     * @param node
     * @param links
     * @param descendants
     */
    private void getDescendants(Node node, LinkList links, HashSet<Node> descendants){
        // Get the children of node and insert them into descendants if
        // needed
        Vector<Node> children=net.getLinkList().getChildrenInList(node);
        Node child;
        for(int i=0; i < children.size(); i++){
            child=children.elementAt(i);
            if (!descendants.contains(child)){
                descendants.add(child);

                // Make a new recursive call
                getDescendants(child,links,descendants);
            }
        }
    }

    /**
     * Stores information about a path
     * @param node
     * @param visited
     */
    private void storePath(Node node, NodeList visited) {
        NodeList newPath = new NodeList();
        newPath.insertNode(node);
        newPath.join(visited);

        HashMap<Integer, ArrayList<NodeList>> storeForNode;
        ArrayList<NodeList> storeForDistance;

        // Compute the distance
        int distance = visited.size();

        // Check if there are paths for this node
        storeForNode = paths.get(node);
        if (storeForNode == null) {
            // Creates the structure needed for storing paths
            // for this node
            storeForNode = new HashMap<Integer, ArrayList<NodeList>>();

            // It is needed to create storage for the distance
            storeForDistance = new ArrayList<NodeList>();
        } else {
            // Check if there is space for storing paths with distance
            storeForDistance = storeForNode.get(distance);
            if (storeForDistance == null) {
                storeForDistance = new ArrayList<NodeList>();
            }
        }

        // Now everything must be ok
        storeForDistance.add(newPath);
        storeForNode.put(distance, storeForDistance);
        paths.put(node, storeForNode);
    }

    /**
     * Computes non visited children of a node
     * @param parent
     * @param visited
     * @return
     */
    private NodeList getNonVisitedChildren(Node parent, NodeList visited) {
        NodeList nonVisitedChildren;
        NodeList children;

        // Get the children
        children = new NodeList(net.getLinkList().getChildrenInList(parent));

        // Insert in nonVisitedChildren those nodes in children but not
        // in visited
        nonVisitedChildren = children.difference(visited);

        // return children
        return nonVisitedChildren;
    }

    /**
     * Print the paths previously computedremoveLink
     */
    private void printPaths() {
        System.out.println("Impresion de caminos .................");

        for (Node node : paths.keySet()) {
            System.out.println("Paths for node: " + node.getName());

            // Get the dif. lengths for the cycles
            for (Integer length : paths.get(node).keySet()) {
                System.out.println(" Paths of length: " + length);

                // Get the diferent paths
                ArrayList<NodeList> relatedPaths = paths.get(node).get(length);

                // print the paths
                for (NodeList list : relatedPaths) {
                    for (int i = 0; i < list.size(); i++) {
                        System.out.print("  " + list.elementAt(i).getName());
                    }
                    System.out.println();
                }
            }
        }
    }

    /**
     * Clean previously computed paths
     */
    private void cleanPaths() {
        paths = new HashMap<Node, HashMap<Integer, ArrayList<NodeList>>>();
    }

    /**
     * Removes a link from the net
     * @param tail
     * @param head
     */
    private void removeLink(Node tail, Node head) {
        // remove the link between nodeX and nodeY
        try {
            net.removeLink(tail, head);
        } catch (Exception e) {
            System.out.println("Class BuildGraph, method: removeLink(Node, Node)");
            System.out.println("Fail when deleting link between " + tail.getName() + " and " + head.getName());
            System.exit(0);
        }
    }

    /**
     * Method to change the parents of a given node, after learning a tree
     * @param node
     */
    private void setParents(Node node){
        TreeForVariable tree=result.get(node);
        HashSet<Node> parents;
        Link newLink;

        // Now get the variables in the tree
        parents=tree.getTree().getVariables();

        // Now set the links. First remove node as parent
        parents.remove(node);

        // Now set the links between parentVariables and node
        for (Node parent : parents) {
             newLink = new Link(parent, node);
             net.getLinkList().insertLink(newLink);
        }
    }

    /**
     * Check if any of the nodes in reference appears in second
     * @param reference
     * @param second
     * @return
     */
    private boolean nonEmptyIntersection(HashSet<Node> reference, HashSet<Node> second){
        boolean match=false;

        for(Node node : reference){
            if (second.contains(node)){
                match=true;
                break;
            }
        }
        return match;
    }

    /**
     * Print the result of the learning process
     */
    public void print() {
        Link link;
        TreeForVariable treeForVar;
        TreeNode treeNode = null;

        for (Node node : result.keySet()) {
            System.out.println("---------------- " + node.getName() + " ---------------");
            treeForVar = result.get(node);
            treeNode = treeForVar.getTree();
            treeNode.print(2);
            System.out.println("\n");
            // Print the score
            System.out.println("Score: " + treeForVar.getScore());
            System.out.println("\n\n\n");

        }

        // Print the links
        System.out.println("Print the links.............");
        LinkList links = net.getLinkList();

        for (int i = 0; i < links.size(); i++) {
            link = links.elementAt(i);
            System.out.println(link);
        }
    }
}

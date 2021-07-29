/*  StructuralMTELearning.java  */

package elvira.learning;

import java.io.*;
import java.util.*;

import elvira.*;
import elvira.database.DataBaseCases;
import elvira.learning.*;
import elvira.potential.*;
import elvira.parser.ParseException;


/**
 *
 * Performs structural learning from dataBases with discrete and continuous
 * variables. Conditional distributions of the result net are MTE. It's necessary
 * to exit any continuous variable.
 *
 * @author  avrofe
 * @since 14/10/04
 * @version 1.2
 */



public class StructuralMTELearning {
    
    //dataBase that we use to learn
    DataBaseCases cases;
    //the learnt net
    Bnet output;
    //hash table to store values for families
    Hashtable table;
    
    Hashtable tablePotential;
    
    //number of split
    int numSplit = 3;
    
    //constants to make the tree
    int numPoints = 4 ;
    
    
    
    /**
     * Creates a new instance of StructuralContinuousLearning
     */
    
    public StructuralMTELearning() {
    }
    
    
    /**
     * Creates a new instance of StructuralContinuousLearning
     */
    
    public StructuralMTELearning(DataBaseCases cases) {
        this.cases = cases;
        
        //compute maximum size of the hash table, i.e.
        //maximum number of potentials in the net
        
        int numVars, numCases, tamMax;
        numVars = cases.getVariables().size();
        numCases = cases.getNumberOfCases();
        
        //if n is even
        if ( (numVars % 2) == 0){
            tamMax = numVars;
            for (int j = 2; j < (numVars/2) ; j++){
                tamMax = tamMax + combination(numVars - 1 , j);
            }
            tamMax = tamMax * 2 * numVars;
        }
        else{//if n is odd
            tamMax = numVars;
            
            for (int j = 2; j < (numVars/2) ; j++){
                tamMax = tamMax + combination(numVars - 1 , j);
            }
            tamMax = tamMax * 2;
            tamMax = tamMax + combination(numVars - 1 , numVars / 2);
            tamMax = tamMax * numVars;
        }
        
        //to decrease the time, I don't consider this maximum size
        tamMax = tamMax/2 + 1;
        if (tamMax > 50000)
            tamMax = tamMax/2 + 1;
        table = new Hashtable(tamMax);
    }
    
    
    /**
     * Return combinations of m element over n
     *
     * @param m int
     * @param n int
     * @return combinations of m element over n
     */
    
    public static int combination( int m , int n){
        int combination = 1;
        int nfactorial = 1;
        for (int i = 0; i < n; i++)
            combination = (m - i)*combination;
        for (int i = 0 ; i < n; i++)
            nfactorial = (n-i)*nfactorial;
        return combination/nfactorial;
    }
    
    
    public void learning() {}
    
    
    /**
     *
     * Performs structural learning from dataBases with discrete and continuous
     * variables. It's a hill-climbing algorithm based on a metric.
     *
     */
    
    public void structuralLearning() throws InvalidEditException{
        
        int numVars, numCases, numLinks ;
        double oldLog, oldDim, newLog, newDim, newLog1, newDim1, newLog2, newDim2;
        double oldLog1, oldLog2, oldDim1, oldDim2, oldMax, newMax, newMax1, newMax2;
        double maxQuality, auxQuality, quality, log, dim;
        double maxMax; //el valor maximo de prob cond para el movimiento elegido en cada iteracion
        double[] values = new double[3];
        boolean firstTime;
        String key, maxMovement;
        double maxLikelihood = 0, maxPenal = 0 ;
        
        //links that I can add
        LinkList addLinks = new LinkList();
        //links that I can reverse
        LinkList reverseLinks = new LinkList();
        //Links that I can remove
        LinkList removeLinks = new LinkList();
        Link newLink, auxLink, maxLink, link ;
        
        NodeList vars = cases.getVariables();
        Node head, tail, var, parent;
        
        numVars = cases.getVariables().size();
        numCases = cases.getNumberOfCases();
        
        //to print the quality as sum of the likelihood y the penalisation
        double likelihood = 0, penal = 0;
        
        //begin with a net without link
        Bnet net = new Bnet();
        numLinks = 0;
        NodeList nodes = new NodeList();
        for(int i=0 ; i< vars.size(); i++){
            var = (Node) vars.elementAt(i);
            var.setParents(new LinkList());
            var.setChildren(new LinkList());
            var.setSiblings(new LinkList());
            nodes.insertNode(var);
        }
        net.setNodeList(nodes);
        
        //set the relations of the net
        
        MTELearning l0 = new MTELearning(cases) ;
        ContinuousProbabilityTree tree0;
        NodeList X0;
        Configuration conf0;
        PotentialContinuousPT p0;
        Vector relations0 = new Vector();
        Node y0;
        
        for (int i = 0; i < numVars; i++){
            y0 = net.getNodeList().elementAt(i);
            X0 =  y0.getParentNodes();
            NodeList variables0 = new NodeList();
            variables0.insertNode(y0);
            for (int j = 0; j < X0.size(); j++){
                variables0.insertNode(X0.elementAt(j));
            }
            tree0 = l0.learnConditional(y0 , X0, cases, numSplit, numPoints);
            p0 = new PotentialContinuousPT(variables0, tree0);
            Relation relationy0 = new Relation(y0);
            relationy0.setValues(p0);
            relations0.insertElementAt(relationy0, i);
        }
        
        net.setRelationList(relations0);
        
        //the first time we have to calculate all terms of quality
        
        //calculamos el maximo de las prob cond en todas las variables y todos los casos
        double max = Double.NEGATIVE_INFINITY;
        double maxi;
        log = 0;
        dim = 0;
        for (int i = 0; i < numVars; i++){
            //suma para todas las variables de la
            //suma en todos los casos del log de la prob condicionada de la
            //var i a sus padres en esta red, osea: semiQuality
            var = net.getNodeList().elementAt(i);
            values = new double[3];
            int ki = 1;
            int type = var.getTypeOfVariable();
            if (type!=Node.CONTINUOUS && type!=Node.MIXED){
                //is finite-states
                ki = ((FiniteStates) var).getNumStates()-1;
            }
            else
                ki = 14;
            values[1] = ki * dimension(var);
            PotentialContinuousPT p = new PotentialContinuousPT();
            //esta es la suma en todos los casos de la log likelihood
            values[0] = ((double[])semiQuality( var, p))[0];
            //este es el maximo de las probabilidades cond en todos los casos para esta variable
            maxi = ((double[])semiQuality( var, p))[1];
            values[2] = maxi;
            if (maxi > max ) max = maxi;
            
            key = (new Integer(net.getNodeList().getId(var))).toString();
            key = "s"+key+"/";
            
            table.put(key, values);
            
            log = values[0] + log;
            //Calcule the dimension for each family
            dim = values[1] + dim;
        }
        //our proposed metric takes into account the likelihood but with a penalty
        quality = log - numVars*numCases*((Math.log(max))/(Math.log(2))) - (0.5)*dim*((Math.log(numCases))/(Math.log(2)));
        likelihood = log - numVars*numCases*((Math.log(max))/(Math.log(2)));
        penal = ((0.5)*dim*((Math.log(numCases))/(Math.log(2))));
        
        //System.out.println("el maximo es : "+max+" y la calidad : "+quality);
        
        //maximum in the last iteration
        double maxLastIterat = max;
        
        if (Double.isInfinite(quality)){
            quality =  0 - Double.MAX_VALUE;
            //System.out.println("*************  quality es -Infinito ******************");
        }
        
        double oldQuality = quality;
        double oldLikelihood = likelihood;
        double oldPenal = penal;
        boolean beginning = true;
        Vector relations = relations0;
        
        double maxCurrentIterat;
        
        while ( (oldQuality < quality) || beginning ){
            
            //if (!beginning)
            //System.out.println("\n\n***************  NEXT ITERATION: ********************************************************");
            //else
            //System.out.println("\nQUALITY: "+quality);
            
            //System.out.println("The last quality is "+oldQuality+" and the current one is "+quality);
            
            oldQuality = quality;
            oldLikelihood = likelihood;
            oldPenal = penal;
            
            
            addLinks = new LinkList();
            reverseLinks = new LinkList();
            removeLinks = new LinkList();
            
            //check which arcs can be added
            
            for (int i = 0 ; i < numVars ; i++){
                //test every combination
                for (int j = i+1; j < numVars ; j++){
                    
                    //with this combination, I have two possible arcs: one and the reverted
                    //we have to make sure no one already exits in the net, and that
                    //adding it the net has not directed cicle
                    if ((net.getLink(vars.elementAt(i), vars.elementAt(j))==null)
                    && (net.getLink(vars.elementAt(j), vars.elementAt(i))==null)){
                        //test i -> j
                        Link enlace = new Link(vars.elementAt(i), vars.elementAt(j));
                        auxLink = new Link(vars.elementAt(i), vars.elementAt(j));
                        
                        try{
                            net.createLink(vars.elementAt(i), vars.elementAt(j), true);
                            if (net.isADag()){
                                addLinks.insertLink(auxLink);
                            }
                            net.removeLinkOnly(new Link(vars.elementAt(i), vars.elementAt(j)));
                        }
                        catch (InvalidEditException e3){
                        }
                        
                        //test j -> i
                        auxLink = new Link(vars.elementAt(j), vars.elementAt(i));
                        try{
                            net.createLink(vars.elementAt(j), vars.elementAt(i), true);
                            if (net.isADag()){
                                addLinks.insertLink(auxLink);
                            }
                            net.removeLinkOnly(new Link(vars.elementAt(j), vars.elementAt(i)));
                        }
                        catch (InvalidEditException e2){
                        }
                    }//end if
                }//end j
            }//end i
            
            
            //check which arcs can be removed
            LinkList links = net.getLinkList();
            numLinks = links.size();
            LinkList linksCopy = links.copy();
            for(int i = 0; i < numLinks; i++ ){
                auxLink = new Link();
                auxLink = (Link) links.getLinks().elementAt(i);
                removeLinks.insertLink(auxLink);
            }
            
            Link auxLink2;
            
            ////check which arcs can be reversed
            for (int i = 0; i< numLinks; i++){
                //we have to make sure that reversing this arc, the net has not directed cicle
                auxLink = new Link();
                auxLink = (Link) linksCopy.getLinks().elementAt(i);
                //that is the reversed
                auxLink2 = new Link(auxLink.getHead(), auxLink.getTail());
                //try to reverse it
                net.removeLink(auxLink);
                try{
                    net.createLink(auxLink.getHead(), auxLink.getTail());
                    if (net.isADag()){
                        reverseLinks.insertLink(auxLink);
                    }
                    net.removeLink(auxLink2);
                }
                catch(InvalidEditException e4){
                }
                net.createLink(auxLink.getTail(), auxLink.getHead());
                
            }
            
            net.setLinkList(linksCopy);
            
            //System.out.println("number of links now "+net.getLinkList().size()+" and those are \n"+net.getLinkList()+"\n");
            
            //Recalculate terms for each movement, and the quality for each one
            //Store the movement that maximise the quality
            
            maxMax = 0;
            maxQuality = 0;
            maxLink = new Link();
            maxMovement = "";
            link = new Link();
            firstTime = true;
            
            //movement: add (only change head's values)
            for (int i = 0; i < addLinks.size(); i++){
                link = addLinks.elementAt(i);
                var = link.getHead();
                values = new double[3];
                net.getLinkList().getID(link.getTail().getName(), link.getHead().getName());
                values = new double[3];
                values = getValues(var, net);
                oldLog = values[0];
                oldDim = (int) values[1];
                //try to add
                net.createLink(link.getTail(), link.getHead());
                values = new double[3];
                values = getValues(var, net);
                newLog = values[0];
                newDim = (int) values[1];
                //maximo para la variable var con este movimiento
                newMax = values[2];
                //recalcule quality
                //si el maximo para esta variable con este movimiento es mayor q el maximo
                //q teniamos en la etapa anterior , lo intercambiamos (los maximos de las otras variables
                //no cambian respecto a la etapa anterior)
                if (newMax > maxLastIterat) maxCurrentIterat = newMax;
                else maxCurrentIterat = maxLastIterat;
                auxQuality = quality + newLog - oldLog
                - numVars*numCases*((Math.log(maxCurrentIterat))/(Math.log(2))) + numVars*numCases*((Math.log(maxLastIterat))/(Math.log(2)))
                - (0.5)*(newDim - oldDim)*((Math.log(numCases))/(Math.log(2)));
                
                likelihood = oldLikelihood + newLog - oldLog
                - numVars*numCases*((Math.log(maxCurrentIterat))/(Math.log(2)))
                + numVars*numCases*((Math.log(maxLastIterat))/(Math.log(2)));
                penal = oldPenal + (0.5)*(newDim - oldDim)*((Math.log(numCases))/(Math.log(2)));
                
                net.removeLink(link.getTail(), link.getHead());
                
                if (Double.isInfinite(auxQuality)){
                    auxQuality =  0 - Double.MAX_VALUE;
                    System.out.println("*************  quality es -Infinito ******************");
                }
                
                
                if (firstTime){
                    //first time there is not with which compare
                    maxQuality = auxQuality;
                    maxLink = link;
                    maxMovement = "Add";
                    //I have to store the value of the max for this move, because if it was chosen
                    maxMax = maxCurrentIterat;
                    maxLikelihood = likelihood;
                    maxPenal = penal;
                    firstTime = false;
                }
                else{
                    //compare qualities
                    if (auxQuality > maxQuality){
                        //that is the best move, until this moment, with features:
                        maxQuality = auxQuality;
                        maxLink = link;
                        maxMovement = "Add";
                        //guardo las dos componentes de la calidad
                        maxLikelihood = likelihood;
                        maxPenal= penal;
                        //I have to store the value of the max for this move, because if it was chosen
                        maxMax = maxCurrentIterat;
                    }
                }
                
            }//end add movement
            
            //movement: remove (only change head's values)
            for (int i = 0; i < removeLinks.size(); i++){
                link = removeLinks.elementAt(i);
                values = new double[3];
                values = getValues(link.getHead(), net);
                oldLog = values[0];
                oldDim = values[1];
                //try to remove
                net.removeLink(link.getTail(), link.getHead());
                values = new double[3];
                values = getValues(link.getHead(), net);
                
                newLog = values[0];
                newDim = (int) values[1];
                newMax = values[2];
                //si el maximo para esta variable con este movimiento es mayor q el maximo
                //q teniamos en la etapa anterior , lo intercambiamos (los maximos de las otras variables
                //no cambian respecto a la etapa anterior)
                if (newMax > maxLastIterat) maxCurrentIterat = newMax;
                else maxCurrentIterat = maxLastIterat;
                auxQuality = quality + newLog - oldLog
                - numVars*numCases*((Math.log(maxCurrentIterat))/(Math.log(2))) + numVars*numCases*((Math.log(maxLastIterat))/(Math.log(2)))
                - (0.5)*(newDim - oldDim)*((Math.log(numCases))/(Math.log(2)));
                
                likelihood = oldLikelihood + newLog - oldLog
                - numVars*numCases*((Math.log(maxCurrentIterat))/(Math.log(2))) + numVars*numCases*((Math.log(maxLastIterat))/(Math.log(2)));
                penal = oldPenal + (0.5)*(newDim - oldDim)*((Math.log(numCases))/(Math.log(2)));
                
                if (Double.isInfinite(auxQuality)){
                    auxQuality =  0 - Double.MAX_VALUE;
                    System.out.println("*************  quality es -Infinito ******************");
                }
                
                //undo the movement
                net.createLink(link.getTail(), link.getHead());
                
                //compare qualities
                if (auxQuality > maxQuality){
                    maxQuality = auxQuality;
                    maxLink = link;
                    maxMovement = "Remove";
                    maxLikelihood = likelihood;
                    maxPenal = penal;
                    //I have to store the value of the max for this move, because if it was chosen
                    maxMax = maxCurrentIterat;
                }
            }//end remove movements
            
            //movement: reverse (change values of both terms)
            for (int i = 0; i < reverseLinks.size(); i++){
                link = new Link();
                link = reverseLinks.elementAt(i);
                
                values = new double[3];
                values = getValues(link.getHead(), net);
                oldLog1 = values[0];
                oldDim1 = values[1];
                
                values = new double[3];
                values = getValues(link.getTail(), net);
                oldLog2 = values[0];
                oldDim2 = values[1];
                oldLog = oldLog1 + oldLog2;
                oldDim = oldDim1 + oldDim2;
                
                net.removeLink(link.getTail(), link.getHead());
                net.createLink(link.getHead(),link.getTail());
                values = new double[3];
                values =  getValues(link.getHead(), net);
                newLog1 = values[0];
                newDim1 = (int) values[1];
                newMax1 = values[2];
                
                values = new double[3];
                values = getValues(link.getTail(), net);
                newLog2 = values[0];
                newDim2 = (int) values[1];
                newMax2 = values[2];
                newLog = newLog1 + newLog2;
                newDim = newDim1 + newDim2;
                //considero el mayor de los dos
                if (newMax1 > newMax2) newMax = newMax1;
                else newMax = newMax2;
                
                //si el maximo para alguna de estas variables con este movimiento es mayor q el maximo
                //q teniamos en la etapa anterior  , lo intercambiamos (los maximos de las otras variables
                //no cambian respecto a la etapa anterior)
                if (newMax > maxLastIterat) maxCurrentIterat = newMax;
                else maxCurrentIterat = maxLastIterat;
                auxQuality = quality + newLog - oldLog
                - numVars*numCases*((Math.log(maxCurrentIterat))/(Math.log(2))) + numVars*numCases*((Math.log(maxLastIterat))/(Math.log(2)))
                - (0.5)*(newDim - oldDim)*((Math.log(numCases))/(Math.log(2)));
                
                likelihood = oldLikelihood + newLog - oldLog
                - numVars*numCases*((Math.log(maxCurrentIterat))/(Math.log(2))) + numVars*numCases*((Math.log(maxLastIterat))/(Math.log(2)));
                penal = oldPenal + (0.5)*(newDim - oldDim)*((Math.log(numCases))/(Math.log(2)));
                
                //undo the movement
                net.removeLink(link.getHead(),link.getTail());
                
                net.createLink(link.getTail(),link.getHead());
                
                //compare qualities
                if (auxQuality > maxQuality){
                    maxQuality = auxQuality;
                    maxLink = link;//I store the original link, nor the reversed
                    maxMovement = "Reverse";
                    maxLikelihood = likelihood;
                    maxPenal = penal;
                    //I have to store the value of the max for this move, because if it was chosen
                    maxMax = maxCurrentIterat;
                }
            }//end reverse movement
            
            quality = maxQuality;
            
            //oldQuality no me vale la anterior, ya que no puedo compararla conla actual dado
            //q usa otra constante como maximo para normalizar, lo q hago es normalizar la anterior
            //con la constante actual del maximo
            //si hiciese ese movimiento el maximo seria maxMax
            oldQuality = oldQuality - numVars*numCases*((Math.log(maxMax))/(Math.log(2))) +
            numVars*numCases*((Math.log(maxLastIterat))/(Math.log(2)));
            
            //if the quality increases, make the movement
            if ( quality - oldQuality > 0.00000000001 ) {
                
                //como en este se mejora, para q permita detedctar otro opt local
                //empeoraPoco = false;
                
                //make the movement that maximise the quality
                tail = maxLink.getTail();
                head = maxLink.getHead();
                
                if (maxMovement.compareTo("Add")==0){
                    net.createLink(tail, head);
                }
                if (maxMovement.compareTo("Remove")==0){
                    net.removeLink(tail, head);
                }
                if (maxMovement.compareTo("Reverse")==0){
                    net.removeLink(tail, head);
                    net.createLink(head, tail);
                }
                
                //System.out.print("\nquality that I chose: "+maxQuality+ " corresponding to ");
                //System.out.println(maxMovement+" "+tail.getName()+" -> "+head.getName());
                //System.out.println("likelihood ("+maxLikelihood+") - penal ("+maxPenal+")\n");
                quality = maxQuality;
                likelihood = maxLikelihood;
                penal = maxPenal;
            }
            else{
                //System.out.println("\n\nquality does not increase, I consider the last one");
                //System.out.println("The quality is: "+quality);
                
                //set the relations
                
                MTELearning l = new MTELearning(cases) ;
                ContinuousProbabilityTree tree;
                NodeList X;
                Configuration conf;
                PotentialContinuousPT p;
                relations = new Vector();
                Node y;
                for (int i = 0; i < numVars; i++){
                    y = net.getNodeList().elementAt(i);
                    X =  y.getParentNodes();
                    NodeList variables = new NodeList();
                    variables.insertNode(y);
                    for (int j = 0; j < X.size(); j++){
                        variables.insertNode(X.elementAt(j));
                    }
                    tree = l.learnConditional(y , X, cases, numSplit, numPoints);
                    p = new PotentialContinuousPT(variables, tree);
                    Relation relationy = new Relation(y);
                    relationy.setValues(p);
                    relations.insertElementAt(relationy, i);
                }
                
                net.setRelationList(relations);
                
            }//end else
            
            beginning = false;
            
        }//end of while
        
        output = net;
        
    }//end structuralLearning
    
    
    /**
     * Calculates the sum, in all cases, of the logarithm of the conditional
     * probability, for the current variable. Also, returns the maximum of these probabilities.
     *
     * @param y the objetive variable
     * @return results two doubles: the semiquality and the maximum for the variable y
     *
     */
    
    public double[] semiQuality( Node y, PotentialContinuousPT p ){
        
        double[] result = new double[2];
        double maxi = Double.NEGATIVE_INFINITY;
        double semiQuality = 0;
        double value;
        MTELearning l = new MTELearning(cases) ;
        ContinuousProbabilityTree tree;
        NodeList X = y.getParentNodes();
        Configuration conf;
        NodeList variables = new NodeList();
        variables.insertNode(y);
        for (int i = 0; i < X.size(); i++){
            
            variables.insertNode(X.elementAt(i));
            
        }
        
        CaseList caseList = cases.getCases();
        tree = l.learnConditional(y , X, cases, numSplit, numPoints);
        p = new PotentialContinuousPT(variables, tree);
        
        for (int i = 0; i < cases.getNumberOfCases(); i++){
            conf = caseList.get(i);
            value = p.getValue(conf);
            //maxi es el maximo del valor de la dist cond para todos los casos
            if (value > maxi ) maxi = value;
            
            if ( value < 0 ){
                System.out.println("*************** log de un n. negativo ***************");
            }
            
            if ( value == 0 ){
                System.out.println("*************** log de 0 ***************");
            }
            
            //if it's 0, I assign the smallest double
            if ( value == 0){
                System.out.println("sale un valor 0");
                value = 0 - Double.MAX_VALUE;
            }else
                value = (Math.log(value))/(Math.log(2));
            
            if ((new Double(value)).isNaN()) System.out.println("Un valor tiene log NaN");
            
            semiQuality = semiQuality + value;
        }
        
        if ((new Double(semiQuality)).isInfinite()){
            System.out.println("*************** sale -infinity ********************");
            semiQuality = 0 - Double.MAX_VALUE;
        }
        
        result[0] = semiQuality;
        result[1] = maxi;
        
        if (maxi == Double.NEGATIVE_INFINITY) System.out.println("el maximo sale infinito");
        
        return result;
        
    }//end semiQuality
    
    
    /**
     * Calculates the dimension of the var's family, i.e. var and its parents.
     * It's the product of |Xi| for each member, where |Xi| is the number of
     * states, if it is finite-states, or the number of splits, if it is continuous.
     *
     * @ param var the objective variable
     * @ return dimension the dimension
     */
    
    public int dimension( Node var){
        
        int size, type, dimension;
        Vector nodesSet;
        Node currentNode;
        
        //nodesSet is the set of nodes which dimension I'm going to calculate
        nodesSet = new Vector();
        nodesSet = var.getParentNodes().getNodes();
        //I'm not considering var, only its parents
        size = nodesSet.size();
        dimension = 1;
        
        for (int i = 0; i < size; i++){
            currentNode = (Node) nodesSet.elementAt(i);
            type = currentNode.getTypeOfVariable();
            //we consider the number of values, if var is discrete, and the number
            //of splits, if it's continuous
            if (type!=Node.CONTINUOUS && type!=Node.MIXED){
                //is finite-states
                dimension = dimension * ((FiniteStates) currentNode).getNumStates();
            }
            else{
                dimension = dimension * numSplit;
            }
        }
        //if var has no any parent, return 1
        return dimension;
        
    }//end dimension
    
    
    /**
     * Check if this configuration: the current variable and its parents, has been studied before. 
     * If it has, the dimension, the semiQuality and the maximum of the values of the density functions are stored in the hash table.
     * Else, they have to be calculated. Returns these values.
     *
     * @ param var the objective variable
     * @ param net the current Bnet
     * @ retun values three values: the semiQuality (0), the dimension of var (1) and the maximum (2)
     */
    
    public double[] getValues(Node var, Bnet net){
        String key;
        Node parent;
        double[] values = new double[3];
        int i,k,aux;
        int N = var.getParentNodes().size();
        double maxi;
        
        //I have to sort parents' ID, to don't distinguish, for example, x1|x2,x3 from x1|x3,x2
        
        PotentialContinuousPT pot = new PotentialContinuousPT();
        NodeList parents = new NodeList();
        parents = var.getParentNodes();
        Vector vectorID = new Vector();
        int[] id  = new int [var.getParentNodes().size()];
        
        //vectorID has parents' Id
        for (int j = 0; j < var.getParentNodes().size(); j++){
            parent = parents.elementAt(j);
            id [j] = net.getNodeList().getId(parent);
        }
        
        //sort the vector, using burble method
        for(i=0;i<N-1;i++)
            for(k=0;k<N-i-1;k++)
                if(id[k+1]<id[k]) {
                    aux=id[k+1];
                    id[k+1]=id[k];
                    id[k]=aux;
                }
        
        key = (new Integer(net.getNodeList().getId(var))).toString()+"/";
        for (int j = 0; j < var.getParentNodes().size(); j++){
            key = key+(id[j])+"_";
        }
        key = "s"+key;
        
        int ki = 1;
        if ( table.get(key)==null){
            //if they are not into the table, I have to calculate them
            
            double[] resultSemiquality = new double[2];
            resultSemiquality = ((double[])semiQuality( var, pot));
            values[0] = resultSemiquality[0];
            maxi = resultSemiquality[1];
            //en values[1] meto tb el ki, q es 14 si la var es cont y numStates-1 si es discreta
            int type = var.getTypeOfVariable();
            if (type!=Node.CONTINUOUS && type!=Node.MIXED){
                //is finite-states
                ki = ((FiniteStates) var).getNumStates()-1;
            }
            else
                ki = 14;
            values[1] = ki*dimension(var);
            //el maximo de las prob cond para esa variable
            values[2] = maxi;
            //Save these values with the respective distribution into the hash table
            table.put(key, values);
        }//end if
        else{
            //if they are into the table, recover them
            values = (double[]) table.get(key);
        }
        
        return values;
        
    }//end getValues
    
    
        
    public Bnet getOutput(){
        System.out.println("\nNet has been learnt sucessfuly :");
        System.out.println("Number of Nodes: "+cases.getVariables().size());
        System.out.println("Number of Links: "+output.getLinkList().size());
        return output;
    }
    
    
    /**
     * Computes the log-likelihood of the net over the data; also returns the maximum
     * of the values of the density functions.
     *
     * @param net the Bnet
     * @return results two doubles: the logLikelihood and the maximum for the variable
     */
    
    public double[] logLikelihood(Bnet net){
        
        double[] results = new double[2];
        double logLikelihood = 0;
        double aux;
        int numVars = cases.getVariables().size();
        int numCases = cases.getNumberOfCases();
        double[] values = new double[3];
        Node var;
        //maximo para todas las variables y casos de la prob cond
        double max = Double.NEGATIVE_INFINITY;
        //maximo de dist cond para una variable (para todos los casos)
        double maxi = 0;
        
        for (int i = 0; i < numVars; i++){
            var = cases.getVariables().elementAt(i);
            values = getValues(var, net);
            //esta es la suma en todos los casos del log de la prob condic
            aux = values[0];
            maxi = values[2];
            if(maxi > max) max = maxi;
            logLikelihood = logLikelihood + aux;
        }
                
        //to normalise, remain  n*m*log max
        logLikelihood = logLikelihood - numVars*numCases*((Math.log(max))/(Math.log(2)));
        
        results[0] = logLikelihood;
        results[1] =  max;
        
        return results;
    }
    
    
    /**
     * Calculates the number of coincident links between two Bnets.
     *
     * @param learnt a Bnet
     * @param original a Bnet
     * @return an integer
     */
    
    public int sameArcs(Bnet learnt, Bnet original){
        
        int i ;
        LinkList learnList, originalList, interList;
        Link l;
        
        originalList = original.getLinkList();
        learnList = learnt.getLinkList();
        
        interList = learnList.intersection(originalList);
        
        
        return interList.size();
        
    }
    
    
    /**
     * Calculates the number of reversed links between two Bnets.
     *
     * @param learnt a Bnet
     * @param original a Bnet
     * @return an integer
     */
    
    public int reversedArcs(Bnet learnt, Bnet original){
        
        int i, rev = 0;
        LinkList learnList, originalList;
        Link l, lReversed;
        Node a , b;
        
        originalList = original.getLinkList();
        learnList = learnt.getLinkList();
        
        for(i=0 ; i < learnList.size() ; i++){
            
            l = learnList.elementAt(i);
            a = l.getHead();
            b = l.getTail();
            
            lReversed = new Link(a,b);
            
            if(originalList.indexOf(lReversed)>= 0)
                rev++;
            
        }
        
        return rev;
    }
    
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws IOException, ParseException, elvira.InvalidEditException {
        
        FileInputStream f;
        FileWriter f2;
        DataBaseCases cases;
        Bnet net;
        StructuralMTELearning l;
        
        if(args.length < 2){
            System.out.println("too few arguments: Usage: file.dbc file.elv");
            System.exit(0);
        }
        
        //Learns a network from a database, with this algorithm
        
        f = new FileInputStream(args[0]);
        cases = new DataBaseCases(f);
        f.close();
        
        l = new StructuralMTELearning(cases);
        l.structuralLearning();
        
        f2 = new FileWriter(args[1]);
        net = l.getOutput();
        net.saveBnet(f2);
        f2.close();
        
    }//end main
    
    
}//end StructuralMTELearning class

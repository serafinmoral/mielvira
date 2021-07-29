
/**
 * Learning.java
 * This class implements the Learning Algorithms.
 *
 * Created: Tue May 11 12:09:55 1999
 *
 * @author P. Elvira
 * @version 1.0
 */

import Bnet;

abstract class Learning  {
    
    private Bnet output; // The output of a Learning Algorithm.
    
    /**
     * This method carries out the learning proccess.
     */

    abstract public void learning();

    /** Access methods ***/
    
    public Bnet getOutput(){
	return output;
    }

    public void setOutput(Bnet b){
	output = b;
    }
	
    /**
     * This method compare the output of the learning algorithm 
     * with the true bayes net used as input in the 
     * learning process.
     * @param Bnet. the true bayes net to be compared.
     * @return int[3]. int[0]= the links added in the learning process.
     *                 int[1]= the links deleted in the learning process.
     *                 int[2]= the links wrong oriented in the output net.
     */

    public int[] compareOutput(Bnet b){

	int addel[] = new int[3];
	int i,del,add,change,pos1,pos2;
	NodeList nodes;
	LinkList linkstrue,linksfalse;
	Link link;
	Node nodeT,nodeH;
	
	del=add=change=0;
	nodes = getOutput().getNodeList();
	linkstrue = b.getLinkList();
	linksfalse = getOutput().getLinkList();

	for(i=0 ; i< linkstrue.size(); i++){
	    link = (Link) linkstrue.elementAt(i);
	    nodeT =(Node)link.getTail();
	    nodeH =(Node)link.getHead();
	    pos1 = linksfalse.getID(nodeT.getName(),nodeH.getName());
	    pos2 = linksfalse.getID(nodeH.getName(),nodeT.getName());
	    if((pos1 == -1)&&(pos2 == -1)) del++;
	    if((pos1 == -1)&&(pos2 != -1)) change++;
	}

	for(i=0 ; i< linksfalse.size();i++){
	    link = (Link)linksfalse.elementAt(i);
	    nodeT =(Node)link.getTail();
	    nodeH =(Node)link.getHead();
	    pos1 = linkstrue.getID(nodeT.getName(),nodeH.getName());
	    pos2 = linkstrue.getID(nodeH.getName(),nodeT.getName());
	    if((pos1 == 1)&&(pos2 == -1)) add++;
	}

	addel[0]=add;
	addel[1]=del;
	addel[2]=change;
	return addel;

    }



} // Learning










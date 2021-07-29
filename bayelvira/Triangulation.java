/* Triangulation.java */

import NodeList;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Random;
import java.util.Date;
import Relation;
import RelationList;
import PairTable;
import java.io.*;

/**
 * Class Triangulation
 * Implements the variable elimination method of triangulation.
 *
 * @since 14/9/99
 */

public class Triangulation extends Propagation {
    
   /**
    * Contains the relations of the triangulated net.
    */
   private RelationList triangulatedRelations;
   
   /**
    * Contains the triangulation secuence.
    */
   private NodeList triangulatedNodes;  
   
   /**
    * Contains the net to Triangulate.
    */
   private Bnet netToTriangulate;
   
   
   public static void main(String args[]) throws ParseException, IOException {

      Bnet b;
      Evidence e;
      FileInputStream networkFile, evidenceFile;
      Triangulation ve;
      NodeList numeration = new NodeList();
      RelationList tmp = new RelationList();
      int i;
  
      networkFile = new FileInputStream("ejemplo.elv");
      b = new Bnet(networkFile);     
    
      e = new Evidence();  
      ve = new Triangulation(b);
      ve.getTriangulation();  
      numeration = ve.maximaCardinalidad();
      tmp = ve.numerateCliques (numeration);
      tmp = ve.getSeparators (tmp);
   }

   /**
    * Creates an empty Triangulation object.
    */

   public Triangulation (){
      triangulatedRelations = new RelationList();
      triangulatedNodes = new NodeList();
      netToTriangulate = new Bnet();      
   }

   
   /**
    * Constructor 2. Create a Triangulation object using the Bnet passed as parameter
    * 
    * @param b Bnet that will cotain the object created.
    */
   
   public Triangulation (Bnet b){
      triangulatedRelations = new RelationList();
      triangulatedNodes = new NodeList();
      netToTriangulate = new Bnet();
      netToTriangulate = b;         		
   }

   /**
     Stores in CurrentRelations the initial relations present
     in the network.
     @param Net Contains the net where we obtain the initial relations
     @return A list of the relations of the Net
    */

   public RelationList getInitialRelations(Bnet bayesNet) {
  
      Relation r, newRelation;
      RelationList initialRelations;
      int i;
 
      initialRelations = new RelationList();
  
      for (i=0 ; i<bayesNet.getRelationList().size() ; i++) {
         r = (Relation)bayesNet.getRelationList().elementAt(i);
         newRelation = new Relation();
         newRelation.setVariables(r.getVariables().copy());            
         initialRelations.insertRelation(newRelation);
      }
  
      return initialRelations;
   }





   /**
     Gets the triangulation and the remove secuence of the net that
     it is store in netToTriangulate    
   */

   public void getTriangulation () {
     NodeList nl=new NodeList();
     getTriangulation(nl);
   }


   /**
    * Gets the triangulation and the remove secuence of the net that
    * it is store in netToTriangulate. If set is not empty, get a constrained
    * deletion sequence with the nodes of set at the end of the sequence.
    *
    * @param set a list of nodes (could be empty)
    */

   public void getTriangulation (NodeList set) {
 
      NodeList notRemoved;
      FiniteStates x;
      Relation r, newRelation, newRelationClone, tmp;
      RelationList currentRelations, auxRelationList;  
      PairTable pairTable;
      int i, j, k, p, pos, s;
      boolean getOut;


      notRemoved = new NodeList();
      pairTable = new PairTable();
  
      s = netToTriangulate.getNodeList().size();
      
      for (i=0 ; i<s ; i++) {
         x = (FiniteStates)netToTriangulate.getNodeList().elementAt(i);         
         notRemoved.insertNode(x);
         pairTable.addElement(x);         
      }

      currentRelations = getInitialRelations(netToTriangulate); 
  
      for (i=0 ; i<currentRelations.size() ; i++)
         pairTable.addRelation(currentRelations.elementAt(i));

  
      for (i=notRemoved.size() ; i>0 ; i--) {
         /* DEBUG
         NodePairTable n;
         System.out.println("Valores para los nodos:");
         for (j=0; j<pairTable.size();j++){
           n = pairTable.elementAt(j);
           System.out.print("[" + n.getVariable().getName() + "," + 
               (int) n.totalSize() + "] ");
         }
          END DEBUG */
         if (set.size()>0) x = pairTable.nextToRemove(set);
         else x = pairTable.nextToRemove();
         /* DEBUG
         System.out.println(x.getName());
         try{
           System.in.read();
         }catch (IOException e) {
           System.out.println("error en la lectura .....");
         }
         */ // END DEBUG  
  
         auxRelationList = currentRelations.getRelationsOfAndRemove(x);
         notRemoved.removeNode(x);
         pairTable.removeVariable(x);
         
         newRelation = new Relation();
         newRelation = auxRelationList.elementAt(0);                   
         pairTable.removeRelation(newRelation);
                  
         for (j=1 ; j<auxRelationList.size() ; j++) {
             r = auxRelationList.elementAt(j);
             combine (newRelation, r);
             pairTable.removeRelation(r);      
         }
     
         this.triangulatedNodes.insertNode(x);              
         this.triangulatedRelations.insertRelation(newRelation);
         
         
         /* Check that the relation inserted is not contained in another one and
            that the relation inserted is not contained in another one too. */
         
         j=0;
         getOut = false;      
         
         while ((j<this.triangulatedRelations.size()-1) && (!getOut)){            
            tmp = this.triangulatedRelations.elementAt(j);
            
            if (tmp.getVariables().size() > newRelation.getVariables().size()) {           
               if (tmp.isContained(newRelation)) {
                  this.triangulatedRelations.removeRelation(newRelation);
                  getOut = true;
               }
            }
            else
               if (tmp.getVariables().size() < newRelation.getVariables().size()) {           
                  if (newRelation.isContained(this.triangulatedRelations.elementAt(j))) {
                     this.triangulatedRelations.removeRelation(tmp);
                     getOut = true;
                  }
               }
            j++;
         }
                     
            
         /* Copy the object newRelation because we need remove a 
         element of the Relation. Any modification in newRelation will
         modify the Relation introduced in triangulatedRelations */
            
         newRelationClone = new Relation();
         newRelationClone = newRelation.copy();
         newRelationClone.getVariables().removeNode(x);
         
         currentRelations.getRelations().addElement(newRelationClone);
         pairTable.addRelation(newRelationClone);
         
      }        
  
   }

   
   /**
    * Obtains a list of the cliques in the network.
    * @return a RelationList corresponding to the cliques.
    */
   
   public RelationList getCliques() {
     NodeList notRemoved;
     FiniteStates x;
     RelationList currentRelations, cliques;  
     PairTable pairTable;
     int i,s;
     
     cliques = new RelationList();

     notRemoved = new NodeList();
     pairTable = new PairTable();
  
     s = netToTriangulate.getNodeList().size();
      
     for (i=0 ; i<s ; i++) {
       x = (FiniteStates)netToTriangulate.getNodeList().elementAt(i);         
       notRemoved.insertNode(x);
       pairTable.addElement(x);         
     }

     currentRelations = getInitialRelations(netToTriangulate); 
     
     for (i=0 ; i<currentRelations.size() ; i++)
       pairTable.addRelation(currentRelations.elementAt(i));

     for (i=notRemoved.size() ; i>0 ; i--) {
       x = pairTable.nextToRemove();
       removeNode(x,notRemoved,currentRelations,cliques,pairTable);  
     }
     
     return cliques;
   }

    /**
    * Obtains a list of the cliques in the network adapted to
    * the initial evidence of the network.
    * @param ev is the initial evidence.
    * @return a RelationList corresponding to the cliques.
    */
   
   public RelationList getCliques(Evidence ev) {
     NodeList notRemoved;
     FiniteStates x;
     RelationList currentRelations, cliques;  
     PairTable pairTable;
     int i,s;
     boolean introInPT;
     RelationList auxRelationList;
     Relation newRelation,newRelationClone,r;
     int j;
     
     cliques = new RelationList();

     notRemoved = new NodeList();
     pairTable = new PairTable();
  
     s = netToTriangulate.getNodeList().size();
      
     for (i=0 ; i<s ; i++) {
       x = (FiniteStates)netToTriangulate.getNodeList().elementAt(i);         
       notRemoved.insertNode(x);
       pairTable.addElement(x);         
     }

     currentRelations = getInitialRelations(netToTriangulate);
     //BORRADO DE OBSERVACIONES
     // Hacer inicialmente todas las relaciones activas(condicionales) (ya lo son por defecto)     
     // En aquellas relaciones en que variable observada (la que borramos) sea la de
     // condicionamiento(primera),  la relacion resultante sera inactiva.     
     // En las relaciones condicionales (activas) en las que borramos otra
     // distinta a la primera, el resultado es activo.
     // Todas estas relaciones se meteran en la PairTable
     currentRelations.restrictToObservations(ev);
     //currentRelations.print(); 
     
     // INICIALIZACION DE LA PAIRTABLE
     for (i=0 ; i<currentRelations.size() ; i++)
       pairTable.addRelation(currentRelations.elementAt(i));  
          
     //BORRADO DE VARIABLES
     // Cuando en una relacion activa borramos primera variable, y esta variable
     // solo esta en esta relacion, el resultado sera inactivo, y no se
     // metera en la PairTable. 
     
     for (i=notRemoved.size() ; i>0 ; i--) {
       x = pairTable.nextToRemove();
       auxRelationList = currentRelations.getRelationsOfAndRemove(x);
       notRemoved.removeNode(x);
       pairTable.removeVariable(x);
      // System.out.println("Variable a borrar");
       //x.print();
       //System.out.println("Tamano de auxRelationList"+auxRelationList.size());
       //auxRelationList.print();
       
       if(auxRelationList.size()>0) {
	 newRelation = auxRelationList.elementAt(0); 
	 
	 if(auxRelationList.size()>1) {
	   newRelation.setActive(false);
	   introInPT=true;
	 }
	 else if(auxRelationList.elementAt(0).getActive() & 
		 auxRelationList.elementAt(0).getVariables().getId(x)==0) {
	   newRelation.setActive(false);
	   introInPT=false;
	 }
	 else {
	   newRelation.setActive(true);
	   introInPT=true;
	 }
	 
	 pairTable.removeRelation(newRelation);
	 
	 for (j=1 ; j<auxRelationList.size() ; j++) {
	   r = auxRelationList.elementAt(j);
	   combine (newRelation, r);
	   pairTable.removeRelation(r);      
	 }
	 
	 cliques.insertRelation(newRelation);
	 
	 /* Copy the object newRelation because we need remove a 
	    element of the Relation. Any modification in newRelation will
	    modify the Relation introduced in cliques */
	 
	 newRelationClone = new Relation();
	 newRelationClone = newRelation.copy();
	 newRelationClone.getVariables().removeNode(x);
	 
	 currentRelations.getRelations().addElement(newRelationClone);
	 
	 if(introInPT)
	   pairTable.addRelation(newRelationClone);   
       }
     }
     
     return cliques;
   }


   /** Get all the variables of both relations in one of them 
       @param Rmain Cotains one of the relations. It will contain the 
                    relation with the final combination
              r Contains the other relation to combine */
   
   public void combine (Relation rMain, Relation r) {
      
      int i;      
      
      for (i=0;i<r.getVariables().size();i++) {
         if (!rMain.isInRelation(r.getVariables().elementAt(i))) 
            rMain.getVariables().insertNode(r.getVariables().elementAt(i));
      }
   }         
    
   /** Carry out the operations need to delete a node when we
     * are getting the cliques from a Bnet. This method is used
     * by getCliques().
     * @see getCliques()
     * @param x is the node to be deleted
     * @param notRemoved is the list of non removed nodes. After this
     * method the node x will be deleted of this list.
     * @param currentRelations is the list of current relations that 
     * contains only non deleted nodes. After this method the 
     * relations containing node x will be sustituited by a relation
     * with all variables of such relations.
     * @pairTable allow recover the relations in which is every non 
     * deleted node.
     */
   
   private void removeNode(FiniteStates x,NodeList notRemoved,
	RelationList currentRelations,RelationList cliques,
	PairTable pairTable) {
     int j;
     
     RelationList auxRelationList;
     Relation newRelation,newRelationClone,r;
     
     auxRelationList = currentRelations.getRelationsOfAndRemove(x);
     notRemoved.removeNode(x);
     pairTable.removeVariable(x);
     
     newRelation = auxRelationList.elementAt(0);                   
     pairTable.removeRelation(newRelation);
     
     for (j=1 ; j<auxRelationList.size() ; j++) {
       r = auxRelationList.elementAt(j);
       combine (newRelation, r);
       pairTable.removeRelation(r);      
     }
     
     cliques.insertRelation(newRelation);
     
     /* Copy the object newRelation because we need remove a 
	element of the Relation. Any modification in newRelation will
	modify the Relation introduced in cliques */
     
     newRelationClone = new Relation();
     newRelationClone = newRelation.copy();
     newRelationClone.getVariables().removeNode(x);
     
     currentRelations.getRelations().addElement(newRelationClone);
     pairTable.addRelation(newRelationClone);   
   }
   
   public void restrictToObservations(RelationList relationList) {

      Relation r;
      int i, s;
      
      if(observations.size()>0){
	s = relationList.size();
	
	for (i=0 ; i<s ; i++) {
	  r = relationList.elementAt(i);
	  r.setValues (((PotentialTree)r.getValues()).restrictVariable(observations));
	  r.getVariables().nodes = r.getValues().getVariables();
	}
      }
   }
      
   
    /** Obtain the numeration for the list of nodes contained in the variable
        triangulatedNodes of the class.        
        @return The list of ordered nodes
        @see Triangulation#maximumNeighbours */
   

   public NodeList maximaCardinalidad () {

      NodeList visitados,  /* Contiene la lista de nodos que han sido visitados */
               vecinos,    /* Contendrß la lista de nodo adyacentes a uno dado */
               tmp;        /* Contiene una copia de la variable orden, para no perder
                           los datos que hay en ella */

      int i, j, numberOfNodes;
      int k=0;      
   
      numberOfNodes = this.triangulatedNodes.size();  
      tmp = new NodeList();
      tmp.nodes = (Vector) this.triangulatedNodes.nodes.clone();      
      
      visitados = new NodeList();
      visitados.insertNode (tmp.elementAt(0));
      tmp.removeNode(tmp.elementAt(0));

      for (i=1; i<numberOfNodes; i++) {
        visitados.insertNode (this.maximumNeighbours (tmp,visitados));                    
      }        
      
      return visitados;

   }

    
    


    /** Obtain the numeration for the list of nodes contained in the variable
     *  triangulatedNodes of the class. The following restrictions are
     *  applied:
     *     - the first node is a node belonging to set
     *     - if two nodes are tied, we break the tie in favour of the node
     *       belongin to set (if possible)
     *  @param set a node list containing a set of relevants nodes,
     *  @return The list of ordered nodes
     */
   

   public NodeList maximumCardinalitySearch (NodeList set) {

      NodeList visited,    // the list to be returned  
               neighbours; // the neighbours for a node
      Node node;        
      int i, j,k, numberOfNodes,next;
      int numNeighbours[]; // contains in position i the number of 
                           // numbered neighbours for the node
                           // triangulatedNodes.elementAt(i)
                           // if the value is -1 the node not will be 
                           // considered
   
      numberOfNodes = this.triangulatedNodes.size();  
      numNeighbours = new int[numberOfNodes];
      for (i=0; i<numberOfNodes; i++) 
        numNeighbours[i]=0;
        
      visited = new NodeList();
            
      // we choose a node beloging to set as the first node. We begin by
      // the end of triangulated nodes because if the triangulation has been
      // obtained respecto to set, then the last node of triangulatednodes 
      // will be a node of set

      next = 0; // initialization of next
      if (set.size()>0){
        for(i=numberOfNodes-1; i>=0; i--){
          node = this.triangulatedNodes.elementAt(i);
          if (set.getId(node) != -1){
            next=i;
            break;
          }
        }
      }

      // begining the numeration process

      for (i=0; i<numberOfNodes; i++) {
        // inserting the node and setting its value to -1
        visited.insertNode(triangulatedNodes.elementAt(next));
        numNeighbours[next]=-1;
        // getting a nodelist with all the nodes that appear with next in 
        // some relation
        neighbours = this.neighbourhood(next);
        // for all node in neighbours do numberofneigbours++
        for(j=0;j<neighbours.size();j++){
          node = neighbours.elementAt(j); 
          k = triangulatedNodes.getId(node);
          if (k!=-1){
            if (numNeighbours[k] != -1) numNeighbours[k]++;
          }
          else { System.out.println("We have missed a node!!!!");}
        }

        // getting the position of the next node to be numbered
        next = 0;
        for(j=1; j<numberOfNodes; j++){
          if (numNeighbours[j]>numNeighbours[next]) next = j;
          else if (set.size()>0){
            if (numNeighbours[j]==numNeighbours[next])
              if (set.getId(this.triangulatedNodes.elementAt(j))!=-1)
                next = j;
          }
        }
          
      }        
      
      return visited;

   }


    /** Obtain the numeration for the list of nodes contained in the variable
     *  triangulatedNodes of the class. Call the previous method with an
     *  empty node list.
     *        
     *  @return The list of ordered nodes
     */
   

    public NodeList maximumCardinalitySearch () {
      NodeList nl=new NodeList();

      return maximumCardinalitySearch(nl);
    }


   /** This method returns the node with most neighbours visited previously       
       @param orden Contains the list of the nodes not visited yet
       @param Visitados Contains the list of the nodes already visited
       @return The node with the highest number of neighbours
       @see Triangulation#neighbourhood
      */

   public Node maximumNeighbours (NodeList orden, NodeList visitados)  {
   
      int vecino=0, mayor=0, indice=0, i, j, k;
      NodeList vecinos = new NodeList();
      Node elemento;
   
   /* Looking for the node with most neighbours numerated */

      for (i=0;i<orden.size();i++) {
         vecinos = neighbourhood (orden.elementAt(i));
         vecino = 0;
         for (k=0; k<vecinos.size(); k++) {
            if (visitados.getId(vecinos.elementAt(k).getName())!=-1) 
               vecino++;
         
         }
   
         /* Find a node with a higher number of neighbours visited */
         
         if (vecino > mayor) {
            indice=i;
            mayor=vecino;
         }
      }
      
      elemento=orden.elementAt(indice);
      orden.removeNode(indice);
      
      return elemento;
   }
   
   
   /** This method returns the list of the neighbours of a node 
       @param N The node whose neighbours are looking for 
       @return A list of neighbours */       

   public NodeList neighbourhood (Node n) {
      int numberOfNodes, i, j;
      NodeList neighbours = new NodeList();
      int p, q;
      Relation r = new Relation();
            
      numberOfNodes = this.triangulatedRelations.size();
      
      for (i=0; i<numberOfNodes; i++) {
         r = (this.triangulatedRelations.elementAt(i)).copy();
         p = r.getVariables().getId(n);
         
         if (p!=-1) {                 
            r.getVariables().removeNode(n);
            
            for (j=0; j<r.getVariables().size(); j++) {
               q = neighbours.getId(r.getVariables().elementAt(j));
               if (q==-1)
                  neighbours.insertNode(r.getVariables().elementAt(j));
            }
            
         }
         
      }
      
      return neighbours;
      
   }
   /** This method returns the list of neighbours for the node
    *  whose index in triangulatedNodes is pased as parameter.
    *  @param pos The position of the node whose neighbours are looking for 
    *  @return the list of neighbours 
    */       

   public NodeList neighbourhood (int pos) {
     
     NodeList neighbours = new NodeList();
     int i,numberOfNodes;
     Node node;
     Relation r;
     
     numberOfNodes = this.triangulatedRelations.size();
     node = this.triangulatedNodes.elementAt(pos);
      
     for (i=0; i<numberOfNodes; i++) {
       r = this.triangulatedRelations.elementAt(i);
       if (r.getVariables().getId(node) != -1){
         neighbours.join(r.getVariables());
       }
     }
     
     return neighbours;
   }


   /**
    * @return true if the relation A is lower than relation B, respect
    *  to the order stablished by NodeOrder, from position pos
    * @param r1 the first relation
    * @param r2 the second relation
    * @param nodeOrder the list of nodes 
    * @param pos the position from which nodeOrder is considered
    */

   public boolean lower(Relation r1, Relation r2, NodeList nodeOrder, int pos){

     int i,p1,p2;
     Node node;

     for(i=pos+1;i<nodeOrder.size();i++){
       node = nodeOrder.elementAt(i);
       p1 = r1.getVariables().getId(node.getName());
       p2 = r2.getVariables().getId(node.getName());
       
       if ((p1!=-1) && (p2==-1)) return true;
       if ((p1==-1) && (p2!=-1)) return false;
     }

     System.out.println("Triangulation.lower: error ...");
     return false; 
   }
   
   
   /** This method numerates the cliques according with the node of less order
       that contains. 
       @param nodeOrder Contains the list of nodes in order.
       @return A list of cliques (relations) in order according the numeration
               obtained */
   
   public RelationList numerateCliques (NodeList nodeOrder) {
      RelationList numeration, relationCopy, tmp;
      FiniteStates x;
      int i,j,k;
      
      relationCopy = new RelationList();
      tmp = new RelationList();
      numeration = new RelationList();
      x = new FiniteStates();
      Relation aux;      

      relationCopy.setRelations ((Vector)
                         this.triangulatedRelations.getRelations().clone());
      
      for (i=0; i<nodeOrder.size(); i++) {
         x = (FiniteStates) nodeOrder.elementAt(i);
         tmp = relationCopy.getRelationsOfAndRemove(x);
         
         // if tmp contains more than one relation, the list 
         // have to be sorted considering the other nodes
 
         if (tmp.size() > 1) { // sorting by selection
        
           for(j=0; j < tmp.size()-1; j++){
             int posLower;
             Relation relLower;

             posLower = j;
             relLower = tmp.elementAt(j);

             for(k=j+1; k<tmp.size(); k++)
               if (lower(tmp.elementAt(k),relLower,nodeOrder,i)){
                 posLower = k;
                 relLower = tmp.elementAt(k);
               }

             tmp.setElementAt(tmp.elementAt(j),posLower);
             tmp.setElementAt(relLower,j);
           }

         }
        
         // introducing the relations in numeration
         for (j=0; j<tmp.size(); j++) 
           numeration.insertRelation(tmp.elementAt(j));         
      }
      
      return numeration;
   }
   
   
   /** Obtains the separators of a list of order cliques (relations).
       @param orderedCliques A list of cliques order according its numeration
       @return A list of Separators 
       @see Triangulation#union
       @see Triangulation#intersection*/
   
   public RelationList getSeparators (RelationList orderedCliques) {
      
      int i;
      RelationList separators = new RelationList();
      Relation r = new Relation();
      
      separators.insertRelation (new Relation());
      r = orderedCliques.elementAt(0).copy();
      
      for (i=1; i<orderedCliques.size(); i++) {
         union (r, orderedCliques.elementAt(i-1));      
         separators.insertRelation (intersection (r, orderedCliques.elementAt(i)));
      }
      
      return separators;
      
   }   
   
   
   /** This method obtains the union between two relations. 
       @param r1 The first relation. When the method finished contains the union
                 with the second relation.
       @param r2 The second relation */
   
   public void union (Relation r1, Relation r2) {
      int j, q;
      for (j=0; j<r2.getVariables().size(); j++) {
         q = r1.getVariables().getId(r2.getVariables().elementAt(j));
         if (q==-1)
            r1.getVariables().insertNode(r2.getVariables().elementAt(j));
      }      
         
   }

   
   /** Calculates the intersection between two relations
       @param r1 The first relation
       @param r2 The other relation
       @return The relation list with the intersection
    */
   
   public Relation intersection (Relation r1, Relation r2) {

      Relation intersection = new Relation();
      int i=0, j, tamanol1, tamanol2;
      boolean salir;
    
      tamanol1=r1.getVariables().size();
      tamanol2=r2.getVariables().size();
    
      /* The first while controles the nodes of the first relation */

      while (i<tamanol1){
         salir=false;
         j=0;
        
         /* Check if the actual node of the first list is in the second
            with the next while */
           
         while ((!salir) && (j<tamanol2)){
            if (r1.getVariables().elementAt(i).getName().equals(r2.getVariables().elementAt(j).getName())){
                intersection.getVariables().insertNode(r1.getVariables().elementAt(i));
                salir=true;
            }
            j++;
         }
         i++;    
      }
      return intersection;
   }

         
       

}  // end of class

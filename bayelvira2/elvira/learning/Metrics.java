/* Metrics.java */

package elvira.learning;

import java.util.Hashtable;
import elvira.database.DataBaseCases;
import elvira.Bnet;
import elvira.Node;
import elvira.NodeList;
import java.io.Serializable;
import elvira.ConditionalIndependence;
/**
 * Metrics.java
 *
 * This class implements the abstract class of the all possible metrics that 
 * score a Bayes Net or a list of nodes from data.
 *
 * Created: Thu Nov  4 18:22:31 1999
 *
 * @author P. Elvira
 * @version 1.0
 */

public abstract class Metrics  implements Serializable, ConditionalIndependence{
    
    private DataBaseCases data;    // The data
    private Hashtable cscores = new Hashtable();
    public  double tStEval = 0.0;  
    public  double totalSt = 0.0;  
    public  double totalTime = 0.0;
    public  double timeStEval = 0.0;
    public  double avStNVar = 0.0;


    /**
     * This method scores a Bayes Net from data base of cases.
     * @param Bnet b. The Bayes Net to be scoring.
     * @return double. The score.
     */

    
    public abstract double score (Bnet b);

    /**
     * This method scores a Node List from data base of cases.
     * @param NodeList vars. The Node List.
     * @return double. The score.
     */


    public  double scoret (NodeList vars) {
        double x=0.0;
        int n,i,j;
        Node z;
        char[] code;
        String aux,aux2;
        
        n = data.getVariables().size();
       
        
     
       
        code = new char[n];
        
        for (i=0;i<n;i++) {
            z = data.getVariables().elementAt(i);
            if (vars.getId(z) > -1 ) {
                code[i] = '1';
            }
            else {code[i] = '0';}
        }
        
        aux2 = ((Node) vars.elementAt(0)).getName();
       aux =  aux2.concat( new String(code));
        
        
        
        
         if (cscores.containsKey(aux) )
         {
             x =  ((Double) cscores.get(aux)); 
  //           System.out.println( aux + " Contains " + x);
         }
         else {
            x = this.score(vars);
    //        System.out.println(aux + " Non Contains " + x);
            cscores.put(aux, x);
            
         }
        
        
        return x;
    }
    
    
    /**
     * This method scores a Node List from data base of cases.
     * @param NodeList vars. The Node List.
     * @return double. The score.
     */


    public abstract double score (NodeList vars);

    /** Access methods **/

    public DataBaseCases getData(){
	return data;
    }

    public void setData(DataBaseCases data){
	this.data = data;
    }
    
    public double getTotalTime(){
        return totalTime;
    }
    public double getTimeStEval(){
        return timeStEval;
    }
    public double getTotalSt(){
        return totalSt;
    }
    public double getTotalStEval(){
        return tStEval;
    }
    public double getAverageNVars(){
        return (avStNVar/tStEval);
    }
       public double getDep (Node x, Node y, NodeList z) {
  return(scoreDep(x,y,z));
    
    
}   
    
    public double scoreDep (Node x, Node y, NodeList z) {
    
    NodeList aux;
    double x1,x2;
    
    aux = new NodeList();
    aux.insertNode(x);
    aux.join(z);
    x1 = score(aux);
    aux.insertNode(y);
    x2 = score(aux);
    return(x2-x1);
    
    
}

    public double[][] computeMatrixDep(NodeList nodes) {
    double[][] dependence;
    double x,y;
    NodeList aux;
    int i,j;
    int size = nodes.size();



     dependence = new double[size][size];
          aux = new NodeList();
       for (i=0; i<size-1; i++){
         
           aux.insertNode(nodes.elementAt(i));
           x = score(aux);
         for (j=i+1; j<size; j++)  {
           aux.insertNode(nodes.elementAt(j));
           y = score(aux);
           aux.removeNode(1);
           dependence[i][j] = y-x;
         }
           aux.removeNode(0);
       }
     return dependence;
    }

    
public boolean independents (Node x, Node y, NodeList z) {
    
  double aux;
  
  aux = scoreDep(x,y,z);
  if (aux>=0) {return(true);}
  else {return(false);}
    
 
  
}


   
public boolean independents (Node x, Node y, NodeList z, int degree) {
    
  return(independents(x,y,z));
    
 
  
}


   
public boolean independents (Node x, Node y, NodeList z, double degree) {
    
  return(independents(x,y,z));
    
 
  
}
   
    
   public NodeList getNodeList(){
        
        return(getData().getNodeList());
    }  
    
    
}
// Metrics


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package elvira.learning.classification.unsupervised.discrete;

import java.io.*;
import java.util.*;

import elvira.*;
import elvira.database.*;
import elvira.learning.*;
import elvira.inference.clustering.HuginPropagation;
import elvira.potential.*;
import elvira.inference.elimination.VariableElimination;
import elvira.learning.preprocessing.*;
import elvira.parser.ParseException;
import elvira.learning.classification.AuxiliarPotentialTable;
import elvira.learning.classification.unsupervised.discrete.NBayesMLEMMStart;
import elvira.potential.RPT.*;

/**
 *
 * @author smc
 */
public class NBayesMLEMSelective {

  protected int     numberOfVariables;
  protected DataBaseCases  cases;
  protected NodeList  nodes;
  protected NodeList artnodes;
  protected int numberOfCases;
  protected BDeMetrics metric;
  protected double dependences[][];
  protected Vector clusters;
  protected Vector databases;
  protected Vector classifiers;
  protected int mscore; // 0 Akaike 1 BIC
  protected boolean lowlimit;
  protected RecursiveBnet rgraph,upgraph;
  protected Bnet treecluster;
  static int NCASES=20;
  protected String name;
  protected int level;
  protected RecursivePotentialTree RPT1, RPT2, RPT3;
  protected ListTreeNode LTN;
  protected int nvariable;
  protected Map refvar;


   public static void main(String args[]) throws ParseException, IOException {

     
     
     
      if(args.length < 1){
	  System.out.println("too few arguments: Usage: file.dbc output.elv");
	  System.exit(0);
      }

      System.out.println(args[0]);

       System.out.println(args[1]);

      FileInputStream f = new FileInputStream(args[0]);
     // System.out.println("antes de abrir base datos");
      DataBaseCases cases = new DataBaseCases(f);
     //    System.out.println("despues de abrir base datos");
      NBayesMLEMSelective graph = new NBayesMLEMSelective(cases);


      graph.learning();
      graph.treecluster.save(args[1]);
      
//      graph.printDataBases();

      
 //    for(i=0; i<graph.nodes.size(); i++)
 //        for(j=i+1; j< graph.nodes.size(); j++) {
 //            System.out.println("Dependencia Nodo " + i + " Nodo "+ j + " = " + graph.dependences[i][j]);
   //      }


      
//       
    


//          double d2 = net.KL(baprend);
//          System.out.println("Divergencia KL de la red aprendida - real " + d2);
	
//	  System.out.print(outputNet2.linkUnOriented().toString());
      }


public NBayesMLEMSelective(DataBaseCases data) {
        
          name = "newNBayesSelective";
          nodes    = data.getVariables().copy();
          refvar = new HashMap();
          cases = data;
          metric = new BDeMetrics(data,4);
          level = 0;
          nvariable = 0;
          artnodes = new NodeList();
           initialRef();
    this.numberOfVariables = this.nodes.size();  
    lowlimit = false;
    mscore = 0; 
        
    }


public NBayesMLEMSelective(DataBaseCases data, String argument) {
        
          name = "newNBayesSelective";
          nodes    = data.getVariables().copy();
          refvar = new HashMap();
          cases = data;
          metric = new BDeMetrics(data,4);
          level = 0;
          nvariable = 0;
          artnodes = new NodeList();
           initialRef();
    this.numberOfVariables = this.nodes.size();  
    lowlimit = true;
    mscore =  Integer.parseInt(argument); 
        
    }



public NBayesMLEMSelective(DataBaseCases data, int l) {
          name = "newNBayesSelective";
          nodes    = data.getVariables().copy();
          cases = data;
          metric = new BDeMetrics(data,4);
          level = l;
                nvariable = 0;
artnodes = new NodeList();
initialRef();
    this.numberOfVariables = this.nodes.size();

    }

public Bnet getOutput() {
    return treecluster;
}

private void initialRef(){
    int i;
    Node k;
    
    for (i=0;i<nodes.size();i++) {
        k=nodes.elementAt(i);
        refvar.put(k,k);
    }
}

public void computeClusters() {
    int i,j,k,imax,jmax,nnodes,tnodes;
    boolean selected[];
    NodeList newcluster;
    Node basic;
    double maxdep,thisdep,pairdep;
    int present[];
    int npresent;
    double weight = 1.0;


    maxdep = -1;
    dependences = metric.computeMatrixDep(nodes);
    
    nnodes = nodes.size();
    present = new int[nnodes];
    tnodes = nnodes;

    selected = new boolean[nnodes];

    for(i=0;i<nnodes;i++) {selected[i] = false;}
    
    
  for(i=0;i<nnodes;i++) {
      for(j=0; j<nnodes;j++) {
          System.out.print(dependences[i][j]+ " - ");
          
          
      }
      System.out.println ();
  
  }

    clusters = new Vector();

    while (nnodes>0) {

    newcluster = new NodeList();
    npresent = 0;

    if (nnodes==1){
        basic = nodes.elementAt(0);
        for(i=0;i<tnodes;i++) {if (!selected[i]) {basic=nodes.elementAt(i);selected[i]=true; break;}}

        newcluster.insertNode(basic);
        clusters.addElement(newcluster);
        nnodes--;
    }
    else {
       imax=0;jmax=0;
       for(i=0;i<tnodes;i++) {
            for(j=i+1;j<tnodes;j++) {
                if (!selected[i]&& !selected[j]) {
                    if (dependences[i][j]>maxdep) {
                         imax= i;
                         jmax = j;
                         maxdep = dependences[i][j];
                    }
                }
            }
       }
    if (maxdep > 0) {
        System.out.println("Comenzando cluster " + imax + " con " + jmax);
        basic = nodes.elementAt(imax);
        newcluster.insertNode(basic);
        present[npresent]=imax;
        npresent++;
        selected[imax]=true;
        nnodes--;
        basic = nodes.elementAt(jmax);
        newcluster.insertNode(basic);
        present[npresent]=jmax;
        npresent++;
        selected[jmax]=true;
        nnodes--;
        while(maxdep>0) {
            maxdep = -1.0;
            for(i=0;i<tnodes;i++) {
                if (!selected[i]) {
                    System.out.println("Probando nodo " + i);
                    thisdep=0.0;
                    for(j=0;j<npresent;j++){
                        k = present[j];
                        pairdep = 0.0;
                        if (i<k) {pairdep = dependences[i][k];} else {pairdep = dependences[k][i];}
                        if (pairdep>0) {thisdep+=pairdep;} else {thisdep += weight*pairdep;}
     //                     if (pairdep>thisdep) {thisdep=pairdep;}
                        System.out.println("Dependencia con " + k + " = " + pairdep);
                    }
                  if (thisdep > maxdep) {
                      imax = i;
                      maxdep = thisdep;
                  }


                }
            }
            if (maxdep >0 ) {
                    System.out.println("añadiendo nodo " + imax);

                basic = nodes.elementAt(imax);
               newcluster.insertNode(basic);
                  present[npresent]=imax;
                npresent++;
                selected[imax]=true;
                nnodes--;


            }


        }


        clusters.addElement(newcluster);



    }

    else {
          basic = nodes.elementAt(0);
        for(i=0;i<tnodes;i++) {if (!selected[i]) {basic=nodes.elementAt(i);selected[i]=true; break;}}

        newcluster.insertNode(basic);

        clusters.addElement(newcluster);
        nnodes--;

      

    }


    }

}
}



public void initialClusters() {
    int i,j,k,nnodes;
    NodeList newcluster;
    Node basic;
    double maxdep;
   
   
   


   
    dependences = metric.computeMatrixDep(nodes);
    nnodes = nodes.size();
   
  
 
      
    clusters = new Vector();
    
    
    for(i=0;i<nnodes;i++) {
             newcluster = new NodeList();
             clusters.addElement(newcluster); 
              basic = nodes.elementAt(i);
              newcluster.insertNode(basic);
             
    }

    
    
}




public void initialClustersCE() {
    int i,j,k,nnodes;
    NodeList newcluster;
    Node basic;
    double maxdep;
   
   
   


   
    dependences = cases.computeMatrixDep(nodes);
    nnodes = nodes.size();
   
  
 
      
    clusters = new Vector();
    
    
    for(i=0;i<nnodes;i++) {
             newcluster = new NodeList();
             clusters.addElement(newcluster); 
              basic = nodes.elementAt(i);
              newcluster.insertNode(basic);
             
    }

    
    
}



public void initialClusters(NodeList variables) {
    int i,j,k,nnodes;
    NodeList newcluster;
    Node basic;
    double maxdep;
     
     
   
   
  
    
   
    
    nnodes = variables.size();
   
  
 
      
     clusters = new Vector();
    
    
    for(i=0;i<nnodes;i++) {
             newcluster = new NodeList();
             clusters.addElement(newcluster); 
              basic = variables.elementAt(i);
              newcluster.insertNode(basic);
             
    }

    
   
  
    
    
}





public void printClusters() {
   int i,nclusters;
   NodeList aux;

   System.out.println("Computed Clusters");

   nclusters = clusters.size();

   for (i=0;i<nclusters;i++) {
    aux = (NodeList) clusters.elementAt(i);
      System.out.println("Cluster Number " + i);
      aux.printNames();



   }

}



public void printClusters(PrintWriter p) {
   int i,nclusters;
   NodeList aux;

   p.println("Computed Clusters");

   nclusters = clusters.size();

   for (i=0;i<nclusters;i++) {
    aux = (NodeList) clusters.elementAt(i);
      p.println("Cluster Number " + i);
      p.println(aux.toString2());



   }

}




public void refineClusters() {
   int i,j,nclusters,k,l,imax,posvar,pos2;
   double dep,maxdep;
   NodeList aux, aux2,aux3;
   Node var;
   boolean changes;
     
   changes=true;

   while (changes) {

   changes=false;

 nclusters = clusters.size();

   for (i=0;i<nclusters;i++) {
    aux = (NodeList) clusters.elementAt(i);
      int nsize = aux.size();
      j= 0;
      while (j<nsize) {

          var = aux.elementAt(0);
           posvar = nodes.getId(var);
          j++;
          aux.removeNode(var);
          imax = i;
          maxdep = -1;
           for (k=0;k<nclusters;k++) {
                aux2 = (NodeList) clusters.elementAt(k);
                dep = 0.0;
                for (l=0; l<aux2.size(); l++) {
                    pos2 = nodes.getId(aux2.elementAt(l));
                    if (pos2<posvar) {
                    dep += dependences[pos2][posvar];}
                    else {dep += dependences[posvar][pos2];}
                }
                
                if (dep>maxdep) {maxdep=dep;imax = k;}


      }
     if ((maxdep>0)      ) {
     ((NodeList) clusters.elementAt(imax)).insertNode(var);
     
        if (imax != i) {
         changes=true; nsize--;
          System.out.println("Nodo " + var + " cambia de cluster " + i + " a cluster " + imax);
     
        }
     
     }
     else {
        if ((maxdep==0)&& (aux.size()==0)) {

     
             ((NodeList) clusters.elementAt(i)).insertNode(var);
        }
        else {
        changes = true;
        aux3 = new NodeList();
        aux3.insertNode(var);
        clusters.add(aux3);
       nsize--;

     }
     }

    

      }

   }
   }

   // Removing emptyclusters

   i=0;
   while(i<clusters.size()) {
       aux = (NodeList) clusters.elementAt(i);
       if (aux.size()>0) {i++;}
       else {clusters.remove(i);}
   }
   
}




public void refineClusters2(NodeList thenodes, double[][] thedep) {
   int i,j,nclusters,k,l,imax,posvar,pos2;
   double dep,maxdep,comdep,averdep;
   NodeList aux, aux2,aux3;
   Node var;
   boolean changes;
     
   changes=true;
 nclusters = clusters.size();
 
  
  
  
   while (changes) {

   changes=false;



 

   for (i=0;i<nclusters;i++) {
    aux = (NodeList) clusters.elementAt(i);
      int nsize = aux.size();
      j= 0;
      while (j<nsize) {

          var = aux.elementAt(0);
           posvar = thenodes.getId(var);
          j++;
          aux.removeNode(var);
          imax = i;
          maxdep = -1.0; 
           for (k=0;k<nclusters;k++) {
                aux2 = (NodeList) clusters.elementAt(k);
                dep=0.0; averdep=0.0;
                for (l=0; l<aux2.size(); l++) {
                    pos2 = thenodes.getId(aux2.elementAt(l));
                    if (pos2<posvar) {
                    comdep = thedep[pos2][posvar];}
                    else {comdep = thedep[posvar][pos2];}
                    if (comdep>dep) {dep=comdep;}
                    if (comdep>0) {averdep+=comdep;}
                }
                
                 
                averdep = averdep/aux2.size();
                if (dep>maxdep) {maxdep=dep;imax = k;}


      }
     if ((maxdep>0)      ) {
     ((NodeList) clusters.elementAt(imax)).insertNode(var);
     
        if (imax != i) {
         changes=true; nsize--;
          System.out.println("Nodo " + var + " cambia de cluster " + i + " a cluster " + imax);
     
        }
     
     }
     else {
        if ((maxdep==0)&& (aux.size()==0)) {

     
             ((NodeList) clusters.elementAt(i)).insertNode(var);
        }
        else {
        changes = true;
        aux3 = new NodeList();
        aux3.insertNode(var);
        clusters.add(aux3);
       nsize--;

     }
     }

    

      }

   }
   }

   // Removing emptyclusters

   i=0;
   while(i<clusters.size()) {
       aux = (NodeList) clusters.elementAt(i);
       if (aux.size()>0) {i++;}
       else {clusters.remove(i);}
   }
   
}

  



public void refineClusters3(NodeList thenodes, double[][] thedep) {
   int i,j,nclusters,k,l,imax,posvar,pos2;
   double dep,maxdep,comdep,averdep;
   NodeList aux, aux2,aux3;
   Node var;
   boolean changes;
     
   changes=true;
 nclusters = clusters.size();
 
  
  
  
   while (changes) {

   changes=false;



 

   for (i=0;i<nclusters;i++) {
    aux = (NodeList) clusters.elementAt(i);
      int nsize = aux.size();
      j= 0;
      while (j<nsize) {

          var = aux.elementAt(0);
           posvar = thenodes.getId(var);
          j++;
          aux.removeNode(var);
          imax = i;
          maxdep = 0.0; 
           for (k=0;k<nclusters;k++) {
                aux2 = (NodeList) clusters.elementAt(k);
                dep=0.0; averdep=0.0;
                for (l=0; l<aux2.size(); l++) {
                    pos2 = thenodes.getId(aux2.elementAt(l));
                    if (pos2<posvar) {
                    comdep = thedep[pos2][posvar];}
                    else {comdep = thedep[posvar][pos2];}
                    if (comdep>dep) {dep=comdep;}
                      {averdep+=comdep;}
                }
                
                
               if(aux2.size()>0) { averdep = averdep/aux2.size();
               
                if (averdep>maxdep) {maxdep=averdep;imax = k;}
               
               }

      }
     
     ((NodeList) clusters.elementAt(imax)).insertNode(var);
     
        if (imax != i) {
         changes=true; nsize--;
          System.out.println("****************************** Nodo " + var + " cambia de cluster " + i + " a cluster " + imax);
     
        }
     
    
     }

    

      

   }
   }

   // Removing emptyclusters

   i=0;
   while(i<clusters.size()) {
       aux = (NodeList) clusters.elementAt(i);
       if (aux.size()>0) {i++;}
       else {clusters.remove(i);}
   }
   
}





public void computeDataBases() {

  int i;
  NodeList aux;
  DataBaseCases clusterdata;

 databases = new Vector();

  for (i=0; i<clusters.size(); i++) {
      aux = ((NodeList) clusters.elementAt(i));
      clusterdata = cases.copy();
      clusterdata.projection(aux);
      databases.add(clusterdata);
      





  }



}


public void printDataBases() {
   int i,j,ndata;
   DataBaseCases aux;

   System.out.println("Computed Data");

   ndata = databases.size();

   for (i=0;i<ndata;i++) {
    aux = (DataBaseCases) databases.elementAt(i);
      System.out.println("Cluster Number " + i);
      aux.getNodeList().printNames();
      aux.getCaseListMem().print();



   }

}


  


public void estimateEM()  throws  IOException {
   int i;
   NBayesMLEMMStart unsupervised;
   Bnet aux;
   Node var;
   Relation rel;
   PotentialTable pot;

   classifiers= new Vector();

   for (i=0; i<databases.size(); i++) {

       if (((NodeList) clusters.elementAt(i)).size()>1) {
       unsupervised = new NBayesMLEMMStart( (DataBaseCases) databases.elementAt(i),2);
       unsupervised.learning(true,10);
       unsupervised.classifier.setName("Cluster"+i+".elv");
       classifiers.add(unsupervised.classifier);
       

   }
       else{
           var = ((NodeList) clusters.elementAt(i)).elementAt(0);
           aux = new Bnet();
           aux.getNodeList().insertNode(var);
           pot =  cases.getPotentialTable((NodeList) clusters.elementAt(i));
      //     System.out.println("Potential Isolated " + var.getName() + " Number of Cases " + cases.getCaseListMem().getNumberOfCases() );
      //     System.out.println("Variables in Database " + cases.getNodeList());
      //      System.out.println("Variables in DatabaseListMemory " + cases.getCaseListMem().getVariables()) ;
           pot.print();
            
            pot.sum(1.0);
            pot.normalize();

            
            rel = new Relation(pot);
            aux.getRelationList().add(rel);
             aux.setName("Isolated variable"+i+".elv");
       classifiers.add(aux);
      
       }
   }
 




  
}

/*
public void learning(int level,int par) throws  IOException {

   Bnet aux;
   RecursiveBnet raux;

    int i,j,k;
    DataBaseCases cases1, cases2, newcases;
    CaseListMem mc1,mc2,mcu;
    NodeList rnodes;
    Configuration conf;
    Node classnode;
    VariableElimination prop;
    Evidence evid;
    PotentialTable pot,pot2;
    double x;
    NBayesMLEMSelective r,l,u;
    NodeList newvar;
    Configuration newconf;
    int values[][];
    ListTreeNode BasicList, BasicList2, BasicList3;

      LTN = new ListTreeNode();
      initialClusters();
      
      
            refineClusters3(nodes,dependences);

     printClusters();
      computeDataBases();
      estimateEM();

       newvar = new NodeList();

        j=0;

         for (i=0;i<classifiers.size();i++)  {
           aux = (Bnet) classifiers.elementAt(i);
              if (aux.getNodeList().size()>1) {
              //   classnode = aux.getNodeList().lastElement();
              //classnode.setTitle("Aux" + j);
               //  classnode.setName("Aux" + j);
                  j++;
                 //newvar.insertNode(classnode);
           //      System.out.println("Adding variable " + classnode);
          //       classnode.print();
              }



        }

      

         values = new int[cases.getNumberOfCases()][j];

      rgraph = new RecursiveBnet();
      RPT1 = new RecursivePotentialTree();
      BasicList = new ListTreeNode();
      RPT1.setValues(BasicList);

       RPT2 = new RecursivePotentialTree();
      BasicList2 = new ListTreeNode();
      RPT2.setValues(BasicList2);

      RPT3 = new RecursivePotentialTree();
      BasicList3 = new ListTreeNode();
      RPT3.setValues(BasicList3);

     //   System.out.println("Created Recursive Bnbet");

     // RPT1.print();

      if (classifiers.size()>0) {
           k=0;
        for (i=0;i<classifiers.size();i++) {
          
            aux = (Bnet) classifiers.elementAt(i);

             pot2 = (PotentialTable) ((Relation) aux.getRelationList().lastElement()).getValues();
               // System.out.println("Printing Potential added");

                BasicList.addChild(new PotentialTreeNode(pot2));

                

            if ((aux.getNodeList().size()==1) || cases.getNumberOfCases()<NCASES) {
                raux = new RecursiveBnet(aux);
                rgraph.addBnet(raux);
               
                 if ((aux.getNodeList().size()==1))
                     {BasicList2.addChild(new PotentialTreeNode(pot2));
                      LTN.addChild(new PotentialTreeNode(pot2));
                      BasicList3.addChild(new PotentialTreeNode(pot2));
                 }



                 if (cases.getNumberOfCases()<NCASES) {
                     for(j=0; j < aux.getRelationList().size()-1; j++) {
                           pot2 = (PotentialTable) ((Relation) aux.getRelationList().elementAt(j)).getValues();
                           BasicList2.addChild(new PotentialTreeNode(pot2));
                             BasicList.addChild(new PotentialTreeNode(pot2));
                             
                     }
                 }
               // System.out.print("Adding a potential");
               //  BasicList.print(3);
            }
            else {

                    for(j=0; j < aux.getRelationList().size()-1; j++) {
                           pot2 = (PotentialTable) ((Relation) aux.getRelationList().elementAt(j)).getValues();
                           LTN.addChild(new PotentialTreeNode(pot2));
                           BasicList3.addChild(new PotentialTreeNode(pot2));

                     }

               
                rnodes = aux.getNodeList().copy();
                classnode = rnodes.lastElement();
                rnodes.removeNode(rnodes.size()-1);
                mc1 = new CaseListMem(rnodes);
                 mc2 = new CaseListMem(rnodes);
              cases1 = new DataBaseCases("Database1",mc1);
               cases2 = new DataBaseCases("Database2",mc2);
               
               for(j=0;j<cases.getNumberOfCases(); j++) {

                   conf = cases.getCaseListMem().get(j);
                    evid = new Evidence(conf);
                   prop = new VariableElimination(aux,evid);
                   prop.getPosteriorDistributionOf(classnode);
                   pot = (PotentialTable) prop.getResults().elementAt(0);
                    
                   pot.normalize();
                 
                   
                   x = Math.random();
                   if (x<=pot.getValue(0)) {
                       cases1.getCaseListMem().put(conf);
                       values[j][k]=0;

                       
                   }
                   else {cases2.getCaseListMem().put(conf);  values[j][k]=1;}

                  

               }

              k++;
             
             cases1.setNumberOfCases(cases1.getCaseListMem().getNumberOfCases());
             cases2.setNumberOfCases(cases2.getCaseListMem().getNumberOfCases());
             

         //      System.out.println("Base 1");

        //      for(j=0;j<cases1.getNumberOfCases(); j++) {
        //          conf = cases1.getCaseListMem().get(j);
        //          conf.print();
        //      }
       //      System.out.println("Base 2");

       //         for(j=0;j<cases2.getNumberOfCases(); j++) {
      //            conf = cases2.getCaseListMem().get(j);
     //             conf.print();
      //        }

            r = new NBayesMLEMSelective(cases1);
            r.learning(level+1,1);
             l = new NBayesMLEMSelective(cases2);
            l.learning(level+1,2);
            raux = new RecursiveBnet(l.rgraph,r.rgraph,aux);
               raux.setVar((FiniteStates) classnode);
                rgraph.addBnet(raux);
                SplitTreeNode split = new SplitTreeNode((FiniteStates) classnode);
                split.addSon(l.RPT1.getRoot());
                 split.addSon(r.RPT1.getRoot());
               BasicList.addChild(split);
               BasicList2.addChild(split);


            }







            }

         
 j=0;

         for (i=0;i<classifiers.size();i++)  {
           aux = (Bnet) classifiers.elementAt(i);
              if (aux.getNodeList().size()>1) {
                classnode = aux.getNodeList().lastElement();
                classnode.setTitle("Aux" + j + "_" + level + "_" + par);
                 classnode.setName("Aux" + j + "_" + level + "_" + par);
                  j++;
                 newvar.insertNode(classnode);
           //      System.out.println("Adding variable " + classnode);
          //       classnode.print();
              }



        }

        mcu = new CaseListMem(newvar);
        newcases = new DataBaseCases("Databaseu",mcu);

         //   System.out.println("Base arriba");
              newvar.printNames();
              upgraph = new RecursiveBnet();
              upgraph.setLabel(3);

              if (newvar.size()>0){
              for(j=0;j<cases.getNumberOfCases(); j++) {

                  newconf = new Configuration();
                 for(k=0;k<newvar.size();k++) {
                    
                     newconf.putValue((FiniteStates) newvar.elementAt(k), values[j][k]);

                 }
              //    newconf.print();
                 newcases.getCaseListMem().put(newconf);
              }
              //       System.out.println("Variables");
                //     newcases.getNodeList().printNames();

                     newcases.setNumberOfCases(cases.getNumberOfCases());



              u = new NBayesMLEMSelective(newcases);
              u.learning(level-1,1);
              upgraph.addBnet(u.rgraph);
              upgraph.addBnet(u.upgraph);

              for(j=0;j<u.LTN.getNumberOfChildren(); j++) {
              LTN.addChild(u.LTN.getChild(j));
              BasicList3.addChild(u.LTN);
              }

              

                for(j=0;j<LTN.getNumberOfChildren(); j++) {
              BasicList2.addChild(LTN.getChild(j));
              }



        }
              else {upgraph = null;}


      }
   

      }

 */

public void learning() throws  IOException {

   Bnet aux;
   RecursiveBnet raux;
   DELearning param;

    int i,j,k, ndim;
    DataBaseCases cases1, cases2, newcases;
    CaseListMem mc1,mc2,mcu;
    NodeList rnodes, newnodes, oldnodes;
    Configuration conf;
    Node classnode;
    VariableElimination prop;
    Evidence evid;
    PotentialTable pot,pot2;
    double x;
    
    NBayesMLEMSelective r,l,u;
    NodeList newvar;
    Configuration newconf;
    int values[][];
    double newdep[][], olddep[][];

      treecluster = new Bnet(nodes);
      initialClusters();
      oldnodes = nodes;
      
      System.out.println("Firt call to refine");
     
       refineClusters2(nodes,dependences);
       
  //         System.out.println("Refine second stage");
       
// refineClusters3(nodes,dependences);

     printClusters();
     olddep = dependences;
  
     
     
     
     do {
     
        ndim=0;
        for (i=0;i< clusters.size(); i++  ) {
            if (((NodeList) clusters.elementAt(i)).size() >1) {ndim++;}
            
        }
         
         newnodes = new NodeList();
         newdep = new double[ndim][ndim];
         
     updateBn2(newnodes, oldnodes, newdep,olddep);
     initialClusters(newnodes);
     System.out.println(" call to refine inside loop2");
     
     refineClusters2(newnodes,newdep);
     oldnodes=newnodes;
     olddep=newdep;
     
} while (newnodes.size() > 1);

      System.out.println("Comienzo parametros");
  if (newnodes.size()==1) {  treecluster.addRelation(newnodes.elementAt(0));}
     initialPotentials();
     param = new DELearning(cases,treecluster,0.01);
     param.optimizelearningmissing();
     optimizeCases();
     optimizeCases();

      }

private NodeList replaceArtificial(NodeList var) {
    NodeList var2;
    int i;
    
    var2 = new NodeList();
    
    for(i=0; i<var.size();i++) {
        var2.insertNode((Node) refvar.get(var.elementAt(i)));
    }
    return (var2);
}

private void initialPotentials () {
    Vector Relations;
    int i,j,k,nrel,ncases;
    Potential pot;
    PotentialTable pot2;
    NodeList var,var2,parents;
    FiniteStates head,tail;
    double total;
    Configuration conf;
    
   //   System.out.println("Entrando en initial potentials");
  Relations = treecluster.getRelationList();    
  nrel = Relations.size(); 
    for (i=0;i<nrel;i++) {
        System.out.println("First Potential");
      pot =  ( ((Relation) Relations.elementAt(i)).getValues());
      var = ( ((Relation) Relations.elementAt(i)).getVariables());
      var2 = replaceArtificial(var);
      if (var.size()>1) {
      head = (FiniteStates) var2.elementAt(0);
      tail = (FiniteStates) var2.elementAt(1);
      double[][] cprob =  cases.getFreq(tail,head);
      
      System.out.println("Tabla frecuencias calculada");
      
      for(j=0;j<tail.getNumStates();j++) {
          total = 0.0;
           for(k=0;k<head.getNumStates();k++) {
               cprob[j][k]+= Math.log((cases.getNumberOfCases()+1.0)) + (Math.random());
            //   cprob[j][k]+= (2.0+Math.random());
               total += cprob[j][k];
           }
            for(k=0;k<head.getNumStates();k++) {
             cprob[j][k]= cprob[j][k]/total;
            }
      }
       System.out.println("Tabla condicional calculada");
      
       head = (FiniteStates) var.elementAt(0);
       tail =   (FiniteStates) var.elementAt(1);
           
          for(j=0;j<tail.getNumStates();j++) {
              conf = new Configuration();
                       conf.insert(tail, j);
           for(k=0;k<head.getNumStates();k++) {
                    conf.insert(head, k);  
                   pot.setValue(conf, cprob[j][k]);
                    conf.remove(head);
           }
          }
           pot.print();
         
  }
      else {
            ;
            }
    }
    
      }
  
    
      
  


   
   public void  updateBn(NodeList newnodes, NodeList oldnodes, double[][] newdep, double[][] olddep) {
       int i,j,k,l,nclusters,clustersize,nnew;
       NodeList aux,aux2;
       FiniteStates auxNode, nodeCl, x,y;
       double maxdep, depxy, averdep;
       
       
       nclusters = clusters.size();
       
       
   for (i=0;i<nclusters;i++) {
    aux = (NodeList) clusters.elementAt(i);
    
    clustersize = aux.size();
    
    if (clustersize>1){
        auxNode = new FiniteStates(2);
        auxNode.setName("AuxNode" + nvariable);
        artnodes.insertNode(auxNode);
        newnodes.insertNode(auxNode);
        nvariable++;
       try{
	treecluster.addNode(auxNode);
        }catch (InvalidEditException e){};
        for (j=0; j<aux.size(); j++) {
            nodeCl = (FiniteStates) aux.elementAt(j);
             try{
            treecluster.createLink(auxNode, nodeCl);
              }catch (InvalidEditException e){};
        }
       }
    else {
        treecluster.addRelation(aux.elementAt(0));
    }



   }
   
  nnew = newnodes.size();
  
   
   
   
   for (i=0;i<nnew;i++) {
       for(j=i+1; j<nnew; j++) {
             aux = (NodeList) clusters.elementAt(i);
             aux2 = (NodeList) clusters.elementAt(j);
             
             maxdep=-1.0; averdep= 0.0;
             for (k=0; k<aux.size(); k++) {
                 x = (FiniteStates) aux.elementAt(k);
                 for (l=0; l<aux2.size(); l++) {
                     y = (FiniteStates) aux2.elementAt(l);
                     if (oldnodes.getId(x)<oldnodes.getId(y)) {
                     depxy = olddep[oldnodes.getId(x)][oldnodes.getId(y)];}
                     else {depxy = olddep[oldnodes.getId(y)][oldnodes.getId(x)];}
                     if (depxy>maxdep) {maxdep=depxy;}
                //     averdep+=depxy;
                     if (depxy>0) {averdep+=depxy;} 
                 }
              
              
             }
            if (i==j) {newdep[i][j] = 0.0;}
            //  else {newdep[i][j] = maxdep; newdep[j][i] = maxdep;}
            // else {newdep[i][j] = averdep/(aux.size()*aux2.size()) + maxdep; newdep[j][i] = newdep[i][j];}
            else {newdep[i][j] = averdep/(aux.size()*aux2.size()); newdep[j][i] = newdep[i][j];}
         //   System.out.println("Dependeica entre " + i + " y " + j + " = " + newdep[i][j] );
       }
       
       
       
       
       
   }
   
   
   
   
   
   
   
       
       
       
   }

   
   
   
   public void  updateBn2(NodeList newnodes, NodeList oldnodes, double[][] newdep, double[][] olddep) {
       int i,j,k,l,nclusters,clustersize,nnew, casesmax;
       NodeList aux,aux2;
       FiniteStates auxNode, nodeCl, nodemax, nodeaux, x,y;
       double maxdep, depxy, averdep,ax;
       
       
       nclusters = clusters.size();
       
       
   for (i=0;i<nclusters;i++) {
    aux = (NodeList) clusters.elementAt(i);
    
    clustersize = aux.size();
    
    if (clustersize>1){
        
        maxdep=0.0;
        nodemax =   (FiniteStates) aux.elementAt(0);
        casesmax = ((FiniteStates) aux.elementAt(0)).getNumStates();
        x=nodemax;
        for (j=1; j<clustersize; j++) {
            y = (FiniteStates) aux.elementAt(j);
              if (oldnodes.getId(x)<oldnodes.getId(y)) {
                     depxy = olddep[oldnodes.getId(x)][oldnodes.getId(y)];}
                     else {depxy = olddep[oldnodes.getId(y)][oldnodes.getId(x)];}
            maxdep +=  depxy;} 
        
        for (k=1; k<clustersize;k++) {
            x =   (FiniteStates) aux.elementAt(k);
            ax=0.0;
            for (j=1; j<k; j++) { 
               y = (FiniteStates) aux.elementAt(j);
               if (oldnodes.getId(x)<oldnodes.getId(y)) {
                     depxy = olddep[oldnodes.getId(x)][oldnodes.getId(y)];}
                     else {depxy = olddep[oldnodes.getId(y)][oldnodes.getId(x)];}
                ax+=  depxy;
            } 
            for (j=k+1; j<clustersize; j++) { 
               y = (FiniteStates) aux.elementAt(j);   
                if (oldnodes.getId(x)<oldnodes.getId(y)) {
                     depxy = olddep[oldnodes.getId(x)][oldnodes.getId(y)];}
                     else {depxy = olddep[oldnodes.getId(y)][oldnodes.getId(x)];}
                ax+=  depxy;
            } 
             
          
        
        if (ax>maxdep) {
            maxdep = ax;
            casesmax = ((FiniteStates) aux.elementAt(k)).getNumStates();
            nodemax = (FiniteStates) aux.elementAt(k);
        }
        }
        
        auxNode = new FiniteStates(casesmax);
        auxNode.setName("AuxNode" + nvariable);
        artnodes.insertNode(auxNode);
        newnodes.insertNode(auxNode);
        refvar.put(auxNode, refvar.get(nodemax)   );
        nvariable++;
       try{
	treecluster.addNode(auxNode);
        }catch (InvalidEditException e){};
        for (j=0; j<aux.size(); j++) {
            nodeCl = (FiniteStates) aux.elementAt(j);
             try{
            treecluster.createLink(auxNode, nodeCl);
              }catch (InvalidEditException e){};
        }
        
        
        
       }
    else {
        treecluster.addRelation(aux.elementAt(0));
    }



   }
   
  nnew = newnodes.size();
  
   
   
   
   for (i=0;i<nnew;i++) {
       for(j=i+1; j<nnew; j++) {
             aux = (NodeList) clusters.elementAt(i);
             aux2 = (NodeList) clusters.elementAt(j);
             
             maxdep=-1.0; averdep= 0.0;
             for (k=0; k<aux.size(); k++) {
                 x = (FiniteStates) aux.elementAt(k);
                 for (l=0; l<aux2.size(); l++) {
                     y = (FiniteStates) aux2.elementAt(l);
                     if (oldnodes.getId(x)<oldnodes.getId(y)) {
                     depxy = olddep[oldnodes.getId(x)][oldnodes.getId(y)];}
                     else {depxy = olddep[oldnodes.getId(y)][oldnodes.getId(x)];}
                     if (depxy>maxdep) {maxdep=depxy;}
                //     averdep+=depxy;
                     if (depxy>0) {averdep+=depxy;} 
                 }
              
              
             }
            if (i==j) {newdep[i][j] = 0.0;}
            //  else {newdep[i][j] = maxdep; newdep[j][i] = maxdep;}
            // else {newdep[i][j] = averdep/(aux.size()*aux2.size()) + maxdep; newdep[j][i] = newdep[i][j];}
            else {newdep[i][j] = averdep/(aux.size()*aux2.size()); newdep[j][i] = newdep[i][j];}
         //   System.out.println("Dependeica entre " + i + " y " + j + " = " + newdep[i][j] );
       }
       
       
       
       
       
   }
   
   
   
   
   
   
   
       
       
       
   }

   
   

 public void optimizeCases() throws  IOException {
   
     FiniteStates var;
     int i;
     System.out.println("Entering Optimize Cases");
     for ( i=0; i<treecluster.getNodeList().size(); i++) {
         var = (FiniteStates) treecluster.getNodeList().elementAt(i);
         
         if (artnodes.getId(var) > -1) {
             optimizeCases(var);
         }
         
         
         
     }
     
     
     
     
     
     
 }
 
 
 public double scoreBIC(Bnet net){
     
     double score;
     Configuration conf;
     Evidence evi;
     VariableElimination prog;
     
     score = 0.0;
     cases.initializeIterator();
      while(cases.hasNext()) {
                 conf = cases.getNext();
                 evi = new Evidence(conf);
                 prog = new  VariableElimination(net,evi,true); 
                 score += Math.log(prog.computeProbabilityEvidence());
                 
      }
      
//      System.out.println("Likelihood part " + score);
      
 //           System.out.println("Log size " + Math.log(cases.getNumberOfCases()));
// System.out.println("Complexity " + net.computeDimension());
      
 //     System.out.println("Complexity Penalty " + -0.5*Math.log(cases.getNumberOfCases())*net.computeDimension());
   
      if (mscore == 1 )
      {  score += -0.5*Math.log(cases.getNumberOfCases())*net.computeDimension();}
      else 
      
      { score += -net.computeDimension();}
      return score;
 }
 
 
 public void optimizeCases(FiniteStates var) throws  IOException {
     int ncases, ccases,low;
     boolean changes = true;
     boolean incre = false;
     double oldbic, newbic;
     Bnet oldn, newn;
     DELearning param;
     
     System.out.println("Entering optimize cases for variable " + var.getName());
     ncases = var.getNumStates();
     
     ccases = ncases;
     oldn= new Bnet();
     
     try {
     oldn=treecluster.copyBnet();
     } catch (Throwable ex1) {};
     
     oldbic = scoreBIC(oldn);
     
     System.out.println("FIrst BIC " + oldbic);
     
  //   System.out.println("old network nodes " );
  //   System.out.println(oldn.getNodeList());
     
     
     while(changes) {
         System.out.println("Aumentando casos " + ccases);
         changes = false;
         ccases++;
         newn = oldn.splitNode(var);
    //      System.out.println("new network nodes " );
    
    //     System.out.println(newn.getNodeList());
    //     System.out.println(newn.getRelationList());
             param = new DELearning(cases,newn,0.01);
            param.optimizelearningmissing();
            newbic = scoreBIC(newn);
             System.out.println("old BIC " + oldbic);
            System.out.println("New BIC " + newbic);
     
     
         if (newbic>oldbic) {
             changes = true;
             oldn = newn;
             oldbic = newbic;
             incre=true;
             var = (FiniteStates) newn.getNode(var.getName());
         }
         
     }
     if (lowlimit) {low = 2;}
     else {low=1;}
     
     ccases--;
     if (! incre){
          System.out.println("Disminuyendo  casos " + ccases);
         changes=true;
          while(changes && (ccases>low)) {
         changes = false;
         ccases--;
         newn = oldn.jointNode(var);
 //        System.out.println("new network nodes " );
    
 //        System.out.println(newn.getNodeList());
   //      System.out.println(newn.getRelationList());
          param = new DELearning(cases,newn,0.01);
            param.optimizelearningmissing();
            newbic = scoreBIC(newn);
             System.out.println("old BIC " + oldbic);
            System.out.println("New BIC " + newbic);
     
     
         if (newbic>oldbic) {
             changes = true;
             oldn = newn;
             oldbic = newbic;
             var = (FiniteStates) newn.getNode(var.getName());
         }
         
     }
     }
     
     
     treecluster = oldn;
     
     
     
     
 }
   
   
   

 public void save(String dir) throws IOException {

        PrintWriter p;


        System.out.println(dir);

     dir.trim();

       File file = new File(dir);
       System.out.println(file.getAbsolutePath());

	if (!file.exists()) {
            
		if (file.mkdir()) {
			System.out.println("Directory is created!");
		} else {
			System.out.println("Failed to create directory!");
		}
	}

        


        p = new PrintWriter(dir+"/main");
       
      rgraph.saver(0,p);
 if(upgraph!=null) { upgraph.save(0,p);}

        p.close();

         p = new PrintWriter(dir+"/RPT1.elv");
       //    RPT1.save(p);
         RPT1.print();

         RPT2.print();
         p.close();
    }




}




  

  






















































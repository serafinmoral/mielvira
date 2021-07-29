/*
 * Dbc2Intances.java
 *
 * Created on 11 de abril de 2006, 18:10
 */

package elvira2weka;

import weka.core.*;
import elvira.database.DataBaseCases;
import elvira.*;
import java.util.*;
import java.io.*;



/**
 * This class allows to convert a DataBaseCase object to an Instances object (the used class in WEKA as data 
 * storage) and the opposite step, convert an Instance object to a DataBaseCase object.
 * 
 * @author Andres
 */
public class Dbc2Intances {
    
    /** Creates a new instance of Dbc2Intances */
    public Dbc2Intances() {
    }
    

    public static DataBaseCases Instances2Dbc(Instances instances) throws Exception{
        boolean continuous=false;
        for (int i=0; i<instances.numAttributes(); i++){
            Attribute att = instances.attribute(i);
            if (att.isNumeric()){
                continuous=true;
                break;
            }
        }
        
        NodeList nodes=new NodeList();
        for (int i=0; i<instances.numAttributes(); i++){
            Attribute att = instances.attribute(i);
            nodes.insertNode(Attribute2Node(att));
        }
        
        
        if (continuous){
            Continuous cont=new Continuous("temp");
            Vector data=new Vector();
            Enumeration enu=instances.enumerateInstances();
            while (enu.hasMoreElements()) {
                Instance inst = (Instance) enu.nextElement();
                double[] val=new double[inst.numAttributes()];
                for (int i=0; i<val.length ; i++){
                    if (inst.isMissing(i))
                        val[i]=cont.undefValue();
                    else
                        val[i]=inst.value(i);
                }
                data.addElement(val);
            }
           
            ContinuousCaseListMem cases=new ContinuousCaseListMem(nodes);
            cases.setCases(data);
            DataBaseCases dbc=new DataBaseCases(instances.relationName(),cases);
            dbc.setMinMax();
            return dbc;
        }else{
            FiniteStates fn=new FiniteStates("temp");
            Vector data=new Vector();
            Enumeration enu=instances.enumerateInstances();
            while (enu.hasMoreElements()) {
                Instance inst = (Instance) enu.nextElement();
                int[] val=new int[inst.numAttributes()];
                for (int i=0; i<val.length ; i++){
                    if (inst.isMissing(i))
                        val[i]=(int)fn.undefValue();
                    else
                        val[i]=(int)inst.value(i);
                }
                data.addElement(val);
            }
           
            CaseListMem cases=new CaseListMem(nodes);
            cases.setCases(data);
            return new DataBaseCases(instances.relationName(),cases);
        }
    }    
    
    public static Instances Dbc2Intances(DataBaseCases dbc) throws Exception{
        
        NodeList nodes=dbc.getVariables();
        FastVector attinfo=new FastVector(nodes.size());
        for (int i=0; i<nodes.size(); i++)
            attinfo.addElement(Node2Attribute(nodes.elementAt(i)));
        Instances instances=new Instances(dbc.getName(),attinfo,0);
        
        for (int i=0; i< dbc.getNumberOfCases(); i++){
            Instance instance=new Instance(nodes.size());
            for (int j=0; j<nodes.size(); j++){
                double val=dbc.getCaseListMem().getValue(i,j);
                if(nodes.elementAt(j).getClass()==FiniteStates.class){
                    if (val==((FiniteStates)nodes.elementAt(j)).undefValue()){ 
                        instance.setValue(j,instance.missingValue());
                    }else{
                        instance.setValue(j,val);
                    }
                }else{
                    if (val==((Continuous)nodes.elementAt(j)).undefValue()){ 
                        instance.setValue(j,instance.missingValue());
                    }else{
                        instance.setValue(j,val);
                    }
                }
            }
            instance.setDataset(instances);
            instances.add(instance);
        }
        instances.compactify();
        
        return instances;
    }
    
    private static Node Attribute2Node(Attribute att) throws Exception{
        if (att.isNominal()){
            java.util.Vector states=new java.util.Vector();
            for (int i=0; i< att.numValues(); i++){
                states.addElement(att.value(i));
            }
            return new FiniteStates(att.name(),states);
        }else if (att.isNumeric()){
            Continuous node = new Continuous(att.name());
            node.setName(att.name());
            return node;
        }else{
            throw new Exception("Not Nominal or Numeric Attribute: "+att.name());
        }
    }
    
    
    private static Attribute Node2Attribute(Node node) throws Exception{
        
        if (node.getClass()==FiniteStates.class){
            FiniteStates fn=(FiniteStates)node;
            FastVector states = new FastVector(fn.getNumStates());
            for (int i=0; i<fn.getNumStates(); i++)
                states.addElement(fn.getState(i));
            return new Attribute(fn.getName(),states);
        }else if (node.getClass()==Continuous.class){
            return new Attribute(node.getName());
        }else{
            throw new Exception("Not FiniteStates or Continuous Node: "+node.getName());
        }
        
    }

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
        
    }
    
}

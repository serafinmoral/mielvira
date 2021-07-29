package elvira.learning;

import java.util.*;
import elvira.*;
import elvira.database.*;
import elvira.learning.*;
import elvira.potential.*;
import java.io.*;
import elvira.inference.clustering.HuginPropagation;

public class DELearning extends ParameterLearning {

    private double differ = 0.01;

    public DELearning(DataBaseCases input, Bnet net) {

        setInput(input);
        setOutput(net);

    }

    public DELearning(DataBaseCases input, Bnet net, double theres) {

        setInput(input);
        setOutput(net);
        differ = theres;

    }

    public void learning() {

        int i;
        Relation relation;
        PotentialTable potential;
        FiniteStates nodei;
        NodeList vars, varsDb, pa;

        getOutput().getRelationList().removeAllElements();
        for (i = 0; i < getOutput().getNodeList().size(); i++) {
            nodei = (FiniteStates) getOutput().getNodeList().elementAt(i);
            pa = getOutput().parents(nodei);
            vars = new NodeList();
            vars.insertNode(nodei);
            vars.join(pa);
            relation = new Relation(vars.toVector());
            varsDb = getInput().getNodeList().intersectionNames(vars).sortNames(vars);
            potential = getInput().getPotentialTable(varsDb);
            potential.normalize();
            if (vars.size() > 1) {
                nodei = (FiniteStates) varsDb.elementAt(0);
                potential = (PotentialTable) potential.divide(potential.addVariable(nodei));
            }
            potential.setVariables(vars.toVector());
            relation.setValues(potential);
            getOutput().getRelationList().addElement(relation);
            //potential.print();
            //try{System.in.read();}catch(IOException iee){};
        }

    }

    public void learninglaplace(FiniteStates n) {

        int i;
        Relation relation;
        PotentialTable potential;
        FiniteStates nodei;
        NodeList vars, varsDb, pa;

        nodei = n;
        pa = getOutput().parents(nodei);
        vars = new NodeList();
        vars.insertNode(nodei);
        vars.join(pa);
        relation = new Relation(vars.toVector());
        varsDb = getInput().getNodeList().intersectionNames(vars).sortNames(vars);
        potential = getInput().getPotentialTable(varsDb);
        potential.incValue(1.0);
        potential.normalize();
        if (vars.size() > 1) {
            nodei = (FiniteStates) varsDb.elementAt(0);
            potential = (PotentialTable) potential.divide(potential.addVariable(nodei));
        }
        potential.setVariables(vars.toVector());
        relation.setValues(potential);
        getOutput().getRelationList().addElement(relation);
        //potential.print();
        //try{System.in.read();}catch(IOException iee){};

    }

    public void learninglaplace() {

        int i;
        Relation relation;
        PotentialTable potential;
        FiniteStates nodei;
        NodeList vars, varsDb, pa;

        getOutput().getRelationList().removeAllElements();
        for (i = 0; i < getOutput().getNodeList().size(); i++) {
            nodei = (FiniteStates) getOutput().getNodeList().elementAt(i);
            pa = getOutput().parents(nodei);
            vars = new NodeList();
            vars.insertNode(nodei);
            vars.join(pa);
            relation = new Relation(vars.toVector());
            varsDb = getInput().getNodeList().intersectionNames(vars).sortNames(vars);
            potential = getInput().getPotentialTable(varsDb);
            potential.incValue(1.0);
            potential.normalize();
            if (vars.size() > 1) {
                nodei = (FiniteStates) varsDb.elementAt(0);
                potential = (PotentialTable) potential.divide(potential.addVariable(nodei));
            }
            potential.setVariables(vars.toVector());
            relation.setValues(potential);
            getOutput().getRelationList().addElement(relation);
            //potential.print();
            //try{System.in.read();}catch(IOException iee){};
        }

    }

    /* It implements Bayesian parameter learning 
     *
     *
     */
    public void learning(double s) {

        int i;
        Relation relation;
        PotentialTable potential;
        FiniteStates nodei;
        NodeList vars, varsDb, pa;

        getOutput().getRelationList().removeAllElements();
        for (i = 0; i < getOutput().getNodeList().size(); i++) {
            nodei = (FiniteStates) getOutput().getNodeList().elementAt(i);
            pa = getOutput().parents(nodei);
            vars = new NodeList();
            vars.insertNode(nodei);
            vars.join(pa);
            relation = new Relation(vars.toVector());
            varsDb = getInput().getNodeList().intersectionNames(vars).sortNames(vars);
            potential = getInput().getPotentialTable(varsDb);
            potential.incValueTotal(s);
            potential.normalize();
            if (vars.size() > 1) {
                nodei = (FiniteStates) varsDb.elementAt(0);
                potential = (PotentialTable) potential.divide(potential.addVariable(nodei));
            }
            potential.setVariables(vars.toVector());
            relation.setValues(potential);
            getOutput().getRelationList().addElement(relation);
            //potential.print();
            //try{System.in.read();}catch(IOException iee){};
        }

    }

    public void learningmissing() throws IOException {

        NodeList NO;
        NodeList networkN, nodes;
        Node var;
        Vector Relations;
        Potential pot;
        NodeList potvar;
        Vector Current;
        int i, j, k;
        int nrel;
        FiniteStates cnode;
        NodeList parents;
        int ncases;
        int nconf;
        Configuration conf, tconf;
        double total, x, y;

        boolean more;
        int niter;
        PotentialTable potest, auxpot;
        HuginPropagation prog;
        Evidence evi;
        Bnet treecluster;

        treecluster = getOutput();
        networkN = treecluster.getNodeList();

        Current = new Vector();

        Relations = treecluster.getRelationList();
        nrel = Relations.size();

        // Compute Initial Potentials     
        for (i = 0; i < nrel; i++) {
            pot = (((Relation) Relations.elementAt(i)).getValues());
            potvar = (((Relation) Relations.elementAt(i)).getVariables());

            potest = new PotentialTable(potvar);
            Current.add(potest);

            if (potvar.size() > 1) {
                cnode = (FiniteStates) potvar.elementAt(0);
                parents = new NodeList();
                for (j = 1; j < potvar.size(); j++) {
                    parents.insertNode(potvar.elementAt(j));
                }

                ncases = cnode.getNumStates();

                conf = new Configuration(parents);
                nconf = (int) parents.getSize();
                double[] cprob = new double[ncases];

                for (j = 0; j < nconf; j++) {
                    total = 0.0;
                    for (k = 0; k < ncases; k++) {
                        if (Math.abs(k - (j % ncases)) < j / ncases + 1) {
                            cprob[k] = 2.0;
                            total = total + 2;
                        } else {
                            cprob[k] = 1.0;
                            total = total + 1;
                        }

                    }

                    for (k = 0; k < ncases; k++) {
                        cprob[k] = cprob[k] / total;
                        conf.insert(cnode, k);
                        pot.setValue(conf, cprob[k]);
                        conf.remove(cnode);
                    }
                    conf.nextConfiguration();

                }
            }
        }

        niter = 1;

        more = true;
        while (more) {

            System.out.println("Interation " + niter);
            niter++;
            more = false;
            for (i = 0; i < nrel; i++) {
                potest = (PotentialTable) Current.elementAt(i);
                potest.setValue(0.0);

            }
            getInput().initializeIterator();
            while (getInput().hasNext()) {
                conf = getInput().getNext();
                evi = new Evidence(conf);

                prog = new HuginPropagation(treecluster, evi);
                prog.propagate(prog.getJoinTree().elementAt(0), "no");
                for (i = 0; i < nrel; i++) {
                    potest = (PotentialTable) Current.elementAt(i);
                    potvar = (((Relation) Relations.elementAt(i)).getVariables());
                    auxpot = prog.getPosterior(potvar);
                    conf = new Configuration(potvar);
                    nconf = (int) potvar.getSize();
                    for (j = 0; j < nconf; j++) {
                        potest.setValue(conf, potest.getValue(conf) + auxpot.getValue(conf));
                        conf.nextConfiguration();
                    }
                }
            }
            for (i = 0; i < nrel; i++) {
                potest = (PotentialTable) Current.elementAt(i);
                potvar = (((Relation) Relations.elementAt(i)).getVariables());
                potest.incValue(1.0);
                cnode = (FiniteStates) potest.getVariables().elementAt(0);
                parents = new NodeList();
                for (j = 1; j < potest.getVariables().size(); j++) {
                    parents.insertNode(potvar.elementAt(j));
                }
                ncases = cnode.getNumStates();
                conf = new Configuration(parents);
                nconf = (int) parents.getSize();
                for (j = 0; j < nconf; j++) {
                    total = 0.0;
                    for (k = 0; k < ncases; k++) {
                        conf.insert(cnode, k);
                        total += potest.getValue(conf);
                        conf.remove(cnode);
                    }
                    for (k = 0; k < ncases; k++) {
                        conf.insert(cnode, k);
                        potest.setValue(conf, potest.getValue(conf) / total);
                        conf.remove(cnode);
                    }
                    conf.nextConfiguration();
                }

                nconf = (int) potvar.getSize();

                for (j = 0; j < nconf; j++) {
                    x = ((PotentialTable) ((Relation) Relations.elementAt(i)).getValues()).getValue(j);
                    y = potest.getValue(j);
                    if (Math.abs(x - y) > differ) {
                        more = true;
                    }
                    ((PotentialTable) ((Relation) Relations.elementAt(i)).getValues()).setValue(j, y);
                }

            }

        }

    }

    public double learningmix(FiniteStates n, Vector v, NodeList parents) throws InvalidEditException {
        double bic = 0.0;
        int size;
        FiniteStates H;
        int i, niter;
        boolean more;
        PotentialTable[] prob, count;
        PotentialTable hp, hc, pr;
        NodeList totali;
        NodeList pari;
        Configuration conf;
        FiniteStates node;
        int nconf, j;
        double x, y;
        NodeList vars;
        Relation relation;
        ProbabilityTree t;
        PotentialTree pt;

        size = v.size();

        H = new FiniteStates(size);
        H.setName(n.getName()+"aux");

        getOutput().addNode(H);

        getOutput().addLink(H, n);
        prob = new PotentialTable[size];
        count = new PotentialTable[size];

        for (i = 0; i < size; i++) {
            pari = (NodeList) v.elementAt(i);
            totali = new NodeList();
            totali.insertNode(n);
            totali.join(pari);
            prob[i] = getInput().getPotentialTable(totali); 
            prob[i].incValue(1.0);
            prob[i].normalize();
            node = (FiniteStates) prob[i].getVariables().elementAt(0);
            prob[i] = (PotentialTable) prob[i].divide(prob[i].addVariable(node));
            count[i] = new PotentialTable(totali);
        }

        hp = new PotentialTable(size);
        hc = new PotentialTable(size);
        pr = new PotentialTable(size);

        hp.setValue(1.0 / size);

        niter = 1;

        more = true;
        while (more) {

            more = false;
            bic = 0.0;
            System.out.println("Iteration " + niter);
            niter++;
            more = false;
            for (i = 0; i < size; i++) {
                count[i].setValue(0.0);
            }
            hc.setValue(1.0);
           
            getInput().initializeIterator();
            while (getInput().hasNext()) {
                pr.setValue(1.0);
                conf = getInput().getNext();
                for (i = 0; i < size; i++) {
                    pr.setValue(i, pr.getValue(i)*prob[i].restrictVariable(conf).totalPotential());
                }
                pr.combine(hp);
                bic += pr.normalizeand();
                for (i = 0; i < size; i++) {
                    count[i].setValue(conf, count[i].getValue(conf) + pr.getValue(i));
                    hc.setValue(i, hc.getValue(i)+ pr.getValue(i));
                }

            }
            for (i = 0; i < size; i++) {
                count[i].incValue(1.0);
                count[i].normalize();
                if (count[i].getVariables().size() > 1) {
                    node = (FiniteStates) count[i].getVariables().elementAt(0);
                    count[i] = (PotentialTable) count[i].divide(count[i].addVariable(node));
                }
                nconf = (int) count[i].getSize();

                for (j = 0; j < nconf; j++) {
                    x = count[i].getValue(j);
                    y = prob[i].getValue(j);
                    prob[i].setValue(j, x);
                    if (Math.abs(x - y) > differ) {
     //                   System.out.println("Diferencia " + i  + "j= " + j + "x= " + x + "y= " + y);
                        more = true;
                    }

                }
            }

            hc.normalize();
            nconf = (int) hc.getSize();

            for (j = 0; j < nconf; j++) {
                x = hc.getValue(j);
                y = hp.getValue(j);
                 hp.setValue(j, x);
                if (Math.abs(x - y) > differ) {
                    more = true;
         //            System.out.println("Diferencia " + j + "x= " + x + "y= " + y);
                }

            }

        }

        vars = new NodeList();
        vars.insertNode(n);
        vars.insertNode(H);
        vars.join(parents);
        relation = new Relation();
        relation.setVariables(vars);
        t = new ProbabilityTree(H);
        for (i = 0; i < size; i++) {
            t.setNewChild(prob[i].toTree().getTree(), i);
        }
        pt = new PotentialTree(t, vars.toVector());
        relation.setValues(pt);

        
        getOutput().removeRelation(n);
        getOutput().getRelationList().addElement(relation);

        vars = new NodeList();
        vars.insertNode(H);
        relation = new Relation(vars.toVector());
        relation.setValues(hp);
        getOutput().removeRelation(H);
        getOutput().getRelationList().addElement(relation);

        x = -0.5 * Math.log(getInput().getNumberOfCases());
        y = size - 1;
        for (i = 0; i < size; i++) {
            y += ((NodeList) v.elementAt(i)).getSize();
        }
        bic += x * y;

        return bic;

    }
    
    
    public double learningmixnoem(FiniteStates n, Vector v, NodeList parents) throws InvalidEditException {
        double bic = 0.0;
        int size;
        FiniteStates H;
        int i, niter;
        boolean more;
        PotentialTable[] prob, count;
        PotentialTable hp, hc, pr;
        NodeList totali;
        NodeList pari;
        Configuration conf;
        FiniteStates node;
        int nconf, j;
        double x, y;
        NodeList vars;
        Relation relation;
        ProbabilityTree t;
        PotentialTree pt;

        size = v.size();

        H = new FiniteStates(size);
        H.setName(n.getName()+"aux");

        getOutput().addNode(H);

        getOutput().addLink(H, n);
        prob = new PotentialTable[size];
        count = new PotentialTable[size];

        for (i = 0; i < size; i++) {
            pari = (NodeList) v.elementAt(i);
            totali = new NodeList();
            totali.insertNode(n);
            totali.join(pari);
            prob[i] = getInput().getPotentialTable(totali);
            prob[i].incValue(1.0);
            prob[i].normalize();
            node = (FiniteStates) prob[i].getVariables().elementAt(0);
            prob[i] = (PotentialTable) prob[i].divide(prob[i].addVariable(node));
           
        }

        hp = new PotentialTable(size);
        

        hp.setValue(1.0 / size);


       

        vars = new NodeList();
        vars.insertNode(n);
        vars.insertNode(H);
        vars.join(parents);
        relation = new Relation();
        relation.setVariables(vars);
        t = new ProbabilityTree(H);
        for (i = 0; i < size; i++) {
            t.setNewChild(prob[i].toTree().getTree(), i);
        }
        pt = new PotentialTree(t, vars.toVector());
        relation.setValues(pt);

        
        getOutput().removeRelation(n);
        getOutput().getRelationList().addElement(relation);

        vars = new NodeList();
        vars.insertNode(H);
        relation = new Relation(vars.toVector());
        relation.setValues(hp);
        getOutput().removeRelation(H);
        getOutput().getRelationList().addElement(relation);

        x = -0.5 * Math.log(getInput().getNumberOfCases());
        y = size - 1;
        for (i = 0; i < size; i++) {
            y += ((NodeList) v.elementAt(i)).getSize();
        }
        bic += x * y;

        return bic;

    }


    public void learningmix(int maxparents, int procedure) throws InvalidEditException {

        int i,initialnodes;
        FiniteStates nodei;
        NodeList pa;
        Vector v;

        getOutput().getRelationList().removeAllElements();
        initialnodes = getOutput().getNodeList().size();
        for (i = 0; i < initialnodes; i++) {

            nodei = (FiniteStates) getOutput().getNodeList().elementAt(i);
            pa = getOutput().parents(nodei);
            if (pa.size() <= maxparents) {
                learninglaplace(nodei);
            } else {
                v = new Vector();
                switch (procedure) {
                    case 0: {
                        computerandomList(nodei, v, pa, maxparents, pa.size());
                        learningmix(nodei, v, pa);
                        break;
                    }
                     case 1: {
                        computeGoodList(nodei, v, pa, maxparents, pa.size());
                        learningmix(nodei, v, pa);
                        break;
                    }
                     case 2: {
                        computeGoodList(nodei, v, pa, maxparents, pa.size());
                        learningmixnoem(nodei, v, pa);
                        break;
                    }
                }
            }

        }
    }

    private void computeGoodList(FiniteStates nodei, Vector v, NodeList pa, int size, int n) {
        int i,j,imax,np;
         NodeList nodes,nwithc;
         NodeList candidates;
         double scoremax,x;
         Node cand;
         BDeMetrics t;
         
         t = new BDeMetrics(getInput(),2.0);
         
        for (i = 0; i < n; i++) {
            nwithc = new NodeList();
            nwithc.insertNode(nodei);
           nodes =    new NodeList();
            nodes.insertNode(pa.elementAt(i));
     //        System.out.println("New List: Adding Node " + pa.elementAt(i).getName());
            nwithc.insertNode(pa.elementAt(i));
            candidates = new NodeList();
            np = 1;
            for(j=0;j<pa.size();j++) {
                if(i!=j) {
                    
                    candidates.insertNode(pa.elementAt(j));
                }
            }
            
            
            while ((candidates.size()>0) && (np<size)) {
                cand = candidates.elementAt(0);
                imax = 0;
                nwithc.insertNode(cand);
                x = t.scoret(nwithc);
                nwithc.removeNode(cand);
                scoremax = x;
                
                for(j=1;j<candidates.size(); j++) {
                    cand = candidates.elementAt(j);
                      nwithc.insertNode(cand);
                x = t.scoret(nwithc);
                nwithc.removeNode(cand);
                if (x>scoremax) {
                     scoremax = x;
                     imax = j;
                }
                }
                
                cand = candidates.elementAt(imax);
         //        System.out.println("Adding Node " + cand.getName());
                candidates.removeNode(imax);
                nodes.insertNode(cand);
                 nwithc.insertNode(cand);
                np++;
                
                
            }
            
             v.add(nodes);
            
            
        }
        
        
        
    }
    
    private void computerandomList(FiniteStates nodei, Vector v, NodeList pa, int size, int n) {

        int[] freq;
        boolean[] posible, selected;
        int i, j, k, min,l;
        
        NodeList nodes;
        double npos,x;
        double sum;

        freq = new int[pa.size()];
        posible = new boolean[pa.size()];
        selected = new boolean[pa.size()];

        for (i = 0; i < n; i++) {
        //     System.out.println("Conjunto "+i);
            npos = 0.0;
            for (j = 1; j < pa.size(); j++) {
                selected[j] = false;
            }
            nodes = new NodeList();
            j=0;
            while (j < size) {
                if (npos == 0.0) {

                    
                    min = Integer.MAX_VALUE;
                    for (k = 0; k < pa.size(); k++) {
                        if ((freq[k] < min)&&(!selected[k])) {
                            min = freq[k];
                        }
                    }

                    for (k = 0; k < pa.size(); k++) {
                        if ((freq[k] == min)&&(!selected[k])) {
                            npos += 1.0;
                            posible[k] = true;
                        } else {
                            posible[k] = false;
                        }
                    }
                }
             x = Math.random();
             sum = 1.0/npos;
             l = 0;
             while(!posible[l]||selected[l]) {l++;}
             while(x>sum) {
                 sum+= 1.0/npos;
                 l++;
                 while(!posible[l]||selected[l]) {l++;}
             }
             selected[l] = true;
             npos--;
             freq[l]++;
       //      System.out.println("Eligiendo nodo " + pa.elementAt(l).getName());
             nodes.insertNode(pa.elementAt(l));
             
             j++;
             
                
            }

            v.add(nodes);
        }

    }

    public void optimizelearningmissing() throws IOException {

        NodeList NO;
        NodeList networkN, nodes;
        Node var;
        Vector Relations;
        Potential pot;
        NodeList potvar;
        Vector Current;
        int i, j, k;
        int nrel;
        FiniteStates cnode;
        NodeList parents;
        int ncases;
        int nconf;
        Configuration conf, tconf;
        double total, x, y;

        boolean more;
        int niter;
        PotentialTable potest, auxpot;
        HuginPropagation prog;
        Evidence evi;
        Bnet treecluster;

        treecluster = getOutput();
        networkN = treecluster.getNodeList();

        Current = new Vector();

        Relations = treecluster.getRelationList();
        nrel = Relations.size();

        // Compute Initial Potentials     
        for (i = 0; i < nrel; i++) {
            pot = (((Relation) Relations.elementAt(i)).getValues());
            potvar = (((Relation) Relations.elementAt(i)).getVariables());

            potest = new PotentialTable(potvar);
            Current.add(potest);
        }

        niter = 1;

        more = true;
        while (more) {

            System.out.println("Interation " + niter);
            niter++;
            more = false;
            for (i = 0; i < nrel; i++) {
                potest = (PotentialTable) Current.elementAt(i);
                potest.setValue(0.0);

            }
            getInput().initializeIterator();
            while (getInput().hasNext()) {
                conf = getInput().getNext();
                evi = new Evidence(conf);

                prog = new HuginPropagation(treecluster, evi);
                prog.propagate(prog.getJoinTree().elementAt(0), "no");
                for (i = 0; i < nrel; i++) {
                    potest = (PotentialTable) Current.elementAt(i);
                    potvar = (((Relation) Relations.elementAt(i)).getVariables());
                    auxpot = prog.getPosterior(potvar);
                    conf = new Configuration(potvar);
                    nconf = (int) potvar.getSize();
                    for (j = 0; j < nconf; j++) {
                        potest.setValue(conf, potest.getValue(conf) + auxpot.getValue(conf));
                        conf.nextConfiguration();
                    }
                }
            }
            for (i = 0; i < nrel; i++) {
                potest = (PotentialTable) Current.elementAt(i);
                potvar = (((Relation) Relations.elementAt(i)).getVariables());
                potest.incValue(1.0);
                cnode = (FiniteStates) potest.getVariables().elementAt(0);
                parents = new NodeList();
                for (j = 1; j < potest.getVariables().size(); j++) {
                    parents.insertNode(potvar.elementAt(j));
                }
                ncases = cnode.getNumStates();
                conf = new Configuration(parents);
                nconf = (int) parents.getSize();
                for (j = 0; j < nconf; j++) {
                    total = 0.0;
                    for (k = 0; k < ncases; k++) {
                        conf.insert(cnode, k);
                        total += potest.getValue(conf);
                        conf.remove(cnode);
                    }
                    for (k = 0; k < ncases; k++) {
                        conf.insert(cnode, k);
                        potest.setValue(conf, potest.getValue(conf) / total);
                        conf.remove(cnode);
                    }
                    conf.nextConfiguration();
                }

                nconf = (int) potvar.getSize();

                for (j = 0; j < nconf; j++) {
                    x = ((PotentialTable) ((Relation) Relations.elementAt(i)).getValues()).getValue(j);
                    y = potest.getValue(j);
                    if (Math.abs(x - y) > differ) {
                        more = true;
                    }
                    ((PotentialTable) ((Relation) Relations.elementAt(i)).getValues()).setValue(j, y);
                }

            }

        }

    }

    public void learningmissing(FiniteStates ref) throws IOException {

        NodeList NO;
        NodeList networkN, nodes;
        Node var;
        Vector Relations;
        Potential pot;
        NodeList potvar;
        Vector Current;
        int i, j, k;
        int nrel;
        FiniteStates cnode;
        NodeList parents;
        int ncases;
        int nconf;
        Configuration conf, tconf;
        double total, x, y;

        boolean more;
        int niter;
        PotentialTable potest, auxpot;
        HuginPropagation prog;
        Evidence evi;
        Bnet treecluster;

        treecluster = getOutput();
        networkN = treecluster.getNodeList();

        Current = new Vector();

        Relations = treecluster.getRelationList();
        nrel = Relations.size();

        // Compute Initial Potentials     
        for (i = 0; i < nrel; i++) {
            pot = (((Relation) Relations.elementAt(i)).getValues());
            potvar = (((Relation) Relations.elementAt(i)).getVariables());

            potest = new PotentialTable(potvar);
            Current.add(potest);

            if (potvar.size() > 1) {
                cnode = (FiniteStates) potvar.elementAt(0);
                parents = new NodeList();
                for (j = 1; j < potvar.size(); j++) {
                    parents.insertNode(potvar.elementAt(j));
                }

                ncases = cnode.getNumStates();

                if ((parents.size() == 1) && (((FiniteStates) parents.elementAt(0)).getNumStates() == ref.getNumStates())) {
                    FiniteStates tail = (FiniteStates) parents.elementAt(0);
                    if (ref.equals(cnode)) {

                        for (k = 0; k < ncases; k++) {
                            conf = new Configuration();
                            conf.insert(tail, k);
                            for (j = 0; j < ncases; j++) {
                                conf.insert(cnode, j);

                                if (j == k) {
                                    pot.setValue(conf, 2 / (ncases + 1.0));
                                } else {
                                    pot.setValue(conf, 1 / (ncases + 1.0));
                                }
                                conf.remove(cnode);
                            }

                        }

                    } else {
                        double[][] cprob = getInput().getFreq(ref, cnode);
                        for (k = 0; k < tail.getNumStates(); k++) {
                            conf = new Configuration();
                            conf.insert(tail, k);
                            x = 0.0;
                            for (j = 0; j < ncases; j++) {
                                x += cprob[k][j];
                            }

                            for (j = 0; j < ncases; j++) {
                                conf.insert(cnode, j);

                                pot.setValue(conf, (cprob[k][j] + 1.0) / (x + ncases));

                                conf.remove(cnode);
                            }

                        }

                    }

                } else {
                    conf = new Configuration(parents);
                    nconf = (int) parents.getSize();
                    double[] cprob = new double[ncases];

                    for (j = 0; j < nconf; j++) {
                        total = 0.0;
                        for (k = 0; k < ncases; k++) {
                            if (Math.abs(k - (j % ncases)) < j / ncases + 1) {
                                cprob[k] = 2.0;
                                total = total + 2;
                            } else {
                                cprob[k] = 1.0;
                                total = total + 1;
                            }

                        }

                        for (k = 0; k < ncases; k++) {
                            cprob[k] = cprob[k] / total;
                            conf.insert(cnode, k);
                            pot.setValue(conf, cprob[k]);
                            conf.remove(cnode);
                        }
                        conf.nextConfiguration();

                    }
                }
            }
        }

        niter = 1;

        more = true;
        while (more) {

            System.out.println("Interation " + niter);
            niter++;
            more = false;
            for (i = 0; i < nrel; i++) {
                potest = (PotentialTable) Current.elementAt(i);
                potest.setValue(0.0);

            }
            getInput().initializeIterator();
            while (getInput().hasNext()) {
                conf = getInput().getNext();
                evi = new Evidence(conf);

                prog = new HuginPropagation(treecluster, evi);
                prog.propagate(prog.getJoinTree().elementAt(0), "no");
                for (i = 0; i < nrel; i++) {
                    potest = (PotentialTable) Current.elementAt(i);
                    potvar = (((Relation) Relations.elementAt(i)).getVariables());
                    auxpot = prog.getPosterior(potvar);
                    conf = new Configuration(potvar);
                    nconf = (int) potvar.getSize();
                    for (j = 0; j < nconf; j++) {
                        potest.setValue(conf, potest.getValue(conf) + auxpot.getValue(conf));
                        conf.nextConfiguration();
                    }
                }
            }
            for (i = 0; i < nrel; i++) {
                potest = (PotentialTable) Current.elementAt(i);
                potvar = (((Relation) Relations.elementAt(i)).getVariables());
                potest.incValue(1.0);
                cnode = (FiniteStates) potest.getVariables().elementAt(0);
                parents = new NodeList();
                for (j = 1; j < potest.getVariables().size(); j++) {
                    parents.insertNode(potvar.elementAt(j));
                }
                ncases = cnode.getNumStates();
                conf = new Configuration(parents);
                nconf = (int) parents.getSize();
                for (j = 0; j < nconf; j++) {
                    total = 0.0;
                    for (k = 0; k < ncases; k++) {
                        conf.insert(cnode, k);
                        total += potest.getValue(conf);
                        conf.remove(cnode);
                    }
                    for (k = 0; k < ncases; k++) {
                        conf.insert(cnode, k);
                        potest.setValue(conf, potest.getValue(conf) / total);
                        conf.remove(cnode);
                    }
                    conf.nextConfiguration();
                }

                nconf = (int) potvar.getSize();

                for (j = 0; j < nconf; j++) {
                    x = ((PotentialTable) ((Relation) Relations.elementAt(i)).getValues()).getValue(j);
                    y = potest.getValue(j);
                    if (Math.abs(x - y) > differ) {
                        more = true;
                    }
                    ((PotentialTable) ((Relation) Relations.elementAt(i)).getValues()).setValue(j, y);
                }

            }

        }

    }

}

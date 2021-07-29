/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package elvira.learning;

import elvira.NodeList;
import elvira.Bnet;
import elvira.Node;
import elvira.Continuous;
import elvira.FiniteStates;
import elvira.Configuration;
import elvira.ContinuousDiscrete;
import elvira.CaseListMem;
import elvira.LinkList;
import elvira.DynamicNetwork;
import elvira.database.DataBaseCases;
import elvira.parser.ParseException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import elvira.potential.PotentialTable;
import elvira.potential.Potential;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.*;
import java.util.Enumeration;
import java.util.Arrays;
import java.awt.Frame;
import org.rosuda.REngine.*;

import org.rosuda.REngine.Rserve.*;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.RMainLoopCallbacks;

/**
 *
 * @author smc
 */
public class DynamicLearning {

    DynamicNetwork Output;
    NodeList nodes;
    Vector sequences;
    Vector rawdata;
    int nnodes;
    Metrics m;
    boolean convar;
    int[] vartype;
    int score = 0;
    int method = 1;
    int artvar = 0;
    int initialnnodes;

    Vector cutpoints;
    Vector indexcuts;

    boolean nonarti[];

    Vector linearcoefficients;
    Vector indexvariables;

    int[] indexconvar;

    int nsequences;

    int nobser;

    public static void main(String[] args) throws IOException {

        DynamicLearning.Experiment(args[0], args[1], args[2]);

        System.out.println("Main final");

        System.exit(0);

        // just making sure we have the right version of everything
    }

    /* Read the data with the structure of the problem 
     @dirname: The name of the directory with the problem data
     */
    DynamicLearning(String dirname, String filename, int sc, int use) throws IOException {

        this(dirname, getFirst(dirname, filename), getSecond(dirname, filename), sc, use);

    }

    /* Read the data with the structure of the problem 
     @dirname: The name of the directory with the problem data
     */
    public DynamicLearning(String dirname, String variablesfile, String[] flights, int sc, int use) throws IOException {

        String line;
        BufferedReader nodesreader, casesreader;
        CaseListMem cases;
        Vector rdata;
        double[] rrow;
        int[] crow;
        String[] parts;
        int i, j, k, l;
        Node x;
        int total;
        double cuts[];
        double[] cutvar;
        double former, fcut;
        int nvalid;
        NodeList vnodes;

        Continuous nod;

        convar = false;
        score = sc;

        switch (score) {
            case 0:
                m = new BICMetrics();
                break;
            case 1:
                m = new AkaikeMetrics();
                break;
            case 2:
                m = new K2Metrics();
                break;
            case 3:
                m = new BDeMetrics();
                break;
            case 4:
                m = new BDeMetrics();
                break;
        }

        sequences = new Vector();
        rawdata = new Vector();
        cutpoints = new Vector();
        indexcuts = new Vector();

        indexvariables = new Vector();

        linearcoefficients = new Vector();

        line = variablesfile; //name of variables file

        nodesreader = new BufferedReader(new FileReader(dirname + "/" + line));

        nodes = new NodeList(nodesreader);

        nodesreader.close();

        nonarti = new boolean[nodes.size()];
        nnodes = nodes.size();
        initialnnodes = nnodes;

        nvalid = 36;

        for (i = 0; i < 71; i++) {
            nonarti[i] = true;
        }
        for (i = 71; i < 83; i++) {
            nonarti[i] = false;
        }

        for (i = 83; i < 95; i++) {
            nonarti[i] = true;
        }
        for (i = 95; i < 119; i++) {
            nonarti[i] = false;
        }

        vnodes = new NodeList();

        if (use == 2) {

            for (i = 0; i < nnodes; i++) {
                if (nonarti[i]) {
                    vnodes.insertNode(nodes.elementAt(i));
                }
            }

            nnodes -= nvalid;
            nodes = vnodes;

        } else {
            vnodes = nodes;
        }

        vartype = new int[nnodes * 3];

        indexconvar = new int[nnodes];

        for (i = 0; i < nnodes; i++) {
            vartype[i] = nodes.elementAt(i).getTypeOfVariable();
            if (vartype[i] == Node.CONTINUOUS) {
                convar = true;
            }
        }

        nsequences = 0;

        total = 0;
        for (j = 0; j < flights.length; j++) {
            line = flights[j];
          //  System.out.println("Secuencia " + line);
            cases = new CaseListMem(vnodes);
            rdata = new Vector();
            casesreader = new BufferedReader(new FileReader(dirname + "/" + line));
            line = casesreader.readLine();

            while ((line = casesreader.readLine()) != null) {
                total++;
                //                System.out.println("Reading line " + line);
                rrow = new double[nnodes];
                crow = new int[nnodes];
                parts = line.split(",");
                i = 0;
                l = 0;
                while (i < nnodes) {
                    if (use == 2) {
                        while (!nonarti[l]) {
                            l++;
                        }
                    }
                    x = nodes.elementAt(i);
                    if (x.getTypeOfVariable() == Node.FINITE_STATES) {
                        crow[i] = ((FiniteStates) x).getId(parts[l].trim());
                        rrow[i] = crow[i];

                    } else {
                        if (x.getTypeOfVariable() == Node.MIXED) {

                            rrow[i] = Double.parseDouble(parts[l]);
                            //  System.out.println(rrow[i]);
                            crow[i] = ((ContinuousDiscrete) x).getCase(rrow[i]);

                            //      System.out.println(x.getName() + " " + crow[i]);
                        } else {
                            if (x.getTypeOfVariable() == Node.CONTINUOUS) {
                                rrow[i] = Double.parseDouble(parts[l]);
                                //    System.out.println(rrow[i]);
                                crow[i] = 0;
                            }
                        }
                    }
                    i++;
                    l++;
                }
                cases.addCase(crow);
                rdata.add(rrow);

            }

            casesreader.close();
            sequences.add(cases);
            rawdata.add(rdata);
            nsequences++;

        }

        nobser = total;

        for (i = 0; i < nnodes; i++) {

            if (vartype[i] == Node.CONTINUOUS) {

                nod = ((Continuous) nodes.elementAt(i));
                l = 2;
                cuts = new double[total + 2];
                cuts[0] = nod.getMin();
                cuts[1] = nod.getMax();
                for (j = 0; j < nsequences; j++) {
                    //                    System.out.println("Sequence " + j);
                    rdata = (Vector) rawdata.elementAt(j);

                    for (k = 0; k < rdata.size(); k++) {
                        rrow = (double[]) rdata.elementAt(k);
                        //                  System.out.println("data " + l + "value " + rrow[i]);
                        cuts[l] = rrow[i];
                        l++;
                    }
                }

                Arrays.sort(cuts);
                {
                    former = ((Continuous) nodes.elementAt(i)).getMin();
                }

                cutvar = new double[total + 1];
                fcut = ((Continuous) nodes.elementAt(i)).getMin();

                for (j = 1; j <= total + 1; j++) {
                    if (former != cuts[j]) {
                        cutvar[j - 1] = (cuts[j] + former) / 2.0;
                        former = cuts[j];
                        fcut = cutvar[j - 1];
                    } else {
                        cutvar[j - 1] = fcut;
                    }
                }
                cutvar[0] = ((Continuous) nodes.elementAt(i)).getMin();
                cutvar[total] = ((Continuous) nodes.elementAt(i)).getMax();

                //   System.out.println(cutvar[k]);
            } else {
                cutvar = null;
            }

            cutpoints.insertElementAt(cutvar, i);
            indexcuts.insertElementAt(null, i);

        }

    }

    public NodeList getNodes() {
        return nodes;
    }

    static String getFirst(String dir, String file) throws IOException {
        String line;

        BufferedReader reader = new BufferedReader(new FileReader(dir + "/" + file));

        line = reader.readLine();
        reader.close();

        return line;

    }

    static String[] getSecond(String dir, String file) throws IOException {

        String line;
        String[] names;
        BufferedReader reader = new BufferedReader(new FileReader(dir + "/" + file));

        line = reader.readLine();
        line = reader.readLine();

        names = line.split("\\s*,\\s*");

        reader.close();
        return names;
    }

    public double learning() {

        double x;
        int i;

        //   System.out.println("Empiezo aprender");
        x = 0.0;

        Output = new DynamicNetwork(nodes);
        Output.vartypes = vartype;
        if (!convar) {

            for (i = 0; i < nnodes; i++) {
                x += learning(i);

            }
        } else {
            if (method == 0) {
                x = learningd5();
            } else {
                x = learningd6();
            }
        }

        return x;
    }

    public double learning(NodeList nl) {

        double x;
        int i;

        //   System.out.println("Empiezo aprender");
        x = 0.0;

        Output = new DynamicNetwork(nodes);
        Output.vartypes = vartype;
        if (!convar) {

            for (i = 0; i < nnodes; i++) {
                x += learning(i);

            }
        } else {
            if (method == 0) {
                x = learningd5();
            } else {
                x = learningd6(nl);
            }
        }

        return x;
    }

    public double learningd6() {
        double x;
        int i;

        x = 0.0;
        String[] Rargs = {"--vanilla"};
        Rengine re = Rengine.getMainEngine();
        if (re == null) {
            re = new Rengine(Rargs, false, null);
        }

        for (i = 0; i < nnodes; i++) {
          //  System.out.println("Learning Node " + i);
            if (vartype[i] == Node.CONTINUOUS) {
                x += learningcont(i, re);

            }
        }
     //   System.out.println("Añadiendo datos extra");
        addartificialdata();
     //   System.out.println("Entro en calcular puntos de corte");

        computecutpointsart();

        nnodes += artvar;
  //      System.out.println("Discretizacion");

        discretize();

        Output = new DynamicNetwork(nodes);

        Output.vartypes = vartype;

   //     System.out.println("Aprendiendo la estructura");

        x = learningfix2();

        return x;
    }

    public double learningd6(NodeList nl) {
        double x;
        int i;
        Node ni;

        x = 0.0;
        String[] Rargs = {"--vanilla"};
        Rengine re = Rengine.getMainEngine();
        if (re == null) {
            re = new Rengine(Rargs, false, null);
        }

        for (i = 0; i < initialnnodes; i++) {
         //   System.out.println("Learning Node " + i);
            if (vartype[i] == Node.CONTINUOUS) {
                ni = nodes.elementAt(i);
                if (nl.getId(ni) == -1) {
                    x += learningcont(i, re);
                } else {
                    x += learningcont2(i, re);
                }

            }
        }
      //  System.out.println("Añadiendo datos extra");
        addartificialdata();
      //  System.out.println("Entro en calcular puntos de corte");

        computecutpointsart();

        nnodes += artvar;
    //    System.out.println("Discretizacion");

        discretize();

        Output = new DynamicNetwork(nodes);

        Output.vartypes = vartype;

   //     System.out.println("Aprendiendo la estructura");

        x = learningfix2(nl);

        return x;
    }

    private void computecutpointsart() {
        int i, j, k, l;
        Continuous nod;
        double[] cuts, rrow, cutvar;
        int total;
        Vector rdata;
        double fcut, former;
        double vmin, vmax;

        total = nobser;

        for (i = nnodes; i < nnodes + artvar; i++) {

            vmin = 1e23;
            vmax = -1e23;

            if ((vartype[i] == 4) || (vartype[i] == 6)) {

                nod = ((Continuous) nodes.elementAt(i));
                l = 2;
                cuts = new double[total + 2];

                for (j = 0; j < nsequences; j++) {
                  //  System.out.println("Sequence " + j);
                    rdata = (Vector) rawdata.elementAt(j);

                    for (k = 0; k < rdata.size(); k++) {
                        rrow = (double[]) rdata.elementAt(k);
                        if (rrow[i] < vmin) {
                            vmin = rrow[i];
                        }
                        if (rrow[i] > vmax) {
                            vmax = rrow[i];
                        }
                        //                  System.out.println("data " + l + "value " + rrow[i]);
                        cuts[l] = rrow[i];
                        l++;
                    }
                }

                cuts[0] = vmin - 1;
                cuts[1] = vmax + 1;

                ((Continuous) nodes.elementAt(i)).setMin(vmin - 1.0);
                ((Continuous) nodes.elementAt(i)).setMax(vmax + 1.0);

                Arrays.sort(cuts);
                {
                    former = ((Continuous) nodes.elementAt(i)).getMin();
                }

                cutvar = new double[total + 1];
                fcut = ((Continuous) nodes.elementAt(i)).getMin();

                for (j = 1; j <= total + 1; j++) {
                    if (former != cuts[j]) {
                        cutvar[j - 1] = (cuts[j] + former) / 2.0;
                        former = cuts[j];
                        fcut = cutvar[j - 1];
                    } else {
                        cutvar[j - 1] = fcut;
                    }
                }
                cutvar[0] = ((Continuous) nodes.elementAt(i)).getMin();
                cutvar[total] = ((Continuous) nodes.elementAt(i)).getMax();

                //   System.out.println(cutvar[k]);
            } else {
                cutvar = null;
            }

            cutpoints.insertElementAt(cutvar, i);
            indexcuts.insertElementAt(null, i);

        }

    }

    /**
     * Creates and assigns a matrix object in R from 2D table of double
     *
     * @param rEngine the R instance used
     * @param sourceArray the 2D table of double the matrix must have always the
     * same column number on every row
     * @param nameToAssignOn the R object name
     * @return R matrix instance or null if R return an error
     */
    public static REXP assignAsRMatrix(Rengine rEngine, double[][] sourceArray, String nameToAssignOn) {
        if (sourceArray.length == 0) {
            return null;
        }

        rEngine.assign(nameToAssignOn, sourceArray[0]);
        REXP resultMatrix = rEngine.eval(nameToAssignOn + " <- matrix( " + nameToAssignOn + " ,nr=1)");
        for (int i = 1; i < sourceArray.length; i++) {
            rEngine.assign("temp", sourceArray[i]);
            resultMatrix = rEngine.eval(nameToAssignOn + " <- rbind(" + nameToAssignOn + ",matrix(temp,nr=1))");
        }

        return resultMatrix;
    }

    public double learningcont(int i, Rengine re) {
        double x = 0.0;
        String[] args;
        double[][] matr;
        double[] resp;
        int j, l;
        int nca;
        int nvarmodel;
        int[] h;
        double[] t;
        String[] names;

        nca = 0;

        for (l = 0; l < nsequences; l++) {
            nca += ((CaseListMem) sequences.elementAt(l)).getNumberOfCases() - 1;
        }

    //    System.out.println(nca);
        matr = new double[nca][nnodes + 1];
        resp = new double[nca];

        REXP y;
        re.eval("library(bestglm)");

   //     System.out.println("Cargada libreria");

        preparevectors2(i, matr);
        assignAsRMatrix(re, matr, "data");
        //  re.assign("resp",resp);
        //      y=re.eval("l=c(1000,100,50.0,10.0,2.0,1.0,0.001)");

        y = re.eval("a<-bestglm(as.data.frame(data),nvmax=3,IC=\"BICg\",t=1.0)");
      //  System.out.println(y);
        //     System.out.println(re.eval("names(as.data.frame(data))"));

    //    System.out.println(re.eval("a$BestModel$coefficients"));
    //    System.out.println(re.eval("names(a$BestModel$coefficients)"));
    //    System.out.println(re.eval("length(a$BestModel$coefficients)"));

        nvarmodel = re.eval("length(a$BestModel$coefficients)").asInt() - 1;

      //  System.out.println(nvarmodel);

        Continuous lin = new Continuous();
        Continuous err = new ContinuousDiscrete();

        lin.setName(nodes.elementAt(i).getName() + "linear");
        err.setName(nodes.elementAt(i).getName() + "error");

        nodes.insertNode(lin);
        nodes.insertNode(err);

        vartype[nnodes + artvar] = 4;
        vartype[nnodes + artvar + 1] = 6;

        vartype[i] = 5;

        indexconvar[artvar / 2] = i;

        h = new int[2];

        h[0] = nnodes + artvar;
        h[1] = nnodes + artvar + 1;
        artvar = artvar + 2;

        indexvariables.add(h);

        h = new int[nvarmodel + 1];
        names = re.eval("names(a$BestModel$coefficients)").asStringArray();

        t = re.eval("a$BestModel$coefficients").asDoubleArray();

        for (j = 0; j < nvarmodel; j++) {

            h[j + 1] = Integer.parseInt(names[j + 1].substring(1)) - 1;

        }
        indexvariables.add(h);
        linearcoefficients.add(t);

        return x;
    }

    public double learningcont2(int i, Rengine re) {
        double x = 0.0;
        String[] args;
        double[][] matr;
        double[] resp;
        int j, l;
        int nca;
        int nvarmodel;
        int[] h;
        double[] t;
        String[] names;

        nca = 0;

        for (l = 0; l < nsequences; l++) {
            nca += ((CaseListMem) sequences.elementAt(l)).getNumberOfCases() - 1;
        }

      //  System.out.println(nca);
        matr = new double[nca][nnodes + 1];
        resp = new double[nca];

       // REXP y;
        //   re.eval("library(bestglm)");
      //  System.out.println("Learning New SA Variable");

     //   preparevectors2(i, matr);
        //   assignAsRMatrix(re, matr, "data");
        //  re.assign("resp",resp);
        //      y=re.eval("l=c(1000,100,50.0,10.0,2.0,1.0,0.001)");
       // y = re.eval("a<-bestglm(as.data.frame(data),nvmax=0,IC=\"BICg\",t=1.0)");
        // System.out.println(y);
        //     System.out.println(re.eval("names(as.data.frame(data))"));
      //  System.out.println(re.eval("a$BestModel$coefficients"));
        //  System.out.println(re.eval("names(a$BestModel$coefficients)"));
        //  System.out.println(re.eval("length(a$BestModel$coefficients)"));
        nvarmodel = 1;

   //     System.out.println(nvarmodel);

        Continuous lin = new Continuous();
        Continuous err = new ContinuousDiscrete();

        lin.setName(nodes.elementAt(i).getName() + "linear");
        err.setName(nodes.elementAt(i).getName() + "error");

        nodes.insertNode(lin);
        nodes.insertNode(err);

        vartype[nnodes + artvar] = 4;
        vartype[nnodes + artvar + 1] = 6;

        vartype[i] = 5;

        indexconvar[artvar / 2] = i;

        h = new int[2];

        h[0] = nnodes + artvar;
        h[1] = nnodes + artvar + 1;
        artvar = artvar + 2;

        indexvariables.add(h);

        h = new int[nvarmodel + 1];

        t = new double[2];
        t[0] = 0.0;
        t[1] = 1.0;

     //   t = re.eval("a$BestModel$coefficients").asDoubleArray();
        h[1] = i;

        indexvariables.add(h);
        linearcoefficients.add(t);

        return x;
    }

    public DynamicNetwork getOutput() {

        return Output;
    }

    public void preparevectors(int i, double[][] matr, double[] resp) {

        double[] rawval;
        Vector original;
        int index;

        index = 0;
        for (int k = 0; k < nsequences; k++) {
            //   System.out.println("Sequence "+ k);
            original = (Vector) rawdata.elementAt(k);
            rawval = (double[]) original.elementAt(0);
            for (int l = 0; l < rawval.length; l++) {
                matr[index][l] = rawval[l];
            }
            index++;
            for (int j = 1; j < original.size() - 1; j++) {
                rawval = (double[]) original.elementAt(j);
                resp[index - 1] = rawval[i];
                for (int l = 0; l < rawval.length; l++) {
                    matr[index][l] = rawval[l];
                }
                index++;
            }
            rawval = (double[]) original.elementAt(original.size() - 1);
            resp[index - 1] = rawval[i];
        }

    }

    public void preparevectors2(int i, double[][] matr) {

        double[] rawval;
        Vector original;
        int index;
        double x;

        index = 0;

        rawval = new double[1];

        for (int k = 0; k < nsequences; k++) {
            //   System.out.println("Sequence "+ k);
            original = (Vector) rawdata.elementAt(k);
            rawval = (double[]) original.elementAt(0);
            for (int l = 0; l < rawval.length; l++) {
                matr[index][l] = rawval[l];
            }

            index++;
            for (int j = 1; j < original.size() - 1; j++) {
                rawval = (double[]) original.elementAt(j);

                for (int l = 0; l < rawval.length; l++) {
                    matr[index][l] = rawval[l];
                    matr[index - 1][rawval.length] = rawval[i];
                }
                index++;
            }
            rawval = (double[]) original.elementAt(original.size() - 1);
            matr[index - 1][rawval.length] = rawval[i];
        }

    }

    public double learningfix() {

        double x;
        int i;

     //   System.out.println("Empiezo aprender");
        x = 0.0;

        for (i = 0; i < nnodes; i++) {
            x += learning(i);
            //     System.out.println("Score after learning variable " + i + " = " + x);
        }

        return x;
    }

    public double learningfix2(NodeList nl) {

        double x;
        int i, number;

     //   System.out.println("Empiezo aprender");
        x = 0.0;
        number = 0;
        Node ni;

        for (i = 0; i < nnodes; i++) {
            switch (vartype[i]) {

                case 0:
                case 1:
                case 3: {
                    ni = nodes.elementAt(i);
                    if (nodes.getId(ni) == -1) {
                        x += learning(i);
                    } else {
                        x += learningsa(i);
                    }
                    break;
                }
                case 6: {
                    ni = nodes.elementAt(i);
                    if (nodes.getId(ni) == -1) {
                        x += learning(i);
                    }
                    else {
                        x+= learningerrorsa(i);
                    }
                    break;
                }
                case 4: {
                    learning2(number, 4);
                    number++;
                    break;
                }

                case 5: {
                    learning2(number, 5);
                    number++;
                    if (number == artvar / 2) {
                        number = 0;
                    }
                    break;
                }
            }

        }
            //     System.out.println("Score after learning variable " + i + " = " + x);

        return x;
    }

    public double learningfix2() {

        double x;
        int i, number;

       // System.out.println("Empiezo aprender");
        x = 0.0;
        number = 0;

        for (i = 0; i < nnodes; i++) {
            switch (vartype[i]) {

                case 0:
                case 1:
                case 3: {
                    x += learning(i);
                    break;
                }
                case 6: {
                    x += learning(i);
                    break;
                }
                case 4: {
                    learning2(number, 4);
                    number++;
                    break;
                }

                case 5: {
                    learning2(number, 5);
                    number++;
                    if (number == artvar / 2) {
                        number = 0;
                    }
                    break;
                }
            }

        }
            //     System.out.println("Score after learning variable " + i + " = " + x);

        return x;
    }

    public double learningd() {
        double x, xmax;
        x = 0.0;
        boolean improv;

        initialdisc();
        xmax = learningfix();
      //  System.out.println("first score " + xmax);
        improv = true;

        while (improv) {
            improv = false;
            x = learningd2(xmax);
            if (x > xmax) {
                xmax = x;
                improv = true;
            }

        }

        xmax = learningfix();
        return xmax;

    }

    public double learningd5() {

        discretize();
        Output = new DynamicNetwork(nodes);
        Output.vartypes = vartype;
        return (learningfix());

    }

    public double learningd2(double init) {

        double x, max;
        int ind;
        int i, j, k;
        boolean changes = true;
        ContinuousDiscrete var;
        boolean possible;

        //     System.out.println("Empiezo aprender cambiando con score " + init);
        max = init;

        while (changes) {
         //   System.out.println("Mejorando el score " + max);
            changes = false;
            for (k = 0; k < nnodes; k++) {
                if (vartype[k] == Node.CONTINUOUS) {

                    var = (ContinuousDiscrete) nodes.elementAt(k);
                    i = 0;
                    while (i < var.getNumberofIntevals()) {
      //                  System.out.println("Splitting Interval " + i + " of " + var.getName());
                        possible = splitInterval(k, i);

                        for (j = 0; j <= var.getNumberofIntevals(); j++) {
                        //    System.out.print(" " + var.getLimit(j) + " ");
                        }
                   //     System.out.println();

                        if (possible) {
                            x = learningfix();

                        //    System.out.println("New Score " + x);

                            if (x > max) {
                                max = x;
                                changes = true;
                         //       System.out.println("Improving to " + max);
                            } else {
                                joinInterval(k, i);

                                i++;
                            }
                        } else {
                            i++;
                        }
                    }
                    i = 0;
                    while (i < var.getNumberofIntevals() - 1) {
                     //   System.out.println("Testing joining intervals");
                        ind = joinInterval(k, i);
                        x = learningfix();
                        if (x > max) {
                            max = x;
                            changes = true;
                          //  System.out.println("Improving to " + max);
                        } else {
                            splitInterval(k, i, ind);
                            i++;
                        }
                    }

                }
                if (vartype[k] == Node.CONTINUOUS) {
                    var = (ContinuousDiscrete) nodes.elementAt(k);
                    for (i = 0; i <= var.getNumberofIntevals(); i++) {
                   //     System.out.print(" " + var.getLimit(i) + " ");
                    }
                  //  System.out.println();

                }

            }
        }

        return max;
    }

    public double learningd3() {
        double x, xmax;
        x = 0.0;
        boolean improv;
        int i;

        for (i = 0; i < nnodes; i++) {
            switch (vartype[i]) {
                case Node.FINITE_STATES: {
                    x += learning(i);
                    break;
                }
                case Node.MIXED: {
                    x += learning(i);
                    break;
                }
                case Node.CONTINUOUS: {
                    x += learningd(i);
                    break;
                }
            }
        }

        return x;
    }

    private void discretize() {
        int i, k, j, ind;
        ContinuousDiscrete var;
        boolean possible;
        double x, bestx;
        NodeList l;
        boolean changes;

        l = new NodeList();
        possible = false;
       // System.out.println("Discretizaciones Iniciales");
        initialdisc();
        for (k = 0; k < nnodes; k++) {

            if ((vartype[k] == Node.CONTINUOUS) || (vartype[k] == 4) || (vartype[k] == 5) || (vartype[k] == 6)) {

                var = (ContinuousDiscrete) nodes.elementAt(k);
                changes = true;

                while (changes) {
                    changes = false;
                    x = computeScoret(var, l);
                    bestx = x;
                    i = 0;
                    while (i < var.getNumberofIntevals()) {
                      //  System.out.println("Splitting Interval " + i + " of " + var.getName());
                        possible = splitInterval(k, i);

                        for (j = 0; j <= var.getNumberofIntevals(); j++) {
                         //   System.out.print(" " + var.getLimit(j) + " ");
                        }
                       // System.out.println();

                        if (possible) {
                            x = computeScoret(var, l);
                            if (x > bestx) {
                                bestx = x;
                            //    System.out.println("Improving to " + bestx);
                                changes = true;
                            } else {
                                joinInterval(k, i);

                                i++;
                            }
                        } else {
                            i++;
                        }
                    }
                    i = 0;
                    while (i < var.getNumberofIntevals() - 1) {
                      //  System.out.println("Testing joining intervals");
                        ind = joinInterval(k, i);
                        x = computeScoret(var, l);
                        if (x > bestx) {
                            changes = true;
                            bestx = x;
                         //   System.out.println("Improving to " + bestx);
                        } else {
                            splitInterval(k, i, ind);
                            i++;
                        }
                    }

                }
            }
            if (vartype[k] == Node.CONTINUOUS) {
                var = (ContinuousDiscrete) nodes.elementAt(k);
                for (i = 0; i <= var.getNumberofIntevals(); i++) {
                 //   System.out.print(" " + var.getLimit(i) + " ");
                }
             //   System.out.println();

            }

        }

    }

    private void initialdisc() {
        int i, size;
        Node var;
        double[] limits;
        double[] possible;
        int[] indixes;

        for (i = 0; i < nnodes; i++) {
           // System.out.println("variable " + i);
            var = nodes.elementAt(i);

            if ((vartype[i] == Node.CONTINUOUS) || (vartype[i] == 4) || (vartype[i] == 5) || (vartype[i] == 6)) {
                indixes = new int[3];
                limits = new double[3];
                possible = (double[]) cutpoints.elementAt(i);
                size = possible.length;
                if (size == 1) {
                    limits = new double[2];
                    indixes = new int[2];
                    indixes[0] = 0;
                    indixes[1] = 1;
                    limits[0] = ((Continuous) var).getMin();
                    limits[1] = ((Continuous) var).getMax();
                    var = new ContinuousDiscrete((Continuous) var, 1, limits);
                } else {
                    if (size == 2) {
                        limits = new double[2];
                        limits[0] = ((Continuous) var).getMin();
                        limits[1] = ((Continuous) var).getMax();
                        indixes = new int[2];
                        indixes[0] = 0;
                        indixes[1] = 1;
                        var = new ContinuousDiscrete((Continuous) var, 1, limits);

                    } else {
                        if (size > 2) {
                            int middle;
                            int low, up;

                            limits = new double[3];

                            middle = size / 2;
                            low = 0;
                            up = size - 1;

                            while ((possible[middle] == possible[low]) && middle < up) {
                                middle++;
                            }
                            while ((possible[middle] == possible[up]) && middle > low) {
                                middle--;
                            }
                            if (possible[middle] != possible[low]) {
                                limits[1] = possible[middle];
                                limits[0] = possible[low];
                                limits[2] = possible[up];
                                indixes[0] = low;
                                indixes[1] = middle;
                                indixes[2] = up;
                            } else {
                                limits = new double[2];
                                limits[0] = ((Continuous) var).getMin();
                                limits[1] = ((Continuous) var).getMax();
                                indixes = new int[2];
                                indixes[0] = 0;
                                indixes[1] = 1;
                            }

                            var = new ContinuousDiscrete((Continuous) var, 2, limits);
                        }
                    }
                }

                //        System.out.println(limits[0] + " " + limits[1] + " " + limits[2]);
                nodes.setElementAt(var, i);
                indexcuts.setElementAt(indixes, i);
            }
        }

        transformdata();

    }

    private void transformdata() {
        int i, j, k;
        double[] rawval;
        int[] disval;

        for (i = 0; i < nsequences; i++) {
            for (k = 0; k < nnodes; k++) {
                if (vartype[k] == Node.CONTINUOUS) {
                    ((CaseListMem) sequences.elementAt(i)).getVariables().setElementAt(nodes.elementAt(k), k);
                    //     System.out.println("Replaced variable " +  nodes.elementAt(k).getName());
                }

            }
            for (j = 0; j < ((CaseListMem) sequences.elementAt(i)).getNumberOfCases(); j++) {
                rawval = (double[]) ((Vector) rawdata.elementAt(i)).elementAt(j);
                disval = (int[]) ((CaseListMem) sequences.elementAt(i)).getCase(j);
                for (k = 0; k < nnodes; k++) {
                    if (vartype[k] == Node.CONTINUOUS) {
                        disval[k] = ((ContinuousDiscrete) nodes.elementAt(k)).getCase(rawval[k]);
                        //  System.out.println("replacing value " + rawval[k] + " by " +  disval[k]);
                    }
                }
            }
        }

    }

    private void addartificialdata() {
        int i, j, k, l;
        double[] rawval, rawval2, rawval2old;
        int[] disval, disval2;
        int index;
        double x, y;
        double[] coe;
        int[] var;

        for (i = 0; i < nsequences; i++) {

            rawval2old = new double[nnodes + artvar];
            for (j = 0; j < ((CaseListMem) sequences.elementAt(i)).getNumberOfCases(); j++) {
                rawval = (double[]) ((Vector) rawdata.elementAt(i)).elementAt(j);
                disval = (int[]) ((CaseListMem) sequences.elementAt(i)).getCase(j);
                disval2 = new int[nnodes + artvar];

                rawval2 = new double[nnodes + artvar];

                for (k = 0; k < nnodes; k++) {
                    rawval2[k] = rawval[k];
                    disval2[k] = disval[k];
                }

                for (k = 0; k < artvar; k = k + 2) {
                    index = indexconvar[k / 2];

                 //   System.out.println("Variable continua " + index);

                    coe = ((double[]) linearcoefficients.elementAt(k / 2));
                    var = ((int[]) indexvariables.elementAt(k + 1));
                    x = coe[0];
                    for (l = 1; l < coe.length; l++) {
                        x += coe[l] * rawval[var[l]];
                        // System.out.println("Aproximable con coeficiente " + var[l] + " coe " + coe[l]);
                    }

                    if (j == 0) {
                        y = 0;
                    } else {
                        y = rawval[index] - rawval2old[nnodes + k];
                    }

                    rawval2[nnodes + k] = x;
                    rawval2[nnodes + k + 1] = y;

                  //  System.out.println("Error " + y);
                }
                rawval2old = rawval2;

                ((Vector) rawdata.elementAt(i)).setElementAt(rawval2, j);
                ((CaseListMem) sequences.elementAt(i)).setCase(disval2, j);

            }
        }

    }

    private boolean transformdata(int k, int l) {
        int i, j;
        double[] rawval;
        int[] disval;
        boolean possible = false;
        boolean vall, vallp;
        vall = false;
        vallp = false;

        for (i = 0; i < nsequences; i++) {
            for (j = 0; j < ((CaseListMem) sequences.elementAt(i)).getNumberOfCases(); j++) {
                rawval = (double[]) ((Vector) rawdata.elementAt(i)).elementAt(j);
                disval = (int[]) ((CaseListMem) sequences.elementAt(i)).getCase(j);

                disval[k] = ((ContinuousDiscrete) nodes.elementAt(k)).getCase(rawval[k]);
                if (disval[k] == l) {
                    vall = true;
                }
                if (disval[k] == (l + 1)) {
                    vallp = true;
                }

                //      System.out.println("variable " + k +"new value " + disval[k] + " rawvalue " + rawval[k]);
                //     disval =  (int[]) ((CaseListMem) sequences.elementAt(i)).getCase(j);
                //       System.out.println("variable " + k +"new value " + disval[k] + " rawvalue " + rawval[k]);
            }
        }

        possible = vall && vallp;

        return possible;

    }

    private void transformdata(int k) {
        int i, j;
        double[] rawval;
        int[] disval;

        for (i = 0; i < nsequences; i++) {
            for (j = 0; j < ((CaseListMem) sequences.elementAt(i)).getNumberOfCases(); j++) {
                rawval = (double[]) ((Vector) rawdata.elementAt(i)).elementAt(j);
                disval = (int[]) ((CaseListMem) sequences.elementAt(i)).getCase(j);

                disval[k] = ((ContinuousDiscrete) nodes.elementAt(k)).getCase(rawval[k]);

                //      System.out.println("variable " + k +"new value " + disval[k] + " rawvalue " + rawval[k]);
                //     disval =  (int[]) ((CaseListMem) sequences.elementAt(i)).getCase(j);
                //       System.out.println("variable " + k +"new value " + disval[k] + " rawvalue " + rawval[k]);
            }
        }

    }

    public void estimateParameters() {

        int i;

        for (i = 0; i < nnodes; i++) {

            if ((vartype[i] == 1) || (vartype[i] == 3) || (vartype[i] == 6)) {

                estimateParameters(i);
            }
        }

    }

    public double learning(int k) {

        double x, max, thismax;
        Node var;
        NodeList parents;
        int i;
        boolean[] isp;
        Node v;
        boolean add;
        int varmax;
        int nmaxparents = 6;

        x = 0.0;
        var = nodes.elementAt(k);
        parents = new NodeList();
     //   System.out.println("Aprendiendo variable " + var.getName());

        isp = new boolean[nnodes];
        for (i = 0; i < nnodes; i++) {
            isp[i] = false;
        }

        max = computeScoret(var, parents);

     //   System.out.println("primera llamada a score sin padres");
        boolean changes = true;

     //   System.out.println("Modificando el conjunto de padres");
        while (changes) {
            thismax = max - 1.0;
            changes = false;
            add = true;
            varmax = 0;
            for (i = 0; i < nnodes; i++) {
                v = nodes.elementAt(i);
                if (isp[i]) {
                    parents.removeNode(v);
                    x = computeScoret(var, parents);
                    parents.insertNode(v);
                    if (x > thismax) {
                        thismax = x;
                        varmax = i;
                        add = false;
                    }
                } else {
                    if (parents.size() < nmaxparents) {

                        parents.insertNode(v);
                  //      System.out.println("adding variable " + v.getName());
                  //      System.out.println("Numero de variables " + parents.size());
                        x = computeScoret(var, parents);

                   //     System.out.println("Score " + x);
                        parents.removeNode(v);

                        if ((x > thismax)) {
                            thismax = x;
                            varmax = i;
                            add = true;
                        }
                    }
                }
            }
            if (thismax > max) {
                max = thismax;
                changes = true;
             //   System.out.println("New score " + max);
                if (add) {
               //     System.out.println("Adding variable " + nodes.elementAt(varmax).getName());
                    parents.insertNode(nodes.elementAt(varmax));
                    isp[varmax] = true;
                } else {
                    parents.removeNode(nodes.elementAt(varmax));
                 //   System.out.println("Removing variable " + nodes.elementAt(varmax).getName());
                    isp[varmax] = false;
                }
            }

        }

        for (i = 0; i < nnodes; i++) {
            if (isp[i]) {
                //  System.out.println("Arco de " + nodes.elementAt(i).getName() + " a " + var.getName() + " " + isp[i]);
            }
            Output.temporalarcs[i][k] = isp[i];

        }

        return max;
    }

    public double learningsa(int k) {

        double x, max, thismax;
        Node var;
        NodeList parents;
        int i;
        boolean[] isp;
        Node v;
        boolean add;
        int varmax;
        int nmaxparents = 6;

        x = 0.0;
        var = nodes.elementAt(k);
        parents = new NodeList();
     //   System.out.println("Aprendiendo variable " + var.getName());

        parents.insertNode(var);

        max = computeScoret(var, parents);

        for (i = 0; i < nnodes; i++) {
            if (i == k) {
                Output.temporalarcs[i][k] = true;
                //  System.out.println("Arco de " + nodes.elementAt(i).getName() + " a " + var.getName() + " " + isp[i]);
            } else {
                Output.temporalarcs[i][k] = false;
            }

        }

        return max;
    }

    public double learningerrorsa(int k) {

        double x, max;
        Node var;
        NodeList parents;
        int i;

        x = 0.0;
        var = nodes.elementAt(k);
        parents = new NodeList();
    //    System.out.println("Aprendiendo variable " + var.getName());

        max = computeScoret(var, parents);

        for (i = 0; i < nnodes; i++) {

            Output.temporalarcs[i][k] = false;

        }

        return max;
    }

    public double learning2(int number, int ty) {

        double x = 0.0;
        int i;

        int varorig, indexvar, lvar;
        int[] vecparents, othervec;
        double[] y;

        indexvar = indexconvar[number];

        if (ty == 4) {

       //    System.out.println("Variable Original " + indexvar);

            lvar = nnodes - artvar + 2 * number;

            vecparents = (int[]) indexvariables.elementAt(number * 2 + 1);
            for (i = 0; i < vecparents.length; i++) {
                Output.sametimearcs[vecparents[i]][lvar] = true;
            }
            Output.linearvar[lvar] = vecparents;
            Output.linearcoe[lvar] = (double[]) linearcoefficients.elementAt(number);

        }
        if (ty == 5) {

           // System.out.println("Variable Original " + indexvar);

            vecparents = (int[]) indexvariables.elementAt(number * 2);

            Output.sametimearcs[vecparents[1]][indexvar] = true;
            Output.temporalarcs[vecparents[0]][indexvar] = true;

            y = new double[2];
            y[0] = 1.0;
            y[1] = 1.0;

            Output.linearvar[indexvar] = vecparents;
            Output.linearcoe[indexvar] = y;

            othervec = new int[2];

            othervec[1] = indexvar;
            othervec[0] = vecparents[0];
            y = new double[2];

            y[0] = -1.0;
            y[1] = 1.0;

            Output.linearvar[vecparents[1]] = othervec;
            Output.linearcoe[vecparents[1]] = y;

        }

        return x;

    }

    public double learningd(int k) {

        double x, max, thismax;
        ContinuousDiscrete var;
        NodeList parents;
        int i;
        boolean[] isp;
        Node v;
        boolean add;
        int varmax;
        boolean changes;
        double y;

        x = 0.0;

        max = learning(k);
        var = (ContinuousDiscrete) nodes.elementAt(k);

        changes = true;

        while (changes) {
            i = 0;
            while (i < var.getNumberofIntevals()) {
                splitInterval(k, i);
                x = learning(k);
                if (x > max) {
                    max = x;
                    changes = true;
                } else {
                    var.join(i);
                    i++;
                }
            }
            i = 0;
            while (i < var.getNumberofIntevals() - 1) {
                y = joinInterval(k, i);
                x = learning(k);
                if (x > max) {
                    max = x;
                    changes = true;
                } else {
                    var.split(i, y);
                    i++;
                }
            }

        }

        return max;

    }

    public boolean splitInterval(int k, int i) {
        ContinuousDiscrete var;
        int indexes[];
        int newindex[];
        double varlimits[];
        int cutindex;
        int newcut;
        int min, max, j;
        double x;
        int size;

        boolean possible = false;

        var = (ContinuousDiscrete) nodes.elementAt(k);

        varlimits = (double[]) cutpoints.elementAt(k);
        indexes = (int[]) indexcuts.elementAt(k);

        min = indexes[i];
        max = indexes[i + 1];

        size = indexes.length;

        newcut = (max + min) / 2;

        while ((varlimits[newcut] == varlimits[min]) && newcut < max) {
            newcut++;
        }
        while ((varlimits[newcut] == varlimits[max]) && newcut > min) {
            newcut--;
        }
        if (varlimits[newcut] == varlimits[min]) {
            return possible;
        }

        possible = true;

        newindex = new int[size + 1];

        for (j = 0; j <= i; j++) {
            newindex[j] = indexes[j];
        }

        newindex[i + 1] = newcut;

        for (j = i + 2; j <= size; j++) {
            newindex[j] = indexes[j - 1];
        }

        x = varlimits[newcut];

        indexcuts.set(k, newindex);

        var.split(i, x);
        transformdata(k);

        /*     for(j=0;j<=var.getNumberofIntevals();j++)
         {
         System.out.println("Limite intervalo " + var.getLimit(j));
         }
         */
  //     System.out.println("Testing nterval " + i + " of variable " + var.getName() + " " + possible);
        return possible;
    }

    public boolean splitInterval(int k, int i, int index) {
        ContinuousDiscrete var;
        int indexes[];
        int newindex[];
        double varlimits[];
        int cutindex;
        int newcut;
        int min, max, j;
        double x;
        int size;

        boolean possible = false;

        var = (ContinuousDiscrete) nodes.elementAt(k);

        varlimits = (double[]) cutpoints.elementAt(k);
        indexes = (int[]) indexcuts.elementAt(k);

        min = indexes[i];
        max = indexes[i + 1];

        size = indexes.length;

        if (max - min <= 1) {
            return possible;
        }
        possible = true;
        newcut = index;

        newindex = new int[size + 1];

        for (j = 0; j <= i; j++) {
            newindex[j] = indexes[j];
        }

        newindex[i + 1] = newcut;

        for (j = i + 2; j <= size; j++) {
            newindex[j] = indexes[j - 1];
        }

        x = varlimits[newcut];

        indexcuts.set(k, newindex);

        var.split(i, x);
        transformdata(k);

        /*     for(j=0;j<=var.getNumberofIntevals();j++)
         {
         System.out.println("Limite intervalo " + var.getLimit(j));
         }
         */
     //   System.out.println("Testing nterval " + i + " of variable " + var.getName() + " " + possible);
        return possible;
    }

    public int joinInterval(int k, int i) {
        ContinuousDiscrete var;
        int y;
        int indexes[];
        int newindex[];

        int size;
        int l;

        y = -1;
        //   System.out.println("First call joint");

        indexes = (int[]) indexcuts.elementAt(k);
        size = indexes.length;

        var = (ContinuousDiscrete) nodes.elementAt(k);

        if (var.getNumberofIntevals() > (i + 1)) {
            newindex = new int[size - 1];

            for (l = 0; l <= i; l++) {
                newindex[l] = indexes[l];

            }
            for (l = i + 1; l < size - 1; l++) {
                newindex[l] = indexes[l + 1];

            }

            y = indexes[i + 1];
            var.join(i);
            transformdata(k);
            indexcuts.set(k, newindex);

        }

        return y;

    }

    public void estimateParameters(int k) {
        Node var, varc;
        NodeList potvar, parents, potvar0, parents0;
        int i, j, ncases, nconf;
        PotentialTable pot, pot0;
        double[][] freq, freq0;
        double[] value, value0;
        double total;

        var = nodes.elementAt(k);
        varc = var.copy();
        varc.setName(var.getName() + "n");
        varc.setTypeOfVariable(var.getTypeOfVariable());

        potvar = new NodeList();
        parents = new NodeList();
        parents0 = new NodeList();
        potvar0 = new NodeList();

        potvar0.insertNode(var);

        potvar.insertNode(varc);

        for (i = 0; i < nnodes; i++) {
            if (Output.temporalarcs[i][k]) {
                potvar.insertNode(nodes.elementAt(i));
                parents.insertNode(nodes.elementAt(i));

            }
        }

        for (i = 0; i < nnodes; i++) {
            if (Output.time0arcs[i][k]) {
                potvar0.insertNode(nodes.elementAt(i));
                parents0.insertNode(nodes.elementAt(i));
            }
        }

        pot = new PotentialTable(potvar);
        pot0 = new PotentialTable(potvar0);

        freq = computeFreq(var, parents);
        freq0 = computeFreq0(potvar0);

        nconf = (int) (parents.getSize() + 0.5);
        ncases = 1;

        if (var.getTypeOfVariable() == 1) {
            ncases = ((FiniteStates) var).getNumStates();
        } else {
            if (var.getTypeOfVariable() == 3) {
                ncases = ((ContinuousDiscrete) var).getNumberofIntevals();
            }
        }

        value = new double[ncases * nconf];

        for (j = 0; j < nconf; j++) {
            total = 0.0;
            for (i = 0; i < ncases; i++) {
                total += freq[i][j];
            }
            for (i = 0; i < ncases; i++) {
                value[i * nconf + j] = (freq[i][j] + 1.0) / (total + ncases);
            }
        }

        pot.setValues(value);

        nconf = (int) (parents0.getSize() + 0.5);
        value0 = new double[ncases * nconf];

        for (j = 0; j < nconf; j++) {
            total = 0.0;
            for (i = 0; i < ncases; i++) {
                total += freq0[i][j];
            }
            for (i = 0; i < ncases; i++) {
                value0[i * nconf + j] = (freq0[i][j] + 1.0) / (total + ncases);
            }
        }
        pot0.setValues(value0);
        Output.setPotentialt(k, pot);
        Output.setPotential0(k, pot0);

   //     System.out.println("Variable " + var.getName());

    }

    private double computeScore(Node var, NodeList parents) {

        double[][] frequencies;
        double[] change;
        double x;
        int ncases, nconf;

        change = new double[2];
        if (score != 4) {
            frequencies = computeFreq(var, parents);
        } else {

            frequencies = computeFreq2(var, parents, change);
        }
        ncases = 1;
        if (var.getTypeOfVariable() == 1) {
            ncases = ((FiniteStates) var).getNumStates();
        } else {
            if (var.getTypeOfVariable() == 3) {

                ncases = ((ContinuousDiscrete) var).getNumberofIntevals();
            }
        }

        nconf = (int) (parents.getSize() + 0.5);

        x = 0.0;
        if (score == 0) {
            x = ((BICMetrics) m).score(frequencies, ncases, nconf, 2.0);
        }
        if (score == 1) {
            x = ((AkaikeMetrics) m).score(frequencies, ncases, nconf, 2.0);
        }
        if (score == 2) {
            x = ((K2Metrics) m).score(frequencies, ncases, nconf);
        }
        if (score == 3) {
            x = ((BDeMetrics) m).score(frequencies, ncases, nconf, 2.0);
        }
        if (score == 4) {
            x = ((BDeMetrics) m).score0(frequencies, ncases, nconf, 2.0);
            x += ((BDeMetrics) m).score(change, 2, 2.0);
        }
        return x;

    }

    private double computeScoret(Node var, NodeList parents) {

        double[][] frequencies;
        double[] change;
        double[] frevar;
        double[] leng;
        double lengt;

        double x;
        int ncases, nconf;
        int i, j;

        lengt = 0.0;
        leng = new double[1];
        change = new double[2];
        if (score != 4) {
            frequencies = computeFreq(var, parents);
        } else {

            frequencies = computeFreq2(var, parents, change);
        }
        ncases = 1;
        if (var.getTypeOfVariable() == 1) {
            ncases = ((FiniteStates) var).getNumStates();
            leng = new double[ncases];
            for (i = 0; i < ncases; i++) {
                leng[i] = 1.0;
            }
            lengt = ncases;

        } else {
            if (var.getTypeOfVariable() == 3) {
                ncases = ((ContinuousDiscrete) var).getNumberofIntevals();
                leng = new double[ncases];
                lengt = 0.0;
                for (i = 0; i < ncases; i++) {
                    leng[i] = ((ContinuousDiscrete) var).getLimit(i + 1) - ((ContinuousDiscrete) var).getLimit(i);
                    lengt += leng[i];
                }

            }
        }

        nconf = (int) (parents.getSize() + 0.5);

        frevar = new double[ncases];

        // System.out.println("Empiezo score");
        x = 0.0;
        if (score == 0) {
            x = ((BICMetrics) m).scorecon(frequencies, ncases, nconf, 2.0);
        }
        if (score == 1) {
            x = ((AkaikeMetrics) m).scorecon(frequencies, ncases, nconf, 2.0);
        }
        if (score == 2) {
            x = ((K2Metrics) m).scorecon(frequencies, ncases, nconf, leng, lengt);
        }
        if (score == 3) {
            x = ((BDeMetrics) m).scorecon(frequencies, ncases, nconf, 2.0, leng, lengt);
        }
        if (score == 4) {
            x = ((BDeMetrics) m).score0(frequencies, ncases, nconf, 2.0);
            x += ((BDeMetrics) m).score(change, 2, 2.0);
        }

        //   System.out.println("Scorein variable " + var.getName());
        if (var.getTypeOfVariable() == 3) {
            //     System.out.println("Score before " + x);
            for (i = 0; i < ncases; i++) {
                frevar[i] = 0.0;
                for (j = 0; j < nconf; j++) {
                    frevar[i] += frequencies[i][j];
                }

                //  if (frevar[i]==0) {x-= 100000;}
                //    System.out.println("frecuencia de variable  " + var.getName() + " en caso  " + i + " = " + frevar[i] );
                {
                    x -= (frevar[i]) * Math.log(leng[i]);
                }

            }
            //     System.out.println("Score after " + x);

        }

        return x;

    }

    private double[][] computeFreq(Node var, NodeList parents) {

        double[][] finalfreq, partialfreq;
        int ncases, nconf, i, j, k;

        ncases = 0;
        nconf = 1;

        if (var.getTypeOfVariable() == 1) {
            ncases = ((FiniteStates) var).getNumStates();
        } else {
            if (var.getTypeOfVariable() == 3) {
                ncases = ((ContinuousDiscrete) var).getNumberofIntevals();
            }
        }

        nconf = (int) (parents.getSize() + 0.5);

        finalfreq = new double[ncases][nconf];

        //   System.out.println("Numero de configuraciones partial " + nconf);
        //   System.out.println(nsequences);
        for (i = 0; i < nsequences; i++) {
            /* for(k=0; k<((CaseListMem)sequences.elementAt(i)).getVariables().size() ; k++) {
             System.out.println(( (Node) ((CaseListMem)sequences.elementAt(i)).getVariables().elementAt(k)).getName());
             }
             */
            partialfreq = ((CaseListMem) sequences.elementAt(i)).getFrequencies(var, parents);
            for (j = 0; j < nconf; j++) {
                //      System.out.println("Comiento configuracion " + j);
                for (k = 0; k < ncases; k++) {
                    finalfreq[k][j] += partialfreq[k][j];
                    //               System.out.print(finalfreq[k][j]); 

                }
                //   System.out.println();
            }
        }

        return finalfreq;

    }

    private double[][] computeFreq2(Node var, NodeList parents, double[] change) {

        double[][] finalfreq, partialfreq;
        int ncases, nconf, i, j, k;

        ncases = 0;
        nconf = 1;

        if (var.getTypeOfVariable() == 1) {
            ncases = ((FiniteStates) var).getNumStates();
        } else {
            if (var.getTypeOfVariable() == 3) {
                ncases = ((ContinuousDiscrete) var).getNumberofIntevals();
            }
        }

        nconf = (int) (parents.getSize() + 0.5);

        finalfreq = new double[ncases][nconf];

        //   System.out.println("Numero de configuraciones partial " + nconf);
        //   System.out.println(nsequences);
        for (i = 0; i < nsequences; i++) {
            partialfreq = ((CaseListMem) sequences.elementAt(i)).getFrequencies2(var, parents, change);
            for (j = 0; j < nconf; j++) {
                //      System.out.println("Comiento configuracion " + j);
                for (k = 0; k < ncases; k++) {
                    finalfreq[k][j] += partialfreq[k][j];
                    //          System.out.print(finalfreq[k][j]); 
                    //       System.out.println();
                }
            }
        }

        return finalfreq;

    }

    private double[][] computeFreq0(NodeList vars) {

        double[][] finalfreq;
        int ncases, nconf, i, j;
        Node var;
        NodeList parents;
        int[] values, size;
        int iv;
        CaseListMem cases;
        int factor, indexparents;

        var = vars.elementAt(0);

        parents = new NodeList();
        for (i = 1; i < vars.size(); i++) {
            parents.insertNode(vars.elementAt(i));
        }

        ncases = 0;
        nconf = 1;

        if (var.getTypeOfVariable() == 1) {
            ncases = ((FiniteStates) var).getNumStates();
        } else {
            if (var.getTypeOfVariable() == 3) {
                ncases = ((ContinuousDiscrete) var).getNumberofIntevals();
            }
        }
        size = new int[parents.size()];
        nconf = (int) (parents.getSize() + 0.5);

        for (i = 0; i < parents.size(); i++) {
            size[i] = 1;
            if (parents.elementAt(i).getTypeOfVariable() == 1) {
                size[i] = ((FiniteStates) parents.elementAt(i)).getNumStates();
            } else {
                if (parents.elementAt(i).getTypeOfVariable() == 3) {
                    size[i] = ((ContinuousDiscrete) parents.elementAt(i)).getNumberofIntevals();
                }
            }

        }

        finalfreq = new double[ncases][nconf];

        //   System.out.println("Numero de configuraciones partial " + nconf);
        //   System.out.println(nsequences);
        for (i = 0; i < nsequences; i++) {
            cases = (CaseListMem) sequences.elementAt(i);
            values = cases.getCase(0);
            iv = values[cases.getVariables().indexOf(var)];
            factor = 1;
            indexparents = 0;
            //   System.out.println("Valor de la variable en caso " + i + " = " + valuevar);
            for (j = parents.size() - 1; j >= 0; j--) {
                indexparents += factor * values[cases.getVariables().indexOf(parents.elementAt(j))];
                factor = factor * size[j];
            }
            finalfreq[iv][indexparents]++;
        }

        return finalfreq;

    }

    static void Experiment(String directory, String description, String output) throws IOException {

        String line;
        String[] variables;
        String[] fls, learning;
        String[] parts;
        String test;
        PrintStream ps;
        PrintWriter psred;
        int i, j, k, nvar, nfls, l;
        double x;
        DynamicLearning learn;
        int[] scores;
        int nscores;
        Bnet red;
        int varscore;

        directory.trim();
        description.trim();
        output.trim();
        BufferedReader reader = new BufferedReader(new FileReader(directory + "/" + description));

        line = reader.readLine();

        variables = line.split("\\s*,\\s*");

        nvar = variables.length;

        line = reader.readLine();

        fls = line.split("\\s*,\\s*");

        nfls = fls.length;
        line = reader.readLine();

        parts = line.split("\\s*,\\s*");

        nscores = parts.length;
        scores = new int[nscores];

        for (i = 0; i < nscores; i++) {
            scores[i] = Integer.parseInt(parts[i]);
        }

        line = reader.readLine();

        varscore = Integer.parseInt(line);

        reader.close();

        ps = new PrintStream(directory + "/" + output);

        for (l = 0; l < nscores; l++) {

            switch (scores[l]) {
                case 0:
                    ps.println("BIC Score ");
                    break;
                case 1:
                    ps.println("Akaike Score ");
                    break;
                case 2:
                    ps.println("K2 Score ");
                    break;
                case 3:
                    ps.println("BDEu Score ");
                    break;
                case 4:
                    ps.println("Change BDEu Score ");
                    break;
            }

            ps.print("Discretizations ");

            for (i = 0; i < nvar; i++) {
                ps.print(" \t " + variables[i]);
            }
            ps.println();
            learning = new String[nfls - 1];

            for (i = 0; i < nvar; i++) {
                x = 0.0;

                for (j = 0; j < nfls; j++) {
                    test = fls[j];
                    for (k = 0; k < j; k++) {
                        learning[k] = fls[k];
                    }
                    for (k = j; k < (nfls - 1); k++) {
                        learning[k] = fls[k + 1];
                    }
                    learn = new DynamicLearning(directory, variables[i], learning, scores[l], varscore);
                    learn.learning();
            //        System.out.println("Fin de Learning");
                    learn.estimateParameters();

                    x += learn.Output.ScoreLogDynamicNoMissing(directory, test, varscore, learn.nonarti);

          //          System.out.println("Score " + x);
              //      psred = new PrintWriter(directory + "/network_var" + i + "_fl" + j + "_sc" + scores[l]);
//                    red = learn.Output.expand(2);
//                    red.save(psred);
                    //            psred.close();

                }

                ps.print(" \t " + x);

            }
            System.out.println("Finalizo");
            ps.println();
        }
        System.out.println("No mas scores");

        ps.close();

    }

}

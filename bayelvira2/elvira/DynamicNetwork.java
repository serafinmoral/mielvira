package elvira;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import elvira.parser.ParseException;
import elvira.potential.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import elvira.CaseListMem;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author smc
 */
public class DynamicNetwork {

    public NodeList nodes;

    public boolean[][] temporalarcs;
    public boolean[][] time0arcs;
    public boolean[][] sametimearcs;

    private int timesteps;
    private double timedelay;

    public Potential[] time0potentials;
    public Potential[] temporalpotentials;

    public Object[] linearvar;
    public Object[] linearcoe;
    public int[] indexcontvar;

    public int[] vartypes;

    public DynamicNetwork(NodeList list) {
        int n;
        int i, j;
        nodes = list.copy();
        n = nodes.size();
        temporalarcs = new boolean[n][n];
        time0arcs = new boolean[n][n];
        sametimearcs = new boolean[n][n];
        timesteps = 1;
        timedelay = 1.0;
        time0potentials = new Potential[n];
        temporalpotentials = new Potential[n];
        linearvar = new Object[n];
        linearcoe = new Object[n];
        for (i = 0; i < n; i++) {
            for (j = 0; j < n; j++) {
                temporalarcs[i][j] = false;
                time0arcs[i][j] = false;
                sametimearcs[i][j] = false;
            }
        }
    }

    public void setPotentialt(int k, Potential pot) {
        temporalpotentials[k] = pot;
    }

    public void setPotential0(int k, Potential pot) {
        time0potentials[k] = pot;
    }

    /* This method computes the log of the probability of a database cases
     * taking the dynamic network as model. No missing values are assumed.
     * 
     */
    public double ScoreLogDynamicNoMissingAc(CaseListMem cases) {

        double x = 0.0, y;
        PotentialTable pot;
        int[] val, valn;
        Vector vars, totalvars;
        Node var;
        int value;

        int i, n, j, si, index;

        n = cases.getNumberOfCases();
        si = nodes.size();
        totalvars = cases.getVariables();

        if (n > 0) {
            valn = cases.getCase(0);
            for (j = 0; j < si; j++) {
                if ((vartypes[j] == 1) || (vartypes[j] == 3) || (vartypes[j] == 6)) {
                    pot = (PotentialTable) time0potentials[j];
                    var = (Node) pot.getVariables().elementAt(0);
                        if   ((var.getName().startsWith("PA")&& !var.getName().contains("ValChange")   ) ||(var.getName().startsWith("IC")&&!var.getName().contains("Avg"))) {

                        index = computeIndex(valn, pot.getVariables(), totalvars);
                        y = pot.getValues()[index];
                        x += Math.log(y);
         
                        if (var.getTypeOfVariable() == Node.MIXED) {
                            index = locate(var, totalvars);
                            value = valn[index];
                            x -= Math.log(((ContinuousDiscrete) var).getLimit(value + 1) - ((ContinuousDiscrete) var).getLimit(value));

                        }
                    }
                }
            }

            for (i = 1; i < n; i++) {
                val = valn;
                valn = cases.getCase(i);
                for (j = 0; j < si; j++) {
                    if ((vartypes[j] == 1) || (vartypes[j] == 3) || (vartypes[j] == 6)) {
                        pot = (PotentialTable) temporalpotentials[j];
                        //               System.out.println("Variable " + j);
                        index = computeIndex(val, valn, pot.getVariables(), totalvars);
                        y = pot.getValues()[index];

                        var = (Node) pot.getVariables().elementAt(0);
                        if   ((var.getName().startsWith("PA")&& !var.getName().contains("ValChange")   ) ||(var.getName().startsWith("IC")&&!var.getName().contains("Avg"))) {
                            x += Math.log(y);
          //        System.out.println("Probability " + y);

                            //               System.out.println("Var type " + var.getTypeOfVariable());
                            if (var.getTypeOfVariable() == Node.MIXED) {
                                index = locaten(var, totalvars);
                                value = valn[index];
                                //  System.out.println("Value " + value);
                                x -= Math.log(((ContinuousDiscrete) var).getLimit(value + 1) - ((ContinuousDiscrete) var).getLimit(value));

                            }
                        }
                    }
                }

            }

        }

        return x;
    }
    
    

    public double ScoreLogDynamicNoMissing(CaseListMem cases) {

        double x = 0.0, y;
        PotentialTable pot;
        int[] val, valn;
        Vector vars, totalvars;
        Node var;
        int value;

        int i, n, j, si, index;

        n = cases.getNumberOfCases();
        si = nodes.size();
        totalvars = cases.getVariables();

        if (n > 0) {
            valn = cases.getCase(0);
            for (j = 0; j < si; j++) {
                if ((vartypes[j] == 1) || (vartypes[j] == 3) || (vartypes[j] == 6)) {
                    pot = (PotentialTable) time0potentials[j];
                    var = (Node) pot.getVariables().elementAt(0);
                    index = computeIndex(valn, pot.getVariables(), totalvars);
                    y = pot.getValues()[index];
                    x += Math.log(y);
                    if (var.getTypeOfVariable() == Node.MIXED) {
                        index = locate(var, totalvars);
                        value = valn[index];
                        x -= Math.log(((ContinuousDiscrete) var).getLimit(value + 1) - ((ContinuousDiscrete) var).getLimit(value));

                    }
                }
            }

            for (i = 1; i < n; i++) {
                val = valn;
                valn = cases.getCase(i);
                for (j = 0; j < si; j++) {
                    if ((vartypes[j] == 1) || (vartypes[j] == 3) || (vartypes[j] == 6)) {
                        pot = (PotentialTable) temporalpotentials[j];
                        //               System.out.println("Variable " + j);
                        index = computeIndex(val, valn, pot.getVariables(), totalvars);
                        y = pot.getValues()[index];

                        var = (Node) pot.getVariables().elementAt(0);

                        x += Math.log(y);
          //        System.out.println("Probability " + y);

                        //               System.out.println("Var type " + var.getTypeOfVariable());
                        if (var.getTypeOfVariable() == Node.MIXED) {
                            index = locaten(var, totalvars);
                            value = valn[index];
                            //  System.out.println("Value " + value);
                            x -= Math.log(((ContinuousDiscrete) var).getLimit(value + 1) - ((ContinuousDiscrete) var).getLimit(value));

                        }
                    }
                }

            }

        }

        return x;
    }

    /* This method computes the log of the probability of a database cases
     * taking the dynamic network as model. No missing values are assumed.
     * 
     */
    public double ScoreLogDynamicNoMissing(String dir, String file, int varscore, boolean[] nonarti) throws IOException {
        CaseListMem cases;
        BufferedReader casesreader;
        String line;
        int[] crow, var;
        int i, j, nnodes, totaln, l;
        String[] parts;
        double[] rawline, rawlineold, coe;
        Node x;
        double y, z, sco;

        cases = new CaseListMem(nodes);
        nnodes = nodes.size();
        totaln = nodes.size();

        casesreader = new BufferedReader(new FileReader(dir + "/" + file));
        line = casesreader.readLine();
        line = casesreader.readLine();
        parts = line.split(",");
        rawlineold = new double[totaln];
        rawline = new double[totaln];
        crow = new int[totaln];
        sco = 0.0;
        l = 0;
        i = 0;
        while (i < totaln) {
            //      System.out.println("i = " + i + "parts[i] = " + parts[i]);

            x = nodes.elementAt(i);

            if (varscore == 2) {
                while (!nonarti[l] && !(vartypes[i] == 4  )&& !(vartypes[i] == 6  )) {
                    l++;
                }
            }

            switch (vartypes[i]) {
                case Node.FINITE_STATES: {
                    crow[i] = ((FiniteStates) x).getId(parts[l].trim());
                    rawlineold[i] = crow[i];
                    break;
                }
                case Node.CONTINUOUS: {
                    rawlineold[i] = Double.parseDouble(parts[l]);
                    crow[i] = -1;
                    break;
                }
                case 5: {
                    y = Double.parseDouble(parts[l]);
                    crow[i] = ((ContinuousDiscrete) x).getCase(y);
                    rawlineold[i] = y;
                    break;
                }
                case Node.MIXED: {
                    rawlineold[i] = Double.parseDouble(parts[l]);
                    crow[i] = ((ContinuousDiscrete) x).getCase(rawlineold[i]);
                    break;
                }
                case 4: {
                    coe = (double[]) linearcoe[i];
                    var = (int[]) linearvar[i];
                    z = coe[0];
                    for (j = 1; j < coe.length; j++) {
                        z += coe[j] * rawlineold[var[j]];
                    }
                    rawlineold[i] = z;
                    crow[i] = ((ContinuousDiscrete) x).getCase(z);
                    break;
                }

                case 6: {
                    crow[i] = ((ContinuousDiscrete) x).getCase(0.0);
                    rawlineold[i] = 0.0;
                    break;
                }

            }
            i++;
           if ( !(vartypes[i] == 4  )&& !(vartypes[i] == 6  )) {    l++;}
        }

        cases.addCase(crow);

        while ((line = casesreader.readLine()) != null) {

            crow = new int[totaln];
            parts = line.split(",");
            i = 0;
            l = 0;
            while (i < totaln) {
                //      System.out.println("i = " + i + "parts[i] = " + parts[i]);

                x = nodes.elementAt(i);
                if (varscore == 2) {
                    while (!nonarti[l] && !(vartypes[i] == 4  )&& !(vartypes[i] == 6  )) {
                        l++;
                    }
                }
                switch (vartypes[i]) {
                    case Node.FINITE_STATES: {
                        crow[i] = ((FiniteStates) x).getId(parts[l].trim());
                        rawline[i] = crow[i];
                        break;
                    }
                    case Node.CONTINUOUS: {
                        y = Double.parseDouble(parts[l]);
                        crow[i] = -1;
                        rawline[i] = y;
                        break;
                    }
                    case 5: {
                        y = Double.parseDouble(parts[l]);
                        crow[i] = ((ContinuousDiscrete) x).getCase(y);
                        rawline[i] = y;
                        break;
                    }
                    case Node.MIXED: {
                        y = Double.parseDouble(parts[l]);
                        crow[i] = ((ContinuousDiscrete) x).getCase(y);
                        rawline[i] = y;
                        break;
                    }
                    case 4: {
                        coe = (double[]) linearcoe[i];
                        var = (int[]) linearvar[i];
                        z = coe[0];
                        for (j = 1; j < coe.length; j++) {
                            z += coe[j] * rawline[var[j]];
                        }
                        rawline[i] = z;
                        crow[i] = ((ContinuousDiscrete) x).getCase(z);
                        break;
                    }

                    case 6: {
                        var = (int[]) linearvar[i];
                        y = rawline[var[1]] - rawlineold[var[0]];
                        crow[i] = ((ContinuousDiscrete) x).getCase(y);
                        rawline[i] = y;
                        break;
                    }
                }
                i++;
                if ( !(vartypes[i] == 4  )&& !(vartypes[i] == 6  )) {    l++;}
            }

            for(i=0;i<rawline.length;i++) {rawlineold[i] = rawline[i];}

            cases.addCase(crow);

        }

        if (varscore == 0) {
            sco = ScoreLogDynamicNoMissing(cases);
        } else {
            if ((varscore == 1) || (varscore == 2)) {
                sco = ScoreLogDynamicNoMissingAc(cases);
            }
        }
        return sco;
    }

    public void span(String[] parts, int crow[], double rawlineold[]) {
       int[] var;
       int i,j,k;
       Node x;
       double[] coe;
       double y,z;
       
       int totaln = nodes.size();
       
       
       
           i=0;
           while (i < totaln) {
            //      System.out.println("i = " + i + "parts[i] = " + parts[i]);

            x = nodes.elementAt(i);
             

            switch (vartypes[i]) {
                case Node.FINITE_STATES: {
                    crow[i] = ((FiniteStates) x).getId(parts[i].trim());
                    rawlineold[i] = crow[i];
                    break;
                }
                case Node.CONTINUOUS: {
                    rawlineold[i] = Double.parseDouble(parts[i]);
                    crow[i] = -1;
                    break;
                }
                case 5: {
                    y = Double.parseDouble(parts[i]);
                    crow[i] = ((ContinuousDiscrete) x).getCase(y);
                    rawlineold[i] = y;
                    break;
                }
                case Node.MIXED: {
                    rawlineold[i] = Double.parseDouble(parts[i]);
                    crow[i] = ((ContinuousDiscrete) x).getCase(rawlineold[i]);
                    break;
                }
                case 4: {
                    coe = (double[]) linearcoe[i];
                    var = (int[]) linearvar[i];
                    z = coe[0];
                    for (j = 1; j < coe.length; j++) {
                        z += coe[j] * rawlineold[var[j]];
                    }
                    rawlineold[i] = z;
                    crow[i] = ((ContinuousDiscrete) x).getCase(z);
                    break;
                }

                case 6: {
                    crow[i] = ((ContinuousDiscrete) x).getCase(0.0);
                    rawlineold[i] = 0.0;
                    break;
                }

            }
            i++;
           
        }
        
        
       
       
        
      
        
    }
    
    
     public void span(String[] parts, int[] crow, double[] rawline, double rawlineold[] ) {
       int[] var;
       int i,j;
       Node x;
       double[] coe;
       double y,z;
       
       int totaln = nodes.size();
       
       
           i=0;
           while (i < totaln) {
            //      System.out.println("i = " + i + "parts[i] = " + parts[i]);

          

                x = nodes.elementAt(i);
             
                switch (vartypes[i]) {
                    case Node.FINITE_STATES: {
                        crow[i] = ((FiniteStates) x).getId(parts[i].trim());
                        rawline[i] = crow[i];
                        break;
                    }
                    case Node.CONTINUOUS: {
                        y = Double.parseDouble(parts[i]);
                        crow[i] = -1;
                        rawline[i] = y;
                        break;
                    }
                    case 5: {
                        y = Double.parseDouble(parts[i]);
                        crow[i] = ((ContinuousDiscrete) x).getCase(y);
                        rawline[i] = y;
                        break;
                    }
                    case Node.MIXED: {
                        y = Double.parseDouble(parts[i]);
                        crow[i] = ((ContinuousDiscrete) x).getCase(y);
                        rawline[i] = y;
                        break;
                    }
                    case 4: {
                        coe = (double[]) linearcoe[i];
                        var = (int[]) linearvar[i];
                        z = coe[0];
                        for (j = 1; j < coe.length; j++) {
                            z += coe[j] * rawline[var[j]];
                        }
                        rawline[i] = z;
                        crow[i] = ((ContinuousDiscrete) x).getCase(z);
                        break;
                    }

                    case 6: {
                        var = (int[]) linearvar[i];
                        y = rawline[var[1]] - rawlineold[var[0]];
                        crow[i] = ((ContinuousDiscrete) x).getCase(y);
                        rawline[i] = y;
                        break;
                    }
                }
                i++;
              
            }

       
       
        
        
    }
    
    
    
    static int computeIndex(int[] val, Vector vars, Vector totalvars) {
        int index = 0;
        int nv, i, j;
        Node var;
        int temp, factor;
        int size;

        nv = vars.size(); // Number of variables.

        factor = 1;

        for (i = nv - 1; i >= 0; i--) {
            var = (Node) vars.elementAt(i);

            j = locate(var, totalvars);

            temp = val[j];

            index += temp * factor;

            size = 1;
            if (var.getTypeOfVariable() == 1) {
                size = ((FiniteStates) var).getNumStates();
            } else {
                if (var.getTypeOfVariable() == 3) {
                    size = ((ContinuousDiscrete) var).getNumberofIntevals();
                }

            }

            factor *= size;
        }

        return index;
    }

    static int computeIndex(int[] val, int[] valn, Vector vars, Vector totalvars) {
        int index = 0;
        int nv, i, j;
        Node var;
        int temp, factor;
        int size;

        nv = vars.size(); // Number of variables.

        factor = 1;

        for (i = nv - 1; i >= 1; i--) {
            var = (Node) vars.elementAt(i);

            j = locate(var, totalvars);

            temp = val[j];

            index += temp * factor;

            size = 1;
            if (var.getTypeOfVariable() == 1) {
                size = ((FiniteStates) var).getNumStates();
            } else {
                if (var.getTypeOfVariable() == 3) {
                    size = ((ContinuousDiscrete) var).getNumberofIntevals();
                }

            }

            factor *= size;
        }

        var = (Node) vars.elementAt(0);
        j = locaten(var, totalvars);

        temp = valn[j];

        index += temp * factor;

        return index;
    }

    static int locate(Node var, Vector list) {
        int pos = -1;
        int i, size;
        Node varx;

        size = list.size();
        for (i = 0; i < size; i++) {
            varx = (Node) list.elementAt(i);
            if (varx.getName().equals(var.getName())) {
                pos = i;
                break;
            }
        }

        return pos;

    }

    static int locaten(Node var, Vector list) {
        int pos = -1;
        int i, size;
        Node varx;
        String name;

        size = list.size();
        for (i = 0; i < size; i++) {
            varx = (Node) list.elementAt(i);
            name = varx.getName();
            name = name + "n";
            if (name.equals(var.getName())) {
                pos = i;
                break;
            }
        }

        return pos;

    }

    public Bnet expand(int n) {
        Bnet output = new Bnet();
        int i, j, k, nnodes;
        Node var, var2;
        NodeList anodes, anodesp;
        Potential pot;
        Relation rel;

        nnodes = nodes.size();
        anodes = new NodeList();
        for (j = 0; j < nnodes; j++) {
            var = nodes.elementAt(j).copy();
            anodes.insertNode(var);
            var.setName(var.getName() + "[0]");
            try {
                output.addNode(var);
            } catch (InvalidEditException iee) {
            };
        }

        for (j = 0; j < nnodes; j++) {
            var2 = anodes.elementAt(j);

            for (i = 0; i < nnodes; i++) {
                if (time0arcs[i][j]) {
                    var = anodes.elementAt(i);

                    try {
                        output.createLink(var, var2, true);
                    } catch (InvalidEditException iee) {
                    };
                }
            }
            pot = time0potentials[j].copy();
            replaceVar(pot, anodes);
            rel = new Relation(pot);
            output.addRelation(rel);

        }

        for (i = 1; i < n; i++) {
            anodesp = anodes;
            anodes = new NodeList();
            for (j = 0; j < nnodes; j++) {
                var = nodes.elementAt(j).copy();
                anodes.insertNode(var);
                var.setName(var.getName() + "[" + Integer.toString(i) + "]");
                try {
                    output.addNode(var);
                } catch (InvalidEditException iee) {
                };
            }
            for (j = 0; j < nnodes; j++) {
                var2 = anodes.elementAt(j);
                for (k = 0; k < nnodes; k++) {
                    if (temporalarcs[k][j]) {
                        var = anodesp.elementAt(k);

                        try {
                            output.createLink(var, var2, true);
                        } catch (InvalidEditException iee) {
                        };
                    }
                }

                pot = temporalpotentials[j].copy();
                pot.getVariables().setElementAt(var2, 0);
                replaceVar2(pot, anodesp);

            }
        }

        return output;
    }

    private void replaceVar(Potential pot, NodeList anodes) {
        int i, n, j;
        Vector pnodes;
        Node var, var2;
        String name;

        pnodes = pot.getVariables();
        n = pnodes.size();
        for (i = 0; i < n; i++) {
            var = (Node) pnodes.elementAt(i);
            for (j = 0; j < anodes.size(); j++) {
                var2 = anodes.elementAt(j);
                name = var2.getName();
                if (name.matches(var.getName() + "\\[\\d+\\]")) {
                    pnodes.setElementAt(var2, i);
                }

            }
        }

    }

    private void replaceVar2(Potential pot, NodeList anodes) {
        int i, n, j;
        Vector pnodes;
        Node var, var2;
        String name;

        pnodes = pot.getVariables();
        n = pnodes.size();
        for (i = 1; i < n; i++) {
            var = (Node) pnodes.elementAt(i);
            for (j = 0; j < anodes.size(); j++) {
                var2 = anodes.elementAt(j);
                name = var2.getName();
                if (name.matches(var.getName() + "\\[\\d+\\]")) {
                    pnodes.setElementAt(var2, i);
                }

            }
        }

    }

}

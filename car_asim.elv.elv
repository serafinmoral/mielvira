// Influence Diagram
//   Elvira format 

idiagram  "/home/gte/smc/textos/docencia/ia/murcia/Ejemplos de Redes/car_asim.elv" { 

// Network Properties

kindofgraph = "directed";
visualprecision = "0.00";
version = 1.0;
default node states = ("present" , "absent");

// Variables 

node NetValue(continuous) {
kind-of-node = utility;
type-of-variable = continuous;
pos_x =875;
pos_y =465;
relevance = 7.0;
purpose = "";
min = 0.0;
max = 1031.0;
precision = 2;
}

node Cars_Conditions(finite-states) {
kind-of-node = chance;
type-of-variable = finite-states;
pos_x =140;
pos_y =140;
relevance = 7.0;
purpose = "";
num-states = 2;
states = (Peach Lemon);
}

node SecondTestResults(finite-states) {
kind-of-node = chance;
type-of-variable = finite-states;
pos_x =257;
pos_y =306;
relevance = 7.0;
purpose = "";
num-states = 3;
states = (NoResults Defects0 Defects1);
}

node FirstTestResults(finite-states) {
kind-of-node = chance;
type-of-variable = finite-states;
pos_x =394;
pos_y =115;
relevance = 7.0;
purpose = "";
num-states = 4;
states = (NoResults Defects0 Defects1 Defects2);
}

node PurchaseDecision(finite-states) {
kind-of-node = decision;
type-of-variable = finite-states;
pos_x =809;
pos_y =297;
relevance = 7.0;
purpose = "";
num-states = 2;
states = (No Yes);
}

node SecondTestDecision(finite-states) {
kind-of-node = decision;
type-of-variable = finite-states;
pos_x =706;
pos_y =118;
relevance = 7.0;
purpose = "";
num-states = 2;
states = (NoTest Differential);
}

node FirstTestDecision(finite-states) {
kind-of-node = decision;
type-of-variable = finite-states;
pos_x =485;
pos_y =432;
relevance = 7.0;
purpose = "";
num-states = 4;
states = (NoTest Steering Transmission FuelElectrical);
}

// Links of the associated graph:

link Cars_Conditions FirstTestResults;

link Cars_Conditions NetValue;

link Cars_Conditions SecondTestResults;

link FirstTestDecision FirstTestResults;

link FirstTestDecision NetValue;

link FirstTestDecision PurchaseDecision;

link FirstTestDecision SecondTestDecision;

link FirstTestDecision SecondTestResults;

link FirstTestResults PurchaseDecision;

link FirstTestResults SecondTestDecision;

link FirstTestResults SecondTestResults;

link PurchaseDecision NetValue;

link SecondTestDecision NetValue;

link SecondTestDecision PurchaseDecision;

link SecondTestDecision SecondTestResults;

link SecondTestResults PurchaseDecision;

//Network Relationships: 

relation Cars_Conditions { 
comment = "";
deterministic=false;
values= table (# 0.2 );
}

relation SecondTestResults FirstTestDecision FirstTestResults SecondTestDecision Cars_Conditions { 
comment = "";
deterministic=false;
values= table (# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.89 0.67 0.0 0.0 0.44 1.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.11 0.33 0.0 0.0 0.56 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 );
}

relation FirstTestResults FirstTestDecision Cars_Conditions { 
comment = "";
deterministic=false;
values= table (# # # # # # # # 0.0 0.0 0.9 0.4 0.9 0.4 0.8 0.13 0.0 0.0 0.1 0.6 0.1 0.6 0.2 0.53 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.34 );
}

relation FirstTestDecision FirstTestResults { 
comment = "";
kind-of-relation = constraint;
deterministic=false;
values=logical-expression(FirstTestDecision in {NoTest} <-> FirstTestResults in {NoResults});
}

relation SecondTestDecision SecondTestResults { 
comment = "";
kind-of-relation = constraint;
deterministic=false;
values=logical-expression(SecondTestDecision in {NoTest} <-> SecondTestResults in {NoResults});
}

relation NetValue FirstTestDecision SecondTestDecision PurchaseDecision Cars_Conditions { 
comment = "";
kind-of-relation = utility;
deterministic=false;
values= table (800.0 940.0 1000.0 1000.0 0.0 0.0 0.0 0.0 891.0 1031.0 1000.0 1000.0 0.0 0.0 0.0 0.0 890.0 1030.0 1000.0 1000.0 886.0 10.0 1000.0 1000.0 887.0 1027.0 1000.0 1000.0 0.0 0.0 0.0 0.0 );
}

}

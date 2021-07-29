// Influence Diagram
//   Elvira format 

idiagram  "/home/gte/smc/textos/docencia/ia/murcia/Ejemplos de Redes/apples.elv" { 

// Network Properties

kindofgraph = "directed";
visualprecision = "0.00";
version = 1.0;
default node states = ("present" , "absent");

// Variables 

node Sick(finite-states) {
kind-of-node = chance;
type-of-variable = finite-states;
pos_x =111;
pos_y =77;
relevance = 7.0;
purpose = "";
num-states = 2;
states = (sick not);
}

node Dry(finite-states) {
kind-of-node = chance;
type-of-variable = finite-states;
pos_x =244;
pos_y =75;
relevance = 7.0;
purpose = "";
num-states = 2;
states = (dry not);
}

node Loses(finite-states) {
kind-of-node = chance;
type-of-variable = finite-states;
pos_x =174;
pos_y =195;
relevance = 7.0;
purpose = "";
num-states = 2;
states = (yes no);
}

node Sickf(finite-states) {
kind-of-node = chance;
type-of-variable = finite-states;
pos_x =377;
pos_y =150;
relevance = 7.0;
purpose = "";
num-states = 2;
states = (sick not);
}

node Dryf(finite-states) {
kind-of-node = chance;
type-of-variable = finite-states;
pos_x =525;
pos_y =149;
relevance = 7.0;
purpose = "";
num-states = 2;
states = (dry not);
}

node Losesf(finite-states) {
kind-of-node = chance;
type-of-variable = finite-states;
pos_x =467;
pos_y =269;
relevance = 7.0;
purpose = "";
num-states = 2;
states = (yes no);
}

node Treat(finite-states) {
kind-of-node = decision;
type-of-variable = finite-states;
pos_x =271;
pos_y =206;
relevance = 7.0;
purpose = "";
num-states = 2;
states = (treat not);
}

node Harvest(continuous) {
kind-of-node = utility;
type-of-variable = continuous;
pos_x =320;
pos_y =274;
relevance = 7.0;
purpose = "";
min = 0.0;
max = 1.0;
precision = 2;
}

// Links of the associated graph:

link Dry Dryf;

link Dry Loses;

link Dryf Losesf;

link Sick Loses;

link Sick Sickf;

link Sickf Harvest;

link Sickf Losesf;

link Treat Harvest;

link Treat Sickf;

//Network Relationships: 

relation Loses Sick Dry { 
comment = "";
deterministic=false;
values= table (# # # # 0.05 0.1 0.15 0.98 );
}

relation Losesf Sickf Dryf { 
comment = "";
deterministic=false;
values= table (# # # # 0.05 0.1 0.15 0.98 );
}

relation Dryf Dry { 
comment = "";
deterministic=false;
values= table (# # 0.4 0.95 );
}

relation Sickf Sick Treat { 
comment = "";
deterministic=false;
values= table (# # # # 0.8 0.01 0.99 0.98 );
}

relation Harvest Sickf Treat { 
comment = "";
kind-of-relation = utility;
deterministic=false;
values= table (-5000.0 3000.0 12000.0 20000.0 );
}

relation Dry { 
comment = "";
deterministic=false;
values= table (# 0.9 );
}

relation Sick { 
comment = "";
deterministic=false;
values= table (# 0.9 );
}

}

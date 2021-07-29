This elvira-weka package provides WEKA bindings for Elvira platform. 
WEKA is the Waikato Environment for Knowledge Analysis (http://www.cs.waikato.ac.nz/~ml).
elvira-weka package allows running some Elvira classifiers and utilities from within WEKA, 
in particular from within WEKA's graphical user interfaces called Explorer
and KnowledgeFlow.

--------------
 INSTALLATION
--------------

1) Download WEKA (http://sourceforge.net/projects/weka/) and install it.
   Add weka.jar to your CLASSPATH environment variable.

2) Download elvira-weka from leo.ugr.es cvs. To compile it, weka.jar and bayelvira2.jar
	 have to have been added to your CLASSPATH environment variable.. 
	 After that, add elvira-weka.jar to your CLASSPATH environment variable.
    
4) Start WEKA user interface. 
   The new classes will appear in Weka interface with a "E_" suffix to indicate these classes
   do not belong to original Weka software.

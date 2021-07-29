 /**
  * LogFactorial.java
  * Esta clase implementa una forma eficiente de calcular Logaritmos de 
  * factoriales de forma reiterada.
  */
import java.util.Vector;
import java.io.*;

public class LogFactorial{

Vector index;
Vector computed;


public LogFactorial(){
	
    Double valfact = new Double(0.0);
    Integer val = new Integer(0);
    index = new Vector();
    computed = new Vector();
    index.addElement(val);
    computed.addElement(valfact);
    val = new Integer(1);
    index.addElement(val);
    computed.addElement(valfact);
}
	
	
	
public double logFactorial(int x){
	
    int i,j,valbefore,valafter;
    double valfact;
    Integer valx;
    Double valcomputed;
    
    if(x > ((Integer)index.lastElement()).intValue()){
	valfact = ((Double)computed.lastElement()).doubleValue();
	for(j=((Integer)index.lastElement()).intValue(); j<x ; j++){
	    valfact+=(Math.log(j+1));
	}
	valcomputed = new Double(valfact);
	computed.addElement(valcomputed);
	valx = new Integer(x);
	index.addElement(valx);
	
    }else {
	i = constains(x);
	
	if(x == ((Integer)index.elementAt(i)).intValue())
	    return ((Double)computed.elementAt(i)).doubleValue();
	else {
	    if( x > ((Integer)index.elementAt(i)).intValue()){
		valx = new Integer(x);
		i++;
		index.insertElementAt(valx,i);
		computed.insertElementAt(new Double(-1.0),i);
	    }else{
		valx = new Integer(x);
		index.insertElementAt(valx,i);
		computed.insertElementAt(new Double(-1.0),i);
	    }
	}	
	
	valbefore = ((Integer)index.elementAt(i-1)).intValue();
	valafter = ((Integer)index.elementAt(i+1)).intValue();
	
	if((valafter - x) < (x - valbefore)) {
	    valfact = ((Double)computed.elementAt(i+1)).doubleValue();
	    for(j = valafter ; j > x ; j--){
		valfact-=(Math.log(j));
	    }
	}else{
	    valfact = ((Double)computed.elementAt(i-1)).doubleValue();
	    for(j = valbefore;j<x;j++){
		valfact+=(Math.log(j+1));
	    }
	}
	valcomputed = new Double(valfact);
	computed.setElementAt(valcomputed,i);
	
	
    }

    return valfact;
}	
    
    
    private int constains(int x){
	
	int left,right,midle;
	boolean found = false;
	
	left = 0;
	right = index.size()-1;
	midle = (left + right)/2;
	
	while((left <= right) & (!found)){
	    midle = (left + right)/2;
	    if(x == ((Integer)index.elementAt(midle)).intValue())
		found = true;
	    if(x < ((Integer)index.elementAt(midle)).intValue())
		right = midle - 1;
	    else left = midle + 1;
	    
	}
	
	return midle;
    }
    
    
}//end of class

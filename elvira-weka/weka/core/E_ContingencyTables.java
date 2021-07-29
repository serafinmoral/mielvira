package weka.core;

/**
 * This class inherits from the weka.core.ContingencyTables class of weka. 
 * The added functionality is the implementing of new statistical routines 
 * for contingency tables.
 */
public class E_ContingencyTables extends ContingencyTables{

    /** The natural logarithm of 2 */
  private static double log2 = Math.log(2);

  /**
   * Help method for computing entropy.
   */
  private static double lnFunc(double num){
    
    // Constant hard coded for efficiency reasons
    if (num < 1e-6) {
      return 0;
    } else {
      return num * Math.log(num);
    }
  }
  
  private static double[] reps(double[] array,int n, double svalue){
       
        double masa=1;

        double[] l=new double[n];
        System.arraycopy(array,0,l,0,n);
        double min=l[0];
        int cont=0;
        for (int i=0;i<n;i++){
            if (min>l[i]) min=l[i];
        }
        for (int i=0;i<n;i++){
            if (min==l[i]) cont++;
        }
        if (svalue<=cont)
            masa=svalue;
        else
            masa=cont;
        for (int i=0;i<n;i++){
            if (min==l[i]) l[i]=l[i]+(masa/cont);
        }
        masa=masa-cont;
        if (masa>0.0001)
            return reps(l,n,masa);
        else
            return l;

    }
  
  /**
   * Computes the imprecise entropy for s=1 of the given array.
   *
   * @param array the array
   * @return the entropy
   */
  public static double entropyGiniIndex(double[] array) {
  
    double gini = 0;
    double total=0;
    for (int j = 0; j < array.length; j++) {
        total+=array[j];
    }
    for (int j = 0; j < array.length; j++) {
      if (array[j] > 0) {
        gini -= (array[j]/total) * (array[j]/total);
      }
    }
   
    return gini;
  
  }  
  
  /**
   * Computes the imprecise entropy for s=1 of the given array.
   *
   * @param array the array
   * @return the entropy
   */
  public static double entropyImprecise(double[] array, double svalue) {

    double returnValue = 0, sum = 0;
    double[] arraynew=reps(array,array.length, svalue);
    
    for (int i = 0; i < arraynew.length; i++) {
      if (arraynew[i]>0)
        returnValue -= arraynew[i]*Utils.log2(arraynew[i]);
      sum += array[i];
    }
    sum+=svalue;
    if (Utils.eq(sum, 0)) {
      return 0;
    } else {
      returnValue/=sum;
      return (returnValue + Utils.log2(sum));
    }
  }
  
  /**
   * Computes the entropy of the given array.
   *
   * @param array the array
   * @return the entropy
   */
  public static double entropyLaplace(double[] array) {

    double returnValue = 0, sum = 0;

    for (int i = 0; i < array.length; i++) {
      returnValue -= lnFunc(array[i]+1);
      sum += array[i];
    }
    if (Utils.eq(sum, 0)) {
      return 0;
    } else {
      return (returnValue + lnFunc(sum)) / (sum * log2);
    }
  }

   /**
   * Computes the entropy of the given array.
   *
   * @param array the array
   * @return the entropy
   */
  public static double entropyL1O(double[] array) {

    double returnValue = 0, sum = 0;

    for (int i = 0; i < array.length; i++) {
      returnValue -= lnFunc(array[i]);
      sum += array[i];
    }
    sum+=array.length-1;
    if (Utils.eq(sum, 0)) {
      return 0;
    } else {
      return (returnValue + lnFunc(sum)) / (sum * log2);
    }
  }
 

  /**
   * Main method for testing this class.
   */
  public static void main(String[] ops) {

    double[] firstRow = {10, 5, 20};
    double[] secondRow = {2, 10, 6};
    double[] thirdRow = {5, 10, 10};
    double[][] matrix = new double[3][0];

    matrix[0] = firstRow; matrix[1] = secondRow; matrix[2] = thirdRow;
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
	System.out.print(matrix[i][j] + " ");
      }
      System.out.println();
    }
    System.out.println("Chi-squared probability: " +
		       ContingencyTables.chiSquared(matrix, false));
    System.out.println("Chi-squared value: " +
		       ContingencyTables.chiVal(matrix, false));
    System.out.println("Cochran's criterion fullfilled: " +
		       ContingencyTables.cochransCriterion(matrix));
    System.out.println("Cramer's V: " +
		       ContingencyTables.CramersV(matrix));
    System.out.println("Entropy of first row: " +
		       ContingencyTables.entropy(firstRow));
    System.out.println("Entropy conditioned on columns: " +
		       ContingencyTables.entropyConditionedOnColumns(matrix));
    System.out.println("Entropy conditioned on rows: " +
		       ContingencyTables.entropyConditionedOnRows(matrix));
    System.out.println("Entropy conditioned on rows (with Laplace): " +
		       ContingencyTables.entropyConditionedOnRows(matrix, matrix, 3));
    System.out.println("Entropy of rows: " +
		       ContingencyTables.entropyOverRows(matrix));
    System.out.println("Entropy of columns: " +
		       ContingencyTables.entropyOverColumns(matrix));
    System.out.println("Gain ratio: " +
		       ContingencyTables.gainRatio(matrix));
    System.out.println("Negative log2 of multiple hypergeometric probability: " +
		       ContingencyTables.log2MultipleHypergeometric(matrix));
    System.out.println("Symmetrical uncertainty: " +
		       ContingencyTables.symmetricalUncertainty(matrix));
    System.out.println("Tau value: " +
		       ContingencyTables.tauVal(matrix));
    double[][] newMatrix = new double[3][3];
    newMatrix[0][0] = 1; newMatrix[0][1] = 0; newMatrix[0][2] = 1;
    newMatrix[1][0] = 0; newMatrix[1][1] = 0; newMatrix[1][2] = 0;
    newMatrix[2][0] = 1; newMatrix[2][1] = 0; newMatrix[2][2] = 1;
    System.out.println("Matrix with empty row and column: ");
    for (int i = 0; i < newMatrix.length; i++) {
      for (int j = 0; j < newMatrix[i].length; j++) {
	System.out.print(newMatrix[i][j] + " ");
      }
      System.out.println();
    }
    System.out.println("Reduced matrix: ");
    newMatrix = ContingencyTables.reduceMatrix(newMatrix);
    for (int i = 0; i < newMatrix.length; i++) {
      for (int j = 0; j < newMatrix[i].length; j++) {
	System.out.print(newMatrix[i][j] + " ");
      }
      System.out.println();
    }
  }
}









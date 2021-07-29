/**
 * Implements a binary heap.
 *
 * Last modified: 9/2/99.
*/


import NodeQueue;

public class PriorityQueue {

  /**
   * Creates a new PriorityQueue.
   * @param infinity a value grater than any other one.
  */
  
  public PriorityQueue(NodeQueue infinity) {

    currentSize = 0;
    getArray(DEFAULT_CAPACITY);
    array[0] = infinity;
  }
  
  
  

  /**
   * Inserts into the queue keeping order. Duplicates are allowed.
   * @param X the element to insert.
  */
  
  public void insert(NodeQueue X) {
  
    checkSize();
    
    
    int hole = ++currentSize;
    for ( ; X.greaterThan(array[hole/2]); hole /=2)
      array[hole] = array[hole/2];
    array[hole] = X;
  }
  
  /**
   * Find the item with highest priority.
   * @return the highest priority item.
  */
  
  public NodeQueue findMax() {
    
    return array[1];
  }
  

  /**
   * Removes the element with highest priority.
  */
  
  public NodeQueue deleteMax() {
    
    NodeQueue maxItem = findMax();
    
    array[1] = array[currentSize--];
    percolateDown(1);
    
    return maxItem;
  }
  
  /**
   * @return true if the queue is empty, false otherwise.
  */
  
  public boolean isEmpty() {
  
    return currentSize == 0;
  }
  
  
  /**
   * Makes the queue be empty.
  */
  
  public void makeEmpty() {
   
    currentSize = 0;
  }
  
  
  /**
   * Gives the number of elements in the heap.
   * @return the number of elements in the queue.
   */
  
  public int size() {
   
    return currentSize;
  }
  
  
  private int currentSize; // Number of elements in the queue.
  private NodeQueue [] array; // The heap array.
  private static final int DEFAULT_CAPACITY = 11;
  
  /**
   * Allocates the binary heap array.
   * Includes an extra cell for the centinel (infinity).
   * @param newMaxSize the capacity of the heap.
  */
  
  private void getArray(int newMaxSize) {

    array = new NodeQueue[newMaxSize+1];
  }
  

  /**
   * Private method that doubles the heap array if full.
  */
  
  private void checkSize() {
    
    if (currentSize == array.length -1) {
      NodeQueue [] oldArray = array;
      
      getArray(currentSize * 2);
      for (int i = 0; i < oldArray.length; i++)
	array[i] = oldArray[i];
    }
  }


  /**
   * Internal method to percolate down in the tree.
   * @param hole the index at which the percolate begins.
  */
  
  private void percolateDown(int hole) {
   
    int child;
    NodeQueue tmp = array[hole];
    
    for ( ; hole * 2 <= currentSize; hole = child) {
      child = hole * 2;
      if (child != currentSize && array[child+1].greaterThan(array[child]))
	child++;
      if (array[child].greaterThan(tmp))
	array[hole] = array[child];
      else
	break;
    }
    array[hole] = tmp;
  }
}
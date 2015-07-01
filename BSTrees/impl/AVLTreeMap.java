package impl;


/**
 * AVLTreeMap
 * 
 * The class that finishes AVLTreeMapAbs for implementing an AVL
 * tree. Specifically it contains the fixup() method.
 * 
 * @author 
 * CSCI 345, Wheaton College
 * @param <K> The key-type of the map
 * @param <V> The value-type of the map
 */

public class AVLTreeMap<K extends Comparable<K>, V> extends AVLTreeMapAbs<K, V>{

 
    /**
     * Gratuitous, perfunctory constructor.
     */
    public AVLTreeMap() {
        root = null;
        searchTrace = null;
    }

    
    /**
     * Fix-up this tree. 
     * PRECONDITIONS: put() has just been called and searchTrace contains
     * a stack indicating the route we took from the root to the 
     * "prospective parent" (it does not include the new node itself,
     * if a new node was added).
     */
    protected void fixup() {
    	
    	Node current = null;
    	Node previous = null;
    	Node replacement = null;
    	
    	while (!searchTrace.isEmpty()){
    		// pop a new current off the stack and update previous:
    		previous = current;
    		current = searchTrace.pop();

    		// replace current's previous child with replacement (if any exists):
    		if (previous != null && current.left != null && current.left.equals(previous)){
    			current.left = replacement;
    		}
    		else if (previous != null && current.right != null && current.right.equals(previous)){
    			current.right = replacement;

    		}
    		else {}


    		// check for imbalance in current node:
    		if (current.left  != null) {current.left.softRecompute();
    		}
    		
    		if (current.right != null) {current.right.softRecompute();	
    		}

    		
    		current.softRecompute();

    		
    		if (current.balance > 1){
    			// LEFT LARGER

    			// check if current.left's balance is to left or right:
    			current.left.softRecompute();
    			if (current.left.balance < 0){
    				// it's a left-right imbalance
    				Node temp = current.left;
    				Node temp2 = current.left.right.left;

    				current.left = current.left.right;
    				current.left.left = temp;
    				current.left.left.right = temp2;

    			}

    			// rotateLeft:
    			replacement = current.left;
    			current.left = replacement.right;
    			replacement.right = current;
    			

    			if (current.equals(root)){
    				root = replacement;
    			}

    		} else if (current.balance < -1){	
    			// RIGHT LARGER


    			// check if current.right's balance is to left or right:
    			current.right.softRecompute();
    			if (current.right.balance > 0){
    				// it's a right-left imbalance
    				Node temp = current.right;
    				Node temp2 = current.right.left.right;

    				current.right = current.right.left;
    				current.right.right = temp;
    				current.right.right.left = temp2;

    			}

    			// rotateRight:
    			replacement = current.right;
    			current.right = replacement.left;
    			replacement.left = current;


    			if (current.equals(root)){
    				root = replacement;

    			}

    		} else {
    			replacement = current;
    		}
    	current.softRecompute();
    	} 
    	return;
    }
    
    
}

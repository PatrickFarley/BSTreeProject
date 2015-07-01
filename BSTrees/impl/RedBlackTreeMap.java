package impl;

import impl.AVLTreeMapAbs.Node;

import java.util.Iterator;
import java.util.NoSuchElementException;

import adt.Map;
import adt.Stack;

/**
 * RedBlackTreeMap
 * 
 * An implementation of a (reduced, no-remove) map using 
 * a Red-Black Tree. This is a basic demonstration of a Red-Black Tree
 * with the really hard parts (ie, removal) eliminated.
 * 
 * @author Thomas VanDrunen
 * CSCI 345, Wheaton College
 * July 29, 2014
 * @param <K> The key-type of the map
 * @param <V> The value-type of the map
 */

public class RedBlackTreeMap<K extends Comparable<K>, V> implements Map<K, V> {

    
    // --------- Exceptions ---------------
    
    /**
     * Exception class to indicate that a node's two subtrees have
     * different black heights.
     */
    private static class InconsistentBlackHeightException extends RuntimeException {

        private String keyRep;
        private int leftHeight, rightHeight;

        public InconsistentBlackHeightException(String keyRep, int leftHeight, int rightHeight) {
            this.keyRep = keyRep;
            this.leftHeight = leftHeight;
            this.rightHeight = rightHeight;
        }

        @Override
        public String getMessage() {
            return keyRep + " has left height " + leftHeight + 
                    " and right height " + rightHeight + ".";
        }
    }
    
    /**
     * Exception class to indicate that one of a node's children is red and
     * has a red child. This normally happens during insertion and should
     * be fixed up.
     */
    private static class DoubleRedException extends Exception {}

    /**
     * Exception class to indicate that one of a node's children is red and
     * has a red child; differs from plain old DoubleRedException is that 
     * this is thrown when the situation is found at a time other than the
     * tree is currently being modified.
     */
    private static class DoubleRedWhenVerifyingException extends RuntimeException { }
    
    /**
     * Are we in debugging mode? 
     */
    public static boolean DEBUG = false;

    //  ------------ Node classes ------------------
    // For RB trees, we want "null" links to be able to do things,
    // so we'll make a null-like object
    
    /**
     * The algorithms will be implemented recursively in the nodes,
     * so the important operations are part of the Node interface.
     */
    private interface Node<KK, VV> {
        /**
         * Add an association for a key. Since this might involve a
         * tree rotation, this returns a node, the one that should take
         * the receiver's place in the tree. If no rotation happens,
         * the returned node will be the receiver; if there is a rotation,
         * the returned node will be whatever node is rotated up.
         * @param key The key to this association
         * @param val The value to which this key is associated
         * @return The node to stand in this one's place (possibly still this one)
         * @throws DoubleRedException If the work so far has resulted in two reds.
         */
        public Node<KK, VV> put(KK key, VV val) throws DoubleRedException;

        /**
         * Is this a red node?
         * @return True if it is red, false otherwise
         */
        public boolean isRed();
        
        /**
         * Determine the black height of the subtree rooted at this node.
         * An exception will be thrown if the black height is inconsistent,
         * or if a double red is detected.
         * @return The black height of the subtree rooted at this node.
         */
        public int blackHeight();
        
        /**
         * Does the map represented by the subtree rooted at this node
         * have the given key?
         * @param key The key to test.
         * @return true if there is an association for this key here, false otherwise.
         */
        public boolean containsKey(KK key);
        
        /**
         * Get the value for a key
         * @param key The key whose value we're retrieving.
         * @return The value associated with this key, null if none exists
         */
        public VV get(KK key);
    }

    
    /**
     * The (singleton) null object. It is an anonymous inner class.
     */
    private Node<K, V> nully = new Node<K, V>() {
        public Node<K, V> put(K key, V val) {
            return new RBNode(key, val, this, this);
        }

        /**
         * The null object is black.
         */
        public boolean isRed() {
            return false;
        }

        /**
         * The subtree here has 1 black height (the node itself).
         */
        public int blackHeight() {
            return 1;
        }

        /**
         * No key is contained here
         */
        public boolean containsKey(K key) {
            return false;
        }
        
        @Override
        public String toString() {
            return "(:)";
        }

        /**
         * No key is contained here
         */
        public V get(K key) {
            return null;
        }
    };
    

    /**
     * The class for all nodes besides the null node.
     */
    private class RBNode implements Node<K, V> {
        K key;
        V value;
        Node<K, V> left, right;
        boolean isRed;
        
        RBNode(K key, V value, Node left, Node right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
            this.isRed = true;
        }

        public boolean isRed() { return isRed; }
        
        /**
         * Add an association for a key. Since this might involve a
         * tree rotation, this returns a node, the one that should take
         * the receiver's place in the tree. If no rotation happens,
         * the returned node will be the receiver; if there is a rotation,
         * the returned node will be whatever node is rotated up.
         * @param key The key to this association
         * @param val The value to which this key is associated
         * @return The node to stand in this one's place (possibly still this one)
         * @throws DoubleRedException If the work so far has resulted in two reds.
         */
        public Node<K, V> put(K key, V val) throws DoubleRedException {
        	// work begins:

        		int compare = key.compareTo(this.key);
        		if (compare < 0) {	
        			// ADDING ON LEFT SIDE:
        			try {
        				left = left.put(key, val);
        			} catch (DoubleRedException dre){
        				// check the three cases:
        				RBNode child = (RedBlackTreeMap<K, V>.RBNode) left;
        				if (child.right.isRed()){
        					// it's case 2a:
        					case2aLeftRotate();	
        				} 
        				assert(child.left.isRed());
        				if (right.isRed()){
        					// it's case 1: 'uncle' case:
        					return case1Rotate();
        				} else {
        					// it's just case 2b:
        					return case2bLeftRotate();
        				}
        			}

        			if (this.isRed() && left.isRed()){
        				throw new DoubleRedException();
        			}
        			return this;
        		}

        		else if (compare == 0) {
        			value = val;
        			return this;
        		}

        		else { // if (compare > 0) 
        			// ADDING ON RIGHT SIDE:
        			try {
        				right = right.put(key, val);
        			} catch (DoubleRedException dre){
        				// check the three cases:
        				RBNode child = (RedBlackTreeMap<K, V>.RBNode) right;
        				if (child.left.isRed()){
        					// it's case 2a:
        					case2aRightRotate();
        				} 
        				assert(child.right.isRed());
        				if (left.isRed()){
        					// it's case 1: 'uncle' case:
        					return case1Rotate();
        				} else {
        					// it's just case 2b:
        					return case2bRightRotate();
        				}
        			}

        			if (this.isRed() && right.isRed()){
        				throw new DoubleRedException();
        			}
        			return this;
        		}
        }
        
        /*
         * Helper methods can go here:
         */
        private Node<K,V> case2bRightRotate() {
        	RBNode temp = (RBNode)this.right;
        	
        	this.right = temp.left;
        	this.isRed = true;
        	temp.left = this;
        	temp.isRed = false;
        	return temp;
        }
        
        private Node<K,V> case2bLeftRotate() {
        	RBNode temp = (RBNode)this.left;
        	
        	this.left = temp.right;
        	this.isRed = true;
        	temp.right = this;
        	temp.isRed = false;
        	return temp;
        }
        
        private void case2aRightRotate() {
        	RBNode temp = (RBNode)this.right;
			RBNode temp2 = (RBNode)temp.left;
			Node<K, V> temp3 = temp2.right;
			
			temp.left = temp3;
			temp2.right = temp;
			this.right = temp2;
        }
        
        private void case2aLeftRotate() {
        	RBNode temp = (RBNode)this.left;
			RBNode temp2 = (RBNode)temp.right;
			Node<K, V> temp3 = temp2.left;
			
			temp.right = temp3;
			temp2.left = temp;
			this.left = temp2;
        }
        
        private Node<K,V> case1Rotate(){
        	assert (this.isRed() == false);
        	
        	RBNode Tleft = (RBNode)left;
        	RBNode Tright = (RBNode)right;
        	
        	Tleft.isRed = false;
        	Tright.isRed = false;
        	
        	this.isRed = true;
        	this.left = Tleft;
        	this.right = Tright;
        	return this;
        }


        /**
         * Determine the black height of the subtree rooted at this node.
         * An exception will be thrown if the black height is inconsistent,
         * or if a double red is detected.
         * @return The black height of the subtree rooted at this node.
         */
        public int blackHeight() {
            if (isRed && (left.isRed() || right.isRed()))
                    throw new DoubleRedWhenVerifyingException();
                
            int leftBlackHeight = left.blackHeight(),
                rightBlackHeight = right.blackHeight();
            if (leftBlackHeight != rightBlackHeight)
                throw new InconsistentBlackHeightException(key.toString(), 
                        leftBlackHeight, rightBlackHeight);
            return leftBlackHeight + (isRed? 0 : 1);
        }

        public boolean containsKey(K key) {
            int compare = key.compareTo(this.key);
            if (compare < 0) 
                return left.containsKey(key);
            else if (compare == 0) 
                return true;
            else  // if (compare > 0)
                return right.containsKey(key);
        }

        @Override
        public String toString() {
            return (isRed?"{":"[") + left + " " + key + " " + right +
                    (isRed?"}":"]");
        }

        public V get(K key) {
            int compare = key.compareTo(this.key);
            if (compare < 0) 
                return left.get(key);
            else if (compare == 0) 
                return value;
            else  // if (compare > 0)
                return right.get(key);
           
        }
    }
    

    /**
     * The root of the entire red-black tree
     */
    private Node<K, V> root;

    /**
     * Constructor to set the root initially to the "null" object
     */
    public RedBlackTreeMap() {
        root = nully;
    }
    
    @Override
    public String toString() {
        return root.toString();
    }
    
    /**
     * Add an association to the map.
     * @param key The key to this association
     * @param val The value to which this key is associated
     */
    public void put(K key, V val) {
        
        try {
        	root = root.put(key, val);
        } catch (DoubleRedException dre) {

            // work begins here:
        	// simply change the root back to black?
        	// I can't think of a case when this exception would be called...
            ((RBNode) root).isRed = false;


        }

        
        
        // The root is never red. If the previous put resulted
        // in a red root, we simply make it black.
        ((RBNode) root).isRed = false;

        
        // below is essentially an assertion that that
        // the black height is consistent (and there are no
        // double reds); if not, an exception
        // will be thrown.
        if (DEBUG)
            root.blackHeight();
    }

    /**
     * Get the value for a key.
     * @param key The key whose value we're retrieving.
     * @return The value associated with this key, null if none exists
     */
    public V get(K key) {
        return root.get(key);
    }

    /**
     * Test if this map contains an association for this key.
     * @param key The key to test.
     * @return true if there is an association for this key, false otherwise
     */
    public boolean containsKey(K key) {
        return root.containsKey(key);
    }

    public Iterator<K> iterator() {
        // The stack contains the left-link lineage of the 
        // the next node, including the next node itself;
        // the next node is the top element
        final Stack<Node<K,V>> st = new ListStack<Node<K,V>>();
        for (Node<K,V> current = root; current != nully; 
                current = ((RBNode) current).left)
            st.push(current);

        return new Iterator<K>() {
            public boolean hasNext() {
                return ! st.isEmpty();
            }

            public K next() {
                if (st.isEmpty())
                    throw new NoSuchElementException();
                else {
                    RBNode nextNode = (RBNode) st.pop();
                    for (Node<K,V> current = nextNode.right; current != nully; 
                            current = ((RBNode) current).left)
                        st.push(current);
                    return nextNode.key;
                }
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
            
        };
        
    }

}

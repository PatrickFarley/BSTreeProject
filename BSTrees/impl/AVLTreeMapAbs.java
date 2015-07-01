package impl;


import java.util.Iterator;
import java.util.NoSuchElementException;

import adt.Map;
import adt.Stack;

/**
 * AVLTreeMapAbs
 * 
 * This contains all the code for an AVL-Tree implementation of
 * a reduced-form Map (no removes) except for the code for
 * fixing it up, which is deferred to an abstract class.
 * 
 * @author Thomas VanDrunen
 * CSCI 345, Wheaton College
 * @param <K> The key-type of the map
 * @param <V>The value-type of the map
 */
public abstract class AVLTreeMapAbs<K extends Comparable<K>, V> implements Map<K, V> {

    /**
     * To be thrown if a violation of the "AVL" condition is detected, that is,
     * if there is a node the heights of whose subtrees differ by more than one.
     */
    public static class ImbalanceException extends RuntimeException {
            
    }

    /**
     * Like the Node class for BasicBSTMap, except equipped to
     * keep track of and check the balance.
     */
    protected class Node  {
        K key;
        V value;
        Node left, right;

        /**
         * The total number of associations in the subtree rooted here.
         */
        int size; 

        /**
         * The height of the subtree rooted here
         * (longest distance from here to any leaf below)
         */
        int height; 

        /**
         * The difference between the left height and the right
         * height. If that value is other than -1, 0, or 1, then the
         * tree is in violation.
         */
        int balance;

        Node(K key, V value, Node left, Node right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
            softRecompute();
        }

        /**
         * Update the balance, size, and height recorded at this
         * node, but do not recursively recompute those values for
         * the children; assume the children are correct.
         */
        public void softRecompute() {
            balance = (left == null? 0 : left.height) - 
                    (right == null? 0 : right.height);
            size = (left == null? 0 : left.size) + 
                    (right == null? 0 : right.size) + 1;
            int leftHeight = (left == null? 0 : left.height),
                    rightHeight = (right == null? 0 : right.height);
            height = (leftHeight > rightHeight ? leftHeight : rightHeight) + 1;
        }

        /**
         * Recursively recompute the balance, size, and height of the
         * subtree rooted here, from scratch. If implemented correctly,
         * this should never be necessary. Accordingly this exists only for
         * debugging purposes.
         */
        public void hardRecompute() {
            // we should never do a hard recompute except
            // when verifying.
            if (!DEBUG)
                throw new UnsupportedOperationException();
            if (left != null) left.hardRecompute();
            if (right != null) right.hardRecompute();
            softRecompute();
        }


        /**
         * Test to see whether this tree is in violation or not.
         * If there is a violation, an exception will be thrown.
         */
        public void verify() {
            hardRecompute();
            if (balance < -1 || 1 < balance)
                throw new ImbalanceException();
        }
        
        
        @Override
        public String toString() {
            return "(" + key + ":" + left + "," + right + ")";
        }
    }

    
    /**
     * Are we in debugging mode? 
     */
    public static boolean DEBUG = false;

    /**
     * The root of this tree
     */
    protected Node root;

    /**
     * A trace of the route we took on the most recent search through
     * the tree, to be used for fixing up.
     */
    protected Stack<Node> searchTrace;


    /**
     * Fixup this tree....
     */
    protected abstract void fixup();
    
    /**
     * Find the place in the tree where this key should
     * go. Specifically, if this key is in the tree, return
     * the node where it is. If this key is not in the tree,
     * find the node, if any, that would be its parent if
     * it were inserted as a leaf. If there is not such
     * parent (equivalently, the tree is empty), return null.
     * @return A node with this key if one exists; a node that
     * would be this key's parent if inserted as a leaf if one
     * exists; null if the tree is empty.
     */
    private Node findNodeOrProspectiveParent(K key) {
    
        Node previous = null,    // the previous node we considered, if any
                current = root;  // the next node to consider, if any
    
        searchTrace = new ListStack<Node>();
        
        // Very complicated invariant needed to prove correctness.
        // Simpler one: 
        //    - previous == null or current == null or previous is the parent of current
        //    - previous == null iff current == root
        //    - current != null or key == previous.key or key is not in the tree
        // (Interpreting that last one: current is null only if previous
        //  contains the key we're looking for or if we have gone off the
        //  end of the tree (in which case key is not in the tree at all).)
        while (current != null) {
            searchTrace.push(current);
            previous = current;
            int compare = key.compareTo(current.key);
            if (compare < 0) 
                current = current.left;
            else if (compare > 0)
                current = current.right;
            else // if (compare == 0)   
                current = null;  // terminate loop -- we found what we wanted
            
        }
    
        // On exit: 
        // - If the tree were empty, we would have terminated immediately,
        //   and previous would be null, which we should return.
        // - If we ever found the node containing key, previous would be it
        // - If we went past the edge of the tree, previous is the parent-to-be
        
        return previous;
    }

    /**
     * Add an association to the map.
     * @param key The key to this association
     * @param val The value to which this key is associated
     */
    public void put(K key, V val) {
    
    	//System.out.println("Put: " + key);
    	
        Node prospective = findNodeOrProspectiveParent(key);
    
        // Four options:
        // -- prospective is node, tree is empty
        // -- prospective has exactly this key; replace its value
        // -- prospective comes before key; make right child
        // -- prospective come after key; make left child
        
        if (prospective == null)
            root = new Node(key, val, null, null);
        else if (prospective.key.equals(key))
            prospective.value = val;
        else if (prospective.key.compareTo(key) < 0) {
            assert prospective.right == null;
            prospective.right = new Node(key, val, null, null);
        }
        else { // if (prosepctive.key.compareTo(key) > 0)
            assert prospective.left == null;
            prospective.left = new Node(key, val, null, null);
        }
    
        fixup();

        //System.out.println(root);
        
        if (DEBUG) root.verify();
    }

    /**
     * Get the value for a key.
     * @param key The key whose value we're retrieving.
     * @return The value associated with this key, null if none exists
     */
    public V get(K key) {
        Node prospective = findNodeOrProspectiveParent(key);
        
        // prospective will return null iff the tree is empty.
        // If prospective is not null, then check if the key is 
        // what we're looking for.
        
        if (prospective == null)
            return null;
        else if (prospective.key.equals(key))
            return prospective.value;
        else
            return null;
    }

    /**
     * Test if this map contains an association for this key.
     * @param key The key to test.
     * @return true if there is an association for this key, false otherwise
     */
    public boolean containsKey(K key) {
        Node prospective = findNodeOrProspectiveParent(key);
        return prospective != null && prospective.key.equals(key);
    }

    /**
     * Iterator that returns the keys in sorted order.
     * @return The iterator 
     */
    public Iterator<K> iterator() {
    
        // The stack contains the left-link lineage of the 
        // the next node, including the next node itself;
        // the next node is the top element
        final Stack<Node> st = new ListStack<Node>();
        for (Node current = root; current != null; current = current.left)
            st.push(current);
    
        return new Iterator<K>() {
            public boolean hasNext() {
                return ! st.isEmpty();
            }
    
            public K next() {
                if (st.isEmpty())
                    throw new NoSuchElementException();
                else {
                    Node nextNode = st.pop();
                    for (Node current = nextNode.right; current != null; current = current.left)
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

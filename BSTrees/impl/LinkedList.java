package impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import adt.List;

/**
 * LinkedList
 * 
 * A linked implementation of the List interface
 * with optimizations to keep add() constant, namely
 * a reference to the last node (as well as the first).
 * 
 * @author Thomas VanDrunen
 * CSCI 345, Wheaton College
 * July 9, 2014
 * @param <E> The base-type of the list
 */


public class LinkedList<E> implements List<E> {

	/**
	 * Simple node class for this list.
	 */
	private class Node {
		E datum;
		Node next;
		Node(E datum, Node next) {
			this.datum = datum;
			this.next = next;
		}
	}
	
	/**
	 * The head node for this list
	 */
	private Node head;
	
	/**
	 * The last node in the list (to optimize add()) 
	 */
	private Node tail;
	
	/**
	 * The number of nodes in this list (to optimize size())
	 */
	private int size;

	/**
	 * Completely unnecessary constructor. It's here to assert
	 * that we intend this all to happen, even though it would
	 * happen by default.
	 */
	public LinkedList() {
		head = tail = null;
		size = 0;
	}

	/**
	 * Helper to check the validity of the index, throwing an
	 * exception if not.
	 * @param index The index in question
	 */
	private void checkIndex(int index) {
		if (index < 0 || index >= size)
    		throw new IndexOutOfBoundsException("" + index);
	}
	
	/**
	 * Append the specified element to the end of this list.
	 * This increases the size by one.
	 * @param element The element to be appended
	 */
	public void add(E element) {
		if (tail == null) {
			assert size == 0 && head == null;
			head = tail = new Node(element, null);
		}
		else {
			tail.next = new Node(element, null);
			tail = tail.next;
		}
		size++;
	}

	/**
	 * Replace the element at the specified position in this list
	 * with the specified element. If the index is invalid, an 
	 * IndexOutOfBoundsException is thrown.
	 * @param index The index of the element to return
	 * @param element The element at the specified position
	 */
    public void set(int index, E element) {
    	checkIndex(index);
    	Node current = head;
    	for (int i = 0; i < index; i++) current = current.next;
    	current.datum = element;
    }


    /**
     * Retrieve the element at the specified position in this list.
	 * If the index is invalid, an IndexOutOfBoundsException is thrown.
     * @param index The index of the element to return
     * @return The element at the specified position
     */
    public E get(int index) {
    	checkIndex(index);
    	Node current = head;
    	for (int i = 0; i < index; i++) current = current.next;
    	return current.datum;
   	
    }

    /**
     * Remove (and return) the element at the specified position.
     * This reduces the size of the list by one and, if necessary,
     * shifts other elements over. If the index is invalid, an 
     * IndexOutOfBoundsException is thrown.
     * @param index The index of the element to remove
     * @return The item removed
     */
    public E remove(int index) {
    	checkIndex(index);
    	E removed;
    	if (index == 0) {
    		removed = head.datum;
    		if (size == 1) {
    			assert (head.next == null && tail == head);
    			head = tail = null;
    		}
    		else 
    			head = head.next;
    	}
    	else { 
    		// At the end, this will be the node preceding the one
    		// to remove
    		Node current = head;
    		for (int i = 0; i < index - 1; i++) 
    			current = current.next;
    		if (current.next == tail) {
    			assert(index == size - 1);
    			tail = current;
    		}
    		removed = current.next.datum;
    		current.next = current.next.next;
    	}    		
    	size--;
    	return removed;
    }

    /**
     * Return the number of elements in this list.
     * @return The number of elements in this list.
     */
    public int size() {
    	return size;
    }

	/**
	 * Return an iterator over this collection (remove() is
	 * unsupported, nor is concurrent modification checked).
	 */
	public Iterator<E> iterator() {
		return new Iterator<E>(){

			Node current = head;
			
			public boolean hasNext() {
				return current != null;
			}

			public E next() {
				if (current == null)
					throw new NoSuchElementException();
				E nextElement = current.datum;
				current = current.next;
				return nextElement;
				
			}

			public void remove() {
				// TODO Auto-generated method stub
				
			}};
		
	}
}

package adt;

/**
 * List
 * 
 * Interface to serve as a simplified version of Java's 
 * java.util.List interface and as an example of the
 * List ADT.
 * 
 * @author Thomas VanDrunen
 * CSCI 345, Wheaton College
 * July 9, 2014
 * @param <E> The base-type of the list
 */

public interface List<E> extends Iterable<E> {

	
	
	/**
	 * Append the specified element to the end of this list.
	 * This increases the size by one.
	 * @param element The element to be appended
	 */
	void add(E element);

	/**
	 * Replace the element at the specified position in this list
	 * with the specified element. If the index is invalid, an 
	 * IndexOutOfBoundsException is thrown.
	 * @param index The index of the element to return
	 * @param element The element at the specified position
	 */
    void set(int index, E element);

    /**
     * Retrieve the element at the specified position in this list.
	 * If the index is invalid, an IndexOutOfBoundsException is thrown.
     * @param index The index of the element to return
     * @return The element at the specified position
     */
    E get(int index);

    /**
     * Remove (and return) the element at the specified position.
     * This reduces the size of the list by one and, if necessary,
     * shifts other elements over. If the index is invalid, an 
     * IndexOutOfBoundsException is thrown.
     * @param index The index of the element to remove
     * @return The item removed
     */
    E remove(int index);

    /**
     * Return the number of elements in this list.
     * @return The number of elements in this list.
     */
    int size();
}

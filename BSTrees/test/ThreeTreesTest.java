package test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import adt.Map;

import impl.AVLTreeMap;
import impl.BasicBSTMap;
import impl.RedBlackTreeMap;

/**
 * This code can test different kinds of binary search trees for the
 * speed at which they can add and retrieve random items. In its current
 * configuration, it will run treeTestSequence which calls combineTest
 * 
 * Run as a JUnit test.
 * 
 * @author patrick.farley
 *
 */
public class ThreeTreesTest {
	// instantiation of each kind of tree:
	AVLTreeMap<Integer,String> AVT = new AVLTreeMap();
	BasicBSTMap<Integer,String> BST = new BasicBSTMap();
	RedBlackTreeMap<Integer,String> RBT = new RedBlackTreeMap();

	
	/**
	 * treeTestSequence
	 * runs a test method on a given tree multiple times, and writes result data to a file.
	 * 
	 * Specifically, this test runs the combineTest method many times, varying the range of 
	 * values from which the put and get operations are called. In this way, we can track 
	 * how the time of put and get operations scales with the size of the range of values 
	 * out of which these put and get operations are called (for a fixed number of put and
	 * get operations each time).
	 * 
	 * NOTE: user input is taken in this method.
	 */
	@Test
	public void treeTestSequence(){
		int range,min,max,increment,n,averaging;
		Random r = new Random();
		String data = "";
		
		// *NOTE begin user input section*
		min =1000;			// enter starting integer range for put and get operations
		max =10000;			// enter final integer range for put and get operations
		increment = 2000;	// enter increment by which the range will change
		n = 500000;			// enter number of put and get operations to perform in each test
		averaging = 3;		// enter number of times to run each identical test (to get an average answer)
		Map<Integer,String> tree = BST; // enter one of the 3 tree objects here
		// *NOTE end user input section*
				
		// for each of the specified values for 'range':
		for (range = min; range <= max; range+=increment){
			// run combineTest and store results:
			long[] times = combineTest(tree,r,range,n,averaging); 
			
			// print results to command line (this is optional)
			System.out.println(times[0] + " ms for "+n+" puts from range " + range);
			System.out.println(times[1] + " ms for "+n+" gets from range " + range);
			
			// concatenate acquired data
			data = data + range + " " + times[0] + " " + times[1]  + "\n";
		}
		

		// when all range values have been tested (the for loop ends), write output data 
		// string to a text file:
		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("tree_test_data.txt"), "utf-8"));
			writer.write(data);
		} catch (IOException ex) {
			System.out.println("file writer error");
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				System.out.println("file close error");
			}
		}
	}
	
	
	/**
	 * combineTest
	 * Helper method which records the time elapsed for a number of put and get operations for a
	 * tree. Meant to give a general idea of tree performance.
	 * 
	 * @param tree	The tree instance to be tested
	 * @param r		A Random object to be used
	 * @param range	The range of values out of which the put and get values will be randomly selected
	 * @param n	The number of put and get operations to perform.
	 * @param averaging	The number of times to run each whole test (tests are averaged for result)
	 * @return The array of test times for the put and get operations (2 indices)
	 */
	public long[] combineTest(Map<Integer,String> tree, Random r,int range,long n,int averaging){
		Map<Integer,String> emptyTree = tree; // get a reference to the empty tree
		long putTime = 0;
		long getTime = 0;
		
		// for the given number of iterations:
		for (int j = 0;j<averaging;j++){
			long time;
			tree = emptyTree; // re-empty the tree;
			
			// *Put operations*
			time = System.currentTimeMillis(); // log the current time
			
			// for the given number of put operations:
			for (int i = 0; i < n; i++){
				tree.put(r.nextInt(range),"a"); // put a random integer within the specified range
			}
			time = System.currentTimeMillis() - time; 	// record elapsed time
			putTime += time;							// add this on to a total put time
			
			// *Get operations*
			time = System.currentTimeMillis(); // log the current time
			
			// for the given number of get operations:
			for (int i = 0; i < n; i++){
				tree.get(r.nextInt(range));		// call get on a random integer within the specified range
			}
			time = System.currentTimeMillis() - time;	// record elapsed time
			getTime += time;							// add this on to a total get time
		}
		long[] answer = {putTime/averaging, getTime/averaging}; // average out both recorded times
		return answer;		// return average times for the specified number of puts and gets
	}
	
	/**
	 * simplePutTest
	 * Helper method which records the time elapsed for a number of put operations for a
	 * tree. Has similar form as combineTest, but handles only put and not get operations.
	 * 
	 * @param tree	The tree instance to be tested
	 * @param r		A Random object to be used
	 * @param range	The range of values out of which the put values will be randomly selected
	 * @param n	The number of put operations to perform.
	 * @param averaging	The number of times to run each whole test (tests are averaged for result)
	 * @return The average time for the given number of put operations
	 */
	public long simplePutTest(Map<Integer,String> tree, Random r,int range,long n,int averaging){
		Map<Integer,String> emptyTree = tree;
		long totalTime = 0;
		
		// for the given number of iterations:
		for (int j = 0;j<averaging;j++){
			
			long time;
			tree = emptyTree; // re-empty the tree
			time = System.currentTimeMillis();	// log the current time
			
			// for the given number of put operations:
			for (int i = 0; i < n; i++){
				tree.put(r.nextInt(range),"a"); // put a random integer within the specified range
			}
			time = System.currentTimeMillis() - time;	// record the elapsed time
			totalTime += time;	// add this on to the total put time
		}
		return totalTime/averaging;	// return average time for the specified number of puts
	}
	
	
	
	
	/**
	 * Experimental test used to get a general idea if how the different trees behave.
	 * This method was not used to collect project data.
	 * @param tree The type of binary search tree to use
	 * @param r Object for generating random values
	 */
	public void putgetSampleTest(Map<Integer,String> tree, Random r,int iters){
		int range = 770000;
		int putno = 1000000;
		int getno = 1;
		long putTime = 0;
		long getTime = 0;
		Map<Integer,String> emptyTree = tree;

		for (int j = 0; j<iters;j++){
			long time;
			tree = emptyTree;
			//puts
			time = System.currentTimeMillis();
			for (int i = 0; i < putno; i++){
				tree.put(r.nextInt(range),"a");
			}
			time = System.currentTimeMillis() - time;
			putTime += time;
			
			//gets
			time = System.currentTimeMillis();
			for (int i = 0; i < getno; i++){
				tree.get(r.nextInt(range));
			}
			time = System.currentTimeMillis() - time;
			getTime += time;
		}
		putTime = putTime/iters;
		getTime = getTime/iters;
		System.out.print(putTime + " ms for put and \t");
		System.out.print(getTime + " ms for get \n");	

	}
	
	// DEPRECATED TESTS:
	/*
	//@Test
	public void calcProbability() {
		double expected = 0;
		double probRepeat = 1;
		double n = 2.0; // the range where BST crosses RBT
		
		// for EVERY entry:
		for (int k = 1; k<3;k++){
			probRepeat = 1;
			// consider the number of items already entered:
			for (int i = 1; i <= k ; i++){
				probRepeat *= (1- i/n);
			}
			probRepeat = 1-probRepeat; // probability of getting a double at that stage.
			System.out.println("" + probRepeat + " chance of a repeat at k = " + k);
			expected += probRepeat;
		}
		System.out.println("expected number of repeats in data is " + expected);
	}
	
	//@Test
	public void testSequence2(){
		Random r = new Random();
		int range;
		String data = "";

		System.out.println("RBT actual: "+ simplePutTest(RBT,r,750000,1000000,3));

		System.out.println("B actual: "+ simplePutTest(BST,r,750000,1000000,3));

		System.out.println("A actual: "+ simplePutTest(AVT,r,750000,1000000,3));
	}
	*/
}



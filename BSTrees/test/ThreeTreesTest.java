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
 * This code tests three different kinds of binary search trees for the
 * speed at which they can add and retrieve random items. The overall performance
 * of trees will depend on the ratio of puts to gets.
 * @author patrick.farley
 *
 */
public class ThreeTreesTest {
	AVLTreeMap<Integer,String> AVT = new AVLTreeMap();
	BasicBSTMap<Integer,String> BST = new BasicBSTMap();
	RedBlackTreeMap<Integer,String> RBT = new RedBlackTreeMap();

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
	public void testSequence(){
		Random r = new Random();
		int range;
		String data = "";
		//System.out.println("RBT actual: "+ simplePutTest(RBT,r,100000,100000,2));
		//System.out.println("BST actual: "+ simplePutTest(BST,r,100000,100000,2));
		//System.out.println("AVT actual: "+ simplePutTest(AVT,r,100000,100000,2));
		
		System.out.println("RBT actual: "+ simplePutTest(RBT,r,750000,1000000,3));

		System.out.println("B actual: "+ simplePutTest(BST,r,750000,1000000,3));

		System.out.println("A actual: "+ simplePutTest(AVT,r,750000,1000000,3));
	}
	
	@Test
	public void testSequence2(){
		Random r = new Random();
		int range;
		String data = "";
		for (range = 1000; range <= 10000; range+=2000){
			long[] times = combineTest(BST,r,range,500000,3);
			System.out.println(times[0] + " ms for range = " + range);
			data = data + range + " " + times[0] + " " + times[1]  + "\n";
		}
		
		for (range = 10000; range <= 1100000; range+=20000){
			long[] times = combineTest(BST,r,range,500000,3);
			System.out.println(times[0] + " ms for range = " + range);
			data = data + range + " " + times[0] + " " + times[1]  + "\n";
		}
		

		Writer writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("tree_test_data.txt"), "utf-8"));
			writer.write(data);
		} catch (IOException ex) {
			// report
		} finally {
			try {writer.close();} catch (Exception ex) {}
		}
		

	}

	
	
	public long simplePutTest(Map<Integer,String> tree, Random r,int range,long putno,int iters){
		Map<Integer,String> emptyTree = tree;
		long totalTime = 0;
		for (int j = 0;j<iters;j++){
			//puts
			long time;
			tree = emptyTree;
			time = System.currentTimeMillis();
			for (int i = 0; i < putno; i++){
				tree.put(r.nextInt(range),"a");
			}
			time = System.currentTimeMillis() - time;
			totalTime += time;
		}
		return totalTime/iters;
	}
	
	
	public long[] combineTest(Map<Integer,String> tree, Random r,int range,long getno,int iters){
		Map<Integer,String> emptyTree = tree;
		long putTime = 0;
		long getTime = 0;
		
		for (int j = 0;j<iters;j++){
			long time;
			tree = emptyTree;
			//Put operations:
			time = System.currentTimeMillis();
			for (int i = 0; i < getno; i++){
				tree.put(r.nextInt(range),"a");
			}
			time = System.currentTimeMillis() - time;
			putTime += time;
			
			// start the timer
			time = System.currentTimeMillis();
			// get values from the tree
			for (int i = 0; i < getno; i++){
				tree.get(r.nextInt(range));
			}
			time = System.currentTimeMillis() - time;
			getTime += time;
		}
		long[] answer = {putTime/iters, getTime/iters};
		return answer;
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
}



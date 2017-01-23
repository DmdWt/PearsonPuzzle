package modelTest;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class OrderFailures_test {
	
	private LinkedList<Integer> sortedCode;
	private LinkedList<Boolean> groupFailures;
	private Vector<Vector<Integer>> codeLine_GroupMatrix;
	private LinkedHashMap<String, Integer> codeMap;
	private Vector<String> codeVector_normal;
	
	
	@Before
	public void initialize(){
		
	}
	private void getTestCase(int testCase){
		groupFailures = new LinkedList<Boolean>();
		codeVector_normal = new Vector<String>();
		sortedCode = new LinkedList<Integer> () ;
		codeLine_GroupMatrix = new Vector<Vector<Integer>>();
		codeMap = new LinkedHashMap<String, Integer>();
		String[] codeLines;
		Integer[] sortedLines;
		Integer[][] ruleGroup;
		switch (testCase) {
		case 0:
			codeLines= new String[]			{"Line0","Line1","Line2","Line3","Line4","Line5"};
			sortedLines = new Integer[]		{0,1,3,2, 4,5};
			ruleGroup = new Integer[][] 	{{0,1,1,2,3,0}, {1,1,1,1,1,1}};
			break;
		case 1:
			codeLines= new String[]			{"Line0","Line0","Line2","Line3","Line2","Line5"};
			sortedLines = new Integer[]		{1,0,4,2,5,3};
			ruleGroup = new Integer[][] 	{{1,2,3,0,0,4}, {1,1,2,3,0,0}};
			break;
		case 2:
			codeLines= new String[]			{"Line0","Line0","Line0","Line1","Line2","Line2"};
			sortedLines = new Integer[]		{1,0,2,5,3,4};
			ruleGroup = new Integer[][] 	{{1,2,3,4,4,6}, {1,1,1,2,2,3}};
			break;
		default:
			codeLines= new String[] 		{"Line0","Line1","Line2","Line3","Line4","Line5"};
			sortedLines = new Integer[] 	{0,1,2,3,4,5};
			ruleGroup = new Integer[][]		{{1,2,3,4,5,6}, {0,0,0,1,1,1}, {0,1,0,1,1,1}};
			break;
		} 
		for (int i = 0; i < ruleGroup.length; i++) {
			codeLine_GroupMatrix.add(new Vector<Integer>());
		}
		for(int i=0; i<codeLines.length;i++){
			codeVector_normal.add(codeLines[i]);
			if(sortedLines.length>i)
				sortedCode.add(sortedLines[i]);
			for(int j=0; j<codeLine_GroupMatrix.size(); j++){
				codeLine_GroupMatrix.get(j).add(ruleGroup[j][i]);
			}
			if(!codeMap.containsKey(codeLines[i]))
			codeMap.put(codeLines[i], i);
		}	
	}

	@Test
	public void test() {
		System.out.println("Testfall 0");
		getTestCase(0);
		groupFailures = OrderFailures.testOrder_groups(sortedCode, codeLine_GroupMatrix, codeMap, codeVector_normal);
		assertFalse(groupFailures.get(0));
		assertTrue(groupFailures.get(1));
		

		System.out.println("Testfall 1");
		getTestCase(1);
		groupFailures = OrderFailures.testOrder_groups(sortedCode, codeLine_GroupMatrix, codeMap, codeVector_normal);
		assertTrue(groupFailures.get(0));
		assertTrue(groupFailures.get(1));
		
		System.out.println("Testfall 2");
		getTestCase(2);
		groupFailures = OrderFailures.testOrder_groups(sortedCode, codeLine_GroupMatrix, codeMap, codeVector_normal);
		assertTrue(groupFailures.get(0));
		assertTrue(groupFailures.get(1));
		
		System.out.println("Testfall 10");
		getTestCase(10);
		groupFailures = OrderFailures.testOrder_groups(sortedCode, codeLine_GroupMatrix, codeMap, codeVector_normal);
		assertTrue(groupFailures.get(0));
		assertTrue(groupFailures.get(1));
		assertTrue(groupFailures.get(2));
		
	}

}

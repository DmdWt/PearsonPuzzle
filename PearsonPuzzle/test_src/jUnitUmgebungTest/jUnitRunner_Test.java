package jUnitUmgebungTest;

import static org.junit.Assert.assertEquals;

import jUnitUmgebung.JUnitRunner;

import java.util.Arrays;
import java.util.Collection;


import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;


public class jUnitRunner_Test {
	Boolean expectedResult;
	String testString;
	JUnitRunner jUnitRunner;
	final String unitText = "import org.junit.Test; \nimport static org.junit.Assert.*;\n\npublic class testcase_Test{\n\t@Test\n\t"+"public void testMethode1(){ \n"+"\t\tassertTrue(true);\n\t}\n";
	
	@Before
	   public void initialize() {
			jUnitRunner = new JUnitRunner(unitText, testString, null);
	   }

	   // Each parameter should be placed as an argument here
	   // Every time runner triggers, it will pass the arguments
	   // from parameters we defined in primeNumbers() method
		
	   public jUnitRunner_Test(Boolean expectedResult,String testString) {
	      this.testString = testString;
	      this.expectedResult = expectedResult;
	   }

	   @Parameterized.Parameters
	   public static Collection<Object[]> primeNumbers() {
	      return Arrays.asList(new Object[][] {
	    		// normale Klasse
	         {true, "public class first{ \npublic static void main(String args[]){\nSystem.out.println(\"alles klar\");}}"},	// 	
	         	// Berechtiguns-Test (darf nicht anschlagen, da nur compiliert wird)
	         {true, "public class first{ public static void main(String args[]){add(2,2);}}\n"+
	         		"public class second{ private int add(int a, int b){return a+b;}}"}, 
	         	// ohne main
	         {false, "public class first{ int a=0; int b=3; int c=a+b;\n System.out.println(\"wer braucht schon ne main\");}"},
	         	// ohne Klasse und main
	         {true, "int a=0; int b=3; int c=a+b;"},
	         	// selbst definierte Methode ohne Klasse
	         {true, "private void run(){int a=0; int b=3; int c=a+b;}"},
	         	// EmptyString
	         {true, ""},
	         	// main Ohne Klasse
	         {false, "public static void main(String args[]){add(2,2);}"+	
	         		 "private int add(int a, int b){return a+b;}"},
	         	// zwei Klassen
	         {true, "public class first{ \npublic first(){}}\n"+
	        		 "public class second{ \npublic second(){}}"},
	         	// zwei main Klassen,	
	         {true, "public class first{ \npublic static void main(String args[]){System.out.println(\"first\");}}\n"+
	        		 "public class second{ \npublic static void main(String args[])\n{System.out.println(\"second\");}}"},
	        	// Nonsense 
	        {false, "asd"}		
	      });
	   }

	   @Test
	   public void test() {
		   assertEquals(expectedResult, jUnitRunner.run(unitText));
	   }

}

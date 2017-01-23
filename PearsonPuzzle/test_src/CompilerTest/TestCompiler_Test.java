package CompilerTest;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import compiler.TestCompiler;


@RunWith(Parameterized.class)
public class TestCompiler_Test {
	Boolean expectedResult;
	String testString;
	TestCompiler testCompiler;
	
	@Before
	   public void initialize() {
			testCompiler = new TestCompiler();
	   }

	   // Each parameter should be placed as an argument here
	   // Every time runner triggers, it will pass the arguments
	   // from parameters we defined in primeNumbers() method
		
	   public TestCompiler_Test(Boolean expectedResult,String testString) {
	      this.testString = testString;
	      this.expectedResult = expectedResult;
	   }

	   @Parameterized.Parameters
	   public static Collection<Object[]> primeNumbers() {
	      return Arrays.asList(new Object[][] {
	    		// normale Klasse
	         {true, "" +
	         		//"package gen_src.testCode;" +
	         		"\npublic class first{ \npublic static void main(String args[]){\nSystem.out.println(\"alles klar\");}}"},	// 	
	         	// Berechtiguns-Test (darf nicht anschlagen, da nur compiliert wird)
	         {true, "public class first{ public static void main(String args[]){add(2,2);}}\n"+
	         		"public class second{ private int add(int a, int b){return a+b;}}"}, 
	         	// Anweisung außerhalb einer Methode (im Klassenrumpf)
	         {false, "public class first{ int a=0; int b=3; int c=a+b;\n System.out.println(\"Wer braucht schon Methoden\");}"},
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
	        	// import Test
	         {true, "import java.util.Vector; \n" +
	        		"public class first{ \n" +
	        		"public void main(String args[]){\n" +
	        		"getOrdervektor();}\n" +
	        		"public void getOrdervektor(){\n" +
	        		"Vector<Integer> ordervector = new Vector<Integer>();\n" +
	        		"for(int ordernumber=0;ordernumber<4;ordernumber++){\n" +
	        		"ordervector.add(ordernumber);		}\n" +
	        		"System.out.println(ordervector.toString());}" +
	        		"}"},
	         	// zwei main Klassen,	
	         {true, "public class first{ \n" +
	         		"public static void main(String args[]){System.out.println(\"first\");}}\n"+
	        		"public class second{ \n" +
	        		"public static void main(String args[])\n" +
	        		"{System.out.println(\"second\");}}"},
	        	// Nonsense 
	        {false, "asd"}, 
	        {true, " "}
	      });
	   }

	@Test
	public void test() {
		assertEquals(expectedResult, testCompiler.compileCode(testString));
	}
	
	@After
	public void print(){
		if(false != expectedResult != testCompiler.getFailures().isEmpty()){
			 System.out.println(testCompiler.getFailures().get(0).get("Nachricht"));
			 System.out.println(testCompiler.getFailures().get(0).get("Class"));
		 }
		testCompiler=null;
	}
}

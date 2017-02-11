package jUnitUmgebung;

import view.PPException;

public class run {
	public static void main (String args[]){
		runUnitTest();
	}
	
	public static void runUnitTest(){
		
		final String unitText = "import Test.Te; import org.junit.Test; \n" +
				"import static org.junit.Assert.*; \n\n" +
				"public class Unittestcase_Test{\n\t" +
					"@Test\n\t"+"" +
					"public void testMethode1(){ \n"+"\t\t" +
						"assertTrue(Te.runStatic());\n\t" +
						"Te te = new Te();"+
						"assertEquals(te.runDynamic(), true);"+
					"}" +
				"}\n";
		final String code = "package Test; public class Te{" +
								"public Te(){" +
								""+
								"}"+
								"public static boolean runStatic()" +
								"{" +
									//"int a=0; int b=3; int c=a+b; " +
									"System.out.println(\"asd\");"+
									"return true;" +
								"}" +
								"public boolean runDynamic()" +
								"{" +
									//"int a=0; int b=3; int c=a+b; " +
									"return true;" +
								"}" +
							"}";
		UnitRunner jUnitRunner;
		try {
			jUnitRunner = new UnitRunner(unitText, code, null, null, null);
			jUnitRunner.run();
		} catch (PPException e) {
			e.printStackTrace();
		}
		//jUnitRunner.compileClasses();
	}

}

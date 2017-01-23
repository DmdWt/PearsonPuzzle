package compiler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

public class TestCompiler {		
	
	private final static String DEFAULT_CLASS_NAME="testCode";
	private Vector<HashMap<String, String>> compileFailures;
	private String packageString;
	private LinkedList<String> importStrings;
	private HashMap <String, String> classes;
	
	public TestCompiler(){
		packageString = new String();
		importStrings = new LinkedList<String>();
		classes = new HashMap<String, String>();
		compileFailures = new Vector<HashMap<String, String>>();
		}
	
	public Vector<HashMap<String,String>> getFailures(){
		return compileFailures;
	}

		/**
		 * Kompiliert eine Liste von Codezeilen.
		 * @param solutionArray Liste von Codezeilen
		 * @return Kompilieren erfolgreich
		 */
		public boolean compileCode(Vector<String> solutionArray){
			
			boolean ret=true;
//			HashMap<String, String> classes = new LinkedHashMap<String, String>();
			String className=new String();
			String solutionString = new String();
				for(String line: solutionArray){
					if(line.trim().startsWith("package ")){
						packageString = line.substring(line.indexOf("package")+7, line.indexOf(";"));
						packageString = packageString.trim();
					}
					else if (line.trim().startsWith("import ")){
						String importString = line.substring(line.indexOf("import")+6, line.indexOf(";"));
						importStrings.add(importString.trim());
					}
					if(line.contains(" class ")){
						if(className.isEmpty()){
							className=line.substring(line.indexOf("class")+5, line.indexOf("{"));
							className=className.trim();
							solutionString=solutionString+"\n"+line;
						}
						else{
//							if(classes.containsKey(className))
//								return false;
//							classes.put(className, solutionString);
							
							ret= ret && compileCode(solutionString, className);
							className = new String();
							solutionString=new String(line);
						}
					}
					else{
						solutionString=solutionString+"\n"+line;
					}
				}
//			for(String classN : classes.keySet()){
//				ret = ret & compileCode(classN, classes.get(classN));
//			}
//			return ret;
			if(className.isEmpty())
				return compileCode(solutionString);
			else 
				return compileCode(solutionString, className);
		}
		
		/**
		 * Kompiliert einen als String übergebenen Source Code.
		 * @param src Source Code
		 * @return Kompilieren erfolgreich
		 */
		public boolean compileCode(String src){
			

			 // Konflikte bezüglich des Klassennamens werden ausgeschlossen
			 String className=new String(DEFAULT_CLASS_NAME);
			 
			 // Falls der Code keine Klasse enthält, wird hier eine generiert 
			 if(!src.contains(" class ")){
				 while(src.contains(className)){
					 className=className+"_";
				 }
				 src = "class "+className+" { "+src+" }";
				 return compileCode(src,className);
			 }
			 else{
				 Vector<String> srcVector= new Vector<String>();
				 for(String line: src.split("\n")){
					 srcVector.add(line);
				 }
				 return compileCode(srcVector);
			 }
		}
			 
		/**			 
		 * Methode kompilert.
		 * @param src Source Code
		 * @param className Klassenname
		 * @return Kompilieren erfolgreich
		 */
		private boolean compileCode(String src, String className){
			
			// TODO: Imports hinzufügen
			//System.out.println("package:"+packageString);
			 StringJavaFileObject javaFile = new StringJavaFileObject( "gen_src/"+className, src );
			 JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			 DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			 StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnostics, null, null );
			 Iterable<? extends JavaFileObject> units = Arrays.asList( javaFile );
			 CompilationTask task = compiler.getTask( null, fileManager, diagnostics, null, null, units );
			 task.call();
			 //System.out.println(src);

			 // Diagnose (bei aufgetretenem Fehler)
			 for ( Diagnostic<?> diagnostic : diagnostics.getDiagnostics() )
			 {
				 HashMap <String, String> compileFailure = new HashMap<String, String>(9);
				 compileFailure.put("Class", ""+className);
				 compileFailure.put("Kind", ""+diagnostic.getKind());
				 compileFailure.put("Quelle", ""+diagnostic.getSource());
				 compileFailure.put("Code", ""+src);
				 // TODO: Fehlerbericht anpassen (Nachricht)
				 compileFailure.put("Nachricht",""+diagnostic.getMessage( null ) );
				 compileFailure.put("Zeile", ""+diagnostic.getLineNumber() );
				 compileFailure.put("Position", ""+diagnostic.getPosition());
				 compileFailure.put("Spalte", ""+diagnostic.getColumnNumber() );
				 compileFailure.put("Startpostion", ""+diagnostic.getStartPosition());
				 compileFailure.put("Endposition", ""+diagnostic.getEndPosition() );
				 compileFailures.add(compileFailure);
				 
			 }			 
		
			 try {
				 fileManager.close();
			 } catch (IOException e) {
				 return false;
			 }

			 URLClassLoader classLoader = null;
			 try {
				 classLoader = new URLClassLoader( new URL[] { new File(".").getAbsoluteFile().toURI().toURL() } );
				 //System.out.println(classLoader.getURLs());
			 } catch (MalformedURLException e) {
				 return false;
			 }
			 try {
				 //System.out.println(className);
				 //className = "sourceCode_toTest/"+className;
				 Class.forName( className, true, classLoader );
				 deleteTestClass(className);
				 
			 } catch (ClassNotFoundException e) {
				 return false;
			 }    // Java Compiler API 2
			 return true;
		 }
		 
		 /**
		  * Klassendatei wird gelöscht, falls sie existiert.
		  * @param className Name der Klasse (Dateiname = className.class)
		  */
		 private static void deleteTestClass(String className){
			 File file = new File(className+".class");
			 if(file.exists()){
				 file.delete();
			 }
		 }
}
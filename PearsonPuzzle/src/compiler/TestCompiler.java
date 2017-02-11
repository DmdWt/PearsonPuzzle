package compiler;

import jUnitUmgebung.MemClassLoader;
import jUnitUmgebung.MemJavaFileManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

/**
 * Testcompiler, der rudimentär arbeitet, Klassen nach dem kompilieren in den Arbeitsspeicher lädt. 
 * Fehler während des Kompiliervorgangs werden notiert können bei Bedarf in Form einer Hash-Map abgerufen werden. 
 * @author workspace
 */
public class TestCompiler {		
	
	private final static String DEFAULT_CLASS_NAME="testCode";
	private Vector<HashMap<String, String>> compileFailures;
	private String packageString;
	private LinkedList<String> importStrings;
	private HashMap<String, String> srcCodeMap;
	
	public TestCompiler(String sourceCode_ToBeTested, String methodImports, String onlineImports, String classImports){
		packageString = new String();
		importStrings = new LinkedList<String>();
		compileFailures = new Vector<HashMap<String, String>>();
		ClassModel model = new ClassModel(sourceCode_ToBeTested, methodImports, onlineImports, classImports);
		srcCodeMap = model.getCodeMap();
	}
	
	public Vector<HashMap<String,String>> getFailures(){
		return compileFailures;
	}
	
	public MemClassLoader compile(){
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		MemClassLoader normalClassLoader = new MemClassLoader();
	
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		JavaFileManager classFileManager = new MemJavaFileManager(diagnostics, compiler, normalClassLoader);
	
		LinkedList<JavaFileObject> classFiles = new LinkedList<JavaFileObject>();
		for(String className : srcCodeMap.keySet()){
			classFiles.add(new StringJavaFileObject(className, srcCodeMap.get(className)));
		}
		Iterable <?extends JavaFileObject> units = classFiles;
		
		Set<String> options = new HashSet<String>();		
			// Hier kann man den Compiler verfolgen mit :
			// options.add( "-verbose");
			// options.add("-deprecation");
		CompilationTask task = compiler.getTask( null, classFileManager,diagnostics, options, null, units );
		task.call();
		makeDiagnose(diagnostics);
		System.out.println("Kompilierte:"+srcCodeMap);
		
		try {
			classFileManager.close();
		} catch (IOException e1) { e1.printStackTrace(); }
		return normalClassLoader;
	}
	
	private void makeDiagnose(DiagnosticCollector<JavaFileObject> diagnostics){
		// Diagnose (bei aufgetretenem Fehler)
		for ( Diagnostic<?> diagnostic : diagnostics.getDiagnostics() )
		 {
			String className = null;
			Pattern pattern = Pattern.compile("string:///");
			Matcher matcher  = pattern.matcher(diagnostic.getSource().toString());
			if(matcher.find())
				className = diagnostic.getSource().toString().substring(matcher.end()).replaceAll("]", "");
			else 
				className = diagnostic.getSource().toString();
			 HashMap <String, String> compileFailure = new HashMap<String, String>(9);
			 compileFailure.put("Klasse", ""+className);
			 compileFailure.put("Art", ""+diagnostic.getKind());
			 compileFailure.put("Quelle", ""+diagnostic.getSource().toString());
			 compileFailure.put("Code", ""+diagnostic.getCode());
			 compileFailure.put("Nachricht",""+diagnostic.getMessage( null ) );
			 compileFailure.put("Zeile", ""+diagnostic.getLineNumber() );
			 compileFailure.put("Position", ""+diagnostic.getPosition());
			 compileFailure.put("Spalte", ""+diagnostic.getColumnNumber() );
			 compileFailure.put("Startpostion", ""+diagnostic.getStartPosition());
			 compileFailure.put("Endposition", ""+diagnostic.getEndPosition() );
			 compileFailures.add(compileFailure);			 
		 }	
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
	
	@Deprecated
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
		
	@Deprecated
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
		
	@Deprecated
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
				 
				 
			 } catch (ClassNotFoundException e) {
				 return false;
			 }    // Java Compiler API 2
			 deleteTestClass(className);
			 return true;
		 }
}
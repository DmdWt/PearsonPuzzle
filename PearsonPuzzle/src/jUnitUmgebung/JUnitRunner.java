package jUnitUmgebung;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import compiler.StringJavaFileObject;

public class JUnitRunner {
	private final static String DEFAULT_CLASS_NAME="testCode";
	private HashMap<String, String> srcCodeMap;
	private String unitClassName;
	private String unitSourceCode;
	private String unitImports;
	private Integer unitLineOffset;
	private Vector<HashMap<String, String>> compileFailures;
	/**
	 * Klasse dient dazu, in Textform vorliegenden Code <br>
	 * auf unterschiedliche Art und Weise zu testen.
	 * @param sourceCode_ToBeTested zu testende Klassen, Methoden, ...
	 * @param imports evtl. notwendige imports
	 */
	public JUnitRunner(String unitSourceCode, String sourceCode_ToBeTested, String methodImports){
		unitLineOffset = 0;
		this.unitSourceCode = unitSourceCode;
		this.unitClassName = extractClassName(unitSourceCode);
		compileFailures = new Vector<HashMap<String, String>>();
		fillNeccessaryImports();
		fillSrcCodeMap(sourceCode_ToBeTested);
		addMethods(methodImports);
	}
	/**
	 * FIXME: package handeln
	 */
	public void addOnlineImport(String onlineImports){
		if(onlineImports == null || onlineImports.trim().isEmpty())
			return;
		for(String codeName : srcCodeMap.keySet()){
			if(!srcCodeMap.get(codeName).trim().startsWith("package "))
				srcCodeMap.put(codeName, onlineImports.trim()+"\n"+srcCodeMap.get(codeName));
			else 
				srcCodeMap.put(codeName, onlineImports.trim()+"\n"+srcCodeMap.get(codeName));
		}
	}
	
	private void addMethods(String methodImports){
		if(methodImports == null || methodImports.trim().isEmpty())
			return;
		for(String codeName : srcCodeMap.keySet()){
			StringBuffer codeString = new StringBuffer(srcCodeMap.get(codeName));
			Integer closeIndex = codeString.lastIndexOf("}");
			codeString.insert(closeIndex, methodImports.trim()+"\n");
			
			srcCodeMap.put(codeName, codeString.toString());
		}
	}
	public void addClasses(String classImports){
		if(classImports == null || classImports.trim().isEmpty())
			return;
		fillSrcCodeMap(classImports);
	}
	public Vector<HashMap<String,String>> getFailures(){
		return compileFailures;
	}
	
	public Integer getLineOffset(){
		return unitLineOffset;
	}
	
	private String extractClassName(String unitSourceCode){
		if(!unitSourceCode.contains(" class "))
			return null;
		String unitClassName = new String();
		
		unitClassName=unitSourceCode.substring(unitSourceCode.indexOf("class")+5, unitSourceCode.indexOf("{")).trim();
		return unitClassName;
	}
	
	private void fillNeccessaryImports(){
		if(unitImports==null)
			unitImports = new String();
		final String[] neccessaryImports = {"\n import org.junit.Test;\n", "\n import static org.junit.Assert.*;\n"};
		for(String importString: neccessaryImports){
			if(!unitSourceCode.contains(importString))
				unitImports+= importString;
				unitLineOffset+= importString.split("\n").length;
		}
	}
	
	/**
	 * HashMap sourceCodeMap wird mit Inhalt gefüllt.<br>
	 * Falls in src keine Klasse defineirt wird, wird klasse "gewrapt".
	 * 
	 * @param src sourceCode
	 */
	private void fillSrcCodeMap(String src){
		if(srcCodeMap==null)
			srcCodeMap = new HashMap<String, String>();
		
		 // Konflikte bezüglich des Klassennamens werden ausgeschlossen
		 String className=new String(DEFAULT_CLASS_NAME);
		 
		 // Falls der Code keine Klasse enthält, wird hier eine generiert 
		 if(!src.contains(" class ")){
			 while(src.contains(className)){
				 className=className+"_";
			 }
			 src = "class "+className+" {\n"+src+"}";
			 srcCodeMap.put(className, src);
		 }
		 else{
			 Vector<String> srcVector= new Vector<String>();
			 for(String line: src.split("\n")){
				 srcVector.add(line);
			 }
			 fillSrcCodeMap(srcVector);
		 }
	}
	
	/**
	 * HashMap sourceCodeMap wird mit Inhalt gefüllt.<br>
	 * Kann nicht agieren, wenn keine codeLine mit " class " enthalten ist,<br>
	 * spirch in codeLines keine Klasse definiert ist. <br>
	 * Imports und packages werden herausgefiltert und Klassenspezifisch verarbeitet.
	 * 
	 * @param codeLines
	 */
	private void fillSrcCodeMap(Vector<String> codeLines){
		// FIXME: packages und imports sollen verwertet werden.
		String className=new String();
		String packageString = new String();
		LinkedList<String> importStrings = new LinkedList<String>();
		
		String solutionString = new String();
			for(String line: codeLines){
				if(line.trim().startsWith("package ")){
					if(packageString.isEmpty()){
						packageString = line.substring(line.indexOf("package")+7, line.indexOf(";"));
						packageString = packageString.trim();
					}
					else if(!className.isEmpty()){
						srcCodeMap.put(className, solutionString);
						packageString = new String();
						importStrings = new LinkedList<String>();
						className = new String();
						solutionString=new String(line);
					}
				}
				else if (line.trim().startsWith("import ")){
					if(className.isEmpty()){
						String importString = line.substring(line.indexOf("import")+6, line.indexOf(";"));
						importStrings.add(importString.trim());
					}
					else{
						srcCodeMap.put(className, solutionString);
						packageString = new String();
						importStrings = new LinkedList<String>();
						className = new String();
						solutionString=new String(line);
					}
				}
				if(line.contains(" class ")){
					if(className.isEmpty()){
						className=line.substring(line.indexOf("class")+5, line.indexOf("{"));
						className=className.trim();
						solutionString=solutionString+"\n"+line;
					}
					else{						
						srcCodeMap.put(className, solutionString);
						packageString = new String();
						importStrings = new LinkedList<String>();
						className = new String();
						solutionString=new String(line);
					}
				}
				else{
					solutionString=solutionString+"\n"+line;
				}
			}
		if(className.isEmpty())	
			fillSrcCodeMap(solutionString);
		else
			srcCodeMap.put(className, solutionString);
	}
	
	
	/**
	 * Klassen in srcCodeMap werden 
	 * @return
	 */
	public void compileClasses_ToDisk(){
		for(String className:srcCodeMap.keySet()){
			String sourceCode = srcCodeMap.get(className);
			// TODO: Imports hinzufügen
			StringJavaFileObject javaFile = new StringJavaFileObject( "gen_src/"+className, sourceCode );
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnostics, null, null );
			Iterable<? extends JavaFileObject> units = Arrays.asList( javaFile );
			CompilationTask task = compiler.getTask( null, fileManager, diagnostics, null, null, units );
			task.call();
			makeDiagnose(diagnostics, className, sourceCode);		 
		
			 try {
				 fileManager.close();
			 } catch (IOException e) {
				 e.printStackTrace();
			 }

			 URLClassLoader classLoader = null;
			 try {
				 classLoader = new URLClassLoader( new URL[] { new File(".").getAbsoluteFile().toURI().toURL() } );
			 } catch (MalformedURLException e) {
				 e.printStackTrace();
			 }
			 try {
				 Class.forName( className, true, classLoader );
				 deleteTestClasses();
				 
			 } catch (ClassNotFoundException e) {
				 e.printStackTrace();
			 }    
		 }
	}
	
	/**
	  * Klassendateien werden gelöscht, falls sie existiert.
	  * @param className Name der Klasse (Dateiname = className.class)
	  */
	 private void deleteTestClasses(){
		 for(String className : srcCodeMap.keySet()){
			 File file = new File(className+".class");
			 if(file.exists()){
				 file.delete();
			 } 
		 }
	 }
	 
	/**
	 * Die in srcCodeMap (in textform) enthaltenen Klassen werden kompiliert.
	 */
	public void compileClasses_ToMemory(){
		for(String className : srcCodeMap.keySet()){
			String sourceCode = srcCodeMap.get(className);
			final String[] imports = {"",""};
			for(String importString: imports){
				if(!sourceCode.contains(importString))
					sourceCode=importString+sourceCode;
			}
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			MemClassLoader classLoader = new MemClassLoader();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();			
			
			JavaFileManager fileManager = new MemJavaFileManager(diagnostics, compiler, classLoader );
			JavaFileObject javaFile = new StringJavaFileObject( className, sourceCode);
			Collection<JavaFileObject> units = Collections.singleton( javaFile );
			Set<String> options = new HashSet<String>();		
			// Hier kann man den Compiler verfolgen:
				//options.add( "-verbose");
				//options.add("-deprecation");
			CompilationTask task = compiler.getTask( null, fileManager,null, options, null, units );
			task.call();
			makeDiagnose(diagnostics, className, sourceCode);
		
			try {
				fileManager.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private void makeDiagnose(DiagnosticCollector<JavaFileObject> diagnostics, String className, String sourceCode){
		// Diagnose (bei aufgetretenem Fehler)
		for ( Diagnostic<?> diagnostic : diagnostics.getDiagnostics() )
		 {
			 HashMap <String, String> compileFailure = new HashMap<String, String>(9);
			 compileFailure.put("Class", ""+className);
			 compileFailure.put("Kind", ""+diagnostic.getKind());
			 compileFailure.put("Quelle", ""+diagnostic.getSource());
			 compileFailure.put("Code", ""+sourceCode);
			 // TODO: Fehlerbericht anpassen (Nachricht)
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
	 * Unit Test wird kompiliert und gestartet.
	 * Anschließend werden vorhandene Klassendateien gelöscht.
	 * @param unitText JUnit Klasse in Textform
	 * @return Result
	 */
	public Result run(String unitText){
		if(!unitText.contains(" class "))
			return new Result();
		String unitClassName = new String();
		final String[] imports = {"\n import org.junit.Test; \n", "import static org.junit.Assert.*"};
		
		unitClassName=unitText.substring(unitText.indexOf("class")+5, unitText.indexOf("{")).trim();
		for(String importString: imports){
			if(!unitText.contains(importString))
				unitText=importString+unitText;
		}
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		MemClassLoader_JUnit unitClassLoader = new MemClassLoader_JUnit();
		MemClassLoader normalClassLoader = new MemClassLoader(); 
				
		JavaFileManager unitFileManager = new MemJavaFileManager_JUnit( compiler, unitClassLoader, normalClassLoader);		
		JavaFileObject javaClassFile = null;
		
		LinkedList<JavaFileObject> classFiles = new LinkedList<JavaFileObject>();
		JavaFileObject unitFile = new StringJavaFileObject( unitClassName, unitText );
		classFiles.add(unitFile);
		for(String className : srcCodeMap.keySet()){
			classFiles.add(new StringJavaFileObject(className, srcCodeMap.get(className)));
			//javaClassFile = new StringJavaFileObject(className, srcCodeMap.get(className));
		}
		
		//Iterable<? extends JavaFileObject> units = Arrays.asList( new JavaFileObject[]{unitFile, javaClassFile} );
		Iterable <?extends JavaFileObject> units = classFiles;
		
		Set<String> options = new HashSet<String>();		
			// Hier kann man den Compiler verfolgen mit :
			// options.add( "-verbose");
			// options.add("-deprecation");
		CompilationTask task = compiler.getTask( null, unitFileManager,null, options, null, units );
		task.call();
		
		try {
			unitFileManager.close();
		} catch (IOException e1) { e1.printStackTrace(); }
		
		try {
			Result result = JUnitCore.runClasses(unitClassLoader.findClass(unitClassName));
			System.out.println("Fehler beim Test: "+result.getFailures());
			return result;
		} catch (ClassNotFoundException e1) { e1.printStackTrace(); return null; }
	}
	
	/**
	 * Führt die UnitTests, die im Konstruktor in einer Testklasse in Stringform übergeben wurde aus und bindet die Klasse(n), die ebenfalls im Construktor übergeben wurden, aus.
	 * @return Result
	 */
	public Result run(){
		String unitSourceCode = unitImports+this.unitSourceCode;
		System.out.println(srcCodeMap);
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		MemClassLoader_JUnit unitClassLoader = new MemClassLoader_JUnit();
		MemClassLoader normalClassLoader = new MemClassLoader(); 
		JavaFileManager unitFileManager = new MemJavaFileManager_JUnit( compiler, unitClassLoader, normalClassLoader);				
		
		JavaFileObject unitFile = new StringJavaFileObject( unitClassName, unitSourceCode );
		LinkedList<JavaFileObject> classFiles = new LinkedList<JavaFileObject>();
		classFiles.add(unitFile);
		for(String className : srcCodeMap.keySet()){
			classFiles.add(new StringJavaFileObject(className, srcCodeMap.get(className)));
		}
		Iterable <?extends JavaFileObject> units = classFiles;
		
		Set<String> options = new HashSet<String>();		
			// Hier kann man den Compiler verfolgen mit :
			// options.add( "-verbose");
			// options.add("-deprecation");
		CompilationTask task = compiler.getTask( null, unitFileManager,null, options, null, units );
		task.call();
		
		try {
			unitFileManager.close();
		} catch (IOException e1) { e1.printStackTrace(); }		
		try {
			Result result = JUnitCore.runClasses(unitClassLoader.findClass(unitClassName));
			System.out.println("Fehler beim Test: "+result.getFailures());
			return result;
		} catch (ClassNotFoundException e1) { e1.printStackTrace(); return null; }
	}
}

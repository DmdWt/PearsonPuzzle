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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import view.PPException;

import compiler.CodeCompletion;
import compiler.ClassModel;
import compiler.StringJavaFileObject;

/**
 * Klasse hält jUnit Test und zugehörige, zu testende Klassen in Form von Strings bereit, um diese bei bedarf zu kompilieren.
 * Es können auch nur Mehtoden oder nur der Inhalt einer Methode (ohne Methodenrumpf) übergeben werden. Diese werden dann "gewrapt".<br><br>
 * 
 * Falls kein Klassenname existiert, wird versucht, den Sourcecode mit dem unter DEFAULT_CLASS_NAME definierten Namen zu wrappen. 
 * Bsp: public void do(){ return;} -> public class <DEFAULT_CLASS_NAME> { public void do( return;} } <br><br>
 * 
 * Falls kein Methodenrumpf existiert, wird zusätzlich noch ein Methodenrumpf dazugebaut, um das ganze kompilierbar zu machen. 
 * Bsp: int a; int b=0; a=b; return a; -> public int methode(){ int a; int b; a=b; return a; } <br><br>
 * @author workspace
 */
public class UnitRunner {
	
	private HashMap<String, String> srcCodeMap;
	private String unitClassName;
	private String unitSourceCode;
	private String unitImports;
	private Integer unitLineOffset;
	private Vector<HashMap<String, String>> compileFailures;
	/**
	 *
	 * @param sourceCode_ToBeTested zu testende Klassen, Methoden, ...
	 * @param imports evtl. notwendige imports
	 * @throws PPException 
	 */
	public UnitRunner(String unitSourceCode, String sourceCode_ToBeTested, String methodImports, String onlineImports, String classImports) throws PPException {
		unitLineOffset = 0;
		this.unitSourceCode = unitSourceCode;
			extractClassName(unitSourceCode);
		compileFailures = new Vector<HashMap<String, String>>();
		fillNeccessaryImports();
		ClassModel model = new ClassModel(sourceCode_ToBeTested, methodImports, onlineImports, classImports);
		srcCodeMap = model.getCodeMap();
	}
	
//	/**
//	 * FIXME: package handeln
//	 */
//	public void addOnlineImport(String onlineImports){
//		if(onlineImports == null || onlineImports.trim().isEmpty())
//			return;
//		for(String codeName : srcCodeMap.keySet()){
//			if(!srcCodeMap.get(codeName).trim().startsWith("package "))
//				srcCodeMap.put(codeName, onlineImports.trim()+"\n"+srcCodeMap.get(codeName));
//			else 
//				srcCodeMap.put(codeName, onlineImports.trim()+"\n"+srcCodeMap.get(codeName));
//		}
//	}
//	
//	/**
//	 * Gewünschte Mehtoden oder Feld-Definitionen werden am Ende !aller! zu testenden Klassen eingefügt. 
//	 * (Beziehungsweise aller zu dieser Zeit in srcCodeMap entahltenen Klassen)
//	 * @param methodImports
//	 */
//	private void addMethods(String methodImports){
//		if(methodImports == null || methodImports.trim().isEmpty())
//			return;
//		for(String codeName : srcCodeMap.keySet()){
//			StringBuffer codeString = new StringBuffer(srcCodeMap.get(codeName));
//			Integer closeIndex = codeString.lastIndexOf("}");
//			codeString.insert(closeIndex, methodImports.trim()+"\n");
//			srcCodeMap.put(codeName, codeString.toString());
//		}
//	}
//	/**
//	 * Fügt zu importierende Klassen der srcCodeMap hinzu.
//	 * @param classImports Klassen in String Form
//	 */
//	public void addClasses(String classImports){
//		if(classImports == null || classImports.trim().isEmpty())
//			return;
//		fillSrcCodeMap(classImports);
//	}
	public Vector<HashMap<String,String>> getFailures(){
		return compileFailures;
	}
	
	public Integer getLineOffset(){
		return unitLineOffset;
	}
	
	private void extractClassName(String unitSourceCode) throws PPException{
		this.unitClassName = CodeCompletion.extractClassName(unitSourceCode);
		if(unitClassName==null || unitClassName.trim().isEmpty()){
			throw new PPException(PPException.NoClassName);
		}
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
//	
//	/**
//	 * HashMap sourceCodeMap wird mit Inhalt gefüllt.<br>
//	 * Falls in src keine Klasse definiert wird, wird der Code "gewrapt". Dies geschieht, in dem eine Klasse namens <DEFAULT_CLASS_NAME> erstellt wird, kommt dieser Name bereits in der srcCodeMap (als Key) oder in src vor, werden Uterstriche angefügt, bis ein klassenname gefunden wurde, der nicht bereits belegt ist.<br>
//	 * Falls in src zusätzlich auch keine Methode deklariert ist, wird  auch diese "gewrapt". Dies geschieht, indem eine Methode namens <DEFAULT_METHOD_NAME> erstellt wird. Kommt dieser Name bereits in src vor, werden Unterstriche eingefügt, bis ein klassenname gefunden wurde, der nicht bereits belegt ist. <br>
//	 * Falls ein return enthalten ist, wird der Methode der Rückgabewert Object gegeben.
//	 * 
//	 * @param src Sourcecode
//	 */
//	private void fillSrcCodeMap(String src){
//		String method="";
//		if(srcCodeMap==null)
//			srcCodeMap = new HashMap<String, String>();
//		
//		 // Konflikte bezüglich des Klassennamens werden ausgeschlossen
//		 String className=new String(DEFAULT_CLASS_NAME);
//		 // Falls der Code keine Klasse enthält, wird hier eine generiert.
//		 if(!src.contains(" class ")){
//			 // Es wird überprüft, ob ein Methodenkopf existiert
//			 String patternString = "(public|protected|native|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;])";
//			 Pattern pattern = Pattern.compile(patternString);
//			 Matcher matcher_method = pattern.matcher(src);
//			 if(!matcher_method.find()){
//				 // Eindeutiger Methodenname wird erstellt
//				 String methodName = DEFAULT_METHOD_NAME;
//				 while(src.contains(methodName)){
//					 methodName+="_";
//				 }
//				 patternString = "\\s+[return]+\\s+.+;";
//				 pattern = Pattern.compile(patternString);
//				 Matcher matcher_ret = pattern.matcher(src);
//				 if(matcher_ret.find()){	// kein return Statement im Sourcecode
//					 method = "public Object "+methodName+"() {\n";
//				 }
//				 else	// return statement im Sourcecode
//					 method = "public void "+methodName+"(){\n";				 
//			 }
//			 
//			 while(src.contains(className) | srcCodeMap.containsKey(className)){
//				 className=className+"_";
//			 }
//			 
//			 // Code wird um Klasse und/oder Methode ergänzt.
//			 src = "class "+className+" {\n"+method+src+"}";
//			 if(!method.isEmpty()){
//				 src+="\n}";
//			 }
//			 System.out.println(src);
//			 srcCodeMap.put(className, src);
//		 }
//		 
//		 // Falls der Code " class " enthält, werden diese Klassen herausgefiltert.
//		 else{
//			 Vector<String> srcVector= new Vector<String>();
//			 for(String line: src.split("\n")){
//				 srcVector.add(line);
//			 }
//			 fillSrcCodeMap(srcVector);
//		 }
//	}
//	
//	/**
//	 * HashMap sourceCodeMap wird mit Inhalt gefüllt. Tut nicht, wenn keine codeLine mit " class " enthalten, spirch in codeLines keine Klasse definiert ist. 
//	 * Imports und packages werden herausgefiltert und Klassenspezifisch verarbeitet.
//	 * 
//	 * @param codeLines
//	 */
//	private void fillSrcCodeMap(Vector<String> codeLines){
//		String className=new String();
//		String packageString = new String();
//		LinkedList<String> importStrings = new LinkedList<String>();
//		
//		String solutionString = new String();
//			for(String line: codeLines){
//				String nextLine = null;
//				if(codeLines.indexOf(line)+1<codeLines.size()){
//					nextLine = codeLines.get(codeLines.indexOf(line)+1);
//				}
//				if(line.trim().startsWith("package ")){
//					if(packageString.isEmpty()){
//						packageString = CodeCompletion.extractDeclarationName(line, nextLine, "package");
////						packageString = line.substring(line.indexOf("package")+7, line.indexOf(";"));
////						packageString = packageString.trim();
//					}
//					else if(!className.isEmpty()){
//						srcCodeMap.put(className, solutionString);
//						packageString = new String();
//						importStrings = new LinkedList<String>();
//						className = new String();
//						solutionString=new String(line);
//					}
//				}
//				else if(CodeCompletion.extractDeclarationName(line, nextLine, "import")!=null){
//				//else if (line.trim().startsWith("import ")){
//					if(className.isEmpty()){
//						String importString = CodeCompletion.extractDeclarationName(line, nextLine, "import");
////						String importString = line.substring(line.indexOf("import")+6, line.indexOf(";"));
//						importStrings.add(importString.trim());
//					}
//					else{
//						srcCodeMap.put(className, solutionString);
//						packageString = new String();
//						importStrings = new LinkedList<String>();
//						className = new String();
//						solutionString=new String(line);
//					}
//				}
//				if(line.contains(" class ")){
//					if(className.isEmpty()){
//						className = CodeCompletion.extractClassName(line);
//						solutionString=solutionString+"\n"+line;
//					}
//					else{						
//						srcCodeMap.put(className, solutionString);
//						packageString = new String();
//						importStrings = new LinkedList<String>();
//						className = new String();
//						solutionString=new String(line);
//					}
//				}
//				else{
//					solutionString=solutionString+"\n"+line;
//				}
//			}
//		if(className.isEmpty())	
//			fillSrcCodeMap(solutionString);
//		else
//			srcCodeMap.put(className, solutionString);
//	}
//	
	@Deprecated
	/**
	 * Klassen in srcCodeMap werden in den Hauptspeicher geladen, stehen dann zwischenzeitlich zum Zugriff bereit und werden dann wieder gelöscht.
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
				// Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private void makeDiagnose(DiagnosticCollector<JavaFileObject> diagnostics, String className, String sourceCode){
		// Diagnose (bei aufgetretenem Fehler)
		for ( Diagnostic<?> diagnostic : diagnostics.getDiagnostics() )
		 {
			 HashMap <String, String> compileFailure = new HashMap<String, String>(9);
			 compileFailure.put("Klasse", ""+className);
			 compileFailure.put("Art", ""+diagnostic.getKind());
			 compileFailure.put("Quelle", ""+diagnostic.getSource());
			 compileFailure.put("Code", ""+sourceCode);
			 compileFailure.put("Nachricht",""+diagnostic.getMessage( null ) );
			 compileFailure.put("Zeile", ""+diagnostic.getLineNumber() );
			 compileFailure.put("Position", ""+diagnostic.getPosition());
			 compileFailure.put("Spalte", ""+diagnostic.getColumnNumber() );
			 compileFailure.put("Startpostion", ""+diagnostic.getStartPosition());
			 compileFailure.put("Endposition", ""+diagnostic.getEndPosition() );
			 compileFailures.add(compileFailure);		 
		 }	
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
		
		unitClassName = CodeCompletion.extractClassName(unitText);
		for(String importString: imports){
			if(!unitText.contains(importString))
				unitText=importString+unitText;
		}
		this.unitClassName = unitClassName;
		this.unitSourceCode = unitText;
		return run();
	}
	
	public MemClassLoader_JUnit compile(){
		String unitSourceCode = unitImports+this.unitSourceCode;
		System.out.println(srcCodeMap);
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		MemClassLoader_JUnit unitClassLoader = new MemClassLoader_JUnit();
		MemClassLoader normalClassLoader = new MemClassLoader();
	
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		JavaFileManager unitFileManager = new MemJavaFileManager_JUnit(diagnostics, compiler, unitClassLoader, normalClassLoader);
		System.out.println(unitClassName);
		System.out.println(unitSourceCode);
	
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
		CompilationTask task = compiler.getTask( null, unitFileManager,diagnostics, options, null, units );
		task.call();
		makeDiagnose(diagnostics);
		System.out.println(srcCodeMap);
		
		try {
			unitFileManager.close();
		} catch (IOException e1) { e1.printStackTrace(); }
		return unitClassLoader;
	}
	
	/**
	 * Führt die UnitTests, die im Konstruktor in einer Testklasse in Stringform übergeben wurde aus und bindet die Klasse(n), die ebenfalls im Construktor übergeben wurden, aus.
	 * @return Result
	 */
	public Result run(){
		MemClassLoader_JUnit unitClassLoader = compile();
		try {
			Result result = JUnitCore.runClasses(unitClassLoader.findClass(unitClassName));
			System.out.println("Fehler beim Test: "+result.getFailures());
			return result;
		} catch (ClassNotFoundException e1) {
			
			
			e1.printStackTrace(); return null; }
	}
}

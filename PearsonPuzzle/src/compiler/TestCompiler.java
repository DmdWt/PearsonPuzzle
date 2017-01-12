package compiler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

public class TestCompiler {
	

	// -------------------------- http://openbook.rheinwerk-verlag.de/java7/1507_19_002.html ---------------------
	
	
//	public static boolean compile(ArrayList solution) throws IOException{
//		
//		String solutionString= String.join(" ", solution);
//				
//		
//	
//		File javaSrcFile = new File( "B.java" );
//		Writer p = new FileWriter( javaSrcFile );
//		p.write( "class B { static { "+solutionString+" } }" );
//		p.close();
//		
//		
//		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//		StandardJavaFileManager fileManager = compiler.getStandardFileManager( null, null, null );
//		Iterable<? extends JavaFileObject> units;
//		units = fileManager.getJavaFileObjectsFromFiles( Arrays.asList( javaSrcFile ) );
////		Iterable<String> options = Arrays.asList( "-verbose" );
//		CompilationTask task = compiler.getTask( null, fileManager, null, null, null, units );
//		task.call();
//		fileManager.close();
//		
//		
//		URLClassLoader classLoader = new URLClassLoader(
//				  new URL[] { javaSrcFile.getAbsoluteFile().getParentFile().toURI().toURL() } );
//				try {
//					Class.forName( "B", true, classLoader );
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}    // Java Compiler API
//
//				javaSrcFile.delete();
//		
	
	private final static String DEFAULT_CLASS_NAME="testCode";
	private static Vector<HashMap<String, String>> compileFailures;
	
	public TestCompiler(){
		// TODO: Test Compiler ObjektKonstruktor verfassen
		}
	
	public static Vector<HashMap<String,String>> getFailures(){
		return compileFailures;
	}

		/**
		 * Kompiliert eine Liste von Codezeilen.
		 * @param solutionArray Liste von Codezeilen
		 * @return Kompilieren erfolgreich
		 */
		public static boolean compileCode(Vector<String> solutionArray){
			boolean ret=true;
			String className=new String();
			String solutionString = new String();
				for(String line: solutionArray){
					if(line.contains("class")){
						if(className.isEmpty()){
							className=line.substring(line.indexOf("class")+5, line.indexOf("{"));
							className=className.trim();
							solutionString=solutionString+"\n"+line;
						}
						else{
							ret= ret && compileCode(solutionString, className);
							solutionString=new String(line);
						}
					}
					else{
						solutionString=solutionString+"\n"+line;
					}
				}
			if(className.isEmpty())
				return compileCode(solutionString);
			else 
				return compileCode(solutionString, className);
		}
		
		public static boolean compileCode(String src){
			
			 compileFailures = new Vector<HashMap<String, String>>();
			 // Konflikte bezüglich des Klassennamens werden ausgeschlossen
			 String className=new String(DEFAULT_CLASS_NAME);
			 
			 // Falls der Code keine Klasse enthält, wird hier eine generiert 
			 if(!src.contains("class")){
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
			 
			 // FIXME: Wenn der Code meherere Klassen enthält, kommt es im Moment zum Problem
			 
		private static boolean compileCode(String src, String className){
			
			 StringJavaFileObject javaFile = new StringJavaFileObject( className, src );
			 JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			 DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			 StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnostics, null, null );
			 Iterable<? extends JavaFileObject> units = Arrays.asList( javaFile );
			 CompilationTask task = compiler.getTask( null, fileManager, diagnostics, null, null, units );
			 boolean success = task.call();
		
			 System.out.println( success ); // Compilieren erfolgreich oder nicht

			 // Diagnose (bei aufgetretenem Fehler)
			 for ( Diagnostic<?> diagnostic : diagnostics.getDiagnostics() )
			 {
				 HashMap <String, String> compileFailure = new HashMap<String, String>(9);
				 compileFailure.put("Kind", ""+diagnostic.getKind());
				 compileFailure.put("Quelle", ""+diagnostic.getSource());
				 compileFailure.put("Code", ""+diagnostic.getCode());
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
			 } catch (MalformedURLException e) {
				 return false;
			 }
			 try {
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





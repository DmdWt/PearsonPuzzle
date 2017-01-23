package compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;

public class Compiler {
	
	public static void main(String args[]){
		ArrayList<String> obj = new ArrayList<String>(
				Arrays.asList("System.out.println(","\"hansbelakjsöfar", "Harsh\")"));
//			  try {
				compile(obj);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	}
	
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
		
		 public static boolean compile(ArrayList solution){
		
		String solutionString= String.join(" ", solution);
				
		 
		String src = "class A { static { "+solutionString+"} }";
		StringJavaFileObject javaFile = new StringJavaFileObject( "A", src );



		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics =
				  new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnostics, null, null );
		Iterable<? extends JavaFileObject> units = Arrays.asList( javaFile );
		CompilationTask task = compiler.getTask( null, fileManager, diagnostics, null, null, units );
		boolean success = task.call();
		
		System.out.println( success ); // false

		// diagnose
		for ( Diagnostic<?> diagnostic : diagnostics.getDiagnostics() )
		{
		  System.out.printf( "Kind: %s%n", diagnostic.getKind() );
		  System.out.printf( "Quelle: %s%n", diagnostic.getSource() );
		  System.out.printf( "Code und Nachricht: %s: %s%n", diagnostic.getCode(),
		                                                     diagnostic.getMessage( null ) );
		  System.out.printf( "Zeile: %s%n", diagnostic.getLineNumber() );
		  System.out.printf( "Position/Spalte: %s/%s%n", diagnostic.getPosition(),
		                                                 diagnostic.getColumnNumber() );
		  System.out.printf( "Startpostion/Endposition: %s/%s%n", diagnostic.getStartPosition(),
		                                                          diagnostic.getEndPosition() );
		  System.out.println();
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
			Class.forName( "A", true, classLoader );
		} catch (ClassNotFoundException e) {
			return false;
		}    // Java Compiler API 2
		
		
		return true;
		
	}

}





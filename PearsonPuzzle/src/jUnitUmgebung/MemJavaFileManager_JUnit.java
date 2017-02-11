package jUnitUmgebung;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaFileObject.Kind;

/**
* Macht es möglich, Klassen in den Arbeitspeicher zu laden. 
* Stellt auch eine Methode für den Compiler bereit, damit dieser compilierte Klassen zum ClassLoader hinzufügen und im Arbeitsspeicher zur Verfügung stellen kann.
* Unterstützt im gegensatz zu MemClassLoader auch JUnit-Klassen.
* @author workspace
*/
public class MemJavaFileManager_JUnit extends ForwardingJavaFileManager<StandardJavaFileManager> {
	
	private final MemClassLoader_JUnit unitClassLoader;
	
	@SuppressWarnings("unused")
	private final MemClassLoader classLoader;

	 /**
	   * Arbeitsspeicher-Manager zum Halten von Klassen.
	   * @param compiler Compiler (z.B. standard Systemcompiler)
	   * @param classLoader ClassLoader
	   */
	public MemJavaFileManager_JUnit( JavaCompiler compiler, MemClassLoader_JUnit unitClassLoader, MemClassLoader classLoader)
	{
		super( compiler.getStandardFileManager( null, null, null ) );

		this.unitClassLoader = unitClassLoader;
		this.classLoader = classLoader;
	}
	
	/**
	   * Arbeitsspeicer Manager zum Halten von Klassen mit Diagnosefunktion. (Falls es zu Fehlern kommt, werden diese protokolliert)
	   * @param diagnostics Diagnosetool
	   * @param compiler Compiler (z.B. standard Systemcompiler)
	   * @param classLoader ClassLoader
	   */
	public MemJavaFileManager_JUnit( DiagnosticCollector<JavaFileObject> diagnostics, JavaCompiler compiler, MemClassLoader_JUnit unitClassLoader, MemClassLoader classLoader)
	{
		super( compiler.getStandardFileManager( diagnostics, null, null ) );
		
		this.unitClassLoader = unitClassLoader;
		this.classLoader = classLoader;
	}
	
	/**
	   * Methode wird vom Compiler aufgerufen, wenn dieser etwas speichern möchte.
	   * Stellt ein neues MemJavaFileObject bereit und fügt dieses dem ClassLoader hinzu, damit dieser die Klasse später laden kann.
	   */
	@Override
	public JavaFileObject getJavaFileForOutput( Location location,
	                                            String className,
	                                            Kind kind,
	                                            FileObject sibling )
	{
		// gibt bei Bedarf die Klassen an unterschiedliche Klassenloader weiter.
			//if(className.contains("Unit")){
				MemJavaFileObject fileObject = new MemJavaFileObject( className );
				unitClassLoader.addClassFile( fileObject );
				return fileObject;
	//		}
	//		else {
	//			MemJavaFileObject fileObject = new MemJavaFileObject(className);
	//			classLoader.addClassFile(fileObject);
	//			return fileObject;
	//		}
		}
	}

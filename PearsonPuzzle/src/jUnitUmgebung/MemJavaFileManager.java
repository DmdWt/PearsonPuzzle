package jUnitUmgebung;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;

/**
 * Macht es möglich, Dateien in den Arbeitspeicher zu laden. 
 * Stellt auch eine Methode für den Compiler bereit, damit dieser compilierte Klassen im Arbeitsspeicher zur Verfügung stellen kann.
 * @author workspace
 */
public class MemJavaFileManager extends
  ForwardingJavaFileManager<StandardJavaFileManager>
{
  private final MemClassLoader classLoader;

  /**
   * Arbeitsspeicher-Manager zum Halten von Klassen.
   * @param compiler Compiler (z.B. standard Systemcompiler)
   * @param classLoader ClassLoader
   */
  public MemJavaFileManager( JavaCompiler compiler, MemClassLoader classLoader )
  {
    super( compiler.getStandardFileManager( null, null, null ) );

    this.classLoader = classLoader;
  }
  
  /**
   * Arbeitsspeicer Manager zum Halten von Klassen mit Diagnosefunktion. (Falls es zu Fehlern kommt, werden diese protokolliert)
   * @param diagnostics Diagnosetool
   * @param compiler Compiler (z.B. standard Systemcompiler)
   * @param classLoader ClassLoader
   */
  public MemJavaFileManager( DiagnosticCollector<JavaFileObject> diagnostics, JavaCompiler compiler, MemClassLoader classLoader )
  {
    super( compiler.getStandardFileManager( diagnostics, null, null ) );

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
    MemJavaFileObject fileObject = new MemJavaFileObject( className );
    classLoader.addClassFile( fileObject );
    return fileObject;
  }
}
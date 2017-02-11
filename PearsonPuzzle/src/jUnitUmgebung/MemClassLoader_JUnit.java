package jUnitUmgebung;

import java.util.*;

import org.junit.runner.JUnitCore;

/**
 * Hält Klassen in einer Hash Map nach Namen sortiert zum Abruf bereit.
 * Exteded zwar den ClassLoader, der Konstruktor erbt aber vom ClassLoader des JUnitCore.
 * @author workspace
 *
 */

public class MemClassLoader_JUnit extends ClassLoader
{
  private final Map<String, MemJavaFileObject> classFiles =
    new HashMap<String, MemJavaFileObject>();

  /**
   * Entspricht dem Konstruktor des ClassLoaders des JUnitCore
   */
  public MemClassLoader_JUnit()
  {
	//super(UnitRunner.class.getClassLoader());
	 super(JUnitCore.class.getClassLoader());
  }

  /**
   * Fügt dem ClassLoader eine Klasse hinzu, um diese später abrufen zu können.
   * @param memJavaFileObject
   */
  public void addClassFile( MemJavaFileObject memJavaFileObject )
  {
    classFiles.put( memJavaFileObject.getClassName(), memJavaFileObject );
  }
  
  /**
   * Sucht im ClassLoader nach einer klasse mit entsprechendem Namen. 
   */
  @Override
  protected Class<?> findClass( String className ) throws ClassNotFoundException
  {
    MemJavaFileObject fileObject = classFiles.get( className );

    if ( fileObject != null )
    {
      byte[] bytes = fileObject.getClassBytes();
      return defineClass( className, bytes, 0, bytes.length );
    }
    return super.findClass( className );
  }
}

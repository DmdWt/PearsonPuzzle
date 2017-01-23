package jUnitUmgebung;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.JavaFileObject.Kind;

public class MemJavaFileManager_JUnit extends
ForwardingJavaFileManager<StandardJavaFileManager>
{
private final MemClassLoader_JUnit unitClassLoader;
private final MemClassLoader classLoader;

public MemJavaFileManager_JUnit( JavaCompiler compiler, MemClassLoader_JUnit unitClassLoader, MemClassLoader classLoader)
{
  super( compiler.getStandardFileManager( null, null, null ) );

  this.unitClassLoader = unitClassLoader;
  this.classLoader = classLoader;
}

@Override
public JavaFileObject getJavaFileForOutput( Location location,
                                            String className,
                                            Kind kind,
                                            FileObject sibling )
{
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

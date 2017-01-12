package JUnitUmgebung;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import compiler.StringJavaFileObject;

public class JUnitRunner {
	public static Result run(String unitText){
		if(!unitText.contains(" class "))
			return new Result();
		String className = new String();
		final String[] imports = {"import org.junit.Test; \n", "import static org.junit.Assert.*"};
		
		className=unitText.substring(unitText.indexOf("class")+5, unitText.indexOf("{")).trim();
		for(String importString: imports){
			if(!unitText.contains(importString))
				unitText=importString+unitText;
		}
		System.out.println(className);
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		MemClassLoader classLoader = new MemClassLoader();
		JavaFileManager fileManager = new MemJavaFileManager( compiler, classLoader );
		JavaFileObject javaFile = new StringJavaFileObject( className, unitText );
		Collection<JavaFileObject> units = Collections.singleton( javaFile );
		Set<String> options = new HashSet<String>();		
		options.add( "-verbose");
		options.add("-deprecation");
		CompilationTask task = compiler.getTask( null, fileManager,null, options, null, units );
		//System.out.println(task.call());
		task.call();
		try {
			fileManager.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			return JUnitCore.runClasses(classLoader.findClass(className));
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
//		
//		try {
//			System.out.println(unitText);
//			System.out.println(Class.forName(className,true, classLoader).newInstance().getClass());
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InstantiationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//
//		try {
//			return JUnitCore.runClasses(Class.forName(className, false, classLoader).getClass());
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}

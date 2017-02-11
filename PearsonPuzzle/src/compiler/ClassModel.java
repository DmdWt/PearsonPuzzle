package compiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassModel {
	
	private final static String DEFAULT_CLASS_NAME= "TestClass";
	private final static String DEFAULT_METHOD_NAME = "testMethod";
	private String constructedClass_Name;
	private String constructedMethod_Name;
	private HashMap<String, String> srcCodeMap;
	private HashMap<String, Collection<String>> importMap;
	private HashMap<String, String> packageMap;

	/**
	 * HashMap sourceCodeMap wird mit Inhalt gefüllt.<br>
	 * Falls in src keine Klasse definiert wird, wird der Code "gewrapt". Dies geschieht, in dem eine Klasse namens <DEFAULT_CLASS_NAME> erstellt wird, kommt dieser Name bereits in der srcCodeMap (als Key) oder in src vor, werden Uterstriche angefügt, bis ein klassenname gefunden wurde, der nicht bereits belegt ist.<br>
	 * Falls in src zusätzlich auch keine Methode deklariert ist, wird  auch diese "gewrapt". Dies geschieht, indem eine Methode namens <DEFAULT_METHOD_NAME> erstellt wird. Kommt dieser Name bereits in src vor, werden Unterstriche eingefügt, bis ein klassenname gefunden wurde, der nicht bereits belegt ist. <br>
	 * Falls ein return enthalten ist, wird der Methode der Rückgabewert Object gegeben.
	 * 
	 */
	public ClassModel(String sourceCode, String methodImports, String onlineImports, String classImports) {
		sourceCode = CodeCompletion.removeComment(sourceCode);
		methodImports = CodeCompletion.removeComment(methodImports);
		onlineImports = CodeCompletion.removeComment(onlineImports);
		classImports = CodeCompletion.removeComment(classImports);
		
		fillSrcCodeMap(sourceCode);
		addMethods(methodImports);
		addOnlineImport(onlineImports);
		addClasses(classImports);
	}
	
	/**
	 * Liefert eine Hash Map in der Form:
	 * Klassenname -> Sourcecode
	 * @return Klassenname -> Sourcecode
	 */
	public HashMap<String, String> getCodeMap(){
		return srcCodeMap;
	}
	
	/**
	 * Liefert eine Liste mit imports, die die Klasse benötigt. 
	 * Wollen diese verwendet werden, müssen die Einträge in der Collection im Stil <b>import importName;</b> verwendet werden
	 * @param className
	 * @return importNames
	 */
	public Collection<String> getImport(String className){
		if(importMap==null)
			return null;
		else if(importMap.containsKey(className))
			return importMap.get(className);
		else
			return null;
	}
	
	/**
	 * Liefert den Packetnamen zum Klassennamen.
	 * @param className
	 * @return packegeName
	 */
	public String getPackage(String className){
		if(packageMap==null)
			return null;
		else if(packageMap.containsKey(className))
			return packageMap.get(className);
		else
			return null;
	}
	
	/**
	 * Falls der im Konstruktor übergebene sourceCode keine Klasse enthält, wird eine solche erzeugt und der Code "gewrapt".
	 * Der Name der Klasse kann hier abgerufen werden.
	 * @return Name der künstliche konstruierten Klasse
	 */
	public String getConstructedClass_Name(){
		return constructedClass_Name;
	}
	
	/**
	 * Fall der im Kostruktor übergebene sourceCode keine Methode enthält, wird eine solche erzeugt und der Code "gewrapt". 
	 * Der Name der Methode kann hier abgerufen werden.
	 * @return Name der künstlich konstruierten Methode
	 */
	public String getContructedMethod_Name(){
		return constructedMethod_Name;
	}

	/**
	 * Fügt online imports (import ...;) zu Beginn der Klasse an.
	 * Dabei wird auf etwaige vorherige festgelegte packages geachtet.
	 */
	private void addOnlineImport(String onlineImports){
		if(onlineImports == null || onlineImports.trim().isEmpty())
			return;
		for(String codeName : srcCodeMap.keySet()){
			if(!srcCodeMap.get(codeName).trim().startsWith("package "))
				srcCodeMap.put(codeName, onlineImports.trim()+"\n"+srcCodeMap.get(codeName));
			else{
				Pattern patter = Pattern.compile("package\\s.*;");
				Matcher matcher = patter.matcher(srcCodeMap.get(codeName));
				matcher.find();
				srcCodeMap.put(codeName, 
						srcCodeMap.get(codeName).substring(0, matcher.end())
						+"\n"+onlineImports.trim()
						+srcCodeMap.get(codeName).substring(matcher.end()));
			}
		}
	}
	
	/**
	 * Gewünschte Mehtoden oder Feld-Definitionen werden am Ende !aller! zu testenden Klassen eingefügt. 
	 * (Beziehungsweise aller zu dieser Zeit in srcCodeMap entahltenen Klassen)
	 * @param methodImports
	 */
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
	/**
	 * Fügt zu importierende Klassen der srcCodeMap hinzu.
	 * @param classImports Klassen in String Form
	 */
	private void addClasses(String classImports){
		if(classImports == null || classImports.trim().isEmpty())
			return;
		fillSrcCodeMap(classImports);
	}
	
	/**
	 * HashMap sourceCodeMap wird mit Inhalt gefüllt.<br>
	 * Falls in src keine Klasse definiert wird, wird der Code "gewrapt". Dies geschieht, in dem eine Klasse namens <DEFAULT_CLASS_NAME> erstellt wird, kommt dieser Name bereits in der srcCodeMap (als Key) oder in src vor, werden Uterstriche angefügt, bis ein klassenname gefunden wurde, der nicht bereits belegt ist.<br>
	 * Falls in src zusätzlich auch keine Methode deklariert ist, wird  auch diese "gewrapt". Dies geschieht, indem eine Methode namens <DEFAULT_METHOD_NAME> erstellt wird. Kommt dieser Name bereits in src vor, werden Unterstriche eingefügt, bis ein klassenname gefunden wurde, der nicht bereits belegt ist. <br>
	 * Falls ein return enthalten ist, wird der Methode der Rückgabewert Object gegeben.
	 * 
	 * @param src Sourcecode
	 */
	private void fillSrcCodeMap(String src){
		String method="";
		if(srcCodeMap==null)
			srcCodeMap = new HashMap<String, String>();
		// TODO: Klasse in Klasse handeln
		
		 // Konflikte bezüglich des Klassennamens werden ausgeschlossen
		 String className=new String(DEFAULT_CLASS_NAME);
		 
		 // Falls der Code keine Klasse enthält, wird hier eine generiert.
		 if(!src.contains(" class ")){
			 // Es wird überprüft, ob ein Methodenkopf existiert
			 String patternString = "(public|protected|native|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;])";
			 Pattern pattern = Pattern.compile(patternString);
			 Matcher matcher_method = pattern.matcher(src);
			 
			 String methodName = DEFAULT_METHOD_NAME;
			 
			 if(!matcher_method.find()){
				 // Eindeutiger Methodenname wird erstellt
				 
				 while(src.contains(methodName)){
					 methodName+="_";
				 }
				 patternString = "\\s+[return]+\\s+.+;";
				 pattern = Pattern.compile(patternString);
				 Matcher matcher_ret = pattern.matcher(src);
				 if(matcher_ret.find()){		// kein return Statement im Sourcecode
					 method = "public Object "+methodName+"() {\n";
				 }
				 else							// return statement im Sourcecode
					 method = "public void "+methodName+"(){\n";				 
			 }
			 
			 while(src.contains(className) | srcCodeMap.containsKey(className)){
				 className=className+"_";
			 }
			 
			 // Code wird um Klasse und/oder Methode ergänzt.
			 src = "class "+className+" {\n"+method+src+"}";
			 constructedClass_Name = className;
			 
			 if(!method.isEmpty()){
				 constructedMethod_Name = methodName;
				 src+="\n}";
			 }
			 srcCodeMap.put(className, src);
		 }
		 
		 // Falls der Code " class " enthält, werden diese Klassen herausgefiltert.
		 else{
			 LinkedList<String> srcVector= new LinkedList<String>();
			 for(String line: src.split("\n")){
				 srcVector.add(line);
			 }
			 fillSrcCodeMap(srcVector);
		 }
	}
	
	/**
	 * HashMap sourceCodeMap wird mit Inhalt gefüllt. Tut nicht, wenn keine codeLine mit " class " enthalten, spirch in codeLines keine Klasse definiert ist. 
	 * Imports und packages werden herausgefiltert und können Klassenspezifisch verarbeitet werden.
	 * 
	 * @param codeLines
	 */
	private void fillSrcCodeMap(LinkedList<String> codeLines){
		String className=new String();
		String packageString = new String();
		LinkedList<String> importStrings = new LinkedList<String>();
		
		String solutionString = new String();
			for(String line: codeLines){
				String nextLine = null;
				if(codeLines.indexOf(line)+1<codeLines.size()){
					nextLine = codeLines.get(codeLines.indexOf(line)+1);
				}
				if(line.trim().startsWith("package ")){
					if(packageString.isEmpty()){
						packageString = CodeCompletion.extractDeclarationName(line, nextLine, "package");
//						packageString = line.substring(line.indexOf("package")+7, line.indexOf(";"));
//						packageString = packageString.trim();
					}
					else if(!className.isEmpty()){
						putClass(className, solutionString, packageString, importStrings);
						packageString = new String();
						importStrings = new LinkedList<String>();
						className = new String();
						solutionString=new String(line);
					}
				}
				else if(CodeCompletion.extractDeclarationName(line, nextLine, "import")!=null){
				//else if (line.trim().startsWith("import ")){
					if(className.isEmpty()){
						String importString = CodeCompletion.extractDeclarationName(line, nextLine, "import");
//						String importString = line.substring(line.indexOf("import")+6, line.indexOf(";"));
						importStrings.add(importString.trim());
					}
					else{
						putClass(className, solutionString, packageString, importStrings);
						packageString = new String();
						importStrings = new LinkedList<String>();
						className = new String();
						solutionString=new String(line);
					}
				}
				if(line.contains(" class ")){
					if(line.indexOf(" class ")!=line.lastIndexOf(" class ")){
						// FIXME: zwei Klassen in einer Zeile handeln
					}
					if(className.isEmpty()){
						className = CodeCompletion.extractClassName(line);
						solutionString=solutionString+"\n"+line;
					}
					else{					
						putClass(className, solutionString, packageString, importStrings);
						packageString = new String();
						importStrings = new LinkedList<String>();
						className = CodeCompletion.extractClassName(line);
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
			putClass(className, solutionString, packageString, importStrings);
	}

	private void putClass(String className, String solutionString,
			String packageString, LinkedList<String> importStrings) {
		if(srcCodeMap.containsKey(className)){
			// TODO: Mehrfacheinträge handeln
		}
		if(packageMap.containsKey(className)){
			// TODO: Mehrfacheinträge handeln
		}
		if(importMap.containsKey(className)){
			Collection<String> buffer = importMap.get(className);
			buffer.addAll(importStrings);
			importMap.put(className, buffer);
		}
			
		srcCodeMap.put(className, solutionString);
		
		if(packageString!=null && !packageString.isEmpty()){
			if(packageMap==null)
				packageMap = new HashMap<String, String>();
			packageMap.put(className, packageString);
		}
		if(importStrings!=null && !importStrings.isEmpty()){
			if(importMap==null)
				importMap = new HashMap<String, Collection<String>>();
			importMap.put(className, importStrings);
		}		
	}
	

}

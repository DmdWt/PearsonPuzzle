package compiler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasse mit primär ststischen Methoden, die primär dazu dienen, Sourccodeteile in der richtigen Reihenfolge zusammenzufügen, 
 * oder Informationen, wir Klassenname, Packeges und Imports aus derm Sourcecode zu extrahieren.
 * 
 * @author workspace
 */
public class CodeCompletion {
	
	public static String completeCode(String code, String onlineImports, String methodsImport){
		code=onlineImports+code;
		return code;
	}
	
	/**
	 * Sucht nach " class " und gibt den danach folgenden zusammenhängenden String wieder.
	 * Als Namensende kommt Whitespace oder '{' in Frage.
	 * @param sourceCode Sourcecode
	 * @return Klassenname (erster vorkommender)
	 */
	public static String extractClassName(String sourceCode){
		sourceCode = removeComment(sourceCode);
		if(sourceCode==null)
			return null;
		String className = null;
		String substring = null;
		Pattern pattern = Pattern.compile("(\\s+class+\\s)");
		Matcher matcher = pattern.matcher(sourceCode);
		if(matcher.find()){
			//Integer indexOfClass = matcher.start();
			Integer endIndex = sourceCode.indexOf("{",matcher.end());
			if(endIndex == -1)
				substring = sourceCode.substring(matcher.end()).trim();
			else
				substring = sourceCode.substring(matcher.end(), endIndex).trim();
			pattern = Pattern.compile("\\s");
			matcher = pattern.matcher(substring);
			if(matcher.find())
				className = substring.substring(0, matcher.start());
			else if(!substring.isEmpty())
				className = substring;
		}
		return className;
	}
	

	/**
	 * Estrahiert aus den beiden Codezeilen line1 und line2 den auf expression folgenden zusammenhängenden String. 
	 * Eignet sich primär, um packages und imports herauszufiltern.
	 * 
	 * @param line1	Codezeile
	 * @param line2	darauf folgende Codezeile
	 * @param expression {"import", "package"}
	 * @return	Paket- bzw. Import-Name
	 */
	public static String extractDeclarationName(String line1, String line2, String expression) {
		if(expression == null || line1 ==null )
			return null;
		System.out.println("finde "+expression+" in: \t"+line1+line2);
		if(line2==null)
			line2="";
		String declarationName=null;
		Pattern pattern = Pattern.compile("(;|\\s)+"+expression+"+\\s");
		Matcher matcher = pattern.matcher(line1.trim());
		// Wenn die ertse Zeile nur aus dem zu suchenden String besteht
		if(line1.trim().equals(expression.trim())){
			if(line2.trim().contains(" "))
				declarationName = line2.trim().substring(0, line2.indexOf(" ")).trim();
			else if(line2.contains(";"))
				declarationName = line2.substring(0, line2.indexOf(";")).trim();
			else
				declarationName = line2.trim();
		}
		// Wenn die erste Zeile mit dem zu suchenden String beginnt
		else if(line1.trim().startsWith(expression)){
			if(line1.trim().contains(" "))
				line1=line1.replaceFirst(expression, "").trim();
				if(line1.trim().contains(" "))
					declarationName = line1.substring(0, line1.indexOf(" ")).trim();
				else if(line1.contains(";"))
					declarationName = line1.substring(0, line1.indexOf(";")).trim();
				else 
					declarationName = line2.trim();
					
		}
		// Wenn die erste Zeile mit dem zu suchenden String endet
		else if(line1.trim().endsWith(expression))
			declarationName = line2.trim();
				
		// whitespace oder ; + import + whitespace
		else if(matcher.find()){
			line1 = line1.substring(matcher.end()).trim();
			if(line1.trim().contains(" "))
				declarationName = line1.substring(0, line1.indexOf(" ")).trim();
			else if(line1.contains(";"))
				declarationName = line1.substring(0, line1.indexOf(";")).trim();
			else
				declarationName = line2.trim();
		}
		
		// Fall die Zweite zeile mehr enthält, als die Deklaration
		if(declarationName!=null && declarationName.contains(";"))
			declarationName = declarationName.substring(0, declarationName.indexOf(";"));
		if(declarationName!=null && declarationName.isEmpty())
			return null;
		System.out.println(declarationName);
		return declarationName;				
	}
	
	/**
	 * Entfern alle Arten von Java-Kommentaren.
	*/
	public static String removeComment(String string){
		if(string==null)
				return null;
		String comment = "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";
		string = string.replaceAll(comment, "");
		return string;		
	}

}

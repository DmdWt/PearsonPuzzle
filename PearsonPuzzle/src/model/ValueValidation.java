package model;

import java.util.LinkedList;
import java.util.Vector;

/**
 * Klasse ist dafür zuständig, die Daten, die das Model erhält zu validieren und nötige Casts durchzuführen.<br>
 * 
 * @author workspace
 *
 */
public class ValueValidation {
	
	/**
	 * Passwort wird auf Stärke geprüft (mindestens eine Zahl, ein Sonderzeichen, einen Großbuchstaben und einen Kleinbuchstaben) 
	 * @param password
	 * @return Passwort entspricht Sicherheitsanforderungen
	 */
	public static boolean proovePassword(char[] password){
		boolean number = false;
		boolean specialChar = false;
		boolean upperCase = false;
		boolean lowerCase = false;
		
		for(char c : password){
			if(Character.isDigit(c))
				number = true;
			if(Character.isLowerCase(c))
				lowerCase = true;
			if(Character.isUpperCase(c))
				upperCase = true;
			int asci = (int)c;
			if(asci < 32 || asci >126 )	// auf nicht erlaubte Zeichen prüfen
				return false;
			else if(asci<48 || (asci > 57 && asci <65) || (asci >90 && asci < 97) || asci >123) // auf Sonderzeichen prüfen
				specialChar = true;
		}
		return (number && specialChar && upperCase && lowerCase);
	}
	
	public static boolean isValid_OnlineImport(String text){
		String[] parts = text.split("\n");
		for(String part : parts){
			if(part.trim().isEmpty())
				break;
			if(part.trim().startsWith("import: ")
					&& part.trim().endsWith(";")){
				part = part.replaceFirst("import ","");
				if(part.trim().contains(" "))
					return false;
			}
			else 
				return false;
			System.out.println("part: "+part);
		}
		return true;
	}
	
	public static Integer validateTabSize(Integer tabSize){
		if(tabSize==null)
			return 0;
		else if(tabSize>10)
			tabSize = 10;
		return tabSize;
	}
	public static Integer validateTabSize(String tabSize_String){
		Integer tabSize=null;
		try{
			tabSize = Integer.parseInt(tabSize_String);
		}
		catch(NumberFormatException e){
			return 0;
		}
		if(tabSize==null)
			return 0;
		else if(tabSize>10)
			tabSize = 10;
		return tabSize;
	}
	
	public static String removeEmptyLines(String string){
		System.out.println(string);
		if(string==null || string.isEmpty())
			return "";
		String[] projectCodeArray = string.split("\n");
		StringBuffer codeBuffer = new StringBuffer();
		for(String line: projectCodeArray){
			if(!line.trim().isEmpty())
				if(codeBuffer.length()==0)
					codeBuffer.append(line);
				else
					codeBuffer.append("\n"+line);
		}
		System.out.println("code Buffer: "+ codeBuffer.toString());
		return codeBuffer.toString();
	}

	public static boolean isValidProjectName(String projectName) {
		if(projectName.contains("_Test"))
			return false;
		return false;
	}
	
	public static boolean isValidRandomization(LinkedList<Integer> randomization, Vector<String> codeVector){
		if(randomization==null)
			return false;
		else if(randomization.isEmpty())
			return false;
		else if(randomization.size()!=codeVector.size())
			return false;
		for(Integer i=0; i<randomization.size(); i++){
			if(!randomization.contains(i))
				return false;
		}
		return true;
	}
}

package model;


import javax.swing.JList;


public class Code extends Element{
	static String[] CodeLines = {"Line1","Line2","Line3","Line4","Line5"};
	static JList<String> CodeList = new JList<String>(CodeLines);
	/*
	 * soll noch erweitert werden, Daten aus datenbank, List, ...
	 */
	Code(){
		
		
	}
	public static JList<String> getCode(){
		return CodeList;
	}
}

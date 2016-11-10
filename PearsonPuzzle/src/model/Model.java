package model;

import javax.swing.DefaultListModel;

/**
 * Klasse dient dazu, alle für die Benutzer notwendigen Daten einzulesen, zu halten
 * und zu speichern.
 * 
 * @author workspace
 *
 */

public class Model {
	private String username;
	private char[] password;
	private static Code code;
	private static DefaultListModel codeModel;
	private static DefaultListModel saveModel;
	public Model(){
		code = new Code();
		this.codeModel=code.getCode();
		this.saveModel=code.getSaveModel();
	}
	public void setPassword(char[] password) {
		this.password = password;
	}
	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		System.out.println(username);
		this.username=username;
	}
	public static DefaultListModel getCodeModel(){
		return codeModel;
	}
	public static DefaultListModel getSaveModel(){
		return saveModel;
	}
}

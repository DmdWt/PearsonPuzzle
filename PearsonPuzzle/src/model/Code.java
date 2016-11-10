package model;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
/**
 * Klasse, um den Code aus der Datenbank zu holen, zu speichern 
 * und für andere Klassen bereit zu stellen.
 * 
 * @author workspace
 *
 */
public class Code {
	private static String[] codeLines = {"Line1","Line2","Line3","Line4","Line5"};
	private static DefaultListModel code=new DefaultListModel();
	private static DefaultListModel saveModel=new DefaultListModel();
	private JList<JTextField> codeTextFields;
	/*
	 * soll noch erweitert werden, Daten aus datenbank, List, ...
	 */
	Code(){
		codeTextFields=new JList<JTextField>();
		for(String line: codeLines){
			codeTextFields.add(new JTextField(line));
			code.add(0, line);
			saveModel.add(0, new String());
		}
	}
	public void setCodeTextFields(){
		codeTextFields=new JList<JTextField>();
		for(String line: codeLines){
			codeTextFields.add(new JButton(line));
			System.out.println(JButton.getDefaultLocale());
		}	
	}
	public DefaultListModel getCode(){
		return code;
	}
	public DefaultListModel getSaveModel(){
		return saveModel;
	}

}

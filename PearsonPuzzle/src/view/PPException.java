package view;

import javax.swing.JOptionPane;

import model.Model;

public class PPException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String noDatabaseExists = "Es existiert noch keine Datenbank. \nWollen Sie eine neue Datenbank anlegen?";
	public final static String anotherDatasetExists = "<html>Datensatz mit gleichem Namen existiert bereits.<br>Wollen sie die existierende Datensatz überschreiben?</html>";
	public final static String anotherInstanceIsRunnign = "<html><body>Das Programm läuft bereits.<br>Sollte das Problem in einigen Minuten weiterhin auftreten,<br>versuchen Sie, das Programm über den Task Manager zu beenden.<br><b>Vorsicht:</b> dabei können ungespeicherte Änderungen verloren gehen.</body></html>";
	public final static String databaseIsEmpty = "Keine Einträge in der Datenbank gefunden";
	public final static String NoClassName = "Es wurde kein Klassenname für den Unittest angegeben. Daher kann nicht kompiliert werden.";
	private String answer;
	String[] yesNoCancelOptions = { "Ja", "Nein", "Abbrechen" };
	String[] yesNoOptions = {"Ja", "Nein"};
	
	//private final int exceptionKind;
	private final String reason;
	
	
	public PPException(String exceptionKind) {
		super();
		this.reason = exceptionKind;
	}
	
	public PPException(int maximaleLänge, int aktuelleLänge, String betroffenesFeld) {
		super();
		this.reason = "<html>"+betroffenesFeld+" leider zu lang. ("+aktuelleLänge+" Zeichen)<br>Bitte maximal "+maximaleLänge+" Zeichen verwenden</html>";
	}
	
	@Override
	public String getMessage(){
		return reason;
	}
	
	
	public Integer handleException(Model model){
		Integer n;
		switch(reason){
		
		case noDatabaseExists:
			model.setException(this);
			n = JOptionPane.showOptionDialog(null, noDatabaseExists, null , JOptionPane.YES_NO_CANCEL_OPTION,
			          JOptionPane.QUESTION_MESSAGE,  // Icon
			          null, yesNoCancelOptions,yesNoCancelOptions[0] );
			 answer = yesNoCancelOptions[n];
			 break;
		case databaseIsEmpty:
			model.setException(this);
			n=0;
			answer="";
			break;
			
		 case anotherDatasetExists:
			 n = JOptionPane.showOptionDialog(null, anotherDatasetExists, null , JOptionPane.YES_NO_CANCEL_OPTION,
			          JOptionPane.QUESTION_MESSAGE,  // Icon
			          null, yesNoCancelOptions,yesNoCancelOptions[0] );
			 answer = yesNoCancelOptions[n];
			 break;
		 case anotherInstanceIsRunnign: 
			 JOptionPane.showMessageDialog(null, anotherInstanceIsRunnign, 
			         null, JOptionPane.ERROR_MESSAGE  // Icon
					 );
			 n = 0;
			 answer = "OK";
			 break;
		 default:
			 JOptionPane.showMessageDialog(null, reason, null, JOptionPane.INFORMATION_MESSAGE);
			 answer = "";
			 n=null;
		 }
		return n;
//		this.model = model;
//		this.view = new InitializeAccess(model);
//		this.controller = new ExceptionController(model, view); 
	}
	
	public void printMessage(){
		handleException(null);
	}
	
	public String getAnswer(){
		return answer;
	}
	
}

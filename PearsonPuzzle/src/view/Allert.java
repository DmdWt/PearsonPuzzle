package view;

import java.awt.Component;

import javax.swing.JOptionPane;

import model.Model;
import model.access.AccessGroup;

/**
 * Klasse dient dazu, mögliche Allerts zu definieren und dem Nutzer <br>
 * Warnungen, Fehler auszugeben und eventuell um Bestätigung zu bitten.
 * @author workspace
 */
public enum Allert {
	noProjectSelected, noContentInput, projectSaved, projectDeleted, projectExists, notSaved, reset, Failure, chooseAccessGroup, deleteUser, help_Orders, code_not_fully_sorted, help_UnitTest;
	
	public Integer allert(Component parentComponent, Model model){
		Integer n;
		String[] yesNoCancelOptions = { "Ja", "Nein", "Abbrechen" };
		String[] yesNoOptions = {"Ja", "Nein"};
		switch(this){
			case help_Orders:
				JOptionPane.showMessageDialog(parentComponent, "<html><body style=\"text-align: center;\"><p style=\"width:700px; text-align:justify;\">An dieser Stelle werden Reihenfolgen festgelegt, in denen die SchülerInnnen die Codezeilen zusammensetzen sollen. <br>" +
						"Dabei werden Codezeilen, die nach anderen Codezeilen kommen sollen, mit höheren Zahlen markiert und die Codezeilen, die vorher kommen sollen, " +
						"mit niedrigeren Zahlen. Codezeilen, die austauschbar sind, werden mit gleichen Zahlen markiert." +
						" Die Zahl '0' wird beim Testen der Reihenfolgen nicht beachtet. Die Zahlen einer Reihenfolge müssen stets aufsteigend sein und sollten aufeinander folgen. " +
						"Über die Schaltfläche 'Gruppe hinzufügen' können weitere Reihenfolgen festgelegt werden.</p></body></html>", "Hilfetext", JOptionPane.INFORMATION_MESSAGE);
				return null;
			case help_UnitTest:
				JOptionPane.showMessageDialog(parentComponent, "<html><body style=\"width:700px; text-align: center;\"><p style=\"text-align:justify;\">Hier kann ein JUnit Test verfasst werden, der den zusammengepuzzelten Code testet (falls dieser kompilierbar ist). " +
						"Die hier zu Beginn hinterlegte Unit Testklasse dient nur als Beispiel und wird, solange sie unverändert (oder leer) bleibt, nicht ausgeführt. Ob ein JUnit Test hinterlegt ist oder nicht, ist über den Reiter \"JUnit\" ersichtlich. " +
						"Ist dieser blau eingefärbt, so ist ein Unit Test hinterlegt. Ist dieser hingegen schwarz, so ist kein Unit Test hinterlegt.<br><br>" +
						"" +
						"Unabhängig davon, ob ein Unit Test eingebunden wird, können:</p>" +
						"<ul style=\"text-align:left;\"><li>zusätzlich üben das Textfeld links oben noch etwaige nötige Imports angegeben werden. </li>" +
						"<li>zusätzliche spezielle oder eigene Klasse(n) angegeben werden, falls diese nötig sind, um den Puzzlecode auszuführen/zu kompilieren. Diese sollten in Textform über den entsprechenden Button angehängt werden.</li>" +
						"<li>zusätzliche Methode(n) angegeben werden, falss diese nötig sind, um den Puzzlecode ausführen/compilieren zu können. Diese werden dann dem Puzzlecode angehängt, sind aber im Puzzlemodus nicht sichtbar.</li>" +
						"</ul></body></html>", "Hilfetext", JOptionPane.INFORMATION_MESSAGE);
				return null;
			case noProjectSelected:
				JOptionPane.showMessageDialog(parentComponent, "Bitte Projekt auswählen");
				return null;
			case code_not_fully_sorted:
				JOptionPane.showMessageDialog(parentComponent, "<html><body>Code nicht vollstädnig sortiert, bitte sortieren Sie den Code fertig.<br> Erst dann ist diese Aktion möglich.</body></html>");
				return null;
			case noContentInput:
				JOptionPane.showMessageDialog(parentComponent,"Bitte Titel und Inhalt des Projekts angeben");
				return null;
			case projectSaved:
				JOptionPane.showMessageDialog(parentComponent,"Projekt wurde erfolgreich gespeichert");
				return null;
			case projectDeleted: 
				//JOptionPane.showMessageDialog(null, "Projekt wurde gelöscht");
				return null;
			case projectExists:
				JOptionPane.showMessageDialog(parentComponent, "Es existert ein Projekt mit gleichem Namen.\nBitte wählen Sie einen anderen Namen.");
				return null;
			case notSaved:
				n = JOptionPane.showOptionDialog( parentComponent,
				          "Änderungen am Projekt wurden noch nicht gespeichert.\nWollen Sie speichern bevor Sie fortfahren?",	// Nachricht/Frage
				          "Änderungen noch nicht gespeichert.",	// Titel
				          JOptionPane.YES_NO_CANCEL_OPTION,
				          JOptionPane.QUESTION_MESSAGE,  // Icon
				          null, yesNoCancelOptions,yesNoCancelOptions[0] );
				return n;
			case deleteUser:
				n = JOptionPane.showOptionDialog( parentComponent,
				          "<html>Sind sie sicher, dass Sie die angegeben Nutzer löschen wollen?<br>" +
				          "Bei unbedachtem Einsatz können Sie dadurch den Zugriff <br>" +
				          "auf die Datenbank verlieren.</html>",	// Nachricht/Frage
				          "Benutzer Löschen.",	// Titel
				          JOptionPane.YES_NO_OPTION,
				          JOptionPane.QUESTION_MESSAGE,  // Icon
				          null, yesNoOptions,yesNoOptions[0] );
				return n;
			case reset:
				n = JOptionPane.showOptionDialog(parentComponent, 
						"Sind Sie sicher, dass Sie die Datenbank zurücksetzten wollen? \nDies verwirft alle gespeicherten Projekte, Klassen und Schüler!",	// Nachricht/Frage
						"System Reset",	// Titel
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, // Icon
						null, yesNoOptions, yesNoOptions[0]);
				return n;
			case chooseAccessGroup:
				Object[] possibilities = AccessGroup.values();
				n = JOptionPane.showOptionDialog(parentComponent, 		// Frame
						"Wählen Sie ein Benutzerprofil aus:",	// Nachricht/Frage
						"Benutzerprofil wählen",	// Titel
						JOptionPane.OK_OPTION,
						JOptionPane.QUESTION_MESSAGE, // Icon
						null, 
						possibilities, 	// Optionen
						AccessGroup.TEACHER);
				return n;
				
			case Failure: 
				return null;
			default:
				return null;
		}
	}
	
	public static void allert(Component parentComponent, String message){
		if(message!=null)
			JOptionPane.showMessageDialog(parentComponent, message);
	}

}

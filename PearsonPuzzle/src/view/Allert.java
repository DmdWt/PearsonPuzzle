package view;

import javax.swing.JOptionPane;

import model.Model;
import model.access.AccessGroup;

/**
 * Klasse dient dazu, mögliche Allerts zu definieren und dem Nutzer <br>
 * Warnungen, Fehler auszugeben und eventuell um Bestätigung zu bitten.
 * @author workspace
 */
public enum Allert {
	noProjectSelected, noContentInput, projectSaved, projectDeleted, projectExists, notSaved, reset, Failure, chooseAccessGroup, deleteUser;
	
	public Integer allert(Model model){
		Integer n;
		String[] yesNoCancelOptions = { "Ja", "Nein", "Abbrechen" };
		String[] yesNoOptions = {"Ja", "Nein"};
		switch(this){
			case noProjectSelected:
				JOptionPane.showMessageDialog(null, "Bitte Projekt auswählen");
				return null;
			case noContentInput:
				JOptionPane.showMessageDialog(null,"Bitte Titel und Inhalt des Projekts angeben");
				return null;
			case projectSaved:
				JOptionPane.showMessageDialog(null,"Projekt wurde erfolgreich gespeichert");
				return null;
			case projectDeleted: 
				//JOptionPane.showMessageDialog(null, "Projekt wurde gelöscht");
				return null;
			case projectExists:
				JOptionPane.showMessageDialog(null, "Es existert ein Projekt mit gleichem Namen.\nBitte wählen Sie einen anderen Namen.");
				return null;
			case notSaved:
				n = JOptionPane.showOptionDialog( null,
				          "Änderungen am Projekt wurden noch nicht gespeichert.\nWollen Sie speichern bevor Sie fortfahren?",	// Nachricht/Frage
				          "Änderungen noch nicht gespeichert.",	// Titel
				          JOptionPane.YES_NO_CANCEL_OPTION,
				          JOptionPane.QUESTION_MESSAGE,  // Icon
				          null, yesNoCancelOptions,yesNoCancelOptions[0] );
				return n;
			case deleteUser:
				n = JOptionPane.showOptionDialog( null,
				          "<html>Sind sie sicher, dass Sie die angegeben Nutzer löschen wollen?<br>" +
				          "Bei unbedachtem Einsatz können Sie dadurch den Zugriff <br>" +
				          "auf die Datenbank verlieren.</html>",	// Nachricht/Frage
				          "Benutzer Löschen.",	// Titel
				          JOptionPane.YES_NO_OPTION,
				          JOptionPane.QUESTION_MESSAGE,  // Icon
				          null, yesNoOptions,yesNoOptions[0] );
				return n;
			case reset:
				n = JOptionPane.showOptionDialog(null, 
						"Sind Sie sicher, dass Sie die Datenbank zurücksetzten wollen? \nDies verwirft alle gespeicherten Projekte, Klassen und Schüler!",	// Nachricht/Frage
						"System Reset",	// Titel
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, // Icon
						null, yesNoOptions, yesNoOptions[0]);
				return n;
			case chooseAccessGroup:
				Object[] possibilities = AccessGroup.values();
				n = JOptionPane.showOptionDialog(null, 		// Frame
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

}

package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import view.dialog.AddImportDialog;
import view.dialog.AddUserDialog;
import view.dialog.DeleteOrderDialog;
import view.dialog.EditOrderDialog;

import model.Model;
import controller.Controller;
import controller.DCCommand;
import controller.DialogController;

/**
 * Design Klasse, um Erscheinungsbild der grafischen Oberfläche zu definieren
 * und auf jeweiligen Nutzer einzustellen.
 * 
 * Die Implementierung von Observer 
 * @author workspace
 *
 */

public abstract class JView implements Observer {
	private static JFrame frame = new JFrame("PearsonPuzzle");
	// BorderLayout mit 5 horizontalem und 1 vertikalem Versatz zwischen den Komponenten
	// mainPanel = new JPanel(new BorderLayout(5,1));
	protected static JPanel mainPanel = new JPanel(new BorderLayout(5,1));
	protected static Menu menu;
	protected Model model;
	private Controller controller;
	private DialogController dialogController;
	public JView(Model model){
			// main Panel wird bereinigt
			// da mainPanel static ist, ist dies notwendig (nur eine einzige Instanz existeiert) 
			// XXX: Herangehensweise ändern (protected static ist nicht optimal
			this.model=model;
			model.addObserver(this);
		}
		
		
		/**
		 * Methode soll vom Controller ausgeführt werden, um sich selbst <br>
		 * als konkreter Controller für einen konkreten View hinzuzufügen.
		 * Action Controller @param controller
		 */
		public void setController(Controller controller){
			this.controller=controller;
		}
		public abstract void addController(Controller controller);
		
		/**
		 * Methode, um das Fenster aufzusetzen ung grundlegende Fenstereinstellungen festzulegen. <br>
		 * Wird nur zu Beginn aufgerufen, wenn ein neues Fesnter erstellt werden soll, dies ist <br>
		 * in der Regel nur zu Beginn des Programms der Fall.
		 */
		public void setupFrame(){
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new FlowLayout());
			frame.add(mainPanel);
			frame.setSize(800,500);
			frame.setLocationRelativeTo(null);
		}
		
		/**
		 * Frame wird um Menü ergänzt.
		 * @param menuBar Menü
		 */
		public void addMenuToFrame(JMenuBar menuBar){
			frame.setJMenuBar(menuBar);
		}
		
		/**
		 * Frame wird auf sichtbar gesetzt und Größe festgelegt.
		 */
		public void draw(){
		    frame.pack();
			frame.setSize(800,520);
			if(frame.isVisible()==false){
				frame.setVisible(true);
			}
		}
		
		public void setMainContent(Container contentPane){		
			frame.setContentPane(contentPane);
		}
		
		/**
		 * @return Controller dieses View 
		 */
		public Controller getController(){
			// TODO: !!! Return kann noch null sein
			return controller;
		}
		
		/**
		 * Der View soll geschlossen werden (in erster Linie das mainPanel)
		 */
		public void quitView(){
			mainPanel.removeAll();
			model.deleteObserver(this);
		}
		
		/**
		 * Hier müssen alle Elemente notiert werden, die aktualisiert <br>
		 * die aktualiseirt werden sollen können. <br>
		 * (Methodenaufruf erfolgt durch den Controller) 
		 */
		abstract public void update();
		abstract public void update(Observable o, Object arg);
		public void selectView(int i){};
		
		/**
		 * Frame wird geschlossen.
		 */
		public void exit() {
		    frame.dispose();
		}
		
		
		/**
		 * Nachricht wird als Allert ausgegeben.
		 * @param message Darzustellende Nachricht
		 */
		public void showDialog(String message) {
			JOptionPane.showMessageDialog(frame, message);
		}
		
		/**
		 * Es wird eine Nachricht, Meldung oder Warnung ausgegeben
		 * @param allert
		 * @return Antwort des Benutzers 
		 */
		public Integer showDialog(Allert allert){
			if(allert==Allert.projectSaved){
				//menu.add(new JLabel("Projekt wurde gespeichert"));
				return null;
			}
			else
				return allert.allert(model);
		}
		public void showDialog(final PPException exception, boolean modal){
			view.dialog.JDialog dialog;
			if(exception.getMessage()==PPException.databaseIsEmpty){
				// Titel "Ersten Nutzer anlegen" wichtig für Dialog Controller
				dialog = new AddUserDialog(frame, model, "Ersten Nutzer anlegen");
			}
			else 
				dialog=null;
			dialogController = new DialogController(model, dialog);
			dialog.pack();
			dialog.setVisible(true);
			dialog.repaint();
		}
		public Integer showDialog(final DCCommand command, boolean modal){
			view.dialog.JDialog dialog;
			// Titel "Nutzer hinzufügen" wichtig für Dialog Controller
			switch(command){
				case AddUser:
					dialog = new AddUserDialog(frame, model, "Nutzer hinzufügen");
					break;
				case AddClasses:
					dialog = new AddImportDialog(frame, model, "Nötige Klassen");
					break;
				case AddMethods:
					dialog = new AddImportDialog(frame, model, "Nötige Methoden");
					break;
				case DeleteOrder:
					dialog = new DeleteOrderDialog(frame, model, "Gruppe löschen");
					break;
				case ShowHelp:
					JOptionPane.showMessageDialog(frame, "An dieser Stelle werden Reihenfolgen festgelegt, in denen die SchülerInnen die Codezeilen zusammensetzen sollen.\n"+
														"Dabei werden Codezeilen, die nach anderen Codezeilen kommen sollen, mit höheren Zahlen markiert und die Codezeilen, die vorher kommen sollen, mit niedrigeren Zahlen.\n"+
														"Codezeilen, die untereinander austauschbar sind, werden mit gleichen Zahlen markiert.\n"+
														"Die Zahl '0' wird beim Testen der Reihenfolgen nicht beachtet.\n"+
														"Die Zahlen einer Reihenfolge müssen stets aufsteigend sein und sollten aufeinander folgen.\n"+
														"Über die Schaltfläche 'Gruppe hinzufügen' können weitere Reihenfolgen festgelegt werden.\n"+
														"Über die Schaltfläche 'Gruppe explizieren' können individuelle Fehlermeldungen für nicht Einhalten einer Reihenfolge eingegeben werden.");
					dialog = null;
					break;
				case EditOrderGroup:
					dialog = new EditOrderDialog(frame, model, "Gruppe expliziter beschreiben");
					break;
				default:
					dialog = null;
					break;
			}			
			if(dialog!=null){
				dialogController = new DialogController(model, dialog);
				dialog.pack();
				dialog.setVisible(true);
				dialog.repaint();
			}
			return null;
		}
		public void closeAllert(){
			
			//optionPane.setVisible(false);
			//frame.remove(optionPane);
		}
		public Object get(String variable){
			return null;
		}
}

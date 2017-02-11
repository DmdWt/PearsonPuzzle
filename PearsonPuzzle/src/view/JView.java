package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Observer;

import javax.swing.*;

import view.dialog.AddImportDialog;
import view.dialog.AddUserDialog;
import view.dialog.CompileDialog;
import view.dialog.DeleteOrderDialog;
import view.dialog.EditOrderDialog;
import view.teacher.ConfigEditor;
import view.teacher.UnitEditor;

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

public abstract class JView implements Observer, View {
	
	private static JFrame frame = new JFrame("PearsonPuzzle");
	protected static final Color DEFAULTBUTTONCOLOR = (new JButton()).getBackground();
	protected static final ImageIcon saveIcon = new ImageIcon("rsc/icon/file/save_blue.png");
	
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
		
		/**
		 * Methode, um das Fenster aufzusetzen ung grundlegende Fenstereinstellungen festzulegen. <br>
		 * Wird nur zu Beginn aufgerufen, wenn ein neues Fesnter erstellt werden soll, dies ist <br>
		 * in der Regel nur zu Beginn des Programms der Fall.
		 */
		public void setupFrame(){
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new FlowLayout());
			//mainPanel.setPreferredSize(new Dimension(1000,500));
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
		public void drawFrame(){
		    frame.pack();
			frame.setSize(850,500);
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
			Allert.allert(frame, message);
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
				return allert.allert(frame, model);
		}
		public void showDialog(final PPException exception, boolean modal){
			view.dialog.JDialog dialog;
			if(exception.getMessage()==PPException.databaseIsEmpty){
				Object[] options = {"Datensatz laden", "Datensatz erstellen"};
				int n = JOptionPane.showOptionDialog(null,
					    "<html><body>Es wurde keine Datenbank gefunden.<br>Wollen Sie einen existierenden Datensatz laden oder eigene Projekte anlegen?</body></html>",
					    "Datenbank wählen",
					    JOptionPane.OK_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[0]);
				if(n==0){
					JFileChooser fc_imp = new JFileChooser();
					int returnVal_imp = fc_imp.showOpenDialog(new JPanel());

		            if (returnVal_imp == JFileChooser.APPROVE_OPTION) {
		                File file = fc_imp.getSelectedFile();
		                model.replaceDatabase(file.getName(), file.getParentFile().getAbsolutePath());
		            }
					return;
				}
				else
					dialog = new AddUserDialog(null, model, "Ersten Nutzer anlegen");
			}
			else 
				dialog=null;
			dialogController = new DialogController(model, dialog);
			dialog.addController(dialogController);
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
					if(this.getClass().equals(ConfigEditor.class))
						showDialog(Allert.help_Orders);
					else if(this.getClass().equals(UnitEditor.class))
						showDialog(Allert.help_UnitTest);
					dialog = null;
					break;
				case EditOrderGroup:
					dialog = new EditOrderDialog(frame, model, "Gruppe expliziter beschreiben");
					break;
				case Compile:
					dialog = new CompileDialog(frame, model, "Ergebnis des Kompiliervorgangs");
					break;
				default:
					dialog = null;
					break;
			}			
			if(dialog!=null){
				dialogController = new DialogController(model, dialog);
				dialog.addController(dialogController);
				dialog.pack();
				dialog.setVisible(true);
				dialog.repaint();
			}
			return null;
		}

		public Object get(String variable){
			return null;
		}
		public void hide(){
			frame.setVisible(false);
		}
		
		/**
		 * Pimär für Tests gedacht.
		 * @return Model
		 */
		public Model getModel(){
			return model;
		}
}

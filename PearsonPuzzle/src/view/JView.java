package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import model.Model;
import controller.Controller;

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
		public void addMenuToFrame(JMenuBar menuBar){
			frame.setJMenuBar(menuBar);
		}
		
		/**
		 * Frame wird auf sichtbar gesetzt und Größe festgelegt.
		 */
		public void draw(){
		    frame.pack();
			frame.setSize(800,500);
			if(frame.isVisible()==false){
				frame.setVisible(true);
			}
		}
		
		/**
		 * Controller dieses View @return
		 */
		public Controller getController(){
			// TODO: !!! Return kann noch null sein
			return controller;
		}
		
		/**
		 * Der View soll geschlossen werden (in erster Linie das mainPanel
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
		 * Nachricht wird als Allert ausgegeben
		 * Nachricht @param message
		 */
		public void allert(String message) {
			JOptionPane.showMessageDialog(frame, message);
		}
		
		/**
		 * Frame soll geschlossen werden.
		 */
		public void exit() {
		    frame.dispose();
		}
		
		/**
		 * Es wird eine Nachricht, Meldung oder Warnung ausgegeben
		 * @param allert
		 * Antwort des Benutzers @return
		 */
		public Integer showMessage(Allert allert){
			if(allert==Allert.projectSaved){
				//menu.add(new JLabel("Projekt wurde gespeichert"));
				return null;
			}
			else
				return allert.allert(model);
		}
		public void closeAllert(){
			
			//optionPane.setVisible(false);
			//frame.remove(optionPane);
		}
}

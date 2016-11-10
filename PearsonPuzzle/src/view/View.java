package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;
import controller.Controller;

/**
 * Design Klasse, um Erscheinungsbild der grafischen Oberfläche zu definieren
 * und auf jeweiligen Nutzer einzustellen.
 * 
 * @author workspace
 *
 */

public abstract class View{
		protected static JFrame frame;
		protected static JPanel mainPanel;
		private Controller controller;
		
		/**
		 * Methode, um das Fenster aufzusetzen ung grundlegende Fenstereinstellungen festzulegen. 
		 * Wird nur zu Beginn aufgerufen, wenn ein neues Fesnter erstellt werden soll, dies ist 
		 * in der Regel nur zu Beginn des Programms der Fall.
		 */
		protected void buildFrame(){
			frame = new JFrame("PearsonPuzzle");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new FlowLayout());
			System.out.println("Neuer Frame erstelle");
			// BorderLayout mit 5 horizontalem und 1 vertikalem Versatz zwischen den Komponenten
			mainPanel = new JPanel(new BorderLayout(5,1));
			frame.add(mainPanel);
			frame.setSize(800,500);
		}
		public void draw(){
			if(frame.isVisible()==false){
				frame.setVisible(true);
			}
			// TODO: wenn frame bereits sichtbar ist, updaten
		}
		public View makePupilView(DefaultListModel codeModel, DefaultListModel<String> saveDropList, String username){
			this.quitView();
			return new PupilView(codeModel, saveDropList, username);
		}	
		public void addController(Controller controller){
			this.controller=controller;
		}
		public void quitView(){
		}
}

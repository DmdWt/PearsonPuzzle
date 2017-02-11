package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.Model;
/**
 * Oberklasse der verschiedenen Controller, die alle dafür zuständig sind, <br>
 * Daten vom Model an den jeweiligen darstellenden View weiterzureichen und <br>
 * bei Interaktion des Benutzers die von den Listenern erfassten Veränderungen <br>
 * an das Model weiterzureichen.
 * 
 * @author workspace
 *
 */
public interface Controller extends java.awt.event.ActionListener, ListSelectionListener, ItemListener {
	
	/**
	 * ActionEvents, wi zum Beispiel das Drücken von Buttons wird verarbeitet.
	 */
	void actionPerformed(ActionEvent arg0);
	
	/**
	 * List-Selektionen können verarbeitet werden.
	 */
	void valueChanged(ListSelectionEvent arg0);
	
	/**
	 * Ist primär für Tests gedacht. 
	 * @return Die View des Controllers
	 */
	public Object getView();
	
	/**
	 * Ist primät für Tests gedacht.
	 * @return Das Model des Controllers
	 */
	public Model getModel();

}

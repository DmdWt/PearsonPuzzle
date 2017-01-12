package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import view.JView;
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
	
	public abstract void actionPerformed(ActionEvent arg0);
	
	public abstract void valueChanged(ListSelectionEvent arg0);
	
	public JView getView();
}

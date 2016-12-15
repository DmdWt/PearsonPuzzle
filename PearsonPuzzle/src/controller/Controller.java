package controller;

import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;

import view.JView;
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
public abstract class Controller implements java.awt.event.ActionListener,  ListSelectionListener, ItemListener, FocusListener, MouseListener, TableModelListener{
	protected Model model;
	protected JView view;
	public Controller(Model model, JView view){
		this.model=model;
		this.view=view;
		view.addController(this);
	}
	
	public abstract void actionPerformed(ActionEvent arg0);
	
	public abstract void valueChanged(ListSelectionEvent arg0);
	
	public JView getView(){
		return view;
	}
}

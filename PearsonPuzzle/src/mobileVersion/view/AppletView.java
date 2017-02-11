package mobileVersion.view;

import java.awt.BorderLayout;

import java.util.Observer;
import javax.swing.JPanel;

import model.Model;
import view.Allert;
import view.PPException;
import view.View;

import controller.Controller;

public abstract class AppletView extends JPanel implements Observer, View{
	
	private static final long serialVersionUID = -5146860469857534054L;
	private Model model;
	
	public AppletView(Model model) {
		super(new BorderLayout());
		this.model = model;
		if(model.getException()!=null)
			showDialog(model.getException(), true);
	}

	public abstract Object get(String valueToReturn);
	
	public void draw(){
		this.setOpaque(true);
		this.setVisible(true);
	}
	
	/**
	 * Nachricht wird als Allert ausgegeben.
	 * @param message Darzustellende Nachricht
	 */
	public void showDialog(String message) {
		Allert.allert(this, message);
	}
	public Integer showDialog(Allert allert) {
		return allert.allert(this, model);
	}
	public void showDialog(PPException exception, boolean b) {
		@SuppressWarnings("unused")
		view.dialog.JDialog dialog;
		if(exception.getMessage()==PPException.databaseIsEmpty);
	}

	public abstract void removeController(Controller Controller);
}

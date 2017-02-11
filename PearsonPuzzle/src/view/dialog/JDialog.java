package view.dialog;

import java.awt.Frame;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;

import controller.DialogController;

import model.Model;

public abstract class JDialog extends javax.swing.JDialog implements Observer {
	private static final long serialVersionUID = -5942298207950804210L;
	public JDialog(Frame frame, Model model, String title){
    	super(frame, true);
    	model.addObserver(this);
    	setTitle(title);
	}
	public JDialog(JDialog dialog, Model model, String title){
    	super(dialog, false);
    	model.addObserver(this);
    	setTitle(title);
	}
	
	public abstract void addController(DialogController controller);
	public abstract JOptionPane getOptionPane();

	public abstract void clearAndHide();
	public void clear(){
		setVisible(false);
	}
	public abstract Object get(String string);
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

}

package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.event.ListSelectionEvent;

import model.Model;
import view.JView;

public class ExceptionController implements Controller{
	
	private JView view;
	private Model model;

	public ExceptionController(Model model, JView view) {
		this.model=model;
		this.view=view;
		view.addController(this);
		view.setController(this);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(DCCommand.Save)){
			model.saveUser(view.get("username"), view.get("password"), view.get("accessgroup"));
		}
	}

	
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public JView getView() {
		return view;
	}
	
	public Model getModel(){
		return model;
	}

}

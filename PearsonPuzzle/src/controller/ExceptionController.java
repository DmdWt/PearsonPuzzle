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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(DCCommand.Save)){
			model.saveUser(view.get("username"), view.get("password"), view.get("accessgroup"));
		}
	}

	
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JView getView() {
		return view;
	}

}

package controller;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;

import model.Model;
import view.JView;

public class LoginController implements Controller{
	
	private Model model;
	private JView view;
	
	public LoginController(Model model, JView view) {
		this.model=model;
		this.view=view;
		view.addController(this);
		view.setController(this);
	}
	JTextField username;
	JPasswordField password;
	
    public void LoginListener(JTextField username, JPasswordField password) {
        this.username = username;
        this.password = password;
    }
	public void actionPerformed(ActionEvent e) {
		model.setUsername(username.getText());
		view.update();
	}
	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public JView getView() {
		// TODO Auto-generated method stub
		return null;
	}
}
	/*
	String username; 
	char[] password;

	public LoginController(Model model, View view, JTextField username, JPasswordField password) {
		super(model, view);
		try {
			this.username=username.getText(0, 20);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		view.addController(this);	
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		//model.setUsername(view.getUsername());
		System.out.println(username);
		this.view=new PupilView();
		// TODO Auto-generated method stub
		
	}

}
*/
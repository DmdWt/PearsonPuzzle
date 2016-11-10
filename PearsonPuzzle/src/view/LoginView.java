package view;

import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Listener.LoginListener;

import controller.Controller;
import controller.DefaultController;
/**
 * Definiert die Login Ansicht, die zugleich als start Screen fungiert.
 * 
 * @author workspace
 *
 */
public class LoginView extends View{
	private JPanel loginPanel;
	private JButton enter;
	private JTextField username;
	private JPasswordField password;
	public LoginView(){
		buildFrame();
		frame.setLocationRelativeTo(null);
		loginPanel = new JPanel();
		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
		frame.add(loginPanel);
		username = new JTextField("Name");
		username.setActionCommand("asdsd");
		JLabel label = new JLabel("Login");
		label.setLabelFor(username);
		loginPanel.add(label);
		loginPanel.add(username);
		loginPanel.add(new JLabel("Password"));
		password = new JPasswordField(10);
		password.setName("pwd");
		loginPanel.add(password);
		enter = new JButton("Los gehts");
		//enter.setActionCommand();
		loginPanel.add(enter);
	}
	// Controller ist dafür zuständig
	//public void addActionListener(ActionListener listener){
		//enter.addActionListener(listener);
		//enter.addActionListener(controller);
		//username.addActionListener(controller);
		//password.addActionListener(controller);
	//}
	public void addController(Controller controller) {
		//enter.addActionListener(controller);
		enter.addActionListener(new LoginListener(username, password, controller));
		//username.addActionListener(controller);
		//password.addActionListener(controller);
	}
	public void quitView(){
		loginPanel.removeAll();
	}

}

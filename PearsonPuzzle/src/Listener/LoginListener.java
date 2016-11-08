package Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


import controller.Controller;
/**
 * Klasse dient dazu, auf Betätigung des Login Buttons zu warten und dann 
 * eingegebenes Passwort und Nutzernamen an den Controller weiterzuleiten.
 * 
 * @author workspace
 *
 */
public class LoginListener implements ActionListener {
	String username; 
	JPasswordField password;
	Controller controller;

    public LoginListener(JTextField username, JPasswordField password, Controller controller) {
        this.username = username.getText();
        this.password = password;
        this.controller = controller;
    }
	public void actionPerformed(ActionEvent e) {
		controller.login(username, password.getPassword());
    }
}
package mobileVersion.view;

import java.util.Observable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import controller.Controller;
import controller.DCCommand;

import model.Model;

public class LoginAView extends AppletView {
	private static final long serialVersionUID = -6804602953131314060L;
	private JPanel mainPanel;
	private JPanel loginPanel;
	private JTextField username;
	private JPasswordField password;
	private JButton enter;
	private Model model;
	
	public LoginAView(Model model){
		super (model);
		this.model = model;
		mainPanel = new JPanel();
		this.setPreferredSize(this.getToolkit().getScreenSize());
		
		setupLoginPanel();
		add(mainPanel);
	}
	private void setupLoginPanel(){
		loginPanel = new JPanel();
		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
		
		username = new JTextField("TUM");
		username.setActionCommand(DCCommand.Login.toString());
		JLabel label = new JLabel("Login");
		label.setLabelFor(username);
		loginPanel.add(label);
		loginPanel.add(username);
		
		loginPanel.add(new JLabel("Password"));
		password = new JPasswordField("TUM");
		password.setName("pwd");
		password.setActionCommand(DCCommand.Login.toString());
		loginPanel.add(password);
		
		enter = new JButton("Los gehts");
		enter.setActionCommand(DCCommand.Login.toString());
		loginPanel.add(enter);
		
		mainPanel.add(loginPanel);
		
		// Für Erstbenutzung prüfen, ob Datenbank existiert
		if(model.getException()!=null)
			showDialog(model.getException(), true);
	}
	
	/**
	 * Controller mit Action Listener Implementierung @param controller
	 */
	public void addController(Controller controller) {
		enter.addActionListener(controller);
		username.addActionListener(controller);
		password.addActionListener(controller);
	}
	@Override
	public void removeController(Controller controller) {
		enter.removeActionListener(controller);
		username.removeActionListener(controller);
		password.removeActionListener(controller);
	}
	@Override
	public Object get(String valueToReturn) {
		if(valueToReturn.equals("username"))
			return username.getText();
		else if(valueToReturn.equals("password"))
			return password.getPassword();
	return null;
	}
	
	public void update(Observable arg0, Object arg1) {	
	}
}

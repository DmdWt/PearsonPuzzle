package view;

import java.util.Observable;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import controller.Controller;
import controller.DCCommand;
import controller.DefaultController;
import model.Model;

/**
 * Definiert die Login Ansicht, die zugleich als start Screen fungiert.
 * 
 * @author workspace
 *
 */
public class LoginView extends JView{
	private JPanel loginPanel;
	private JButton enter;
	private JTextField username;
	private JPasswordField password;
	public LoginView(Model model){
		super(model);
		setupFrame();
		setupLoginPanel();
		this.addMenuToFrame(new JMenuBar());
		draw();
	}
	
	private void setupLoginPanel(){
		loginPanel = new JPanel();
		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.PAGE_AXIS));
		
		username = new JTextField("TUM");
		username.setActionCommand(DCCommand.SubmitPassword.toString());
		JLabel label = new JLabel("Login");
		label.setLabelFor(username);
		loginPanel.add(label);
		loginPanel.add(username);
		
		loginPanel.add(new JLabel("Password"));
		password = new JPasswordField("TUM");
		password.setName("pwd");
		password.setActionCommand(DCCommand.SubmitPassword.toString());
		loginPanel.add(password);
		
		enter = new JButton("Los gehts");
		enter.setActionCommand(DCCommand.SubmitPassword.toString());
		loginPanel.add(enter);
		
		mainPanel.add(loginPanel);
	}
	
	/**
	 * Controller mit Action Listener Implementierung @param controller
	 */
	public void addController(Controller controller) {
		enter.addActionListener(controller);
		username.addActionListener(controller);
		password.addActionListener(controller);
	}
	
	/**
	 * TODO: Sollte auf eine Login Methode des Models zugreifen
	 * Login Methode wird ausgeführt
	 */
	public void submitChangeToController(){
		if(this.getController().getClass().equals(DefaultController.class)){
			((DefaultController)(this.getController())).login(username.getText(), password.getPassword());
		}
	}
	@Override
	public void update() {
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}

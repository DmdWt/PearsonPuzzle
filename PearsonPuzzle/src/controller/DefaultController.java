package controller;

import java.awt.event.ActionEvent;

import model.Model;
import view.LoginView;
import view.View;
/**
 * Klasse dient dazu, die standardmäßige Benutzeroberfläche aufzurufen und 
 * mit dem Controller zu verknüpfen.
 * 
 * 
 * @author workspace
 *
 */
public class DefaultController extends Controller{
	public DefaultController(Model model, LoginView view) {
		super(model, view);
		view.addController(this);
		//view.addActionListener()
	}
	public DefaultController(Model model, View view) {
		super(model, view);
		view.addController(this);
	}
	/*public void submitPassword(JPasswordField password){
		if(password!=null)
		{
		model.setPassword(password.getPassword());
		}
	}
	public void submitUsername(JTextField username){
		try{
			model.setUsername(username.getText(0, 20));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	private JTextField jTextField;
	public void textFieldListener(JTextField jTextField){
		this.jTextField=jTextField;
	}*/

	@Override
	public void actionPerformed(ActionEvent e) {
		view.draw();	
	}
}

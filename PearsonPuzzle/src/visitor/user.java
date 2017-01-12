package visitor;

import javax.swing.UIManager;

import controller.Controller;
import controller.DefaultController;
import controller.ExceptionController;
import model.Model;
import view.JView;
import view.LoginView;
import view.dialog.InitializeAccess;

/**
 * Klasse dient dazu, die GUI zu erzeugen.<br>
 * Soll später zur Factory werden, um Controller, View und Model zu verknüpfen.
 * 
 * @author workspace
 */
public class user {
	private static Controller controller;
	public static void main (String args[]){
//		try {
//			  UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
//		} catch( Exception e ) { e.printStackTrace(); }
		setupGUI();
	}
	public static void setupGUI(){
		Model model = new Model();
		LoginView startView = new LoginView(model);
		controller = new DefaultController(model, startView);
		startView.draw();
	}
	public static void setupExceptionGUI(){
		Model model = new Model();
		JView view = new InitializeAccess(model);
		controller = new ExceptionController(model, view);
		view.draw();
	}
}

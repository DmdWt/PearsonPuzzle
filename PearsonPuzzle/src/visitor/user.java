package visitor;

import java.sql.SQLException;

import javax.swing.UIManager;

import controller.Controller;
import controller.DefaultController;
import model.Model;
import model.DB.DBModelTransfer;
import view.LoginView;

/**
 * Klasse dient dazu, die GUI zu erzeugen.<br>
 * Soll später zur Factory werden, um Controller, View und Model zu verknüpfen.
 * 
 * @author workspace
 */
public class user {
	
	
	public static void main (String args[]){
//		try {
//			  UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
//		} catch( Exception e ) { e.printStackTrace(); }
		setupGUI();
	}
	public static void setupGUI(){
		Model model = new Model();
		LoginView startView = new LoginView(model);
		Controller controller = new DefaultController(model, startView);
		startView.draw();
	}
}

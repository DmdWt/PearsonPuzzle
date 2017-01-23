package visitor;

import jUnitUmgebung.JUnitRunner;

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
		//runUnitTest();
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
	public static void runUnitTest(){
		
		final String unitText = "import org.junit.Test; \nimport static org.junit.Assert.*; \n\npublic class testcase_Test{\n\t@Test\n\t"+"public void testMethode1(){ \n"+"\t\tassertTrue( true);\n\t}}\n";
		final String code = "public class test{public static boolean runt(){int a=0; int b=3; int c=a+b; return true;}}";
		JUnitRunner jUnitRunner = new JUnitRunner(unitText, code, null);
		jUnitRunner.run(unitText);
	}
}

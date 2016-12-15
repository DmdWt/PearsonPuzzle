package ControllerTest;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import javax.swing.JButton;
import org.junit.*;

import model.Model;
import view.*;
import view.pupil.CodeSortView;
import view.pupil.PupilView;
import view.teacher.TeacherView;
import view.teacher.TextEditor;
import controller.Controller;
import controller.DCCommand;
import controller.DefaultController;

public class LogoutButtonTest {

	private Model model;
	
	@Before
	public void create(){
		model = new Model();
	}
	
	private void sleep(int duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	@Test 
	public void logout_LoginView(){
		JView view = new LoginView(model);
		Controller controller = new DefaultController(model, view);
		JButton testButton = new JButton();
		sleep(100);
		ActionEvent e = new ActionEvent(testButton, 1, DCCommand.Logout.toString());
		controller.actionPerformed(e);
		assertEquals(controller.getView().getClass(), LoginView.class);
	}

	@Test 
	public void logout_LogoutPupilView(){
		//model.setAccessGroup(AccessGroup.PUPIL);
		JView view = new PupilView(model);
		Controller controller = new DefaultController(model, view);
		JButton testButton = new JButton();
		sleep(100);
		ActionEvent e = new ActionEvent(testButton, 1, DCCommand.Logout.toString());
		controller.actionPerformed(e);
		assertEquals(controller.getView().getClass(), LoginView.class);
	}
	@Test 
	public void logout_CodeSortView(){
		//model.setAccessGroup(AccessGroup.PUPIL);
		JView view = new CodeSortView(model);
		Controller controller = new DefaultController(model, view);
		JButton testButton = new JButton();
		sleep(100);
		ActionEvent e = new ActionEvent(testButton, 1, DCCommand.Logout.toString());
		controller.actionPerformed(e);
		assertEquals(controller.getView().getClass(), LoginView.class);
	}
	@Test 
	public void logout_TeacherView(){
		//model.setAccessGroup(AccessGroup.TEACHER);
		JView view = new TeacherView(model);
		Controller controller = new DefaultController(model, view);
		JButton testButton = new JButton();
		sleep(100);
		ActionEvent e = new ActionEvent(testButton, 1, DCCommand.Logout.toString());
		controller.actionPerformed(e);
		assertEquals(controller.getView().getClass(), LoginView.class);
	}
	@Test 
	public void logout_TextEditorView(){
		//model.setAccessGroup(AccessGroup.TEACHER);
		JView view = new TextEditor(model);
		Controller controller = new DefaultController(model, view);
		JButton testButton = new JButton();
		sleep(100);
		ActionEvent e = new ActionEvent(testButton, 1, DCCommand.Logout.toString());
		controller.actionPerformed(e);
		assertEquals(controller.getView().getClass(), LoginView.class);
	}
	
	@After
	public void sleep(){
		sleep(10);
	}
}

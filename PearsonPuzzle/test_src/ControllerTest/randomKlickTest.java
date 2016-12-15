package ControllerTest;
import static org.junit.Assert.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JButton;

import model.AccessGroup;
import model.Model;

import org.junit.*;

import view.JView;
import view.pupil.PupilView;
import controller.Controller;
import controller.DCCommand;
import controller.DefaultController;


public class randomKlickTest {
	
	
	@Test
	public void randomKlicktestPupil() {
		randomSwingKlick(AccessGroup.PUPIL);
		
	}
	@Test
	public void randomKlicktestTeacher() {
		randomSwingKlick(AccessGroup.TEACHER);
	}
	@Test
	public void randomKlicktestUnknown(){
		randomSwingKlick(AccessGroup.UNKNOWN);
	}
	
	private void randomSwingKlick(final AccessGroup accessGroup){
		final int numberOfKlicks = 20;
		// ----- Array mit möglichen Kommandos wird erstellt
		ArrayList <String> commandList = new ArrayList<String>();
		for(DCCommand command : DCCommand.values()){
			commandList.add(command.toString());
		}
		for(int i=0; i<numberOfKlicks;i++){
			int randomInt = new java.util.Random().nextInt(commandList.size());
			Model model = new Model();
			model.setAccessGroup(accessGroup);
			JView view = new PupilView(model);
			Controller controller = new DefaultController(model, view); 
			JButton testButton = new JButton();
			ActionEvent e = new ActionEvent(testButton, 1, commandList.get(randomInt));
			// ----- Es wird versucht zu klicken.
			try{
				controller.actionPerformed(e);
			}
			catch(Exception ex){
				ex.printStackTrace();
				fail("Fehler bei Befehl "+commandList.get(randomInt)+"in View "+view.getClass().getName());
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				fail("Konnte Thread nicht pausieren");
			}
			// Wenn Allert erscheint, 
		}
	}
}

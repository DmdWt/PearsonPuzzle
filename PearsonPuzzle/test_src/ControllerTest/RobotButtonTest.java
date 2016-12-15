package ControllerTest;

import static org.junit.Assert.*;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.junit.*;

import view.LoginView;
import visitor.user;

public class RobotButtonTest {
	
	LoginView view;
	Robot bot;
	@Before
	public void init(){
		user.setupGUI();	
	}

	@Test
	public void timeoutTest() {
		try {
			bot=new Robot();} 
		catch (AWTException e) {
			fail("Robot konnte nicht  Initialisiert werden");
		}	
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		bot.mouseMove((int)screenSize.width/2, (int)screenSize.height/2-110);
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.getAutoDelay();
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		bot.getAutoDelay();
		bot.mousePress(InputEvent.BUTTON1_MASK);
		bot.getAutoDelay();
		bot.mouseRelease(InputEvent.BUTTON1_MASK);
		sleep(4000);
	}
	@Test
	public void enterTest(){
		try {
			bot=new Robot();} 
		catch (AWTException e) {
			fail("Robot konnte nicht  Initialisiert werden");
		}
		bot.keyPress(KeyEvent.VK_ENTER);
		sleep(100);
		bot.keyRelease(KeyEvent.VK_ENTER);
		sleep(4000);
	}
	
	private void sleep(int time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

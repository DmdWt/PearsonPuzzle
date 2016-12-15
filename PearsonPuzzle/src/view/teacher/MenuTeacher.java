package view.teacher;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import view.Menu;

import controller.Controller;
import controller.DCCommand;

/**
 * Menü in der Lehrerperspektive
 * 
 * @author workspace
 */
public class MenuTeacher extends Menu{
	private static final long serialVersionUID = 1L;
	private List <JMenuItem> menuItems;
	
	MenuTeacher(){
		menuItems=new ArrayList <JMenuItem>();
		setupMenu();
		}
	
	@Override
	protected void setupMenu(){
		JMenu mainMenu = new JMenu("Projekte");
		JMenu classMenu = new JMenu("Klassen");
		JMenu configMenu = new JMenu("Account");
		
		menuItems.add(new JMenuItem("Neues Projekt"));
		menuItems.get(menuItems.size()-1).setActionCommand(DCCommand.NewProject.toString());
		menuItems.get(menuItems.size()-1).setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_N, 
		        java.awt.Event.CTRL_MASK));		
		
		menuItems.add(new JMenuItem("Projekte anzeigen"));
		menuItems.get(menuItems.size()-1).setActionCommand(DCCommand.ProjectList.toString());
		menuItems.get(menuItems.size()-1).setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_A, 
		        java.awt.Event.CTRL_MASK));
		
		menuItems.add(new JMenuItem("Klassen verwalten"));
		menuItems.get(menuItems.size()-1).setActionCommand("editClass");
		
		menuItems.add(new JMenuItem("Account verwalten"));
		menuItems.get(menuItems.size()-1).setActionCommand(DCCommand.Admin.toString());
		menuItems.add(new JMenuItem("Logout"));
		menuItems.get(menuItems.size()-1).setActionCommand(DCCommand.Logout.toString());
		
		this.add(mainMenu);
		this.add(classMenu);
		this.add(configMenu, JMenuBar.RIGHT_ALIGNMENT);
		int seperator=0;
		for(JMenuItem menuItem: menuItems){
			if(seperator<2){
				mainMenu.add(menuItem);
			}
			else if(seperator<3){
				classMenu.add(menuItem);
			}
			else{
				configMenu.add(menuItem);
			}
			seperator++;
		}
	}
	@Override
	public void addActionListener(Controller controller){
		for(JMenuItem menuItem : menuItems){
	    	menuItem.addActionListener(controller);
	    }
	}
}

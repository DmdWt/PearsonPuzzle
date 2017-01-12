package view.teacher;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JButton;
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
	private List <JButton> extendedNavigation;
	
	MenuTeacher(){
		menuItems=new ArrayList <JMenuItem>();
		extendedNavigation = new ArrayList <JButton>();
		setupMenu();
	}
	MenuTeacher(int navigationSelection){
		menuItems=new ArrayList <JMenuItem>();
		extendMenu();
		this.setNavigation(navigationSelection);
		setupMenu();
	}
	
	@Override
	protected void setupMenu(){
		JMenu mainMenu = new JMenu("Projekte");
		JMenu classMenu = new JMenu("Nutzer");
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
		
		menuItems.add(new JMenuItem("Nutzer hinzufügen"));
		menuItems.get(menuItems.size()-1).setActionCommand(DCCommand.AddUser.toString());
		menuItems.add(new JMenuItem("Nutzer löschen"));
		menuItems.get(menuItems.size()-1).setActionCommand(DCCommand.EditUsers.toString());
		
		menuItems.add(new JMenuItem("Account verwalten"));
		menuItems.get(menuItems.size()-1).setActionCommand(DCCommand.Admin.toString());
		menuItems.add(new JMenuItem("Logout"));
		menuItems.get(menuItems.size()-1).setActionCommand(DCCommand.Logout.toString());
		
		this.add(Box.createHorizontalGlue());
		this.add(mainMenu);
		this.add(classMenu);
		this.add(configMenu, JMenuBar.RIGHT_ALIGNMENT);
		int seperator=0;
		for(JMenuItem menuItem: menuItems){
			if(seperator<2){
				mainMenu.add(menuItem);
			}
			else if(seperator<4){
				classMenu.add(menuItem);
			}
			else{
				configMenu.add(menuItem);
			}
			seperator++;
		}
	}
	private void extendMenu(){
		// Wenn das Menü noch nicht erweitert ist, geschieht dies hier.
		if(this.getComponentCount()<=menuItems.size()){
			extendedNavigation = new ArrayList <JButton>();
			extendedNavigation.add(new JButton("Code"));
			extendedNavigation.get(extendedNavigation.size()-1).setActionCommand(DCCommand.EditProject.toString());
			extendedNavigation.add(new JButton("Reihenfolgen"));
			extendedNavigation.get(extendedNavigation.size()-1).setActionCommand(DCCommand.ConfigureProject.toString());
			extendedNavigation.add(new JButton("JUnit"));
			extendedNavigation.get(extendedNavigation.size()-1).setActionCommand(DCCommand.EditJUnit.toString());
			for(JButton comp: extendedNavigation){
				comp.setOpaque(true);
				comp.setBackground(Color.WHITE);
				//comp.setMaximumSize(new Dimension(200,20));
				this.add(comp);
			}
		}
	}
	public void reduceMenu(){
		for(int i=this.getComponentCount(); i>menuItems.size();i++){
			this.remove(i);
		}
	}
	private void setNavigation(int navigationIndex){
		if(extendedNavigation.size()>=navigationIndex){
			for(JButton comp: extendedNavigation){
				comp.setBackground(Color.WHITE);
			}
			extendedNavigation.get(navigationIndex).setBackground(Color.LIGHT_GRAY);
		}
	}
	
	@Override
	public void addActionListener(Controller controller){
		for(JMenuItem menuItem : menuItems){
	    	menuItem.addActionListener(controller);
	    }
		for(JButton comp: extendedNavigation){
			comp.addActionListener(controller);
		}
	}
}

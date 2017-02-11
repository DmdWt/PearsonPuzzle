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

import model.Model;
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
	private JMenuItem save;
	private Model model;
	
	MenuTeacher(){
		menuItems=new ArrayList <JMenuItem>();
		extendedNavigation = new ArrayList <JButton>();
		setupMenu();
	}
	MenuTeacher(Model model, int navigationSelection){
		this.model=model;
		menuItems=new ArrayList <JMenuItem>();
		extendMenu();
		this.setNavigation(navigationSelection);
		setupMenu();
	}
	private void extendMenu(){
		JButton buttonBuffer;
		Color containsSomething = new Color(013220);
		// Wenn das Menü noch nicht erweitert ist, geschieht dies hier.
		if(this.getComponentCount()<=menuItems.size()){
			extendedNavigation = new ArrayList <JButton>();
			buttonBuffer = new JButton("Code");
			if(model.getProjectCode() != null && !model.getProjectCode().isEmpty())
				buttonBuffer.setForeground(containsSomething);
			buttonBuffer.setActionCommand(DCCommand.EditProject.toString());
			extendedNavigation.add(buttonBuffer);
			
			buttonBuffer = new JButton("Reihenfolgen");
			if(model.getGroupMatrix() != null && !model.getGroupMatrix().isEmpty()){
				buttonBuffer.setForeground(containsSomething);
				//buttonBuffer.setForeground(Color.LIGHT_GRAY);
			}
			buttonBuffer.setActionCommand(DCCommand.EditConfig.toString());
			extendedNavigation.add(buttonBuffer);
			
			buttonBuffer = new JButton("JUnit");
			if(model.getJUnitCode() != null && !model.getJUnitCode().isEmpty() && model.getJUnitCode()!=UnitEditor.DEFAULT_UNIT_CODE){
				buttonBuffer.setForeground(containsSomething);
			}
			buttonBuffer.setActionCommand(DCCommand.EditJUnit.toString());
			extendedNavigation.add(buttonBuffer);
			
			buttonBuffer = new JButton("Preview");
			if(model.getProjectCode()!= null && !model.getProjectCode().isEmpty())
				buttonBuffer.setForeground(containsSomething);
			buttonBuffer.setActionCommand(DCCommand.EditPreview.toString());
			extendedNavigation.add(buttonBuffer);
			
			for(JButton comp: extendedNavigation){
				comp.setOpaque(true);
				comp.setBackground(Color.WHITE);
				//comp.setMaximumSize(new Dimension(200,20));
				this.add(comp);
			}
			
		}
	}
	
	@Override
	protected void setupMenu(){
		JMenu mainMenu = new JMenu("Projekte");
		JMenu classMenu = new JMenu("Nutzer");
		JMenu configMenu = new JMenu("Account");
		
		JMenuItem itemBuffer;
		
		itemBuffer = new JMenuItem("Neues Projekt");
		itemBuffer.setActionCommand(DCCommand.NewProject.toString());
		itemBuffer.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_N, 
		        java.awt.Event.CTRL_MASK));		
		menuItems.add(itemBuffer);
		
		itemBuffer = new JMenuItem("Projekte anzeigen");
		itemBuffer.setActionCommand(DCCommand.ProjectList.toString());
		itemBuffer.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_P, 
		        java.awt.Event.CTRL_MASK));
		menuItems.add(itemBuffer);
		
		
		itemBuffer = new JMenuItem("Datenbank exportieren");
		itemBuffer.setActionCommand(DCCommand.DB_Export.toString());
		itemBuffer.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_E, 
		        java.awt.Event.CTRL_MASK));
		menuItems.add(itemBuffer);
		
		itemBuffer = new JMenuItem("Nutzer verwalten");
		itemBuffer.setActionCommand(DCCommand.EditUsers.toString());
		itemBuffer.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_MINUS, 
		        java.awt.Event.CTRL_MASK));
		menuItems.add(itemBuffer);
		
		itemBuffer = new JMenuItem("Account verwalten");
		itemBuffer.setActionCommand(DCCommand.Admin.toString());
		menuItems.add(itemBuffer);
		
		itemBuffer = new JMenuItem("Logout");
		itemBuffer.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_Q, 
		        java.awt.Event.CTRL_MASK));
		itemBuffer.setActionCommand(DCCommand.Logout.toString());
		menuItems.add(itemBuffer);
		
		this.add(Box.createHorizontalGlue());
		this.add(mainMenu);
		this.add(classMenu);
		this.add(configMenu, JMenuBar.RIGHT_ALIGNMENT);
		int seperator=0;
		for(JMenuItem menuItem: menuItems){
			if(seperator<3){
				mainMenu.add(menuItem);
			}
			else if(seperator<5){
				classMenu.add(menuItem);
			}
			else{
				configMenu.add(menuItem);
			}
			seperator++;
		}
		save = new JMenuItem("save");
		save.setActionCommand(DCCommand.Save.toString());
		save.setAccelerator(KeyStroke.getKeyStroke(
		        java.awt.event.KeyEvent.VK_S, 
		        java.awt.Event.CTRL_MASK));
		configMenu.add(save);
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
		
		save.addActionListener(controller);
		save.setVisible(false);
	}
}

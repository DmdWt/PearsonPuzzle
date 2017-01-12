package view.teacher;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import model.Model;
import model.access.AccessGroup;

import controller.Controller;
import controller.DCCommand;
import view.JView;

public class UserEditor extends JView{

	private JButton deleteUsers;
	private JButton addUser;
	private LinkedList<JCheckBox> users;
	private LinkedList<JRadioButton> userGroup;

	public UserEditor(Model model) {
		super(model);
		menu = new MenuTeacher();
		this.addMenuToFrame(menu);
		setupEditor();
		draw();
	}

	private void setupEditor() {
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.PAGE_AXIS));
		selectionPanel.add(new JLabel("Nutzergruppe"));
		selectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		userGroup = new LinkedList<JRadioButton>();
		ButtonGroup buttonGroup = new ButtonGroup();
		for(AccessGroup ac : AccessGroup.values()){
			JRadioButton radioButton = new JRadioButton(ac.toString());
			radioButton.setName(ac.toString());
			userGroup.add(radioButton);
			buttonGroup.add(radioButton);
			selectionPanel.add(radioButton);
			if(ac.equals(model.getUserGroup_toEdit()))
				radioButton.setSelected(true);
		}
		
		JPanel usersPanel = new JPanel(new GridLayout(model.getUsers(model.getUserGroup_toEdit()).size()+1, 2,5,5));
		usersPanel.setMaximumSize(new Dimension(500,model.getUsers(model.getUserGroup_toEdit()).size()*10));
		JPanel buttonPanel = new JPanel();
		
		users = new LinkedList<JCheckBox>();
		usersPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		usersPanel.add(new JLabel("Nutzername"));
		usersPanel.add(new JLabel("Gruppe"));
		for(String username: model.getUsers(model.getUserGroup_toEdit())){
			JCheckBox checkbox = new JCheckBox(username, false);			
			users.add(checkbox);
			usersPanel.add(checkbox);
			usersPanel.add(new JLabel("("+model.getUserGroup_toEdit()+")"));
		}
		JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		userPanel.add(usersPanel);
		JScrollPane userPanel_SP = new JScrollPane(userPanel);
		userPanel_SP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		userPanel_SP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		userPanel_SP.setPreferredSize(new Dimension(500,300));
		userPanel_SP.setMaximumSize(new Dimension(500, 400));
		//userPanel_SP.setBorder(null);
		
		deleteUsers = new JButton("<html>Markierte<br>Nutzer löschen</html>");
		deleteUsers.setActionCommand(DCCommand.Save.toString());
		addUser = new JButton("<html>Neuen Nutzer<br>anlegen</html>");
		addUser.setActionCommand(DCCommand.AddUser.toString());
		buttonPanel.add(deleteUsers);
		buttonPanel.add(addUser);
		buttonPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		mainPanel.add(selectionPanel, BorderLayout.WEST);
		mainPanel.add(userPanel_SP, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	}
	
//	private void removeController(Controller controller){
//		addUser.removeActionListener(controller);
//		deleteUsers.removeActionListener(controller);
//		for(JCheckBox box: users){
//			box.removeItemListener(controller);
//		}
//		for( JRadioButton radio : userGroup)
//			radio.removeItemListener(controller);
//		
//	}

	@Override
	public void addController(Controller controller) {
		addUser.addActionListener(controller);
		deleteUsers.addActionListener(controller);
		menu.addActionListener(controller);
		for(JCheckBox box: users){
			box.addItemListener(controller);
		}
		for( JRadioButton radio : userGroup)
			radio.addItemListener(controller);
	}
	
	public Vector<String> getUsers(){
		Vector<String> userList = new Vector<String>();
		for(JCheckBox box: users)
			if(box.isSelected())
				userList.add(box.getText());
		return userList;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Observable o, Object arg) {
		//this.removeController(getController());
		setupEditor();
		this.setController(getController());
		//setMainContent(mainPanel);
		this.draw();
	}

}

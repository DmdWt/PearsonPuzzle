package view.teacher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import controller.Controller;
import controller.DCCommand;
import view.JView;
import model.Model;

public class ProjectConfiguration extends JView{
	
	private JList <String> codeList;
	private JList <Integer> sequenceList;
	private ArrayList <JTextField> inputList;
	
	private JButton save;
	private JButton newSelection;
	private JButton newGroup;
	private JButton saveGroup;
	private JButton cancelSelection;
	private JButton deleteGroup;
	private JButton showHelp;
	
	private JTable projectTable;
	private DefaultTableModel tableModel;
	
	
	public ProjectConfiguration(Model model) {
		super(model);
		menu = new MenuTeacher(1);
		this.addMenuToFrame(menu);
		setupConfigPanel();
		draw();
	}
	
	private void setupConfigPanel(){
		tableModel = new DefaultTableModel();
		projectTable = new JTable(tableModel);
		updateTable();
		JScrollPane projectTable_SP = new JScrollPane(projectTable);
		projectTable_SP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		projectTable_SP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		projectTable_SP.setPreferredSize(new Dimension(500,300));
		projectTable_SP.setMaximumSize(new Dimension(500, 350));
		projectTable_SP.setBorder(null);
		
		JPanel editGroup_Buttons = new JPanel();
		editGroup_Buttons.setLayout(new BoxLayout(editGroup_Buttons, BoxLayout.Y_AXIS));
		newSelection = new JButton("<html><body style=\"text-align:center;\">Selektion<BR>starten</body></html>");
		newSelection.setActionCommand(DCCommand.StartGroupSelection.toString());
		
		cancelSelection = new JButton("<html><body style=\"text-align:center;\">Selektion<BR>abbrechen</body></html>");
		cancelSelection.setActionCommand(DCCommand.CancelGroupSelection.toString());
		cancelSelection.setEnabled(false);
		//newGroup = new JButton("<html><body style=\"text-align:center;\">Neue<BR>Gruppe</body></html>");
		//newGroup.setEnabled(false);

		saveGroup= new JButton("<html><body style=\"text-align:center;\">Gruppe<BR>speichern</body></html>");
		saveGroup.setActionCommand(DCCommand.SaveGroupSelection.toString());
		saveGroup.setEnabled(false);
		
		deleteGroup = new JButton("<html><body style=\"text-align:center;\">Gruppe<BR>löschen</body></html>");
		deleteGroup.setActionCommand("DeleteGroup");
		
		editGroup_Buttons.add(newSelection);
		editGroup_Buttons.add(cancelSelection);
		editGroup_Buttons.add(new JLabel(" "));
		//editGroup_Buttons.add(newGroup);
		editGroup_Buttons.add(saveGroup);
		editGroup_Buttons.add(deleteGroup);
		
		JPanel testPhrase_Buttons = new JPanel();
		testPhrase_Buttons.setLayout(new BoxLayout(testPhrase_Buttons, BoxLayout.Y_AXIS));
		showHelp = new JButton("<html><body style=\"text-align:center;\">Hilfe<BR>anzeigen</body></html>");
		JLabel helpField = new JLabel("<html><body><p>Der Testausdruck <br>sollte folgendermaßen<br> aufgebaut sein:</p></body></html>");
		
		testPhrase_Buttons.add(showHelp);
		//testPhrase_Buttons.add(helpField);
		
		JPanel save_Button = new JPanel();
		save= new JButton("Änderungen Speichern");
		save.setActionCommand("SaveProjectSettings");
		save_Button.add(save);

		mainPanel.add(editGroup_Buttons, BorderLayout.WEST);
		mainPanel.add(projectTable_SP, BorderLayout.CENTER);
		mainPanel.add(testPhrase_Buttons, BorderLayout.EAST);
		mainPanel.add(save_Button, BorderLayout.SOUTH);
	}
	
	private void updateTable(){
		// Tabelle wird geleert
		tableModel.setColumnCount(0);
		// Tabelle wird neu aufgebaut
		TableCellRenderer defaultRenderer = new TableCellRenderer() {
			
			public Component getTableCellRendererComponent(JTable arg0, Object arg1,
					boolean arg2, boolean arg3, int arg4, int arg5) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		if(!model.getGroupMatrix().isEmpty())
		{
			for(int i=0; i<model.getGroupMatrix().size();i++)
			{
				// Hier werden Gruppennamen vergeben Gruppe A.... Gruppe Z, Gruppe A2, ... Gruppe Z2, ...
				String groupName;
				if(i+65<91)
					groupName="Gruppe "+(char)(i+65);
				else
					groupName="Gruppe "+(char)(i%26+65)+(i-i%26)/26+1;
				tableModel.addColumn(groupName, model.getGroupMatrix().get(i));			
			}	
			
		}
		tableModel.addColumn("Codezeile", model.getCodeVector());
		tableModel.addColumn("Testausdruck", model.getTestExpressionsVector());
	}
	
	/*private void setupConfigPanel_old(){
		JPanel configPanel=new JPanel();
		
		codeList = new JList <St<html><body style=\"text-align:center;\">Selektion<BR>abbrechen</body></html>ring>(model.getCodeVector());
		codeList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		codeList.setFixedCellHeight(19);		
		
		ListSelectionModel codeListModel=codeList.getSelectionModel();
		sequenceList = new JList<Integer> (model.getSequenceVector());
		sequenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sequenceList.setFixedCellHeight(19);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		inputPanel.setSize(new Dimension(50,350));
		inputList=new ArrayList <JTextField>();
		for(int index=0; index < model.getCodeVector().size();index++){
			inputList.add(new JTextField(20));
			inputList.get(index).setSize(18, 100);
			inputPanel.add(inputList.get(index));
		}
		
		JPanel editGroup_Buttons = new JPanel();
		editGroup_Buttons.setLayout(new BoxLayout(editGroup_Buttons, BoxLayout.Y_AXIS));
		newGroup = new JButton("Neue Gruppe");
		newGroup.setActionCommand(DCCommand.StartGroupSelection.toString());
		saveGroup= new JButton("<html><body>Gruppe<BR>speichern</html></body>");
		saveGroup.setActionCommand(DCCommand.SaveGroupSelection.toString());
		cancelGroup = new JButton("<html><body>Selektion<BR>abbrechen</html></body>");
		cancelGroup.setActionCommand(DCCommand.CancelGroupSelection.toString());
		editGroup_Buttons.add(newGroup);
		editGroup_Buttons.add(saveGroup);
		editGroup_Buttons.add(cancelGroup);
		
		save = new JButton();
		// FIXME: Action Command definierenn
		save.setActionCommand("saveProjectSettings");
		
		configPanel.add(codeList);
		configPanel.add(sequenceList);
		configPanel.add(inputPanel);
		JScrollPane config_SP = new JScrollPane(configPanel);
		config_SP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		config_SP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		config_SP.setMaximumSize(new Dimension(400,500));
		config_SP.setBorder(null);
		
		mainPanel.add(config_SP, BorderLayout.CENTER);
		//mainPanel.add(editGroup_Buttons, BorderLayout.EAST);
		mainPanel.add(save, BorderLayout.SOUTH);
	}
	*/

	@Override
	public void addController(Controller controller) {
//		sequenceList.getSelectionModel().addListSelectionListener(controller);
//		codeList.getSelectionModel().addListSelectionListener(controller);
		
		newSelection.addActionListener(controller);
		saveGroup.addActionListener(controller);
		cancelSelection.addActionListener(controller);
		tableModel.addTableModelListener(controller);
		menu.addActionListener(controller);
	}

	@Override
	public void update() {
		this.updateTable();
	}

	@Override
	public void update(Observable o, Object arg) {
		update();
	}

}

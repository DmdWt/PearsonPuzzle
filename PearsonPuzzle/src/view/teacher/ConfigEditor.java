package view.teacher;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import controller.Controller;
import controller.DCCommand;
import controller.DefaultController;
import view.JView;
import model.Model;
import model.MyTableCellEditor;

public class ConfigEditor extends JView{
	
	private JList <String> codeList;
	private JList <Integer> sequenceList;
	private ArrayList <JTextField> inputList;
	
	private JButton save;
	private JButton newGroup;
	private JButton deleteGroup;
	private JButton showHelp;
	
	private JTable projectTable;
	private DefaultTableModel tableModel;
	
	
	public ConfigEditor(Model model) {
		super(model);
		menu = new MenuTeacher(1);
		this.addMenuToFrame(menu);
		setupConfigPanel();
		draw();
	}
	
	private void setupConfigPanel(){
		tableModel = new DefaultTableModel();
		projectTable = new JTable(tableModel);
		projectTable.setName("projectTable");
		updateTable();
		JScrollPane projectTable_SP = new JScrollPane(projectTable);
		projectTable_SP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		projectTable_SP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		projectTable_SP.setPreferredSize(new Dimension(650,300));
		projectTable_SP.setMaximumSize(new Dimension(650, 350));
		projectTable_SP.setBorder(null);
		
		JPanel editGroup_Buttons = new JPanel();
		editGroup_Buttons.setLayout(new BoxLayout(editGroup_Buttons, BoxLayout.Y_AXIS));
		
		newGroup = new JButton("<html><body style=\"text-align:center;\">Gruppe<BR>hinzufügen</body></html>");
		deleteGroup = new JButton("<html><body style=\"text-align:center;\">Gruppe<BR>löschen</body></html>");


//		saveGroup= new JButton("<html><body style=\"text-align:center;\">Gruppe<BR>speichern</body></html>");
//		saveGroup.setActionCommand(DCCommand.Save.toString());
//		saveGroup.setEnabled(false);
//		
				
		editGroup_Buttons.add(newGroup);
		editGroup_Buttons.add(new JLabel(" "));
		//editGroup_Buttons.add(saveGroup);
		editGroup_Buttons.add(deleteGroup);
		
		JPanel testPhrase_Buttons = new JPanel();
		testPhrase_Buttons.setLayout(new BoxLayout(testPhrase_Buttons, BoxLayout.Y_AXIS));
		showHelp = new JButton("<html><body style=\"text-align:center;\">Hilfe<BR>anzeigen</body></html>");
		JLabel helpField = new JLabel("<html><body><p>Der Testausdruck <br>sollte folgendermaßen<br> aufgebaut sein:</p></body></html>");
		editGroup_Buttons.add(new JLabel(" "));
		editGroup_Buttons.add(showHelp);
		//testPhrase_Buttons.add(showHelp);
		//testPhrase_Buttons.add(helpField);
		
		JPanel save_Button = new JPanel();
		save= new JButton("Änderungen Speichern");
		save_Button.add(save);

		mainPanel.add(editGroup_Buttons, BorderLayout.WEST);
		mainPanel.add(projectTable_SP, BorderLayout.CENTER);
		//mainPanel.add(testPhrase_Buttons, BorderLayout.EAST);
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
		
		if(model.getGroupMatrix()!=null && !model.getGroupMatrix().isEmpty())
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

	@Override
	public void addController(Controller controller) {
		menu.addActionListener(controller);
//		sequenceList.getSelectionModel().addListSelectionListener(controller);
//		codeList.getSelectionModel().addListSelectionListener(controller);	
		
		newGroup.addActionListener(controller);
		newGroup.setActionCommand(DCCommand.AddOrder.toString());
		deleteGroup.addActionListener(controller);
		deleteGroup.setActionCommand(DCCommand.DeleteOrder.toString());
		showHelp.addActionListener(controller);
		showHelp.setActionCommand(DCCommand.ShowHelp.toString());
		
		projectTable.addFocusListener((DefaultController)controller);
		//projectTable.setCellEditor(new MyTableCellEditor());
		tableModel.addTableModelListener((DefaultController)controller);
		
		save.addActionListener(controller);
		save.setActionCommand(DCCommand.Save.toString());
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

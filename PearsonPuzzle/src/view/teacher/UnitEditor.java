package view.teacher;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Observable;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;


import org.junit.runner.notification.Failure;

import model.Model;

import controller.Controller;
import controller.DCCommand;
import controller.DefaultController;
import view.JView;

public class UnitEditor extends JView{
	
	public final static String DEFAULT_IMPORT_TEXT="Hier können Imports <b>für die zu testende Klasse</b> in der typischen Form \"import paketname;\" angegeben werden. Zusätzliche nötige Methoden und Klassen bitte über den Button unterhalb angeben.";
	
	public static String DEFAULT_UNIT_CODE;
	private JTextArea textArea;
	//private JEditTextArea textArea; 
	private JTextArea messageBox;
	private JTextArea imports;
	private JButton addClasses;
	private JButton addMethods;
	private JButton showHelp;
	private JButton save;
	private JButton test;
	private JButton compile;

	public UnitEditor(Model model) {
		super(model);
		DEFAULT_UNIT_CODE = "// Automatisch erstellte Vorlage für JUnit Test\n \nimport org.junit.Test; \nimport static org.junit.Assert.*;\n\npublic class "+model.getProjectName()+"_Test{\n\t@Test\n\t"+"public void testMethode1(){ \n"+"\t\tassertTrue(true);\n\t}\n"+"}";
		menu = new MenuTeacher(model, 2);
		this.addMenuToFrame(menu);
		setupEditor();
		mainPanel.revalidate();
	}

	private void setupEditor() {
//		textArea = new JEditTextArea();
//		textArea.setElectricScroll(10);
//		textArea.setPreferredSize(new Dimension(450,350));
//		textArea.setText(model.getJUnitCode());
//		textArea.setTokenMarker(new JavaTokenMarker());		
//		textArea.validate();
		
		textArea = new JTextArea();
		textArea.setEditable(true);
		textArea.setLineWrap(false);
		textArea.setTabSize(3);
		textArea.setText(model.getJUnitCode());
		
		Border border = BorderFactory.createEmptyBorder();
		textArea.setBorder(BorderFactory.createCompoundBorder(border, 
	            BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		if(textArea.getText().trim().isEmpty())
			textArea.setText(DEFAULT_UNIT_CODE);
		JScrollPane textScrollPane = new JScrollPane(textArea);
		textScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textScrollPane.setPreferredSize(new Dimension(400,350));
		textScrollPane.setMinimumSize(new Dimension(400,100));
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		messageBox= new JTextArea("Hier erfolgt die Ergebnisausgabe");
		messageBox.setEditable(false);
		messageBox.setLineWrap(false);
		messageBox.setName("MessageBox");
		messageBox.setTabSize(3);
		messageBox.setBorder(BorderFactory.createCompoundBorder(border, 
	            BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		JScrollPane messageSP = new JScrollPane(messageBox);
		messageSP.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		messageSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageSP.setPreferredSize(new Dimension(200,100));
		messageSP.setMinimumSize(new Dimension(200,100));
	
		String importText = model.getImport("online");
		if(importText== null|| importText.isEmpty())
			imports = new JTextArea(DEFAULT_IMPORT_TEXT);
		else 
			imports = new JTextArea(importText);
		imports.setLineWrap(true);
		imports.setWrapStyleWord(true);
		imports.setBorder(BorderFactory.createCompoundBorder(border, 
	            BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		JScrollPane importsSP = new JScrollPane(imports);
		importsSP.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		importsSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		importsSP.setPreferredSize(new Dimension(200,150));
		importsSP.setMinimumSize(new Dimension(200,150));
		
		if(model.getImport("classes")==null || model.getImport("classes").isEmpty())
			addClasses = new JButton("<html><body>Zum Ausführen nötige <b>Klassen hinzufügen</b></body></html>");
		else 
			addClasses = new JButton("<html><body>Zum Ausführen nötige <b>Klassen bearbeiten</b></body></html>");
		addClasses.setAlignmentX(Component.CENTER_ALIGNMENT);
		addClasses.setIcon(new ImageIcon("rsc/icon/file/class.png"));
		
		if(model.getImport("methods") == null || model.getImport("methods").isEmpty())
			addMethods = new JButton("<html><body>Zum Ausführen nötige <b>Methoden hinzufügen</b></body></html>");
		else 
			addMethods = new JButton("<html><body>Zum Ausführen nötige <b>Methoden bearbeiten</b></body></html>");
		addMethods.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftPanel.add(importsSP);
		leftPanel.add(addClasses);
		leftPanel.add(addMethods);
		leftPanel.add(messageSP);
		
		JPanel buttonPanel = new JPanel();
		compile = new JButton("Test kompilieren");
		compile.setIcon(new ImageIcon("rsc/icon/file/compute.png"));
		save = new JButton("Speichern");
		save.setIcon(saveIcon);
		test = new JButton("Run Test");
		test.setIcon(new ImageIcon("rsc/icon/file/circle.png"));
		test.setEnabled(false);
		
		showHelp = new JButton("<html><body style=\"text-align:center;\">Hilfe</body></html>");
		showHelp.setIcon(new ImageIcon("rsc/icon/file/help_small.png"));
		buttonPanel.add(compile);
		buttonPanel.add(test);
		buttonPanel.add(save);
		buttonPanel.add(showHelp);
		
//		mainPanel.add(textScrollPane);
		mainPanel.add(textScrollPane);
		mainPanel.add(leftPanel, BorderLayout.EAST);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
	public void addController(Controller controller) {
		menu.addActionListener(controller);
		
		textArea.setName("JUnitCode");
		textArea.addFocusListener((DefaultController)controller);
		imports.setName("Imports");
		imports.addFocusListener((DefaultController) controller);
		
		addClasses.setActionCommand(DCCommand.AddClasses.toString());
		addClasses.addActionListener(controller);
		addMethods.setActionCommand(DCCommand.AddMethods.toString());
		addMethods.addActionListener(controller);
		compile.setActionCommand(DCCommand.Compile.toString());
		compile.addActionListener(controller);
		save.setActionCommand(DCCommand.Save.toString());
		save.addActionListener(controller);
		test.setActionCommand(DCCommand.TestCode.toString());
		test.addActionListener(controller);
		showHelp.addActionListener(controller);
		showHelp.setActionCommand(DCCommand.ShowHelp.toString());
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Observable o, Object arg) {
		if(arg==DCCommand.Compile){
			Vector<HashMap<String, String>> failures = model.getCompileFailures();
			if(failures.isEmpty()){
				messageBox.setText("Kompilieren war erfolgreich!");
				compile.setBackground(GREEN);
				compile.setForeground(WHITE);
				test.setEnabled(true);
			}
			else{
				String failureText = "Kompilieren war nicht erfolgreich. \nAufgetretenen Fehler: ";
				for(HashMap<String, String> failure : failures){
					failureText = failureText+"\n  "+failure.get("Art")+" "+failure.get("Nachricht")+" in Zeile "+failure.get("Zeile")+" von "+failure.get("Klasse");
				}
				compile.setBackground(RED);
				compile.setForeground(WHITE);
				messageBox.setText(failureText);
			}
		}
		else if(arg==DCCommand.TestCode){
			// FIXME: Nur, falls ein Test existeiert! Zusätzlich noch Reihenfolgentest.
			String failureText = new String("Ergebnis des Unit-Test: "+model.getjUnitFailures().size()+" Fehler");
			for(Failure failure: model.getjUnitFailures()){
				failureText=failureText+"\n\t"+failure.getTestHeader();
			}
			if(model.getjUnitFailures().size()==0){
				test.setBackground(GREEN);
				test.setForeground(WHITE);
			}
			else{
				test.setBackground(RED);
				test.setForeground(WHITE);
			}
			messageBox.setText(failureText);
		}
		// TODO Auto-generated method stub
		
	}

	public String getContent() {
		return textArea.getText();
	}
	public String getImport(){
		return imports.getText();
	}

}

package view.teacher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Observable;
import java.util.Vector;

import javax.swing.*;

import org.junit.runner.notification.Failure;

import model.Model;

import controller.Controller;
import controller.DCCommand;
import controller.DefaultController;
import view.JView;

public class UnitEditor extends JView{
	
	private JTextArea textArea;
	private JTextArea messageBox;
	private JButton save;
	private JButton test;
	private JButton compile;

	public UnitEditor(Model model) {
		super(model);
		menu = new MenuTeacher(2);
		this.addMenuToFrame(menu);
		setupEditor();
		draw();
	}

	private void setupEditor() {
		textArea = new JTextArea();
		textArea.setEditable(true);
		textArea.setLineWrap(false);
		textArea.setTabSize(3);
		textArea.setText(model.getJUnitCode());
		if(textArea.getText().equals(""))
			textArea.setText("// Automatisch erstellte Vorlage für JUnit Test\n \nimport org.junit.Test; \nimport static org.junit.Assert.*;\n\npublic class "+model.getProjectName()+"_Test{\n\t@Test\n\t"+"public void testMethode1(){ \n"+"\t\tassertTrue(true);\n\t}\n"+"}");
		JScrollPane textScrollPane = new JScrollPane(textArea);
		textScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textScrollPane.setPreferredSize(new Dimension(400,350));
		textScrollPane.setMinimumSize(new Dimension(400,100));
		
		messageBox= new JTextArea("Hier erfolgt die Ergebnisausgabe");
		messageBox.setEditable(false);
		messageBox.setLineWrap(false);
		messageBox.setName("MessageBox");
		JScrollPane messageScrollPane = new JScrollPane(messageBox);
		messageScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(200,150));
		messageScrollPane.setMinimumSize(new Dimension(200,150));
		
		
		JPanel buttonPanel = new JPanel();
		compile = new JButton("Test Kompilieren");
		save = new JButton("Speichern");
		test = new JButton("Run Test");
		buttonPanel.add(compile);
		buttonPanel.add(test);
		buttonPanel.add(save);
		
		mainPanel.add(textScrollPane);
		mainPanel.add(messageScrollPane, BorderLayout.EAST);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
	public void addController(Controller controller) {
		menu.addActionListener(controller);
		
		textArea.setName("JUnitCode");
		textArea.addFocusListener((DefaultController)controller);
		
		compile.setActionCommand(DCCommand.Compile.toString());
		compile.addActionListener(controller);
		save.setActionCommand(DCCommand.Save.toString());
		save.addActionListener(controller);
		test.setActionCommand(DCCommand.TestCode.toString());
		test.addActionListener(controller);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Observable o, Object arg) {
		if(arg==DCCommand.Compile){
			Vector<HashMap<String, String>> failures = model.getCompileFailures();
			if(failures.isEmpty())
				messageBox.setText("Kompilieren war erfolgreich!");
			else{
				String failureText = "Kompilieren war nicht erfolgreich. \nAufgetretenen Fehler: ";
				for(HashMap<String, String> failure : failures){
					failureText = failureText+"\n "+failure.get("Nachricht")+" in Zeile "+failure.get("Zeile");
				}
				messageBox.setText(failureText);
			}
		}
		else if(arg==DCCommand.TestCode){
			// FIXME: Nur, falls ein Test existeiert! Zusätzlich noch Reihenfolgentest.
			String failureText = new String("Ergebnis des Unit-Test:\n"+model.getjUnitFailures().size()+" Fehler");
			for(Failure failure: model.getjUnitFailures()){
				failureText=failureText+"\n"+failure;
			}
			messageBox.setText(failureText);
		}
		// TODO Auto-generated method stub
		
	}

	public String getContent() {
		return textArea.getText();
	}

}

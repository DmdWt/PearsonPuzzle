package view.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.syntax.jedit.JEditTextArea;
import org.syntax.jedit.tokenmarker.JavaTokenMarker;

import model.Model;

import controller.DialogController;

public class AddImportDialog extends JDialog{

	private static final long serialVersionUID = -3755040607779146557L;
	private JOptionPane optionPane;
	private Model model;
	private JEditTextArea textArea;
	//private JTextArea input;

	public AddImportDialog(Frame frame, Model model, String title) {
		super(frame, model, title);
		this.model = model;
    	setupContentPane();
    	setContentPane(optionPane);
	}
	public AddImportDialog(JDialog dialog, Model model, String title) {
		super(dialog, model, title);
		this.model = model;
    	setupContentPane();
    	setContentPane(optionPane);
	}

	
	private void setupContentPane() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		textArea = new JEditTextArea();
		textArea.setElectricScroll(10);
		textArea.setPreferredSize(new Dimension(450,350));
		//textArea.setText(model.getJUnitCode());
		textArea.setTokenMarker(new JavaTokenMarker());		
		textArea.validate();
		
//		input = new JTextArea();
//		Border border = BorderFactory.createEmptyBorder();
//		input.setBorder(BorderFactory.createCompoundBorder(border, 
//	            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
//		input.setLineWrap(true);
//		JScrollPane inputSP = new JScrollPane(input);
//		inputSP.setVerticalScrollBarPolicy(
//		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		inputSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		inputSP.setPreferredSize(new Dimension(300,100));
//		inputSP.setMinimumSize(new Dimension(300,100));	
//		
		JLabel instruction = null;
		if(getTitle().equals("Nötige Klassen")){
			instruction = new JLabel("<html>Geben Sie hier alle <B>Klassen</B> in Textform ein, die für das Ausführen des Programms benötigt werden.</html>");
			//input.setText(model.getImport("classes"));
			textArea.setText(model.getImport("classes"));
		}
		else if(getTitle().equals("Nötige Methoden")){
			instruction = new JLabel("<html>Geben Sie hier <B>Methoden</B> in Textform ein, die für das Ausführen des Programms benötigt werden.</html>");
			//input.setText(model.getImport("methods"));
			textArea.setText(model.getImport("methods"));
		}
		else if(getTitle().equals("Nötige Imports")){
			instruction = new JLabel("<html>Geben Sie hier <B>Imports</B> ein, die für das Ausführen des Programms benötigt werden.</html>");
			//input.setText(model.getImport("online"));
			textArea.setText(model.getImport("online"));
		}
		instruction.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		contentPanel.add(instruction);
		contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
		contentPanel.add(textArea);
		optionPane = new JOptionPane(contentPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		optionPane.setSize(new Dimension(350,200));
	}


	@Override
	public void addController(DialogController controller) {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
            /*
             * Instead of directly closing the window,
             * we're going to change the JOptionPane's
             * value property.
             */
                optionPane.setValue(new Integer(
                                    JOptionPane.CLOSED_OPTION));
            }
		});
		// Sichert ab, dass Access Group Feld immer als erstes den Focus bekommt.
		optionPane.addPropertyChangeListener(controller);		
	}

	@Override
	public JOptionPane getOptionPane() {
		return optionPane;
	}

	@Override
	public void clearAndHide() {
		clear();
	}

	@Override
	public Object get(String string) {
		return textArea.getText();
	}

}

package view.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import model.Model;

import controller.DialogController;

public class AddImportDialog extends JDialog{

	private static final long serialVersionUID = -3755040607779146557L;
	private JOptionPane optionPane;
	private Model model;
	private JTextArea input;

	public AddImportDialog(Frame frame, Model model, String title) {
		super(frame, model, title);
		this.model = model;
    	setupContentPane();
    	setContentPane(optionPane);
	}

	
	private void setupContentPane() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		input = new JTextArea();
		Border border = BorderFactory.createEmptyBorder();
		input.setBorder(BorderFactory.createCompoundBorder(border, 
	            BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		
		JLabel instruction = null;
		if(getTitle().equals("Nötige Klassen")){
			instruction = new JLabel("<html>Geben Sie hier alle <B>Klassen</B> in Textform ein, die für das Ausführen des Programms benötigt werden.</html>");
			input.setText(model.getImport("classes"));
		}
		else if(getTitle().equals("Nötige Methoden")){
			instruction = new JLabel("<html>Geben Sie hier <B>Methoden</B> in Textform ein, die für das Ausführen des Programms benötigt werden.</html>");
			input.setText(model.getImport("methods"));
		}
		else if(getTitle().equals("Nötige Imports")){
			instruction = new JLabel("<html>Geben Sie hier <B>Imports</B> ein, die für das Ausführen des Programms benötigt werden.</html>");
			input.setText(model.getImport("online"));
		}
		instruction.setAlignmentX(Component.CENTER_ALIGNMENT);
		input.setLineWrap(true);
		JScrollPane inputSP = new JScrollPane(input);
		inputSP.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		inputSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		inputSP.setPreferredSize(new Dimension(300,100));
		inputSP.setMinimumSize(new Dimension(300,100));	
		
		contentPanel.add(instruction);
		contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
		contentPanel.add(inputSP);
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
	public void clear() {
		setVisible(false);			
	}

	@Override
	public Object get(String string) {
		return input.getText();
	}

}

package view.dialog;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.Model;

import controller.DCCommand;
import controller.DialogController;

public class EditOrderDialog extends JDialog implements Observer{
	
	private static final long serialVersionUID = 6546427675009283113L;
	public final String DEFAULT_CONTENT = "Dieser Text wird angezeigt, wenn die gewählte Gruppe getestet wird.\n" +
			"Ist der Test erfolgreich, wird folgendes ausgegeben: \t <eingegebener Text>: Ja\n" +
			"Ist der Test nicht erfolgreich, wird folgendes ausgegeben: \t <eingegebener Text>: Nein";
	private Model model;
	private JOptionPane optionPane;
	private JComboBox<String> orderGroup;
	private JPanel messagePanel;
	private JTextArea textPanel;
	public final String[] yesNoCancelOptions = { "Speichern", "Fertig", "Abbrechen" };

	public EditOrderDialog(Frame frame, Model model, String title) {
		super(frame, model, title);
		this.model = model;
    	setupContentPane();
    	setContentPane(optionPane);
    	setLocationRelativeTo(null);
	}

	private void setupContentPane() {
		JPanel contentPanel = new JPanel();
    	JPanel dialogPanel = new JPanel();
		orderGroup = new JComboBox<String>();
		String groupName;
		
		for(int i=0; i< model.getGroupMatrix().size();i++){
			if(i+65<91)
				groupName="Gruppe "+(char)(i+65);
			else
				groupName="Gruppe "+(char)(i%26+65)+(i-i%26)/26+1;
			orderGroup.addItem(groupName);
		}
		
		contentPanel.add(orderGroup);		
		messagePanel = new JPanel();
		messagePanel.add(new JLabel());
		
		
		dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
		dialogPanel.setAlignmentX(CENTER_ALIGNMENT);
		dialogPanel.add(new JLabel("Geben Sie die Gruppe an, die Sie bearbeiten möchten wollen"));
		dialogPanel.add(contentPanel);
		
		textPanel = new JTextArea();
		String failureText = model.getOrderFailures(orderGroup.getSelectedIndex());
		if(failureText==null || failureText.isEmpty()){
			textPanel.setText(DEFAULT_CONTENT);
		}
		else 
			textPanel.setText(failureText);
		textPanel.setLineWrap(true);
		textPanel.setWrapStyleWord(true);
		JScrollPane textSP = new JScrollPane(textPanel);
		textSP.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textSP.setPreferredSize(new Dimension(300,100));
		textSP.setMinimumSize(new Dimension(300,100));	
		dialogPanel.add(textSP);
		dialogPanel.add(messagePanel);
		dialogPanel.setVisible(true);
		if(model.getGroupMatrix().size()==0){
			optionPane = new JOptionPane("Es sind keine Gruppen vorhanden, die man löschen könnte", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
		}
		else{
			optionPane = new JOptionPane(dialogPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
		}
	}

	@Override
	public void addController(DialogController controller) {
		// Es wird festgelegt, was beim schließen passiert 
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
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				orderGroup.requestFocusInWindow();
			}
		});
		optionPane.addPropertyChangeListener(controller);
		
		// Keine Interaktion mit dem Model, deshalb hier definiert (um DialogController schlank zu halten)
		textPanel.addFocusListener(new FocusListener() {	
			@Override
			public void focusLost(FocusEvent e) {}
			@Override
			public void focusGained(FocusEvent e) {
				
			}
		});	
		textPanel.addFocusListener(controller);
		orderGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
			}
		});
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
		if(string.equals("group"))
			return orderGroup.getSelectedItem();
		else if(string.equals("groupID"))
			return orderGroup.getSelectedIndex();
		else if(string.equals("text"))
			return textPanel.getText();
		else 
			return null;
	}
	
	private void update(){
		String failureText = model.getOrderFailures(orderGroup.getSelectedIndex());
		if(failureText==null || failureText.isEmpty()){
			textPanel.setText(DEFAULT_CONTENT);
		}
		else 
			textPanel.setText(failureText);
	}
	
	@Override
	public void update(Observable obs, Object arg) {
		String message;
		if(arg!=null){
			if(arg.equals(DCCommand.EditOrderGroup))
				message = "Änderungen wurden übernommen.";
			else 
				message = "";
		}
		else
			message = "";
		((JLabel)messagePanel.getComponent(0)).setText(message);
			this.pack();
	}
}

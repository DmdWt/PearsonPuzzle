package view.dialog;


import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.Model;
import controller.DCCommand;
import controller.DialogController;

public class DeleteOrderDialog extends JDialog implements Observer{
	private static final long serialVersionUID = -7269510734285263261L;
	private JOptionPane optionPane;
	private JComboBox<String> orderGroup;
	private JPanel messagePanel;
	private Model model;

	public DeleteOrderDialog(Frame frame, Model model, String title){
    	super(frame, model, title);
    	this.model = model;
    	setupContentPane();
    	setContentPane(optionPane);
    	setLocationRelativeTo(null);
	}
	
	public void addController(DialogController controller){
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
	}
    
    private void setupContentPane(){
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
		dialogPanel.add(new JLabel("Geben Sie die Gruppe an, die Sie löschen wollen"));
		dialogPanel.add(contentPanel);
		dialogPanel.add(messagePanel);
		dialogPanel.setVisible(true);
		if(model.getGroupMatrix().size()==0){
			optionPane = new JOptionPane("Es sind keine Gruppen vorhanden, die man löschen könnte", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE);
		}
		else{
			optionPane = new JOptionPane(dialogPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		}
    }

    /**
     * Gemachte Eingaben werden gelöscht und Dialog wird versteckt.
     */
    public void clearAndHide() {
    	clear();
    }

	@Override
	public void update(Observable obs, Object arg) {
		String message;
		if(arg.equals(DCCommand.DeleteOrder))
			message = "Gruppe wurde gelöscht.";
		else
			message = "";
		((JLabel)messagePanel.getComponent(0)).setText(message);
		this.pack();
	}

	@Override
	public void clear() {
		setVisible(false);	
	}

	@Override
	public Object get(String string) {
		if(string.equals("groupNumber"))
			return orderGroup.getSelectedIndex();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JOptionPane getOptionPane() {
		return optionPane;
	}
}

package view.dialog;

import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.DialogController;

import model.Model;

public class NoDatabaseDialog extends JDialog{

	private static final long serialVersionUID = -5684931694324513461L;
	private Model model;
	private JOptionPane optionPane;

	public NoDatabaseDialog (Frame frame, Model model, String title){
    	super(frame, model, title);
    	this.model = model;
    	setupContentPane();
    	setContentPane(optionPane);
    	setLocationRelativeTo(null);
}

	private void setupContentPane() {
		JPanel dialogPanel = new JPanel();
		Object[] options = {"Datensatz Laden", "Datensatz anlegen"};
		optionPane = new JOptionPane(dialogPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
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
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				//accessGroup.requestFocusInWindow();
			}
		});
		optionPane.addPropertyChangeListener(controller);	}

	
	@Override
	public Object get(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JOptionPane getOptionPane() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAndHide() {
		// TODO Auto-generated method stub
		
	}
}

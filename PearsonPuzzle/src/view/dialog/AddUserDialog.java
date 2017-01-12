package view.dialog;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import view.PPException;

import controller.DCCommand;
import controller.DialogController;

import model.Model;
import model.access.AccessGroup;

import java.util.Observable;
import java.util.Observer;
import java.awt.*;
import java.awt.event.*;


public class AddUserDialog extends JDialog
                   implements Observer {

	private static final long serialVersionUID = -7269510734285263261L;
	private JOptionPane optionPane;
	private JTextField userName;
	private JComboBox<AccessGroup> accessGroup;
	private JPasswordField password;
	private JPanel messagePanel;
	private Model model;

	public AddUserDialog(Frame frame, Model model, String title){
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
				accessGroup.requestFocusInWindow();
			}
		});
		optionPane.addPropertyChangeListener(controller);
	}
	
	public JOptionPane getOptionPane(){
		return optionPane;
	}
	
	/**
	 * Password, username oder accessgroup können angefordert werden.
	 * @param variableToReturn {username, password, accessgroup}
	 * @return Angeforderte Variable
	 */
    public Object get(String variableToReturn){
    	if(variableToReturn.equals("password")){
			return password.getPassword();
		}
		else if(variableToReturn.equals("username")){
			return userName.getText();
		}
		else if(variableToReturn.equals("accessgroup")){
			return accessGroup.getSelectedItem();
		}
		return null;
    }
    
    private void setupContentPane(){
    	JPanel contentPanel = new JPanel(new GridLayout(0,2));		
		userName = new JTextField(15);
		userName.setSize(new Dimension(100,10));
		userName.setAlignmentY(Component.TOP_ALIGNMENT);
		if(this.getTitle().equals("Ersten Nutzer anlegen"))
			userName.setText(model.getUsername());
		password = new JPasswordField(15);

		accessGroup = new JComboBox<AccessGroup>();
		accessGroup.addItem(null);
		AccessGroup[] possibleAccessGroups = AccessGroup.values();
		if(this.getTitle().equals("Ersten Nutzer anlegen"))
			accessGroup.addItem(AccessGroup.TEACHER);
		else
			for(int i=0; i<possibleAccessGroups.length;i++){
				accessGroup.addItem(possibleAccessGroups[i]);
			}
		
		contentPanel.add(new JLabel("Nutzergruppe"));
		contentPanel.add(accessGroup);
		contentPanel.add(new JLabel("Nutzername"));
		contentPanel.add(userName);
		contentPanel.add(new JLabel("Passwort"));
		contentPanel.add(password);
		
		messagePanel = new JPanel();
		messagePanel.add(new JLabel());
		
		JPanel dialogPanel = new JPanel();
		dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
		dialogPanel.add(contentPanel);
		dialogPanel.add(Box.createVerticalGlue());
		dialogPanel.add(messagePanel);
		dialogPanel.setVisible(true);
        
		optionPane = new JOptionPane(dialogPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
    }

    /**
     * Gemachte Eingaben werden gelöscht und Dialog wird versteckt.
     */
    public void clearAndHide() {
    	password.setText(null);
    	userName.setText(null);
        setVisible(false);
    }
    public void clear(){
    	password.setText(null);
    	userName.setText(null);
    }

	@Override
	public void update(Observable obs, Object arg) {
		String message;
		if(arg==null){
			message = " ";
		}
		else if(arg==DCCommand.Save)
			message = "Nutzer wurde gespeichert";
		else if(arg.getClass()==PPException.class)
			message = ((PPException) arg).getMessage();
		else if(arg.equals("username_unset"))
			message = "Bitte geben Sie einen Nutzernamen an";
		else if (arg.equals("password_unset"))
			message = "Bitte geben Sie ein Passwort ein";
		else if (arg.equals("accessgroup_unset"))
			message = "Bitte geben Sie eine Nutzergruppe an";
		else if(arg.equals("username_toShort"))
			message = "Nutzername ist zu kurz.";
		else if(arg.equals("password_toShort"))
			message = "<html>Das angegebene Passwort ist zu kurz, <br>bitte wählen Sie ein anderes.</html>";
		else if(arg.equals("password_unsave"))
			message = "<html>Das angegebene Passwort ist unsicher. <br> Bitte verwenden Sie mindestens einen Großbuchstaben, eine Zahl und ein Sonderzeichen.</html>";
		else 
			message = " ";
		((JLabel)messagePanel.getComponent(0)).setText(message);
		this.pack();
	}
}

package view.dialog;

import java.awt.BorderLayout;
import java.util.Observable;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.Model;
import model.access.AccessGroup;

import controller.Controller;
import view.JView;

public class InitializeAccess extends JView{
	
	//private JPanel contentPanel;
	//private JDialog dialog;
	private JPanel dialogPanel;
	//private JButton moreUsers;
	private JPanel messagePanel;
	private JTextField userName;
	private JPasswordField password;
	private JComboBox<AccessGroup> accessGroup;

	public InitializeAccess(Model model) {
		super(model);
		setupFrame();
		setupDialogPanel();
		//showDialog(PPException.noDatabaseExists,true);
	}
//	private void setupDialog(){
//		dialog = new JDialog();
//		dialog.setModal(true);
//		dialog.setLayout(new FlowLayout());
//		dialog.setTitle("Nutzer Anlegen");
//		dialog.add(dialogPanel);
//	}
	
	private void setupDialogPanel(){
		dialogPanel = new JPanel(new BorderLayout());
		
//		submit = new JButton("Nutzer anlegend");
//		submit.setActionCommand(DCCommand.Save.toString());
//		submit.setName("Nutzer erstellen");
//		
		JPanel leftPanel = new JPanel ();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		JPanel midPanel = new JPanel ();
		midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
		JPanel topPanel = new JPanel ();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		
		
		userName = new JTextField(15);
		password = new JPasswordField(15);

		accessGroup = new JComboBox<AccessGroup>();
		accessGroup.addItem(null);
		AccessGroup[] possibleAccessGroups = AccessGroup.values();
		for(int i=0; i<possibleAccessGroups.length;i++){
			accessGroup.addItem(possibleAccessGroups[i]);
		}
		leftPanel.add(new JLabel("Nutzername"));
		leftPanel.add(userName);
		midPanel.add(new JLabel("Passwort"));
		midPanel.add(password);
		topPanel.add(new JLabel("Nutzergruppe"));
		topPanel.add(accessGroup);
		
		messagePanel = new JPanel();
		
		dialogPanel.add(leftPanel, BorderLayout.WEST);
		dialogPanel.add(midPanel, BorderLayout.CENTER);
		dialogPanel.add(topPanel, BorderLayout.NORTH);
		dialogPanel.add(messagePanel, BorderLayout.SOUTH);
		dialogPanel.setVisible(true);
	}
		
		
//
//		JOptionPane optionPane = new JOptionPane(
//		  public CustomDialog(Frame aFrame, String aWord, DialogDemo parent) {
//		        super(aFrame, true);
//		        dd = parent;
//
//		        magicWord = aWord.toUpperCase();
//		        setTitle("Quiz");
//
//		        textField = new JTextField(10);
//
//		        //Create an array of the text and components to be displayed.
//		        String msgString1 = "What was Dr. SEUSS's real last name?";
//		        String msgString2 = "(The answer is \"" + magicWord
//		                              + "\".)";
//		        Object[] array = {msgString1, msgString2, textField};
//
//		        //Create an array specifying the number of dialog buttons
//		        //and their text.
//		        Object[] options = {btnString1, btnString2};
//
//		        //Create the JOptionPane.
//		        optionPane = new JOptionPane(array,
//		                                    JOptionPane.QUESTION_MESSAGE,
//		                                    JOptionPane.YES_NO_OPTION,
//		                                    null,
//		                                    options,
//		                                    options[0]);
//
//		        //Make this dialog display it.
//		        setContentPane(optionPane);
//
//		        //Handle window closing correctly.
//		        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//		        addWindowListener(new WindowAdapter() {
//		                public void windowClosing(WindowEvent we) {
//		                /*
//		                 * Instead of directly closing the window,
//		                 * we're going to change the JOptionPane's
//		                 * value property.
//		                 */
//		                    optionPane.setValue(new Integer(
//		                                        JOptionPane.CLOSED_OPTION));
//		            }
//		        });
//
//		        //Ensure the text field always gets the first focus.
//		        addComponentListener(new ComponentAdapter() {
//		            public void componentShown(ComponentEvent ce) {
//		                textField.requestFocusInWindow();
//		            }
//		        });
//
//		        //Register an event handler that puts the text into the option pane.
//		        textField.addActionListener(this);
//
//		        //Register an event handler that reacts to option pane state changes.
//		        optionPane.addPropertyChangeListener(this);
//		    }


	@Override
	public void addController(Controller controller) {
				
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Observable o, Object arg) {
		messagePanel.removeAll(); 
		if(arg.equals("username_unset"))
			messagePanel.add(new JLabel("Bitte geben Sie einen Nutzernamen an"));
		else if (arg.equals("password_unset"))
			messagePanel.add(new JLabel("Bitte geben Sie ein Passwort ein"));
		else if (arg.equals("accessgroup_unset"))
			messagePanel.add(new JLabel("Bitte geben Sie eine Nutzergruppe an"));
		
		if(arg.equals("username_toShort"))
			messagePanel.add(new JLabel("Nutzername ist zu kurz."));
		else if(arg.equals("password_toShort"))
			messagePanel.add(new JLabel("<html>Das angegebene Passwort ist zu kurz, <br>bitte wählen Sie ein anderes.</html>"));
		
		if(arg.equals("password_unsave"))
			messagePanel.add(new JLabel("<html>Das angegebene Passwort ist unsicher. <br> Bitte verwenden Sie mindestens einen Großbuchstaben, eine Zahl und ein Sonderzeichen.</html>"));
		
	}
	
	public Object get(String variable){
		if(variable.equals("password")){
			return password.getPassword();
		}
		else if(variable.equals("username")){
			return userName.getText();
		}
		else if(variable.equals("accessgroup")){
			return accessGroup.getSelectedItem();
		}
		return null;
	}

}

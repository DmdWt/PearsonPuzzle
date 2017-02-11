package view.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.Model;

import controller.DCCommand;
import controller.DialogController;

public class CompileDialog extends JDialog implements Observer{
	private static final long serialVersionUID = -1766879916483775420L;

	private static final String DEFAULT_MESSAGE = "<html><body style=\"width:600px; text-align: center;\"><p style=\"text-align:justify;\">"+
			"Hier können:" +
			"<ul style=\"text-align:left;\">" +
			//"<li>zusätzlich üben das Textfeld links oben noch etwaige nötige Imports angegeben werden. </li>" +
			"<li>Puzzeltexte kompiliert werden.</li>"+
			"<li>Zusätzliche spezielle oder eigene Klasse(n) angegeben werden, falls diese nötig sind, um den Puzzlecode auszuführen/zu kompilieren. Diese sollten in Textform über den entsprechenden Button angehängt werden.</li>" +
			"<li>Zusätzliche Methode(n) angegeben werden, falls diese nötig sind, um den Puzzlecode ausführen/compilieren zu können. Diese werden dann dem Puzzlecode angehängt, sind aber im Puzzlemodus nicht sichtbar.</li>" +
			"</ul></body></html>";
	private Model model;
	private JOptionPane optionPane;
	private JLabel messageBox;
	private JButton compile;
	private JButton addClass;
	private JButton addMethod;
	
	
	
	public CompileDialog(Frame frame, Model model, String title) {
		super(frame, model, title);
		this.model = model;
    	setupContentPane();
    	setContentPane(optionPane);
    	model.addObserver(this);
	}

	
	private void setupContentPane() {
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		messageBox = new JLabel();
		messageBox.setAlignmentY(TOP_ALIGNMENT);
		messageBox.setText(DEFAULT_MESSAGE);

		JScrollPane messageSP = new JScrollPane(messageBox);
		messageSP.setPreferredSize(new Dimension(600,350));
		messageSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		messageSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JPanel buttonPanel = new JPanel();
		compile = new JButton("Kompilieren");
		addClass = new JButton("Zum Ausführen notwendige Klasse(n)");
		addMethod = new JButton("Zum Ausführen notwendige Methode(n)");
		
		buttonPanel.add(compile);
		buttonPanel.add(addClass);
		buttonPanel.add(addMethod);
		
		contentPanel.add(messageSP);
		contentPanel.add(buttonPanel);
		optionPane = new JOptionPane(contentPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE);	
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
		
		compile.addActionListener(controller);
		compile.setActionCommand(DCCommand.Compile.toString());
		compile.setIcon(new ImageIcon("rsc/icon/file/compute.png"));
		addClass.addActionListener(controller);
		addClass.setActionCommand(DCCommand.AddClasses.toString());
		addClass.setIcon(new ImageIcon("rsc/icon/file/class.png"));
		addMethod.addActionListener(controller);
		addMethod.setActionCommand(DCCommand.AddMethods.toString());
	}

	@Override
	public JOptionPane getOptionPane() {
		return optionPane;
	}

	@Override
	public void clearAndHide() {
		model.deleteObserver(this);
		clear();
	}

	@Override
	public Object get(String string) {
		return null;
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		super.update(arg0, arg1);
		updateMessageBox();
		messageBox.revalidate();
	}
	
	private void updateMessageBox(){
		StringBuffer messageBuffer = new StringBuffer();
		messageBuffer.append("<html><body><p>Ergebnis des Kompiliervorgangs: ");
		if(model.getCompileFailures().size()==0)
			messageBuffer.append("<span style=\"color: green;\">Kompilieren war erfolgreich.</span></p></body></html>");
		else{
			messageBuffer.append("<span style=\"color: red;\">Kompilieren war nicht erfolgreich.</span></p>");
			for(HashMap<String, String> failure: model.getCompileFailures()){
				messageBuffer.append("<div style=\"margin-left:20px;\">"+failure.get("Art")+": "+failure.get("Nachricht")+" in Zeile "+failure.get("Zeile")+" von "+failure.get("Klasse")+"</div>");
			}
			messageBuffer.append("</body></html>");
		}			
		messageBox.setText(messageBuffer.toString());
	}

}

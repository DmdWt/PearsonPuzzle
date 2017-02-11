package mobileVersion;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controller.Controller;

import mobileVersion.controller.AppletController;
import mobileVersion.view.AppletMenu;
import mobileVersion.view.AppletView;
import mobileVersion.view.ProjectListAView;
import model.Model;

/**
 * Diese Klasse ist als Java Web Start Application designed, um aus dem Internet (per Webstart) geladen werden zu können. 
 * Da Lese- und Schreibrechte, sowie die Nutzung eines Class Loaders notwendig sind, kannd die Applikation allerdings nicht im Sandbox-Modus ausgeführt werden.
 * Vergleiche hierzu: https://docs.oracle.com/javase/tutorial/deployment/webstart/index.html
 * 
 * Das Applet kann auch aus dem Betrieb heraus instanziert werden.
 * Dies kann auf verschiedene Arten geschehen: 
 * <ul>Ein neues Model wird vom Konstruktor angelegt
 * <li>Applet app = new Applet(); app.createGUI();</li></ul>
 * <ul>Das Model wird vorher festgelegt:
 * <li>Model model = new Model(); Applet app = new Applet(model); app.createGUI()</li>
 * <li>Model model = new Model(); Applet app = new Applet(); app.createGUI(model)</li>
 * <li>Model model = new Model(); Applet app = new Applet(model); app.createGUI(model)</li></ul>
 * 
 * @author workspace
 */
public class Applet extends JFrame {
	private static final long serialVersionUID = -6156615178571385705L;

	private Model model;
	
	public static void main(String[] args) {
		Model model = new Model();
		Applet app = new Applet();
		app.createGUI(model);
	}
	
	public Applet(){
	}
	public Applet(Model model){
		this.model = model;
	}
	
	public void createGUI() {
		if(model==null)
			model = new Model();
		AppletView startPanel = new ProjectListAView(model);		
		startPanel.setOpaque(true);
		setContentPane(startPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		Controller controller = new AppletController(model, startPanel);
		startPanel.addController(controller);
	}
	
	public void createGUI(Model model) {
		this.model = model;
		
		// Falls keine Projekte in der Datenbank zu finden sind, wird hier direkt ein 
		// Dialog geöffnet, um Daten zu importieren.
		if(model.getProjectVector().isEmpty()){
			JFileChooser fc_imp = new JFileChooser();
			int returnVal_imp = fc_imp.showOpenDialog(new JPanel());
			
			if (returnVal_imp == JFileChooser.APPROVE_OPTION) {
				File file = fc_imp.getSelectedFile();
				model.replaceDatabase(file.getName(), file.getParentFile().getAbsolutePath());
			}
		}
		AppletView startPanel = new ProjectListAView(model);		
		startPanel.setOpaque(true);
		setContentPane(startPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		startPanel.setPreferredSize(new Dimension(400,600));
		setSize(new Dimension(400,600));
		setLocationRelativeTo(null);
		AppletMenu menu = new AppletMenu();
		setJMenuBar(menu);
		//pack();
		setVisible(true);
		Controller controller = new AppletController(model, startPanel);
		menu.addController(controller);
	}
}

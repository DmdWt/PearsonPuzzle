package mobileVersion.controller;

import jUnitUmgebung.UnitRunner;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.JRootPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import org.junit.runner.Result;

import compiler.TestCompiler;

import controller.Controller;
import controller.DCCommand;

import mobileVersion.view.AppletMenu;
import mobileVersion.view.AppletView;
import mobileVersion.view.CodeSortAView;
import mobileVersion.view.ProjectListAView;
import model.Model;
import model.access.AccessGroup;

import view.Allert;
import view.PPException;
import view.teacher.UnitEditor;

/**
 * Schlanker und performanter Controller für die Applet-Views.
 * Entspricht von den Implementierten Methoden i.A. dem Default Controller.
 * 
 * @author workspace
 */
public class AppletController implements Controller{
	
	private Model model;
	private AppletView view;

	public AppletController(Model model, AppletView view){
		this.model = model;
		this.view = view;
		view.addController(this);
	}

	public void itemStateChanged(ItemEvent arg0) {
		// Wird aktuell nicht benötigt
	}

	public void actionPerformed(ActionEvent e) {
		DCCommand cmd = DCCommand.valueOf(e.getActionCommand());
		switch(cmd){
		
		// ---------------------- View Wechsel-----------------------------------
		case Login: 
			model.login((String)view.get("username"), (char[])view.get("password"));
			if(model.getAccessGroup()==AccessGroup.UNAUTHORIZED)
				return;
			// !!!Position ist wichtig:
			// Hier entscheidet sich, welche View nach dem Login erscheint.
		case ProjectList: 
				removeView();
				setView( new ProjectListAView(model));
			break;
		case OpenProject:
			if(model.getProjectListID()==null)
				view.showDialog(Allert.noProjectSelected);
			else{
				removeView();
				setView(new CodeSortAView(model));
			}
			break;
			
		// -----------------------View Anpassung -----------------------------------
		case Compile:		
				TestCompiler testCompiler = new TestCompiler(model.getSollution(), model.getImport("methods"), model.getImport("online"), model.getImport("classes"));
				testCompiler.compile();
				model.setCompilerFailures(testCompiler.getFailures());
			break;
		case Test:
				model.testOrderOfSollution();
			break;
		case TestCode:
				Result result;
				if(model.getJUnitCode()!=null && !model.getJUnitCode().isEmpty() && !model.getJUnitCode().equals(UnitEditor.DEFAULT_UNIT_CODE)){ //Junit Test soll nur erfolgen, wenn auch einer definerit ist
					UnitRunner unitRunner;
					try {
						unitRunner = new UnitRunner(model.getJUnitCode(), model.getProjectCode(), model.getImport("methods"),model.getImport("online"), model.getImport("classes"));
						result = unitRunner.run();
						model.setJunitFailures(result);
					} catch (PPException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			break;
		default:
			return;
		}
	}

	/**
	 * Methode instanziert neue Views. Etwaige vorher vorhandene Views werden nicht entfernt. (Model hat weiterhin die alte View als Observer, View diesen Controller als Controller). 
	 * Ist gewünscht, dass alte Views komplett verworfen werden, ist dies über dei Methode removeView() zu tun. 
	 * 
	 * <ul><li>Übergebene View wird beim Model als Observer registriert</li>
	 * <li>Das aktuelle RootPane erhält die übergebene View als Inhalt</li>
	 * <li>View und Controller werden verknüpft</li>
	 * <li>Die Menü Bar erhält die View, um sich eventuell anzupassen. (Navigation etc.)</li>
	 * <li>Das RootPane wird neu gezeichnet</li></ul>
	 * @param view
	 */
	private void setView(AppletView view) {
		
		if(view!=null){
			
			model.addObserver(view);
			
			JRootPane frame = this.view.getRootPane();
			frame.setContentPane(view);
			
			view.addController(this);
			this.view=view;
			
			((AppletMenu) (frame).getJMenuBar()).setView(view);
			frame.revalidate();
		}
	}
	
	/**
	 * Entfernt die Bindung der Komponenten der aktuell gehaltenen View, sowie die Bindung dieser View an das Model.
	 * ACHTUNG: Entfernt nicht die View als Feld von Controller! (Um keine Null-Pointer-Exception zu riskieren)
	 */
	private void removeView(){
		model.deleteObserver(view);
		view.removeController(this);
	}

	/**
	 * Gibt die aktuelle View zurück.
	 */
	public AppletView getView() {
		return view;
	}
	
	public Model getModel(){
		return model;
	}

	/**
	 * Legt fest, was beim Ändern der Selektion eines Listenelemts passiert.
	 * @param <ListSelectionModel>
	 */
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel)e.getSource();
        if (((ListSelectionModel) e.getSource()).isSelectionEmpty()) {
        } 
        else {
            // Findet heraus, welcher Index selektiert ist
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) {
                if (lsm.isSelectedIndex(i)) {
                		model.selectProject(i);
                }
             }
        }
	}
}

package controller;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

import java.util.ArrayList;

import javax.swing.*;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import compiler.TestCompiler;

import model.AccessGroup;
import model.Model;
import view.Allert;
import view.LoginView;
import view.JView;
import view.pupil.CodeSortView;
import view.pupil.PupilView;
import view.teacher.ConfigEditor;
import view.teacher.ProjectConfiguration;
import view.teacher.TeacherView;
import view.teacher.TextEditor;

import CodeTest.LineOrderTest;
/**
 * Klasse dient dazu, die standardmäßige Benutzeroberfläche aufzurufen und 
 * mit dem Controller zu verknüpfen.
 * @author workspace
 */
public class DefaultController extends Controller {
	
	public DefaultController(Model model, JView view) {
		super(model, view);
		view.setController(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// !!!ACHTUNG!!! hier wird eine Exception geworfen, falls zu diesem Action Event kein Kommando existiert!
		act(DCCommand.valueOf(e.getActionCommand()), e);
	}

	/**
	 * Legt fest, was bei einem Action Event (z.B. Button drücken) passiert
	 */
	private void act(DCCommand cmd, ActionEvent e){
		// Es erfolgt Warnung, wenn Projekt noch nicht gespeicher wurde
		if(view.getClass().equals(TextEditor.class)  
				&& cmd!=DCCommand.SaveProject
				&& model.hasChanged()){
			Integer allert=view.showMessage(Allert.notSaved);
			if(allert==JOptionPane.YES_OPTION)
				this.act(DCCommand.SaveProject, null);
			else if(allert==JOptionPane.NO_OPTION)
				model.fetchAll();				
			else if(allert==JOptionPane.CANCEL_OPTION)
				return;
		}
		switch(cmd){
			case SubmitPassword:
				if(view.getClass().equals(LoginView.class))
					((LoginView)view).submitChangeToController();
				break;
			case EditProject:
				if(model.getProjectListID()==null)
					view.showMessage(Allert.noProjectSelected);
				else{
					view.quitView();
					this.view=new TextEditor(model);
					view.addController(this);
				}
				break;
			case NewProject:
				model.selectProject(null);
				view.quitView();
				this.view=new TextEditor(model);
				view.addController(this);
				break;
			case OpenProject:
				if(model.getProjectListID()==null)
					view.showMessage(Allert.noProjectSelected);
				else{
					view.quitView();
					this.view=new CodeSortView(model);
					view.addController(this);
				}
				break;
			case ProjectList:
				view.quitView();
				if(model.getAccessGroup().equals(AccessGroup.PUPIL))
					this.view= new PupilView(model);
				else
					this.view= new TeacherView(model);
				view.addController(this);
				break;
			case Admin:
				if(model.getAccessGroup().equals(AccessGroup.TEACHER)){
					view.quitView();
					this.view=new ConfigEditor(model);
					view.addController(this);
				}
				break;
			case Logout:
				view.quitView();
				this.model=new Model();
				LoginView startView= new LoginView(model);
				startView.setController(this);
				startView.addController(this);
				this.view=startView;
				view.update();
				break;
			case SaveProject:
				// Berechtigung wird geprüft
				if(view.getClass().equals(TextEditor.class) 
						&& model.getAccessGroup().equals(AccessGroup.TEACHER)){
					
					if(((TextEditor) view).getCode()==null 
							|| ((TextEditor)view).getProjectName()==null
							|| (((TextEditor) view).getCode()).equals("")
							|| (((TextEditor) view).getProjectName()).equals("")){
					
						view.showMessage(Allert.noContentInput);
					}
					
					else{
						
						// ---- Es wird versucht das Projekt zu speichern, schlägt dies fehl, so existiert bereits ein Projekt mit gleichem Namen
						if(model.saveProject(((TextEditor)view).getCode(), ((TextEditor)view).getProjectName(), ((TextEditor)view).getProjectDescription(),150))
						{
							act(DCCommand.SetTextConfig, null);
							model.saveProjectSettings();
							view.showMessage(Allert.projectSaved);
						}
						else{
							view.showMessage(Allert.projectExists);
						}
					}
				}
				break;
			case DeleteProject:
				if(view.getClass().equals(TeacherView.class) 
						&& model.getAccessGroup().equals(AccessGroup.TEACHER)){
					if(model.getProjectListID()==null)
						view.showMessage(Allert.noProjectSelected);
					else{
						view.allert("Sind Sie sicher, dass Sie das Projekt löschen wollen?");
						// TODO: Auswahlmöglichkeit zu Allert hinzufügen
						if(model.removeProject())
						{
							view.showMessage(Allert.projectDeleted);
							view.update();
						}
					}
				}
				break;
			case SetConfig:
				if(model.getAccessGroup()==AccessGroup.TEACHER)
					model.updateConfig();
				break;
			case ConfigureProject:
				if(model.getAccessGroup()==AccessGroup.TEACHER){
					view.quitView();
					this.view = new ProjectConfiguration(model);
					view.addController(this);
				}
				break;
			case SetTextConfig:
				if(view.getClass().equals(TextEditor.class)){
					ArrayList <JTextField> inputFields = (((TextEditor)view).getInputComponents()); 
					model.setTabSize(Integer.parseInt(inputFields.get(0).getText()) % 10);
					model.setGrade(Integer.parseInt(inputFields.get(1).getText()));
				}
				break;
			case StartGroupSelection:
				((JButton)e.getSource()).setEnabled(false);
				((JButton)((JButton)e.getSource()).getParent().getComponent(1)).setEnabled(true);
				((JButton)((JButton)e.getSource()).getParent().getComponent(3)).setEnabled(true);
				((JButton)((JButton)e.getSource()).getParent().getComponent(4)).setEnabled(false);
				model.addTestGroup();
				break;
			case CancelGroupSelection:
				((JButton)e.getSource()).setEnabled(false);
				((JButton)((JButton)e.getSource()).getParent().getComponent(0)).setEnabled(true);
				((JButton)((JButton)e.getSource()).getParent().getComponent(3)).setEnabled(false);
				((JButton)((JButton)e.getSource()).getParent().getComponent(4)).setEnabled(true);
				model.removeTestGroup(model.getGroupMatrix().size()-1);
				break;
			case SaveGroupSelection:
				((JButton)((JButton)e.getSource()).getParent().getComponent(0)).setEnabled(true);
				((JButton)((JButton)e.getSource()).getParent().getComponent(1)).setEnabled(false);
				((JButton)((JButton)e.getSource()).getParent().getComponent(4)).setEnabled(true);
				break;
			case Compile:
				TestCompiler.compileCode(model.getSolutionStrings());
				model.setCompilerFailures(TestCompiler.getFailures());
				break;
			case TestCode:
				System.out.println(model.getSollution());
				//model.testSolution();
				if(model.testSolution())
					System.out.println("Herzlichen Glückwunsch, richtige Reihenfolge!");
				else
					System.out.println("Reihenfolge nicht 1:1, Test auf Korrektheit folgt");
				Result result = JUnitCore.runClasses(LineOrderTest.class);
				System.out.println("Anzahl der Fehler im Junit Testlauf:"+result.getFailureCount());;
			    for (Failure failure : result.getFailures()) {
			    	if(failure!=null){
			    		model.addjUnitFailure(failure);
			    		System.out.println(failure);
			    	}
			    }
			    view.showMessage(Allert.Failure);
				break;
			default:
				break;
		}
	}

	/**
	 * XXX: Soll !eventuell! ins Modell ausgelagert werden
	 * Nutzername @param username
	 * Passwort @param password
	 */
	public void login(String username, char[] password){
		model.setUsername(username);
		if(username.isEmpty() || password.length==0){
			view.allert("Bitte Nutzernamen und Passwort eingeben");
		}
		else if(model.getAccessGroup(username, password)==AccessGroup.TEACHER){
			model.setAccessGroup(AccessGroup.TEACHER);
			view.quitView();
			this.view=new TeacherView(model);
			view.addController(this);
		}
		else if(model.getAccessGroup(username, password)==AccessGroup.PUPIL){
			model.setAccessGroup(AccessGroup.PUPIL);
			view.quitView();
			this.view=new PupilView(model);
			view.addController(this);
		}
		else{
			view.allert("Zugang verweigert");
		}
	}
	
	/**
	 * Legt fest, was beim Ändern der Selektion eines Listenelemts passiert.
	 * @param <ListSelectionModel>
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(view.getClass().equals(ProjectConfiguration.class)){
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        if (((ListSelectionModel) e.getSource()).isSelectionEmpty()) {
	        } 
	        else {
	            // Findet heraus, welcher Index selektiert ist
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                    System.out.println(i);
	                }
	             }
	        }
		}
		else if(view.getClass().equals(TeacherView.class)
			|| view.getClass().equals(PupilView.class))
		{
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
	                    view.update();
	                }
	             }
	        }
		}	
	}

	/**
	 * Handelt Checkboxen.
	 */
	public void itemStateChanged(ItemEvent e) {
		// ----- Reset Knopf in: view.teacher.ConfigEditor
		if(((AbstractButton) e.getItem()).getActionCommand()==DCCommand.ResetDB.toString()){
			if(e.getStateChange() == ItemEvent.SELECTED
				&& !model.isResetDB()){
					if(view.showMessage(Allert.reset)==JOptionPane.YES_OPTION)
					model.setResetDB(true);
					else
					model.setResetDB(false);
			}
			else
			model.setResetDB(e.getStateChange()==ItemEvent.SELECTED);
		}
	}

	
	public void focusGained(FocusEvent e) {
		// Sorgt dafür, dass der Defaut Text im Text Editor verschwindet
		if(e.getComponent().getClass().equals(JTextArea.class)
				&& ((JTextArea)(e.getComponent())).getText().contains(((TextEditor)view).getDefaultText())){
			((JTextArea)(e.getComponent())).setText("");
		}
	}

	public void focusLost(FocusEvent e) {
		if(e.getComponent().getName().equals("ProjectCode")){
			model.setProjectCode(((JTextArea)(e.getComponent())).getText());
		}
		else if(e.getComponent().getName().equals("ProjectDescription")){
			model.setProjectDescription(((JTextArea)(e.getComponent())).getText());
		}
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON3){
			if(e.getComponent().getName().equals("dropList")){
				//ListSelectionModel lsm= ((JList<String>) (e.getComponent())).getSelectionModel();
				//System.out.println(e.getComponent().getComponentAt(e.getLocationOnScreen()).getName());
				//System.out.println(e.getComponent().getComponentAt(e.getPoint()));
			}
		}
			
	}
		// TODO Auto-generated method stub


	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void tableChanged(TableModelEvent e) {
		if(view.getClass().equals(ProjectConfiguration.class)){
			int row = e.getFirstRow();
			int column = e.getColumn();
	        TableModel tableModel = (TableModel)e.getSource();
	        String columnName = tableModel.getColumnName(column);
	        // damit beim Aktualisieren der Tabelle nichts getan wird
	        if(row>=0 && column>=0){
	        	Object data = tableModel.getValueAt(row, column);
	        	if(columnName.equals("Codezeile")){
	        		// nichts zu tun
	        	}
	        	else if(columnName.equals("Testausdruck")){    	
	        		// FIXME: Testausdruck verändern
	        	}
	        	else {
	        		model.setGroupMatrixEntry(row, column, data);
	        	}
	        }
		}		
	}
}

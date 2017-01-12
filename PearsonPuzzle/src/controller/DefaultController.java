package controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import javax.swing.*;
import org.junit.runner.Result;
import compiler.TestCompiler;

import model.Model;
import model.access.AccessGroup;
import view.Allert;
import view.LoginView;
import view.JView;
import view.PPException;
import view.pupil.CodeSortView;
import view.pupil.PupilView;
import view.teacher.OptionConfiguration;
import view.teacher.ConfigEditor;
import view.teacher.TeacherView;
import view.teacher.TextEditor;
import view.teacher.UnitEditor;
import view.teacher.UserEditor;

import JUnitUmgebung.JUnitRunner;
/**
 * Klasse dient dazu, die standardmäßige Benutzeroberfläche aufzurufen und 
 * mit dem Controller zu verknüpfen.
 * @author workspace
 */
public class DefaultController implements Controller, TableModelListener, MouseListener, FocusListener{
	
	private Model model;
	private JView view;
	
	public DefaultController(Model model, JView view){
		this.model=model;
		this.view=view;
		view.addController(this);
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
		if( (view.getClass().equals(TextEditor.class)  
				|| view.getClass().equals(UnitEditor.class)
				|| view.getClass().equals(ConfigEditor.class))
				&& cmd!=DCCommand.Save && cmd!=DCCommand.ConnectedComponent){
			if(model.hasChanged()){
				Integer allert=view.showDialog(Allert.notSaved);
				if(allert==JOptionPane.YES_OPTION)
					this.act(DCCommand.Save, null);
				else if(allert==JOptionPane.NO_OPTION){
					model.fetchAll();
				}
				else if(allert==JOptionPane.CANCEL_OPTION)
					return;
			}
			else if(view.get("projectname")==""){
				view.showDialog(Allert.noContentInput);
				return;
			}
			
		}
		switch(cmd){
			case SubmitPassword:
				if(view.getClass().equals(LoginView.class))
					((LoginView)view).submitChangeToController();
				break;
			case AddUser:
				if(!this.view.getClass().equals(LoginView.class)
						&& !this.view.getClass().equals(UserEditor.class)){
					act(DCCommand.EditUsers, e);
				}
				view.showDialog(cmd, false);
				break;
			case EditUsers:
				view.quitView();
				this.view = new UserEditor(model);
				view.addController(this);
				break;
			case EditProject:
				if(model.getProjectListID()==null
						&& view.getClass()==TeacherView.class)
					view.showDialog(Allert.noProjectSelected);
				else{
					view.quitView();
					this.view=new TextEditor(model);
					view.addController(this);
				}
				break;
			case NewProject:
				model.selectProject(null);
				model.fetchAll();
				view.quitView();
				this.view=new TextEditor(model);
				view.addController(this);
				break;
			case OpenProject:
				if(model.getProjectListID()==null)
					view.showDialog(Allert.noProjectSelected);
				else{
					view.quitView();
					this.view=new CodeSortView(model);
					view.addController(this);
				}
				break;
			case ProjectList:
				view.quitView();
				// Daten werden aus der Datenbank geladen
				model.fetchAll();
				if(model.getAccessGroup().equals(AccessGroup.STUDENT))
					this.view= new PupilView(model);
				else
					this.view= new TeacherView(model);
				view.addController(this);
				break;
			case Admin:
				if(model.getAccessGroup().equals(AccessGroup.TEACHER)){
					view.quitView();
					this.view=new OptionConfiguration(model);
					view.addController(this);
				}
				break;
			case EditJUnit:
				if(model.getAccessGroup().equals(AccessGroup.TEACHER)){
					view.quitView();
					this.view= new UnitEditor(model);
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
			case ShowHelp:
				view.showDialog(DCCommand.ShowHelp, false);
				break;
			case Save:
				if(view.getClass().equals(TextEditor.class) 
						&& model.getAccessGroup().equals(AccessGroup.TEACHER)){
					if(((TextEditor) view).getCode()==null 
							|| ((TextEditor)view).getProjectName()==null
							|| (((TextEditor) view).getCode()).trim().equals("")
							|| (((TextEditor) view).getProjectName()).trim().equals("")){
					
						view.showDialog(Allert.noContentInput);
					}		
					else{						
						// ---- Es wird versucht das Projekt zu speichern, schlägt dies fehl, so existiert bereits ein Projekt mit gleichem Namen
						if(model.saveProject(((TextEditor)view).getCode(), ((TextEditor)view).getProjectName(), ((TextEditor)view).getProjectDescription(),150))
						{
							view.showDialog(Allert.projectSaved);
						}
						else{
							view.showDialog(Allert.projectExists);
						}
					}
				}
				else if(view.getClass().equals(UnitEditor.class)){
					model.setJUnitCode(((UnitEditor) view).getContent());
					model.saveProjectSettings();
				}
				else if(view.getClass().equals(ConfigEditor.class)){
					model.saveGroupMatrix();
				}
				else if(view.getClass().equals(UserEditor.class)){
					view.showDialog(Allert.deleteUser);
					model.deleteUsers(((UserEditor)view).getUsers());
					//view.addController(this);
					act(DCCommand.EditUsers, null);
				}
				break;
			case DeleteProject:
				if(view.getClass().equals(TeacherView.class) 
						&& model.getAccessGroup().equals(AccessGroup.TEACHER)){
					if(model.getProjectListID()==null)
						view.showDialog(Allert.noProjectSelected);
					else{
						view.allert("Sind Sie sicher, dass Sie das Projekt löschen wollen?");
						// TODO: Auswahlmöglichkeit zu Allert hinzufügen
						if(model.removeProject())
						{
							view.showDialog(Allert.projectDeleted);
							view.update();
						}
					}
				}
				break;
			case DeleteOrder:
				if(model.getGroupMatrix().size()!=0)
					view.showDialog(cmd, true);
				else 
					view.allert("Es sind keine Gruppen vorhanden, die man löschen könnte.");
				break;
			case SetConfig:
				if(model.getAccessGroup()==AccessGroup.TEACHER)
					model.updateConfig();
				break;
			case ConfigureProject:
				if(model.getAccessGroup()==AccessGroup.TEACHER){
					view.quitView();
					this.view = new ConfigEditor(model);
					view.addController(this);
				}
				break;
			case ConnectedComponent:
				if(((Component) e.getSource()).getName()!=null){
					String compName = ((Component) e.getSource()).getName();
					String compValue = ((JTextComponent) e.getSource()).getText();
//					if(compName.equals("ProjectCode"))
//						model.setProjectCode(compValue);
//					else if(compName.equals("ProjectDescription"))
//						model.setProjectDescription(compValue);
					if(compName.equals("TabSize"))
						model.setTabSize(Integer.parseInt(compValue));
					else if(compName.equals("ProjectName"))
						model.setProjectName(compValue);
					else if(compName.equals("Grade")){
						model.setGrade(Integer.parseInt(compValue));
					}
				}
				else 
					throw new RuntimeException("View Componente falsch verknüpft!");
				
				
				
				
				
				
				
				

//				if(view.getClass().equals(TextEditor.class)){
//					ArrayList <JTextField> inputFields = (((TextEditor)view).getInputComponents());
//					model.setTabSize(Integer.parseInt(inputFields.get(0).getText()) % 10);
//					model.setGrade(Integer.parseInt(inputFields.get(1).getText()));
//					unsavedChanges = true;
//				}
				break;
			case AddOrder:
				model.addTestGroup();
				break;
			case Compile:
				if(model.getAccessGroup()==AccessGroup.STUDENT)
					TestCompiler.compileCode(model.getSolutionStrings());
				else if(model.getAccessGroup()==AccessGroup.TEACHER)
					TestCompiler.compileCode(((UnitEditor)view).getContent());
				model.setCompilerFailures(TestCompiler.getFailures());
				break;
			case TestCode:
				Result result;
				if(model.getAccessGroup()==AccessGroup.STUDENT){
					System.out.println(model.getSollution());
					//model.testSolution();
					model.testSolution();
					//result = JUnitRunner.run();
				}
				else{
					result = JUnitRunner.run(((UnitEditor) view).getContent());
					System.out.println(result.getFailures());
					System.out.println("Anzahl der Fehler im Junit Testlauf:"+result.getFailureCount());;
					model.setJunitFailures(result);
				}
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
		model.login(username, password);
		if(model.getAccessGroup()==AccessGroup.TEACHER){
			view.quitView();
			this.view=new TeacherView(model);
			view.addController(this);
		}
		else if(model.getAccessGroup()==AccessGroup.STUDENT){
			view.quitView();
			this.view=new PupilView(model);
			view.addController(this);
		}
		else if(model.getAccessGroup()==AccessGroup.UNAUTHORIZED){
			view.allert("Zugang verweigert");
		}
	}
	
	/**
	 * Legt fest, was beim Ändern der Selektion eines Listenelemts passiert.
	 * @param <ListSelectionModel>
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(view.getClass().equals(ConfigEditor.class)){
			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
	        if (((ListSelectionModel) e.getSource()).isSelectionEmpty()) {
	        } 
	        else {
	            // Findet heraus, welcher Index selektiert ist
	            int minIndex = lsm.getMinSelectionIndex();
	            int maxIndex = lsm.getMaxSelectionIndex();
	            for (int i = minIndex; i <= maxIndex; i++) {
	                if (lsm.isSelectedIndex(i)) {
	                    //System.out.println(i);
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
					if(view.showDialog(Allert.reset)==JOptionPane.YES_OPTION)
					model.setResetDB(true);
					else
					model.setResetDB(false);
			}
			else
			model.setResetDB(e.getStateChange()==ItemEvent.SELECTED);
		}
		if(view.getClass()==UserEditor.class){
			if(e.getSource().getClass()==JRadioButton.class){
				model.setUserGroup_toEdit(AccessGroup.valueOf(( (Component) e.getSource()).getName()));
				act(DCCommand.EditUsers, null);
			}
			else if(e.getSource().getClass()==JCheckBox.class){
			}
		}
	}

	
	public void focusGained(FocusEvent e) {
		// Sorgt dafür, dass der Defaut Text im Text Editor verschwindet
		if(view.getClass().equals(TextEditor.class)){
			if(e.getComponent().getClass().equals(JTextArea.class)
					&& ((JTextArea)(e.getComponent())).getText().contains(((TextEditor)view).getDefaultText())){
				((JTextArea)(e.getComponent())).setText("");
			}
		}
		else if(view.getClass().equals(ConfigEditor.class)){
			
		}
	}

	public void focusLost(FocusEvent e) {
		if(view.getClass().equals(TextEditor.class)
				|| view.getClass().equals(UnitEditor.class)){
			String compName = ((Component) e.getSource()).getName();
			String compValue = ((JTextComponent) e.getSource()).getText();
			if(compName.equals("ProjectCode"))
				model.setProjectCode(compValue);
			else if(compName.equals("ProjectDescription"))
				model.setProjectDescription(compValue);
			else if(compName.equals("TabSize"))
				model.setTabSize(Integer.parseInt(compValue));
			else if(compName.equals("ProjectName"))
				model.setProjectName(compValue);
			else if(compName.equals("Grade"))
				model.setGrade(Integer.parseInt(compValue));
			else if(compName.equals("JUnitCode"))
				model.setJUnitCode(compValue);
			//System.out.println("Focus Lost: "+e.getComponent().getName());
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
		if(view.getClass().equals(ConfigEditor.class)){
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
	        		// Daten werden geprüft
	    			int max=0;
	    			for(Integer rule : model.getGroupMatrix().get(column)){
	    				if(rule<max && rule!=0){
	    					try {
	    						throw new PPException("<html><body>Problem in Zeile "+(1+(Integer)row)+", Spalte "+((Integer)column+1)+"<BR> Die Einträge dürfen nicht der Vorgabe widersprechen.</body></html>");
	    					} catch (PPException e1) {
	    							model.setGroupMatrixEntry(row, column, "0");
	    							return;
	    						}
	    				}
	    				max=rule;
	    			}
	        	}
	        }
		}		
	}

	@Override
	public JView getView() {
		return view;
	}

//	@Override
//	public void propertyChange(PropertyChangeEvent evt) {
//		if(view.getClass()==TextEditor.class){
//			String componentName = ((Component) evt.getSource()).getName();
//			String componentText = ((JTextComponent) evt.getSource()).getText();
//			if(componentName!=null)
//			{
//				if(componentName.equals("ProjectName") 
//						&& !componentText.equals(model.getProjectName()))
//					unsavedChanges = true;
//				else if(componentName.equals("ProjectCode")
//						&& !componentText.equals(model.getProjectCode())
//						&& !componentText.equals(TextEditor.defaultCode))
//					unsavedChanges = true;
//				else if(componentName.equals("TabSize")
//						&& !componentText.equals(Integer.toString(model.getTabSize()))){
//					unsavedChanges = true;
//				}
//				else if(componentName.equals("ProjectDescription")
//						&& !componentText.equals(model.getProjectDescription()))
//					unsavedChanges = true;
//			System.out.println(componentName);
//			}
//		}	
//	}
}

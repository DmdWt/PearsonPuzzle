package controller;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;

import compiler.TestCompiler;

import model.Model;

import view.dialog.AddImportDialog;
import view.dialog.AddUserDialog;
import view.dialog.CompileDialog;
import view.dialog.DeleteOrderDialog;
import view.dialog.EditOrderDialog;
import view.dialog.JDialog;

public class DialogController implements Controller, PropertyChangeListener, FocusListener {
	
	private JDialog dialog;
	private Model model;

	public DialogController(Model model, JDialog dialog){
		this.dialog = dialog;
		this.model = model;
		model.addObserver(dialog);
	}

	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/** This method handles events for the text field. */
    public void actionPerformed(ActionEvent e) {
    	if(dialog.getClass().equals(CompileDialog.class)){
    		if(e.getActionCommand().equals(DCCommand.Compile.toString())){
    			TestCompiler testCompiler = new TestCompiler(model.getProjectCode(), model.getImport("methods"), model.getImport("online"), model.getImport("classes"));
				testCompiler.compile();
				model.setCompilerFailures(testCompiler.getFailures());
    		}
    		else if(e.getActionCommand().equals(DCCommand.AddMethods.toString())){
    			JDialog dialog = new AddImportDialog(this.dialog, model, "Nötige Methoden");
    			dialog.addController(new DialogController(model, dialog));
				dialog.pack();
				dialog.setVisible(true);
				dialog.revalidate();
    		}
    		else if(e.getActionCommand().equals(DCCommand.AddClasses.toString())){
    			JDialog dialog = new AddImportDialog(this.dialog, model, "Nötige Klassen");
				dialog.addController(new DialogController(model, dialog));
				dialog.pack();
				dialog.setVisible(true);
				dialog.revalidate();
    		}
    	}
        //view.getOptionPane().setValue(btnString1);
    }

	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public JDialog getView() {
		return dialog;
	}
	
	public Model getModel(){
		return model;
	}

	/** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
	        String prop = e.getPropertyName();
	
	        if (dialog.isVisible()
	         && (e.getSource() == dialog.getOptionPane())
	         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
	             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
	            Object value = dialog.getOptionPane().getValue();
	
	            if (value == JOptionPane.UNINITIALIZED_VALUE){ 
	            	return;
	            }
	
	            //Reset the JOptionPane's value.
	            //If you don't do this, then if the user
	            //presses the same button next time, no
	            //property change event will be fired.
	            dialog.getOptionPane().setValue(
	                    JOptionPane.UNINITIALIZED_VALUE);
	            if(dialog.getClass().equals(AddUserDialog.class)){
		            if(dialog.getTitle().equals("Ersten Nutzer anlegen")){
		
		                if(value.equals(JOptionPane.YES_OPTION)){
		                	if(model.saveUser(dialog.get("username"), dialog.get("password"), dialog.get("accessgroup"))){
		                		model.login((String)dialog.get("username"), (char[])dialog.get("password"));
		                		dialog.clearAndHide();
		                	}
		                }
		                else if(value.equals(JOptionPane.CANCEL_OPTION) && model.getAccessGroup()==null){
		                	System.exit(0);
		                }
		            }
		            else if(dialog.getTitle().equals("Nutzer hinzufügen")){
		            	if(value.equals(JOptionPane.OK_OPTION)){
		            		model.saveUser(dialog.get("username"), dialog.get("password"), dialog.get("accessgroup"));
		            		dialog.clear();
		            	}
		            	else{
		            		dialog.clearAndHide();
		            	}
		            }
	        	}
	            else if(dialog.getClass().equals(DeleteOrderDialog.class)){
	    			if(value.equals(JOptionPane.OK_OPTION) && model.getGroupMatrix().size()!=0){
	    				model.removeTestGroup((Integer)dialog.get("groupNumber"));
	    				model.saveGroupMatrix();
	    				dialog.clearAndHide();	    				
	    			}
	    			else
	    				dialog.clearAndHide();
				}
	            else if(dialog.getClass().equals(AddImportDialog.class)){
	            	if(value.equals(JOptionPane.OK_OPTION)){
	            		if(dialog.getTitle().equals("Nötige Klassen"))
	            			model.setImports("classes", (String)dialog.get("input"));
	            		else if(dialog.getTitle().equals("Nötige Methoden"))
	            			model.setImports("methods", (String)dialog.get("input"));
	            		else if(dialog.getTitle().equals("Nötige Imports"))
	            			model.setImports("online", (String)dialog.get("input"));
	            		dialog.clearAndHide();
	            	}
	            	else
	            		dialog.clearAndHide();
	            }
	            else if(dialog.getClass().equals(EditOrderDialog.class)){
	            	if(value.equals(JOptionPane.OK_OPTION)){
	            		model.saveOrderFailures();
	            		dialog.clearAndHide();
	            	}
	            	else if(value.equals(JOptionPane.CANCEL_OPTION)){
	            		dialog.clearAndHide();
//	            		if(model.hasChanged()){
//	            			int n = JOptionPane.showConfirmDialog(dialog, "Sicher, dass Sie ungespeicherte Veränderungen verwerfen wollen?", prop, JOptionPane.OK_CANCEL_OPTION);
//	            			if(n==JOptionPane.OK_OPTION)
//	            				dialog.clearAndHide();
//	            		}
//	            		else 
//	            			dialog.clearAndHide();
	            	}
	            }
	            else if(dialog.getClass().equals(CompileDialog.class)){
	            	dialog.clear();
	            }
			}
		}

	public void focusGained(FocusEvent e) {
		if(dialog.getClass().equals(EditOrderDialog.class)){
			if(e.getSource().getClass().equals(JTextArea.class)
					&& ((JTextArea) e.getSource()).getText().equals(((EditOrderDialog)dialog).DEFAULT_CONTENT)){
				((JTextArea) e.getSource()).setText("");
			}
		}
		// TODO Auto-generated method stub
		
	}

	public void focusLost(FocusEvent e) {
		if(e.getSource().getClass().equals(JTextArea.class)){
			model.setOrderFailures((Integer)dialog.get("groupID"), (String)dialog.get("text"));
		}
	}
}
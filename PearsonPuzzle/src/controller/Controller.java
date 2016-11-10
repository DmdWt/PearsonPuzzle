package controller;

import java.awt.event.ActionEvent;
import view.View;
import model.Model;
/**
 * Oberklasse der verschiedenen Controller, die alle dafür zuständig sind,
 * Daten vom Model an den jeweiligen darstellenden View weiterzureichen und 
 * bei Interaktion des Benutzers die von den Listenern erfassten Veränderungen 
 * an das Model weiterzureichen.
 * 
 * @author workspace
 *
 */
public abstract class Controller implements java.awt.event.ActionListener {
	protected static Model model;
	View view;
	public Controller(Model model, View view){
		this.model=model;
		this.view=view;
	}
	public void updateView(){
	}
	public void editModel(){
		
	}
	public void login(String username, char[] password){
		model.setPassword(password);
		model.setUsername(username);
		// TODO: Passworttest und View Auswahl
		this.view=view.makePupilView(Model.getCodeModel(), model.getSaveModel(),username);
		view.draw();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}
	/*
	List<Model> models;
	List <View> views;
	public Controller(Model model, View view) {
		this.models.add(model);
		this.views.add(view);
		view.addController(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
	}
	public void addView(View view){
		this.views.add(view);
	}
	public void removeView(View view){
		this.views.remove(view);
	}
	public void addModel(Model model){
		this.models.add(model);
	}
	public void removeModel(Model model){
		this.models.remove(model);
	}
	
	//noch unvollsändig es soll eine änderung übergeben werden (runde klammern sind noch zu füllen)
	public void updateModels(){
		for(View view: views){
			//view.update(Event e);
		}
	}
	public void setModel(){
		
	}
	*/
}

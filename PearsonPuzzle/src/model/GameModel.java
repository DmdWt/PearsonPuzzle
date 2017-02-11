package model;

import java.util.HashMap;
import java.util.Observable;

import javax.swing.ImageIcon;

/**
 * Ermöglicht, Punkte zu sammeln oder zu verlieren.
 * Dabei können die Punkte in Module aufgeteilt werden. Der Gesamtpunktestand findet sich stets im Modul "main".
 * Die angegebene maximale Punktzahl bezieht sich auf das Punktemodul main. Diese kann nicht überschritten werden.
 * Es ist durchgehend folgende Bedungung erfüllt:<br> 
 * <b>Punkte der Module (ohne "main") in Summe = Punkte des Moduls "main" < maxScore</b>
 * 
 * <dl><dt>Dabei ist zu beachten:<dt>
 * 	<dd>- Negativer Punktestand ist möglich und nicht nach unten begrenzt.</dd>
 *  <dd>- score(String module) setzt den Punktestand des Moduls auf 1, wenn er <= 0 ist</dd>
 *  <dd>- loose(String module) setzt den Punktestadn des Moduls auf -1, wenn er >= 0 ist</dd>
 *  <dd>- mit score(String module, int points) bzw. loose(String module, int points) wird der Punktestand nach Belieben verändert.</dd>  
 * </dl>
 * @author workspace
 *
 */
public class GameModel extends Observable{
	private Integer maxScore;
	private HashMap <String, Integer> scoreModules;
	
	/**
	 * Eein Game Model mit maximalem Punktestand.
	 * @param maxScore 
	 */
	public GameModel(Integer maxScore){
		scoreModules = new HashMap<String, Integer>();
		scoreModules.put("main", 0);
		this.maxScore = maxScore;
	}
	
	/**
	 * Game Model, das für den Kompiler, jeden Reihenfolgentest und für eine vorahandene JUnit-Testklasse den Maximalen Punktestand um 1 erhöht.
	 * @param model
	 */
	public GameModel(Model model){
		scoreModules = new HashMap<String, Integer>();
		scoreModules.put("main", 0);
		
		// Kompiler
		maxScore=1;
		
		// Reihenfolgentests
		maxScore += model.getGroupMatrix().size();
		
		// Falls kein Reihenfolgentest
		if(maxScore==1)
			maxScore=2;
		
		// JUnitTests
		if(model.getJUnitCode()!=null && !model.getJUnitCode().trim().isEmpty())
			maxScore+=1;
	}
	
	/**
	 * Verändert den Punktestand des angegeben Moduls um die angegeben Punktezahl.
	 * Der Maximale Punktestand kann nicht überschritten werden. 
	 * Wird ein zu hoher Punktestand übergeben, wird stattdessen der maximal zulässige Wert gesetzt.
	 * @param module Name des Moduls
	 * @param points Anzahl der Punkte
	 */
	public void score(String module, int points){
		
		// Falls max Score überschritten würde
		if(scoreModules.get("main")+Math.abs(points)>maxScore){
			Integer difference = maxScore-scoreModules.get("main");
			score(module, difference);
			return;
		}
		
		// Es wird gepunktet
		if(scoreModules.containsKey(module))
			scoreModules.put(module, scoreModules.get(module)+Math.abs(points));
		else
			scoreModules.put(module, Math.abs(points));
		scoreModules.put("main", scoreModules.get("main")+Math.abs(points));
		setChanged();
		notifyObservers("score");
	}
	
	/**
	 * Setzt den Punktestand des angegebenen Moduls auf 1, falls dieser <= 0 ist.
	 * @param module Modulname
	 */
	public void score(String module){
		if(scoreModules.get(module)==null || scoreModules.get(module)==0)
			score(module, 1);
		else if(scoreModules.get(module)<0)
			score(module, Math.abs(scoreModules.get(module))+1);
	}
	
	/**
	 * Verändert den Punktestand des angegeben Moduls um die angegeben Punktezahl nach unten.
	 * 
	 * @param module Modulname
	 * @param points Punkteanzahl
	 */
	public void loose(String module, int points){
		
		if(scoreModules.containsKey(module))
			scoreModules.put(module, scoreModules.get(module)-Math.abs(points));
		else
			scoreModules.put(module, -Math.abs(points));
		
		scoreModules.put("main", scoreModules.get("main")-Math.abs(points));
		
		setChanged();
		notifyObservers("score");

	}
	
	/**
	 * Setzt den Punktestand des angegebenen Moduls auf -1, falls dieser >= 0 ist.
	 * @param module Modulname
	 */
	public void loose(String module){
		if(scoreModules.get(module)==null || scoreModules.get(module)==0)
			loose(module, 1);
		else if(scoreModules.get(module)>0)
			loose(module, Math.abs(scoreModules.get(module))+1);
	}
	
	/**
	 * Gibt den Aktuellen Punktestand des angegeben Moduls zurück. 
	 * Das Modul "main" beinhaltet den Gesamtpunktestand.
	 * 
	 * @param module Modulname
	 * @return Punktestand
	 */
	public Integer getScore(String module){
		if(scoreModules.containsValue(module))
			return scoreModules.get(module);
		else 
			return null;
	}
	
	/**
	 * Gibt ein Bild zurück, das den akutellen Gesamtpunktestand repräsentiert.
	 * @return Smiley-Icon
	 */
	public ImageIcon getScoreImage(){
		ImageIcon[] images = { new ImageIcon("rsc/icon/Smiley/face-crying.png"), 
				new ImageIcon("rsc/icon/Smiley/face-sad.png"),
				new ImageIcon("rsc/icon/Smiley/face-plain.png"),
				new ImageIcon("rsc/icon/Smiley/face-smile.png"),
				new ImageIcon("rsc/icon/Smiley/face-grin.png"),
				new ImageIcon("rsc/icon/Smiley/face-glasses.png"),
				new ImageIcon("rsc/icon/Smiley/face-wink.png")};
		if(scoreModules.get("main")<-1)
			return images[0];
		else if(scoreModules.get("main")<0)
			return images[1];
		else if(scoreModules.get("main")<1)
			return images[2];
		else if(scoreModules.get("main")<2)
			return images[3];
		else if(scoreModules.get("main")<3)
			return images[4];
		else 
			return images[5];
	}
	
	/**
	 * Setzt alle Module auf einen Punktestand von 0
	 */
	public void reset(){
		for(String module : scoreModules.keySet())
			scoreModules.put(module, 0);
		setChanged();
		notifyObservers("reset");
	}
}

package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;


public class OrderFailures {
	public OrderFailures(){
		
	}
	public static boolean testOrder_simple(Model model, String projectCode){
		String tab;
		if(model.getTabSize()==0)
			tab="";
		else
			tab=" ";
		for(int i=0;i<model.getTabSize();i++){
			tab=tab+" ";
		}
		String sollutionString = new String();
		for (String string : model.getSolutionStrings()){
			sollutionString=sollutionString+string+"\n";
		}
		
		if(sollutionString.equals(model.getProjectCode()))
		 	return true;
		return false;
	}
	
	/**
	 * Prüft, ob sollutionCode und normalCode gleiche Einträge haben.
	 * Falls ignoreTabs true gesetzt ist, werden einführende Tabs und Leerzeichen nicht berücksichtigt.
	 * @param sollutionCode	Lösungsvektor
	 * @param normalCode	Vorgegebener (richtiger) Vektor
	 * @param ignoreTabs	Tabs beim Vergleich ignorieren
	 * @return Vektoren sind gleich
	 */
	public static boolean testOrder_simple(Vector<String> sollutionCode, Vector<String> normalCode, boolean ignoreTabs){
		Iterator sollutionIterator = sollutionCode.iterator();
		Iterator normalIterator = normalCode.iterator();
		while(sollutionIterator.hasNext() && normalIterator.hasNext()){
			if(ignoreTabs){
				if(!((String) sollutionIterator.next()).trim().equals(((String) normalIterator.next()).trim()))
					return false;
			}
			else if(!sollutionIterator.next().equals(normalIterator.next()))
				return false;
		}
		if(sollutionIterator.hasNext() || normalIterator.hasNext())
			return false;
		return true;
	}
	
	/**
	 * Prüft, ob die in sortedCode gespeicherte Abfolge von Indizes des codeVektor_normal den in codeLine_GroupMatrix gespeicherten Abfolgeregeln entspricht.
	 * Wichtig dabei ist, dass die Vektoren in codeLineGroupMAtrix monoton steigende Zahlenfolgen beinhalten müssen. (Wäre dies nicht der Fall, würde der vorgegebene Vektor selbst nicht den Regeln entsprechen)
	 * Ausnahme hierbei stellt die 0 dar. Sie symbolisiert, dass der Eintrag nicht relementiert ist.
	 * 
	 * @param sortedCode Vektor von Indizes des codeVector_normal
	 * @param codeLine_GroupMatrix	Matrix mit Abfolgeregeln
	 * @param codeMap	Zuordnung von Codezeile -> Index 
	 * @param codeVector_normal Vektor in einwandfreier Reihenfolge
	 * @return
	 */
	public static LinkedList<Boolean> testOrder_groups(LinkedList<Integer> sortedCode,
			 Vector<Vector<Integer>> codeLine_GroupMatrix, LinkedHashMap<String, Integer> codeMap, Vector<String> codeVector_normal) {
		
		
//		System.out.println("sortedCode: "+sortedCode);
		LinkedList<Boolean> groupFailures = new LinkedList<Boolean>();
		TreeMap<Integer, HashMap<String, Integer>>  treeMap;
		for(Vector<Integer> groupRule : codeLine_GroupMatrix){
			//System.out.println("Rule: "+groupRule);
			int line=0;
			int ruleLayer = 0;
			int linemax =0;
			treeMap =  new TreeMap<Integer, HashMap<String, Integer>>();
			HashMap<String, Integer> minIndex = new HashMap<String, Integer>();
			treeMap.clear();
			for(Integer rule : groupRule){
				if(rule!= null && rule !=0){
					String keyString = codeVector_normal.get(line);
					
					// Regel nicht auf gleicher Ebene wie vorhergehende
					if(ruleLayer<rule){
						if(ruleLayer!=0){
							//System.out.println(treeMap);
							TreeSet<Integer> treeSet = new TreeSet<Integer>(treeMap.lastEntry().getValue().values());
							linemax = treeSet.last();
						}
						ruleLayer=rule;
						treeMap.put(rule, new HashMap<String,Integer>());
					}
					// Regel auf gleicher Ebene
					else if(ruleLayer== rule){
						//treeMap.get(rule).add(new LinkedList<Integer>());
					}
					else{
						// TODO: Fehlerbehandlung
					}
					
					
					
					// Falls auf dieser Ebene bereits ein Regeleintrag zu diesem String existiert, 
					// wird nach dem nächsten vorkommenden String gesucht
					Integer sameStringBuffer = null;
					Integer startIndex;
					if(treeMap.get(rule).containsKey(keyString)){
						sameStringBuffer=treeMap.get(rule).get(keyString);
						startIndex=Math.max(linemax, sameStringBuffer+1);
					}
					else
						startIndex=linemax;
					
					
					// Lösungsstring wird durchsucht und 
					for(int lineNr=startIndex; lineNr<codeVector_normal.size();lineNr++){
							// sortedCode.size()> falls nicht alle Einträge geschaufelt wurden
						if( sortedCode.size()>lineNr && codeMap.get(codeVector_normal.get(line))== codeMap.get(codeVector_normal.get(sortedCode.get(lineNr)))){
							if(minIndex.get(keyString)==null || minIndex.get(keyString)<lineNr){
								treeMap.get(rule).put(keyString, lineNr );
								minIndex.put(keyString, lineNr);
								//System.out.println("Regelebene: "+rule+"\t String: "+keyString+"\t wurde in in Zeile "+lineNr+ " gefunden\t Linemax:"+linemax);
								break;
							}
						}
					}
					if(treeMap.get(rule).get(keyString)==null 
							|| treeMap.get(rule).get(keyString)<=linemax && linemax!=0 
							|| sameStringBuffer!= null && sameStringBuffer==treeMap.get(rule).get(keyString)){
						//System.out.println("Regelebene: "+rule+"\t String: "+keyString+" konnte auf dieser Ebene zuletzt in Zeile "+sameStringBuffer+" gefunden werden."+"\n\ttreeEintrag: "+treeMap.get(rule).get(keyString)+"\t Letzte Zeile ohne Probleme:"+ linemax);
						groupFailures.add(false);
						break;
					}
				}
				line++;
			}
			if(groupFailures.size()<=codeLine_GroupMatrix.indexOf(groupRule)){
				groupFailures.add(true);
				//System.out.println("Fehler in Gruppe Nummer "+groupRule);
			}
		}
		return groupFailures;
	}

}

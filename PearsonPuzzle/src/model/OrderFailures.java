package model;

import java.util.HashMap;
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
	public static LinkedList<Boolean> testOrder_groups(LinkedList<Integer> sortedCode,
			 Vector<Vector<Integer>> codeLine_GroupMatrix, LinkedHashMap<String, Integer> codeMap, Vector<String> codeVector_normal) {
		LinkedList<Boolean> groupFailures = new LinkedList<Boolean>();
		TreeMap<Integer, HashMap<String, Integer>>  treeMap;
		for(Vector<Integer> groupRule : codeLine_GroupMatrix){
			System.out.println(groupRule);
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
							System.out.println(treeMap);
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
					
						if(codeMap.get(codeVector_normal.get(line))== codeMap.get(codeVector_normal.get(sortedCode.get(lineNr)))){
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

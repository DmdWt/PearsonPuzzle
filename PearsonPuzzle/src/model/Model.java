package model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Vector;

import model.access.AccessGroup;
import model.database.dbTransaction;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import view.Allert;
import view.PPException;
import view.teacher.UnitEditor;

import controller.DCCommand;

/**
 * Klasse dient dazu, alle für die Benutzer notwendigen Daten einzulesen, zu
 * halten und zu speichern. Die Extension Observable implementiert die Methoden:
 * - add Observer (Observer o) - setChanged() - notifyObservers()
 * 
 * @author workspace
 * 
 */

public class Model extends Observable {
	private String username;
	
	private Vector<String> codeVector_random;
	private Vector<String> codeVector_normal;
	
	// Bei einer LinkedHaspMap ist auch die Reihenfolge der eingefügten Elemente einsehbar.
	// Die linked Hash Map soll NICHT frei verfügbar sein 
	// (sonst kann man eventuell die richtige Reihenfolge auslesen)
	private LinkedHashMap<String, Integer> codeMap;
	private LinkedList<Integer> sortedCode;	
	private Vector<String> testExpressionsVector;
	private Vector<Vector<Integer>> codeLine_GroupMatrix;
	private String projectName;
	private String projectDescription;
	private String projectCode;
	private HashMap<String, String> projectImports;
	
	private List<String> projectList;
	private Vector<String> projectVector;
	private Vector<Integer> tabVector;	// TODO: Tab Vector nutzen 
	private Integer projectID;
	private int tabSize;
	private boolean randomMode;
	private boolean resetDB;
	private int grade;
	private dbTransaction dataBase;
	private AccessGroup accessGroup;
	private AccessGroup userGroup_toEdit;
	
	private String jUnitCode;
	private LinkedList<String> orderFailureText;
	private LinkedList<Failure> jUnitFailures;
	private Vector<HashMap<String, String>> compileFailures;
	//private LinkedList<Boolean> groupFailures;
	private LinkedHashMap<String,Boolean> successMap;

	private PPException exception;

	private HashMap<String, String> personMap;

	private String studentGroup;

	// private Exception exceptionModel;

	public Model() {
		jUnitFailures=new LinkedList<Failure>();
		compileFailures = new Vector<HashMap<String,String>>();
		personMap = new HashMap<String, String>();
		
		try{
			dataBase = new dbTransaction(this);			
		}
		catch(PPException e){
			if(e.getMessage().equals(PPException.anotherInstanceIsRunnign)){
				e.handleException(this);
				System.exit(0);			
			}
			else if(e.getMessage().equals(PPException.noDatabaseExists)){
				e.handleException(this);
			}
			else 
				e.printStackTrace();
			
		}
		// Datenbankverbindung wird aufgebaut
		
		
		
		// Holt Daten aus der Datenbank
		this.fetchAll();
	}
	
	/**
	 * Bei Methodenaufruf wird das Model nur auf setChanged() gestetzt, <br>
	 * wenn sich auch wirklich etwas geändert hat.
	 * @param string Zeichenkette
	 * @param string_to_compare Zu vergleichende Zeichenkette
	 */
	private boolean setChanged(final String string, final String string_to_compare){
		if(string !=null && string.equals(string_to_compare))
			return false;
		else
			this.setChanged();
		return true;
	}
	
	/**
	 * Bei Methodenaufruf wird das Model nur auf setChanged() gestetzt, <br>
	 * wenn sich auch wirklich etwas geändert hat.
	 * @param integer Integer
	 * @param integer_to_compare Zu vergelichende Integer
	 */
	private boolean setChanged (final Integer integer, final Integer integer_to_compare){
		if(integer != null && integer.equals(integer_to_compare))
			return false;
		else
			this.setChanged();
		return true;
	}
	
	/**
	 * Methode dient dazu, Views über auftretende Exceptions zu informieren.
	 * @param exception Exception
	 */
	public void setException(PPException exception){
		this.exception = exception;
		setChanged();
		notifyObservers(exception);
	}
	public PPException getException(){
		return exception;
	}

	

	// ------------------------------------ Getter und Setter ------------------------------
	// --- Klassenstufe
	public void setGrade(int grade) {
		if (grade < 14 && grade > 5) {
			setChanged(this.grade, grade);
			this.grade = grade;
		} else {
			// Fehlerausgabe: Diese Jahrgangsstufe ist nicht klassifiziert
		}
		boolean hasChanged = new Boolean(hasChanged());
		notifyObservers();
		if(hasChanged)
			setChanged();
	}
	
	public int getGrade() {
		return grade;
	}
	
	// --- Tabbreite
	public void setTabSize(String tabSize_String) {
		Integer tabSize = ValueValidation.validateTabSize(tabSize_String);
		setChanged(this.tabSize, tabSize);
		this.tabSize = tabSize;
		Boolean hasChanged = new Boolean(hasChanged());
		notifyObservers();
		if(hasChanged)
			setChanged();
	}
	public int getTabSize() {
		return tabSize;
	}
	public Vector<Integer> getTabVector_random(){
		return tabVector;
	}

	// --- Zufallsmodus
	public void setRandomMode(boolean random) {
		this.randomMode = random;
	}
	public boolean getRandomMode() {
		return randomMode;
	}

	// --- Zugriffsgruppe
	public void login(String username, char[] password){
		this.accessGroup = getAccessGroup(username, password);
		this.username = username;
	}
	public AccessGroup getAccessGroup(String username, char[] password) {
		if (dataBase.lookUpstudent(username, password)) {
			return AccessGroup.STUDENT;
		} else if (dataBase.lookUpteacher(username, password)) {
			return AccessGroup.TEACHER;
		} else
			return AccessGroup.UNAUTHORIZED;
	}
	public AccessGroup getAccessGroup(){
		return accessGroup;
	}
	
	// --- Nutzername
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	
	// -------- Code

	// --- Projekte
	public void clearProjectData(){
		projectID = null;
		projectName = new String("");
		projectCode = new String("");
		projectDescription = new String("");
		jUnitCode = null;
		tabSize = 0;
		grade = 0;
		testExpressionsVector = new Vector<String>();
		codeLine_GroupMatrix = new Vector<Vector<Integer>>();
		
	}
	public Vector<String> getProjectVector(){
		return projectVector;
	}
		// -- Gewähltes Projekt
	public void selectProject(Integer projectID) {
		this.projectID = projectID;
		this.fetchProjectCode();
		this.fetchProjectSettings();
		if(projectID!=null)
		setChanged();
		notifyObservers();
	}
	
	public void setProjectName(String projectName) {
		setChanged(this.projectName, projectName);
		this.projectName = projectName;
	}
		
	public Integer getProjectListID() {
		return projectID;
	}
	public String getProjectName(){
		if(projectID!=null)
			return projectList.get(projectID);
		else 
			return projectName;
	}
	public void setProjectDescription(String descriptionString) {
		setChanged(this.projectDescription, descriptionString);
		this.projectDescription=descriptionString;
	}
	public String getProjectDescription() {
		return projectDescription;
	}

	/**
	 * Setzt den Projectcode. 
	 * Leere Zeilen werden dabei entfernt.
	 * @param codeString
	 */
	public void setProjectCode(String codeString){
		codeString = ValueValidation.removeEmptyLines(codeString);
		System.out.println("Model:"+codeString);
		setChanged(this.projectCode, codeString);
		projectCode=codeString;
		boolean hasChanged = new Boolean(hasChanged());
		notifyObservers();
		if(hasChanged)
			setChanged();
	}
	
	
	public String getProjectCode() {
		// XXX: Hier wurde am 7.2. trim() ergänzt Nebenwirkungen unbekannt
		return projectCode.trim();
	}
	
				// Projektvektor
	
	/**
	 * Liefert einen Code Vektor. <br>
	 * Nur TEACHER kann sowohl einen randomisierten, als auch einen normal sortierten Vektor anfordern. 
	 * Standardmäßig bekommt TEACHER einen nicht randomisierten Vektor. 
	 * Von STUDENT kann nur der ranomisierte Vektor abgefragt werden.
	 * @param random
	 * @return
	 */
	public Vector<String> getCodeVector(Boolean random) {
		if(projectID==null)
			return new Vector<String>();
		if(accessGroup == AccessGroup.TEACHER
				&& (random==null || random == false))
			return codeVector_normal;
		return codeVector_random;
	}
//	public void setCodeVector(Vector<String> codeVector) {
//		this.codeVector_random = codeVector;
//	}
	
		
	// ------------------ Reihenfolgen und Tests
		/**
		 * Gibt eine zewidimensionale Matrix zurück, die aus Vektoren besteht, die ihrerseits eine aufsteigene Zahlenfolge beinhaltet.
		 * Jeder Vektor der Matrix reprästiert eine Gruppe von Bedingungen für den Lösungsverktor. 
		 * @return
		 */
		public Vector<Vector<Integer>> getGroupMatrix() {
			return codeLine_GroupMatrix;
		}
		/**
		 * Belegt den Eintrag an der Stelle x/y der Reihenfolgenmatrix mit dem Wert value.
		 * @param yPosition
		 * @param xPosition
		 * @param value
		 */
		public void setGroupMatrixEntry(int yPosition, int xPosition, Object value){
			String string = (String)value;
			string=string.trim();
			try{
			codeLine_GroupMatrix.get(xPosition).set(yPosition, Integer.parseInt(string));
				}
				catch(NumberFormatException e){}
			setChanged();
			notifyObservers();
			setChanged();
		}
		/**
		 * Matrix mit Reihenfolgenregeln wird in der Datenbank gespeichert.
		 */
		public void saveGroupMatrix(){
			dataBase.saveOrder(getProjectName(), codeLine_GroupMatrix);
			notifyObservers();
		}
		
		public void saveRandomisation(){
			if(!sortedCode.isEmpty() && sortedCode.size()==codeVector_normal.size())
				normalizeSortedCode();
			if(ValueValidation.isValidRandomization(sortedCode, codeVector_normal)){
				dataBase.setRandomKeys(getProjectName(), getSollutionOrder());
				fetchProjectCode();
				setChanged();
				notifyObservers(DCCommand.Save);
			}
			else{
				setChanged();
				notifyObservers(Allert.code_not_fully_sorted);
			}
				
//			if(sortedCode.isEmpty() || sortedCode.size()<codeVector_normal.size()){
//				setChanged();
//				notifyObservers(Allert.code_not_fully_sorted);
//			}
//			else{
//				setChanged();
//				System.out.println(getSollutionOrder());
//				normalizeSortedCode();
//				System.out.println(getSollutionOrder());
//				dataBase.setRandomKeys(getProjectName(), getSollutionOrder());
//				fetchProjectCode();
//				System.out.println(dataBase.getRandomKeys(getProjectName()));
//				notifyObservers(DCCommand.Save);
//			}
		}
		
	// ---------------- Asserts können in der Zeile ergänzt werden (nicht vollständig implementier)
		public Vector<String> getTestExpressionsVector() {
			return testExpressionsVector;
		}
		public void setTestExpressionsVector(Vector<String> testVector) {
			this.testExpressionsVector = testVector;
		}	
	public void addTestGroup(){
		Vector<Integer> codeGroup=new Vector<Integer>();
		for (Iterator<String> iterator = codeVector_random.iterator(); iterator.hasNext();) {
			iterator.next();
			codeGroup.add(new Integer(0));
		}
		codeLine_GroupMatrix.add(codeGroup);
		orderFailureText.add(new String());
		setChanged();
		notifyObservers();
		setChanged();
	}
	public void removeTestGroup(int index){
		if(index < codeLine_GroupMatrix.size()){
			codeLine_GroupMatrix.remove(index);
			orderFailureText.remove(index);
		}
		setChanged();
		notifyObservers(DCCommand.DeleteOrder);
		setChanged();
	}	
	
	// --------------- Editieren des Lösungsvektors -----------------------
	public void insertInSollution(int index, String value){
		// Wird so gelöst, damit codeMap nicht öffentlich wird (diskutabel)
		//sortedCode.add(index, codeVector_normal.indexOf(value));
		
		//sortedCode.add(index, codeMap.get(value.trim()));
		sortedCode.add(index, codeMap.get(value));
		setChanged();
		notifyObservers();
	}
	public void replaceInSollution(int index, String value){
		sortedCode.remove(index);
		//sortedCode.add(index, codeMap.get(value.trim()));
		sortedCode.add(index, codeMap.get(value));
		setChanged();
		notifyObservers();
		
		
		
		//sortedCode.add(index, codeVector_normal.indexOf(value));
	}
	public void removeInSollution(int index){
		sortedCode.remove(index);
		setChanged();
		notifyObservers();
	}
	public void setSollutionVector(LinkedList<Integer> sollution){
		sortedCode = sollution;
	}
	public LinkedList<Integer> getSollutionOrder(){
		return sortedCode;
	}
	public String getSollution(){
		StringBuffer solution = new StringBuffer();
		for(Integer index : sortedCode){
			if(index!=0)
				solution.append("\n");
			solution.append(codeVector_normal.get(index));
		}
		return new String(solution);
	}
	public Vector<String> getSolutionStrings(){
		Vector<String> solution = new Vector<String>(codeMap.size());
		for(Integer index: sortedCode){
			solution.add(codeVector_normal.get(index));
		}
		return solution;
	}
	
	/**
	 * Der Vektor sortedCode wird normalisiert, das heißt, dass vorher doppelt vorhandene Einträge durch eindeutige Zuordnung ersetzt werden.
	 * Beispel: <br>
	 * Zustand vor der Methode: codeVector_normal: ("a","a","b") codeMap: {"a"-> 0, "b"->2} sortedCode (0,0,2)<br>
	 * Zustand nach der Methode: codeVector_normal: ("a","a","b") codeMap: {"a"-> 0, "b"->2} sortedCode (0,1,2)
	 */
	private void normalizeSortedCode(){
		for(int i=0; i<codeVector_normal.size(); i++){
			if(!sortedCode.contains(i) && sortedCode.contains(codeMap.get(codeVector_normal.get(i)))){
				try{
				List<Integer> buffer = sortedCode.subList(sortedCode.indexOf(codeMap.get(codeVector_normal.get(i)))+1, sortedCode.size());
				int j = buffer.indexOf(codeMap.get(codeVector_normal.get(i)));
				
				//int j = sortedCode.lastIndexOf(codeMap.get(codeVector_normal.get(i)));
				if(j>=0)
					sortedCode.set(j+sortedCode.size()-buffer.size(), i);
					// sortedCode.set(j,i);
				}
				catch(Exception e){
					//e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Gibt eine Fehlerbeschreibung in Form einer HashMap zurück.
	 * TODO: Sind für eine "OrderGroup" spezielle Ergebnisstrings in der Datenbank hinterlegt, werden diese bei der zugehörigen Gruppe angehängt.
	 *
	 * @return
	 */
	public LinkedHashMap<String,Boolean> testOrderOfSollution(){
		successMap = new LinkedHashMap<String,Boolean>();
		//System.out.println(sortedCode+"vorher"+codeMap);
		if(sortedCode.isEmpty()){
			successMap.put("Ausreichend viele Einträge", false);
			setChanged();
			notifyObservers(DCCommand.Test);
			return successMap;
		}
//		else if(sortedCode.size()<codeVector_normal.size()){
//			successMap.put("Ausreichend viele Einträge", false);
//			setChanged();
//			notifyObservers(DCCommand.Test);
//			return successMap;
//		}
		
		normalizeSortedCode();
		
		//System.out.println(sortedCode+"nachher"+codeMap);
//		LinkedList<Integer> sortedCode = new LinkedList<Integer>();
//		Vector<String> codeVector_normal = (Vector<String>) codeVector_normal.clone();
//		for(
		
		Boolean result;
		
		//result = OrderFailures.testOrder_simple(this, projectCode);
		
		LinkedList<Boolean> groupFailures = OrderFailures.testOrder_groups(sortedCode, codeLine_GroupMatrix, codeMap, codeVector_normal);
		if(groupFailures.size()==0){
			result = OrderFailures.testOrder_simple(getSolutionStrings(), codeVector_normal, true);
			successMap.put("Test auf 1:1 Reihenfolge", result);
		}
		else
			successMap.put("Gruppentests", !groupFailures.contains(false));
		for(int i=0;i<groupFailures.size();i++){
			successMap.put("Test "+(i+1), groupFailures.get(i));
		}
		setChanged();
		
		notifyObservers(DCCommand.Test);
		return successMap;
//		
//		String sollutionString = new String();
//		for (String string : getSolutionStrings()){
//			sollutionString=sollutionString+string+"\n";
//		}
//		if(sollutionString.equals(projectCode))
//		 	return true;
//		return false;
	}
	/**
	 * Gibt das ergebnis des Reihenfolgentest zurück.
	 * @return the successMap
	 */
	public LinkedHashMap<String, Boolean> getSuccessMap() {
		return successMap;
	}

	/**
	 * Gibt Aufschluss, ob die Lösungsmatrix die Codezeile enthält.
	 * Leerzeichen und Tabs werden nicht berücksichtigt.
	 * @param codeLine Codezeile, die geprüft werden soll
	 * @return Lösungsmatrix enthält Codezeile
	 */
	public boolean codeContains(String codeLine){
		if(codeMap.containsValue(codeLine.trim()))
			return true;
		return false;
	}
	

	/**
	 * Fehler, die beim Unit Testlauf aufgetreten sind, werden zurückgegeben.
	 * @return the jUnitFailures
	 */
	public LinkedList<Failure> getjUnitFailures() {
		return jUnitFailures;
	}

	/**
	 * Dient dazu, Fehler zu hinterlegen, die beim Unit Testlauf aufgetreten sind.
	 * @param jUnitFailures the jUnitFailures to set
	 */
	public void setJunitFailures(Result result) {
		if(result == null)
			jUnitFailures = null;
		else{
		jUnitFailures = new LinkedList<Failure>();
		for (Failure failure : result.getFailures()) {
			if(failure!=null)
				this.jUnitFailures.add(failure);
		}
		setChanged();
		notifyObservers(DCCommand.TestCode);
		}
	}
	
	/**
	 * Gibt einen leeren String (nicht null) zurück, wenn kein Unit Code hinterlegt ist.
	 * Gibt Unit Sourcecode in Textform zurück, falls ein solcher hinterlegt ist.
	 * @return
	 */
	public String getJUnitCode() {
		return jUnitCode;
	}
	
	/**
	 * JUnit Code wird ans Model übergeben. 
	 * Dalls sich der übergebene Wert vom im Model hinterlegten Wert unterscheidet, 
	 * wird der Status des Models auf "changed" gesetzt.
	 * @param jUnitCode JUnit Sourcecode in Textform
	 */
	public void setJUnitCode(String jUnitCode) {
		if(jUnitCode==null || jUnitCode.equals(UnitEditor.DEFAULT_UNIT_CODE)){
			setChanged(this.jUnitCode, "");
			this.jUnitCode = "";
		}
		else{ 
			setChanged(this.jUnitCode, jUnitCode);
			this.jUnitCode = jUnitCode;
		}
	}

	/**
	 * @return the compileFailures
	 */
	public Vector<HashMap<String,String>> getCompileFailures() {
		return compileFailures;
	}
	public void setCompilerFailures(Vector<HashMap<String, String>> failures) {
		this.compileFailures = failures;
		setChanged();
		notifyObservers(DCCommand.Compile);
	}

	// ------------------------------------ Datenbankinteraktionen----------------------------------------
	/**
	 * Model wird mit in Datenbank vorhandenen Werten gefüllt. <br>
	 * Ist Abhängig vom unter <b>projectID</b> gespeicherten Listeneintag. 
	 */
	public void fetchAll(){
		//this.randomMode = true;
		
		// Default Werte werden gesetzt
		tabSize = 0;
		grade = 0;
		projectName = "";
		
		// Datenbank wird ausgelesen
		this.fetchProjects();
		this.fetchProjectCode();
		this.fetchProjectSettings();
		setChanged();
		notifyObservers();
		clearChanged();
	}
	
	/**
	 * Speichert die im Model hinterlegten groben Eckdaten zum Projekt ab. 
	 * Die "groben Eckdaten" sind: Projektname, Projektcode, Beschreibung/Arbeitsanweisung.
	 */
	public void saveProject(boolean randomize){
		if(randomize){
			saveProject(getProjectCode(), getProjectName(), getProjectDescription(), null);
			setChanged();
		}
		else if(sortedCode.isEmpty() || sortedCode.size()<codeVector_normal.size()){
			setChanged();
			notifyObservers(Allert.code_not_fully_sorted);
		}
		System.out.println(dataBase.getRandomKeys(getProjectName()));
//		else{
//			normalizeSortedCode();			
//			saveProject(getProjectCode(), getProjectName(), getProjectDescription(), getSollution());
//		}
		notifyObservers(DCCommand.Save);
	}
	
	/**
	 * Speichert das Projekt. Für den Fall, dass das Projekt umbenannt wurde, <br>
	 * wird projectName und der gewählte Eintrag ind projectList auf Ungleichheit geprüft.
	 * @param codeString Neuer Codeinhalt 
	 * @param projectName Neuer Projektname 
	 * @param linelength
	 */
	public boolean saveProject(String codeString, String projectName, String projectDescription, Integer linelength) {
		projectCode = new String(codeString);
		
		if(projectID==null 
				|| !projectName.equals(projectList.get(projectID))){
			// ---- Prüfen, ob bereits ein gleichnamiges Projekt existiert 
			if( dataBase.projectExists(projectName)){
				return false;
			}
			// ---- Wenn der Projektname geändert wurde, Projektnamen updaten
			else if(projectID != null && !projectName.equals(projectList.get(projectID))){
				dataBase.renameProject(projectList.get(projectID), projectName);
			}
		}
		
		// ----- Projekt speichern
		dataBase.saveProject(projectName, codeString,"", "", projectDescription ,tabSize, null);
		dataBase.updateDescription(projectName, projectDescription);
		
		// TODO: Test, ob erfolgreich gespeichert wurde
		if(projectID!=null){
			dataBase.saveProjectSettings(projectName, tabSize, grade);
			if(jUnitCode!=null)
				dataBase.saveJUnitTest(projectName,jUnitCode);
			dataBase.saveOrder(projectName, codeLine_GroupMatrix);
			dataBase.saveOrderFailure(projectName, orderFailureText);
			if(projectImports!=null)
				dataBase.saveImports(projectName, projectImports);
			System.out.println(codeLine_GroupMatrix);
		}
		
		this.fetchProjects();	
		selectProject(projectList.indexOf(projectName));
		
		this.setChanged();
		this.notifyObservers();		
		return true;
	}
	
	/**
	 * Kann nur ausgeführt werden, wenn Projekt selektiert wurde.
	 * Speichert TabSize, Grade, JUnitCode, und alle Imports (Klassen, Methoden, Online). 
	 */
	public void saveProjectSettings(){
		if(projectID!=null){
			dataBase.saveProjectSettings(projectList.get(projectID), tabSize, grade);
			if(jUnitCode!=null)
				dataBase.saveJUnitTest(getProjectName(),jUnitCode);		
			dataBase.saveImports(projectList.get(projectID), projectImports);
		}
		notifyObservers();
		clearChanged();
	}
	
	/**
	 * Löscht das mit <b>projectID</b> selektierte Projekt <br>und löscht es aus der Datenbank <br>
	 * Löschen_erfolgreich@return
	 */
	public boolean removeProject() {
		if(dataBase.delete(projectList.get(projectID))){
			this.selectProject(null);
			this.fetchAll();
			return true;
		}
		return false;
	}
	
	/**
	 * Speichert die neue Konfiguration
	 */
	public void updateConfig() {
		if(isResetDB()){
			try {
				dataBase.resetAll();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// ----- private Methoden zur Datenbankinteraktion
	/**
	 * Holt Liste mit Projekten aus Datenbank
	 */
	private void fetchProjects() {
		List <String> projects;
		projects = dataBase.getProjects(grade);
		List<String> projectList = new ArrayList<String>();
		projectVector = new Vector<String>();
		for (String line : projects) {
			projectList.add(projectList.size(), line);
			projectVector.add(line);
		}		
		this.projectList=projectList;
	}
	
	/**
	 * Holt Projektbeschreibung <br><u>des aktuell in der Liste selektieren Projekts </u> <br> aus der Datenbank
	 */
	private void fetchProjectSettings(){
		if(projectID!=null){
			try{
				this.projectDescription = dataBase.getProjectDescription(projectList.get(projectID));
				this.tabSize = dataBase.getTabSize(projectList.get(projectID));
				}
			catch(SQLException e){
				this.projectDescription="Noch keine Beschreibung vorhanden";
				}
			jUnitCode = dataBase.getJUnitCode(getProjectName());
			projectImports = dataBase.getImports(getProjectName());
		}
		else{
			this.projectDescription = new String();
		}
	}
	
	/**
	 * Holt Projekt-Code <br> <u>des aktuell in der Liste selektierten Projekts </u> <br> aus der Datenbank
	 */
	private void fetchProjectCode(){
			if(projectID!=null){
				jUnitCode = dataBase.getJUnitCode(getProjectName());
				projectCode = dataBase.getCode(projectList.get(projectID));
				String[] strings = projectCode.split("\n");
				
				codeVector_normal = new Vector<String>(strings.length);
				codeVector_random = new Vector<String>(strings.length);
				tabVector = new Vector<Integer>(strings.length);
				for(int i=0;i<strings.length; i++){
					codeVector_random.add(new String());
					tabVector.add(new Integer(0));
				}
				
				
				Vector<Integer> randomInts = dataBase.getRandomKeys(getProjectName());				
				if(randomInts.size()!=strings.length){
					System.out.println("Index out of Bounds!!!");
				}
				testExpressionsVector = new Vector<String>();
				codeMap = new LinkedHashMap<String, Integer>();
				sortedCode = new LinkedList<Integer>();
				codeLine_GroupMatrix = new Vector<Vector<Integer>>();
				codeLine_GroupMatrix = dataBase.getOrdervektor(getProjectName());
				orderFailureText = dataBase.getOrderFailure(getProjectName());
				
				for(int index=0; index<strings.length; index++){
					
					
					// Dies ist notwendig, damit im Text sort view die Tabs richtig dargestellt werden.
					String tab;
					if(this.tabSize==0)
						tab="";
					else
						tab=" ";
					for(int i=0;i<this.tabSize;i++){
						tab=tab+" ";
					}
					String bString = strings[index].replaceAll("\t", tab);
//					int tabs=0;
//					while(strings[index].startsWith("\t")){
//						tabs++;
//						strings[index]=strings[index].replaceFirst("\t", "");
//					}
//					tabVector.set(randomInts.get(index), tabs);
//					
//					codeVector_random.set(randomInts.get(index), strings[index]);
					codeVector_random.set(randomInts.get(index), bString);
					//codeVector_normal.add(strings[index]); 			16.1.2016
					codeVector_normal.add(bString);
					
					testExpressionsVector.add(new String());
					
					// 10.1.2016
					//codeMap.put(strings[index], randomInts.get(index));
//					if(!codeMap.containsKey(strings[index]))			16.1.2017
//						codeMap.put(strings[index], index);				16.1.2017
					if(!codeMap.containsKey(bString))
						codeMap.put(bString, index);
				}
				System.out.println("codeMap"+codeMap+"\trandom"+codeVector_random+" "+randomInts);
			}				
	}
	// --- Datenbank zurücksetzen
		public boolean isResetDB() {
			return resetDB;
		}
		public void setResetDB(boolean resetDB) {
			this.resetDB = resetDB;
			setChanged();
			notifyObservers();
		}

		
		
		
		
		
		
		
		
		
		public void setPersons(HashMap <String, String> person_password) {
			this.personMap = person_password;
		}

		public boolean saveUser(Object username, Object password, Object accessgroup) {
			
			String userName = (String)username;
			char[] passWord = (char[]) password;
			AccessGroup accessGroup = (AccessGroup) accessgroup;
			this.setChanged();
			
			if(accessGroup==null)
				notifyObservers("accessgroup_unset");
			else if(accessGroup==AccessGroup.UNAUTHORIZED)
				notifyObservers("username_noTable");
			else if(userName == null 
					|| userName.equals(""))
				notifyObservers("username_unset");
			else if(userName.length()<3)
				notifyObservers("username_toShort");
			
			else if(password == null
					|| passWord.length<1)
				notifyObservers("password_unset");
			else if(passWord.length<8)
				notifyObservers("password_toShort");
			else if(!ValueValidation.proovePassword(passWord))
				notifyObservers("password_unsave");
			else{
				if(dataBase.addUser(accessGroup.toString(), userName, new String (passWord))){
					notifyObservers(DCCommand.Save);
					return true;
				}
			}
			return false;
			
		}	
	
	/**
	 * Gibt eine Liste aller Nutzer der übergebenen Nutzergruppe zurück.
	 * Wenn null übergeben wird, werden alle Nutzer zurückgegeben.
	 * @param accessgroup Nutzergruppe
	 * @return Liste mit Nutzernamen
	 */
	public Vector<String> getUsers(AccessGroup accessgroup) {
		if(accessgroup==null){
			Vector<String> namevector = new Vector<String>();
			for(AccessGroup ac: AccessGroup.values())
				namevector.addAll(dataBase.getNames(ac.toString()));
			return namevector;
		}
		else 
			return dataBase.getNames(accessgroup.toString());
	}

	/**
	 * Löscht die in der Liste übergebenen Nutzer aus der Datenbank.
	 * @param userNames Liste mit Nutzernamen
	 */
	public void deleteUsers(Vector<String> userNames) {
		for(String user: userNames){
			for(AccessGroup ac : AccessGroup.values())
				dataBase.deleteUser(user, ac.toString());
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * Wenn eine Nutzergruppe editiert wird, kann diese Funktion genutzt werden, <br>
	 * um die gwählte Nutzergruppe abzurufen.
	 * @param userGroup_toEdit
	 */
	public AccessGroup getUserGroup_toEdit() {
		if(userGroup_toEdit==null)
			return AccessGroup.TEACHER;
		return userGroup_toEdit;
	}
	/**
	 * Wenn eine Nutzergruppe editiert wird, kann diese Funktion genutzt werden, <br>
	 * um eine Nutzergruppe zu wählen.
	 * @param userGroup_toEdit
	 */
	public void setUserGroup_toEdit(AccessGroup userGroup_toEdit) {
		this.userGroup_toEdit = userGroup_toEdit;
		setChanged();
		notifyObservers();
	}

	/**
	 * Setzt imports. Offiziell mögliche Typen sind: 
	 * {methods, classes, online}<br>
	 * Im Fall "online" müssen die imports in der klassischen Form: *import paket.paket;* übergeben werden. <br>
	 * Im Fall "methods" und "classes" müssen die Imports in Textform (als Sourcecode) übergeben werden.
	 * @param type {"methods", "classes", "online"}
	 * @param text
	 * @return
	 */
	public boolean setImports(String type, String text) {
		if(text.equals(UnitEditor.DEFAULT_IMPORT_TEXT))
			return true;
		setChanged(projectImports.get(type), text);
		if(type.equals("methods"))
			projectImports.put("methods", text);
		else if(type.equals("classes"))
			projectImports.put("classes", text);
		else if(type.equals("online")){
			if(ValueValidation.isValid_OnlineImport(text))
				projectImports.put("online", text);
			else 
				projectImports.put("online", text);
		}
		notifyObservers();
		System.out.println("imports: "+projectImports);
		setChanged();
		return true;
	}
	
	/**
	 * Gibt den hinterlegten Import zurück. <br>
	 * Im Fall "online" sind die imports in der klassischen Form: *import paket.paket;* hinterlegt. <br>
	 * Im Fall "methods" und "classes" sind die Imports in Textform (als Sourcecode) hinterlegt.
	 * @param type {"online", "methods", "classes"}
	 * @return Import
	 */
	public String getImport(String type){
		if(projectImports!=null
				&& projectImports.get(type)!=null)
			return projectImports.get(type);
		return "";
	}

	/**
	 * Holte den Puzzlemodus (aus der Datenbank).
	 * @return Puzzlemodus
	 */
	public Integer getPuzzlemode() {
		// Puzzlemodus wird direkt aus der Datenbank gehot, 
		// da er im Prinip nur die Darstellung bestimmt und nicht im eigentlichen Sinn zum Model gehört
		if(projectID!=null)
			return dataBase.getPuzzleMode(projectList.get(projectID));
		return null;
	}
	/**
	 * Speichert den Puzzlemodus
	 * @param puzzlemode Puzzlemodus
	 */
	public void savePuzzlemode(Integer puzzlemode) {
		if(puzzlemode!=null)
			dataBase.savePuzzlemode(projectList.get(projectID), puzzlemode);
	}

	/**
	 * Dient dazu, den Zugriff auf ein bestimmtes Prokjekt nur für einen Bestimmten Studentennamen zu ermöglichen.
	 * @param student
	 */
	public void setStudentGroup(String student) {
		if(getUsers(AccessGroup.STUDENT).contains(student)){
			this.studentGroup = student;
			setChanged();
		}
	}
	
	/**
	 * Gibt einen Studentennamen zurück, falls dieses Projekt als nur für einen Studenten sichtbar markiert ist. 
	 * Gibt null zurück, falls das Projekt für alle sichtbar ist. 
	 * @return
	 */
	public String getStudentGroup(){
		return studentGroup;
	}
	public void setOrderFailures(Integer index, String failureText) {
		setChanged(failureText, orderFailureText.get(index));
		this.orderFailureText.set(index, failureText);
	}

	public String getOrderFailureText(int index) {
		if(orderFailureText!=null && index<orderFailureText.size())
			return orderFailureText.get(index);
		return null;
	}
	public String getOrderFailureText(String gruppe) {
		gruppe = gruppe.replace("Test", "").trim();
		try{
			Integer index = Integer.parseInt(gruppe);
			return getOrderFailureText(index-1);
		} catch(Exception e){ return null;}
	}

	public void saveOrderFailures() {
		dataBase.saveOrderFailure(projectList.get(projectID), orderFailureText);
	}
	
	public void exportDatabase(String diskplace){
		dataBase.exportAll(diskplace);
	}
	
	public void replaceDatabase(String importfile, String diskplace){
		dataBase.replaceDb(importfile, diskplace);
		fetchAll();
		notifyObservers();
	}
	
	public void setDatabase(dbTransaction dataBaseConnection){
		if(dataBaseConnection==null && this.dataBase!=null){
			// TODO: Datenbankverbindung schließen implementieren;
		}
		this.dataBase = dataBaseConnection;
	}
	
	public void setAccessGroup(AccessGroup accessGroup){
		this.accessGroup = accessGroup;
	}

}

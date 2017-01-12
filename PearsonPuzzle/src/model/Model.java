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

import view.PPException;

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
	private LinkedList<Failure> jUnitFailures;
	private Vector<HashMap<String, String>> compileFailures;
	//private LinkedList<Boolean> groupFailures;
	private LinkedHashMap<String,Boolean> successMap;

	private PPException exception;

	private HashMap<String, String> personMap;

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
	 * <html>Bei Methodenaufruf wird das Model nur auf setChanged() gestetzt, <br>
	 * wenn sich auch wirklich etwas geändert hat.</html>
	 * @param string Zeichenkette
	 * @param string_to_compare Zu vergleichende Zeichenkette
	 */
	private void setChanged(final String string, final String string_to_compare){
		if(string !=null && string.equals(string_to_compare));
		else
			this.setChanged();
	}
	
	/**
	 * <html>Bei Methodenaufruf wird das Model nur auf setChanged() gestetzt, <br>
	 * wenn sich auch wirklich etwas geändert hat.</html>
	 * @param integer Integer
	 * @param integer_to_compare Zu vergelichende Integer
	 */
	private void setChanged (final Integer integer, final Integer integer_to_compare){
		if(integer != null && integer.equals(integer_to_compare));
		else
			this.setChanged();
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
			// TODO: Fehlerausgabe: Diese Jahrgangsstufe ist nicht klassifiziert
		}
		notifyObservers();
	}
	
	public int getGrade() {
		return grade;
	}
	
	// --- Tabbreite
	public void setTabSize(int tabSize) {
		// Tab Size ist auf 10 begrenzt
		if(tabSize>10)
			tabSize = 10;
		setChanged(this.tabSize, tabSize);
		this.tabSize = tabSize;
		notifyObservers();
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
	// TODO: in accessGroup auslagern
	public void login(String username, char[] password){
		this.accessGroup = getAccessGroup(username, password);
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
	public Vector<String> getProjectVector(){
		return projectVector;
	}
		// -- Gewähltes Projekt
	public void selectProject(Integer projectID) {
		this.projectID = projectID;
		this.fetchProjectCode();
		this.fetchProjectSettings();
		notifyObservers();
	}
	
	public void setProjectName(String projectName) {
		setChanged(this.projectName, projectName);
		this.projectName = projectName;
	}
		
	public Integer getProjectListID() {
		return projectID;
	}
			// - Projektname
	public String getProjectName(){
		if(projectID!=null){
			return projectList.get(projectID);
		}
		return "";
	}
			// - Projektbeschreibung
	public void setProjectDescription(String descriptionString) {
		setChanged(this.projectDescription, descriptionString);
		this.projectDescription=descriptionString;
	}
	public String getProjectDescription() {
		if(projectID==null)
			return "";
		return projectDescription;
	}
			// - Projektcode
	public String getProjectCode() {
		if(projectID==null)
			return "";
		return projectCode;
	}
	public void setProjectCode(String codeString){
		setChanged(this.projectCode, codeString);
		projectCode=codeString;
	}
				// Projektvektor
	public Vector<String> getCodeVector() {
		if(projectID==null)
			return new Vector<String>();
		if(accessGroup == AccessGroup.TEACHER)
			return codeVector_normal;
		return codeVector_random;
	}
	public void setCodeVector(Vector<String> codeVector) {
		this.codeVector_random = codeVector;
	}
	
	// Projekt Sequenz Vektor
	public Vector<Vector<Integer>> getGroupMatrix() {
		return codeLine_GroupMatrix;
	}
	public void setGroupMatrixEntry(int yPosition, int xPosition, Object value){
		String string = (String)value;
		string=string.trim();
		try{
		codeLine_GroupMatrix.get(xPosition).set(yPosition, Integer.parseInt(string));
			}
			catch(NumberFormatException e){}
		setChanged();
		notifyObservers();
	}
	
	public void saveGroupMatrix(){
		dataBase.saveOrder(getProjectName(), codeLine_GroupMatrix);
	}
	// Testrelevante Daten
	public void addTestGroup(){
		Vector<Integer> codeGroup=new Vector<Integer>();
		for (Iterator<String> iterator = codeVector_random.iterator(); iterator.hasNext();) {
			iterator.next();
			codeGroup.add(new Integer(0));
		}
		codeLine_GroupMatrix.add(codeGroup);
		setChanged();
		notifyObservers();
	}
	public void removeTestGroup(int index){
		if(index < codeLine_GroupMatrix.size())
			codeLine_GroupMatrix.remove(index);	
		setChanged();
		notifyObservers(DCCommand.DeleteOrder);
	}
	public Vector<String> getTestExpressionsVector() {
		if(projectID==null)
			return null;
		return testExpressionsVector;
	}
	public void setTestExpressionsVector(Vector<String> testVector) {
		this.testExpressionsVector = testVector;
	}

//	// Code zum puzzeln
//	private String[] getRandomCode() {
//		String[] parts = projectCode.split("\n");
//		if (randomMode) {
//			String buffer;
//			for (int i = parts.length - 1; i > 0; i--) {
//				int randomInt = new java.util.Random().nextInt(i);
//				buffer = parts[randomInt];
//				parts[randomInt] = parts[i];
//				parts[i] = buffer;
//			}
//		}
//		return parts;
//	}
	
	// --- Vom Schüler zusammengepuzzelter Code
	
	public LinkedList<Integer> getSollution(){
		return sortedCode;
	}
	public void setSollution(LinkedList<Integer> sollution){
		sortedCode = sollution;
	}
	public Vector<String> getSolutionStrings(){
		Vector<String> solution = new Vector<String>(codeMap.size());
		for(Integer index: sortedCode){
			solution.add(codeVector_normal.get(index));
		}
		return solution;
	}
	// Wird so gelöst, damit codeMap nicht öffentlich wird (diskutabel)
	public void insertInSollution(int index, String value){
		//sortedCode.add(index, codeVector_normal.indexOf(value));
		sortedCode.add(index, codeMap.get(value.trim()));
	}
	public void replaceInSollution(int index, String value){
		sortedCode.remove(index);
		sortedCode.add(index, codeMap.get(value.trim()));
		//sortedCode.add(index, codeVector_normal.indexOf(value));
	}
	public void removeInSollution(int index){
		sortedCode.remove(index);
	}
	public LinkedHashMap<String,Boolean> testSolution(){
		//System.out.println(sortedCode+"vorher");
		for(int i=0; i<codeVector_normal.size(); i++){
			if(!sortedCode.contains(i)){
				int j = sortedCode.lastIndexOf(codeMap.get(codeVector_normal.get(i)));
				sortedCode.set(j, i);
			}
		}
		//System.out.println(sortedCode+"nachher");
//		LinkedList<Integer> sortedCode = new LinkedList<Integer>();
//		Vector<String> codeVector_normal = (Vector<String>) codeVector_normal.clone();
//		for(
		successMap = new LinkedHashMap<String,Boolean>();
		Boolean result;
		
		result = OrderFailures.testOrder_simple(this, projectCode);
		successMap.put("Test auf 1:1 Reihenfolge", result);
		LinkedList<Boolean> groupFailures = OrderFailures.testOrder_groups(sortedCode, codeLine_GroupMatrix, codeMap, codeVector_normal);
		successMap.put("Gruppentest", result);
		for(int i=0;i<groupFailures.size();i++){
			successMap.put("Gruppe"+(i+1), groupFailures.get(i));
		}
		setChanged();
		
		notifyObservers(DCCommand.TestCode);
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
	 * @return the successMap
	 */
	public LinkedHashMap<String, Boolean> getSuccessMap() {
		return successMap;
	}

	/**
	 * @param successMap the successMap to set
	 */
	public void setSuccessMap(LinkedHashMap<String, Boolean> successMap) {
		this.successMap = successMap;
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
	 * @return the jUnitFailures
	 */
	public LinkedList<Failure> getjUnitFailures() {
		return jUnitFailures;
	}

	/**
	 * @param jUnitFailures the jUnitFailures to set
	 */
	public void setJunitFailures(Result result) {
		for (Failure failure : result.getFailures()) {
			if(failure!=null)
				this.jUnitFailures.add(failure);
		}
		setChanged();
		notifyObservers(DCCommand.TestCode);
	}

	public String getJUnitCode() {
		if(projectID==null)
			return "";
		return jUnitCode;
	}

	public void setJUnitCode(String jUnitCode) {
		setChanged(this.jUnitCode, jUnitCode);
		this.jUnitCode = jUnitCode;
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
	 * Speichert das Projekt. Für den Fall, dass das Projekt umbenannt wurde, <br>
	 * wird projectName und der gewählte Eintrag ind projectList auf Ungleichheit geprüft.
	 * @param codeString Neuer Codeinhalt 
	 * @param projectName Neuer Projektname 
	 * @param linelength
	 */
	public boolean saveProject(String codeString, String projectName, String projectDescription,int linelength) {
		projectCode = new String(codeString);
		
		// ---- Prüfen, ob bereits ein gleichnamiges Projekt existiert 
		if(projectID==null 
				|| !projectName.equals(projectList.get(projectID))){
			if( dataBase.projectExists(projectName)){
				return false;
			}
		}
		
		// ---- Wenn der Projektname geändert wurde, Projektnamen updaten
		else if(!projectName.equals(projectList.get(projectID))){
			dataBase.renameProject(projectList.get(projectID), projectName);
		}
		
		// ----- Projekt speichern
		dataBase.saveProject(projectName, codeString,"","", projectDescription ,tabSize);
		dataBase.updateDescription(projectName, projectDescription);
		
		// TODO: Test, ob erfolgreich gespeichert wurde
		this.fetchProjects();
		
		selectProject(projectList.indexOf(projectName));
		saveProjectSettings();
		
		this.setChanged();
		this.notifyObservers();
		this.clearChanged();
		
		return true;
	}
	
	/**
	 * Kann nur ausgeführt werden, wenn Projekt selektiert wurde.
	 */
	public void saveProjectSettings(){
		if(projectID!=null){
			dataBase.saveProjectSettings(projectList.get(projectID), tabSize, grade);
			if(jUnitCode!=null)
				dataBase.saveJUnitTest(getProjectName(),jUnitCode);
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
					codeVector_normal.add(strings[index]);
					
					testExpressionsVector.add(new String());
					
					// 10.1.2016
					//codeMap.put(strings[index], randomInts.get(index));
					if(!codeMap.containsKey(strings[index]))
						codeMap.put(strings[index], index);
				}
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
			else if(!proovePassword(passWord))
				notifyObservers("password_unsave");
			else{
				if(dataBase.addUser(accessGroup.toString(), userName, new String (passWord))){
					notifyObservers(DCCommand.Save);
					return true;
				}
			}
			return false;
			
		}	

	private boolean proovePassword(char[] password){
		boolean number = false;
		boolean specialChar = false;
		boolean upperCase = false;
		boolean lowerCase = false;
		
		for(char c : password){
			if(Character.isDigit(c))
				number = true;
			if(Character.isLowerCase(c))
				lowerCase = true;
			if(Character.isUpperCase(c))
				upperCase = true;
			int asci = (int)c;
			if(asci < 32 || asci >126 )	// auf nicht erlaubte Zeichen prüfen
				return false;
			else if(asci<48 || (asci > 57 && asci <65) || (asci >90 && asci < 97) || asci >123) // auf Sonderzeichen prüfen
				specialChar = true;
		}
		return (number && specialChar && upperCase && lowerCase);
	}
	
	/**
	 * Beschränkt auswahl auf übergebene Nutzergruppe. 
	 * Wenn null übergeben wird, werden alle Nutzer zurückgegeben.
	 * @param accessgroup
	 * @return
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

	public void deleteUsers(Vector<String> users) {
		for(String user: users){
			for(AccessGroup ac : AccessGroup.values())
				dataBase.deleteUser(user, ac.toString());
		}
		setChanged();
		notifyObservers();
	}

	public AccessGroup getUserGroup_toEdit() {
		if(userGroup_toEdit==null)
			return AccessGroup.TEACHER;
		return userGroup_toEdit;
	}

	public void setUserGroup_toEdit(AccessGroup userGroup_toEdit) {
		this.userGroup_toEdit = userGroup_toEdit;
		setChanged();
		notifyObservers();
	}
	
}

/*
public class Model extends Observable {
	private String username;
	private ArrayList<String> codeList;
	private ArrayList<String> saveList;
	private List<String> projectList;
	private Integer projectListID;
	// private String projectDescription;
	private String projectCode;
	private int tabSize;
	private boolean randomMode;
	private int grade;
	private UserDBaccess userDBaccess;
	private AccessGroup accessGroup;

	public Model() {
		try {
			userDBaccess = new UserDBaccess();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setup();		
		// Default Werte werden gesetzt
		this.tabSize = 3;
		this.randomMode = false;
		this.grade = 0;
			}
	private void setup(){
		this.codeList= fetchProject();
		this.saveList= new ArrayList<String>();
		for (Iterator<String> iterator = codeList.iterator(); iterator.hasNext();) {
			saveList.add(new String());
		}
		this.projectList = fetchProjects();
		
		this.projectCode = new String(
				" Zeile 1: Dies ist ein erstes Testprojekt \n \t \t Zeile 2: um zu sehen,\n \t Zeile 3: wie Java dies und die Zeilenumbrüche \n \t Zeile 4: darstellt");

	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		if (grade < 14 && grade > 5) {
			this.grade = grade;
		} else {
			// TODO: Fehlerausgabe: Diese Jahrgangsstufe ist nicht klassifiziert
		}
	}

	public void setTabSize(int tabWidth) {
		this.tabSize = tabWidth;
	}

	public int getTabSize() {
		return tabSize;
	}

	public void setRandomMode(boolean random) {
		this.randomMode = random;
	}

	public boolean getRandomMode() {
		return randomMode;
	}

	public void setSaveList(ArrayList<String> listModelToSave) {
		saveList = listModelToSave;
	}

	private ArrayList<String> fetchProjects() {
		String[] projects = { "Project1", "Project2", "Project3" };
		ArrayList<String> projectList = new ArrayList<String>();
		for (String line : projects) {
			projectList.add(projectList.size(), line);
		}
		return projectList;
	}
	
	private ArrayList<String> fetchProject(){
		//try {
		//	return userDBaccess.getProject("DefaultProjekt");
		//} catch (SQLException e) {
			// TODO Auto-generated catch block
			ArrayList<String> failure= new ArrayList<String>();
			failure.add("Probleme beim Laden des Projekts.");
			failure.add("Bitte nehmen Sie kontakt zum Systemadministrator auf.");
			return failure;
		//}
	}

	public void setPassword(char[] password) {
		// if (isPasswordCorrect(password)) {
		// }
		// else{}
		// //Zero out the possible password, for security.
		// Arrays.fill(password, '0');
	}

	// TODO: in accessGroup auslagern
	public AccessGroup getAccessGroup(String username, char[] password) {

		if (userDBaccess.lookUpstudent(username, password)) {
			return AccessGroup.PUPIL;
		} else if (userDBaccess.lookUpteacher(username, password)) {
			return AccessGroup.TEACHER;
		} else
			return AccessGroup.UNKNOWN;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public ArrayList<String> getCodeModel() {
		return codeList;
	}

	public ArrayList<String> getSaveModel() {
		return saveList;
	}

	public List<String> getProjects() {
		return projectList;
	}

	public Integer getProjectListID() {
		return projectListID;
	}
	public String getProjectName(){
		if(projectListID!=null){
			return projectList.get(projectListID);
		}
		return "";
	}

	public void setProject(Integer projectID) {
		this.projectListID = projectID;
	}

	public String getProjectDescription() {
		// TODO: Projektbeschreibung ergänzen
		if(projectListID!=null)
		{
			return projectList.get(projectListID);
		}
		return "Leider keine Projektbeschreibung voranden";
	}

	public String getProjectCode() {
		if(projectListID!=null){
			return new String(projectCode);
		}
		return "";
	}

	public String[] getProjectCodeArray() {
		String[] parts = projectCode.split("\n");
		if (randomMode) {
			String buffer;
			for (int i = parts.length - 1; i > 0; i--) {
				int randomInt = new java.util.Random().nextInt(i);
				buffer = parts[randomInt];
				parts[randomInt] = parts[i];
				parts[i] = buffer;
			}
		}
		return parts;
	}

	// Speicherung eines Projekts
	public void setProjectCode(String codeString, String projectname,
			int linelength) {
		// TODO linelength maximum an puzzlestücke anpassen
		projectCode = new String(codeString);
		String[] projectArray = projectCode.split("\n");
		try {
			userDBaccess.saveProject(projectArray, projectname, linelength, 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void setAccessGroup(AccessGroup accessGroup) {
		this.accessGroup=accessGroup;		
	}
	public AccessGroup getAccessGroup(){
		return accessGroup;
	}

}
*/

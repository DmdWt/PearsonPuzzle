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

import org.junit.runner.notification.Failure;

import controller.DCCommand;
import model.DB.DBModelTransfer;

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
	private DBModelTransfer dBModelTransfer;
	private AccessGroup accessGroup;
	
	private LinkedList<Failure> jUnitFailures;
	private Vector<HashMap<String, String>> compileFailures;
	private LinkedList<Boolean> groupFailures;

	public Model() {
		jUnitFailures=new LinkedList<Failure>();
		compileFailures = new Vector<HashMap<String,String>>();
		groupFailures = new LinkedList<Boolean>();
		dBModelTransfer = new DBModelTransfer();

		// Datenbankverbindung wird aufgebaut
		
		
		// Holt Daten aus der Datenbank
//		try {
//			dBModelTransfer.resetAll();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.fetchAll();
		
		// Default Werte werden gesetzt
		this.tabSize = 0;
		this.randomMode = true;
		this.grade = 0;
		
	}

	// ------------------------------------ Getter und Setter ------------------------------
	// --- Klassenstufe
	public void setGrade(int grade) {
		if (grade < 14 && grade > 5) {
			this.grade = grade;
		} else {
			// TODO: Fehlerausgabe: Diese Jahrgangsstufe ist nicht klassifiziert
		}
		clearChanged();
	}
	public int getGrade() {
		return grade;
	}

	
	// --- Tabbreite
	public void setTabSize(int tabWidth) {
		this.tabSize = tabWidth;
		setChanged();
		notifyObservers();
		clearChanged();
	}
	public int getTabSize() {
		if(projectID!=null)
			return tabSize;
		return 0;
	}
	public Vector<Integer> getTabVector(){
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
	public void setAccessGroup(AccessGroup accessGroup) {
		this.accessGroup=accessGroup;		
	}
	public AccessGroup getAccessGroup(String username, char[] password) {
		if (dBModelTransfer.lookUpstudent(username, password)) {
			return AccessGroup.PUPIL;
		} else if (dBModelTransfer.lookUpteacher(username, password)) {
			return AccessGroup.TEACHER;
		} else
			return AccessGroup.UNKNOWN;
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
	}
		
	public Integer getProjectListID() {
		return projectID;
	}
			// - Projektname
	public String getProjectName(){
		if(projectID!=null){
			return projectList.get(projectID);
		}
		return new String();
	}
			// - Projektbeschreibung
	public void setProjectDescription(String descriptionString) {
		this.projectDescription=descriptionString;
		setChanged();
	}
	public String getProjectDescription() {
		if(projectID!=null){
		return projectDescription;
		}
		else return null;
	}
			// - Projektcode
	public String getProjectCode() {
		// TODO: Abfrage, ob Benutzergrupe Lehrer
		if(projectID!=null){
			return projectCode;
		}
		else return new String();
	}
	public void setProjectCode(String codeString){
		projectCode=codeString;
		setChanged();
	}
				// Projektvektor
	public Vector<String> getCodeVector() {
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
		notifyObservers();
	}
	public Vector<String> getTestExpressionsVector() {
		return testExpressionsVector;
	}
	public void setTestExpressionsVector(Vector<String> testVector) {
		System.out.println(testVector+"asd");
		this.testExpressionsVector = testVector;
	}

	// Code zum puzzeln
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
	public Vector<String> getSolutionStrings(){
		Vector<String> solution = new Vector<String>(codeMap.size());
		for(Integer index: sortedCode){
			solution.add(codeVector_random.get(index));
		}
		return solution;
	}
	// Wird so gelöst, damit codeMap nicht öffentlich wird (diskutabel)
	public void insertInSollution(int index, String value){
		sortedCode.add(index, codeMap.get(value.trim()));
	}
	public void replaceInSollution(int index, String value){
		sortedCode.remove(index);
		sortedCode.add(index, codeMap.get(value.trim()));
	}
	public void removeInSollution(int index){
		sortedCode.remove(index);
	}
	public boolean testSolution(){
		Boolean result = OrderFailures.testOrder_simple(this, projectCode);
		result = result & OrderFailures.testOrder_groups(sortedCode, groupFailures, codeLine_GroupMatrix, codeMap, codeVector_normal);
		setChanged();
		notifyObservers(DCCommand.TestCode);
		return result;
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
	public void addjUnitFailure(Failure failure) {
		this.jUnitFailures.add(failure);
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
		this.fetchProjects();
		this.fetchProjectCode();
		this.fetchProjectSettings();
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
			if( dBModelTransfer.projectExists(projectName)){
				return false;
			}
		}
		
		// ---- Wenn der Projektname geändert wurde, Projektnamen updaten
		else if(!projectName.equals(projectList.get(projectID))){
			dBModelTransfer.renameProject(projectList.get(projectID), projectName);
		}
		
		// ----- Projekt speichern
		dBModelTransfer.saveProject(projectName, codeString,"","", 0);
		dBModelTransfer.updateDescription(projectName, projectDescription);
		
		// TODO: Test, ob erfolgreich gespeichert wurde
		this.fetchProjects();
		this.selectProject(projectList.indexOf(projectName));
		this.setChanged();
		this.notifyObservers();
		this.clearChanged();
		
		return true;
	}
	
	public void saveProjectSettings(){
		if(projectID!=null)
			dBModelTransfer.saveProjectSettings(projectList.get(projectID), tabSize, grade);
	}

	/**
	 * Löscht das mit <b>projectID</b> selektierte Projekt <br>und löscht es aus der Datenbank <br>
	 * Löschen_erfolgreich@return
	 */
	public boolean removeProject() {
		if(dBModelTransfer.delete(projectList.get(projectID))){
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
				dBModelTransfer.resetAll();
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
		projects = dBModelTransfer.getProjects(grade);
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
				this.projectDescription = dBModelTransfer.getProjectDescription(projectList.get(projectID));
				this.tabSize = dBModelTransfer.getTabSize(projectList.get(projectID));
				}
			catch(SQLException e){
				this.projectDescription="Noch keine Beschreibung vorhanden";
				}
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
			if(projectID!=null){
			try {
				String[] strings = dBModelTransfer.getCodeArray(projectList.get(projectID));
				codeVector_normal = new Vector<String>();
				String pCode= new String();
				for(String string:strings){
					codeVector_normal.add(string);
					pCode=pCode+"\n"+string;
				}
				projectCode=pCode;
				System.out.println(codeVector_normal.toString());
				String[] stringField = dBModelTransfer.getRandomCodeArray(projectList.get(projectID));
				
				this.codeVector_random = new Vector<String>();
				this.testExpressionsVector = new Vector<String>();
				this.codeMap = new LinkedHashMap<String, Integer>();
				this.sortedCode= new LinkedList<Integer>();
				this.codeLine_GroupMatrix = new Vector<Vector<Integer>>();
				sortedCode= new LinkedList<Integer>();
				Vector<Integer> codeLine_Group= new Vector<Integer>();
				for(String line: stringField){
					
					// Dies ist notwendig, damit im Text sort view die Tabs richtig dargestellt werden.
					String tab;
					if(tabSize==0)
						tab="";
					else
						tab=" ";
					for(int i=0;i<tabSize;i++){
						tab=tab+" ";
					}
					String bString = line.replaceAll("\t", tab);
					codeVector_random.add(bString);
					testExpressionsVector.add(new String());
					codeMap.put(line.trim(), codeVector_random.size()-1);
					codeLine_Group.add(new Integer(0));
				}		
				codeLine_GroupMatrix.add(codeLine_Group);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			}
			else{
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
	private dBModelTransfer dBModelTransfer;
	private AccessGroup accessGroup;

	public Model() {
		try {
			dBModelTransfer = new dBModelTransfer();
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
		//	return dBModelTransfer.getProject("DefaultProjekt");
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

		if (dBModelTransfer.lookUpstudent(username, password)) {
			return AccessGroup.PUPIL;
		} else if (dBModelTransfer.lookUpteacher(username, password)) {
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
			dBModelTransfer.saveProject(projectArray, projectname, linelength, 0);
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

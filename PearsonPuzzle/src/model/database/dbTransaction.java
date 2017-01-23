package model.database;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import view.PPException;

import model.Model;
import model.database.UserDBaccess;

/**
 * Klasse Kapselt die Zugrifsslogik auf die Datenbank.
 * @author workspace
 *
 */
public class dbTransaction implements Transaction{
	
	private final static int length_projectName = 36;
	private final static int length_projectDescription = 1024;
	private final static int MAX_line_length_Code = 8000;
	private final static int MIN_line_length_Code = 10;
	 
	private UserDBaccess userDBaccess;
	private Model model;
	
	public dbTransaction(Model model) throws PPException{
		this.model=model;
		try {
			userDBaccess = new UserDBaccess();
			//userDBaccess.resetAll();
		} catch (SQLException e) {
			if(((SQLException) e).getSQLState().equals("XJ040")){ // Failed to start database '<databaseName>', see the next exception for details.
				PPException exception = new PPException(PPException.anotherInstanceIsRunnign);
				throw exception;
			}
			else if(e.getSQLState().equals("XJ004")){ // Database '<databaseName>' not found.
				PPException exception = new PPException(PPException.noDatabaseExists);
				throw exception;
			}
			else if(e.getSQLState().equals("42X05")){ // Table/View '<objectName>' does not exist.
				PPException exception = new PPException(PPException.noDatabaseExists);
				throw exception;
			}
			else{
				e.printStackTrace();
			}
		}

	}
	
	


	
	
	
	
	
	//------------------------------------------------------------------------------------
	//------------------------------- Teacher Student Tabellen ---------------------------
	//------------------------------------------------------------------------------------
		   
	public boolean addUser(String tablename, String username, String password){
		try {
			if(!userDBaccess.doesUserExists(username)){
				userDBaccess.addUser(tablename, username, password);
			}
			else{
				throw new PPException("<html>Nutzername existiert bereits. <br> Es können keine zwei Nutzer mit gleichem Namen angelegt werden.</html>");
			}
		} 
		catch(SQLException e){
			// Von doesUserExist geworfene Exception:
			if(e.getSQLState().equals("XSCB1")	 // Container <containerName> not found.
					|| e.getSQLState().equals("42X05")){ // Table/View <table> does not exist.
				try {
					userDBaccess.recreateTable_Student();
					userDBaccess.recreateTable_Teacher();
					userDBaccess.addUser(tablename, username, password);
				} catch (SQLException e1) {
			   		e1.printStackTrace();
			   		return false;
			   	}
			}
			else{
				e.printStackTrace();
				return false;
			}
		} 
		catch (PPException e) {
			e.printMessage();
			return false;
		}
		return true;
	}
	
	//gibt nur die namen aus der tabelle table zurück
	public Vector<String> getNames(String table){
		 return userDBaccess.getNames(table);
	}
		
	//gibt alle namen von Lehrern und Schülern aus
	public Vector<String> getAllNames(){
		 Vector<String> namevector = userDBaccess.getNames("teachers");
		 namevector.addAll(userDBaccess.getNames("students"));
		 return namevector;
		 
	}
	
	
	 public void deleteUser(String username, String table){
		 try {
			userDBaccess.deleteUser(username, table);
		} catch (SQLException e) {
			if(e.getSQLState().equals("42X05"))
				return;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	
	public boolean lookUpstudent(String name, char[] password){
		String passwordstring = new String(password);
		try {
			return userDBaccess.lookUpstudent(name, passwordstring);
		} catch (SQLException e) {
			if(e.getSQLState().equals("42X05")){
				try {
					throw new PPException(PPException.databaseIsEmpty);
				} 
				catch (PPException e1) {
					e1.handleException(model);
					try{
						return userDBaccess.lookUpstudent(name, passwordstring);
					} 
					catch (SQLException e2) {
						e2.printStackTrace();
					}
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
			return false;
			}
	   }
	
	public boolean lookUpteacher(String name, char[] password){
		String passwordstring = new String(password);
		try {
			return userDBaccess.lookUpteacher(name, passwordstring);
		} catch (SQLException e) {if(e.getSQLState().equals("42X05")){
			try {
				throw new PPException(PPException.databaseIsEmpty);
			} catch (PPException e1) {
				e1.handleException(model);
				try {
					return userDBaccess.lookUpteacher(name, passwordstring);
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
//				throw new PPException(PPException.noDatabaseExists);
//			} catch (PPException e1) {
//				e1.handleException(model);
//				if(e1.getAnswer().equals("Ja")){
//					try {
//						userDBaccess.createTable_Projects();
//						userDBaccess.createTable_Students();
//						userDBaccess.createTable_Teachers();
//						
//					} catch (SQLException e2) {
//						JOptionPane.showMessageDialog(null, "Konnte Datenbank nicht erstellen.", "Ernsthaftes Problem aufgetreten!", JOptionPane.ERROR_MESSAGE, null);
//						e2.printStackTrace();
//					}
//				}
//				else if(e1.getAnswer().equals("Nein"))
//					System.exit(0);
//				else 
//					System.exit(0);
//				
				e1.printStackTrace();
			}
		}
		return false; 
		}
	   }
	
	//-----------------------------------------------------------------------------
	//------------------------------- Projekte Tabellen ---------------------------
	//-----------------------------------------------------------------------------
	
	
	private String unite(String[] codeStrings, boolean random, int tab){
		   if(random){
			   String buffer;
			   for(int i=codeStrings.length-1; i>0;i--){
					int randomInt = new java.util.Random().nextInt(i);
					buffer=codeStrings[randomInt];
					codeStrings[randomInt]=codeStrings[i];
					codeStrings[i]=buffer;
				}
		   }
		   StringBuffer stringBuffer = new StringBuffer();
		   for(String line: codeStrings){
			   if(!line.trim().equals("")){
				   // trim entfernt auch tabs, deshalb while schleife
				   line=line.replaceAll("\t ", "\t");
				   while(line.startsWith(" ")){
					   line=line.replaceFirst(" ", "");
				   }
				   stringBuffer.append(line+"\n");
			   }
		   }
		   return new String(stringBuffer);
	   }
	
	private String createRandomString(int length){
		String Randomname="";
		boolean alreadyexists = true;
		while(alreadyexists){
			Randomname="";
		   for (int j=0; j<length; j++) {
		     Randomname = Randomname + (char) ('a' + 26*Math.random());  // 'a' + (0..23) = ('a' .. 'z')
		   }
		   alreadyexists=userDBaccess.doesRandomnameExist(Randomname);
		}
		return Randomname;
	}
	
//	public Vector<String> getRandomNames(){
//		Vector<String> randomNames = new Vector<String>();
//		try {
//			randomNames = userDBaccess.getRandomNames();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return randomNames;
//	}
	
	private String getRandomName(String projectname){
		String randomString = "";
		try {
			randomString = userDBaccess.getRandomName(projectname);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return randomString;
	}
	
	public void saveProject(String projectname, String codeString, String onlineimports, String localimports,
			String description, int tab) {
		   // TODO: Linelength umdefinieren (wird nicht zwingend benötigt, übergebene Integer kann aber evtl. Verwendung finden
		String[] codeStrings=codeString.split("\n");
		
		//linelength wird automatisch zugewiesen
		int linelength=10;
		for(int i=0;i<codeStrings.length;i++){
			linelength=Math.max(linelength,codeStrings[i].length());
		}
		
		if(linelength < 1){
			   linelength = MIN_line_length_Code;
		   }
		   else if(linelength >8000){
			   linelength = MAX_line_length_Code;
		   }
		   if(projectname==null || codeString==null){
			   return;
		   }
		   else if(projectname.length()==0){
			   return;
		   }
		   else if(projectname.length()>length_projectName){
			   projectname= new String(projectname.substring(0,length_projectName-1));
		   }
		   else if(description.length()>length_projectDescription){
			   description= new String(description.substring(0,length_projectDescription-1));
		   }
		   
		   // -- Code Array wird erzeugt und wieder zu einem String zusammengefasst
		   codeString = unite(codeStrings, false, tab);
		   ArrayList<Integer> randomKey = getRandomKeys(codeStrings.length);
		   try {
			//userDBaccess.saveProject(projectname, codeStrings, imports, description, randomKey, tab, linelength); 13.01.2017
			   userDBaccess.saveProject(projectname, createRandomString(15), codeStrings, description, randomKey,
					   tab, linelength);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void saveJUnitTest(String projectname,String jUnitCode) {
		if(jUnitCode.length()>1000)
			try {
				throw new PPException("JUnit Code ist zu lang und konnte deshalb nicht gespeichert werden!");
			} catch (PPException e) {
				e.printStackTrace();
				return;
			}
		userDBaccess.saveJUnitTest(projectname, jUnitCode);		
	}
	public String getJUnitCode(String projectName) {
		try {
			return userDBaccess.getJUnitCode(projectName);
		} catch (SQLException e) {
			if(e.getSQLState().equals("42X04")){ // Table/View '<objectName>' does not exist.
				return null;
			}
			e.printStackTrace();
			return null;
		}
	}
	public void saveImports(String projectname, HashMap<String, String> importMap){
		
		userDBaccess.saveImports(projectname, importMap.get("online"), importMap.get("classes"), importMap.get("methods"));
	}
	public HashMap<String, String> getImports(String projectname){
		try {
			return userDBaccess.getImports(projectname);
		} catch (SQLException e) {
			if(e.getSQLState().equals("42X04")){ // Kein Eintrag gefunden

				// TODO Auto-generated catch block
				HashMap<String, String> projectImports = new HashMap<String, String>();
				projectImports.put("classes", "");
				projectImports.put("methods", "");
				projectImports.put("online", "");
				return projectImports;
			}
			else 
				e.printStackTrace();
			return null;			
		}
	}
	
	public Vector<Integer> getRandomKeys(String projectname){
		projectname = getRandomName(projectname); //13.1.2017 
		return userDBaccess.getRandomKeys(projectname);
	}
	
	private ArrayList<Integer> getRandomKeys(int number){
		ArrayList<Integer> randomKey = new ArrayList<Integer>();
		int array[] = new int[number];
		for(int j=0;j<number;j++){
			array[j]=j;
		}
		
		//erstellt ein Array mit Randomkeys
		for(int i=number; i>0;i--){
			int randomInt = new java.util.Random().nextInt(i);
			int buffer = array[randomInt];
			array[randomInt]=array[i-1];
			array[i-1]=buffer;
		}
		
		//transformiert das Array in eine ArrayList<Integer>
		for (int index = 0; index < number; index++)
		{
		    randomKey.add(array[index]);
		}
		
		return randomKey;
	}
	
	public void updateDescription(String projectname, String description){
		userDBaccess.updateDescription(projectname, description);
		
	}
	
	public void renameProject(String oldName, String newName){
		userDBaccess.renameProject(oldName, newName);
	}
	
	
//	public void saveProject(String[] codeString, String projectname, int linelength, final int projectID) throws SQLException{
//		   projectname=new String(projectname.trim());
//		   if(linelength < 1){
//			   linelength =MIN_line_length_Code;
//		   }
//		   else if(linelength >8000){
//			   linelength = MAX_line_length_Code;
//		   }
//		   // leerer Projektname ist unzulässig
//		   if(projectname.equals("")){
//			   return;
//		   }
//		   
//		   
//		   // Zeilen zufällig anordnen
//		   String buffer;
//		   String[] mixedcodeString=codeString;
//			for(int i=mixedcodeString.length-1; i>0;i--){
//				int randomInt = new java.util.Random().nextInt(i);
//				buffer=mixedcodeString[randomInt];
//				mixedcodeString[randomInt]=mixedcodeString[i];
//				mixedcodeString[i]=buffer;
//			}
//			userDBaccess.saveProject(codeString, mixedcodeString, projectname, linelength, projectID);
//			
//			//-----------------------------testing------------------------------------
//		   
//	   }
	
	public void saveProjectSettings(String projectname, int tabSize, int grade){
		if(projectname.equals("")){
			   return;
		   }
		userDBaccess.saveProjectSettings(projectname, tabSize, grade);
	}
	
	
	
//	
//	public String[] getRandomCodeArray(String projectname) throws SQLException{  
//		   String[] codeStrings=userDBaccess.getCodeArray(projectname);  
//		   String[] randomCodeStrings = new String[codeStrings.length];
//		   ArrayList<Integer> randomKeys = userDBaccess.getRandomKeys(projectname);
//		   for(int i=0;i<randomKeys.size();i++){
//			   randomCodeStrings[i]=codeStrings[randomKeys.get(i)];
//		   }
//		   return randomCodeStrings;
//	   }
	
	public ArrayList <String> getCodeList(String projectname) throws SQLException{
			projectname = getRandomName(projectname); //13.1.2017 
		   
		   return userDBaccess.getCodeList(projectname);
	   }
	public String getCode(String projectname){
		projectname = getRandomName(projectname); //13.1.2017 
		try{
			return userDBaccess.getCode(projectname);
		} catch(SQLException e){
			if(e.getSQLState().equals("42X05")){ // Table/View '<objectName>' does not exist.
				try {
					throw new PPException("Dieses Projekt enthält keinen Inhalt");
				} catch (PPException e1) {} 
			}
			e.printStackTrace();
			return new String();
		}
	}
	
	public Vector<Vector<Integer>> getOrdervektor(String projectname){
		projectname = getRandomName(projectname); //13.1.2017 
		Vector<Vector<Integer>> ordervector = new Vector<Vector<Integer>>();
		//solange order nicht leer ist wird ordervector befüllt
		for(int ordernumber=0;!userDBaccess.getOrder(projectname, ordernumber).isEmpty();ordernumber++){
			ordervector.add(userDBaccess.getOrder(projectname, ordernumber));
		}
		return ordervector;
	}
	public void saveOrder(String projectname, Vector<Vector<Integer>> orderMatrix){
		projectname = getRandomName(projectname); //13.1.2017 
		deleteAllOrders(projectname);
		for(Vector<Integer> orderVector : orderMatrix){
			userDBaccess.addOrder(projectname, orderVector);
		}
	}
//	
//	private void addOrder(String projectname, Vector<Vector<Integer>> newOrdervektor){
//		for(int i = 0;i<newOrdervektor.size();i++){
//			userDBaccess.addOrder(projectname, newOrdervektor.get(i));
//		}	
//		}
//	
	@SuppressWarnings("unused")
	private boolean deleteOrder(String randomName, final int ordernumber){
		   return userDBaccess.deleteOrder(randomName, ordernumber);
	 }
	
	private boolean deleteAllOrders(String randomName){
		int i = 0;
		boolean success = true;
		   while(success){
			   success = userDBaccess.deleteOrder(randomName, i);
			   i++;
		   }
		   return !success;
	   }
	//-------------------------------------------------------------------------------------
	//---------------------------------- Tabelle mit Projekten ----------------------------
	//-------------------------------------------------------------------------------------
	 public ArrayList <String> getProjects(int grade) {
		 try {
			return userDBaccess.getProjects(grade);
		
		 } catch (SQLException e) {
			try {
				if(e.getSQLState().equals("42X05")){
					// Zentrale Stelle für Erstbenutzung
					PPException pEx = new PPException(PPException.databaseIsEmpty);
					pEx.handleException(model);
					userDBaccess.recreateTable_Projects();
					return userDBaccess.getProjects(grade);
				}
				else{
					e.printStackTrace();
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return new ArrayList<String>();
		 }
	 }
	
	 public String getProjectDescription(String projectname) throws SQLException{
		 
		 return userDBaccess.getProjectDescription(projectname);
	 }
	 
	 public int getTabSize(String projectname) {
		 
		 return userDBaccess.getTabSize(projectname);
	 }
	 
	 private void createTable_Projects() throws SQLException{
		 userDBaccess.recreateTable_Projects();	 }
			
	 private void createTable_Students() throws SQLException{
		 userDBaccess.recreateTable_Student();
	 }
	 
	 private void createTable_Teachers() throws SQLException{
		 userDBaccess.recreateTable_Teacher();
	 }
	 
	 public void resetAll() throws SQLException{
		 createTable_Projects();
		 createTable_Students();
		 createTable_Teachers();
	 }
	 
	 public boolean delete(String projectname) {
		 return userDBaccess.delete(projectname);
	 }
	 
	 public boolean projectExists(String projectName) {
		 return userDBaccess.projectExists(projectName);
	 }
	 
	public Integer getPuzzleMode(String projectName) {
		try {
			if(userDBaccess.getPuzzleMode(projectName)== null)
				return null;
			return Integer.parseInt(userDBaccess.getPuzzleMode(projectName));
		} catch (SQLException e) {
			if(e.getSQLState().equals("42X04"))
				return null;
			else
				e.printStackTrace();
		}
		return null;
	}
	public void savePuzzlemode(String projectname, int puzzlemode) {
		userDBaccess.savePuzzlemode(projectname, puzzlemode);
	}



	public void saveOrderFailure(String projectname,
			LinkedList<String> orderFailureText) {
		String randomname = getRandomName(projectname);
		
		String failurname = randomname+"orderfailure";
		for(int index=0;index<orderFailureText.size();index++){
		userDBaccess.addOrderfailurMassage(failurname, index, orderFailureText.get(index));
		}
	}

	public boolean updateOrderFailure(String projectname, int ordernumber, String orderFailureText){
		String randomname = getRandomName(projectname);
		String failurname = randomname+"orderfailure";
		return userDBaccess.updateOrderfailurMassage(failurname, ordernumber, orderFailureText);
	}
	
	public String getOrderFailure(String projectname, int ordernumber){
		String randomname = getRandomName(projectname);
		String failurname = randomname+"orderfailure";
		return userDBaccess.getOrderFailurMassage(failurname, ordernumber);
	}
	
	public LinkedList<String> getOrderFailure(String projectname){
		LinkedList<String> OrderFailureText = new LinkedList<String>();
		String randomname = getRandomName(projectname);
		String failurname = randomname+"orderfailure";
		for(int ordernumber= 0; userDBaccess.doesOrderExist(randomname, ordernumber);ordernumber++){
		OrderFailureText.add(getOrderFailure(projectname, ordernumber));
		}
		return OrderFailureText;
	}
}

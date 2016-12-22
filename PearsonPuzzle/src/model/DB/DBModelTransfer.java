package model.DB;



import java.sql.SQLException;
import java.util.ArrayList;

import model.DB.UserDBaccess;

public class DBModelTransfer {
	
	private final static int length_projectName = 36;
	private final static int length_projectDescription = 1024;
	private final static int MAX_line_length_Code = 8000;
	private final static int MIN_line_length_Code = 10;
	 
	private UserDBaccess userDBaccess;
	
	public DBModelTransfer(){
		try {
			userDBaccess = new UserDBaccess();
			//userDBaccess.resetAll();
		} catch (SQLException e) {
			if(e.getSQLState().equals("XJ040"))
				// TODO Ausgabe, dass bereits eine Programminstanz gestartet wurde
				System.out.println("Nichts wie raus hier!!!!\n");
			else
				e.printStackTrace();
			}
	}
	public boolean lookUpstudent(String name, char[] password){
		String passwordstring = new String(password);
		return userDBaccess.lookUpstudent(name, passwordstring);
	   }
	
	public boolean lookUpteacher(String name, char[] password){
		String passwordstring = new String(password);
		return userDBaccess.lookUpteacher(name, passwordstring);
	   }
	
	
	
	
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
	
	
	
	public void saveProject(String projectname, String codeString, String imports,
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
		   else if(projectname.length()>length_projectName){
			   projectname= new String(projectname.substring(0,length_projectName-1));
		   }
		   else if(description.length()>length_projectDescription){
			   description= new String(description.substring(0,length_projectDescription-1));
		   }
		   else if(projectname.length()==0){
			   return;
		   }
		   
		   // -- Code Array wird erzeugt und wieder zu einem String zusammengefasst
		   codeString = unite(codeStrings, false, tab);
		   ArrayList<Integer> randomKey = getRandomKeys(codeStrings.length);
		   try {
			userDBaccess.saveProject(projectname, codeStrings, imports, description, randomKey, tab, linelength);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   	}
	
	
	public ArrayList<Integer> getRandomKeys(int number){
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
	
	
	public String[] getCodeArray(String projectname) throws SQLException{
		   
		   return userDBaccess.getCodeArray(projectname);  
	   }
	
	
	public String[] getRandomCodeArray(String projectname) throws SQLException{  
		   String[] codeStrings=userDBaccess.getCodeArray(projectname);  
		   String[] randomCodeStrings = new String[codeStrings.length];
		   ArrayList<Integer> randomKeys = userDBaccess.getRandomKeys(projectname);
		   for(int i=0;i<randomKeys.size();i++){
			   randomCodeStrings[i]=codeStrings[randomKeys.get(i)];
		   }
		   return randomCodeStrings;
	   }
	
	public ArrayList <String> getCodeList(String projectname) throws SQLException{
		   
		   return userDBaccess.getCodeList(projectname);
	   }
	
	 public ArrayList <String> getProjects(int grade) {
		 return userDBaccess.getProjects(grade);
	 }
	
	 public String getProjectDescription(String projectname) throws SQLException{
		 
		 return userDBaccess.getProjectDescription(projectname);
	 }
	 
	 public int getTabSize(String projectname) {
		 
		 return userDBaccess.getTabSize(projectname);
	 }
	 
	 private void createTable_Projects() throws SQLException{
		 userDBaccess.createTable_Projects();
		 saveProject(
					"HalloWorld", 
					"public static void main(String args[]){ \n\t System.out.println(\"hallo world\")\n }" , 
					"",
					"",
					0);
	 }
			
	 private void createTable_Students() throws SQLException{
		 userDBaccess.createTable_Students();
	 }
	 
	 private void createTable_Teachers() throws SQLException{
		 userDBaccess.createTable_Teachers();
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
}

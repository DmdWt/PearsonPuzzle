package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class UserDBaccess {

	 Connection conn;
	 private final static int length_projectName = 36;
	 private final static int length_projectDescription = 1024;
	 private final static int MAX_line_length_Code = 8000;
	 private final static int MIN_line_length_Code = 10;
	 public UserDBaccess() throws SQLException {
		String dbUrl = "jdbc:derby:database;create=true";
		conn = DriverManager.getConnection(dbUrl);
		//derby.ui.codeset
		this.normalDbUsage();
		}
	 
	 public void connectionToDerby() {
	     // -------------------------------------------
	     // URL format is
	     // jdbc:derby:<local directory to save data>
	     // -------------------------------------------  
	 }
	 
	 public static int getLength_ProjectName(){
		 return length_projectName;
	 }
	 
	   
	   
	   // ------------------------ Nutzername und Passwortvergleich -------------------------------
	   
	   
	  
	   
	   // -----  Nutzername und Passwort Vergleich Schüler
	   public boolean lookUpstudent(String name, char[] password){
		   try{	  
			   Statement stmt = conn.createStatement();
			   String passwordstring = new String(password);
			   ResultSet rs = stmt.executeQuery("SELECT * FROM students WHERE username= '" + name + "' AND password = '"+passwordstring+"'");	
			   if(!rs.next() ){
			    	 return false;
			   }
			   else{
			   	 	return true;
			   }		     
		   }
		   catch(Exception e){
			   return false;
		   }
	   }
	   
	   // ----- Nutzername und Passwort Vergleich Lehrer
	   public boolean lookUpteacher(String name, char[] password) {
		   try{
			   String passwordstring = new String(password);
			   Statement stmt = conn.createStatement();
			   ResultSet te = stmt.executeQuery("SELECT * FROM teachers WHERE username = '" + name + "' AND password = '"+passwordstring+"'");	
			     if(!te.next() ){
			    	return false;}
			     else{
			    	return true;}
		   } catch(Exception e){
			   		return false;}
	   }


	   private void normalDbUsage() throws SQLException {
		   Statement stmt = conn.createStatement();

		   /*Student*/
		   ResultSet rsS;
		   try{
			   // query
			   rsS = stmt.executeQuery("SELECT * FROM students");
		   }
		   catch(SQLException e){
			   // Hier wird abgehandelt, wenn was schief läuft
			   // bzw. wenn die Tabelle (noch) nicht existiert 
			   try {
				   stmt.executeUpdate("Drop Table students");
			   } 
			   catch (SQLException ex) { // schmeißt keine Exception, da nur der Fall, dass bereits eine Tabelle existiert, abgefangen werden soll
			   }
			   // create table 
			   this.createTable_Students();
			   rsS = stmt.executeQuery("SELECT * FROM students");
		   }
		   // print out query result
		   while (rsS.next()) { 
			   //System.out.printf("%d\t%s\t%s\n", rs.getInt("id"), rs.getString("username"), rs.getString("password"));
		   }
	       
		   /*Teacher*/
		   ResultSet rsT;
		   try{
			   rsT = stmt.executeQuery("SELECT * FROM teachers");
		   }
		   catch(SQLException e){
			   // Hier wird abgehandelt, wenn was schief läuft
			   // bzw. wenn die Tabelle (noch) nicht existiert 
			   try {
				   stmt.executeUpdate("Drop Table teachers");
			   } 
			   catch (SQLException ex){ // Schmeißt keine Exception, da nur der Fall, dass keine Tabelle existiert, abgefangen werden soll.
			   }   
			   this.createTable_Teachers();
			   // query
			   rsT = stmt.executeQuery("SELECT * FROM teachers");
		   }
		   // print out query result
		   while (rsT.next()) { 
			   //System.out.printf("%d\t%s\t%s\n", rs.getInt("id"), rs.getString("username"), rs.getString("password"));			   
		   }
	   }
	   
	   
	 /**
	 * Speichert das übergebene Projekt in der Datenbank.<br>
	 * <b>Leere Zeilen werden beim Apspeichern gelöscht.</b>
	 * 
	 * @param projectname
	 * @param codeString
	 * @param linelength
	 * @param tab
	 */
	   
	   public void saveProject(String projectname, String codeString, int linelength, int tab) {
		   // TODO: Linelength umdefinieren (wird nicht zwingend benötigt, übergebene Integer kann aber evtl. Verwendung finden
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
		   else if(projectname.length()==0){
			   return;
		   }
		   
		   // -- Code Array wird erzeugt und wieder zu einem String zusammengefasst
		   String[] codeStrings=codeString.split("\n");
		   codeString = unite(codeStrings, false, tab);
		   String randomCode=unite(codeStrings, true, tab);
		   
		   try{
			   Statement stmt = conn.createStatement();
			   stmt.execute("INSERT INTO Projects VALUES ('"+projectname+"', '"+codeString+"','"+randomCode.toString()+"','',0)");
		   }
		   catch(SQLException e){
			   // Falls der Eintrag schon existiert.
			   if(e.getSQLState().equals("23505")){
				   try{
					   Statement stmt = conn.createStatement();
					   stmt.execute("UPDATE Projects SET Code='"+codeString+"', randomCode='"+randomCode.toString()+"' WHERE pName='"+projectname+"'");
				   }
				   catch(SQLException e1){
					   e1.printStackTrace(); 
					   //XXX: Auto generated Method stub 
					   }
				   return;
				   }
			   e.printStackTrace();
		   }
	   }
	   
	   /**
	    * Aus dem String[] wird wieder ein String. Dieser wird entweder zufällig zusammengesetzt <br>
	    * oder aber so wie er im String[] abgespeichert ist. Leerzeichen am Anfang, Leerzeichen <br>
	    * nach Tabs sowie leere Zeilen werden vor dem Zusammensetzten entfernt-
	    * <br><br>
	    * Array mit Strings @param codeStrings<br>
	    * Zufällige Zusammensetung @param random<br>
	    * Tabbreite @param tab<br>
	    * Zusammengesetzter String @return<br>
	    */
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
	   
	   public void updateDescription(String projectname, String description){
		   try{
			   Statement stmt = conn.createStatement();
			   stmt.execute("UPDATE Projects SET Description='"+description+"' WHERE pName='"+projectname+"'");
		   }
		   catch(SQLException e1){
			   e1.printStackTrace();
			   //TODO: Auto generated Method stub 
			   }
	   }
	   
	   /**
	    * Projekt umbenennen (wenn der Projektname geändert werden soll)<br>
	    * Alter Projektname@param oldName
	    * Neuer Projektname@param newName
	    */
	   public void renameProject(String oldName, String newName){
		   Statement stmt;
		   try {
			   stmt = conn.createStatement();
			   stmt.execute("UPDATE Projects SET pName='"+newName+"' WHERE pName='"+oldName+"'");
		   } catch (SQLException e) {
			   // TODO Auto-generated catch block
			   e.printStackTrace();
		   }		   
	   }
	
	   /**
	    * Speichert das übergebene Projekt 
	    * #depricated
	    * 
	    * @param codeString
	    * @param projectname
	    * @param linelength
	    * @param projectID
	    * @throws SQLException
	    */
	   public void saveProject(String[] codeString, String projectname, int linelength, final int projectID) throws SQLException{
		   projectname=new String(projectname.trim());
		   if(linelength < 1){
			   linelength =MIN_line_length_Code;
		   }
		   else if(linelength >8000){
			   linelength = MAX_line_length_Code;
		   }
		   // leerer Projektname ist unzulässig
		   if(projectname.equals("")){
			   return;
		   }
		   Statement stmt = conn.createStatement();
		   
		   //XXX doppelte Tabellennamen besser handeln
		   
		   // ----- Speichert Projektnamen Tabelle
		   try{
			   stmt.executeUpdate("CREATE TABLE Projects ( Projectname varchar("+length_projectName+") UNIQUE)");
		   }
		   catch(Exception e){ //Tabelle existiert bereits
			   }
		   
		   try{stmt.execute("INSERT INTO Projects VALUES ('"+projectname+"')");
		   }
		   catch(Exception e){	//Projektname bereits vorhanden
		   }
		   
		   // ----- Speichert normalen Code
		   try{
			   stmt.executeUpdate("DROP TABLE "+projectname);
		   }
		   catch(Exception e){ //falls Tabelle noch nicht existiert
		   }
		   stmt.executeUpdate("CREATE TABLE "+projectname+" (linenumber int primary key, codeline varchar("+linelength+"))");
		   for(int i=0 ; i<codeString.length ; i++){
				 stmt.executeUpdate("insert into "+projectname+" values ("+i+",'"+codeString[i]+"')");
		   }
		   
		   // Zeilen zufällig anordnen
		   String buffer;
			for(int i=codeString.length-1; i>0;i--){
				int randomInt = new java.util.Random().nextInt(i);
				buffer=codeString[randomInt];
				codeString[randomInt]=codeString[i];
				codeString[i]=buffer;
			}
			// Speichert zufällig angeordnetetn Code
			try{
				stmt.executeUpdate("DROP TABLE "+projectname+"_mixed");
			}
			catch(Exception e){ // Falls Tabelle noch nicht existiert
			}
			stmt.executeUpdate("CREATE TABLE "+projectname+"_mixed (linenumber int primary key, codeline varchar("+linelength+"))");	
			for(int i=0 ; i<codeString.length ; i++){
				 stmt.executeUpdate("insert into "+projectname+"_mixed values ("+i+",'"+codeString[i]+"')");
			}
			
			//-----------------------------testing------------------------------------
		   
	   }
	   public void saveProjectSettings(String projectname, int tabSize, int grade) {
		   if(projectname.equals("")){
			   return;
		   }
		   try {
			   Statement stmt = conn.createStatement();
			   stmt.executeUpdate("UPDATE Projects SET tabSize="+tabSize+" WHERE pName='"+projectname+"'");
		   		} catch (SQLException e) {
		   			// XXX: Auto-generated catch block
		   			e.printStackTrace();
		   		}
		}
	   
	   
	   //-------------------------- Code aus Datenbank auslesen -------------------------------	   
	   
	   public String getCode(String projectname) throws SQLException{
		   
		   Statement stmt = conn.createStatement();
		   ResultSet te = stmt.executeQuery("SELECT Code FROM Projects WHERE pName='"+projectname+"'");
		   te.next();
		   //System.out.println(te.getString("Code"));
		   return te.getString("Code");  
	   }
	   public ArrayList <String> getCodeList(String projectname) throws SQLException{
		   Statement stmt = conn.createStatement();
		   
		   ResultSet te = stmt.executeQuery("SELECT codeline FROM "+projectname);
		   
		   ArrayList<String> codeliste = new ArrayList<String>();
		   
		   while (te.next()) { 
			   codeliste.add(te.getString("codeline"));
			     }
		   // String[] codeString;
		   // codeString= codeliste.toArray(new String[0]);
		   // return codeString;
		   return codeliste;
	   }
	   
	   /**
	    * Holt sich eine Projektliste aus der Datenbank.<br>
	    * Es könnten Projekte einer bestimmten Klassenstufe <br>
	    * abgefragt werden (müssen aber nicht).
	    * ProjektListe@return
	    */
	   public ArrayList <String> getProjects(int grade) {
		   Statement stmt;
		   ResultSet te;
		   try {
			   stmt = conn.createStatement();
			   te = stmt.executeQuery("SELECT pName FROM Projects");
			   ArrayList<String> projectList = new ArrayList<String>();
			   while (te.next()) { 
				   projectList.add(te.getString("pName"));
				     }
			   return projectList;
			   
		   } catch (SQLException e1) {
			   try{
				   this.createTable_Projects();
				   return this.getProjects(grade);
			   } catch(SQLException e2){
				   e2.printStackTrace();
			   }
			   return null;
		   // String[] codeString;
		   // codeString= codeliste.toArray(new String[0]);
		   // return codeString;
		   }
	   	}
	   
	   /**
	    * Liefert zum übergenenen Projektnamen die Projektbeschreibung.
	    * Projektname@param projectname
	    * Beschreibung@return
	    * @throws SQLException
	    */
	   public String getProjectDescription(String projectname) throws SQLException{
		   Statement stmt;
		   ResultSet te;
		   stmt = conn.createStatement();
		   te = stmt.executeQuery("SELECT Description FROM Projects WHERE pName='"+projectname+"'");
		   te.next();
		   return te.getString("Description");
	   }
	   public int getTabSize(String projectname) {
		   Statement stmt;
		   ResultSet te;
		   try {
			stmt = conn.createStatement();
			te = stmt.executeQuery("SELECT tabsize FROM Projects WHERE pName='"+projectname+"'");
			   te.next();
			   return te.getInt("tabsize");
		   } catch (SQLException e) {
			   // TODO Auto-generated catch block
				e.printStackTrace();
			}
		   return 0;
		   
		}
	   private void createTable_Projects() throws SQLException{
		   Statement stmt = conn.createStatement();
		   // TODO: Allert soll erfolgen, der abfrägt, ob dies das erst Projekt ist, das man erstellt. Erst bei ja soll DROP TABLE erfolgen (sonst werden eventuell vorhandenen DAten verworfen)
		   try{stmt.executeUpdate("DROP TABLE Projects");}
		   catch(SQLException e){}
		   stmt.executeUpdate("CREATE TABLE Projects ( " +
		   		"pName varchar("+length_projectName+") UNIQUE, " +
		   		"Code LONG VARCHAR, " +
		   		"randomCode LONG VARCHAR, " +
		   		"Description varchar("+length_projectDescription+"), " +
		   		"tabSize INT)");
		   saveProject(
				"TestProjekt", 
				"Beispielcode 1 \nBeispielcode2\n\tBeispielcode3\n \t \t \t \t \t\t\t\t\t\t\t\t \tLanger" , 
				0,
				0);
	   }
	   
	   
	   /**
	    * Schülertabelle wird verworfen, falls vorhanden und neu kreiert.<br>
	    * <b>!!!ACHTUNG!!!</b> Löscht eventuell vorhanenden Daten!
	    * @throws SQLException
	    */
	   private void createTable_Students() throws SQLException{
		   Statement stmt = conn.createStatement();
		   try{stmt.executeUpdate("DROP TABLE students");}
		   catch(SQLException e){}
		   stmt.executeUpdate("Create table students (" +
		   		"id int primary key, " +
		   		"username varchar(30), " +
		   		"password varchar(8) NOT NULL default 'student')");  
		   // insert 2 rows
		   stmt.executeUpdate("insert into students values (1,'tom','tom')");
		   stmt.executeUpdate("insert into students values (2,'peter','peter')");
	   }
	   
	   /**
	    * Lehrertabelle wird verworfen, falls vorhanden und neu kreiert.<br>
	    * <b>!!!ACHTUNG!!!</b> Löscht eventuell vorhanenden Daten!
	    * @throws SQLException
	    */
	   private void createTable_Teachers() throws SQLException{
		   Statement stmt = conn.createStatement();
		   try{stmt.executeUpdate("DROP TABLE teachers");}
		   catch(SQLException e){}
		   stmt.executeUpdate("CREATE TABLE teachers (" +
		   		"id int primary key, " +
		   		"username varchar(30), " +
		   		"password varchar(8)  NOT NULL default 'teacher')");
		   stmt.executeUpdate("insert into teachers values (1,'Herr','Herr')");
		   stmt.executeUpdate("insert into teachers values (2,'Frau','Frau')");
		   stmt.executeUpdate("insert into teachers values (3,'TUM','TUM')");
	   }
	   
	   /**
	    * <b>!!!ACHTUNG!!!</b> Löscht ALLE vorhanenden Daten!
	    * @throws SQLException
	    */
	   public void resetAll() throws SQLException{
		   createTable_Projects();
		   createTable_Students();
		   createTable_Teachers();
	   }

	   /**
	    * Löscht das angegebene Projekt.
	    * @param projectname
	    * @return
	    */
	   public boolean delete(String projectname) {
		   try{
			   Statement stmt = conn.createStatement();
			   stmt.executeUpdate("DELETE FROM Projects WHERE pName = '"+projectname+"'");
			   return true;
		   } catch(SQLException e){
			   e.printStackTrace();
			   return false;
		   }
	   }

	   /**
	    * Gibt Aufschluss, ob ein Projekt mit diesem Namen existiert. 
	    * @param projectName
	    * @return
	    */
	   public boolean projectExists(String projectName) {
			try{
				Statement stmt = conn.createStatement();
				ResultSet rs=stmt.executeQuery("SELECT * FROM Projects WHERE pName='"+projectName+"'");
				rs.next();
				if(rs.getString("pName").equals("")){
					return false;
				}
			}
			catch(SQLException e){
				if(e.getSQLState().equals("24000"))
					return false;
				else
					e.printStackTrace();
			}
			return true;
		}
	}
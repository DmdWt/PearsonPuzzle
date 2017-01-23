package modelTest;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import model.database.UserDBaccess;

import org.junit.*;

import view.PPException;

public class DBAccess {

	UserDBaccess db;
	// TODO: null testen
	private final static String normalString ="r2d2r2d2r2d2r2d";
	private final static String emptyString="";
	private final static String spaceString="     ";
	private final static String noString=null;
	private final static String tabString= "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
	private final static String newLines = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
	private final static String fancyString = "äöü!(){}"; //getFancyString();
	private ArrayList <String> stringList;
		
	@Before
	public void initModel(){
		stringList = new ArrayList<String>();
		stringList.add(normalString);
		stringList.add(emptyString);
		stringList.add(spaceString);
		stringList.add(tabString);
		stringList.add(newLines);
		stringList.add(fancyString);
		stringList.add(noString);
		try {
			db = new UserDBaccess();
		} catch (SQLException e) {
			fail("Problem beim Erstellen oder Öffnen der Datenbank");
		} 
	}
	
	@After
	public void clear(){
		for(String projectName : stringList){
			if(projectName!= null && projectName.length()>UserDBaccess.getLength_ProjectName()){
				projectName = new String(projectName.substring(0, UserDBaccess.getLength_ProjectName()-1));
			}
			db.delete(projectName);
		}
	}
	
	@Test void saveProject_Test2() throws SQLException{
		db.saveProject(normalString,"", normalString.split("\n"),"",  new ArrayList<Integer>(), 100, 0);
	}
	
	@Test
	public void saveProject_Test() throws SQLException {
		for(String projectName : stringList){
			for(String codeString : stringList){
				// ---- Projekt wird gespeichert.
				db.saveProject(projectName, "",codeString.split("\n"),"",new ArrayList<Integer>(),100, 0);
				
				try{
					// --- Projektname wird auf richtige Länge gebracht
				if(projectName!= null && projectName.length()>UserDBaccess.getLength_ProjectName())
					projectName = new String(projectName.substring(0, UserDBaccess.getLength_ProjectName()-1));
				
					// ---- 
				if(codeString==null)
					//	NotEqual, weil dann nichts gespeichert wurde (und noch der Code aus dem letzten Schleifendurchlauf abgerufen wird)
					assertNotEquals(null, db.getCode(projectName));
				else if(codeString.equals(newLines))
					assertEquals("", db.getCode(projectName));
				else
					assertEquals(codeString, db.getCode(projectName));
				}catch(SQLException e){
					// Manche Projektnamen sind nicht zulässig und werfen dann beim get eine Exception (weil kein zugehöriger Datensatz vorhanden ist)
					if(projectName==null)
						continue;
					if(projectName.equals(emptyString) || projectName.equals(spaceString) && e.equals("24000"))
						//	SQL Error: Invalid cursor state - no current row. (
						continue;
					e.printStackTrace();
					fail("Problem beim vergleichen von "+codeString+"... in "+projectName+"...");
				}
			}
		}	
	}
	/*
	 
	 private void saveProject(final String projectName, final String codeString, final int linelength){
		// TODO: Testfehler sauber definieren
		db.saveProject(projectName, codeString, linelength, 0);
		try {
			assertEquals(db.getCode(projectName),codeString);
		} catch (SQLException e) {
			fail("Problem nicht spezifiziert");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test 
	public void multiThread(){
		try {
			UserDBaccess database = new UserDBaccess();
			database.connectionToDerby();
		} catch (SQLException e) {
			fail("Keine zwei Verbingunen möglich");
			e.printStackTrace();
		}
	}
	@Test
	public void test_Normal() {
		assertTrue(isEqual(normalString, "2", normalString,150));	
	}
	@Test
	public void test_Normal_noSplit() {
		assertTrue(isEqual(normalString, "", normalString,150));	
	}
	@Test
	public void test_Normal_Empty() {
		assertTrue(isEqual(normalString, "2", emptyString,150));
	}
	@Test
	public void test_Empty_Normal() {
		assertTrue(isEqual(emptyString,normalString,"2",150));
	}
	
	@Test//(expected=IllegalArgumentException.class)
	public void test_MIN() {
		assertTrue(isEqual(normalString,"2",normalString,Integer.MIN_VALUE));	
	}
	
	@Test//(expected=IllegalArgumentException.class)
	public void test_Empty_Empty_MIN() {
		assertTrue(isEqual(emptyString,"",emptyString,Integer.MIN_VALUE));	
	}
	
	@Test//(expected=IllegalArgumentException.class)
	public void test_MAX() {
		assertTrue(isEqual(normalString, "", normalString, Integer.MAX_VALUE));			
	}
	
	@Test//(expected=IllegalArgumentException.class)
	public void test_Tab_Tab() {
		assertTrue(isEqual(tabString, "", tabString, 150));
	}
	@Test//(expected=IllegalArgumentException.class)
	public void test_NewLine_NewLine() {
		assertTrue(isEqual(newLines, "", newLines, 150));
	}
	
	@Test
	public void test_Fancy() {
		assertTrue(isEqual(fancyString, "2", fancyString, 150));
	}
	@Test
	public void test_Normal_Fancy() {
		assertTrue(isEqual(normalString, "2", fancyString, 150));
	}
	@Test
	public void test_Fancy_Normal() {
		assertTrue(isEqual(fancyString, "", normalString, 150));
	}
	*/
	/**
	 * Methode, um Eingabe und Ausgabe der Datenbank auf Gleichheit zu prüfen.
	 * @param code
	 * @param split_String
	 * @param projectname
	 * @param linelength
	 * @return
	 */
	private boolean isEqual(final String code, final String split_String, final String projectname, final int linelength){
		try {
			db.saveProject(projectname, "",code.split(split_String),"", new ArrayList<Integer>(), linelength, 0);
		} catch (SQLException e) {
			fail("Problem beim Speichern");
			e.printStackTrace();
			}
		try {
			String[] split=code.split(split_String);
			// FIXME muss geändert werden (getCodeList is depricated)
			ArrayList<String> dbResult = db.getCodeList(projectname);
			assertEquals(split.length, dbResult.size());
			for(int i=0; i<split.length; i++){
				assertEquals(split[i],dbResult.get(i));
			}
		} catch (SQLException e) {

			fail("Problem beim Holen der Daten");
			e.printStackTrace();
			}
		return true;
	}
	
	
	
	/**
	 * String mit allen möglichen ASCI Zeichen @return
	 */
	private static String getFancyString(){
		StringBuffer buf = new StringBuffer();
		for(int i=32; i!=127 && i<256;i++){
			if(i!=40 && i!=41)
			buf.append((char)i);
		}
		String ret=new String(buf);
		return ret;		
	}
	
	/**
	 * String mit zufälligen ASCI Zeichen @return
	 */
	private static String getRandomString(){
		StringBuffer buf = new StringBuffer();
		Random rand = new Random();
		for(int i=0; i<256 ;i++){
			int n=rand.nextInt(256);
			if(n>=32 && n!=127)
				buf.append((char)n);
		}
		String ret=new String(buf);
		return ret;
	}
	
	
	
	
}

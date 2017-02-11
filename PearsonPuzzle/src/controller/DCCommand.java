package controller;

/**
 * Definiert globale Kommandos für ActionEvents u.Ä.<br>
 * (über deren lokale Wohldefiniertheit der Controller entscheidet)
 * @author workspace
 *
 */
public enum DCCommand {
	// Text Component Connection
		ConnectedComponent,
	// Button Commands
		ShowHelp, Save, ResetDB, SetConfig, DeleteProject, AddOrder, Compile, TestCode, SaveProjectConfiguration,
	// View Changes
		EditConfig,EditProject, EditUsers, EditJUnit, EditPreview, NewProject, OpenProject, ProjectList, Login, Logout, Admin,
	// Dialogs
		AddUser, AddClasses, AddMethods, EditOrderGroup, DB_Import, DB_Export, Applet, Test, DeleteOrder;
	
	@Override
	public String toString(){
		switch(this){
		case AddUser:
			return "AddUser";
		case AddClasses:
			return "AddClasses";
		case AddMethods:
			return "AddMethods";
		case Applet: 
			return "Applet";
		case EditUsers:
			return "EditUsers";
		case EditProject:
			return "EditProject";
		case EditPreview: 
			return "EditPreview";
		case EditOrderGroup:
			return "EditOrderGroup";
		case NewProject:
			return "NewProject";
		case OpenProject:
			return "OpenProject";
		case ProjectList:
			return "ProjectList";
		case Login:
			return "Login";
		case Logout:
			return "Logout";
		case DeleteProject:
			return "DeleteProject";
		case DeleteOrder:
			return "DeleteOrder";
		case Admin:
			return "Admin";
		case ResetDB: 
			return "ResetDB";
		case SetConfig:
			return "SetConfig";
		case ShowHelp:
			return "ShowHelp";
		case EditConfig:
			return "EditConfig";
		case ConnectedComponent:
			return "ConnectedComponent";
		case AddOrder:
			return "AddOrder";
		case Save:
			return "Save";
		case SaveProjectConfiguration:
			return "SaveProjectConfiguration";
		case EditJUnit:
			return "EditJUnit";
		case Compile:
			return "Compile";
		case Test:
			return "Test";
		case TestCode: 
			return "TestCode";
		case DB_Export:
			return "DB_Export";
		case DB_Import: 
			return "DB_Import";
		default:
			return "DoNothing";
		}
	}
}

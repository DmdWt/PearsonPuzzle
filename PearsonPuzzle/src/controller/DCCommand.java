package controller;

/**
 * Definiert globale Kommandos für ActionEvents u.Ä.<br>
 * (über deren lokale Wohldefiniertheit der Controller entscheidet)
 * @author workspace
 *
 */
public enum DCCommand {
	SubmitPassword, EditProject, NewProject, OpenProject, ProjectList, SaveProject, Logout, 
	DeleteProject, Admin, ResetDB, SetConfig, ConfigureProject, SetTextConfig, StartGroupSelection, SaveGroupSelection, CancelGroupSelection, Compile, TestCode;
	
	@Override
	public String toString(){
		switch(this){
		case EditProject:
			return "EditProject";
		case NewProject:
			return "NewProject";
		case OpenProject:
			return "OpenProject";
		case ProjectList:
			return "ProjectList";
		case SaveProject:
			return "SaveProject";
		case SubmitPassword:
			return "SubmitPassword";
		case Logout:
			return "Logout";
		case DeleteProject:
			return "DeleteProject";
		case Admin:
			return "Admin";
		case ResetDB: 
			return "ResetDB";
		case SetConfig:
			return "SetConfig";
		case ConfigureProject:
			return "ConfigureProject";
		case SetTextConfig:
			return "SetTextConfig";
		case StartGroupSelection:
			return "StartGroupSelection";
		case SaveGroupSelection:
			return "SaveGroupSelection";
		case CancelGroupSelection:
			return "CancelGroupSelection";
		case Compile:
			return "Compile";
		case TestCode: 
			return "TestCode";
		default:
			return "DoNothing";
		}
	}
}

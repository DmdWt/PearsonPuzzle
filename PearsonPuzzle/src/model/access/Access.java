package model.access;


import model.database.dbTransaction;

public class Access {
	
	private AccessGroup accessGroup;
	
	public Access(char[] password, String username, dbTransaction database){
		if (database.lookUpstudent(username, password))
			accessGroup = AccessGroup.STUDENT;
		else if (database.lookUpteacher(username, password))
			accessGroup = AccessGroup.TEACHER;
		else
			accessGroup = AccessGroup.UNAUTHORIZED;
	}
	public Access(){
		accessGroup = AccessGroup.UNAUTHORIZED;
	}
	
	public AccessGroup getAccessGroup(){
		return accessGroup;
	}
	public void setAccessGroup(AccessGroup accessGroup){
		return;
	}
	public void verarbeite(){
		accessGroup.object().verarbeite();
	}

}

package view;

import java.awt.Color;

import controller.Controller;

public interface View {
	
	Color WHITE = Color.decode("#FFFFFF");
	Color RED = Color.decode("#AF002A");
	Color GREEN = Color.decode("#008000");
	
	public void addController(Controller controller);
	
	public Integer showDialog(Allert allert);
	public void showDialog(String message);
	public void showDialog(final PPException exception, boolean modal);
}

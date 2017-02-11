package mobileVersion.view;


import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

import model.GameModel;

import controller.Controller;
import controller.DCCommand;

public class AppletMenu extends JMenuBar implements Observer{
	
	private static final long serialVersionUID = 481730455096186586L;
	public JButton leftArrow;
	public JButton rightArrow;
	public JLabel Smiley;
	public AppletMenu() {
		setupMenu();
	}
	
	private void setupMenu(){
		
		leftArrow = new JButton(new ImageIcon("rsc/icon/arrow/arrow_red_left.png"));
		leftArrow.setActionCommand(DCCommand.ProjectList.toString());
		leftArrow.setEnabled(false);
		
		rightArrow = new JButton(new ImageIcon("rsc/icon/arrow/arrow_green_right.png"));
		rightArrow.setActionCommand(DCCommand.OpenProject.toString());
		this.add(leftArrow);
		this.add(Box.createHorizontalGlue());
		
		Smiley = new JLabel(new ImageIcon("rsc/icon/Smiley/face-wink.png"));
		this.add(Smiley);
		
		this.add(Box.createHorizontalGlue());
		this.add(rightArrow);
	}
	public void addController(Controller controller) {
		leftArrow.addActionListener(controller);
		rightArrow.addActionListener(controller);
	}
	

	public void setView(AppletView view){
		if(view.getClass().equals(ProjectListAView.class)){
			leftArrow.setEnabled(false);
			rightArrow.setEnabled(true);
			rightArrow.setActionCommand(DCCommand.OpenProject.toString());
			Smiley.setIcon(new ImageIcon("rsc/icon/Smiley/face-wink.png"));
		}
		else if(view.getClass().equals(CodeSortAView.class)){
			leftArrow.setEnabled(true);
			rightArrow.setEnabled(false);
			leftArrow.setActionCommand(DCCommand.ProjectList.toString());
			Smiley.setVisible(true);
			Smiley.setIcon(new ImageIcon("rsc/icon/Smiley/face-plain.png"));
		}
	}
	public void setAction(DCCommand command){
		if(command==null)
			rightArrow.setEnabled(false);
		else 
			rightArrow.setEnabled(true);	
		rightArrow.setActionCommand(command.toString());
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o.getClass().equals(GameModel.class))
			Smiley.setIcon(((GameModel) o).getScoreImage());
		Smiley.revalidate();
	}
}

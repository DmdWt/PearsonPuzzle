package view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import model.Model;


public class Design{	
	public static void main(String[] args){
		
		JFrame frame = new JFrame("PearsonPuzzle");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.setSize(250,100);
		frame.add(new JLabel("Login"));
		final JTextField input = new JTextField("Name");
		frame.add( input );
		
		final JList<String> codeList= Model.getCode();

		codeList.setDragEnabled(true);	
		frame.add(codeList);
		frame.pack();;
		frame.setVisible(true);
		javax.swing.Timer t = new javax.swing.Timer( 1000, new ActionListener() {
			  public void actionPerformed( ActionEvent e ) {
			    codeList.repaint();
			  }
			});
			t.start();   // t.stop() beendet.
		
		
	}

}

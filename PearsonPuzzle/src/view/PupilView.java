package view;

import java.awt.BorderLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import controller.Controller;

import Listener.FromTransferHandler;
import Listener.ToSaveTransferHandler;

/**
 * Definiert die Schüler Perspektive der grafischen Oberfläche
 * 
 * @author workspace
 *
 */
public class PupilView extends View{
	JList<String> dragDropList;
	JList<String> saveDropList;
	public PupilView(DefaultListModel<String> codeList, DefaultListModel<String> emptyList, String username){
		// Konfiguration der Listen für Drag&Drop
		/*	
		 * TODO: Frage klären: Eventuell die beiden folgenden Blocks auslagern, 
		 * im Controller erzeugen und nur die JList an den Konstruktor übergeben?!?
		 */
		this.dragDropList=new JList<String>(codeList);
		this.saveDropList=new JList<String>(emptyList);
		// dises zwei Zeilen sollten definitiv noch ausgelagert werden
		dragDropList.setTransferHandler(new FromTransferHandler(codeList, dragDropList));
		saveDropList.setTransferHandler(new ToSaveTransferHandler(TransferHandler.COPY));
		
		dragDropList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dragDropList.setDragEnabled(true);
		dragDropList.setDropMode(DropMode.ON);
		saveDropList.setDropMode(DropMode.ON);
		saveDropList.setFixedCellHeight(20);
		dragDropList.setFixedCellHeight(20);
		saveDropList.setFixedCellWidth(300);
		dragDropList.setFixedCellWidth(300);
		// Swing Elemete werden integriert und dargestellt

		JPanel topPanel=new JPanel(new BorderLayout());
		JButton compileButton=new JButton("Compile");
		JButton takeButton = new JButton("Übernehmen");
		topPanel.add(compileButton,BorderLayout.LINE_START);
		topPanel.add(takeButton, BorderLayout.LINE_END);
		mainPanel.add(topPanel,BorderLayout.PAGE_START);
		JScrollPane sp = new JScrollPane(saveDropList);
		mainPanel.add(sp, BorderLayout.LINE_START);
		sp = new JScrollPane(dragDropList);
		mainPanel.add(sp, BorderLayout.LINE_END);
		// TODO: Arbeitsanweisungen für Schüler defeinieren und einfügen
		mainPanel.add(new JTextField("Hier erfolgt eine möglichst präzise Arbeitsanweisung für den Schüler"),BorderLayout.PAGE_END);
		frame.pack();
		frame.setSize(800,500);
		this.draw();
	}
	public void addController(Controller controller){
	}
}

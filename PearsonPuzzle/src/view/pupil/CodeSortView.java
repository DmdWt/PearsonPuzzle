package view.pupil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Observable;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import view.JView;

import controller.Controller;
import controller.DCCommand;

import Listener.FromTransferHandler;
import Listener.ToSaveTransferHandler;

import model.Model;

/**
 * Drag and Drop Liste für den Schüler. 
 * Hier kann gepuzzlet werden!
 * 
 * @author workspace
 */
public class CodeSortView extends JView {
	
	// Puzzlemodus 0: Reines Drag and Drop
	// Puzzlemodus 1: Elemente werden von rechts nach links "geschaufelt", mit zurückschaufeln
	// Puzzlemodus 2: Elemente werden von rechts nach links geschaufelt, ohne zurückschaufeln
	// Puzzlemodus 3: Elemente bleiben rechts vorhanden, mehrfach-Drag ist möglich
	private static final int Puzzlemodus=1;
	private static final String defaultDescription="Puzzle den Code in die richtige Reihenfolge!\n \nViel Spaß ;-)";
	
	private JList<String> dragList;
	private JList<String> saveDropList;
	private DefaultListModel <String> dragModel;
	private DefaultListModel <String> saveDropModel;
	private JButton compileButton;
	private JButton testButton;
	private JTextArea description;
	
	public CodeSortView(Model model) {
		super(model);
		// TODO: Arbeitsanweisungen für Schüler definieren und einfügen
		menu=new MenuPupil();
		this.addMenuToFrame(menu);
		setupCodeLists();
		setupButtons();
		draw();
	}

	// private Methode, um die Drag and Drop Liste zu konstruieren
	private void setupCodeLists(){
		saveDropModel=new DefaultListModel<String>();
		saveDropList=new JList<String>(saveDropModel);
		dragModel=makeDefaultListModel();
		dragList=new JList<String>(dragModel);
		FromTransferHandler dragTransferH = new FromTransferHandler(dragModel, dragList, model);
		ToSaveTransferHandler dragDropTransferH = new ToSaveTransferHandler(saveDropModel, saveDropList, Puzzlemodus, model);
		
		switch(Puzzlemodus){
			case 0:
				// Einzelne Drag and Drop List (nicht zwei)
				saveDropModel=makeDefaultListModel();
				saveDropList=new JList<String>(saveDropModel);
				dragDropTransferH = new ToSaveTransferHandler(saveDropModel, saveDropList, Puzzlemodus, model);
				dragModel= new DefaultListModel<String>();
				dragList = new JList<String>();
				saveDropList.setDropMode(DropMode.INSERT);				
				saveDropList.setTransferHandler(dragDropTransferH);
				break;
			case 1:
				// Elemente werden rechts entfernt, können auch wieder zurück nach rechts transferiert werden
				saveDropList.setDropMode(DropMode.INSERT);
				dragList.setDropMode(DropMode.INSERT);
				saveDropList.setTransferHandler(dragDropTransferH);
				dragList.setTransferHandler(dragTransferH);
				break;
			case 2:
				// Elemte werden rechts entfernt, können links nicht entfernt werden
				saveDropList.setDropMode(DropMode.INSERT);
				dragTransferH.disableRevert();
				saveDropList.setTransferHandler(dragDropTransferH);
				dragList.setTransferHandler(dragTransferH);
				break;
			case 3:
				// Elemente bleiben rechts vorhanden, mehrfacher Drag&Drop möglich
				saveDropList.setDropMode(DropMode.ON_OR_INSERT);
				dragList.setDropMode(DropMode.ON_OR_INSERT);
				dragTransferH.disableRevert();
				dragTransferH.setAction(TransferHandler.COPY);
				saveDropList.setTransferHandler(dragDropTransferH);
				dragList.setTransferHandler(dragTransferH);
				break;
			default:
				break;	
		}
		
		// Linke Liste (Drop)
		saveDropList.setName("dropList");
		saveDropList.setFixedCellHeight(20);
		saveDropList.setDragEnabled(true);
		JScrollPane scrollPanel_sDL = new JScrollPane(saveDropList);
		scrollPanel_sDL.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPanel_sDL.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel_sDL.setPreferredSize(new Dimension(360,300));
		mainPanel.add(scrollPanel_sDL, BorderLayout.LINE_START);
		
		// Rechte Liste (Drag)
		dragList.setName("dragList");
		dragList.setFixedCellHeight(20);
		dragList.setDragEnabled(true);
		dragList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPanel_dDL = new JScrollPane(dragList);
		scrollPanel_dDL.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPanel_dDL.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel_dDL.setPreferredSize(new Dimension(360,300));
		if(Puzzlemodus!=0)
			mainPanel.add(scrollPanel_dDL, BorderLayout.LINE_END);
		
		// Arbeitsanweisung und Ergebnisse
		description=new JTextArea(defaultDescription);
		if(!model.getProjectDescription().trim().equals(""))
			description.setText(model.getProjectDescription());
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setEditable(false);
		JScrollPane scrollPane_description = new JScrollPane(description);
		scrollPane_description.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane_description.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_description.setPreferredSize(new Dimension(360,100));
		mainPanel.add(scrollPane_description,BorderLayout.PAGE_END);
	}
	
	/**
	 * Buttoons werden definiert. Hizufügen eines Action Listeners noch notwendig.
	 */
	private void setupButtons(){
		compileButton=new JButton("Kompilieren");
		testButton = new JButton("Test starten");
		JPanel topPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(compileButton);
		topPanel.add(testButton);
		mainPanel.add(topPanel,BorderLayout.PAGE_START);
	}
	
	/**
	 * Wird vom Controller asugeführt, um Listener, Handler und <br>
	 * Controller hinzuzufügen
	 */
	public void addController(Controller controller){
		// TODO: In den offiziellen Controller auslagern
		saveDropList.addMouseListener(controller);
		compileButton.addActionListener(controller);
		compileButton.setActionCommand(DCCommand.Compile.toString());
		testButton.setActionCommand(DCCommand.TestCode.toString());
		testButton.addActionListener(controller);
		menu.addActionListener(controller);
	}
	
	/**
	 * Notwendig, um die Menge der Tabs richtig darzustellen. <br>
	 * Tabs werden hierzu in Leerzeichen umgewandelt.
	 * @param stringList Liste aus dem Model 
	 * @return DefaultListModel
	 */
	private DefaultListModel<String> makeDefaultListModel(){
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		
		// Dies ist nötig, um bei JList Elementen die Tabbreite berücksichtigen zu können
		// Steht hier, weil es ein Problem von Swing ist, kein allgemeines Problem
		Vector<String> codeVector = model.getCodeVector();
		for(String string : codeVector){
			
			listModel.add(listModel.size(),  string);
		}
		return listModel;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1==DCCommand.Compile){
			// Fehlerbericht oder Erfolg ausgeben
			Vector<HashMap<String, String>> failures = model.getCompileFailures();
			if(failures.isEmpty())
				description.setText("Kompilieren war erfolgreich!");
			else{
				String failureText = "Kompilieren war nicht erfolgreich. \nAufgetretenen Fehler: ";
				for(HashMap<String, String> failure : failures){
					failureText = failureText+"\n "+failure.get("Nachricht")+" in Zeile "+failure.get("Zeile");
				}
				description.setText(failureText);
			}
			dragList.setEnabled(false);
			saveDropList.setEnabled(false);
		}
		update();
	}

	@Override
	public void update() {
		dragModel=makeDefaultListModel();
		saveDropModel=new DefaultListModel <String>();
		dragList=new JList<String>(dragModel);
		saveDropList=new JList<String>(saveDropModel);
		dragList.setTransferHandler(new FromTransferHandler(dragModel, dragList, model));
		saveDropList.setTransferHandler(new ToSaveTransferHandler(saveDropModel, saveDropList, Puzzlemodus, model));	
	}

}

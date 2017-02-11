package view.pupil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Observable;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import org.junit.runner.notification.Failure;

import view.JView;
import view.teacher.UnitEditor;

import controller.Controller;
import controller.DCCommand;
import controller.transferHandler.FromTransferHandler;
import controller.transferHandler.ToSaveTransferHandler;


import model.GameModel;
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
	private final int Puzzlemode;
	private static final String defaultDescription="Puzzle den Code in die richtige Reihenfolge!\n \nViel Spaß ;-)";
	
	private JList<String> dragList;
	private JList<String> saveDropList;
	private DefaultListModel <String> dragModel;
	private DefaultListModel <String> saveDropModel;
	private JButton compileButton;
	private JButton unitTestButton;
	private JButton testButton;
	private JLabel messageBox;
	
	private GameModel gameModel;
	private JLabel smiley;
	
	public CodeSortView(Model model) {
		super(model);
		menu=new MenuPupil();
		
		if(model.getPuzzlemode()==null)
			Puzzlemode = 0;
		else 
			Puzzlemode = model.getPuzzlemode();

		this.addMenuToFrame(menu);
		
		setupCodeLists();
		
		setupButtons();
		// In Puzzlemodus 3 ist eine Sortierung der Reihenfolgen nicht sinnvoll
		if(Puzzlemode==3)
			testButton.setVisible(false);		
		
		mainPanel.revalidate();
	}

	// private Methode, um die Drag and Drop Liste zu konstruieren
	private void setupCodeLists(){
		saveDropModel=new DefaultListModel<String>();
		saveDropList=new JList<String>(saveDropModel);
		dragModel=makeDefaultListModel();
		dragList=new JList<String>(dragModel);
		FromTransferHandler dragTransferH = new FromTransferHandler(dragModel, dragList, model);
		ToSaveTransferHandler dragDropTransferH = new ToSaveTransferHandler(saveDropModel, saveDropList, Puzzlemode, model);
		
		switch(Puzzlemode){
			case 0:
				// Einzelne Drag and Drop List (nicht zwei)
				saveDropModel=makeDefaultListModel();
				saveDropList=new JList<String>(saveDropModel);
				dragDropTransferH = new ToSaveTransferHandler(saveDropModel, saveDropList, Puzzlemode, model);
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
		if(Puzzlemode==0)
			scrollPanel_sDL.setPreferredSize(new Dimension(650, 260));
		else
			scrollPanel_sDL.setPreferredSize(new Dimension(360,260));
		mainPanel.add(scrollPanel_sDL, BorderLayout.LINE_START);
		
		// Rechte Liste (Drag)
		dragList.setName("dragList");
		dragList.setFixedCellHeight(20);
		dragList.setDragEnabled(true);
		dragList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPanel_dDL = new JScrollPane(dragList);
		scrollPanel_dDL.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPanel_dDL.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel_dDL.setPreferredSize(new Dimension(360,260));
		if(Puzzlemode!=0)
			mainPanel.add(scrollPanel_dDL, BorderLayout.LINE_END);
		
		// Arbeitsanweisung und Ergebnisse
		messageBox = new JLabel(defaultDescription);
//		messageBox=new JTextArea(defaultDescription);
		if(!model.getProjectDescription().trim().equals(""))
			messageBox.setText(model.getProjectDescription());
//		messageBox.setLineWrap(true);
//		messageBox.setWrapStyleWord(true);
//		messageBox.setEditable(false);
		JScrollPane scrollPane_description = new JScrollPane(messageBox);
		scrollPane_description.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane_description.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane_description.setPreferredSize(new Dimension(650,100));
		mainPanel.add(scrollPane_description,BorderLayout.PAGE_END);
	}
	
	/**
	 * Buttoons werden definiert. Hizufügen eines Action Listeners noch notwendig.
	 */
	private void setupButtons(){
		compileButton=new JButton("Kompilieren");
		
		testButton = new JButton("Testen");
		unitTestButton = new JButton("Starten");
		
		gameModel = new GameModel(model);
		smiley = new JLabel(gameModel.getScoreImage());
		
		JPanel topPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.add(compileButton);
		topPanel.add(testButton);
		topPanel.add(unitTestButton);
		topPanel.add(smiley);
		System.out.println(compileButton.getBackground());
		mainPanel.add(topPanel,BorderLayout.PAGE_START);
	}
	
	/**
	 * Wird vom Controller asugeführt, um Listener, Handler und <br>
	 * Controller hinzuzufügen
	 */
	public void addController(Controller controller){
		// TODO: In den offiziellen Controller auslagern
		//saveDropList.addMouseListener((DefaultController)controller);
		compileButton.addActionListener(controller);
		compileButton.setActionCommand(DCCommand.Compile.toString());
		testButton.setActionCommand(DCCommand.Test.toString());
		testButton.addActionListener(controller);
		unitTestButton.setActionCommand(DCCommand.TestCode.toString());
		unitTestButton.addActionListener(controller);
		unitTestButton.setEnabled(false);
		if(model.getJUnitCode()==null || model.getJUnitCode().isEmpty() || model.getJUnitCode().equals(UnitEditor.DEFAULT_UNIT_CODE))
			unitTestButton.setVisible(false);
		menu.addActionListener(controller);
		
		gameModel.addObserver(this);
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
		Vector<String> codeVector = model.getCodeVector(true);
		for(String string : codeVector){
			
			listModel.add(listModel.size(),  string);
		}
		return listModel;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1==null && compileButton!=null && unitTestButton!=null && testButton!=null){
			compileButton.setBackground(DEFAULTBUTTONCOLOR);
			compileButton.setForeground(Color.BLACK);
			unitTestButton.setBackground(DEFAULTBUTTONCOLOR);
			unitTestButton.setForeground(Color.BLACK);
			unitTestButton.setEnabled(false);
			testButton.setBackground(DEFAULTBUTTONCOLOR);
			testButton.setForeground(Color.BLACK);
			gameModel.reset();
			if(model.getProjectDescription()==null || model.getProjectDescription().isEmpty())
				messageBox.setText(defaultDescription);
			else 
				messageBox.setText(model.getProjectDescription());
			messageBox.revalidate();
			smiley.setIcon(gameModel.getScoreImage());
		}
		else if(arg1!= null && arg1.equals("score"))
			if(model.getSollution().isEmpty())
				smiley.setIcon(new ImageIcon("rsc/icon/Smiley/face-wink.png"));
			else
				smiley.setIcon(gameModel.getScoreImage());
		else if(arg1==DCCommand.Compile){
			// Fehlerbericht oder Erfolg ausgeben
			Vector<HashMap<String, String>> failures = model.getCompileFailures();
			if(failures.isEmpty()){
				messageBox.setText("Kompilieren war erfolgreich!");
				compileButton.setBackground(GREEN);
				compileButton.setForeground(WHITE);
				gameModel.score("compile");
				unitTestButton.setEnabled(true);
			}
			else{
				String failureText = "<html><body>Kompilieren war nicht erfolgreich. <br>Aufgetretenen Fehler: ";
				for(HashMap<String, String> failure : failures){
					failureText = failureText+"<br> "+failure.get("Nachricht")+" in Zeile "+failure.get("Zeile");
				}
				messageBox.setText(failureText+"</body></html>");
				compileButton.setBackground(RED);
				compileButton.setForeground(WHITE);
				gameModel.loose("compile");
			}
		}
		else if(arg1==DCCommand.TestCode){
			
			String failureText = new String("<html><head><style type=\"text/css\"> .success {color:green;} .failure{color:red;} .unitFailure{color:red; margin-left:20px;} .increment {margin-left:24px;} .comment {font-style:italic;} .heading{font-style: oblique;}</style> </head><body>");
				System.out.println(model.getJUnitCode());
				String cssClass;				
				if(model.getjUnitFailures()!=null && model.getjUnitFailures().size()==0
						//&& model.getCompileFailures().isEmpty()
						){
					cssClass = " class=\"success\" ";
					unitTestButton.setBackground(GREEN);
					unitTestButton.setForeground(WHITE);
					gameModel.score("unitTest");
				}
				else{
					cssClass = " class=\"failure\" ";
					unitTestButton.setBackground(RED);
					unitTestButton.setForeground(WHITE);
					gameModel.loose("unitTest");
				}
				failureText+="<span class=\"heading\">Ergebnis des Unit-Testlaufs:</u><span"+cssClass+">"+model.getjUnitFailures().size()+" Fehler</span>";
				System.out.println(failureText);
				for(Failure failure: model.getjUnitFailures()){
					System.out.println(failure);
					failureText=failureText+"<div class=\"unitFailure\">"+failure+"</div>";
				}
				messageBox.setText(failureText+"</body></html>");
//				dragList.setEnabled(false);
//				saveDropList.setEnabled(false);
		}
		else if(arg1==DCCommand.Test){
			String failureText = new String("<html><head><style type=\"text/css\"> .success {color:green;} .failure{color:red;} .unitFailure{color:red; margin-left:20px;} .increment {margin-left:24px;} .comment {font-style:italic;} .heading{font-style: oblique;}</style> </head><body>");
			//failureText = failureText + "<br>";
			for(String key : model.getSuccessMap().keySet()){
				if(key.equals("Gruppentests") || key.contains("Reihenfolge")){
					failureText+="<div><span class=\"heading\">"+key+": </span>";
					if(model.getSuccessMap().get(key)){
						testButton.setBackground(GREEN);
						testButton.setForeground(WHITE);
						gameModel.score("orderTest");
					}
					else{
						testButton.setBackground(RED);
						testButton.setForeground(WHITE);
						gameModel.loose("orderTest");
					}
				}
				else
					failureText+="<div class=\"increment\"><span class=\"heading\">"+key+": </span>";		
				
				if(model.getSuccessMap().get(key))
					failureText+="<span class=\"success\">Erfolgreich!";
				else 
					failureText+="<span class=\"failure\">Failed!";
				if(model.getOrderFailureText(key)!=null
						&& !model.getOrderFailureText(key).trim().isEmpty())
					failureText+="</span><span class=\"comment\"> ( "+model.getOrderFailureText(key)+")</span>";
				failureText += "</span></div>";
			}
			messageBox.setText(failureText+"</body></html>");
			System.out.println(failureText);
		}			
		//update();
	}

	@Override
	public void update() {
		dragModel=makeDefaultListModel();
		saveDropModel=new DefaultListModel <String>();
		dragList=new JList<String>(dragModel);
		saveDropList=new JList<String>(saveDropModel);
		dragList.setTransferHandler(new FromTransferHandler(dragModel, dragList, model));
		saveDropList.setTransferHandler(new ToSaveTransferHandler(saveDropModel, saveDropList, Puzzlemode, model));	
	}

}

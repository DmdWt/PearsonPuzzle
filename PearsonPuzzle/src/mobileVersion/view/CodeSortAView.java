package mobileVersion.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Observable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import model.GameModel;
import model.Model;

import org.junit.runner.notification.Failure;

import controller.Controller;
import controller.DCCommand;
import controller.transferHandler.ToSaveTransferHandler;
import view.teacher.UnitEditor;

public class CodeSortAView extends AppletView{
	
	private static final long serialVersionUID = -1258749785785828266L;
	private final static int Puzzlemode=0;
	private static final String defaultDescription="Puzzle den Code in die richtige Reihenfolge!\n \nViel Spaß ;-)";
	
	private JList<String> saveDropList;
	private DefaultListModel <String> saveDropModel;
	private JButton compileButton;
	private JButton testButton;
	private JLabel messageBox;
	private Model model;
	private AbstractButton unitTestButton;
	private final Color DEFAULTBUTTONCOLOR = (new JButton()).getBackground();
	private GameModel gameModel;
	
	
	public CodeSortAView(Model model) {
		super(model);
		this.model = model;
		gameModel = new GameModel(model);		
		setupCodeLists();
		setupButtons();
	}

	// private Methode, um die Drag and Drop Liste zu konstruieren
	private void setupCodeLists(){
		saveDropModel=new DefaultListModel<String>();
		saveDropList=new JList<String>(saveDropModel);
		
		ToSaveTransferHandler dragDropTransferH = new ToSaveTransferHandler(saveDropModel, saveDropList, Puzzlemode, model);
		// Einzelne Drag and Drop List (nicht zwei)
				saveDropModel=makeDefaultListModel();
				saveDropList=new JList<String>(saveDropModel);
				dragDropTransferH = new ToSaveTransferHandler(saveDropModel, saveDropList, Puzzlemode, model);
				saveDropList.setDropMode(DropMode.INSERT);				
				saveDropList.setTransferHandler(dragDropTransferH);
		
		// Linke Liste (Drop)
		Border border = BorderFactory.createEmptyBorder();
		saveDropList.setBorder(BorderFactory.createCompoundBorder(border, 
			            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		saveDropList.setName("dropList");
		saveDropList.setFixedCellHeight(20);
		saveDropList.setDragEnabled(true);
		saveDropList.setMinimumSize(new Dimension(100,100));
		saveDropList.setPreferredSize(new Dimension(100,100));
		JScrollPane scrollPanel_sDL = new JScrollPane(saveDropList);
		scrollPanel_sDL.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPanel_sDL.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel_sDL.setMinimumSize(new Dimension(100,100));
		scrollPanel_sDL.setPreferredSize(new Dimension(360,300));
		this.add(scrollPanel_sDL, BorderLayout.CENTER);
		
		// Arbeitsanweisung und Ergebnisse
		messageBox = new JLabel(defaultDescription);
		border = BorderFactory.createEmptyBorder();
		messageBox.setBorder(BorderFactory.createCompoundBorder(border, 
			            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
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
		this.add(scrollPane_description,BorderLayout.PAGE_END);
	}
	
	/**
	 * Buttoons werden definiert. Hizufügen eines Action Listeners noch notwendig.
	 */
	private void setupButtons(){
		
		compileButton=new JButton("Kompilieren");
		testButton = new JButton("Testen");
		unitTestButton = new JButton("Starten");
		unitTestButton.setEnabled(false);
		
		JPanel topPanel=new JPanel(new FlowLayout(FlowLayout.CENTER));
		topPanel.add(compileButton);
		topPanel.add(testButton);
		topPanel.add(unitTestButton);
		
		this.add(topPanel,BorderLayout.PAGE_START);
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
		if(model.getJUnitCode()==null || model.getJUnitCode().isEmpty() || model.getJUnitCode().equals(UnitEditor.DEFAULT_UNIT_CODE))
			unitTestButton.setVisible(false);
		
		gameModel.addObserver((AppletMenu) this.getRootPane().getJMenuBar());
		//menu.addActionListener(controller);
	}
	@Override
	public void removeController(Controller controller) {
		compileButton.removeActionListener(controller);
		testButton.removeActionListener(controller);
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
		}
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
		}	
		messageBox.revalidate();
	}

	public void update() {
		saveDropModel=new DefaultListModel <String>();
		saveDropList=new JList<String>(saveDropModel);
		saveDropList.setTransferHandler(new ToSaveTransferHandler(saveDropModel, saveDropList, Puzzlemode, model));
	}
	

	@Override
	public Object get(String valueToReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	

}

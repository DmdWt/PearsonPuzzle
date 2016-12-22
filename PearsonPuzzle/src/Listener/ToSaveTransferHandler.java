package Listener;

import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import model.Model;

/**
 * Klasse ermöglicht, Drag&Drop Aktionen durchzuführen. <br>
 * Es werden nur bekannte Elemente (aus codeVector) aufdgenommen. <br>
 * Um dies zu prüfen greift die Klasse auf das Model zurück.  
 * 
 * Das Verhalten kann über die Ü
 * @author workspace
 *
 */
public class ToSaveTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	
	// Puzzlemodus 0: Reines Drag and Drop
		public final static int DnD_simple=0;
	// Puzzlemodus 1: Elemente werden von rechts nach links "geschaufelt", mit zurückschaufeln
		public final static int BuggerDnD=1;
	// Puzzlemodus 2: Elemente werden von rechts nach links geschaufelt, ohne zurückschaufeln
		public final static int DnD_Bugger_OneWay=2;
	// Puzzlemodus 3: Elemente bleiben rechts vorhanden, mehrfach-Drag ist möglich
		public final static int DnD_Bugger_Endless=3;
		
	private DefaultListModel<String> dropDList;
	private JList<String> dropJList;
	private Integer dragIndex;			// Listenindex des gedragten Elements
	private Integer dropIndex;			// Listenindex der Position, an der ein Element gedropt wird.
	

	private int action;					// COPY oder MOVE
	private DropMode defaultDropmode;	// Damit externer DnD prinzipiell auch Elemnte ersetzten kann (bei internem DnD immer Insert)
	private boolean internDnD;			// Gibt Auskunft, ob es sich um ein Listeninternes DnD Event handelt (wird beim Export gesetzt und beim Import ausgelesen)	
	private boolean dragSameElement;	// Das gleiche Element kann mehrfach importiert werden
	private boolean removeElements;  	// Elemente wandern wieder zurück
	private boolean deleteElements; 	// Elemente wandern nicht wieder zurück, sondern werden entfernt
	private Model model;
	
	public ToSaveTransferHandler(DefaultListModel<String> dropDList, JList<String> dropJList,int Type, Model model) {
		this.model=model;
		switch(Type){
        case 0:
        	action=TransferHandler.MOVE;
        	defaultDropmode = DropMode.INSERT;
        	removeElements=true;
        	dragSameElement=true;
        	Vector<String> codeVector = model.getCodeVector();
        	if(model.getSollution().isEmpty())
        		for(int i=0;i<codeVector.size();i++){
        			System.out.println(i);
        			model.insertInSollution(i, codeVector.get(i));
        		}
        	break;
        case 1:
        	action=TransferHandler.MOVE;
        	defaultDropmode = DropMode.INSERT;
        	removeElements=true;
        	deleteElements=false;
        	dragSameElement=true;
        	break;
        case 2:
        	action=TransferHandler.MOVE;
        	defaultDropmode = DropMode.INSERT;
        	removeElements=false;
        	deleteElements=false;
        	dragSameElement=true;
        	break;
        case 3:
        	action=TransferHandler.COPY;
        	defaultDropmode = DropMode.INSERT;
        	removeElements=true;
        	deleteElements=true;
        	dragSameElement=true;
        	break;
        default:      
        	action = TransferHandler.MOVE;
        	defaultDropmode = DropMode.INSERT;
        	removeElements=false;
        	deleteElements=false;
        	dragSameElement=false;
        	break;
        }
		internDnD=false;
        this.dropDList=dropDList;
        this.dropJList=dropJList;
    }
	/**
	 * Legt fest, ob beim <b>DROP</b> Elemente kopiert oder verschoben werden.
	 * @param action
	 */
	public void setAction(int action){
		this.action=action;
	}
	/**
	 * Entfernen von Elementen wird ermöglicht.
	 */
	public void enableRemove(){
		this.removeElements=true;
	}
	
	/**
	 * Entfernen von Elementen wird verhindert.
	 */
	public void disableRemove(){
		this.removeElements=false;
	}
    
	// --------------- Import Methoden
	@Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        
    	if (!support.isDrop())
        	return false;
        
    	if (!support.isDataFlavorSupported(DataFlavor.stringFlavor))
            return false;
        
        boolean actionSupported = (action & support.getSourceDropActions()) == action;
        if (actionSupported) {
            support.setDropAction(action);
            return true;
        }
        
        return false;
    }
	@Override
    public boolean importData(TransferHandler.TransferSupport support) {
        
    	if (!canImport(support))
        	return false;
        
        JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
        dropIndex = dl.getIndex();
        String data;
        try {
        	data = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
        	} catch (UnsupportedFlavorException e) {
        		return false;
        	} catch (java.io.IOException e) {
        		return false;
        }
        
        JList<String> list = (JList<String>)support.getComponent();
        DefaultListModel<String> listModel = (DefaultListModel<String>)list.getModel();
        if(!model.getCodeVector().contains(data))
        	// Daten von Extern werden nicht anerkannt
        	return false;
    	if(internDnD){
    		// Wenn internes DnD, werden Elemente nur verschoben, nicht ersetzt
    		listModel.insertElementAt(data, dropIndex);
    		model.insertInSollution(dropIndex, data);

        	
    		Rectangle rect = list.getCellBounds(dropIndex, dropIndex);
            list.scrollRectToVisible(rect);
            list.setSelectedIndex(dropIndex);
            list.requestFocusInWindow();
        	return true;
    	}
    	else if(listModel.contains(data)
        		&& !dragSameElement){
    		// Gleiches Element wird nicht 2x aus der rechten Liste gezogen.
        	return false;
        }
        else{
        	// 
        	listModel.insertElementAt(data, dropIndex);
        	if(list.getDropLocation().isInsert())
        		model.insertInSollution(dropIndex, data);
        	else{
        		model.replaceInSollution(dropIndex, data);
        		listModel.remove(dropIndex+1);
        	}
            Rectangle rect = list.getCellBounds(dropIndex, dropIndex);
            list.scrollRectToVisible(rect);
            list.setSelectedIndex(dropIndex);
            list.requestFocusInWindow();
            return true;
        }
    }
    
    
    // --------- Export Methoden -----------------
	@Override
    public int getSourceActions(JComponent comp) {
        return COPY_OR_MOVE;
    }
    
	@Override
    public Transferable createTransferable(JComponent comp) {
        internDnD=true; // Information, dass es sich um ein itnernes DnD handelt
    	dropJList.setDropMode(DropMode.INSERT); // Elemente werden verschoben, nicht ersetzt
        dragIndex = dropJList.getSelectedIndex();
        
        if (dragIndex < 0 || dragIndex >= dropDList.getSize())
            return null;
        return new StringSelection((String)dropJList.getSelectedValue());
    }
    
	@Override
    public void exportDone(JComponent comp, Transferable trans, int action) {
    	// Elemente werden entfernt 
        if(dropIndex==null && removeElements && action!=0){
        	dropDList.removeElementAt(dragIndex);
        	model.removeInSollution(dragIndex);
        	if(defaultDropmode==DropMode.ON){
        		dropDList.addElement("");
        	}
        }
        // Elemente werden gelöscht (rechts nicht wieder aufgenommen)
        else if(dropIndex==null && deleteElements){
        	dropDList.removeElementAt(dragIndex);
        	model.removeInSollution(dragIndex);
        	if(defaultDropmode==DropMode.ON){
        		dropDList.addElement("");
        	}
        }
        
        // Internes DnD wird abgehandelt (verschieben)
        else if(dropIndex!=null && action!=0){
        	System.out.println(action);
        	if(dragIndex<=dropIndex){
        		dropDList.removeElementAt(dragIndex);
        		model.removeInSollution(dragIndex);
        	}
        	else if(dragIndex>dropIndex){
        		model.removeInSollution(dragIndex+1);
        		dropDList.removeElementAt(dragIndex+1);
        	}
        }
        // Damit erkannt wird, ob interne oder externe DnD Aktion vorliegt, 
        // werden wieder die defaultwerte gesetzt
        dropJList.clearSelection();
        internDnD=false;
        dropJList.setDropMode(defaultDropmode);
        dropIndex=null;
        return;
    }
} 

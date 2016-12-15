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
 * Klasse definiert, was beim "droppen" mit dem Element passiert, auf das
 * "gedropped" wurde.
 * 
 * @author workspace
 *
 */
public class ToSaveTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	int action;
	private DefaultListModel<String> dropDList;
	private DropMode defaultDropmode;
	private JList<String> dropJList;
	private int dragIndex = 0;
	private int dropIndex = 0;
	private boolean internDnD;
	private boolean dragSameElement;
	private boolean removeElements;
	private Model model;
	
	// Puzzlemodus 0: Reines Drag and Drop
	public final static int DnD_simple=0;
	// Puzzlemodus 1: Elemente werden von rechts nach links "geschaufelt", mit zurückschaufeln
	//public final static int BuggerDnD=1;
	// Puzzlemodus 2: Elemente werden von rechts nach links geschaufelt, ohne zurückschaufeln
	public final static int DnD_Bugger_OneWay=2;
	// Puzzlemodus 3: Elemente bleiben rechts vorhanden, mehrfach-Drag ist möglich
	public final static int DnD_Bugger_Endless=3;
	
	public ToSaveTransferHandler(DefaultListModel<String> dropDList, JList<String> dropJList,int Type, Model model) {
		this.model=model;
		switch(Type){
        case 0:
        	action=TransferHandler.MOVE;
        	removeElements=true;
        	dragSameElement=true;
        	defaultDropmode=DropMode.INSERT;
        	Vector<String> codeVector = model.getCodeVector();
        	if(model.getSollution().isEmpty())
        		for(int i=0;i<codeVector.size();i++){
        			System.out.println(i);
        			model.insertInSollution(i, codeVector.get(i));
        		}
        	break;
        case 1:
        	// FIXME: Zu füllen
        	break;
        case 2:
        	action=TransferHandler.MOVE;
        	removeElements=false;
        	dragSameElement=true;
        	defaultDropmode=DropMode.INSERT;
        	break;
        case 3:
        	action=TransferHandler.COPY;
        	removeElements=true;
        	dragSameElement=true;
        	defaultDropmode=DropMode.ON_OR_INSERT;
        	break;
        default:      
        	action = TransferHandler.MOVE;
        	removeElements=true;
        	dragSameElement=false;
        	defaultDropmode=DropMode.INSERT;
        	break;
        }
		internDnD=false;
        defaultDropmode=dropJList.getDropMode();
        defaultDropmode=DropMode.INSERT;
        System.out.println(defaultDropmode);
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
	
	public void enableRemove(){
		this.removeElements=true;
	}
	public void disableRemove(){
		this.removeElements=false;
	}
	public void setDefaultDropMode(DropMode dropMode){
		this.defaultDropmode=dropMode;
	}
    
	// --------------- Import Methoden
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) {
        	return false;
        }
        if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return false;
        }
        boolean actionSupported = (action & support.getSourceDropActions()) == action;
        if (actionSupported) {
            support.setDropAction(action);
            return true;
        }
        return false;
    }

    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
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
        
    	if(internDnD){
    		// Wenn Drag and Drop Liste gleich ist
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
    		// Gleiches Element kann nicht 2x aus der rechten Liste gezogen werden.
        	return false;
        }
        else{
        	listModel.insertElementAt(data, dropIndex);
        	if(list.getDropLocation().isInsert()){
        		model.insertInSollution(dropIndex, data);
        	}
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
    public int getSourceActions(JComponent comp) {
        return COPY_OR_MOVE;
    }
    
    public Transferable createTransferable(JComponent comp) {
    	dropJList.setDropMode(DropMode.INSERT);
        dragIndex = dropJList.getSelectedIndex();
        // Information, dass es sich um ein internes Drag and Drop handelt
        internDnD=true;
        if (dragIndex < 0 || dragIndex >= dropDList.getSize()) {
            return null;
        }
        return new StringSelection((String)dropJList.getSelectedValue());
    }
    
    public void exportDone(JComponent comp, Transferable trans, int action) {
    	
    	System.out.print(action);
    	// Elemente werden entfernt 
        if(removeElements && action==0 ){
        	// FIXME: Insert muss besser gehandelt werden
        	dropDList.removeElementAt(dragIndex);
        	model.removeInSollution(dragIndex);
        	if(defaultDropmode==DropMode.ON){
        		dropDList.addElement("");
        	}
        }
        
        // Internes DnD
        if(internDnD && action!=0){
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
        // Damit erkannt wird, wenn Drag and Drop von Extern kommt
        dropJList.clearSelection();
        internDnD=false;
        dropJList.setDropMode(defaultDropmode);
        return;
    }

	public boolean isDragSameElement() {
		return dragSameElement;
	}

	public void setDragSameElement(boolean dragSameElement) {
		this.dragSameElement = dragSameElement;
	}
} 

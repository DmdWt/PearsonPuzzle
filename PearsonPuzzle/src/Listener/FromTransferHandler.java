package Listener;


import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

import model.Model;

/**
 * 
 * @author workspace
 *
 */
public class FromTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	
	private Model model;
	
	private int action;					// COPY oder MOVE
	private boolean internDnD;			// Gibt Auskunft, ob es sich um ein Listeninternes DnD Event handelt (wird beim Export gesetzt und beim Import ausgelesen)
	private boolean canRevertAction;	// Gibt an, ob Elemente wieder zurück in die Liste gedropt werden können.
	 private Integer dragIndex;			// Index des Elements, das gedragt wird.
	private JList<String> dragJList;
	private DefaultListModel<String> dragDList;
	
	public FromTransferHandler(DefaultListModel<String> dragDList, JList<String> dragJList, Model model){
		this.dragDList=dragDList;
		this.dragJList=dragJList;
		this.model=model;
		internDnD = false;
		canRevertAction = true;
		action = TransferHandler.MOVE;
	}
	
	public void setAction(int action){
		this.action=action;
	}
	/**
	 * Ermöglicht, dass Elemente wieder zurück in die Liste gedropt werden.
	 */
	public void enableRevert(){
		canRevertAction=true;
	}
	/**
	 * Elemente können dann nicht mehr zur Liste hinzugefügt/gedragt werden.
	 */
	public void disableRevert(){
		canRevertAction=false;
	}
	
	// ---- Import Methoden
	@Override
    public boolean canImport(TransferHandler.TransferSupport support) {
    	if(internDnD)
    		return false;
    	
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
        if(!canRevertAction)
    		return false;
        JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
        Integer dropIndex = dl.getIndex();
        
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
        // Daten von Extern werden nicht anerkannt
        if(!model.getCodeVector().contains(data)){
        	return false;
        }
        listModel.insertElementAt(data, dropIndex);
        Rectangle rect = list.getCellBounds(dropIndex, dropIndex);
        list.scrollRectToVisible(rect);
        list.setSelectedIndex(dropIndex);
        list.requestFocusInWindow();
      	return true;
    }
    
	// ----- ExportMethoden
	@Override
    public int getSourceActions(JComponent comp) {
        return COPY_OR_MOVE;
    }
    @Override
    public Transferable createTransferable(JComponent comp) {
        dragIndex = dragJList.getSelectedIndex();        
        if (dragIndex < 0 || dragIndex >= dragDList.getSize())
            return null;
        internDnD=true;
        return new StringSelection((String)dragJList.getSelectedValue());
    }
    @Override
    public void exportDone(JComponent comp, Transferable trans, int action) {
    	internDnD = false;
    	if (action != MOVE) {
        	dragJList.clearSelection();
    		return;
        }
        if(action == MOVE){
        	dragDList.removeElementAt(dragIndex);
        	dragJList.clearSelection();
        	return;
        }
    }
}

package Listener;

import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.TransferHandler;

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
	public ToSaveTransferHandler(int action) {
        this.action = action;
    }
    
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

        int index = dl.getIndex();
        
        String data;
        try {
            data = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (java.io.IOException e) {
            return false;
        }

        JList list = (JList)support.getComponent();
        DefaultListModel listModel = (DefaultListModel)list.getModel();
        if(listModel.contains(data)){
        	// TODO: Allert ausgeben, dass Element bereits gedragt wurde oder alternativ Drag für dieses Element deaktivieren
        	return false;
        }
        else{
        	listModel.insertElementAt(data, index);
            listModel.remove(index+1);
            
            Rectangle rect = list.getCellBounds(index, index);
            list.scrollRectToVisible(rect);
            list.setSelectedIndex(index);
            list.requestFocusInWindow();
            return true;
        }     
    }  
} 

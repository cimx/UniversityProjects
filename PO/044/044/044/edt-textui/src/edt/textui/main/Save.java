/** @version $Id: Save.java,v 1.11 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.main;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.Command;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.Editor;

/**
 * Save to file under current name (if unnamed, query for name).
 */
public class Save extends Command<Editor> {
	public Save(Editor editor) {
		super(MenuEntry.SAVE, editor);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		String filename;
		if (!_receiver.hasFilename()){
			filename = IO.readString(Message.newSaveAs());	
			_receiver.setDocFilename(filename);
		}
		else{
			filename = _receiver.getDocFilename();
		}
		try{
			_receiver.saveDocument(filename);
		}
		catch(IOException e){ e.printStackTrace(); }
	}
}
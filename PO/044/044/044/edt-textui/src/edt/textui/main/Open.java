/** @version $Id: Open.java,v 1.10 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.main;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.Command;
import ist.po.ui.DialogException;
import java.io.IOException;
import java.io.FileNotFoundException;
import edt.core.Editor;

/**
 * Open existing document.
 */
public class Open extends Command<Editor> {
	public Open(Editor editor) {
		super(MenuEntry.OPEN, editor);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		String filename = IO.readString( Message.openFile() );
		try{
			_receiver.openDocument(filename);
		}
		catch(FileNotFoundException e) { IO.println( Message.fileNotFound()); }
	}
}
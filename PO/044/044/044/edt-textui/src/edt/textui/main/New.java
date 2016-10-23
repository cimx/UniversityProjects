/** @version $Id: New.java,v 1.7 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.main;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.Command;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.Editor;

/**
 * Open a new document.
 */
public class New extends Command<Editor> {
	public New(Editor editor) {
		super(MenuEntry.NEW, editor);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		_receiver.newDocument();
	}
}
/** @version $Id: ShowIndex.java,v 1.11 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.main;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.Command;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.Editor;
import edt.core.Section;

/**
 * §2.1.4.
 */
public class ShowIndex extends Command<Editor> {
	public ShowIndex(Editor editor) {
		super(MenuEntry.SHOW_INDEX, editor);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		VisitIndex v = new VisitIndex();
		_receiver.docIndex(v);
		IO.println(v.getter());	
	}
}
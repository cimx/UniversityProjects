/** @version $Id: Edit.java,v 1.7 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.main;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.Command;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.Editor;

/**
 * §2.3.1.
 */
public class Edit extends Command<Editor> {
	public Edit(Editor editor) {
		super(MenuEntry.OPEN_DOCUMENT_EDITOR, editor);
  }

	@Override
	public final void execute() throws DialogException, IOException {
		edt.textui.section.MenuBuilder.menuFor(_receiver.getDocument(),_receiver.getDocument());
  }

}
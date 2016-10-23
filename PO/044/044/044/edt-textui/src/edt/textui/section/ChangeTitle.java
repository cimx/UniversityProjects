/** @version $Id: ChangeTitle.java,v 1.6 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.Document;
import edt.core.Section;

/**
 * §2.2.1.
 */
public class ChangeTitle extends SectionCommand {
	public ChangeTitle(Section section,Document document) {
		super(MenuEntry.CHANGE_TITLE, section, document);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		String title = IO.readString( Message.requestSectionTitle() );
		_receiver.setTitle(title);
	}
}
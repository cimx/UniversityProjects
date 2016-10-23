/** @version $Id: InsertSection.java,v 1.8 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.Document;
import edt.core.Section;

/**
 * §2.2.5.
 */
public class InsertSection extends SectionCommand {
	public InsertSection(Section section,Document document) {
		super(MenuEntry.INSERT_SECTION, section, document);
  	}

	@Override
	public final void execute() throws DialogException, IOException {
		int id = IO.readInteger(Message.requestSectionId());
		String title = IO.readString(Message.requestSectionTitle());
		_receiver.insertSection(id,title);
	}
}
/** @version $Id: InsertParagraph.java,v 1.9 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.Document;
import edt.core.Section;

/**
 * §2.2.8.
 */
public class InsertParagraph extends SectionCommand {
	public InsertParagraph(Section section,Document document) {
		super(MenuEntry.INSERT_PARAGRAPH, section, document);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		int id = IO.readInteger(Message.requestParagraphId());
		String text = IO.readString(Message.requestParagraphContent());
		_receiver.insertParagraph(id,text);
	}
}
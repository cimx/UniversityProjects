/** @version $Id: EditParagraph.java,v 1.7 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import edt.core.NoSuchParagraphException;
import java.io.IOException;
import edt.core.Section;
import edt.core.Document;

/**
 * §2.2.10.
 */
public class EditParagraph extends SectionCommand {
	public EditParagraph(Section section,Document document) {
		super(MenuEntry.EDIT_PARAGRAPH, section, document);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		int id = IO.readInteger(Message.requestParagraphId());
		String text = IO.readString(Message.requestParagraphContent());
		try {
			_receiver.editParagraph(id, text);
		}
		catch (NoSuchParagraphException e){
			IO.println(Message.noSuchParagraph(id));
		}
	}
}
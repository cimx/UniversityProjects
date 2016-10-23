/** @version $Id: RemoveParagraph.java,v 1.6 2015/11/30 20:32:51 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.NoSuchParagraphException;
import edt.core.Document;
import edt.core.Section;

/**
 * §2.2.11.
 */
public class RemoveParagraph extends SectionCommand {
	public RemoveParagraph(Section section,Document document) {
		super(MenuEntry.REMOVE_PARAGRAPH, section, document);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		int id = IO.readInteger(Message.requestParagraphId());
		try{
			_receiver2.removeParagraph(id, _receiver);
		}
		catch (NoSuchParagraphException e){ 
			IO.println(Message.noSuchParagraph(id)); 
		}
	}
}
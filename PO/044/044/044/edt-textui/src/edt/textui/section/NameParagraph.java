/** @version $Id: NameParagraph.java,v 1.9 2015/11/30 20:32:51 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.NoSuchParagraphException;
import edt.core.Document;
import edt.core.Section;

/**
 * §2.2.9.
 */
public class NameParagraph extends SectionCommand {
	public NameParagraph(Section section,Document document) {
		super(MenuEntry.NAME_PARAGRAPH, section, document);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		int id = IO.readInteger(Message.requestParagraphId());
		String uniqueID = IO.readString(Message.requestUniqueId());
		try{
			boolean nameChanged = _receiver2.nameParagraph(id,uniqueID,_receiver);
		    if(nameChanged)
				IO.println(Message.paragraphNameChanged()); 
		}
		catch (NoSuchParagraphException e){
			IO.println(Message.noSuchParagraph(id));
		}
	}
}
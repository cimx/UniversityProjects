/** @version $Id: RemoveSection.java,v 1.10 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.NoSuchSectionException;
import edt.core.Document;
import edt.core.Section;

/**
 * §2.2.7.
 */
public class RemoveSection extends SectionCommand {
	public RemoveSection(Section section,Document document) {
		super(MenuEntry.REMOVE_SECTION, section, document);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		int id = IO.readInteger(Message.requestSectionId());
		try{
			_receiver2.removeSection(id,_receiver);
		}
		catch (NoSuchSectionException e){ 
			IO.println(Message.noSuchSection(id));
		}
	}
}
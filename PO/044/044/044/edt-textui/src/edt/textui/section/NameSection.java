/** @version $Id: NameSection.java,v 1.9 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.NoSuchSectionException;
import edt.core.Document;
import edt.core.Section;

/**
 * §2.2.6.
 */
public class NameSection extends SectionCommand {
	public NameSection(Section section,Document document) {
		super(MenuEntry.NAME_SECTION, section, document);
	}

	@Override
	public final void execute() throws DialogException, IOException {
 		int id = IO.readInteger(Message.requestSectionId());
		String uniqueID = IO.readString(Message.requestUniqueId());
		try{
		    boolean nameChanged = _receiver2.nameSection(id,uniqueID,_receiver);
		    if(nameChanged)
				IO.println(Message.sectionNameChanged());
		}
		catch (NoSuchSectionException e){
			IO.println(Message.noSuchSection(id));
		} 
	}
}
/** @version $Id: SelectSection.java,v 1.6 2015/12/01 01:44:25 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.Document;
import edt.core.Section;

/**
 * §2.2.4.
 */
public class SelectSection extends SectionCommand {
	public SelectSection(Section section,Document document) {
		super(MenuEntry.SELECT_SECTION, section, document);
	}

	@Override
	public final void execute() throws DialogException, IOException  {
	 	int id = IO.readInteger(Message.requestSectionId());
	 	if(_receiver.hasSection(id)){
			IO.println(Message.newActiveSection(id));
			edt.textui.section.MenuBuilder.menuFor(_receiver.getSection(id),_receiver2);
		}
		else
			IO.println(Message.noSuchSection(id));	
	}
}
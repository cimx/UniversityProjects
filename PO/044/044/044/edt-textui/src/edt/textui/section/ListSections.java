/** @version $Id: ListSections.java,v 1.15 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import java.io.IOException;
import java.util.LinkedList;
import edt.core.Document;
import edt.core.Section;
import edt.core.Visitor;
import edt.textui.section.VisitListSection;

/**
 * §2.2.2.
 */
public class ListSections extends SectionCommand {
	public ListSections(Section section,Document document) {
		super(MenuEntry.LIST_SECTIONS, section, document);
	}
	
	@Override
	public final void execute() throws DialogException, IOException {
		if(_receiver.hasSubsections()){
			VisitListSection v = new VisitListSection();
			_receiver.accept(v);
			IO.println(v.getter());
		}
	}
}
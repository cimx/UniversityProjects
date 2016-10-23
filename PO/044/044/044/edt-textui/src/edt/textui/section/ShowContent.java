/** @version $Id: ShowContent.java,v 1.9 2015/12/01 01:44:25 ist181172 Exp $ */
package edt.textui.section;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.Document;
import edt.core.Section;
import edt.core.Visitor;
import edt.textui.main.VisitInfo;

/**
 * §2.2.3.
 */
public class ShowContent extends SectionCommand {
	public ShowContent(Section section,Document document) {
		super(MenuEntry.SHOW_CONTENT, section, document);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		VisitInfo v = new VisitInfo();
		_receiver.accept(v);
		IO.println(v.getter());
	}
}
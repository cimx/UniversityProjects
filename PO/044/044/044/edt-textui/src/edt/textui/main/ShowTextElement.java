/** @version $Id: ShowTextElement.java,v 1.12 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.main;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.Command;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.NoSuchTextElementException;
import edt.core.Editor;
import edt.core.Visitor;

/**
 * §2.1.5.
 */
public class ShowTextElement extends Command<Editor> {
	public ShowTextElement(Editor editor) {
		  super(MenuEntry.SHOW_TEXT_ELEMENT, editor);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		String id = IO.readString(Message.requestElementId());
		VisitInfo v = new VisitInfo();
		try{
			_receiver.docAccept(v,id);
			IO.println(v.getter());
		}
		catch (NoSuchTextElementException e){ 
			IO.println(Message.noSuchTextElement(id)); 
		}
	}
}

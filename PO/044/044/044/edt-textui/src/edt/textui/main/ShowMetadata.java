/** @version $Id: ShowMetadata.java,v 1.7 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.main;

import static ist.po.ui.Dialog.IO;
import ist.po.ui.Command;
import ist.po.ui.DialogException;
import java.io.IOException;
import edt.core.Editor;
import edt.core.Author;

/**
 * §2.1.2.
 */
public class ShowMetadata extends Command<Editor> {
	public ShowMetadata(Editor editor) {
		super(MenuEntry.SHOW_METADATA, editor);
	}

	@Override
	public final void execute() throws DialogException, IOException {
		IO.println(Message.documentTitle( _receiver.getDocTitle()) );
		for(Author author: _receiver.getAuthors()){
			IO.println(Message.author( author.getName() , author.getEmail()) );
		}
		IO.println( Message.documentSections(_receiver.numberTopSections()) );
		IO.println ( Message.documentBytes(_receiver.bytesContent()) );
		IO.println ( Message.documentIdentifiers(_receiver.numberUniqueID()) );	
	}
}
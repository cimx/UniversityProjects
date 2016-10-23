/** @version $Id: TextEditor.java,v 1.5 2015/11/13 23:08:16 ist181172 Exp $ */
package edt.textui;

import static ist.po.ui.Dialog.IO;

import java.io.IOException;

import edt.core.Editor;

/**
 * Class that starts the application's textual interface.
 */
public class TextEditor {
	public static void main(String[] args) {
		Editor editor = new Editor();
		
		String datafile = System.getProperty("import"); //$NON-NLS-1$
		if (datafile != null) {
			try{
				editor.factory(datafile);
			}
			catch(IOException e){}
			}
		
		edt.textui.main.MenuBuilder.menuFor(editor);
		IO.closeDown();
	}
}

/** @version $Id: SectionCommand.java,v 1.5 2015/12/01 01:44:24 ist181172 Exp $ */
package edt.textui.section;

import ist.po.ui.Command;
import edt.core.Document;
import edt.core.Section;

/**
 * Superclass of all section-context commands.
 */
public abstract class SectionCommand extends Command< Section > {
	Document _receiver2;  
	public SectionCommand(String title,Section section, Document document) {
		super(title, section);
		_receiver2 = document;
	}
}
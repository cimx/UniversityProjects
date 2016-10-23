package edt.textui.main;

import edt.core.Document;
import edt.core.Section;
import edt.core.Paragraph;
import edt.core.Visitor;

public class VisitIndex implements Visitor{
	private String _index = "";
	public String getter(){
		if(_index.length()>0)	
			return _index.substring(0,_index.length()-1);
		else
			return "";
	}
	public void visitDocument(Document document){
		_index += "{" + document.getTitle() + "}\n";
		for (Section section: document.getSections()){
			_index += Message.sectionIndexEntry(section.getID(),section.getTitle());
			_index += "\n";
		}
	}
	public void visitSection(Section section){}
	public void visitParagraph(Paragraph paragraph){}
}
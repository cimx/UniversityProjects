package edt.textui.section;

import edt.core.Document;
import edt.core.Section;
import edt.core.Paragraph;
import edt.core.Visitor;

public class VisitListSection implements Visitor{
	private String _listSections = "";
	public String getter(){
		if(_listSections.length()>0)	
			return _listSections.substring(1);
		else
			return "";
	}
	public void visitSection(Section section){
		for(Section s: section.getSections()){
			_listSections += "\n";
			_listSections += Message.sectionIndexEntry(s.getID(),s.getTitle());
			s.accept(this);  
		}
	}
	public void visitParagraph(Paragraph paragraph){}
	public void visitDocument(Document document){
		visitSection(document);
	}
}
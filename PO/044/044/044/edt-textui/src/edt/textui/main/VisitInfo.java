package edt.textui.main;

import edt.core.Document;
import edt.core.Section;
import edt.core.Paragraph;
import edt.core.Visitor;

public class VisitInfo implements Visitor{
	private String content="";
	public String getter(){
			return content;
	}
	public void visitDocument(Document document){
		content += "{"+document.getTitle()+"}";
		for(Paragraph paragraph: document.getParagraphs()){
			content += "\n";
			paragraph.accept(this);
		}
		for (Section section: document.getSections()){
			content += "\n";	
			section.accept(this); 
		}
	}
	public void visitSection(Section section){
		content += Message.sectionIndexEntry(section.getID(), section.getTitle());
		for(Paragraph paragraph: section.getParagraphs()){
			content += "\n";
			paragraph.accept(this);
		}
		for(Section s: section.getSections()){
			content += "\n"; 
			s.accept(this); 
		} 
	}
	public void visitParagraph(Paragraph paragraph){
		content += paragraph.getText();
	}	
}
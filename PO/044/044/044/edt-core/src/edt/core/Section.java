package edt.core;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import edt.core.Paragraph;

/** 
* Class that has _sections and _paragraphs as atributes, they both save in a LinkedList structure the sections / subsections and paragraphs
* of a Document and of each one of the sections
* this class also has _title as an atribute, it has the title of the section
*/

public class Section extends Content implements Serializable{
	private String _title;
	private LinkedList<Section> _sections = new LinkedList<Section>();
	private LinkedList<Paragraph> _paragraphs = new LinkedList<Paragraph>();
	
	//CONSTRUCTORS
	public Section(){ _title = ""; }
	public Section(String title){ _title = title; }
	//

	//GETTERS & SETTERS
	public String getTitle(){ return _title; }
	public void setTitle(String title){ _title = title; }
	public LinkedList<Section> getSections(){ return _sections; }
	public LinkedList<Paragraph> getParagraphs(){ return _paragraphs; }
	public Section getSection(int index){ return _sections.get(index); }
	public Paragraph getParagraph(int index){ return _paragraphs.get(index); }
	//

	//FACTORY (Editor)
	public void addID(String id , Section section){ section.setID(id); }
	//

	/**
	* this methods add Sections or Paragraphs to the respectives lists 
	* if we have an index in we add them in that position
	* if not we add them in the end of the list
	*/
	public void addSection(Section section){ _sections.addLast(section); }
	public void addSection(int index, Section section){ _sections.add(index,section); }
	public void addParagraph(Paragraph paragraph){ _paragraphs.addLast(paragraph); }
	public void addParagraph(int index, Paragraph paragraph){ _paragraphs.add(index,paragraph); }

	//SHOW META DATA
	/**
	* @return number of sections and paragraphs with unique IDs
	*/
	public int numUniqueID(){
		int num = 0;
		for(Section section: getSections()){
			num += section.numUniqueID();
			if (section.getID() != "")
				num++;
		}
		for(Paragraph paragraph: getParagraphs()){
			if (paragraph.getID() != "") 
				num++;
		}
		return num;	
	}
	/**
	* @return dimension of a document (number of characters / bytes in a document, including titles)
	*/
	public int sizeContent(){
		int size = 0;
		for(Section subsection: _sections)
			size += subsection.sizeContent();
		for(Paragraph paragraph: _paragraphs)
			size += paragraph.getSizeContent();
		return size + _title.length();
	}
	//

	//SELECT SECTION
	public boolean hasSection(int id){
		if(getSections().size() > id && !(_sections.isEmpty()) && id>=0)
			return true;
		return false;
	}
	//

	//LIST SECTIONS
	public boolean hasSubsections(){
		return !(_sections.isEmpty());
	}
	//

	//EDIT PARAGRAPH
	public void editParagraph(int id, String text) throws NoSuchParagraphException {
		int i=0;
		if (id >= _paragraphs.size())
			throw new NoSuchParagraphException(); 
		_paragraphs.get(id).setText(text);	
	}
	//

	//INSERT CONTENT
	public void insertSection(int id,String title){
		Section section  = new Section(title);
		if (id < _sections.size() && id>=0)
			addSection( id , section);
		else
			addSection(section);
	}
	public void insertParagraph(int id, String text){
		Paragraph paragraph = new Paragraph(text);
		if (id < _paragraphs.size() && id>=0 )
			addParagraph( id , paragraph);
		else
			addParagraph(paragraph);
	}
	//

	//VISITOR
	public void accept(Visitor v){
		v.visitSection(this);
	}
}
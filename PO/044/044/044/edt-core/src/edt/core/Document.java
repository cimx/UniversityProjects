package edt.core; 

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;

public class Document extends Section implements Serializable { 
	
	private String _filename;
	private TreeMap<String, Author> _authors  = new TreeMap<String, Author>();
	private TreeMap<String, Content> _content = new TreeMap<String, Content>();
	
	//CONSTRUCTOR
	public Document(){ }
	
	//GETTERS & SETTERS
	public String getFilename(){ return _filename; }
	public void setFilename(String filename){ _filename = filename;	}
	public Author getAuthor(String name) { return _authors.get(name); }
	//

	//FACTORY (Editor)
	public void addTreeID(String id, Section section){
		_content.put(id,section);
	}
	//

	//AUTHORS - add an author to the document
	public void addAuthor(String name, String email) throws DuplicateAuthorException{
		Author author = new Author(name,email);
		if ( _authors.containsKey(name))
			throw new DuplicateAuthorException();
		else
			_authors.put(name, author);
	}
	//

	//SHOW META DATA
	public Author[] getAuthors(){
		Author[] authors = new Author[_authors.size()];
		int index = 0;
		for (String key: _authors.keySet()){
			authors[index]= _authors.get(key);
			index++;
		}
		return authors;
	}
	//

	//REMOVE PARAGRAPH
	public void removeParagraph(int id,Section actualSection) throws NoSuchParagraphException{
		if ( id < actualSection.getParagraphs().size() ){
			_content.remove(actualSection.getParagraphs().get(id).getID());
			actualSection.getParagraphs().remove(id);  
		}
		else
			throw new NoSuchParagraphException();
	}
	//

	//REMOVE SECTION
	public void removeSection(int id, Section actualSection) throws NoSuchSectionException{
		if ( id < actualSection.getSections().size() ){
			_content.remove(actualSection.getSections().get(id).getID());
			actualSection.getSections().remove(id);
		}
		else
			throw new NoSuchSectionException();	
	}
	//
	
	//NAME CONTENT
	public boolean nameSection(int id, String uniqueID, Section actualSection) throws NoSuchSectionException{
		boolean hasUniqueID = false;
		String actualID ="";
		if (uniqueID.length()>0){
			if ( id >= actualSection.getSections().size() || id<0)
				throw new NoSuchSectionException();
			if( actualSection.getSections().get(id).hasUniqueID()){
				hasUniqueID = true;
				actualID = actualSection.getSections().get(id).getID();
			}
			for (String key: _content.keySet()){
				if (key.equals(uniqueID))
				    _content.get(key).setID("");
			}
			if(actualID != "")
				_content.remove(actualID);
			_content.remove(uniqueID);
			actualSection.getSections().get(id).setID(uniqueID);
			_content.put(uniqueID,actualSection.getSections().get(id));
		}
		return hasUniqueID;
	}
	public boolean nameParagraph(int id,String uniqueID,Section actualSection) throws NoSuchParagraphException{
		boolean hasUniqueID = false;
		String actualID ="";
		if (uniqueID.length()>0){
			if ( id >= actualSection.getParagraphs().size() || id<0 )
				throw new NoSuchParagraphException();
			if( actualSection.getParagraphs().get(id).hasUniqueID()){
				hasUniqueID = true;
				actualID = actualSection.getParagraphs().get(id).getID();
			}

			for (String key: _content.keySet()){
				if (key.equals(uniqueID))
				    _content.get(key).setID("");
			}
			if(actualID != "")
				_content.remove(actualID);
			_content.remove(uniqueID);
			actualSection.getParagraphs().get(id).setID(uniqueID);
			_content.put(uniqueID,actualSection.getParagraphs().get(id));
		}
		return hasUniqueID;
	}
	//

	//SHOW TEXT ELEMENT
	public Content getContent(String id) throws NoSuchTextElementException{
		if (!_content.containsKey(id))
			throw new NoSuchTextElementException();
		return _content.get(id);
	}
	public void textElement(Visitor v, String id) throws NoSuchTextElementException{
		Content content = getContent(id); 
		content.accept(v);
	}
	//

	//VISITOR
	public void accept(Visitor v){
		v.visitDocument(this);
	}
}
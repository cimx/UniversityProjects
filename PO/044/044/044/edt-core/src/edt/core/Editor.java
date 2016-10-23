package edt.core;

import java.io.Serializable;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import edt.core.DuplicateAuthorException;
import edt.core.Author;
import edt.core.Section;
import edt.core.Document;
import edt.core.Paragraph;

public class Editor{
	private Document _document = new Document();
	private Section _actualSection;
	
	public Editor(){ }
	public Document getDocument(){return _document;}
	public boolean hasFilename(){ return getDocFilename()!=null; }
	public String getDocFilename(){ return _document.getFilename(); }
	public void setDocFilename(String filename){ _document.setFilename(filename); }
	public String getDocTitle(){ return _document.getTitle();}
	public Author[] getAuthors(){return _document.getAuthors();}
	public int numberTopSections() {return _document.getSections().size();}
	public int numberUniqueID(){ return _document.numUniqueID(); }
	public int bytesContent() { return _document.sizeContent(); }
	public void docIndex(Visitor v) {_document.accept(v);}
	public void addDocAuthor(String name, String email) throws DuplicateAuthorException {
		 _document.addAuthor(name,email); 
		}
	public void docAccept(Visitor v , String id ) throws NoSuchTextElementException { 
		_document.textElement(v,id);
	}

	public Document newDocument(){
		_document  = new Document();
		return _document;
	}
	public void openDocument(String docName) throws FileNotFoundException{
		try{
			ObjectInputStream ois = new ObjectInputStream(
						new BufferedInputStream(
						new FileInputStream(docName)));
			_document = (Document)ois.readObject();	
			_document.setFilename(docName);
			ois.close();	
		}
		catch (FileNotFoundException e) { throw new FileNotFoundException(); }
		catch (IOException e){ e.printStackTrace(); }
		catch (ClassNotFoundException e ) { e.printStackTrace(); }
	}
	public void saveDocument(String filename) throws IOException {
		
			ObjectOutputStream oos = new ObjectOutputStream(
						 new BufferedOutputStream(
						 new FileOutputStream(filename)));
			oos.writeObject(_document);
			oos.close(); 
		
	}	  
	
	//FACTORY - read text imported
	public void factory(String datafile) throws IOException{
		 BufferedReader reader = null;
		 try{
			reader = new BufferedReader( new FileReader(datafile) );
			_document = new Document();
			_actualSection = _document;
			
			readTitle( reader.readLine() );
			readAuthors( reader.readLine() );
			
			String line;
			
			while (( line = reader.readLine() ) != null){
				 readLineContent(line);
			}
		 }
		 catch(IOException e){}
		 finally{
			if (reader != null)
				reader.close();
			}
	}
	public void readTitle(String title){
		_document.setTitle(title);
	}
	public void readAuthors(String authors){
		for(String author: authors.split("\\|")) {
			String[] data = author.split("\\/");
			try{
				_document.addAuthor(data[0],data[1]);
			 }
			 catch (DuplicateAuthorException e){continue;}
		}	
	}
	public void readLineContent(String line){
		String data[] = line.split("\\|");
		switch(data[0]){
			case "SECTION":
				_actualSection = new Section(data[2]);
				_document.addSection(_actualSection);
				if ( data[1].length() > 0 ){
					_document.addID( data[1] , _actualSection );
					_document.addTreeID(data[1],_actualSection);
				}
				break;
			case "PARAGRAPH":
				 Paragraph paragraph = new Paragraph(data[1]);
				 _actualSection.addParagraph( paragraph ); //CRIAR
				 break;
		}
	}
}
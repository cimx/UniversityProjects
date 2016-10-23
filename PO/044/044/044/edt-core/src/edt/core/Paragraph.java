package edt.core;

import java.io.Serializable;

public class Paragraph extends Content implements Serializable{
	
	private String _text;
	
	public Paragraph(String text){
		_text = text;
	}
	public String getText(){
		return _text;
	}
	public void setText(String text){
		_text = text;
	}
	public int getSizeContent(){
		return _text.length();
	}
	
	public void accept(Visitor v){
		 v.visitParagraph(this);
	}
}
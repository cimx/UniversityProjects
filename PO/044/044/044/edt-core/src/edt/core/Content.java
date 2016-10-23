package edt.core;

import java.io.Serializable;

public abstract class Content implements Serializable{ 
	
	private String _id;
	
	public Content(){ }
	
	public String getID(){
		if (_id == null)
			return "";
		return _id;
	}
	public void setID(String id){
		_id = id;
	}
	public boolean hasUniqueID(){
		return getID()!="";
	}
	public abstract void accept(Visitor v);
}
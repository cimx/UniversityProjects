package edt.core;

import java.io.Serializable;

public class Author implements Serializable{
	
	private String _name;
	private String _email;
	
	public Author(String name, String email){
		_name = name;
		_email = email;
	}  
	public String getName(){
		return _name;
	}
	public String getEmail(){
		return _email;
	}
	public void setName(String name){
		_name = name;
	}
	public void setEmail(String email){
		_email = email;
	}	
}
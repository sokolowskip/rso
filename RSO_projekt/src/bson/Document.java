package bson;

public class Document<T> 
{
	String name;
	T data;
	
	public Document() 
	{
		name = "";
	}
}
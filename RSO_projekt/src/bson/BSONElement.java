package bson;

public class BSONElement<T> 
{
	String name;
	T data;
	
	public BSONElement() 
	{
		name = "";
	}
}
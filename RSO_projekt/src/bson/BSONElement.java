package bson;

public class BSONElement<T> 
{
	String name;
	T data;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	public BSONElement() 
	{
		name = "";
	}
}